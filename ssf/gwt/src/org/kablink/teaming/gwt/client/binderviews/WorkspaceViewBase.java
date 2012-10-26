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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetWorkspaceContributorIdsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetWorkspaceContributorIdsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.HandlerRegistration;


/**
 * Base class that workspace views MUST extend.
 * 
 * @author jwootton@novell.com
 */
public abstract class WorkspaceViewBase extends ViewBase
	implements ContributorIdsRequestEvent.Handler
{
	private List<Long> m_contributorIds;
	private BinderInfo m_binderInfo;
	private List<HandlerRegistration> m_registeredEventHandlers;	// Event handlers that are currently registered.

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[]
	{
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST
	};
	
	/**
	 * Constructor method.
	 * 
	 * MUST be called by classes that extend this base class.
	 */
	public WorkspaceViewBase( BinderInfo binderInfo, ViewReady viewReady )
	{
		super( viewReady );
		
		m_binderInfo = binderInfo;
		m_contributorIds = new ArrayList<Long>();
		
		ScheduledCommand cmd;
		cmd = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				// Get the contributor ids for this workspace
				requestContributorIds();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}

	/**
	 * 
	 */
	public Long getBinderId()
	{
		if ( m_binderInfo != null )
			return m_binderInfo.getBinderIdAsLong();
		
		return null;
	}
	
	/**
	 * 
	 */
	public String getBinderIdAsString()
	{
		Long binderId;
		
		binderId = getBinderId();
		if ( binderId != null )
			return String.valueOf( binderId );
		
		return null;
	}
	
	/**
	 * 
	 */
	public BinderInfo getBinderInfo()
	{
		return m_binderInfo;
	}
	
	/**
	 * 
	 */
	public List<Long> getContributorIds()
	{
		return m_contributorIds;
	}

	/**
	 * Called when this WorkspaceViewBase is attached.
	 * 
	 */
	@Override
	public void onAttach()
	{
		// Let the widget attach and then register our event handlers.
		super.onAttach();
	
		registerEvents();
	}
	
	/**
	 * Called when the data table is detached.
	 * 
	 */
	@Override
	public void onDetach() 
	{
		// Let the widget detach and then unregister our event handlers.
		super.onDetach();
		
		unregisterEvents();
	}
	
	/**
	 * Handles ContributorIdsRequestEvent's received by this class.
	 * 
	 * Implements the ContributorIdsRequestEvent.Handler.onContributorIdsRequest() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContributorIdsRequest( ContributorIdsRequestEvent event )
	{
		// Is the event targeted to this folder?
		final Long eventBinderId = event.getBinderId();

		// Is this request for the workspace we are working with?
		if ( eventBinderId.equals( getBinderId() ) )
		{
			ScheduledCommand cmd;
				
			// Yes!  Asynchronously fire the corresponding reply event with the contributor IDs.
			cmd = new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					ContributorIdsReplyEvent replyEvent;
					
					replyEvent = new ContributorIdsReplyEvent(
														eventBinderId,
														m_contributorIds ); 
					GwtTeaming.fireEvent( replyEvent );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() 
	{
		// If we having allocated a list to track events we've registered yet...
		if ( null == m_registeredEventHandlers ) 
		{
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if ( m_registeredEventHandlers.isEmpty() ) 
		{
			// ...register the events.
			EventHelper.registerEventHandlers(
										GwtTeaming.getEventBus(),
										m_registeredEvents,
										this,
										m_registeredEventHandlers );
		}
	}

	/**
	 * Issue an rpc request to get the contributor ids for this workspace
	 */
	private void requestContributorIds()
	{
		Long binderId;
		
		binderId = getBinderId();
		if ( binderId != null )
		{
			GetWorkspaceContributorIdsCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback = null;
			
			// Create the callback that will be used when we issue an ajax call to
			// get the list of contributor ids.
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( final Throwable t )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							GwtClientHelper.handleGwtRPCFailure(
									t,
									GwtTeaming.getMessages().rpcFailure_GetWorkspaceContributorIds() );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( final VibeRpcResponse response )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						@Override
						public void execute()
						{
							GetWorkspaceContributorIdsRpcResponseData responseData;
							
							responseData = (GetWorkspaceContributorIdsRpcResponseData) response.getResponseData();
							m_contributorIds = responseData.getContributorIds();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			
			// Issue an rpc request to get the contributor ids for this workspace
			cmd = new GetWorkspaceContributorIdsCmd( binderId );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}

	/**
	 * 
	 */
	public void setBinderInfo( BinderInfo binderInfo )
	{
		m_binderInfo = binderInfo;
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( (null != m_registeredEventHandlers) && !(m_registeredEventHandlers.isEmpty()) )
		{
			// ...unregister them.  (Note that this will also empty the list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}
}