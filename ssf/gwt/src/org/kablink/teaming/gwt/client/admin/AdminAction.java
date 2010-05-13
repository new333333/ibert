/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.client.admin;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class defines all the possible types of administration actions that a user can perform.
 * 
 * @author jwootton
 */
public enum AdminAction implements IsSerializable
{
	ACCESS_CONTROL_FOR_ZONE_ADMIN_FUNCTIONS( "Access control for zone administration functions" ),
	ADD_USER( "Add user" ),
	CONFIGURE_EMAIL( "Configure email" ),
	CONFIGURE_FOLDER_INDEX( "Configure folder index" ),
	CONFIGURE_FOLDER_SEARCH_NODES( "Configure folder search nodes" ),
	CONFIGURE_GUEST_ACCESS( "Configure guest access" ),
	CONFIGURE_MOBILE_ACCESS( "Configure mobile access" ),
	CONFIGURE_ROLE_DEFINITIONS( "Configure role definitions" ),
	CONFIGURE_SEARCH_INDEX( "Configure search index" ),
	FORM_VIEW_DESIGNER( "Form/View Designer" ),
	IMPORT_PROFILES( "Import profiles" ),
	LDAP_CONFIG( "LDAP configuration" ),
	MANAGE_APPLICATIONS( "Manage applications" ),
	MANAGE_APPLICATION_GROUPS( "Manage application groups" ),
	MANAGE_EXTENSIONS( "Manage extensions" ),
	MANAGE_GROUPS( "Manage groups" ),
	MANAGE_LICENSE( "Manage license" ),
	MANAGE_QUOTAS( "Manage quotas" ),
	MANAGE_WORKSPACE_AND_FOLDER_TEMPLATES( "Manage workspace and folder templates" ),
	MANAGE_ZONES( "Manage zones" ),
	REPORT_ACTIVITY_BY_USER( "Report: activity by user" ),
	REPORT_CREDITS( "Report: credits" ),
	REPORT_DATA_QUOTA_EXCEEDED( "Report: data quota exceeded" ),
	REPORT_DATA_QUOTA_HIGHWATER_EXCEEDED( "Report: data quota highwater exceeded" ),
	REPORT_DISK_USAGE( "Report: disk usage" ),
	REPORT_LICENSE( "Report: license" ),
	REPORT_LOGIN( "Report: login" ),
	REPORT_USER_ACCESS( "Report: user access" ),
	REPORT_VIEW_CHANGELOG( "Report: view change log" ),
	REPORT_VIEW_CREDITS( "Report: view credits" ),
	REPORT_VIEW_SYSTEM_ERROR_LOG( "Report: view system error log" ),
	SITE_BRANDING( "Site branding" ),

	// This is used as a default case to store a TeamingAction when
	// there isn't a real value to store.
	UNDEFINED( "Undefined Action - Should Never Be Triggered" );

	private final String m_unlocalizedDesc;
	
	/**
	 */
	private AdminAction( String unlocalizedDesc )
	{
		m_unlocalizedDesc = unlocalizedDesc;
	}// end AdminAction()
	
	
	/**
	 */
	public String getUnlocalizedDesc()
	{
		return m_unlocalizedDesc;
	}// end getUnlocalizedDesc()

}// end AdminAction
