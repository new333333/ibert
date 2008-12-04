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
<% //Date widget form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.kablink.teaming.util.CalendarHelper" %>
<c:if test="${empty ssReadOnlyFields[property_name]}">

<style type="text/css">
        @import "<html:rootPath />js/dojo/dijit/themes/tundra/tundra.css";
        @import "<html:rootPath />js/dojo/dojo/resources/dojo.css"
</style>

<div class="ss_entryContent tundra">
	<span class="ss_labelAbove" id='${property_name}_label'>${property_caption}<c:if test="${property_required}"><span class="ss_required">*</span></c:if></span>
	<div id="${property_name}_error" style="visibility:hidden; display:none;"><span class="ss_formError"><ssf:nlt tag="date.validate.error"/></span></div>
	

	<c:set var="initDate" value="<%= new Date() %>"/>
	<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
		<c:set var="initDate" value="${ssDefinitionEntry.customAttributes[property_name].value}"/>
		<c:set var="property_initialSetting" value="entry"/>
	</c:if>

	<table class="ss_style" cellpadding="0" border="0">
		<tr>
			<td>
				<input type="text" dojoType="dijit.form.DateTextBox" 
					id="date_${property_name}_${prefix}" 
					name="${property_name}_fullDate" 
					lang="<ssf:convertLocaleToDojoStyle />" 
					<c:if test="${property_initialSetting != 'none'}">
					  value="<fmt:formatDate value="${initDate}" 
					    pattern="yyyy-MM-dd" timeZone="${ssUser.timeZone.ID}"/>"
					</c:if>
					<c:if test="${property_initialSetting == 'none'}">
					  value=""
					</c:if>
				/>
			</td>
			<td>
				<input type="text" dojoType="dijit.form.TimeTextBox"
					id="date_time_${property_name}_${prefix}" 
					name="${property_name}_0_fullTime" 
					lang="<ssf:convertLocaleToDojoStyle />" 
					<c:if test="${property_initialSetting != 'none'}">
					  value="T<fmt:formatDate value="${initDate}" 
					    pattern="HH:mm:ss" timeZone="${ssUser.timeZone.ID}"/>"
					</c:if>
					<c:if test="${property_initialSetting == 'none'}">
					  value=""
					</c:if>
				></div>
				<input type="hidden" name="${property_name}_timezoneid" value="${ssUser.timeZone.ID}" />
			</td>
		</tr>
	</table>
	
	<input type="hidden" name="${property_name}" value="" />
	
	<script type="text/javascript">
		dojo.addOnLoad(function() {
				dojo.addClass(document.body, "tundra");
			}
		);	
		dojo.require("dijit.form.DateTextBox");
		dojo.require("dijit.form.TimeTextBox");
	</script>
	
</div>
</c:if>
<c:if test="${!empty ssReadOnlyFields[property_name]}">
<div class="ss_entryContent">
<span class="ss_labelAbove">${property_caption}</span>
<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				      value="${ssDefinitionEntry.customAttributes[property_name].value}" 
				      type="both" dateStyle="medium" timeStyle="short" />
</c:if>
</div>
</c:if>