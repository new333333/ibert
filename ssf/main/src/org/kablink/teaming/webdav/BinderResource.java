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
package org.kablink.teaming.webdav;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderIndexData;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.security.AccessControlException;

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.CopyableResource;
import com.bradmcevoy.http.DeletableCollectionResource;
import com.bradmcevoy.http.DeletableResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.MakeCollectionableResource;
import com.bradmcevoy.http.MoveableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;

/**
 * @author jong
 *
 */
public abstract class BinderResource extends WebdavCollectionResource  
implements PropFindableResource, GetableResource, CollectionResource, MakeCollectionableResource, DeletableResource, CopyableResource, MoveableResource, DeletableCollectionResource {

	// Required properties
	protected Long id;
	protected EntityType entityType;
	protected String title;
	protected String path;
	protected Date createdDate;
	protected Date modifiedDate;
	protected boolean library;
	protected boolean mirrored;
	protected String webdavPath;

	private EntityIdentifier entityIdentifier;

	private BinderResource(WebdavResourceFactory factory, String webdavPath, EntityIdentifier entityIdentifier, String title, String path, Date createdDate, Date modifiedDate,
			boolean library, boolean mirrored) {
		super(factory);
		this.webdavPath = webdavPath;
		this.id = entityIdentifier.getEntityId();
		this.entityType = entityIdentifier.getEntityType();
		this.entityIdentifier = entityIdentifier;
		this.title = title;
		this.path = path;
		this.createdDate = getMiltonSafeDate(createdDate);
		this.modifiedDate = getMiltonSafeDate(modifiedDate);
		this.library = library;
		this.mirrored = mirrored;
	}
	
	public BinderResource(WebdavResourceFactory factory, String webdavPath, Binder binder) {
		this(factory,
				webdavPath,
				binder.getEntityIdentifier(),
				binder.getTitle(),
				binder.getPathName(),
				binder.getCreation().getDate(),
				binder.getModification().getDate(),
				binder.isLibrary(),
				binder.isMirrored());
	}
	
	public BinderResource(WebdavResourceFactory factory, String webdavPath, BinderIndexData bid) {
		this(factory,
				webdavPath,
				new EntityIdentifier(bid.getId(), bid.getEntityType()),
				bid.getTitle(),
				bid.getPath(),
				bid.getCreatedDate(),
				bid.getModifiedDate(),
				bid.isLibrary(),
				bid.isMirrored());
	}
	
	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getUniqueId()
	 */
	@Override
	public String getUniqueId() {
		return entityIdentifier.toString();
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getName()
	 */
	@Override
	public String getName() {
		return title;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getModifiedDate()
	 */
	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.PropFindableResource#getCreateDate()
	 */
	@Override
	public Date getCreateDate() {
		return createdDate;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.DeletableCollectionResource#isLockedOutRecursive(com.bradmcevoy.http.Request)
	 */
	@Override
	public boolean isLockedOutRecursive(Request request) {
		// For now, always return false without checking actual locks on individual files
		// so that efficient version of delete can proceed.
		// In the current system this is expensive to implement and execute, but it's no 
		// worse than the browser interface since both interface proceed with binder
		// deletion without first checking any locks that might exist.
		return false;
	}

	@Override
	public String getWebdavPath() {
		/*
		StringBuilder sb = new StringBuilder(DavResource.WEBDAV_PATH);
		if(!path.startsWith("/"))
			sb.append("/");
		return sb.append(path).toString();
		*/
		return webdavPath;
	}

	@Override
	public String toString() {
		return path;
	}
	
	public EntityIdentifier getEntityIdentifier() {
		return entityIdentifier;
	}
	
	protected Resource makeResourceFromBinder(Binder binder) {
		if(binder == null)
			return null;
		
		if(binder instanceof Workspace) {
			Workspace w = (Workspace) binder;
			if(w.isDeleted() || w.isPreDeleted())
				return null;
			else 
				return new WorkspaceResource(factory, getChildWebdavPath(binder.getTitle()), w);
		}
		else if(binder instanceof Folder) {
			Folder f = (Folder) binder;
			if(f.isDeleted() || f.isPreDeleted())
				return null;
			else 
				return new FolderResource(factory, getChildWebdavPath(binder.getTitle()), f);
		}
		else {
			return null;
		}
	}
	
	protected Resource makeResourceFromBinder(BinderIndexData binder) {
		if(binder == null)
			return null;
		
		EntityType entityType = binder.getEntityType();
		if(EntityType.workspace == entityType) {
			return new WorkspaceResource(factory, getChildWebdavPath(binder.getTitle()), binder);
		}
		else if(EntityType.profiles == entityType) {
			return new WorkspaceResource(factory, getChildWebdavPath(binder.getTitle()), binder);
		}
		else if(EntityType.folder == entityType) {
			return new FolderResource(factory, getChildWebdavPath(binder.getTitle()), binder);
		}
		else {
			return null;
		}
	}
	
	protected void renameBinder(Binder binder, String newTitle) 
			throws AccessControlException, WriteFilesException, WriteEntryDataException {
		// Do this only if the new title is actually different from the current value.
		if(binder != null && !newTitle.equals(binder.getTitle())) {
			Map data = new HashMap();
			data.put("title", newTitle);
			getBinderModule().modifyBinder(binder.getId(), new MapInputData(data), null, null, null);
		}
	}
	
	private String getChildWebdavPath(String childName) {
		if(webdavPath.endsWith("/"))
			return webdavPath + childName;
		else
			return webdavPath + "/" + childName;
	}
}