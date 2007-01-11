<% //Title view %>
<div class="ss_entryContent">
<span class="ss_entryTitle">
<c:if test="${!empty ssDefinitionEntry.docNumber}">
  <c:out value="${ssDefinitionEntry.docNumber}"/>.
  <a style="text-decoration: none;" href="<ssf:url 
    folderId="${ssDefinitionEntry.parentFolder.id}" 
    action="view_folder_entry"
    entryId="${ssDefinitionEntry.id}"/>">
</c:if>
<c:if test="${empty ssDefinitionEntry.title}">
  <span class="ss_light">--<ssf:nlt tag="entry.noTitle"/>--</span>
</c:if><c:out value="${ssDefinitionEntry.title}"/></a></span>
</div>