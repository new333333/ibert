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
<%@ page import="com.sitescape.team.util.NLT" %>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript">
var <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_savedIndex;

function <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_showAliasDiv(index) {
	
	self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasFm.alias.value=eval('self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm.alias' + index + '.value');
	<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_savedIndex=index;
	ss_showPopupDivCentered('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasDiv');	
}
function <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAlias() {
	var param = eval('self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm.alias' + <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_savedIndex);
	param.value = self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasFm.alias.value;
<c:if test="${mail_posting_use_aliases == 'false'}">
	var param = eval('self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm.password' + <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_savedIndex);
	param.value = self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasFm.password.value;
</c:if>
	var param = document.getElementById('aliasSpan' + <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_savedIndex);
	param.innerHTML = self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasFm.alias.value;
}
</script>
<div class="ss_style ss_portlet">

<form class="ss_style ss_form" name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" method="post" 
	action="<portlet:actionURL windowState="maximized"><portlet:param 
	name="action" value="configure_posting_job"/></portlet:actionURL>">
<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" />">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="notify.header" /></legend>	
<table class="ss_style" border="0" cellspacing="3" cellpadding="3">
<tr>
<td valign="top">
<input type="checkbox" class="ss_style" id="notifyenabled" name="notifyenabled" <c:if test="${ssScheduleInfonotify.enabled}">checked</c:if> />
<span class="ss_labelRight"><ssf:nlt tag="notify.schedule.enable"/> </span><ssf:inlineHelp tag="ihelp.email.enableCheckBox"/>
<br/>
<c:set var="schedule" value="${ssScheduleInfonotify.schedule}"/>
<c:set var="schedPrefix" value="notify"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
</td></tr></table>
</fieldset>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="incoming.header" /></legend>	
<table class="ss_style" border="0" cellspacing="3" cellpadding="3">
<tr><td valign="top"> 
<input type="checkbox" class="ss_style" id="postenabled" name="postenabled" <c:if test="${ssScheduleInfopost.enabled}">checked</c:if>/>
<span class="ss_labelRight"><ssf:nlt tag="incoming.enable.all"/> <ssf:inlineHelp jsp="workspaces_folders/misc_tools/email_enable_posting" /></span>
<br/>
<c:set var="schedule" value="${ssScheduleInfopost.schedule}"/>
<c:set var="schedPrefix" value="post"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
</td></tr>
<c:if test="${!empty ssPostings}"> 
<tr>
<td valign="top">
<span class="ss_labelAbove"> <ssf:nlt tag="incoming.aliases"/><ssf:inlineHelp tag="ihelp.email.incomingAddresses" /></span>
<table border="0" cellspacing="0" cellpadding="0" class="ss_style ss_borderTable" >
  <tr class="ss_headerRow">
  <td class="ss_bold" align="center" width="5%" scope="col"><ssf:nlt tag="incoming.delete"/></td>
  <td class="ss_bold" align="left" colspan="2" scope="col"><ssf:nlt tag="incoming.alias" /></td>
  <td class="ss_bold" align="left" scope="col"><ssf:nlt tag="incoming.folder" /></td>
</tr>

<c:forEach var="alias" varStatus="status" items="${ssPostings}" >
<input type="hidden" id="aliasId${status.index}" name="aliasId${status.index}" value="${alias.id}"/>
<input type="hidden" id="alias${status.index}" name="alias${status.index}" value="${alias.emailAddress}"/>
<c:if test="${mail_posting_use_aliases == 'false'}">
  <c:set var="emailPassword" value=""/>
  <c:if test="${!empty alias.password}"><c:set var="emailPassword" value="_____"/></c:if>
  <input type="hidden" id="password${status.index}" name="password${status.index}" value="${emailPassword}"/>
</c:if>
<tr><td>
<input type="checkbox" class="ss_normal" id="delete${status.index}" name="delete${status.index}">
</td><td>
<span class="ss_normal" id="aliasSpan${status.index}" name="aliasSpan${status.index}">${alias.emailAddress}</span>
</td><td>
<input type="button" value="Edit" onClick="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_showAliasDiv('${status.index}'); return false"/>
</td><td>
<c:if test="${!empty alias.binder}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${alias.binder.id}">
		    <ssf:param name="entityType" value="${alias.binder.entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>">
${alias.binder.title}&nbsp;&nbsp<span  class="ss_smallprint ss_light">(${alias.binder.parentBinder.title})</span>
</a>
</c:if>
</td></tr>
</c:forEach>
</table>
</td></tr>
</c:if>
</table>
</fieldset>
<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" />">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
</form>
<div class="ss_style ss_popupMenu" style="visibility:hidden; display:block" name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasDiv" id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasDiv">
<form class="ss_style ss_form" 
  name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasFm" 
  id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasFm"
  onSubmit="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAlias(); ss_cancelPopupDiv('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasDiv'); return false;">
<br/><br/>
<span class="ss_labelRight"><ssf:nlt tag="incoming.modifyAlias"/></span>
 <input type="text" name="alias" id="alias" value="" size="32"/> 
<c:if test="${mail_posting_use_aliases == 'false'}">
<br/>
<span class="ss_labelRight"><ssf:nlt tag="incoming.password"/></span>
 <input type="password" name="password" id="password" value="" size="32"/> 
</c:if>
<br/><br/>
<div class="ss_buttonBarLeft">
  <input type="submit" value="<ssf:nlt tag="button.ok"/>" 
  onClick="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAlias(); ss_cancelPopupDiv('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasDiv'); return false;">
  <input type="submit" value="<ssf:nlt tag="button.cancel"/>"
  onClick="ss_cancelPopupDiv('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasDiv');return false;">  
</div>
</form>
</div>
</div>
