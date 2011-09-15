<%
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
%>
<%@ page import="org.kablink.teaming.ObjectKeys" %>

<script type="text/javascript">
function ss_logoff() {

	<c:if test="${!empty nativeMobileApp}">
	<%
		if(isIPhone) {
	%>
			alert('Logout of Iphone');
		    //document.location = "NovellVibe:" + "doAppLogout:" + "iphone:";
	<%
		} else {
	%>
			Android.doAppLogout();
	<%
		}
	%>
	</c:if>

	var x = '<%= org.kablink.teaming.web.util.WebUrlUtil.getSsoProxyLogoffUrl(request) %>';
	if (x == null || x == "") {
		var y = '${ss_logoutUrl}';
		var logoutForm;
		
		// Get the logout form.  We use a form so the logout request can be made with
		// a "post" instead of a "get".  This prevents logout spoofing.
		logoutForm = document.getElementById( 'logoutForm' );
		if ( logoutForm != null )
		{
			logoutForm.action = y;
			logoutForm.submit();
		}
		else
		{
			// This should never happen.
			alert( 'Could not find the logout form.' );
		}

		// This is how it used to be done
		//alert(y);
		//self.location.href=y;
	} else {
		//alert (x);
		var y = '${ss_logoutUrl}';
		ss_logoff_from_teaming_then_sso(y);
	}
}
function ss_logoff_from_teaming_then_sso(logoutURL) {
	callbackRoutine = ss_logoff_from_sso
	var x;

	if (window.XMLHttpRequest) {
	x = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
	x = new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	x.open("POST", logoutURL, true);
	
	x.onreadystatechange = function() {
		if (x.readyState != 4) {
			return;
		}
		if (x.status == 200) {
			callbackRoutine(x.responseText)        	
		} else {		
			callbackRoutine(x.statusText)
		}
	}
	x.send(null);
	delete x;
}      
function ss_logoff_from_sso(s) {
	self.location.href='<%= org.kablink.teaming.web.util.WebUrlUtil.getSsoProxyLogoffUrl(request) %>';
}
</script>

<!-- This form is used for logging out. -->
<!-- The value of the action attribute will be filled in at runtime. -->
<form name="logoutForm" id="logoutForm" method="post" action="" >
</form> 