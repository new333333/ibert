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
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // This is JSON type AJAX response with IOFrame  %>
{
	<c:choose>
		<c:when test="${ss_ajaxStatus.ss_ajaxNotLoggedIn}">
			notLoggedIn : ${ss_ajaxStatus.ss_ajaxNotLoggedIn} 
		</c:when>
		<c:when test="${!empty exception}">
			parseExceptionMsg : "<ssf:nlt tag="calendar.import.status.${exception}" />"
		</c:when>
		<c:otherwise>
			entriesAmountMsg: "<c:choose><%--
				--%><c:when test="${entriesAddedAmount == 0 && entriesModifiedAmount == 0}"><%--
					--%><ssf:nlt tag="calendar.import.added.status.message.zero" />\n<%--
				--%></c:when><%--
				--%><c:otherwise><%--
					--%><c:if test="${entriesAddedAmount == 1}"><%--
						--%><ssf:nlt tag="calendar.import.added.status.message.single" />\n<%--
					--%></c:if><%--
					--%><c:if test="${entriesAddedAmount > 1}"><%--
						--%><ssf:nlt tag="calendar.import.added.status.message.plural"><%--
							--%><ssf:param name="value" value="${entriesAddedAmount}"/><%--
						--%></ssf:nlt>\n<%--
					--%></c:if><%--
					--%><c:if test="${entriesModifiedAmount == 1}"><%--
						--%><ssf:nlt tag="calendar.import.modified.status.message.single" />\n<%--
					--%></c:if><%--
					--%><c:if test="${entriesModifiedAmount > 1}"><%--
						--%><ssf:nlt tag="calendar.import.modified.status.message.plural"><%--
							--%><ssf:param name="value" value="${entriesModifiedAmount}"/><%--
						--%></ssf:nlt><%--
					--%></c:if><%--	
				--%></c:otherwise><%--
			--%></c:choose>",					
			entryAddedIds: [<c:forEach var="id" items="${entryAddedIds}" varStatus="status">
					"${id}"<c:if test="${!status.last}">,</c:if>
				</c:forEach>],
			entryModifiedIds: [<c:forEach var="id" items="${entryModifiedIds}" varStatus="status">
					"${id}"<c:if test="${!status.last}">,</c:if>
				</c:forEach>]				
		</c:otherwise>
	</c:choose>
}