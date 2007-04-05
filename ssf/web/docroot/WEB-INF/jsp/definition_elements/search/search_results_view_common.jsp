<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% // Search results listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<script type="text/javascript" src="<html:rootPath/>js/forum/ss_folder.js"></script>
<%
	String displayStyle2 = ssUser.getDisplayStyle();
	if (displayStyle2 == null) displayStyle2 = "";
	
	String slidingTableStyle2 = "sliding";
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
		slidingTableStyle2 = "sliding_scrolled";
	}
	boolean useAdaptor2 = true;
	if (ssUser.getDisplayStyle() != null && 
	        (ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) ||
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP))) {
		useAdaptor2 = false;
	}
	String ssFolderTableHeight2 = "";
	if (ssUserProperties != null && ssUserProperties.containsKey("folderEntryHeight")) {
		ssFolderTableHeight2 = (String) ssUserProperties.get("folderEntryHeight");
	}
	if (ssFolderTableHeight2 == null || ssFolderTableHeight2.equals("") || 
			ssFolderTableHeight2.equals("0")) ssFolderTableHeight2 = "300";
%>
<script type="text/javascript">
var ss_displayStyle = "<%= displayStyle2 %>";
var ss_saveFolderColumnsUrl = "<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="view_search_results_listing"/><portlet:param 
		name="operation" value="save_folder_columns"/></portlet:actionURL>";
</script>

<% // People, places, things selection bar %>



<div>
  <ul class="ss_actions_bar ss_actions_bar1">
    <li class="ss_search_results_selection_inactive" id="ss_search_results_people_tab"><a href="javascript:;" 
      onClick="ss_showSearchResults('people');return false;"
      ><ssf:nlt tag="search.People"/></a></li>
    <li class="ss_search_results_selection_inactive" id="ss_search_results_places_tab"><a href="javascript:;" 
      onClick="ss_showSearchResults('places');return false;"
      ><ssf:nlt tag="search.Places"/></a></li>
    <li class="ss_search_results_selection_inactive" id="ss_search_results_tags_tab"><a href="javascript:;" 
      onClick="ss_showSearchResults('tags');return false;"
      ><ssf:nlt tag="search.Tags"/></a></li>
    <li class="ss_search_results_selection_active" id="ss_search_results_things_tab"><a href="javascript:;" 
      onClick="ss_showSearchResults('things');return false;"
      ><ssf:nlt tag="search.Things"/></a></li>
  </ul>
</div>

<div class="ss_folder" id="ss_folder_table_parent">

<div id="ss_search_results_things_div" style="display:block;">  
	<c:set var="ss_search_results_display_format" value="nontable"/>
	<c:if test="${ss_search_results_display_format == 'nontable'}">
		<%@ include file="/WEB-INF/jsp/definition_elements/search/search_results_things_non_table.jsp" %>
	</c:if>
</div>

<div id="ss_search_results_people_div" style="display:none;">  
	<%@ include file="/WEB-INF/jsp/definition_elements/search/search_results_people.jsp" %>
</div>

<div id="ss_search_results_places_div" style="display:none;">  
	<%@ include file="/WEB-INF/jsp/definition_elements/search/search_results_places.jsp" %>
</div>

<div id="ss_search_results_tags_div" style="display:none;">  
	<%@ include file="/WEB-INF/jsp/definition_elements/search/search_results_tags.jsp" %>
</div>

<script type="text/javascript">
var ss_currentSearchResultsDiv = null;
var ss_currentSearchResultsTab = null;
function ss_showSearchResultsThings() {
	if(self.ss_setFolderDivHeight) ss_setFolderDivHeight('<%= ssFolderTableHeight2 %>');
	ss_showSearchResults('things');
}
ss_createOnLoadObj('ss_showSearchResultsThings', ss_showSearchResultsThings);
</script>

</div>
