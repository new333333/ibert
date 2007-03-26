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
				<span class="ss_entryTitle ss_normalprint">
					<ssf:menuLink displayDiv="false" action="view_folder_entry" adapter="true" entryId="${entry._docId}" 
					folderId="${entry._binderId}" binderId="${entry._binderId}" entityType="${entry._entityType}" 
					imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
				    menuDivId="ss_emd_${renderResponse.namespace}"
					linkMenuObj="ss_linkMenu${renderResponse.namespace}" 
					namespace="${renderResponse.namespace}"
					entryCallbackRoutine="${showEntryCallbackRoutine}">
					
						<ssf:param name="url" useBody="true">
							<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
							action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
						</ssf:param>
					
						<c:if test="${empty entry.title}">
					    	${entry._principal.title} <ssf:nlt tag="guestbook.author.wrote"/>: 
					    </c:if>
						<c:out value="${entry.title}" escapeXml="false"/>
					</ssf:menuLink>
				</span>

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

</table>

<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObj="ss_linkMenu${renderResponse.namespace}" 
	namespace="${renderResponse.namespace}">
</ssf:menuLink>

<script type="text/javascript">
var ss_linkMenu${renderResponse.namespace} = new ss_linkMenuObj();
</script>