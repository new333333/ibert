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
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.sitescape.team.domain.User;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.util.CollectionUtil;
import com.sitescape.util.Validator;
/**
 * @hibernate.subclass discriminator-value="G" dynamic-update="true" 
 *
 */
public class Group extends Principal implements WorkArea {
    private List members;  //initialized by hibernate access=field  
    private Principal owner; //initialized by hibernate access=field  
    
    private Boolean functionMembershipInherited = Boolean.TRUE;//initialized by hibernate access=field
      
	public EntityIdentifier.EntityType getEntityType() {
		return EntityIdentifier.EntityType.group;
	}
    public String getTitle() {
    	String title = super.getTitle();
    	if (Validator.isNull(title)) return getName();
    	return title;
    }
   
    public List getMembers() {
    	if (members == null) members = new ArrayList();
    	return members;
    }
    /**
     * Set the group membership.  Each members memberOf set will by updated
     * @param members
     */
    public void setMembers(Collection newMembers) { 		
   		if (newMembers == null) newMembers = new ArrayList();
		if (members == null) members = new ArrayList();
		Set newM = CollectionUtil.differences(newMembers, members);
		Set remM = CollectionUtil.differences(members, newMembers);
		this.members.addAll(newM);
		this.members.removeAll(remM);
		for (Iterator iter=newM.iterator(); iter.hasNext();) {
			Principal p = (Principal)iter.next();
			p.getMemberOf().add(this);
		}
		for (Iterator iter=newM.iterator(); iter.hasNext();) {
			Principal p = (Principal)iter.next();
			p.getMemberOf().remove(this);
		}
  	} 	
    
    public void addMember(Principal member) {
		if (members == null) members = new ArrayList();
    	if (members.contains(member)) return;
    	members.add(member);
    	member.getMemberOf().add(this);
    }
    public void removeMember(Principal member) {
		if (members == null) members = new ArrayList();
    	members.remove(member);
    	member.getMemberOf().remove(this);
    }
    
	public Long getWorkAreaId() {
		return getId();
	}
	public String getWorkAreaType() {
		return EntityIdentifier.EntityType.group.name();
	}
	public WorkArea getParentWorkArea() {
		// Group can be a child of many other groups. No single parent.
		// TODO Then where should we inherit the function membership from?
		return null; // For now
	}
	public Principal getOwner() {
		if (owner != null) return owner;
	   	HistoryStamp creation = getCreation();
    	if ((creation != null) && creation.getPrincipal() != null) {
    		return creation.getPrincipal();
    	}
    	return null;
		
	}
	public void setOwner(Principal owner) {
		this.owner = owner;
	}
	public Long getOwnerId() {
		Principal owner = getOwner();
		if (owner == null)	return null;
		return owner.getId();
 
	}
	
	// I have separate sets of methods for handling functionMembershipInherited
	// field - one for WorkArea interface and the other for Hibernate persistence.
	// Strictly speaking this separation is not at all necessary. But in order
	// to allow people to continue working with existing databases without having
	// to re-build them, I had to allow nulls for existing records, and hence
	// this ugly code. 
	public boolean isFunctionMembershipInherited() {
    	if(functionMembershipInherited == null)
    		return true; // Default value
    	else		
    		return functionMembershipInherited.booleanValue();
	}
    //this is needed for templates, which may inherit from a yet to be determined parent
    public boolean isFunctionMembershipInheritanceSupported() {
    	return true;
    }

	public void setFunctionMembershipInherited(boolean functionMembershipInherited) {
        this.functionMembershipInherited = Boolean.valueOf(functionMembershipInherited);
    }
	

}
