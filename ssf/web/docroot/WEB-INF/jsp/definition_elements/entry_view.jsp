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
<% //View an entry %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />

<div class="ss_style ss_portlet">
<%
	String displayStyle = ssUser.getDisplayStyle();
	if (displayStyle == null || displayStyle.equals("")) {
		displayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
	}
	if (!displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME) && 
		!displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL) &&
		!displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
%>
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
<%
	}
%>
  <ssHelpSpot helpId="tools/entry_toolbar" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.entryToolbar"/>">
<ssf:toolbar toolbar="${ssFolderEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" />
<table cellspacing="0" cellpadding="0" width="100%">
<tr>
<td valign="top"><%@ include file="/WEB-INF/jsp/definition_elements/popular_view.jsp" %></td>
<td valign="top" align="right"><%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %></td>
</tr>
</table>

<ssf:ifnotadapter>
<% // Navigation links %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
<br/>
</ssf:ifnotadapter>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" 
  entry="${ssDefinitionEntry}" />
  
</div>
