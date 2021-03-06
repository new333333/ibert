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
<% //Text view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>
<c:out value="${ssDefinitionEntry.customAttributes[property_name].value}" escapeXml="false"/>
</div>
<c:if test="${!empty ss_userVersionPrincipals}">
    <div class="ss_perUserViewElement">
    <ssf:expandableArea title='<%= NLT.get("element.perUser.viewPersonalVersions") %>' titleClass="ss_fineprint">
    <table cellspacing="0" cellpadding="0">
    <c:forEach var="perUserUser" items="${ss_userVersionPrincipals}">
      <c:set var="perUserPropertyName" value="${property_name}.${perUserUser.name}"/>
      <c:if test="${!empty ssDefinitionEntry.customAttributes[perUserPropertyName].value}">
      <tr>
      <td style="padding-left:10px;">
		<div class="ss_entryContent"><ssf:showUser user="${perUserUser}"/></div>
	  </td>
	  <td style="padding-left:10px;">
      <span class="${ss_element_display_style_item}"><c:out 
          value="${ssDefinitionEntry.customAttributes[perUserPropertyName].value}" 
          escapeXml="false"/></span>
      </td>
      </tr>
      </c:if>
    </c:forEach>
    </table>
    </ssf:expandableArea>
    </div>
</c:if>
</c:if>

<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
    <span class="${ss_element_display_style_caption}"><c:out value="${property_caption}" /></span>
  </td>
  <td valign="top" align="left">
    <span class="${ss_element_display_style_item}"><c:out 
      value="${ssDefinitionEntry.customAttributes[property_name].value}" 
      escapeXml="false"/></span>
  </td>
</tr>
<c:if test="${!empty ss_userVersionPrincipals}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
  </td>
  <td valign="top" align="left">
    <div class="ss_perUserViewElement">
    <ssf:expandableArea title='<%= NLT.get("element.perUser.viewPersonalVersions") %>' titleClass="ss_fineprint">
      <table cellspacing="0" cellpadding="0">
      <c:forEach var="perUserUser" items="${ss_userVersionPrincipals}">
        <c:set var="perUserPropertyName" value="${property_name}.${perUserUser.name}"/>
        <c:if test="${!empty ssDefinitionEntry.customAttributes[perUserPropertyName].value}">
        <tr>
        <td style="padding-left:10px;">
		  <ssf:showUser user="${perUserUser}"/>
	    </td>
	    <td style="padding-left:10px;">
          <span class="${ss_element_display_style_item}"><c:out 
            value="${ssDefinitionEntry.customAttributes[perUserPropertyName].value}" 
            escapeXml="false"/></span>
        </td>
        </tr>
        </c:if>
      </c:forEach>
    </table>
    </ssf:expandableArea>
    </div>
  </td>
</tr>
</c:if>
</c:if>
