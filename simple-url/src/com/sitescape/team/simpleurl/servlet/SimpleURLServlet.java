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
package com.sitescape.team.simpleurl.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sitescape.team.asmodule.bridge.BridgeClient;

public class SimpleURLServlet extends HttpServlet {

	private static final String SERVICE_CLASS_NAME = "com.sitescape.team.util.SimpleNameUtil";
	
	private static final String SERVICE_METHOD_NAME = "resolveURL";
	
	private static final Class[] SERVICE_METHOD_ARG_TYPES = 
		new Class[] {boolean.class, String.class, int.class, String.class};

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
    	boolean isSecure = req.isSecure();
    	String hostname = req.getServerName();
    	int port = req.getServerPort();
    	String simpleName = req.getPathInfo();
    	if(simpleName.startsWith("/"))
    		simpleName = simpleName.substring(1);
    	
		String resolvedURI;
		try {
			resolvedURI = (String) BridgeClient.invoke(null, null, 
					SERVICE_CLASS_NAME, SERVICE_METHOD_NAME, SERVICE_METHOD_ARG_TYPES,
					new Object[] {isSecure, hostname, port, simpleName});
		} catch (Exception e) {
			throw new ServletException(e);
		}

		if(resolvedURI != null)
			resp.sendRedirect(resolvedURI);
		else
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, simpleName);
    }

}
