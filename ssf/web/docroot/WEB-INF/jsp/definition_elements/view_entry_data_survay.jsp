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


<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>
<c:out value="${ssDefinitionEntry.customAttributes[property_name].value}" escapeXml="false"/>
</div>