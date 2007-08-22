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
<% // Task view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />

<script type="text/javascript" src="<html:rootPath/>js/common/ss_tasks.js"></script>
<script type="text/javascript">
var ss_saveSubscriptionUrl = "<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="${action}"/><portlet:param 
		name="binderId" value="${ssBinder.id}"/><portlet:param 
		name="operation" value="subscribe"/></portlet:actionURL>";
var ss_noEntryTitleLabel = "<ssf:nlt tag="entry.noTitle" />";
</script>

<table class="ss_statisticTable"><tr>
<c:if test="${!empty ssBinder &&
				!empty ssBinder.customAttributes['statistics'] && 
				!empty ssBinder.customAttributes['statistics'].value && 
				!empty ssBinder.customAttributes['statistics'].value.value}">		
	<c:forEach var="definition" items="${ssBinder.customAttributes['statistics'].value.value}">
		<c:if test="${!empty definition.value}">
			<c:if test="${!empty definition.value.priority}">
				<td><ssf:drawStatistic statistic="${definition.value.priority}" style="coloredBar" showLabel="true" showLegend="true" labelAll="true"/></td>
			</c:if>

			<c:if test="${!empty definition.value.status}">
				<td><ssf:drawStatistic statistic="${definition.value.status}" style="coloredBar ss_statusBar" showLabel="true" showLegend="true" labelAll="true"/></td>
			</c:if>
		</c:if>
	</c:forEach>
</c:if>
</tr></table>


<div class="ss_folder_border">
<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">
<% // Entry toolbar %>
<c:if test="${!empty ssEntryToolbar}">
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />
</c:if>
</ssf:toolbar>
</div>
<%@ include file="/WEB-INF/jsp/forum/page_navigation_bar.jsp" %>
<div class="ss_folder" id="ss_task_folder_div">
<%@ include file="/WEB-INF/jsp/definition_elements/description_view.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/task/task_nav_bar.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/task/task_folder_listing.jsp" %>
</div>
