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
<% // Form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	String enctype = "application/x-www-form-urlencoded";
	if (item.selectSingleNode(".//item[@name='file']") != null || 
			item.selectSingleNode(".//item[@name='fileEntryTitle']") != null || 
			item.selectSingleNode(".//item[@name='graphic']") != null || 
			item.selectSingleNode(".//item[@name='profileEntryPicture']") != null || 
			item.selectSingleNode(".//item[@name='attachFiles']") != null) {
		enctype = "multipart/form-data";
	}
	String formName = (String) request.getAttribute("property_name");
	if (formName == null || formName.equals("")) {
		formName = WebKeys.DEFINITION_DEFAULT_FORM_NAME;
	}
	request.setAttribute("formName", formName);
	String methodName = (String) request.getAttribute("property_method");
	if (methodName == null || methodName.equals("")) {
		methodName = "post";
	}


	//Get the entry type of this definition (folder, file, or event)
	String formViewStyle = "form";
	Element formViewTypeEle = (Element)item.selectSingleNode("properties/property[@name='type']");
	if (formViewTypeEle != null) formViewStyle = formViewTypeEle.attributeValue("value", "form");

%>
<c:set var="ss_formViewStyle" value="<%= formViewStyle %>" scope="request" />
<jsp:useBean id="ss_formViewStyle" type="String" scope="request" />

<c:choose>
  <c:when test="${ss_formViewStyle == 'guestbook'}">
		<jsp:include page="/WEB-INF/jsp/definition_elements/guestbook/guestbook_form.jsp" />
  </c:when>

  <c:when test="${ss_formViewStyle == 'survey'}">
		<jsp:include page="/WEB-INF/jsp/definition_elements/survey/survey_form.jsp" />
  </c:when>

  <c:when test="${ss_formViewStyle == 'profile'}">
		<jsp:include page="/WEB-INF/jsp/definition_elements/profile_entry_form.jsp" />
  </c:when>

  <c:otherwise>
	<form style="background: transparent;" method="<%= methodName %>" enctype="<%= enctype %>" name="<%= formName %>" 
	  id="<%= formName %>" action="" onSubmit="return ss_onSubmit(this);">
	<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	  configElement="<%= item %>" 
	  configJspStyle="${ssConfigJspStyle}" />
	</form>  
  </c:otherwise>  
  
</c:choose>
