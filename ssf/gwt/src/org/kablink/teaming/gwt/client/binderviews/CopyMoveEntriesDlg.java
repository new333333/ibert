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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.CopyEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.CopyMoveEntriesCmdBase;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.MoveEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntryId;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ProgressDlg;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;
import org.kablink.teaming.gwt.client.widgets.ProgressBar;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements Vibe's copy/move multiple entries dialog.
 *  
 * @author drfoster@novell.com
 */
public class CopyMoveEntriesDlg extends DlgBox
	implements EditSuccessfulHandler,
	// Event handlers implemented by this class.
		SearchFindResultsEvent.Handler
{
	private boolean						m_doCopy;					// true -> The dialog is doing a copy.  false -> It's doing a move.
	private FindCtrl					m_findControl;				// The search widget.
	private GwtFolder					m_recentDest;				// Tracks the last destination used.  If a new folder isn't selected, this will be used.
	private GwtFolder					m_selectedDest;				// The currently selected folder returned by the search widget.
	private GwtTeamingMessages 			m_messages;					// Access to Vibe's string resource.
	private InlineLabel					m_progressIndicator;		// Label containing the 'x of y' progress indicator.
	private int							m_totalDone;				// Tracks the number of entries that have been copied/moved while the operation is in progress.
	private List<EntryId>				m_entryIds;					// Current list of entry IDs to be copied/moved.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private ProgressBar					m_progressBar;				// Progress bar shown as part of progress handling.
	private VibeFlowPanel				m_progressPanel;			// Panel containing the progress indicator.
	private VibeVerticalPanel			m_vp;						// The panel holding the dialog's content.

	// The following manage the strings used by the dialog.  The map is
	// loaded with the appropriate strings from the resource bundle for
	// the dialog based on whether it's in copy or move mode each time
	// the dialog is run.  See initDlgStrings().
	private enum StringIds{
		CAPTION1,
		CAPTION2,
		CURRENT_DEST,
		CURRENT_DEST_NONE,
		ERROR_INVALID_SEARCH,
		ERROR_OP_FAILURE,
		ERROR_TARGET_IN_SOURCE_ALL,
		ERROR_TARGET_IN_SOURCE_SOME,
		HEADER,
		RPC_FAILURE,
		SELECT_DEST,
		WARNING_NO_DEST,
	}
	private Map<StringIds, String> m_strMap;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static final TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		// Search events.
		TeamingEvents.SEARCH_FIND_RESULTS,
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private CopyMoveEntriesDlg() {
		// Initialize the superclass...
		super(false, true);
		
		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			"",							// No caption yet.  It's set appropriately when the dialog runs.
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCancledHandler.
			null);						// Create callback data.  Unused. 
	}

	/*
	 * Constructs and returns an Image with a spinner in it.
	 */
	private Image buildSpinnerImage(String style) {
		Image reply = new Image(GwtTeaming.getImageBundle().spinner16());
		reply.getElement().setAttribute("align", "absmiddle");
		if (GwtClientHelper.hasString(style)) {
			reply.addStyleName(style);
		}
		return reply;
	}

	/*
	 * Returns a List<EntryId> clone of the original list.
	 */
	private List<EntryId> cloneEntryIds(List<EntryId> entryIds) {
		List<EntryId> reply = new ArrayList<EntryId>();
		for (EntryId entryId:  entryIds) {
			reply.add(new EntryId(entryId.getBinderId(), entryId.getEntryId()));
		}
		return reply;
	}
	
	/*
	 * Asynchronously performs the copy/move of the entries.
	 */
	private void copyMoveEntriesAsync(
			final CopyMoveEntriesCmdBase cmd,
			final GwtFolder              targetFolder,
			final List<EntryId>          sourceEntryIds,
			final int                    totalEntryCount,
			final List<String>           collectedErrors) {
		ScheduledCommand doCopyMove = new ScheduledCommand() {
			@Override
			public void execute() {
				copyMoveEntriesNow(
					cmd,
					targetFolder,
					sourceEntryIds,
					totalEntryCount,
					collectedErrors);
			}
		};
		Scheduler.get().scheduleDeferred(doCopyMove);
	}
	
	/*
	 * Synchronously performs the copy/move of the entries.
	 */
	private void copyMoveEntriesNow(
			final CopyMoveEntriesCmdBase cmd,
			final GwtFolder              targetFolder,
			final List<EntryId>          sourceEntryIds,
			final int                    totalEntryCount,
			final List<String>           collectedErrors) {
		// Do we need to send the request to copy/move in chunks?  (We
		// do if we've already been sending chunks or the source list
		// contains more items then our threshold.)
		boolean cmdIsChunkList = (cmd.getEntryIds() != sourceEntryIds);
		if (cmdIsChunkList || ProgressDlg.needsChunking(sourceEntryIds.size())) {
			// Yes!  If we're not showing the progress bar or panel
			// yet...
			if ((!(m_progressPanel.isVisible())) || (!(m_progressBar.isVisible()))) {
				// ...show them now.
				m_progressBar.setVisible(  true);
				m_progressPanel.setVisible(true);
				updateProgress(0, totalEntryCount);
			}
			
			// Make sure we're using a separate list for the chunks
			// vs. the source list that we're copying.
			List<EntryId> chunkList;
			if (cmdIsChunkList) {
				chunkList = cmd.getEntryIds();
				chunkList.clear();
			}
			else {
				chunkList = new ArrayList<EntryId>();
				cmd.setEntryIds(chunkList);
			}
			
			// Scan the entry IDs to be copied/moved...
			while(true) {
				// ...moving each entry ID from the source list into
				// ...the chunk list.
				chunkList.add(sourceEntryIds.get(0));
				sourceEntryIds.remove(0);
				
				// Was that the last entry to be copied/moved?
				if (sourceEntryIds.isEmpty()) {
					// Yes!  Break out of the loop and let the chunk
					// get handled as if we weren't sending by chunks.
					break;
				}
				
				// Have we reached the size we chunk things at?
				if (ProgressDlg.isChunkFull(chunkList.size())) {
					// Yes!  Send this chunk.  Note that this is a
					// recursive call and will come back through this
					// method for the next chunk.
					copyMoveEntriesImpl(
						cmd,
						targetFolder,
						sourceEntryIds,
						totalEntryCount,
						collectedErrors,
						true);	// true -> This is a one of multiple chunks to be copied/moved.
					
					return;
				}
			}
		}

		// Do we have any entries to be copied/moved?
		if (!(cmd.getEntryIds().isEmpty())) {
			// Yes!  Perform the final move/copy.
			copyMoveEntriesImpl(
				cmd,
				targetFolder,
				sourceEntryIds,
				totalEntryCount,
				collectedErrors,
				false);	// false -> This is the last copy/move that needs to happen.
		}
	}

	/*
	 * Sends an RPC request with a copy/move command that either copies
	 * everything or simply the next chunk in a sequence of chunks.
	 */
	private void copyMoveEntriesImpl(
			final CopyMoveEntriesCmdBase cmd,
			final GwtFolder              targetFolder,
			final List<EntryId>          sourceEntryIds,
			final int                    totalEntryCount,
			final List<String>           collectedErrors,
			final boolean                moreRemaining) {
		// Send the request to copy/move the entries.
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_strMap.get(StringIds.RPC_FAILURE));
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Did everything we ask get copied/moved?
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData());
				List<String> chunkErrors = responseData.getErrorList();
				int chunkErrorCount = ((null == chunkErrors) ? 0 : chunkErrors.size());
				if (0 < chunkErrorCount) {
					// No!  Copy the errors into the List<String> we're
					// collecting them in.
					for (String chunkError:  chunkErrors) {
						collectedErrors.add(chunkError);
					}
				}

				// Did we just copy/move a part of what we need to
				// copy/move?
				if (moreRemaining) {
					// Yes!  Clear the entry ID list in the command
					// and request that the next chunk be send.
					updateProgress(cmd.getEntryIds().size(), totalEntryCount);
					copyMoveEntriesAsync(
						cmd,
						targetFolder,
						sourceEntryIds,
						totalEntryCount,
						collectedErrors);
				}
				
				else {
					// No, we didn't just copy/move part of it, but
					// everything remaining!  Did we collect any errors
					// in during the copy/move process?
					updateProgress(cmd.getEntryIds().size(), totalEntryCount);
					int totalErrorCount = collectedErrors.size();
					if (0 < totalErrorCount) {
						// Yes!  Tell the user about the problem(s).
						GwtClientHelper.displayMultipleErrors(
							m_strMap.get(StringIds.ERROR_OP_FAILURE),
							collectedErrors);
					}
					
					// If we just completed moving anything...
					if ((!m_doCopy) && totalErrorCount != totalEntryCount) {
						// ...force the content to refresh to reflect
						// ...what was moved.  We don't do this for a
						// ...copy as there's generally nothing showing
						// ...that needs to be changed.
						FullUIReloadEvent.fireOne();
					}
					
					// Finally, save the target folder we just
					// copied/moved to and close the dialog, we're
					// done!
					m_recentDest = targetFolder;
					hide();
				}
			}
		});
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
		// Create and return a vertical panel to hold the dialog's
		// content.
		m_vp = new VibeVerticalPanel();
		m_vp.addStyleName("vibe-cmeDlg_RootPanel");
		return m_vp;
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
		// Do we have a target folder to work with?
		GwtFolder targetFolder = ((null == m_selectedDest) ? m_recentDest : m_selectedDest);
		if (null == targetFolder) {
			// No!  The user must select one before they can press OK.
			// Tell them about the problem and bail.
			GwtClientHelper.deferredAlert(m_strMap.get(StringIds.WARNING_NO_DEST));
			return false;
		}
		
		// Yes, we have a target folder to work with!  Are we trying to
		// copy/move any entries from the target folder?
		Long targetFolderId = Long.parseLong(targetFolder.getFolderId());
		int  sourceIsTargetCount = getFolderReferenceCount(m_entryIds, targetFolderId);
		if (0 < sourceIsTargetCount) {
			// Yes!  Tell the user about the problem and bail.
			String msg;
			if (sourceIsTargetCount == m_entryIds.size())
			     msg = m_strMap.get(StringIds.ERROR_TARGET_IN_SOURCE_ALL);
			else msg = m_strMap.get(StringIds.ERROR_TARGET_IN_SOURCE_SOME);
			GwtClientHelper.deferredAlert(msg);
			return false;
		}

		// No, the target is a unique folder!  Create the appropriate
		// copy/move command...
		List<EntryId> sourceEntryIds  = cloneEntryIds(m_entryIds);	// We use a clone because we manipulate the list's contents during the copy/move.
		int           totalEntryCount = sourceEntryIds.size();		// Total number of entries that we're starting with.
		List<String>  collectedErrors = new ArrayList<String>();
		
		CopyMoveEntriesCmdBase cmd;
		if (m_doCopy)
		     cmd = new CopyEntriesCmd(targetFolderId, sourceEntryIds);
		else cmd = new MoveEntriesCmd(targetFolderId, sourceEntryIds);
		
		// ...and start the operation.
		m_totalDone = 0;
		setOkEnabled(false);
		copyMoveEntriesAsync(
			cmd,
			targetFolder,
			sourceEntryIds,
			totalEntryCount,
			collectedErrors);
		
		// Return false.  We'll close the dialog manually if/when the
		// copy/move completes.
		return false;
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
		// Put the focus in the search widget.
		return ((null == m_findControl) ? null : m_findControl.getFocusWidget());
	}

	/*
	 * Returns a count of the number of entries in a List<EntryId>
	 * that refer to a given folder ID.
	 */
	private int getFolderReferenceCount(List<EntryId> entryIds, Long targetFolderId) {
		int reply = 0;
		for (EntryId entryId:  entryIds) {
			if (entryId.getBinderId().equals(targetFolderId)) {
				reply += 1;
			}
		}
		return reply;
	}
	
	/*
	 * Initialize the Map of the strings used by the dialog based its
	 * current mode.
	 */
	private void initDlgStrings() {
		// Start with an empty map...
		if (null == m_strMap)
		     m_strMap = new HashMap<StringIds, String>();
		else m_strMap.clear();

		// ...and put the appropriate strings into it.
		if (m_doCopy) {
			m_strMap.put(StringIds.CAPTION1,                    m_messages.copyEntriesDlgCaption1());
			m_strMap.put(StringIds.CAPTION2,                    m_messages.copyEntriesDlgCaption2());
			m_strMap.put(StringIds.CURRENT_DEST,                m_messages.copyEntriesDlgCurrentDestination());
			m_strMap.put(StringIds.CURRENT_DEST_NONE,           m_messages.copyEntriesDlgCurrentDestinationNone());
			m_strMap.put(StringIds.ERROR_INVALID_SEARCH,        m_messages.copyEntriesDlgErrorInvalidSearchResult());
			m_strMap.put(StringIds.ERROR_OP_FAILURE,            m_messages.copyEntriesDlgErrorCopyFailures());
			m_strMap.put(StringIds.ERROR_TARGET_IN_SOURCE_ALL,  m_messages.copyEntriesDlgErrorTargetInSourceAll());
			m_strMap.put(StringIds.ERROR_TARGET_IN_SOURCE_SOME, m_messages.copyEntriesDlgErrorTargetInSourceSome());
			m_strMap.put(StringIds.HEADER,                      m_messages.copyEntriesDlgHeader());
			m_strMap.put(StringIds.RPC_FAILURE,                 m_messages.rpcFailure_CopyEntries());
			m_strMap.put(StringIds.SELECT_DEST,                 m_messages.copyEntriesDlgSelectDestination());
			m_strMap.put(StringIds.WARNING_NO_DEST,             m_messages.copyEntriesDlgWarningNoSelection());
		}
		
		else {
			m_strMap.put(StringIds.CAPTION1,                    m_messages.moveEntriesDlgCaption1());
			m_strMap.put(StringIds.CAPTION2,                    m_messages.moveEntriesDlgCaption2());
			m_strMap.put(StringIds.CURRENT_DEST,                m_messages.moveEntriesDlgCurrentDestination());
			m_strMap.put(StringIds.CURRENT_DEST_NONE,           m_messages.moveEntriesDlgCurrentDestinationNone());
			m_strMap.put(StringIds.ERROR_INVALID_SEARCH,        m_messages.moveEntriesDlgErrorInvalidSearchResult());
			m_strMap.put(StringIds.ERROR_OP_FAILURE,            m_messages.moveEntriesDlgErrorMoveFailures());
			m_strMap.put(StringIds.ERROR_TARGET_IN_SOURCE_ALL,  m_messages.moveEntriesDlgErrorTargetInSourceAll());
			m_strMap.put(StringIds.ERROR_TARGET_IN_SOURCE_SOME, m_messages.moveEntriesDlgErrorTargetInSourceSome());
			m_strMap.put(StringIds.HEADER,                      m_messages.moveEntriesDlgHeader());
			m_strMap.put(StringIds.RPC_FAILURE,                 m_messages.rpcFailure_MoveEntries());
			m_strMap.put(StringIds.SELECT_DEST,                 m_messages.moveEntriesDlgSelectDestination());
			m_strMap.put(StringIds.WARNING_NO_DEST,             m_messages.moveEntriesDlgWarningNoSelection());
		}
	}
	
	/*
	 * Asynchronously loads the find control.
	 */
	private void loadPart1Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the find control.
	 */
	private void loadPart1Now() {
		FindCtrl.createAsync(this, GwtSearchCriteria.SearchType.FOLDERS, new FindCtrlClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(FindCtrl findCtrl) {
				m_findControl = findCtrl;
				m_findControl.addStyleName("vibe-cmeDlg_FindWidget");
				
				populateDlgAsync();
			}
		});
	}
	
	/**
	 * Called when the dialog is attached.
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
	 * Called when the dialog is detached.
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
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults(SearchFindResultsEvent event) {
		// If the find results aren't for the move entries dialog...
		if (!(((Widget) event.getSource()).equals(this))) {
			// ...ignore the event.
			return;
		}
		
		// Hide the search results list.
		m_findControl.hideSearchResults();

		// Is the search result a GwtFolder?
		GwtTeamingItem obj = event.getSearchResults();
		if (obj instanceof GwtFolder) {
			// Yes!  Save it for when the user selects OK.
			m_selectedDest = ((GwtFolder) obj);
		}
		else {
			// No, it's not a GwtFolder!  Whatever it is, we can't
			// handle it.
			Window.alert(m_strMap.get(StringIds.ERROR_INVALID_SEARCH));
		}
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
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_vp.clear();
		
		// Add the caption...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-cmeDlg_CaptionPanel");
		m_vp.add(fp);
		InlineLabel il = new InlineLabel(m_strMap.get(StringIds.CAPTION1));
		il.addStyleName("vibe-cmeDlg_CaptionLeft");
		il.setWordWrap(false);
		fp.add(il);
		il = new InlineLabel(m_strMap.get(StringIds.CAPTION2));
		il.addStyleName("vibe-cmeDlg_CaptionRight");
		il.setWordWrap(false);
		fp.add(il);

		// ...add the search widget...
		fp = new VibeFlowPanel();
		fp.addStyleName("vibe-cmeDlg_FindPanel");
		m_vp.add(fp);
		il = new InlineLabel(m_strMap.get(StringIds.SELECT_DEST));
		il.addStyleName("vibe-cmeDlg_FindLabel");
		il.setWordWrap(false);
		fp.add(il);
		fp.add(m_findControl);

		// ...add information about any recent destination...
		fp = new VibeFlowPanel();
		fp.addStyleName("vibe-cmeDlg_DestPanel");
		m_vp.add(fp);
		il = new InlineLabel(m_strMap.get(StringIds.CURRENT_DEST));
		il.addStyleName("vibe-cmeDlg_DestLabel");
		il.setWordWrap(false);
		fp.add(il);
		il = new InlineLabel((null != m_recentDest) ? m_recentDest.getFolderName() : m_strMap.get(StringIds.CURRENT_DEST_NONE));
		il.addStyleName("vibe-cmeDlg_Dest");
		il.setWordWrap(false);
		if (null != m_recentDest) {
			String pName = m_recentDest.getParentBinderName();
			if (GwtClientHelper.hasString(pName)) {
				il.setTitle(pName);
			}
		}
		fp.add(il);

		// ...add a progress bar...
		m_progressBar = new ProgressBar(0, m_entryIds.size());
		m_progressBar.addStyleName("vibe-cmeDlg_ProgressBar");
		m_vp.add(m_progressBar);
		m_progressBar.setVisible(false);
		
		// ...and a panel for displaying progress, when needed...
		m_progressPanel = new VibeFlowPanel();
		m_progressPanel.addStyleName("vibe-cmeDlg_ProgressPanel");
		m_vp.add(m_progressPanel);
		m_progressPanel.setVisible(false);
		m_progressPanel.add(buildSpinnerImage("vibe-cmeDlg_ProgressSpinner"));
		m_progressIndicator = new InlineLabel("");
		m_progressIndicator.addStyleName("vibe-cmeDlg_ProgressLabel");
		m_progressPanel.add(m_progressIndicator);

		// ...and finally, make sure the buttons are enabled and show
		// ...the dialog.
		setCancelEnabled(true);
		setOkEnabled(    true);
		show(true);
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
				REGISTERED_EVENTS,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Asynchronously runs the given instance of the move entries
	 * dialog.
	 */
	private static void runDlgAsync(final CopyMoveEntriesDlg cmeDlg, final boolean doCopy, final FolderType folderType, final List<EntryId> entryIds) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				cmeDlg.runDlgNow(doCopy, folderType, entryIds);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the move entries
	 * dialog.
	 */
	private void runDlgNow(boolean doCopy, FolderType folderType, List<EntryId> entryIds) {
		// Store the parameters...
		m_doCopy   = doCopy;
		m_entryIds = entryIds;
		
		// ...initialize any other data members...
		m_selectedDest = null;
		initDlgStrings();
		setCaption(m_strMap.get(StringIds.HEADER));

		// ...and populate the dialog.
		loadPart1Async();
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

	/*
	 * Called up update the progress indicator in the dialog.
	 */
	private void updateProgress(int justCompleted, int totalEntryCount) {
		// If we're done...
		m_totalDone += justCompleted;
		if (m_totalDone == totalEntryCount) {
			// ...hide the progress bar and panel.
			m_progressBar.setVisible(  false);
			m_progressPanel.setVisible(false);
		}
		else {
			// ...otherwise, set the number we've completed.
			m_progressBar.setMaxProgress(totalEntryCount);
			m_progressBar.setProgress(   m_totalDone    );
			m_progressIndicator.setText(
				(m_doCopy                                                                         ?
					m_messages.copyEntriesDlgProgress(m_totalDone, totalEntryCount) :
					m_messages.moveEntriesDlgProgress(m_totalDone, totalEntryCount)));
		}
	}
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the move entries dialog and perform some operation on it.     */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the move entries dialog
	 * asynchronously after it loads. 
	 */
	public interface CopyMoveEntriesDlgClient {
		void onSuccess(CopyMoveEntriesDlg cmeDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the CopyMoveEntriesDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final CopyMoveEntriesDlgClient cmeDlgClient,
			
			// initAndShow parameters,
			final CopyMoveEntriesDlg cmeDlg,
			final boolean            doCopy,
			final FolderType         folderType,
			final List<EntryId>      entryIds) {
		GWT.runAsync(CopyMoveEntriesDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_CopyMoveEntriesDlg());
				if (null != cmeDlgClient) {
					cmeDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != cmeDlgClient) {
					// Yes!  Create it and return it via the callback.
					CopyMoveEntriesDlg cmeDlg = new CopyMoveEntriesDlg();
					cmeDlgClient.onSuccess(cmeDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(cmeDlg, doCopy, folderType, entryIds);
				}
			}
		});
	}
	
	/**
	 * Loads the CopyMoveEntriesDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param cmeDlgClient
	 */
	public static void createAsync(CopyMoveEntriesDlgClient cmeDlgClient) {
		doAsyncOperation(cmeDlgClient, null, false, null, null);
	}
	
	/**
	 * Initializes and shows the move entries dialog.
	 * 
	 * @param cmeDlg
	 * @param doCopy
	 * @param folderType
	 * @param entryIds
	 */
	public static void initAndShow(CopyMoveEntriesDlg cmeDlg, boolean doCopy, FolderType folderType, List<EntryId> entryIds) {
		doAsyncOperation(null, cmeDlg, doCopy, folderType, entryIds);
	}
}
