<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>
<form action="<ssf:url adapter="true" portletName="ss_forum" 
	action="__ajax_mobile" operation="mobile_login" actionUrl="true" />" method="post">
<ssf:nlt tag="mobile.loginName"/><br/>
<input type="text" name="name" size="20" value="${ss_defaultLoginName}" 
style="font-size:10px; line-height:12px; margin:4px 0px 0px 2px; padding:4px 0px 0px 2px; border-width:1px 2px 2px 1px;">
<br/>
<br/>
<ssf:nlt tag="mobile.password"/><br/>
<input type="password" name="password" size="20">
<br/>
<br/>
<input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>"
style="font-size:10px;margin:4px 0px 0px 2px; padding:4px 0px 0px 2px; border-width:1px 2px 2px 1px;">
</form>

</body>
</html>
