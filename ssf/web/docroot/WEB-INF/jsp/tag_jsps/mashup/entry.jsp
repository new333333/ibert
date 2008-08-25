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
<% //table2_col1 top %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="mashupEntry" value="${ss_mashupEntries[mashup_value1]}"/>
<div style="margin:10px; padding:10px; " width="100%">
	<div style="border:1px solid #cecece; background-color:#e5e5e5; padding:6px;">
		<a href="<ssf:url action="view_folder_entry" 
		  folderId="${mashupEntry.parentBinder.id}"
		  entryId="${mashupEntry.id}">
		  <ssf:param name="newTab" value="1"/>
		  </ssf:url>"><span class="ss_largeprint ss_bold">${mashupEntry.title}</span></a>
	</div>
	<div style="border:1px solid #cecece;padding:6px;">
	    <ssf:markup type="view" entity="${mashupEntry}"><c:out 
	      value="${mashupEntry.description.text}" 
	      escapeXml="false"/></ssf:markup>
	</div>
</div>
