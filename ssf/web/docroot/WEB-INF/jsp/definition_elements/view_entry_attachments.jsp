<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
<% // View entry attachments %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.ssfs.util.SsfsUtil" %>
<%
	boolean presence_service_enabled = org.kablink.teaming.util.SPropsUtil.getBoolean("presence.service.enable", false);
	String webdavSuffix = org.kablink.teaming.util.SPropsUtil.getString("webdav.folder.url.suffix", "");
%>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${empty ss_tabDivCount}">
  <c:set var="ss_tabDivCount" value="0" scope="request"/>
</c:if>
<c:set var="ss_tabDivCount" value="${ss_tabDivCount + 1}" scope="request"/>
<script type="text/javascript">
ss_createOnLoadObj("ss_initThisTab${ss_tabDivCount}", 
		function() {ss_initTab('viewAttachments${ss_tabDivCount}', '${ss_tabDivCount}');});
</script>

<div class="ss_entryContent">
<div style="text-align: left; margin: 0px 10px; border: 0pt none;" 
  class="wg-tabs margintop3 marginbottom2">
  <table cellspacing="0" cellpadding="0" width="100%">
  <tr>
  <td valign="middle" width="1%" nowrap>
  <div id="viewAttachments${ss_tabDivCount}Tab" 
    class="wg-tab roundcornerSM on" 
    onMouseOver="ss_hoverOverTab('viewAttachments${ss_tabDivCount}', '${ss_tabDivCount}');"
    onMouseOut="ss_hoverOverTabStopped('viewAttachments${ss_tabDivCount}', '${ss_tabDivCount}');"
    onClick="ss_showTab('viewAttachments${ss_tabDivCount}', '${ss_tabDivCount}');">
    <ssf:nlt tag="__entry_attachments"/>
  </div>
  </td>
  <c:if test="${!empty ssDefinitionEntry.fileAttachments}">
  <td valign="middle" width="1%" nowrap>
  <div id="viewFileVersions${ss_tabDivCount}Tab" 
    class="wg-tab roundcornerSM" 
    onMouseOver="ss_hoverOverTab('viewFileVersions${ss_tabDivCount}', '${ss_tabDivCount}');"
    onMouseOut="ss_hoverOverTabStopped('viewFileVersions${ss_tabDivCount}', '${ss_tabDivCount}');"
    onClick="ss_showTab('viewFileVersions${ss_tabDivCount}', '${ss_tabDivCount}');">
    <ssf:nlt tag="__entry_file_versions"/>
  </div>
  </td>
  </c:if>
  </tr>
  </table>
</div>

<div id="attachmentsRegion${ss_tabDivCount}" class="wg-tab-content">
<div id="viewAttachments${ss_tabDivCount}Div" 
  style="display:block;"
>
  <c:set var="property_caption" value="" scope="request"/>
  <c:set var="ss_showPrimaryFileAttachmentOnly" value="true" scope="request"/>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachments_tab.jsp" />
</div>

<div id="viewFileVersions${ss_tabDivCount}Div" style="display:none;">
  <c:set var="property_caption" value="" scope="request"/>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_file_versions.jsp" />
</div>

</div>

</div>

