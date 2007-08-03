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
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<c:if test="${empty ss_inlineHelpDivIdNumber}">
  <c:set var="ss_inlineHelpDivIdNumber" value="0" scope="request"/>
</c:if>
<c:set var="ss_inlineHelpDivIdNumber" value="${ss_inlineHelpDivIdNumber + 1}" scope="request"/>
<a href="javascript:;" onClick="ss_showDiv('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_inlineHelpDiv${ss_inlineHelpDivIdNumber}');return false;"
><img border="0" alt="${alt}" src="<html:imagesPath/>pics/sym_s_help.gif"></a>
<div id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_inlineHelpDiv${ss_inlineHelpDivIdNumber}" class="ss_inlineHelp">
<div align="right"><a href="javascript:;" 
  onClick="ss_hideDivNone('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_inlineHelpDiv${ss_inlineHelpDivIdNumber}');return false;"><img 
  border="0" alt="<ssf:nlt tag="button.close"/>" 
  src="<html:imagesPath/>pics/sym_s_delete.gif"></a></div>
