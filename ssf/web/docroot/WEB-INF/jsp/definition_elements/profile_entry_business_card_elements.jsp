<% //Business card elements %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
%>
<c:if test="${empty property_maxWidth}">
  <c:set var="property_maxWidth" value="200" scope="request"/>
</c:if>
<c:if test="${empty property_maxHeight}">
  <c:set var="property_maxHeight" value="200" scope="request"/>
</c:if>
<table width="100%">
<tr>
<td valign="top">
<div style="width:${property_maxWidth + 4}px; height:${property_maxHeight + 4}px;">
<c:if test="${!empty ssDefinitionEntry.customAttributes['picture']}">
<c:set var="selections" value="${ssDefinitionEntry.customAttributes['picture'].value}" />
<c:set var="pictureCount" value="0"/>
<c:forEach var="selection" items="${selections}">
  <c:if test="${pictureCount == 0}">
	<img id="ss_profilePicture<portlet:namespace/>"
	  border="0" 
	  src="<ssf:url 
	    webPath="viewFile"
	    folderId="${ssDefinitionEntry.parentBinder.id}"
	    entryId="${ssDefinitionEntry.id}" >
	    <ssf:param name="fileId" value="${selection.id}"/>
	    <ssf:param name="viewType" value="scaled"/>
	    </ssf:url>" alt="${property_caption}" />
  </c:if>
  <c:set var="pictureCount" value="${pictureCount + 1}"/>
</c:forEach>
</c:if>
</div>

</td>
<td valign="top" style="padding-left:30px;">

<c:set var="ss_profile_element_display_style" value="tableAlignLeft" scope="request"/>
<table>

<c:forEach var="element" items="${propertyValues__elements}">

<tr>
<td align="right" class="ss_profile_elements_spacer">
  <span class="ss_bold"><ssf:nlt tag="profile.element.${element}"/></span>
</td>
<td>
 <c:if test="${!empty ssDefinitionEntry[element]}">
<c:out value="${ssDefinitionEntry[element]}"/>
  </c:if>
  <c:if test="${!empty ssDefinitionEntry.customAttributes[element]}">
    <c:out value="${ssDefinitionEntry[element]}"/>
  </c:if>
</td>
</tr>
</c:forEach>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" 
  configJspStyle="${ssConfigJspStyle}" />

</table>
<c:set var="ss_profile_element_display_style" value="" scope="request"/>

</td>
<td valign="top" class="ss_profile_elements_spacer">
  <div id="ss_presenceOptions_${renderResponse.namespace}"></div>
  <ssf:presenceInfo user="${ssDefinitionEntry}" 
    showOptionsInline="true" 
    optionsDivId="ss_presenceOptions_${renderResponse.namespace}"/>
</td>
</tr>
</table>

<c:if test="${pictureCount > 1}">
<table>
<tr>
<td align="left">
  <div class="ss_thumbnail_gallery ss_thumbnail_small_no_text">
  <c:set var="selections" value="${ssDefinitionEntry.customAttributes['picture'].value}" />
  <c:forEach var="selection" items="${selections}">
	<div><a href="#" 
	  onMouseover="ss_showProfileImg(this, 'ss_profilePicture<portlet:namespace/>'); return false;">
	<img src="<ssf:url 
	    webPath="viewFile"
	    folderId="${ssDefinitionEntry.parentBinder.id}"
	    entryId="${ssDefinitionEntry.id}" >
	    <ssf:param name="fileId" value="${selection.id}"/>
	    <ssf:param name="viewType" value="scaled"/>
	    </ssf:url>" /></a></div>
  </c:forEach>
  </div>
</td>
</tr>
</table>
</c:if>
