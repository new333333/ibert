/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.web.crosscontext;

/**
 * IMPORTANT: Do NOT make this class dependent upon any other class in the
 * system. In other word, do NOT import any class other than java or
 * javax classes.
 * 
 * @author jong
 *
 */
public abstract class CrossContextConstants {
	
	// Key names - We need to qualify each name with something unique 
	// (i.e., com.sitescape.crosscontext.portal) so that the name will 
	// not collide with other names already in the request object.
	public static final String OPERATION = "com.sitescape.crosscontext.portal.operation";
	public static final String USER_NAME = "com.sitescape.crosscontext.portal.username";
	public static final String PASSWORD = "com.sitescape.crosscontext.portal.password";
	public static final String ZONE_NAME = "com.sitescape.crosscontext.portal.zonename";
	public static final String USER_INFO="com.sitescape.crosscontext.portal.userinfo";
	// Operation names
	public static final String OPERATION_AUTHENTICATE = "authenticate";
	public static final String OPERATION_CREATE_SESSION = "createSession";
}
