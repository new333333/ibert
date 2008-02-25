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
package com.sitescape.team.security.accesstoken;

public interface AccessTokenManager {

	/**
	 * Validate the access token.
	 * 
	 * @param token
	 * @throws InvalidAccessTokenException thrown if the access token is invalid
	 */
	public void validate(String tokenStr, AccessToken token) throws InvalidAccessTokenException;
	

	/**
	 * Generates an access token of given type.
	 * 
	 * @param type
	 * @param applicationId
	 * @param userId
	 * @return
	 */
	public AccessToken newAccessToken(AccessToken.TokenType type, 
			Long applicationId, Long userId);
	
	/**
	 * Generates an access token of given type.
	 * If <code>binderId</code> is specified, its <code>includeDescendants</code> 
	 * flag is set to <code>true</code>.
	 * 
	 * @param type
	 * @param applicationId
	 * @param userId
	 * @param binderId optional
	 * @return
	 */
	public AccessToken newAccessToken(AccessToken.TokenType type, 
			Long applicationId, Long userId, Long binderId);
	
	/**
	 * Generates an access token of given type.
	 * 
	 * @param type
	 * @param applicationId
	 * @param userId
	 * @param binderId optional
	 * @param includeDescendants ignored if <code>binderId</code> is <code>null</code>
	 * @return
	 */
	public AccessToken newAccessToken(AccessToken.TokenType type, 
			Long applicationId, Long userId, Long binderId, Boolean includeDescendants);
	
	/**
	 * Update the seed value, if exists, used for generating interactive tokens for the specified user.
	 * Effectively this invalidates all outstanding interactive tokens issued on behalf of the user.
	 * 
	 * @param userId
	 */
	public void updateSeedForInteractive(Long userId);
	
	/**
	 * Update the seed value, if exists, used for generating background tokens for the
	 * specified application/user/binder combination.
	 * Effectively this invalidates all outstanding background tokens issued for the
	 * combination.
	 * 
	 * @param applicationId
	 * @param userId
	 * @param binderId optional
	 */
	public void updateSeedForBackground(Long applicationId, Long userId, Long binderId);
	
	/**
	 * Empty all states used for generating interactive tokens. 
	 * Effectively this invalidates all outstanding interactive tokens for all users.
	 */
	public void emptyAllInteractive();
}
