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
package com.sitescape.team.module.binder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.SingletonViolationException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WfAcl;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.AccessControlManager;
import com.sitescape.team.security.acl.AclAccessControlException;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.FunctionManager;
import com.sitescape.team.security.function.OperationAccessControlException;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.CollectionUtil;
import com.sitescape.team.util.SPropsUtil;

public class AccessUtils  {
	private static AccessUtils instance; // A singleton instance
	protected AccessControlManager accessControlManager;
	protected FunctionManager functionManager;
	protected ProfileDao profileDao;
	public AccessUtils() {
		if(instance != null)
			throw new SingletonViolationException(AccessUtils.class);
		
		instance = this;
	}
    private static AccessUtils getInstance() {
    	return instance;
    }
	public void setAccessControlManager(AccessControlManager accessControlManager) {
		this.accessControlManager = accessControlManager;
	}
	protected AccessControlManager getAccessControlManager() {
		return accessControlManager;
	}
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}
	protected FunctionManager getFunctionManager() {
		return functionManager;
	}

	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	protected ProfileDao getProfileDao() {
		return profileDao;
	}

    public static Set getReadAccessIds(Entry entry) {
        Set binderIds = getInstance().getAccessControlManager().getWorkAreaAccessControl(entry.getParentBinder(), WorkAreaOperation.READ_ENTRIES);
	       
		Set<Long> entryIds = new HashSet<Long>();
	    if (entry instanceof WorkflowSupport) {
			WorkflowSupport wEntry = (WorkflowSupport)entry;
			if (wEntry.hasAclSet()) {
				//index binders access
				if (wEntry.isWorkAreaAccess(WfAcl.AccessType.read)) {
					entryIds.addAll(binderIds);
				}
				entryIds.addAll(wEntry.getStateMembers(WfAcl.AccessType.read));
		        //replaces reserved ownerId with entry owner
				
	    		if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
	    			//only index ids in both sets ie(AND)
	    			//remove ids in entryIds but not in binderIds
	    			entryIds.removeAll(CollectionUtil.differences(entryIds, binderIds));
	    		}

				//	no access specified, add binder default
				if (entryIds.isEmpty())
					entryIds.addAll(binderIds);
        		return entryIds;
			} else {
				//doesn't have any active workflow, use binder access
				return binderIds;
			}
		} else {
			//use binder access
			return binderIds;
		}
    	 
     }
     public static Set getReadAccessIds(Binder binder) {
        return getInstance().getAccessControlManager().getWorkAreaAccessControl(binder, WorkAreaOperation.READ_ENTRIES);     	 
      }     	
	
	public static void readCheck(Entry entry) throws AccessControlException {
		readCheck(RequestContextHolder.getRequestContext().getUser(), entry);
	}
	public static void readCheck(User user, Entry entry) throws AccessControlException {
        if (user.isSuper()) return;
    	if (entry instanceof WorkflowSupport)
    		readCheck(user, entry.getParentBinder(), (WorkflowSupport)entry);
    	else 
    		readCheck(user, entry.getParentBinder(), (Entry)entry);
    		
   }
	private static void readCheck(User user, Binder binder, Entry entry) {
		getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
    }
    private static void readCheck(User user, Binder binder, WorkflowSupport entry) throws AccessControlException {
		if (!entry.hasAclSet()) {
			readCheck(user, binder, (Entry)entry);
		} else if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
 			//just check entry acl, ignore binder
 			try {
 				checkAccess(user, entry, WfAcl.AccessType.read);
 			} catch (AccessControlException ex) {
 				if (entry.isWorkAreaAccess(WfAcl.AccessType.read)) { 		
 					readCheck(user, binder, (Entry)entry);
 				} else throw ex;
 			}
 			
 		} else {
 			//must have READ access to binder AND entry
 			//see if pass binder test
 			readCheck(user, binder, (Entry)entry);
 			//	see if binder default is enough
 			if (entry.isWorkAreaAccess(WfAcl.AccessType.read)) return;
 			//This basically AND's the binder and entry, since we already passed the binder
 			checkAccess(user, entry, WfAcl.AccessType.read);
  		}
	}
    
    private static void checkAccess(User user, WorkflowSupport entry, WfAcl.AccessType type) {
         Set allowedIds = entry.getStateMembers(type);
        if (testAccess(user, allowedIds)) return;
        throw new AclAccessControlException(user.getName(), type.toString());
    }
    private static boolean testAccess(User user, Set allowedIds) {
     	Set principalIds = getInstance().getProfileDao().getPrincipalIds(user);
        for(Iterator i = principalIds.iterator(); i.hasNext();) {
            if (allowedIds.contains(i.next())) return true;
        }
        return false;
    }
    public static void modifyCheck(Entry entry) throws AccessControlException {
		modifyCheck(RequestContextHolder.getRequestContext().getUser(), entry);
    }
    public static void modifyCheck(User user, Entry entry) throws AccessControlException {
        if (user.isSuper()) return;
		if (entry instanceof WorkflowSupport)
    		modifyCheck(user, entry.getParentBinder(), (WorkflowSupport)entry);
    	else 
    		modifyCheck(user, entry.getParentBinder(), (Entry)entry);
    		
   }
    private static void modifyCheck(User user, Binder binder, Entry entry) {
       	try {
       		getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.MODIFY_ENTRIES);
       	} catch (OperationAccessControlException ex) {
      		if (user.equals(entry.getCreation().getPrincipal())) 
      			getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_MODIFY);
      		else throw ex;
      	}
    }
     private static void modifyCheck(User user, Binder binder, WorkflowSupport entry) {
 		if (!entry.hasAclSet()) {
			modifyCheck(user, binder, (Entry)entry);
		} else if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
 			//just check entry acl, ignore binder
 			try {
 				//check explicit users
 				checkAccess(user, entry, WfAcl.AccessType.write);
 			} catch (AccessControlException ex) {
 				if (entry.isWorkAreaAccess(WfAcl.AccessType.write)) { 		
 					modifyCheck(user, binder, (Entry)entry);
 				} else throw ex;
 			}
 			
 		} else {
 			//must have READ access to binder AND modify to the entry
 			if (entry.isWorkAreaAccess(WfAcl.AccessType.write)) {
 				//optimzation: if pass modify binder check, don't need to do read binder check
 				try {
					modifyCheck(user, binder, (Entry)entry);
					return;
 				} catch (AccessControlException ex) {} //move on to next checks
 			}
 			//see if pass binder READ test
 			readCheck(user, binder, (Entry)entry);
 			//This basically AND's the binder and entry, since we already passed the binder
 			checkAccess(user, entry, WfAcl.AccessType.write);
  		}
    }

     public static void deleteCheck(Entry entry) throws AccessControlException {
    	 deleteCheck(RequestContextHolder.getRequestContext().getUser(), entry);
     }
     public static void deleteCheck(User user, Entry entry) throws AccessControlException {
        if (user.isSuper()) return;
     	if (entry instanceof WorkflowSupport)
     		deleteCheck(user, entry.getParentBinder(), (WorkflowSupport)entry);
     	else 
     		deleteCheck(user, entry.getParentBinder(), (Entry)entry);
     		
    }
    private static void deleteCheck(User user, Binder binder, Entry entry) throws AccessControlException {
      	try {
      		getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.DELETE_ENTRIES);
       	} catch (OperationAccessControlException ex) {
      		if (user.equals(entry.getCreation().getPrincipal())) 
   				getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_DELETE);
      		else throw ex;
      	}   
    }
    private static void deleteCheck(User user, Binder binder, WorkflowSupport entry)  throws AccessControlException {
		if (!entry.hasAclSet()) {
			deleteCheck(user, binder, (Entry)entry);
		} else if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
 			//just check entry acl, ignore binder
 			try {
 				//check explicit users
 				checkAccess(user, entry, WfAcl.AccessType.delete);
 			} catch (AccessControlException ex) {
 				if (entry.isWorkAreaAccess(WfAcl.AccessType.delete)) { 		
 					deleteCheck(user, binder, (Entry)entry);
 				} else throw ex;
 			}
 			
 		} else {
 			//must have READ access to binder AND delete to the entry
 			if (entry.isWorkAreaAccess(WfAcl.AccessType.delete)) {
 				//optimzation: if pass delete binder check, don't need to do read binder check
 				try {
 					deleteCheck(user, binder, (Entry)entry);
					return;
 				} catch (AccessControlException ex) {} //move on to next checks
 			}
 			//see if pass binder READ test
 			readCheck(user, binder, (Entry)entry);
 			//This basically AND's the binder and entry, since we already passed the binder
 			checkAccess(user, entry, WfAcl.AccessType.delete);
  		}
     }
     public static void overrideReserveEntryCheck(Entry entry) {
     	try {
     		getInstance().getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getUser(), entry.getParentBinder(), WorkAreaOperation.BINDER_ADMINISTRATION);
     	} catch (OperationAccessControlException ex) {
    		throw ex;
    	}
     }          
     
     public static void overrideReserveEntryCheck(Binder binder) {
        try {
        	getInstance().getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getUser(), binder, WorkAreaOperation.BINDER_ADMINISTRATION);
        } catch (OperationAccessControlException ex) {
       		throw ex;
       	}
     }     
     
     public static void checkTransitionIn(Binder binder, WorkflowSupport entry, Definition definition, String toState)  
     	throws AccessControlException {
     	checkTransitionAcl(binder, entry, WfAcl.AccessType.transitionIn);
     }
     public static void checkTransitionOut(Binder binder, WorkflowSupport entry, Definition definition, String toState)  
     	throws AccessControlException {
     	checkTransitionAcl(binder, entry, WfAcl.AccessType.transitionOut);
     }
     private static void checkTransitionAcl(Binder binder, WorkflowSupport entry, WfAcl.AccessType type)  
      	throws AccessControlException {
      	User user = RequestContextHolder.getRequestContext().getUser();
        if (user.isSuper()) return;
  
		if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
 			//just check entry acl, ignore binder
 			try {
 				//check explicit users
 				checkAccess(user, entry, type);
 			} catch (AccessControlException ex) {
 				if (entry.isWorkAreaAccess(type)) { 		
 					modifyCheck(user, binder, (Entry)entry);
 				} else throw ex;
 			}
 			
 		} else {
 			//must have READ access to binder AND modify to the entry
 			if (entry.isWorkAreaAccess(type)) {
 				//optimzation: if pass modify binder check, don't need to do read binder check
 				try {
					modifyCheck(user, binder, (Entry)entry);
					return;
 				} catch (AccessControlException ex) {} //move on to next checks
 			}
 			//see if pass binder READ test
 			readCheck(user, binder, (Entry)entry);
 			//This basically AND's the binder and entry, since we already passed the binder
 			checkAccess(user, entry, type);
  		}
     }
     
     
  	public static boolean checkIfAllOperationsAllowed(Long functionId, WorkArea workArea) {
      	User user = RequestContextHolder.getRequestContext().getUser();
  		Function f = getInstance().functionManager.getFunction(user.getZoneId(), functionId);
  		return checkIfAllOperationsAllowed(f, workArea);
  	}
  	public static boolean checkIfAllOperationsAllowed(Function f, WorkArea workArea) {
		Iterator itOperations = f.getOperations().iterator();
		while (itOperations.hasNext()) {
			WorkAreaOperation o = (WorkAreaOperation) itOperations.next();
			if (!getInstance().getAccessControlManager().testOperation((WorkArea) workArea, o)) return false;
		}
		return true;
	}
    	    
    public static User getZoneSuperUser() {
        User user = RequestContextHolder.getRequestContext().getUser();
		User superUser = getInstance().getProfileDao().getReservedUser(ObjectKeys.SUPER_USER_INTERNALID, user.getZoneId());
		return superUser;
    }
}