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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${ssConfigJspStyle != 'template'}">
<c:if test="${empty ss_tagDivNumber}">
  <c:set var="ss_tagDivNumber" value="0" scope="request"/>
</c:if>
<c:set var="ss_tagDivNumber" value="${ss_tagDivNumber + 1}" scope="request"/>
<script type="text/javascript">
function ss_showTags<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(divNumber, entityType, entryId) {
	var divId = 'ss_tags<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_' + parseInt(divNumber) + '_pane';
	ss_moveDivToBody(divId);
	var divObj = document.getElementById(divId);
	divObj.style.display = "block";
	divObj.visibility = "visible";
	divObj.style.zIndex = ssMenuZ;
	var anchorObj = document.getElementById('ss_tags_anchor<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_'+parseInt(divNumber));
	ss_setObjectTop(divObj, (ss_getDivTop('ss_tags_anchor<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_'+parseInt(divNumber)) + 20) + "px");
	var leftEdge = parseInt(ss_getDivLeft('ss_tags_anchor<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_'+parseInt(divNumber))) + 10;
	var rightEdge = parseInt(leftEdge + ss_getObjectWidth(divObj));
	if (leftEdge < 0) leftEdge = 0;
	self.parent.ss_debug("top = "+ss_getDivTop('ss_tags_anchor<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_'+parseInt(divNumber)) + ", left = " +leftEdge)
	ss_setObjectLeft(divObj, leftEdge + "px")
	ss_showDiv(divId);
	
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
	if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
}
function ss_hideTags<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(divNumber, entityType, entryId) {
	var divId = 'ss_tags<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_' + parseInt(divNumber) + '_pane';
	ss_hideDiv(divId);
}
function ss_addTag<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(divNumber, entityType, entryId) {
	ss_modifyTags<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('add', '', divNumber, entityType, entryId);
	
}
function ss_addTag2<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(id, divNumber, entityType, entryId) {
	alert(entryId)
	document.forms['ss_modifyTagsForm<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_'+divNumber].communityTag.value = id;
	ss_modifyTags<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('add', '', divNumber, entityType, entryId);
}

function ss_deleteTag<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(tagId, divNumber, entityType, entryId) {
	ss_modifyTags<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('delete', tagId, divNumber, entityType, entryId);
}
function ss_modifyTags<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(operation2, tagId, divNumber, entityType, entryId) {
	ss_setupStatusMessageDiv();
	var tagToDelete = "";
	if (operation2 == 'delete') tagToDelete = tagId;
	var url = "<ssf:url 
		adapter="true" 
		portletName="ss_forum" 
		action="__ajax_request" 
		actionUrl="true" >
		<ssf:param name="operation" value="modify_tags" />
		<ssf:param name="binderId" value="${ssBinder.id}" />
		</ssf:url>";
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("operation2", operation2)
	ajaxRequest.addKeyValue("namespace", "<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>")
	ajaxRequest.addKeyValue("tagToDelete", tagToDelete)
	ajaxRequest.addKeyValue("tagDivNumber", divNumber)
	ajaxRequest.addKeyValue("entityId", entryId);
	ajaxRequest.addKeyValue("entityType", entityType);
	ajaxRequest.addFormElements("ss_modifyTagsForm<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_"+divNumber);
	ajaxRequest.setData("divNumber", divNumber);
	ajaxRequest.setData("entityId", entryId);
	ajaxRequest.setData("entityType", entityType);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postModifyTags<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postModifyTags<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_debug("post: "+obj.getData("divNumber"))
	ss_showTags<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(obj.getData("divNumber"), obj.getData("entityType"), obj.getData("entityId"));
}
</script>
<c:set var="ss_tagViewNamespace" value="${renderResponse.namespace}" scope="request"/>
<c:set var="ssEntry" value="${ss_tagObject}" scope="request"/>
<%
boolean isIEtag = BrowserSniffer.is_ie(request);
%>
<table class="ss_style" cellspacing="0" cellpadding="0">
<tbody>
<tr>
<td valign="top" style="padding-right:2px;">
  <ssHelpSpot helpId="workspaces_folders/misc_tools/tags" offsetX="-10" xAlignment="left" 
    <c:if test="<%= isIEtag %>">
       offsetY="-3" 
    </c:if>
    <c:if test="<%= !isIEtag %>">
       offsetY="-17" 
    </c:if>
     xAlignment="left" title="<ssf:nlt tag="helpSpot.tags" text="Tags"/>">
  </ssHelpSpot>
<a href="javascript:;" 
	<ssf:ifaccessible>
  		onClick="ss_showAccessibleMenu('ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_pane'); return false;"
	</ssf:ifaccessible>
	<ssf:ifnotaccessible>
		onClick="ss_showTags<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('${ss_tagDivNumber}', '${ss_tagObject.entityType}', '${ss_tagObject.id}'); return false;"
	</ssf:ifnotaccessible>
	<ssf:title tag="title.open.tag.menu" />
><div class="ss_iconed_label ss_add_tag"><ssf:nlt tag="tags.tags"/></div></a>
  <div id="ss_tags_anchor<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_${ss_tagDivNumber}">
  </div>
</td>
<td width="100%">
<ssf:ifaccessible>
	<c:set var="ssCloseScript" value="ss_hideAccessibleMenu('ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_pane'); return false;" scope="request"/>
</ssf:ifaccessible>
<ssf:ifnotaccessible>
	<c:set var="ssCloseScript" value="ss_hideTags${ss_tagViewNamespace}('${ss_tagDivNumber}', '${ss_tagObject.entityType}', '${ss_tagObject.id}');return false;" scope="request"/>
</ssf:ifnotaccessible>

<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data_cloud.jsp" />
</td></tr></tbody></table>

<div id="ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_pane" 
	<ssf:ifaccessible>
		style="visibility:hidden;display:none;white-space:nowrap;"  		
	</ssf:ifaccessible>
	<ssf:ifnotaccessible>
		class="ss_tag_pane"
	</ssf:ifnotaccessible>
>

<ssf:popupPane width="220px" titleTag="tags.manageTags" closeScript="${ssCloseScript}">

<div style="padding:0px 10px;">
<form class="ss_style ss_form ss_tag_pane_color" 
  method="post" action=""
  id="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" 
  name="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}">
<table class="ss_tag_pane_color">
<tbody>

<tr><th align="left"><ssf:nlt tag="tags.personalTags"/></th></tr>

<tr><td>
<c:set var="ssTags" value="${ssPersonalTags}" scope="request" />
<c:set var="ssTagsType" value="p" scope="request" />
<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
</td></tr>

<tr><td>
  <table class="ss_tag_pane_color"><tbody><tr><td>
    <!-- input type="text" class="ss_text" name="personalTag" / -->
<ssf:find formName="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" formElement="personalTag" type="personalTags" width="70px" singleItem="true" accessibilityText="title.add.personal.tags" />
    </td><td>
      <a class="ss_linkButton" href="javascript:;" 
        onClick="ss_addTag${ss_tagViewNamespace}('${ss_tagDivNumber}', '${ss_tagObject.entityType}', '${ss_tagObject.id}');setTimeout('document.ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}.reset()', 100);return false;"
        <ssf:title tag="title.add.personal.tags" />
      ><ssf:nlt tag="button.add"/></a>
    </td></tr>
  </tbody></table>
</td></tr>

<tr><td></td></tr>

<tr><th align="left"><ssf:nlt tag="tags.communityTags"/></th></tr>

<tr><td>
<c:set var="ssTags" value="${ssCommunityTags}" scope="request" />
<c:set var="ssTagsType" value="c" scope="request" />
<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
</td></tr>

<tr><td>
  <table class="ss_tag_pane_color"><tbody><tr><td>
    <!--input type="text" class="ss_text" name="communityTag"/ -->
   
<ssf:ifAccessAllowed binder = "${ssBinder}" operation = "manageTag">    
<ssf:find formName="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" formElement="communityTag" type="communityTags" width="70px" singleItem="true" accessibilityText="title.add.community.tags" />
</ssf:ifAccessAllowed>
   
    </td><td style="padding-left:4px;">
    
    <ssf:ifAccessAllowed binder = "${ssBinder}" operation = "manageTag">  
    <a class="ss_linkButton" href="javascript:;" 
      onClick="ss_addTag${ss_tagViewNamespace}('${ss_tagDivNumber}', '${ss_tagObject.entityType}', '${ss_tagObject.id}');setTimeout('document.ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}.reset()', 100);return false;"
      <ssf:title tag="title.add.community.tags" />
    ><ssf:nlt tag="button.add"/></a>
    </ssf:ifAccessAllowed>  

    </td></tr>
  </tbody></table>
</td></tr>

<ssf:ifaccessible>

<tr><td>
  <table class="ss_tag_pane_color" colspan="2"><tbody><tr><td>
    <a class="ss_linkButton" href="javascript:;" title="<ssf:nlt tag="title.closeMenu" />"
      onClick="ss_hideAccessibleMenu('ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_pane'); return false;"
    ><ssf:nlt tag="button.close"/></a>
    </td></tr>
  </tbody></table>
</td></tr>

</ssf:ifaccessible>

</tbody>
</table>
<input type="submit" value="ok" style="height:10px; width:10px; margin-left: -8000px;"
  onClick="ss_addTag${ss_tagViewNamespace}('${ss_tagDivNumber}', '${ss_tagObject.entityType}', '${ss_tagObject.id}');return false;"/>
</form>
</div>

</ssf:popupPane>

</div>

</c:if>