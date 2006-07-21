package com.sitescape.ef.web;

public class WebKeys {
	// Attribute names reserved by portlet specification
	public static final String JAVAX_PORTLET_CONFIG = "javax.portlet.config";
	public static final String JAVAX_PORTLET_REQUEST = "javax.portlet.request";
	public static final String JAVAX_PORTLET_RESPONSE = "javax.portlet.response";

	// Calendar view stuff
	public static final String CALENDAR_EVENTDATES = "ssEventDates";
	public static final String CALENDAR_VIEWMODE = "ssCalendarViewMode";
	public static final String CALENDAR_VIEW_DAY = "day";
	public static final String CALENDAR_VIEW_WEEK = "week";
	public static final String CALENDAR_VIEW_MONTH = "month";
	public static final String CALENDAR_CURRENT_DATE = "ssCurrentDate";
	public static final String CALENDAR_CURRENT_VIEW_STARTDATE = "ssCalStartDate";
	public static final String CALENDAR_CURRENT_VIEW_ENDDATE = "ssCalEndDate";
	public static final String CALENDAR_DOW = "cal_dow";
	public static final String CALENDAR_DOM = "cal_dom";
	public static final String CALENDAR_ENTRYTITLE = "cal_entrytitle";
	public static final String CALENDAR_STARTTIMESTRING = "cal_starttimestring";
	public static final String CALENDAR_ENDTIMESTRING = "cal_endtimestring";
	public static final String CALENDAR_VIEWBEAN = "ssCalendarViewBean";	
	public static final String CALENDAR_EVENTDATAMAP = "cal_eventdatamap";	
	public static final String CALENDAR_URL_VIEWMODE = "cal_url_viewmode";	
	public static final String CALENDAR_URL_NEWVIEWDATE = "cal_url_newviewdate";	
	
	// miscellaneous
	public static final String DEFINITION_DEFAULT_FORM_NAME = "entryForm";
    public static final String LOCALE = "ss_locale";
    public static final String SESSION_LAST_ENTRY_VIEWED = "last_entry_viewed";
    public static final String EVENT = "event";
    public static final String PRINCIPAL = "_principal";
    
    //URL parameters
    public static final String IS_ACTION_URL="actionUrl";
    public static final String URL_AJAX = "ajax";
    public static final String URL_ATTRIBUTE = "attr";
    public static final String URL_ATTRIBUTE_ID = "attrId";
    public static final String URL_BINDER_ID="binderId";
    public static final String URL_BINDER_TYPE="binderType";
    public static final String URL_DEBUG="enableDebug";
    public static final String URL_DOWNLOAD_FILE="download";
	public static final String URL_ENTRY_ID = "entryId";
    public static final String URL_ENTRY_TYPE="entryType";
	public static final String URL_FILE = "file";
	public static final String URL_FILE_ID = "fileId";
	public static final String URL_FILE_VIEW_TYPE = "viewType";
    public static final String URL_OPERATION="operation";
    public static final String URL_OPERATION2="operation2";
	public static final String URL_RANDOM = "random";
	public static final String URL_VALUE = "value";

    //Preferences
	public static final String FORUM_PREF_FORUM_ID_LIST = "forumIdList";
	public static final String PRESENCE_PREF_USER_LIST = "com.sitescape.presence.user.list";
	public static final String PRESENCE_PREF_GROUP_LIST = "com.sitescape.presence.group.list";
	
    //actions
    public static final String ADMIN_ACTION_CONFIGURE_ROLES = "configure_roles";
    public static final String ACTION_ADD_FOLDER_ENTRY = "add_folder_entry";
    public static final String ACTION_ADD_BINDER = "add_binder";
    public static final String ACTION_MODIFY_BINDER = "modify_binder";
 	public static final String ACTION_ADD_FOLDER_REPLY = "add_folder_reply";
	public static final String ACTION_ACCESS_CONTROL = "configure_access_control";
	public static final String ACTION_CONFIGURE_FORUM = "configure_forum";
	public static final String ACTION_DEFINITION_BUILDER = "definition_builder";
	public static final String ACTION_DEFINITION_BUILDER_DEFINITION_TYPE = "definition_type";
	public static final String ACTION_MODIFY_FOLDER_ENTRY = "modify_folder_entry";
	public static final String ACTION_MODIFY_PROFILE_ENTRY = "modify_profile_entry";
	public static final String ACTION_VIEW_PROFILE_LISTING = "view_profile_listing";
	public static final String ACTION_VIEW_FOLDER_LISTING = "view_folder_listing";
	public static final String ACTION_VIEW_WS_LISTING = "view_ws_listing";
	public static final String ACTION_VIEW_FOLDER_ENTRY = "view_folder_entry";
	public static final String ACTION_VIEW_PROFILE_ENTRY = "view_profile_entry";
	public static final String FORUM_AJAX_REQUEST = "__ajax_request";
	public static final String LDAP_ACTION_CONFIGURE="configure_ldap";
	public static final String NOTIFY_ACTION_CONFIGURE="configure_notify";
	public static final String POSTING_ACTION_CONFIGURE="configure_posting";
	public static final String POSTINGJOB_ACTION_CONFIGURE="configure_posting_job";
	public static final String FOLDER_INDEX_ACTION_CONFIGURE="configure_folder_index";
	public static final String PROFILE_INDEX_ACTION_CONFIGURE="configure_profile_index";
	public static final String DEFINITION_ACTION_IMPORT="import_definition";
	//operations
 	public static final String FORUM_OPERATION_ADD_FAVORITE_BINDER = "add_favorite_binder";
 	public static final String FORUM_OPERATION_ADD_FAVORITES_CATEGORY = "add_favorites_category";
 	public static final String FORUM_OPERATION_SAVE_FAVORITES = "save_favorites";
 	public static final String FORUM_OPERATION_ADMINISTRATION = "administration";
    public static final String OPERATION_ADD_FOLDER = "add_folder";
    public static final String OPERATION_ADD_SUB_FOLDER = "add_subFolder";
    public static final String OPERATION_ADD_WORKSPACE = "add_workspace";
    public static final String OPERATION_DELETE="delete";
    public static final String OPERATION_MODIFY="modify";
    public static final String OPERATION_MOVE="move";
       
    public static final String FORUM_OPERATION_DASHBOARD_DELETE_COMPONENT="delete_component";
    public static final String FORUM_OPERATION_DASHBOARD_HIDE_COMPONENT="hide_component";
    public static final String FORUM_OPERATION_DASHBOARD_SHOW_COMPONENT="show_component";
	public static final String FORUM_OPERATION_GET_CONDITION_ENTRY_ELEMENTS = "get_condition_entry_elements";
	public static final String FORUM_OPERATION_GET_CONDITION_ENTRY_OPERATIONS = "get_condition_entry_element_operations";
	public static final String FORUM_OPERATION_GET_CONDITION_ENTRY_VALUE_LIST = "get_condition_entry_element_values";
	public static final String FORUM_OPERATION_GET_ENTRY_ELEMENTS = "get_entry_elements";
	public static final String FORUM_OPERATION_GET_ELEMENT_VALUES = "get_element_values";
	public static final String FORUM_OPERATION_GET_ELEMENT_VALUE_DATA = "get_element_value_data";
	public static final String FORUM_OPERATION_GET_FAVORITES_TREE = "get_favorites_tree";
	public static final String FORUM_OPERATION_GET_FILTER_TYPE = "get_filter_type";
	public static final String FORUM_OPERATION_SET_DASHBOARD_TITLE = "set_dashboard_title";
	public static final String FORUM_OPERATION_SET_DISPLAY_STYLE = "set_display_style";
	public static final String FORUM_OPERATION_SET_DISPLAY_DEFINITION = "set_display_definition";
	public static final String FORUM_OPERATION_VIEW_ENTRY = "view_entry";
	public static final String FORUM_OPERATION_VIEW_FILE = "view_file";
	public static final String FORUM_OPERATION_SET_CALENDAR_DISPLAY_MODE = "set_cal_display_mode";
	public static final String FORUM_OPERATION_SET_CALENDAR_DISPLAY_DATE = "set_cal_display_date";
	public static final String FORUM_OPERATION_CALENDAR_GOTO_DATE = "cal_goto_date";
	public static final String FORUM_OPERATION_UNSEEN_COUNTS = "unseen_counts";
	public static final String FORUM_OPERATION_RELOAD_LISTING = "reload_listing";
	public static final String FORUM_OPERATION_SAVE_COLUMN_POSITIONS = "save_column_positions";
	public static final String FORUM_OPERATION_SAVE_ENTRY_WIDTH = "save_entry_width";
	public static final String FORUM_OPERATION_SAVE_ENTRY_HEIGHT = "save_entry_height";
	public static final String FORUM_OPERATION_GET_SEARCH_FORM_FILTER_TYPE = "get_search_form_filter_type";
	public static final String FORUM_OPERATION_GET_SEARCH_FORM_ENTRY_ELEMENTS = "get_searchForm_entry_elements";
	public static final String FORUM_OPERATION_GET_SEARCH_FORM_ELEMENT_VALUES = "get_searchForm_element_values";
	public static final String FORUM_OPERATION_GET_SEARCH_FORM_ELEMENT_VALUE_DATA = "get_searchForm_element_value_data";
	public static final String FORUM_OPERATION_SELECT_FILTER = "select_filter";
	public static final String FORUM_OPERATION_USER_FILTER = "user_filter";
	public static final String FORUM_OPERATION_UNSEEN_LIST = "unseen_list";
	public static final String FORUM_OPERATION_USER_LIST_SEARCH = "user_list_search";
	public static final String FORUM_OPERATION_WORKSPACE_TREE = "workspace_tree";
	public static final String OPERATION_POSTING_ALIASES="alias";
	public static final String OPERATION_ADD_POSTING_ALIASES="addAlias";
	public static final String OPERATION_MODIFY_POSTING_ALIASES="modifyAlias";
	
	// MODEL TAGS & Attributes
	public static final String ACTION = "action";
	public static final String ADMIN_TREE="ssAdminDomTree";
	public static final String BINDER="ssBinder";
	public static final String BINDER_ID="ssBinderId";
	public static final String BINDER_DEFINITION_TYPE="ssBinderDefinitionType";
	public static final String BINDER_ENTRIES="ssBinderEntries";
	public static final String BLOG_ENTRIES="ssBlogEntries"; 
	public static final String COMMUNITY_TAGS="ssCommunityTags";
	public static final String CONDITION_ENTRY_DEF_ID="conditionDefinitionId";
	public static final String CONDITION_ELEMENT_NAME="conditionElementName";
	public static final String CONDITION_ELEMENT_OPERATION="conditionElementOperation";
	public static final String CONDITION_ELEMENT_VALUE="conditionElementValue";
	public static final String CONFIG_ELEMENT="ssConfigElement";
    public static final String CONFIG_DEFINITION="ssConfigDefinition";
    public static final String CONFIG_JSP_STYLE="ssConfigJspStyle";
    public static final String DASHBOARD="ssDashboard";
    public static final String DASHBOARD_BEAN_MAP="beans";
    public static final String DASHBOARD_COMPONENT_ID="ssComponentId";
    public static final String DASHBOARD_COMPONENT_LIST_WIDE_TOP="wide_top";
    public static final String DASHBOARD_COMPONENT_LIST_NARROW_FIXED="narrow_fixed";
    public static final String DASHBOARD_COMPONENT_LIST_NARROW_VARIABLE="narrow_variable";
    public static final String DASHBOARD_COMPONENT_LIST_WIDE_BOTTOM="wide_bottom";
    public static final String DASHBOARD_COMPONENT_TITLES="component_titles";
    public static final String DASHBOARD_COMPONENTS_NARROW_FIXED="components_narrow_fixed";
    public static final String DASHBOARD_COMPONENTS_NARROW_VARIABLE="components_narrow_variable";
    public static final String DASHBOARD_COMPONENTS_WIDE="components_wide";
    public static final String DASHBOARD_LIST="dashboardList";
    public static final String DASHBOARD_MAP="dashboard";
    public static final String DASHBOARD_RETURN_VIEW="returnView";
    public static final String DASHBOARD_BINDER_MAP="dashboard_binder";
    public static final String DASHBOARD_GLOBAL_MAP="dashboard_global";
    public static final String DASHBOARD_LOCAL_MAP="dashboard_local";
    public static final String DASHBOARD_ID="ssDashboardId";
    public static final String DASHBOARD_INCLUDE_BINDER_TITLE="includeBinderTitle";
    public static final String DASHBOARD_NARROW_FIXED_WIDTH="narrowFixedWidth";
    public static final String DASHBOARD_NARROW_FIXED_WIDTH2="narrowFixedWidth2";
    public static final String DASHBOARD_SHARED_MODIFICATION_ALLOWED="sharedModificationAllowed";
    public static final String DASHBOARD_SCOPE="scope";
    public static final String DASHBOARD_TITLE="title";
    public static final String DASHBOARD_WORKSPACE_TREE="workspaceTree";
    public static final String DASHBOARD_WORKSPACE_TOPID="topId";
    public static final String DEBUG_ON="on";
    public static final String DEBUG_OFF="off";
    public static final String DEFAULT_FOLDER_DEFINITION="ssDefaultFolderDefinition";
    public static final String DEFAULT_FOLDER_DEFINITION_ID="ssDefaultFolderDefinitionId";
    public static final String DEFINITION="ssDefinition";
    public static final String DEFINITION_ID="ssDefinitionId";
    public static final String DEFINITION_BINDER="ssDefinitionBinder";
    public static final String DEFINITION_ENTRY="ssDefinitionEntry";
    public static final String EMAIL_ALIASES="ssEmailAliases";
    public static final String ENTRY="ssEntry";
    public static final String ENTRIES="ssEntries";
    public static final String ENTRY_DEFINITION="ssEntryDefinition";
    public static final String ENTRY_DEFINTION_MAP="ssEntryDefinitionMap";
    public static final String ENTRY_DEFINTION_ELEMENT_DATA="ssEntryDefinitionElementData";
    public static final String ENTRY_ID = "ssEntryId";
    public static final String FAVORITES_TREE = "ss_favoritesTree";
    public static final String FAVORITES_TREE_DELETE = "ss_favoritesTreeDelete";
    public static final String FILE_VIEW_TYPE_SCALED = "scaled";
    public static final String FILE_VIEW_TYPE_THUMBNAIL = "thumbnail";
    public static final String FILTER_ENTRY_DEF_ID = "ss_entry_def_id";
    public static final String FILTER_ENTRY_ELEMENT_NAME = "ss_filter_entry_element_name";
    public static final String FILTER_ENTRY_FILTER_TERM_NUMBER="ss_filterTermNumber";
    public static final String FILTER_ENTRY_FILTER_TERM_NUMBER_MAX="ss_filterTermNumberMax";
    public static final String FILTER_SEARCH_FILTERS="ss_searchFilters";
    public static final String FILTER_SEARCH_FILTER_DATA="ss_searchFilterData";
    public static final String FILTER_SELECTED_FILTER_NAME="ss_selectedFilter";
    public static final String FILTER_TYPE="ss_filterType";
    public static final String FILTER_VALUE_TYPE="ss_filterValueType";
    public static final String FOLDER = "ssFolder";
    public static final String FOLDER_COLUMN_POSITIONS="folderColumnPositions";
    public static final String FOLDER_DEFINTION_MAP="ssFolderDefinitionMap";
    public static final String FOLDER_ENTRY_WIDTH="folderEntryWidth";
    public static final String FOLDER_ENTRY_HEIGHT="folderEntryHeight";
    public static final String FOLDER_LIST = "ssFolderList";
    public static final String FOLDER_ID_LIST = "ssFolderIdList";
    public static final String FOLDERS = "ssFolders";
    public static final String FOLDER_DOM_TREE="ssFolderDomTree";
    public static final String FOLDER_ENTRIES="ssFolderEntries";
    public static final String FOLDER_ENTRY_DESCENDANTS="ssFolderEntryDescendants";
    public static final String FOLDER_ENTRY_ANCESTORS="ssFolderEntryAncestors";
    public static final String FOLDER_ENTRY_TOOLBAR="ssFolderEntryToolbar";
    public static final String FOLDER_TOOLBAR="ssFolderToolbar";
    public static final String FORUM_ID_LIST = "ssForumIdList";
    public static final String FORUM_TOOLBAR="ssForumToolbar";
    public static final String FUNCTIONS="ssFunctions";
    public static final String FUNCTION_MAP="ssFunctionMap";
    public static final String FUNCTION_MEMBERSHIP="ssFunctionMemberships";
    public static final String GROUPS="ssGroups";
    public static final String IS_REFRESH="ssRefresh";
    public static final String LDAP_CONFIG="ssLdapConfig";
    public static final String LIST_UNSEEN_COUNTS="ss_unseenCounts";
    public static final String AJAX_STATUS="ss_ajaxStatus";
    public static final String AJAX_STATUS_NOT_LOGGED_IN="ss_ajaxNotLoggedIn";
    public static final String NOTIFICATION="ssNotification";
    public static final String PERSONAL_TAGS="ssPersonalTags";
    public static final String POSTINGS="ssPostings";
    public static final String PRESENCE_COMPONENT_ID="ss_presence_component_id";
    public static final String PRESENCE_DUDE="ss_presence_dude";
    public static final String PRESENCE_STATUS="ss_presence_userStatus";
    public static final String PRESENCE_SWEEP_TIME="ss_presence_sweep_time";
    public static final String PRESENCE_TEXT="ss_presence_text";
    public static final String PRESENCE_USER="ss_presence_user";
    public static final String PRESENCE_ZON_BRIDGE="ss_presence_zonBridge";
    public static final String PUBLIC_DEFINITIONS="ssPublicDefinitions";
    public static final String PUBLIC_BINDER_DEFINITIONS="ssPublicBinderDefinitions";
    public static final String PUBLIC_BINDER_ENTRY_DEFINITIONS="ssPublicBinderEntryDefinitions";
    public static final String PUBLIC_ENTRY_DEFINITIONS="ssPublicEntryDefinitions";
    public static final String PUBLIC_FOLDER_DEFINITIONS="ssPublicFolderDefinitions";
    public static final String PUBLIC_FILE_FOLDER_DEFINITIONS="ssPublicFileFolderDefinitions";
    public static final String PUBLIC_PROFILE_DEFINITIONS="ssPublicProfileDefinitions";
    public static final String PUBLIC_PROFILE_ENTRY_DEFINITIONS="ssPublicProfileEntryDefinitions";
    public static final String PUBLIC_WORKFLOW_DEFINITIONS="ssPublicWorkflowDefinitions";
    public static final String PUBLIC_WORKSPACE_DEFINITIONS="ssPublicWorkspaceDefinitions";
    public static final String RELOAD_URL="ss_reloadUrl";
    public static final String SCHEDULE="ssSchedule";
    public static final String SCHEDULE_INFO="ssScheduleInfo";
    public static final String SEARCH_FORM_DATA="ssSearchFormData";
    public static final String SEARCH_FORM_ELEMENT="ssSearchFormElement";
    public static final String SEARCH_FORM_FORM="ssSearchFormForm";
    public static final String SEARCH_FORM_ENTRY_DEF_ID = "ss_entry_def_id";
    public static final String SEARCH_FORM_QUERY_DATA="searchQueryData";
    public static final String SEARCH_FORM_RESULTS="searchResults";
    public static final String SEARCH_FORM_SEARCH_FILTERS="ss_searchSearchForms";
    public static final String SEARCH_FORM_SEARCH_FILTER_DATA="ss_searchSearchFormData";
    public static final String SEARCH_FORM_SELECTED_FILTER_NAME="ss_selectedSearchForm";
    public static final String SEARCH_SEARCH_SORT_BY="sortBy";
    public static final String SEEN_MAP="ssSeenMap";
    public static final String SELECTED_GROUPS="ssSelectedGroups";
    public static final String SELECTED_PRINCIPALS="ssSelectedPrincipals";
    public static final String SELECTED_USERS="ssSelectedUsers";
    public static final String TOOLBAR="ssToolbar";
    public static final String UNAUTHENTICATED_REQUEST = "com.sitescape.unauthenticated.request";
    public static final String USERS="ssUsers";
    public static final String USER_IDS_TO_SKIP="ssUserIdsToSkip";
	public static final String USER_NAME = "com.sitescape.username";
	public static final String USER_PRINCIPAL = "ssUser";
	public static final String USER_ID = "com.sitescape.userId";
    public static final String USER_PROPERTIES="ssUserProperties";
    public static final String USER_FOLDER_PROPERTIES="ssUserFolderProperties";
    public static final String USER_SEARCH_USER_GROUP_TYPE="ss_userGroupType";
    public static final String USER_SEARCH_USER_GROUP_TYPE_GROUP="group";
    public static final String USER_SEARCH_USER_GROUP_TYPE_USER="user";
    public static final String WORKFLOW_CAPTIONS="ssWorkflowCaptions";
    public static final String WORKFLOW_QUESTIONS="ssWorkflowQuestions";
    public static final String WORKFLOW_QUESTION_TEXT="ssWorkflowQuestionText";
    public static final String WORKFLOW_QUESTION_RESPONSES="ssWorkflowQuestionResponses";
    public static final String WORKFLOW_TRANSITIONS="ssWorkflowTransitions";
    public static final String WORKSPACE="ssWorkspace";
    public static final String WORKSPACE_DOM_TREE="ssWsDomTree";
    public static final String WORKSPACE_DOM_TREE_BINDER_ID="ssWsDomTreeBinderId";
    public static final String WORKSPACES="ssWorkspaces";
    public static final String ZONE_NAME = "com.sitescape.zonename";

    //View names
    public static final String VIEW_ACCESS_CONTROL = "binder/access_control";
    public static final String VIEW_ADD_ENTRY = "entry/add_entry";
    public static final String VIEW_ADD_BINDER = "forum/add_binder";
    public static final String VIEW_ADD_BINDER_TYPE = "forum/add_binder_type";
    public static final String VIEW_ADD_BINDER_DEFINITION = "forum/add_binder_definition";
    public static final String VIEW_ADD_BINDER_DEFINITION_AJAX="forum/add_binder_definition_ajax";
    public static final String VIEW_ADMIN_CONFIGURE_LDAP="administration/configureLdap";
    public static final String VIEW_ADMIN_CONFIGURE_NOTIFICATION="administration/configureNotify";
    public static final String VIEW_ADMIN_CONFIGURE_POSTING="administration/configurePosting";
    public static final String VIEW_ADMIN_CONFIGURE_POSTING_JOB="administration/configurePostingJob";
    public static final String VIEW_ADMIN_CONFIGURE_SEARCH_INDEX="administration/configureSearchIndex";
    public static final String VIEW_ADMIN_IMPORT_DEFINITIONS ="administration/importDefinitions"; 
    public static final String VIEW_ADMIN_REDIRECT="administration/view_admin_redirect";
    public static final String VIEW_BUILD_FILTER = "binder/build_filter";
    public static final String VIEW_BUILD_FILTER_SELECT = "binder/build_filter_select";
    public static final String VIEW_CONFIGURE = "binder/configure";
    public static final String VIEW_DEFINITION="definition_builder/view_definition_builder";
    public static final String VIEW_DEFINITION_OPTION="definition_builder/view_definition_builder_option";
    public static final String VIEW_DEFINITION_XML="definition_builder/view_definition_xml";
    public static final String VIEW_DELETE_ENTRY="entry/delete_entry";
    public static final String VIEW_EDIT="forum/edit";
    public static final String VIEW_ENTRY="entry/view_entry";
    public static final String VIEW_FORUM="forum/view";
    public static final String VIEW_LISTING="entry/view_listing";
    public static final String VIEW_LISTING_IFRAME="entry/view_listing_iframe";
    public static final String VIEW_LISTING_POPUP="entry/view_listing_popup";
    public static final String VIEW_LISTING_ACCESSIBLE="entry/view_listing_accessible";
    public static final String VIEW_LISTING_VERTICAL="entry/view_listing_vertical";
    public static final String VIEW_MODIFY_ENTRY="entry/modify_entry";
    public static final String VIEW_MOVE_ENTRY="entry/move_entry";
    public static final String VIEW_MODIFY_BINDER="forum/modify_binder";
    public static final String VIEW_MOVE_BINDER="binder/move_binder";
    public static final String VIEW_NO_DEFINITION="forum/view_default";
    public static final String VIEW_NO_ENTRY="entry/view_no_entry";
    public static final String VIEW_PRESENCE="presence/view";
    public static final String VIEW_PRESENCE_AJAX="presence/view_ajax";
    public static final String VIEW_PRESENCE_EDIT="presence/edit";
    public static final String VIEW_PROFILE="profile/view";
    public static final String VIEW_WORKSPACE="forum/view_workspace";
    //servlet paths
    public static final String SERVLET_VIEW_FILE="viewFile";
    public static final String SERVLET_VIEW_CSS="viewCss";
    
}
