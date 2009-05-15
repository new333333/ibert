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
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.lang.String" %>


<% // This is a JSON AJAX response %>
<% // Do we have an access report? %>
<c:if test="${!empty userAccessReport}">
	<% // Yes %>
	<jsp:useBean id="ldapSyncResults" type="org.kablink.teaming.module.ldap.LdapSyncResults" scope="request" />

<%
	org.kablink.teaming.module.ldap.LdapSyncResults.PartialLdapSyncResults	partialSyncResults;
	ArrayList<String>	syncResults;
	int					i;
	int					status;
	String				name;
	String				seperator;
	String				errDesc;
	String				errLdapConfigId;
	
	status = ldapSyncResults.getStatus().ordinal();
	errDesc = ldapSyncResults.getErrorDesc();
	errLdapConfigId = ldapSyncResults.getErrorLdapConfigId();
%>
{
	status : <%= status %>,
	errDesc : '<ssf:escapeJavaScript value="<%= errDesc %>" />',
	errLdapConfigId : '<ssf:escapeJavaScript value="<%= errLdapConfigId %>" />',

<%
	// Get the list of users that were added.
	partialSyncResults = ldapSyncResults.getAddedUsers();
	syncResults = null;
	if ( partialSyncResults != null )
		syncResults = partialSyncResults.getResults();
	
%>
	addedUsers : [
<%
	if ( syncResults != null && syncResults.size() > 0 )
	{
		for (i = 0; i < syncResults.size(); ++i)
		{
			// If this is not the first name, add a ',' before we add another name.
			if ( i != 0 )
				seperator = ",";
			else
				seperator = "";

			name = (String) syncResults.get( i );
%>
			<%= seperator %>'<ssf:escapeJavaScript value="<%= name %>" />'
<%
		}
	}
%>
	]
}
</c:if>

<% // Do we have an access report? %>
<c:if test="${empty userAccessReport}">
	<% // No %>
{
	status : 1,
	errDesc : null,
	userName : 'some user',
	reportData : [ {entryType : 'workspace', id : 40, path : '/Home Workspace/Personal Workspaces/admin (admin)/Landing Page WS'}, {entryType : 'folder', id : 41, path : '/Home Workspace/Personal Workspaces/admin (admin)/Blog'}, {entryType : 'folder', id : 646, path : '/Home Workspace/Personal Workspaces/user-hhh uboten (user-hhh)/Task Folder'}, {entryType : 'profiles', id : 97, path : '/Home Workspace/Personal Workspaces'}, {entryType : 'foobar', id : 100, path : '/Home Workspace/Bogus'} ]
}
</c:if>
