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
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>

<div class="ss_portlet">
<br/>

<form class="ss_style ss_form" 
  name="<portlet:namespace/>fm" 
  method="post" >
<input type="hidden" name="_operation" value="${operation}"/>
<fieldset class="ss_fieldset">
<input type="hidden" name="binderDefinitionType" value="${ssBinderDefinitionType}"/>
<c:choose>
<c:when test="${ssBinderDefinitionType == 8}">
  <legend class="ss_legend"><ssf:nlt tag="binder.add.workspace.definition.legend" 
    text="Workspace definition"/></legend>
  <br>
  <span class="ss_bold"><ssf:nlt tag="binder.add.workspace.select.definition" 
  text="Select the workspace definition:"/></span>
</c:when>
<c:otherwise>
  <legend class="ss_legend"><ssf:nlt tag="binder.add.folder.definition.legend" 
    text="Folder definition"/></legend>
  <br>
  <span class="ss_bold"><ssf:nlt tag="binder.add.folder.select.definition" 
  text="Select the folder definition:"/></span>
</c:otherwise>
</c:choose>
  <br/>
  <c:forEach var="item" items="${ssPublicBinderDefinitions}">
      <c:choose>
        <c:when test="${ssDefaultWorkspaceDefinitionId == item.value.id}">
          <input type="radio" name="binderDefinition" value="${item.value.id}" checked/>
          <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
        </c:when>
        <c:otherwise>
          <input type="radio" name="binderDefinition" value="${item.value.id}"/>
          <c:out value="${item.value.title}"/> (<c:out value="${item.value.name}"/>)<br/>
        </c:otherwise>
      </c:choose>
  </c:forEach>

</fieldset>

<br/>

	
<input type="submit" class="ss_submit" name="selectDefBtn" value="<ssf:nlt tag="button.ok"/>">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">

</form>
</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
