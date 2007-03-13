package com.sitescape.team.module.binder.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.SFQuery;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.TitleException;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WorkflowResponse;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.lucene.Hits;
import com.sitescape.team.module.binder.AccessUtils;
import com.sitescape.team.module.binder.EntryProcessor;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.file.FilesErrors;
import com.sitescape.team.module.file.FilterException;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.ChangeLogUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.EntryBuilder;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.workflow.WorkflowUtils;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.IndexSynchronizationManager;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchFieldResult;
import com.sitescape.team.search.SearchObject;
import com.sitescape.team.security.acl.AclControlled;
import com.sitescape.team.security.acl.AclContainer;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.FilterHelper;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.Validator;
/**
 *
 * Add entries to the binder
 * @author Jong Kim
 */
public abstract class AbstractEntryProcessor extends AbstractBinderProcessor 
	implements EntryProcessor {
    
	private static final int DEFAULT_MAX_CHILD_ENTRIES = ObjectKeys.LISTING_MAX_PAGE_SIZE;
	//***********************************************************************************************************	
    
	public Entry addEntry(final Binder binder, Definition def, Class clazz, 
    		final InputDataAccessor inputData, Map fileItems) 
    	throws WriteFilesException {
		Boolean filesFromApplet = new Boolean (false);
		return addEntry(binder, def, clazz, inputData, fileItems, filesFromApplet);
	}
	
	public Entry addEntry(final Binder binder, Definition def, Class clazz, 
    		final InputDataAccessor inputData, Map fileItems, Boolean filesFromApplet) 
    	throws WriteFilesException {
        // This default implementation is coded after template pattern. 
        
    	SimpleProfiler sp = new SimpleProfiler(false);
        final Map ctx = addEntry_setCtx(binder, null);
    	Map entryDataAll;
    	if (!filesFromApplet) {
        	sp.reset("addEntry_toEntryData").begin();
            entryDataAll = addEntry_toEntryData(binder, def, inputData, fileItems, ctx);
            sp.end().print();
    	}
    	else {
	    	sp.reset("createNewEntryWithAttachmentAndTitle").begin();
	    	entryDataAll = createNewEntryWithAttachmentAndTitle(def, inputData, fileItems, ctx);
		    sp.end().print();
    	}        
        
        final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
        List fileUploadItems = (List) entryDataAll.get(ObjectKeys.DEFINITION_FILE_DATA);
       
        try {
        	
        	sp.reset("addEntry_create").begin();
        	final Entry entry = addEntry_create(def, clazz, ctx);
        	sp.end().print();
        
        	sp.reset("addEntry_transactionExecute").begin();
        	// 	The following part requires update database transaction.
        	getTransactionTemplate().execute(new TransactionCallback() {
        		public Object doInTransaction(TransactionStatus status) {
        			//need to set entry/binder information before generating file attachments
        			//Attachments/Events need binder info for AnyOwner
        			addEntry_fillIn(binder, entry, inputData, entryData, ctx);
        			addEntry_preSave(binder, entry, inputData, entryData, ctx);      
        			addEntry_save(binder, entry, inputData, entryData,ctx);      
                   	//After the entry is successfully added, start up any associated workflows
                	addEntry_startWorkflow(entry, ctx);
         			addEntry_postSave(binder, entry, inputData, entryData, ctx);
       			return null;
        		}
        	});
        	sp.end().print();
           	// We must save the entry before processing files because it makes use
        	// of the persistent id of the entry. 
            sp.reset("addEntry_filterFiles").begin();
        	FilesErrors filesErrors = addEntry_filterFiles(binder, entry, entryData, fileUploadItems, ctx);
        	sp.end().print();

        	sp.reset("addEntry_processFiles").begin();
        	// We must save the entry before processing files because it makes use
        	// of the persistent id of the entry. 
        	filesErrors = addEntry_processFiles(binder, entry, fileUploadItems, filesErrors, ctx);
        	sp.end().print();
        
 
        	sp.reset("addEntry_indexAdd").begin();
        	// This must be done in a separate step after persisting the entry,
        	// because we need the entry's persistent ID for indexing. 
        	addEntry_indexAdd(binder, entry, inputData, fileUploadItems, ctx);
        	sp.end().print();
        	
        	sp.reset("addEntry_done").begin();
        	addEntry_done(binder, entry, inputData, ctx);
        	sp.end().print();
        	
         	if(filesErrors.getProblems().size() > 0) {
        		// At least one error occured during the operation. 
        		throw new WriteFilesException(filesErrors);
        	}
        	else {
        		return entry;
        	}
        } finally {
           	cleanupFiles(fileUploadItems);       	
        }
    }
	
    //Method Used to get the files uploaded by the Applet and title information from the entry
    protected Map createNewEntryWithAttachmentAndTitle(Definition def, InputDataAccessor inputData, Map fileItems, Map ctx)
    {
    	List fileData = new ArrayList();
    	String nameValue = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER;

    	Map entryDataAll = new HashMap();
    	Map entryData = new HashMap();
        entryDataAll.put(ObjectKeys.DEFINITION_ENTRY_DATA,  entryData);    	

		if (inputData.exists(ObjectKeys.FIELD_ENTITY_TITLE)) entryData.put(ObjectKeys.FIELD_ENTITY_TITLE, inputData.getSingleValue(ObjectKeys.FIELD_ENTITY_TITLE));
        
        if (def != null) {
        	boolean blnCheckForAppletFile = true;
        	int intFileCount = 1;

        	while (blnCheckForAppletFile) {
        		String fileEleName = nameValue + Integer.toString(intFileCount);
        		if (fileItems.containsKey(fileEleName)) {
        	    	MultipartFile myFile = (MultipartFile)fileItems.get(fileEleName);
        	    	String fileName = myFile.getOriginalFilename();
        	    	
        	    	if (fileName != null && !fileName.equals("")) {
            	    	// Different repository can be specified for each file uploaded.
            	    	// If not specified, use the statically selected one.  
            	    	String repositoryName = null;
            	    	if (inputData.exists(nameValue + "_repos" + Integer.toString(intFileCount))) 
            	    		repositoryName = inputData.getSingleValue(nameValue + "_repos" + Integer.toString(intFileCount));
            	    	if (repositoryName == null) {
            		    	repositoryName = "simpleFileRepository";
            		    	if (Validator.isNull(repositoryName)) repositoryName = RepositoryUtil.getDefaultRepositoryName();
            	    	}
            	    	FileUploadItem fui = new FileUploadItem(FileUploadItem.TYPE_ATTACHMENT, null, myFile, repositoryName);
            	    	fileData.add(fui);
        	    	}
        	    	intFileCount++;
        		}
        		else {
        			blnCheckForAppletFile = false;
        		}
        	}
	        entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA,  fileData);
            
        } else {
	        entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA,  new ArrayList());
        }
    	
        return entryDataAll;
    }
    protected Map addEntry_setCtx(Binder binder, Map ctx) {
    	return ctx;
    }

    protected FilesErrors addEntry_filterFiles(Binder binder, Entry entry, 
    		Map entryData, List fileUploadItems, Map ctx) throws FilterException {
   		FilesErrors nameErrors = new FilesErrors();
   		//name must be unique within Entry
   		for (int i=0; i<fileUploadItems.size(); ++i) {
			FileUploadItem fui1 = (FileUploadItem)fileUploadItems.get(i);
			for (int j=i+1; j<fileUploadItems.size(); ) {
    			FileUploadItem fui2 = (FileUploadItem)fileUploadItems.get(j);
    			if (fui1.getOriginalFilename().equalsIgnoreCase(fui2.getOriginalFilename()) &&
    				!fui1.getRepositoryName().equals(fui2.getRepositoryName())) {
    				fileUploadItems.remove(j);
    				nameErrors.addProblem(new FilesErrors.Problem(fui1.getRepositoryName(), 
       	   				fui1.getOriginalFilename(), FilesErrors.Problem.PROBLEM_FILE_EXISTS, new TitleException(fui1.getOriginalFilename())));
    			} else ++j;
			}
   		}
    			 
   		if (binder.isLibrary()) {
    		// 	Make sure the file name is unique if requested		
    		for (int i=0; i<fileUploadItems.size(); ) {
    			FileUploadItem fui = (FileUploadItem)fileUploadItems.get(i);
    			try {
    				getCoreDao().registerFileName(binder, entry, fui.getOriginalFilename());
    				fui.setRegistered(true);
    				++i;
    			} catch (TitleException te) {
    				fileUploadItems.remove(i);
    				nameErrors.addProblem(new FilesErrors.Problem(fui.getRepositoryName(), 
       	   					fui.getOriginalFilename(), FilesErrors.Problem.PROBLEM_FILE_EXISTS, te));
    			}
    		}
   		}
   		FilesErrors filterErrors = getFileModule().filterFiles(binder, fileUploadItems);
    	filterErrors.getProblems().addAll(nameErrors.getProblems());
    	return filterErrors;
    }

    protected FilesErrors addEntry_processFiles(Binder binder, 
    		Entry entry, List fileUploadItems, FilesErrors filesErrors, Map ctx) {
    	return getFileModule().writeFiles(binder, entry, fileUploadItems, filesErrors);
    }
    
    protected Map addEntry_toEntryData(Binder binder, Definition def, InputDataAccessor inputData, Map fileItems, Map ctx) {
        //Call the definition processor to get the entry data to be stored
        if (def != null) {
        	return getDefinitionModule().getEntryData(def.getDefinition(), inputData, fileItems);
        } else {
        	//handle basic fields only without definition
        	Map entryDataAll = new HashMap();
	        Map entryData = new HashMap();
			List fileData = new ArrayList();
			entryDataAll.put(ObjectKeys.DEFINITION_ENTRY_DATA, entryData);
			entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA, fileData);
 			if (inputData.exists(ObjectKeys.FIELD_ENTITY_TITLE)) entryData.put(ObjectKeys.FIELD_ENTITY_TITLE, inputData.getSingleValue("title"));
			if (inputData.exists(ObjectKeys.FIELD_ENTITY_DESCRIPTION)) {
				Description description = new Description();
				description.setText(inputData.getSingleValue(ObjectKeys.FIELD_ENTITY_DESCRIPTION));
				WebHelper.scanDescriptionForUploadFiles(description, fileData);
				WebHelper.scanDescriptionForAttachmentFileUrls(description);
				description.setFormat(Description.FORMAT_HTML);
				entryData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, description);
			}
      	
        	return entryDataAll;
        }
    }
    
    protected Entry addEntry_create(Definition def, Class clazz, Map ctx)  {
    	try {
    		Entry entry = (Entry)clazz.newInstance();
           	entry.setEntryDef(def);
        	if (def != null) entry.setDefinitionType(new Integer(def.getType()));
        	return entry;
    	} catch (Exception ex) {
    		return null;
    	}
    }
    
    protected void addEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setCreation(new HistoryStamp(user));
        entry.setModification(entry.getCreation());
        entry.setParentBinder(binder);
        entry.setLogVersion(Long.valueOf(1));
        
        //initialize collections, or else hibernate treats any new 
        //empty collections as a change and attempts a version update which
        //may happen outside the transaction
        entry.getAttachments();
        entry.getEvents();
        entry.getCustomAttributes();
        
        // The entry inherits acls from the parent by default, except workflow
        if ((entry instanceof AclControlled) && (binder instanceof AclContainer)) {
        	getAclManager().doInherit((AclContainer)binder, (AclControlled) entry);
        }
        for (Iterator iter=entryData.values().iterator(); iter.hasNext();) {
        	Object obj = iter.next();
        	//need to generate id for the event so its id can be saved in customAttr
        	if (obj instanceof Event)
        		getCoreDao().save(obj);
        }
        EntryBuilder.buildEntry(entry, entryData);
        takeCareOfLastModDate(entry, inputData);
        
    }

    //inside write transaction
    protected void addEntry_preSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    }

    //inside write transaction
    protected void addEntry_save(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx){
        getCoreDao().save(entry);
    }
    
    //inside write transaction
    protected void addEntry_postSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    	//create history - using timestamp and version from fillIn
		if (binder.isUniqueTitles()) getCoreDao().updateTitle(binder, entry, null, entry.getNormalTitle());
    	processChangeLog(entry, ChangeLog.ADDENTRY);
    }

    protected void addEntry_indexAdd(Binder binder, Entry entry, 
    		InputDataAccessor inputData, List fileUploadItems, Map ctx){
        
    	indexEntry(binder, entry, fileUploadItems, null, true);
    }
 
    protected void addEntry_done(Binder binder, Entry entry, InputDataAccessor inputData, Map ctx) {
    }
 
    //inside write transaction
    protected void addEntry_startWorkflow(Entry entry, Map ctx){
    	if (!(entry instanceof WorkflowSupport)) return;
    	Binder binder = entry.getParentBinder();
    	Map workflowAssociations = (Map) binder.getWorkflowAssociations();
    	if (workflowAssociations != null) {
    		//See if the entry definition type has an associated workflow
    		Definition entryDef = entry.getEntryDef();
    		if (entryDef != null) {
    			Definition wfDef = (Definition)workflowAssociations.get(entryDef.getId());
    			if (wfDef != null)	getWorkflowModule().addEntryWorkflow((WorkflowSupport)entry, entry.getEntityIdentifier(), wfDef);
    		}
    	}
    }
 	
   //***********************************************************************************************************
    public void modifyEntry(final Binder binder, final Entry entry, 
    		final InputDataAccessor inputData, Map fileItems, 
    		final Collection deleteAttachments, final Map<FileAttachment,String> fileRenamesTo)  
    		throws WriteFilesException {
    	
    	Boolean filesFromApplet = new Boolean(false);
    	modifyEntry(binder, entry, inputData, fileItems, deleteAttachments, fileRenamesTo, filesFromApplet);
    }
    
    public void modifyEntry(final Binder binder, final Entry entry, 
    		final InputDataAccessor inputData, Map fileItems, 
    		final Collection deleteAttachments, final Map<FileAttachment,String> fileRenamesTo, Boolean filesFromApplet)  
    		throws WriteFilesException {
    	SimpleProfiler sp = new SimpleProfiler(false);
        final Map ctx = modifyEntry_setCtx(entry, null);

    	Map entryDataAll;
    	if (!filesFromApplet) {
	    	sp.reset("modifyEntry_toEntryData").begin();
	    	entryDataAll = modifyEntry_toEntryData(entry, inputData, fileItems, ctx);
		    sp.end().print();
    	}
    	else {
	    	sp.reset("getFilesUploadedByApplet").begin();
	    	entryDataAll = getFilesUploadedByApplet(entry, inputData, fileItems, ctx);
		    sp.end().print();
    	}
	    
	    final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
	    List fileUploadItems = (List) entryDataAll.get(ObjectKeys.DEFINITION_FILE_DATA);
	    
	    try {	    	
	    	sp.reset("modifyEntry_transactionExecute").begin();
	    	// The following part requires update database transaction.
	    	//ctx can be used by sub-classes to pass info
	    	getTransactionTemplate().execute(new TransactionCallback() {
	    		public Object doInTransaction(TransactionStatus status) {
	    			modifyEntry_fillIn(binder, entry, inputData, entryData, ctx);
	    	    	modifyEntry_startWorkflow(entry, ctx);
	    			modifyEntry_postFillIn(binder, entry, inputData, entryData, fileRenamesTo, ctx);
 	    			return null;
	    		}});
	    	sp.end().print();
	        //handle outside main transaction so main changeLog doesn't reflect attactment changes
	        sp.reset("modifyBinder_removeAttachments").begin();
	    	List<FileAttachment> filesToDeindex = new ArrayList<FileAttachment>();
	    	List<FileAttachment> filesToReindex = new ArrayList<FileAttachment>();	    
            modifyEntry_removeAttachments(binder, entry, deleteAttachments, filesToDeindex, filesToReindex, ctx);
	        sp.end().print();
	    	
	    	sp.reset("modifyEntry_filterFiles").begin();
	    	FilesErrors filesErrors = modifyEntry_filterFiles(binder, entry, entryData, fileUploadItems, ctx);
	    	sp.end().print();

           	sp.reset("modifyEntry_processFiles").begin();
	    	filesErrors = modifyEntry_processFiles(binder, entry, fileUploadItems, filesErrors, ctx);
	    	sp.end().print();


	    	// Since index update is implemented as removal followed by add, 
	    	// the update requests must be added to the removal and then add
	    	// requests respectively. 
	    	filesToDeindex.addAll(filesToReindex);
	    	sp.reset("modifyEntry_indexRemoveFiles").begin();
	    	modifyEntry_indexRemoveFiles(binder, entry, filesToDeindex, ctx);
	    	sp.end().print();
	    	
	    	sp.reset("modifyEntry_indexAdd").begin();
	    	modifyEntry_indexAdd(binder, entry, inputData, fileUploadItems, filesToReindex,ctx);
	    	sp.end().print();
	    	
	    	sp.reset("modifyEntry_done").begin();
	    	modifyEntry_done(binder, entry, inputData,ctx);
	    	sp.end().print();
	    		    
	    	if(filesErrors.getProblems().size() > 0) {
	    		// At least one error occured during the operation. 
	    		throw new WriteFilesException(filesErrors);
	    	}
	    	else {
	    		return;
	    	} 
	    }finally {
		    cleanupFiles(fileUploadItems);
	    }
	}

    //Method Used to get the files uploaded by the Applet
    protected Map getFilesUploadedByApplet(Entry entry, InputDataAccessor inputData, Map fileItems, Map ctx)
    {
    	List fileData = new ArrayList();
    	String nameValue = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER;
    	Map entryDataAll = new HashMap();
    	
    	//No Definition Related Information - So it is set to empty HashMap
        entryDataAll.put(ObjectKeys.DEFINITION_ENTRY_DATA,  new HashMap());    	
    	
        //Call the definition processor to get the entry data to be stored
        Definition def = entry.getEntryDef();
        if (def != null) {
        	boolean blnCheckForAppletFile = true;
        	int intFileCount = 1;

        	while (blnCheckForAppletFile) {
        		String fileEleName = nameValue + Integer.toString(intFileCount);
        		if (fileItems.containsKey(fileEleName)) {
        	    	MultipartFile myFile = (MultipartFile)fileItems.get(fileEleName);
        	    	String fileName = myFile.getOriginalFilename();
        	    	
        	    	if (fileName != null && !fileName.equals("")) {
            	    	// Different repository can be specified for each file uploaded.
            	    	// If not specified, use the statically selected one.  
            	    	String repositoryName = null;
            	    	if (inputData.exists(nameValue + "_repos" + Integer.toString(intFileCount))) 
            	    		repositoryName = inputData.getSingleValue(nameValue + "_repos" + Integer.toString(intFileCount));
            	    	if (repositoryName == null) {
            		    	repositoryName = "simpleFileRepository";
            		    	if (Validator.isNull(repositoryName)) repositoryName = RepositoryUtil.getDefaultRepositoryName();
            	    	}
            	    	FileUploadItem fui = new FileUploadItem(FileUploadItem.TYPE_ATTACHMENT, null, myFile, repositoryName);
            	    	fileData.add(fui);
        	    	}
        	    	intFileCount++;
        		}
        		else {
        			blnCheckForAppletFile = false;
        		}
        	}
	        entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA,  fileData);
            
        } else {
	        entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA,  new ArrayList());
        }
    	
        return entryDataAll;
    }
    protected Map modifyEntry_setCtx(Entry entry, Map ctx) {
    	if (ctx == null) ctx = new HashMap();
    	//save normalized title and title before changes
		ctx.put(ObjectKeys.FIELD_ENTITY_NORMALIZED_TITLE, entry.getNormalTitle());
		ctx.put(ObjectKeys.FIELD_ENTITY_TITLE, entry.getTitle());
    	return ctx;
    }
   protected FilesErrors modifyEntry_filterFiles(Binder binder, Entry entry,
    		Map entryData, List fileUploadItems, Map ctx) throws FilterException, TitleException {
   		FilesErrors nameErrors = new FilesErrors();
   	 	//name must be unique within Entry
   		for (int i=0; i<fileUploadItems.size(); ++i) {
			FileUploadItem fui1 = (FileUploadItem)fileUploadItems.get(i);
			for (int j=i+1; j<fileUploadItems.size(); ) {
    			FileUploadItem fui2 = (FileUploadItem)fileUploadItems.get(j);
    			if (fui1.getOriginalFilename().equalsIgnoreCase(fui2.getOriginalFilename()) &&
    				!fui1.getRepositoryName().equals(fui2.getRepositoryName())) {
    				fileUploadItems.remove(j);
    				nameErrors.addProblem(new FilesErrors.Problem(fui1.getRepositoryName(), 
       	   				fui1.getOriginalFilename(), FilesErrors.Problem.PROBLEM_FILE_EXISTS, new TitleException(fui1.getOriginalFilename())));
    			} else ++j;
			}
   		}
    	// 	Make sure the file name is unique if requested		
    	for (int i=0; i<fileUploadItems.size(); ) {
    		FileUploadItem fui = (FileUploadItem)fileUploadItems.get(i);
       		String name = fui.getOriginalFilename();
    		try {
    			//check if name exists
    			FileAttachment fa = entry.getFileAttachment(name);
    			if (fa == null) {
    		   		if (binder.isLibrary()) {
    		   			getCoreDao().registerFileName(binder, entry, fui.getOriginalFilename());
    		   			fui.setRegistered(true);
    		   		}
    		   		++i;
       			} else if (!fui.getRepositoryName().equals(fa.getRepositoryName())) {
       	   			fileUploadItems.remove(i);
       	   			nameErrors.addProblem(new FilesErrors.Problem(fa.getRepositoryName(), 
       	  					fui.getOriginalFilename(), FilesErrors.Problem.PROBLEM_FILE_EXISTS, new TitleException(fui.getOriginalFilename())));
       			} else 	++i;      				
    		} catch (TitleException te) {
    			fileUploadItems.remove(i);
    			nameErrors.addProblem(new FilesErrors.Problem(fui.getRepositoryName(), 
       	  					fui.getOriginalFilename(), FilesErrors.Problem.PROBLEM_FILE_EXISTS, te));
    		}
    	}
   		
    	FilesErrors filterErrors = getFileModule().filterFiles(binder, fileUploadItems);
    	filterErrors.getProblems().addAll(nameErrors.getProblems());
    	return filterErrors;
    }

    protected FilesErrors modifyEntry_processFiles(Binder binder, 
    		Entry entry, List fileUploadItems, FilesErrors filesErrors, Map ctx) {
    	return getFileModule().writeFiles(binder, entry, fileUploadItems, filesErrors);
    }
    protected void modifyEntry_removeAttachments(Binder binder, Entry entry, 
    		Collection deleteAttachments, List<FileAttachment> filesToDeindex,
    		List<FileAttachment> filesToReindex, Map ctx) {
       	removeAttachments(binder, entry, deleteAttachments, filesToDeindex, filesToReindex);
       	
    }
    protected void modifyEntry_indexRemoveFiles(Binder binder, Entry entry, Collection<FileAttachment> filesToDeindex, Map ctx) {
    	removeFilesIndex(entry, filesToDeindex);
    }
   
    protected Map modifyEntry_toEntryData(Entry entry, InputDataAccessor inputData, Map fileItems, Map ctx) {
        //Call the definition processor to get the entry data to be stored
        Definition def = entry.getEntryDef();
        if (def != null) {
            return getDefinitionModule().getEntryData(def.getDefinition(), inputData, fileItems);
        } else {
           	Map entryDataAll = new HashMap();
	        entryDataAll.put(ObjectKeys.DEFINITION_ENTRY_DATA,  new HashMap());
	        entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA,  new ArrayList());
	        return entryDataAll;
        }
    }
    //inside write transaction
    protected void modifyEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setModification(new HistoryStamp(user));
        entry.incrLogVersion();
        for (Iterator iter=entryData.entrySet().iterator(); iter.hasNext();) {
        	Map.Entry mEntry = (Map.Entry)iter.next();
        	//need to generate id for the event so its id can be saved in customAttr
        	if (entry.getCustomAttribute((String)mEntry.getKey()) == null) {
        			Object obj = mEntry.getValue();
        			if (obj instanceof Event)
        				getCoreDao().save(obj);
        	}
        }
        
        EntryBuilder.updateEntry(entry, entryData);
 	   takeCareOfLastModDate(entry, inputData);
 
    }
    //inside write transaction
    protected void modifyEntry_startWorkflow(Entry entry, Map ctx) {
    	if (!(entry instanceof WorkflowSupport)) return;
    	WorkflowSupport wEntry = (WorkflowSupport)entry;
    	//see if updates to entry, trigger transitions in workflow
    	if (!wEntry.getWorkflowStates().isEmpty()) getWorkflowModule().modifyWorkflowStateOnUpdate(wEntry);
     }   
    //inside write transaction
    protected void modifyEntry_postFillIn(Binder binder, Entry entry, InputDataAccessor inputData, 
    		Map entryData, Map<FileAttachment,String> fileRenamesTo, Map ctx) {
    	//create history - using timestamp and version from fillIn
  		if (entry.isTop() && binder.isUniqueTitles()) getCoreDao().updateTitle(binder, entry, (String)ctx.get(ObjectKeys.FIELD_ENTITY_NORMALIZED_TITLE), entry.getNormalTitle());		
    	processChangeLog(entry, ChangeLog.MODIFYENTRY);
 
    	if(fileRenamesTo != null)
	    	for(FileAttachment fa : fileRenamesTo.keySet()) {
	    		String toName = fileRenamesTo.get(fa);
	    		getFileModule().renameFile(binder, entry, fa, toName);
	    	}
    }
    
    protected void modifyEntry_indexAdd(Binder binder, Entry entry, 
    		InputDataAccessor inputData, List fileUploadItems, 
    		Collection<FileAttachment> filesToIndex, Map ctx) {
    	indexEntry(binder, entry, fileUploadItems, filesToIndex, false);
    }

    protected void modifyEntry_done(Binder binder, Entry entry, InputDataAccessor inputData, Map ctx) {
    }
    protected void takeCareOfLastModDate(Entry entry, InputDataAccessor inputData) {
 	   Date lastModDate = (Date) inputData.getSingleObject("_lastModifiedDate");
 	   if(lastModDate != null) {
 		   // We have a caller-supplied last-modified date.
 	        User user = RequestContextHolder.getRequestContext().getUser();
 	        entry.setModification(new HistoryStamp(user, lastModDate));
 	   }
    }
    //***********************************************************************************************************   
    public void deleteEntry(Binder parentBinder, Entry entry) {
    	SimpleProfiler sp = new SimpleProfiler(false);
    	Map ctx = deleteEntry_setCtx(entry, null);
    	sp.reset("deleteEntry_preDelete").begin();
        deleteEntry_preDelete(parentBinder, entry, ctx);
        sp.end().print();
        
        sp.reset("deleteEntry_workflow").begin();
        deleteEntry_workflow(parentBinder, entry, ctx);
        sp.end().print();
        
        sp.reset("deleteEntry_processFiles").begin();
        deleteEntry_processFiles(parentBinder, entry, ctx);
        sp.end().print();
         
        sp.reset("deleteEntry_delete").begin();
        deleteEntry_delete(parentBinder, entry, ctx);
        sp.end().print();
        
        sp.reset("deleteEntry_postDelete").begin();
        deleteEntry_postDelete(parentBinder, entry, ctx);
        sp.end().print();
        
        sp.reset("deleteEntry_indexDel").begin();
        deleteEntry_indexDel(parentBinder, entry, ctx);
        sp.end().print();
    }
    protected Map deleteEntry_setCtx(Entry entry, Map ctx) {
    	return ctx;
    }
    protected void deleteEntry_preDelete(Binder parentBinder, Entry entry, Map ctx) {
   		if (entry.isTop() && parentBinder.isUniqueTitles()) 
   			getCoreDao().updateTitle(parentBinder, entry, entry.getNormalTitle(), null);		
    	//create history - using timestamp and version from fillIn
        User user = RequestContextHolder.getRequestContext().getUser();
        entry.setModification(new HistoryStamp(user));
        entry.incrLogVersion();
        processChangeLog(entry, ChangeLog.DELETEENTRY);
    }
        
    protected void deleteEntry_workflow(Binder parentBinder, Entry entry, Map ctx) {
    	if (entry instanceof WorkflowSupport)
    		getWorkflowModule().deleteEntryWorkflow((WorkflowSupport)entry);
    }
    
    protected void deleteEntry_processFiles(Binder parentBinder, Entry entry, Map ctx) {
    	//attachment meta-data not deleted.  Done in optimized delete entry
    	getFileModule().deleteFiles(parentBinder, entry, null);
    }
    
    protected void deleteEntry_delete(Binder parentBinder, Entry entry, Map ctx) {
    	//use the optimized deleteEntry or hibernate deletes each collection entry one at a time
    	getCoreDao().delete(entry);   
    }
    protected void deleteEntry_postDelete(Binder parentBinder, Entry entry, Map ctx) {
   }

    protected void deleteEntry_indexDel(Binder parentBinder, Entry entry, Map ctx) {
        // Delete the document that's currently in the index.
    	// Since all matches will be deleted, this will also delete the attachments
        IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
   }
    //***********************************************************************************************************
    public void moveEntry(Binder binder, Entry entry, Binder destination) {
		throw new NotSupportedException(
				NLT.get("errorcode.notsupported.moveEntry", new String[]{entry.getTitle()}));
    }
    
    //***********************************************************************************************************
    /*
     * classes must provide code to delete files belonging to entries
     */
    protected abstract Object deleteBinder_processFiles(Binder binder, Object ctx);
    /*
     * classes must provide code to delete entries in the binder
     */   
    protected abstract Object deleteBinder_delete(Binder binder, Object ctx);
    protected Object deleteBinder_indexDel(Binder binder, Object ctx) {
        // Delete the document that's currently in the index.
    	// Since all matches will be deleted, this will also delete the attachments
    	indexDeleteEntries(binder);
    	super.deleteBinder_indexDel(binder, ctx);
       	return ctx;
    }
	    
    //***********************************************************************************************************
    public void indexBinder(Binder binder, boolean includeEntries) {
    	if (includeEntries == true) indexEntries(binder);
    	else  indexBinder(binder, null, null, false);

    }
    protected void indexDeleteEntries(Binder binder) {
		// Since all matches will be deleted, this will also delete the attachments 
		IndexSynchronizationManager.deleteDocuments(new Term(EntityIndexUtils.BINDER_ID_FIELD, binder.getId().toString()));
   	
    }
    //***********************************************************************************************************
    public void modifyWorkflowState(Binder binder, Entry entry, Long tokenId, String toState) {

 		if (!(entry instanceof WorkflowSupport)) return;
 		WorkflowSupport wEntry = (WorkflowSupport)entry;
		//Find the workflowState
		WorkflowState ws = wEntry.getWorkflowState(tokenId);
 		if (ws != null) {
 	   		entry.incrLogVersion();
 	        getWorkflowModule().modifyWorkflowState(wEntry, ws, toState);
			processChangeLog(entry, ChangeLog.MODIFYWORKFLOWSTATE);
	     	// Do NOT use reindexEntry(entry) since it reindexes attached
			// files as well. We want workflow state change to be lightweight
			// and reindexing all attachments will be unacceptably costly.
			// TODO (Roy, I believe this was your design idea, so please 
			// verify that this strategy will indeed work). 

			indexEntry(entry.getParentBinder(), entry, new ArrayList(), null, false);
		}
    }
 
    public void setWorkflowResponse(Binder binder, Entry entry, Long stateId, InputDataAccessor inputData)  {
		if (!(entry instanceof WorkflowSupport)) return;
 		WorkflowSupport wEntry = (WorkflowSupport)entry;
        User user = RequestContextHolder.getRequestContext().getUser();

 		WorkflowState ws = wEntry.getWorkflowState(stateId);
		Definition def = ws.getDefinition();
		boolean changes = false;
		//TODO: what access check belongs here??
		Map questions = WorkflowUtils.getQuestions(def, ws.getState());
		for (Iterator iter=questions.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();
			String question = (String)me.getKey();
			if (!inputData.exists(question)) continue;
			String response = inputData.getSingleValue(question);
			Map qData = (Map)me.getValue();
			Map rData = (Map)qData.get(WebKeys.WORKFLOW_QUESTION_RESPONSES);
			if (!rData.containsKey(response)) {
				throw new IllegalArgumentException("Illegal workflow response: " + response);
			}
			//now see if response to this question from this user exists
			Set responses = wEntry.getWorkflowResponses();
			boolean found=false;
			WorkflowResponse wr=null;
			for (Iterator iter2=responses.iterator(); iter2.hasNext();) {
				wr = (WorkflowResponse)iter2.next();
				if (def.getId().equals(wr.getDefinitionId()) && 
						question.equals(wr.getName()) &&
						user.getId().equals(wr.getResponderId())) {
					found = true;
					break;
				}			
			}
			if (found) {
				if (!response.equals(wr.getResponse())) {
					//if no changes have been made update timestamp
					if (!changes) {
						entry.setModification(new HistoryStamp(user));
						entry.incrLogVersion();	
						changes = true;
					}
					wr.setResponse(response);
					wr.setResponseDate(entry.getModification().getDate());
				}
			} else {
				//if no changes have been made update timestamp
				if (!changes) {
					entry.setModification(new HistoryStamp(user));
					entry.incrLogVersion();				
					changes = true;
				}
				wr = new WorkflowResponse();
				wr.setResponderId(user.getId());
				wr.setResponseDate(entry.getModification().getDate());
				wr.setDefinitionId(def.getId());
				wr.setName(question);
				wr.setResponse(response);
				wr.setOwner(entry);
				getCoreDao().save(wr);
				wEntry.addWorkflowResponse(wr);
			}
		}
		if (changes) {
			getWorkflowModule().modifyWorkflowStateOnResponse(wEntry);
			processChangeLog(entry, ChangeLog.ADDWORKFLOWRESPONSE);
			// Do NOT use reindexEntry(entry) since it reindexes attached
			// files as well. We want workflow state change to be lightweight
			// and reindexing all attachments will be unacceptably costly.
			// TODO (Roy, I believe this was your design idea, so please 
			// verify that this strategy will indeed work). 

			indexEntry(entry.getParentBinder(), entry, new ArrayList(), null, false);
		}
    	
    }

    //***********************************************************************************************************
    /**
     * Index binder and its entries
     */
    public void indexEntries(Binder binder) {
    	
     	// this is just here until we get our indexes in sync with
    	// the db.  (Early in development, they're not...
   		//iterate through results
    	indexEntries_preIndex(binder);
   		//flush any changes so any exiting changes don't get lost on the evict
   		getCoreDao().flush();
   		//index just the binder first
   		indexBinder(binder, null, null, false);
   		SFQuery query = indexEntries_getQuery(binder);
	   	
	   	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
       	try {       
  			List batch = new ArrayList();
  			List docs = new ArrayList();
  			int total=0;
      		while (query.hasNext()) {
       			int count=0;
       			batch.clear();
       			docs.clear();
       			// get 1000 entries, then build collections by hand 
       			//for performance
       			while (query.hasNext() && (count < 1000)) {
       				Object obj = query.next();
       				if (obj instanceof Object[])
       					obj = ((Object [])obj)[0];
       				batch.add(obj);
       				++count;
       			}
       			total += count;
       			//have 1000 entries, manually load their collections
       			indexEntries_load(binder, batch);
       			logger.info("Indexing at " + total + "(" + binder.getId().toString() + ")");
       			Map tags = indexEntries_loadTags(binder, batch);
       			for (int i=0; i<batch.size(); ++i) {
       				Entry entry = (Entry)batch.get(i);
       				List entryTags = (List)tags.get(entry.getEntityIdentifier());
       				// 	Create an index document from the entry object.
       				org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromEntry(binder, entry, entryTags);
      				docs.add(indexDoc);
            		if (logger.isDebugEnabled())
            			logger.info("Indexing entry: " + entry.toString() + ": " + indexDoc.toString());
       		        //Create separate documents one for each attached file and index them.
       				List atts = entry.getFileAttachments();
       		        for (int j = 0; j < atts.size(); j++) {
       		        	FileAttachment fa = (FileAttachment)atts.get(j);
       		        	try {
       		        		indexDoc = buildIndexDocumentFromEntryFile(binder, entry, fa, null, entryTags);
      		        		// Register the index document for indexing.
       		        		docs.add(indexDoc);
      		        	} catch (Exception ex) {
      		        		//log error but continue
      		        		logger.error("Error indexing file for entry " + entry.getId() + " attachment " + fa, ex);
       		        	}
       		        }
      				getCoreDao().evict(entry);
      				indexEntries_postIndex(binder, entry);
       			}
	            
       			// Delete the document that's currently in the index.
 // turn back on later when don't delete everything
//       				luceneSession.deleteDocument(entry.getIndexDocumentUid());
	            
       			// Register the index document for indexing.
       			luceneSession.addDocuments(docs);
       			logger.info("Indexing done at " + total + "("+ binder.getId().toString() + ")");
       		
        	}
        	
        } finally {
        	query.close();
        	luceneSession.close();
        }
 
    }
    protected void indexEntries_preIndex(Binder binder) {
         //need to use session directly, cause index_entries does.
    	//make sure this gets out first
       	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        try {	            
	        logger.info("Indexing (" + binder.getId().toString() + ") ");
	        
	        // Delete the document that's currently in the index.
	        luceneSession.deleteDocuments(new Term(EntityIndexUtils.BINDER_ID_FIELD, binder.getId().toString()));

	            
        } finally {
	        luceneSession.close();
	    }
 
    }
    
   	protected abstract SFQuery indexEntries_getQuery(Binder binder);
   	protected void indexEntries_postIndex(Binder binder, Entry entry) {
   	}
   	protected void indexEntries_load(Binder binder, List entries)  {
   		// bulkd load any collections that neeed to be indexed
   		getCoreDao().bulkLoadCollections(entries);
   	}
	protected Map indexEntries_loadTags(Binder binder, List<Entry> entries) {
		List<EntityIdentifier> ids = new ArrayList();
		for (Entry e: entries) {
			ids.add(e.getEntityIdentifier());
		}
		return getCoreDao().loadAllTagsByEntity(ids);
	}
 
    //***********************************************************************************************************
   	public void indexEntries(Collection entries) {
   		for (Iterator iter=entries.iterator(); iter.hasNext();) {
   			Entry entry = (Entry)iter.next();
   			indexEntry(entry);
   		}
   	}
   	public void moveFiles(Binder binder, Collection entries, Binder destination) {
   		for (Iterator iter=entries.iterator(); iter.hasNext();) {
   			Entry entry = (Entry)iter.next();
   			moveFiles(binder, entry, destination);
   		}
   	}
   	
    protected void moveFiles(Binder binder, Entry entry, Binder destination) {
    	List atts = entry.getFileAttachments();
    	for(int i = 0; i < atts.size(); i++) {
    		FileAttachment fa = (FileAttachment) atts.get(i);
    		getFileModule().moveFile(binder, entry, fa, destination);
    	}
    }
   	
    //***********************************************************************************************************
    public Map getBinderEntries(Binder binder, String[] entryTypes, Map options) {
        //search engine will only return entries you have access to
         //validate entry count
    	//do actual search index query
        Hits hits = getBinderEntries_doSearch(binder, entryTypes, options);
        //iterate through results
        ArrayList childEntries = getBinderEntries_entriesArray(hits);
       	Map model = new HashMap();
        model.put(ObjectKeys.BINDER, binder);      
        model.put(ObjectKeys.SEARCH_ENTRIES, childEntries);
        model.put(ObjectKeys.SEARCH_COUNT_TOTAL, new Integer(hits.getTotalHits()));
        //Total number of results found
        model.put(ObjectKeys.TOTAL_SEARCH_COUNT, new Integer(hits.getTotalHits()));
        //Total number of results returned
        model.put(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, new Integer(hits.length()));
        return model;
   }
 
    public ArrayList getBinderEntries_entriesArray(Hits hits) {
        //Iterate through the search results and build the entries array
        ArrayList childEntries = new ArrayList(hits.length());
        try {
            int count=0;
            Field fld;
 	        while (count < hits.length()) {
	            HashMap ent = new HashMap();
	            Document doc = hits.doc(count);
	            //enumerate thru all the returned fields, and add to the map object
	            Enumeration flds = doc.fields();
	            while (flds.hasMoreElements()) {
	            	fld = (Field)flds.nextElement();
	            	//TODO This hack needs to go.
	            	if (fld.name().toLowerCase().indexOf("date") > 0) {
	            		try {
	            			ent.put(fld.name(),DateTools.stringToDate(fld.stringValue()));
	            		} catch (ParseException e) {ent.put(fld.name(),new Date());
	            		}
	            	} else if (!ent.containsKey(fld.name())) {
	            		ent.put(fld.name(), fld.stringValue());
	            	} else {
	            		Object obj = ent.get(fld.name());
	            		SearchFieldResult val;
	            		if (obj instanceof String) {
	            			val = new SearchFieldResult();
	            			//replace
	            			ent.put(fld.name(), val);
	            			val.addValue((String)obj);
	            		} else {
	            			val = (SearchFieldResult)obj;
	            		}
	            		val.addValue(fld.stringValue());
	            	} 
	            }
	            childEntries.add(ent);
	            ++count;
	            
	        }
        } finally {
        }
        List users = loadEntryHistoryLuc(childEntries);
        // walk the entries, and stuff in the user object.
        for (int i = 0; i < childEntries.size(); i++) {
        	HashMap child = (HashMap)childEntries.get(i);
        	if (child.get(getEntryPrincipalField()) != null) {
        		child.put(WebKeys.PRINCIPAL, getPrincipal(users,child.get(getEntryPrincipalField()).toString()));
        	}        	
        }
        return childEntries;
   }
 
    protected abstract String getEntryPrincipalField();
    

    protected int getBinderEntries_maxEntries(int maxChildEntries) {
        if (maxChildEntries == 0 || maxChildEntries == Integer.MAX_VALUE) maxChildEntries = DEFAULT_MAX_CHILD_ENTRIES;
        return maxChildEntries;
    }
     
    protected Hits getBinderEntries_doSearch(Binder binder, String [] entryTypes, 
    		Map options) {
    	int maxResults = 0;
    	if (options.containsKey(ObjectKeys.SEARCH_MAX_HITS)) 
    		maxResults = (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS);
    	maxResults = getBinderEntries_maxEntries(maxResults); 
        
    	int searchOffset = 0;
    	if (options.containsKey(ObjectKeys.SEARCH_OFFSET)) 
    		searchOffset = (Integer) options.get(ObjectKeys.SEARCH_OFFSET);
        
        org.dom4j.Document searchFilter = null;
    	if (options.containsKey(ObjectKeys.SEARCH_SEARCH_FILTER)) 
    		searchFilter = (org.dom4j.Document) options.get(ObjectKeys.SEARCH_SEARCH_FILTER);

       	Hits hits = null;
       	// Build the query
    	if (searchFilter == null) {
    		//If there is no search filter, assume the caller wants everything
    		searchFilter = DocumentHelper.createDocument();
    		Element rootElement = searchFilter.addElement(FilterHelper.FilterRootName);
        	rootElement.addElement(FilterHelper.FilterTerms);
    		Element filterTerms = rootElement.addElement(FilterHelper.FilterTerms);
    	}
       	org.dom4j.Document queryTree = getBinderEntries_getSearchDocument(binder, entryTypes, searchFilter);

       	//See if there is a title field search request
       	if (options.containsKey(ObjectKeys.SEARCH_TITLE)) {
        	Element rootElement = queryTree.getRootElement();
        	if (rootElement != null) {
        		Element boolElement = rootElement.element(QueryBuilder.AND_ELEMENT);
        		if (boolElement != null) {
        			Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
        			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.TITLE_FIELD);
	    	    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    	    	child.setText((String) options.get(ObjectKeys.SEARCH_TITLE));
        		}
        	}
       	}

       	//See if there is an end date
       	if (options.containsKey(ObjectKeys.SEARCH_END_DATE)) {
        	Element rootElement = queryTree.getRootElement();
        	if (rootElement != null) {
        		Element boolElement = rootElement.element(QueryBuilder.AND_ELEMENT);
        		if (boolElement != null) {
        			Element range = boolElement.addElement(QueryBuilder.RANGE_ELEMENT);
        			range.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.CREATION_DAY_FIELD);
        			range.addAttribute(QueryBuilder.INCLUSIVE_ATTRIBUTE, "true");
        			Element start = range.addElement(QueryBuilder.RANGE_START);
        	        Calendar cal = Calendar.getInstance();
        	        cal.set(1970, 0, 1);
        	        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        	        String s = formatter.format(cal.getTime());
        			start.addText((String) s);
        			Element finish = range.addElement(QueryBuilder.RANGE_FINISH);
        			finish.addText((String) options.get(ObjectKeys.SEARCH_END_DATE));
        		}
        	}
       	}

       	//See if there is a year/month
       	if (options.containsKey(ObjectKeys.SEARCH_YEAR_MONTH)) {
        	Element rootElement = queryTree.getRootElement();
        	if (rootElement != null) {
        		Element boolElement = rootElement.element(QueryBuilder.AND_ELEMENT);
        		if (boolElement != null) {
        			Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
        			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.CREATION_YEAR_MONTH_FIELD);
	    	    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    	    	child.setText((String) options.get(ObjectKeys.SEARCH_YEAR_MONTH));
        		}
        	}
       	}
       	//See if there is a tag
       	if (options.containsKey(ObjectKeys.SEARCH_COMMUNITY_TAG)) {
        	Element rootElement = queryTree.getRootElement();
        	if (rootElement != null) {
        		Element boolElement = rootElement.element(QueryBuilder.AND_ELEMENT);
        		if (boolElement != null) {
        			Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
        			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, BasicIndexUtils.TAG_FIELD);
	    	    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    	    	child.setText((String) options.get(ObjectKeys.SEARCH_COMMUNITY_TAG));
        		}
        	}
       	}
       	if (options.containsKey(ObjectKeys.SEARCH_PERSONAL_TAG)) {
        	Element rootElement = queryTree.getRootElement();
        	if (rootElement != null) {
        		Element boolElement = rootElement.element(QueryBuilder.AND_ELEMENT);
        		if (boolElement != null) {
        			Element field = boolElement.addElement(QueryBuilder.PERSONALTAGS_ELEMENT);
	    	    	Element child = field.addElement(QueryBuilder.TAG_ELEMENT);
        			child.addAttribute(QueryBuilder.TAG_NAME_ATTRIBUTE, (String)options.get(ObjectKeys.SEARCH_PERSONAL_TAG));
        		}
        	}
       	}
       	if (options.containsKey(ObjectKeys.FOLDER_ENTRY_TO_BE_SHOWN)) {
           	//Forget all of the above and just get the desired entry
       		queryTree = getBinderEntries_getSearchDocument(binder, entryTypes, null);
       		Element rootElement = queryTree.getRootElement();
       		if (rootElement != null) {
        		Element boolElement = rootElement.element(QueryBuilder.AND_ELEMENT);
        		if (boolElement != null) {
        			Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
        			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.DOCID_FIELD);
	    	    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    	    	child.setText((String) options.get(ObjectKeys.FOLDER_ENTRY_TO_BE_SHOWN));
        		}
        	}
       	}
       	//queryTree.asXML();
       	
       	//Create the Lucene query
    	QueryBuilder qb = new QueryBuilder(getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser()));
    	SearchObject so = qb.buildQuery(queryTree);
    	
    	//Set the sort order
    	SortField[] fields = BinderHelper.getBinderEntries_getSortFields(options); 
    	so.setSortBy(fields);
    	Query soQuery = so.getQuery();    //Get the query into a variable to avoid doing this very slow operation twice
    	
    	if(logger.isInfoEnabled()) {
    		logger.info("Query is: " + queryTree.asXML());
    		logger.info("Query is: " + soQuery.toString());
    	}
    	
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        
        try {
	        hits = luceneSession.search(soQuery, so.getSortBy(), searchOffset, maxResults);
        }
        finally {
            luceneSession.close();
        }
    	
        return hits;
     
    }
    protected org.dom4j.Document getBinderEntries_getSearchDocument(Binder binder, 
    		String [] entryTypes, org.dom4j.Document searchFilter) {
    	if (searchFilter == null) {
    		searchFilter = DocumentHelper.createDocument();
    		Element rootElement = searchFilter.addElement(FilterHelper.FilterRootName);
        	rootElement.addElement(FilterHelper.FilterTerms);
    	}
    	org.dom4j.Document qTree = FilterHelper.convertSearchFilterToSearchBoolean(searchFilter);
    	Element rootElement = qTree.getRootElement();
    	if (rootElement == null) return qTree;
    	Element boolElement = rootElement.element(QueryBuilder.AND_ELEMENT);
    	if (boolElement == null) return qTree;
    	//boolElement.addElement(QueryBuilder.USERACL_ELEMENT);
     	
    	//Look only for binderId=binder and doctype = entry (not attachement)
    	if (binder != null) {
    		Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
        	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.BINDER_ID_FIELD);
        	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
        	child.setText(binder.getId().toString());
        	
        	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
        	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
        	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
        	child.setText(BasicIndexUtils.DOC_TYPE_ENTRY);
    	}
    	return qTree;
    }

    //***********************************************************************************************************
    public Entry getEntry(Binder parentBinder, Long entryId) {
    	//get the entry
    	Entry entry = entry_load(parentBinder, entryId);
        //Initialize users
        loadEntryHistory(entry);
        return entry;
    }
          
    
    //***********************************************************************************************************
    /*
     * Load all principals assocated with an entry.  
     * This is a performance optimization for display.
     */
    protected abstract Entry entry_load(Binder parentBinder, Long entryId);
        
    protected void loadEntryHistory(Entry entry) {
        Set ids = new HashSet();
        if (entry.getCreation() != null)
            ids.add(entry.getCreation().getPrincipal().getId());
        if (entry.getModification() != null)
            ids.add(entry.getModification().getPrincipal().getId());
        getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId(), false);
     } 

    protected List loadEntryHistoryLuc(List pList) {
        Set ids = new HashSet();
        Iterator iter=pList.iterator();
        HashMap entry;
        while (iter.hasNext()) {
            entry = (HashMap)iter.next();
            if (entry.get(EntityIndexUtils.CREATORID_FIELD) != null)
            	try {ids.add(new Long(entry.get(EntityIndexUtils.CREATORID_FIELD).toString()));
        	    } catch (Exception ex) {}
            if (entry.get(EntityIndexUtils.MODIFICATIONID_FIELD) != null) 
        		try {ids.add(new Long(entry.get(EntityIndexUtils.MODIFICATIONID_FIELD).toString()));
        		} catch (Exception ex) {}
        }
        return getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId(), false);
     }   

    protected void loadEntryHistory(List pList) {
        Set ids = new HashSet();
        Iterator iter=pList.iterator();
        Entry entry;
        while (iter.hasNext()) {
            entry = (Entry)iter.next();
            if (entry.getCreation() != null)
                ids.add(entry.getCreation().getPrincipal().getId());
            if (entry.getModification() != null)
                ids.add(entry.getModification().getPrincipal().getId());
        }
        getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId(),false);
     }     
    

    public void indexEntry(Entry entry) {
    	indexEntry(entry.getParentBinder(), entry, null, null, false);
    }
    /**
     * Index entry and optionally its attached files.
     * 
     * @param biner
     * @param entry
     * @param fileUploadItems uploaded files or <code>null</code>. 
     * At minimum, those files in the list must be indexed.  
     * @param filesToIndex a list of FileAttachments or <code>null</code>. 
     * At minimum, those files in the list must be indexed. 
     * @param newEntry
     */
    protected void indexEntry(Binder binder, Entry entry, List fileUploadItems, 
    		Collection<FileAttachment> filesToIndex, boolean newEntry) {
    	// Logically speaking, the only files we need to index are the ones
    	// that have been uploaded (fileUploadItems) and the ones explicitly
    	// specified (in the filesToIndex). In ideal world, indexing only
    	// those subset will yield better performance. However, the way 
    	// file indexing currently works is that, whenever some aspect of its 
    	// enclosing entry changes, it deletes not only the entry but also all 
    	// file attachments from the index, because each file index entry 
    	// actually duplicates the common data from the entry itself. (see 
    	// overloaded indexEntry method in this class). Therefore, we must 
    	// (re)index "all" the file attachments in the entry regardless of 
    	// whether a particular file content has changed or not. 
    	// Consequently we obtain and pass "all" the attachments to the 
    	// following method and ignore the filesToIndex list (for now).
    	
    	indexEntryWithAttachments(binder, entry, entry.getFileAttachments(), fileUploadItems, newEntry);
    }
    
    /**
     * Index entry along with its file attachments. 
     * 
     * @param binder
     * @param entry
     * @param fileAttachments list of FileAttachments that need to be (re)indexed.
     * Only those files explicitly listed in this list are indexed. 
     * @param fileUploadItems a list of uploaded files or a <code>null</code>.
     * If each FileAttachment in fileAttachments list finds a corresponding
     * uploaded file in this list, the content of the uploaded file can be
     * used for indexing. Otherwise, the file content must be obtained from
     * the repository. Note that the elements in this list do not positionally
     * correspond to the elements in fileAttachments list. 
     * @param newEntry
     */
	protected void indexEntryWithAttachments(Binder binder, Entry entry,
			List fileAttachments, List fileUploadItems, boolean newEntry) {
		List docs = new ArrayList();
		if(!newEntry) {
			// This is modification. We must first delete existing document(s) from the index.
			
			// Since all matches will be deleted, this will also delete the attachments 
	        IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
		}
		
        // Create an index document from the entry object.
		org.apache.lucene.document.Document indexDoc;
		indexDoc = buildIndexDocumentFromEntry(entry.getParentBinder(), entry, null);
       // Register the index document for indexing.
        docs.add(indexDoc);        
        
        //Create separate documents one for each attached file and index them.
        for(int i = 0; i < fileAttachments.size(); i++) {
        	FileAttachment fa = (FileAttachment) fileAttachments.get(i);
        	FileUploadItem fui = null;
        	if(fileUploadItems != null)
        		fui = findFileUploadItem(fileUploadItems, fa.getRepositoryName(), fa.getFileItem().getName());
        	try {
        		indexDoc = buildIndexDocumentFromEntryFile(binder, entry, fa, fui, null);
           		// Register the index document for indexing.
        		docs.add(indexDoc);
	        } catch (Exception ex) {
		       		//log error but continue
		       		logger.error("Error indexing file for entry " + entry.getId() + " attachment " + fa, ex);
        	}
         }
        IndexSynchronizationManager.addDocuments(docs);
	}

    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry, List tags) {
    	org.apache.lucene.document.Document indexDoc = new org.apache.lucene.document.Document();
        
    	fillInIndexDocWithCommonPartFromEntry(indexDoc, binder, entry);
    	
    	// Add document type
        BasicIndexUtils.addDocType(indexDoc, com.sitescape.team.search.BasicIndexUtils.DOC_TYPE_ENTRY);
                
        // Add the events - special indexing for calendar view
        EntityIndexUtils.addEvents(indexDoc, entry);
        
        // Add the tags for this entry
        if (tags == null) tags =  getCoreDao().loadAllTagsByEntity(entry.getEntityIdentifier());
        EntityIndexUtils.addTags(indexDoc, entry, tags);
        
        // Add the workflows
        EntityIndexUtils.addWorkflow(indexDoc, entry);
        
       
        return indexDoc;
    }
    protected org.apache.lucene.document.Document buildIndexDocumentFromEntryFile
	(Binder binder, Entry entry, FileAttachment fa, FileUploadItem fui, List tags) {
   		org.apache.lucene.document.Document indexDoc = buildIndexDocumentFromFile(binder, entry, fa, fui, tags);
        // Add the workflows - different for files
        EntityIndexUtils.addWorkflow(indexDoc, entry);
   		fillInIndexDocWithCommonPartFromEntry(indexDoc, binder, entry);
   		return indexDoc;
 
    }

    /**
     * Fill in the Lucene document object with information that is common between
     * entry doc and attachment docs. We duplicate data so that we won't have to
     * perform multiple queries or run our own filtering in multiple steps. 
     * 
     * @param indexDoc
     * @param binder
     * @param entry
     */
    protected void fillInIndexDocWithCommonPartFromEntry(org.apache.lucene.document.Document indexDoc, 
    		Binder binder, Entry entry) {
      	EntityIndexUtils.addEntryType(indexDoc, entry);       
        // Add ACL field. We only need to index ACLs for read access.
   		EntityIndexUtils.addReadAccess(indexDoc, binder, entry);
      		
        //add parent binder - this isn't added for binders because it is used
        //in delete terms for entries in a binder. 
        //
        EntityIndexUtils.addBinder(indexDoc, binder);

        fillInIndexDocWithCommonPart(indexDoc, binder, entry);
    }
	public ChangeLog processChangeLog(DefinableEntity entry, String operation) {
		if (entry instanceof Binder) return processChangeLog((Binder)entry, operation);
		ChangeLog changes = new ChangeLog(entry, operation);
		ChangeLogUtils.buildLog(changes, entry);
		getCoreDao().save(changes);
		return changes;
	}
	
}
