
if (!window.teamMembersCountLoaded)
	window.teamMembersCountLoaded = new Array();
if (!window.teamMembersLoaded)
	window.teamMembersLoaded  = new Array();
if (!window.teamMembersLoadUrl)
	window.teamMembersLoadUrl  = new Array();
if (!window.teamMembersClickRoutine)
	window.teamMembersClickRoutine  = new Array();

function setTeamMembersVariables(prefix, url, clickRoutine) {
	teamMembersLoadUrl[prefix]  = url;
	teamMembersClickRoutine[prefix]  = clickRoutine;
}


function displayTeamMembersMenu(url, prefix) {
	if (teamMembersCountLoaded[prefix]) {
		var aR = new ss_AjaxRequest("");
		aR.setData("prefix", prefix);	
		displayTeamMembersMenu_postRequest(aR);
		return;
	}		

	url += "&ss_divId=teamMembersAmount_" + prefix;
	
	var ajaxRequest = new ss_AjaxRequest(url);	
	ajaxRequest.setPostRequest(displayTeamMembersMenu_postRequest);
	ajaxRequest.setUseGET();
	ajaxRequest.setData("prefix", prefix);
	ajaxRequest.sendRequest();
}

function displayTeamMembersMenu_postRequest(ajaxRequest) {
	var prefix = ajaxRequest.getData("prefix");
	var divObj = document.getElementById("teamMenu_" + prefix);
	ss_moveDivToBody("teamMenu_" + prefix);
	ss_setObjectTop(divObj, parseInt(ss_getDivTop("teamIcon_" + prefix)) + + ss_getDivWidth("teamIcon_" + prefix))
	ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("teamIcon_" + prefix)))
	ss_showDivActivate("teamMenu_" + prefix);	
	
	if (teamMembersLoaded[prefix] || teamMembersCountLoaded[prefix]) 
		return;
	
	teamMembersCountLoaded[prefix] = true;
	var ulObj = document.getElementById("teamMembersListUL_" + prefix);
	if (ulObj) {
		var getAllLIObj = document.createElement("li");
		getAllLIObj.setAttribute("class", "getAllUsers");
		getAllLIObj.setAttribute("id", "teamMembersList_" + prefix);
		getAllLIObj.setAttribute("onmouseover", "this.style.backgroundColor='#333'; this.style.color='#FFF'; loadTeamMembers('" + prefix + "');");
		getAllLIObj.setAttribute("onmouseout", "this.style.backgroundColor='#FFF'; this.style.color='#333';");
		
		var getAllIMGObj = document.createElement("img");
		getAllIMGObj.setAttribute("class", "getAllUsers");
		getAllIMGObj.setAttribute("border", "0");
		getAllIMGObj.setAttribute("src", ss_imagesPath + "pics/sym_s_collapse.gif");

		getAllLIObj.appendChild(getAllIMGObj);
		ulObj.appendChild(getAllLIObj);
	}
}

function addAllUsersFromTeam(ajaxRequest) {
	var prefix = ajaxRequest.getData("prefix");
	var ulObj = document.getElementById('teamMembersListUL_' + prefix);
	var lisObj = ulObj.getElementsByTagName("li");
	for (var i = 0; i < lisObj.length; i++) {
		if (lisObj[i].getElementsByTagName("a") && 
			lisObj[i].getElementsByTagName("a").length > 0 &&
			lisObj[i].getElementsByTagName("a").item(0).onclick) {
			lisObj[i].getElementsByTagName("a").item(0).onclick();
		}
	}
}

function loadTeamMembers(prefix, afterPostRoutine) {
	if (teamMembersLoaded[prefix]) {
		if (afterPostRoutine) {
			var aR = new ss_AjaxRequest("");
			aR.setData("prefix", prefix);	
			afterPostRoutine(aR);
		}
		return;
	}
	toggleAjaxLoadingIndicator("teamMembersList_" + prefix, false);
	teamMembersLoaded[prefix] = true;
	
	url = teamMembersLoadUrl[prefix];
	url += "&ss_divId=teamMembersList_" + prefix;
	url += "&clickRoutine=" + teamMembersClickRoutine[prefix];
	
	var ajaxRequest = new ss_AjaxRequest(url);	
	if (afterPostRoutine)
		ajaxRequest.setPostRequest(afterPostRoutine);
	ajaxRequest.setUseGET();
	ajaxRequest.setData("prefix", prefix);
	ajaxRequest.sendRequest();
}

		