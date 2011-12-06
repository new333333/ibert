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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetMyTeamsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetMyTeamsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * Class used for the My Teams menu item popup.  
 * 
 * @author drfoster@novell.com
 */
public class MyTeamsMenuPopup extends MenuBarPopupBase {
	private final String IDBASE = "myTeams_";
	
	@SuppressWarnings("unused")
	private BinderInfo m_currentBinder;	// The currently selected binder.
	
	/*
	 * Inner class that handles selecting an individual team.
	 */
	private class TeamCommand implements Command {
		private TeamInfo m_myTeam;	// The team selected on.

		/**
		 * Class constructor.
		 * 
		 * @param myTeam
		 */
		TeamCommand(TeamInfo myTeam) {
			// Simply store the parameter.
			m_myTeam = myTeam;
		}

		/**
		 * Called when the user selects a team.
		 * 
		 * Implements the Command.execute() method.
		 */
		@Override
		public void execute() {
			// Hide the menu...
			hideMenu();
			
			// ...and fire a change context event.
			GwtTeaming.fireEvent(
				new ChangeContextEvent(
					new OnSelectBinderInfo(
						m_myTeam.getBinderId(),
						m_myTeam.getPermalinkUrl(),
						false,
						Instigator.TEAM_SELECT)));
		}
	}
	
	/**
	 * Class constructor.
	 */
	public MyTeamsMenuPopup() {
		// Initialize the super class.
		super(GwtTeaming.getMessages().mainMenuBarMyTeams());
	}
	
	/**
	 * Stores information about the currently selected binder.
	 * 
	 * Implements the MenuBarPopupBase.setCurrentBinder() abstract
	 * method.
	 * 
	 * @param binderInfo
	 */
	@Override
	public void setCurrentBinder(BinderInfo binderInfo) {
		// Simply store the parameter.
		m_currentBinder = binderInfo;
	}
	
	/**
	 * Not used for the My Teams menu.
	 * 
	 * Implements the MenuBarPopupBase.setToolbarItemList() abstract
	 * method.
	 * 
	 * @param toolbarItemList
	 */
	@Override
	public void setToolbarItemList(List<ToolbarItem> toolbarItemList) {
		// Unused.
	}
	
	/*
	 * Asynchronously shows the 'My Teams' popup menu.
	 */
	private void showMyTeamsMenuAsync(final List<TeamInfo> mtList) {
		ScheduledCommand showMenu = new ScheduledCommand() {
			@Override
			public void execute() {
				showMyTeamsMenuNow(mtList);
			}
		};
		Scheduler.get().scheduleDeferred(showMenu);
	}
	
	/*
	 * Synchronously shows the 'My Teams' popup menu.
	 */
	private void showMyTeamsMenuNow(List<TeamInfo> mtList) {
		// Scan the teams...
		int mtCount = 0;
		MenuPopupAnchor mtA;
		for (Iterator<TeamInfo> mtIT = mtList.iterator(); mtIT.hasNext(); ) {
			// ...creating an item structure for each.
			TeamInfo mt = mtIT.next();
			String mtId = (IDBASE + mt.getBinderId());
			
			mtA = new MenuPopupAnchor(mtId, mt.getTitle(), mt.getEntityPath(), new TeamCommand(mt));
			addContentMenuItem(mtA);
			mtCount += 1;
		}
		
		// If there weren't any teams...
		if (0 == mtCount) {
			// ...put something in the menu that tells the user
			// ...that.
			MenuPopupLabel content = new MenuPopupLabel(m_messages.mainMenuMyTeamsNoTeams());
			addContentMenuItem(content);
		}
		
		// Finally, show the menu popup.
		show();
	}
	
	/**
	 * Not used for the My Teams menu.  Always returns true.
	 * 
	 * Implements the MenuBarPopupBase.shouldShowMenu() abstract
	 * method.
	 * 
	 * @return
	 */
	@Override
	public boolean shouldShowMenu() {
		return true;
	}
	
	/**
	 * Completes construction of the menu and shows it.
	 * 
	 * Implements the MenuBarPopupBase.showPopup() abstract method.
	 * 
	 * @param left
	 * @param top
	 */
	@Override
	public void showPopup(int left, int top) {
		GetMyTeamsCmd cmd;
		
		// Position the popup and if we've already constructed its
		// content...
		setPopupPosition(left, top);
		if (hasContent()) {
			// ...simply show it and bail.
			show();
			return;
		}
		
		// Otherwise, read the users teams.
		cmd = new GetMyTeamsCmd();
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetMyTeams());
			}
			
			public void onSuccess(VibeRpcResponse response)  {
				List<TeamInfo> mtList;
				GetMyTeamsRpcResponseData responseData;
				
				responseData = (GetMyTeamsRpcResponseData) response.getResponseData();
				mtList = responseData.getTeams();
				
				// Show the 'My Teams' popup menu asynchronously so
				// that we can release the AJAX request ASAP.
				showMyTeamsMenuAsync(mtList);
			}
		});
	}	
}
