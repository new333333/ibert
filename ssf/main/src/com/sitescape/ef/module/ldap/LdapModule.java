
package com.sitescape.ef.module.ldap;

import com.sitescape.ef.domain.NoUserByTheNameException;
import javax.naming.NamingException;

/**
 * @author Janet McCann
 *
 */
public interface LdapModule {

	public LdapConfig getLdapConfig();
	public void setLdapConfig(LdapConfig config);

	public void syncAll() throws NamingException;
	public boolean authenticate(String companyId, String loginName,String password) throws NamingException;
	public void syncUser(String companyId, String loginName) throws NoUserByTheNameException,NamingException;
}
