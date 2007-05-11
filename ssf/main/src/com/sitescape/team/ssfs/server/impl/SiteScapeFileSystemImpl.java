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
package com.sitescape.team.ssfs.server.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import javax.activation.FileTypeMap;

import com.sitescape.team.ssfs.AlreadyExistsException;
import com.sitescape.team.ssfs.CrossContextConstants;
import com.sitescape.team.ssfs.LockException;
import com.sitescape.team.ssfs.NoAccessException;
import com.sitescape.team.ssfs.NoSuchObjectException;
import com.sitescape.team.ssfs.TypeMismatchException;
import com.sitescape.team.ssfs.server.SiteScapeFileSystem;
import com.sitescape.team.util.AbstractAllModulesInjected;

public class SiteScapeFileSystemImpl extends AbstractAllModulesInjected 
implements SiteScapeFileSystem {

	private SiteScapeFileSystemInternal ssfsInt;
	private SiteScapeFileSystemLibrary ssfsLib;
	
	public SiteScapeFileSystemImpl() {
		ssfsInt = new SiteScapeFileSystemInternal(this);
		ssfsLib = new SiteScapeFileSystemLibrary(this);
	}
	
	public void setMimeTypes(FileTypeMap mimeTypes) {
		ssfsInt.setMimeTypes(mimeTypes);
		ssfsLib.setMimeTypes(mimeTypes);
	}

	public void createResource(Map uri) throws NoAccessException, 
	AlreadyExistsException, TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.createResource(uri);
		else
			ssfsLib.createResource(uri);
	}

	public void setResource(Map uri, InputStream content) 
	throws NoAccessException, NoSuchObjectException, TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.setResource(uri, content);
		else
			ssfsLib.setResource(uri, content);
	}

	public void createAndSetResource(Map uri, InputStream content) 
	throws NoAccessException, AlreadyExistsException, TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.createAndSetResource(uri, content);
		else
			ssfsLib.createAndSetResource(uri, content);
	}
	
	public void createDirectory(Map uri) throws NoAccessException, 
	AlreadyExistsException, TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.createDirectory(uri);
		else
			ssfsLib.createDirectory(uri);
	}
	
	public InputStream getResource(Map uri) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException {
		if(isInternal(uri))
			return ssfsInt.getResource(uri);
		else
			return ssfsLib.getResource(uri);
	}

	/*
	public long getResourceLength(Map uri) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException {
		if(isInternal(uri))
			return ssfsInt.getResourceLength(uri);
		else
			return ssfsLib.getResourceLength(uri);
	}*/

	public void removeObject(Map uri) throws NoAccessException, NoSuchObjectException {
		if(isInternal(uri))
			ssfsInt.removeObject(uri);
		else
			ssfsLib.removeObject(uri);
	}
	
	/*
	public Date getLastModified(Map uri) throws NoAccessException, NoSuchObjectException {
		if(isInternal(uri))
			return ssfsInt.getLastModified(uri);
		else
			return ssfsLib.getLastModified(uri);
	}

	public Date getCreationDate(Map uri) throws NoAccessException, NoSuchObjectException {
		if(isInternal(uri))
			return ssfsInt.getCreationDate(uri);
		else
			return ssfsLib.getCreationDate(uri);
	}*/

	public String[] getChildrenNames(Map uri) throws NoAccessException, 
	NoSuchObjectException {
		if(isInternal(uri))
			return ssfsInt.getChildrenNames(uri);
		else
			return ssfsLib.getChildrenNames(uri);
	}

	public Map getProperties(Map uri) throws NoAccessException,
	NoSuchObjectException {
		if(isInternal(uri))
			return ssfsInt.getProperties(uri);
		else
			return ssfsLib.getProperties(uri);
	}
	
	public void lockResource(Map uri, String lockId, String lockSubject, 
			Date lockExpirationDate, String lockOwnerInfo)
	throws NoAccessException, NoSuchObjectException, LockException,
	TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.lockResource(uri, lockId, lockSubject, lockExpirationDate, lockOwnerInfo);
		else
			ssfsLib.lockResource(uri, lockId, lockSubject, lockExpirationDate, lockOwnerInfo);
	}
	
	public void unlockResource(Map uri, String lockId) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.unlockResource(uri, lockId);
		else
			ssfsLib.unlockResource(uri, lockId);
	}
	
	public void copyObject(Map sourceUri, Map targetUri, boolean overwrite, boolean recursive)
	throws NoAccessException, NoSuchObjectException, 
	AlreadyExistsException, TypeMismatchException {
		if(isInternal(sourceUri))
			ssfsInt.copyObject(sourceUri, targetUri, overwrite, recursive);
		else
			ssfsLib.copyObject(sourceUri, targetUri, overwrite, recursive);
	}
	
	public void moveObject(Map sourceUri, Map targetUri, boolean overwrite)
	throws NoAccessException, NoSuchObjectException, 
	AlreadyExistsException, TypeMismatchException {
		if(isInternal(sourceUri))
			ssfsInt.moveObject(sourceUri, targetUri, overwrite);
		else
			ssfsLib.moveObject(sourceUri, targetUri, overwrite);		
	}
	
	private boolean isInternal(Map uri) {
		if(((String) uri.get(CrossContextConstants.URI_TYPE)).equals(CrossContextConstants.URI_TYPE_INTERNAL))
			return true;
		else
			return false;
	}
}
