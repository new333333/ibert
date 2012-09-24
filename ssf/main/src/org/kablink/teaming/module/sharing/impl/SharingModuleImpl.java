/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.sharing.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoShareItemByTheIdException;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.ShareItem.RecipientType;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.jobs.ExpiredShareHandler;
import org.kablink.teaming.jobs.LicenseMonitor;
import org.kablink.teaming.jobs.ZoneSchedule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.processor.BinderProcessor;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.Validator;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * This module gives us the transaction semantics to deal with the "Shared with Me" features.  
 *
 * @author Peter Hurley
 *
 */
public class SharingModuleImpl extends CommonDependencyInjection implements SharingModule, ZoneSchedule {

	private FolderModule folderModule;
	private BinderModule binderModule;
	private ProfileModule profileModule;
	private TransactionTemplate transactionTemplate;
	
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

    @Override
	public void checkAccess(ShareItem shareItem, SharingOperation operation)
	    	throws AccessControlException {
    	User user = RequestContextHolder.getRequestContext().getUser();
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
		EntityIdentifier entityIdentifier = shareItem.getSharedEntityIdentifier();
		AccessControlManager accessControlManager = getAccessControlManager();
		if (accessControlManager == null) {
			accessControlManager = ((AccessControlManager) SpringContextUtil.getBean("accessControlManager"));
		}
    	
		switch (operation) {
		case addShareItem:
			//Make sure sharing is enabled at the zone level for this type of user
			accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING);

			//Check that the user is either the entity owner, or has the right to share entities
			if (entityIdentifier.getEntityType().equals(EntityType.folderEntry)) {
				FolderEntry fe = getFolderModule().getEntry(null, entityIdentifier.getEntityId());
				if (accessControlManager.testOperation(user, fe, WorkAreaOperation.ALLOW_SHARING)) {
					return;
				}
				//User didn't have AllowSharing, so now check if user is owner of the entity
				if (user.getId().equals(fe.getCreation().getPrincipal().getId())) {
					//This is the owner of the entry. Allow the sharing.
					return;
				}
			} else if (entityIdentifier.getEntityType().equals(EntityType.folder) ||
					entityIdentifier.getEntityType().equals(EntityType.workspace)) {
				Binder binder = getBinderModule().getBinder(entityIdentifier.getEntityId());
				//If this is a Filr Net Folder, check if sharing is allowed at the folder level
				if (binder.isAclExternallyControlled() && 
						!SPropsUtil.getBoolean("sharing.netFolders.allowed", false)) {
					throw new AccessControlException("errorcode.sharing.netfolders.notAllowed", new Object[] {});
				}
				if (accessControlManager.testOperation(user, binder, WorkAreaOperation.ALLOW_SHARING)) {
					return;
				}
				//User didn't have AllowSharing, so now check if user is owner of the entity
				if (user.getId().equals(binder.getCreation().getPrincipal().getId())) {
					//This is the owner of the binder. Allow the sharing.
					return;
				}
			}
			break;
		case modifyShareItem:
			//The share creator and the entity owner can modify a shareItem
			if (!user.getId().equals(shareItem.getSharerId())) {
				//The user is not the creator of the share. Only the share item creator is allowed to modify it
				throw new AccessControlException();
			}
			//Check if this is the owner of the entity
			if (entityIdentifier.getEntityType().equals(EntityType.folderEntry)) {
				FolderEntry fe = getFolderModule().getEntry(null, entityIdentifier.getEntityId());
				if (accessControlManager.testOperation(user, fe, WorkAreaOperation.ALLOW_SHARING)) {
					return;
				}
				//User didn't have AllowSharing, so now check if user is owner of the entity
				if (user.getId().equals(fe.getCreation().getPrincipal().getId())) {
					//This is the owner of the entry. Allow the modification.
					return;
				}
			} else if (entityIdentifier.getEntityType().equals(EntityType.folder) ||
					entityIdentifier.getEntityType().equals(EntityType.workspace)) {
				Binder binder = getBinderModule().getBinder(entityIdentifier.getEntityId());
				//If this is a Filr Net Folder, check if sharing is allowed at the folder level
				if (binder.isAclExternallyControlled() && 
						!SPropsUtil.getBoolean("sharing.netFolders.allowed", false)) {
					throw new AccessControlException("errorcode.sharing.netfolders.notAllowed", new Object[] {});
				}
				if (accessControlManager.testOperation(user, binder, WorkAreaOperation.ALLOW_SHARING)) {
					return;
				}
				if (user.getId().equals(binder.getCreation().getPrincipal().getId())) {
					//This is the owner of the binder. Allow the modification.
					return;
				}
			}
			break;
		case deleteShareItem:
			//The share creator, the entity owner, or the site admin can delete a shareItem
			if (user.getId().equals(shareItem.getSharerId())) {
				//The user is the creator of the share
				return;
			}
			//Check if this is the owner of the entity
			if (entityIdentifier.getEntityType().equals(EntityType.folderEntry)) {
				FolderEntry fe = getFolderModule().getEntry(null, entityIdentifier.getEntityId());
				if (user.getId().equals(fe.getCreation().getPrincipal().getId())) {
					//This is the owner of the entry. Allow the modification.
					return;
				}
			} else if (entityIdentifier.getEntityType().equals(EntityType.folder) ||
					entityIdentifier.getEntityType().equals(EntityType.workspace)) {
				Binder binder = getBinderModule().getBinder(entityIdentifier.getEntityId());
				if (user.getId().equals(binder.getCreation().getPrincipal().getId())) {
					//This is the owner of the binder. Allow the modification.
					return;
				}
			}
			//Check for site administrator
			if (accessControlManager.testOperation(user, zoneConfig, WorkAreaOperation.ZONE_ADMINISTRATION)) {
				//This is a site administrator
				return;
			}
			break;
		default:
			throw new NotSupportedException(operation.toString(),
					"checkAccess");
		}
		//No access was found
		throw new AccessControlException();
	}
    
    @Override
	public boolean testAccess(ShareItem shareItem, SharingOperation operation) {
    	try {
    		checkAccess(shareItem, operation);
    		return true;
    	}
    	catch (AccessControlException ac) {
    		return false;
    	}
    }

    /**
     * Returns true if the current user can share the given
     * DefinableEntity and false otherwise.
     * 
     * @param de
     * 
     * @return
     */
    @Override
	public boolean testAddShareEntity(DefinableEntity de) {
		boolean reply = false;
		try {
			// Is sharing enabled at the zone level for this type of user.
	    	Long					zoneId               = RequestContextHolder.getRequestContext().getZoneId();
	    	ZoneConfig				zoneConfig           = getCoreDao().loadZoneConfig(zoneId);
			AccessControlManager	accessControlManager = getAccessControlManager();
			if (null == accessControlManager) {
				accessControlManager = ((AccessControlManager) SpringContextUtil.getBean("accessControlManager"));
			}
			accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING);

			// Is the entity a folder entry?
	    	User user = RequestContextHolder.getRequestContext().getUser();
			if (de.getEntityType().equals(EntityType.folderEntry)) {
				// Yes!  Does the user have AllowSharing rights on it?
				FolderEntry fe = ((FolderEntry) de);
				if (accessControlManager.testOperation(user, fe, WorkAreaOperation.ALLOW_SHARING)) {
					// Yes!
					reply = true;
				}
				
				// Is the user the owner of the folder entry?
				else if (user.getId().equals(fe.getCreation().getPrincipal().getId())) {
					// Yes!
					reply = true;
				}
				
			}

			// No, the entity isn't a folder entry!  Is it a folder or
			// workspace (i.e., a binder)?
			else if (de.getEntityType().equals(EntityType.folder) || de.getEntityType().equals(EntityType.workspace)) {
				// Yes!  Does the user have AllowSharing rights on it?
				Binder binder = ((Binder) de);
				//If this is a Filr Net Folder, check if sharing is allowed at the folder level
				//Also check that the folder isn't a Net Folder. Sharing Net Folders is not allowed
				if (!binder.isAclExternallyControlled() || 
						SPropsUtil.getBoolean("sharing.netFolders.allowed", false)) {
					if (accessControlManager.testOperation(user, binder, WorkAreaOperation.ALLOW_SHARING)) {
						// Yes!
						reply = true;
					}
					
					// Is the user the owner of the binder?
					else if (user.getId().equals(binder.getCreation().getPrincipal().getId())) {
						// Yes.
						reply = true;
					}
				}
			}
		}
		
		catch (AccessControlException ace) {
			// AccessControlException implies sharing isn't allowed.
			reply = false;
		}

		// If we get here, reply contains true if the user can add a
		// share to the given entity and false otherwise.  Return it.
		return reply;
	}
	
    
	@Override
	public boolean isSharingEnabled() {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
		AccessControlManager accessControlManager = getAccessControlManager();
		if (accessControlManager == null) {
			accessControlManager = ((AccessControlManager) SpringContextUtil.getBean("accessControlManager"));
		}
		return accessControlManager.testOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING);
	}

    //NO transaction
	@Override
	public void addShareItem(final ShareItem shareItem) {
		// Access check (throws error if not allowed)
		checkAccess(shareItem, SharingOperation.addShareItem);
		
		getTransactionTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				getCoreDao().save(shareItem);
				return null;
			}
		});

		//Index the entity that is being shared
		indexSharedEntity(shareItem);
	}
	
    //NO transaction
	@Override
	public void modifyShareItem(final ShareItem latestShareItem, final Long previousShareItemId) {
		if(latestShareItem == null)
			throw new IllegalArgumentException("Latest share item must be specified");
		if(previousShareItemId == null)
			throw new IllegalArgumentException("Previous share item ID must be specified");
		if(latestShareItem.getId() != null)
			throw new IllegalArgumentException("Latest share item must be transient");
		
		// Access check (throws error if not allowed)
		checkAccess(latestShareItem, SharingOperation.modifyShareItem);
		
		getTransactionTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				// Update previous snapshot
				try {
					ShareItem previousShareItem = getProfileDao().loadShareItem(previousShareItemId);
					
					previousShareItem.setLatest(false);
					
					getCoreDao().update(previousShareItem);
				}
				catch(NoShareItemByTheIdException e) {
					// The previous snapshot isn't found.
					logger.warn("Previous share item with id '" + previousShareItemId + "' is not found.");
				}

				// Save new snapshot
				getCoreDao().save(latestShareItem);
				
				return null;
			}
		});

		//Index the entity that is being shared
		indexSharedEntity(latestShareItem);		
	}
	
    //NO transaction
	@Override
	public void deleteShareItem(Long shareItemId) {
		final ShareItem shareItem;
		try {
			shareItem = getProfileDao().loadShareItem(shareItemId);
		}
		catch(NoShareItemByTheIdException e) {
			// already gone, ok
			return;
		}
		
		// Access check (throws error if not allowed)
		checkAccess(shareItem, SharingOperation.deleteShareItem);
		
		getTransactionTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				getCoreDao().delete(shareItem);
				return null;
			}
		});
		
		//Index the entity that is being shared
		indexSharedEntity(shareItem);
	}
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.profile.ProfileModule#getShareItem(java.lang.Long)
	 */
	@Override
	public ShareItem getShareItem(Long shareItemId)
			throws NoShareItemByTheIdException {
		// Access check
		// There is no access check on getting shareItems due to performance concerns. 
		// We are counting on the UI to not show items that the user is not allowed to see.
		return getProfileDao().loadShareItem(shareItemId);
	}
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.profile.ProfileModule#getShareItems(java.util.Collection)
	 */
	@Override
	public List<ShareItem> getShareItems(Collection<Long> shareItemIds) {
		// Access check?
		// There is no access check on getting shareItems due to performance concerns. 
		// We are counting on the UI to not show items that the user is not allowed to see.
		return getProfileDao().loadShareItems(shareItemIds);
	}
    
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.sharing.SharingModule#getShareItems(org.kablink.teaming.dao.util.ShareItemSelectSpec)
	 */
	@Override
	public List<ShareItem> getShareItems(ShareItemSelectSpec selectSpec) {
		// Access check?
		// There is no access check on getting shareItems due to performance concerns. 
		// We are counting on the UI to not show items that the user is not allowed to see.
		
		if(selectSpec.accountForInheritance && selectSpec.sharedEntityIdentifiers != null && selectSpec.sharedEntityIdentifiers.size() > 0) {
			Collection<EntityIdentifier> orig = selectSpec.sharedEntityIdentifiers;
			HashSet<EntityIdentifier> copy = new HashSet<EntityIdentifier>(orig);
			for(EntityIdentifier entityIdentifier:orig) {
				addShareRightInheritingParents(entityIdentifier, copy);
			}
			selectSpec.sharedEntityIdentifiers = copy;
			List<ShareItem> result = getProfileDao().findShareItems(selectSpec);
			selectSpec.sharedEntityIdentifiers = orig; // Restore the original
			return result;
		}
		else {
			return getProfileDao().findShareItems(selectSpec);
		}
	}
    
	private void addShareRightInheritingParents(EntityIdentifier entityIdentifier, Set<EntityIdentifier> set) {
		DefinableEntity entity;
		
		try {
			entity = loadDefinableEntity(entityIdentifier);
		}
		catch(Exception e) {
			logger.warn("Error loading shared entity '" + entityIdentifier.toString() + "': " + e.toString());
			return;
		}
		
		if(entity instanceof FolderEntry) {
			FolderEntry entry = (FolderEntry) entity;
			if(!entry.hasEntryAcl() || (entry.hasEntryAcl() && entry.isIncludeFolderAcl())) {
				// This entry inherits the parent's ACLs.
				set.add(entry.getParentFolder().getEntityIdentifier());
				WorkArea workArea = entry.getParentFolder();
	        	while(workArea.isFunctionMembershipInherited()) {
	        		workArea = workArea.getParentWorkArea();
	        		if(workArea instanceof DefinableEntity)
	        			set.add(((DefinableEntity)workArea).getEntityIdentifier());
	        	}
			}
		}
		else if(entity instanceof Folder) {
	   		WorkArea workArea = (Folder) entity;
        	while(workArea.isFunctionMembershipInherited()) {
        		workArea = workArea.getParentWorkArea();
        		if(workArea instanceof DefinableEntity)
        			set.add(((DefinableEntity)workArea).getEntityIdentifier());
        	}
		}
		else {
			logger.warn("Invalid shared entity '" + entityIdentifier + "'");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.sharing.SharingModule#getSharedEntity(org.kablink.teaming.domain.ShareItem)
	 */
	@Override
	public DefinableEntity getSharedEntity(ShareItem shareItem) {
		return loadDefinableEntity(shareItem.getSharedEntityIdentifier());
	}

	private DefinableEntity loadDefinableEntity(EntityIdentifier entityIdentifier) {
		EntityIdentifier.EntityType entityType = entityIdentifier.getEntityType();
		if(entityType == EntityIdentifier.EntityType.folderEntry) {
			return getFolderModule().getEntry(null, entityIdentifier.getEntityId());
		}
		else if(entityType == EntityIdentifier.EntityType.folder || entityType == EntityIdentifier.EntityType.workspace) {
			return getBinderModule().getBinder(entityIdentifier.getEntityId());
		}
		else {
			throw new IllegalArgumentException("Unsupported entity type '" + entityType.name() + "' for sharing");
		}
	}
	
	@Override
	public DefinableEntity getSharedRecipient(ShareItem shareItem) {
		ShareItem.RecipientType recipientType = shareItem.getRecipientType();
		if(recipientType == RecipientType.user || recipientType == RecipientType.group) {
			return getProfileModule().getEntry(shareItem.getRecipientId());
			
		} else if(recipientType == RecipientType.team) {
			return getBinderModule().getBinder(shareItem.getRecipientId());
			
		} else {
			throw new IllegalArgumentException("Unsupported recipient type '" + recipientType.name() + "' for sharing");
		}
	}

	protected FolderModule getFolderModule() {
		if (folderModule == null) {
			folderModule = (FolderModule) SpringContextUtil.getBean("folderModule");
		}
		return folderModule;
	}

	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}

	protected BinderModule getBinderModule() {
		if (binderModule == null) {
			binderModule = (BinderModule) SpringContextUtil.getBean("binderModule");
		}
		return binderModule;
	}

	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	
	protected ProfileModule getProfileModule() {
		if (profileModule == null) {
			profileModule = (ProfileModule) SpringContextUtil.getBean("profileModule");
		}
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	
	//Routine to re-index an entity after a change in sharing
	protected void indexSharedEntity(ShareItem shareItem) {
		DefinableEntity entity = getSharedEntity(shareItem);
		if (entity.getEntityType() == EntityType.folderEntry) {
			folderModule.indexEntry((FolderEntry) entity, Boolean.TRUE);
		}
		else if (entity.getEntityType() == EntityIdentifier.EntityType.folder || 
				entity.getEntityType() == EntityIdentifier.EntityType.workspace) {
			// Sharing a binder can give the recipient access not only to the binder being explicitly
			// shared but also to the sub-binders as long as those sub-binders inherit ACLs from
			// their parents. 
			Binder binder = (Binder) entity;
			loadBinderProcessor(binder).indexFunctionMembership(binder, true, Boolean.FALSE);
		}
	}

	protected ExpiredShareHandler getExpiredShareHandler(Workspace zone) {
		String className = SPropsUtil.getString("job.expired.share.handler.class", "org.kablink.teaming.jobs.DefaultExpiredShareHandler");
		return (ExpiredShareHandler) ReflectHelper.getInstance(className);
	}
	
	//called on zone startup
	@Override
    public void startScheduledJobs(Workspace zone) {
 	   	if (zone.isDeleted()) return;
 	   	ExpiredShareHandler job = getExpiredShareHandler(zone);
    	job.schedule(zone.getId(), SPropsUtil.getInt("job.expired.share.handler.interval.minutes", 5));
	}

	//called on zone delete
	@Override
	public void stopScheduledJobs(Workspace zone) {
		ExpiredShareHandler job = getExpiredShareHandler(zone);
   		job.remove(zone.getId());
	}

	//NO transaction
	@Override
	public void handleExpiredShareItem(final ShareItem shareItem) {
		if(shareItem.isExpirationHandled())
			return; // Already handled
		
		//Re-index the entity that has been shared and now expired.
		indexSharedEntity(shareItem);		

		// Mark the share item as handled
		getTransactionTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				shareItem.setExpirationHandled(true);
				getCoreDao().update(shareItem);
				return null;
			}
		});
	}

	private BinderProcessor loadBinderProcessor(Binder binder) {
		return (BinderProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
	}

}
