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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<%@ page import="java.util.Date" %>
<%@ page import="org.kablink.teaming.util.CalendarHelper" %>

<div id="date_select_div">
	<c:set var="initDate" value="<%= new Date() %>"/>
	<c:if test="${!empty propertyValue}">
		<c:set var="initDateTime" value="${propertyValue}"/>
		<jsp:useBean id="initDate" type="java.util.Date" />
		<jsp:useBean id="initDateTime" type="java.lang.String" />
		<%
			long endTime = Long.valueOf(initDateTime);
			initDate.setTime(endTime);
		%>
	</c:if>
	
	<table border="0">
	  <tr>
	    <th><ssf:nlt tag="__transition_on_date_date"/></th>
	    <th><ssf:nlt tag="__transition_on_date_time"/></th>
	  </tr>
	  <tr>
	    <td>
			<input type="text" 
				name="${propertyId}_date" 
				value="<fmt:formatDate value="${initDate}" pattern="yyyy-MM-dd" timeZone="${ssUser.timeZone.ID}"/>"
			/>
	    </td>
	    <td>
			<input type="text" 
				name="${propertyId}_time" 
				value="<fmt:formatDate value="${initDate}" pattern="HH:mm" timeZone="${ssUser.timeZone.ID}"/>"
			/>
	    </td>
	  </tr>
	</table>

</div>




