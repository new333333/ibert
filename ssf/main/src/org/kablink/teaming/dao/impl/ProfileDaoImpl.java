/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.dao.impl;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.engine.SessionFactoryImplementor;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.comparator.LongIdComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.KablinkDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.ObjectControls;
import org.kablink.teaming.dao.util.SFQuery;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.ApplicationPrincipal;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EmailAddress;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.IndividualPrincipal;
import org.kablink.teaming.domain.Membership;
import org.kablink.teaming.domain.NoApplicationByTheIdException;
import org.kablink.teaming.domain.NoGroupByTheIdException;
import org.kablink.teaming.domain.NoGroupByTheNameException;
import org.kablink.teaming.domain.NoPrincipalByTheIdException;
import org.kablink.teaming.domain.NoPrincipalByTheNameException;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.Rating;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.SharedEntity;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserEntityPK;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.UserPropertiesPK;
import org.kablink.teaming.domain.Visits;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.Validator;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateSystemException;

/**
 * @author Jong Kim
 *
 */
public class ProfileDaoImpl extends KablinkDao implements ProfileDao {
	protected int inClauseLimit=1000;
	private CoreDao coreDao;
	Map reservedIds = new HashMap();
	
	private static final String FAKE_NAME_PREFIX = "__unavailable_";
	
	public void setCoreDao(CoreDao coreDao) {
		   this.coreDao = coreDao;
		}
	private CoreDao getCoreDao() {
	    return coreDao;
	}
    /**
     * Called after bean is initialized.  
     */
	protected void initDao() throws Exception {
		//some database limit the number of terms 
		inClauseLimit=SPropsUtil.getInt("db.clause.limit", 1000);
	}
	/*
	 * In most cases this will be initialized at startup and won't change,
	 * but adding a zone or changing the ids could do it.
	 */
	protected synchronized Long getReservedId(String internalId, Long zoneId) {
		String key = internalId + "-" + zoneId;
   		return (Long)reservedIds.get(key);	
	}
	protected synchronized void setReservedId(String internalId, Long zoneId, Long id) {
		String key = internalId + "-" + zoneId;
   		reservedIds.put(key, id);	
	}
	
	/**
	 * Delete the binder object and its assocations.
	 * Child binders should already have been deleted
	 * This code assumes all users/groups are stored
	 */	
	public void delete(final ProfileBinder binder) {
		long begin = System.nanoTime();
		try {
			//cleanup entries - the delete of the folder in coreDao will handle associations through owningBinderId + LibraryEntries
		    getHibernateTemplate().execute(
		    	new HibernateCallback() {
		       		public Object doInHibernate(Session session) throws HibernateException {
	    	   			Statement s = null;
	       	   			try {
	       	   				s = session.connection().createStatement();
			   				String schema = ((SessionFactoryImplementor)session.getSessionFactory()).getSettings().getDefaultSchemaName();
			   				if (Validator.isNotNull(schema)) schema = schema+".";
			   				else schema = "";
	     	   				s.executeUpdate("delete from " + schema + "SS_WorkAreaFunctionMembers where memberId in " + 
	      	   						"(select p.id from " + schema + "SS_Principals p where  p.parentBinder=" + binder.getId() + ")");
	      	   				s.executeUpdate("delete from " + schema + "SS_Notifications where principalId in " + 
	      	   						"(select p.id from " + schema + "SS_Principals p where  p.parentBinder=" + binder.getId() + ")");
	     	   			} catch (SQLException sq) {
	       	   				throw new HibernateException(sq);
	      	   			} finally {
	       	   				try {if (s != null) s.close();} catch (Exception ex) {};
	       	   			}
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Membership where groupId in " +
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
				  				" p.parentBinder=:profile)")
					   			.setEntity("profile", binder)
		 		   				.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.Membership where userId in " +
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
				  				" p.parentBinder=:profile)")
					   			.setEntity("profile", binder)
	 	 		   				.executeUpdate();
	      	   			//If we are here the user space is being deleted, so get rid of things they own
			   			session.createQuery("Delete org.kablink.teaming.domain.UserProperties where zoneId=" + binder.getZoneId())
				         	.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.SeenMap where zoneId=" + binder.getZoneId())
				         	.executeUpdate();
			   			//delete ratings/visits owned or on hese principals
			   			session.createQuery("Delete org.kablink.teaming.domain.Rating where zoneId=" + binder.getZoneId())
			   				.executeUpdate();
			   			//delete subscriptions owned by these principals
			   			session.createQuery("Delete org.kablink.teaming.domain.Subscription where zoneId=" + binder.getZoneId())
			   				.executeUpdate();
			   			//delete tags owned or on these principals
			   			session.createQuery("Delete org.kablink.teaming.domain.Tag where zoneId=" + binder.getZoneId())
			   				.executeUpdate();
	
			   			//delete dahsboards in Zone = not totally correct, but if we are here the zone is being deleted
			   			session.createQuery("Delete org.kablink.teaming.domain.Dashboard where zoneId=" + binder.getZoneId())
			   				.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.EmailAddress where zoneId=" + binder.getZoneId())
			   				.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.SharedEntity where zoneId=" + binder.getZoneId())
		   				.executeUpdate();
			   		/*Pre zoneId on each item
	  
	 		   			session.createQuery("Delete org.kablink.teaming.domain.SeenMap where principalId in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
	 		   					" p.parentBinder=:profile)")
			   			  	.setEntity("profile", binder)
			   				.executeUpdate();
	
			   			session.createQuery("Delete org.kablink.teaming.domain.UserProperties where principalId in " +
				   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
				  				" p.parentBinder=:profile)")
	 				   			.setEntity("profile", binder)
	 				         	.executeUpdate();
			   			//delete ratings/visits owned by these principals
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Rating where principalId in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
			   			  			" p.parentBinder=:profile)")
					   		.setEntity("profile", binder)
			   				.executeUpdate();
			   			//delete subscriptions owned by these principals
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Subscription where principalId in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
			   			  			" p.parentBinder=:profile)")
					   		.setEntity("profile", binder)
			   				.executeUpdate();
	
			   			List types = new ArrayList();
		       			types.add(EntityIdentifier.EntityType.user.getValue());
		       			types.add(EntityIdentifier.EntityType.group.getValue());
	
		       			//delete subscriptions to these principals
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Subscription where entityId in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
			   			  			" p.parentBinder=:profile) and entityType in (:tList)")
					   		.setEntity("profile", binder)
		   			  		.setParameterList("tList", types)
			   				.executeUpdate();
	 	       			
			   			//delete ratings/visits for these principals
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Rating where entityId in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
			   			  			" p.parentBinder=:profile) and entityType in (:tList)")
				   			.setEntity("profile", binder)
		   			  		.setParameterList("tList", types)
		   			  		.executeUpdate();
		     	   		//delete dashboards owned
		     	   		session.createQuery("Delete org.kablink.teaming.domain.Dashboard where owner_id in " +
				   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
		   			  			" p.parentBinder=:profile) and owner_type in (:tList)")
	 			   			.setEntity("profile", binder)
		   					.setParameterList("tList", types)
		     	   			.executeUpdate();
			   			//delete tags owned by these users
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Tag where owner_id in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
			   			  			" p.parentBinder=:profile) and owner_type in (:tList)")
			   			  	.setEntity("profile", binder)
			   			  	.setParameterList("tList", types)
			   				.executeUpdate();
		       			
			   			//delete tags for these principals - not likely to exist
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Tag where entity_id in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
				  			" p.parentBinder=:profile) and entity_type in (:tList)")
			   			  	.setEntity("profile", binder)
			   			  	.setParameterList("tList", types)
			   				.executeUpdate();
	 */
			   			session.createQuery("Update org.kablink.teaming.domain.Principal set creation.principal=null, modification.principal=null " +
	 		   					"where parentBinder=:profile")
	 		   			  	.setEntity("profile", binder)
			   				.executeUpdate();
	 		   			session.createQuery("Update org.kablink.teaming.domain.Binder set creation.principal=null, modification.principal=null, owner=null, entryDef=null " +
		   					"where id=" + binder.getId())
		   			  		.executeUpdate();
	 		   			
	 		   			//the delete of the binder in coreDao will handle associations through owningBinderId + LibraryEntries + entries
	 		   			getCoreDao().delete((Binder)binder, Principal.class);			
		       	   		return null;
		       		}
		       	}
		    );  
    	}
    	finally {
    		end(begin, "delete(ProfileBinder)");
    	}	        
	    	
	 }
	 /**
     * Delete an object and its assocations 
     * @param entry
     */
    public void delete(Principal entry) {
		long begin = System.nanoTime();
		try {
	    	List entries = new ArrayList();
	    	entries.add(entry);
	    	deleteEntries(entries);
    	}
    	finally {
    		end(begin, "delete(Principal)");
    	}	        
             	
     } 
    /*
     * Delete a list of entries
     * We only actually delete groups.  Users are left forever, but
     * there associations are deleted
     */
    public void deleteEntries(final Collection<Principal> entries) {
		long begin = System.nanoTime();
		try {
	    	if (entries == null || entries.size() == 0) return;
	      	getHibernateTemplate().execute(
	        	   	new HibernateCallback() {
	        	   		public Object doInHibernate(Session session) throws HibernateException {
	        	   	   	Set ids = new HashSet();
	           			StringBuffer inList = new StringBuffer();
	        			for (Principal p: entries) {
	        	    		ids.add(p.getId());
	        	    		inList.append(p.getId().toString() + ",");
	        	    	}
	        			inList.deleteCharAt(inList.length()-1);
	        			//these associations are link tables, no class exists to access them
	     	   			Statement s = null;
	       	   			try {
	       	   				s = session.connection().createStatement();
			   				String schema = ((SessionFactoryImplementor)session.getSessionFactory()).getSettings().getDefaultSchemaName();
			   				if (Validator.isNotNull(schema)) schema = schema+".";
			   				else schema = "";
	      	   				s.executeUpdate("delete from " + schema + "SS_WorkAreaFunctionMembers where memberId in (" + inList.toString() + ")");
	      	   				s.executeUpdate("delete from " + schema + "SS_Notifications where principalId in (" + inList.toString() + ")");
	       	   			} catch (SQLException sq) {
	       	   				throw new HibernateException(sq);
	     	   			} finally {
	       	   				try {if (s != null) s.close();} catch (Exception ex) {};
	       	   			}
	        			//need to use ownerId, cause versionattachments/customattributeList sets not indexed by principal
			   			getCoreDao().deleteEntityAssociations("ownerId in (" + inList.toString() + ") and ownerType in  ('" +
			   					EntityType.user.name() + "','" + 
			   					EntityType.group.name() + "','" +
			   					EntityType.application.name() +"','" + 
			   					EntityType.applicationGroup.name() + "')");
	 		   			session.createQuery("Delete org.kablink.teaming.domain.SeenMap where principalId in (:pList)")
			   				.setParameterList("pList", ids)
	       	   				.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.Membership where userId in (:uList) or groupId in (:gList)")
			   				.setParameterList("uList", ids)
			   				.setParameterList("gList", ids)
	       	   				.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.UserProperties where principalId in (:pList)")
		   					.setParameterList("pList", ids)
		   					.executeUpdate();
	
			   			//delete ratings/visits owned by these users
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Rating where principalId in (:pList)")
		   					.setParameterList("pList", ids)
			   				.executeUpdate();
	
			   			//delete subscriptions owned by these users
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Subscription where principalId in (:pList)")
		   					.setParameterList("pList", ids)
			   				.executeUpdate();
	
			   			//delete email addresses associated with these users
	 		   			session.createQuery("Delete org.kablink.teaming.domain.EmailAddress where principal in (:pList)")
		   					.setParameterList("pList", entries)
			   				.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.SharedEntity where accessId in (:pList) and accessType=:accessType")
		   					.setParameterList("pList", ids)
		   				.setParameter("accessType", SharedEntity.ACCESS_TYPE_PRINCIPAL)
		   				.executeUpdate();
	
	 		   			List types = new ArrayList();
		       			types.add(EntityIdentifier.EntityType.user.name());
		       			types.add(EntityIdentifier.EntityType.group.name());
		       			types.add(EntityIdentifier.EntityType.application.name());
		       			types.add(EntityIdentifier.EntityType.applicationGroup.name());
	      	   			//delete workflow history
			   			session.createQuery("Delete org.kablink.teaming.domain.WorkflowHistory where entityId in (:pList) and entityType in (:tList)")
	 	   					.setParameterList("pList", ids)
		   					.setParameterList("tList", types)
			   				.executeUpdate();
			   			types.clear();
		       			types.add(EntityIdentifier.EntityType.user.getValue());
		       			types.add(EntityIdentifier.EntityType.group.getValue());
		       			types.add(EntityIdentifier.EntityType.application.getValue());
		       			types.add(EntityIdentifier.EntityType.applicationGroup.getValue());
		       			//delete subscriptions to these principals - not likely to exist
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Subscription where entityId in (:pList) and entityType in (:tList)")
	 	   					.setParameterList("pList", ids)
		   					.setParameterList("tList", types)
			   				.executeUpdate();
	 	       			
			   			//delete ratings/visits at these principals - not likely to exist
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Rating where entityId in (:pList) and entityType in (:tList)")
	 	   					.setParameterList("pList", ids)
		   					.setParameterList("tList", types)
			   				.executeUpdate();
		     	   		//delete dashboards owned
		     	   		session.createQuery("Delete org.kablink.teaming.domain.Dashboard where owner_id in (:pList) and owner_type in (:tList)")
	 	   					.setParameterList("pList", ids)
		   					.setParameterList("tList", types)
		     	   			.executeUpdate();
	
	 		   			//delete tags owned by this entity
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Tag where owner_id in (:pList) and owner_type in (:tList)")
		   					.setParameterList("pList", ids)
		   					.setParameterList("tList", types)
		   					.executeUpdate();
	 		   			//delete tags on this entity - not likely to exist
			   			session.createQuery("Delete org.kablink.teaming.domain.Tag where entity_id in (:pList) and entity_type in (:tList)")
	   						.setParameterList("pList", ids)
	   						.setParameterList("tList", types)
	   						.executeUpdate();
			   			
	
	 		   			//only delete groups,applications; leave users around
	 		   			types.clear();
		       			types.add(EntityIdentifier.EntityType.group.name());
		       			types.add(EntityIdentifier.EntityType.application.name());
		       			types.add(EntityIdentifier.EntityType.applicationGroup.name());
			   			session.createQuery("Delete org.kablink.teaming.domain.Principal where id in (:pList) and type in (:tList)")
	            			.setParameterList("pList", ids)
	   						.setParameterList("tList", types)
	      	   				.executeUpdate();
			   			
	 		   			//leave users around; just mark deleted
	 		   			session.createQuery("update org.kablink.teaming.domain.Principal set deleted=:deleted where id in (:pList) and type='user'")
	            			.setBoolean("deleted", Boolean.TRUE)
	            			.setParameterList("pList", ids)
	       	   				.executeUpdate();
			   			//this flushes secondary cache
			   			session.getSessionFactory().evict(Principal.class);			   			
			   			session.getSessionFactory().evictCollection("org.kablink.teaming.domain.UserPrincipal.memberOf");
			   			session.getSessionFactory().evictCollection("org.kablink.teaming.domain.ApplicationPrincipal.memberOf");
	      	   				
	           	   		return null;
	        	   		}
	        	   	}
	        	 );    	
    	}
    	finally {
    		end(begin, "deleteEntries(Collection<Principal>)");
    	}	        
       	
    }
        
    public void disablePrincipals(final Collection<Long> ids, final Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	getHibernateTemplate().execute(
	        	new HibernateCallback() {
	        		public Object doInHibernate(Session session) throws HibernateException {
	        			session.createQuery("UPDATE org.kablink.teaming.domain.Principal set disabled = :disable where zoneId = :zoneId and internalId is null and id in (:pList)")
	        			.setBoolean("disable", true)
	        			.setLong("zoneId", zoneId)
	        			.setParameterList("pList", ids)
	        			.executeUpdate();
	        			return null;
	        		}
	        	}
	        );
    	}
    	finally {
    		end(begin, "disablePrincipals(Collection<Long>,Long)");
    	}	        

    }
    //used for login
    public User findUserByName(final String userName, String zoneName) {
		long begin = System.nanoTime();
		try {
	    	final Binder top = getCoreDao().findTopWorkspace(zoneName);
	    	return findUserByName(userName, top.getId());
    	}
    	finally {
    		end(begin, "findUserByName(String,String)");
    	}	        
    }
    
    public User findUserByNameDeadOrAlive(final String userName, String zoneName) {
		long begin = System.nanoTime();
		try {
	    	final Binder top = getCoreDao().findTopWorkspace(zoneName);
	    	return findUserByNameDeadOrAlive(userName, top.getId());
    	}
    	finally {
    		end(begin, "findUserByNameDeadOrAlive(String,String)");
    	}	        
    }
    
 	public User findUserByName(final String userName, final Long zoneId) 
	throws NoUserByTheNameException {
	   long begin = System.nanoTime();
	   try {
	       User user = (User)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                 	   //only returns active users
	                 	   User user = (User)session.getNamedQuery("find-User-Company")
	                             		.setString(ParameterNames.USER_NAME, userName.toLowerCase())
	                             		.setLong(ParameterNames.ZONE_ID, zoneId)
	                             		.setCacheable(isPrincipalQueryCacheable())
	                             		.uniqueResult();
	                        //query ensures user is not deleted and not disabled
	                 	   if (user == null) {
	                            throw new NoUserByTheNameException(userName); 
	                        }
	                        return user;
	                    }
	                }
	             );		
	        user = (User) filterInaccessiblePrincipal(user);
	        if (user == null) 
	        	throw new NoUserByTheNameException(userName); 
	        return user;
	   	}
	   	finally {
	   		end(begin, "findUserByName(String,Long)");
	   	}	        
 	}

 	/**
 	 * Find an user in the ss_principals table using the ldap guid.
 	 */
 	public User findUserByLdapGuid( final String ldapGuid, final Long zoneId ) 
		throws NoPrincipalByTheNameException
	{
	   long begin = System.nanoTime();
	   try
	   {
	       User user = (User)getHibernateTemplate().execute(
                new HibernateCallback()
                {
                    public Object doInHibernate(Session session) throws HibernateException
                    {
                 	   //only returns active users
                 	   User user = (User)session.getNamedQuery( "find-User-By-LdapGuid-Company" )
                             		.setString( ParameterNames.LDAP_UGID, ldapGuid )
                             		.setLong( ParameterNames.ZONE_ID, zoneId )
                             		.setCacheable( isPrincipalQueryCacheable() )
                             		.uniqueResult();
                 	   
                        //query ensures user is not deleted and not disabled
                 	   if ( user == null )
                 	   {
                 		   throw new NoUserByTheNameException( ldapGuid ); 
                       }
                       
                 	   return user;
                    }
                }
             );		

	       user = (User) filterInaccessiblePrincipal(user);
	       if (user == null) 
	    	   throw new NoUserByTheNameException( ldapGuid ); 

	       return user;
	   }
	   finally
	   {
		   end(begin, "findUserByLdapGuid(String,Long)");
	   }	        
	}// end findUserByLdapGuid()

 	public Principal findPrincipalByName(final String name, final Long zoneId) 
 		throws NoPrincipalByTheNameException {
 		if(name.startsWith(FAKE_NAME_PREFIX)) {
 			Long id = Long.valueOf(name.substring(FAKE_NAME_PREFIX.length()));
 			if(logger.isDebugEnabled())
 				logger.debug("Turning findPrincipalByName with '" + name + "' into loadPrincipal with '" + id + "'");
 			return loadPrincipal(id, zoneId, false);
 		}
 		else {
			long begin = System.nanoTime();
			try {
		        Principal principal = (Principal)getHibernateTemplate().execute(
		                new HibernateCallback() {
		                    public Object doInHibernate(Session session) throws HibernateException {
		                 	   //only returns active users
		                    	Principal p= (Principal)session.getNamedQuery("find-Principal-Company")
		                             		.setString(ParameterNames.USER_NAME, name.toLowerCase())
		                             		.setLong(ParameterNames.ZONE_ID, zoneId)
		                             		.setCacheable(isPrincipalQueryCacheable())
		                             		.uniqueResult();
		                        //query ensures user is not deleted and not disabled
		                 	   if (p == null) {
		                            throw new NoPrincipalByTheNameException(name); 
		                 	   }
		                 	   return resolveProxy(session, p);
		                    }
		                }
		             );
		        principal = filterInaccessiblePrincipal(principal);
		        if (principal == null) 
		        	throw new NoPrincipalByTheNameException(name); 
		        return principal;
	    	}
	    	finally {
	    		end(begin, "findPrincipalByName(String,Long)");
	    	}	
 		}
 	}
 	private Principal resolveProxy(Session session, Principal proxy) {
        Principal p = null;
        try {
       		p = (Principal)session.get(User.class, proxy.getId());
            if (p != null) return  p;
       } catch (Exception ex) {};  // proxies will force an exception, didn't expect with session.get?
       try {
    	   p = (Principal)session.get(Application.class, proxy.getId());
           if (p != null) return  p;
       } catch (Exception ex) {};
       try {
    	   p = (Principal)session.get(Group.class, proxy.getId());
           if (p != null) return  p;
       } catch (Exception ex) {};
       
       return (Principal)session.get(ApplicationGroup.class, proxy.getId());
 	}

    public ProfileBinder getProfileBinder(Long zoneId) {
		long begin = System.nanoTime();
		try {
			return (ProfileBinder)getCoreDao().loadReservedBinder(ObjectKeys.PROFILE_ROOT_INTERNALID, zoneId);
    	}
    	finally {
    		end(begin, "getProfileBinder(Long)");
    	}	        
    }

    /*
     *  (non-Javadoc)
     * @see org.kablink.teaming.dao.CoreDao#loadPrincipal(java.lang.Long, java.lang.Long)
     */
    public UserPrincipal loadUserPrincipal(final Long prinId, final Long zoneId, final boolean checkActive) {
		long begin = System.nanoTime();
		try {
	        UserPrincipal principal = (UserPrincipal)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                    	//hoping for cache hit
	                    	UserPrincipal principal = (UserPrincipal)session.get(UserPrincipal.class, prinId);
	                        if (principal == null) {throw new NoPrincipalByTheIdException(prinId);}
	                        //Get the real object, not a proxy to abstract class
	                        try {
	                        	principal = (UserPrincipal)session.get(User.class, prinId);
	                        } catch (Exception ex) {};  // group proxies will force an exception, didn't expect with session.get? 
	                        if (principal==null) 
	                            principal = (UserPrincipal)session.get(Group.class, prinId);
	                        //make sure from correct zone
	                        if (!principal.getZoneId().equals(zoneId) ||
	                        		(checkActive && !principal.isActive())) {throw new NoPrincipalByTheIdException(prinId);}
	                        return principal;
	                    }
	                }
	        );
	       
	        principal = (UserPrincipal)filterInaccessiblePrincipal(principal);
	        if (principal == null) throw new NoPrincipalByTheIdException(prinId); 
	        return principal;
    	}
    	finally {
    		end(begin, "loadUserPrincipal(Long,Long,boolean)");
    	}	        
              
    }
    public List<UserPrincipal> loadUserPrincipals(final Collection ids, final Long zoneId, boolean checkActive) {
		long begin = System.nanoTime();
		try {
	    	List<UserPrincipal> result = loadPrincipals(ids, zoneId, UserPrincipal.class, true, checkActive);
			//remove proxies
			for (int i=0; i<result.size(); ++i) {
				UserPrincipal p = result.get(i);
				if (!(p instanceof User) && !(p instanceof Group)) {
					UserPrincipal principal = null;
					try {
						principal = (UserPrincipal)getHibernateTemplate().get(User.class, p.getId());
					} catch(Exception e) {}; // get throws exception rather than returning null if the object was already loaded into Hibernate as proxy and it is of different type
					if (principal==null) principal = (UserPrincipal)getHibernateTemplate().get(Group.class, p.getId());
	   				result.set(i, principal);
	            }
	        }
			return result;
    	}
    	finally {
    		end(begin, "loadUserPrincipals(Collection,Long,boolean)");
    	}	        
    }
    public List<Principal> loadPrincipalByEmail(final String email, final String emailType, final Long zoneId) {
		long begin = System.nanoTime();
		try {
	        List result = (List)getHibernateTemplate().execute(
	               	new HibernateCallback() {
	                		public Object doInHibernate(Session session) throws HibernateException {
	                			String queryStr = "select x.principal from org.kablink.teaming.domain.EmailAddress x where " +
	          					"lower(x.address)=:address";
	                			if(Validator.isNotNull(emailType))
	                				queryStr += " and x.type=:type";
	    	                  	Query q = session.createQuery(queryStr);
	    	                  	q.setParameter("address", email.toLowerCase());
	                			if(Validator.isNotNull(emailType))
	                				q.setParameter("type", emailType);
	    	                  	List result = q.list();
	    	                  	for (int i=0; i<result.size();) { //check zone here, so index is used
	    	                  		if (!((Principal)result.get(i)).getZoneId().equals(zoneId)) result.remove(i);
	    	                  		else ++i;
	    	                  	}
	    	                  	return result;
	                		}
	               	}
	            );
	        return filterInaccessiblePrincipals(result);
    	}
    	finally {
    		end(begin, "loadPrincipalByEmail(String,String,Long)");
    	}	        
        	   	
    }
    private List loadPrincipals(final Collection ids, final Long zoneId, final Class clazz, final boolean cacheable, final boolean checkActive) {
        if ((ids == null) || ids.isEmpty()) return new ArrayList();
        List result = (List)getHibernateTemplate().execute(
           	new HibernateCallback() {
            		public Object doInHibernate(Session session) throws HibernateException {
       					//some databases restrict the size of the inList
            			int useLimit = inClauseLimit-3;  //account for the 3 we add
            			if (ids.size() <= useLimit) {
           					Criteria crit = session.createCriteria(clazz)
        					.add(Expression.in(Constants.ID, ids))
        					.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
        					.setFetchMode("emailAddresses", FetchMode.JOIN)
                    		// Unlike some other query caches used for reference type objects,
                    		// this cache is not very useful in the sense that the result of
                    		// this query is very unlikely to be shared across users.
                    		// However, some WebDAV usage patterns make it useful because it
                    		// can repeatedly asks for the same set of information (for a 
                    		// request from the same user). We can use this as a short-lived
                    		// temporary cache. 
                    		.setCacheable(cacheable);
        					if (checkActive) {
        						crit.add(Expression.eq(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE));
        						crit.add(Expression.eq(ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.FALSE));
        					}  
        					List result = crit.list();
                            //eagar select results in duplicates
                            Set res = new HashSet(result);
                            result.clear();
                            result.addAll(res);
        					return result;
            			} else {
            				List idList = new ArrayList(ids);
            				Set results = new HashSet(); //fetch returns duplicates, se weed them out
            				for (int i=0; i<idList.size(); i+=useLimit) {
            					List subList = idList.subList(i, Math.min(idList.size(), i+useLimit));
            					Criteria crit = session.createCriteria(clazz)
            					.add(Expression.in(Constants.ID, subList))
            					.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
            					.setFetchMode("emailAddresses", FetchMode.JOIN)
            					// Unlike some other query caches used for reference type objects,
                        		// this cache is not very useful in the sense that the result of
                        		// this query is very unlikely to be shared across users.
                        		// However, some WebDAV usage patterns make it useful because it
                        		// can repeatedly asks for the same set of information (for a 
                        		// request from the same user). We can use this as a short-lived
                        		// temporary cache. 
                        		.setCacheable(cacheable);
            					if (checkActive) {
            						crit.add(Expression.eq(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE));
            						crit.add(Expression.eq(ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.FALSE));
            					}            				
            					results.addAll(crit.list());
            				}
            				return new ArrayList(results);
            			}
            		}
           	}
        );
        return filterInaccessiblePrincipals(result);
    	
    }
    private List loadPrincipals(final FilterControls filter, Long zoneId, final Class clazz) throws DataAccessException { 
       	filter.add(ObjectKeys.FIELD_ZONE, zoneId);
    	filter.add(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE);
    	filter.add(ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.FALSE);
        List result = (List)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        //sqlqueries, filters and criteria don't help with frontbase problem
                        //
                        Query query = session.createQuery("from " + clazz.getName() + " u " + filter.getFilterString("u"));
                		List filterValues = filter.getFilterValues();
               			for (int i=0; i<filterValues.size(); ++i) {
                			query.setParameter(i, filterValues.get(i));
                		}
               			
                       return query.list();
                    }
                }
            );  
        return filterInaccessiblePrincipals(result);
    }    
  
	public Group loadGroup(final Long groupId, Long zoneId)  {
		long begin = System.nanoTime();
		try {
			Group group = (Group)getHibernateTemplate().get(Group.class, groupId);
			if (group == null) {throw new NoGroupByTheIdException(groupId);}
			//make sure from correct zone
			if (!group.getZoneId().equals(zoneId) || !group.isActive()) {throw new NoGroupByTheIdException(groupId);}
			return group;
		} catch (ClassCastException ce) {
			throw new NoGroupByTheIdException(groupId);
		}
		finally {
			end(begin, "loadGroup(Long,Long)");
		}	        
	}
	public ApplicationGroup loadApplicationGroup(final Long groupId, Long zoneId)  {
		long begin = System.nanoTime();
		try {
			ApplicationGroup group = (ApplicationGroup)getHibernateTemplate().get(ApplicationGroup.class, groupId);
			if (group == null) {throw new NoGroupByTheIdException(groupId);}
			//make sure from correct zone
			if (!group.getZoneId().equals(zoneId) || !group.isActive()) {throw new NoGroupByTheIdException(groupId);}
			return group;
		} catch (ClassCastException ce) {
			throw new NoGroupByTheIdException(groupId);
		}
		finally {
			end(begin, "loadApplicationGroup(Long,Long)");
		}	        
	}
	public List<Group> loadGroups(Collection<Long> ids, Long zoneId) {
		long begin = System.nanoTime();
		try {
			return loadPrincipals(ids, zoneId, Group.class, false, true);
    	}
    	finally {
    		end(begin, "loadGroups(Collection<Long>,Long)");
    	}	        
	}

    public List<Group> loadGroups(FilterControls filter, Long zoneId) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
			return loadPrincipals(filter, zoneId, Group.class);
    	}
    	finally {
    		end(begin, "loadGroups(FilterControls,Long)");
    	}	        
    }  
	public User loadUser(Long userId, String zoneName) {
		long begin = System.nanoTime();
		try {
			Binder top = getCoreDao().findTopWorkspace(zoneName);
			return loadUser(userId, top.getZoneId());
    	}
    	finally {
    		end(begin, "loadUser(Long,String)");
    	}	        
    }
    public User loadUser(Long userId, Long zoneId) {
		long begin = System.nanoTime();
    	try {
    		User user = (User)getHibernateTemplate().get(User.class, userId);
    		if (user == null) {throw new NoUserByTheIdException(userId);}
    		//	make sure from correct zone
    		if (!user.getZoneId().equals(zoneId) || !user.isActive()) {
    			throw new NoUserByTheIdException(userId);
    		}
    		user = (User) filterInaccessiblePrincipal(user);
            if (user == null) 
            	throw new NoUserByTheIdException(userId); 
    		return user;
    	} catch (ClassCastException ce) {
   			throw new NoUserByTheIdException(userId);   		
    	}
		finally {
			end(begin, "loadUser(Long,Long)");
		}	        
    }
    public User loadUserDeadOrAlive(Long userId, Long zoneId) {
		long begin = System.nanoTime();
    	// STOP!!! THIS METHOD IS FOR INTERNAL/SPECIAL USE ONLY. USE THE REGULAR loadUser() METHOD INSTEAD.
    	try {
    		User user = (User)getHibernateTemplate().get(User.class, userId);
    		if (user == null) {throw new NoUserByTheIdException(userId);}
    		//	make sure from correct zone
    		if (!user.getZoneId().equals(zoneId)) {
    			throw new NoUserByTheIdException(userId);
    		}
    		return user;
    	} catch (ClassCastException ce) {
   			throw new NoUserByTheIdException(userId);   		
    	}
		finally {
			end(begin, "loadUserDeadOrAlive(Long,Long)");
		}	        
    }
    
	public List<User> loadUsers(Collection<Long> ids, Long zoneId) {
		long begin = System.nanoTime();
		try {
			return loadPrincipals(ids, zoneId, User.class, false, true);
    	}
    	finally {
    		end(begin, "loadUsers(Collection<Long>,Long)");
    	}	        
    }
    public List<User> loadUsers(FilterControls filter, Long zoneId) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
			return loadPrincipals(filter, zoneId, User.class);
    	}
    	finally {
    		end(begin, "loadUsers(FilterControls,Long)");
    	}	        
    }

     
    public SFQuery queryUsers(FilterControls filter, Long zoneId) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
			return queryPrincipals(filter, zoneId, User.class);
    	}
    	finally {
    		end(begin, "queryUsers(FilterControls,Long)");
    	}	        
    }
    public SFQuery queryGroups(FilterControls filter, Long zoneId) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
			return queryPrincipals(filter, zoneId, Group.class);
    	}
    	finally {
    		end(begin, "queryGroups(FilterControls,Long)");
    	}	        
    }  
    public SFQuery queryAllPrincipals(FilterControls filter, Long zoneId) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
			return queryPrincipals(filter, zoneId, Principal.class);
    	}
    	finally {
    		end(begin, "queryAllPrincipals(FilterControls,Long)");
    	}	        
    }
    
    private SFQuery queryPrincipals(FilterControls filter, Long zoneId, final Class clazz) throws DataAccessException { 
		final FilterControls myFilter = filter==null?new FilterControls():filter;
		if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
		myFilter.add(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE);
		myFilter.add(ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.FALSE);
        Query query = (Query)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        //sqlqueries, filters and criteria don't help with frontbase problem
                        //
                        Query query = session.createQuery("from " + clazz.getName() + " u " + myFilter.getFilterString("u"));
                		List filterValues = myFilter.getFilterValues();
               			for (int i=0; i<filterValues.size(); ++i) {
                			query.setParameter(i, filterValues.get(i));
                		}
                       return query;
                    }
                }
            );  
       return new SFQuery(query);
    }    
 	public void bulkLoadCollections(final Collection<Principal> entries) {
		long begin = System.nanoTime();
		try {
	 		if (entries.size() > inClauseLimit) throw new IllegalArgumentException("Collection to large");
	  	    getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                	//ids will be sorted
	                 	TreeSet<Principal> sorted = new TreeSet(new LongIdComparator());
	      		    	sorted.addAll(entries);
	                   	List<Long> ids = new ArrayList();
	      		    	//ids will now be in order
	      		    	for (Principal p : sorted) {
	      		    		ids.add(p.getId());
	       		    	}
	      		    	List<EmailAddress> objs = session.createCriteria(EmailAddress.class)
	      		    		.add(Expression.in("principal", entries))
	      		    		.addOrder(Order.asc("principal"))
	      		    		.list();
	      		    	//now build EMAIL collection
	 		    		HashSet tSet = new HashSet();
	 		    		for (Principal p:sorted) {
	      		    		HashMap tMap = new HashMap();
	      		    		for (EmailAddress email:objs) {
	      		    			if (p.equals(email.getPrincipal())) {
	      		    				tMap.put(email.getType(), email);
	      		    				tSet.add(email);    
	      		    			} else break;
	      		    		}
	      		    		p.setIndexEmailAddresses(tMap);
	      		    		objs.removeAll(tSet);
	      		    		tSet.clear();
	      		    	}
	     		    	
	      		    	/* not cacheing membership
	        	    	List<Membership> result = session.createCriteria(Membership.class)
	                 		.add(Expression.in("userId", ids))
							.addOrder(Order.asc("userId"))
							.list();
	       		    	//now build memberOf collection
	       		    	for (Long pId: ids) {
			    			try {
			    				//skip if it is a group
			       				User u = (User)getCoreDao().load(User.class, pId);
			       				List groups = new ArrayList();
			       				while (!result.isEmpty()) {
			       					Membership m = result.get(0);
			       					if (m.getUserId().equals(pId)) {
			       						try {
			       							// skip if error
			       							groups.add(getCoreDao().load(Group.class, m.getGroupId()));
			       						} finally {
			       							//clear up memory
			       							getCoreDao().evict(m);
			       							result.remove(0);
			       						}
			       					} else {break;}
			       				}
			       				u.setIndexMemberOf(groups);
			    			} catch (Exception ex) {
			    				//remove membership if belongs to this it
			    				while (!result.isEmpty() && pId.equals(result.get(0).getUserId())) {
			    					result.remove(0);
			    				}
			    			};
	      		    		
	       		    	}
	*/
						return null;
	                 }
	            }
	        );
	
	 		getCoreDao().bulkLoadCollections(entries);
    	}
    	finally {
    		end(begin, "bulkLoadCollections(Collection<Principal>)");
    	}	        
		
	}
 	
    public UserProperties loadUserProperties(Long userId) {
		long begin = System.nanoTime();
		try {
	    	UserPropertiesPK id = new UserPropertiesPK(userId);
	    	UserProperties uProps=null;
	    	try {
	    		uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);         
	 		} catch (HibernateSystemException se) {
	    		//old dom trees cause errors with the new dom4j.  These properties should be stored as strings, and
	    		//this problem was fixed before ship
	    		if (se.getRootCause() instanceof java.io.InvalidClassException || 
	    				se.getRootCause() instanceof ClassNotFoundException) {
					//bad serialized data - get rid of it
	    			executePropertiesDelete(id);
				} 
	    		else if(se.getRootCause() instanceof java.io.EOFException) {
	    			// the data in the blob is corrupt; this most likely means last update failed because
	    			// the data exceeded the size limit - there's no way we can fix up this data, get rid of it
	    			// (this is meant to be one time only cleanup on a system that already has corrupt lobs)
	    			logger.error("User properties for " + id.toString() + " seems corrupt: Resetting it to empty value", se);
	    			executePropertiesDelete(id);	    			
	    		}
	    		else {
	    			throw se;
	    		}
			}
	   		if (uProps == null) {
				uProps = new UserProperties(id);
				try {
					uProps=(UserProperties)getCoreDao().saveNewSession(uProps);
				} catch (Exception ex) {
					//contension
					uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);   
				}
			}
			return uProps;
    	}
    	finally {
    		end(begin, "loadUserProperties(Long)");
    	}	        
    }
 
    public UserProperties loadUserProperties(Long userId, Long binderId) {
		long begin = System.nanoTime();
		try {
	    	UserPropertiesPK id = new UserPropertiesPK(userId, binderId);
	    	UserProperties uProps=null;
	    	try {
	    		uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);
	    	} catch (HibernateSystemException se) {
	    		//old dom trees cause errors with the new dom4j.  These properties should be stored as strings, and
	    		//	this problem was fixed before ship
	    		if (se.getRootCause() instanceof java.io.InvalidClassException || 
	    				se.getRootCause() instanceof ClassNotFoundException) {
	    			//	bad serialized data - get rid of it
	    			executePropertiesDelete(id);
	    		} 
	    		else if(se.getRootCause() instanceof java.io.EOFException) {
	    			// the data in the blob is corrupt; this most likely means last update failed because
	    			// the data exceeded the size limit - there's no way we can fix up this data, get rid of it
	    			// (this is meant to be one time only cleanup on a system that already has corrupt lobs)
	    			logger.error("User properties for " + id.toString() + " seems corrupt: Resetting it to empty value", se);
	    			executePropertiesDelete(id);	    			
	    		}
	    		else {
	    			throw se;
	    		}
	    	}
			if (uProps == null) {
	        	uProps = new UserProperties(id);
	        	//quick write
	        	try {
	        		uProps=(UserProperties)getCoreDao().saveNewSession(uProps);
	   			} catch (Exception ex) {
	   				//contension?
	   				uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);
	   			}
	    	}
			return uProps;
    	}
    	finally {
    		end(begin, "loadUserProperties(Long,Long)");
    	}	        
    }
	//At one point dom4j (1.5) objects where serialized,  donm4j(1.6) does not recognize them
	//Somewhere along the way we changed it so we saved the string value instead of the serialized object, but 
	//occasionally they pop up at home.
	private void executePropertiesDelete(final UserPropertiesPK id) {
		//need to execute in new session, so it gets commited
       	SessionFactory sf = getSessionFactory();
    	Session session = sf.openSession();
		Statement statement = null;
    	try {
    		String schema = ((SessionFactoryImplementor)session.getSessionFactory()).getSettings().getDefaultSchemaName();
    		if (Validator.isNotNull(schema)) schema = schema+".";
    		else schema = "";
    		statement = session.connection().createStatement();
    		statement.executeUpdate("delete from " + schema + "SS_UserProperties where principalId=" + id.getPrincipalId() + 
    				" and binderId=" + id.getBinderId());
	   	} catch (SQLException sq) {
	    		throw new HibernateException(sq);
	   	} finally {
	   		try {if (statement != null) statement.close();} catch (Exception ex) {};
	   		session.close();
	   	}

	}
	private void executeSeenmapDelete(final Long userId) {
		//need to execute in new session, so it gets commited
       	SessionFactory sf = getSessionFactory();
    	Session session = sf.openSession();
		Statement statement = null;
    	try {
    		String schema = ((SessionFactoryImplementor)session.getSessionFactory()).getSettings().getDefaultSchemaName();
    		if (Validator.isNotNull(schema)) schema = schema+".";
    		else schema = "";
    		statement = session.connection().createStatement();
    		statement.executeUpdate("delete from " + schema + "SS_SeenMap where principalId=" + userId);
	   	} catch (SQLException sq) {
	    		throw new HibernateException(sq);
	   	} finally {
	   		try {if (statement != null) statement.close();} catch (Exception ex) {};
	   		session.close();
	   	}

	}
    public Group getReservedGroup(String internalId, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	Long id = getReservedId(internalId, zoneId);
	    	if (id == null) {
	    		List<Group>objs = getCoreDao().loadObjects(Group.class, new FilterControls(ObjectKeys.FIELD_INTERNALID, internalId), zoneId);
	    		if ((objs == null) || objs.isEmpty()) throw new NoGroupByTheNameException(internalId);
	    		Group g = objs.get(0);
	    		setReservedId(internalId, zoneId, g.getId());
	    		return g;
	    	}
	    	return loadGroup(id, zoneId);
    	}
    	finally {
    		end(begin, "getReservedGroup(String,Long)");
    	}	        
    }
    public Long getReservedGroupId(String internalId, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	Long id = getReservedId(internalId, zoneId);
	    	if (id == null) {
	    		List<Group>objs = getCoreDao().loadObjects(Group.class, new FilterControls(ObjectKeys.FIELD_INTERNALID, internalId), zoneId);
	    		if ((objs == null) || objs.isEmpty()) throw new NoGroupByTheNameException(internalId);
	    		Group g = objs.get(0);
	    		setReservedId(internalId, zoneId, g.getId());
	    		return g.getId();
	    	}
	    	return id;
    	}
    	finally {
    		end(begin, "getReservedGroup(String,Long)");
    	}	        
    }
    public ApplicationGroup getReservedApplicationGroup(String internalId, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	Long id = getReservedId(internalId, zoneId);
	    	if (id == null) {
	    		List<ApplicationGroup>objs = getCoreDao().loadObjects(ApplicationGroup.class, new FilterControls(ObjectKeys.FIELD_INTERNALID, internalId), zoneId);
	    		if ((objs == null) || objs.isEmpty()) throw new NoGroupByTheNameException(internalId);
	    		ApplicationGroup g = objs.get(0);
	    		setReservedId(internalId, zoneId, g.getId());
	    		return g;
	    	}
	    	return loadApplicationGroup(id, zoneId);
    	}
    	finally {
    		end(begin, "getReservedApplicationGroup(String,Long)");
    	}	        
    }
    public User getReservedUser(String internalId, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	Long id = getReservedId(internalId, zoneId);
	    	if (id == null) {
	    		List<User>objs = getCoreDao().loadObjects(User.class, new FilterControls(ObjectKeys.FIELD_INTERNALID, internalId), zoneId);
	    		if ((objs == null) || objs.isEmpty()) throw new NoUserByTheNameException(internalId);
	    		User u = objs.get(0);
	    		setReservedId(internalId, zoneId, u.getId());
	    		if (!u.isActive())
	    			throw new NoUserByTheIdException(u.getId());
	    		return u;
	    	}
	    	return loadUser(id, zoneId);
    	}
    	finally {
    		end(begin, "getReservedUser(String,Long)");
    	}	        
   }
    public User getReservedUserDeadOrAlive(String internalId, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	Long id = getReservedId(internalId, zoneId);
	    	if (id == null) {
	    		List<User>objs = getCoreDao().loadObjects(User.class, new FilterControls(ObjectKeys.FIELD_INTERNALID, internalId), zoneId);
	    		if ((objs == null) || objs.isEmpty()) throw new NoUserByTheNameException(internalId);
	    		User u = objs.get(0);
	    		setReservedId(internalId, zoneId, u.getId());
	    		return u;
	    	}
	    	return loadUserDeadOrAlive(id, zoneId);
    	}
    	finally {
    		end(begin, "getReservedUserDeadOrAlive(String,Long)");
    	}	        
   }
    
    public Set<Long> getPrincipalIds(Principal p) {
		long begin = System.nanoTime();
		try {
	    	if (p instanceof IndividualPrincipal) {
		    	if(((IndividualPrincipal)p).isAllIndividualMember()) {
		        	GroupPrincipal gp;
		        	if(p instanceof User)
		        		gp = getReservedGroup(ObjectKeys.ALL_USERS_GROUP_INTERNALID, p.getZoneId());
		        	else if(p instanceof Application)
		        		gp = getReservedApplicationGroup(ObjectKeys.ALL_APPLICATIONS_GROUP_INTERNALID, p.getZoneId());
		        	else
		        		throw new IllegalArgumentException(p.getClass().getName());
		        	
		    		return new HashSet(p.computePrincipalIds(gp));
		    	}
		    	else {
		    		return new HashSet(p.computePrincipalIds(null));
		    	}
	    	} else {
	    		return new HashSet(p.computePrincipalIds(null));
	    	}
    	}
    	finally {
    		end(begin, "getPrincipalIds(Principal)");
    	}	        
    }
	/**
	 * Given a set of principal ids, return all userIds that represent userIds in 
	 * the original list, or members of groups and their nested groups.
	 * This is used to turn a distribution list into usersIds only.
	 * Use when don't need to load the entire object
	 * @param Set of principalIds
	 * @returns Set of userIds
	 */
    public Set<Long> explodeGroups(final Collection<Long> ids, Long zoneId) {
    	return explodeGroups(ids, zoneId, true);
    }
	public Set<Long> explodeGroups(final Collection<Long> ids, Long zoneId, boolean allowAllUsersGroup) {   
		long begin = System.nanoTime();
		try {
			if ((ids == null) || ids.isEmpty()) return new TreeSet();
			Set users = (Set)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                    Set<Long> result = new TreeSet(ids);
	                    List mems;
	                    Set loopDectector = new HashSet(ids);
	                    Set currentIds = new HashSet(ids);
	                    while (!currentIds.isEmpty()) {
	                    	if (currentIds.size() <= inClauseLimit) {
	                           	mems  = session.createCriteria(Membership.class)
	                           			.add(Expression.in("groupId", currentIds))
	                           			.list();                   		
	                    	} else {
	                    		List idsList = new ArrayList(currentIds);
	                    		mems = new ArrayList();
	                    		for (int i=0; i<idsList.size(); i+=inClauseLimit) {
	                    			List partial  = session.createCriteria(Membership.class)
	                    				.add(Expression.in("groupId", idsList.subList(i, Math.min(idsList.size(), i+inClauseLimit))))
	                    				.list();
	                    			mems.addAll(partial);
	                    		}
	           				}
	                       	currentIds.clear();
							for (int i=0; i<mems.size(); ++i) {
								Membership m = (Membership)mems.get(i);
								result.remove(m.getGroupId());
								//potential user - may be another group
								if (!result.contains(m.getUserId()) && !loopDectector.contains(m.getUserId())) {
									result.add(m.getUserId());
									currentIds.add(m.getUserId());
								}
							}
							loopDectector.addAll(currentIds);
							
							//note: empty groups may appear in the resultant list
	                    }
	                    return result;
	                }
	            }
	        );
			Long allId = getReservedId(ObjectKeys.ALL_USERS_GROUP_INTERNALID, zoneId);
			if (allowAllUsersGroup && (ids.contains(allId) || users.contains(allId))) {
				//need to remove some users from the all users group.  Original list may add them back in.
				//so need to do this step last
				List<Object[]> result = getCoreDao().loadObjects(
						new ObjectControls(User.class, new String[]{ObjectKeys.FIELD_ID}), null, zoneId);
				//remove users not included
				result.remove(getReservedId(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, zoneId));
				result.remove(getReservedId(ObjectKeys.GUEST_USER_INTERNALID, zoneId));
				result.remove(getReservedId(ObjectKeys.JOB_PROCESSOR_INTERNALID, zoneId));
				result.remove(getReservedId(ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID, zoneId));
				users.addAll(result);
			} 
			users.remove(allId);
			return users;		
    	}
    	finally {
    		end(begin, "explodeGroups(Collection<Long>,<Long>)");
    	}	        
	}
	/**
	 * Get the Membership of a group.  Does not explode nested groups.
	 * Does not load the group object
	 * @param groupId
	 * @result List of <code>Membership</code>
	 */
	public List<Long> getMembership(final Long groupId, Long zoneId) {
		long begin = System.nanoTime();
		try {
			if (groupId == null) return new ArrayList();
		    List membership = (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                   	Query query = session.createQuery("from org.kablink.teaming.domain.Membership m where m.groupId=?");
	                   	query.setParameter(0, groupId);
	                    return query.list();
	                 }
	            }
	        );
			return membership;		
    	}
    	finally {
    		end(begin, "getMembership(Long,Long)");
    	}	        
		
	}
	
	public List<Long> getOwnedBinders(final Set<Principal> users) {
		long begin = System.nanoTime();
		try {
			if (users == null || (users.size() < 1)) return new ArrayList<Long>();
		    List membership = (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                   	Query query = session.createQuery("select w.id from org.kablink.teaming.domain.Binder w where w.owner in (:pList)")
	                   	.setParameterList("pList", users);
	                    return query.list();
	                 }
	            }
	        );
			return membership;		
    	}
    	finally {
    		end(begin, "getOwnedBinders(Set<Principal>)");
    	}	        
		
	}
	
    //        		Set binderIds = new HashSet(session.createQuery("select w.binderId from org.kablink.teaming.domain.Binder w where w.ownerId in (:pList)")
//            
	/**
	 * Get all groups a principal is a member of, either directly or through nested group
	 * membership.
	 * Use when don't want to load the entire group object
	 * @param principalId
	 * @return Set of groupIds
	 */
	public Set<Long> getAllGroupMembership(final Long principalId, Long zoneId) {
		long begin = System.nanoTime();
		try {
			if (principalId == null)  return new TreeSet();
			return (Set)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                    Principal principal = (Principal)session.get(Principal.class, principalId);
	                    if (principal == null) {throw new NoPrincipalByTheIdException(principalId);}
	                    Set result = new TreeSet();
	                    Set currentIds = new HashSet();
	                    currentIds.add(principalId);
	                    while (!currentIds.isEmpty()) {
	                    	List mems = session.createCriteria(Membership.class)
	                    					.add(Expression.in("userId", currentIds))
											.list();
	                       	currentIds.clear();
							for (int i=0; i<mems.size(); ++i) {
								Membership m = (Membership)mems.get(i);
								//prevent infinite loops
								if (result.add(m.getGroupId())) {
									currentIds.add(m.getGroupId());
								}
							}                       	
	                    }
	                    return result;
	                }
	            }
	        );
    	}
    	finally {
    		end(begin, "getAllGroupMembership(Long,Long)");
    	}	        
	}

	public SeenMap loadSeenMap(Long userId) {
		long begin = System.nanoTime();
		try {
			SeenMap seen = null;
			try {
				seen =(SeenMap)getHibernateTemplate().get(SeenMap.class, userId);
			}
			catch(HibernateSystemException se) {
				if(se.getRootCause() instanceof java.io.EOFException) {
	    			// the data in the blob is corrupt; this most likely means last update failed because
	    			// the data exceeded the size limit - there's no way we can fix up this data, get rid of it
	    			// (this is meant to be one time only cleanup on a system that already has corrupt lobs)
	    			logger.error("Seen map for user " + userId + " seems corrupt: Resetting it to empty value", se);
	    			executeSeenmapDelete(userId);	    								
				}
				else {
					throw se;
				}
			}
	   		if (seen == null) {
	   			seen = new SeenMap(userId);
	   			//quick write
	   			try {
	   				seen = (SeenMap)getCoreDao().saveNewSession(seen);
	   			} catch (Exception ex) {
	   				//contension?
	   				seen =(SeenMap)getHibernateTemplate().get(SeenMap.class, userId);
	   			}
	   		}
	   		return seen;
    	}
    	finally {
    		end(begin, "loadSeenMap(Long)");
    	}	        
	}
	public Visits loadVisit(Long userId, EntityIdentifier entityId) {
		long begin = System.nanoTime();
		try {
	    	UserEntityPK id = new UserEntityPK(userId, entityId);
	        return (Visits)getHibernateTemplate().get(Visits.class, id);	
    	}
    	finally {
    		end(begin, "loadVisit(Long,EntityIdentifier)");
    	}	        
	}
	public Rating loadRating(Long userId, EntityIdentifier entityId) {
		long begin = System.nanoTime();
		try {
	    	UserEntityPK id = new UserEntityPK(userId, entityId);
	        return (Rating)getHibernateTemplate().get(Rating.class, id);	
    	}
    	finally {
    		end(begin, "loadRating(Long,EntityIdentifier)");
    	}	        
	}
	public Subscription loadSubscription(Long userId, EntityIdentifier entityId) {
		long begin = System.nanoTime();
		try {
	    	UserEntityPK id = new UserEntityPK(userId, entityId);
	        return (Subscription)getHibernateTemplate().get(Subscription.class, id);	
    	}
    	finally {
    		end(begin, "loadSubscription(Long,EntityIdentifier)");
    	}	        
	}
	//mark entries deleted - used when deleting entries in bulk and want
	//to exclude some from future queries
	//entries evicted from cache
	public void markEntriesDeleted(final ProfileBinder binder, final Collection<Principal> entries) {
		long begin = System.nanoTime();
		try {
			if (entries.isEmpty()) return;
			getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session) throws HibernateException {
							Set ids = new HashSet();	               			
							for (Principal p:entries) {
								ids.add(p.getId());
								session.evict(p);
		            	    }
							session.createQuery("Update org.kablink.teaming.domain.Principal set deleted=:delete where id in (:pList)")
							.setBoolean("delete", Boolean.TRUE)
							.setParameterList("pList", ids)
							.executeUpdate();
							return null;
						}
					}
			);    		             		 
    	}
    	finally {
    		end(begin, "markEntriesDeleted(ProfileBinder,Collection<Principal>)");
    	}	        
	}
	public Application loadApplication(Long applicationId, Long zoneId) throws NoApplicationByTheIdException {
		long begin = System.nanoTime();
    	try {
    		Application application = (Application)getHibernateTemplate().get(Application.class, applicationId);
    		if (application == null) {throw new NoApplicationByTheIdException(applicationId);}
    		//	make sure from correct zone
    		if (!application.getZoneId().equals(zoneId) || !application.isActive()) {
    			throw new NoApplicationByTheIdException(applicationId);
    		}
    		return application;
    	} catch (ClassCastException ce) {
   			throw new NoApplicationByTheIdException(applicationId);   		
    	}
		finally {
			end(begin, "loadApplication(Long,Long)");
		}	        
	}
	public Application loadApplication(Long applicationId, String zoneName) throws NoUserByTheIdException {
		long begin = System.nanoTime();
		try {
			Binder top = getCoreDao().findTopWorkspace(zoneName);
			return loadApplication(applicationId, top.getZoneId());
    	}
    	finally {
    		end(begin, "loadApplication(Long,String)");
    	}	        
	}

    public Principal loadPrincipal(final Long prinId, final Long zoneId, final boolean checkActive) {
		long begin = System.nanoTime();
		try {
	        Principal principal = (Principal)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                    	//hoping for cache hit
	                    	Principal principal = (Principal)session.get(Principal.class, prinId);
	                        if (principal == null) {throw new NoPrincipalByTheIdException(prinId);}
	                        //Get the real object, not a proxy to abstract class
	                        principal = resolveProxy(session, principal);
	                         //make sure from correct zone
	                        if (!principal.getZoneId().equals(zoneId) ||
	                        		(checkActive && !principal.isActive())) {throw new NoPrincipalByTheIdException(prinId);}
	                        return principal;
	                    }
	                }
	        );
	       
	        principal = filterInaccessiblePrincipal(principal);
	        if (principal == null) 
	        	throw new NoPrincipalByTheIdException(prinId); 
	        return principal;
    	}
    	finally {
    		end(begin, "loadPrincipal(Long,Long,boolean)");
    	}	        
              
    }
    
	public List<ApplicationGroup> loadApplicationGroups(Collection<Long> groupsIds, Long zoneId) {
		long begin = System.nanoTime();
		try {
			return loadPrincipals(groupsIds, zoneId, ApplicationGroup.class, false, true);
    	}
    	finally {
    		end(begin, "loadApplicationGroups(Collection<Long>,Long)");
    	}	        
	}

	public ApplicationPrincipal loadApplicationPrincipal(final Long prinId, final Long zoneId, final boolean checkActive) {
		long begin = System.nanoTime();
		try {
			ApplicationPrincipal principal = (ApplicationPrincipal)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                    	//hoping for cache hit
	                    	ApplicationPrincipal principal = (ApplicationPrincipal)session.get(ApplicationPrincipal.class, prinId);
	                        if (principal == null) {throw new NoPrincipalByTheIdException(prinId);}
	                        //Get the real object, not a proxy to abstract class
	                        try {
	                        	principal = (ApplicationPrincipal)session.get(Application.class, prinId);
	                        } catch (Exception ex) {};  // group proxies will force an exception, didn't expect with session.get? 
	                        if (principal==null) 
	                            principal = (ApplicationPrincipal)session.get(ApplicationGroup.class, prinId);
	                        //make sure from correct zone
	                        if (!principal.getZoneId().equals(zoneId) ||
	                        		(checkActive && !principal.isActive())) {throw new NoPrincipalByTheIdException(prinId);}
	                        return principal;
	                    }
	                }
	        );
	       
	        return principal;
    	}
    	finally {
    		end(begin, "loadApplicationPrincipal(Long,Long,boolean)");
    	}	        
	}
	
	public List<ApplicationPrincipal> loadApplicationPrincipals(Collection<Long> ids, Long zoneId, boolean checkActive) {
		long begin = System.nanoTime();
		try {
	    	List<ApplicationPrincipal> result = loadPrincipals(ids, zoneId, ApplicationPrincipal.class, true, checkActive);
			//remove proxies
			for (int i=0; i<result.size(); ++i) {
				ApplicationPrincipal p = result.get(i);
				if (!(p instanceof Application) && !(p instanceof ApplicationGroup)) {
					ApplicationPrincipal principal = null;
					try {
						principal = (ApplicationPrincipal)getHibernateTemplate().get(Application.class, p.getId());
					} catch(Exception ex) {}
					if (principal==null) principal = (ApplicationPrincipal)getHibernateTemplate().get(ApplicationGroup.class, p.getId());
	   				result.set(i, principal);
	            }
	        }
			return result;
    	}
    	finally {
    		end(begin, "loadApplicationPrincipals(Collection<Long>,Long,boolean)");
    	}	        
	}
	
	public List loadGroupPrincipals(Collection<Long> ids, Long zoneId, boolean checkActive) {
		long begin = System.nanoTime();
		try {
	    	List<Principal> result = loadPrincipals(ids, zoneId, Principal.class, false, checkActive);
			//remove proxies
			for (int i=0; i<result.size(); ++i) {
				Principal p = result.get(i);
				if (!(p instanceof Group) && !(p instanceof ApplicationGroup)) {
					Principal principal = null;
					try {
						principal = (Principal)getHibernateTemplate().get(Group.class, p.getId());
					} catch(Exception ex) {}
					if (principal==null) {
						try {
							principal = (Principal)getHibernateTemplate().get(ApplicationGroup.class, p.getId());
						} catch(Exception ex) {}
					}
					if(principal != null) {					
		   				result.set(i, principal);
					}
					else { // what we got is not of the right type
						result.remove(i);
						--i;
					}
	            }
	        }
			return result;
    	}
    	finally {
    		end(begin, "loadGroupPrincipals(Collection<Long>,Long,boolean)");
    	}	        
	}
	
	public List loadIndividualPrincipals(Collection<Long> ids, Long zoneId, boolean checkActive) {
		long begin = System.nanoTime();
		try {
	    	List<Principal> result = loadPrincipals(ids, zoneId, Principal.class, false, checkActive);
			//remove proxies
			for (int i=0; i<result.size(); ++i) {
				Principal p = result.get(i);
				if (!(p instanceof User) && !(p instanceof Application)) {
					Principal principal = null;
					try {
						principal = (Principal)getHibernateTemplate().get(User.class, p.getId());
					} catch(Exception ex) {}
					if (principal==null) {
						try {
							principal = (Principal)getHibernateTemplate().get(Application.class, p.getId());
						} catch(Exception ex) {}
					}
					if(principal != null) {					
		   				result.set(i, principal);
					}
					else { // what we got is not of the right type
						result.remove(i);
						--i;
					}
	            }
	        }
			return result;
    	}
    	finally {
    		end(begin, "loadIndividualPrincipals(Collection<Long>,Long,boolean)");
    	}	        
	}

    public List<Application> loadApplications(Collection<Long> applicationIds, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	// If applicationIds is null, load all principals of the specified type rather
	    	// than returning an empty list. This symantics differs from the counterpart 
	    	// methods returning users and groups. I decided to introduce this inconsistency
	    	// rather than risking breaking the existing system by changing the symantics of 
	    	// the other methods for the sake of consistency. 
	    	// 
	    	if(applicationIds != null)
	    		return loadPrincipals(applicationIds, zoneId, Application.class, false, true);
	    	else
	    		return loadAllPrincipals(zoneId, Application.class, true, true);
    	}
    	finally {
    		end(begin, "loadApplications(Collection<Long>,Long)");
    	}	        
    }
    
    public List<Principal> loadPrincipals(Collection<Long> ids, Long zoneId,  boolean checkActive) {
		long begin = System.nanoTime();
		try {
	    	List<Principal> result = loadPrincipals(ids, zoneId, Principal.class, true, checkActive);
			//remove proxies
			for (int i=0; i<result.size(); ++i) {
				Principal p = result.get(i);
				if (!(p instanceof User) && !(p instanceof Group) && !(p instanceof Application) && !(p instanceof ApplicationGroup)) {
					Principal principal = null;
					try {
						principal = (Principal)getHibernateTemplate().get(User.class, p.getId());
					} catch(Exception ex) {}
					if (principal==null) {
						try {
							principal = (Principal)getHibernateTemplate().get(Group.class, p.getId());
						} catch(Exception ex) {}
					}
					if (principal==null) {
						try {
							principal = (Principal)getHibernateTemplate().get(Application.class, p.getId());
						} catch(Exception ex) {}
					}
					if (principal==null) principal = (Principal)getHibernateTemplate().get(ApplicationGroup.class, p.getId());
	   				result.set(i, principal);
	            }
	        }
			return result;
    	}
    	finally {
    		end(begin, "loadPrincipals(Collection<Long>,Long,boolean)");
    	}	        
	
    }
    
    private List loadAllPrincipals(final Long zoneId, final Class clazz, final boolean cacheable, final boolean checkActive) {
        List result = (List)getHibernateTemplate().execute(
           	new HibernateCallback() {
        		public Object doInHibernate(Session session) throws HibernateException {
       					Criteria crit = session.createCriteria(clazz)
    					.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
    					.setFetchMode("emailAddresses", FetchMode.JOIN)
                		.setCacheable(cacheable);
    					if (checkActive) {
    						crit.add(Expression.eq(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE));
    						crit.add(Expression.eq(ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.FALSE));
    					}  
    					List result = crit.list();
                        //eagar select results in duplicates
                        Set res = new HashSet(result);
                        result.clear();
                        result.addAll(res);
    					return result;
        		}
           	}
        );
        return result;
    	
    }


    //get list representing entities that have been shared with ids and binderIds
    public List<SharedEntity> loadSharedEntities(final Collection ids, final Collection binderIds, final Date after, Long zoneId) {   	
		long begin = System.nanoTime();
		try {
	      	List result = (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                    	Criteria crit = session.createCriteria(SharedEntity.class)
	                    	.add(Expression.gt("sharedDate", after))
	             			.addOrder(Order.desc("sharedDate"));
	                    	if (!binderIds.isEmpty() && !ids.isEmpty() ) {
	                    		crit.add(Expression.disjunction()
	              					.add(Expression.conjunction()
	              							.add(Expression.in("accessId", ids))
	              							.add(Expression.eq("accessType", SharedEntity.ACCESS_TYPE_PRINCIPAL)))
	              					.add(Expression.conjunction()
	              							.add(Expression.in("accessId", binderIds))
	              							.add(Expression.eq("accessType", SharedEntity.ACCESS_TYPE_TEAM))));
	                    	} else if (binderIds.isEmpty()) {
	                    		//not a member of a team
	                    		crit.add(Expression.disjunction()
	                  				.add(Expression.conjunction()
	                  						.add(Expression.in("accessId", ids))
	                  						.add(Expression.eq("accessType", SharedEntity.ACCESS_TYPE_PRINCIPAL))));
	                        		
	                    	} else {
	                    		//should never happen
	                    		crit.add(Expression.in("accessId", binderIds))
	                  				.add(Expression.eq("accessType", SharedEntity.ACCESS_TYPE_TEAM));
	                    		
	                    	}
	               			
	                       return crit.list();
	                    }
	                }
	            );
	      	
	       return result;   	
    	}
    	finally {
    		end(begin, "loadSharedEntities(Collection,Collection,Date,Long)");
    	}	              	
    	
    }
    
 	private User findUserByNameDeadOrAlive(final String userName, final Long zoneId) 
	throws NoUserByTheNameException {
    	// STOP!!! THIS METHOD IS FOR INTERNAL/SPECIAL USE ONLY. USE THE REGULAR findUserByName() METHOD INSTEAD.
        return (User)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                 	   //only returns active users
                 	   User user = (User)session.getNamedQuery("find-User-Company-DeadOrAlive")
                             		.setString(ParameterNames.USER_NAME, userName.toLowerCase())
                             		.setLong(ParameterNames.ZONE_ID, zoneId)
                             		.setCacheable(isPrincipalQueryCacheable())
                             		.uniqueResult();
                        //this query doesn't care if user is deleted or disabled
                 	   if (user == null) {
                            throw new NoUserByTheNameException(userName); 
                        }
                        return user;
                    }
                }
             );		
 	}

	//If this user cannot see all users, then filter out the ones not allowed
	public Principal filterInaccessiblePrincipal(Principal principal) {
		long begin = System.nanoTime();
		try {
			List principals = new ArrayList();
			principals.add(principal);
			principals = filterInaccessiblePrincipals(principals);
			if (principals.isEmpty()) return null;
			return (Principal)principals.get(0);
    	}
    	finally {
    		end(begin, "filterInaccessiblePrincipal(Principal)");
    	}	        
	}
	public List filterInaccessiblePrincipals(List principals) {
		if (RequestContextHolder.getRequestContext() == null) return principals;
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		long begin = System.nanoTime();
		try {
			User user = null;
			try {
				if(RequestContextHolder.getRequestContext() != null)
					user = RequestContextHolder.getRequestContext().getUser();
			} catch(Exception e) {}
			if (user == null) return principals;
			
	      	WorkArea zone = getCoreDao().loadZoneConfig(user.getZoneId());
			AccessControlManager accessControlManager = (AccessControlManager)SpringContextUtil.getBean("accessControlManager");
			try {
				boolean canOnlySeeGroupMembers = accessControlManager.testOperation(user, zone, WorkAreaOperation.ONLY_SEE_GROUP_MEMBERS);
				boolean overrideCanOnlySeeGroupMembers = accessControlManager.testOperation(user, zone, WorkAreaOperation.OVERRIDE_ONLY_SEE_GROUP_MEMBERS);
				if (!canOnlySeeGroupMembers || overrideCanOnlySeeGroupMembers) return principals;
			} catch(Exception e) {
				//If any error occurs, just deny access to them all
				return principals;
			}
			
			//This user does not have the right to see all users, so filter out those not allowed
			Set groupIds = getAllGroupMembership(user.getId(), RequestContextHolder.getRequestContext().getZoneId());
			List result = new ArrayList();
			for (int i = 0; i < principals.size(); i++) {
				Principal principal = (Principal)principals.get(i);
				if (EntityType.user.equals(principal.getEntityType())) {
					if (user.getId() == principal.getId()) {
						//Always allow a user to see his/her own profile
						if (!result.contains(principal)) result.add(principal);
					} else {
						Set<Long> userGroupIds = getAllGroupMembership(principal.getId(), zoneId);
						for (Long id : userGroupIds) {
							if (groupIds.contains(id)) {
								result.add(principal);
								break;
							}
						}
						if (!result.contains(principal)) {
							//The current user cannot see this principal, so add a dummy principal object
							result.add(makeItFake(principal));
						}
					}
				} else {
					result.add(principal);
				}
			}
			return result;
    	}
    	finally {
    		end(begin, "filterInaccessiblePrincipals(List)");
    	}	        
	}
	
	private Principal makeItFake(Principal p) {
		User u = new User();
		u.setId(p.getId());
		u.setZoneId(p.getZoneId());
		u.setParentBinder(p.getParentBinder());
		u.setName(FAKE_NAME_PREFIX + p.getId().toString());
		String title = NLT.get("user.redacted.title");
		u.setLastName(title);
		u.setDescription("");
		u.setEntryDefId(p.getEntryDefId());
		u.setWorkspaceId(p.getWorkspaceId());
		return u;
	}
	
	//Initialize (or reset) the disk space used by each user
	public void resetDiskUsage(Long zone) {
		long begin = System.nanoTime();
		try {
			final Long zoneId = zone;
			getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session) throws HibernateException {
							String sql ="Update SS_Principals " +
										"SET diskSpaceUsed = " + 
										"COALESCE( (SELECT SUM(fileLength) FROM SS_Attachments b " +
										"WHERE b.creation_principal = SS_Principals.id " + 
										" AND SS_Principals.type = 'user' " +
										" AND SS_Principals.zoneId = " + zoneId +
										" AND b.versionNumber > 0" +
										" AND b.zoneId = " + zoneId +
										" AND b.repositoryName != \'" + ObjectKeys.FI_ADAPTER + "\'), 0)" +
										" WHERE zoneId = " + zoneId;
							session.createSQLQuery(sql).executeUpdate();
							return null;
						}
					}
			);    		             		 
    	}
    	finally {
    		end(begin, "filterInaccessiblePrincipals(List)");
    	}	        
	}

	// this returns non-zero quotas (any quota which has been set by the admin)
	public List getNonDefaultQuotas(String type, final long zoneId) {
		long begin = System.nanoTime();
		try {
			final String principalType = type;
			List userList = new ArrayList();
			List results = null;
			results = (List) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException {
							
							String sql = "Select w.id "
								+ " FROM org.kablink.teaming.domain.UserPrincipal w "
								+ " WHERE w.diskQuota IS NOT NULL "
								+ " AND w.diskQuota != 0 "
								+ " AND w.type = :principalType" 
								+ " AND w.zoneId = :zoneId";
	
							Query query = session.createQuery(sql)
		                   	.setString("principalType", principalType)
		                   	.setLong("zoneId", zoneId);
							
							List l = null;
							l = query.list();
							return l;
						}
					});
	
			for (int i = 0; i < results.size(); i++) {
				userList.add(results.get(i));
			}
	
			return userList;
    	}
    	finally {
    		end(begin, "getNonDefaultQuotas(String,long)");
    	}	        
	}

	// this returns non-zero file size limits (any limit which has been set by the admin)
	public List getNonDefaultFileSizeLimits(String type, final long zoneId) {
		long begin = System.nanoTime();
		try {
			final String principalType = type;
			List userList = new ArrayList();
			List results = null;
			results = (List) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException {
							
							String sql = "Select w.id "
								+ " FROM org.kablink.teaming.domain.UserPrincipal w "
								+ " WHERE w.fileSizeLimit IS NOT NULL "
								+ " AND w.type = :principalType" 
								+ " AND w.zoneId = :zoneId";
	
							Query query = session.createQuery(sql)
		                   	.setString("principalType", principalType)
		                   	.setLong("zoneId", zoneId);
							
							List l = null;
							l = query.list();
							return l;
						}
					});
	
			for (int i = 0; i < results.size(); i++) {
				userList.add(results.get(i));
			}
	
			return userList;
    	}
    	finally {
    		end(begin, "getNonDefaultQuotas(String,long)");
    	}	        
	}

	// This returns a list of all disabled user accounts
	public List<Long> getDisabledUserAccounts(final long zoneId) {
		long begin = System.nanoTime();
		try {
			final String principalType = ObjectKeys.PRINCIPAL_TYPE_USER;
			List<Long> userList = new ArrayList<Long>();
			List results = null;
			results = (List) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException {
							
							String sql = "Select w.id "
								+ " FROM org.kablink.teaming.domain.UserPrincipal w "
								+ " WHERE w.disabled = true "
								+ " AND w.deleted = false "
								+ " AND w.type = :principalType" 
								+ " AND w.zoneId = :zoneId";
	
							Query query = session.createQuery(sql)
		                   	.setString("principalType", principalType)
		                   	.setLong("zoneId", zoneId);
							
							List l = null;
							l = query.list();
							return l;
						}
					});
	
			for (int i = 0; i < results.size(); i++) {
				userList.add((Long) results.get(i));
			}
	
			return userList;
    	}
    	finally {
    		end(begin, "getDisabledUserAccounts(long)");
    	}	        
	}
}
