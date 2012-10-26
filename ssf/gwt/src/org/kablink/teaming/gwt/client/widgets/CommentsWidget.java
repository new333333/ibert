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
package org.kablink.teaming.gwt.client.widgets;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.ActivityStreamEntryListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCommentsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.CommentAddedCallback;
import org.kablink.teaming.gwt.client.util.CommentsInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamComment;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCommentsContainer;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu.ActionMenuItem;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.ActivityStreamCtrlClient;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.DescViewFormat;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;

/**
 * This widget is used to display the comments for a given entity.
 * 
 * @author jwootton@novell.com
 */
public class CommentsWidget extends Composite
	implements ActivityStreamCommentsContainer
{
	private CommentAddedCallback m_commentAddedCallback;
	private CommentsInfo m_commentsInfo;
	private VibeFlowPanel m_mainPanel;
	private ActivityStreamCtrl m_activityStreamCtrl;
	private AsyncCallback<VibeRpcResponse> m_getCommentsCallback;
	private boolean m_showTitle;
	
	private static final BigDecimal DELTA = BigDecimal.valueOf( 1, 1 );
	

	/**
	 * 
	 */
	public CommentsWidget( boolean showTitle )
	{
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName( "commentsWidget_commentsPanel" );
		
		m_showTitle = showTitle;

		initWidget( m_mainPanel );
	}

	/**
	 * Add the given comment to our list of comments.
	 * 
	 * @param activityStreamEntry
	 * @param append
	 * @param scrollIntoView
	 */
	public void addComment( ActivityStreamEntry activityStreamEntry, boolean scrollIntoView )
	{
//!		Note:  Dennis -> Jay:
//!		...Jay, you need to do whatever you need to reflect the
//!		...animation Lynn wanted when fromCommentManager is true.
		
		ActivityStreamComment commentUI = null;
		
		commentUI = new ActivityStreamComment(
											m_activityStreamCtrl,
											this,
											DescViewFormat.FULL,
											m_showTitle );
		commentUI.addStyleName( "commentsWidget_commentStylesOverride" );
		commentUI.setData( activityStreamEntry );
		m_mainPanel.add( commentUI );
		
		if ( scrollIntoView )
		{
			showNewComment( commentUI );
		}
	}
	
	/**
	 * Issue an rpc request to get the comments for the given entity
	 */
	private void getCommentsFromServer()
	{
		if ( m_commentsInfo != null )
		{
			GetEntryCommentsCmd cmd;
			
			if ( m_getCommentsCallback == null )
			{
				m_getCommentsCallback = new AsyncCallback<VibeRpcResponse>()
				{
					/**
					 * 
					 */
					@Override
					public void onFailure(Throwable t)
					{
						GwtClientHelper.handleGwtRPCFailure(
												t,
												GwtTeaming.getMessages().rpcFailure_GetEntryComments(),
												m_commentsInfo.getEntityTitle() );
					}
					
					/**
					 * 
					 */
					@Override
					public void onSuccess(  VibeRpcResponse response )
					{
						Scheduler.ScheduledCommand cmd;
						ActivityStreamEntryListRpcResponseData responseData;
						final List<ActivityStreamEntry> listOfComments;
	
						// Clear all data from the existing ui comment objects
						if ( m_mainPanel != null )
						{
							m_mainPanel.clear();
						}
						
						// Get the list of comments from the response.
						responseData = (ActivityStreamEntryListRpcResponseData) response.getResponseData();
						listOfComments = responseData.getActivityStreamEntryList();
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Do we have a list of comments?
								if ( listOfComments != null )
								{
									// Yes
									for (ActivityStreamEntry nextComment: listOfComments)
									{
										addComment( nextComment, false );
									}
								}
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				};
			}
			
			// Issue a request to get all the comments for this entry.
			cmd = new GetEntryCommentsCmd( getEntityId() );
			GwtClientHelper.executeCommand( cmd, m_getCommentsCallback );
		}
	}
	
	/**
	 * 
	 */
	private String getEntityId()
	{
		String entityIdS = "";
		
		if ( m_commentsInfo != null )
		{
			EntityId entityId;
			
			entityId = m_commentsInfo.getEntityId();
			if ( entityId != null )
			{
				if ( entityId.getEntityType().equalsIgnoreCase( EntityId.FOLDER_ENTRY ) )
					entityIdS = entityId.getEntityId().toString();
				else
					entityIdS = entityId.getBinderId().toString();
			}
		}
		
		return entityIdS;
	}
	
	/**
	 * Initialize the widget
	 * 
	 * @param commentsInfo
	 * @param commentAddedCallback
	 */
	public void init( CommentsInfo commentsInfo, CommentAddedCallback commentAddedCallback )
	{
		m_commentsInfo = commentsInfo;
		m_commentAddedCallback = commentAddedCallback;
		
		// Have we created an ActivityStreamCtrl before?
		if ( m_activityStreamCtrl == null )
		{
			ActionsPopupMenu actionsMenu;
			ArrayList<ActionMenuItem> list;
			
			list = new ArrayList<ActionMenuItem>();
			list.add( ActionMenuItem.REPLY );
			list.add( ActionMenuItem.SEND_TO_FRIEND );
			list.add( ActionMenuItem.SUBSCRIBE );
			if ( GwtTeaming.m_requestInfo.isLicenseFilr() == false )
				list.add( ActionMenuItem.TAG );
			list.add( ActionMenuItem.SEPARATOR );
			list.add( ActionMenuItem.MARK_READ );
			list.add( ActionMenuItem.MARK_UNREAD );
			
			actionsMenu = new ActionsPopupMenu( true, true, list.toArray( new ActionMenuItem[list.size()] ) );
			
			// No, create one.  The only reason we need to create an ActivityStreamCtrl
			// is because the ActivityStreamComment object needs one.
			ActivityStreamCtrl.createAsync( false, actionsMenu, new ActivityStreamCtrlClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ActivityStreamCtrl asCtrl )
				{
					m_activityStreamCtrl = asCtrl;
					m_activityStreamCtrl.setCheckForChanges( false );
					m_activityStreamCtrl.setDefaultDescViewFormat( DescViewFormat.FULL );
					
					// Issue an rpc request to get the comments on the given entity
					getCommentsFromServer();
				}
			} );
		}
		else
		{
			// Issue an rpc request to get the comments on the given entity
			getCommentsFromServer();
		}
	}
	
	public void init( CommentsInfo commentsInfo )
	{
		// Always use the initial form of the method.
		init( commentsInfo, null );	// null -> Caller doesn't need notifications of added comments.
	}
	
	/**
	 * Insert the given comments as the first comment.
	 */
	@Override
	public void insertReply( ActivityStreamEntry reply )
	{
		addComment( reply, true );
		
		if ( m_commentAddedCallback != null )
			m_commentAddedCallback.commentAdded( m_commentsInfo );
	}
	
	/**
	 * 
	 */
	private void showNewComment( ActivityStreamComment asComment )
	{
		Timer showTimer;
		final Element element;
		
		element = asComment.getElement();
		
		element.scrollIntoView();
		element.getStyle().setOpacity( 0 );

		showTimer = new Timer()
		{
			@Override
			public void run()
			{
				String opacityStr;
				boolean increased = false;

				opacityStr = element.getStyle().getOpacity();
				if ( opacityStr != null && opacityStr.length() > 0 )
				{
					try
					{
						BigDecimal opacity;
				
						opacity = new BigDecimal( opacityStr );
						if ( opacity.compareTo( new BigDecimal( 1 ) ) < 0 )
						{
							element.getStyle().setOpacity( opacity.add( DELTA ).doubleValue() );
							increased = true;
						}
					}
					catch ( NumberFormatException nfe )
					{
					}
				}
				
				if ( increased == false )
				{
					element.getStyle().setOpacity( 1 );
					cancel();
				}
			}
		};
         
		showTimer.scheduleRepeating( 75 );
	}
}
