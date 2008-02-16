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
package com.sitescape.team.remoting.ws.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.util.Validator;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.domain.LoginInfo;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SpringContextUtil;

public class SecurityPropagatingAxisServlet extends org.apache.axis.transport.http.AxisServlet {
	
	protected Log logger = LogFactory.getLog(getClass());
	
	private static final String TOKEN_SERVICES_PATH = "token.servicesPath";
	private static final String TOKEN_SERVICES_PATH_DEFAULT_VALUE = "/token/";

	protected String tokenServicesPath;
	
    public void init() throws javax.servlet.ServletException {
    	super.init();
    	tokenServicesPath = getServletConfig().getServletContext().getInitParameter(TOKEN_SERVICES_PATH);
    	if(Validator.isNull(tokenServicesPath))
    		tokenServicesPath = TOKEN_SERVICES_PATH_DEFAULT_VALUE;
    }
    
	public void service(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		String servletPath = req.getServletPath();
		if(servletPath.startsWith(tokenServicesPath)) {
			// Service request with token-based authentication.
			// In this case, authentication has NOT happened yet. 
			// It will be handled in the downstream.
			
			if(SPropsUtil.getBoolean("remoting.token.enable", true)) {
				try {
					Long zoneId = getZoneModule().getZoneIdByVirtualHost(
							req.getServerName().toLowerCase());
					// Set a temporary context with fake user ID. 
					// Pass the zone and the authenticator information down the call stack.
					RequestContextUtil.setThreadContext(zoneId, Long.valueOf(0)).setAuthenticator(LoginInfo.AUTHENTICATOR_REMOTING_T);
					
					super.service(req, res);
				}
				finally {
					RequestContextHolder.clear();
				}		
			}
			else {
				if(logger.isDebugEnabled())
					logger.debug("Denying " + req.getRemoteAddr() + " access to token-based remote services: It is disabled");
				throw new ServletException("The service is disabled");
			}
		}
		else {
			// Non-secured request (ie, request requiring no authentication)
			// or secured request that has already passed authentication.
			// For these two scenarios, no additional authentication is needed.
			String remoteUser = req.getRemoteUser();
	
			String zoneName = getZoneModule().getZoneNameByVirtualHost(
					req.getServerName().toLowerCase());
	
			String userName;
			if (remoteUser != null) { // authenticated user
				userName = remoteUser;
				if (logger.isDebugEnabled()) {
					logger.debug("Remote user " + remoteUser);
				}
			} else { // unauthenticated user (anonymous access) - assume guest identity
				if(SPropsUtil.getBoolean("remoting.anonymous.enable", true)) {
					userName = SZoneConfig.getGuestUserName(zoneName);
					if (logger.isDebugEnabled()) {
						logger.debug("Anonymous access: assuming " + userName);
					}
				}
				else {
					if(logger.isDebugEnabled())
						logger.debug("Denying " + req.getRemoteAddr() + " anonymous access to remote services: It is disabled");
					throw new ServletException("The service is disabled");		
				}
			}
	
			try {
				RequestContextUtil.setThreadContext(zoneName, userName);
	
				super.service(req, res);
			} finally {
				RequestContextHolder.clear();
			}
		}
	}

	private ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}

}
