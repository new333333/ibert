package com.sitescape.ef.module.workflow;
/**
 * Handle setting variables, starting/stoping threads and recording the state when
 * a new node is entered or cancelling timers when a node is exitted.
 * This is done as part on one action so we can maintain the ordering
 * specified in the definition and reduce the amount of synchronization needed between the
 * JBPM definition and the Sitescape definition.
 * @author Janet McCann
 *
 */
import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.util.Validator;

public class EnterExitEvent extends AbstractActionHandler {
	private static final long serialVersionUID = 1L;
	  
	//Indexing the entry is handled by the code that initiates a transition/nodeEnter/nodeExit
	//Because mutiple states can be effected, we don't want to re-index
	//each time.  Only need one at the end of the transaction

	public void execute(ExecutionContext executionContext) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token token = executionContext.getToken();
		Long id = new Long(token.getId());
		String state = token.getNode().getName();
		WorkflowSupport entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(id);
		if (infoEnabled) logger.info("Workflow event (" + executionContext.getEvent().getEventType() + ")");
		if (ws != null) {
			List items;
			if (Event.EVENTTYPE_NODE_ENTER.equals(executionContext.getEvent().getEventType())) {
				Date current = new Date();
				HistoryStamp stamp = new HistoryStamp(RequestContextHolder.getRequestContext().getUser(), current);
				if (entry.getWorkflowChange() == null) {
					entry.setWorkflowChange(stamp);
				} else if ((entry.getWorkflowChange().getDate() != null) && current.after(entry.getWorkflowChange().getDate())) {
					entry.setWorkflowChange(stamp);
				}
				//	record when we enter the state
				ws.setWorkflowChange(stamp);
				ws.setState(state);
				entry.setStateChange(ws);
				if (infoEnabled) logger.info("Workflow event (" + executionContext.getEvent().getEventType() + ") recorded: " + state);
				items  = WorkflowUtils.getOnEntry(ws.getDefinition(), state);
			} else {
				//cancel timers associated with this state.
				executionContext.getJbpmContext().getSchedulerSession().cancelTimersByName("onDataValue", token);
				items  = WorkflowUtils.getOnExit(ws.getDefinition(), state);				
			}
			boolean check = false;
			for (int i=0; i<items.size(); ++i) {
				Element item = (Element)items.get(i);
	   			String name = item.attributeValue("name","");
	   			if ("variable".equals(name)) {
	   				if (TransitionUtils.setVariable(item, executionContext, entry, ws)) {
	   					check = true;
	   				}
	   			} else if ("startParallelThread".equals(name)) {
	   				startThread(item, executionContext, entry, ws);
	   			} else if ("stopParallelThread".equals(name)) {
	   				if (stopThread(item, executionContext, entry, ws)) check = true;
	   				
	   			}
			}
			//See if other threads conditions are now met.
			if (check) TransitionUtils.processConditions(entry, token);
			
		}
	}

	protected void startThread(Element item, ExecutionContext executionContext, WorkflowSupport entry, WorkflowState currentWs) {
		//Get the "startState" property
		Element threadEle = (Element)item.selectSingleNode("./properties/property[@name='name']");
		if (threadEle == null) return;
		String threadName = threadEle.attributeValue("value", "");
		if (Validator.isNull(threadName)) return;
		threadEle = (Element) item.getDocument().getRootElement().selectSingleNode("//item[@name='parallelThread']/properties/property[@name='name' and @value='"+threadName+"']");
		if (threadEle == null) return;
		threadEle = threadEle.getParent().getParent();
		Element startStateEle = (Element) threadEle.selectSingleNode("./properties/property[@name='startState']");
		if (startStateEle == null) return;
		String startState = startStateEle.attributeValue("value", "");
		if (Validator.isNull(startState)) return;

		if (infoEnabled) logger.info("Starting thread: " + threadName);
		
		//	if thread exists, terminate it
		WorkflowState thread = entry.getWorkflowStateByThread(currentWs.getDefinition(), threadName);
		if (thread != null) {
			Token childToken = executionContext.getJbpmContext().loadToken(thread.getTokenId().longValue());
			childToken.end(false);
			entry.removeWorkflowState(thread);
		}
		ProcessInstance pI = executionContext.getToken().getProcessInstance();
		ProcessDefinition pD = pI.getProcessDefinition();
		//Now start a thread - since threads can be restarted and we don't delete old
		//tokens, each thread instance needs a unique name
		//This also implies we cannot look child tokens up by name cause we don't know it
		//the 'real' thread name is kept in WorkflowState
		Token subToken = new Token(pI.getRootToken(), threadName + "-" + new Date());
		executionContext.getJbpmContext().getSession().save(subToken);
		//Track state of thread
		thread = (WorkflowState) new WorkflowState();
		thread.setThreadName(threadName);
		thread.setTokenId(new Long(subToken.getId()));
		thread.setState(startState);
		//Use the same workflow definition as the current workflow state
		thread.setDefinition(currentWs.getDefinition());
		//need to save explicitly - actions called by the node.enter may look it up 
		getCoreDao().save(thread);
		entry.addWorkflowState(thread);
			
		Node node = pD.findNode(startState);
		node.enter(new ExecutionContext(subToken));
	}
	protected boolean stopThread(Element item, ExecutionContext executionContext, WorkflowSupport entry, WorkflowState currentWs) {
		Element threadEle = (Element)item.selectSingleNode("./properties/property[@name='name']");
		if (threadEle == null) return false;
		String threadName = threadEle.attributeValue("value", "");
		if (Validator.isNull(threadName)) return false;

		//See if child has ended
		WorkflowState thread = entry.getWorkflowStateByThread(currentWs.getDefinition(), threadName);
		if (thread != null) {
			//child is active, end it
			Token childToken = executionContext.getJbpmContext().loadToken(thread.getTokenId().longValue());
			if (childToken != null)	childToken.end();
			entry.removeWorkflowState(thread);
			if (infoEnabled) logger.info("Stoping thread: " + threadName);
			return true;
		}
		return false;
	}
}
