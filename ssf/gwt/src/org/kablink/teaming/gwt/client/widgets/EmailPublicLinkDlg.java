/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Implements Vibe's Email Public Link dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class EmailPublicLinkDlg extends DlgBox implements EditSuccessfulHandler {
	private GwtTeamingMessages	m_messages;		// Access to Vibe's messages.
	private List<EntityId>		m_entityIds;	// List<EntityId> of the entities whose public links are to be e-mailed.
	private VibeFlowPanel		m_mainPanel;	//
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EmailPublicLinkDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.OkCancel);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			"",							// Dialog caption set when the dialog runs.
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Create callback data.  Unused. 
	}

	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData (unused)
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName("vibe-emailPublicLinkDlg-content");
		return m_mainPanel;
	}

	/**
	 * This method gets called when user user presses the OK push
	 * button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object callbackData) {
//!		...this needs to be implemented...
		return true;
	}
	
	/**
	 * Returns the edited List<FavoriteInfo>.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Unused.
		return "";
	}

	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return null;
	}

	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Clear the dialog's current contents...
		m_mainPanel.clear();

		// ...recreate its contents...
//!		...this needs to be implemented...
		m_mainPanel.add(new InlineLabel("...this needs to be implemented..."));

		// ...and show it centered on the screen.
		center();;
	}

	/*
	 * Asynchronously runs the given instance of the e-mail public link
	 * dialog.
	 */
	private static void runDlgAsync(final EmailPublicLinkDlg eplDlg, final String caption, final List<EntityId> entityIds) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				eplDlg.runDlgNow(caption, entityIds);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the e-mail public link
	 * dialog.
	 */
	private void runDlgNow(String caption, List<EntityId> entityIds) {
		// Store the parameters...
		setCaption(caption);
		m_entityIds = entityIds;

		// ...and populate the dialog.
		populateDlgAsync();
	}

	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the e-mail public link dialog and perform some operation on   */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the e-mail public link
	 * dialog asynchronously after it loads. 
	 */
	public interface EmailPublicLinkDlgClient {
		void onSuccess(EmailPublicLinkDlg eplDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the EmailPublicLinkDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final EmailPublicLinkDlgClient eplDlgClient,
			
			// initAndShow parameters,
			final EmailPublicLinkDlg	eplDlg,
			final String				caption,
			final List<EntityId>		entityIds) {
		GWT.runAsync(EmailPublicLinkDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_EmailPublicLinkDlg());
				if (null != eplDlgClient) {
					eplDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != eplDlgClient) {
					// Yes!  Create it and return it via the callback.
					EmailPublicLinkDlg eplDlg = new EmailPublicLinkDlg();
					eplDlgClient.onSuccess(eplDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(eplDlg, caption, entityIds);
				}
			}
		});
	}
	
	/**
	 * Loads the EmailPublicLinkDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param eplDlgClient
	 */
	public static void createAsync(EmailPublicLinkDlgClient eplDlgClient) {
		doAsyncOperation(eplDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the e-mail public link dialog.
	 * 
	 * @param eplDlg
	 * @param entityIds
	 */
	public static void initAndShow(EmailPublicLinkDlg eplDlg, String caption, List<EntityId> entityIds) {
		doAsyncOperation(null, eplDlg, caption, entityIds);
	}
}
