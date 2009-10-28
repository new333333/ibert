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

/*
 * Returns a string contining information from an array of
 * SSTrashEntry's for embedding in an AJAX url.  
 */
function ss_buildTrashEntryUrlParams(entries) {
	var count = ((null == entries) ? 0 : entries.length);
	var reply = new Array();
	for (var i = 0; i < count; i += 1) {
		reply[reply.length] = entries[i].serialize();
	}
	return(ss_pack(reply));
}

/*
 * Called when the selects 'Purge' from the trash viewer's menu bar.
 */
function ss_trashPurge() {
	// If we don't have any trash entries...
	if (0 == g_trashEntriesCount) {
		// ...there's nothing to purge.  Bail!
		return;
	}
	
	// Have any entries been selected to purge?
	var checkedEntries = getCheckedTrashEntries();
	var count = ((null == checkedEntries) ? 0 : checkedEntries.length);
	if (0 == count) {
		// No!  Tell the user about the problem and bail.
		alert(g_trashStrings["trash.error.NoItemsSelected"]);
		return;
	}

	// If the user is sure they want to do purge...	
	if (confirm(g_trashStrings["trash.confirm.Purge"])) {
		// ...do it.
		var urlParams = ss_buildTrashEntryUrlParams(checkedEntries);
		ajaxTrashRequest_Submit("trash_purge", urlParams);
	}
}

/*
 * Called when the selects 'Purge All' from the trash viewer's menu
 * bar.
 */
function ss_trashPurgeAll() {
	// If we don't have any trash entries...
	if (0 == g_trashEntriesCount) {
		// ...there's nothing to purge.  Bail!
		return;
	}
	
	// If the user is sure they want to do purge...	
	if (confirm(g_trashStrings["trash.confirm.PurgeAll"])) {
		// ...do it.
		ajaxTrashRequest_Submit("trash_purge_all", "");
	}
}

/*
 * Called when the selects 'Restore' from the trash viewer's menu bar.
 */
function ss_trashRestore() {
	// If we don't have any trash entries...
	if (0 == g_trashEntriesCount) {
		// ...there's nothing to restore.  Bail!
		return;
	}
	
	// Have any entries been selected to restore?
	var checkedEntries = getCheckedTrashEntries();
	var count = ((null == checkedEntries) ? 0 : checkedEntries.length);
	if (0 == count) {
		// No!  Tell the user about the problem and bail.
		alert(g_trashStrings["trash.error.NoItemsSelected"]);
		return;
	}
	
	var urlParams = ss_buildTrashEntryUrlParams(checkedEntries);
	ajaxTrashRequest_Submit("trash_restore", urlParams);
}

/*
 * Called when the selects 'Restore All' from the trash viewer's menu
 * bar.
 */
function ss_trashRestoreAll() {
	// If we don't have any trash entries...
	if (0 == g_trashEntriesCount) {
		// ...there's nothing to restore.  Bail!
		return;
	}
	
	ajaxTrashRequest_Submit("trash_restore_all", "");
}

/*
 * Called when the user checks/unchecks the 'Select All' checkbox in
 * the trash viewer's menu bar.
 */
function ss_trashSelectAll(eCBox) {
	// If we don't have any trash entries...
	if (0 == g_trashEntriesCount) {
		// ...there's nothing to select.  Bail!
		return;
	}
	
	var bChecked = eCBox.checked;
	for (var i = 0; i < g_trashEntriesCount; i += 1) {
		eCBox = g_trashEntries[i].getCheckbox();
		eCBox.checked = bChecked;
		ss_trashSelectOne(eCBox);
	}
}

/*
 * Called when an individual item's checkbox is checked/unchecked in
 * the trash viewer.
 * 
 * This may seem unnecessary, but is required to work in conjunction
 * with ss_showMouseOverInfo()/ss_clearMouseOverInfo() in
 * slider_table.js.
 */
function ss_trashSelectOne(eCBox) {
	var eCBoxDIV = document.getElementById(eCBox.id + "_DIV");
	var sCBoxDIV = eCBoxDIV.innerHTML;
	if (eCBox.checked) {
		sCBoxDIV = ss_trashStrReplace(sCBoxDIV, "class=", "checked=\"checked\" class=");
	}
	else {
		sCBoxDIV = ss_trashStrReplace(sCBoxDIV, "checked=\"checked\" class=", "class=");
	}
	eCBoxDIV.innerHTML = sCBoxDIV;
}

/*
 * Replaces one occurence of sPattern in sIn with sReplace. 
 */
function ss_trashStrReplace(sIn, sPattern, sReplace) {
	// If sIn contains an occurence of sPattern...
	var i = sIn.indexOf( sPattern );
	if (0 <= i) {
		// ...replace it with sReplace.
		sIn = (sIn.substring(0, i) + sReplace + sIn.substring(i + sPattern.length));
	}


	// If we get here, sIn contains the original sIn with one
	// occurence of sPattern replaced with sReplace.  Return it.
	return(sIn);
}

/*
 * Returns an Array of those SSTrashEntry's whose selection checkbox
 * is checked.
 */	
function getCheckedTrashEntries() {
	var reply = new Array();
	var trashEntry;
	var eCBox;
	for (var i = 0; i < g_trashEntriesCount; i += 1) {
		trashEntry = g_trashEntries[i];
		eCBox = trashEntry.getCheckbox();
		if (eCBox.checked) {
			reply[reply.length] = trashEntry;
		}
	}
	return(reply);
}

/*
 * Searches for an SSTrashEntry whose ID is docId and whose type is
 * docType.  If one is found, it's returned.  Otherwise, null is
 * returned. 
 */
function getTrashEntryByDoc(docId, docType) {
	var docId   = Number(docId);
	var docType = String(docType);
	var trashEntry;
	for (var i = 0; i < g_trashEntriesCount; i += 1) {
		trashEntry = g_trashEntries[i];
		if ((trashEntry.m_docId == docId) && (trashEntry.m_docType == docType)) {
			return(trashEntry);
		}
	}
	return(null);
}

/*
 * Constructor method for SSTrashEntry objects.
 */
function SSTrashEntry() {
	/*
	 * Returns the checkbox ID for this SSTrashEntry's selection
	 * checkbox.
	 */
	this.getCheckboxId = function() {
		var sCBox = ("trash_selectOneCB_" + String(this.m_docType) + "_" + String(this.m_docId));
		return(sCBox);
	}

	/*
	 * Returns the HTML DOM element for this SSTrashEntry's selection
	 * checkbox.
	 */	
	this.getCheckbox = function() {
		var eCBox = document.getElementById(this.getCheckboxId());
		return(eCBox);
	}
	
	/*
	 * Returns a serialized (i.e., string) representation of this
	 * SSTrashEntry.
	 */
	this.serialize = function () {
		var	reply =	(String(this.m_docId)            + ":" +
					 String(this.m_locationBinderId) + ":" +
					 String(this.m_docType)          + ":" +
					 String(this.m_entityType));
		return(reply);
	}
}

/*
 * Called to handle the response from an AJAX operation on the trash.
 */
function ajaxTrashRequest_Response(data, sOperation) {
	// Simply force the page to reload.  This will cause the trash to
	// be re-read and re-displayed.
	setTimeout("document.location.reload();", 100);
}

/*
 * Called to submit an AJAX request to perform an operation on the
 * trash.
 */
function ajaxTrashRequest_Submit(sOperation, urlParams) {
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:sOperation, params:urlParams, binderId:  g_binderId});
	ss_get_url(url, ajaxTrashRequest_Response, sOperation);
}
