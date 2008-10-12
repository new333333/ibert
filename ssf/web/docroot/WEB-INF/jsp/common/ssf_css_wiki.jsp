<%
/* *
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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>

/**********************************/
/**********************************/
/** CSS DOCUMENT FOR WIKIs **/
/**********************************/
/**********************************/

/* Wiki */

div.ss_wiki_content {
	padding-bottom: 30px;
}
div.ss_wiki_sidebar {
    padding-left:5px;
 	padding-right: 5px;
    padding-top: 10px;
    padding-bottom: 10px;
    background-color:${ss_style_background_color_side_panel_featured};
}
.ss_wiki_sidebar_subhead {
	font-weight: bold;
	font-style: italic;
	font-size: ${ss_style_font_largeprint} !important;
	color: #0066CC !important; /* ${ss_style_muted_label_color}; */
	border-bottom: 1px solid #0066CC;
	padding-top: 2px;
	padding-bottom: 2px;
}
div.ss_wiki_sidebar_box {
/*	background-color: #FFFFFF;
	border: 1px solid ${ss_blog_sidebar_box_outline}; */
	margin-bottom: 10px;
	margin-top: 2px;
	padding: 2px 5px;
}
.ss_wiki_sidebar table {
	background-color: transparent;
}
