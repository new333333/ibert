<% //Blog creation date view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="0">
<div class="ss_entryContent">
<c:out value="${property_caption}" />
<fmt:formatDate 
     value="${ssDefinitionEntry.creation.date}" type="both" 
	 pattern="dd MMMM yyyy, HH:mm" /> GMT
</div>
</c:if>
