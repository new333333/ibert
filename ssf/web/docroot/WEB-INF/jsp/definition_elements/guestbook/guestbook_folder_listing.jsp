<% //View the listing part of a guestbook folder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/guestbook/guestbook_sign.jsp" %>


	<c:forEach var="entry" items="${ssFolderEntries}" >
<table class="ss_guestbook" width="100%">
		<jsp:useBean id="entry" type="java.util.HashMap" />
		
		<tr>
			<td class="ss_miniBusinessCard" style="padding-bottom: 5px;">
				<ssf:miniBusinessCard user="<%=(User)entry.get("_principal")%>"/> 
			</td>
			<td class="ss_guestbookContainer">
				<a href="<portlet:renderURL windowState="maximized"><portlet:param 
					name="action" value="view_folder_entry"/><portlet:param 
					name="binderId" value="${entry._binderId}"/><portlet:param 
					name="entryId" value="${entry._docId}"/><portlet:param 
					name="newTab" value="1"/></portlet:renderURL>">
				<span class="ss_entryTitle ss_normalprint">
					<c:if test="${empty entry.title}">
				    	${entry._principal.title} <ssf:nlt tag="guestbook.author.wrote"/>: 
				    </c:if>
					<c:out value="${entry.title}" escapeXml="false"/>
				</span></a>



				<span class="ss_entrySignature"><fmt:formatDate timeZone="${fileEntry._principal.timeZone.ID}"
				      value="${entry._modificationDate}" type="both" 
					  timeStyle="short" dateStyle="short" /></span>
				
				<c:if test="${!empty entry._desc}">
				<div class="ss_entryContent">
					<span><ssf:markup type="view"><c:out 
					  value="${entry._desc}" escapeXml="false"/></ssf:markup></span>
				</div>
				</c:if>
			</td>
		</tr>
</table>
	</c:forEach>
