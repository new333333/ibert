<% // The html editor widget %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<%
	String formName = (String) request.getAttribute("formName");
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String height = (String) request.getAttribute("property_height");
	if (height == null || height.equals("")) {
		height = "200";
	}
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<b>"+caption+"</b><br>";
	}

String background = gammaColor;
%>
<c:set var="textValue" value=""/>
<c:if test="${!empty ss_forum_entry}">
  <c:if test="${property_name == 'description'}" >
    <c:set var="textValue" value="${ss_forum_entry.description.text}"/>
  </c:if>
  <c:if test="${property_name != 'description'}" >
    <c:set var="textValue" value="${ss_forum_entry.customAttributes[property_name].value.text}"/>
  </c:if>
</c:if>
<jsp:useBean id="textValue" type="java.lang.String" />
<div class="formBreak">
  <div class="labelLeft"><%= caption %>
    <ssf:htmleditor id="<%= elementName %>" 
      formName="<%= formName %>" height="<%= height %>" color="<%= background %>"
      initText="<%= textValue %>" />
  </div>
</div>
