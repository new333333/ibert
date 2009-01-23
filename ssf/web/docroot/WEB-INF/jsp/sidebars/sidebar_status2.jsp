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
<% // Status %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<%@ page import="org.kablink.util.PropertyNotFoundException" %>
<%@ page import="org.kablink.teaming.ObjectKeys" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="guestInternalId" value="<%= ObjectKeys.GUEST_USER_INTERNALID %>"/>
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />
<c:if test="${ssUser.internalId != guestInternalId}">
	<c:if test="${!empty ssUser.statusDate}">
	  <span style="float:right;"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
        value="${ssUser.statusDate}" type="both" 
	    timeStyle="short" dateStyle="short" /></span>
	</c:if>
	<a href="javascript: ;" onclick="ss_viewMiniBlog('${ssUser.id}', '0', true);return false;"
	   title="<ssf:nlt tag="miniblog.title"/>">
	   <span class="ss_status_header"><ssf:nlt tag="relevance.userStatus"/></span>
	</a>
	<ssf:ifLoggedIn>
		<script type="text/javascript">
		  ss_statusCurrent = "";
		  <c:if test="${!empty ssUser.status}">
		    ss_statusCurrent = "<%= java.net.URLEncoder.encode(ssUser.getStatus()) %>";
		  </c:if>
		</script>

		<label for="ss_status_textarea${renderResponse.namespace}"></label>

		<textarea cols="22" rows="2" id="ss_status_textarea${renderResponse.namespace}"
			wrap="virtual" class="ss_input_myStatus" 
  			onFocus="ss_setStatusBackground(this, 'focus');"
  			onKeyPress="ss_updateStatusSoon(this, event, <%= ObjectKeys.USER_STATUS_DATABASE_FIELD_LENGTH %>);"
  			onBlur="ss_setStatusBackground(this, 'blur')"
  			onMouseover="ss_setStatusBackground(this, 'mouseOver');"
  			onMouseout="ss_setStatusBackgroundCheck(this);"
  		><c:out value="${ssUser.status}" escapeXml="true"/></textarea>
  		
  		<div style="margin-left:4px;">
	  		<table cellspacing="0" cellpadding="0" width="164">
	  		  <tr>
	  		    <td>
		  		  <input type="button" value="<ssf:nlt tag="button.ok"/>" class="ss_inlineButtonSmall"
		  		    onclick="ss_updateStatusNowId('ss_status_textarea${renderResponse.namespace}');return false;"/>
	  		    </td>
		  		<td align="right">
		  		  <input type="button" value="<ssf:nlt tag="button.clear"/>" class="ss_inlineButtonSmall"
		  		    onclick="ss_clearStatus('ss_status_textarea${renderResponse.namespace}');return false;"/>
		  		</td>
	  		  </tr>
	  		</table>
	  	</div>
	</ssf:ifLoggedIn> 
</c:if>
