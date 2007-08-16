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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${empty ss_portletInitialization}">
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
</c:if>

<c:set var="folderIdList" value=""/>
<jsp:useBean id="folderIdList" type="java.lang.String" />

<script type="text/javascript">
var count = 0
function <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_getUnseenCounts() {
	ss_setupStatusMessageDiv()
	<c:forEach var="binder" items="${ssFolderList}">
	  <c:if test="${binder.entityIdentifier.entityType == 'folder'}">
		document.getElementById("<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_count_<c:out value="${binder.id}"/>").style.color = "silver";
	  </c:if>
	</c:forEach>
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="unseen_counts" />
    	</ssf:url>"
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_unseenCountForm")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preRequest);
	ajaxRequest.setPostRequest(<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_getUnseenCountsReturn);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_getUnseenCountsReturn() {
	// alert('postRequest: ' + obj.getXMLHttpRequestObject().responseText);
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		if (obj.getData('timeout') != "timeout") {
			//This call wasn't made from a timeout. So, give error message
			ss_showNotLoggedInMsg();
		}
	}
}


</script>

<div class="ss_portlet_style ss_portlet">

<c:if test="${ss_windowState == 'maximized'}">
<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />
</c:if>
<div class="ss_style" style="padding:4px;">

<ssHelpSpot helpId="portlets/folder_bookmarks_portlet" offsetX="0" offsetY="-10" 
			    title="<ssf:nlt tag="helpSpot.folderBookmarksPortlet"/>"></ssHelpSpot>

<c:if test="${empty ssFolderList}">
<div align="right">
  <a class="ss_linkButton" 
    href="<portlet:renderURL 
      portletMode="edit" 
      windowState="maximized" />"
    ><ssf:nlt tag="portlet.forum.configure"/></a>
</div>
<div>
  <ssf:nlt tag="portlet.bookmarksNotConfigured"/>
</div>
</c:if>

<c:if test="${!empty ssFolderList}">
<div style="padding:5px 0px;">
  <a class="ss_linkButton ss_bold ss_smallprint" 
    href="javascript: ;" onClick="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_getUnseenCounts();return false;"
    ><ssf:nlt tag="portlet.showUnread"/></a>
</div>
</c:if>

<c:if test="${!empty ssFolderList}">
<table cellspacing="0" cellpadding="0">
<c:forEach var="binder" items="${ssFolderList}">
<jsp:useBean id="binder" type="com.sitescape.team.domain.Binder" />
  <tr>
  <td>
      <span id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_count_<c:out value="${binder.id}"/>"><font color="silver">-</font></span>
  </td>
  <td>&nbsp;&nbsp;&nbsp;</td>
  <td>
	<c:if test="${binder.entityIdentifier.entityType == 'folder'}">
	  <a href="<portlet:renderURL windowState="maximized"><portlet:param 
	  	name="action" value="view_folder_listing"/><portlet:param 
	  	name="binderId" value="${binder.id}"/></portlet:renderURL>"><span>${binder.title}</span></a>
	  <c:if test="${binder.parentBinder.entityIdentifier.entityType == 'folder'}">
	    <a style="padding-left:20px;" 
	    	href="<portlet:renderURL windowState="maximized"><portlet:param 
			name="action" value="view_folder_listing"/><portlet:param 
			name="binderId" value="${binder.parentBinder.id}"/></portlet:renderURL>">
			<span class="ss_smallprint ss_light">(${binder.parentBinder.title})</span></a>
	  </c:if>
	  <c:if test="${binder.parentBinder.entityIdentifier.entityType != 'folder'}">
	    <a style="padding-left:20px;" 
	    	href="<portlet:renderURL windowState="maximized"><portlet:param 
	    	name="action" value="view_ws_listing"/><portlet:param 
	    	name="binderId" value="${binder.parentBinder.id}"/></portlet:renderURL>">
			<span  class="ss_smallprint ss_light">(${binder.parentBinder.title})</span></a>
	  </c:if>
	</c:if>
	<c:if test="${binder.entityIdentifier.entityType == 'workspace'}">
	  <a href="<portlet:renderURL windowState="maximized"><portlet:param 
	  	name="action" value="view_ws_listing"/><portlet:param 
	  	name="binderId" value="${binder.id}"/></portlet:renderURL>"><span>${binder.title}</span></a>
	</c:if>
	<c:if test="${binder.entityIdentifier.entityType == 'profiles'}">
	  <a href="<portlet:renderURL windowState="maximized"><portlet:param 
	  	name="action" value="view_profile_listing"/><portlet:param 
	  	name="binderId" value="${binder.id}"/></portlet:renderURL>"><span>${binder.title}</span></a>
	</c:if>
  </td>
  </tr>
  <%
  	if (!folderIdList.equals("")) folderIdList += " ";
  	folderIdList += binder.getId().toString();
  %>
</c:forEach>
</table>
<div id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_note">
</div>
<div align="right">
  <a class="ss_linkButton" 
    href="<portlet:renderURL 
      portletMode="edit" 
      windowState="maximized" />"
    ><ssf:nlt tag="portlet.forum.configure"/></a>
</div>
</c:if>

</div>
</div>
<form class="ss_portlet_style ss_form" id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_unseenCountForm" style="display:none;">
<input type="hidden" name="forumList" value="<%= folderIdList %>">
<input type="hidden" name="ssNamespace" value="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>">
</form>
