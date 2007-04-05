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
package com.sitescape.team.jbossportal.jaas;

import java.lang.reflect.Method;

import com.sitescape.team.asmodule.bridge.SiteScapeBridgeUtil;

import org.jboss.portal.identity.auth.IdentityLoginModule;

import javax.security.auth.login.LoginException;

/**
 * A login module that wraps JBoss Portal's default login module 
 * <code>org.jboss.portal.identity.auth.IdentityLoginModule</code>.
 * <p>
 * The side effect of using this login module is the possible automatic
 * user synchronization from the Portal user database to Aspen user database.
 *
 */
public class SiteScapeLoginModule extends IdentityLoginModule {
	
	private static final String CLASS_NAME =
		"com.sitescape.team.util.SynchUser";
	
	private Object synchUser;
	private Method synchMethod;

	public SiteScapeLoginModule() {
		try { 
			// Instantiate a SynchUser. Assign it to a variable of Object
			// rather than of SynchUser type to prevent current classloader 
			// from attempting to load SynchUser class.
			synchUser = SiteScapeBridgeUtil.newInstance(CLASS_NAME);
			
			// We use reflection to invoke the method later on.
			synchMethod = SiteScapeBridgeUtil.getMethod
			(CLASS_NAME, "synch", String.class, String.class, String.class);
		}
		catch (Exception e) {
			// Perhaps we should have log facility available here...
			e.printStackTrace();
		}	
	}
	
	public boolean commit() throws LoginException {
		boolean result = super.commit();
		if(result) {
			String username = getUsername();
			String password = new String((char[]) getCredentials());
			
			//System.out.println("*** [" + username + "], [" + password + "]");
			
			/*
			try {
				AuthenticationManager.authenticate(null, username, password, null);
			} 
			catch (Exception e) {
				// It's unclear whether we should abort the user login or let
				// the user continue in this case. For now, we will abort it.
				throw new LoginException(e.toString());
			}
			*/
			
			try {
				SiteScapeBridgeUtil.invoke(synchMethod, synchUser, null, username, password);
			}
			catch(Exception e) {
				e.printStackTrace();
				// It's unclear whether we should abort the user login or let
				// the user continue in this case. For now, we will abort it.
				throw new LoginException(e.toString());				
			}
		}
		return result;
	}
}
