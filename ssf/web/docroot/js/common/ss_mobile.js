/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

//These routines are a copy of routines in ss_common
//We use ss_mobile to minimize the size of the common javascript routines loaded

//Routine to create a new "onLoadObj" object
//onLoadObj objects are set up whenever you want to call something at onLoad time.
var ss_onLoadList = new Array();
var ss_onLoadRoutineLoaded;
function ss_createOnLoadObj(name, initName) {
    for (var i = 0; i < ss_onLoadList.length; i++) {
    	if (ss_onLoadList[i].name == name) return;
    }
    var next = ss_onLoadList.length;
    ss_onLoadList[next] = new onLoadObj(name);
    ss_onLoadList[next].setInitRoutine(initName);
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

//Routine called by the body's onLoad event
function ss_onLoadInit() {
    //Call any routines that want to be called at onLoad time
    for (var i = 0; i < ss_onLoadList.length; i++) {
        if (ss_onLoadList[i].initRoutine) {
        	ss_onLoadList[i].initRoutine();
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

//Mailto: replacement routines
function ss_showEmailLinks() {
	var mailtoElements = document.getElementsByTagName('ssmailto')
	while (mailtoElements != null && mailtoElements.length > 0) {
		var mailtoName = mailtoElements[0].getAttribute("name");
		mailtoName = ss_replaceSubStrAll(mailtoName, "<", "&lt;")
		mailtoName = ss_replaceSubStrAll(mailtoName, ">", "&gt;")
		var mailtoHost = mailtoElements[0].getAttribute("host");
		mailtoHost = ss_replaceSubStrAll(mailtoHost, "<", "&lt;")
		mailtoHost = ss_replaceSubStrAll(mailtoHost, ">", "&gt;")
		var mailtoNoLink = mailtoElements[0].getAttribute("noLink");
		var aNode = mailtoElements[0].parentNode;
		if (mailtoNoLink == "true") {
			aNode.innerHTML = mailtoName+"@"+mailtoHost;
		} else {
			aNode.setAttribute("href", "mailto:"+mailtoName+"@"+mailtoHost);
			aNode.removeChild(mailtoElements[0]);
			spanObj = self.document.createElement("span");
			spanObj.innerHTML = mailtoName+"@"+mailtoHost;
			aNode.appendChild(spanObj);
		}
		mailtoElements = document.getElementsByTagName('ssmailto')
	}
}

function ss_replaceSubStrAll(str, subStr, newSubStrVal) {
    if (typeof str == 'undefined') return str;
    var newStr = str;
    var i = -1
    //Prevent a possible loop by only doing 1000 passes through this loop
    while (1000) {
        i = newStr.indexOf(subStr, i);
        var lenS = newStr.length;
        var lenSS = subStr.length;
        if (i >= 0) {
            newStr = newStr.substring(0, i) + newSubStrVal + newStr.substring(i+lenSS, lenS);
            i += newSubStrVal.length
        } else {
            break;
        }
    }
    return newStr;
}

//Micro Bolg routines
function ss_clearStatusMobile(textareaId) {
	var obj = document.getElementById(textareaId);
	if (obj && typeof obj.value != "undefined") {
		obj.value = "";
		try {obj.focus();} catch(e){}
	}
}


//jQuery Functions

$(document).ready(function(){

//login/logout
	$("#loginbut, #logout-a").click(function () {
   if ($("#login-pg").is(":hidden")) {
     $("#pages").hide();
     $("#login-pg").fadeIn("normal");
   } else {
     $("#login-pg").hide();
     $("#pages").fadeIn("normal");
 	$("#actions-menu").hide();
   }
 });


//home - mystuff navigation

	$(".homelink, .homelink-menu").click(function () {
 	$("#myws-pg, #blog-pg, #blogEntry-pg, #actions-menu").hide();
 	$("#home-pg").fadeIn("fast");
 });
	$(".myws-a").click(function () {
 	$("#home-pg").hide();
 	$("#myws-pg").fadeIn("fast");
 });
	$(".blog-a").click(function () {
 	$("#home-pg, #blogEntry-pg").hide();
 	$("#blog-pg").fadeIn("fast");
 });
	$("#blog-entry-a").click(function () {
 	$("#blog-pg").hide();
 	$("#blogEntry-pg").fadeIn("fast");
 });
	$("#back-to-blog-a").click(function () {
 	$("#blogEntry-pg").hide();
 	$("#blog-pg").fadeIn("fast");
 });
	$(".back-to-ws").click(function () {
 	$("#blog-pg").hide();
 	$("#ws-pg").fadeIn("fast");
 });
	$(".back-to-blog").click(function () {
 	$("#blogEntry-pg").hide();
 	$("#blog-pg").fadeIn("fast");
 });


	$(".search").click(function () {
 	$("#search-dialog").slideDown("normal");
 });
	$("#search-ok, #search-cancel").click(function () {
 	$("#search-dialog").slideUp("normal");
 });

	$(".hierarchy-a").click(function () {
 	$("#hierarchy-dialog").slideDown("normal");
 });
	$("#hierarchy-ok, #hierarchy-cancel").click(function () {
 	$("#hierarchy-dialog").slideUp("normal");
 });




//toggle dialog content/edit

	$(".actions-a").click(function () {
 	$("#actions-menu").slideDown("normal");
 });
	$("#actions-menu-close").click(function () {
 	$("#actions-menu").slideUp("normal");
 });
	$(".micro-blog-a").click(function () {
 	$("#micro-blog-edit").slideDown("normal");
 });
	$("#micro-blog-post, #micro-blog-cancel").click(function () {
 	$("#micro-blog-edit").slideUp("normal");
 });

	$(".new-blog-entry-a").click(function () {
 	$("#BlogEntry-new").slideDown("fast");
 });
	$("#blog-entry-post, #blog-entry-cancel").click(function () {
 	$("#BlogEntry-new").slideUp("normal");
 });


});

