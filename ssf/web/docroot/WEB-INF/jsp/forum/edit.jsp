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

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
//treename must start with editForum
String wsTreeName = "editForum_" + renderResponse.getNamespace();
%>
<table class="ss_style" width="100%"><tr><td>
<c:if test="${!empty ssFolderList}">
<table class="ss_style" cellspacing="0" cellpadding="0">
<tr><th align="left"><ssf:nlt tag="portlet.forum.selected.forums" text="Currently selected forums:"/></th></tr>
<tr><td>&nbsp;</td></tr>
<c:forEach var="folder" items="${ssFolderList}">
<tr><td><c:out value="${folder.title}" /></td></tr>
</c:forEach>
</table>
<br>
</c:if>

<form class="ss_style ss_form" action="<portlet:actionURL/>" method="post" name="<portlet:namespace />fm">

<br>
<span class="ss_bold"><ssf:nlt tag="portlet.forum.select.forums" text="Select the forums to be shown:"/></span>
<br>
<script type="text/javascript">
function <%= wsTreeName %>_showId(forum, obj) {
	if (self.document.<portlet:namespace />fm["id_"+forum] && self.document.<portlet:namespace />fm["id_"+forum].checked) {
		self.document.<portlet:namespace />fm["id_"+forum].checked=false
	} else {
		self.document.<portlet:namespace />fm["id_"+forum].checked=true
	}
	return false
}
</script>
<ssf:tree treeName="<%= wsTreeName %>"  treeDocument="${ssWsDomTree}" 
 	topId="${ssWsDomTreeBinderId}" rootOpen="true" 
	  multiSelect="${ssBinderIdList}" multiSelectPrefix="id_" />

<br>
<input type="submit" class="ss_submit" name="applyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">
<input style="margin-left:15px;" type="submit" class="ss_submit" name="closeBtn"
 value="<ssf:nlt tag="button.close"/>"
 onClick="self.location.href='<portlet:renderURL windowState="normal" portletMode="view"/>';return false;"
</form>
<br>

</td></tr></table>

