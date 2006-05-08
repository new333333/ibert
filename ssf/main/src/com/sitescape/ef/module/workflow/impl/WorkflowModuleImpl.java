package com.sitescape.ef.module.workflow.impl;

import java.io.StringWriter;
import java.util.ArrayList;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.WorkflowUtils;
import com.sitescape.ef.module.workflow.WorkflowModule;
import com.sitescape.ef.util.ReflectHelper;
import com.sitescape.ef.util.SZoneConfig;
import com.sitescape.ef.domain.WfWaits;
import com.sitescape.util.Validator;
import com.sitescape.ef.jobs.WorkflowTimeout;
import com.sitescape.ef.module.binder.EntryProcessor;


import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.HibernateException;
import org.jbpm.calendar.BusinessCalendar;
import org.jbpm.calendar.Duration;
import org.jbpm.context.exe.ContextInstance;
import com.sitescape.ef.module.workflow.impl.JbpmContext;
import org.jbpm.db.SchedulerSession;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.StartState;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.xml.JpdlXmlWriter;
import org.jbpm.scheduler.def.CancelTimerAction;
import org.jbpm.scheduler.def.CreateTimerAction;
import org.jbpm.scheduler.exe.Timer;

import org.springframework.orm.hibernate3.SessionFactoryUtils;

public class WorkflowModuleImpl extends CommonDependencyInjection implements WorkflowModule {
   static BusinessCalendar businessCalendar;

   /**
    * Called after bean is initialized.  Use this to make sure
    * scheduler has workflowtimeout job active
    *
    */
   public void init() {
//	   businessCalendar = new BusinessCalendar();
	   List companies = getCoreDao().findCompanies();
	   for (int i=0; i<companies.size(); ++i) {
		   String zoneName = (String)companies.get(i);
		   String jobClass = SZoneConfig.getString(zoneName, "workflowConfiguration/property[@name='" + WorkflowTimeout.TIMEOUT_JOB + "']");
		   if (Validator.isNull(jobClass)) jobClass = "com.sitescape.ef.jobs.DefaultWorkflowTimeout";
		   try {
			   Class processorClass = ReflectHelper.classForName(jobClass);
			   WorkflowTimeout job = (WorkflowTimeout)processorClass.newInstance();
			   //make sure a timeout job is scheduled for the zone
			   String secsString = (String)SZoneConfig.getString(zoneName, "workflowConfiguration/property[@name='" + WorkflowTimeout.TIMEOUT_SECONDS + "']");
			   int seconds = 300;
			   try {
				   seconds = Integer.parseInt(secsString);
			   } catch (Exception ex) {};
			   	job.schedule(zoneName, seconds);
    	
		   } catch (ClassNotFoundException e) {
			   throw new ConfigurationException(
    				"Invalid WorkflowTimeout class name '" + jobClass + "'",
    				e);
		   } catch (InstantiationException e) {
			   throw new ConfigurationException(
    				"Cannot instantiate WorkflowTimeout of type '"
                        	+ jobClass + "'");
		   } catch (IllegalAccessException e) {
			   throw new ConfigurationException(
    				"Cannot instantiate WorkflowTimeout of type '"
    				+ jobClass + "'");
		   }
	   }
   }
	public List getAllDefinitions() {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	        return context.getGraphSession().findAllProcessDefinitions();
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	};

	public List getAllDefinitions(String name) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	        return context.getGraphSession().findAllProcessDefinitionVersions(name);
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	};

	public List getLatestDefinitions() {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	        return context.getGraphSession().findLatestProcessDefinitions();
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	};

	public List getNodes(Long id) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	        ProcessDefinition pD = context.getGraphSession().loadProcessDefinition(id.longValue());
	        return pD.getNodes();
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	};

	public List getProcessInstances(Long id) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	        return context.getGraphSession().findProcessInstances(id.longValue());
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	};

	public void deleteProcessInstance(Long processInstanceId) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	       	ProcessInstance pI = context.loadProcessInstanceForUpdate(processInstanceId.longValue());
	       	if (pI != null) context.getGraphSession().deleteProcessInstance(pI);
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	};
	
	public ProcessDefinition getWorkflow(Long id) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	        return context.getGraphSession().loadProcessDefinition(id.longValue());
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	};

	public ProcessInstance getProcessInstance(Long id) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
        	ProcessInstance pI = context.getGraphSession().loadProcessInstance(id.longValue());
	        return pI;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	};

	public ProcessDefinition addWorkflow(String xmlString){
		JbpmContext context = WorkflowFactory.getContext();
	    try {
        	ProcessDefinition pD = ProcessDefinition.parseXmlString(xmlString);
        	context.getGraphSession().saveProcessDefinition(pD);
        	return pD;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	}
	public ProcessInstance addWorkflowInstance(Long id) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
        	ProcessDefinition pD = context.getGraphSession().loadProcessDefinition(id.longValue());
        	ProcessInstance pI = new ProcessInstance(pD);
        	context.save(pI);
        	return pI;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	}
	public ProcessInstance setTransition(Long processInstanceId, String transitionId) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
        	ProcessInstance pI = context.loadProcessInstanceForUpdate(processInstanceId.longValue());
            pI.signal(transitionId);
            return pI;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	}
	public ProcessInstance setNextTransition(Long processInstanceId) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
        	ProcessInstance pI = context.loadProcessInstanceForUpdate(processInstanceId.longValue());
        	Token token = pI.getRootToken();
        	Transition transition = token.getNode().getDefaultLeavingTransition();
        	if (transition == null) {
        		List transitions = token.getNode().getLeavingTransitions();
        		if (transitions.size() <= 0) return pI;
        		transition = (Transition) transitions.get((int) 0);
        	}
            token.signal(transition);
            return pI;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	}
	public ProcessInstance setNode(Long processInstanceId, String nodeId) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	    	ProcessInstance pI = context.loadProcessInstanceForUpdate(processInstanceId.longValue());
        	Token token = pI.getRootToken();
            ProcessDefinition pD = pI.getProcessDefinition();
            Node node = pD.findNode(nodeId);
        	token.setNode(node);
            ExecutionContext executionContext = new ExecutionContext(token);
            node.enter(executionContext);
            return pI;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	}
	public void deleteProcessDefinition(Long id) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	    	context.getGraphSession().deleteProcessDefinition(id.longValue());
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	}
	public void deleteProcessDefinition(String name) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	        ProcessDefinition pD = context.getGraphSession().findLatestProcessDefinition(name);
	        context.getGraphSession().deleteProcessDefinition(pD);
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	};
	private RuntimeException convertJbpmException(Exception ex) {
		// try to decode and translate HibernateExceptions
	    if (ex instanceof HibernateException) {
	        return SessionFactoryUtils.convertHibernateAccessException((HibernateException) ex);
	    }

	    if (ex.getCause() instanceof HibernateException) {
	        // todo: going to loose a message here - perhaps create a NestedDataAccessException or similar
	        return SessionFactoryUtils.convertHibernateAccessException((HibernateException) ex.getCause());
	    }

	    // todo: classify into something like UncategorizedWorkflowException
	    return new RuntimeException(ex);
	}
	
	//Routine to build (or modify) a workflow process definition from a Definition
	public void modifyProcessDefinition(String definitionName, Definition def) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	        ProcessDefinition pD = context.getGraphSession().findLatestProcessDefinition(definitionName);
	        if (pD == null) {
	        	//The process definition doesn't exist yet, go create one
	        	pD = ProcessDefinition.createNewProcessDefinition();
	    		pD.setName(definitionName);        	
	        }
	        modifyProcessDefinition(pD, def);
	    	context.getGraphSession().saveProcessDefinition(pD);

	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
		
	}

	public void modifyProcessDefinition(ProcessDefinition pD, Definition def) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	    	Document defDoc = def.getDefinition();
	    	Element defRoot = defDoc.getRootElement();
		
	    	//Start by remembering all of the nodes
	    	Map nodesMap = pD.getNodesMap();
	    	if (nodesMap == null) nodesMap = new HashMap();
	    	nodesMap = new HashMap(nodesMap);
		
	    	Map events = pD.getEvents();
	    	if (events == null) events = new HashMap();
		
	    	Map actions = pD.getActions();
	    	if (actions == null) actions = new HashMap();
		
	    	//	Add the standard events (if they aren't there already)
	    	Action recordEvent = null;
	    	if (!actions.containsKey("recordEvent")) {
	    		recordEvent = setupAction(pD, "recordEvent", "com.sitescape.ef.module.workflow.RecordEvent");
	    	} else {
	    		recordEvent = (Action) actions.get("recordEvent");
	    	}
		
	    	Action decisionAction = null;
	    	if (!actions.containsKey("decisionAction")) {
	    		decisionAction = setupAction(pD, "decisionAction", "com.sitescape.ef.module.workflow.DecisionAction");
	    		pD.addAction(decisionAction);
	    	} else {
	    		decisionAction = (Action) actions.get("decisionAction");
	    	}
	    	
	    	Action startThreads = null;
	    	if (!actions.containsKey("startThreads")) {
	    		startThreads = setupAction(pD, "startThreads", "com.sitescape.ef.module.workflow.StartThreads");
	    		pD.addAction(startThreads);
	    	} else {
	    		startThreads = (Action) actions.get("startThreads");
	    	}
		
	    	Action stopThreads = null;
	    	if (!actions.containsKey("stopThreads")) {
	    		stopThreads = setupAction(pD, "stopThreads", "com.sitescape.ef.module.workflow.StopThreads");
	    		pD.addAction(stopThreads);
	    	} else {
	    		stopThreads = (Action) actions.get("stopThreads");
	    	}

	    	Action notifyAction = null;
	    	if (!actions.containsKey("notifyAction")) {
	    		notifyAction = setupAction(pD, "notifyAction", "com.sitescape.ef.module.workflow.Notify");
	    		pD.addAction(notifyAction);
	    	} else {
	    		notifyAction = (Action) actions.get("notifyAction");
	    	}

	    	//add global named events - will fire on every node
	    	if (!events.containsKey("node-enter")) {
	    		Event enterEvent = new Event("node-enter");
	    		enterEvent.addAction(recordEvent);
	    		pD.addEvent(enterEvent);
	    	}
		
	    	//Add our common start and end states
	    	if (!nodesMap.containsKey(ObjectKeys.WORKFLOW_START_STATE)) {
	    		StartState startState = new StartState(ObjectKeys.WORKFLOW_START_STATE);
	    		pD.addNode(startState);
	    	} else {
	    		nodesMap.remove(ObjectKeys.WORKFLOW_START_STATE);
	    	}
	    	if (!nodesMap.containsKey(ObjectKeys.WORKFLOW_END_STATE)) {
	    		EndState endState = new EndState(ObjectKeys.WORKFLOW_END_STATE);
	    		pD.addNode(endState);
	    	} else {
	    		nodesMap.remove(ObjectKeys.WORKFLOW_END_STATE);
	    	}
		
	
	    	//Add all of the states in the definition
	    	List stateNodes = defRoot.selectNodes("//item[@name='state']");
	    	Iterator itStates = stateNodes.iterator();
	    	while (itStates.hasNext()) {
	    		Element state = (Element) itStates.next();
	    		Element stateNameProperty = (Element) state.selectSingleNode("./properties/property[@name='name']");
	    		String stateName = stateNameProperty.attributeValue("value", "");
	    		if (!stateName.equals("")) {
	    			//determine type of node needed
	    			Node stateNode = (Node)nodesMap.get(stateName);
	    			if (!nodesMap.containsKey(stateName)) {
	    				stateNode = new Node(stateName);
	    				Action action = new Action();
	    				action.setReferencedAction(decisionAction);
	    				stateNode.setAction(action);
	    				pD.addNode(stateNode);
	    			} else {
	    				nodesMap.remove(stateName);
	    			}	
	    			//Check if need parallel threads - if so add special actgion
	    			List threads = (List)state.selectNodes("./item[@name='startParallelThread']");
	    			if (!threads.isEmpty()) {
	    				//make sure start threads action exists
	    				addEnterEventAction(context, stateNode, startThreads);				
	    			} else {
	    				// remove any old startThreads for this node
	    				removeEnterEventAction(context, stateNode, startThreads);
	    			}
	    			//Check if top stop threads - if so add special node
	    			threads = (List)state.selectNodes("./item[@name='stopParallelThread']");
	    			if (!threads.isEmpty()) {
	    				//make sure start threads action exists
	    				addEnterEventAction(context, stateNode, stopThreads);
					
	    			} else {
	    				// remove any old stopThreads for this node
	    				removeEnterEventAction(context, stateNode, stopThreads);
	    			}
	    			List notifications = (List)state.selectNodes("./item[@name='notifications']/item[@name='entryNotification']");
	    			if (!notifications.isEmpty()) {
	    				//make sure notify action exists
	    				addEnterEventAction(context, stateNode, notifyAction);
	    			} else {
	    				// remove any old notifyAction for this node
	    				removeEnterEventAction(context, stateNode, notifyAction);
	    			}
	    			notifications = (List)state.selectNodes("./item[@name='notifications']/item[@name='exitNotification']");
	    			if (!notifications.isEmpty()) {
	    				//make sure notify action exists
	    				addExitEventAction(context, stateNode, notifyAction);
	    			} else {
	    				// remove any old notifyAction for this node
	    				removeExitEventAction(context, stateNode, notifyAction);
	    			}
	    			Element timer = (Element)state.selectSingleNode("./item[@name='transitions']/item[@name='conditionOnElapsedTime']");
	    			if (timer != null) {
	    				Element props = (Element)timer.selectSingleNode("./properties/property[@name='toState']");
	    				String toState = null;
	    				if (props != null) {
	    					toState = props.attributeValue("value");
	    				}
	    				//get days and convert to minutes
	    				props = (Element)timer.selectSingleNode("./properties/property[@name='days']");
	    				String val=null;
	    				long total = 0;
	    				if (props != null) {
	    					val= props.attributeValue("value");
	    					if (!Validator.isNull(val))
	    						total += Long.parseLong(val)*24*60;
	    				}
	    				props = (Element)timer.selectSingleNode("./properties/property[@name='hours']");
	    				if (props != null) {
	    					val= props.attributeValue("value");
	    					if (!Validator.isNull(val))
	    						total += Long.parseLong(val)*60;				    	
	    				}
	    				props = (Element)timer.selectSingleNode("./properties/property[@name='mins']");
	    				if (props != null) {
	    					val = props.attributeValue("value");
	    					if (!Validator.isNull(val))
	    						total += Long.parseLong(val);
	    				}
	    				addTimer(context, stateNode, "onElapsedTime", String.valueOf(total) + " minutes", toState);
	    			} else {
					// 	remove any old timers for this node
	    				removeTimer(context, stateNode, "onElapsedTime");
	    			}
	    		}
	    	}
		
	    	//Remove any nodes that are remaining in nodesMap. 
	    	//  These must have been deleted from the definition.
	    	Iterator itNodes = nodesMap.entrySet().iterator();
	    	while (itNodes.hasNext()) {
	    		Map.Entry me = (Map.Entry) itNodes.next();
	    		Node delNode = (Node)me.getValue();
	    		pD.removeNode(delNode);
	    		context.getSession().delete(delNode);
	    	}
	    	//Add all of the manual transitions
	    	nodesMap = pD.getNodesMap();
	    	itNodes = nodesMap.entrySet().iterator();
	    	while (itNodes.hasNext()) {
	    		Map.Entry me = (Map.Entry) itNodes.next();
	    		Node fromNode = (Node) me.getValue();
	    		String stateName = fromNode.getName();
	    		//Get all existing transitions
	    		Map oldTransitions = fromNode.getLeavingTransitionsMap();
	    		if (oldTransitions == null) oldTransitions = new HashMap();
	    		else oldTransitions = new HashMap(oldTransitions);
			
	    		//Get the list of manual transitions from the workflow definition
	    		Map manualTransitions = WorkflowUtils.getManualTransitions(def, fromNode.getName());
	    		Iterator itTransitions = manualTransitions.entrySet().iterator();
	    		while (itTransitions.hasNext()) {
	    			Map.Entry me2 = (Map.Entry) itTransitions.next();
	    			String toNodeName = (String) me2.getKey();
	    			String tName = stateName + "." + toNodeName;					
	    			if (oldTransitions.containsKey(tName)) {
	    				oldTransitions.remove(tName);
	    			} else {
	    				Node toNode = (Node) pD.getNode(toNodeName);
	    				if (toNode != null) {
	    					Transition t = new Transition();
	    					t.setProcessDefinition(pD);
	    					t.setName(tName);
	    					t.setTo(toNode);
	    					fromNode.addLeavingTransition(t);
	    				}
	    				
	    			}
	    		}
	    		//Get the list of automatic transitions from the workflow definition
	    		List waits = WorkflowUtils.getParallelThreadWaits(def, stateName);
	    		for (int i=0; i<waits.size(); ++i) {
	    			WfWaits w = (WfWaits)waits.get(i);
	    			String toNodeName = w.getToStateName();
	    			String tName = stateName + "." + toNodeName;					
	    			if (oldTransitions.containsKey(tName)) {
	    				oldTransitions.remove(tName);
	    			} else {
	    				Node toNode = (Node) pD.getNode(toNodeName);
	    				if (toNode != null) {
	    					Transition t = new Transition();
	    					t.setProcessDefinition(pD);
	    					t.setName(tName);
	    					t.setTo(toNode);
	    					fromNode.addLeavingTransition(t);
	    				}
	    				
	    			}
	    		}
	    		Iterator itTrans = oldTransitions.entrySet().iterator();
	    		while (itTrans.hasNext()) {
	    			Map.Entry me2 = (Map.Entry) itTrans.next();
	    			Transition delTrans = (Transition)me2.getValue();
	    			fromNode.removeLeavingTransition(delTrans);
	    			context.getSession().delete(delTrans);
	    		}
	    	}
		
	    } finally {
	    	context.close();
	    }
    	Writer writer = new StringWriter();
	    JpdlXmlWriter jpdl = new JpdlXmlWriter(writer);
	    jpdl.write(pD);
	    logger.info("Workflow process definition created: " + pD.getName());
	    logger.info(writer.toString());
	}
	private Event addTimer(JbpmContext context, Node node, String name, String timeout, String toState) {
		//	make sure start threads event exits
		Event event = node.getEvent(Event.EVENTTYPE_NODE_ENTER);
		if (event == null) {
			event = new Event(Event.EVENTTYPE_NODE_ENTER);
			node.addEvent(event);
		}
		List eventActions=event.getActions();
		CreateTimerAction createAction=null;
		if ((eventActions != null) && !eventActions.isEmpty()) {
			for (int i=0; i<eventActions.size(); ++i) {
				Action a = (Action)eventActions.get(i);
				if (a instanceof CreateTimerAction) {
					createAction = (CreateTimerAction)a;
					if (name.equals(createAction.getTimerName()))
						break;
					createAction = null;
				}
			}
		}
		if (createAction == null) {
			createAction = new CreateTimerAction();
			createAction.setTimerName(name);
			event.addAction(createAction);
			createAction.setName("createTimer");
		}
		createAction.setDueDate(timeout);
		createAction.setTransitionName(node.getName() + "." + toState);
		//add leave node action
		event = node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
		if (event == null) {
			event = new Event(Event.EVENTTYPE_NODE_LEAVE);
			node.addEvent(event);
		}
		eventActions=event.getActions();
		CancelTimerAction cancelAction=null;
		if ((eventActions != null) && !eventActions.isEmpty()) {
			for (int i=0; i<eventActions.size(); ++i) {
				Action a = (Action)eventActions.get(i);
				if (a instanceof CancelTimerAction) {
					cancelAction = (CancelTimerAction)a;
					if (name.equals(cancelAction.getTimerName()))
						break;
					cancelAction = null;
				}
			}
		}
		if (cancelAction == null) {
			cancelAction = new CancelTimerAction();
			cancelAction.setTimerName(name);
			event.addAction(cancelAction);
			cancelAction.setName("cancelTimer");
		}
			
		return event;
	
	}
	private void removeTimer(JbpmContext context, Node node, String name) {
		Event event = node.getEvent(Event.EVENTTYPE_NODE_ENTER);
		if (event != null) {
			List eventActions=event.getActions();
			if ((eventActions != null) && !eventActions.isEmpty()) {
				for (int i=0; i<eventActions.size(); ++i) {
					Action a = (Action)eventActions.get(i);
					if (a instanceof CreateTimerAction) {
						CreateTimerAction createAction = (CreateTimerAction)a;
						if (name.equals(createAction.getTimerName())) {
							event.removeAction(a);
							context.getSession().delete(a);	
							break;
						}
					}
				}
			}	
		}
		event = node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
		if (event != null) {
			List eventActions=event.getActions();
			if ((eventActions != null) && !eventActions.isEmpty()) {
				for (int i=0; i<eventActions.size(); ++i) {
					Action a = (Action)eventActions.get(i);
					if (a instanceof CancelTimerAction) {
						CancelTimerAction cancelAction = (CancelTimerAction)a;
						if (name.equals(cancelAction.getTimerName())) {
							event.removeAction(a);
							context.getSession().delete(a);	
							break;
						}
					}
				}
			}	
		}
		
	}
	private Event addEnterEventAction(JbpmContext context,Node node, Action action) {
		//	make sure start threads event exits
		Event event = node.getEvent(Event.EVENTTYPE_NODE_ENTER);
		if (event == null) {
			event = new Event(Event.EVENTTYPE_NODE_ENTER);
			node.addEvent(event);
		}
		List eventActions=event.getActions();
		boolean found = false;
		if ((eventActions != null) && !eventActions.isEmpty()) {
			for (int i=0; i<eventActions.size(); ++i) {
				Action a = (Action)eventActions.get(i);
				if ((a.getReferencedAction() != null)&& a.getReferencedAction().equals(action)) {
					found = true;
					break;
				}
			}
		}
		if (!found) {
			Action a = new Action();
			a.setReferencedAction(action);
			event.addAction(a);
		}
		return event;
	
	}
	private void removeEnterEventAction(JbpmContext context,Node node, Action action) {
		Event event = node.getEvent(Event.EVENTTYPE_NODE_ENTER);
		if (event == null) return;
		List eventActions = new ArrayList(event.getActions());
		if ((eventActions != null) && !eventActions.isEmpty()) {
			for (int i=0; i<eventActions.size(); ++i) {
				Action a = (Action)eventActions.get(i);
				if ((a.getReferencedAction() != null) && a.getReferencedAction().equals(action)) {
					event.removeAction(a);
					//this is necessary cause the hibernate settings try to do a cascade-delete on 
					//these associations (STUPID)
					a.setReferencedAction(null);
					a.setActionDelegation(null);
					context.getSession().delete(a);
					break;
					
				}
			}
		}
	}

	private Event addExitEventAction(JbpmContext context, Node node, Action action) {
		//	make sure start threads event exits
		Event event = node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
		if (event == null) {
			event = new Event(Event.EVENTTYPE_NODE_LEAVE);
			node.addEvent(event);
		}
		List eventActions=event.getActions();
		boolean found = false;
		if ((eventActions != null) && !eventActions.isEmpty()) {
			for (int i=0; i<eventActions.size(); ++i) {
				Action a = (Action)eventActions.get(i);
				if ((a.getReferencedAction() != null) && (a.getReferencedAction().equals(action))) {
					found = true;
					break;
				}
			}
		}
		if (!found) {
			Action a = new Action();
			a.setReferencedAction(action);
			event.addAction(a);
		}
		return event;
	
	}
	private void removeExitEventAction(JbpmContext context, Node node, Action action) {
		Event event = node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
		if (event == null) return;
		List eventActions = new ArrayList(event.getActions());
		if ((eventActions != null) && !eventActions.isEmpty()) {
			for (int i=0; i<eventActions.size(); ++i) {
				Action a = (Action)eventActions.get(i);
				if ((a.getReferencedAction() != null) && (a.getReferencedAction().equals(action))) {
					event.removeAction(a);
					//this is necessary cause the hibernate settings try to do a cascade-delete on 
					//these associations (STUPID)
					a.setReferencedAction(null);
					a.setActionDelegation(null);
					context.getSession().delete(a);
					break;
				}
			}
		}
	}

	private Action setupAction(ProcessDefinition pD, String name, String className) {
		Action action = new Action();
		action.setName(name);
		action.setProcessDefinition(pD);
		Delegation recordDelegation = new Delegation(className);
		recordDelegation.setConfigType("bean");
		action.setActionDelegation(recordDelegation);
		return action;

	}
	public void addEntryWorkflow(WorkflowSupport entry, EntityIdentifier id, Definition workflowDef) {
		String initialState = WorkflowUtils.getInitialState(workflowDef);
		if (!Validator.isNull(initialState)) {
			//Now start the workflow at the desired initial state
			JbpmContext context=WorkflowFactory.getContext();
			ProcessDefinition pD;
			try {
		        pD = context.getGraphSession().findLatestProcessDefinition(workflowDef.getId());
		        Node node = pD.getNode(initialState);
			    if (node != null) {
		        	ProcessInstance pI = new ProcessInstance(pD);
					Token token = pI.getRootToken();
					ContextInstance cI = (ContextInstance) pI.getInstance(ContextInstance.class);
					cI.setVariable(WorkflowUtils.ENTRY_ID, id.getEntityId(), token);
					cI.setVariable(WorkflowUtils.ENTRY_TYPE, id.getEntityType().name(), token);
					//doesn't exist, add a new one
					WorkflowState ws = new WorkflowState();
					ws.setTokenId(new Long(token.getId()));
					ws.setState(initialState);
					ws.setDefinition(workflowDef);
					//need to save explicitly - actions called by the node.enter may look it up 
					getCoreDao().save(ws);
					entry.addWorkflowState(ws);
					//Start the workflow process at the initial state
				    ExecutionContext executionContext = new ExecutionContext(token);
		            node.enter(executionContext);
		            context.save(pI);
			    }
		    } catch (Exception ex) {
		        throw convertJbpmException(ex);
		    } finally {
		    	context.close();
		    }
		}
	}
	
	public void modifyWorkflowState(Long tokenId, String fromState, String toState) {
		JbpmContext context=WorkflowFactory.getContext();
	    try {
        	Token t = context.loadTokenForUpdate(tokenId.longValue());
            t.signal(fromState + "." + toState);
            context.save(t);
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	}

	public void modifyWorkflowStateOnTimeout(Long timerId) {
		JbpmContext context=WorkflowFactory.getContext();
	    try {
	    	
    		SchedulerSession schedulerSession = context.getSchedulerSession();
    		Timer timer = (Timer)context.getSession().load(Timer.class, timerId);
    		if (timer == null) return;
    		Token token = timer.getToken();
    		Entry entry = null;
    		if (token != null) {
    			//	token id is id of workflowState
    			WorkflowState ws = (WorkflowState)getCoreDao().load(WorkflowState.class, new Long(token.getId()));
    			entry = (Entry)ws.getOwner().getEntity();
        		//only process timers in current zone
        		if (!ws.getDefinition().getZoneName().equals(RequestContextHolder.getRequestContext().getZoneName())) return;
    		}
  
    		// execute
    		timer.execute();
    		//re-index for state changes
    		if (entry != null) {
    			EntryProcessor processor = 
    				(EntryProcessor) getProcessorManager().getProcessor(entry.getParentBinder(), 
        						EntryProcessor.PROCESSOR_KEY);
    			processor.reindexEntry(entry);
    		}
    		// if there was an exception, just save the timer
    		if (timer.getException()==null) {
    			// 	if repeat is specified
    			if (timer.getRepeat()!=null) {
    				// update timer by adding the repeat duration
    				Date dueDate = timer.getDueDate();
          
    				// suppose that it took the timer runner thread a 
    				// very long time to execute the timers.
    				// then the repeat action dueDate could already have passed.
    				while (dueDate.getTime()<=System.currentTimeMillis()) {
    					dueDate = businessCalendar
    					.add(dueDate, 
        					new Duration(timer.getRepeat()));
    				}
    				timer.setDueDate( dueDate );
    				// save the updated timer in the database
    				logger.debug("saving updated timer for repetition '"+timer+"' in '"+(dueDate.getTime()-System.currentTimeMillis())+"' millis");
    				schedulerSession.saveTimer(timer);
    			} else {
    				// delete this timer
    				logger.debug("deleting timer '"+timer+"'");
    				schedulerSession.deleteTimer(timer);
    			}
    		}
    		if (token != null) context.save(token);
        } finally {
        	context.close();
        }
		
	}
	
	public void deleteEntryWorkflow(WorkflowSupport entry) {
		//Delete all JBPM tokens and process instances associated with this entry
		JbpmContext context=WorkflowFactory.getContext();
	    try {
			Set processInstances = new HashSet();
	  		Set workflowStates = entry.getWorkflowStates();
   			for (Iterator iter=workflowStates.iterator(); iter.hasNext();) {
				WorkflowState ws = (WorkflowState)iter.next();
				Token t = context.loadToken(ws.getTokenId().longValue());
				//Remember all of the unique process instances that we have to delete
				//tokens may belong to the same PI
				processInstances.add(t.getProcessInstance());
			}
			//Now delete the process instances used by this entry
			for (Iterator iter=processInstances.iterator(); iter.hasNext();) {
				ProcessInstance pI = (ProcessInstance)iter.next();
				pI.end();
				context.getSchedulerSession().cancelTimersForProcessInstance(pI);
				context.getGraphSession().deleteProcessInstance(pI);
			}
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	}
	
	public void deleteWorkflowToken(WorkflowState ws) {
		JbpmContext context=WorkflowFactory.getContext();
	    try {
	    	Token t = context.loadTokenForUpdate(ws.getTokenId().longValue());
	    	t.end();
	    } finally {
	    	context.close();
	    }
	}
	
}
