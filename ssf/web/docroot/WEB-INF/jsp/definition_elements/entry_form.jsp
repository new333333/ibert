<% //Add an entry %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_configJspStyle" type="String" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
%>
<div class="ss_portlet" width="100%">
<ssf:displayConfiguration configDefinition="<%= ss_forum_config_definition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= ss_forum_configJspStyle %>" />
</div>
