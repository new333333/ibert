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
package com.sitescape.team.web.servlet.handler;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;

public class UserPreloadInterceptor extends HandlerInterceptorAdapter {
	private ProfileDao profileDao;
	private ProfileModule profileModule;
	
	protected ProfileDao getProfileDao() {
		return profileDao;
	}

	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	
	protected ProfileModule getProfileModule() {
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		
		if(requestContext.getUser() == null) {
			loadUser(request, requestContext);
		}

		return true;
	}
	    
	private void loadUser(HttpServletRequest request, RequestContext reqCxt) {
		User user;
		HttpSession ses = WebHelper.getRequiredSession(request);
		
	   	Long userId = (Long)ses.getAttribute(WebKeys.USER_ID);
		if (userId == null) { 
			String zoneName = reqCxt.getZoneName();
			String userName = reqCxt.getUserName();
			try {
				user = getProfileDao().findUserByName(
					userName, zoneName);
			}
			catch(NoUserByTheNameException e) {
				// The user doesn't exist in the Aspen user database. Since this
				// interceptor is never called unless the user is first authenticated,
				// this means that the user was authenticated against the portal
				// database, and yet the Aspen user database doesn't know about
				// the user. In this case, we create the user account in Aspen
				// if the system is configured to allow such.
				boolean userCreate = 
					SPropsUtil.getBoolean("portal.user.auto.create", false);
	 			if (userCreate) {
	 				Map updates = new HashMap();
	 				// Since we have no information about the user other than 
	 				// its login name, there's not much to store. However, we
	 				// will temporarily set the user's last name to the login 
	 				// name, to make it a bit more useful. The last name will
	 				// be properly updated when the same user accesses the
	 				// system through the regular portlet container if the
	 				// system is configured to support auto synch of user.
	 				// If not, the user can manually change the info later on. 
	 				updates.put("lastName", userName);
					updates.put("locale", request.getLocale());
					
	 				user = getProfileModule().addUserFromPortal(zoneName, userName, null, updates);
	 			}
	 			else
	 				throw e;
			}
			ses.setAttribute(WebKeys.USER_ID, user.getId());
		} else {
			user = getProfileDao().loadUser(userId, reqCxt.getZoneName());
		}

		reqCxt.setUser(user);
	}

}
