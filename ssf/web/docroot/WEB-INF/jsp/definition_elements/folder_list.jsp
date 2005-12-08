<% // Folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssFolder" type="com.sitescape.ef.domain.Folder" scope="request" />
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="ssFolderDomTree" type="org.dom4j.Document" scope="request" />
<%
	String folderId = ssFolder.getId().toString();
	String parentFolderId = "";
	if (ssFolder instanceof Folder) {
		Folder parentFolder = ((Folder) ssFolder).getParentFolder();
		if (parentFolder != null) parentFolderId = parentFolder.getId().toString();
	}
%>
<script language="javascript">
var ss_currentEntryId = "";
function ss_loadEntry(obj,id) {
	var folderLine = 'folderLine_'+id;
	ss_currentEntryId = id;
	<c:out value="${showEntryMessageRoutine}"/>("<ssf:nlt tag="loading" text="Loading..."/>");
	highlightLineById(folderLine);
	ss_showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

function ss_loadEntryUrl(url,id) {
	var folderLine = 'folderLine_'+id;
	ss_currentEntryId = id;
	<c:out value="${showEntryMessageRoutine}"/>("<ssf:nlt tag="loading" text="Loading..."/>");
	highlightLineById(folderLine);
	ss_showForumEntry(url, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

</script>
<div class="folder">
<table width="100%">
<tr>
  <th align="left">Folders</th>
</tr>
<tr>
  <td>
	<div>
	  <ssf:tree treeName="folderTree" treeDocument="<%= ssFolderDomTree %>" 
	    rootOpen="false" 
	    nodeOpen="<%= parentFolderId %>" highlightNode="<%= folderId %>" />
	</div>
  </td>
</tr>
</table>
</div>
<br>
<div class="folder">
<table width="100%">
<tr>
  <th align="left"><img border="0" src="<html:imagesPath/>pics/sym_s_unseen_header.gif"></th>
  <th align="left">Number</th>
  <th align="left">Title</th>
  <th align="left">Author</th>
  <th align="left">Date</th>
</tr>
<c:forEach var="entry" items="${ssFolderEntries}" >
<jsp:useBean id="entry" type="com.sitescape.ef.domain.FolderEntry" />
<tr id="folderLine_<c:out value="${entry.id}"/>">
  <td align="right" valign="top" width="1%">
<%
	if (ssSeenMap.checkIfSeen(entry)) {
%>&nbsp;<%
	} else {
%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
%>
  </td>
  <td align="right" valign="top" width="5%">
	<c:out value="${entry.docNumber}"/>.&nbsp;&nbsp;&nbsp;
  </td>
  <td valign="top" width="40%">
    <a href="<ssf:url 
    adapter="true" 
    portletName="ss_forum" 
    folderId="<%= folderId %>" 
    action="view_entry" 
    entryId="<%= entry.getId().toString() %>" actionUrl="false" popup="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry.id}"/>');return false;" >
    <c:if test="${empty entry.title}">
    <span class="fineprint"><i>(no title)</i></span>
    </c:if>
    <c:out value="${entry.title}"/></a>
  </td>
  <td valign="top" width="30%">
    <c:out value="${entry.creation.principal.title}"/>
  </td>
  <td valign="top" width="20%">
    <c:out value="${entry.modification.date}"/>
  </td>
</tr>
</c:forEach>
</table>
</div>
