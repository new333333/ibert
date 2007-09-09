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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.ssfs.util.SsfsUtil" %>
<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<div id="${ss_viewEntryAttachmentDivId}">
<%
boolean isIECheck = BrowserSniffer.is_ie(request);
String strBrowserType = "nonie";
if (isIECheck) strBrowserType = "ie";
boolean isAppletSupportedCheck = SsfsUtil.supportApplets();
String operatingSystem = BrowserSniffer.getOSInfo(request);
boolean ss_isMail = false;
%>
<c:if test="${ssConfigJspStyle == 'mail'}"><% ss_isMail = true; %></c:if>

<table class="ss_attachments_list" cellpadding="0" cellspacing="0">
<tbody>
<c:set var="selectionCount" value="0"/>
<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
  <jsp:useBean id="selection" type="com.sitescape.team.domain.FileAttachment" />
<%
	String fn = selection.getFileItem().getName();
	String ext = "";
	if (fn.lastIndexOf(".") >= 0) ext = fn.substring(fn.lastIndexOf("."));
	boolean editInPlaceSupported = false;
%>
  <ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
<%  editInPlaceSupported = true;  %>
  </ssf:ifSupportsEditInPlace>

  <c:set var="selectionCount" value="${selectionCount + 1}"/>
  <c:set var="versionCount" value="0"/>
  <c:forEach var="fileVersion" items="${selection.fileVersionsUnsorted}">
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

<%
	if (isIECheck && ext.equals(".ppt") && editInPlaceSupported && !ss_isMail) {
	    //This is IE and a ppt file; use the edit app to launch powerpoint because of bug in IE and/or powerpoint
%>
		<td valign="top" width="80" rowspan="${thumbRowSpan}">
		  <div class="ss_thumbnail_gallery ss_thumbnail_small"> 
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="applet">
				<ssf:isFileEditorConfiguredForOS relativeFilePath="${selection.fileItem.name}" operatingSystem="<%= operatingSystem %>">
					<a style="text-decoration: none;" href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${selection}"/>" 
						onClick="javascript:ss_openWebDAVFile('${ssDefinitionEntry.parentBinder.id}', 
						    '${ssDefinitionEntry.id}', 
						    '${ss_attachments_namespace}', 
						    '<%= operatingSystem %>', 
							'${selection.id}');
							return false;"
				    	<ssf:title tag="title.open.file">
					      <ssf:param name="value" value="${selection.fileItem.name}" />
				    	</ssf:title>
					><img border="0" <ssf:alt text="${selection.fileItem.name}"/> 
					  src="<ssf:url 
					    webPath="viewFile"
					    folderId="${ssDefinitionEntry.parentBinder.id}"
					    entryId="${ssDefinitionEntry.id}"
					    entityType="${ssDefinitionEntry.entityType}" >
					    <ssf:param name="fileId" value="${selection.id}"/>
					    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
					    <ssf:param name="viewType" value="thumbnail"/>
					    </ssf:url>"/></a>
				</ssf:isFileEditorConfiguredForOS>
			</ssf:editorTypeToUseForEditInPlace>
			
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
				<a href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${selection}"/>"
				><img border="0" <ssf:alt text="${selection.fileItem.name}"/> 
					  src="<ssf:url 
					    webPath="viewFile"
					    folderId="${ssDefinitionEntry.parentBinder.id}"
					    entryId="${ssDefinitionEntry.id}"
					    entityType="${ssDefinitionEntry.entityType}" >
					    <ssf:param name="fileId" value="${selection.id}"/>
					    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
					    <ssf:param name="viewType" value="thumbnail"/>
					    </ssf:url>"/></a>
			</ssf:editorTypeToUseForEditInPlace>

		  </div>
		</td>
		<td class="ss_att_title" width="25%">
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="applet">
				<ssf:isFileEditorConfiguredForOS relativeFilePath="${selection.fileItem.name}" operatingSystem="<%= operatingSystem %>">
					<a style="text-decoration: none;" href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${selection}"/>" 
						onClick="javascript:ss_openWebDAVFile('${ssDefinitionEntry.parentBinder.id}', 
						    '${ssDefinitionEntry.id}', 
						    '${ss_attachments_namespace}', 
						    '<%= operatingSystem %>', 
							'${selection.id}');
							return false;"
				    	<ssf:title tag="title.open.file">
					      <ssf:param name="value" value="${selection.fileItem.name}" />
				    	</ssf:title>
					><c:out value="${selection.fileItem.name} "/></a>
				</ssf:isFileEditorConfiguredForOS>
			</ssf:editorTypeToUseForEditInPlace>
			
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
				<a href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${selection}"/>"
				><c:out value="${selection.fileItem.name} "/></a>
			</ssf:editorTypeToUseForEditInPlace>

			<c:if test="${!empty selection.fileLock}">
			  <br/>
			  <img <ssf:alt tag="alt.locked"/> src="<html:imagesPath/>pics/sym_s_caution.gif"/>
			  <span class="ss_fineprint"><ssf:nlt tag="entry.lockedBy">
	    		<ssf:param name="value" value="${selection.fileLock.owner.title}"/>
			  </ssf:nlt></span>
			</c:if>
		</td>
<%
	}

	if (!isIECheck || !ext.equals(".ppt") || !editInPlaceSupported || ss_isMail) {
%>
		<td valign="top" width="80" rowspan="${thumbRowSpan}">
		<c:if test="${ssConfigJspStyle != 'mail'}">
		<div class="ss_thumbnail_gallery ss_thumbnail_small"> 
			<a style="text-decoration: none;" href="<ssf:fileurl 
					    webPath="viewFile"
    					fileName="${selection.fileItem.name}"
					    folderId="${ssDefinitionEntry.parentBinder.id}"
					    entryId="${ssDefinitionEntry.id}"
					    entityType="${ssDefinitionEntry.entityType}" >
					    <ssf:param name="fileId" value="${selection.id}"/>
					    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
					    </ssf:fileurl>" 
					<c:if test="${ssConfigJspStyle != 'mail'}">    
					    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${selection.fileItem.name}"/>');"
					</c:if>
					
				    <ssf:title tag="title.open.file">
					    <ssf:param name="value" value="${selection.fileItem.name}" />
				    </ssf:title>
					     ><img border="0" <ssf:alt text="${selection.fileItem.name}"/> src="<ssf:url 
		    webPath="viewFile"
		    folderId="${ssDefinitionEntry.parentBinder.id}"
		    entryId="${ssDefinitionEntry.id}"
		    entityType="${ssDefinitionEntry.entityType}" >
		    <ssf:param name="fileId" value="${selection.id}"/>
		    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
		    <ssf:param name="viewType" value="thumbnail"/>
		    </ssf:url>"/></a>
	    </div>
	    </c:if>
		</td>
		<td class="ss_att_title" width="25%"><a style="text-decoration: none;" 
					<c:if test="${ssConfigJspStyle != 'mail'}">    
						href="<ssf:url 
					    webPath="viewFile"
					    folderId="${ssDefinitionEntry.parentBinder.id}"
					    entryId="${ssDefinitionEntry.id}"
					    entityType="${ssDefinitionEntry.entityType}" >
					    <ssf:param name="fileId" value="${selection.id}"/>
					    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
					    </ssf:url>" 
					    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${selection.fileItem.name}"/>');"
					</c:if>
					<c:if test="${ssConfigJspStyle == 'mail'}">   
						href="<ssf:url 
				  		adapter="true" 
				    	portletName="ss_forum" 
				   		action="view_permalink"
						binderId="${ssDefinitionEntry.parentBinder.id}"
						entryId="${ssDefinitionEntry.id}">
						<ssf:param name="entityType" value="${ssDefinitionEntry.entityType}" />
					    <ssf:param name="fileId" value="${selection.id}"/>
					    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
				    	<ssf:param name="newTab" value="1"/>
				 	  	</ssf:url>" 
					</c:if> 

				    <ssf:title tag="title.open.file">
					    <ssf:param name="value" value="${selection.fileItem.name}" />
				    </ssf:title>
					><c:out value="${selection.fileItem.name} "/></a>
			<c:if test="${!empty selection.fileLock}">
			  <br/>
			  <img <ssf:alt tag="alt.locked"/> src="<html:imagesPath/>pics/sym_s_caution.gif"/>
			  <span class="ss_fineprint"><ssf:nlt tag="entry.lockedBy">
	    		<ssf:param name="value" value="${selection.fileLock.owner.title}"/>
			  </ssf:nlt></span>
			</c:if>
		</td>
<%
	}
%>

		<td class="ss_att_meta" width="10%"></td>
		<td class="ss_att_meta">
		<c:if test="${ssConfigJspStyle != 'mail'}">
		<ssf:ifSupportsViewAsHtml relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
				<a target="_blank" style="text-decoration: none;" href="<ssf:url 
				    webPath="viewFile"
				    folderId="${ssDefinitionEntry.parentBinder.id}"
			   	 	entryId="${ssDefinitionEntry.id}"
				    entityType="${ssDefinitionEntry.entityType}" >
			    	<ssf:param name="fileId" value="${selection.id}"/>
			    	<ssf:param name="fileTime" value="${selection.modification.date.time}"/>
			    	<ssf:param name="viewType" value="html"/>
			    	</ssf:url>" <ssf:title tag="title.open.file.in.html.format" /> ><span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="entry.HTML" />]</span></a>
		</ssf:ifSupportsViewAsHtml>
		</c:if>
		</td>
		<td class="ss_att_meta">
		<c:if test="${ssConfigJspStyle != 'mail'}">
		
		<ssf:ifnotaccessible>
		
			<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
				<ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
					
					<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="applet">
					
						<ssf:isFileEditorConfiguredForOS relativeFilePath="${selection.fileItem.name}" operatingSystem="<%= operatingSystem %>">
					
							<a href="javascript: ;" 
								onClick="javascript:ss_openWebDAVFile('${ssDefinitionEntry.parentBinder.id}', '${ssDefinitionEntry.id}', '${ss_attachments_namespace}', '<%= operatingSystem %>', 
									'${selection.id}');
									return false;">
								<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="EDIT"/>]</span></a>

						</ssf:isFileEditorConfiguredForOS>
							
					</ssf:editorTypeToUseForEditInPlace>
					
					<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
						<a href="<ssf:ssfsInternalAttachmentUrl 
								binder="${ssDefinitionEntry.parentBinder}"
								entity="${ssDefinitionEntry}"
								fileAttachment="${selection}"/>">
								<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="EDIT"/>]</span></a>
					</ssf:editorTypeToUseForEditInPlace>
				
				</ssf:ifSupportsEditInPlace>
			</c:if>	
			
		</ssf:ifnotaccessible>
			
	 	</c:if>
		</td>
		<td class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${selection.modification.date}" type="both" 
					 timeStyle="medium" dateStyle="short" /></td>
		<td class="ss_att_meta">${selection.fileItem.lengthKB}KB</td>
		<td class="ss_att_meta ss_att_space">${selection.modification.principal.title}</td>
		<td class="ss_att_meta" width="15%"></td>
	</tr>
	<c:if test="${!empty selection.fileVersions && versionCount > 1 && ssConfigJspStyle != 'mail'}">
        <tr><td class="ss_att_title" colspan="8"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
		<tr>
		  <td class="ss_att_title ss_subhead2" colspan="8"><ssf:nlt tag="entry.PreviousVersions"/></td>
		</tr>	
		<c:forEach var="fileVersion" items="${selection.fileVersions}" begin="1">
	          <tr>
				<td class="ss_att_title" width="25%" style="padding-left: 5px; font-weight: normal;"><a style="text-decoration: none;"
				  href="<ssf:url 
				    webPath="viewFile"
				    folderId="${ssDefinitionEntry.parentBinder.id}"
				    entryId="${ssDefinitionEntry.id}"
				    entityType="${ssDefinitionEntry.entityType}" >
				    <ssf:param name="fileId" value="${selection.id}"/>
				    <ssf:param name="versionId" value="${fileVersion.id}"/>
				    <ssf:param name="fileTime" value="${fileVersion.modification.date.time}"/>
				    </ssf:url>"
					<c:if test="${ssConfigJspStyle != 'mail'}">    
					    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${selection.fileItem.name}"/>');"
					</c:if>
					
				    <ssf:title tag="title.open.file.version">
					    <ssf:param name="value" value="${selection.fileItem.name}" />
					    <ssf:param name="value" value="${fileVersion.versionNumber}" />
				    </ssf:title>
					
				    ><ssf:nlt tag="entry.Version"/> ${fileVersion.versionNumber}</a></td>
				<td class="ss_att_meta" width="10%"></td>
				<td class="ss_att_meta"></td>
				<td class="ss_att_meta"></td>    
				<td class="ss_att_meta ss_att_space"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${fileVersion.modification.date}" type="both" 
					 timeStyle="medium" dateStyle="short" /></td>
				<td class="ss_att_meta">${fileVersion.fileItem.lengthKB}KB</td>
				<td width="25%" class="ss_att_meta ss_att_space">${fileVersion.modification.principal.title}</td>
				<td class="ss_att_meta" width="15%"></td>
			  </tr>
 	    </c:forEach>
	</c:if>
</c:forEach>
<c:if test="${selectionCount > 0}">
     <tr><td colspan="9"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
</c:if>
</tbody>
</table>

</div>
