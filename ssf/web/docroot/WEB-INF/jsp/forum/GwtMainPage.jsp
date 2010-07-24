<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value="${ssProductName}" scope="request"/>
<c:set var="ss_skip_head_close" value="true" scope="request"/>
<c:set var="ss_GWT_main_page" value="true" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/common/initializeGWT.jsp"     %>
<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_appConfig.jsp" /> 

	<script type="text/javascript" src="<html:tinyMcePath/>tiny_mce.js"></script>
	
	<script type="text/javascript" language="javascript">
		// Save away information such as the binder id and the adapted url for the request we are working with.
		// Through an overlay we will access m_requestInfo from java.
		var m_requestInfo = {
			binderId : '${binderId}',
			userName : '<ssf:escapeJavaScript>${userFullName}</ssf:escapeJavaScript>',
			adaptedUrl : '${adaptedUrl}',
			imagesPath : '<ssf:escapeJavaScript><html:imagesPath/></ssf:escapeJavaScript>',
			myWSUrl : '${myWorkspaceUrl}',
			errMsg : '${errMsg}',
			isNovellTeaming : ${isNovellTeaming},
			language : '${ssUser.locale.language}',
			loginRefererUrl : '${ssUrl}',
			loginPostUrl : '<ssf:escapeJavaScript>${ss_loginPostUrl}</ssf:escapeJavaScript>',
			isUserLoggedIn : ${isUserLoggedIn},
			promptForLogin : ${promptForLogin},
			forceSidebarReload : false,
			loginError : '<ssf:escapeJavaScript>${ss_loginError}</ssf:escapeJavaScript>',
			contentCss : '<ssf:url webPath="viewCss"><ssf:param name="sheet" value="editor"/></ssf:url>',
			simpleSearchUrl: '<ssf:escapeJavaScript><ssf:url action="advanced_search" actionUrl="true"><ssf:param name="newTab" value="1"/><ssf:param name="quickSearch" value="true"/><ssf:param name="operation" value="ss_searchResults"/></ssf:url></ssf:escapeJavaScript>',
			advancedSearchUrl: '<ssf:escapeJavaScript><ssf:url action="advanced_search" actionUrl="true" windowState="maximized"><ssf:param name="action" value="advancedSearch"/><ssf:param name="tabTitle" value="SEARCH FORM"/><ssf:param name="newTab" value="0"/></ssf:url></ssf:escapeJavaScript>',
			teamingFeedUrl : '<ssf:escapeJavaScript><ssf:url adapter="true" portletName="ss_forum" action="__ajax_mobile" operation="view_teaming_live" actionUrl="false" /></ssf:escapeJavaScript>',
			savedSearchUrl: '<ssf:escapeJavaScript><ssf:url action="advanced_search" actionUrl="true"><ssf:param name="newTab" value="1"/><ssf:param name="operation" value="ss_savedQuery"/></ssf:url></ssf:escapeJavaScript>',
			recentPlaceSearchUrl: '<ssf:escapeJavaScript><ssf:url action="advanced_search" actionUrl="true"><ssf:param name="operation" value="viewPage"/></ssf:url></ssf:escapeJavaScript>',
			topWSId: '${topWSId}'
		};

		var ss_workareaIframeMinOffset = 12;
		function ss_setWorkareaIframeSize() {
			//If possible, try to directly set the size of the iframe
			//This may fail if the iframe is showing something in another domain
			//If so, the alternate method (via ss_communicationFrame) is used to set the window height
			try {
				var iframeDiv = document.getElementById('contentControl')
				var startOfContent = ss_getObjectTop(iframeDiv);
				var windowHeight = ss_getWindowHeight();
				var iframeMinimum = parseInt(windowHeight - startOfContent - ss_workareaIframeMinOffset);
				if (iframeMinimum < 100) iframeMinimum = 100;
				if (window.frames['gwtContentIframe'] != null) {
					var iframeHeight = window.gwtContentIframe.document.body.scrollHeight;
					if (parseInt(iframeDiv.style.height) != parseInt(iframeMinimum)) {
						iframeDiv.style.height = parseInt(iframeMinimum) + "px";
					}
				}
				//Also resize the entry iframe if needed
				ss_setEntryPopupIframeSize();
			} catch(e) {
				//alert('Error during frame resizing: ' + e)
			}
		}

		//Routine to set the size and position of the entry popup frame in the "newpage" mode
		var ss_entryPopupBottomMargin = 46;
		function ss_setEntryPopupIframeSize() {
			if (ss_isGwtUIActive && ss_getUserDisplayStyle() == "newpage") {
				try {
					var contentIframe = document.getElementById('contentControl');
					var startOfContent = ss_getObjectTop(contentIframe);
					var entryIframeDiv = document.getElementById('ss_showentrydiv');
					var entryIframeFrame = document.getElementById('ss_showentryframe');
					if (entryIframeDiv == null || entryIframeFrame == null) return;
					var top = ss_getObjectTop(contentIframe);
					var left = ss_getObjectLeft(contentIframe);
					ss_setObjectTop(entryIframeDiv, top);
					ss_setObjectLeft(entryIframeDiv, left);
					ss_setObjectWidth(entryIframeFrame, contentIframe.style.width);
					var windowHeight = parseInt(ss_getWindowHeight());
					var iframeMinimum = parseInt(windowHeight - startOfContent - ss_entryPopupBottomMargin);
					if (iframeMinimum < 100) iframeMinimum = 100;
					if (window.frames['ss_showentryframe'] != null) {
						if (parseInt(entryIframeFrame.style.height) != parseInt(iframeMinimum)) {
							//alert(entryIframeFrame.style.height + ", set to: " + iframeMinimum)
							entryIframeFrame.style.height = parseInt(iframeMinimum) + "px";
						}
					}
				} catch(e) {
					//alert('Error during frame resizing: ' + e)
				}
			} else {
				ss_setCurrentIframeHeight();
			}
		}

		//Routine to hide the content frame if necessary
		function ss_hideContentFrame() {
			if (ss_isGwtUIActive && ss_getUserDisplayStyle() == "newpage") {
				var contentIframe = document.getElementById('contentControl');
				if (contentIframe != null) {
					contentIframe.style.visibility = "hidden";
				}
			}
		}

		// ss_wikiLinkUrl is used with the tinyMCE editor plugin that lets you insert a link to a Teaming page.
		var ss_wikiLinkUrl = "<ssf:url adapter="true" actionUrl="true" portletName="ss_forum" action="__ajax_request">
			  					<ssf:param name="operation" value="wikilink_form" />
			  					<ssf:param name="binderId" value="${wikiLinkBinderId}" />
		    				   </ssf:url>";

		// ss_youTubeUrl and ss_invalidYouTubeUrl are used with the tinyMCE editor plugin that lets you add a youtube video.
		var ss_youTubeUrl = "<ssf:url adapter="true" actionUrl="true" portletName="ss_forum" action="__ajax_request">
			  					<ssf:param name="operation" value="youtube_form" />
		    				  </ssf:url>";
		var ss_invalidYouTubeUrl = "<%= NLT.get("__youTubeInvalidUrl").replaceAll("\"", "\\\\\"") %>";

		// The following variables are used by the tinyMCE editor plugin that lets you
		// upload an image and insert it into the tinyMCE editor.
		var ss_imageUploadError1 = "<ssf:nlt tag="imageUpload.badFile"/>"
		var ss_imageUploadUrl = "<ssf:url adapter="true" actionUrl="true" portletName="ss_forum" action="__ajax_request">
			  						<ssf:param name="operation" value="upload_image_file" />
		    					 </ssf:url>";

		/*
		 * Implementation method for GwtClientHelper.jsEvalString().
		 *
		 * Note:  The code contained here was originally inside that
		 *    native method but GWT's obfuscation used for our
		 *    production compile broke it.
		 */
		function jsEvalStringImpl(url, jsString) {
			// Setup an object to pass through the URL...
			var hrefObj = {href: url};
			
			// ...patch the JavaScript string...
			jsString = jsString.replace("this", "hrefObj");
			jsString = jsString.replace("return false;", "");
			jsString = ("window.top.gwtContentIframe." + jsString);
			
			// ...and evaluate it.
			eval(jsString);
		}
	</script>
	<script type="text/javascript" src="<html:rootPath/>js/common/ss_common.js"></script>
	<script type="text/javascript" src="<html:rootPath/>js/forum/view_iframe.js"></script>
	<script type="text/javascript" language="javascript" src="<html:rootPath />js/gwt/gwtteaming/gwtteaming.nocache.js"></script>
  </head>

  <body>
    <c:set var="gwtUI" value="true" scope="request"/>

    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>

	<!-- This div will hold the content of the main Teaming page. -->
	<div id="gwtMainPageDiv">
	</div>

    <%@ include file="/WEB-INF/jsp/dashboard/portletsupport.jsp" %>
  </body>
</html>
