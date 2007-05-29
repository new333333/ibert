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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_style" align="left">
<%@ include file="/WEB-INF/jsp/help/hide_help_panel_button.jsp" %>

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.myTeams"/></span>

<p><ssf:nlt tag="help.myTeams.intro" /></p>

<p><ssf:nlt tag="help.myTeams.teamsDefined" /></p>

<div style="margin-left:25px;">

<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/product_intro', 'ss_moreinfo_panel');"><ssf:nlt tag="help.getStartedProduct.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a></p>

</div>

</div>
