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

import org.kablink.teaming.gwt.client.event.ActivityStreamEnterEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.GetManageMenuPopupEvent;
import org.kablink.teaming.gwt.client.event.GetManageMenuPopupEvent.ManageMenuPopupCallback;
import org.kablink.teaming.gwt.client.event.HideManageMenuEvent;
import org.kablink.teaming.gwt.client.event.TreeNodeCollapsedEvent;
import org.kablink.teaming.gwt.client.event.TreeNodeExpandedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.ManageMenuPopup;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl.TreeMode;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl.WorkspaceTreeControlClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Class used for the content of the bread crumb tree in the binder
 * views.  
 * 
 * @author drfoster@novell.com
 */
public class BreadCrumbPanel extends ToolPanelBase
	implements
	// Event handlers implemented by this class.
		TreeNodeCollapsedEvent.Handler,
		TreeNodeExpandedEvent.Handler
{
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private ManageMenuPopup				m_selectorConfigPopup;		//
	private VibeFlowPanel				m_fp;						// The panel holding the content.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.TREE_NODE_COLLAPSED,
		TeamingEvents.TREE_NODE_EXPANDED,
	};
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private BreadCrumbPanel(RequiresResize containerResizer, BinderInfo binderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
		// ...construct the root panel...
		VibeFlowPanel rootContainer = new VibeFlowPanel();
		rootContainer.addStyleName("vibe-binderViewTools vibe-breadCrumbRoot");

		// ...construct the bread crumb tree panel...
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-breadCrumbPanel");
		rootContainer.add(m_fp);

		// ...if required...
		if (needsWhatsNewLink()) {
			// ...construct the What's New link...
			VibeFlowPanel whatsNewPanel = new VibeFlowPanel();
			whatsNewPanel.addStyleName("vibe-breadCrumbWhatsNewPanel");
			InlineLabel whatsNewLabel = new InlineLabel(m_messages.vibeDataTable_WhatsNew());
			whatsNewLabel.addStyleName("vibe-breadCrumbWhatsNewLink");
			whatsNewLabel.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showWhatsNewAsync();
				}
			});
			whatsNewPanel.add(whatsNewLabel);
			rootContainer.add(whatsNewPanel);
		}

		// ...and tie it all together.
		initWidget(rootContainer);
		loadPart1Async();
	}

	/**
	 * Loads the BreadCrumbPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(BreadCrumbPanel.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				BreadCrumbPanel bcp = new BreadCrumbPanel(containerResizer, binderInfo, toolPanelReady);
				tpClient.onSuccess(bcp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_BreadCrumbPanel());
				tpClient.onUnavailable();
			}
		});
	}

	/*
	 * Adds access to the binder configuration menu.
	 */
	private void addProfileRootConfig(VibeFlowPanel fp) {
		// For a trash view...
		if (m_binderInfo.isBinderTrash()) {
			// ...we don't show the binder configuration menu.
			return;
		}
		
		// Create an anchor to run the configuration menu on this
		// binder.
		final Anchor  selectorConfigA  = new Anchor();
		final Element selectorConfigAE = selectorConfigA.getElement();
		selectorConfigA.setTitle(m_binderInfo.isBinderFolder() ? m_messages.treeAltConfigureFolder() : m_messages.treeAltConfigureWorkspace());
		Image selectorConfigImg = GwtClientHelper.buildImage(m_images.configOptions());
		selectorConfigImg.addStyleName("breadCrumb_ContentTail_configureImg");
		selectorConfigAE.appendChild(selectorConfigImg.getElement());
		selectorConfigA.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (null == m_selectorConfigPopup)
				     buildAndRunSelectorConfigMenuAsync(selectorConfigA);
				else runSelectorConfigMenuAsync(        selectorConfigA);
			}
		});
		fp.add(selectorConfigA);
		
		// ...and hide the manage menu in the main menu bar.
		HideManageMenuEvent.fireOneAsync();
	}
	
	/*
	 * Asynchronously builds and runs the selector configuration menu.
	 */
	private void buildAndRunSelectorConfigMenuAsync(final Anchor selectorConfigA) {
		ScheduledCommand doBuildAndRunMenu = new ScheduledCommand() {
			@Override
			public void execute() {
				buildAndRunSelectorConfigMenuNow(selectorConfigA);
			}
		};
		Scheduler.get().scheduleDeferred(doBuildAndRunMenu);
	}
	
	/*
	 * Synchronously builds and runs the selector configuration menu.
	 */
	private void buildAndRunSelectorConfigMenuNow(final Anchor selectorConfigA) {
		GwtTeaming.fireEvent(
			new GetManageMenuPopupEvent(new ManageMenuPopupCallback() {
				@Override
				public void manageMenuPopup(ManageMenuPopup mmp) {
					// Is there anything in the selector configuration
					// menu?
					m_selectorConfigPopup = mmp;
					if ((null == m_selectorConfigPopup) || (!(m_selectorConfigPopup.shouldShowMenu()))) {
						// No!  Clear the selector widget, tell the
						// user about the problem and bail.
						clearSelectorConfig();
						GwtClientHelper.deferredAlert(m_messages.treeErrorNoManageMenu());
					}
					
					else {
						// Yes, there's stuff in the selector
						// configuration menu!  Complete populating it
						// and run it.
						m_selectorConfigPopup.setCurrentBinder(m_binderInfo);
						m_selectorConfigPopup.populateMenu();
						runSelectorConfigMenuAsync(selectorConfigA);
					}
				}
			}));
	}
	
	/*
	 * Asynchronously handles the panel being resized.
	 */
	private void doResizeAsync() {
		ScheduledCommand doResize = new ScheduledCommand() {
			@Override
			public void execute() {
				doResizeNow();
			}
		};
		Scheduler.get().scheduleDeferred(doResize);
	}
	
	/*
	 * Clears an previous binder configuration panel and menu.
	 */
	private void clearSelectorConfig() {
		// Clear the previous menu.
		if (null != m_selectorConfigPopup) {
			m_selectorConfigPopup.clearItems();
			m_selectorConfigPopup = null;
		}
	}
	
	/*
	 * Synchronously handles the panel being resized.
	 */
	private void doResizeNow() {
		panelResized();
	}
	
	/*
	 * Asynchronously construct's the contents of the bread crumb
	 * panel.
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
	 * Synchronously construct's the contents of the panel.
	 */
	private void loadPart1Now() {
		// Are we displaying a bread crumb panel for a collection?
		if (m_binderInfo.isBinderCollection()) {
			// Yes!  We don't need a tree, just the image and title.
			// Create the panel for it...
			VibeFlowPanel fp = new VibeFlowPanel();
			fp.addStyleName("vibe-breadCrumbCollection-panel");

			// ...create the image...
			TreeInfo ti = new TreeInfo();
			ti.setBinderInfo(m_binderInfo);
			Image i = GwtClientHelper.buildImage(ti.getBinderImage(BinderIconSize.getBreadCrumbIconSize()).getSafeUri().asString());
			i.addStyleName("vibe-breadCrumbCollection-image");
			int width  = BinderIconSize.getBreadCrumbIconSize().getBinderIconWidth();
			if ((-1) != width) {
				i.setWidth(width + "px");
			}
			int height = BinderIconSize.getBreadCrumbIconSize().getBinderIconHeight();
			if ((-1) != height) {
				i.setHeight(height + "px");
			}
			fp.add(i);

			// ...create the title label...
			InlineLabel il = new InlineLabel(m_binderInfo.getBinderTitle());
			il.addStyleName("vibe-breadCrumbCollection-label");
			fp.add(il);

			// ...tie it all together and tell our container that we're
			// ...ready.
			m_fp.add(fp);
			toolPanelReady();
		}
		
		// No, we we aren't displaying a bread crumb panel for a
		// collection!  Are we displaying it for the profile root
		// workspace?
		else if (m_binderInfo.isBinderWorkspace() && m_binderInfo.getWorkspaceType().isProfileRoot()) {
			// Yes!  We don't need a tree, just the image and title.
			// Create the panel for it...
			VibeFlowPanel fp = new VibeFlowPanel();
			fp.addStyleName("vibe-breadCrumbProfiles-panel");

			// ...create the image...
			ImageResource iRes;
			switch (BinderIconSize.getBreadCrumbIconSize()) {
			default:
			case SMALL:   iRes = m_filrImages.profileRoot();        break;
			case MEDIUM:  iRes = m_filrImages.profileRoot_medium(); break;
			case LARGE:   iRes = m_filrImages.profileRoot_large();  break;
			}
			Image i = GwtClientHelper.buildImage(iRes.getSafeUri().asString());
			i.addStyleName("vibe-breadCrumbProfiles-image");
			int width  = BinderIconSize.getBreadCrumbIconSize().getBinderIconWidth();
			if ((-1) != width) {
				i.setWidth(width + "px");
			}
			int height = BinderIconSize.getBreadCrumbIconSize().getBinderIconHeight();
			if ((-1) != height) {
				i.setHeight(height + "px");
			}
			fp.add(i);

			// ...create the title label...
			InlineLabel il = new InlineLabel(m_messages.vibeDataTable_People());
			il.addStyleName("vibe-breadCrumbProfiles-label");
			fp.add(il);
			
			addProfileRootConfig(fp);

			// ...tie it all together and tell our container that we're
			// ...ready.
			m_fp.add(fp);
			toolPanelReady();
		}
		
		else {
			// No, we aren't displaying a bread crumb panel for the
			// profile root workspace either!  We need the full bread
			// crumb tree.
			WorkspaceTreeControl.createAsync(
					GwtTeaming.getMainPage(),
					m_binderInfo,
					m_binderInfo.isBinderTrash(),
					TreeMode.HORIZONTAL_BINDER,
					new WorkspaceTreeControlClient() {				
				@Override
				public void onUnavailable() {
					// Nothing to do other than tell our container that
					// we're ready.  The error is handled in the
					// asynchronous provider.
					toolPanelReady();
				}
				
				@Override
				public void onSuccess(WorkspaceTreeControl wsTreeCtrl) {
					// Add the tree to the panel and tell our container
					// that we're ready.
					m_fp.add(wsTreeCtrl);
					toolPanelReady();
				}
			});
		}
	}

	/*
	 * Return true if the binder being viewed requires a What's New
	 * link and false otherwise.
	 */
	private boolean needsWhatsNewLink() {
		return (!(m_binderInfo.isBinderTrash()));
	}
	
	/**
	 * Called when the accessories panel is attached to the document.
	 * 
	 * Overrides Widget.onAttach()
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the accessories panel is detached from the document.
	 * 
	 * Overrides Widget.onDetach()
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Handles TreeNodeCollapsedEvent's received by this class.
	 * 
	 * Implements the TreeNodeCollapsedEvent.Handler.onTreeNodeCollapsed()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onTreeNodeCollapsed(TreeNodeCollapsedEvent event) {
		// If this is our bread crumb tree being collapsed...
		Long binderId = event.getBinderInfo().getBinderIdAsLong();
		if ((binderId.equals(m_binderInfo.getBinderIdAsLong())) && event.getTreeMode().isHorizontalBinder()) {
			// ...tell our container about the size change.
			doResizeAsync();
		}
	}
	
	/**
	 * Handles TreeNodeExpandedEvent's received by this class.
	 * 
	 * Implements the TreeNodeExpandedEvent.Handler.onTreeNodeExpanded()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onTreeNodeExpanded(TreeNodeExpandedEvent event) {
		// If this is our bread crumb tree being expanded...
		Long binderId = event.getBinderInfo().getBinderIdAsLong();
		if ((binderId.equals(m_binderInfo.getBinderIdAsLong())) && event.getTreeMode().isHorizontalBinder()) {
			// ...tell our container about the size change.
			doResizeAsync();
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
	
	/**
	 * Called from the binder view to allow the panel to do any work
	 * required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Reset the widgets and reload the bread crumb tree.
		m_fp.clear();
		loadPart1Async();
	}
	
	/*
	 * Asynchronously runs the selector configuration menu.
	 */
	private void runSelectorConfigMenuAsync(final Anchor selectorConfigA) {
		ScheduledCommand doRunMenu = new ScheduledCommand() {
			@Override
			public void execute() {
				runSelectorConfigMenuNow(selectorConfigA);
			}
		};
		Scheduler.get().scheduleDeferred(doRunMenu);
	}
	
	/*
	 * Synchronously runs the selector configuration menu.
	 */
	private void runSelectorConfigMenuNow(final Anchor selectorConfigA) {
		final PopupMenu configureDropdownMenu = new PopupMenu(true, false, false);
		configureDropdownMenu.addStyleName("vibe-configureMenuBarDropDown");
		configureDropdownMenu.setMenu(m_selectorConfigPopup.getMenuBar());
		configureDropdownMenu.showRelativeToTarget(selectorConfigA);
	}

	/*
	 * Asynchronously runs What's New on the current binder.
	 */
	private void showWhatsNewAsync() {
		ScheduledCommand doWhatsNew = new ScheduledCommand() {
			@Override
			public void execute() {
				showWhatsNewNow();
			}
		};
		Scheduler.get().scheduleDeferred(doWhatsNew);
	}

	/*
	 * Synchronously runs What's New on the current binder.
	 */
	private void showWhatsNewNow() {
		// Are we viewing a collection?
		ActivityStreamInfo asi = new ActivityStreamInfo();
		if (m_binderInfo.isBinderCollection()) {
			// Yes!  Determine the appropriate collection
			// ActivityStream to view.
			ActivityStream as;
			switch (m_binderInfo.getCollectionType()) {
			default:
			case MY_FILES:        as = ActivityStream.MY_FILES;       break;
			case NET_FOLDERS:     as = ActivityStream.NET_FOLDERS;    break;
			case SHARED_BY_ME:    as = ActivityStream.SHARED_BY_ME;   break;
			case SHARED_WITH_ME:  as = ActivityStream.SHARED_WITH_ME; break;
			}
			asi.setActivityStream(as);
		}
		
		else {
			// No, we are viewing a collection!  We must be viewing
			// a binder.  Determine the appropriate activity stream to
			// view.
			ActivityStream as = (m_binderInfo.isBinderFolder() ? ActivityStream.SPECIFIC_FOLDER : ActivityStream.SPECIFIC_BINDER);
			asi.setActivityStream(as);
			asi.setBinderId(m_binderInfo.getBinderId());
		}
		
		// Finally, fire the appropriate activity stream event.
		GwtTeaming.fireEvent(new ActivityStreamEnterEvent(asi));
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
}
