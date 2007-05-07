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
<%@ include file="/WEB-INF/jsp/common/servlet.include.jsp" %>

<script>
function reloadUrlFromApplet${ssEntryId}${ss_namespace}(strErrorMessage) {
	if (strErrorMessage != "") {
		alert(strErrorMessage);
	}
	parent.ss_hideAddAttachmentDropboxAndAJAXCall('${ssBinderId}', '${ssEntryId}', '${ss_namespace}');
}
function ss_hideDropTarget${ssEntryId}${ss_namespace}() {
	parent.ss_hideAddAttachmentDropbox('${ssEntryId}', '${ss_namespace}');
}
function getWindowBgColor${ssEntryId}${ss_namespace}() {
	return "#ffffff";
}
function ss_hideLoadingDropTargetDiv${ssEntryId}${ss_namespace}() {
	var divId = 'ss_divDropTargetLoading${ssEntryId}${ss_namespace}';
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
	var appletObj = document.getElementById('dropboxobj${ssEntryId}${ss_namespace}');
	if (appletObj != null) appletObj.focus();
}
function ss_showLoadingDropTargetDiv${ssEntryId}${ss_namespace}() {
	var divId = 'ss_divDropTargetLoading${ssEntryId}${ss_namespace}';
	ss_showDiv(divId);
}
</script>

<body align="top" class="ss_entryContent ss_style" onLoad="javascript:ss_showLoadingDropTargetDiv${ssEntryId}${ss_namespace}();">

	<%
	 boolean isIE = com.sitescape.util.BrowserSniffer.is_ie(request);
	%>
	<table border="0" cellspacing="0" cellpadding="0" valign="top" height="100%" width="100%">
		<tr><td align="center">
			<div id="ss_divDropTargetLoading${ssEntryId}${ss_namespace}">
				<ssf:nlt tag="Loading"/>
			</div>
			<c:if test="<%= isIE %>">
			<object id="dropboxobj${ssEntryId}${ss_namespace}" classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" CLASS="dropbox" 
			  WIDTH = "20" HEIGHT = "20" NAME = "launcher" ALIGN = "middle" VSPACE = "0" HSPACE = "0" 
			  codebase="http://java.sun.com/update/1.5.0/jinstall-1_5-windows-i586.cab#Version=5,0,0,3">
			</c:if>
			<c:if test="<%= !isIE %>">
			<applet CODE = "com.sitescape.team.applets.droptarget.TopFrame" name="dropboxobj${ssEntryId}${ss_namespace}"
			  JAVA_CODEBASE = "<html:rootPath/>applets" 
			  ARCHIVE = "droptarget/ssf-droptarget-applet.jar" 
			  WIDTH = "20" HEIGHT = "20" MAYSCRIPT>
			</c:if>
			    <PARAM NAME="CODE" VALUE = "com.sitescape.team.applets.droptarget.TopFrame" />
			    <PARAM NAME ="CODEBASE" VALUE = "<html:rootPath/>applets" />
			    <PARAM NAME ="ARCHIVE" VALUE = "droptarget/ssf-droptarget-applet.jar" />
			    <PARAM NAME ="type" value="application/x-java-applet;version=1.5" />
			    <param name = "scriptable" value="true" />
			    <PARAM NAME = "NAME" VALUE = "droptarget" />
			    <PARAM NAME = "startingDir" VALUE=""/>
			    <PARAM NAME = "reloadFunctionName" VALUE="reloadUrlFromApplet${ssEntryId}${ss_namespace}"/>
			    <PARAM NAME = "bgcolorFunctionName" VALUE="getWindowBgColor${ssEntryId}${ss_namespace}"/>
			    <PARAM NAME = "onLoadFunction" VALUE="ss_hideLoadingDropTargetDiv${ssEntryId}${ss_namespace}" />
			    <PARAM NAME = "onCancelFunction" VALUE="ss_hideDropTarget${ssEntryId}${ss_namespace}" />
			    <PARAM NAME = "savePreviousVersions" VALUE="yes"/>
			    <PARAM NAME = "fileReceiverURL" VALUE="${ssAttachmentFileReceiverURL}" />
			    <PARAM NAME = "deactivationUrl" VALUE=""/>
			    <PARAM NAME = "displayUrl" VALUE="1"/>
			    <PARAM NAME = "loadDirectory" VALUE="yes" />
			    <PARAM NAME = "menuLabelPaste" VALUE="<ssf:nlt tag="binder.add.files.applet.menu.paste" />" />
			    <PARAM NAME = "menuLabelCancel" VALUE="<ssf:nlt tag="binder.add.files.applet.menu.cancel" />" />
			    <PARAM NAME = "menuLabelDeactivate" VALUE="<ssf:nlt tag="binder.add.files.applet.menu.deactivate" />" />
			    <PARAM NAME = "directoryLoadErrorMessage" value="<ssf:nlt tag="binder.add.files.applet.no.directory" />" />
			    <PARAM NAME = "noFileAlertMessage" value="<ssf:nlt tag="binder.add.files.applet.no.files.in.clipboard" />" />
			    <PARAM NAME = "fileUploadNotSupported" value="<ssf:nlt tag="binder.add.files.applet.upload.not.supported" />" />
			<c:if test="<%= !isIE %>">
			</applet>
			</c:if>
			<c:if test="<%= isIE %>">
			</object>
			</c:if>
		</td></tr>
		<tr>
			<td class="ss_entrySignature">
				<ssf:nlt tag="entry.dropboxAddAttachmentHelpText"/>
			</td>
		</tr>
	</table>
</body>