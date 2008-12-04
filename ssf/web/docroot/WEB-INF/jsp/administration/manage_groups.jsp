<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<div class="ss_pseudoPortal">

<script type="text/javascript">

function ${renderResponse.namespace}_onsub(obj) {
	if (obj.name.value == '') {
		alert('<ssf:nlt tag="general.required.name"/>');
		return false;
	}
	return true;
}
</script>
<div class="ss_style ss_portlet">
<div style="padding:10px;" id="ss_manageGroups">
<span class="ss_titlebold"><ssf:nlt tag="administration.manage.groups" /></span>
<br>
<br>

<c:if test="${!empty ssException}">
<font color="red">

<span class="ss_largerprint"><c:out value="${ssException}"/></span>
<br/>

</font>
</c:if>
<ssf:expandableArea title='<%= NLT.get("administration.add.group") %>'>
<form class="ss_style ss_form" method="post" 
	action="<ssf:url action="manage_groups" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/></ssf:url>" 
	onSubmit="return(${renderResponse.namespace}_onsub(this))">
		
	<span class="ss_bold"><ssf:nlt tag="administration.add.groupName"/></span><ssf:inlineHelp tag="ihelp.groups.data_name"/><br/>
	<input type="text" class="ss_text" size="70" name="name"><br/><br/>
		
	<span class="ss_bold"><ssf:nlt tag="administration.add.groupTitle"/></span><br/>
	<input type="text" class="ss_text" size="70" name="title"><br/><br/>
		
	<span class="ss_bold"><ssf:nlt tag="administration.add.groupDescription"/></span><br/>
	<textarea name="description" wrap="virtual" rows="4" cols="80"></textarea><br/><br/>
		
	<input type="submit" class="ss_submit" name="addBtn" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>
</ssf:expandableArea>
<br/>
<br/>

<table>
<tr>
<td valign="top">
<span class="ss_bold"><ssf:nlt tag="administration.selectGroupToManage"/></span>
<br/>
<div class="ss_indent_medium" id="ss_modifyGroups">
  <c:forEach var="group" items="${ss_groupList}">
  	<a href="<ssf:url action="manage_groups" actionUrl="true"><ssf:param 
		name="binderId" value="${ssBinder.id}"/><ssf:param 
		name="entryId" value="${group._docId}"/>></ssf:url>"
	><span>${group.title}</span> <span class="ss_smallprint">(${group._groupName})</span></a><br/>
  </c:forEach>
</div>
</td>

<td valign="top">
<c:if test="${!empty ssGroup}">
<div class="ss_style ss_portlet" style="margin-left:20px; padding:8px; border:solid 1px black;">
<span class="ss_bold ss_largerprint">${ssGroup.title}</span> <span class="ss_smallprint">(${ssGroup.name})</span>
<br/>
<br/>
<form name="ss_groupForm" id="ss_groupForm" method="post"
  action="<ssf:url action="manage_groups" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/><ssf:param 
	name="entryId" value="${ssGroup.id}"/></ssf:url>"
  onSubmit="return ss_onSubmit(this);">
		
<ssf:expandableArea title='<%= NLT.get("administration.modify.groupTitle") %>'>
	<span class="ss_bold"><ssf:nlt tag="administration.add.groupTitle"/></span><br/>
	<input type="text" class="ss_text" size="40" name="title" value="${ssGroup.title}"><br/><br/>
		
	<span class="ss_bold"><ssf:nlt tag="administration.add.groupDescription"/></span><br/>
	<textarea name="description" wrap="virtual" rows="4" cols="40">${ssGroup.description}</textarea><br/><br/>
		
</ssf:expandableArea>
<br/>
<br/>
<span class="ss_bold"><ssf:nlt tag="administration.modifyGroupMembership" /></span>
<br/>
<table class="ss_style" border="0" cellspacing="0" cellpadding="3" width="95%">
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
<td valign="top">
  <ssf:find formName="ss_groupForm" formElement="users" 
    type="user" userList="${ssUsers}" binderId="${ssBinderId}"/>
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
<td valign="top">
  <ssf:find formName="ss_groupForm" formElement="groups" 
    type="group" userList="${ssGroups}"/>
</td>
</tr>
<tr><td colspan="2">
	<ssf:clipboard type="user" formElement="users" />
</td></tr>
</table>
<br/>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
<input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete"/>">
 
</form>
</div>

</c:if>
</td>

</tr>
</table>

<br/>

<div class="ss_formBreak"/>

<form class="ss_style ss_form" method="post"
		  action="<ssf:url action="manage_groups" actionUrl="true"><ssf:param 
		  name="binderId" value="${ssBinder.id}"/></ssf:url>" 
		  name="${renderResponse.namespace}fm">
<div class="ss_buttonBarLeft">

<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
</div>
</form>
</div>

</div>
</div>

</div>
</body>
</html>
