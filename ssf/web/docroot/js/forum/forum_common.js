//Common javascript functions for forum portlets

//   Copyright (c) 2005 / SiteScape, Inc.  All Rights Reserved.
//
//  This information in this document is subject to change without notice 
//  and should not be construed as a commitment by SiteScape, Inc.  
//  SiteScape, Inc. assumes no responsibility for any errors that may appear 
//  in this document.
//
//  Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
//  is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
//  Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
//
//  SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
//
//
// browser-specific vars

var jsDebug = 0;

var isNSN = (navigator.appName == "Netscape");
var isNSN4 = isNSN && ((navigator.userAgent.indexOf("Mozilla/4") > -1));
var isNSN6 = ((navigator.userAgent.indexOf("Netscape6") > -1));
var isMoz5 = ((navigator.userAgent.indexOf("Mozilla/5") > -1) && !isNSN6);
var isMacIE = ((navigator.userAgent.indexOf("IE ") > -1) && (navigator.userAgent.indexOf("Mac") > -1));
var isIE = ((navigator.userAgent.indexOf("IE ") > -1));

//Routine called by the body's onLoad event
var ss_savedOnLoadRoutine = null;
var ss_onLoadRoutineLoaded;
function ss_onLoadInit() {
    //Call any routines that want to be called at onLoad time
    for (var name in onLoadList) {
        if (onLoadList[name].initRoutine) {
        	onLoadList[name].initRoutine();
        }
    }
    if (ss_savedOnLoadRoutine != null) {
    	window.onload = ss_savedOnLoadRoutine;
    	if (window.onload != null) window.onload();
    }
}

//Add the onLoadInit routine to the onload event
if (!ss_onLoadRoutineLoaded) {
	ss_onLoadRoutineLoaded = 1;
	ss_savedOnLoadRoutine = window.onload;
	window.onload = ss_onLoadInit;
}

//Routine to open a url in the portlet. This routine determines if the current code is
//  running inside an iframe. It it is, then the url is opened in the parent of the iframe.
//This routine returns "true" without opening the url if the caller is not inside a frame.
//If the caller is in a frame (or iframe), then the routine opens the url in the parent and returns false.
function ss_openUrlInPortlet(url, popup) {
	//Is this a request to pop up?
	if (popup) {
		self.window.open(url, "_blank", "directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no");
		return;
	}
	//Are we at the top window?
	if (self.window != self.top) {
		parent.location.href = url;
		return false
	} else if (self.opener) {
		self.opener.location.href = url
		setTimeout('self.window.close();', 200)
		return false
	} else {
		return true
	}
}

function ss_reloadOpener(fallBackUrl) {
	//Are we at the top window?
	if (self.window != self.top) {
		parent.location.reload();
		return false
	} else if (self.opener) {
		self.opener.location.reload(true)
		setTimeout('self.window.close();', 200)
		return false
	} else {
		self.location.href = fallBackUrl;
		return false
	}
}

// Replace an image (e.g. expand/collapse arrows)
function ss_replaceImage(imgName, imgPath) {
    if (document.images) {
        eval('if (document.images[\''+imgName+'\']) {document.images[\''+imgName+'\'].src = imgPath}');
    }
}


//Routine to show or hide an object
function ss_showHideObj(objName, visibility, displayStyle) {
    var obj
    if (isNSN || isNSN6 || isMoz5) {
        obj = self.document.getElementById(objName)
    } else {
        obj = self.document.all[objName]
    }
    if (obj && obj.style) {
	    if (obj.style.visibility != visibility) {
		    obj.style.visibility = visibility;
		    obj.style.display = displayStyle;
		}
	} else {
		if (jsDebug) {alert('Div "'+objName+'" does not exist. (ss_showHideObj)')}
	}
}

//Routine to add html to a div
function ss_addToDiv(target, source) {
    var objTarget
    var objSource
    if (isNSN || isNSN6 || isMoz5) {
        objTarget = self.document.getElementById(target)
        objSource = self.document.getElementById(source)
    } else {
        objTarget = self.document.all[target]
        objSource = self.document.all[source]
    }
    var targetHtml = ss_getDivHtml(target)
    var sourceHtml = ss_getDivHtml(source)
    ss_setDivHtml(target, targetHtml + sourceHtml)
}

//Routines to get and set the html of an area
function ss_getDivHtml(divId) {
    var obj
    if (isNSN || isNSN6 || isMoz5) {
        obj = self.document.getElementById(divId)
    } else {
        obj = self.document.all[divId]
    }
    var value = "";
    if (obj) {
    	value = obj.innerHTML;
    }
    return value;
}

function ss_setDivHtml(divId, value) {
    var obj
    if (isNSN || isNSN6 || isMoz5) {
        obj = self.document.getElementById(divId)
    } else {
        obj = self.document.all[divId]
    }
    if (obj) {
    	obj.innerHTML = value
    }
}

//Routines for the definition builder
var ss_declaredDivs = new Array();
function ss_setDeclaredDiv(id) {
	ss_declaredDivs[id] = id;
}

function ss_hideAllDeclaredDivs() {
	for (var i in ss_declaredDivs) {
		ss_showHideObj(ss_declaredDivs[i], 'hidden', 'none')
	}
}

var eventList = new Array();
var eventTypeList = new Array();
//Routine to create a new "eventObj" object
//eventObj objects are set up whenever you want to call a routine on an event.
//   event_name is the event name (e.g., "MOUSEDOWN")
function createEventObj(function_name, event_name) {
    if (eventList[function_name] == null) {
        eventList[function_name] = new eventObj(function_name);
        eventList[function_name].setEventName(event_name);
    }
    if (eventTypeList[event_name] == null) {
        eventTypeList[event_name] = event_name
        //Enable the event
        if (isNSN) {
            eval("self.document.captureEvents(Event."+event_name+")")
        }
        eval("self.document.on"+eventList[function_name].eventName+" = ssf_event_handler;")
    }
}
function eventObj(function_name) {
    this.functionName = function_name;
    this.eventName = null;
    this.setEventName = m_setEventName;
    this.callEventFunction = this.functionName;
}
function m_setEventName(event_name) {
    this.eventName = event_name.toLowerCase();
}

//Common event handler
//  This function will call the desired routines on an event
function ssf_event_handler(e) {
    if (!isNSN) {e = event}
    for (var n in eventList) {
        if (e.type.toLowerCase() == eventList[n].eventName) {
            eval(eventList[n].functionName+'(e)');
        }
    }
}


var onLoadList = new Array();
//Routine to create a new "onLoadObj" object
//onLoadObj objects are set up whenever you want to call something at onLoad time.
function createOnLoadObj(name, initName) {
    if (onLoadList[name] == null) {
        onLoadList[name] = new onLoadObj(name);
        onLoadList[name].setInitRoutine(initName);
    }
}
function onLoadObj(name) {
    this.name = name;
    this.initRoutine = null;
    this.getInitRoutine = m_getInitRoutine;
    this.setInitRoutine = m_setInitRoutine;
}
function m_getInitRoutine() {
    return this.initRoutine;
}
function m_setInitRoutine(initRoutine) {
    this.initRoutine = initRoutine;
}

var onSubmitList = new Array();
//Routine to create a new "onSubmitObj" object
//onSubmitObj objects are set up whenever you want to call something at form submit time.
function createOnSubmitObj(name, formName, submitRoutine) {
    if (onSubmitList[name] == null) {
        onSubmitList[name] = new onSubmitObj(name, formName);
        onSubmitList[name].setSubmitRoutine(submitRoutine);
    }
}
function onSubmitObj(name, formName) {
    this.name = name;
    this.formName = formName;
    this.submitRoutine = null;
    this.getSubmitRoutine = m_getSubmitRoutine;
    this.setSubmitRoutine = m_setSubmitRoutine;
}
function m_getSubmitRoutine() {
    return this.submitRoutine;
}
function m_setSubmitRoutine(submitRoutine) {
    this.submitRoutine = submitRoutine;
}

//Common onSubmit handler
//  This function will call the desired routines at form submit time
//  If any routine returns "false", then this routine returns false.
function ssf_onSubmit(obj) {
    for (var i in onSubmitList) {
        if (onSubmitList[i].formName == obj.name) {
            if (!onSubmitList[i].submitRoutine()) {return false;}
        }
    }
    return true;
}


var onResizeList = new Array();
//Routine to create a new "onResizeObj" object
//onResizeObj objects are set up whenever you want to call something at onResize time.
function createOnResizeObj(name, resizeName) {
    if (onResizeList[name] == null) {
        onResizeList[name] = new onResizeObj(name);
        onResizeList[name].setResizeRoutine(resizeName);
    }
}
function onResizeObj(name) {
    this.name = name;
    this.resizeRoutine = null;
    this.getResizeRoutine = m_getResizeRoutine;
    this.setResizeRoutine = m_setResizeRoutine;
}
function m_getResizeRoutine() {
    return this.resizeRoutine;
}
function m_setResizeRoutine(resizeRoutine) {
    this.resizeRoutine = resizeRoutine;
}
function ssf_onresize_event_handler() {
    for (var n in onResizeList) {
        onResizeList[n].resizeRoutine();
    }
}

function getObjAbsX(obj) {
    var x = 0
    var parentObj = obj
    while (parentObj.offsetParent && parentObj.offsetParent != '') {
        x += parentObj.offsetParent.offsetLeft
        parentObj = parentObj.offsetParent
    }
    return x
}

function getObjAbsY(obj) {
    var y = 0
    var parentObj = obj
    while (parentObj.offsetParent && parentObj.offsetParent != '') {
        y += parentObj.offsetParent.offsetTop
        parentObj = parentObj.offsetParent
    }
    return y
}

function getDivTop(divName) {
    var top = 0;
    if (isNSN || isNSN6 || isMoz5) {
        var obj = self.document.getElementById(divName)
        while (1) {
            if (!obj) {break}
            top += parseInt(obj.offsetTop)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else {
        var obj = self.document.all[divName]
        while (1) {
            if (!obj) {break}
            top += obj.offsetTop
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    }
    return parseInt(top);
}

function getDivHeight(divName) {
    if (isNSN || isNSN6 || isMoz5) {
        var obj = self.document.getElementById(divName)
    } else {
        var obj = self.document.all[divName]
    }
    return parseInt(obj.offsetHeight);
}

function getDivWidth(divName) {
    if (isNSN || isNSN6 || isMoz5) {
        var obj = self.document.getElementById(divName)
    } else {
        var obj = self.document.all[divName]
    }
    return parseInt(obj.offsetWidth);
}


function getLeftMargin(defValue) {
    var left = defValue;
    if (isNSN6 || isMoz5) {
        if (self.document.body.style.marginLeft && self.document.body.style.marginLeft != '') {
            left = self.document.body.style.marginLeft
        }
    } else if (isNSN) {
    } else {
        if (self.document.body.leftMargin) {
            left = self.document.body.leftMargin
        }
    }
    return parseInt(left);
}

function getAnchorTop(anchorName) {
    var top = 0;
    if (isNSN6 || isMoz5) {
        var obj = document.anchors[anchorName]
        while (1) {
            if (!obj) {break}
            top += parseInt(obj.offsetTop)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else if (isNSN) {
        top = document.anchors[anchorName].y
    } else {
        var obj = document.all[anchorName]
        while (1) {
            if (!obj) {break}
            top += obj.offsetTop
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    }
    return parseInt(top);
}

function getAnchorLeft(anchorName) {
    var left = 0;
    if (isNSN6 || isMoz5) {
        var obj = document.anchors[anchorName]
        while (1) {
            if (!obj) {break}
            left += parseInt(obj.offsetLeft)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else if (isNSN) {
        left = document.anchors[anchorName].x
    } else {
        var obj = document.all[anchorName]
        while (1) {
            if (!obj) {break}
            left += obj.offsetLeft
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    }
    return parseInt(left);
}

function getImageTop(imageName) {
    var top = 0;
    if (isNSN6 || isMoz5) {
        var obj = document.images[imageName]
        while (1) {
            if (!obj) {break}
            top += parseInt(obj.offsetTop)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else if (isNSN) {
        top = document.images[imageName].y
    } else {
        var obj = document.all[imageName]
        while (1) {
            if (!obj) {break}
            top += obj.offsetTop
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    }
    return parseInt(top);
}

function getImageLeft(imageName) {
    var left = 0;
    if (isNSN6 || isMoz5) {
        var obj = document.images[imageName]
        while (1) {
            if (!obj) {break}
            left += parseInt(obj.offsetLeft)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else if (isNSN) {
        left = document.images[imageName].x
    } else {
        var obj = document.all[imageName]
        while (1) {
            if (!obj ) {break}
            left += obj.offsetLeft
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    }
    return parseInt(left);
}

function getObjectWidth(obj) {
    if (isNSN6 || isMoz5) {
        return parseInt(obj.offsetWidth)
    } else if (isNSN) {
        return parseInt(obj.clip.width)
    } else {
        return parseInt(obj.clientWidth)
    }
}

function getObjectHeight(obj) {
    if (isNSN6 || isMoz5) {
        return parseInt(obj.offsetHeight)
    } else if (isNSN) {
        return parseInt(obj.clip.height)
    } else {
        return parseInt(obj.clientHeight)
    }
}

function getObjectLeft(obj) {
    if (isNSN6 || isMoz5) {
        return parseInt(obj.style.left)
    } else if (isNSN) {
        return parseInt(obj.style.left)
    } else {
        return parseInt(obj.style.pixelLeft)
    }
}

function getObjectTop(obj) {
    if (isNSN6 || isMoz5) {
        return parseInt(obj.style.top)
    } else if (isNSN) {
        return parseInt(obj.style.top)
    } else {
        return parseInt(obj.style.pixelTop)
    }
}

function setObjectWidth(obj, width) {
	obj.style.width = width;
	return

    if (isNSN6 || isMoz5) {
        obj.offsetWidth = width;
    } else if (isNSN) {
        obj.clip.width = width;
    } else {
        obj.clientWidth = width;
    }
}

function setObjectHeight(obj, height) {
    obj.style.height = height;
    return
    
    if (isNSN6 || isMoz5) {
        obj.offsetHeight = height;
    } else if (isNSN) {
        obj.clip.height = height;
    } else {
        obj.clientHeight = height;
    }
}

function setObjectLeft(obj, value) {
    if (isNSN6 || isMoz5) {
        obj.style.left = value;
    } else if (isNSN) {
        obj.style.left = value;
    } else {
        obj.style.pixelLeft = value;
    }
}

function setObjectTop(obj, value) {
    if (isNSN6 || isMoz5) {
        obj.style.top = value;
    } else if (isNSN) {
        obj.style.top = value;
    } else {
        obj.style.pixelTop = value;
    }
}

function getWindowWidth() {
    if (isNSN) {
        return parseInt(self.window.innerWidth)
    } else {
        return self.document.body.clientWidth
    }
}

function getWindowHeight() {
    if (isNSN) {
        return parseInt(self.window.innerHeight)
    } else {
        return self.document.body.clientHeight
    }
}

function getBodyHeight() {
    var h = self.document.body.scrollHeight;
    if (getWindowHeight() > h) {
        h = getWindowHeight();
    }
    return h
}

function getBodyWidth() {
    var w = self.document.body.scrollWidth;
    if (getWindowWidth() > w) {
        w = getWindowWidth();
    }
    return w
}

function smoothScroll(x, y) {
	smoothScrollInTime(x,y,10)
}

function smoothScrollInTime(x, y, steps) {
    if (steps <= 1) {
		window.scroll(x,y)
    } else {
	    var bodyX = self.document.body.scrollLeft
	    var bodyY = self.document.body.scrollTop
	    if (bodyX < x) {
	    	var newX = parseInt(bodyX + ((x - bodyX) / steps))
	    } else {
	    	var newX = parseInt(bodyX - ((bodyX - x) / steps))
	    }
	    if (bodyY < y) {
	    	var newY = parseInt(bodyY + ((y - bodyY) / steps))
	    } else {
	    	var newY = parseInt(bodyY - ((bodyY - y) / steps))
	    }
		window.scroll(newX, newY)
		steps = steps - 1
		eval("setTimeout('smoothScrollInTime("+x+", "+y+", "+steps+")', 8)")
    }
}

// Pop-up menu support
// clicking anywhere will hide the div
var active_menulayer = '';
var lastActive_menulayer = '';
var active_menulayer_form = 0;

function activateMenuLayer(divId, delayHide) {
    // don't do anything if the divs aren't loaded yet
    if (isNSN6 || isMoz5) {
        if (self.document.getElementById(divId) == null) {return}
    } else if (isNSN) {
        var nn4obj = getNN4DivObject(divId)
        if (nn4obj == null) {return}
    } else {
        if (self.document.all[divId] == null) {return}
    }

    var x = getClickPositionX();
    var y = getClickPositionY();
    //Add a little to the y position so the div isn't occluding too much
    y = parseInt(y) + 10

    var maxWidth = 0;
    var divWidth = 0;

    if (isNSN6 || isMoz5) {
        // need to bump layer an extra bit to the right to avoid horiz scrollbar
        divWidth = parseInt(self.document.getElementById(divId).offsetWidth) + 25;
        maxWidth = parseInt(window.innerWidth);
    } else if (isNSN) {    
        var nn4obj = getNN4DivObject(divId)
        divWidth = parseInt(nn4obj.clip.width);
        maxWidth = parseInt(window.innerWidth);
    } else {
        divWidth = parseInt(self.document.all[divId].clientWidth) + 25;
        maxWidth = parseInt(document.body.scrollWidth);
    }

    if (x + divWidth > maxWidth) {
        x = maxWidth - divWidth;
    } 
  
    ShowHideDivXY(divId, x, y, delayHide);
    HideDivOnSecondClick(divId);
}

// activate_menulayer tests this flag to make sure the page is
// loaded before the pulldown menus are clicked.
var layerFlag = 0;
function setLayerFlag() {
    layerFlag = 1;
}

// Clears (hides) the active menulayer (if any)
function clearActive_menulayer() {
    if (active_menulayer_form) {return}
    active_menulayer_form = 0;

    lastActive_menulayer = active_menulayer;
    if (active_menulayer != '') {
        menulayerId = active_menulayer;
        hideMenu(menulayerId);
        active_menulayer = '';
    }     
    if (self.clearActiveMenu) {self.clearActiveMenu()}
}

//Enable the event handler
createEventObj('clearActive_menulayer', 'MOUSEUP')

createOnLoadObj('layerFlag', setLayerFlag);



//Support for positioning divs at x,y 
var ss_mouseX = 0;
var ss_mouseY = 0;
var ss_mousePosX = 0;
var ss_mousePosY = 0;
var divBeingShown = null;
var lastDivBeingShown = null;
var divToBeHidden = new Array;
var divToBeDelayHidden = new Array;

//Enable the event handler
createEventObj('captureXY', 'MOUSEUP')

//General routine to show a div given its name and coordinates
function ShowHideDivXY(divName, x, y, noHideSpannedAreas) {
    if (divBeingShown == divName) {
        hideDiv(divBeingShown)
        divBeingShown = null;
        lastDivBeingShown = null;
    } else {
        if (lastDivBeingShown == divName) {
            lastDivBeingShown = null;
            return
        }
        lastDivBeingShown = null;
        if (divBeingShown != null) {
            hideDiv(divBeingShown)
        }
        divBeingShown = divName;
        lastDivBeingShown = divName;
        positionDiv(divBeingShown, x, y)
        showDiv(divBeingShown, noHideSpannedAreas)
    }
}

//General routine to show a div given its name
function HideDivIfActivated(divName) {
    if (divBeingShown == divName) {
        hideDiv(divBeingShown)
        divBeingShown = null;
        lastDivBeingShown = null;
    }
}

//Routine to make div's be hidden on next click
function HideDivOnSecondClick(divName) {
    divToBeHidden[divName] = true;
}

//Routine to make div's be hidden on next click
function NoHideDivOnNextClick(divName) {
    divToBeDelayHidden[divName] = true;
}

function hideDiv(divName) {
    hideElement(divName);
    divToBeDelayHidden[divBeingShown] = null
    divBeingShown = null;
    
    //Show any spanned areas that may have been turned off
    showSpannedAreas()
}

function hideElement(divName) { 
    if (isNSN6) {
        //positionDiv(divName, -10000, -10000)
        document.getElementById(divName).style.visibility = "hidden";
    } else if (isMoz5) {
        document.getElementById(divName).style.visibility = "hidden";
    } else if (isNSN) {
        var nn4obj = getNN4DivObject(divName)
        nn4obj.visibility = "hidden"
    } else {
        self.document.all[divName].style.visibility = "hidden"
    }
}

function showDiv(divName, noHideSpannedAreas) {
    //Hide any area that has elements that might bleed through
    if (noHideSpannedAreas == null) {
        hideSpannedAreas()
    }
    showElement(divName);
}

function showElement(divName) {
    if (isNSN6 || isMoz5) {
        document.getElementById(divName).style.visibility = "visible";
    } else if (isNSN) {
        var nn4obj = getNN4DivObject(divName)
        nn4obj.visibility = "visible";
    } else {
        self.document.all[divName].style.visibility = "visible";
    }
}

function positionDiv(divName, x, y) {
    if (isNSN6 || isMoz5) {
        self.document.getElementById(divName).style.left= (x - parseInt(self.document.getElementById(divName).offsetParent.offsetLeft)) + "px"
        self.document.getElementById(divName).style.top= (y - parseInt(self.document.getElementById(divName).offsetParent.offsetTop)) + "px"
    } else if (isNSN) {
        var nn4obj = getNN4DivObject(divName)
        nn4obj.left=x
        nn4obj.top=y
    } else {
        self.document.all[divName].style.left=x - self.document.all[divName].offsetParent.offsetLeft
        self.document.all[divName].style.top=y - self.document.all[divName].offsetParent.offsetTop
    }
}

function getClickPositionX() {
    return ss_mousePosX
}

function getClickPositionY() {
    return ss_mousePosY
}

function getClickPositionX() {
    return ss_mousePosX
}

function getClickPositionY() {
    return ss_mousePosY
}

//Routines to get an object handle given the id name of a div
function getNN4DivObject(divName) {
    for (var n = 0; n < self.document.layers.length; n++) {
        var obj = getNN4DivObjectObj(self.document.layers[n], divName)
        if (obj != null) {return obj}
    }
    alert('getNN4DivObject error: unknown div id - '+divName)
    return self.document
}

function getNN4DivObjectObj(obj, divName) {
    if (obj.name == divName) {return obj}
    for (var n = 0; n < obj.document.layers.length; n++) {
        var obj1 = getNN4DivObjectObj(obj.document.layers[n], divName)
        if (obj1 != null) {return obj1}
    }
    return null
}

function captureXY(e) {
    if (!e) e = window.contents.event;

    //Is this click a "right click"? If yes, ignore it
    if (e && e.which && (e.which == 3 || e.which == 2)) {
        return false;
    } else if (e && e.button && (e.button == 2 || e.button == 3)) {
        return false;
    }

    //See if there is a div to be hidden
    lastDivBeingShown = divBeingShown;
    if (divBeingShown != null) {
        if (divToBeHidden[divBeingShown]) {
            if (divToBeDelayHidden[divBeingShown]) {
                divToBeDelayHidden[divBeingShown] = null
            } else {
                hideDiv(divBeingShown)
                divBeingShown = null;
            }
        }
    }
    if (isNSN6 || isMoz5) {
        ss_mousePosX = e.pageX
        ss_mousePosY = e.pageY
        ss_mouseX = e.layerX
        ss_mouseY = e.layerY
        return(true)
    } else if (isNSN) {
        ss_mousePosX = e.x
        ss_mousePosY = e.y
        ss_mouseX = e.layerX
        ss_mouseY = e.layerY
        var imgObj = getNN4ImgObject(e.layerX, e.layerY)
        if (imgObj != null) {
            ss_mouseX = imgObj.x
            ss_mouseY = imgObj.y
        }
        return(true)
    } else {
        //ss_mousePosX = event.x + self.document.body.scrollLeft
        //ss_mousePosY = event.y + self.document.body.scrollTop
        ss_mousePosX = event.clientX + self.document.body.scrollLeft;
        ss_mousePosY = event.clientY + self.document.body.scrollTop;
        ss_mouseX = event.clientX;
        ss_mouseY = event.clientY;
        var imgObj = window.event.srcElement
        if (imgObj.name != null && imgObj.name != "" && !isMacIE) {
            ss_mouseX = getImageLeft(imgObj.name)
            ss_mouseY = getImageTop(imgObj.name)
        }
    }
}

//Routines to get an object handle given the x,y coordinates of the image
function getNN4ImgObject(imgX, imgY) {
    var imgObj = getNN4ImgObjectObj(self, imgX, imgY)
    return imgObj
}

function getNN4ImgObjectObj(divObj, imgX, imgY) {
    //Look in this div for the image
    for (var i = 0; i < divObj.document.images.length; i++) {
        var testImgObj = divObj.document.images[i]
        if ( testImgObj && testImgObj.x &&   (imgX >= testImgObj.x) && 
                (imgX <= testImgObj.x + testImgObj.width) && 
                (imgY >= testImgObj.y) && 
                (imgY <= testImgObj.y + testImgObj.height)    ) {
                return(testImgObj)
        }
    }
    //The image isn't in this div, look in the children divs
    for (var n = 0; n < divObj.document.layers.length; n++) {
        var testObj = divObj.document.layers[n]
        var imgObj = getNN4ImgObjectObj(testObj, imgX, imgY)
        if (imgObj != null) {return imgObj}
    }
    return null
}

//Routines to get a div handle of the owner of an image
function getNN4ImgDivObject(imgObj) {
    var divObj = getNN4ImgDivObjectObj(self, imgObj)
    return divObj
}

function getNN4ImgDivObjectObj(divObj, imgObj) {
    //Look in this div for the image
    for (var i = 0; i < divObj.document.images.length; i++) {
        var testImgObj = divObj.document.images[i]
        if (testImgObj == imgObj) {
            return(divObj)
        }
    }
    //The image isn't in this div, look in the children divs
    for (var n = 0; n < divObj.document.layers.length; n++) {
        var testDivObj = divObj.document.layers[n]
        var testImgObj = getNN4ImgDivObjectObj(testDivObj, imgObj)
        if (testImgObj != null) {return testDivObj}
    }
    return null
}

var onErrorList = new Array();
//Routine to create a new "onErrorObj" object
//onErrorObj objects are set up whenever you want to call something at onError time.
function createOnErrorObj(name, onErrorName) {
    if (onErrorList[name] == null) {
        onErrorList[name] = new onErrorObj(name);
        onErrorList[name].setOnErrorRoutine(onErrorName);
        window.onerror = ssf__onError_event_handler
    }
}
function onErrorObj(name) {
    this.name = name;
    this.onErrorRoutine = null;
    this.getOnErrorRoutine = m_getOnErrorRoutine;
    this.setOnErrorRoutine = m_setOnErrorRoutine;
}
function m_getOnErrorRoutine() {
    return this.onErrorRoutine;
}
function m_setOnErrorRoutine(onErrorRoutine) {
    this.onErrorRoutine = onErrorRoutine;
}
function ssf__onError_event_handler() {
    var ret = false
    for (var n in onErrorList) {
        if (onErrorList[n].onErrorRoutine()) {ret = true}
    }
    return ret
}

var spannedAreasList = new Array();
//Routine to create a new "spannedArea" object
//spannedAreaObj objects are set up whenever you need some form elements to be 
//   blanked when showing the menus
function createSpannedAreaObj(name) {
    if (spannedAreasList[name] == null) {
        spannedAreasList[name] = new spannedAreaObj(name);
    }
    return spannedAreasList[name];
}
function spannedAreaObj(name) {
    this.name = name;
    this.showArgumentString = '';
    this.hideArgumentString = '';
    this.showRoutine = null;
    this.show = m_showSpannedArea;
    this.setShowRoutine = m_setShowRoutine;
    this.hideRoutine = null;
    this.hide = m_hideSpannedArea;
    this.setHideRoutine = m_setHideRoutine;
}
function m_setShowRoutine(showRoutine) {
    this.showRoutine = showRoutine;
    //See if there are any other arguments passed in
    //  These will get passed on to the show routine
    for (var i = 1; i < m_setShowRoutine.arguments.length; i++) {
        if (this.showArgumentString != '') {this.showArgumentString += ',';}
        this.showArgumentString += '"'+m_setShowRoutine.arguments[i]+'"';
    }
}
function m_setHideRoutine(hideRoutine) {
    this.hideRoutine = hideRoutine;
    //See if there are any other arguments passed in
    //  These will get passed on to the show routine
    for (var i = 1; i < m_setHideRoutine.arguments.length; i++) {
        if (this.hideArgumentString != '') {this.hideArgumentString += ',';}
        this.hideArgumentString += '"'+m_setHideRoutine.arguments[i]+'"';
    }
}
function m_showSpannedArea() {
    eval(this.showRoutine+'('+this.showArgumentString+');')
}
function m_hideSpannedArea() {
    eval(this.hideRoutine+'('+this.hideArgumentString+');')
}

function toggleSpannedAreas(spanName,newValue) {
    if (isNSN6 || isMoz5) {
        if (document.getElementById(spanName) != null) {
            document.getElementById(spanName).style.visibility = newValue;
        }    
    } else {  
        if (self.document.layers && document.layers[spanName] != null) {
            self.document.layers[spanName].visibility = newValue;
        } else if (self.document.all && document.all[spanName] != null) {
            self.document.all[spanName].style.visibility = newValue;
        }
    }
}

function hideSpannedAreas() {
    //Hide the various standard form elements used by the product 
    toggleSpannedAreas('attrformspan','hidden')
    toggleSpannedAreas('attrform2span','hidden')
    toggleSpannedAreas('viewentry','hidden')

    //Hide any form elements that may be visible
    for (var name in spannedAreasList) {
        spannedAreasList[name].hide()
    }
}

function showSpannedAreas() {
    //Make the standard form elements visible again
    toggleSpannedAreas('attrformspan','visible')
    toggleSpannedAreas('attrform2span','visible')
    toggleSpannedAreas('viewentry','visible')

    //Show any form elements that should be returned to the visible state
    for (var name in spannedAreasList) {
        spannedAreasList[name].show()
    }
}

//Routine to open a url in a new window
function openUrlInWindow(obj,windowName) {
	if (windowName == "") {
		//There is no window, so open it in this window
		return true;
	} else {
		var url = obj.href
		self.window.open(url, windowName, 'directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no')
	}
	return false;
}

//Routine to show a div at the bottom of the highest size attained by the window
var ss_forum_maxBodyWindowHeight = 0;
function setWindowHighWaterMark(divName) {
	var currentPageHeight = getBodyHeight()
	if (parseInt(ss_forum_maxBodyWindowHeight) < parseInt(currentPageHeight)) {
		//Time to set a new high water mark
		ss_forum_maxBodyWindowHeight = currentPageHeight;
	}
	var dh = getDivHeight(divName);
	var x = 0
	var y = parseInt(ss_forum_maxBodyWindowHeight - dh)
	positionDiv(divName, x, y);
	showDiv(divName)
}

//Routines to support getting stuff from the server without reloading the page
function getXMLObj() {
	var req;
    // branch for native XMLHttpRequest object
    if (window.XMLHttpRequest) {
        req = new XMLHttpRequest();
    // branch for IE/Windows ActiveX version
    } else if (window.ActiveXObject) {
        req = new ActiveXObject("Microsoft.XMLHTTP");
    }
    return req;
}

function fetch_url(url, callbackRoutine) {
	if (url == undefined) {return}
	fetch_url_debug("Request to fetch url: " + url)
	var x;
	x = getXMLObj();
	x.open("GET", url, true);
	x.onreadystatechange = function() {
		if (x.readyState != 4) {
			return;
		}
		fetch_url_debug("received " + x.responseText);
        if (x.status == 200) {
        	callbackRoutine(x.responseText)        	
        } else {
        	callbackRoutine(x.statusText)
        }
	}
	x.send(null);
	fetch_url_debug(" waiting... url = " + url);
	delete x;
}                

function fetch_url_debug(str) {
    //alert(str);
}

//if (!Array.prototype.push) {
	function array_push() {
		for (var i = 0; i < arguments.length; i++) {
			this[this.length] = arguments[i];
		}
	}

	Array.prototype.push = array_push;
//}

//if (!Array.prototype.pop) {
	function array_pop() {
		if (this.length <= 0) {return ""}
		lastElement = this[this.length - 1];
		this.length = Math.max(this.length - 1, 0);

		if (lastElement == undefined) {lastElement = ""}
		return lastElement;
	}

	Array.prototype.pop = array_pop;
//}

