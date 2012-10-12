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

import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase.ToolPanelClient;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.rpc.shared.FolderEntryDetailsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderEntryDetailsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNextPreviousFolderEntryInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.ViewFolderEntryInfoRpcResponseData;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.ViewFolderEntryInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.ContentControl;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Class that holds the folder entry viewer.
 * 
 * @author drfoster@novell.com
 */
public class FolderEntryComposite extends ResizeComposite	
	implements FolderEntryCallback, ToolPanelReady,
		// Event handlers implemented by this class.
		ContributorIdsRequestEvent.Handler,
		ShareSelectedEntriesEvent.Handler
{
	public static final boolean SHOW_GWT_ENTRY_VIEWER	= false;	// DRF:  Leave false on checkin until it's finished.

	private boolean							m_compositeReady;			// Set true once the composite and all its components are ready.
	private boolean							m_isDialog;					// true -> The composite is hosted in a dialog.  false -> It's hosted in a view.
	private FolderEntryDetails				m_fed;						// Details about the folder entry being viewed.
	private FooterPanel						m_footerPanel;				//
	private GwtTeamingDataTableImageBundle	m_images;					// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private int								m_readyComponents;			// Components that are ready, incremented as they callback.
	private Label							m_caption;					// 
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private ViewReady						m_viewReady;				// Stores a ViewReady created for the classes that extends it.
	private VibeFlowPanel 					m_captionImagePanel;		// A panel holding an image in the caption, if one is required.
	private VibeFlowPanel					m_contentPanel;				// The panel containing the composite's main content.
	private VibeFlowPanel					m_rootPanel;				// The panel containing everything about the composite.
	private ViewFolderEntryInfo				m_vfei;						// The view information for the folder entry being viewed.

	private final static int MINIMUM_CONTENT_HEIGHT	= 150;	// The minimum height (in pixels) of the composite's content panel.
	private final static int FOOTER_ADJUST_DLG		=  20;	// Height adjustment required for adequate spacing below the footer when hosted in a dialog.
	private final static int FOOTER_ADJUST_VIEW		=  30;	// Height adjustment required for adequate spacing below the footer when hosted in a view.
	
	// Number of components to coordinate with during construction:
	// - Header;
	// - Menu;
	// - Document;
	// - Comments; and
	// - Footer.
	private final static int FOLDER_ENTRY_COMPONENTS = 5;	//
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		TeamingEvents.SHARE_SELECTED_ENTRIES,
	};
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FolderEntryComposite(ViewFolderEntryInfo vfei, DlgBox dialog, ViewReady viewReady) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_vfei      = vfei;
		m_viewReady = viewReady;
		
		// ...initialize the data members requiring it...
		m_isDialog = (null != dialog);
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
		
		// ...create the base content panels...
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-folderEntryComposite-rootPanel");
		createCaption(m_rootPanel, ((null == dialog) ? null : dialog.getHeaderPanel()));
		m_contentPanel = new VibeFlowPanel();
		m_contentPanel.addStyleName("vibe-folderEntryComposite-contentPanel");
		m_rootPanel.add(m_contentPanel);
		initWidget(m_rootPanel);

		// ...and continue building the composite.
		loadPart1Async();
	}

	/*
	 * Checks the components as they callback and finishes things
	 * once everybody is ready.
	 */
	private void checkReadyness() {
		// Is everything ready?
		if (FOLDER_ENTRY_COMPONENTS == m_readyComponents) {
			// Yes!  Tell the view and resize as appropriate.
			m_compositeReady = true;
			m_viewReady.viewReady();
			onResizeAsync();
		}
	}
	
	/*
	 * Creates the dialog/view caption for the composite.
	 */
	private void createCaption(VibeFlowPanel rootPanel, Panel container) {
		// Do we have the header from a dialog container?
		if (null != container) {
			// Yes!  Clear its contents so we can create our own.
			container.clear();
		}
		
		else {
			// No, we don't have the header from a dialog!  Create one
			// using the styles used in a dialog's header.
			container = new VibeFlowPanel();
			container.setStyleName("teamingDlgBoxHeader");
			if (GwtClientHelper.jsIsIE())
			     container.addStyleName("teamingDlgBoxHeaderBG_IE"   );
			else container.addStyleName("teamingDlgBoxHeaderBG_NonIE");
		}

		// Create a panel to hold an image displayed in the caption, if
		// there will be one.
		m_captionImagePanel = new VibeFlowPanel();
		m_captionImagePanel.setStyleName("teamingDlgBoxHeader-captionImagePanel");
		container.add(m_captionImagePanel);
		
		// Create the label with the entry's title.
		m_caption = new Label(m_vfei.getTitle());
		m_caption.setStyleName("teamingDlgBoxHeader-captionLabel");
		container.add(m_caption);

		// Create the widgets that appear at the right end of the caption.
		createCaptionRight(container);

		// Finally, add the caption panel to the root panel.
		rootPanel.add(container);
	}

	/*
	 * Creates a close button in the caption for non-dialog hosts.
	 */
	private void createCaptionClose(Panel container) {
		// Dialog's hosting this composite already have a close.  Is
		// this one hosted by a dialog?
		if (!m_isDialog) {
			// No!  Create a panel to hold the close widgets.
			VibeFlowPanel closePanel = new VibeFlowPanel();
			closePanel.addStyleName("vibe-folderEntryComposite-closePanel");
			closePanel.setTitle(m_messages.folderEntry_Alt_Close());
			container.add(closePanel);

			// Create a handler to close the view when clicked.
			ClickHandler closeClick = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// To close the view, we simply pop the previous
					// URL and goto that.  Note that we have to protect
					// against the case where the URL is a permalink to
					// an entry as navigating to it again, would simply
					// re-launch the entry viewer.  Hence the ignore.
					String url = ContentControl.getContentHistoryUrl(-1);
					url = GwtClientHelper.appendUrlParam(url, "ignoreEntryView", "1");
					OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(url, Instigator.GOTO_CONTENT_URL);
					if (GwtClientHelper.validateOSBI(osbInfo)) {
						GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
					}
				}
			};

			// Create a close label...
			InlineLabel close = new InlineLabel(m_messages.folderEntry_Close());
			close.addStyleName("vibe-folderEntryComposite-closeLabel");
			close.addClickHandler(closeClick);
			closePanel.add(close);
			
			// ...and an 'X' image to close it.
			Image closeX = GwtClientHelper.buildImage(m_images.closeX());
			closeX.addStyleName("vibe-folderEntryComposite-closeImg");
			closeX.addClickHandler(closeClick);
			closePanel.add(closeX);
		}
	}
	
	/*
	 * Creates the widgets used to navigate between entries being
	 * viewed. 
	 */
	private void createCaptionNavigation(Panel container) {
		// Create a panel to hold the navigation buttons.
		VibeFlowPanel navPanel = new VibeFlowPanel();
		navPanel.addStyleName("vibe-folderEntryComposite-navPanel");
		if (!m_isDialog) {
			navPanel.addStyleName("padding15R");
		}
		container.add(navPanel);

		// Create the previous button.
		Image button = GwtClientHelper.buildImage(m_images.previousTeal());
		button.addStyleName("vibe-folderEntryComposite-navPrevImg");
		button.setTitle(m_messages.folderEntry_Alt_Previous());
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doNavigateAsync(true);	// true -> Previous
			}
		});
		navPanel.add(button);
		
		// Create the next button.
		button = GwtClientHelper.buildImage(m_images.nextTeal());
		button.addStyleName("vibe-folderEntryComposite-navNextImg");
		button.setTitle(m_messages.folderEntry_Alt_Next());
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doNavigateAsync(false);	// false -> Next.
			}
		});
		navPanel.add(button);
	}

	/*
	 * Creates a panel that lives at the right edge of the caption.
	 */
	private void createCaptionRight(Panel container) {
		// Create the right aligned panel
		VibeFlowPanel rightPanel = new VibeFlowPanel();
		rightPanel.addStyleName("vibe-folderEntryComposite-rightPanel");
		container.add(rightPanel);

		//
		createCaptionNavigation(rightPanel);
		
		// Add a close button when necessary.
		createCaptionClose(rightPanel);
	}

	/*
	 * Asynchronously navigates to the previous/next entry.
	 */
	private void doNavigateAsync(final boolean previous) {
		ScheduledCommand doNavigate = new ScheduledCommand() {
			@Override
			public void execute() {
				doNavigateNow(previous);
			}
		};
		Scheduler.get().scheduleDeferred(doNavigate);
	}
	
	/*
	 * Synchronously navigates to the previous/next entry.
	 */
	private void doNavigateNow(final boolean previous) {
		// Can we get the previous/next entry navigate to?
		GetNextPreviousFolderEntryInfoCmd cmd = new GetNextPreviousFolderEntryInfoCmd(m_vfei.getEntityId(), previous);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetNextPreviousFolderEntryInfo());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				ViewFolderEntryInfoRpcResponseData responseData = ((ViewFolderEntryInfoRpcResponseData) response.getResponseData());
				ViewFolderEntryInfo vfei = responseData.getViewFolderEntryInfo();
				if (null == vfei) {
					// No!  Tell the user about the problem.
					String error;
					if (previous)
					     error = m_messages.folderEntry_Error_NoPrevious();
					else error = m_messages.folderEntry_Error_NoNext();
					GwtClientHelper.deferredAlert(error);
				}
				
				else {
					// Yes!  We've got the previous/next entry.
					// Load it.
					m_vfei = vfei;
					m_caption.setText(m_vfei.getTitle());
					m_contentPanel.clear();
					loadPart1Async();
				}
			}
		});
	}

	/*
	 * Returns the footer adjustment to use for the content panel.
	 */
	private int getFooterAdjust() {
		int reply;
		if (m_isDialog)
		     reply = FOOTER_ADJUST_DLG;
		else reply = FOOTER_ADJUST_VIEW;
		return reply;
	}

	/*
	 * Returns true if a List<EntityId> contains a single EntityId that
	 * matches the one loaded in the entry viewer and false otherwise.
	 */
	private boolean isCompositeEntry(List<EntityId> eidList) {
		return (
			GwtClientHelper.hasItems(eidList) &&
			(1 == eidList.size()) &&
			eidList.get(0).equalsEntityId(
				m_vfei.getEntityId()));
	}
	
	/*
	 * Asynchronously loads the next part of the composite.
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
	 * Synchronously loads the next part of the composite.
	 */
	private void loadPart1Now() {
		FooterPanel.createAsync(this, m_vfei.getEntityId(), this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				m_footerPanel = ((FooterPanel) tpb);
				m_footerPanel.addStyleName("vibe-folderEntryComposite-footerPanel");
				m_rootPanel.add(m_footerPanel);
				loadPart2Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the composite.
	 */
	private void loadPart2Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the next part of the composite.
	 */
	private void loadPart2Now() {
		// Can we get the previous/next entry navigate to?
		GetFolderEntryDetailsCmd cmd = new GetFolderEntryDetailsCmd(m_vfei.getEntityId());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetFolderEntryDetails());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				FolderEntryDetailsRpcResponseData responseData = ((FolderEntryDetailsRpcResponseData) response.getResponseData());
				m_fed = responseData.getFolderEntryDetails();
				loadPart3Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the composite.
	 */
	private void loadPart3Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart3Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the next part of the composite.
	 */
	private void loadPart3Now() {
		// Create and add the various components to the composite.
		m_contentPanel.add(new FolderEntryHeader(  this, m_fed)                  );
		m_contentPanel.add(new FolderEntryMenu(    this, m_fed.getToolbarItems()));
		m_contentPanel.add(new FolderEntryDocument(this, m_fed.getHtmlView())    );
		m_contentPanel.add(new FolderEntryComments(this, m_fed.getComments())    );
	}

	/**
	 * Called when the data table is attached.
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
	public void onContributorIdsRequest(final ContributorIdsRequestEvent event) {
//!		...this needs to be implemented...

		// Convert the contributor IDs from a String[] to a
		// List<Long>...
		final List<Long> contributorIds = new ArrayList<Long>();
		String[] contributors = m_fed.getContributors();
		if ((null != contributors) && (0 < contributors.length)) {
			for (String contributor:  contributors) {
				contributorIds.add(new Long(Long.parseLong(contributor)));
			}
		}
		
		// ...and asynchronously fire the corresponding reply event it.
		GwtTeaming.fireEventAsync(
			new ContributorIdsReplyEvent(
				event.getBinderId(),
				contributorIds));
	}
	
	/**
	 * Called when the data table is detached.
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
	 * Synchronously sets the size of the composite based on its
	 * position in the view.
	 * 
	 * Overrides the ViewBase.onResize() method.
	 */
	@Override
	public void onResize() {
		// Pass the resize on to the super class...
		super.onResize();
		
		// ...and do what we need to do locally.
		onResizeImpl();
	}
	
	/*
	 * Asynchronously sets the size of the data table based on its
	 * position in the view.
	 */
	private void onResizeAsync(int delay) {
		if (0 == delay) {
			ScheduledCommand doResize = new ScheduledCommand() {
				@Override
				public void execute() {
					onResize();
				}
			};
			Scheduler.get().scheduleDeferred(doResize);
		}
		
		else {
			Timer timer = new Timer() {
				@Override
				public void run() {
					onResize();
				}
			};
			timer.schedule(delay);
		}
	}

	/*
	 * Asynchronously sets the size of the data table based on its
	 * position in the view.
	 */
	private void onResizeAsync() {
		onResizeAsync(FolderViewBase.INITIAL_RESIZE_DELAY);
	}
	
	/*
	 * Re-sizes / re-lays out the widgets in the component.
	 */
	private void onResizeImpl() {
		Widget	container    = getParent();									// Widget (dialog or view) containing the composite.
		int		cHeight      = container.getOffsetHeight();					// Height       of the container.
		int		cTop         = container.getAbsoluteTop();					// Absolute top of the container.		
		int		contentTop   = (m_contentPanel.getAbsoluteTop() - cTop);	// Top of the composite's main content panel relative to the top of its container.		
		int		footerHeight = m_footerPanel.getOffsetHeight();				// Height of the composite's footer panel.

		// What's the optimal height for the content panel?
		int contentHeight = (((cHeight - contentTop) - footerHeight) - getFooterAdjust());
		if (MINIMUM_CONTENT_HEIGHT > contentHeight) {
			// Too small!  Use the minimum even though this may result
			// in a vertical scroll bar.
			contentHeight = MINIMUM_CONTENT_HEIGHT;
			container.addStyleName("vibe-verticalScroll");
		}
		
		else {
			container.removeStyleName("vibe-verticalScroll");
		}
		
		// Set the height of the content panel.
		m_contentPanel.setHeight(contentHeight + "px");
	}

	/**
	 * Handles ShareSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the ShareSelectedEntriesEvent.Handler.onShareSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShareSelectedEntries(ShareSelectedEntriesEvent event) {
		// Is the event targeted to this entry?
		final List<EntityId> sharedEntities = event.getSelectedEntities();
		if (isCompositeEntry(sharedEntities)) {
			// Yes!  Run the share dialog on it.
			Scheduler.ScheduledCommand doShare = new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					BinderViewsHelper.shareEntities(sharedEntities);
				}
			};
			Scheduler.get().scheduleDeferred(doShare);
		}
	}
	
	/*
	 * Coordinates things as the components of the composite become ready.
	 */
	private void partReady() {
		if (!m_compositeReady) {
			m_readyComponents += 1;
			checkReadyness();
		}
		
		else {
			GwtClientHelper.debugAlert("FolderEntryComposite.partReady( *Internal Error* ):  Unexpected call to partReady() method.");
		}
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

	/*
	 * Updates the caption's image.
	 */
	@SuppressWarnings("unused")
	private void setCaptionImage(Image captionImg) {
		if (null != captionImg) {
			m_captionImagePanel.clear();
			m_captionImagePanel.add(captionImg);
			m_captionImagePanel.addStyleName("padding5R");
		}
	}
	
	/**
	 * Implements the ToolPanelReady.toolPanelReady() method.
	 */
	@Override
	public void toolPanelReady(ToolPanelBase toolPanel) {
		partReady();
	}

	/**
	 * Implements the FolderEntryCallback.viewComponentReady() method.
	 */
	@Override
	public void viewComponentReady() {
		partReady();
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the folder entry composite and perform some operation on it.  */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface used to interact with the composite asynchronously
	 * after it loads. 
	 */
	public interface FolderEntryCompositeClient {
		void onSuccess(FolderEntryComposite fec);
		void onUnavailable();
	}
	
	/**
	 * Loads the FolderEntryComposite split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param fecClient
	 * @param dialog
	 * @param vfei
	 * @param viewReady
	 */
	public static void createAsync(final FolderEntryCompositeClient fecClient, final DlgBox dialog, final ViewFolderEntryInfo vfei, final ViewReady viewReady) {
		GWT.runAsync(FolderEntryComposite.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_FolderEntryComposite());
				fecClient.onUnavailable();
			}

			@Override
			public void onSuccess() {
				FolderEntryComposite fec = new FolderEntryComposite(vfei, dialog, viewReady);
				fecClient.onSuccess(fec);
			}
		});
	}
}
