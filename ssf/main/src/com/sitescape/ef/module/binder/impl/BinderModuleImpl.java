
package com.sitescape.ef.module.binder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.binder.BinderProcessor;
import com.sitescape.ef.module.binder.EntryProcessor;

import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.ef.pipeline.Pipeline;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.security.function.WorkAreaFunctionMembershipManager;
/**
 * @author Janet McCann
 *
 */
public class BinderModuleImpl extends CommonDependencyInjection implements BinderModule {
	private WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager;
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

	
	private Binder loadBinder(Long binderId) {
		return getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
	}
	private EntryProcessor loadEntryProcessor(Binder binder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
		return (EntryProcessor)getProcessorManager().getProcessor(binder, EntryProcessor.PROCESSOR_KEY);
	}
	private BinderProcessor loadBinderProcessor(Binder binder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
		return (BinderProcessor)getProcessorManager().getProcessor(binder, BinderProcessor.PROCESSOR_KEY);
	}
	public Binder getBinderByName(String binderName) 
   			throws NoBinderByTheNameException, AccessControlException {
		Binder binder = getCoreDao().findBinderByName(binderName, RequestContextHolder.getRequestContext().getZoneName());
	    // Check if the user has "read" access to the binder.
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);		
		return binder;
	}
   
	public Binder getBinder(Long binderId)
			throws NoBinderByTheIdException, AccessControlException {
		Binder binder = loadBinder(binderId);
		// Check if the user has "read" access to the binder.
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);		

        return binder;        
	}
    public boolean hasBinders(Binder binder) {
    	List binders = binder.getBinders();
    	for (int i=0; i<binders.size(); ++i) {
    		Binder b = (Binder)binders.get(i);
            if (getAccessControlManager().testOperation(b, WorkAreaOperation.READ_ENTRIES)) return true;    	       		
    	}
    	return false;
    }	

    public void modifyBinder(Long binderId, final InputDataAccessor inputData) 
	throws AccessControlException, WriteFilesException {
    	modifyBinder(binderId, inputData, new HashMap());
}
    public void modifyBinder(Long binderId, InputDataAccessor inputData, 
    		Map fileItems) throws AccessControlException, WriteFilesException {
    	Binder binder = loadBinder(binderId);
    	loadBinderProcessor(binder).modifyBinder(binder, inputData, fileItems);
    }
    public void checkModifyBinderAllowed(Binder binder) {
       	loadBinderProcessor(binder).modifyBinder_accessControl(binder);		
    }
    public void deleteBinder(Long binderId) {
    	Binder binder = loadBinder(binderId);
    	loadBinderProcessor(binder).deleteBinder(binder);
    }
    public void checkDeleteBinderAllowed(Binder binder) {
    	loadBinderProcessor(binder).deleteBinder_accessControl(binder);    	    	
    }
	public Binder setConfiguration(Long binderId, boolean inheritFromParent) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS); 
		boolean oldInherit = binder.isDefinitionsInherited();
		if (inheritFromParent != oldInherit) {
			if (inheritFromParent) {
				//remove old mappings
				Map m = binder.getWorkflowAssociations();
				m.clear();
				binder.setWorkflowAssociations(m);
				List l = binder.getDefinitions();
				l.clear();
				binder.setDefinitions(l);
			} else {
				//copy parents definitions to this binder before changing setting
				binder.setWorkflowAssociations(binder.getWorkflowAssociations());
				binder.setDefinitions(binder.getDefinitions());
			}
			binder.setDefinitionsInherited(inheritFromParent);
		}
		return binder;
		
	}
    public Binder setConfiguration(Long binderId, List definitionIds, Map workflowAssociations) 
	throws AccessControlException {
		Binder binder = setConfiguration(binderId, definitionIds);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_WORKFLOW_DEFINITIONS);    	
		binder.setWorkflowAssociations(workflowAssociations);
		binder.setDefinitionsInherited(false);
		return binder;
	}
	public Binder setConfiguration(Long binderId, List definitionIds) throws AccessControlException {
		Binder binder = loadBinder(binderId);
		String companyId = binder.getZoneName();
		List definitions = new ArrayList(); 
		Definition def;
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
		//	Build up new set - domain object will handle associations
		if (definitionIds != null) {
			for (int i=0; i<definitionIds.size(); ++i) {
				def = getCoreDao().loadDefinition((String)definitionIds.get(i), companyId);
				//	TODO:	getAccessControlManager().checkAcl(def, AccessType.READ);
				definitions.add(def);
			}
		}
	
		binder.setDefinitions(definitions);
		binder.setDefinitionsInherited(false);
		
		return binder;
	}
	/**
	 * Get tags owned by this binder
	 */
	public List getTags(Long binderId) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
		List tags = new ArrayList<Tag>();		
		getCoreDao().loadTagsByOwner(binder.getEntityIdentifier());
		return tags;		
	}
	/**
	 * Modify tag owned by this binder
	 * @see com.sitescape.ef.module.binder.BinderModule#modifyTag(java.lang.Long, java.lang.String, java.util.Map)
	 */
	public void modifyTag(Long binderId, String tagId, Map updates) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
	   	Tag tag = coreDao.loadTagByOwner(tagId, binder.getEntityIdentifier());
	   	ObjectBuilder.updateObject(tag, updates);
	}
	/**
	 * Add a new tag, owned by this binder
	 */
	public void addTag(Long binderId, Map updates) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
	   	Tag tag = new Tag();
	   	tag.setOwnerIdentifier(binder.getEntityIdentifier());
	  	ObjectBuilder.updateObject(tag, updates);
	  	coreDao.save(tag);   	
	}
	/**
	 * Delete a tag owned by this binder
	 */
	public void deleteTag(Long binderId, String tagId) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
	   	Tag tag = coreDao.loadTagByOwner(tagId, binder.getEntityIdentifier());
	   	getCoreDao().delete(tag);
	}
	/**
	 * Get tags owned by the entry
	 * @param binderId
	 * @param entryId
	 * @return
	 */
	public List getTags(Long binderId, Long entryId) {
		Binder binder = loadBinder(binderId);
		Entry entry = loadEntryProcessor(binder).getEntry(binder, entryId, null);
		List tags = new ArrayList<Tag>();
		getCoreDao().loadTagsByOwner(entry.getEntityIdentifier());
		return tags;		
	}
	/**
	 * Modify a tag owned by this entry
	 * @param binderId
	 * @param entryId
	 * @param tagId
	 * @param updates
	 */
	public void modifyTag(Long binderId, Long entryId, String tagId, Map updates) {
		Binder binder = loadBinder(binderId);
		Entry entry = loadEntryProcessor(binder).getEntry(binder, entryId, null);
	   	Tag tag = coreDao.loadTagByOwner(tagId, entry.getEntityIdentifier());
	   	ObjectBuilder.updateObject(tag, updates);
	}
	/**
	 * Add a tag owned by this entry
	 * @param binderId
	 * @param entryId
	 * @param updates
	 */
	public void addTag(Long binderId, Long entryId, Map updates) {
		Binder binder = loadBinder(binderId);
		Entry entry = loadEntryProcessor(binder).getEntry(binder, entryId, null);
	   	Tag tag = new Tag();
	   	tag.setOwnerIdentifier(entry.getEntityIdentifier());
	  	ObjectBuilder.updateObject(tag, updates);
	  	coreDao.save(tag);   	
	}
	/**
	 * Delete a tag owned by this entry
	 * @param binderId
	 * @param entryId
	 * @param tagId
	 */
	public void deleteTag(Long binderId, Long entryId, String tagId) {
		Binder binder = loadBinder(binderId);
		Entry entry = loadEntryProcessor(binder).getEntry(binder, entryId, null);
	   	Tag tag = coreDao.loadTagByOwner(tagId, entry.getEntityIdentifier());
	   	getCoreDao().delete(tag);
	}
  

}
