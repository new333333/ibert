<%@ include file="/WEB-INF/jsp/common/servlet.include.jsp" %>
<body>

<jsp:useBean id="ss_tree_treeName" type="java.lang.String" scope="request" />
<jsp:useBean id="ss_tree_showIdRoutine" type="java.lang.String" scope="request" />
<jsp:useBean id="ss_tree_binderId" type="java.lang.String" scope="request" />
<jsp:useBean id="ss_tree_topId" type="java.lang.String" scope="request" />
<jsp:useBean id="ss_tree_select_id" type="java.lang.String" scope="request" />
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />

<c:if test="${!empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
<div><span class="ss_bold"><ssf:nlt tag="general.notLoggedIn"/></span></div>
</c:if>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
<div class="ss_style">
<%
	java.util.List ss_tree_select = (java.util.List) request.getAttribute("ss_tree_select");
	if (ss_tree_select == null) {
%>
		<ssf:tree treeName="<%= ss_tree_treeName %>" 
		  initOnly="true"
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="<%= ss_tree_treeName %>"
		  topId="<%= ss_tree_topId %>"
		  rootOpen="true" 
		  showIdRoutine="<%= "parent." + ss_tree_showIdRoutine %>"
		/>
		<ssf:tree treeName="<%= ss_tree_treeName %>" 
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="<%= ss_tree_binderId %>"
		  topId="<%= ss_tree_topId %>"
		  rootOpen="true" 
		  showIdRoutine="<%= "parent." + ss_tree_showIdRoutine %>"
		/>
<%
	} else {
%>
		<ssf:tree treeName="<%= ss_tree_treeName %>" 
		  initOnly="true"
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="<%= ss_tree_binderId %>"
		  topId="<%= ss_tree_topId %>"
		  rootOpen="true" 
		  showIdRoutine="<%= "parent." + ss_tree_showIdRoutine %>"
		  multiSelect="<%= ss_tree_select %>"
		  multiSelectPrefix="<%= ss_tree_select_id %>"
		/>
		<ssf:tree treeName="<%= ss_tree_treeName %>" 
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="<%= ss_tree_binderId %>"
		  topId="<%= ss_tree_topId %>"
		  rootOpen="true" 
		  showIdRoutine="<%= "parent." + ss_tree_showIdRoutine %>"
		  multiSelect="<%= ss_tree_select %>"
		  multiSelectPrefix="<%= ss_tree_select_id %>"
		/>
<%
	}
%>
</div>
<script type="text/javascript">
function ss_tree_accessible_open_${ss_tree_treeName}_${ss_tree_binderId}() {
ss_treeOpen('${ss_tree_treeName}', '${ss_tree_binderId}', '${ss_tree_parentId}', '${ss_tree_bottom}', '${ss_tree_type}');
}
ss_createOnLoadObj("ss_tree_accessible_open_${ss_tree_treeName}_${ss_tree_binderId}", ss_tree_accessible_open_${ss_tree_treeName}_${ss_tree_binderId});
self.window.focus();
</script>
</c:if>

</body>
</html>

