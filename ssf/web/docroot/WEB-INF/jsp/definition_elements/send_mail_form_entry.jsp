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
<% // Send mail on submit %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${ssConfigJspStyle != 'mail'}">
<div class="ss_entryContent">
<div style="padding:15px 0px 15px 0px;">
<ssf:expandableArea title="${property_caption}">
<div class="ss_entryContent">
  <span class="ss_labelAbove"><ssf:nlt tag="entry.sendMail.toList" /></span>
  <ssf:find formName="${formName}" formElement="_sendMail_toList" type="user" />

<c:if test="${!empty ssFolder.teamMemberIds}">
  <input type="checkbox" name="_sendMail_toTeam" />
  <span class="ss_labelAfter"><label for="_sendMail_toTeam">
    <ssf:nlt tag="entry.sendMail.toTeam"/>
  </label></span>
  <br/>
</c:if>
  <br/>

  <span class="ss_labelAbove"><label for="_sendMail_subject">
    <ssf:nlt tag="entry.sendMail.subject"/>
  </label></span>
  <input type="text" size="80" name="_sendMail_subject" />
  <br/>

  <span class="ss_labelAbove"><label for="_sendMail_body">
    <ssf:nlt tag="entry.sendMail.body"/>
  </label></span>
  <ssf:htmleditor name="_sendMail_body" height="200" />

  <br/>
  <input type="checkbox" name="_sendMail_includeAttachments" />
  <span class="ss_labelAfter"><label for="_sendMail_includeAttachments">
    <ssf:nlt tag="entry.sendMail.includeAttachments"/>
  </label></span>
</div>
</ssf:expandableArea>
</div>
</div>
</c:if>
