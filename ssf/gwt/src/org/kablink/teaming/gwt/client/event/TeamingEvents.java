/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.event;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This enumeration defines all possible types of events generated by
 * Vibe.
 * 
 * Note:  The events defined here must also be maintained in
 *    EventHelper.registerEventHandlers().
 * 
 * @author drfoster@novell.com
 */
public enum TeamingEvents implements IsSerializable {
	ACCESS_TO_ITEM_DENIED,							// Event that informs that access to a given item has been denied.
	ACTIVITY_STREAM,								// Changes the selected an activity stream.
	ACTIVITY_STREAM_ENTER,							// Enters activity stream mode.
	ACTIVITY_STREAM_EXIT,							// Exits  activity stream mode.
	
	ADMINISTRATION,									// Enters administration mode.
	ADMINISTRATION_ACTION,							// Performs an administration action.
	ADMINISTRATION_EXIT,							// Exits  administration mode.
	ADMINISTRATION_UPGRADE_CHECK,					// Tell the administration control to check for upgrade tasks that need to be performed.
	
	BLOG_ARCHIVE_FOLDER_SELECTED,					// The user selected a folder in the blog archive control
	BLOG_ARCHIVE_MONTH_SELECTED,					// The user selected a month in the blog archive control
	BLOG_GLOBAL_TAG_SELECTED,						// The user selected a tag in the blog tag control
	BLOG_PAGE_CREATED,								// The user created a blog page.
	BLOG_PAGE_SELECTED,								// The user selected a blog page in the blog page control
	
	BROWSE_HIERARCHY,								// Browse Vibe's hierarchy (i.e., the bread crumb tree.) 
	BROWSE_HIERARCHY_EXIT,							// Exits the bread crumb browser, if open.

	CALENDAR_CHANGED,								// Tells listeners that the calendar in question has changed.
	CALENDAR_GOTO_DATE,								// Tells the calendar to navigate to the given date.
	CALENDAR_HOURS,									// Tells the calendar which hours to display in a day (full day or work day.)
	CALENDAR_NEXT_PERIOD,							// Tells the calendar to display the next     time period.
	CALENDAR_PREVIOUS_PERIOD,						// Tells the calendar to display the previous time period.
	CALENDAR_SETTINGS,								// Tells the calendar to invoke its settings dialog.
	CALENDAR_SHOW,									// Tells the calendar which events to display.
	CALENDAR_SHOW_HINT,								// Shows a hint regarding the current CalendarShow mode.
	CALENDAR_VIEW_DAYS,								// Tells the calendar to change is day view.
	
	CHANGE_CONTEXT,									// Tells the UI that a context switch is currently taking place.	(I.e., Is happening.      )
	CONTENT_CHANGED,								// Tells the UI that something in the content panel has changed.
	CONTEXT_CHANGED,								// Tells the UI that a context switch has just taken places.		(I.e., Has happened.      )
	CONTEXT_CHANGING,								// Tells the UI That a context switch is about to take place.		(I.e., Is going to happen.)
	
	CONTRIBUTOR_IDS_REPLY,							// Fired with the current set of contributor IDs.
	CONTRIBUTOR_IDS_REQUEST,						// Requests that the current contributor IDs be posted via a CONTRIBUTOR_IDS_REPLY event.
	
	DELETE_ACTIVITY_STREAM_UI_ENTRY,				// Delete the given ActivityStreamUIEntry.
	EDIT_ACTIVITY_STREAM_UI_ENTRY,					// Edit   the given ActivityStreamUIEntry.

	EDIT_CURRENT_BINDER_BRANDING,					// Edits the branding on the current binder.
	EDIT_LANDING_PAGE_PROPERTIES,					// Edit the landing page properties
	EDIT_PERSONAL_PREFERENCES,						// Edits the user's personal preferences.
	EDIT_SITE_BRANDING,								// Edits the site branding.

	FILES_DROPPED,									// Fired when files have been successfully dropped on the drag and drop applet.
	FULL_UI_RELOAD,									// Forces the full Vibe UI to be reloaded.
	
	GOTO_CONTENT_URL,								// Changes the current context to a non-permalink URL.
	GOTO_MY_WORKSPACE,								// Changes the current context to the user's workspace.
	GOTO_PERMALINK_URL,								// Changes the current context to a permalink URL.
	GOTO_URL,										// Changes the content to the given url.

	GROUP_CREATED,									// Notification that a group was created.
	GROUP_MEMBERSHIP_MODIFICATION_FAILED,			// Notification that the process of modifying a group's membership has failed.
	GROUP_MEMBERSHIP_MODIFICATION_STARTED,			// Notification that the process of modifying a group's membership has started.
	GROUP_MEMBERSHIP_MODIFIED,						// Notification that a group's membership was modified.
	GROUP_MODIFICATION_FAILED,						// Notification that the process of modifying a group has failed.
	GROUP_MODIFICATION_STARTED,						// Notification that the process of modifying a group has started.
	GROUP_MODIFIED,									// Notification that a group was modified.

	INVOKE_ABOUT,									// Invokes the About dialog.
	INVOKE_ADD_NEW_FOLDER,							// Invokes the Add New Folder dialog.
	INVOKE_BINDER_SHARE_RIGHTS_DLG,					// Invokes the 'binder share rights' dialog.
	INVOKE_CHANGE_PASSWORD_DLG,						// Invoke the Change Password dialog.
	INVOKE_CLIPBOARD,								// Invokes the Clipboard dialog.
	INVOKE_COLUMN_RESIZER,							// Invokes the column resizing dialog.
	INVOKE_CONFIGURE_ADHOC_FOLDERS_DLG,				// Invokes the Configure Adhoc Folders dialog
	INVOKE_CONFIGURE_COLUMNS,						// Invokes the Configure Columns dialog.
	INVOKE_CONFIGURE_FILE_SYNC_APP_DLG,				// Invokes the Configure File Sync App dialog.
	INVOKE_CONFIGURE_MOBILE_APPS_DLG,				// Invokes the Configure Mobile Apps dialog.
	INVOKE_CONFIGURE_PASSWORD_POLICY_DLG,			// Invokes the Configure Password Policy dialog.
	INVOKE_CONFIGURE_SHARE_SETTINGS_DLG,			// Invokes the Configure Share Settings dialog.
	INVOKE_CONFIGURE_UPDATE_LOGS_DLG,				// Invokes the Configure Update Logs dialog.
	INVOKE_CONFIGURE_USER_ACCESS_DLG,				// Invokes the Configure User Access dialog.
	INVOKE_COPY_FILTERS_DLG,						// Invokes the 'Copy Filters' dialog.
	INVOKE_DOWNLOAD_DESKTOP_APP,					// Invokes the desktop application download page.
	INVOKE_DROPBOX,									// Invokes the files drop box (i.e., the file drag&drop applet.)
	INVOKE_EDIT_IN_PLACE,							// Invokes the edit-in-place on the specified file.
	INVOKE_EDIT_KEYSHIELD_CONFIG_DLG,				// Invokes the edit KeyShield config dialog.
	INVOKE_EDIT_LDAP_CONFIG_DLG,					// Invokes the edit ldap config dialog.
	INVOKE_EDIT_NET_FOLDER_DLG,						// Invokes the edit net folder dialog
	INVOKE_EMAIL_NOTIFICATION,						// Invokes the Email Notification dialog.
	INVOKE_MANAGE_NET_FOLDERS_DLG,					// Invokes the Manage Net Folders dialog
	INVOKE_MANAGE_NET_FOLDER_ROOTS_DLG,				// Invokes the Manage Net Folder Roots dialog
	INVOKE_NAME_COMPLETION_SETTINGS_DLG,			// Invokes the Name Completion Settings dialog.
	INVOKE_HELP,									// Invokes the Vibe online help.
	INVOKE_IMPORT_ICAL_FILE,						// Invokes the dialog to import an iCal by uploading  a file.
	INVOKE_IMPORT_ICAL_URL,							// Invokes the dialog to import an iCal by specifying a URL.
	INVOKE_IMPORT_PROFILES_DLG,						// Invokes the "Import Profiles" dialog.
	INVOKE_NET_FOLDER_GLOBAL_SETTINGS_DLG,			// Invokes the "Net Folder Global Settings" dialog.
	INVOKE_LDAP_SYNC_RESULTS_DLG,					// Invokes the "Ldap sync results" dialog.
	INVOKE_LIMIT_USER_VISIBILITY_DLG,				// Invokes the Limit User Visibility dialog.
	INVOKE_MANAGE_ADMINISTRATORS_DLG,				// Invokes the "Manage administrators" dialog.
	INVOKE_MANAGE_DATABASE_PRUNE_DLG,				// Invokes the "Manage database prune" dialog.
	INVOKE_MANAGE_GROUPS_DLG,						// Invokes the "Manage groups" dialog.
	INVOKE_MANAGE_MOBILE_DEVICES_DLG,				// Invokes the "Manage mobile devices" dialog.
	INVOKE_MANAGE_USERS_DLG,						// Invokes the "Manage users" dialog.
	INVOKE_MANAGE_TEAMS_DLG,						// Invokes the "Manage teams" dialog.
	INVOKE_EDIT_NET_FOLDER_RIGHTS_DLG,				// Invokes the "edit net folder rights" dialog.
	INVOKE_EDIT_SHARE_RIGHTS_DLG,					// Invokes the "edit share rights" dialog.
	INVOKE_EDIT_USER_ZONE_SHARE_RIGHTS_DLG,			// Invokes the "edit user zone share rights" dialog.
	INVOKE_PRINCIPAL_DESKTOP_SETTINGS_DLG,			// Invokes the 'principal desktop application settings' dialog.
	INVOKE_PRINCIPAL_MOBILE_SETTINGS_DLG,			// Invokes the 'principal mobile  application settings' dialog.
	INVOKE_RENAME_ENTITY,							// Invokes the Rename an Entity dialog.
	INVOKE_REPLY,									// Invokes the 'reply to entry' UI.
	INVOKE_RUN_A_REPORT_DLG,						// Invokes the "Run a Report" dialog.
	INVOKE_SEND_TO_FRIEND,							// Invokes the "Send to friend" dialog.
	INVOKE_SEND_EMAIL_TO_TEAM,						// Invokes the Send Email To Team dialog.
	INVOKE_SHARE,									// Invokes the 'share this entry' UI.
	INVOKE_SHARE_BINDER,							// Invokes the 'share this binder' ui.
	INVOKE_SIGN_GUESTBOOK,							// Invokes the 'sign the guest book' UI.
	INVOKE_SIMPLE_PROFILE,							// Invokes the simple profile dialog.
	INVOKE_SUBSCRIBE,								// Invokes the 'subscribe to this entry' UI.
	INVOKE_TAG,										// Invokes the 'tag this entry' UI.
	INVOKE_USER_PROPERTIES_DLG,						// Invokes the 'user properties'        dialog.
	INVOKE_USER_SHARE_RIGHTS_DLG,					// Invokes the 'user share rights'      dialog.
	INVOKE_WORKSPACE_SHARE_RIGHTS,					// Invokes the 'workspace share rights' dialog.
	
	JSP_LAYOUT_CHANGED,								// The layout of JSP content has changed.
	
	LOGIN,											// Logs into   Vibe.
	LOGOUT,											// Logs out of Vibe.
	PRE_LOGOUT,										// Notifies everybody that we're about to log out of Vibe.
	
	PREVIEW_LANDING_PAGE,							// Invoke the ui to preview the landing page.

	MARK_ENTRY_READ,								// Mark the entry as read.
	MARK_ENTRY_UNREAD,								// Mark the entry as unread.
	MARK_FOLDER_CONTENTS_READ,						// Mark the contents of a folder as read.
	MARK_FOLDER_CONTENTS_UNREAD,					// Mark the contents of a folder as unread.
	
	MASTHEAD_HIDE,									// Hides the masthead.
	MASTHEAD_SHOW,									// Shows the masthead.
	MASTHEAD_UNHIGHLIGHT_ALL_ACTIONS,				// Signals that all actions in the mast head should be unhighlighted
	
	GET_MANAGE_MENU_POPUP,							// Returns the manage menu (Workspace or Folder) currently loaded in the main menu bar.
	HIDE_MANAGE_MENU,								// Hides the manage menu loaded in the main menu bar.
	LDAP_SYNC_STATUS,								// Fired when the ldap sync status changes.
	MANAGE_USERS_FILTER,							// Set or clears a filter in the manage users dialog.
	MENU_HIDE,										// Hides the main menu
	MENU_LOADED,									// Fired when each item on the main menu bar has loaded.
	MENU_SHOW,										// Shows the main menu
	SHARED_VIEW_FILTER,								// Set or clears a filter in a Shared By/With Me view.

	NET_FOLDER_CREATED,								// Notification that a net folder was created.
	NET_FOLDER_MODIFIED,							// Notification that a net folder was modified.

	NET_FOLDER_ROOT_CREATED,						// Notification that a net folder root was created.
	NET_FOLDER_ROOT_MODIFIED,						// Notification that a net folder root was modified.

	QUICK_FILTER,									// Creates or Clears a Quick Filter on the Folder.
	
	SEARCH_ADVANCED,								// Runs the advanced Search in the content area.
	SEARCH_FIND_RESULTS,							// Fired when the FindCtrl is returning its results.
	SEARCH_RECENT_PLACE,							// Executes a recent place search.
	SEARCH_SAVED,									// Executes a saved search using a string as the name.
	SEARCH_SIMPLE,									// Performs a simple search on a string.
	SEARCH_TAG,										// Executes a search using a string as a tag Name.
	
	SET_SHARE_RIGHTS,								// Set the share rights
	
	SHARE_EXPIRATION_VALUE_CHANGED,					// Notification that the share expiration value changed.
	
	SHOW_BLOG_FOLDER,								// Show a blog folder.
	SHOW_CALENDAR_FOLDER,							// Shows a calendar folder.
	SHOW_COLLECTION,								// Show a given collection point
	SHOW_COLLECTION_VIEW,							// Shows a collection view.
	SHOW_CONTENT_CONTROL,							// Shows the ContentControl.
	SHOW_DISCUSSION_FOLDER,							// Shows a discussion folder.
	SHOW_DISCUSSION_WORKSPACE,						// Shows a discussion workspace.
	SHOW_FILE_FOLDER,								// Shows a file folder.
	SHOW_FOLDER_ENTRY,								// Shows a folder entry view.
	SHOW_GENERIC_WORKSPACE,							// Shows a generic workspace.
	SHOW_GLOBAL_WORKSPACE,							// Shows the global workspace
	SHOW_GUESTBOOK_FOLDER,							// Shows a guest book folder.
	SHOW_HOME_WORKSPACE,							// Shows the home (top) workspace
	SHOW_LANDING_PAGE,								// Shows a landing page.
	SHOW_MICRO_BLOG_FOLDER,							// Shows a micro-blog folder.
	SHOW_MILESTONE_FOLDER,							// Shows a milestone folder.
	SHOW_MIRRORED_FILE_FOLDER,						// Shows a mirrored file folder.
	SHOW_NET_FOLDERS_WORKSPACE,						// Shows the root Net Folders workspace.
	SHOW_PERSONAL_WORKSPACE,						// Shows a Personal Workspace view.
	SHOW_PERSONAL_WORKSPACES,						// Shows the Personal workspaces binder.
	SHOW_PHOTO_ALBUM_FOLDER,						// Shows a photo album folder.
	SHOW_PROJECT_MANAGEMENT_WORKSPACE,				// Show a project management workspace.
	SHOW_SURVEY_FOLDER,								// Shows a survey folder.
	SHOW_TASK_FOLDER,								// Shows a task folder.
	SHOW_TEAM_ROOT_WORKSPACE,						// Shows the team root workspace.
	SHOW_TEAM_WORKSPACE,							// Shows a team workspace.
	SHOW_TRASH,										// Shows a trash view.
	SHOW_VIEW_PERMALINKS,							// Shows the permaLinks in the current view.
	SHOW_WIKI_FOLDER,								// Shows a wiki folder.
	
	HIDE_ACCESSORIES,								// Hides the accessories panel on the given binder.
	HIDE_USER_LIST,									// Hides the user list panel on the given binder.
	SHOW_ACCESSORIES,								// Shows the accessories panel on the given binder.
	SHOW_USER_LIST,									// Shows the user list panel on the given binder.
	
	GET_CURRENT_VIEW_INFO,							// Returns the current ViewInfo loaded in the content control.
	GET_SIDEBAR_COLLECTION,							// Returns the collection currently loaded in the sidebar tree.
	REFRESH_SIDEBAR_TREE,							// Refreshes the sidebar tree content maintaining its current root and selected binder.
	REROOT_SIDEBAR_TREE,							// Re-roots the sidebar tree to the currently selected binder.
	SET_FILR_ACTION_FROM_COLLECTION_TYPE,			// Sets the Filr action in the masthead from a collection type.
	SIDEBAR_HIDE,									// Hides the left navigation panel.
	SIDEBAR_SHOW,									// Shows the left navigation panel.
	
	SIZE_CHANGED,									// The size of something changed.
	
	TASK_DELETE,									// Delete the Selected Tasks.
	TASK_HIERARCHY_DISABLED,						// Shows the reasons why task hierarchy manipulation is disabled.
	TASK_LIST_READY,								// The task list in the specified folder has been read and is ready to use.
	TASK_MOVE_DOWN,									// Move the Selected Task Down in the Ordering.
	TASK_MOVE_LEFT,									// Move the Selected Task Left (i.e., Decrease its Subtask Level.)
	TASK_MOVE_RIGHT,								// Move the Selected Task Right (i.e., Increase its Subtask Level.)
	TASK_MOVE_UP,									// Move the Selected Task Up in the Ordering.
	TASK_NEW_TASK,									// Creates a New Task Relative to an Existing One.
	TASK_PICK_DATE,									// Run the date picker with the given ID.
	TASK_PURGE,										// Delete and Immediately Purge the Selected Tasks.
	TASK_SET_PERCENT_DONE,							// Sets a Task's Percentage Done.
	TASK_SET_PRIORITY,								// Sets a Task's Priority.
	TASK_SET_STATUS,								// Sets a Task's Status.
	TASK_VIEW,										// Sets a Task's View.

	TRASH_PURGE_ALL,								// Purges everything in the trash.
	TRASH_PURGE_SELECTED_ENTITIES,					// Purges the selected entries in the trash.
	TRASH_RESTORE_ALL,								// Restores everything in the trash.
	TRASH_RESTORE_SELECTED_ENTITIES,				// Restores the selected entries in the trash.
	
	TREE_NODE_COLLAPSED,							// Fired when the node in a tree is collapsed.
	TREE_NODE_EXPANDED,								// Fired when the node in a tree is expanded.
	
	TRACK_CURRENT_BINDER,							// Tracks the current binder.
	UNTRACK_CURRENT_BINDER,							// Removes tracking from the current binder.
	UNTRACK_CURRENT_PERSON,							// Removes tracking from the current person.
	
	VIEW_ALL_ENTRIES,								// Shows all entries.
	VIEW_CURRENT_BINDER_TEAM_MEMBERS,				// Views the Team Membership of the current binder.
	VIEW_FORUM_ENTRY,								// Opens an entry for viewing.
	VIEW_PINNED_ENTRIES,							// Toggles the state of viewing pinned entries.
	VIEW_RESOURCE_LIBRARY,							// Shows the resource library page.
	VIEW_SELECTED_ENTRY,							// Opens the selected entry for viewing.
	VIEW_TEAMING_FEED,								// Opens the Teaming Feed window.
	VIEW_UNREAD_ENTRIES,							// Show unread entries.
	VIEW_WHATS_UNSEEN_IN_BINDER,					// Shows the items that are unseen in the current binder.
	VIEW_WHATS_NEW_IN_BINDER,						// Shows the items that are new    in the current binder.
	VIEW_WHO_HAS_ACCESS,							// Opens the who has access viewer on an item.

	ADD_PRINCIPAL_ADMIN_RIGHTS,						// Allows selection of users or groups to add administration rights to.
	CHANGE_ENTRY_TYPE_SELECTED_ENTITIES,			// Changes the Entry Type of the Selected Entities.
	CHANGE_FAVORITE_STATE,							// Changes the favorites state of a binder.
	CHECK_MANAGE_DLG_ACTIVE,						// Event to detect if the administration console's manage facilities (i.e., users or teams) is active.
	CLEAR_SCHEDULED_WIPE_SELECTED_MOBILE_DEVICES,	// Clears the scheduled wipe state from the selected mobile devices.
	CLEAR_SELECTED_USERS_ADHOC_FOLDERS,				// Clears the adHoc folder setting for the Selected Users so they revert to the zone setting.
	CLEAR_SELECTED_USERS_DOWNLOAD,					// Clears the download   setting for the Selected Users so they revert to the zone setting.
	CLEAR_SELECTED_USERS_WEBACCESS,					// Clears the web access setting for the Selected Users so they revert to the zone setting.
	COPY_PUBLIC_LINK_SELECTED_ENTITIES,				// Copies the Public Link of the Selected Entities.
	COPY_SELECTED_ENTITIES,							// Copies the Selected Entities.
	DELETE_SELECTED_ENTITIES,						// Deletes the Selected Entities.
	DELETE_SELECTED_MOBILE_DEVICES,					// Deletes the Selected Mobile Devices.
	DELETE_SELECTED_USERS,							// Deletes the Selected Users.
	DIALOG_CLOSED,									// Signals that a DlgBox has closed.
	DISABLE_SELECTED_USERS,							// Disables the Selected Users.
	DISABLE_SELECTED_USERS_ADHOC_FOLDERS,			// Disables adHoc folders for the Selected Users.
	DISABLE_SELECTED_USERS_DOWNLOAD,				// Disables downloading of files for the Selected Users.
	DISABLE_SELECTED_USERS_WEBACCESS,				// Disables web access for the Selected Users.
	DOWNLOAD_FOLDER_AS_CSV_FILE,					// Downloads then entries in a folder as a CSV file.
	EDIT_PUBLIC_LINK_SELECTED_ENTITIES,				// Edits the Public Link of the Selected Entities.
	EMAIL_PUBLIC_LINK_SELECTED_ENTITIES,			// Emails the Public Link of the Selected Entities.
	ENABLE_SELECTED_USERS,							// Enables the Selected Users.
	ENABLE_SELECTED_USERS_ADHOC_FOLDERS,			// Enables adHoc folders for the the Selected Users.
	ENABLE_SELECTED_USERS_DOWNLOAD,					// Enables downloading of file for the the Selected Users.
	ENABLE_SELECTED_USERS_WEBACCESS,				// Enables web access for the the Selected Users.
	FIND_CONTROL_BROWSE,							// Invoke the find control browser.
	FOLDER_ENTRY_ACTION_COMPLETE,					// An action on a folder entry has completed.
	FORCE_FILES_UNLOCK,								// Forces the primary file on the selected entities to be unlocked.
	FORCE_SELECTED_USERS_TO_CHANGE_PASSWORD,		// Forces the selected users to change their password after their next successful login.
	GET_MANAGE_TITLE,								// Event to request the management component's title.
	GET_MASTHEAD_LEFT_EDGE,							// Event to request the masthead's left edge widget.
	HIDE_SELECTED_SHARES,							// Marks the selected shares as being hidden.
	LOCK_SELECTED_ENTITIES,							// Locks the Selected Entities.
	MAILTO_PUBLIC_LINK_ENTITY,						// Mails the Public Link of the Entity Using a 'mailto://...' URL.
	MANAGE_SHARES_SELECTED_ENTITIES,				// Invokes the Share dialog in administrative mode.
	MANAGE_USER_VISIBILITY,							// Invokes the limit user visibility dialog.
	MARK_READ_SELECTED_ENTITIES,					// Marks the Selected Entities as     Having Been Read.
	MARK_UNREAD_SELECTED_ENTITIES,					// Marks the Selected Entities as Not Having Been Read.
	MOBILE_DEVICE_WIPE_SCHEDULE_CHANGED,			// Signifies a mobile devices wipe schedule has changed.
	MOVE_SELECTED_ENTITIES,							// Moves the Selected Entities.
	PUBLIC_COLLECTION_STATE_CHANGED,				// The Visibility State of the Public Collection Changed.
	RELOAD_DIALOG_CONTENT,							// Tells a dialog to reload its content.
	RESET_ENTRY_MENU,								// Resets the Entry Menu on the Given Folder.
	SCHEDULE_WIPE_SELECTED_MOBILE_DEVICES,			// Schedules the selected mobile devices to be wiped.
	SET_DESKTOP_DOWNLOAD_APP_CONTROL_VISIBILITY,	// Sets the visibility of the desktop application download control. 
	SET_FOLDER_SORT,								// Sets a folder sort options.
	SET_SELECTED_BINDER_SHARE_RIGHTS,				// Sets the share               rights   for the selected binders.
	SET_SELECTED_PRINCIPALS_ADMIN_RIGHTS,			// Sets admin                   rights on the selected princiapls (users or groups.)
	SET_SELECTED_PRINCIPALS_LIMIT_USER_VISIBILITY,	// Sets limited user visibility rights on the selected princiapls (users or groups.)
	SET_SELECTED_USER_DESKTOP_SETTINGS,				// Sets the desktop application settings for the selected users.
	SET_SELECTED_USER_MOBILE_SETTINGS,				// Sets the mobile  application settings for the selected users.
	SET_SELECTED_USER_SHARE_RIGHTS,					// Sets the share               rights   for the selected users.
	SHARE_SELECTED_ENTITIES,						// Shares the Selected Entities.
	SHOW_SELECTED_SHARES,							// Marks the selected shares as no longer being hidden.
	SUBSCRIBE_SELECTED_ENTITIES,					// Subscribes the Current User to the Selected Entities.
	TOGGLE_SHARED_VIEW,								// Toggles the 'Shared by/with Me' Views Between Files and All Entries.
	UNLOCK_SELECTED_ENTITIES,						// Unlocks the Selected Entities.
	WINDOW_TITLE_SET,								// Window Title has been Set.
	ZIP_AND_DOWNLOAD_FOLDER,						// Zips and Downloads all files in a folder.
	ZIP_AND_DOWNLOAD_SELECTED_FILES,				// Zips and Downloads the currently selected files.

	UNDEFINED;										// Undefined event - Should never be fired !!!

	/**
	 * Converts the ordinal value of a TeamingEvents to its enumeration
	 * equivalent.
	 * 
	 * @param eventOrdinal
	 * 
	 * @return
	 */
	public static TeamingEvents getEnum(int eventOrdinal) {
		TeamingEvents event;
		try {
			event = TeamingEvents.values()[eventOrdinal];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			event = TeamingEvents.UNDEFINED;
		}
		return event;
	}
}
