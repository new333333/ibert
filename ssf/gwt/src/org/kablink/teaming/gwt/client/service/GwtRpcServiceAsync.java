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
package org.kablink.teaming.gwt.client.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.profile.DiskUsageInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;
import org.kablink.teaming.gwt.client.util.TaskListItem.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamParams;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.SubscriptionData;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskDate;
import org.kablink.teaming.gwt.client.util.TaskId;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.whatsnew.EventValidation;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author jwootton
 */
public interface GwtRpcServiceAsync
{
	// Execute the given command.
	public void executeCommand( HttpRequestInfo ri, VibeRpcCmd cmd, AsyncCallback<VibeRpcResponse> callback );
	
	
	
	// The following are used to manage the tracking of information.
	public void getTrackedPeople( HttpRequestInfo ri,                  AsyncCallback<List<String>> callback );
	public void getTrackedPlaces( HttpRequestInfo ri,                  AsyncCallback<List<String>> callback );
	public void isPersonTracked(  HttpRequestInfo ri, String binderId, AsyncCallback<Boolean>      callback );
	public void trackBinder(      HttpRequestInfo ri, String binderId, AsyncCallback<Boolean>      callback );
	public void untrackBinder(    HttpRequestInfo ri, String binderId, AsyncCallback<Boolean>      callback );
	public void untrackPerson(    HttpRequestInfo ri, String binderId, AsyncCallback<Boolean>      callback );
	
	// Return information about the User Profile
	public void getProfileInfo(		HttpRequestInfo ri, String binderId,                AsyncCallback<ProfileInfo> 	    callback );
	public void getProfileStats(	HttpRequestInfo ri, String binderId, String userId, AsyncCallback<ProfileStats> 	callback );
	public void getProfileAvatars(	HttpRequestInfo ri, String binderId,                AsyncCallback<ProfileAttribute> callback );
	public void getQuickViewInfo( 	HttpRequestInfo ri, String binderId,                AsyncCallback<ProfileInfo> 	    callback );
	public void getTeams(			HttpRequestInfo ri, String binderId,                AsyncCallback<List<TeamInfo>>   callback );
	public void getGroups(			HttpRequestInfo ri, String binderId,                AsyncCallback<List<GroupInfo>>  callback );
	public void getMicrBlogUrl( 	HttpRequestInfo ri, String binderId,                AsyncCallback<String> 			callback );
	public void isPresenceEnabled(  HttpRequestInfo ri,                                 AsyncCallback<Boolean>			callback );
	public void getImUrl(			HttpRequestInfo ri, String binderId,                AsyncCallback<String> 			callback );
	public void getPresenceInfo(    HttpRequestInfo ri, String binderId,                AsyncCallback<GwtPresenceInfo>  callback );

	// Return the URL for the start/schedule meeting page
	public void getAddMeetingUrl( HttpRequestInfo ri, String binderId, AsyncCallback<String> callback);

	// The following are used for the UserStatus control
	public void saveUserStatus( HttpRequestInfo ri, String status, 	 AsyncCallback<Boolean>    callback );
	public void getUserStatus(  HttpRequestInfo ri, String binderId, AsyncCallback<UserStatus> callback ); 
	
	// Get DiskUsage Info.
	public void getDiskUsageInfo( HttpRequestInfo ri, String binderId, AsyncCallback<DiskUsageInfo> callback );

	// Activity Stream servicing APIs.
	public void getActivityStreamData(          HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi, PagingData pd,                              AsyncCallback<ActivityStreamData>   callback );
	public void getActivityStreamData(          HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi, PagingData pd, ActivityStreamDataType asdt, AsyncCallback<ActivityStreamData>   callback );
	public void getActivityStreamData(          HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi,                                             AsyncCallback<ActivityStreamData>   callback );
	public void getActivityStreamData(          HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi,                ActivityStreamDataType asdt, AsyncCallback<ActivityStreamData>   callback );
	
	// Validate the given TeamingEvents for the given entry id.
	public void validateEntryEvents( HttpRequestInfo ri, ArrayList<EventValidation> eventValidations, String entryId, AsyncCallback<ArrayList<EventValidation>> callback );

	// Get subscription information for the given entry id.
	public void getSubscriptionData( HttpRequestInfo ri, String entryId, AsyncCallback<SubscriptionData> callback );
	
	// Save the given subscription data for the given entry id.
	public void saveSubscriptionData( HttpRequestInfo ri, String entryId, SubscriptionData subscriptionData, AsyncCallback<Boolean> callback );

	// Task servicing APIs.
	public void collapseSubtasks(      HttpRequestInfo ri, Long binderId, Long         entryId,                           AsyncCallback<Boolean>              callback );
	public void deleteTask(            HttpRequestInfo ri, Long binderId, Long         entryId,                           AsyncCallback<Boolean>              callback );
	public void deleteTasks(           HttpRequestInfo ri,                List<TaskId> taskIds,                           AsyncCallback<Boolean>              callback );
	public void expandSubtasks(        HttpRequestInfo ri, Long binderId, Long         entryId,                           AsyncCallback<Boolean>              callback );
	public void getGroupMembership(    HttpRequestInfo ri,                Long         groupId,                           AsyncCallback<List<AssignmentInfo>> callback );
	public void getTaskList(           HttpRequestInfo ri, Long binderId, String       filterType, String modeType,       AsyncCallback<List<TaskListItem>>   callback );
	public void getTaskBundle(         HttpRequestInfo ri, Long binderId, String       filterType, String modeType,       AsyncCallback<TaskBundle>           callback );
	public void getTaskLinkage(        HttpRequestInfo ri, Long binderId,                                                 AsyncCallback<TaskLinkage>          callback );
	public void getTeamMembership(     HttpRequestInfo ri, Long binderId,                                                 AsyncCallback<List<AssignmentInfo>> callback );
	public void purgeTask(             HttpRequestInfo ri, Long binderId, Long         entryId,                           AsyncCallback<Boolean>              callback );
	public void purgeTasks(            HttpRequestInfo ri,                List<TaskId> taskIds,                           AsyncCallback<Boolean>              callback );
	public void removeTaskLinkage(     HttpRequestInfo ri, Long binderId,                                                 AsyncCallback<Boolean>              callback );
	public void saveTaskCompleted(     HttpRequestInfo ri, Long binderId, Long         entryId,    String  completed,     AsyncCallback<String>               callback );
	public void saveTaskCompleted(     HttpRequestInfo ri,                List<TaskId> taskIds,    String  completed,     AsyncCallback<String>               callback );
	public void saveTaskLinkage(       HttpRequestInfo ri, Long binderId, TaskLinkage  taskLinkage,                       AsyncCallback<Boolean>              callback );
	public void saveTaskPriority(      HttpRequestInfo ri, Long binderId, Long         entryId,    String  priority,      AsyncCallback<Boolean>              callback );
	public void saveTaskSort(          HttpRequestInfo ri, Long binderId, String       sortKey,    boolean sortAscending, AsyncCallback<Boolean>              callback );
	public void saveTaskStatus(        HttpRequestInfo ri, Long binderId, Long         entryId,    String  status,        AsyncCallback<String>               callback );
	public void saveTaskStatus(        HttpRequestInfo ri,                List<TaskId> taskIds,    String  status,        AsyncCallback<String>               callback );
	public void updateCalculatedDates( HttpRequestInfo ri, Long binderId, Long         entryId,                           AsyncCallback<Map<Long, TaskDate>>  callback );
}// end GwtRpcServiceAsync
