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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!ss_searchResultsPage}">
	<c:if test="${!empty ss_toolbar_subscribe_rss}">
	  <script type="text/javascript">
		//Add the rss feed info
		if (self.document.getElementById("ss_rssLink") == null) {
			var ss_linkEle = document.createElement("link");
			ss_linkEle.setAttribute("id", "ss_rssLink");
			ss_linkEle.setAttribute("rel", "alternate");
			ss_linkEle.setAttribute("type", "application/rss+xml");
			ss_linkEle.setAttribute("title", "RSS<c:if test="${!empty ssBinder.title}"> - ${ssBinder.title}</c:if>");
			ss_linkEle.setAttribute("href", "${ss_toolbar_subscribe_rss}");
			document.getElementsByTagName("head")[0].appendChild(ss_linkEle);
		}
	  </script>
		<div class="ss_leftNav ss_sidePlus">
		  <ul>
			<li>
				<a href="${ss_toolbar_subscribe_rss}" 
				  onclick="ss_showPermalink(this);return false;"><ssf:nlt tag="sidebar.rss"/></a>
			</li>
		  </ul>
		</div>
	</c:if>
</c:if>
