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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body">
<div class="ss_pseudoPortal">

<div class="ss_style ss_portlet">
<c:if test="${!empty ssErrorList}">
<span class="ss_bold"><ssf:nlt tag="administration.errors"/></span>
<br/>
<br/>
<ul>
<c:forEach var="err" items="${ssErrorList}">
	<li>${err}</li>
</c:forEach>
</ul>
</c:if>
<form class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<ssf:url action="import_definition" 
			actionUrl="true"><ssf:param 
		  name="binderId" value="${ssBinder.id}"/></ssf:url>">
<c:if test="${empty ssBinder}">
<span class="ss_titlebold"><ssf:nlt tag="administration.import.definitions.public" /></span>
</c:if>
<c:if test="${!empty ssBinder}">
<span class="ss_titlebold"><ssf:nlt tag="administration.import.definitions.local"><ssf:param
	name="value" value="${ssBinder.pathName}"/></ssf:nlt></span>
</c:if>
<br>

<div class="ss_divider"></div>
<br>


<table class="ss_style" border="0" cellpadding="5" cellspacing="0" width="50%">
<thead><th><ssf:nlt tag="administration.import.replace"/></th>
<th><ssf:nlt tag="administration.selectFiles"/></th>
</thead>
<tbody>
<tr><td><input type="checkbox" name="definition1ck"></td>
<td><input type="file" size="80" class="ss_text" name="definition1" ></td></tr>
<tr><td><input type="checkbox" name="definition2ck"></td>
<td><input type="file" size="80" class="ss_text" name="definition2" ></td></tr>
<tr><td><input type="checkbox" name="definition3ck"></td>
<td><input type="file" size="80" class="ss_text" name="definition3" ></td></tr>
<tr><td><input type="checkbox" name="definition4ck"></td>
<td><input type="file" size="80" class="ss_text" name="definition4" ></td></tr>
<tr><td><input type="checkbox" name="definition5ck"></td>
<td><input type="file" size="80" class="ss_text" name="definition5" ></td></tr>
</tr></tbody></table>
<div class="ss_divider"></div>

<br/>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />"/>

<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"/>

</div>
</div>
</form>
</div>

</div>
</body>
</html>