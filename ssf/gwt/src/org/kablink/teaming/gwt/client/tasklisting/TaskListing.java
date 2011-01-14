/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.gwt.client.tasklisting;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingTaskListingImageBundle;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.widgets.ClickablePanel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Implements a GWT based task folder list user interface.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class TaskListing {
	private ActionHandler						m_actionHandler;	// Call to handle the TeamingActions generated by this TaskListing.
	private boolean								m_sortDescend;		// true -> Sort is descending.  false -> Sort is ascending. 
	private Element								m_taskListingDIV;	// The <DIV> in the content pane that's to contain the task listing.
	private GwtTeamingMessages					m_messages   = GwtTeaming.getMessages();				//
	private GwtRpcServiceAsync					m_rpcService = GwtTeaming.getRpcService();				// 
	private GwtTeamingTaskListingImageBundle	m_images     = GwtTeaming.getTaskListingImageBundle();	//
	private Long								m_binderId;			// The ID of the binder containing the tasks to be listed.
	private String								m_filterType;		// The current filtering in affect, if any.
	private String								m_mode;				// The current mode being displayed (PHYSICAL vs. VITRUAL.)
	private String								m_sortBy;			// The column the tasks are currently sorted by.
	private TaskBundle							m_taskBundle;		// The TaskLinkage and List<TaskListItem> that we're listing.

	/**
	 * Class constructor.
	 * 
	 * @param taskListingDIV
	 * @param binderId
	 * @param filterType
	 * @param mode
	 * @param sortBy
	 * @param sortDescend
	 */
	public TaskListing(Element taskListingDIV, ActionHandler actionHandler, Long binderId, String filterType, String mode, String sortBy, boolean sortDescend) {
		// Simply store the parameters.
		m_taskListingDIV = taskListingDIV;
		m_actionHandler  = actionHandler;
		m_binderId       = binderId;
		m_filterType     = filterType;
		m_mode           = mode;
		m_sortBy         = sortBy;
		m_sortDescend    = sortDescend;
	}

	/*
	 * Renders a List<TaskListItem> in to the task listing DIV.
	 */
	private void render(List<TaskListItem> taskItemList) {
		// Scan the List<TaskListItem>...
		for (TaskListItem taskItem:  taskItemList) {
			// ...rendering each TaskListItem into the task listing.
			renderTaskItem(taskItem);
		}
	}

	// Renders a TaskListItem into the task listing.
	private void renderTaskItem(final TaskListItem taskItem) {
//!		...this needs to be implemented...
		Anchor ta = new Anchor();
		ta.addStyleName("cursorPointer");
		ClickablePanel cp = new ClickablePanel(ta.getElement());
		cp.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				viewTask(taskItem.getTask());
			}});
		ta.getElement().appendChild(new InlineLabel(taskItem.getTask().getTitle()).getElement());
		m_taskListingDIV.appendChild(ta.getElement());
		m_taskListingDIV.appendChild(new InlineLabel(":  ...this needs to be implemented...").getElement());
		m_taskListingDIV.appendChild(new HTML().getElement());
		
		// Does this TaskListItem contain any subtasks?
		List<TaskListItem> subtasks = taskItem.getSubtasks();
		if ((null != subtasks) && (!(subtasks.isEmpty()))) {
			// Yes!  Render them.
			render(subtasks);
		}
	}
	
	/**
	 * Show's the task listing based on the parameters passed into the
	 * constructor.
	 */
	public void show() {
		GwtTeaming.getRpcService().getTaskBundle(HttpRequestInfo.createHttpRequestInfo(), m_binderId, m_filterType, m_mode, new AsyncCallback<TaskBundle>() {
			@Override
			public void onFailure(Throwable t) {
				// Handle the failure...
				String error = m_messages.rpcFailure_GetTaskList();
				GwtClientHelper.handleGwtRPCFailure(t, error);
				
				// ...and display the error as the task listing.
				GwtClientHelper.removeAllChildren(m_taskListingDIV);
				m_taskListingDIV.appendChild(new InlineLabel(error).getElement());
			}

			@Override
			public void onSuccess(TaskBundle result) {
				// Clear the task listing DIV's contents and render the
				// task list.
				GwtClientHelper.removeAllChildren(m_taskListingDIV);
				m_taskBundle = result;
				render(result.getTasks());
			}			
		});		
	}

	/*
	 * Called to run the entry viewer on the given task.
	 */
	private void viewTask(final TaskInfo task) {
		m_rpcService.getViewFolderEntryUrl(HttpRequestInfo.createHttpRequestInfo(), task.getBinderId(), task.getTaskId(), new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetViewFolderEntryUrl(),
					String.valueOf(task.getTaskId()));
			}
			
			@Override
			public void onSuccess(String viewFolderEntryUrl) {
				m_actionHandler.handleAction(
					TeamingAction.SHOW_FORUM_ENTRY,
					viewFolderEntryUrl);
			}
		});
	}	
}
