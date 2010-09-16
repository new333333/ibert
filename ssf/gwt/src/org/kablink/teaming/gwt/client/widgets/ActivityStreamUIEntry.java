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
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */


package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.SimpleProfileParams;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;


/*
 * This class is the base class for the entries that are displayed in the Activity Stream.
 */
public abstract class ActivityStreamUIEntry extends Composite
	implements ClickHandler, MouseOverHandler, MouseOutHandler
{
	protected ActionHandler m_actionHandler;
	private Image m_avatarImg;
	private InlineLabel m_title;
	private InlineLabel m_author;
	private InlineLabel m_date;
	private InlineLabel m_desc;
	private String m_authorId;
	private String m_authorWSId;	// Id of the author's workspace.
	
	
	/**
	 * 
	 */
	public ActivityStreamUIEntry(
		ActionHandler actionHandler )  // We will call this handler when the user selects an item from the search results.
	{
		FlowPanel mainPanel;
		FlowPanel panel;

		mainPanel = new FlowPanel();
		mainPanel.addStyleName( getMainPanelStyleName() );
		
		// Remember the handler we should call when the user selects an item from the search results.
		m_actionHandler = actionHandler;
		
		// Add a place to show the avatar
		m_avatarImg = new Image();
		m_avatarImg.addStyleName( getAvatarImageStyleName() );
		mainPanel.add( m_avatarImg );
		
		// Add mouse-over and mouse-out handlers.
		m_avatarImg.addMouseOverHandler( this );
		m_avatarImg.addMouseOutHandler( this );
		
		// Add a click handler to the avatar.
		m_avatarImg.addClickHandler( this );
		
		// Create the panel that holds the entry's header.
		panel = createHeaderPanel();
		mainPanel.add( panel );
		
		// Create the panel that holds the content.
		panel = createContentPanel();
		mainPanel.add( panel );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}
	
	
	/**
	 * This abstract method gives classes that extend this class an opportunity to add
	 * addition ui to the header.
	 */
	public abstract void addAdditionalHeaderUI( FlowPanel headerPanel );
	
	
	/**
	 * Clear all of the entry specific information such as the title, avatar url, etc
	 */
	public void clearEntrySpecificInfo()
	{
		m_avatarImg.setUrl( "" );
		m_title.getElement().setInnerHTML( "" );
		m_author.setText( "" );
		m_date.setText( "" );
		m_desc.getElement().setInnerHTML( "" );
		m_authorId = null;
		m_authorWSId = null;
	}


	/**
	 * Create the panel that holds the author's name, entry's date and a 2 line description.
	 */
	public FlowPanel createContentPanel()
	{
		FlowPanel panel;
		
		panel = new FlowPanel();
		panel.addStyleName( getContentPanelStyleName() );
		
		m_author = new InlineLabel();
		m_author.addStyleName( getAuthorStyleName() );
		panel.add( m_author );
		
		// Add a mouse-over and mouse-out handlers for the author
		m_author.addMouseOverHandler( this );
		m_author.addMouseOutHandler( this );
		
		// Add a click handler for the author.
		m_author.addClickHandler( this );
		
		m_date = new InlineLabel();
		m_date.addStyleName( getDateStyleName() );
		panel.add( m_date );
		
		m_desc = new InlineLabel();
		m_desc.addStyleName( getDescStyleName() );
		panel.add( m_desc );
		
		return panel;
	}
	
	
	/**
	 * Create the panel that holds the entry's header: avatar, title
	 */
	public FlowPanel createHeaderPanel()
	{
		FlowPanel headerPanel;
		FlowPanel titlePanel;
		
		headerPanel = new FlowPanel();
		headerPanel.addStyleName( getEntryHeaderStyleName() );
		
		// Create a <span> to hold the title.
		titlePanel = new FlowPanel();
		titlePanel.addStyleName( getTitlePanelStyleName() );
		headerPanel.add( titlePanel );
		m_title = new InlineLabel();
		m_title.addStyleName( getTitleStyleName() );
		titlePanel.add( m_title );
		
		// Add a mouse-over handler for the title.
		m_title.addMouseOverHandler( this );
		
		// Add a mouse-out handler for the activity stream source name
		m_title.addMouseOutHandler( this );
		
		// Add a click handler for the activity stream source name
		m_title.addClickHandler( this );
		
		// Add any additional ui to the header.  This gives classes that extend this
		// class an opportunity to put additional data in the header.
		addAdditionalHeaderUI( titlePanel );
		
		return headerPanel;
	}
	
	
	/**
	 * Return the name of the style used with the author.
	 */
	public String getAuthorStyleName()
	{
		return "activityStreamEntryAuthor";
	}
	
	
	/**
	 * Return the name of the style used with the avatar image.
	 */
	public abstract String getAvatarImageStyleName();

	
	/**
	 * Return the name of the style used with the content panel.
	 */
	public abstract String getContentPanelStyleName();

	
	/**
	 * Return the name of the style used with the date.
	 */
	public String getDateStyleName()
	{
		return "activityStreamEntryDate";
	}
	
	
	/**
	 * Return the name of the style used with the description
	 */
	public String getDescStyleName()
	{
		return "activityStreamEntryDesc";
	}
	
	
	/**
	 * Return the name of the style used with the header.
	 */
	public abstract String getEntryHeaderStyleName();
	
	
	/**
	 * 
	 */
	public FlowPanel getMainPanel()
	{
		return (FlowPanel)getWidget();
	}
	
	
	/**
	 * Return the name of the style used with the div that holds the entry.
	 */
	public abstract String getMainPanelStyleName();
	
	
	/**
	 * Return the name of the style used with the panel that holds the title.
	 */
	public abstract String getTitlePanelStyleName();
	
	
	/**
	 * Return the name of the style used with title.
	 */
	public abstract String getTitleStyleName();
	
	
	/**
	 * This method gets invoked when the user clicks on the author.
	 */
	public void handleClickOnAuthor( Widget src )
	{
		SimpleProfileParams params;
		
		// Invoke the Simple Profile dialog.
		params = new SimpleProfileParams( src.getElement(), m_authorWSId, m_author.getText() );
		m_actionHandler.handleAction( TeamingAction.INVOKE_SIMPLE_PROFILE, params );
	}
	
	
	/**
	 * This method gets invoked when the user clicks on the title.  Open the entry
	 * for the user to read.
	 */
	public void handleClickOnTitle()
	{
		Window.alert( "Not implemented yet - Title" );
	}

	
	/**
	 * 
	 */
	public void onClick( ClickEvent event )
	{
		final Object src;
		
		src = event.getSource();
		if ( src == m_title || src == m_author || src == m_avatarImg )
		{
			Command cmd;

			cmd = new Command()
			{
				/**
				 * 
				 */
				public void execute()
				{
					if ( src == m_title )
						handleClickOnTitle();
					else if ( src == m_author || src == m_avatarImg )
						handleClickOnAuthor( (Widget)src );
				}
				
			};
			DeferredCommand.addCommand( cmd );
		}
	}

	
	/**
	 * Remove the mouse-over style from the given label. 
	 */
	public void onMouseOut( MouseOutEvent event )
	{
		Object src;
		
		src = event.getSource();
		if ( src == m_title || src == m_author )
		{
			((Widget)src).removeStyleName( "activityStreamHover" );
		}
		else if ( src == m_avatarImg )
		{
			m_avatarImg.removeStyleName( "cursorPointer" );
		}
	}

	
	/**
	 * Add the mouse-over style to the given label. 
	 */
	public void onMouseOver( MouseOverEvent event )
	{
		Object src;
		
		src = event.getSource();
		if ( src == m_title || src == m_author )
		{
			((Widget)src).addStyleName( "activityStreamHover" );
		}
		else if ( src == m_avatarImg )
		{
			m_avatarImg.addStyleName( "cursorPointer" );
		}
	}

	
	/**
	 * Set the data this we should display from the given ActivityStreamEntry
	 */
	public void setData( ActivityStreamEntry entryItem )
	{
		m_avatarImg.setUrl( entryItem.getAuthorAvatarUrl() );
		m_title.getElement().setInnerHTML( entryItem.getEntryTitle() );
		m_author.setText( entryItem.getAuthorName() );
		m_authorId = entryItem.getAuthorId();
		m_authorWSId = entryItem.getAuthorWorkspaceId();
		m_date.setText( entryItem.getEntryModificationDate() );
		m_desc.getElement().setInnerHTML( entryItem.getEntryDescription() );
	}
	
}