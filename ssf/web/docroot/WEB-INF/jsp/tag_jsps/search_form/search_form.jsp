<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="ssSearchFormData" type="java.util.Map" scope="request" />
<script type="text/javascript" src="<html:rootPath/>js/datepicker/CalendarPopup.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/AnchorPosition.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/PopupWindow.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/datepicker/date.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/tree/tree_widget.js"></script>

<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/find/single_tag.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/find/single_user.js"></script>


<script type="text/javascript">

function ss_getFilterTypeSelection(obj, op2) {
	var divObj = obj.parentNode.parentNode.parentNode;
	ss_getFilterSelectionBox(divObj, 'typeList', 'get_search_form_filter_type', op2)
}
function ss_getFilterSelectionBox(obj, nameRoot, op, op2) {
	ss_setupStatusMessageDiv()
	var formObj = ss_getContainingForm(obj)
	//Set the term number
	var nameObj = obj.name
	if (!obj.name) nameObj = obj.id;
	var termNumber = nameObj.substr(nameRoot.length, nameObj.length)
	formObj.ss_filterTermNumber.value = parseInt(termNumber);
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="binderId" value="${ssBinder.id}"/>
    	</ssf:url>"
    url += "&operation=" + op;
    if (op2 != null && op2 != "") url += "&operation2=" + op2;

	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements(formObj.name);

	if (op2 == 'tags') { 
		url = url+ "&"+ ajaxRequest.getQueryString()+"&ss_formName=formObj.name";
		ss_fetch_url(url, showTagComponents, termNumber);
//		alert("after fetch_url");
	} else if (op2 == 'entry') {
//		alert(obj.id + ' entry'); 
		url = url+ "&"+ ajaxRequest.getQueryString()+"&ss_formName=formObj.name";
		ss_fetch_url(url, showEntryComponents, [termNumber, nameRoot]);
	} else {
//		ajaxRequest.setEchoDebugInfo();
		ajaxRequest.setUsePOST();
		ajaxRequest.sendRequest();  //Send the request
	}
}
function showTagComponents(response, ind) {
// 	alert("response: "+response);
	
	document.getElementById("entryList"+ind).innerHTML = response;
	document.getElementById("entryList"+ind).style.disply="block";
	document.getElementById("entryList"+ind).style.visibility="visible";
	document.getElementById("elementList"+ind).innerHTML="";
	document.getElementById("elementList"+ind).style.visibility="hidden";
	document.getElementById("valueList"+ind).innerHTML="";
	document.getElementById("valueList"+ind).style.visibility="hidden";
}
function showEntryComponents (response, params) {
//	alert("response: "+response);
	var ind = params[0];
	var objId = params[1];
// 	alert("Ind: "+ind+" objId: "+objId);
	if (objId == "typeList") {
		// alert("level1");
		document.getElementById("entryList"+ind).innerHTML = response;
		document.getElementById("entryList"+ind).style.disply="block";
		document.getElementById("entryList"+ind).style.visibility="visible";
		document.getElementById("elementList"+ind).innerHTML="";
		document.getElementById("elementList"+ind).style.visibility="hidden";
		document.getElementById("valueList"+ind).innerHTML="";
		document.getElementById("valueList"+ind).style.visibility="hidden";
		
	} else if (objId == "ss_entry_def_id") {
		// alert("level2");
		document.getElementById("elementList"+ind).innerHTML=response;
		document.getElementById("elementList"+ind).style.disply="block";
		document.getElementById("elementList"+ind).style.visibility="visible";
		document.getElementById("valueList"+ind).innerHTML="";
		document.getElementById("valueList"+ind).style.visibility="hidden";		
	} else {
		// alert("level3");
		document.getElementById("valueList"+ind).innerHTML=response;
		document.getElementById("valueList"+ind).style.disply="block";
		document.getElementById("valueList"+ind).style.visibility="visible";	
	}
	
}

var ss_filterTermNumber = 0;
var ss_filterTermNumberMax = 0;
function ss_addFilterTerm() {
	ss_filterTermNumberMax++;
	var tableDiv = document.getElementById('filterTerms');
	var tbl = document.createElement("table");
	tbl.className = "ss_style";
	var tableBody = document.createElement("tbody");
	tbl.appendChild(tableBody);
	var row = document.createElement("tr");
	tableBody.appendChild(row);
	
	//Add the td cells 
	tdCell = document.createElement("td");
	tdCell.vAlign = "top"
	var typeListDiv = document.getElementById('typeList1');
	var newTypeListDiv = typeListDiv.cloneNode(true);
	newTypeListDiv.id = "typeList" + parseInt(ss_filterTermNumberMax);
	tdCell.appendChild(newTypeListDiv)
	row.appendChild(tdCell);
	
	tdCell = document.createElement("td");
	tdCell.vAlign = "top"
	var newEntryListDiv = document.createElement("div");
	newEntryListDiv.id = "entryList" + parseInt(ss_filterTermNumberMax);
	tdCell.appendChild(newEntryListDiv)
	row.appendChild(tdCell);
	
	tdCell = document.createElement("td");
	tdCell.vAlign = "top"
	var elementListDiv = document.createElement("div");
	elementListDiv.id = "elementList" + parseInt(ss_filterTermNumberMax);
	tdCell.appendChild(elementListDiv)
	row.appendChild(tdCell);
	
	tdCell = document.createElement("td");
	tdCell.vAlign = "top"
	var valueListDiv = document.createElement("div");
	valueListDiv.id = "valueList" + parseInt(ss_filterTermNumberMax);
	tdCell.appendChild(valueListDiv)
	var valueDataDiv = document.createElement("div");
	valueDataDiv.id = "valueData" + parseInt(ss_filterTermNumberMax);
	tdCell.appendChild(valueDataDiv)
	row.appendChild(tdCell);

	tdCell = document.createElement("td");
	tdCell.vAlign = "top"
	row.appendChild(tdCell);

	var br = document.createElement("br");
	tableDiv.appendChild(br);
	tableDiv.appendChild(tbl);
	//alert(tbl.innerHTML)
}

var ss_buttonSelected = "";
function ss_buttonSelect(btn) {
	ss_buttonSelected = btn
}
function ss_checkFilterForm(obj) {
	//Set the term numbers into the form
	var formObj = document.getElementById('${ssSearchFormForm}')
	formObj.ss_filterTermNumber.value = parseInt(ss_filterTermNumber);
	formObj.ss_filterTermNumberMax.value = parseInt(ss_filterTermNumberMax);
	if (ss_buttonSelected == 'ok' && obj.searchFormName && obj.searchFormName.value == "") {
		obj.searchFormName.focus()
		return false;
	}
	return true;
}
ss_createOnSubmitObj('ss_checkFilterForm', '${ssSearchFormForm}', ss_checkFilterForm);

function ss_deleteFilterTerm(obj, termNumber) {
	var formObj = ss_getContainingForm(obj)
	//Set the term number into the form
	formObj.ss_filterTermNumber.value = parseInt(termNumber);
	return true;
}

function t_searchForm_wsTree_showId(forum, obj) {
	if (self.document.${ssSearchFormForm}["ss_sf_id_"+forum] && self.document.${ssSearchFormForm}["ss_sf_id_"+forum].checked) {
		self.document.${ssSearchFormForm}["ss_sf_id_"+forum].checked=false
	} else {
		self.document.${ssSearchFormForm}["ss_sf_id_"+forum].checked=true
	}
	return false
}


function putValueInto(inputId, value) {
	document.getElementById(inputId).value = value;
}
var currentEntryCreatorIndex = 1;

function saveCurrentEntryClick(ind) {
	currentEntryCreatorIndex = ind;
}

function rewriteValueIntoFormElement(id, obj) {
//	alert("ID: "+id);
//	alert("Obj: "+obj+" \n"+obj.innerHTML);
	var spanObj = obj.getElementsByTagName("span").item(0);
//	alert("User: "+spanObj.innerHTML);
//	alert(document.getElementById("elementValue"+currentEntryCreatorIndex));
//	alert("currentEntryCreatorIndex: "+currentEntryCreatorIndex);
//	alert(document.getElementsByName("elementValue"+currentEntryCreatorIndex).length);
	document.getElementsByName("elementValue"+currentEntryCreatorIndex).item(0).value = spanObj.innerHTML;
	document.getElementById("elementValue"+currentEntryCreatorIndex).value=id;
//	alert(document.getElementById("elementValue"+currentEntryCreatorIndex).value);
//	alert(document.getElementsByName("elementValue"+currentEntryCreatorIndex).item(0).value);
}

tagSearchResultUrl = "<portlet:actionURL windowState="maximized" portletMode="view">
			<portlet:param name="action" value="search"/>
			<portlet:param name="searchCommunityTags" value="ss_tagPlaceHolder"/>
			<portlet:param name="searchPersonalTags" value="ss_tagPlaceHolder"/>
			<portlet:param name="searchTags" value="searchTagsOr"/>
			<portlet:param name="tabTitle" value="ss_tagPlaceHolder"/>
			<portlet:param name="newTab" value="1"/>
			</portlet:actionURL>";
// ss_declareFindTagSearchVariables();
</script>
<!-- p> TAG SSF:TREE START</p -->
<ssf:tree 
  treeName="t_searchForm_wsTree" 
  treeDocument="${ssDomTree}"  
  rootOpen="false" 
  multiSelect="<%= new ArrayList() %>" 
  multiSelectPrefix="ss_sf_id_" 
  initOnly="true" />

<!-- p>AFTER TAG SSF TREE</p -->


  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="searchForm.terms"/></legend>
	<span class="ss_bold"><ssf:nlt tag="searchForm.selectFilterType"/></span>
	<br/>
	<div id="filterTerms">
	  <table class="ss_style">
	  <tbody>
	  <c:if test="${empty ss_selectedSearchForm || ssSearchFormData.filterTermCount == '0'}">
	  <tr>
	  <td valign="top">
	    <div id="typeList1" style="display:inline;">
	      <ul class="ss_square" style="margin:0px 14px; padding:2px;">
	      <li><a href="javascript: ;" 
	        onClick="ss_getFilterTypeSelection(this, 'text');return false;">
	          <ssf:nlt tag="searchForm.searchText"/>
	      </a></li>
	      
	      <li><a href="javascript: ;" 
	        onClick="ss_getFilterTypeSelection(this, 'entry');return false;">
	          <ssf:nlt tag="searchForm.entryAttributes"/>
	      </a></li>

	      <li><a href="javascript: ;" 
	        onClick="ss_getFilterTypeSelection(this, 'workflow');return false;">
	          <ssf:nlt tag="searchForm.workflowStates"/>
	      </a></li>

	      <li><a href="#" 
	        onClick="ss_getFilterTypeSelection(this, 'folders');return false;">
	          <ssf:nlt tag="filter.folders"/>
	      </a></li>

	      <li><a href="#" onClick="ss_getFilterTypeSelection(this, 'tags');return false;">
	          <ssf:nlt tag="filter.tags"/>
	      </a></li>

	    </div>
	  </td>
	  <td valign="top">
	    <div id="entryList1" style="display:inline;">
	    </div>
	  </td>
	  <td valign="top">
	    <div id="elementList1" style="visibility:hidden; display:inline;">
	    </div>
	  </td>
	  <td valign="top">
	    <div id="valueList1" style="visibility:hidden; display:inline;">
	    </div>
	    <div id="valueData1" style="visibility:hidden; display:inline;">
	    </div>
	  </td>
	  <td></td>
	  </tr>
<script type="text/javascript">
ss_filterTermNumberMax++;
</script>
	  </c:if>
	  
	  <c:if test="${!empty ss_selectedSearchForm && !empty ssSearchFormData.filterTermCount}">
<%
		for (int i = 1; i <= ((Integer)ssSearchFormData.get("filterTermCount")).intValue(); i++) {
%>
		  <tr>
		  <td valign="top">
		    <div id="typeList<%= String.valueOf(i) %>" style="display:inline;">
		      <ul class="ss_square" style="margin:0px 14px; padding:2px;">
		      <li><a href="javascript: ;" 
		        onClick="ss_getFilterTypeSelection(this, 'text');return false;">
		          <ssf:nlt tag="searchForm.searchText" text="Search text"/>
		      </a></li>
		      
		      <li><a href="javascript: ;" 
		        onClick="ss_getFilterTypeSelection(this, 'entry');return false;">
		          <ssf:nlt tag="searchForm.entryAttributes" text="Entry attributes"/>
		      </a></li>
	
		      <li><a href="javascript: ;" 
		        onClick="ss_getFilterTypeSelection(this, 'workflow');return false;">
		          <ssf:nlt tag="searchForm.workflowStates" text="Workflow states"/>
		      </a></li>
	
		    </div>
		  </td>
		  <td valign="top">
		    <div id="entryList<%= String.valueOf(i) %>" style="display:inline;">
<%
			if (!((String) ssSearchFormData.get("filterType" + String.valueOf(i))).equals("text")) {
%>
		      <select name="ss_entry_def_id<%= String.valueOf(i) %>" size="1" multiple>
<%
				if (ssSearchFormData.containsKey("ss_entry_def_id" + String.valueOf(i))) {
%>
				<option value="<%= (String) ssSearchFormData.get("ss_entry_def_id" + String.valueOf(i)) %>" 
				  selected><%= (String) ssSearchFormData.get("ss_entry_def_id_caption" + String.valueOf(i)) %></option>
<%
				}
%>
		      </select>
<%
			}
%>
			  <input type="hidden" name="filterType<%= String.valueOf(i) %>"
			    value="<%= (String) ssSearchFormData.get("filterType" + String.valueOf(i)) %>"/>
		    </div>
		  </td>
		  <td valign="top">
		    <div id="elementList<%= String.valueOf(i) %>" style="visibility:visible; display:inline;">
<%
			if (((String) ssSearchFormData.get("filterType" + String.valueOf(i))).equals("text")) {
%>
				<span><ssf:nlt tag="searchForm.searchText" text="Search text"/>:</span>
<%
			} else if (ssSearchFormData.containsKey("elementName" + String.valueOf(i))) {
%>
				<select name="elementName<%= String.valueOf(i) %>" size="1" multiple>
				  <option value="<%= (String) ssSearchFormData.get("elementName" + String.valueOf(i)) %>" selected>
				    <%= (String) ssSearchFormData.get("elementNameCaption" + String.valueOf(i)) %></option>
				</select>
<%
			}
%>
		    </div>
		  </td>
		  <td valign="top">
		    <div id="valueList<%= String.valueOf(i) %>" style="visibility:visible; display:inline;">
<%
			if (((String) ssSearchFormData.get("filterType" + String.valueOf(i))).equals("text")) {
				if (ssSearchFormData.containsKey("elementValue" + String.valueOf(i))) {
					String value = (String) ssSearchFormData.get("elementValue" + String.valueOf(i));
%>
				<input type="text" class="ss_text" name="elementValue<%= String.valueOf(i) %>" style="width:150px;" 
				  value="<%= value.replaceAll("\\\"", "\\\"") %>">
<%
				}
			} else {
				if (ssSearchFormData.containsKey("elementValue" + String.valueOf(i))) {
					Map valueMap = (Map) ssSearchFormData.get("elementValue" + String.valueOf(i));
%>
				<select name="elementValue<%= String.valueOf(i) %>" size="<%= String.valueOf(valueMap.entrySet().size()) %>" multiple>
<%
					Iterator itValues = valueMap.entrySet().iterator();
					while (itValues.hasNext()) {
						String value = (String)((Map.Entry)itValues.next()).getKey();
						%>
						<option name="<%= value %>" selected><%= value %></option>
						<%
					}
%>
				</select>
<%
				}
			}
%>
		    </div>
		    <div id="valueData<%= String.valueOf(i) %>" 
		      style="visibility:visible; display:inline;">
<%
			if (((String) ssSearchFormData.get("filterType" + String.valueOf(i))).equals("text")) {
			} else {
				if (ssSearchFormData.containsKey("elementValue" + String.valueOf(i))) {
					Map valueMap = (Map) ssSearchFormData.get("elementValue" + String.valueOf(i));
%>
					<select name="elementValue<%= String.valueOf(i) %>" 
					  size="<%= String.valueOf(valueMap.entrySet().size()) %>" multiple>
<%
					Iterator itValues = valueMap.entrySet().iterator();
					while (itValues.hasNext()) {
						String value = (String)((Map.Entry)itValues.next()).getKey();
						%>
						<option name="<%= value %>" selected><%= value %></option>
						<%
					}
%>
					</select>
					<select name="elementValueData<%= String.valueOf(i) %>" 
					  size="<%= String.valueOf(valueMap.entrySet().size()) %>" multiple>
					    <option>xxxxxxxxxxxx</option>
					</select>
<%
				}
			}
%>
		    </div>
		  </td>
		  <td valign="top">
		    <input type="submit" class="ss_fineprint" name="deleteTerm" 
		      value="<ssf:nlt tag="button.delete" text="Delete this term"/>" 
		      onClick="ss_deleteFilterTerm(this, '<%= String.valueOf(i) %>');" />
		  </td>
		  </tr>
<script type="text/javascript">
ss_filterTermNumberMax++;
</script>
<%
		}
%>
	  </c:if>
	  	  
	  </tbody>
	  </table>
	</div>
	<br/>
	<a class="ss_linkButton" href="javascript: ;" onClick="ss_addFilterTerm();return false;"><ssf:nlt tag="search.addFilterTerm"/></a>
	  
  </fieldset>
  
<input type="hidden" name="ss_filterTermNumber"/>
<input type="hidden" name="ss_filterTermNumberMax"/>
<input type="hidden" name="selectedSearchFilter" value="<c:out value="${ss_selectedFilter}"/>"/>
<input type="hidden" name="ss_filterTags" />
<!-- p> AFTER SEARCH FORM</p -->
