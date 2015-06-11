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
package org.kablink.teaming.gwt.client.rpc.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This enumeration defines all possible Vibe OnPrem GWT RPC commands.
 * 
 * @author drfoster@novell.com
 */
public enum VibeRpcCmdType implements IsSerializable {
	ABORT_FILE_UPLOAD,
	ADD_FAVORITE,
	ADD_NEW_FOLDER,
	ADD_NEW_PROXY_IDENTITY,
	CAN_ADD_FOLDER,
	CAN_MODIFY_BINDER,
	CHANGE_ENTRY_TYPES,
	CHANGE_FAVORITE_STATE,
	CHANGE_PASSWORD,
	CHECK_NET_FOLDERS_STATUS,
	CHECK_NET_FOLDER_SERVERS_STATUS,
	CLEAR_HISTORY,
	COLLAPSE_SUBTASKS,
	COMPLETE_EXTERNAL_USER_SELF_REGISTRATION,
	COPY_ENTRIES,
	CREATE_CHANGE_LOG_REPORT,
	CREATE_DUMMY_MOBILE_DEVICES,
	CREATE_EMAIL_REPORT,
	CREATE_GROUP,
	CREATE_LICENSE_REPORT,
	CREATE_LOGIN_REPORT,
	CREATE_NET_FOLDER,
	CREATE_NET_FOLDER_ROOT,
	CREATE_USER_ACCESS_REPORT,
	CREATE_USER_ACTIVITY_REPORT,
	DELETE_MOBILE_DEVICES,
	DELETE_NET_FOLDERS,
	DELETE_NET_FOLDER_ROOTS,
	DELETE_PROXY_IDENTITIES,
	DELETE_GROUPS,
	DELETE_SELECTED_USERS,
	DELETE_SELECTIONS,
	DELETE_SHARES,
	DELETE_TASKS,
	DISABLE_USERS,
	DUMP_HISTORY_INFO,
	EDIT_ENTRY,
	EMAIL_PUBLIC_LINK,
	ENABLE_USERS,
	EXECUTE_ENHANCED_VIEW_JSP,
	EXECUTE_LANDING_PAGE_CUSTOM_JSP,
	EXECUTE_SEARCH,
	EXPAND_HORIZONTAL_BUCKET,
	EXPAND_SUBTASKS,
	EXPAND_VERTICAL_BUCKET,
	FIND_USER_BY_EMAIL_ADDRESS,
	FORCE_FILES_UNLOCK,
	FORCE_USERS_TO_CHANGE_PASSWORD,
	GET_ACCESSORY_STATUS,
	GET_ACTIVITY_STREAM_DATA,
	GET_ACTIVITY_STREAM_PARAMS,
	GET_ADD_MEETING_URL,
	GET_ADHOC_FOLDER_SETTING,
	GET_ADMIN_ACTIONS,
	GET_ALL_NET_FOLDERS,
	GET_ALL_NET_FOLDER_ROOTS,
	GET_ALL_GROUPS,
	GET_BINDER_BRANDING,
	GET_BINDER_DESCRIPTION,
	GET_BINDER_FILTERS,
	GET_BINDER_INFO,
	GET_BINDER_OWNER_AVATAR_INFO,
	GET_BINDER_PERMALINK,
	GET_BINDER_REGION_STATE,
	GET_BINDER_SHARING_RIGHTS_INFO,
	GET_BINDER_STATS,
	GET_BINDER_TAGS,
	GET_BLOG_ARCHIVE_INFO,
	GET_BLOG_PAGES,
	GET_CALENDAR_APPOINTMENTS,
	GET_CALENDAR_DISPLAY_DATA,
	GET_CALENDAR_DISPLAY_DATE,
	GET_CALENDAR_NEXT_PREVIOUS_PERIOD,
	GET_CAN_ADD_ENTITIES,
	GET_CAN_ADD_ENTITIES_TO_BINDERS,
	GET_CLICK_ON_TITLE_ACTION,
	GET_CLIPBOARD_TEAM_USERS,
	GET_CLIPBOARD_USERS,
	GET_CLIPBOARD_USERS_FROM_LIST,
	GET_COLLECTION_POINT_DATA,
	GET_COLUMN_WIDTHS,
	GET_DATABASE_PRUNE_CONFIGURATION,
	GET_DATE_STR,
	GET_DATE_TIME_STR,
	GET_DEFAULT_ACTIVITY_STREAM,
	GET_DEFAULT_FOLDER_DEFINITION_ID,
	GET_DEFAULT_STORAGE_ID,
	GET_DESKTOP_APP_DOWNLOAD_INFO,
	GET_DOCUMENT_BASE_URL,
	GET_DOWNLOAD_FILE_URL,
	GET_DOWNLOAD_FOLDER_AS_CSV_FILE_URL,
	GET_DOWNLOAD_SETTING,
	GET_DISK_USAGE_INFO,
	GET_DYNAMIC_MEMBERSHIP_CRITERIA,
	GET_EMAIL_NOTIFICATION_INFORMATION,
	GET_ENTITY_ACTION_TOOLBAR_ITEMS,
	GET_ENTITY_ID,
	GET_ENTITY_ID_LIST,
	GET_ENTITY_PERMALINK,
	GET_ENTITY_RIGHTS,
	GET_ENTRY,
	GET_ENTRY_COMMENTS,
	GET_ENTRY_TAGS,
	GET_ENTRY_TYPES,
	GET_EXECUTE_JSP_URL,
	GET_EXTENSION_FILES,
	GET_EXTENSION_INFO,
	GET_FAVORITES,
	GET_FILE_ATTACHMENTS,
	GET_FILE_CONFLICTS_INFO,
	GET_FILE_SYNC_APP_CONFIGURATION,
	GET_FILE_URL,
	GET_FOLDER,
	GET_FOLDER_COLUMNS,
	GET_FOLDER_DISPLAY_DATA,
	GET_FOLDER_ENTRIES,
	GET_FOLDER_ENTRY_DETAILS,
	GET_FOLDER_ENTRY_TYPE,
	GET_FOLDER_FILTERS,
	GET_FOLDER_HAS_USER_LIST,
	GET_FOLDER_ROWS,
	GET_FOLDER_SORT_SETTING,
	GET_FOLDER_TOOLBAR_ITEMS,
	GET_FOOTER_TOOLBAR_ITEMS,
	GET_GROUP_ACTION_TOOLBAR_ITEMS,
	GET_GROUP_ASSIGNEE_MEMBERSHIP,
	GET_GROUP_MEMBERSHIP,
	GET_GROUP_MEMBERSHIP_INFO,
	GET_GROUPS,
	GET_HELP_URL,
	GET_HISTORY_INFO,
	GET_HORIZONTAL_NODE,
	GET_HORIZONTAL_TREE,
	GET_HTML5_SPECS,
	GET_IM_URL,
	GET_IS_USER_EXTERNAL,
	GET_INHERITED_LANDING_PAGE_PROPERTIES,
	GET_IS_DYNAMIC_GROUP_MEMBERSHIP_ALLOWED,
	GET_NET_FOLDER_GLOBAL_SETTINGS,
	GET_JSP_HTML,
	GET_KEYSHIELD_CONFIG,
	GET_LANDING_PAGE_DATA,
	GET_LDAP_CONFIG,
	GET_LDAP_OBJECT_FROM_AD,
	GET_LDAP_SERVER_DATA,
	GET_LDAP_SYNC_RESULTS,
	GET_LIMIT_USER_VISIBILITY_INFO,
	GET_LIST_OF_CHILD_BINDERS,
	GET_LIST_OF_FILES,
	GET_LOCALES,
	GET_LOGGED_IN_USER_PERMALINK,
	GET_LOGIN_INFO,
	GET_MAILTO_PUBLIC_LINKS,
	GET_MAIN_PAGE_INFO,
	GET_MANAGE_ADMINISTRATORS_INFO,
	GET_MANAGE_MOBILE_DEVICES_INFO,
	GET_MANAGE_PROXY_IDENTITIES_INFO,
	GET_MANAGE_TEAMS_INFO,
	GET_MANAGE_USERS_INFO,
	GET_MANAGE_USERS_STATE,
	GET_MICRO_BLOG_URL,
	GET_MOBILE_APPS_CONFIG,
	GET_MODIFY_BINDER_URL,
	GET_MY_FILES_CONTAINER_INFO,
	GET_MY_TASKS,
	GET_MY_TEAMS,
	GET_NAME_COMPLETION_SETTINGS,
	GET_NET_FOLDER,
	GET_NET_FOLDER_SYNC_STATISTICS,
	GET_NEXT_PREVIOUS_FOLDER_ENTRY_INFO,
	GET_NUMBER_OF_MEMBERS,
	GET_PARENT_BINDER_PERMALINK,
	GET_PASSWORD_EXPIRATION,
	GET_PASSWORD_POLICY_CONFIG,
	GET_PASSWORD_POLICY_INFO,
	GET_PERSONAL_PREFERENCES,
	GET_PERSONAL_WORKSPACE_DISPLAY_DATA,
	GET_PHOTO_ALBUM_DISPLAY_DATA,
	GET_PRESENCE_INFO,
	GET_PRINCIPAL_FILE_SYNC_APP_CONFIG,
	GET_PRINCIPAL_INFO,
	GET_PRINCIPAL_MOBILE_APPS_CONFIG,
	GET_PROFILE_AVATARS,
	GET_PROFILE_ENTRY_INFO,
	GET_PROFILE_INFO,
	GET_PROFILE_STATS,
	GET_PROJECT_INFO,
	GET_PUBLIC_LINKS,
	GET_QUICK_VIEW_INFO,
	GET_RECENT_PLACES,
	GET_REPORTS_INFO,
	GET_SYSTEM_BINDER_PERMALINK,
	GET_ROOT_WORKSPACE_ID,
	GET_SAVED_SEARCHES,
	GET_SELECTED_USERS_DETAILS,
	GET_SELECTION_DETAILS,
	GET_SEND_TO_FRIEND_URL,
	GET_SHARE_LISTS,
	GET_SHARED_VIEW_STATE,
	GET_SHARING_INFO,
	GET_SIGN_GUESTBOOK_URL,
	GET_SITE_ADMIN_URL,
	GET_SITE_BRANDING,
	GET_SUBSCRIPTION_DATA,
	GET_SYSTEM_ERROR_LOG_URL,
	GET_TAG_RIGHTS_FOR_BINDER,
	GET_TAG_RIGHTS_FOR_ENTRY,
	GET_TAG_SORT_ORDER,
	GET_TEAM_ASSIGNEE_MEMBERSHIP,
	GET_TEAM_MANAGEMENT_INFO,
	GET_TEAMS,
	GET_TASK_BUNDLE,
	GET_TASK_DISPLAY_DATA,
	GET_TASK_LINKAGE,
	GET_TASK_LIST,
	GET_TIME_ZONES,
	GET_TOOLBAR_ITEMS,
	GET_TOP_RANKED,
	GET_TRASH_URL,
	GET_UPDATE_LOGS_CONFIG,
	GET_UPGRADE_INFO,
	GET_USER_ACCESS_CONFIG,
	GET_USER_AVATAR,
	GET_USER_LIST_INFO,
	GET_USER_PERMALINK,
	GET_USER_PROPERTIES,
	GET_USER_SHARING_RIGHTS_INFO,
	GET_USER_STATUS,
	GET_USER_WORKSPACE_INFO,
	GET_USER_ZONE_SHARE_SETTINGS,
	GET_VERTICAL_ACTIVITY_STREAMS_TREE,
	GET_VERTICAL_NODE,
	GET_VERTICAL_TREE,
	GET_VIEW_FILE_URL,
	GET_VIEW_FOLDER_ENTRY_URL,
	GET_VIEW_INFO,
	GET_WEBACCESS_SETTING,
	GET_WHO_HAS_ACCESS,
	GET_WIKI_DISPLAY_DATA,
	GET_WORKSPACE_CONTRIBUTOR_IDS,
	GET_ZIP_DOWNLOAD_FILES_URL,
	GET_ZIP_DOWNLOAD_FOLDER_URL,
	GET_ZONE_SHARE_RIGHTS,
	HAS_ACTIVITY_STREAM_CHANGED,
	HIDE_SHARES,
	IMPORT_ICAL_BY_URL,
	IS_ALL_USERS_GROUP,
	IS_PERSON_TRACKED,
	IS_SEEN,
	LDAP_AUTHENTICATE_USER,
	LOCK_ENTRIES,
	MARK_FOLDER_CONTENTS_READ,
	MARK_FOLDER_CONTENTS_UNREAD,
	MARKUP_STRING_REPLACEMENT,
	MODIFY_GROUP,
	MODIFY_GROUP_MEMBERSHIP,
	MODIFY_NET_FOLDER,
	MODIFY_NET_FOLDER_ROOT,
	MODIFY_PROXY_IDENTITY,
	MOVE_ENTRIES,
	PERSIST_ACTIVITY_STREAM_SELECTION,
	PERSIST_NODE_COLLAPSE,
	PERSIST_NODE_EXPAND,
	PIN_ENTRY,
	PURGE_TASKS,
	PUSH_HISTORY_INFO,
	REMOVE_EXTENSION,
	REMOVE_FAVORITE,
	REMOVE_TASK_LINKAGE,
	REMOVE_SAVED_SEARCH,
	RENAME_ENTITY,
	REPLY_TO_ENTRY,
	REQUEST_RESET_PASSWORD,
	SAVE_ACCESSORY_STATUS,
	SAVE_ADHOC_FOLDER_SETTING,
	SAVE_BINDER_REGION_STATE,
	SAVE_BRANDING,
	SAVE_CALENDAR_DAY_VIEW,
	SAVE_CALENDAR_HOURS,
	SAVE_CALENDAR_SETTINGS,
	SAVE_CALENDAR_SHOW,
	SAVE_CLIPBOARD_USERS,
	SAVE_COLUMN_WIDTHS,
	SAVE_DATABASE_PRUNE_CONFIGURATION,
	SAVE_DOWNLOAD_SETTING,
	SAVE_EMAIL_NOTIFICATION_INFORMATION,
	SAVE_FILE_SYNC_APP_CONFIGURATION,
	SAVE_FOLDER_COLUMNS,
	SAVE_FOLDER_ENTRY_DLG_POSITION,
	SAVE_FOLDER_FILTERS,
	SAVE_FOLDER_PINNING_STATE,
	SAVE_FOLDER_SORT,
	SAVE_KEYSHIELD_CONFIG,
	SAVE_NET_FOLDER_GLOBAL_SETTINGS,
	SAVE_LDAP_CONFIG,
	SAVE_MANAGE_USERS_STATE,
	SAVE_MOBILE_APPS_CONFIGURATION,
	SAVE_MULTIPLE_ADHOC_FOLDER_SETTINGS,
	SAVE_MULTIPLE_DOWNLOAD_SETTINGS,
	SAVE_MULTIPLE_WEBACCESS_SETTINGS,
	SAVE_NAME_COMPLETION_SETTINGS,
	SAVE_PASSWORD_POLICY_CONFIG,
	SAVE_PERSONAL_PREFERENCES,
	SAVE_PRINCIPAL_FILE_SYNC_APP_CONFIG,
	SAVE_PRINCIPAL_MOBILE_APPS_CONFIGURATION,
	SAVE_SHARE_EXPIRATION_VALUE,
	SAVE_SHARE_LISTS,
	SAVE_SHARED_FILES_STATE,
	SAVE_SHARED_VIEW_STATE,
	SAVE_SUBSCRIPTION_DATA,
	SAVE_TASK_COMPLETED,
	SAVE_TASK_DUE_DATE,
	SAVE_TASK_GRAPH_STATE,
	SAVE_TASK_LINKAGE,
	SAVE_TASK_PRIORITY,
	SAVE_TASK_SORT,
	SAVE_TASK_STATUS,
	SAVE_SEARCH,
	SAVE_TAG_SORT_ORDER,
	SAVE_UPDATE_LOGS_CONFIG,
	SAVE_USER_ACCESS_CONFIG,
	SAVE_USER_LIST_STATUS,
	SAVE_USER_STATUS,
	SAVE_WEBACCESS_SETTING,
	SAVE_WHATS_NEW_SETTINGS,
	SAVE_ZONE_SHARE_RIGHTS,
	SEND_FORGOTTEN_PWD_EMAIL,
	SEND_SHARE_NOTIFICATION_EMAIL,
	SET_BINDER_SHARING_RIGHTS_INFO,
	SET_DESKTOP_APP_DOWNLOAD_VISIBILITY,
	SET_ENTRIES_PIN_STATE,
	SET_HAS_SEEN_OES_WARNING,
	SET_MOBILE_DEVICES_WIPE_SCHEDULED_STATE,
	SET_PRINCIPALS_ADMIN_RIGHTS,
	SET_SEEN,
	SET_UNSEEN,
	SET_USER_SHARING_RIGHTS_INFO,
	SET_USER_VISIBILITY,
	SHARE_ENTRY,
	SHOW_SHARES,
	START_LDAP_SYNC,
	STOP_SYNC_NET_FOLDERS,
	SYNC_NET_FOLDERS,
	SYNC_NET_FOLDER_SERVER,
	TEST_GROUP_MEMBERSHIP_LDAP_QUERY,
	TEST_KEYSHIELD_CONNECTION,
	TEST_NET_FOLDER_CONNECTION,
	TRACK_BINDER,
	TRASH_PURGE_ALL,
	TRASH_PURGE_SELECTED_ENTITIES,
	TRASH_RESTORE_ALL,
	TRASH_RESTORE_SELECTED_ENTITIES,
	UPDATE_BINDER_TAGS,
	UPDATE_CALCULATED_DATES,
	UPDATE_CALENDAR_EVENT,
	UPDATE_ENTRY_TAGS,
	UPDATE_FAVORITES,
	UNLOCK_ENTRIES,
	UNPIN_ENTRY,
	UNTRACK_BINDER,
	UNTRACK_PERSON,
	UPLOAD_FILE_BLOB,
	VALIDATE_CAPTCHA,
	VALIDATE_EMAIL_ADDRESS,
	VALIDATE_ENTRY_EVENTS,
	VALIDATE_SHARE_LISTS,
	VALIDATE_UPLOADS,
	
	UNDEFINED;

	/**
	 * Converts the ordinal value of a VibeRpcCmdType to its enumeration
	 * equivalent.
	 * 
	 * @param cmdOrdinal
	 * 
	 * @return
	 */
	public static VibeRpcCmdType getEnum(int cmdOrdinal) {
		VibeRpcCmdType cmd;
		try {
			cmd = VibeRpcCmdType.values()[cmdOrdinal];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			cmd = VibeRpcCmdType.UNDEFINED;
		}
		return cmd;
	}
}
