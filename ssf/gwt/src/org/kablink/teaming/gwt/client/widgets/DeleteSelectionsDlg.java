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
import org.kablink.teaming.gwt.client.binderviews.util.DeletePurgeEntriesHelper;
import org.kablink.teaming.gwt.client.binderviews.util.DeletePurgeEntriesHelper.DeletePurgeEntriesCallback;
import org.kablink.teaming.gwt.client.util.DeleteSelectionsMode;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.SelectionDetails;
import org.kablink.teaming.gwt.client.util.SelectionDetailsHelper;
import org.kablink.teaming.gwt.client.util.SelectionDetailsHelper.SelectionDetailsCallback;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * Implements the delete selections dialog.
 *  
 * @author drfoster@novell.com
 */
public class DeleteSelectionsDlg extends DlgBox implements EditSuccessfulHandler {
	public final static boolean USE_NEW_DELETE_DIALOG	= false;	//! DRF (20130718):  Leave false on checkin until it's all working.
	
	private boolean						m_isFilr;			// true -> We're in Filr mode.  false -> We're not.
	private DialogMode					m_dialogMode;		// The mode the dialog is running in.  Defines what's displayed on the dialog.
	private DeletePurgeEntriesCallback	m_dpeCallback;		//
	private FlexCellFormatter			m_cellFormatter;	// The formatter to control how m_grid is laid out.
	private FlexTable					m_grid;				// The table holding the dialog's content.
	private GwtTeamingMessages			m_messages;			// Access to our localized strings.
	private List<EntityId>				m_entityIds;		// The entities to be deleted.
	private RadioButton					m_purgeRB;			// The 'Delete from system' radio button.
	private RadioButton					m_trashRB;			// The 'Move to trash'      radio button.
	private SelectionDetails			m_selectionDetails;	// Populated via a GWT RPC call during the population of the dialog's contents.  Contains an analysis of what m_entityIds refers to.

	/*
	 * Enumeration type used to define how the dialog prompts the user.
	 *  
	 * See the following for where the definitions of these come from:
	 *    https://teaming.innerweb.novell.com/ssf/a/c/p_name/ss_forum/p_action/1/action/view_permalink/entityType/folderEntry/entryId/422728/vibeonprem_url/1
	 */
	private enum DialogMode {
		Situation1,	// Selections contain only items from personal storage.
		Situation2,	// Selections contain only remote (i.e., Cloud Folder, Net Folder or Vibe Mirrored Folder) items.
		Situation3	// Selections contain a mixture of personal storage and remote items.
	}
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private DeleteSelectionsDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.OkCancel);

		// ...initialize everything else...
		m_isFilr   = GwtClientHelper.isLicenseFilr();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.deleteSelectionsDlgHeader(),	// The dialog's header.
			this,									// The dialog's EditSuccessfulHandler.
			DlgBox.getSimpleCanceledHandler(),		// The dialog's EditCanceledHandler.
			null);									// Create callback data.  Unused. 
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
		// Create and return a table to hold the dialog's content.
		m_grid = new VibeFlexTable();
		m_grid.addStyleName("vibe-deleteSelectionsDlg-rootPanel");
		m_grid.setCellSpacing(4);
		m_cellFormatter = m_grid.getFlexCellFormatter();
		return m_grid;
	}

	/*
	 * Asynchronously deletes the selected items.
	 */
	private void doDeletesAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doDeletesNow();
			}
		});
	}
	
	/*
	 * Synchronously deletes the selected items.
	 */
	private void doDeletesNow() {
		// Determine how to perform the delete...
		DeleteSelectionsMode dsMode;
		switch (m_dialogMode) {
		default:
		case Situation1:
		case Situation3:
			if (m_purgeRB.getValue()) {
				dsMode = DeleteSelectionsMode.PURGE_ALL;
			}
			
			else {
				if (DialogMode.Situation1.equals(m_dialogMode))
				     dsMode = DeleteSelectionsMode.TRASH_ALL;
				else dsMode = DeleteSelectionsMode.TRASH_ADHOC_PURGE_OTHERS;
			}
			break;
			
		case Situation2:
			dsMode = DeleteSelectionsMode.PURGE_ALL;
			break;
		}

		// ...and do it.
		DeletePurgeEntriesHelper.deleteSelectedEntriesAsync(
			m_entityIds,
			dsMode,
			m_dpeCallback);
	}
	
	/**
	 * Called is the user selects the dialog's Ok button.
	 * 
	 * @param callbackData
	 */
	@Override
	public boolean editSuccessful(Object callbackData) {
		// Start deleting the selections and return false to keep the
		// dialog open.  It will be closed once the deletions have
		// completed.
		doDeletesAsync();
		return true;
	}
	
	/**
	 * Unused.
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
	private void loadPart1Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void loadPart1Now() {
		SelectionDetailsHelper.getSelectionDetails(m_entityIds, new SelectionDetailsCallback() {
			@Override
			public void onFailure() {
				// Nothing to do.  getSelectionDetails() will have told
				// the user about it's problems.
			}

			@Override
			public void onSuccess(SelectionDetails selectionDetails) {
				// Store the details and continue populating the
				// dialog.
				m_selectionDetails = selectionDetails;
				setDialogMode();
				populateDlgAsync();
			}
		});
	}
	
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_grid.removeAllRows();
		
		// ...forget any previous widgets...
		m_trashRB =
		m_purgeRB = null;

		// ...and repopulate the dialog.
		switch (m_dialogMode) {
		case Situation1:  populateSituation1(); break;
		case Situation2:  populateSituation2(); break;
		case Situation3:  populateSituation3(); break;
		}
		
		// ...and show the dialog.
		center();
	}

	/*
	 * Dialog has two radio buttons:
	 *    - Move to trash (default); and
	 *    - Delete from system (and a warning that the operation cannot
	 *      be undone.)
	 */
	private void populateSituation1() {
		// Add the 'Move to trash' radio button...
		m_trashRB = new RadioButton("deleteMode");
		m_trashRB.addStyleName("vibe-deleteSelectionsDlg-radio");
		m_trashRB.setValue(true);
		m_grid.setWidget(0, 0, m_trashRB);
		
		m_grid.setText(              0, 1, m_messages.deleteSelectionsDlgLabel_Trash());
		m_cellFormatter.addStyleName(0, 1, "vibe-deleteSelectionsDlg-radioLabel");
		m_cellFormatter.setWordWrap( 0, 1, false);
		
		// ...add the 'Delete from system' radio button...
		m_purgeRB = new RadioButton("deleteMode");
		m_purgeRB.addStyleName("vibe-deleteSelectionsDlg-radio");
		m_grid.setWidget(1, 0, m_purgeRB);
		
		m_grid.setText(              1, 1, m_messages.deleteSelectionsDlgLabel_Purge());
		m_cellFormatter.addStyleName(1, 1, "vibe-deleteSelectionsDlg-radioLabel");
		m_cellFormatter.setWordWrap( 1, 1, false);
		
		// ...and add the warning about the delete.
		m_grid.setText(              2, 1, m_messages.deleteSelectionsDlgWarning_CantUndo());
		m_cellFormatter.addStyleName(2, 1, "vibe-deleteSelectionsDlg-warningSituation1");
	}
	
	/*
	 * Dialog has no options.
	 * 
	 * Contains only a warning that the operation cannot be undone.
	 */
	private void populateSituation2() {
		m_grid.setText(              0, 0, m_messages.deleteSelectionsDlgLabel_PurgeOnly());
		m_cellFormatter.addStyleName(0, 0, "vibe-deleteSelectionsDlg-purgeNote");
		
		// ...and add a warning about the delete.
		m_grid.setText(              1, 0, m_messages.deleteSelectionsDlgWarning_CantUndo());
		m_cellFormatter.addStyleName(1, 0, "vibe-deleteSelectionsDlg-warningSituation2");
	}
	
	/*
	 * Dialog has two radio buttons:
	 *    - Move items in personal storage to trash and delete
	 *      everything else from system (default); and
	 *    - Delete everything from system.
	 *
	 * Both options will have a warning that deleting items from the
	 * system cannot be undone.
	 */
	private void populateSituation3() {
		// Add the 'Move items in personal storage to trash and delete
		// everything else from system' radio button...
		m_trashRB = new RadioButton("deleteMode");
		m_trashRB.addStyleName("vibe-deleteSelectionsDlg-radio");
		m_trashRB.setValue(true);
		m_grid.setWidget(0, 0, m_trashRB);
		
		m_grid.setText(              0, 1, m_messages.deleteSelectionsDlgLabel_TrashAdHoc());
		m_cellFormatter.addStyleName(0, 1, "vibe-deleteSelectionsDlg-radioLabel");
		m_cellFormatter.setWordWrap( 0, 1, false);
		
		// ...add the 'Delete everything from system' radio button...
		m_purgeRB = new RadioButton("deleteMode");
		m_purgeRB.addStyleName("vibe-deleteSelectionsDlg-radio");
		m_grid.setWidget(1, 0, m_purgeRB);
		
		m_grid.setText(              1, 1, m_messages.deleteSelectionsDlgLabel_PurgeAll());
		m_cellFormatter.addStyleName(1, 1, "vibe-deleteSelectionsDlg-radioLabel");
		m_cellFormatter.setWordWrap( 1, 1, false);
		
		// ...and add the warning about the deletes.
		m_grid.setText(              2, 0, m_messages.deleteSelectionsDlgWarning_CantUndo());
		m_cellFormatter.addStyleName(2, 0, "vibe-deleteSelectionsDlg-warningSituation3");
		m_cellFormatter.setColSpan(  2, 0, 2);
	}
	
	/*
	 * Asynchronously runs the given instance of the delete selections
	 * dialog.
	 */
	private static void runDlgAsync(final DeleteSelectionsDlg dsDlg, final List<EntityId> entityIds, final DeletePurgeEntriesCallback dpeCallback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				dsDlg.runDlgNow(entityIds, dpeCallback);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the delete selections
	 * dialog.
	 */
	private void runDlgNow(final List<EntityId> entityIds, final DeletePurgeEntriesCallback dpeCallback) {
		// Store the parameters and populate the dialog.
		m_entityIds   = entityIds;
		m_dpeCallback = dpeCallback;
		loadPart1Async();
	}

	/*
	 * Maps the information in the dialog's SelectionDetails to a
	 * DialogMode.
	 * 
	 * See the following for where the definitions of these come from:
	 *     https://teaming.innerweb.novell.com/ssf/a/c/p_name/ss_forum/p_action/1/action/view_permalink/entityType/folderEntry/entryId/422728/vibeonprem_url/1
	 */
	private void setDialogMode() {
		if (!(m_selectionDetails.hasRemoteSelections())) {
			m_dialogMode = DialogMode.Situation1;
		}
		
		else {
			if ((!(m_selectionDetails.hasAdHocEntries())) && (!(m_selectionDetails.hasAdHocFolders()))) {
				m_dialogMode = DialogMode.Situation2;
			}
			
			else {
				if (m_isFilr || (!(m_selectionDetails.hasFolders())))
				     m_dialogMode = DialogMode.Situation3;
				else m_dialogMode = DialogMode.Situation2;
			}
		}
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the delete selections dialog and perform some operation on    */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the delete selections dialog
	 * asynchronously after it loads. 
	 */
	public interface DeleteSelectionsDlgClient {
		void onSuccess(DeleteSelectionsDlg dsDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the DeleteSelectionsDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final DeleteSelectionsDlgClient	dsDlgClient,
			
			// initAndShow parameters,
			final DeleteSelectionsDlg			dsDlg,
			final List<EntityId>				entityIds,
			final DeletePurgeEntriesCallback	dpeCallback) {
		GWT.runAsync(DeleteSelectionsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_DeleteSelectionsDlg());
				if (null != dsDlgClient) {
					dsDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != dsDlgClient) {
					// Yes!  Create it and return it via the callback.
					DeleteSelectionsDlg dsDlg = new DeleteSelectionsDlg();
					dsDlgClient.onSuccess(dsDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(dsDlg, entityIds, dpeCallback);
				}
			}
		});
	}
	
	/**
	 * Loads the DeleteSelectionsDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param dsDlgClient
	 */
	public static void createAsync(DeleteSelectionsDlgClient dsDlgClient) {
		// Invoke the appropriate asynchronous operation.
		doAsyncOperation(dsDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the delete selections dialog.
	 * 
	 * @param dsDlg
	 * @param entityIds
	 * @param dpeCallback
	 */
	public static void initAndShow(DeleteSelectionsDlg dsDlg, List<EntityId> entityIds, DeletePurgeEntriesCallback dpeCallback) {
		// Invoke the appropriate asynchronous operation.
		doAsyncOperation(null, dsDlg, entityIds, dpeCallback);
	}
}
