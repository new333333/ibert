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

package org.kablink.teaming.remoting.ws.service.admin;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.remoting.ws.BaseService;
import org.kablink.teaming.security.accesstoken.AccessToken;
import org.kablink.teaming.security.accesstoken.AccessTokenManager;

/**
 * @author Jong Kim
 *
 */
public class AdminServiceImpl extends BaseService implements AdminService {

	protected AccessTokenManager accessTokenManager;

	protected AccessTokenManager getAccessTokenManager() {
		return accessTokenManager;
	}
	public void setAccessTokenManager(AccessTokenManager accessTokenManager) {
		this.accessTokenManager = accessTokenManager;
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.remoting.ws.service.admin.AdminService#getApplicationScopedToken(long, java.lang.Long)
	 */
	public String getApplicationScopedToken(String accessToken, long applicationId, Long userId) {
		Long contextUserId = RequestContextHolder.getRequestContext().getUserId();

		if(userId != null && !userId.equals(contextUserId))
			// check privilege TODO $$$$$
			;
		
		return getAccessTokenManager().getApplicationScopedToken(applicationId, userId).toStringRepresentation();
	}
	
	public void destroyApplicationScopedToken(String accessToken, String token) {
		getAccessTokenManager().destroyApplicationScopedToken(new AccessToken(token));
	}
}
