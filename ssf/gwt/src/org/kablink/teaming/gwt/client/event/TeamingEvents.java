/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.client.event;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This enumeration defines all possible types of events generated by
 * Vibe OnPrem.
 * 
 * Note:  The events defined here must also be maintained in
 *    EventHelper.registerEventHandlers().
 * 
 * @author drfoster@novell.com
 */
public enum TeamingEvents implements IsSerializable {
	TEAMING_ACTION,					// Event whose pay load is one of our original TeamingActions.
	
	ACTIVITY_STREAM,				// Changes the selected an activity stream.
	ACTIVITY_STREAM_ENTER,			// Enters activity stream mode.
	ACTIVITY_STREAM_EXIT,			// Exits  activity stream mode.
	
	ADMINISTRATION,					// Enters administration mode.
	ADMINISTRATION_EXIT,			// Exits  administration mode.
	ADMINISTRATION_UPGRADE_CHECK,	// Tell the administration control to check for upgrade tasks that need to be performed.
	
	BROWSE_HIERARCHY,				// Browse Vibe OnPrem's hierarchy (i.e., the bread crumb tree.) 
	BROWSE_HIERARCHY_EXIT,			// Exits the bread crumb browser, if open.
	
	LOGIN,							// logs into   Vibe OnPrem.
	LOGOUT,							// logs out of Vibe OnPrem.
	
	MASTHEAD_HIDE,					// Hides the masthead.
	MASTHEAD_SHOW,					// Shows the masthead.
	
	SIDEBAR_HIDE,					// Hides the Left Navigation Panel.
	SIDEBAR_SHOW,					// Shows the Left Navigation Panel.
	
	UNDEFINED;
}
