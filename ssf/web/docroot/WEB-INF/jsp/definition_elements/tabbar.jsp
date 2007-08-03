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
<%@ page import="com.sitescape.util.PropertyNotFoundException" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="numTabs" value="0"/>
<ssf:sidebarPanel title="sidebar.history" id="ss_history_box" initOpen="true" sticky="true">
<ul style="padding-top: 2px; padding-left: 5px;">
<c:forEach var="tab" items="${ss_tabs.tablist}">
<jsp:useBean id="tab" type="java.util.HashMap" />
  <c:set var="numTabs" value="${numTabs + 1}"/>
  <c:if test="${numTabs < 6}">
  <li>
		<a id="ss_tabbar_td${tab.tabId}" 
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
		title="${tab.title}" >
<%
	// Truncate long tab titles to 30 characters
	int maxTitle = 30;

	try {
		maxTitle = SPropsUtil.getInt("history.max.title");
	} catch (PropertyNotFoundException e) {
	}

	String tabTitle = (String) tab.get("title");
	if (tabTitle.length() > maxTitle) {
		tabTitle = tabTitle.substring(0, maxTitle) + "...";
	}
%>	
		   <span class="ss_tabs_title"><%= tabTitle %></span></a>
   </li>
   </c:if>
</c:forEach>
</ul>
</ssf:sidebarPanel>
