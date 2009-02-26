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
package org.kablink.teaming.tomcat.valve;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;

/**
 * This class is used to capture some information (eg. server name and client's
 * IP address) from the calling context that is needed by Teaming later on.
 * Same can also be done through servlet filter, and and we have two examples
 * of that in system. However, there are situations where this information 
 * needs to be obtained well before the filter chain gets even invoked.
 * A concrete example of that is when executing JAAS login module. Teaming
 * supplied JAAS login module is used for authentication for ssfs and ssr
 * web apps, and that's why those two web apps require this valve to be set up
 * so that it can use this information during authentication before the 
 * filter chain is executed. 
 * 
 * @author Jong Kim
 *
 */
public class ZoneContextValve extends ValveBase {

	@Override
	public void invoke(Request request, Response response) 
	throws IOException, ServletException {
		ZoneContextHolder.setServerName(request.getServerName());
		ZoneContextHolder.setClientAddr(request.getRemoteAddr());
		try {
			getNext().invoke(request, response);
		}
		finally {
			ZoneContextHolder.clear();
		}
	}

}
