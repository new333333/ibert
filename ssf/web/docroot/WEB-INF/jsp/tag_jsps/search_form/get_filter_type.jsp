<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.ArrayList" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
	<taconite-replace contextNodeID="entryList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div id="entryList<c:out value="${ss_filterTermNumber}"/>" 
	style="display:inline;">
	  <c:if test="${ss_filterType == 'text'}">
         <ssf:nlt tag="searchForm.searchText" text="Search text"/>: <input 
         type="text" class="ss_text" style="width:200px;" 
         name="elementValue<c:out value="${ss_filterTermNumber}"/>" />
	  </c:if>
	  <c:if test="${ss_filterType == 'entry'}">
    	<select name="ss_entry_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	   id="ss_entry_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	  onChange="ss_getFilterSelectionBox(this, 'ss_entry_def_id', 'get_searchForm_entry_elements', '${ss_filterType}')">
    	  <option value="" selected="selected"><ssf:nlt 
    	    tag="searchForm.selectElement" text="--select an entry type--"/></option>
    	  <option value="_common"><ssf:nlt 
    	    tag="searchForm.commonElements" text="--common elements (e.g., title)--"/></option>
		    <c:forEach var="item" items="${ssPublicBinderEntryDefinitions}">
		        <option value="${item.value.id}"><ssf:nlt tag="${item.value.title}" checkIfTag="true"/></option>
		    </c:forEach>
    	</select>
	  </c:if>
	  <c:if test="${ss_filterType == 'workflow'}">
    	<select name="ss_workflow_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	   id="ss_workflow_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	  onChange="ss_getFilterSelectionBox(this, 'ss_workflow_def_id', 'get_workflow_states', '${ss_filterType}')">
    	  <option value="" selected="selected"><ssf:nlt tag="filter.selectWorkflow"/></option>
		  <c:forEach var="item" items="${ssWorkflowDefinitionMap}">
		    <option value="${item.value.id}"><ssf:nlt tag="${item.value.title}" checkIfTag="true"/></option>
		  </c:forEach>
    	</select>
	  </c:if>
	  
	  <c:if test="${ss_filterType == 'folders'}">
<jsp:useBean id="ss_filterTermNumber" type="String" scope="request" />
		<ssf:tree 
		  treeName="<%= "t_searchForm_wsTree" %>" 
		  treeDocument="${ssDomTree}"  
		  rootOpen="false" 
		  multiSelect="<%= new ArrayList() %>" 
		  multiSelectPrefix="ss_sf_id_"
		  noInit="true" />
	  </c:if>
	  <input type="hidden" name="filterType<c:out value="${ss_filterTermNumber}"/>"
	    value="<c:out value="${ss_filterType}"/>"/>
	</div></taconite-replace>

	<taconite-replace contextNodeID="elementList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="elementList<c:out value="${ss_filterTermNumber}"/>" 
	style="visibility:visible; display:inline;">
	 </div></taconite-replace>

	<taconite-replace contextNodeID="valueList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="valueList<c:out value="${ss_filterTermNumber}"/>" 
	style="visibility:visible; display:inline;">
	 </div></taconite-replace>
</c:if>
</taconite-root>
