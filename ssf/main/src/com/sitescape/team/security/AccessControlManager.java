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
package com.sitescape.team.security;

import java.util.Set;

import com.sitescape.team.domain.User;
import com.sitescape.team.security.acl.AccessType;
import com.sitescape.team.security.acl.AclAccessControlException;
import com.sitescape.team.security.acl.AclContainer;
import com.sitescape.team.security.acl.AclControlled;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaOperation;

/**
 *
 * @author Jong Kim
 */
public interface AccessControlManager {

	/**
	 * Return a set of principalIds that have the 
     * privilege to run the operation against the work area. 
	 * @param workArea
	 * @param workAreaOperation
	 * @return
	 */
	public Set getWorkAreaAccessControl(WorkArea workArea,
			WorkAreaOperation workAreaOperation); 
	
    /**
     * Same as {@link #checkOperation(WorkArea, WorkAreaOperation)} except 
     * that this returns <code>boolean</code> flag rather than throwing an 
     * exception.
     * 
     * @param workArea
     * @param workAreaOperation
     * @return
     */
    public boolean testOperation(WorkArea workArea,
            WorkAreaOperation workAreaOperation);
        
    /**
     * Same as {@link #checkOperation(User, WorkArea, WorkAreaOperation)} except
     * that this returns <code>boolean</code> flag rather than throwing an 
     * exception. 
     * 
     * @param user
     * @param workArea
     * @param workAreaOperation
     * @return
     */
    public boolean testOperation(User user, WorkArea workArea,
            WorkAreaOperation workAreaOperation);

    /**
     * Check if the user associated with the current request context has the 
     * privilege to run the operation against the work area. 
     * 
     * @param workArea
     * @param workAreaOperation
     * @throws AccessControlException
     */
    public void checkOperation(WorkArea workArea,
            WorkAreaOperation workAreaOperation) throws AccessControlException;
        
    /**
     * Check if the specified user has the privilege to run the operation
     * against the work area. 
     * <p>
     * Use this method if one of the following conditions is true.
     * <p>
     * 1) There is no <code>RequestContext</code> set up for the calling thread.<br>
     * 2) The user associated with the current request context is not the same
     * as the user against which this access check is being performed. 
     * 
     * @param user
     * @param workArea
     * @param workAreaOperation
     * @throws AccessControlException
     */
    public void checkOperation(User user, WorkArea workArea,
            WorkAreaOperation workAreaOperation) throws AccessControlException;

    public void checkAcl(AclContainer parent, AclControlled aclControlledObj,
            AccessType accessType) throws AccessControlException;
    
    /**
     * Same as <code>checkObjectAccessControl</code> except that this returns
     * <code>boolean</code> flag rather than throwing an exception.
     * 
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @return
     */
    public boolean testAcl(AclContainer parent, AclControlled aclControlledObj,
            AccessType accessType);
    
    /**
     * Check if the specified user has the specified type of access to the object.
     * <p>
     * Use this method if one of the following conditions is true.
     * <p>
     * 1) There is no <code>RequestContext</code> set up for the calling thread.<br>
     * 2) The user associated with the current request context is not the same
     * as the user against which this access check is being performed. 
     * 
     * @param user
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @throws AccessControlException
     */
    public void checkAcl(User user, AclContainer parent, 
            AclControlled aclControlledObj, AccessType accessType) 
    	throws AccessControlException;
    
    /**
     * Same as <code>checkObjectAccessControl</code> except that this returns
     * <code>boolean</code> flag rather than throwing an exception.
     * 
     * @param user
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @return
     */
    public boolean testAcl(User user, AclContainer parent, 
            AclControlled aclControlledObj, AccessType accessType);
    
    public void checkAcl(AclContainer aclContainer, AccessType accessType) 
    	throws AccessControlException;
    
    public boolean testAcl(AclContainer aclContainer, AccessType accessType);
    
    public void checkAcl(User user, AclContainer aclContainer, 
            AccessType accessType) throws AccessControlException;
    
    public boolean testAcl(User user, AclContainer aclContainer,
            AccessType accessType);
    
    /**
     * Check if the user associated with the current request context has the
     * specified type of access to the object. 
     * 
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @throws AccessControlException
     */
    public void checkAcl(AclContainer parent, AclControlled aclControlledObj,
            AccessType accessType, boolean includeParentAcl) throws AccessControlException;
    
    /**
     * Same as <code>checkObjectAccessControl</code> except that this returns
     * <code>boolean</code> flag rather than throwing an exception.
     * 
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @return
     */
    public boolean testAcl(AclContainer parent, AclControlled aclControlledObj,
            AccessType accessType, boolean includeParentAcl);
    
    /**
     * Check if the specified user has the specified type of access to the object.
     * <p>
     * Use this method if one of the following conditions is true.
     * <p>
     * 1) There is no <code>RequestContext</code> set up for the calling thread.<br>
     * 2) The user associated with the current request context is not the same
     * as the user against which this access check is being performed. 
     * 
     * @param user
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @throws AccessControlException
     */
    public void checkAcl(User user, AclContainer parent, 
            AclControlled aclControlledObj, AccessType accessType,
            boolean includeParentAcl) 
    	throws AccessControlException;
    
    /**
     * Same as <code>checkObjectAccessControl</code> except that this returns
     * <code>boolean</code> flag rather than throwing an exception.
     * 
     * @param user
     * @param parent
     * @param aclControlledObj
     * @param accessType
     * @return
     */
    public boolean testAcl(User user, AclContainer parent, 
            AclControlled aclControlledObj, AccessType accessType,
            boolean includeParentAcl);
    
    public void checkAcl(AclContainer aclContainer, AccessType accessType,
    		boolean includeParentAcl) 
    	throws AccessControlException;
    
    public boolean testAcl(AclContainer aclContainer, AccessType accessType,
    		boolean includeParentAcl);
    
    public void checkAcl(User user, AclContainer aclContainer, 
            AccessType accessType, boolean includeParentAcl) throws AccessControlException;
    
    public boolean testAcl(User user, AclContainer aclContainer,
            AccessType accessType, boolean includeParentAcl);

 
}
