/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.datatable.AddFilesDlg;
import org.kablink.teaming.gwt.client.datatable.AddFilesDlg.AddFilesDlgClient;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetTaskDisplayDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TaskDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.tasklisting.TaskListing;
import org.kablink.teaming.gwt.client.tasklisting.TaskListing.TaskListingClient;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Task folder view.
 * 
 * @author drfoster@novell.com
 */
public class TaskFolderView extends FolderViewBase
	implements
	// Event handlers implemented by this class.
		ContributorIdsRequestEvent.Handler,
		InvokeDropBoxEvent.Handler
{
	private AddFilesDlg						m_addFilesDlg;				//
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private TaskDisplayDataRpcResponseData	m_taskDisplayData;			// The task display data read from the server.
	private TaskListing						m_taskListing;				// The TaskList composite.

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		TeamingEvents.INVOKE_DROPBOX,
	};
	
	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 */
	public TaskFolderView(BinderInfo folderInfo, ViewReady viewReady) {
		// Simply initialize the super class.
		super(folderInfo, viewReady, "vibe-taskFolder", false);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public TaskDisplayDataRpcResponseData getTaskDisplayData() {return m_taskDisplayData;}
	
	/**
	 * Returns true if the entry viewer should include next/previous
	 * buttons and false otherwise. 
	 *
	 * Overrides the FolderViewBase.allowNextPrevOnEntryView() method.
	 * 
	 * @return
	 */
	@Override
	protected boolean allowNextPrevOnEntryView() {
		// By default, the task folder doesn't allow next/previous.
		return false;
	}
	
	/**
	 * Called to construct the view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView() {
		loadPart1Async();
	}

	/**
	 * Returns the adjustment to used for a folder view's content so
	 * that it doesn't get a vertical scroll bar.
	 * 
	 * Overrides the FolderViewBase.getNoVScrollAdjustment() method.
	 * 
	 * @return
	 */
	@Override
	public int getNoVScrollAdjustment() {
		return (super.getNoVScrollAdjustment() + 10);
	}

	/**
	 * Returns true for panels that are to be included and false
	 * otherwise.
	 * 
	 * Overrides the FolderViewBase.includePanel() method.
	 * 
	 * @param folderPanel
	 * 
	 * @return
	 */
	@Override
	protected boolean includePanel(FolderPanels folderPanel) {
		// In the task folder view, we show the task graphs panel
		// beyond the default.
		boolean reply;
		switch (folderPanel) {
		case TASK_GRAPHS:  reply = true;                             break;
		default:           reply = super.includePanel(folderPanel);  break;
		}
		return reply;
	}

	/*
	 * Asynchronously loads the task display data.
	 */
	private void loadPart1Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the task display data.
	 */
	private void loadPart1Now() {
		GetTaskDisplayDataCmd cmd = new GetTaskDisplayDataCmd(getFolderInfo().getBinderIdAsLong());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Handle the failure...
				String error = m_messages.rpcFailure_GetTaskDisplayData();
				GwtClientHelper.handleGwtRPCFailure(caught, error);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				m_taskDisplayData = ((TaskDisplayDataRpcResponseData) result.getResponseData());
				loadPart2Async();
			}			
		});
	}

	/*
	 * Asynchronously loads the TaskListing.
	 */
	private void loadPart2Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the TaskListing.
	 */
	private void loadPart2Now() {
		// Load the task listing's split point.
		TaskListing.createAsync(
				this,
				new TaskListingClient() {				
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(TaskListing taskListing) {
				m_taskListing = taskListing;
				loadPart3Async();
			}
		});
	}

	/*
	 * Asynchronously initializes the TaskGraphsPanel.
	 */
	private void loadPart3Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart3Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously initializes the TaskGraphsPanel.
	 */
	private void loadPart3Now() {
		// If we can find the task graphs panel...
		TaskGraphsPanel tgp = getTaskGraphsPanel();
		if (null != tgp) {
			// ...give it what it needs to render...
			TaskGraphsPanel.initAndShow(
				tgp,
				m_taskListing,
				m_taskDisplayData.getExpandGraphs());
		}

		// ...and populate the rest of the view.
		populateViewAsync();
	}

	/**
	 * Called when the task folder view is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Handles ContributorIdsRequestEvent's received by this class.
	 * 
	 * Implements the ContributorIdsRequestEvent.Handler.onContributorIdsRequest() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContributorIdsRequest(ContributorIdsRequestEvent event) {
		if (null != m_taskListing) {
			m_taskListing.handleContributorIdsRequest(event);
		}
	}
	
	/**
	 * Called when the task folder view is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Handles InvokeDropBoxEvent's received by this class.
	 * 
	 * Implements the InvokeDropBoxEvent.Handler.onInvokeDropBox() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeDropBox(InvokeDropBoxEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Invoke the add file dialog on the folder.
			// Have we instantiated an add files dialog yet?
			if (null == m_addFilesDlg) {
				// No!  Instantiate one now.
				AddFilesDlg.createAsync(new AddFilesDlgClient() {			
					@Override
					public void onUnavailable() {
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}
					
					@Override
					public void onSuccess(final AddFilesDlg afDlg) {
						// ...and show it.
						m_addFilesDlg = afDlg;
						ScheduledCommand doShow = new ScheduledCommand() {
							@Override
							public void execute() {
								showAddFilesDlgNow();
							}
						};
						Scheduler.get().scheduleDeferred(doShow);
					}
				});
			}
			
			else {
				// Yes, we've instantiated an add files dialog already!
				// Simply show it.
				showAddFilesDlgNow();
			}
		}
	}
	
	/*
	 * Asynchronously populates the the task view.
	 */
	private void populateViewAsync() {
		Scheduler.ScheduledCommand doPopulate = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				populateViewNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the the task view.
	 */
	private void populateViewNow() {
		getFlowPanel().add(m_taskListing);
		viewReady();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we having allocated a list to track events we've
		// registered yet...
		if (null == m_registeredEventHandlers) {
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if (m_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				m_registeredEvents,
				this,
				m_registeredEventHandlers);
		}
	}

	/**
	 * Called from the base class to reset the content of this
	 * discussion folder view.
	 * 
	 * Implements the FolderViewBase.resetView() method.
	 */
	@Override
	public void resetView() {
		getFlowPanel().clear();
		populateViewAsync();
	}
	
	/**
	 * Synchronously sets the size of the view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView() {
		m_taskListing.resize();
	}

	/*
	 * Synchronously shows the add files dialog.
	 */
	private void showAddFilesDlgNow() {
		AddFilesDlg.initAndShow(
			m_addFilesDlg,
			getFolderInfo(),
			getEntryMenuPanel().getAddFilesMenuItem());
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ((null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty()))) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
	
	/**
	 * Called when everything about the view (tool panels, ...) is
	 * complete.
	 * 
	 * Overrides the FolderViewBase.viewComplete() method.
	 */
	@Override
	public void viewComplete() {
		// Tell the task listing to resize itself now that it can
		// determine how big everything is.
		m_taskListing.resize();
	}
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the task folder view and perform some operation on it.        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Loads the TaskFolderView split point and returns an instance of
	 * it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final BinderInfo folderInfo, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(TaskFolderView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				TaskFolderView dfView = new TaskFolderView(folderInfo, viewReady);
				vClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_TaskFolderView());
				vClient.onUnavailable();
			}
		});
	}
}
