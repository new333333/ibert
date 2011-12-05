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
package org.kablink.teaming.gwt.client.binderviews;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.BinderInfo;

import com.google.gwt.user.client.ui.ResizeComposite;


/**
 * Base class for for all the tool panels used in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public abstract class ToolPanelBase extends ResizeComposite {
	public    final        BinderInfo			m_binderInfo;							// Caches the BinderInfo for use by this tool panel.
	protected final static GwtTeamingMessages	m_messages = GwtTeaming.getMessages();	// Access to the GWT localized string resource.
	private                ToolPanelReady		m_toolPanelReady;						//
	
	/**
	 * Callback interface to provide access to the tool panel after it
	 * has asynchronously loaded. 
	 */
	public interface ToolPanelClient {
		void onSuccess(ToolPanelBase tpb);
		void onUnavailable();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param binderInfo
	 */
	public ToolPanelBase(BinderInfo binderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the superclass...
		super();
		
		// ...and store the parameters.
		m_binderInfo     = binderInfo;
		m_toolPanelReady = toolPanelReady;
	}

	/**
	 * Called from the binder views to allow the tool panel to do any
	 * work required to reset themselves.
	 */
	public abstract void resetPanel();
	
	/**
	 * Called by classes that extend this base class so that it can
	 * inform the world that its panel is ready to go.
	 */
	final public void toolPanelReady() {
		if (null != m_toolPanelReady) {
			m_toolPanelReady.toolPanelReady(this);
		}
	}
}
