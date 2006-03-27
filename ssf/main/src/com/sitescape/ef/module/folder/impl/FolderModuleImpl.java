package com.sitescape.ef.module.folder.impl;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Collection;

import org.apache.lucene.document.DateField;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.binder.BinderComparator;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.folder.FolderCoreProcessor;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.folder.index.IndexUtils;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.ef.util.NLT;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.util.Validator;
/**
 *
 * @author Jong Kim
 */
public class FolderModuleImpl extends CommonDependencyInjection implements FolderModule {
    private String[] entryTypes = {EntryIndexUtils.ENTRY_TYPE_ENTRY};
    protected DefinitionModule definitionModule;
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	/**
	 * Setup by spring
	 * @param definitionModule
	 */
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	private Folder loadFolder(Long folderId)  {
        String companyId = RequestContextHolder.getRequestContext().getZoneName();
        return  getFolderDao().loadFolder(folderId, companyId);
		
	}
	private FolderCoreProcessor loadProcessor(Folder folder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
		return (FolderCoreProcessor)getProcessorManager().getProcessor(folder, FolderCoreProcessor.PROCESSOR_KEY);
	}

	public Folder getFolder(Long folderId)
		throws NoFolderByTheIdException, AccessControlException {
		Folder folder = loadFolder(folderId);
	
		// Check if the user has "read" access to the folder.
		getAccessControlManager().checkAcl(folder, AccessType.READ);		
    return folder;        
} 
	public Collection getFolders(List folderIds) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new BinderComparator(user.getLocale());
       	TreeSet<Binder> result = new TreeSet<Binder>(c);
		for (int i=0; i<folderIds.size(); ++i) {
			try {
				result.add(getFolder((Long)folderIds.get(i)));
			} catch (NoFolderByTheIdException ex) {
			} catch (AccessControlException ax) {
			}
			
		}
		return result;
	}
   

    public Long addFolder(Long parentFolderId, Map input) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Folder parentFolder = folderDao.loadFolder(parentFolderId, user.getZoneName());
    	getAccessControlManager().checkOperation(parentFolder, WorkAreaOperation.CREATE_FOLDERS);
 
    	Folder folder = new Folder();
    	folder.setZoneName(user.getZoneName());
        folder.setCreation(new HistoryStamp(user));
        folder.setModification(folder.getCreation());
        ObjectBuilder.updateObject(folder, input);
        //refresh parentFolder to ensure we have the latest 
        getCoreDao().refresh(parentFolder);
      	parentFolder.addFolder(folder);
        // The sub-folder inherits the default ACLs of the parent folder.
        // The default ACLs of the sub-folder can be changed subsequently. 
        getAclManager().doInherit(folder);
        getCoreDao().save(folder);
                  
        return folder.getId();
    }
 
    public void checkAddFolderAllowed(Folder parentFolder) {
    	getAccessControlManager().checkOperation(parentFolder, WorkAreaOperation.CREATE_FOLDERS);    	
    }
    public Long addEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems) throws AccessControlException, WriteFilesException {
        Folder folder = loadFolder(folderId);
        Definition def = null;
        if (!Validator.isNull(definitionId)) { 
        	def = getCoreDao().loadDefinition(definitionId, folder.getZoneName());
        } else {
        	def = folder.getDefaultEntryDef();
        }
        
        return loadProcessor(folder).addEntry(folder, def, FolderEntry.class, inputData, fileItems);
    }
    public void checkAddEntryAllowed(Folder folder) {
         loadProcessor(folder).addEntry_accessControl(folder);    	    	
    }

    public Long addReply(Long folderId, Long parentId, String definitionId, 
    		InputDataAccessor inputData, Map fileItems) throws AccessControlException, WriteFilesException {
        Folder folder = loadFolder(folderId);
        Definition def = getCoreDao().loadDefinition(definitionId, folder.getZoneName());
        FolderCoreProcessor processor = loadProcessor(folder);
        //load parent entry
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, parentId, EntityType.folderEntry);
        return processor.addReply(entry, def, inputData, fileItems);
    }
    public void checkAddReplyAllowed(FolderEntry entry) throws AccessControlException {
         loadProcessor(entry.getParentFolder()).addReply_accessControl(entry.getParentFolder(), entry);    	
    }
    public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData) 
			throws AccessControlException, WriteFilesException {
    	modifyEntry(binderId, id, inputData, new HashMap());
    }
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems) throws AccessControlException, WriteFilesException {
        Folder folder = loadFolder(folderId);
        loadProcessor(folder).modifyEntry(folder, entryId, inputData, fileItems);
    }

    public void checkModifyEntryAllowed(FolderEntry entry) {
        loadProcessor(entry.getParentFolder()).modifyEntry_accessControl(entry.getParentFolder(), entry);    	    	
    }
    
    public void modifyWorkflowState(Long folderId, Long entryId, Long tokenId, String toState) throws AccessControlException {
        Folder folder = loadFolder(folderId);       
        loadProcessor(folder).modifyWorkflowState(folder, entryId, tokenId, toState);
    }

    public List applyEntryFilter(Definition entryFilter) {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    
    public void indexFolderTree(Long folderId) {
		Folder folder = loadFolder(folderId);

    	//get sub-folders and index them all
		List folders = getFolderDao().loadFolderTree(folder);
		folders.add(folder);
		for (int i=0; i<folders.size(); ++i) {
	    	folder = (Folder) folders.get(i);
	    	try {
		    	loadProcessor(folder).indexBinder(folder);
	    	}
	    	catch(AccessControlException e) {
	    		//Skip folders to which access is denied
	    		continue;
	    	}
		}
    }
    
    public void indexFolder(Long folderId) {
		Folder folder = loadFolder(folderId);
        loadProcessor(folder).indexBinder(folder);
    }

    public Map getCommonEntryElements(Long folderId) {
    	Map entryElements = new HashMap();
    	Map itemData;
    	//Build a map of common elements for use in search filters
    	//  Each map has a "type" and a "caption". Types can be: title, text, user_list, or date.
    	
    	//title
    	itemData = new HashMap();
    	itemData.put("type", "title");
    	itemData.put("caption", NLT.get("filter.title"));
    	entryElements.put("title", itemData);
    	
    	//author
    	itemData = new HashMap();
    	itemData.put("type", "user_list");
    	itemData.put("caption", NLT.get("filter.author"));
    	entryElements.put("owner", itemData);
    	
    	//creation date
    	itemData = new HashMap();
    	itemData.put("type", "date");
    	itemData.put("caption", NLT.get("filter.creationDate"));
    	entryElements.put("creation", itemData);
    	
    	//modification date
    	itemData = new HashMap();
    	itemData.put("type", "date");
    	itemData.put("caption", NLT.get("filter.modificationDate"));
    	entryElements.put("modification", itemData);
    	
    	return entryElements;
    }
    
    public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper) {
        Folder folder = loadFolder(folderId);
        return loadProcessor(folder).getDomFolderTree(folder, domTreeHelper);
    }
    
    public Map getFolderEntries(Long folderId) {
        return getFolderEntries(folderId, 0);
    }

    public Map getFolderEntries(Long folderId, int maxChildEntries) {
    	return getFolderEntries(folderId, 0, null);
    }
    
    public Map getFolderEntries(Long folderId, int maxChildEntries, Document searchFilter) {
        Folder folder = loadFolder(folderId);
         return loadProcessor(folder).getBinderEntries(folder, entryTypes, maxChildEntries, searchFilter);
    }
    

    public Map getUnseenCounts(List folderIds) {
    	//search engine will do acl checks
        User user = RequestContextHolder.getRequestContext().getUser();
        SeenMap seenMap = coreDao.loadSeenMap(user.getId());
        Map results = new HashMap();
        List folders = new ArrayList();
        for (int i=0; i<folderIds.size(); ++i) {
        	try {
        		folders.add(loadFolder((Long)folderIds.get(i)));
        	} catch (NoFolderByTheIdException nf) {} 
        }
        if (folders.size() > 0) {
	        Hits hits = getRecentEntries(folders);
	        Map unseenCounts = new HashMap();
	        for (int i = 0; i < hits.length(); i++) {
				String folderIdString = hits.doc(i).getField(IndexUtils.TOP_FOLDERID_FIELD).stringValue();
				String entryIdString = hits.doc(i).getField(EntryIndexUtils.DOCID_FIELD).stringValue();
				Long entryId = null;
				if (entryIdString != null && !entryIdString.equals("")) {
					entryId = new Long(entryIdString);
				}
				Date modifyDate = DateField.stringToDate(hits.doc(i).getField(EntryIndexUtils.MODIFICATION_DATE_FIELD).stringValue());
				Counter cnt = (Counter)unseenCounts.get(folderIdString);
				if (cnt == null) {
					cnt = new Counter();
					unseenCounts.put(folderIdString, cnt);
				}
				if (entryId != null && (!seenMap.checkAndSetSeen(entryId, modifyDate, false))) {
					cnt.increment();
				}
			}
	        for (int i=0; i<folders.size(); ++i) {
	        	Folder f = (Folder)folders.get(i);
	        	Counter cnt = (Counter)unseenCounts.get(f.getId().toString());
	        	if (cnt == null) cnt = new Counter();
	        	results.put(f, cnt);
	        }
        }
        return results;
    }
 
    public Hits getRecentEntries(List folders) {
    	Hits results = null;
       	// Build the query
    	org.dom4j.Document qTree = DocumentHelper.createDocument();
    	Element rootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
    	Element andElement = rootElement.addElement(QueryBuilder.AND_ELEMENT);
    	andElement.addElement(QueryBuilder.USERACL_ELEMENT);
    	Element rangeElement = andElement.addElement(QueryBuilder.RANGE_ELEMENT);
    	rangeElement.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntryIndexUtils.MODIFICATION_DAY_FIELD);
    	rangeElement.addAttribute(QueryBuilder.INCLUSIVE_ATTRIBUTE, QueryBuilder.INCLUSIVE_TRUE);
    	Element startRange = rangeElement.addElement(QueryBuilder.RANGE_START);
    	Date now = new Date();
    	Date startDate = new Date(now.getTime() - ObjectKeys.SEEN_MAP_TIMEOUT);
    	startRange.addText(EntryIndexUtils.formatDayString(startDate));
    	Element finishRange = rangeElement.addElement(QueryBuilder.RANGE_FINISH);
    	finishRange.addText(EntryIndexUtils.formatDayString(now));
    	Element orElement = andElement.addElement(QueryBuilder.OR_ELEMENT);
    	Iterator itFolders = folders.iterator();
    	while (itFolders.hasNext()) {
    		Folder folder = (Folder) itFolders.next();
        	Element field = orElement.addElement(QueryBuilder.FIELD_ELEMENT);
        	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,IndexUtils.TOP_FOLDERID_FIELD);
        	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
        	Folder topFolder = folder.getTopFolder();
        	if (topFolder == null) topFolder = folder;
    		child.setText(topFolder.getId().toString());
    	}
    	
    	//Create the Lucene query
    	QueryBuilder qb = new QueryBuilder();
    	SearchObject so = qb.buildQuery(qTree);
    	
    	System.out.println("Query is: " + so.getQueryString());
    	
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        
        try {
	        results = luceneSession.search(so.getQuery(),so.getSortBy(),0,0);
        }
        finally {
            luceneSession.close();
        }
        return results;
    }

           
    public FolderEntry getEntry(Long parentFolderId, Long entryId) {
        return getEntry(parentFolderId, entryId, EntityType.folderEntry);
    }
    public FolderEntry getEntry(Long parentFolderId, Long entryId, EntityType type) {
        Folder folder = loadFolder(parentFolderId);
        return (FolderEntry)loadProcessor(folder).getEntry(folder, entryId, type);
    }
    public Map getEntryTree(Long parentFolderId, Long entryId) {
        Folder folder = loadFolder(parentFolderId);
        return loadProcessor(folder).getEntryTree(folder, entryId);   	
    }
    
    public void deleteEntry(Long parentFolderId, Long entryId) {
        Folder folder = loadFolder(parentFolderId);
        loadProcessor(folder).deleteEntry(folder, entryId);
    }
    public void checkDeleteEntryAllowed(FolderEntry entry) {
        loadProcessor(entry.getParentFolder()).deleteEntry_accessControl(entry.getParentFolder(), entry);    	
    	
    }
	public List getTags(Long binderId, Long entryId) {
		FolderEntry entry = getEntry(binderId, entryId);
		List tags = new ArrayList<Tag>();
		getCoreDao().loadTagsByOwner(entry.getEntityIdentifier());
		return tags;		
	}
	public void modifyTag(Long binderId, Long entryId, String tagId, Map updates) {
		FolderEntry entry = getEntry(binderId, entryId);
	   	Tag tag = coreDao.loadTagByOwner(tagId, entry.getEntityIdentifier());
	   	ObjectBuilder.updateObject(tag, updates);
	}
	public void addTag(Long binderId, Long entryId, Map updates) {
		FolderEntry entry = getEntry(binderId, entryId);
	   	Tag tag = new Tag();
	   	tag.setOwnerIdentifier(entry.getEntityIdentifier());
	  	ObjectBuilder.updateObject(tag, updates);
	  	coreDao.save(tag);   	
	}
	public void deleteTag(Long binderId, Long entryId, String tagId) {
		FolderEntry entry = getEntry(binderId, entryId);
	   	Tag tag = coreDao.loadTagByOwner(tagId, entry.getEntityIdentifier());
	   	getCoreDao().delete(tag);
	}
    
    /**
     * Helper classs to return folder unseen counts as an objects
     * @author Janet McCann
     *
     */
    public class Counter {
    	long count=0;
    	protected Counter() {	
    	}
    	public void increment() {
    		++count;
    	}
    	public long getCount() {
    		return count;
    	}
    	public String toString() {
    		return String.valueOf(count);
    	}
    	
    }
 }
