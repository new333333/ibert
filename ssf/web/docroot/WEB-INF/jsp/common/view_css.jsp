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
 
%><%--

--%><c:if test="${!empty ss_portletInitialization}"><%--
	--%><script type="text/javascript">
var url = '${ss_portletInitializationUrl}';
if (url != '') self.location.href = url;
</script><%--
--%></c:if><%--

--%><c:if test="${empty ss_portletInitialization}"><%--
	--%>
	<c:if test="${empty ssf_support_files_loaded}"><%--
		--%><c:set var="ssf_support_files_loaded" value="1" scope="request"/><%--
		--%>
<script type="text/javascript">
var ss_isAdapter="true";
<c:if test="${empty ss_notAdapter}">
<ssf:ifnotadapter>
ss_isAdapter="false";
<c:if test="${empty ss_portletType || ss_portletType != 'ss_portletTypeAdmin'}">
var ss_tagSearchResultUrl = "<portlet:actionURL windowState="maximized" 
	portletMode="view"><portlet:param 
	name="action" value="advanced_search"/><portlet:param 
	name="searchTags" value="ss_tagPlaceHolder"/><portlet:param 
	name="operation" value="ss_searchResults"/><portlet:param 
	name="tabTitle" value="ss_tagPlaceHolder"/><portlet:param 
	name="newTab" value="1"/><portlet:param 
	name="searchItemType" value="workspace"/><portlet:param 
	name="searchItemType" value="folder"/><portlet:param 
	name="searchItemType" value="user"/><portlet:param 
	name="searchItemType" value="entry"/><portlet:param 
	name="searchItemType" value="reply"/></portlet:actionURL>";
var ss_baseEntryUrl = '<portlet:renderURL windowState="maximized"><portlet:param 
	name="action" value="ssActionPlaceHolder"/><portlet:param 
	name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
	name="entryId" value="ssEntryIdPlaceHolder"/><portlet:param 
	name="newTab" value="ssNewTabPlaceHolder"/></portlet:renderURL>';
var ss_baseEntryUrl<portlet:namespace/> = ss_baseEntryUrl;
	
var ss_baseBinderUrl = '<portlet:renderURL windowState="maximized"><portlet:param 
	name="action" value="ssActionPlaceHolder"/><portlet:param 
	name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
	name="newTab" value="ssNewTabPlaceHolder"/></portlet:renderURL>';
var ss_baseBinderUrl<portlet:namespace/> = ss_baseBinderUrl;

</c:if>

</ssf:ifnotadapter>
</c:if>
</script>
		<%
			boolean isIE = com.sitescape.util.BrowserSniffer.is_ie(request);
			%><%--

		--%>
<script type="text/javascript">
// Dojo configuration
djConfig = { 
	isDebug: false,
<c:choose><%--
    --%><c:when test="${ssUser.locale == 'da_DK'}">locale: 'da',</c:when><%--
    --%><c:when test="${ssUser.locale == 'de_DE'}">locale: 'de',</c:when><%--
    --%><c:when test="${ssUser.locale == 'es_ES'}">locale: 'es',</c:when><%--
    --%><c:when test="${ssUser.locale == 'fr_FR'}">locale: 'fr',</c:when><%--
    --%><c:when test="${ssUser.locale == 'it_IT'}">locale: 'it',</c:when><%--
    --%><c:when test="${ssUser.locale == 'ja_JP'}">locale: 'ja',</c:when><%--
    --%><c:when test="${ssUser.locale == 'nl_NL'}">locale: 'nl',</c:when><%--
    --%><c:when test="${ssUser.locale == 'pl_PL'}">locale: 'pl',</c:when><%--
    --%><c:when test="${ssUser.locale == 'pt_BR'}">locale: 'pt-br',</c:when><%--
    --%><c:when test="${ssUser.locale == 'sv_SE'}">locale: 'sv',</c:when><%--
    --%><c:when test="${ssUser.locale == 'sv_SV'}">locale: 'sv',</c:when><%--
    --%><c:when test="${ssUser.locale == 'zh_CN'}">locale: 'zh-cn',</c:when><%--
    --%><c:when test="${ssUser.locale == 'zh_TW'}">locale: 'zh-tw',</c:when><%--
    --%><c:otherwise>locale: 'en',</c:otherwise><%--
--%></c:choose>
	parseWidgets: false, 
	searchIds: []
};
</script>
<script type="text/javascript" src="<html:rootPath/>js/dojo/dojo.js"></script>
<script type="text/javascript">
var ss_scripts_loaded = "no";
var scripts = document.getElementsByTagName("script");
for (var i = 0; i < scripts.length; i++) {
	if (scripts[i].src) {
		if (scripts[i].src.indexOf("/ss_common.js") >= 0) {
			ss_scripts_loaded = "yes";
			break;
		}
	}
}
var undefined;
var ss_urlBase;
var ss_rootPath;
var ss_imagesPath;
var ss_1pix;
var ss_forumCssUrl;
var ss_forumColorsCssUrl;
var ss_not_logged_in;
var ss_rtc_not_configured;
var ss_userDisplayStyle;

var ss_findButtonClose;
var ss_AjaxBaseUrl;
var ss_validationErrorMessage;
function ss_loadJsFile(rootPath, jsFile) {
	var spath = rootPath + jsFile;
	var scripts = document.getElementsByTagName("script");
	for (var i = 0; i < scripts.length; i++) {
		if (scripts[i].src && scripts[i].src == spath) return;
	}
	try {
		document.writeln("<scr"+"ipt type='text/javascript' src='"+spath+"'></scr"+"ipt>");
	} catch (e) {
		var script = document.createElement("script");
		script.src = spath;
		document.getElementsByTagName("head")[0].appendChild(script);
	}
}
function ss_loadDojoFiles() {
	if (ss_scripts_loaded && ss_scripts_loaded == "no") {
		dojo.require("dojo.html.*");
		dojo.require("dojo.lfx.*");
		dojo.require("dojo.event.*");
		dojo.require("dojo.lang.*");
		dojo.require("dojo.dnd.*");
	}
}
if (ss_scripts_loaded && ss_scripts_loaded == "no") {
	ss_urlBase = self.location.protocol + "//" + self.location.host;
	ss_rootPath = "<html:rootPath/>";
	ss_imagesPath = "<html:imagesPath/>";
	
	ss_forumCssUrl = ss_urlBase + ss_rootPath + "css/forum.css";
	ss_1pix = ss_imagesPath + "pics/1pix.gif";
	ss_forumColorsCssUrl = "<ssf:url webPath="viewCss">
	    <ssf:param name="theme" value="${ssUser.theme}"/>
	    </ssf:url>";

	ss_AjaxBaseUrl = "<ssf:url adapter="true" portletName="ss_forum" actionUrl="true" />";

	//Not logged in message
	ss_not_logged_in = "<ssf:nlt tag="general.notLoggedIn"/>";
	
	// RTC client not installed
	ss_rtc_not_configured = "<ssf:nlt tag="rtc.client.not.configured"/>";
	
	//Clipboard text
	ss_clipboardTitleText = "<ssf:nlt tag="clipboard.title"/>";
	ss_addContributesToClipboardText = "<ssf:nlt tag="button.add_contributes_to_clipboard"/>";
	ss_addTeamMembersToClipboardText = "<ssf:nlt tag="button.sdd_team_members_to_clipboard"/>";
	ss_clearClipboardText = "<ssf:nlt tag="button.clear_clipboard"/>";
	ss_noUsersOnClipboardText = "<ssf:nlt tag="clipboard.noUsers"/>";
	ss_closeButtonText = "<ssf:nlt tag="button.close"/>";
	ss_selectAllBtnText = "<ssf:nlt tag="button.selectAll"/>";
	ss_clearAllBtnText = "<ssf:nlt tag="button.clearAll"/>";
	ss_userDisplayStyle = "${ssUser.displayStyle}";
	<c:if test="${empty ssUser.displayStyle || ssUser.displayStyle == ''}">
	ss_userDisplayStyle = "iframe";	
	</c:if>
									
	ss_findButtonClose = "<ssf:nlt tag="button.close"/>";
	ss_validationErrorMessage = "<ssf:nlt tag="validation.errorMessage"/>";
	
	ss_loadJsFile(ss_rootPath, "js/common/ss_common.js");
	ss_loadJsFile(ss_rootPath, "js/common/taconite-client.js");
	ss_loadJsFile(ss_rootPath, "js/common/taconite-parser.js");
	ss_loadJsFile(ss_rootPath, "js/common/ss_dashboard_drag_and_drop.js");
}


function ss_createStyleSheet(url, title, enabled) {
	if (ss_scripts_loaded && ss_scripts_loaded == "no") {
		if (enabled == null || enabled == "") enabled = false;
		var styles = "@import url('" + " " + url + " " + "');";
		var newSS = document.createElement('link');
		newSS.rel = 'stylesheet';
		if (title != null && title != "") {
			newSS.setAttribute("title", title);
			newSS.disabled = true;
			if (enabled == true) {
				newSS.disabled = false;
			}
		}
		newSS.href = 'data:text/css,' + escape(styles);
		//newSS.href = url;
		document.getElementsByTagName("head")[0].appendChild(newSS);
	}
}

</script>

<c:if test="${!empty ss_servlet && ss_servlet == 'true'}">
<link href="<html:rootPath/>css/forum.css" rel="stylesheet" type="text/css" />
<link href="<ssf:url  webPath="viewCss"> <ssf:param name="theme" value=""/>
	    </ssf:url>" rel="stylesheet" type="text/css" />
</c:if>

<script type="text/javascript">
<c:if test="${empty ss_servlet || ss_servlet != 'true'}">
	if (ss_scripts_loaded && ss_scripts_loaded == "no") {
		if (document.createStyleSheet) {
			document.createStyleSheet(ss_forumCssUrl);
			document.createStyleSheet(ss_forumColorsCssUrl);
		} else {
			ss_createStyleSheet(ss_forumCssUrl);
			ss_createStyleSheet(ss_forumColorsCssUrl);
		}
	}
</c:if>

<c:set var="ss_loadCssStylesInline" value="true" scope="request"/>
<c:set var="ss_skipCssStyles" value="true" scope="request"/>
<jsp:include page="/WEB-INF/jsp/common/ssf_css.jsp" />

function ss_defineColorValues() {
	ss_style_background_color = '${ss_style_background_color}';
	ss_dashboard_table_border_color = '${ss_dashboard_table_border_color}';
}

if (ss_scripts_loaded && ss_scripts_loaded == "no") {
	ss_createOnLoadObj('ss_loadDojoFiles', ss_loadDojoFiles);
	ss_createOnLoadObj('ss_defineColorValues', ss_defineColorValues);
}
</script>

<c:if test="${!empty ss_accessibleUrl && (empty ss_displayStyle || ss_displayStyle != 'accessible')}">
  <a href="${ss_accessibleUrl}"><img border="0"
    <ssf:alt tag="accessible.enableAccessibleMode"/> 
    src="<html:imagesPath/>pics/1pix.gif" /></a><%--
		--%></c:if><%--
	--%></c:if><%--
--%></c:if>