<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>


<div class="ss_portlet">
<br/>

<form class="ss_style ss_form" 
  id="<portlet:namespace/>fm" 
  method="post">
<span class="ss_bold">
  <c:if test="${ssOperation == 'add_workspace'}">
<ssf:nlt tag="binder.add.workspace.title"><ssf:param name="value" value="${ssBinder.pathName}"/>
</ssf:nlt>
</c:if>
<c:if test="${ssOperation != 'add_workspace'}">
<ssf:nlt tag="binder.add.folder.title"><ssf:param name="value" value="${ssBinder.pathName}"/>
</ssf:nlt>
</c:if>

</span></br></br>
  
	<span class="ss_labelLeft"><ssf:nlt tag="folder.label.title" text="Title"/></span>
	<input type="text" class="ss_text" size="70" name="title"><br/><br/>
  <span class="ss_bold"><ssf:nlt tag="binder.add.binder.select.config"/></span>
  <br/>
  <c:forEach var="config" items="${ssBinderConfigs}" varStatus="status">
      <input type="radio" name="binderConfigId" value="${config.id}" 
      <c:if test="${status.count == 1}">checked="checked"</c:if>
      ><ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/>(<ssf:nlt tag="${config.title}" checkIfTag="true"/>)<br/>
  </c:forEach>
<br/>  

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">

</form>
</div>

