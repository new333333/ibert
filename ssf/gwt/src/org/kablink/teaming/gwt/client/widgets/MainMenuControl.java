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

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.mainmenu.ManageMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.ManageSavedSearchesDlg;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarBox;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarButton;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarToggle;
import org.kablink.teaming.gwt.client.mainmenu.MyFavoritesMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.MyTeamsMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.RecentPlacesMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.SavedSearchInfo;
import org.kablink.teaming.gwt.client.mainmenu.SearchMenuPanel;
import org.kablink.teaming.gwt.client.mainmenu.SearchOptionsComposite;
import org.kablink.teaming.gwt.client.mainmenu.TopRankedDlg;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.mainmenu.ViewsMenuPopup;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.OnBrowseHierarchyInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.TopRankedInfo;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;


/**
 * This widget will display Teaming's main menu control.
 * 
 * @author drfoster@novell.com
 */
public class MainMenuControl extends Composite implements ActionRequestor, ActionTrigger {
	private boolean							m_activityStreamsUI = GwtMainPage.m_requestInfo.isActivityStreamsEnabled();
	private BinderInfo						m_contextBinder;
	private ContextLoadInfo					m_lastContextLoaded;
	private FlowPanel						m_buttonsPanel;
	private FlowPanel						m_contextPanel;
	private GwtMainPage						m_mainPage;
	private GwtTeamingMainMenuImageBundle	m_images         = GwtTeaming.getMainMenuImageBundle();
	private GwtTeamingMessages 				m_messages       = GwtTeaming.getMessages();
	private List<ActionHandler>				m_actionHandlers = new ArrayList<ActionHandler>();
	private MenuBarBox						m_closeAdminBox;
	private MenuBarBox						m_myFavoritesBox;
	private MenuBarBox						m_myTeamsBox;
	private MenuBarBox						m_myWorkspaceBox;
	private MenuBarBox						m_whatsNewBox;
	private MenuBarButton					m_bhButton;
	private MenuBarButton					m_soButton;
	private MenuBarToggle					m_wsTreeSlider;
	private MenuBarToggle					m_mastHeadSlider;
	private MyFavoritesMenuPopup			m_myFavoritesMenuPopup;
	private MyTeamsMenuPopup				m_myTeamsMenuPopup;
	private SearchMenuPanel					m_searchPanel;

	/*
	 * Inner class used to track the information used to load a
	 * context.
	 * 
	 * See the parameters to contextLoaded().
	 */
	private static class ContextLoadInfo {
		private boolean m_inSearch;
		private String  m_binderId;
		private String  m_searchTabId;

		/*
		 * Class constructor.
		 */
		private ContextLoadInfo(String binderId, boolean inSearch, String searchTabId) {
			// Simply store the parameters.
			m_binderId    = binderId;
			m_inSearch    = inSearch;
			m_searchTabId = searchTabId;
		}
	}
	
	/**
	 * Constructor method.
	 */
	public MainMenuControl(GwtMainPage mainPage) {
		// Store the parameter.
		m_mainPage = mainPage;
		
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
		
		// ...add the search widgets to the right end of the menu...
		m_searchPanel = new SearchMenuPanel(this);
		menuPanel.add(m_searchPanel);
		final MainMenuControl mainMenu = this;
		m_soButton = new MenuBarButton(m_images.searchOptions(), m_messages.mainMenuAltSearchOptions(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				m_soButton.removeStyleName("subhead-control-bg2");
				final TeamingPopupPanel soPopup = new TeamingPopupPanel(true, false);
				GwtClientHelper.rollDownPopup(soPopup);
				soPopup.addStyleName("mainMenuSearchOptions_Browser roundcornerSM-bottom");
				SearchOptionsComposite searchOptionsComposite = new SearchOptionsComposite(soPopup, mainMenu);
				searchOptionsComposite.addStyleName("mainMenuSearchOptions");
				soPopup.setWidget(searchOptionsComposite);
				soPopup.setGlassEnabled(true);
				soPopup.setGlassStyleName("mainMenuPopup_Glass");
				
				// ...and position and show it as per the position of
				// ...the search panel on the menu.
				soPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
					public void setPosition(int offsetWidth, int offsetHeight) {
						int soPopupLeft = ((m_soButton.getAbsoluteLeft() + m_soButton.getOffsetWidth()) - offsetWidth);
						int soPopupTop  = mainMenu.getParent().getElement().getAbsoluteBottom();
						soPopup.setPopupPosition(soPopupLeft, soPopupTop);
					}
				});
			}});
		m_soButton.addStyleName("mainMenuButton mainMenuSearchOptions_Button subhead-control-bg1 roundcornerSM");
		menuPanel.add(m_soButton);
		
		// ...and finally, all composites must call initWidget() in
		// ...their constructors.
		initWidget(menuPanel);
	}

	/**
	 * Called to add an ActionHandler to this MainMenuControl.
	 * 
	 * Implements the ActionRequestor.addActionHandler() interface
	 * method.
	 * 
	 * @param actionHandler
	 */
	public void addActionHandler(ActionHandler actionHandler) {
		m_actionHandlers.add(actionHandler);
	}

	/*
	 * Adds the "Close Administration" button to the common portion of
	 * the menu bar.
	 */
	private void addCloseAdministrationToCommon(FlowPanel menuPanel) {
		m_closeAdminBox = new MenuBarBox("ss_mainMenuCloseAdmin", m_messages.close(), false);
		m_closeAdminBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					triggerAction(TeamingAction.CLOSE_ADMINISTRATION);
				}
			});
		menuPanel.add(m_closeAdminBox);
		m_closeAdminBox.setVisible(false);
	}
	
	/*
	 * Adds the items to the menu bar that are always there, regardless
	 * of context.
	 */
	private void addCommonItems(FlowPanel menuPanel) {
		// Create a panel to hold the buttons at the left edge of the
		// menu bar...
		m_buttonsPanel = new FlowPanel();
		m_buttonsPanel.addStyleName("mainMenuButton_Group");
		
		// ...add the slide-left/right toggle...
		m_wsTreeSlider = new MenuBarToggle(this, m_images.slideLeft(), m_messages.mainMenuAltLeftNavHideShow(), TeamingAction.HIDE_LEFT_NAVIGATION, m_images.slideRight(), m_messages.mainMenuAltLeftNavHideShow(), TeamingAction.SHOW_LEFT_NAVIGATION);
		m_wsTreeSlider.addStyleName("mainMenuButton subhead-control-bg1 roundcornerSM");
		m_buttonsPanel.add(m_wsTreeSlider);

		// ...add the slide-up/down toggle...
		m_mastHeadSlider = new MenuBarToggle(this, m_images.slideUp(), m_messages.mainMenuAltMastHeadHideShow(), TeamingAction.HIDE_MASTHEAD, m_images.slideDown(), m_messages.mainMenuAltMastHeadHideShow(), TeamingAction.SHOW_MASTHEAD);
		m_mastHeadSlider.addStyleName("mainMenuButton subhead-control-bg1 roundcornerSM");
		m_buttonsPanel.add(m_mastHeadSlider);

		// ...add the browse hierarchy button...
		m_bhButton = new MenuBarButton(this, m_images.browseHierarchy(), m_messages.mainMenuAltBrowseHierarchy(), TeamingAction.BROWSE_HIERARCHY);
		m_bhButton.setActionObject(new OnBrowseHierarchyInfo(m_bhButton));
		m_bhButton.addStyleName("mainMenuButton subhead-control-bg1 roundcornerSM");
		m_buttonsPanel.add(m_bhButton);

		// ...if the user is allowed to exit GWT UI mode...
		final ActionTrigger actionTrigger = this;
		GwtTeaming.getRpcService().getGwtUIExclusive(HttpRequestInfo.createHttpRequestInfo(), new AsyncCallback<Boolean>() {
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetGwtUIInfo());
			}
			
			public void onSuccess(Boolean isGwtUIExclusive) {
				if (!isGwtUIExclusive) {
					// ...add the GWT UI button...
					MenuBarButton gwtUIButton = new MenuBarButton(actionTrigger, m_images.gwtUI(), m_messages.mainMenuAltGwtUI(), TeamingAction.TOGGLE_GWT_UI);
					gwtUIButton.addStyleName("mainMenuButton subhead-control-bg1 roundcornerSM");
					m_buttonsPanel.add(gwtUIButton);
				}
			}
		});

		// ...add the buttons to the menu...
		menuPanel.add(m_buttonsPanel);

		// ...and finally, add the common drop down items to the menu bar.
		addMyWorkspaceToCommon(        menuPanel);
		addWhatsNewToCommon(           menuPanel);
		if (m_activityStreamsUI) {
			addMyFavoritesToCommon(    menuPanel);
			addMyTeamsToCommon(        menuPanel);
		}
		else {
			addMyTeamsToCommon(        menuPanel);
			addMyFavoritesToCommon(    menuPanel);
		}
		addCloseAdministrationToCommon(menuPanel);
	}
	
	/*
	 * Adds the Manage item to the context based portion of the menu
	 * bar.
	 */
	private void addManageToContext(final List<ToolbarItem> toolbarItemList, final TeamManagementInfo tmi) {
		String manageName;
		switch (m_contextBinder.getBinderType()) {
		default:
		case OTHER:                                                      return;
		case FOLDER:     manageName = m_messages.mainMenuBarFolder();    break;
		case WORKSPACE:  manageName = m_messages.mainMenuBarWorkspace(); break;
		}
		
		final ManageMenuPopup mmp = new ManageMenuPopup(this, manageName);
		mmp.setCurrentBinder(m_contextBinder);
		mmp.setToolbarItemList(toolbarItemList);
		mmp.setTeamManagementInfo(tmi);
		if (mmp.shouldShowMenu()) {
			final MenuBarBox manageBox = new MenuBarBox("ss_mainMenuManage", manageName, true);
			manageBox.addClickHandler(
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (mmp.isShowing())
						     mmp.hideMenu();
						else mmp.showMenu(manageBox);
					}
				});
			m_contextPanel.add(manageBox);
		}
	}
	
	/*
	 * Adds the My Favorites item to the common portion of the menu
	 * bar.
	 */
	private void addMyFavoritesToCommon(FlowPanel menuPanel) {
		m_myFavoritesBox = new MenuBarBox("ss_mainMenuMyFavorites", m_messages.mainMenuBarMyFavorites(), true);
		final ActionTrigger actionTrigger = this;
		m_myFavoritesBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (null == m_myFavoritesMenuPopup) {
						m_myFavoritesMenuPopup = new MyFavoritesMenuPopup(actionTrigger);
						m_myFavoritesMenuPopup.setCurrentBinder(m_contextBinder);
						m_myFavoritesMenuPopup.showMenu(m_myFavoritesBox);
						m_myFavoritesMenuPopup.addCloseHandler(new CloseHandler<PopupPanel>(){
							public void onClose(CloseEvent<PopupPanel> event) {
							     m_myFavoritesMenuPopup = null;
							}});
					}
					else {
					     m_myFavoritesMenuPopup.hideMenu();
					}
				}
			});
		menuPanel.add(m_myFavoritesBox);
	}
	
	/*
	 * Adds the My Teams item to the common portion of the menu bar.
	 */
	private void addMyTeamsToCommon(FlowPanel menuPanel) {
		m_myTeamsBox = new MenuBarBox("ss_mainMenuMyTeams", m_messages.mainMenuBarMyTeams(), true);
		final ActionTrigger actionTrigger = this;
		m_myTeamsBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (null == m_myTeamsMenuPopup) {
						m_myTeamsMenuPopup = new MyTeamsMenuPopup(actionTrigger);
						m_myTeamsMenuPopup.setCurrentBinder(m_contextBinder);
						m_myTeamsMenuPopup.showMenu(m_myTeamsBox);
						m_myTeamsMenuPopup.addCloseHandler(new CloseHandler<PopupPanel>(){
							public void onClose(CloseEvent<PopupPanel> event) {
								m_myTeamsMenuPopup = null;
							}});
					}
					else {
					     m_myTeamsMenuPopup.hideMenu();
					}
				}
			});
		menuPanel.add(m_myTeamsBox);
	}
	
	/*
	 * Adds the My Workspace item to the common portion of the menu
	 * bar.
	 */
	private void addMyWorkspaceToCommon(FlowPanel menuPanel) {
		m_myWorkspaceBox = new MenuBarBox("ss_mainMenuMyWorkspace", m_images.home16(), m_messages.mainMenuBarMyWorkspace());
		m_myWorkspaceBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					triggerAction(TeamingAction.MY_WORKSPACE);
				}
			});
		menuPanel.add(m_myWorkspaceBox);
	}

	/*
	 * Adds the Recent Places item to the context based portion of the
	 * menu bar.
	 */
	private void addRecentPlacesToContext(List<ToolbarItem> toolbarItemList) {
		final RecentPlacesMenuPopup rpmp = new RecentPlacesMenuPopup(this);
		rpmp.setCurrentBinder(m_contextBinder);
		rpmp.setToolbarItemList(toolbarItemList);
		if (rpmp.shouldShowMenu()) {
			final MenuBarBox rpBox = new MenuBarBox("ss_mainMenuRecentPlaces", m_messages.mainMenuBarRecentPlaces(), true);
			rpBox.addClickHandler(
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (rpmp.isShowing())
						     rpmp.hideMenu();
						else rpmp.showMenu(rpBox);
					}
				});
			m_contextPanel.add(rpBox);
		}
	}

	/*
	 * Add the Saved Searches item to the context based portion of the
	 * menu bar.
	 */
	private void addManageSavedSearchesToContext(final String searchTabId) {
		final ActionTrigger actionTrigger = this;
		final MenuBarBox manageSavedSearchesBox = new MenuBarBox("ss_mainMenuManageSavedSearches", m_messages.mainMenuBarManageSavedSearches());
		manageSavedSearchesBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					// Remove the selection from the menu item...
					Element menuItemElement = Document.get().getElementById("ss_mainMenuManageSavedSearches");
					menuItemElement.removeClassName("mainMenuPopup_BoxHover");
					
					// ...and run the manage saved searches dialog.
					GwtTeaming.getRpcService().getSavedSearches(HttpRequestInfo.createHttpRequestInfo(), new AsyncCallback<List<SavedSearchInfo>>() {
						public void onFailure(Throwable t) {
							GwtClientHelper.handleGwtRPCFailure(
								t,
								m_messages.rpcFailure_GetSavedSearches());
						}
						
						public void onSuccess(List<SavedSearchInfo> ssList) {
							ManageSavedSearchesDlg mssDlg = new ManageSavedSearchesDlg(
								false,	// false -> Don't auto hide.
								true,	// true  -> Modal.
								actionTrigger,
								manageSavedSearchesBox.getAbsoluteLeft(),
								manageSavedSearchesBox.getElement().getAbsoluteBottom(),
								ssList,
								searchTabId);
							mssDlg.addStyleName("manageSavedSearchesDlg");
							mssDlg.show();
						}
					});
				}
			});
		m_contextPanel.add(manageSavedSearchesBox);
	}
	
	/*
	 * Adds the Top Ranked item to the context based portion of the
	 * menu bar.
	 */
	private void addTopRankedToContext() {
		final ActionTrigger actionTrigger = this;
		final MenuBarBox topRankedBox = new MenuBarBox("ss_mainMenuTopRanged", m_messages.mainMenuBarTopRanked());
		topRankedBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					// Remove the selection from the menu item...
					Element menuItemElement = Document.get().getElementById("ss_mainMenuTopRanged");
					menuItemElement.removeClassName("mainMenuPopup_BoxHover");
					
					// ...and run the top ranked dialog.
					GwtTeaming.getRpcService().getTopRanked( HttpRequestInfo.createHttpRequestInfo(), new AsyncCallback<List<TopRankedInfo>>() {
						public void onFailure(Throwable t) {
							GwtClientHelper.handleGwtRPCFailure(
								t,
								m_messages.rpcFailure_GetTopRanked());
						}
						
						public void onSuccess(List<TopRankedInfo> triList) {
							TopRankedDlg topRankedDlg = new TopRankedDlg(
								false,	// false -> Don't auto hide.
								true,	// true  -> Modal.
								actionTrigger,
								topRankedBox.getAbsoluteLeft(),
								topRankedBox.getElement().getAbsoluteBottom(),
								triList);
							topRankedDlg.addStyleName("topRankedDlg");
							topRankedDlg.show();
						}
					});
				}
			});
		m_contextPanel.add(topRankedBox);
	}

	/*
	 * Adds the Views item to the context based portion of the menu
	 * bar.
	 */
	private void addViewsToContext(List<ToolbarItem> toolbarItemList) {
		final ViewsMenuPopup vmp = new ViewsMenuPopup(this);
		vmp.setCurrentBinder(m_contextBinder);
		vmp.setToolbarItemList(toolbarItemList);
		if (vmp.shouldShowMenu()) {
			final MenuBarBox actionsBox = new MenuBarBox("ss_mainMenuViews", m_messages.mainMenuBarViews(), true);
			actionsBox.addClickHandler(
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (vmp.isShowing())
						     vmp.hideMenu();
						else vmp.showMenu(actionsBox);
					}
				});
			m_contextPanel.add(actionsBox);
		}
	}
	
	/*
	 * Adds the What's New item to the common portion of the menu bar.
	 */
	private void addWhatsNewToCommon(FlowPanel menuPanel) {
		// If we're not running in activity streams mode...
		if (!m_activityStreamsUI) {
			// ...we don't display the My Teams menu.
			return;
		}
		
		m_whatsNewBox = new MenuBarBox("ss_mainMenuWhatsNew", m_images.newMenu(), m_messages.mainMenuBarWhatsNew());
		m_whatsNewBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					// Are we connected to a binder?
					if (null != m_contextBinder) {
						// Yes!  Use it as the current binder for the
						// activity stream.
						ActivityStreamInfo asi = new ActivityStreamInfo();
						asi.setActivityStream(ActivityStream.CURRENT_BINDER);
						asi.setBinderId(m_contextBinder.getBinderId());
						asi.setTitle(   m_contextBinder.getBinderTitle());
						triggerAction(TeamingAction.ENTER_ACTIVITY_STREAM_MODE, asi);
					}
					
					else {
						// No, we're not connected to a binder!  Just
						// use the UI supplied default activity stream.
						triggerAction(TeamingAction.ENTER_ACTIVITY_STREAM_MODE);
					}
				}
			});
		menuPanel.add(m_whatsNewBox);
	}

	/**
	 * Called to remove the context based menu items (Workspace,
	 * Folder, ...) from the menu bar.
	 * 
	 * This is typically done immediately before a known context switch
	 * so that invalid menu items (i.e., those based on a previous
	 * context) are not available until the new context fully loads.
	 */
	public void clearContextMenus() {
		if (null != m_contextPanel) {
			m_contextPanel.clear();
		}
	}
	
	/**
	 * Called when a new context has been loaded into the content panel
	 * to refresh the menu contents.
	 * 
	 * Note:  If searchTabId as a non-null, non-empty value, it will be
	 *    used as the ID of the current tab (i.e., search results) and
	 *    implies that that the current search can be saved.
	 *    
	 *    If it's null or an empty value, it implies that there is no
	 *    current search that can be saved and no saving capabilities
	 *    will be exposed.
	 *    
	 * @param binderId
	 * @param inSearch
	 * @param searchTabId
	 */
	public void contextLoaded(final String binderId, final boolean inSearch, final String searchTabId) {
		// Keep track of the context that we're loading.
		setContext(binderId, inSearch, searchTabId);
		
		// Rebuild the context based panel based on the new context.
		clearContextMenus();
		GwtTeaming.getRpcService().getBinderInfo(HttpRequestInfo.createHttpRequestInfo(), binderId, new AsyncCallback<BinderInfo>() {
			public void onFailure(Throwable t) {
				m_contextBinder = null;
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetBinderInfo(),
					binderId);
			}
			public void onSuccess(BinderInfo binderInfo) {
				m_contextBinder = binderInfo;
				GwtTeaming.getRpcService().getToolbarItems(HttpRequestInfo.createHttpRequestInfo(), binderId, new AsyncCallback<List<ToolbarItem>>() {
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
							t,
							m_messages.rpcFailure_GetToolbarItems(),
							binderId);
					}
					public void onSuccess(final List<ToolbarItem> toolbarItemList) {
						GwtTeaming.getRpcService().getTeamManagementInfo( HttpRequestInfo.createHttpRequestInfo(), binderId, new AsyncCallback<TeamManagementInfo>() {
							public void onFailure(Throwable t) {
								GwtClientHelper.handleGwtRPCFailure(
									t,
									m_messages.rpcFailure_GetTeamManagement(),
									binderId);
							}
							public void onSuccess(final TeamManagementInfo tmi) {
								// Handle variations based on activity
								// stream and search mode.
								addRecentPlacesToContext(toolbarItemList);
								boolean inASMode = m_mainPage.getWorkspaceTree().isInActivityStreamMode();
								if (!inASMode) {
									addManageToContext(toolbarItemList, tmi);
								}
								if (inSearch && (!inASMode)) {
									addTopRankedToContext();
									addManageSavedSearchesToContext(searchTabId);
								}
								if (!inASMode) {
									addViewsToContext(toolbarItemList);
								}
							}
						});
					}
				});
			}
		});
	}

	/**
	 * Show all the menus and controls on this menu control and hide
	 * the Close administration menu item..  This is used when we close
	 * the Site Administration" page.
	 */
	public void hideAdministrationMenubar() {
		// Show the widget that holds the expand/contract left
		// navigation, expand/contract header, ... widgets.
		m_bhButton.setVisible(true);
		
		// Show My Workspace, My Teams, My Favorites and What's New.
		m_myWorkspaceBox.setVisible(true);
		if (null != m_myTeamsBox)     m_myTeamsBox.setVisible(    true);
		if (null != m_myFavoritesBox) m_myFavoritesBox.setVisible(true);
		if (null != m_whatsNewBox)    m_whatsNewBox.setVisible(   true);
		
		// Show the panel that holds the menu items.
		m_contextPanel.setVisible(true);
		
		// Show the search panel.
		m_searchPanel.setVisible(true);
		m_soButton.setVisible(   true);
		
		// Hide the Close administration menu item.
		m_closeAdminBox.setVisible(false);
	}

	/**
	 * Resets the current menu context to the one that was last
	 * loaded.
	 */
	public void resetContext() {
		// If we're tracking a previously loaded context...
		if (null != m_lastContextLoaded) {
			// ...re-load it.
			contextLoaded(
				m_lastContextLoaded.m_binderId,
				m_lastContextLoaded.m_inSearch,
				m_lastContextLoaded.m_searchTabId);
		}
	}

	/**
	 * Sets the parameters as the most recently loaded context.
	 * 
	 * @param binderId
	 * @param inSearch
	 * @param searchTabId
	 */
	public void setContext(String binderId, boolean inSearch, String searchTabId) {
		m_lastContextLoaded = new ContextLoadInfo(binderId, inSearch, searchTabId);
	}
	
	/**
	 * Set the state of the "show/hide masthead" menu item.
	 */
	public void setMastheadSliderMenuItemState(TeamingAction action) {
		m_mastHeadSlider.setState(action);
	}
	
	
	/**
	 * Set the state of the "show/hide workspace tree" menu item.
	 */
	public void setWorkspaceTreeSliderMenuItemState(TeamingAction action) {
		m_wsTreeSlider.setState(action);
	}
	
	
	/**
	 * Hide all the menus and controls on this menu control and shows
	 * the Close administration menu item.  This is used when we invoke
	 * the Site Administration page.
	 */
	public void showAdministrationMenubar() {
		// Hide the browse button
		m_bhButton.setVisible(false);
		
		// Hide My Workspace, My Teams, My Favorites and What's New.
		m_myWorkspaceBox.setVisible(false);
		if (null != m_myTeamsBox)     m_myTeamsBox.setVisible(    false);
		if (null != m_myFavoritesBox) m_myFavoritesBox.setVisible(false);
		if (null != m_whatsNewBox)    m_whatsNewBox.setVisible(   false);
		
		// Hide the panel that holds the menu items.
		m_contextPanel.setVisible(false);
		
		// Hide the search panel.
		m_searchPanel.setVisible(false);
		m_soButton.setVisible(   false);
		
		// Show the Close administration menu item.
		m_closeAdminBox.setVisible(true);
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
