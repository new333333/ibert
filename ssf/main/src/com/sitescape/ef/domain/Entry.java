package com.sitescape.ef.domain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.util.CollectionUtil;
import java.util.Collection;


/**
 * @author Jong Kim
 *
 */
public abstract class Entry extends PersistentLongIdTimestampObject 
	implements AttachmentSupport, MultipleWorkflowSupport {

    private String title="";
    private Description description;
    protected boolean attachmentsParsed = false;
    protected Set attachments;
    protected Map customAttributes;
    protected Definition entryDef;
    protected boolean eventsParsed = false;
    protected Set workflowStates;   
    protected HistoryStamp workflowChange;
    protected Binder parentBinder;
    protected Set events;
    
    public Entry() {
    }
    /**
     * @hibernate.component prefix="description_"
     */
    public Description getDescription() {
        return this.description;
    }
    public void setDescription(Description description) {
        if (this.description != null)
        	// try to avoid unecessary updates
        	if (this.description.equals(description)) return;
    	this.description = description; 
    }
  
    public void setDescription(String descriptionText) {
		Description tmp = new Description(descriptionText);
    	if (description != null) {
    		if (description.equals(tmp)) return;
    	}
        this.description = tmp; 
    }
    public Set getWorkflowStates() {
   	 	if (workflowStates == null) workflowStates = new HashSet();
   	 	return workflowStates;  
     }
     public void setWorkflowStates(Set workflowStates) {
    	 //Since ids are assigned on WorkflowState, don't need to do anything
    	 //special to reduce updates.
    	 this.workflowStates = workflowStates;
     }
     public WorkflowState getWorkflowState(Long id) {
     	//Make sure initialized
     	getWorkflowStates();
		WorkflowState ws=null;
		for (Iterator iter=workflowStates.iterator(); iter.hasNext();) {
			ws = (WorkflowState)iter.next();
			if (ws.getId().equals(id)) return ws;
		}
		return null;
     }
     public void addWorkflowState(WorkflowState state) {
     	if (state == null) return;
    	//Make sure initialized
    	getWorkflowStates();
        workflowStates.add(state);
 	   	state.setOwner(this);
    }
    public void removeWorkflowState(WorkflowState state) {
     	if (state == null) return;
    	//Make sure initialized
    	getWorkflowStates();
        workflowStates.remove(state);
 	   	state.setOwner((AnyOwner)null);
    }
    /**
     * @hibernate.component class="com.sitescape.ef.domain.HistoryStamp" prefix="wrk_" 
     */
    public HistoryStamp getWorkflowChange() {
        return this.workflowChange;
    }
    public void setWorkflowChange(HistoryStamp workflowChange) {
        this.workflowChange = workflowChange;
    }
    /**
     * @hibernate.property length="1024"
     * @return
     */
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    /** 
     * @hibernate.many-to-one access="field" class="com.sitescape.ef.domain.Definition"
     * @hibernate.column name="entryDef" sql-type="char(32)"
     * @return
     */
    public Definition getEntryDef() {
    	if (entryDef != null) return entryDef;
    	return getParentBinder().getDefaultEntryDef();
    }
    
    public void setEntryDef(Definition entryDef) {
        this.entryDef = entryDef;
    }
    /**
     * @hibernate.many-to-one
     * @return
     */
    public Binder getParentBinder() {
   	 return parentBinder;
    }
    public void setParentBinder(Binder parentBinder) {
   	 this.parentBinder = parentBinder;
    }
    /**
     * Events are only accessed through custom attributes
     * Remove an event.  Event object will be deleted from
     * database unless it is added somewhere else.
     * @param event
     */
    protected void removeEvent(Event event) {
       	if (event == null) return;
        if (events == null) events = new HashSet();
        events.remove(event);
        event.setOwner((AnyOwner)null);           	
    }
    /**
     * Add an event
     * @param event
     */
    protected void addEvent(Event event) {
    	if (event == null) return;
        if (events == null) events = new HashSet();
        events.add(event);
        event.setOwner(this);
    }
    /**
     * Find event by id
     * @param id
     */
    protected Event getEvent(String id) {
        if (events == null) return null;
        for (Iterator iter=events.iterator(); iter.hasNext();) {
    		Event e = (Event)iter.next();
    		if (e.getId().equals(id)) {
    			return e;
    		}
    	}
    	return null;
    	
    }
    /**
     * Return all attachments 
     * 
	 */
    public Set getAttachments() {
       	//need to implement here to setup the doclet tags
    	if (attachments == null) attachments = new HashSet();
    	return attachments;
    	
    }
    /**
     * Set attachments - this will effect File type custom attributes also
     */
    public void setAttachments(Collection attachments) {   	
    	getAttachments();
    	Set remM = CollectionUtil.differences(this.attachments, attachments);
    	Set addM = CollectionUtil.differences(attachments, this.attachments);
        for (Iterator iter = remM.iterator(); iter.hasNext();) {
        	Attachment a = (Attachment)iter.next();
        	cleanupAttributes(a);
        	a.setOwner((AnyOwner)null);
        	this.attachments.remove(a);
        }
        for (Iterator iter = addM.iterator(); iter.hasNext();) {
        	Attachment a = (Attachment)iter.next();
        	a.setOwner(this);
        	this.attachments.add(a);
        }
    }
    /**
     * Get an attachment by database id
     */
    public Attachment getAttachment(String id) {
    	//make sure loaded
    	getAttachments();
    	for (Iterator iter=attachments.iterator(); iter.hasNext();) {
    		Attachment a = (Attachment)iter.next();
    		if (a.getId().equals(id)) {
    			return a;
    		}
    	}
    	return null;
    }
    /**
     * Remove an attachment.  Attachment object will be deleted from
     * database unless it is added somewhere else.
     * @param attachemnt
     */
    public void removeAttachment(Attachment att) {
       	if (att == null) return;
        getAttachments();
        //remove any custom attributes that point here
        cleanupAttributes(att);
        attachments.remove(att);
       	att.setOwner((AnyOwner)null);           	
    }
    /**
     * Add an attachment
     * @param att
     */
    public void addAttachment(Attachment att) {
    	if (att == null) return;
        getAttachments();
        attachments.add(att);
 	   	att.setOwner(this);
    }

    /**
     * Return list of FileAttachments
     * @return
     */
    public List getFileAttachments() {
    	Set atts = getAttachments();
    	List result = new ArrayList();
    	Attachment att;
    	for (Iterator iter=atts.iterator(); iter.hasNext();) {
    		att = (Attachment)iter.next();
    		//return only file attachments, not versions
    		if (att instanceof FileAttachment) {
    			result.add(att);
    		}
    	}
    	return result;
    }
    
    /**
     * Return FileAttachment corresponding to the specified combination of 
     * repository service name and file name. In other words, file namespace
     * is not based on the file name alone. 
     * 
     * @param fileName
     * @return
     */
    public FileAttachment getFileAttachment(String repositoryServiceName, String fileName) {
    	Set atts = getAttachments();
    	Attachment att;
    	FileAttachment fatt;
    	for (Iterator iter=atts.iterator(); iter.hasNext();) {
    		att = (Attachment)iter.next();
    		if (att instanceof FileAttachment) {
    			fatt = (FileAttachment) att;
    			if(fatt.getRepositoryServiceName().equals(repositoryServiceName) &&
    					fatt.getFileItem().getName().equals(fileName))
    				return fatt;
    		}
    	}
    	return null;
    }
    
    /**
     * Return list of bookmark Attachments
     * @return
     */
 
    public List getBookmarks() {
        Set atts = getAttachments();
       	List result = new ArrayList();
    	Attachment att;
    	for (Iterator iter=atts.iterator(); iter.hasNext();) {
   		att = (Attachment)iter.next();
     		if (att instanceof Bookmark) {
    			result.add(att);
    		}
    	}
    	return result;
    }

    /**
     * Return list of custom attributes. 
     */
    // doclet tags need to be specified in concrete class
	public Map getCustomAttributes() {
    	if (customAttributes == null) customAttributes = new HashMap();
    	return customAttributes;
    }
	/**
	 * Create a new custom attribute and add it to this entry
	 * @param name
	 * @param value
	 * @return
	 */
	public CustomAttribute addCustomAttribute(String name, Object value) {
    	//make sure set up
    	Map atts = getCustomAttributes();
    	if (atts.containsKey(name)) throw new IllegalArgumentException("name exists");
    	CustomAttribute attr = new CustomAttribute(this, name, value);
    	atts.put(name, attr);
    	return attr;
    }
    /**
     * Remove a customAttribute.  Will be deleted from the persistent store.
     * @param customAttribute
     */
    public void removeCustomAttribute(CustomAttribute attr) {
       	if (attr == null) return;   	
       	getCustomAttributes().remove(attr.getName());
       	//the setowner code will make sure any event/files are disconnected
        attr.setOwner((AnyOwner)null);      	
       	
    }
    /**
     * Remove a customAttribute.  Will be deleted from the persistent store.
     * @param name
     */
    public void removeCustomAttribute(String name) {
        CustomAttribute c = getCustomAttribute(name);
        if (c != null) removeCustomAttribute(c);
     }
    /**
     * Retrieve named customAttibute
     * @param name
     * @return
     */
    public CustomAttribute getCustomAttribute(String name) {
    	return (CustomAttribute)getCustomAttributes().get(name);
   }
    /**
     * Retrieve customAttibute
     * @param name
     * @return
     */
    public CustomAttribute getCustomAttributeById(String id) {
    	Map attrs = getCustomAttributes();
    	for (Iterator iter=attrs.values().iterator(); iter.hasNext(); ) {
    		CustomAttribute a = (CustomAttribute)iter.next();
    		if (id.equals(a.getId())) return a;
    	}
    	return null;
   }
    
    /**
     * After an attachment is removed, we must remove it from
     * any custom attributes
     * @param att
     */
    protected void cleanupAttributes(Attachment attachment) {
    	Map attrs = getCustomAttributes();
    	for (Iterator iter=attrs.values().iterator(); iter.hasNext();) {
    		CustomAttribute attr = (CustomAttribute)iter.next();
    		int type = attr.getValueType();
    		if (type == CustomAttribute.ATTACHMENT) {
    			if (attachment.getId().equals(attr.getValue())) 
    				removeCustomAttribute(attr);
    		} else if (type == CustomAttribute.SET) {
    			Set vals = (Set)attr.getValue();
    			
    			Iterator vIter=vals.iterator();
    			if (vIter.hasNext()) {
    				Object obj = vIter.next();
    				//see if set of attachments
    				if (obj instanceof Attachment) {
    					vals.remove(attachment);
    					if (vals.isEmpty()) removeCustomAttribute(attr);
    					else attr.setValue(vals);
    				}
    			}
    		}
    	}
    }
    public String getIndexDocumentUid() {
        return BasicIndexUtils.makeUid(this.getClass().getName(), this.getId());
    }
}
