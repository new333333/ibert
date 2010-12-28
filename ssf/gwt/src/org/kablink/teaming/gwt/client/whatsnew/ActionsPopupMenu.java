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

package org.kablink.teaming.gwt.client.whatsnew;

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.menu.PopupMenuItem;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This popup menu is used to display the actions a user can take on a given entry.
 * @author jwootton
 *
 */
public class ActionsPopupMenu extends PopupMenu
{
	private PopupMenuItem m_replyMenuItem;
	private PopupMenuItem m_shareMenuItem;
	private PopupMenuItem m_subscribeMenuItem;
	private PopupMenuItem m_tagMenuItem;
	private AsyncCallback<ArrayList<ActionValidation>> m_checkRightsCallback = null;
	private ArrayList<ActionValidation> m_actionValidations;
	private ActivityStreamUIEntry m_entry;
	private int m_x;
	private int m_y;
	
	/**
	 * 
	 */
	public ActionsPopupMenu( boolean autoHide, boolean modal, ActionHandler actionHandler )
	{
		super( autoHide, modal );
		
		// Add all the possible menu items.
		{
			m_actionValidations = new ArrayList<ActionValidation>();
			
			// Create the "Reply" menu item.
			m_replyMenuItem = new PopupMenuItem( this, actionHandler, TeamingAction.REPLY, null, null, GwtTeaming.getMessages().reply() );
			addMenuItem( m_replyMenuItem );
			
			// Create the "Share" menu item.
			m_shareMenuItem = new PopupMenuItem( this, actionHandler, TeamingAction.SHARE, null, null, GwtTeaming.getMessages().share() );
			addMenuItem( m_shareMenuItem );
			
			// Create the "Subscribe" menu item.
			m_subscribeMenuItem = new PopupMenuItem( this, actionHandler, TeamingAction.SUBSCRIBE, null, null, GwtTeaming.getMessages().subscribe() );
			addMenuItem( m_subscribeMenuItem );
			
			// Create the "Tag" menu item.
			m_tagMenuItem = new PopupMenuItem( this, actionHandler, TeamingAction.TAG, null, null, GwtTeaming.getMessages().tag() );
			addMenuItem( m_tagMenuItem );
		}
	}
	
	
	/**
	 * 
	 */
	public void addMenuItem( PopupMenuItem menuItem )
	{
		ActionValidation actionValidation;
		
		super.addMenuItem( menuItem );

		actionValidation = new ActionValidation();
		actionValidation.setAction( menuItem.getAction().ordinal() );
		
		m_actionValidations.add( actionValidation );

		
	}
	
	
	/**
	 * Issue an ajax request to get the rights the user has for the given entry.
	 * After the ajax request returns we will hide menu items that the user does not
	 * have the rights to run.  We will then show this popup menu.
	 */
	private void checkRights()
	{
		HttpRequestInfo ri;

		if ( m_entry == null )
			return;
		
		if ( m_checkRightsCallback == null )
		{
			m_checkRightsCallback = new AsyncCallback<ArrayList<ActionValidation>>()
			{
				/**
				 * 
				 */
				public void onFailure(Throwable t)
				{
					Window.alert( "call to validateEntryActions() failed: " + t.toString() );
					//!!! Finish
					//GwtClientHelper.handleGwtRPCFailure(
					//	t,
					//	GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
					//	m_parentBinderId );
				}
				
				/**
				 * 
				 */
				public void onSuccess( ArrayList<ActionValidation> actionValidations )
				{
					for( ActionValidation nextValidation : actionValidations )
					{
						if ( nextValidation.isValid() == false )
						{
							int action;
							
							action = nextValidation.getAction();
							
							if ( action == TeamingAction.REPLY.ordinal() )
								m_replyMenuItem.setVisible( false );
							else if ( action == TeamingAction.SUBSCRIBE.ordinal() )
								m_subscribeMenuItem.setVisible( false );
							else if ( action == TeamingAction.SHARE.ordinal() )
								m_shareMenuItem.setVisible( false );
							else if ( action == TeamingAction.TAG.ordinal() )
								m_tagMenuItem.setVisible( false );
						}
					}
					
					// Now that we have validated all the actions, show this menu.
					showMenu();
				}
			};
		}
		
		// Issue an ajax request to check the rights the user has for the given entry.
		ri = HttpRequestInfo.createHttpRequestInfo();
		GwtTeaming.getRpcService().validateEntryActions( ri, m_actionValidations, m_entry.getEntryId(), m_checkRightsCallback );
	}

	
	/**
	 * Show this menu.  Make an rpc request and see what rights the user has for the given entry.
	 * Then based on those rights show/hide the appropriate menu items.  Then show the menu.
	 */
	public void showActionsMenu( ActivityStreamUIEntry entry, int x, int y )
	{
		// Remember the entry we are dealing with.
		m_entry = entry;
		
		// Remember where we should position this popup menu.
		m_x = x;
		m_y = y;
		
		// Associate the given entry with each menu item.
		m_replyMenuItem.setActionData( entry );
		m_shareMenuItem.setActionData( entry );
		m_subscribeMenuItem.setActionData( entry );
		m_tagMenuItem.setActionData( entry );
		
		// Make sure all the menu items are visible.
		m_replyMenuItem.setVisible( true );
		m_shareMenuItem.setVisible( true );
		m_subscribeMenuItem.setVisible( true );
		m_tagMenuItem.setVisible( true );

		// Make an ajax request to see what rights the user has for the given entry.
		// After the ajax request returns we will display this menu.
		checkRights();
	}
	
	
	/**
	 * Show this popup menu.
	 */
	private void showMenu()
	{
		PopupPanel.PositionCallback posCallback;

		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				setPopupPosition( m_x - offsetWidth, m_y );
			}// end setPosition()
		};
		setPopupPositionAndShow( posCallback );
	}
}
