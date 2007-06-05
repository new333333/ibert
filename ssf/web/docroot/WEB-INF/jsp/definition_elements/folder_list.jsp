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
<% // Folder listing - select the style that the folder should be displayed in %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<script type="text/javascript" src="<html:rootPath/>js/forum/ss_folder.js"></script>

<c:if test="${ss_folderViewStyle == 'event'}">
<%@ include file="/WEB-INF/jsp/definition_elements/calendar_view.jsp" %>
</c:if>
<c:if test="${ss_folderViewStyle == 'file'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/file_folder_view.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'blog'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/blog.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'wiki'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/wiki/wiki.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'photo'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/photo/photo.jsp" />
</c:if>
<c:if test="${empty ss_folderViewStyle || ss_folderViewStyle == 'folder'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/searchview/searchview.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'guestbook'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/guestbook/guestbook.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'task'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/task/task.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'survay'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/survay/survay.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'table'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/folder_view.jsp" />
</c:if>