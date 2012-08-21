/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client;


import java.util.Set;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used in GWT RPC calls to represent a Net Folder Root object.
 * 
 * @author jwootton@novell.com
 */
public class NetFolderRoot 
	implements IsSerializable, VibeRpcResponseData
{
	private Long m_id;
	private String m_name;
	private String m_rootPath;
	private String m_proxyName = "";
	private String m_proxyPwd = "";
	private Set<Long> m_memberIds;
	
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public NetFolderRoot()
	{
		// Nothing to do.
	}	
	
	/**
	 * Copy the info from the given NetFolderRoot
	 */
	public void copy( NetFolderRoot root )
	{
		m_id = root.getId();
		m_name = root.getName();
		m_rootPath = root.getRootPath();
		m_proxyName = root.getProxyName();
		m_proxyPwd = root.getProxyPwd();
		m_memberIds = root.getMemberIds();
	}
	
	/**
	 * 
	 */
	public Long getId()
	{
		return m_id;
	}
	
	/**
	 * 
	 */
	public Set<Long> getMemberIds()
	{
		return m_memberIds;
	}
	
	/**
	 * Returns the Net Folder Root's name.
	 */
	public String getName()
	{
		return m_name;
	}
	
	/**
	 * 
	 */
	public String getProxyName()
	{
		return m_proxyName;
	}
	
	/**
	 * 
	 */
	public String getProxyPwd()
	{
		return m_proxyPwd;
	}
	
	/**
	 * Returns the root path
	 */
	public String getRootPath()
	{
		return m_rootPath;
	}
	
	/**
	 * 
	 */
	public void setId( Long id )
	{
		m_id = id;
	}
	
	/**
	 * Stores the Net Folder Root's name.
	 * 
	 * @param name
	 */
	public void setName( String name )
	{
		m_name = name;
	}
	
	/**
	 * 
	 */
	public void setProxyName( String name )
	{
		m_proxyName = name;
	}
	
	/**
	 * 
	 */
	public void setProxyPwd( String pwd )
	{
		m_proxyPwd = pwd;
	}
	
	/**
	 * Stores the root path
	 * 
	 * @param rootPath
	 */
	public void setRootPath( String rootPath )
	{
		m_rootPath = rootPath;
	}
}
