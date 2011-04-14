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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
	Boolean isAutoComplete = SPropsUtil.getBoolean( "enable.login.autocomplete", false );
%>
<c:set var="ss_windowTitle" value='<%= NLT.get("login.please") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>

<div>
  <img height="225" width="320" src="<html:rootPath/>images/mobile/login_MobileTeaming.png"/>
</div>

<div>
  <div id="contentDetail" style="display: block;">
    <div class="loginDetail">
      <form name="loginForm" id="loginForm" method="post" action="${ss_loginPostUrl}" 
        <c:if test="<%= !isAutoComplete %>"> autocomplete="off" </c:if>
      >
        <table border="0" cellspacing="5">
          <tr>
            <td>
              <label for="j_username"><span><ssf:nlt tag="login.name"/></span></label>
            </td>
            <td valign="top">
               <input type="text" style="width:160px;" name="j_username" id="j_username"/>
            </td>
          </tr>
          <tr>
            <td>
              <label for="j_password"><span><ssf:nlt tag="login.password"/></span></label>
            </td>
            <td valign="top">
              <input type="password" style="width:160px;" name="j_password" id="j_password"/>
            </td>
          </tr>

<!-- If there was an error logging in, show the error. -->
<c:if test="${!empty ss_loginError}">
		       		<tr>
				 		<td>&nbsp;</td>
		         		<td style="color: red;" colspan="2" align="right">
		           			<div id="errorcode.login.failed"><ssf:nlt tag="errorcode.login.failed"/></div>
	<c:if test="${!empty showLoginFailureDetails}">
							<a href="#"
							   onclick="showLoginFailureDetails();return false;"
							   title="<ssf:nlt tag="login.showLoginFailureDetails" />"
								<span><ssf:nlt tag="login.showLoginFailureDetails" /></span>
							</a>
	</c:if>
		         		</td>
		       		</tr>
	<c:if test="${!empty showLoginFailureDetails}">
					<!-- This row will hold the details of the login failure. -->
					<tr>
						<td style="color: red;" colspan="2">
							<div id="login-failure-details" style="display: none;">${ss_loginError}</div>
						</td>
					</tr>
	</c:if>
</c:if>

          <tr>
            <td>
            </td>
            <td>
              <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="login"/>"/>
            </td>
          </tr>
        </table>
        
        <input type="hidden" name="spring-security-redirect" value="${ssUrl}"/>
      </form>
    </div>
  </div>
</div>
  
<div class="footerbar">
<img id="logo" src="<html:rootPath/>images/mobile/N_logo_22.png"/>
<div class="copyright"><ssf:nlt tag="mobile.copyright"/></div>
</div>  
  
<script type="text/javascript">
	var formObj = self.document.getElementById('loginForm');
	formObj.j_username.focus();
</script>
</body>
</html>
