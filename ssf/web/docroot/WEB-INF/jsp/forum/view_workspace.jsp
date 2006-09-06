<% // The main workspace view  %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>
<c:if test="${!empty ssReloadUrl}">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
</script>

</c:if>
<c:if test="${empty ssReloadUrl}">

<script type="text/javascript">
var ss_reloadUrl = "${ss_reloadUrl}";
</script>

<div class="ss_style ss_portlet">

<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/navbar.jsp" %>

<% // Tabs %>
<%@ include file="/WEB-INF/jsp/definition_elements/tabbar.jsp" %>

<% // Workspace toolbar %>
<c:if test="${!empty ssFolderToolbar}">
<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar" />
</c:if>

<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
<br/>

<% // Show the workspace according to its definition %>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  processThisItem="true"
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}"
  entry="${ssBinder}" />
</div>
</c:if>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

