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
	var param = document.getElementById('aliasSpan' + <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_savedIndex);
	param.innerHTML = self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasFm.alias.value;
}
</script>
<div class="ss_style ss_portlet">
<form class="ss_style ss_form" name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" method="post" 
	action="<portlet:actionURL><portlet:param 
	name="action" value="configure_posting_job"/></portlet:actionURL>">

<table class="ss_style"  border="1" cellspacing="0" cellpadding="3" width="100%">
<tr>
<th><ssf:nlt tag="incoming.job_title"/></th>
<th><ssf:nlt tag="incoming.aliases"/> <ssf:inlineHelp tag="ihelp.email.incomingAddresses" /></th>
</tr>
<tr><td valign="top">
<table class="ss_style" border ="0" cellspacing="0" cellpadding="3">
<tr><td> 
<input type="checkbox" class="ss_labelRight" id="enabled" name="enabled" <c:if test="${ssScheduleInfo.enabled}">checked</c:if>/>
<ssf:nlt tag="incoming.enable.all"/> <ssf:inlineHelp jsp="workspaces_folders/misc_tools/email_enable_posting" />
<br/>

<c:set var="schedule" value="${ssScheduleInfo.schedule}"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
</td></tr></table>
</td><td valign="top">
 <br/>
<table border="0" cellspacing="0" cellpadding="0" class="ss_style ss_borderTable" >
  <tr class="ss_headerRow">
  <td class="ss_bold" align="center" width="5%" scope="col"><ssf:nlt tag="incoming.delete"/></td>
  <td class="ss_bold" align="left" colspan="2" scope="col"><ssf:nlt tag="incoming.alias" /></td>
  <td class="ss_bold" align="left" scope="col"><ssf:nlt tag="incoming.folder" /></td>
</tr>

<c:forEach var="alias" varStatus="status" items="${ssPostings}" >
<input type="hidden" id="aliasId${status.index}" name="aliasId${status.index}" value="${alias.id}"/>
<input type="hidden" id="alias${status.index}" name="alias${status.index}" value="${alias.emailAddress}"/>
<tr><td>
<input type="checkbox" class="ss_normal" id="delete${status.index}" name="delete${status.index}">
</td><td>
<span class="ss_normal" id="aliasSpan${status.index}" name="aliasSpan${status.index}">${alias.emailAddress}</span>
</td><td>
<input type="button" value="Edit" onClick="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_showAliasDiv('${status.index}'); return false"/>
</td><td>
<c:if test="${!empty alias.binder}">
${alias.binder.title}
</c:if>
</td></tr>
</c:forEach>
</table>
</table>
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
