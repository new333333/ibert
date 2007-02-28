<%@ page import="com.sitescape.util.BrowserSniffer" %>
<div id="${ss_viewEntryAttachmentDivId}">
	<%
	boolean isIECheck = BrowserSniffer.is_ie(request);
	String strBrowserType = "nonie";
	if (isIECheck) strBrowserType = "ie";
	%>


<table class="ss_attachments_list" cellpadding="0" cellspacing="0">
<tbody>
<c:set var="selectionCount" value="0"/>
<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
  <c:set var="selectionCount" value="${selectionCount + 1}"/>
  <c:set var="versionCount" value="0"/>
  <c:forEach var="fileVersion" items="${selection.fileVersions}">
    <c:set var="versionCount" value="${versionCount + 1}"/>
  </c:forEach>
  <c:set var="thumbRowSpan" value="${versionCount}"/>
  <c:if test="${versionCount > 1}">
    <c:set var="thumbRowSpan" value="${thumbRowSpan + 2}"/>
  </c:if>
  <c:if test="${versionCount == 1}">
    <c:set var="thumbRowSpan" value="1"/>
  </c:if>
     <tr><td colspan="9"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
	  <tr>
		<td valign="top" width="80" rowspan="${thumbRowSpan}"><div class="ss_thumbnail_gallery ss_thumbnail_small"> 
		   <img border="0" src="<ssf:url 
		    webPath="viewFile"
		    folderId="${ssDefinitionEntry.parentBinder.id}"
		    entryId="${ssDefinitionEntry.id}" >
		    <ssf:param name="fileId" value="${selection.id}"/>
		    <ssf:param name="viewType" value="thumbnail"/>
		    </ssf:url>"/>
		    </div>
		</td>
		<td class="ss_att_title" width="25%"><a style="text-decoration: none;" href="<ssf:url 
					    webPath="viewFile"
					    folderId="${ssDefinitionEntry.parentBinder.id}"
					    entryId="${ssDefinitionEntry.id}" >
					    <ssf:param name="fileId" value="${selection.id}"/>
					    </ssf:url>" 
					<c:if test="${ssConfigJspStyle != 'mail'}">    
					    onClick="return ss_launchUrlInNewWindow(this, '${selection.fileItem.name}');"
					</c:if>
					     ><c:out value="${selection.fileItem.name} "/></a>
		</td>
		<td class="ss_att_meta" width="10%"></td>
		<td class="ss_att_meta">
		<ssf:ifSupportsViewAsHtml relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
				<a target="_blank" style="text-decoration: none;" href="<ssf:url 
				    webPath="viewFile"
				    folderId="${ssDefinitionEntry.parentBinder.id}"
			   	 	entryId="${ssDefinitionEntry.id}" >
			    	<ssf:param name="fileId" value="${selection.id}"/>
			    	<ssf:param name="viewType" value="html"/>
			    	</ssf:url>" ><span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="entry.HTML" />]</span></a>
		</ssf:ifSupportsViewAsHtml>
		</td>
		<td class="ss_att_meta">
		<c:if test="${ssConfigJspStyle != 'mail'}">
			<c:if test="${ssEntryAttachmentAllowEdit == 'true'}">
				<ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
				<% if (isIECheck) { %>
					<c:choose>
					<c:when test="${ssEntryAttachmentEditTypeForIE == 'applet'}">
						<c:if test="${ssIsAppletSupported == 'true'}">
							<a href="javascript: ;" onClick="javascript:ss_openWebDAVFile${ssDefinitionEntry.id}${ss_namespace_attach}('<ssf:ssfsInternalAttachmentUrl 
									binder="${ssDefinitionEntry.parentBinder}"
									entity="${ssDefinitionEntry}"
									fileAttachment="${selection}"/>'); return false;"><span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="EDIT"/>]</span></a>
						</c:if>
					</c:when>
					<c:otherwise>
						<a href="<ssf:ssfsInternalAttachmentUrl 
								binder="${ssDefinitionEntry.parentBinder}"
								entity="${ssDefinitionEntry}"
								fileAttachment="${selection}"/>">
								<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="EDIT"/>]</span></a>
					</c:otherwise>
					</c:choose>							
				<% } else { %>
					<c:choose>
					<c:when test="${ssEntryAttachmentEditTypeForNonIE == 'webdav'}">
						<a href="<ssf:ssfsInternalAttachmentUrl 
								binder="${ssDefinitionEntry.parentBinder}"
								entity="${ssDefinitionEntry}"
								fileAttachment="${selection}"/>">
								<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="EDIT"/>]</span></a>
					</c:when>
					<c:otherwise>
						<c:if test="${ssIsAppletSupported == 'true'}">
							<a href="javascript: ;" onClick="javascript:ss_openWebDAVFile${ssDefinitionEntry.id}${ss_namespace_attach}('<ssf:ssfsInternalAttachmentUrl 
									binder="${ssDefinitionEntry.parentBinder}"
									entity="${ssDefinitionEntry}"
									fileAttachment="${selection}"/>'); return false;"><span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="EDIT"/>]</span></a>
						</c:if>
					</c:otherwise>
					</c:choose>							
				<% } %>
			</ssf:ifSupportsEditInPlace>						
		    </c:if>
	 	</c:if>
		</td>
		<td class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${selection.modification.date}" type="both" 
					 timeStyle="short" dateStyle="short" /></td>
		<td class="ss_att_meta">${selection.fileItem.lengthKB}KB</td>
		<td class="ss_att_meta ss_att_space">${selection.creation.principal.title}</td>
		<td class="ss_att_meta" width="15%"></td>
	</tr>
	<c:if test="${!empty selection.fileVersions && versionCount > 1}">
        <tr><td colspan="8"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
		<tr>
		  <td class="ss_att_title ss_subhead2" colspan="8"><ssf:nlt tag="entry.PreviousVersions"/></td>
		</tr>	
		<c:set var="versionCount" value="0"/>
		<c:forEach var="fileVersion" items="${selection.fileVersions}">
			<c:if test="${versionCount > 0}">
	          <tr>
				<td class="ss_att_title" width="25%" style="padding-left: 5px; font-weight: normal;"><a style="text-decoration: none;"
				  href="<ssf:url 
				    webPath="viewFile"
				    folderId="${ssDefinitionEntry.parentBinder.id}"
				    entryId="${ssDefinitionEntry.id}" >
				    <ssf:param name="fileId" value="${selection.id}"/>
				    <ssf:param name="versionId" value="${fileVersion.id}"/>
				    </ssf:url>"
					<c:if test="${ssConfigJspStyle != 'mail'}">    
					    onClick="return ss_launchUrlInNewWindow(this, '${selection.fileItem.name}');"
					</c:if>				    
				    ><ssf:nlt tag="entry.Version"/> ${fileVersion.versionNumber}</a></td>
				<td class="ss_att_meta" width="10%"></td>
				<td class="ss_att_meta"></td>
				<td class="ss_att_meta"></td>    
				<td class="ss_att_meta ss_att_space"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${fileVersion.modification.date}" type="both" 
					 timeStyle="short" dateStyle="short" /></td>
				<td class="ss_att_meta">${fileVersion.fileItem.lengthKB}KB</td>
				<td width="25%" class="ss_att_meta ss_att_space">${fileVersion.creation.principal.title}</td>
				<td class="ss_att_meta" width="15%"></td>
			  </tr>
			</c:if>
			<c:set var="versionCount" value="${versionCount + 1}"/>
 	    </c:forEach>
	</c:if>
</c:forEach>
<c:if test="${selectionCount > 0}">
     <tr><td colspan="9"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
</c:if>
</tbody>
</table>

</div>
