<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.lang.Boolean" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<%
	Map percentStatistic = (Map) request.getAttribute("percentStatistic"); 
	String statisticLabel = (String) request.getAttribute("statisticLabel"); 
	Boolean showLabel = (Boolean) request.getAttribute("showLabel");
	Boolean labelAll = (Boolean) request.getAttribute("labelAll");
	Boolean showLegend = (Boolean) request.getAttribute("showLegend");
	String barStyle = (String) request.getAttribute("barStyle"); 
%>
<c:set var="percentStatistic" value="<%= percentStatistic%>" scope="request"/>
<c:set var="showLegend" value="<%= showLegend%>" scope="request"/>
<c:set var="showLabel" value="<%= showLabel%>" scope="request"/>
<c:set var="barStyle" value="<%= barStyle%>" scope="request"/>
<c:set var="labelAll" value="<%= labelAll%>" scope="request"/>

<c:if test="${!empty percentStatistic}">
	
	<c:if test="${showLabel}">
		<h5 class="ss_statisticLabel"><ssf:nlt tag="<%=statisticLabel%>"/><c:if test="${labelAll}"> (<ssf:nlt tag="alt.viewAll"/>)</c:if>:</h5>
	</c:if>
	
	<table class="ss_statisticContainer ${barStyle}"><tr>
		<c:forEach var="singleValue" items="${percentStatistic}" varStatus="status">
			<c:if test="${singleValue.value.percent > 0}">
				<td class="ss_statisticBar statistic${status.index mod 8}" style="width:${singleValue.value.percent}%;" title="<ssf:nlt tag="${singleValue.value.label}" /> - ${singleValue.value.percent}% (${singleValue.value.value} <ssf:nlt tag="statistic.unity" />)"><span>${singleValue.value.percent}%</span></td>
			</c:if>
		</c:forEach>
	</tr></table>
	
	<c:if test="${showLegend}">
	<div class="${barStyle}">
		<ul class="ss_statisticLegend">
		<c:forEach var="singleValue" items="${percentStatistic}" varStatus="status">
			<li><div class="statistic${status.index mod 8} ss_statisticLegend">&nbsp;</div><ssf:nlt tag="${singleValue.value.label}" />: ${singleValue.value.percent}% (${singleValue.value.value} <c:choose><c:when test="${singleValue.value.value == 1}"><ssf:nlt tag="statistic.unity" /></c:when><c:otherwise><ssf:nlt tag="statistic.unity.plural" /></c:otherwise></c:choose>)<div class="ss_clear_float"></div></li>
		</c:forEach>
		</ul>
	</div>
	</c:if>

</c:if>