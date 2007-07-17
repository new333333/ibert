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
<% //Graphic view %>

<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name]}">
<c:set var="selections" value="${ssDefinitionEntry.customAttributes[property_name].value}" />
<c:forEach var="selection" items="${selections}">
<img border="0" src="<ssf:fileurl 
    webPath="viewFile"
    fileName="${selection.fileItem.name}"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}"
    entityType="${ssDefinitionEntry.entityType}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
    <ssf:param name="viewType" value="scaled"/>
    </ssf:fileurl>" alt="${property_caption}" />
</c:forEach>
</c:if>
