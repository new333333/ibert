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
package com.sitescape.team.portal.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.asmodule.bridge.BridgeUtil;
import com.sitescape.team.module.license.LicenseChecker;
import com.sitescape.team.portal.CrossContextConstants;
import com.sitescape.team.security.authentication.AuthenticationManagerUtil;
import com.sitescape.team.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.team.security.authentication.UserDoesNotExistException;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.web.WebKeys;

public class DispatchServer extends GenericServlet {

	protected static final Log logger = LogFactory.getLog(DispatchServer.class);
	
	private static final String PORTAL_CC_DISPATCHER = "portalCCDispatcher";
	private static final String SSF_CONTEXT_PATH_DEFAULT = "/ssf";
	
	public void init(ServletConfig config) throws ServletException {
		RequestDispatcher rd = config.getServletContext().getNamedDispatcher(PORTAL_CC_DISPATCHER);
		BridgeUtil.setCCDispatcher(rd);
		String cxt = config.getInitParameter("ssfContextPath");
		if(cxt == null || cxt.length() == 0)
			cxt = SSF_CONTEXT_PATH_DEFAULT;
		BridgeUtil.setSSFContextPath(cxt);
		try {
			BridgeUtil.setClassLoader(Thread.currentThread().getContextClassLoader());
		}
		catch(Exception e) {
			new ServletException(e);
		}
	}
	
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		String operation = req.getParameter(CrossContextConstants.OPERATION);

		if(operation.equals(CrossContextConstants.OPERATION_SETUP_SESSION)) {
			String zoneName = req.getParameter(CrossContextConstants.ZONE_NAME);
			if(zoneName != null) {
				if(!(zoneName.equals(SZoneConfig.getDefaultZoneName()) ||
						LicenseChecker.isAuthorizedByLicense("com.sitescape.team.module.zone.MultiZone")))
					return; // don't allow it; simply return					
			}
			else {
				zoneName = SZoneConfig.getDefaultZoneName();
			}
					
			HttpSession ses = ((HttpServletRequest) req).getSession();
			
			String userName = req.getParameter(CrossContextConstants.USER_NAME);
			if(userName == null)
				userName = SZoneConfig.getGuestUserName(zoneName);
			
			ses.setAttribute(WebKeys.ZONE_NAME, zoneName);
			ses.setAttribute(WebKeys.USER_NAME, userName);

			if(ses.getAttribute(WebKeys.SERVER_NAME) == null) {		
				ses.setAttribute(WebKeys.SERVER_NAME, req.getServerName().toLowerCase());
				ses.setAttribute(WebKeys.SERVER_PORT, Integer.valueOf(req.getServerPort()));
				if(logger.isDebugEnabled())
					logger.debug("Server name:port is " + req.getServerName().toLowerCase() + ":" + req.getServerPort() + " for user " + userName + " at the time of login");
			}
		}
		else {
			logger.error("Unrecognized operation [" + operation + "]");
		}
	}
}
