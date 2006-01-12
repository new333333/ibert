package com.sitescape.ef.module.folder;

import java.util.Map;

import org.dom4j.Document;

import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.module.binder.EntryProcessor;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.WriteFilesException;
import com.sitescape.ef.security.AccessControlException;

/**
 * <code>ForumCoreProcessor</code> is a model processor for forum, which
 * defines a set of core operations around forum. 
 * 
 * @author Jong Kim
 */
public interface FolderCoreProcessor extends EntryProcessor {


    public Long addReply(FolderEntry parent, Definition def, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException;
    public Document getDomFolderTree(Folder folder, DomTreeBuilder domTreeHelper);
	public Long addFolder(Folder parentFolder, Folder folder) throws AccessControlException;
    public Map getEntryTree(Folder parentFolderId, Long entryId, int type) throws AccessControlException;
}
