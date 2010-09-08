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
<c:set var="tag" value="toolbar.menu.modify_workspace"/>
<c:if test="${ssBinder.entityType == 'folder'}">
  <c:set var="tag" value="toolbar.menu.modify_folder"/>
</c:if>
<jsp:useBean id="tag" type="String" />
<c:set var="ss_windowTitle" value='<%= NLT.get(tag) %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<body class="ss_style_body tundra">

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js?<%= org.kablink.teaming.util.ReleaseInfo.getContentVersion() %>"></script>
<jsp:useBean id="ssConfigElement" type="org.dom4j.Element" scope="request" />
<c:set var="ssBinderMarker" value="true" scope="request"/>
<c:if test="${!empty ssException}">
<span class="ss_formError"><ssf:nlt tag="administration.errors"/> (<c:out value="${ssException}"/>)</span></br>
</c:if>

<div class="ss_portlet"> 

<% // Show the workspace according to its definition %>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  processThisItem="true"
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}" 
  entry="${ssBinder}"/>
  
</div>

</body>
</html>
