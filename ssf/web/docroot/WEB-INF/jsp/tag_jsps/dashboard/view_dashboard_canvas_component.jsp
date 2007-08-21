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
<% //View dashboard canvas component %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />
<%
String displayStyle = ssUser.getDisplayStyle();
if (displayStyle == null || displayStyle.equals("")) {
	displayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
}
%>
<c:set var="ss_displayStyle" value="<%= displayStyle %>"/>
<c:set var="hideDashboardControls" value="false"/>
<c:if test="${ssDashboard.dashboard.components[ssComponentId].displayStyle == 'none'}">
  <c:set var="hideDashboardControls" value="true"/>
</c:if>
<!-- Start of component -->
<div id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_component_${ss_component_count}"
  <c:if test="${hideDashboardControls}">
    class="ss_content_window_compact" 
  </c:if>
  <c:if test="${!hideDashboardControls}">
    class="ss_content_window" 
  </c:if>
>
<table width="100%" cellspacing="0" cellpadding="0">

<tr>
<td valign="top" class="ss_dashboard_dragHandle">
  <div class="ss_base_title_bar ss_dashboard_component_dragger"
  <c:if test="${hideDashboardControls}">
	id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_dashboard_control_${ss_dashboard_control_count}"
    style="visibility:hidden; display:none;"
    <c:set var="ss_dashboard_control_count" scope="request" 
       value="${ss_dashboard_control_count + 1}"/>
  </c:if>
  >

 <c:if test="${!empty ssBinder}">
    <form 
      method="post" 
  	  action="<portlet:actionURL windowState="maximized"><portlet:param 
  		name="action" value="modify_dashboard"/><portlet:param 
  		name="binderId" value="${ssBinder.id}"/></portlet:actionURL>">
     <c:set var="myId" value="binderId=${ssBinder.id}"/> 
</c:if>
<c:if test="${empty ssBinder}">
    <form 
      method="post" 
  	  action="<portlet:actionURL windowState="maximized"><portlet:param 
  	  	name="action" value="modify_dashboard_portlet"/><portlet:param 
  	  	name="dashboardId" value="${ssDashboardId}"/></portlet:actionURL>">
     <c:set var="myId" value="dashboardId=${ssDashboardId}"/> 
</c:if>
	  <input type="hidden" name="_dashboardList" value="${ss_dashboard_dashboardList}"/>
	  <input type="hidden" name="_componentId" value="${ss_dashboard_id}"/>
	  <input type="hidden" name="_scope" value="${ss_dashboard_componentScope}"/>
	  <input type="hidden" name="_operation" value=""/>
	  <input type="hidden" name="_returnView" value="${ss_dashboard_returnView}"/>
			<ul class="ss_title_bar_icons">
			  <c:if test="${ss_displayStyle == 'accessible'}">
				<li><a href="javascript:;"
				  onClick="ss_submitDashboardChange(this, '_moveUp');return false;"
				><img border="0" src="<html:imagesPath/>icons/accessory_move_up.gif" 
				  alt="<ssf:nlt tag="button.moveUp"/>" /></a></li>
				<li><a href="javascript:;"
				  onClick="ss_submitDashboardChange(this, '_moveDown');return false;"
				><img border="0" src="<html:imagesPath/>icons/accessory_move_down.gif" 
				  alt="<ssf:nlt tag="button.moveDown"/>" /></a></li>
			  </c:if>
			  
			  <c:if test="${ss_dashboard_componentScope == 'local' || ss_dashboard_componentScope == 'global' || ssDashboard.sharedModificationAllowed}">
				<li><a href="javascript:;" 
				    onClick="ss_modifyDashboardComponent(this, '${ss_dashboard_componentScope}');ss_submitDashboardChange(this, '_modifyComponentData');return false;"
				  ><img border="0" class="ss_accessory_modify" src="<html:imagesPath/>pics/1pix.gif" 
				    title="<ssf:nlt tag="button.modify"/>" /></a></li>
				<li><a href="javascript:;"
				  onClick="ss_modifyDashboardComponent(this, '${ss_dashboard_componentScope}'); ss_confirmDeleteComponent(this, '${ss_dashboard_id}', '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_component_${ss_component_count}', '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_component2_${ss_component_count}', '${myId}', '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>'); return false;"
				><img border="0" class="ss_accessory_delete" src="<html:imagesPath/>pics/1pix.gif" 
				  title="<ssf:nlt tag="button.delete"/>" /></a></li>
			  </c:if>
			  <c:if test="${ss_dashboard_visible}">
				<li><a href="javascript:;"
				  onClick="ss_showHideDashboardComponent(this, '${ss_dashboard_id}', '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_dashboard_component_${ss_component_count}', '${myId}', '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>');return false;"
				><img border="0" src="<html:imagesPath/>icons/accessory_hide.gif" 
				  title="<ssf:nlt tag="button.hide"/>" /></a></li>
			  </c:if>
			  <c:if test="${!ss_dashboard_visible}">
				<li><a href="javascript:;"
				  onClick="ss_showHideDashboardComponent(this, '${ss_dashboard_id}', '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_dashboard_component_${ss_component_count}', '${myId}', '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>');return false;"
				><img border="0" src="<html:imagesPath/>icons/accessory_show.gif" 
				  title="<ssf:nlt tag="button.show"/>" /></a></li>
			  </c:if>
			</ul>
	</form>
	<strong>${ssDashboard.dashboard.components[ssComponentId].title}&nbsp;</strong>
  </div>
</td>
</tr>
<tr>
<td>
	<div 
      <c:if test="${hideDashboardControls}">
	    id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_dashboard_border_${ss_dashboard_border_count}"
	      <c:set var="ss_dashboard_border_count" scope="request" 
	         value="${ss_dashboard_border_count + 1}"/>
      </c:if>
      <c:set var="accessoryStyle" value="ss_content_window_content_off" />
      <c:if test="${!hideDashboardControls && ss_dashboard_visible}">
        <c:set var="accessoryStyle" value="ss_content_window_content" />
      </c:if>
      class="${accessoryStyle}">
			<ssf:dashboard id="${ss_dashboard_id}"
			   type="viewData" configuration="${ssDashboard}"
			   initOnly="true" />
<% // this is the div that will be replaced, don't remove setup from init phase %>
		<div id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_dashboard_component_${ss_component_count}" 
		    align="left" style="margin:0px; 
		    <c:if test="${!ss_dashboard_visible}">
		      visibility:hidden; display:none;
		    </c:if>
		    padding:2px;">
		<c:set var="ss_component_count" value="${ss_component_count + 1}" scope="request"/>
			<c:if test="${ss_dashboard_visible}">
			  <ssf:dashboard id="${ss_dashboard_id}"
			     type="viewData" configuration="${ssDashboard}"/>
			</c:if>
    	</div>
	</div>
</td>
</tr>
</table>
</div>
<div style="margin:3px; padding:0px;"><img <ssf:alt/> border="0"
  src="<html:imagesPath/>pics/1pix.gif"></div>
<!-- End of component -->

