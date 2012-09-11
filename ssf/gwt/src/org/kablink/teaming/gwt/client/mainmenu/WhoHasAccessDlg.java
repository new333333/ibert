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
package org.kablink.teaming.gwt.client.mainmenu;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements Vibe's Who Has Access dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class WhoHasAccessDlg extends DlgBox {
	private GwtTeamingMainMenuImageBundle	m_images;		// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;		// Access to Vibe's messages.
	private EntityId						m_entityId;		// EntityId of the entity whose access is being viewed.
	private VerticalPanel					m_vp;			//

	/*
	 * Inner class that wraps items displayed in the dialog's content.
	 */
	private class DlgLabel extends InlineLabel {
		/**
		 * Constructor method.
		 * 
		 * @param label
		 * @param title
		 */
		public DlgLabel(String label, String title) {
			super(label);
			if (GwtClientHelper.hasString(title)) {
				setTitle(title);
			}
			addStyleName("vibe-whoHasAccessDlg_Label");
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param label
		 */
		public DlgLabel(String label) {
			// Always use the initial form of the method.
			this(label, null);
		}
	}

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private WhoHasAccessDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);

		// ...initialize everything else...
		m_images   = GwtTeaming.getMainMenuImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.mainMenuWhoHasAccessDlgHeader(),
			getSimpleSuccessfulHandler(),	// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),		// The dialog's EditCanceledHandler.
			null);							// Create callback data.  Unused. 
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
		// Create and return a panel to hold the dialog's content.
		m_vp = new VerticalPanel();
		m_vp.addStyleName("vibe-whoHasAccessDlg_Content");
		return m_vp;
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
//!		...this needs to be implemented...
		populateFromWhoHasAccessInfoAsync();
	}

	/*
	 * Asynchronously populates the dialog from a
	 * WhoHasAccessInfoRpcResponseData object.
	 */
	private void populateFromWhoHasAccessInfoAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populateFromWhoHasAccessInfoNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the dialog from a
	 * WhoHasAccessInfoRpcResponseData object.
	 */
	private void populateFromWhoHasAccessInfoNow() {
		// Clear the current content of the content panel.
		m_vp.clear();
		
//!		...this needs to be implemented...
		m_vp.add(new DlgLabel("...this needs to be implemented..."));
		
		// Show the dialog (perhaps again) so that it can be positioned
		// correctly based on its new content.
		show(true);
	}
	
	/*
	 * Asynchronously runs the given instance of the who has access
	 * dialog.
	 */
	private static void runDlgAsync(final WhoHasAccessDlg whaDlg, final EntityId entityId) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				whaDlg.runDlgNow(entityId);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the who has access
	 * dialog.
	 */
	private void runDlgNow(EntityId entityId) {
		// Store the parameter...
		m_entityId = entityId;

		// ...and start populating the dialog and show it.
		populateDlgAsync();
		show(true);
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the who has access dialog and perform some operation on it.   */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the who has access dialog
	 * asynchronously after it loads. 
	 */
	public interface WhoHasAccessDlgClient {
		void onSuccess(WhoHasAccessDlg whaDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the WhoHasAccessDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final WhoHasAccessDlgClient whaDlgClient,
			
			// initAndShow parameters,
			final WhoHasAccessDlg	whaDlg,
			final EntityId			entityId) {
		GWT.runAsync(WhoHasAccessDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_WhoHasAccessDlg());
				if (null != whaDlgClient) {
					whaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != whaDlgClient) {
					// Yes!  Create it and return it via the callback.
					WhoHasAccessDlg whaDlg = new WhoHasAccessDlg();
					whaDlgClient.onSuccess(whaDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(whaDlg, entityId);
				}
			}
		});
	}
	
	/**
	 * Loads the WhoHasAccessDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param whaDlgClient
	 */
	public static void createAsync(WhoHasAccessDlgClient whaDlgClient) {
		doAsyncOperation(whaDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the who has access to an entity dialog.
	 * 
	 * @param whaDlg
	 * @param entityId
	 */
	public static void initAndShow(WhoHasAccessDlg whaDlg, EntityId entityId) {
		doAsyncOperation(null, whaDlg, entityId);
	}
}
