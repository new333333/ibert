<%
// The dashboard "workspace tree" component
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<br/>
<c:set var="treeName" value="wsTree${ssComponentId}${renderResponse.namespace}"/>
<script type="text/javascript">
function ${treeName}_showId(forum, obj) {
	var formObj = ss_getContainingForm(obj);
	var r = formObj.data_topId;
    for (var b = 0; b < r.length; b++) {
      if (r[b].value == forum) 	{
      	r[b].checked=true;
      } else {
      	r[b].checked=false;
      }
	}
	ss_clearSingleSelect('${treeName}');
	
	return false;
}
</script>
<table>

<tr>
<td valign="top">
<span class="ss_bold"><ssf:nlt tag="dashboard.startingPoint"/></span>
<br/>
<div class="ss_indent_medium">
<c:set var="checked" value=""/>
<c:if test="${empty ssDashboard.dashboard.components[ssComponentId].data.start || 
    ssDashboard.dashboard.components[ssComponentId].data.start== 'this'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_start" value="this" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="dashboard.startingPoint.current"/><br/>

<c:set var="checked" value=""/>
<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.start == 'select'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_start" value="select" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="dashboard.startingPoint.select"/><br/>
</div>
</td>
</tr>
<tr><td>
<ssf:tree 
  treeName="${treeName}"
  treeDocument="${ssDashboard.beans[ssComponentId].workspaceTree}"  
  rootOpen="false" 
  singleSelect="${ssDashboard.beans[ssComponentId].ssBinder.id}" 
  singleSelectName="data_topId"
/>
</td></tr>
<tr>
<td valign="top"><br/></td>
</tr>

<tr>
<td valign="top">
<span class="ss_bold"><ssf:nlt tag="dashboard.rootOpen"/></span>
<br/>
<div class="ss_indent_medium">
<c:set var="checked" value=""/>
<c:if test="${empty ssDashboard.dashboard.components[ssComponentId].data.rootOpen || 
    ssDashboard.dashboard.components[ssComponentId].data.rootOpen== 'true'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_rootOpen" value="true" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="general.yes"/><br/>

<c:set var="checked" value=""/>
<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.rootOpen == 'false'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_rootOpen" value="false" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="general.no"/><br/>
</div>
</td>
</tr>

</table>
