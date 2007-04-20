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

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.maximizeButton"/></span>  (1/1)

<p><ssf:nlt tag="help.maximizeicon.content.intro"/></p>

<p><ssf:nlt tag="help.maximizeicon.content.normalIcon"/></p>

<p style="text-align:center;"><a href="javascript: ss_toggleShowHidePortal(this);"><ssf:nlt tag="help.maximizeicon.content.toggleMaximize"/></a></p>

</div>
<script type="text/javascript">
ss_helpSystem.highlight('ss_navbarHideShowPortalButton');
</script>

<br/>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showPreviousHelpSpot();">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showNextHelpSpot();"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
