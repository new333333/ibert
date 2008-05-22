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
<% // Discussion Workspace binder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<c:forEach var="binder" items="${ss_binders}">
  <c:if test="${binder._entityType == 'workspace' || binder._entityType == 'profiles'}">
    <div style="padding-left:10px;">
    <c:if test="${binder._entityType == 'workspace'}"><span style="padding-right:10px;">workspace</span></c:if>
    <c:if test="${binder._entityType == 'profiles'}"><span style="padding-right:10px;">profiles</span></c:if>
    <span>(${ss_binderUnseenCounts[binder._docId].count})</span> 
    <a 
    <c:if test="${binder._entityType == 'workspace'}">
      href="<ssf:url 
        action="view_ws_listing"
        binderId="${binder._docId}"/>"
	</c:if>
	<c:if test="${binder._entityType == 'profiles'}">
      href="<ssf:url 
        action="view_ws_listing"
        binderId="${binder._docId}"/>"
    </c:if>
    >
      <span>${binder.title}</span>
    </a>
    <br/>
    </div>
    
    <c:forEach var="subBinder" items="${ss_bindersSubBinders[binder._docId]}">
      <c:if test="${subBinder._entityType == 'workspace'}">
        <div style="padding-left:25px;">
        <span style="padding-right:10px;">workspace</span>
        <span>(${ss_binderUnseenCounts[subBinder._docId].count})</span> 
        <a href="<ssf:url action="view_ws_listing" binderId="${subBinder._docId}"/>">
          <span>${subBinder.title}</span>
        </a>
        <br/>
        </div>
      </c:if>
      <c:if test="${subBinder._entityType == 'profiles'}">
        <div style="padding-left:25px;">
        <span style="padding-right:10px;">profiles</span>
        <span>(${ss_binderUnseenCounts[subBinder._docId].count})</span> 
        <a href="<ssf:url action="view_profiles_listing" binderId="${subBinder._docId}"/>">
          <span>${subBinder.title}</span>
        </a>
        <br/>
        </div>
      </c:if>
      <c:if test="${subBinder._entityType == 'folder'}">
        <div style="padding-left:25px;">
        <span style="padding-right:10px;">folder</span>
        <span>(${ss_binderUnseenCounts[subBinder._docId].count})</span> 
        <a href="<ssf:url action="view_folder_listing" binderId="${subBinder._docId}"/>">
          <span>${subBinder.title}</span>
        </a>
        <br/>
        </div>
      </c:if>
    </c:forEach>
  </c:if>
  
  <c:if test="${binder._entityType == 'folder'}">
    <div style="padding-left:10px;">
    <span style="padding-right:10px;">folder</span>
    <span>(${ss_binderUnseenCounts[binder._docId].count})</span> 
    <a href="<ssf:url action="view_folder_listing" binderId="${binder._docId}"/>">
      <span>${binder.title}</span>
    </a>
    <br/>
    </div>
  </c:if>
  
  <br/>
</c:forEach>