/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/m_images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarBox;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarButton;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarPopup;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarToggle;
import org.kablink.teaming.gwt.client.mainmenu.MyTeamsMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.TeamingMenuItem;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.OnBrowseHierarchyInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;


/**
 * This widget will display Teaming's main menu control.
 * 
 * @author drfoster@novell.com
 */
public class MainMenuControl extends Composite implements ActionRequestor, ActionTrigger {
	private FlowPanel m_contextPanel;
	private GwtTeamingMainMenuImageBundle m_images = GwtTeaming.getMainMenuImageBundle();
	private GwtTeamingMessages m_messages = GwtTeaming.getMessages();
	private List<ActionHandler> m_actionHandlers = new ArrayList<ActionHandler>();
	private String m_contextBinderId;
	
	/**
	 * Constructor method.
	 */
	public MainMenuControl() {
		// Create the menu's main panel...
		FlowPanel menuPanel = new FlowPanel();
		menuPanel.addStyleName("mainMenuControl");

		// ...add the common items at the left end of the menu...
		addCommonItems(menuPanel);
		
		// ...add a FlowPanel for the context dependent items.  (Note
		// ...that these items will be added when the content panel
		// ...loads via calls to MainMenuControl.contextLoaded().)...
		m_contextPanel = new FlowPanel();
		m_contextPanel.addStyleName("mainMenuContent");
		menuPanel.add(m_contextPanel);
		
		// ...and finally, all composites must call initWidget() in
		// ...their constructors.
		initWidget(menuPanel);
	}

	/**
	 * Called to add an ActionHandler to this MainMenuControl.
	 * 
	 * Implements the ActionRequestor.addActionHandler() method.
	 * 
	 * @param actionHandler
	 */
	public void addActionHandler(ActionHandler actionHandler) {
		m_actionHandlers.add( actionHandler );
	}

	/*
	 * Adds the items to the menu bar that are always there, regardless
	 * of context.
	 */
	private void addCommonItems(FlowPanel menuPanel) {
		// Create a panel to hold the buttons at the left edge of the
		// menu bar...
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.addStyleName("mainMenuButton_Group");
		
		// ...add the slide-left/right toggle...
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("mainMenuButton subhead-control-bg1 roundcornerSM");
		MenuBarToggle wsTreeSlider = new MenuBarToggle(this, panel, m_images.slideLeft(), m_messages.mainMenuAltLeftNavHideShow(), TeamingAction.HIDE_LEFT_NAVIGATION, m_images.slideRight(), m_messages.mainMenuAltLeftNavHideShow(), TeamingAction.SHOW_LEFT_NAVIGATION);
		panel.add(wsTreeSlider);
		buttonsPanel.add(panel);

		// ...add the slide-up/down toggle...
		panel = new FlowPanel();
		panel.addStyleName("mainMenuButton subhead-control-bg1 roundcornerSM");
		MenuBarToggle mastHeadSlider = new MenuBarToggle(this, panel, m_images.slideUp(), m_messages.mainMenuAltMastHeadHideShow(), TeamingAction.HIDE_MASTHEAD, m_images.slideDown(), m_messages.mainMenuAltMastHeadHideShow(), TeamingAction.SHOW_MASTHEAD);
		panel.add(mastHeadSlider);
		buttonsPanel.add(panel);

		// ...add the browse hierarchy button...
		panel = new FlowPanel();
		panel.addStyleName("mainMenuButton subhead-control-bg1 roundcornerSM");
		MenuBarButton bhButton = new MenuBarButton(this, panel, m_images.browseHierarchy(), m_messages.mainMenuAltBrowseHierarchy(), TeamingAction.BROWSE_HIERARCHY, new OnBrowseHierarchyInfo(panel));
		panel.add(bhButton);
		buttonsPanel.add(panel);
		
		// ...add the GWT UI button...
		panel = new FlowPanel();
		panel.addStyleName("mainMenuButton subhead-control-bg1 roundcornerSM");
		MenuBarButton gwtUIButton = new MenuBarButton(this, panel, m_images.gwtUI(), m_messages.mainMenuAltGwtUI(), TeamingAction.TOGGLE_GWT_UI);
		panel.add(gwtUIButton);
		buttonsPanel.add(panel);

		// ...add the buttons to the menu...
		menuPanel.add(buttonsPanel);

		// ...and finally, add the common drop down items to the menu bar.
		addMyWorkspace(menuPanel);
		addMyTeams(menuPanel);
		addFavorites(menuPanel);
	}
	
	/*
	 * Adds the Favorites item to the menu bar.
	 */
	private void addFavorites(FlowPanel menuPanel) {
		final String favoritesTitle = m_messages.mainMenuBarFavorites();
		final MenuBarBox favoritesBox = new MenuBarBox("ss_mainMenuFavorites", favoritesTitle, true);
		final ActionTrigger actionTrigger = this;
		favoritesBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					int left =  favoritesBox.getAbsoluteLeft();
					int top  = (favoritesBox.getAbsoluteTop() - 20);
					MenuBarPopup mip = new MenuBarPopup(actionTrigger, favoritesTitle, left, top);
					
//!					...this needs to be implemented...
					Label content = new Label("...this needs to be implemented...");
					content.addStyleName("mainMenuPopup_Item");
					mip.addContentWidget(content);
					mip.show();
				}
			});
		menuPanel.add(favoritesBox);
	}
	
	/*
	 * Adds the My Teams item to the menu bar.
	 */
	private void addMyTeams(FlowPanel menuPanel) {
		final MenuBarBox myTeamsBox = new MenuBarBox("ss_mainMenuMyTeams", m_messages.mainMenuBarMyTeams(), true);
		final ActionTrigger actionTrigger = this;
		myTeamsBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					int left =  myTeamsBox.getAbsoluteLeft();
					int top  = (myTeamsBox.getAbsoluteTop() - 20);
					MyTeamsMenuPopup mip = new MyTeamsMenuPopup(actionTrigger, left, top);
					mip.showMyTeams(m_contextBinderId);
				}
			});
		menuPanel.add(myTeamsBox);
	}
	
	/*
	 * Adds the My Workspace item to the menu bar.
	 */
	private void addMyWorkspace(FlowPanel menuPanel) {
		MenuBarBox myWorkspaceBox = new MenuBarBox("ss_mainMenuMyWorkspace", m_images.home16(), m_messages.mainMenuBarMyWorkspace());
		myWorkspaceBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					triggerAction(TeamingAction.MY_WORKSPACE);
				}
			});
		menuPanel.add(myWorkspaceBox);
	}
	

	/**
	 * Called when a new context has been loaded into the content panel
	 * to refresh the menu contents.
	 * 
	 * @param binderId
	 */
	public void contextLoaded(String binderId) {
		m_contextBinderId = binderId;
		GwtTeaming.getRpcService().getMenuItems(binderId, new AsyncCallback<List<TeamingMenuItem>>() {
			public void onFailure(Throwable t) {
				Window.alert(t.toString());
			}
			public void onSuccess(List<TeamingMenuItem> miList)  {
//!				...this needs to be implemented...
			}
		});
	}

	/**
	 * Fires a TeamingAction at the registered ActionHandler's.
	 * 
	 * Implements the ActionTrigger.triggerAction() method. 
	 *
	 * @param action
	 * @param obj
	 */
	public void triggerAction(TeamingAction action, Object obj) {
		// Scan the ActionHandler's that have been registered...
		for (Iterator<ActionHandler> ahIT = m_actionHandlers.iterator(); ahIT.hasNext(); ) {
			// ...firing the action at each.
			ahIT.next().handleAction(action, obj);
		}
	}
	
	public void triggerAction(TeamingAction action) {
		// Always use the initial form of the method.
		triggerAction(action, null);
	}
}
