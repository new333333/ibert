package com.sitescape.ef.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sitescape.util.Validator;
import com.sitescape.ef.modelprocessor.InstanceLevelProcessorSupport;
import com.sitescape.ef.security.acl.AclContainer;
import com.sitescape.ef.security.acl.AclSet;
import com.sitescape.ef.security.function.WorkArea;

/**
 * This object represents a container.
 * 
 * @hibernate.class table="SS_Forums" dynamic-update="true" dynamic-insert="false" lazy="false"
 * @hibernate.discriminator type="string" length="16" column="type"
 * @hibernate.query name="find-Binder-Company" query="from com.sitescape.ef.domain.Binder binder where binder.name=:binderName and binder.zoneName=:zoneName"
 * @hibernate.cache usage="read-write"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Jong Kim
 *
 */
public abstract class Binder extends DefinableEntity implements WorkArea, AclContainer, InstanceLevelProcessorSupport  {
    private String name;
    private HistoryStamp owner;
    private Map properties;
    protected Binder parentBinder;
    private NotificationDef notificationDef;
    private List postings;
    private Integer upgradeVersion;   
    private String zoneName; 
    private long featureMask=0;
    private String type;
    private List definitions;
    private Definition defaultPostingDef;

    private boolean functionMembershipInherited = true;
    private PersistentAclSet aclSet;
    private boolean inheritAclFromParent = true;
    // these bits signify whether entries of a binder can allow wider access
    // than the binder's .  This does not apply to sub-binders.
    private boolean widenRead=false;
    private boolean widenModify=false;
    private boolean widenDelete=false;
    
    public abstract List getEntryDefs();
    public abstract List getBinderViewDefs();
    /**
     * @hibernate.property length="100" not-null="true" node="zoneName"
     */
    public String getZoneName() {
    	return this.zoneName;
    }
    public void setZoneName(String id) {
    	this.zoneName = id;
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
    public String getAnyOwnerType() {
    	return AnyOwner.BINDER;
    }
	/**
 	 * @hibernate.map  lazy="true" inverse="true" cascade="all,delete-orphan"
	 * @hibernate.key column="binder"
 	 * @hibernate.map-key column="name" type="string"
     * @hibernate.one-to-many class="com.sitescape.ef.domain.CustomAttribute"
     * @return
     */
    private Map getHCustomAttributes() {return customAttributes;}
    private void setHCustomAttributes(Map customAttributes) {this.customAttributes = customAttributes;}   	
    
    /**
     * @hibernate.set  lazy="true" inverse="true" cascade="all,delete-orphan" batch-size="4" 
 	 * @hibernate.key column="binder"
 	 * @hibernate.one-to-many class="com.sitescape.ef.domain.Attachment"
 	 * We are using a set here, cause any outer-joins to load this attribute
 	 * when using a list result in duplicates
   	 */
    private Set getHAttachments() {return attachments;}
    private void setHAttachments(Set attachments) {this.attachments = attachments;}   	

   /**
	* @hibernate.set lazy="true" inverse="true" cascade="all,delete-orphan" batch-size="4" 
    * @hibernate.key column="binder"
    * @hibernate.one-to-many class="com.sitescape.ef.domain.Event"
    * @return
    */
    private Set getHEvents() {return events;}
    private void setHEvents(Set events) {this.events = events;}   	
    /**
     * @hibernate.list table="SS_DefinitionMap" lazy="true" inverse="false" cascade="persist,merge,save-update"
     * @hibernate.key column="forum"
     * @hibernate.index column="position" type="integer"
     * @hibernate.many-to-many fetch="join" class="com.sitescape.ef.domain.Definition"
     * @hibernate.column name="definition" sql-type="char(32)"
     * @hibernate.cache usage="read-write"
	 * @return Returns a Set of Commands
     */
    private List getHDefinitions() {return definitions;}
    private void setHDefinitions(List definitions) {this.definitions = definitions;}
     public List getDefinitions() {
     	if (definitions == null) definitions = new ArrayList();
     	return definitions;
     }
    //definitions doesn't keep an inverse collection
    public void setDefinitions(List newDefs) {
    	//order matters.
    	List oldDefs = getDefinitions();
    	if ((newDefs == null) || newDefs.isEmpty()) {
    		oldDefs.clear();
    		return;
    	}
    	//add new elements in order
    	for (int i=0; i<newDefs.size(); ++i) {
    		oldDefs.add(i, newDefs.get(i));
    	}
    	//remove extras
    	int pos = newDefs.size();
    	while (oldDefs.size() > newDefs.size()) {
    		oldDefs.remove(pos);
    	}
    				
    }
    public void addDefinition(Definition cmd) {
        if (!definitions.contains(cmd)) definitions.add(cmd);
    }
    public void removeDefinition(Definition cmd) {
    	definitions.remove(cmd);
    }    
    public List getWorkflowDefs() {
    	return getDefs(Definition.WORKFLOW);
    }
    public Definition getDefaultEntryDef() {
    	
    	List profileDefinitions = getEntryDefs();
    	if (profileDefinitions.size() > 0)
    		return (Definition)profileDefinitions.get(0);
    	return null;
	}
    
    /**
     * @hibernate.many-to-one access="field" class="com.sitescape.ef.domain.Definition"
     * @hibernate.column name="defaultPostingDef" sql-type="char(32)"
     * @return
     */
    public Definition getDefaultPostingDef() {
  		return defaultPostingDef;
    }
    public void setDefaultPostingDef(Definition defaultPostingDef) {
        this.defaultPostingDef = defaultPostingDef;
    }
    
    protected List getDefs(int type) {
       	Definition def;
    	List result = new ArrayList(); 
    	for (int i=0; i<definitions.size(); ++i) {
    		def = (Definition)definitions.get(i);
    		if (def.getType() == type) {
    			result.add(def);
    		}
       	}
       	return result;
    }
    /**
     * @hibernate.property length="16" insert="false" update="false" node="type"
     *
     */
    public String getType() {
    	return type;
    }
    public void setType(String type) {
    	this.type = type;
    }

    /**
     * @hibernate.property length="128" not-null="true" node="name"
     * @return
     */
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
    	if (Validator.isNull(name)) throw new IllegalArgumentException("null name");
       this.name = name;
    }

    /**
     * @hibernate.component prefix="notify_"
     * @return
     */
    public NotificationDef getNotificationDef() {
    	if (notificationDef ==null) notificationDef = new NotificationDef();
        return notificationDef;
    }
    public void setNotificationDef(NotificationDef notificationDef) {
        this.notificationDef = notificationDef;
    }
    /**
     * @hibernate.bag  lazy="true" cascade="all,delete-orphan" inverse="true" optimistic-lock="false" 
     * @hibernate.key column="binder" 
     * @hibernate.one-to-many class="com.sitescape.ef.domain.PostingDef" 
     * @return
     */
    public List getPostings() {
    	if (postings == null) return new ArrayList();
    	return postings;
    }
    public void setPostings(List postings) {
        this.postings = postings;
    }
    public void addPosting(PostingDef post) {
    	post.setBinder(this);
    	postings.add(post);
    }
    public void removePosting(PostingDef post) {
    	postings.remove(post);
    }
    public PostingDef getPosting(String postingId) {
       	for (int i=0; i<postings.size(); ++i) {
    		PostingDef post = (PostingDef)postings.get(i);
    		if (post.getId().equals(postingId)) 
    			return post;
       	}
       	return null;
    }
    /**
     * @hibernate.component prefix="owner_" node="owner"
     */
    public HistoryStamp getOwner() {
        return owner;
    }
    public void setOwner(HistoryStamp owner) {
        this.owner = owner;
    }
    /**
     * @hibernate.property type="org.springframework.orm.hibernate3.support.BlobSerializableType"
     * @return
     */
    public Map getProperties() {
    	return properties;
    }
    public void setProperties(Map properties) {
        this.properties = properties;
    }
    public void setProperty(String name, Object value) {
    	if (properties == null) properties = new HashMap();
    	properties.put(name, value);
    }
    public Object getProperty(String name) {
    	if (properties == null) return null;
    	return properties.get(name);
    }


    
    /**
     * hibernate.property node="upgradeVersion"
     */
    public Integer getUpgradeVersion() {
        return this.upgradeVersion;
    }
    public void setUpgradeVersion(Integer upgradeVersion) {
        this.upgradeVersion = upgradeVersion;
    }
    
    /**
     * @hibernate.property node="featureMask"
     * @hibernate.column name="featureMask" 
     * @return
     */
    protected long getIFeatureMask() {
        return this.featureMask;
    }
    protected void setIFeatureMask(long featureMask) {
        this.featureMask = featureMask;
    }
    /*
     * each forum application will determine the meaning of the bits
     */
    public String getFeatureMask() {
        return Long.toBinaryString(featureMask);
    }
    public void setFeatureMask(String featureMask) {
        setIFeatureMask(Long.parseLong(featureMask, 2));
    }

    public String toString() {
    	return getZoneName() + ":" + name; 
    }

    public Long getWorkAreaId() {
        return getId();
    }
    public String getWorkAreaType() {
        return getType();
    }
    public WorkArea getParentWorkArea() {
        return this.getParentBinder();
    }
	/**
	 * @hibernate.property not-null="true" node="functionMembershipInherited"
	 * @return
	 */
    public boolean isFunctionMembershipInherited() {
        return functionMembershipInherited;
    }
    public void setFunctionMembershipInherited(boolean functionMembershipInherited) {
        this.functionMembershipInherited = functionMembershipInherited;
    }

    public AclContainer getParentAclContainer() {
        return this.getParentBinder();
    }
    
    public Long getAclContainerId() {
        return getId();
    }
    
    /**
     * Hiberate interfaces to load actual class vs interface needed for AclControlled
     *  
     * @hibernate.component prefix="acl_" 
     */
    private PersistentAclSet getHAclSet() {
        return aclSet;
    }
     private void setHAclSet(PersistentAclSet aclSet) {
        this.aclSet = aclSet;
    }
     /**
     * Used by security manager only. Application should NEVER invoke this
     * method directly.  
     * @hibernate.component prefix="acl_" class="com.sitescape.ef.domain.PersistentAclSet" 
     */
    public void setAclSet(AclSet aclSet) {
        this.aclSet = (PersistentAclSet)aclSet;
    }
    /**
     * Used by security manager only. Application should NEVER invoke this
     * method directly.  
     */
    public AclSet getAclSet() {
        return aclSet;
    } 
    
    /**
     * @hibernate.property column="acl_inheritFromParent" 
     */
    public boolean getInheritAclFromParent() {
        return inheritAclFromParent;
    }

    public void setInheritAclFromParent(boolean inherit) {
        this.inheritAclFromParent = inherit;
    }
    /**
     * @hibernate.property column="acl_widenRead" 
     */
    public boolean isWidenRead() {
        return widenRead;
    }

    public void setWidenRead(boolean widenRead) {
        this.widenRead = widenRead;
    }
    /**
     * @hibernate.property column="acl_widenModify" 
     */
    public boolean isWidenModify() {
        return widenModify;
    }

    public void setWidenModify(boolean widenModify) {
        this.widenModify = widenModify;
    }
    /**
     * @hibernate.property column="acl_widenDelete" 
     */
    public boolean isWidenDelete() {
        return widenDelete;
    }

    public void setWidenDelete(boolean widenDelete) {
        this.widenDelete = widenDelete;
    }    
    public Long getCreatorId() {
    	HistoryStamp creation = getCreation();
    	if(creation != null) {
    		Principal principal = creation.getPrincipal();
    		if(principal != null)
    			return principal.getId();
    	}
    	return null;
    }

    public String getProcessorClassName(String processorKey) {
        return (String) getProperty(processorKey);
    }
    
    public void setProcessorClassName(String processorKey, String processorClassName) {
        setProperty(processorKey, processorClassName);
    }
}
