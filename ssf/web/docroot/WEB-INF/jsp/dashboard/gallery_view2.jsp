<%
// The dashboard "gallery search" component
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
 //Don't include "include.jsp" directly 
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>

<table width="99%"><tr><td>
<c:choose>
<c:when test="${ssDashboard.dashboard.components[componentId].data.galleryImageSize == 'small'}">
<div class="ss_thumbnail_gallery ss_thumbnail_small"> 
</c:when>
<c:otherwise>
<div class="ss_thumbnail_gallery ss_thumbnail_big"> 
</c:otherwise>
</c:choose>
<c:set var="hitCount" value="0"/>
<c:set var="resultCount" value="0"/>
<c:forEach var="fileEntry" items="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}" >
  <c:set var="resultCount" value="${resultCount + 1}"/>
  <c:if test="${not empty fileEntry._fileID}">

  <c:set var="hitCount" value="${hitCount + 1}"/>
    <div>
	  <a href="<ssf:url 
	    webPath="viewFile"
	    folderId="${fileEntry._binderId}"
	    entryId="${fileEntry._docId}" >
	    <ssf:param name="entityType" value="${fileEntry._entityType}"/>
	    <ssf:param name="fileId" value="${fileEntry._fileID}"/>
	    <ssf:param name="fileTime" value="${fileEntry._fileTime}"/>
	    </ssf:url>"
		onClick="return ss_openUrlInWindow(this, '_blank');">
    <img <ssf:alt text="${fileEntry.title}"/> border="0" src="<ssf:url 
    webPath="viewFile"
    folderId="${fileEntry._binderId}"
    entryId="${fileEntry._docId}" >
	<ssf:param name="entityType" value="${fileEntry._entityType}" />
    <ssf:param name="fileId" value="${fileEntry._fileID}"/>
    <ssf:param name="fileTime" value="${fileEntry._fileTime}"/>
    <ssf:param name="viewType" value="thumbnail"/>
    </ssf:url>"></a>
    <br\>
  	<c:choose>
  	<c:when test="${fileEntry._entityType == 'folderEntry'}">
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${fileEntry._binderId}"
		    entryId="${fileEntry._docId}">
		    <ssf:param name="entityType" value="${fileEntry._entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>"
		onClick="return ss_gotoPermalink('${fileEntry._binderId}','${fileEntry._docId}', '${fileEntry._entityType}', '${ss_namespace}', 'yes');">

    </c:when>
    <c:when test="${fileEntry._entityType == 'user'}">
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
			action="view_permalink"
			binderId="${fileEntry._principal.workspaceId}">
			<ssf:param name="entityType" value="workspace" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>" 
		onClick="return ss_gotoPermalink('${fileEntry._binderId}','${fileEntry._docId}', '${fileEntry._entityType}', '${ss_namespace}', 'yes');">

    </c:when>
    <c:when test="${fileEntry._entityType == 'group'}">
    <a target="_blank" href="<ssf:url action="view_profile_entry" 
    		folderId="${fileEntry._binderId}"
    		entryId="${fileEntry._docId}" />" >
    </c:when>
    <c:when test="${fileEntry._entityType == 'folder' || fileEntry._entityType == 'workspace' || fileEntry._entityType == 'profiles'}">
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${fileEntry._docId}">
		    <ssf:param name="entityType" value="${fileEntry._entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>" 
		onClick="return ss_gotoPermalink('${fileEntry._docId}','${fileEntry._docId}', '${fileEntry._entityType}', '${ss_namespace}', 'yes');">

    </c:when>
 	</c:choose>
    <c:out value="${fileEntry.title}"/></a></div>
 </c:if>

</c:forEach>
</div>
</table>

<div>
  <table width="100%">
   <tr>
    <td>
<c:if test="${hitCount > 0}">
      <span class="ss_light ss_fineprint">
	    [<ssf:nlt tag="folder.Results">
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + 1}"/>
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + hitCount}"/>
	    <ssf:param name="value" value="${ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}"/>
	    </ssf:nlt>]
	  </span>
</c:if>
<c:if test="${hitCount == 0}">
    <span class="ss_light ss_fineprint">
	  [<ssf:nlt tag="dashboard.gallery.noneFound"/>]
	</span>
</c:if>
	</td>
	<td align="right">
	<c:if test="${ssDashboard.scope != 'portlet'}">
		<c:set var="binderId" value="${ssBinder.id}"/>
	</c:if>
	<c:if test="${ssDashboard.scope == 'portlet'}">
		<c:set var="binderId" value="${ssDashboardPortlet.id}"/>
	</c:if>
	  <c:if test="${ss_pageNumber > 0}">
	    <span>
	      <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_namespace}', '${ss_divId}', '${componentId}', 'gallery'); return false;"
	        href="javascript:;" >&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/></a>&nbsp;&nbsp;&nbsp;
	    </span>
	  </c:if>
	  <c:if test="${(empty ss_pageNumber || ss_pageNumber == 0) && ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount > ss_pageSize}">
	    <span class="ss_light">&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/>&nbsp;&nbsp;&nbsp;</span>
	  </c:if>
	  <c:if test="${(ss_pageNumber * ss_pageSize + resultCount) < ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
	    <span>&nbsp;&nbsp;
	      <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}', '${ss_namespace}', '${ss_divId}', '${componentId}', 'gallery'); return false;"
	        href="javascript:;" ><ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</a>
	    </span>
	  </c:if>
	  <c:if test="${(ss_pageNumber * ss_pageSize + resultCount) >= ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
	    <span class="ss_light">&nbsp;&nbsp;<ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</span>
	  </c:if>
    </td>
   </tr>
  </table>
</div>
