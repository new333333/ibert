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
<% // Tabs %>
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ page import="com.sitescape.team.util.SPropsUtil" %>
<%@ page import="com.sitescape.team.util.ConfigPropertyNotFoundException" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="numTabs" value="0"/>
<c:forEach var="tab" items="${ss_tabs.tablist}">
  <c:set var="numTabs" value="${numTabs + 1}"/>
</c:forEach>

<ssf:skipLink tag="<%= NLT.get("skip.tabbar") %>" id="tabbar_${renderResponse.namespace}">

<div id="ss_tabbar" class="ss_tabs">
<!-- Start of tabs -->
  <ssHelpSpot helpId="tools/tabs" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.tabs"/>"></ssHelpSpot>

<div id="ss_tabs_variables" style="display:none;">
<script type="text/javascript">
var ss_currentTab = "${ss_tabs.current_tab}";
var ss_nextTabNumber = "${ss_tabs.next_tab_id}";
</script>
</div>
<script type="text/javascript">
var ss_tabs_delete_icon = "<html:imagesPath/>tabs/tab_delete.gif";
var ss_tabs_no_delete_last_tab = "<ssf:nlt tag="tabs.noDeleteLastTab"/>";
</script>
<table cellspacing="0" cellpadding="0" style="background:transparent;">
<tbody>
<tr id="ss_tabbar_tr">
<c:forEach var="tab" items="${ss_tabs.tablist}">
<jsp:useBean id="tab" type="java.util.HashMap" />
  <c:set var="active" value=""/>
  <c:if test="${ss_tabs.current_tab == tab.tabId}">
    <c:set var="active" value="_active"/>
  </c:if>
	<td valign="bottom">
	  <table cellspacing="0" cellpadding="0" style="background:transparent;">
	  <tbody>
	  <tr>
	  <td valign="middle" class="ss_tabs_td_left${active}"><img border="0" <ssf:alt/>
	    src="<html:imagesPath/>pics/1pix.gif" class="ss_tabs_corner_l"/></td>
	  <td valign="middle" class="ss_tabs_td${active}" nowrap="true">
		<a id="ss_tabbar_td${tab.tabId}" style="position:relative; left:-0px;" 
		  <c:if test="${tab.type == 'binder'}">
		    href="<ssf:url 
  				folderId="${tab.binderId}" 
  				action="view_folder_listing">
  				<ssf:param name="binderId" value="${tab.binderId}"/>
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'workspace'}">
		    href="<ssf:url 
  				folderId="${tab.binderId}" 
  				action="view_ws_listing">
  				<ssf:param name="binderId" value="${tab.binderId}"/>
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'entry'}">
		    href="<ssf:url 
  				folderId="${tab.binderId}" 
  				entryId="${tab.entryId}" 
  				action="view_folder_entry">
   				<ssf:param name="entryId" value="${tab.entryId}"/>
  				<ssf:param name="binderId" value="${tab.binderId}"/>
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'user'}">
		    href="<ssf:url 
  				folderId="${tab.binderId}" 
  				entryId="${tab.entryId}" 
  				action="view_profile_entry">
   				<ssf:param name="entryId" value="${tab.entryId}"/>
   				<ssf:param name="binderId" value="${tab.binderId}"/>
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'profiles'}">
		    href="<ssf:url 
  				folderId="${tab.binderId}" 
  				action="view_profile_listing">
  				<ssf:param name="binderId" value="${tab.binderId}"/>
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'query'}">
		    href="<ssf:url 
  				action="advanced_search">
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				<ssf:param name="operation" value="viewPage"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'search'}">
		    href="<ssf:url 
  				action="advanced_search">
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				<ssf:param name="operation" value="searchForm"/>  				
  				</ssf:url>" 
		  </c:if>
		>
	<c:if test="${!empty tab.icon}">
		   <img border="0" class="ss_tabs_icon" <ssf:alt/> src="<html:imagesPath/>${tab.icon}"/>
	</c:if>
<%
	// Truncate long tab titles to 20 characters
	int maxTitle = 20;

	try {
		maxTitle = SPropsUtil.getInt("tabs.max.title");
	} catch (ConfigPropertyNotFoundException e) {
	}

	String tabTitle = (String) tab.get("title");
	if (tabTitle.length() > maxTitle) {
		tabTitle = tabTitle.substring(0, maxTitle) + "...";
	}
%>	
		   <span class="ss_tabs_title"><%= tabTitle %></span></a>
	<c:if test="${numTabs > 1}">
		<a href="#" onClick="ss_deleteTab(this, '${tab.tabId}');return false;">
		  <img border="0" class="ss_tabs_close"
		    <ssf:alt tag="alt.deleteTab"/> src="<html:imagesPath/>pics/1pix.gif"/>
		</a>
	</c:if>
	  </td>
	  <td valign="middle" class="ss_tabs_td_right${active}"><img border="0"
	    <ssf:alt/> src="<html:imagesPath/>pics/1pix.gif" class="ss_tabs_corner_r"/></td>
	  </tr>
	  </tbody>
	  </table>
	</td>

</c:forEach>

</tr>
</tbody>
</table>

<!-- End of tabs -->
</div>

</ssf:skipLink>