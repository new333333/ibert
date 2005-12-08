<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>

<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html xmlns:svg="http://www.w3.org/2000/svg-20000303-stylable">
<head>

<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.ef.ObjectKeys" %>
<%@ page import="com.sitescape.ef.web.WebKeys" %>
<%
//Set some default colors
String alphaColor = "#775325";
String betaColor = "#B89257";
String gammaColor = "#CCCC99";
//Get the Liferay colors
//String alphaColor = GetterUtil.get(request.getParameter("body_background"), skin.getAlpha().getBackground());
//String betaColor = GetterUtil.get(request.getParameter("body_background"), skin.getBeta().getBackground());
//String gammaColor = GetterUtil.get(request.getParameter("body_background"), skin.getGamma().getBackground());

boolean isIE = BrowserSniffer.is_ie(request);
%>
<c:if test="${empty ssf_support_files_loaded}">
<link rel="stylesheet" type="text/css" href="<html:rootPath/>css/forum.css">
<c:if test="<%= isIE %>">
<link rel="stylesheet" type="text/css" href="<html:rootPath/>css/forum_ie.css">
</c:if>
<c:if test="<%= !isIE %>">
<link rel="stylesheet" type="text/css" href="<html:rootPath/>css/forum_nn.css">
</c:if>
<style>
/* Forum toolbar */
div.ss_toolbar {
  width: 100%; 
  background-color: <%= gammaColor %>;
  margin-top: 8px;
  margin-bottom: 8px;
  }
  
/* Forum historybar */
div.ss_historybar {
  width: 100%; 
  background-color: <%= betaColor %>;
  margin-top: 8px;
  margin-bottom: 8px;
  }
  
/* highlights */
.ss_highlight_alpha {
  background-color: <%= alphaColor %>;
  }
  
.ss_highlight_beta {
  background-color: <%= betaColor %>;
  }
  
.ss_highlight_gamma {
  background-color: <%= gammaColor %>;
  }
  
.ss_titlebold {
  font-family: arial, helvetica, sans-serif;
  font-size: 13px;
  font-weight: bold;
  color: <%= alphaColor %>;  
  }


</style>
<script language="JavaScript" src="<html:rootPath/>js/forum/forum_common.js"></script>
</c:if>

</head>