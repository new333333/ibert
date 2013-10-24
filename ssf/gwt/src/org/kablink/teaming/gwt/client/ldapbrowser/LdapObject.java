/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.gwt.client.ldapbrowser;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * ?
 * 
 * @author rvasudevan
 */
public class LdapObject implements IsSerializable,  Comparable<LdapObject> {
	private String		m_dn;			//
	private String		m_name;			//
	private String[]	m_objectClass;	//

	/**
	 * Constructor method.
	 * 
	 * Zero parameters as per GWT serialization requirements.
	 */
	public LdapObject() {
		// Initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param name
	 */
	public LdapObject(String name) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		setName(name);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public String   getDn()          {return m_dn;         }
	public String   getName()        {return m_name;       }
	public String[] getObjectClass() {return m_objectClass;}

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setDn(         String   dn)          {m_dn          = dn;         }
	public void setName(       String   name)        {m_name        = name;       }
	public void setObjectClass(String[] objectClass) {m_objectClass = objectClass;}


	/**
	 * ?
	 * 
	 * @return
	 */
	public boolean isLeaf() {
		if (null == m_objectClass) {
			return false;
		}
		
		return (
			isObjectClassFound("person")                    ||
			isObjectClassFound("organizationalPerson")      ||
			isObjectClassFound("inetOrgPerson")             ||
			isObjectClassFound("groupOfNames")              ||
			isObjectClassFound("groupWiseExternalEntity")   ||
			isObjectClassFound("groupWiseDistributionList") ||
			isObjectClassFound("group")                     ||
			isObjectClassFound("organizationalRole"));
	}

	/**
	 * ?
	 * 
	 * @param type
	 * 
	 * @return
	 */
	public boolean isObjectClassFound(String type) {
		if (null == m_objectClass) {
			return false;
		}

		for (String str:  m_objectClass) {
			if (str.equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ?
	 * 
	 * Implements the Comparator.compare() method.
	 * 
	 * @param o
	 * 
	 * @return
	 */
	@Override
	public int compareTo(LdapObject o) {
		return m_name.compareToIgnoreCase(o.getName());
	}
}
