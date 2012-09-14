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
package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.EditPersonalPreferencesEvent;
import org.kablink.teaming.gwt.client.event.GotoMyWorkspaceEvent;
import org.kablink.teaming.gwt.client.event.LogoutEvent;
import org.kablink.teaming.gwt.client.event.ShowCollectionEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;


/**
 * This class displays the actions a user can perform from the mast head.
 * @author jwootton
 *
 */
public class UserActionsPopup extends TeamingPopupPanel
{
	private FlowPanel m_footerPanel;

	/**
	 * 
	 */
	public UserActionsPopup( String userName, boolean autoHide, boolean modal )
	{
		super( autoHide, modal );
	
		FlowPanel mainPanel;
		FlowPanel allContentPanel;
		FlowPanel topPanel;
		FlowPanel actionsPanel;
	
		// Tell this popup to 'roll down' when opening. 
		GwtClientHelper.rollDownPopup( this );
		
		setStyleName( "userActionsPopup" );
		addStyleName( "teamingPopupPanel_NoClip" );
	
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "userActionsPopup_OutermostPanel" );
		
		// Add the triangle image to the top of the popup
		{
			FlowPanel topImgPanel;
			Image img;

			topImgPanel = new FlowPanel();
			topImgPanel.addStyleName( "userActionsPopup_TopImgPanel" );

			img = new Image( GwtTeaming.getImageBundle().triangle() );
			topImgPanel.add( img );

			mainPanel.add( topImgPanel );
		}
		
		allContentPanel = new FlowPanel();
		allContentPanel.addStyleName( "userActionsPopup_AllContentPanel" );
		mainPanel.add( allContentPanel );
		
		topPanel = new FlowPanel();
		allContentPanel.add( topPanel );
		
		// Create a place for the user's avatar and name to live in
		{
			FlexTable table;
			String avatarUrl;
			
			table = new FlexTable();
			table.addStyleName( "userActionsPopup_HeaderTable" );
			table.setCellPadding( 5 );
			
			// Create a panel for the avatar to live in
			avatarUrl = GwtTeaming.m_requestInfo.getUserAvatarUrl();
			if ( avatarUrl != null && avatarUrl.length() > 0 )
			{
				FlowPanel imgPanel;
				Image img;
				
				imgPanel = new FlowPanel();
				img = new Image( avatarUrl );
				img.addStyleName( "userActionsPopup_AvatarImg" );
				imgPanel.add( img );
				
				table.setHTML( 0, 0, imgPanel.toString() );
			}
			
			// Create a panel for the name to live in.
			{
				FlowPanel namePanel;

				namePanel = new FlowPanel();
				namePanel.addStyleName( "userActionsPopup_NamePanel" );
				namePanel.getElement().setInnerText( userName );
				
				table.setHTML( 0, 1, namePanel.toString() );
			}

			topPanel.add( table );
		}
		
		// Create the actions panel
		actionsPanel = createActionsPanel( userName );
		topPanel.add( actionsPanel );
		
		// Add a footer
		m_footerPanel = createFooterPanel();
		allContentPanel.add( m_footerPanel );
		
		setWidget( mainPanel );
	}

	/**
	 * 
	 */
	private FlowPanel addAction(
		String name,
		ImageResource imgResource,
		final VibeEventBase<?> actionEvent )
	{
		FlowPanel actionPanel;
		Image img;
		InlineLabel actionLabel;
		ClickHandler clickHandler;
		
		actionPanel = new FlowPanel();
		actionPanel.addStyleName( "userActionsPopup_ActionPanel" );
		
		img = new Image( imgResource );
		img.addStyleName( "userActionsPopup_ActionImg" );
		actionPanel.add( img );
		
		actionLabel = new InlineLabel( name );
		actionLabel.addStyleName( "userActionsPopup_ActionLabel" );
		actionPanel.add( actionLabel );

		clickHandler = new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent event )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						hide();
						
						GwtTeaming.fireEvent( actionEvent );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};
		actionPanel.addDomHandler( clickHandler, ClickEvent.getType() );

		return actionPanel;
	}
	
	/**
	 * 
	 */
	private FlowPanel createActionsPanel( String userName )
	{
		FlowPanel panel;
		FlowPanel contentPanel;
		FlowPanel actionPanel;
		
		panel = new FlowPanel();
		panel.addStyleName( "userActionsPopup_ActionsPanel" );
		
		contentPanel = new FlowPanel();
		contentPanel.addStyleName( "userActionsPopup_ActionsPanel_ContentPanel" );
		panel.add( contentPanel );
		
		// Add "View Profile"
		actionPanel = addAction(
							GwtTeaming.getMessages().userActionsPanel_ViewProfile(),
							GwtTeaming.getImageBundle().userActionsPanel_ViewProfile(),
							new GotoMyWorkspaceEvent() );
		contentPanel.add( actionPanel );
		
		// Add "View Shared By Me"
		actionPanel = addAction(
							GwtTeaming.getMessages().userActionsPanel_ViewSharedByMe(),
							GwtTeaming.getImageBundle().userActionsPanel_ViewSharedByMe(),
							new ShowCollectionEvent( CollectionType.SHARED_BY_ME ) );
		contentPanel.add( actionPanel );
		
		// Add "Personal Preferences"
		actionPanel = addAction(
							GwtTeaming.getMessages().userActionsPanel_PersonalPreferences(),
							GwtTeaming.getImageBundle().userActionsPanel_PersonalPreferences(),
							new EditPersonalPreferencesEvent() );
		contentPanel.add( actionPanel );
		
		return panel;
	}
	
	/**
	 * 
	 */
	private FlowPanel createFooterPanel()
	{
		FlowPanel panel;
		InlineLabel signOutLabel;
		
		panel = new FlowPanel();
		panel.addStyleName( "userActionsPopup_FooterPanel" );
		
		signOutLabel = new InlineLabel( GwtTeaming.getMessages().signOut() );
		signOutLabel.addStyleName( "userActionsPopup_SignOutLabel" );
		signOutLabel.addClickHandler( new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent event )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						LogoutEvent.fireOne();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		} );
		panel.add( signOutLabel );
		
		return panel;
	}
	
	/**
	 * Hide the logout link.  We will do this if we are running in captive mode.
	 */
	public void hideLogoutLink()
	{
		if ( m_footerPanel != null )
			m_footerPanel.setVisible( false );
	}

	
	/**
	 * 
	 */
	public void init()
	{
		
	}
	
	/**
	 * Shows this popup.
	 */
	@Override
	public void show()
	{
		super.show();

		// ...and add vertical scrolling to the main frame for the
		// ...duration of the popup.
		GwtClientHelper.scrollUIForPopup( this );
	}	
}

