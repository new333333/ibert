<%
// The dashboard "search" component
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
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ssComponentId}">
<c:set var="ssNamespace" value="${ssNamespace}_${ssComponentId}"/>
</c:if>
<c:set var="portletNamespace" value=""/>
<ssf:ifnotadapter>
<c:set var="portletNamespace" value="${renderResponse.namespace}"/>
</ssf:ifnotadapter>

<c:set var="ss_divId" value="ss_searchResults_${ssNamespace}"/>
<c:set var="ss_pageNumber" value="0"/>
<c:if test="${ssDashboard.scope == 'portlet'}">
<%@ include file="/WEB-INF/jsp/dashboard/portletsupport.jsp" %>
</c:if>
<script type="text/javascript">
function ${ss_divId}_searchurl(binderId, entryId, type) {
	//Build a url to go to
	return ss_gotoPermalink(binderId, entryId, type, '${portletNamespace}', 'yes');
}
</script>

<c:if test="${ssConfigJspStyle == 'template'}">
<script type="text/javascript">
function ${ss_divId}_searchurl(binderId, entryId, type) {
	return false;
}
</script>
</c:if>
<!-- TITLE OR DESCRIPTION?  -->
<p/>
<div id="${ss_divId}">
<%@ include file="/WEB-INF/jsp/dashboard/search_view2.jsp" %>
</div>
