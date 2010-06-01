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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<div class="ss_style ss_portal">
<h2><ssf:nlt tag="relevance.shareThisWithWhom"/></h2>

<form class="ss_style ss_form" 
  action="<ssf:url adapter="true" portletName="ss_forum" 
		action="__ajax_relevance" actionUrl="true"><ssf:param 
		name="operation" value="share_this_binder" /><ssf:param 
		name="binderId" value="${ssBinderId}" /><c:if test="${!empty ssEntryId}"><ssf:param 
		name="entryId" value="${ssEntryId}" /></c:if></ssf:url>"
  name="${renderResponse.namespace}fm" 
  id="${renderResponse.namespace}fm" 
  method="post">
  
<span class="ss_bold"><ssf:nlt tag="relevance.selectUsers"/></span>
<br/>
  <ssf:find formName="${renderResponse.namespace}fm" formElement="users" 
    type="user" userList="${ssUsers}" binderId="${ssBinderId}" width="150px"/>

<br/>

<span class="ss_bold"><ssf:nlt tag="relevance.selectGroups"/></span>
<br/>
  <ssf:find formName="${renderResponse.namespace}fm" formElement="groups" 
    type="group" userList="${ssGroups}" sendingEmail="true" width="150px"/>
    
<br/>
<br/>

<span class="ss_bold"><ssf:nlt tag="relevance.selectTeams"/></span>
<br/>
<ul class="ss_nobullet">
  <c:forEach var="team" items="${ss_myTeams}">
	<li><input type="checkbox" name="cb_${team._docId}" id="cb_${team._docId}"/>
	    <label for="cb_${team._docId}"><span style="padding-left:6px;">${team.title}</span></label>
	</li>
  </c:forEach>
</ul>

<br/>

<span class="ss_labelAbove"><ssf:nlt tag="relevance.shareThisWithComment"/></span>
<div>
<%@ include file="/WEB-INF/jsp/binder/sendMail_htmlTextarea.jsp" %> 
</div>

<input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" />
<input type="button" value="<ssf:nlt tag="button.cancel"/>" onclick="self.window.close();return false;"/>

</form>
<br/>
<br/>
<div>
  <span class="ss_italic"><ssf:nlt tag="relevance.shareHint"/></span>
</div>

</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
