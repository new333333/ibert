<%
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
%>
<% //View the listing part of a photo folder %>

			<table width="99%"><tr><td>
			<div class="ss_thumbnail_gallery ss_thumbnail_medium"> 
			<c:forEach var="fileEntry" items="${ssFolderEntries}" >
<jsp:useBean id="fileEntry" type="java.util.HashMap" />
<%
	String folderLineId = "folderLine_" + (String) fileEntry.get("_docId");
	String seenStyle = "";
	String seenStyleFine = "class=\"ss_finePrint\"";
	if (!ssSeenMap.checkIfSeen(fileEntry)) {
		seenStyle = "class=\"ss_unseen\"";
		seenStyleFine = "class=\"ss_unseen ss_fineprint\"";
	}
%>
			  <c:if test="${not empty fileEntry._fileID}">
<%
	String _fileId = fileEntry.get("_fileID").toString();
	if (_fileId.contains(",")) _fileId = _fileId.substring(0, _fileId.indexOf(","));
	String _fileTime = "";
	if (fileEntry.containsKey("_fileTime")) _fileTime = fileEntry.get("_fileTime").toString();
	if (_fileTime.contains(",")) _fileTime = _fileTime.substring(0, _fileTime.indexOf(","));
%>			
			    <div>
			    <a onMouseOver="ss_showHoverOver(this, 'ss_photoTitle_${fileEntry._docId}')" 
			      onMouseOut="ss_hideHoverOver(this, 'ss_photoTitle_${fileEntry._docId}')"
			      href="<ssf:url 
				    webPath="viewFile"
				    folderId="${fileEntry._binderId}"
				    entryId="${fileEntry._docId}" >
	    			<ssf:param name="entityType" value="${fileEntry._entityType}"/>
				    <ssf:param name="fileId" value="<%= _fileId %>"/>
				    <ssf:param name="fileTime" value="<%= _fileTime %>"/>
				    </ssf:url>"
					onClick="return ss_openUrlInWindow(this, '_blank');">
			    <img <ssf:alt text="${fileEntry.title}"/> border="0" src="<ssf:url 
			    webPath="viewFile"
			    folderId="${fileEntry._binderId}"
			    entryId="${fileEntry._docId}" >
	    		<ssf:param name="entityType" value="${fileEntry._entityType}"/>
			    <ssf:param name="fileId" value="<%= _fileId %>"/>
			    <ssf:param name="fileTime" value="<%= _fileTime %>"/>
			    <ssf:param name="viewType" value="thumbnail"/>
			    </ssf:url>"></a><br\>
			    <a 
				    href="<ssf:url     
				    adapter="true" 
				    portletName="ss_forum" 
				    folderId="${ssFolder.id}" 
				    action="view_folder_entry" 
				    entryId="${fileEntry._docId}" actionUrl="true" />" 
				    onClick="ss_loadEntry(this, '${fileEntry._docId}');return false;" 
				    ><c:if test="${empty fileEntry.title}"
				    ><span id="folderLine_${fileEntry._docId}" <%= seenStyleFine %>
				    >--<ssf:nlt tag="entry.noTitle"/>--</span
				    ></c:if><span id="folderLine_${fileEntry._docId}" <%= seenStyle %>
				    ><c:out value="${fileEntry.title}"/></span></a>
			    </div>
			 </c:if>
			
			  <c:if test="${empty fileEntry._fileID}">
			
			    <div>
			    <img <ssf:alt text="${fileEntry.title}"/> border="0" 
			      src="<html:imagesPath/>thumbnails/NoImage.jpeg"/><br/>
			    <a 
				    href="<ssf:url     
				    adapter="true" 
				    portletName="ss_forum" 
				    folderId="${ssFolder.id}" 
				    action="view_folder_entry" 
				    entryId="${fileEntry._docId}" actionUrl="true" />" 
				    onClick="ss_loadEntry(this, '${fileEntry._docId}');return false;" 
				    ><c:if test="${empty fileEntry.title}"
				    ><span id="folderLine_${fileEntry._docId}" <%= seenStyleFine %>
				    >--<ssf:nlt tag="entry.noTitle"/>--</span
				    ></c:if><span id="folderLine_${fileEntry._docId}" <%= seenStyle %>
				    ><c:out value="${fileEntry.title}"/></span></a>
			    </div>
			 </c:if>
			
			</c:forEach>
			</div>
			</td></tr></table>

   </div>

<c:forEach var="fileEntry" items="${ssFolderEntries}" >
  <div id="ss_photoTitle_${fileEntry._docId}" class="ss_hover_over" 
    style="visibility:hidden; display:none;">
    <c:if test="${empty fileEntry.title}"
      ><span 
      >--<ssf:nlt tag="entry.noTitle"/>--</span
      ></c:if><span id="folderLine_${fileEntry._docId}"
      ><c:out value="${fileEntry.title}"/></span><c:if test="${!empty fileEntry._desc}"
      ><hr width="80%" align="left"/></c:if>
    <span ><ssf:markup type="view" binderId="${fileEntry._binderId}" entryId="${fileEntry._docId}"><%--
    		--%><ssf:textFormat formatAction="limitedDescription" textMaxWords="100">${fileEntry._desc}</ssf:textFormat><%--
    		--%></ssf:markup></span>
  </div>
</c:forEach>

<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>
