<%
// Return for requesting a wikilink popup
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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>

<c:set var="ss_servlet" value="true" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/view_css.jsp" %>

<title><ssf:nlt tag="wiki.link.popup.title"/></title>
	<script language="javascript" type="text/javascript" src="<html:rootPath/>js/tiny_mce/tiny_mce_popup.js"></script>
	<script language="javascript" type="text/javascript" src="<html:rootPath/>js/tiny_mce/plugins/ss_wikilink/jscripts/functions.js"></script>
	<base target="_self" />
</head>
<body onload="tinyMCEPopup.executeOnLoad('init();');" style="display: none">
<form>
<ssf:inlineHelp tag="ihelp.other.linksToEntries.explainTool"/> <ssf:nlt tag="wiki.link.tofolder"/>: <b><span id="linkToFolderName">(<ssf:nlt tag="wiki.link.currentfolder"/>)</span></b>
<input type="hidden" name="binderId" id="binderId" size="5" value="${binderId}"/>

<a href="javascript:;" onclick="ss_popup_folder();">[<ssf:nlt tag="button.change"/>]</a>
</p>
<p>
<b><ssf:nlt tag="wiki.link.topage"/>:</b> <input name="pageName" id="pageName" size="30"/>
<a href="javascript:;" onclick="ss_popup_page();">[<ssf:nlt tag="button.find"/>]</a>
</p>
</form>

<div id="folder_popup" style="position: absolute; display: none; background: #CCCCCC; top: 30px; padding: 10px;  border: 1px solid #333333;">
<div align="right" style="margin-top: -5px; margin-right: -5px;"><a href="javascript:;" onclick="ss_popup_folder();"><img border="0" src="<html:imagesPath/>pics/popup_close_box.gif" alt="x" title=""/></a></div>
<ssf:nlt tag="wiki.link.differentfolder"/>:
<br/>
<form method="post" name="ss_findLinkPlaceForm"
	action="">
 <ssf:find formName="ss_findLinkPlaceForm" 
    formElement="searchTitle" 
    type="places"
    width="350px" 
    binderId="${binderId}"
    searchSubFolders="false"
    foldersOnly="true"
    singleItem="true"
    clickRoutine="ss_loadLinkBinderId"
    accessibilityText="wiki.findFolder"
    /> 
<input type="hidden" name="searchTitle"/>
</form>
</div>
<div id="page_popup" style="position: absolute; display: none; background: #CCCCCC; top: 60px; padding: 10px; border: 1px solid #333333;">
<div align="right" style="margin-top: -5px; margin-right: -5px;"><a href="javascript:;" onclick="ss_popup_page();"><img border="0" src="<html:imagesPath/>pics/popup_close_box.gif" alt="x" title=""/></a></div>
<ssf:nlt tag="wiki.link.findpage"/>:
<form method="post" name="ss_findLinkEntryForm"
	action="">
 <ssf:find formName="ss_findLinkEntryForm"
    formElement="searchTitle" 
    type="entries"
    width="350px" 
    binderId="${binderId}"
    searchSubFolders="false"
    singleItem="true"
    clickRoutine="ss_loadLinkEntryId"
    accessibilityText="wiki.findPage"
    /> 
<input type="hidden" name="searchTitle"/>
</form>
</div>


<a href="javascript:;" onClick="ss_insertICElinkFromForm('${binderId}');"><ssf:nlt tag="button.insert"/></a>
&nbsp;&nbsp;<a href="javascript:;" onClick="ss_cancelICElinkEdit();"><ssf:nlt tag="button.cancel"/></a>
</p>
</body>
</html>
