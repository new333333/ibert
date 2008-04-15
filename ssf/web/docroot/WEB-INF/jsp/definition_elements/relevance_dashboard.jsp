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
<% //Relevance dashboard %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<ssf:ifLoggedIn>
<script type="text/javascript">
var ss_relevanceAjaxUrl${renderResponse.namespace} = "<ssf:url adapter="true" portletName="ss_forum" 
		action="__ajax_relevance" actionUrl="false"><ssf:param 
		name="operation" value="get_relevance_dashboard" /><ssf:param 
		name="type" value="ss_typePlaceHolder" /><ssf:param 
		name="binderId" value="ss_binderIdPlaceHolder" /><ssf:param 
		name="namespace" value="${renderResponse.namespace}" /><ssf:param 
		name="rn" value="ss_rnPlaceHolder" /></ssf:url>";
</script>

<% //Tabs %>

<div id="ss_wrap" align="center">
<div id="ss_tabsC" sytle="margin-top:10px;">
  <ul>
	<!-- CSS Tabs -->
	<li class="ss_tabsCCurrent"><a id="ss_relevanceProfileTab${renderResponse.namespace}" href="javascript: ;"
		onClick="ss_selectRelevanceTab(this, 'profile', '${ssBinder.id}', '${renderResponse.namespace}');return false;"
		><span><ssf:nlt tag="relevance.tab.profile"/></span></a></li>
	<li><a href="javascript: ;"
		onClick="ss_selectRelevanceTab(this, 'whats_new', '${ssBinder.id}', '${renderResponse.namespace}');return false;"
		><span><ssf:nlt tag="relevance.tab.whatsNew"/></span></a></li>
	<li><a href="javascript: ;"
		onClick="ss_selectRelevanceTab(this, 'tasks_and_calendars', '${ssBinder.id}', '${renderResponse.namespace}');return false;"
		><span><ssf:nlt tag="relevance.tab.tasksAndCalendars"/></span></a></li>
	<li><a href="javascript: ;"
		onClick="ss_selectRelevanceTab(this, 'activities', '${ssBinder.id}', '${renderResponse.namespace}');return false;"
		><span><ssf:nlt tag="relevance.tab.activities"/></span></a></li>
	<li><a href="javascript: ;"
		onClick="ss_selectRelevanceTab(this, 'viewed_entries', '${ssBinder.id}', '${renderResponse.namespace}');return false;"
		><span><ssf:nlt tag="relevance.tab.viewedEntries"/></span></a></li>
	<li><a href="javascript: ;"
		onClick="ss_selectRelevanceTab(this, 'hiddenDashboard', '${ssBinder.id}', '${renderResponse.namespace}');return false;"
		><span><ssf:nlt tag="relevance.tab.hide"/></span></a></li>
  </ul>
</div>
<div class="ss_clear_float"></div>
<script type="text/javascript">
var ss_relevanceTabCurrent_${renderResponse.namespace} = self.document.getElementById('ss_relevanceProfileTab${renderResponse.namespace}');
</script>

<% //Changeable tab canvas; this gets replaced when a tab is clicked %>

<div id="relevanceCanvas_${renderResponse.namespace}" style="margin:4px 10px 10px 10px;">
<c:set var="ssRelevanceDashboardConfigElement" value="${item}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/profile.jsp" />
</div>
</div>
</ssf:ifLoggedIn>
