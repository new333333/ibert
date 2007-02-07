//Routines to display an expandable/contractable tree
//
function ss_treeToggle(treeName, id, parentId, bottom, type) {
	if (ss_treeDisplayStyle && ss_treeDisplayStyle == 'accessible') {
		return ss_treeToggleAccessible(treeName, id, parentId, bottom, type);
	}
	ss_setupStatusMessageDiv()
    var tObj = self.document.getElementById(treeName + "div" + id);
    var jObj = self.document.getElementById(treeName + "join" + id);
    var iObj = self.document.getElementById(treeName + "icon" + id);
    eval("var showTreeIdRoutine = ss_treeShowIdRoutine_"+treeName+";");
    if (tObj == null) {
    	//alert("ss_treeToggle div obj = null: " + treeName + "div" + id)
    } else {
    	//alert("ss_treeToggle id: " + tObj.id)
    }
    if (tObj == null) {
        //The div hasn't been loaded yet. Go get the div via ajax
		eval("var url = ss_treeAjaxUrl_" + treeName);
		url = ss_replaceSubStrAll(url, "&amp;", "&");
		var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
		ajaxRequest.addKeyValue("binderId", id)
		ajaxRequest.addKeyValue("treeName", treeName)
		ajaxRequest.addKeyValue("showIdRoutine", showTreeIdRoutine)
		ajaxRequest.setData("treeName", treeName)
		ajaxRequest.setData("id", id)
		ajaxRequest.setData("parentId", parentId)
		ajaxRequest.setData("bottom", bottom)
		ajaxRequest.setData("type", type)
	    eval("var seObj = ss_treeSelected_"+treeName); 	    
	    //add single select id
	    if (seObj != null) {
	    	ajaxRequest.addKeyValue("select", seObj);
	    }
		//ajaxRequest.setEchoDebugInfo();
		//ajaxRequest.setPreRequest(ss_preRequest);
		ajaxRequest.setPostRequest(ss_postTreeDivRequest);
		ajaxRequest.setUsePOST();
		ajaxRequest.sendRequest();  //Send the request
    } else {
	    if (tObj.style.display == "none" || tObj.style.visibility == 'hidden') {
	        tObj.style.display = "block";
	        tObj.style.visibility = 'visible';
			if (bottom == 0) {
				if (parentId == "") {
					jObj.className = "ss_twMinusTop";	   // minus_top.gif
				} else {
					jObj.className = "ss_twMinus";	       // minus.gif
				}
			} else if (bottom == 1) {
				if (parentId == "") {
					jObj.className = "ss_twMinusTopBottom";   // minus_top_bottom.gif
				} else {
					jObj.className = "ss_twMinusBottom";      // minus_bottom.gif
				}
			} else {
				if (parentId == "") {
					jObj.className = "ss_minusTop";        // minus_top.gif (no join lines)
				} else {
					jObj.className = "ss_minus";           // minus.gif (no join lines)
				}
			}
			if (iObj != null && ss_treeIconsOpen[type]) iObj.src = ss_treeIconsOpen[type];
	    } else {
	        tObj.style.display = "none";
	        tObj.style.visibility = 'hidden';
			if (bottom == 0) {
				if (parentId == "") {
					jObj.className = "ss_twPlusTop";	  // plus_top.gif
				} else {
					jObj.className = "ss_twPlus";	      // plus.gif
				}
			} else if (bottom == 1) {
				if (parentId == "") {
					jObj.className = "ss_twPlusTopBottom";	  // plus_top_bottom.gif
				} else {
					jObj.className = "ss_twPlusBottom";	      // plus_bottom.gif
				}
			} else {
				if (parentId == "") {
					jObj.className = "ss_plusTop";        // plus_top.gif (no join lines)
				} else {
					jObj.className = "ss_plus";           // plus.gif (no join lines)
				}
			}
			if (iObj != null && ss_treeIconsClosed[type]) iObj.src = ss_treeIconsClosed[type];
	    }
		//Signal that the layout changed
		if (ssf_onLayoutChange) ssf_onLayoutChange();
		
		self.focus();
	}
}

function ss_treeToggleAccessible(treeName, id, parentId, bottom, type) {
    var tempObj = self.document.getElementById(treeName + "temp" + id);
    var tempObjParent = self.parent.document.getElementById(treeName + "temp" + id);
    var iframeDivObj = self.document.getElementById("ss_treeIframeDiv");
    var iframeObj = self.document.getElementById("ss_treeIframe");
    var iframeDivObjParent = self.parent.document.getElementById("ss_treeIframeDiv");
    var iframeObjParent = self.parent.document.getElementById("ss_treeIframe");
    var jObj = self.document.getElementById(treeName + "join" + id);
    var iObj = self.document.getElementById(treeName + "icon" + id);
    eval("var showTreeIdRoutine = ss_treeShowIdRoutine_"+treeName+";");
    if (iframeDivObjParent == null && iframeDivObj == null) {
	    iframeDivObj = self.document.createElement("div");
	    iframeDivObjParent = iframeDivObj;
        iframeDivObj.setAttribute("id", "ss_treeIframeDiv");
		iframeDivObj.className = "ss_treeIframeDiv";
        iframeObj = self.document.createElement("iframe");
        iframeObj.setAttribute("id", "ss_treeIframe");
        iframeObj.style.width = "400px"
        iframeObj.style.height = "250px"
		iframeDivObj.appendChild(iframeObj);
	    var closeDivObj = self.document.createElement("div");
	    closeDivObj.style.border = "2px solid gray";
	    closeDivObj.style.marginTop = "1px";
	    closeDivObj.style.padding = "6px";
	    iframeDivObj.appendChild(closeDivObj);
	    var aObj = self.document.createElement("a");
	    aObj.setAttribute("href", "javascript: ss_hideDiv('ss_treeIframeDiv');");
	    aObj.style.border = "2px outset black";
	    aObj.style.padding = "2px";
	    aObj.appendChild(document.createTextNode(ss_treeButtonClose));
	    closeDivObj.appendChild(aObj);
		self.document.getElementsByTagName( "body" ).item(0).appendChild(iframeDivObj);
    }
    if (iframeDivObj == null) iframeDivObj = iframeDivObjParent;
    if (iframeObj == null) iframeObj = iframeObjParent;
    if (self.parent == self) {
    	var x = dojo.html.getAbsolutePosition(tempObj, true).x
    	var y = dojo.html.getAbsolutePosition(tempObj, true).y
	    ss_setObjectTop(iframeDivObj, y + "px");
	    ss_setObjectLeft(iframeDivObj, x + "px");
	}
	ss_showDiv("ss_treeIframeDiv");
	eval("var url = ss_treeAjaxUrl_" + treeName);
	url = ss_replaceSubStrAll(url, "&amp;", "&");
	url += "&binderId=" + id;
	url += "&treeName=" + treeName;
	if (showTreeIdRoutine != '') url += "&showIdRoutine=" + showTreeIdRoutine;
	url += "&parentId=" + parentId;
	url += "&bottom=" + bottom;
	url += "&type=" + type;
	eval("var seObj = ss_treeSelected_"+treeName); 	    
	//add single select id
	if (seObj != null) {
	   	url += "&select=" + seObj;
	}
	
    if (iframeDivObjParent != null && iframeDivObjParent != iframeDivObj) {
		self.location.href = url;
	} else {
		iframeObj.src = url;
	}
}
function ss_clearSingleSelect(treeName) {
	eval("ss_treeSelected_" + treeName + "=null;");
	return true;
}
function ss_createTreeCheckbox(treeName, prefix, id) {
	var divObj = document.getElementById("ss_hiddenTreeDiv"+treeName);
	var cbObj = document.getElementById("ss_tree_checkbox" + treeName + prefix + id)
	if (cbObj == null) {
		//alert("null: ss_tree_checkbox" + treeName + prefix + id)
	} else {
		//alert("Not null: ss_tree_checkbox" + treeName + prefix + id)
	}
}

function ss_positionAccessibleIframe(treeName, id) {
	ss_debug('position: '+ parent.ss_getDivTop(treeName + "temp" + id))
    var iframeDivObj = self.document.getElementById("ss_treeIframeDiv");
	ss_setObjectTop(iframeDivObj, ss_getDivTop(treeName + "temp" + id));
	ss_setObjectLeft(iframeDivObj, ss_getDivLeft(treeName + "temp" + id));
	ss_showDiv(iframeDivObj);
}

function ss_postTreeDivRequest(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_treeNotLoggedInMsg);
	} else {
		ss_treeOpen(obj.getData('treeName'), obj.getData('id'), obj.getData('parentId'), obj.getData('bottom'), obj.getData('type'));
	}
}

function ss_treeOpen(treeName, id, parentId, bottom, type) {
    var tObj = self.document.getElementById(treeName + "div" + id);
    var jObj = self.document.getElementById(treeName + "join" + id);
    var iObj = self.document.getElementById(treeName + "icon" + id);
    if (tObj == null) {
    	//alert("ss_treeOpen div obj = null: " + treeName + "div" + id)
    } else {
    	//alert("ss_treeOpen id: " + tObj.id)
    }
    if (tObj == null) {
    	return;
    } else {
        tObj.style.display = "block";
        tObj.style.visibility = 'visible';
		if (jObj != null) {
			if (bottom == '0') {
				if (parentId == "") {
					jObj.className = "ss_twMinusTop";	     // minus_top.gif
				} else {
					jObj.className = "ss_twMinus";	         // minus.gif
				}
			} else if (bottom == '1') {
				if (parentId == "") {
					jObj.className = "ss_twMinusTopBottom";	 // minus_top_bottom.gif
				} else {
					jObj.className = "ss_twMinusBottom";	 // minus_bottom.gif
				}
			} else {
				if (parentId == "") {
					jObj.className = "ss_minus_top";         // minus_top.gif (no join lines)
				} else {
					jObj.className = "ss_minus";             // minus.gif (no join lines)
				}
			}
		}
		if (iObj != null && ss_treeIconsOpen[type]) iObj.src = ss_treeIconsOpen[type];

		//Signal that the layout changed
		if (ssf_onLayoutChange) ssf_onLayoutChange();
		
		self.focus();
	}
}

function ss_treeToggleAll(treeName, id, parentId, bottom, type) {
    var tObj = self.document.getElementById(treeName + "div" + id);
	if (tObj.style.display == "none") {
		ss_treeToggle(treeName, id, parentId, bottom, type)
	}
    var children = tObj.childNodes;
    for (var i = 0; i < children.length; i++) {
    	if (children[i].id && children[i].id.indexOf(treeName + "div") == 0) {
			var nodeRoot = treeName + "div";
			var childnode = children[i].id.substr(nodeRoot.length)
    		if (children[i].style.display == "none") {
    			ss_treeToggle(treeName, childnode, id, bottom, type)
    		}
    		ss_treeToggleAll(treeName, childnode, id, bottom, type)
    	}
    }
}

var ss_treeIcons = new Array();
var ss_treeIconsClosed = new Array();
var ss_treeIconsOpen = new Array();
function ssTree_defineBasicIcons(imageBase) {
	// Basic icons
	
	ss_treeIcons['root'] = imageBase + "/trees/root.gif";
	ss_treeIcons['spacer'] = imageBase + "/trees/spacer.gif";
	ss_treeIcons['line'] = imageBase + "/trees/line.gif";
	ss_treeIcons['join'] = imageBase + "/trees/join.gif";
	ss_treeIcons['join_bottom'] = imageBase + "/trees/join_bottom.gif";
	ss_treeIcons['minus'] = imageBase + "/trees/minus.gif";
	ss_treeIcons['minus_bottom'] = imageBase + "/trees/minus_bottom.gif";
	ss_treeIcons['plus'] = imageBase + "/trees/plus.gif";
	ss_treeIcons['plus_bottom'] = imageBase + "/trees/plus_bottom.gif";
	ss_treeIconsClosed['folder'] = imageBase + "/trees/folder.gif";
	ss_treeIconsOpen['folder'] = imageBase + "/trees/folder_open.gif";
	ss_treeIconsClosed['page'] = imageBase + "/trees/page.gif";
	
	// More icons
	
	ss_treeIconsClosed['doc'] = imageBase + "/trees/file_types/doc.gif";
	ss_treeIconsClosed['pdf'] = imageBase + "/trees/file_types/pdf.gif";
	ss_treeIconsClosed['ppt'] = imageBase + "/trees/file_types/ppt.gif";
	ss_treeIconsClosed['rtf'] = imageBase + "/trees/file_types/rtf.gif";
	ss_treeIconsClosed['sxc'] = imageBase + "/trees/file_types/sxc.gif";
	ss_treeIconsClosed['sxi'] = imageBase + "/trees/file_types/sxi.gif";
	ss_treeIconsClosed['sxw'] = imageBase + "/trees/file_types/sxw.gif";
	ss_treeIconsClosed['txt'] = imageBase + "/trees/file_types/txt.gif";
	ss_treeIconsClosed['xls'] = imageBase + "/trees/file_types/xls.gif";
}

//var dragsort = ss_ToolMan.dragsort()
function ss_setSortable(treeName) {
	//Sorting turned off because it doesn't work in IE - pmh
	//dragsort.makeListSortable(document.getElementById(treeName), ss_verticalOnly, ss_saveTreeOrder)
}

function ss_verticalOnly(item) {
	//item.ss_ToolManDragGroup.verticalOnly()
}

function ss_saveTreeOrder(item) {
	//var group = item.ss_ToolManDragGroup
	//group.register('dragend', ss_save)
	//group.setThreshold(4)
	//var list = group.element.parentNode
	//var id = list.getAttribute("id")
	//if (id == null) return
}

function ss_save(item) {
	//alert(item.group.element.id)
}


