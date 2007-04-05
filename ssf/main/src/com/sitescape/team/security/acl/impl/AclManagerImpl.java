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
package com.sitescape.team.security.acl.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sitescape.team.security.acl.AccessType;
import com.sitescape.team.security.acl.AclContainer;
import com.sitescape.team.security.acl.AclControlled;
import com.sitescape.team.security.acl.AclManager;
import com.sitescape.team.security.acl.AclSet;

/**
 *
 * @author Jong Kim
 */
public class AclManagerImpl implements AclManager {

    public void propagate(AclContainer container, boolean recursive) {
        // Set the acls of the non-container type child objects to that of the 
        // parent. This is done by making them inherit their acls from the
        // parent. 
        List objects = container.getChildAclControlled();
        for(Iterator i = objects.iterator(); i.hasNext();) {
            AclControlled obj = (AclControlled) i.next();
            // TODO We much exclude those entries that are under control by a
            // workflow process...
            doInherit(container, obj);
        }
        
        if(recursive) {
            // Apply it to child containers too.
            List childContainers = container.getChildAclContainers();
            for(Iterator i = childContainers.iterator(); i.hasNext();) {
                AclContainer childContainer = (AclContainer) i.next();
                doInherit(container, childContainer);
                // Proceed recursively.
                propagate(childContainer, recursive);
            }
        }
    }
    
    public Set getMembers(AclContainer parent, AclControlled obj, AccessType accessType) {
        return getAclSet(parent, obj).getMemberIds(accessType);
    }

    public void addMember(AclContainer parent, AclControlled obj, AccessType accessType, Long memberId) {
        initOwnAclSet(parent, obj);
        obj.getAclSet().addMemberId(accessType, memberId);
    }

    public void removeMember(AclContainer parent, AclControlled obj, AccessType accessType, Long memberId) {
        initOwnAclSet(parent, obj);
        obj.getAclSet().removeMemberId(accessType, memberId);
    }

    public void doInherit(AclContainer parent, AclControlled obj) {
        if(obj.getInheritAclFromParent()) {
            // It is already inheriting from the parent. No op. 
        }
        else {
            // The object has its own set of acls.
            obj.getAclSet().clear();
            obj.setInheritAclFromParent(true);
        }
    }

    public void doNotInherit(AclContainer parent, AclControlled obj) {
        initOwnAclSet(parent, obj);
    }

    public Set getMembers(AclContainer obj, AccessType accessType) {
        return getMembers(obj.getParentAclContainer(), obj, accessType);
    }

    public void addMember(AclContainer obj, AccessType accessType, Long memberId) {
        addMember(obj.getParentAclContainer(), obj, accessType, memberId);
    }

    public void removeMember(AclContainer obj, AccessType accessType, Long memberId) {
        removeMember(obj.getParentAclContainer(), obj, accessType, memberId);
    }

    public void doInherit(AclContainer obj) {
        doInherit(obj.getParentAclContainer(), obj);
    }

    public void doNotInherit(AclContainer obj) {
        doNotInherit(obj.getParentAclContainer(), obj);
    }
    
    private AclSet getAclSet(AclContainer parent, AclControlled obj) {
        if(obj.getInheritAclFromParent()) {
            return getAclSet(parent.getParentAclContainer(), parent);
        }
        else {
            return obj.getAclSet();
        }
    }
    
    private AclSet getAclSet(AclContainer obj) {
        return getAclSet(obj.getParentAclContainer(), obj);
    }
    
    private void initOwnAclSet(AclContainer parent, AclControlled obj) {
        if(obj.getInheritAclFromParent()) {
            // The object is currently inheriting its acls from the parent. 
            // Make a local copy. 
            obj.setAclSet((AclSet) getAclSet(parent).clone());
            obj.setInheritAclFromParent(false);
        }
        else {
            // The object already has its own set of acls. In this case,
            // do not reset or reinitialize them. 
        }
    }
}
