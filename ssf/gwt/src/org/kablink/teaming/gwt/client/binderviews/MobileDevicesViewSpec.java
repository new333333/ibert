/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.binderviews;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate information about viewing mobile devices
 * between the client and the server as part of a GWT RPC command.
 * 
 * @author drfoster@novell.com
 */
public class MobileDevicesViewSpec implements IsSerializable {
	private Long 	m_userId;	// When m_mode is USER, species which user.
	private Mode	m_mode;		// System vs. user view of mobile devices.

	/**
	 * Inner class used to specify which mode the view is in.
	 */
	public enum Mode implements IsSerializable {
		SYSTEM,
		USER;
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isSystem()  {return this.equals(SYSTEM);}
		public boolean isUser()    {return this.equals(USER);  }
	}

	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public MobileDevicesViewSpec() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else that requires it.
		m_mode = Mode.SYSTEM;
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param mode
	 * @param userId
	 */
	public MobileDevicesViewSpec(Mode mode, Long userId) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setMode(  mode  );
		setUserId(userId);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param mode
	 */
	public MobileDevicesViewSpec(Mode mode) {
		// Initialize this object.
		this(mode, null);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isSystem()  {return m_mode.isSystem();}
	public boolean isUser()    {return m_mode.isUser();  }
	public Long    getUserId() {return m_userId;         }
	public Mode    getMode()   {return m_mode;           }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setUserId(Long userId) {m_userId = userId;}
	public void setMode(  Mode mode)   {m_mode   = mode;  }
}