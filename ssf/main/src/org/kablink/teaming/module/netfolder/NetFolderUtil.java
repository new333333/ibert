/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.netfolder;

import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.NetFolder;
import org.kablink.teaming.domain.NoNetFolderByTheIdException;
import org.kablink.teaming.domain.NoNetFolderByTheNameException;
import org.kablink.teaming.domain.NoNetFolderServerByTheIdException;
import org.kablink.teaming.domain.NoNetFolderServerByTheNameException;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManagerUtil;
import org.kablink.teaming.util.SpringContextUtil;

/**
 * @author jong
 *
 */
public class NetFolderUtil {

	public static NetFolder getNetFolderById(Long netFolderId) throws NoNetFolderByTheIdException {
		return getCoreDao().loadNetFolder(netFolderId);
	}
	
	public static NetFolder getNetFolderByName(String netFolderName) throws NoNetFolderByTheNameException {
		return getCoreDao().loadNetFolderByName(netFolderName);
	}
	
	public static ResourceDriver getResourceDriverByNetFolderId(Long netFolderId) throws NoNetFolderByTheIdException, FIException {
		NetFolder nf = getNetFolderById(netFolderId);
		return getResourceDriverByNetFolderServerId(nf.getNetFolderServerId());
	}
	
	public static ResourceDriverConfig getNetFolderServerById(Long netFolderServerId) throws NoNetFolderServerByTheIdException {
		return getCoreDao().loadNetFolderServer(netFolderServerId);
	}
	
	public static ResourceDriverConfig getNetFolderServerByName(String netFolderServerName) throws NoNetFolderServerByTheNameException {
		return getCoreDao().loadNetFolderServerByName(netFolderServerName);
	}
	
	public static ResourceDriver getResourceDriverByNetFolderServerId(Long netFolderServerId) throws NoNetFolderByTheIdException, FIException {
		return ResourceDriverManagerUtil.findResourceDriver(netFolderServerId);
	}
	
	private static CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
}
