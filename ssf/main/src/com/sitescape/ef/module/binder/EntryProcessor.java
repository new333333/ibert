package com.sitescape.ef.module.binder;

import java.util.Collection;
import java.util.Map;

import org.dom4j.Document;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.security.AccessControlException;

/**
 * <code>EntryProcessor</code> is used by model processors for binders that
 * support AclControlledEntries.
  * 
 * @author Jong Kim
 */
public interface EntryProcessor extends BinderProcessor {
 
    public Long addBinder(Binder binder, Definition def, Class clazz, InputDataAccessor inputData, Map fileItems) 
	throws AccessControlException, WriteFilesException;
    public void addBinder_accessControl(Binder binder) throws AccessControlException;
  	public void deleteBinder(Binder binder) throws AccessControlException;
    public void deleteBinder_accessControl(Binder binder) throws AccessControlException;
	public Map getBinderEntries(Binder binder, String[] entryTypes, int maxNumEntries) throws AccessControlException;
	public Map getBinderEntries(Binder binder, String[] entryTypes, int maxNumEntries, Document searchFilter) throws AccessControlException;
	public void indexEntries(Binder binder);
    public Long modifyBinder(Binder binder, InputDataAccessor inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
    public void modifyBinder_accessControl(Binder binder) throws AccessControlException;
	
    
    public Long addEntry(Binder binder, Definition def, Class clazz, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException;
    public void addEntry_accessControl(Binder binder) throws AccessControlException;
    public void deleteEntry(Binder binder, Long entryId) throws AccessControlException;
    public void deleteEntry_accessControl(Binder binder, Entry entry) throws AccessControlException;
    public Entry getEntry(Binder binder, Long entryId, EntityIdentifier.EntityType type) throws AccessControlException;
    public Long modifyEntry(Binder binder, Long entryId, InputDataAccessor inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
    public void modifyEntry_accessControl(Binder binder, Entry entry) throws AccessControlException;
    public void modifyWorkflowState(Binder binder, Long entryId, Long tokenId, String toState) 
		throws AccessControlException;
	public void reindexEntry(Entry entry); 
  	public void reindexEntries(Collection entries);


}
