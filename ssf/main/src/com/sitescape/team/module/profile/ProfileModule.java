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
/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.team.module.profile;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

import org.dom4j.Document;

import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;

public interface ProfileModule {
	public enum ProfileOperation {
		addEntry,
		deleteEntry,
		modifyEntry
	}
	
	 public void addEntries(Long binderId, Document doc) throws AccessControlException;
	 public Long addGroup(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
	 public Long addUser(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
	 public Workspace addUserWorkspace(User user) throws AccessControlException;
	/**
	 * Create an user from information from the portal.
	 * 
	 * @param zoneName 
	 * @param userName 
	 * @param password may be null
	 * @param updates may be null
	 * @return created user object
	 */
	public User addUserFromPortal(String zoneName, String userName, String password, Map updates);

	public void deleteEntry(Long binderId, Long id, boolean deleteWS)
		throws AccessControlException, WriteFilesException;
    /**
     * @param userId
     * @return
     */
    public Principal getEntry(Long binderId, Long userId) throws AccessControlException;

    public Map getGroups(Long binderId) throws AccessControlException;
    public Map getGroups(Long binderId, Map options) throws AccessControlException;
	public SortedSet<Group> getGroups(Collection<Long> groupIds) throws AccessControlException;
 	public SortedSet<Principal> getPrincipals(Collection<Long> ids, Long zoneId);
	public ProfileBinder getProfileBinder();
    public UserProperties getUserProperties(Long userId);
    public UserProperties getUserProperties(Long userId, Long folderId);
	public Map getUsers(Long binderId);
    public Map getUsers(Long binderId, Map options);
    public SortedSet<User> getUsers(Collection<Long> userIds);
    public String getUserIds(SortedSet<User> users, String strSeparator);
    
	/**
	 * Return a collection of user ids  They are either in the principal list
	 * or members of groups in the principal list.
	 * @param principalIds
	 * @return
	 */
	public SortedSet<User> getUsersFromPrincipals(Collection<Long> principalIds);
    public SeenMap getUserSeenMap(Long userId);
	
	public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData, 
			   Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo) 
			throws AccessControlException, WriteFilesException;

	public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData) 
		throws AccessControlException, WriteFilesException;
	  
	/**
	 * Update user from information from the portal.
	 * 
	 * @param user
	 * @param updates
	 */
	public void modifyUserFromPortal(User user, Map updates);       
    public UserProperties setUserProperty(Long userId, Long folderId, String property, Object value);
    public UserProperties setUserProperty(Long userId, String property, Object value);
    public void setSeen(Long userId, Entry entry);
    public void setSeen(Long userId, Collection<Entry> entries);
	public boolean testAccess(ProfileBinder binder, ProfileOperation operation);
	public void checkAccess(ProfileBinder binder, ProfileOperation operation) throws AccessControlException;
	public boolean testAccess(Principal entry, ProfileOperation operation);
	public void checkAccess(Principal entry, ProfileOperation operation) throws AccessControlException;


}
