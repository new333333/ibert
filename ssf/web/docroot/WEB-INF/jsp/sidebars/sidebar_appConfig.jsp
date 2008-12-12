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
<%@ page import="org.kablink.teaming.ssfs.util.SsfsUtil, org.kablink.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<style>
	.ss_objlist_columnhead		{color: black; font-weight: bold; font-size: 0.75em; background-color: #edeeec; border-bottom: 1px solid black}
	.ss_objlist_footer			{background-color: #efeeec; margin-top: 1em; padding: 0.5em; border-top: 1px solid #babdb6;}
	.ss_objlist_instructions	{color: #4d6d8b; font-size: 0.8em}
	.ss_objlist_mediumtext		{color: black; font-size: 0.85em; line-height: 1.1em}
	.ss_objlist_smalltext		{color: black; font-size: 0.75em; line-height: 1em}
	.ss_objlist_tablehead		{color: white; font-weight: bold; font-size: 0.95em; background-color: #458ab9; text-align: left; text-indent: 0.2em; padding: 0.2em}
	.ss_objlist_top				{}
</style>

<script>
	var	g_appConfigStrings = new Array();
	g_appConfigStrings["sidebar.appConfig.Application.MSOffice"]		= "<ssf:nlt tag="sidebar.appConfig.Application.MSOffice"/>";
	g_appConfigStrings["sidebar.appConfig.Application.OpenOffice"]		= "<ssf:nlt tag="sidebar.appConfig.Application.OpenOffice"/>";
	g_appConfigStrings["sidebar.appConfig.Application.StarOffice"]		= "<ssf:nlt tag="sidebar.appConfig.Application.StarOffice"/>";
	g_appConfigStrings["sidebar.appConfig.Banner"]						= "<ssf:nlt tag="sidebar.appConfig.Banner"/>";
	g_appConfigStrings["sidebar.appConfig.Button.Cancel"]				= "<ssf:nlt tag="sidebar.appConfig.Button.Cancel"/>";
	g_appConfigStrings["sidebar.appConfig.Button.OK"]					= "<ssf:nlt tag="sidebar.appConfig.Button.OK"/>";
	g_appConfigStrings["sidebar.appConfig.Caption"]						= "<ssf:nlt tag="sidebar.appConfig.Caption"/>";
	g_appConfigStrings["sidebar.appConfig.Column.Application"]			= "<ssf:nlt tag="sidebar.appConfig.Column.Application"/>";
	g_appConfigStrings["sidebar.appConfig.Column.Extension"]			= "<ssf:nlt tag="sidebar.appConfig.Column.Extension"/>";
	g_appConfigStrings["sidebar.appConfig.Error.ApplicationMissing"]	= "<ssf:nlt tag="sidebar.appConfig.Error.ApplicationMissing"/>";
	g_appConfigStrings["sidebar.appConfig.Error.DuplicateExtension"]	= "<ssf:nlt tag="sidebar.appConfig.Error.DuplicateExtension"/>";
	g_appConfigStrings["sidebar.appConfig.Error.NoDelete"]				= "<ssf:nlt tag="sidebar.appConfig.Error.NoDelete"/>";
	g_appConfigStrings["sidebar.appConfig.Error.SelectAnExtension"]		= "<ssf:nlt tag="sidebar.appConfig.Error.SelectAnExtension"/>";
	g_appConfigStrings["sidebar.appConfig.Help"]						= "<ssf:nlt tag="sidebar.appConfig.Help"/>";
	g_appConfigStrings["sidebar.appConfig.Info"]						= "<ssf:nlt tag="sidebar.appConfig.Info"/>";
	g_appConfigStrings["sidebar.appConfig.Menu.Add"]					= "<ssf:nlt tag="sidebar.appConfig.Menu.Add"/>";
	g_appConfigStrings["sidebar.appConfig.Menu.Delete"]					= "<ssf:nlt tag="sidebar.appConfig.Menu.Delete"/>";
	g_appConfigStrings["sidebar.appConfig.Menu.Use"]					= "<ssf:nlt tag="sidebar.appConfig.Menu.Use"/>";
	g_appConfigStrings["sidebar.appConfig.Message.NoData"]				= "<ssf:nlt tag="sidebar.appConfig.Message.NoData"/>";
	g_appConfigStrings["sidebar.appConfig.SelectAnExtension"]			= "<ssf:nlt tag="sidebar.appConfig.SelectAnExtension"/>";

	var	g_appEditInPlaceExtensions = new Array();
	<%
		String[]	editInPlaceExtensions = SsfsUtil.getEditInPlaceExtensions(BrowserSniffer.is_ie(request));
		int			extensions            = ((null == editInPlaceExtensions) ? 0 : editInPlaceExtensions.length);
		for (int i = 0; i < extensions; i += 1) {
			%>g_appEditInPlaceExtensions[g_appEditInPlaceExtensions.length] = "<%= editInPlaceExtensions[i] %>";<%
		}
	%>
</script>

<a class="ss_sideLink" href="javascript: ;"
  onClick="ss_editAppConfig(); return false;" 
><ssf:nlt tag="sidebar.appConfig.Caption"/></a>

