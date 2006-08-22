<% // User filters %>
<%@ page import="java.util.List" %>
<%@ page import="com.sitescape.ef.util.NLT" %>
<%@ page import="com.sitescape.ef.domain.UserProperties" %>
<%
	UserProperties userFolderProperties = (UserProperties) request.getAttribute("ssUserFolderProperties");
	String filterName = "--" + NLT.get("none") + "--";
	if (userFolderProperties != null) {
		Map searchFilters = (Map) userFolderProperties.getProperty("searchFilters");
		if (searchFilters == null) searchFilters = new java.util.HashMap();
		String userFilter = (String) userFolderProperties.getProperty("userFilter");
		if (userFilter != null && !userFilter.equals("")) filterName = userFilter;
		
		renderRequest.setAttribute("ss_searchFilters", searchFilters);
	}
%>
<div style="display:inline;" id="<portlet:namespace/>ss_filter_select">
<span class="ss_bold"><ssf:nlt tag="filter.filter" text="Filter"/>:&nbsp;</span>
<div style="display:inline; border:1px solid black; padding:2px 0px 1px 0px;">
<form name="ss_filterSelect" style="display:inline;"
	action="<portlet:actionURL windowState="maximized">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="operation" value="select_filter"/>
		</portlet:actionURL>" method="post" style="display:inline;">
<div id="ss_filterTitle" style="display:inline;">
<ssf:menu title="<%= filterName %>" titleId="ss_filterTitle">
<ul>
<li><a href="javascript: ;" 
  onClick="ss_changeUserFilter(this, '<c:out value=""/>');return false;"
>--<ssf:nlt tag="none" text="none"/>--</a></li>
<c:forEach var="filter" items="${ss_searchFilters}">
<li><a href="javascript: ;" 
  onClick="ss_changeUserFilter(this, '<c:out value="${filter.key}"/>');return false;"
><c:out value="${filter.key}"/></a></li>
</c:forEach>
</ul>
</ssf:menu>
</div>
<input type="hidden" name="select_filter">
</form>
</div>&nbsp;&nbsp;&nbsp;<a href="<portlet:renderURL windowState="maximized">
		<portlet:param name="action" value="build_filter"/>
		<portlet:param name="binderId" value="${ssBinder.id}"/>
		<portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
		</portlet:renderURL>"
><span class="ss_fineprint"><ssf:nlt tag="edit" text="edit"/></span></a>
</div>
<script type="text/javascript">
function ss_changeUserFilter(obj, filter) {
	var loading = "&nbsp;(<ssf:nlt tag="loading" text="loading"/>)&nbsp;"
	document.getElementById('ss_filterTitle').innerHTML = "&nbsp;"+obj.innerHTML+loading;
	document.forms.ss_filterSelect.select_filter.value = filter;
	document.forms.ss_filterSelect.submit();
}
</script>

