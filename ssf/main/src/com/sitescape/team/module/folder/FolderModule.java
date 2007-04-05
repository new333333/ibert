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
package com.sitescape.team.module.folder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;

import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.ReservedByAnotherUserException;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.web.tree.DomTreeBuilder;

/**
 * <code>FolderModule</code> provides folder-related operations that the caller
 * can invoke without concern for the specific type of the folder. 
 * Consequently, only those generic operations that are meaningful across 
 * many different folder types are defined in this module interface.
 * Additional type-specific operations must be defined in the respective module.   
 * 
 * @author Jong Kim
 */
public interface FolderModule {

	   /**
     * Create an entry object from the input data and add it to the specified
     * folder.  
     * 
     * @param folder
     * @param inputData raw input data
     * @return
     * @throws AccessControlException
     */
    public Long addEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems) throws AccessControlException, WriteFilesException;
    public Long addEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems, Boolean filesFromApplet) throws AccessControlException, WriteFilesException;
    public Long addReply(Long folderId, Long parentId, String definitionId, 
    		InputDataAccessor inputData, Map fileItems) throws AccessControlException, WriteFilesException;

    public Long addFolder(Long folderId, String definitionId, InputDataAccessor inputData,
       		Map fileItems) throws AccessControlException, WriteFilesException;
    public void addSubscription(Long folderId, Long entryId, int style); 

    /**
     * Complete deletion of folders previously marked for delete
     * Used by a scheduled job to finish the delete in the background
     *
     */
    public void cleanupFolders();
    /**
     * Delete a FolderEntry and all of its replies
     * @param parentFolderId
     * @param entryId
     * @throws AccessControlException
     */
    public void deleteEntry(Long parentFolderId, Long entryId) throws AccessControlException;
    public void deleteSubscription(Long folderId, Long entryId);
    public void deleteTag(Long binderId, Long entryId, String tagId);
    
    public List getCommunityTags(Long binderId, Long entryId);
	public List getCommunityTags(FolderEntry entry);
	  /**
     * Return Dom tree of folders starting at the topFolder of the specified folder
     * @param folderId
     * @return
     */
	public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper);
	public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper, int levels);
	public Map getEntries(Long folderId) throws AccessControlException;
	public Map getEntries(Long folderId, Map options) throws AccessControlException;
    public FolderEntry getEntry(Long parentFolderId, Long entryId) throws AccessControlException;
    public Map getEntryTree(Long parentFolderId, Long entryId) throws AccessControlException;
    public Folder getFolder(Long folderId);
	public Collection getFolders(List folderIds);
    /**
     * Returns a list of ids of all folders that the user has read access to. 
     * 
     * @return
     */
    public List<String> getFolderIds(Integer type);
    public Set getFolderEntryByNormalizedTitle(Long folderId, String title) throws AccessControlException;
	public Map getFullEntries(Long folderId) throws AccessControlException;
	public Map getFullEntries(Long folderId, Map options) throws AccessControlException;
	   /**
     * Finds library folder entry by the file name. If no matching entry is 
     * found it returns <code>null</code>. If matching entry is found but the
     * user has no access to it, it throws <code>AccessControlException</code>.
     *  
     * @param libraryFolder
     * @param fileName
     * @return
     * @throws AccessControlException
     */
    public FolderEntry getLibraryFolderEntryByFileName(Folder libraryFolder, String fileName)
    	throws AccessControlException;
    
    
	public Map getManualTransitions(FolderEntry entry, Long stateId);
	public List getPersonalTags(Long binderId, Long entryId);
	public List getPersonalTags(FolderEntry entry);    
    public Set<Folder> getSubfolders(Folder folder);
    public Set<String> getSubfoldersTitles(Folder folder);
	public Subscription getSubscription(FolderEntry entry); 
 	  
	public Map getUnseenCounts(List folderIds);
	public Map getWorkflowQuestions(FolderEntry entry, Long stateId);
	public void indexEntry(FolderEntry entry, boolean includeReplies);
    /**
     * 
     * @param folderId
     * @param entryId
     * @param inputData
     * @param fileItems
     * @param deleteAttachments A collection of either <code>java.lang.String</code>
     * representing database id of each attachment or 
     * {@link com.sitescape.team.domain.Attachment Attachment}.
     * @throws AccessControlException
     * @throws WriteFilesException
     * @throws ReservedByAnotherUserException
     */
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection deleteAttachments, Map<FileAttachment,String> fileRenamesTo) 
    throws AccessControlException, WriteFilesException, ReservedByAnotherUserException;
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Boolean filesFromApplet) 
    throws AccessControlException, WriteFilesException, ReservedByAnotherUserException;
    public void modifyWorkflowState(Long folderId, Long entryId, Long stateId, String toState) throws AccessControlException;

    public void moveEntry(Long folderId, Long entryId, Long destinationId);

    /**
     * Reserve the entry.
     * 
     * @param folderId
     * @param entryId
     * @throws AccessControlException
     * @throws ReservedByAnotherUserException
     * @throws FilesLockedByOtherUsersException
     */
    public void reserveEntry(Long folderId, Long entryId)
    	throws AccessControlException, ReservedByAnotherUserException,
    	FilesLockedByOtherUsersException;
    
    
	public void setTag(Long binderId, Long entryId, String tag, boolean community);
    public void setUserRating(Long folderId, Long entryId, long value);
	public void setUserRating(Long folderId, long value);
	public void setUserVisit(FolderEntry entry);
    public void setWorkflowResponse(Long folderId, Long entryId, Long stateId, InputDataAccessor inputData);
    
    public boolean testAccess(Folder folder, String operation);
    public boolean testAccess(FolderEntry entry, String operation);
    public boolean testTransitionOutStateAllowed(FolderEntry entry, Long stateId);
	public boolean testTransitionInStateAllowed(FolderEntry entry, Long stateId, String toState);
   
    
    /**
     * Cancel reservation on the entry.
     * 
     * @param folderId
     * @param entryId
     * @throws AccessControlException
     * @throws ReservedByAnotherUserException
     */
    public void unreserveEntry(Long folderId, Long entryId)
		throws AccessControlException, ReservedByAnotherUserException;
    

}