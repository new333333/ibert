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

package org.kablink.teaming.module.netfolder.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Binder.SyncScheduleOption;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.netfolder.NetFolderModule;
import org.kablink.teaming.module.netfolder.NetFolderUtil;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.SpringContextUtil;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author jong
 *
 */
public class NetFolderModuleImpl extends CommonDependencyInjection implements NetFolderModule {

	private Log logger  = LogFactory.getLog(getClass());
	
	@Override
    public NetFolderConfig createNetFolder(Long templateId, Long parentBinderId, final String name, User owner, final String rootName, final String path, final Boolean isHomeDir, final boolean indexContent, final Boolean inheritIndexContent, final SyncScheduleOption syncScheduleOption, final Boolean fullSyncDirOnly ) 
    		throws AccessControlException, WriteFilesException, WriteEntryDataException {
    	
    	// Create and save a new net folder
		final NetFolderConfig netFolderConfig = (NetFolderConfig) getTransactionTemplate().execute(new TransactionCallback<Object>() {
        	@Override
			public Object doInTransaction(final TransactionStatus status) {
        		NetFolderConfig nfc = new NetFolderConfig();
            	nfc.setName(name);
            	nfc.setFolderId(0L); // temporary value
            	nfc.setNetFolderServerId(NetFolderUtil.getNetFolderServerByName(rootName).getId());
            	nfc.setResourcePath(path);
            	nfc.setHomeDir(isHomeDir);
            	nfc.setIndexContent(indexContent);
            	nfc.setUseInheritedIndexContent(inheritIndexContent);
            	nfc.setSyncScheduleOption(syncScheduleOption);
            	nfc.setFullSyncDirOnly(fullSyncDirOnly);
        		getCoreDao().save(nfc);
            	//getCoreDao().saveNewSessionWithoutUpdate(nfc);
				return nfc;
        	}
        });

		// Create top-level folder corresponding to the net folder.
   		String folderName = "_netfolder_" + netFolderConfig.getId();
    	final Folder folder = getFolderModule().createNetFolder(netFolderConfig.getId(), templateId, parentBinderId, folderName, owner, rootName, path, isHomeDir, indexContent, inheritIndexContent, syncScheduleOption, fullSyncDirOnly);
    	
    	// Finish linking them
        getTransactionTemplate().execute(new TransactionCallback<Object>() {
        	@Override
			public Object doInTransaction(final TransactionStatus status) {
        		netFolderConfig.setFolderId(folder.getId());
				return null;
        	}
        });

        /* TODO $$$$$$ It is unclear whether we still need scheduled sync job with 1.2
   		if(Boolean.FALSE.equals(inheritIndexContent)) {
   			// This net folder does not inherit file content indexing setting from the parent net folder server
   			if(indexContent) {
   				// Make sure that a background job exists for this net folder
   				netFolderContentIndexingJobSchedule(folderId);
   			}
   			else {
   				// We don't have to try to remove the background job, if any, for this net folder at this point.
   				// It will self-destruct itself at next run.
   				//netFolderContentIndexingUnschedule(folderId);
   			}
   		}
   		else {
   			// Either this net folder inherits file content indexing setting from the parent net folder server
   			// or the setting is unspecified (which is equivalent to 'on' by default). In this case, whether
   			// or not file content indexing job should exist for this net folder depends on how the setting
   			// is defined on the net folder server. 
   	   		Boolean indexContentNFS = null;
   	   		Binder netFolder = getBinderModule().getBinder(folderId);
   	   		ResourceDriver driver = netFolder.getResourceDriver();
   	   		if(driver != null) {
   	   			ResourceDriverConfig config = driver.getConfig();
   	   			indexContentNFS = config.getIndexContent();
   	   		}
   			if(Boolean.TRUE.equals(indexContentNFS)) {
   				// File content indexing is 'on' on the net folder server.
   				// Make sure that a background job exists for this net folder
   				netFolderContentIndexingJobSchedule(folderId);
   			}
   			else {
   				// File content indexing is either 'off' or unspecified (which is equivalent to 'off'
   				// by default) on this net folder server. In this case, we don't have to try to remove
   				// the background job, if any, for this net folder at this point. It will self-destruct
   				// itself at next run.
   				//netFolderContentIndexingUnschedule(folderId);
   			}
   		}
   		*/

   		return netFolderConfig;
    }
    
	@Override
    public void modifyNetFolder(final NetFolderConfig netFolderConfig) {
        getTransactionTemplate().execute(new TransactionCallback<Object>() {
        	@Override
			public Object doInTransaction(final TransactionStatus status) {
        		getCoreDao().update(netFolderConfig);
				return null;
        	}
        });
    }
    
	/*
	@Override
    public void modifyNetFolder(Long netFolderConfigId, final String name, final Long netFolderServerId, final String path, final Boolean isHomeDir, final boolean indexContent, final Boolean inheritIndexContent, final SyncScheduleOption syncScheduleOption, final Boolean fullSyncDirOnly ) throws AccessControlException, WriteFilesException, WriteEntryDataException {
    	final NetFolderConfig nfc = getCoreDao().loadNetFolderConfig(netFolderConfigId);
        getTransactionTemplate().execute(new TransactionCallback<Object>() {
        	@Override
			public Object doInTransaction(final TransactionStatus status) {
            	nfc.setName(name);
            	nfc.setNetFolderServerId(netFolderServerId);
            	nfc.setResourcePath(path);
            	nfc.setHomeDir(isHomeDir);
            	nfc.setIndexContent(indexContent);
            	nfc.setUseInheritedIndexContent(inheritIndexContent);
            	nfc.setSyncScheduleOption(syncScheduleOption);
            	nfc.setFullSyncDirOnly(fullSyncDirOnly);
				return null;
        	}
        });
    }*/
    
	@Override
    public void deleteNetFolder(Long netFolderConfigId, boolean deleteSource) {
    	final NetFolderConfig nfc = getCoreDao().loadNetFolderConfig(netFolderConfigId);

    	getFolderModule().deleteNetFolder(nfc.getFolderId(), deleteSource);
    	
        getTransactionTemplate().execute(new TransactionCallback<Object>() {
        	@Override
			public Object doInTransaction(final TransactionStatus status) {
        		getCoreDao().delete(nfc);
				return null;
        	}
        });
    }
    
	/*
	 * @see org.kablink.teaming.module.netfolder.NetFolderModule#obtainFolderEntry(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public FolderEntry obtainFolderEntry(Long netFolderId, String filePath,
			boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @see org.kablink.teaming.module.netfolder.NetFolderModule#obtainFolder(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public Folder obtainFolder(Long netFolderId, String folderPath,
			boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	
	protected FolderModule getFolderModule() {
		return (FolderModule) SpringContextUtil.getBean("folderModule");
	}
	
	protected TemplateModule getTemplateModule() {
		return (TemplateModule) SpringContextUtil.getBean("templateModule");
	}
	
	protected TransactionTemplate getTransactionTemplate() {
		return (TransactionTemplate) SpringContextUtil.getBean("transactionTemplate");
	}
}