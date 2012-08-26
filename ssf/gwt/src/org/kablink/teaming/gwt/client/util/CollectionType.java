/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Enumeration used to communicate the type of a collection between the
 * client and the server as part of a GWT RPC command.
 * 
 * @author drfoster@novell.com
 */
public enum CollectionType implements IsSerializable {
	// *** WARNING *** WARNING *** WARNING *** WARNING ***
	// ***
	// *** The ordinal value of MY_FILES (i.e., 0) is hard coded in
	// *** PermaLinkUtil.getUserPermalink().  If the ordinal value
	// *** of that enumeration changes, PermaLinkUtil.java MUST be
	// *** changed accordingly.
	// ***
	// *** WARNING *** WARNING *** WARNING *** WARNING ***
	MY_FILES,
	NET_FOLDERS,
	SHARED_BY_ME,
	SHARED_WITH_ME,
	
	OTHER,
	NOT_A_COLLECTION;

	/**
	 * Converts the ordinal value of a CollectionType to its
	 * enumeration equivalent.
	 * 
	 * @param ordinal
	 * 
	 * @return
	 */
	public static CollectionType getEnum(int ordinal) {
		CollectionType reply;
		try {
			reply = CollectionType.values()[ordinal];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			reply = CollectionType.OTHER;
		}
		return reply;
	}
	
	public static CollectionType getEnum(String ordinal) {
		// Always use the initial form of the method.
		return getEnum(Integer.parseInt(ordinal));
	}
	
	/**
	 * Returns true if this CollectionType value represents a
	 * collection and false otherwise.
	 * 
	 * @return
	 */
	public boolean isCollection() {
		return (!(this.equals(NOT_A_COLLECTION)));
	}
}
