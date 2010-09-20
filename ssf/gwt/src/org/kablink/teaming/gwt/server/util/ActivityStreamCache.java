/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.server.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

/**
 * This class manages a cache of the IDs of the binders with entries
 * that have been added or modified in the past few minutes or the IDs
 * of the users that have made modifications in the past few minutes.
 * 
 * @author drfoster@novell.com
 */
public class ActivityStreamCache {
	// The following are used for storing information in the session
	// cache.
	private static final String SESSION_ACTIVITY_STREAM_TRACKED_BINDER_IDS	="activityStreamTrackedBinderIds";
	private static final String SESSION_ACTIVITY_STREAM_TRACKED_USER_IDS	="activityStreamTrackedUserIds";
	private static final String SESSION_ACTIVITY_STREAM_UPDATE_DATE			="activityStreamUpdateDate";
	
    // The following stores the system wide ID caches for all the zones
    // in the system.
	private static ConcurrentMap<Long, ActivityStreamIDCache> m_zonedIDCaches = new ConcurrentHashMap<Long, ActivityStreamIDCache>();

	/*
	 * Inner class use to track the IDs being cached.
	 */
    private static class ActivityStreamIDCache {
    	// Note:  The following fields need to be kept together so that
    	//    they are consistent relative to each other within a
    	//    zone-specific cache.
    	private Date			m_lastUpdate;
    	private Map<Long, Date>	m_binderMap;
    	private Map<Long, Date>	m_userMap;
    	
    	/*
    	 * Constructor method.
    	 */
    	private ActivityStreamIDCache() {
    		m_binderMap  = new HashMap<Long, Date>();
    		m_userMap    = new HashMap<Long, Date>();
    		m_lastUpdate = new Date(
    			System.currentTimeMillis() -
    			mToMS(GwtActivityStreamHelper.m_activityStreamParams.getCacheRefresh()) - 1);
    	}
    	
    	/*
    	 * Returns the binder map from the ID cache.
    	 */
		private Map<Long, Date> getBinderMap() {
			return m_binderMap;
		}
		
		/*
		 * Returns the date of the last time the ID cache was updated.
		 */
		private Date getLastUpdate() {
			return m_lastUpdate;
		}
		
    	/*
    	 * Returns the user map from the ID cache.
    	 */
		private Map<Long, Date> getUserMap() {
			return m_userMap;
		}
		
		/*
		 * Store a new binder map in the ID cache.
		 */
		private void setBinderMap(Map<Long, Date> binderMap) {
			m_binderMap = binderMap;
		}
		
		/*
		 * Store a new user map in the ID cache.
		 */
		private void setUserMap(Map<Long, Date> userMap) {
			m_userMap = userMap;
		}
		
		/*
		 * Sets the date of the last time the ID cache was updated.
		 */
		private void setLastUpdate(Date lastUpdate) {
			m_lastUpdate = lastUpdate;
		}
    }

    /**
     * Returns true if any of the binders in a set have new entries
     * available and false otherwise.
     * 
     * @param bs
     * @param binderIds
     * @param date
     * 
     * @return
     */
    public static boolean checkBindersForNewEntries(AllModulesInjected bs, List<String> binderIds, Date date) {
    	// Do we have an ID cache available?
    	ActivityStreamIDCache cache = getIDCacheForTheZone();
    	if(null != cache) {
    		// Yes!  Look to see if there are any binder IDs in common,
    		// using the smallest list.
    		Map<Long, Date> binderMap = cache.getBinderMap();
    		if (binderMap.size() < binderIds.size()) {
	    		for (Long binderId: binderMap.keySet()) {
	    			String binderIdS = String.valueOf(binderId);
		        	if (binderIds.contains(binderIdS) && binderMap.get(binderId).after(date)) {
		        		return true;
		        	}
	    		}	
    		}
    		
    		else {
        		for (String binderIdS: binderIds) {
        			Long binderId = Long.parseLong(binderIdS);
    	        	if (binderMap.containsKey(binderId) && binderMap.get(binderId).after(date)) {
    	        		return true;
    	        	}
        		}
    		}
    	}
    	
    	return false;
    }
    
    /**
     * Returns true if any of the users in a set have new entries
     * available and false otherwise.
     * 
     * @param bs
     * @param userIds
     * @param date
     * 
     * @return
     */
    public static boolean checkUsersForNewEntries(AllModulesInjected bs, List<String> userIds, Date date) {
    	// Do we have an ID cache available?
    	ActivityStreamIDCache cache = getIDCacheForTheZone();
    	if(null != cache) {
    		// Yes!  Look to see if there are any users in common,
    		// using the smallest list.
    		Map<Long, Date> userMap = cache.getUserMap();
    		if (userMap.size() < userIds.size()) {
	    		for (Long userId: userMap.keySet()) {
	    			String userIdS = String.valueOf(userId);
		        	if (userIds.contains(userIdS) && userMap.get(userId).after(date)) {
		        		return true;
		        	}
	    		}	
    		}
    		
    		else {
        		for (String userIdS: userIds) {
        			Long userId = Long.parseLong(userIdS);
    	        	if (userMap.containsKey(userId) && userMap.get(userId).after(date)) {
    	        		return true;
    	        	}
        		}
    		}
    	}
    	
    	return false;
    }
    
	/*
	 * Returns the ID cache for the current zone.
	 */
	private static ActivityStreamIDCache getIDCacheForTheZone() {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	return m_zonedIDCaches.get(zoneId);
	}

	/**
	 * Returns the cached tracked binder IDs list from the session cache.
	 * 
	 * @param request
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getTrackedBinderIds(HttpServletRequest request) {
        HttpSession session = WebHelper.getRequiredSession(request);
		List<String> reply = ((List<String>) session.getAttribute(SESSION_ACTIVITY_STREAM_TRACKED_BINDER_IDS));
		if (null == reply) {
			reply = new ArrayList<String>();
		}
		return reply;
	}
	
	/**
	 * Returns the cached tracked user IDs list from the session cache.
	 * 
	 * @param request
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getTrackedUserIds(HttpServletRequest request) {
        HttpSession session = WebHelper.getRequiredSession(request);
		List<String> reply = ((List<String>) session.getAttribute(SESSION_ACTIVITY_STREAM_TRACKED_USER_IDS));
		if (null == reply) {
			reply = new ArrayList<String>();
		}
		return reply;
	}

	/**
	 * Returns the date of the last client update from the session
	 * cache.
	 * 
	 * @param request
	 * 
	 * @return
	 */
	public static Date getUpdateDate(HttpServletRequest request) {
        HttpSession session = WebHelper.getRequiredSession(request);
		Date reply = ((Date) session.getAttribute(SESSION_ACTIVITY_STREAM_UPDATE_DATE));
		if (null == reply) {
        	reply = new Date();
        	reply.setTime(reply.getTime() - GwtActivityStreamHelper.m_activityStreamParams.getClientRefresh() - 1);
		}
		return reply;
	}
	
    /*
     * Returns the millisecond equivalent of an interval in minutes.
     */
    private static int mToMS(int minutes) {
    	return (minutes * 60 * 1000);
    }
    
	/*
	 * Stores a new ID cache for the current zone.
	 */
	private static void setIDCacheForTheZone(ActivityStreamIDCache cache) {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	m_zonedIDCaches.put(zoneId, cache);
	}

	/**
	 * Stores a tracked binder IDs list in the session cache.
	 * 
	 * @param request
	 * @param trackedBinderIds
	 */
	public static void setTrackedBinderIds(HttpServletRequest request, List<String> trackedBinderIds) {
        HttpSession session = WebHelper.getRequiredSession(request);
		session.setAttribute(SESSION_ACTIVITY_STREAM_TRACKED_BINDER_IDS, trackedBinderIds);
	}
	
	/**
	 * Stores a tracked user IDs list in the session cache.
	 * 
	 * @param request
	 * @param trackedUserIds
	 */
	public static void setTrackedUserIds(HttpServletRequest request, List<String> trackedUserIds) {
        HttpSession session = WebHelper.getRequiredSession(request);
		session.setAttribute(SESSION_ACTIVITY_STREAM_TRACKED_USER_IDS, trackedUserIds);
	}

	/**
	 * Stores the current date as the activity stream update date in
	 * the session cache.
	 * 
	 * @param request
	 */
	public static void setUpdateDate(HttpServletRequest request) {
        HttpSession session = WebHelper.getRequiredSession(request);
		session.setAttribute(SESSION_ACTIVITY_STREAM_UPDATE_DATE, new Date());
	}

	/*
	 * Constructs new ID maps for the ID cache.
	 */
	@SuppressWarnings("unchecked")
	private static void updateIDMaps(AllModulesInjected bs, Date now, ActivityStreamIDCache idCache) {
		// Create new maps to store into the ID cache.
		Map<Long, Date> newBinderMap = new HashMap<Long, Date>();
		Map<Long, Date> newUserMap   = new HashMap<Long, Date>();
		
		// Run a search for anything that has changed.  Note that we
		// run it as admin because the cache is used by everyone in a
		// zone.
		Criteria crit = SearchUtils.entriesForActivityStreamCache(now, GwtActivityStreamHelper.m_activityStreamParams.getLookback());
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		String adminUserName = SZoneConfig.getAdminUserName(zoneName);
		User admin = bs.getProfileModule().getUser(adminUserName);
		Map results = bs.getBinderModule().executeSearchQuery(crit, 0, GwtActivityStreamHelper.m_activityStreamParams.getMaxHits(), admin.getId(), false, true);
		    		
		// Did we find any changes that need to be cached?
    	List items = ((List) results.get(ObjectKeys.SEARCH_ENTRIES));
    	if ((null != items) && (!(items.isEmpty()))) {
    		// Yes!  Scan them.
	    	for (Iterator it = items.iterator(); it.hasNext(); ) {
	    		// Yes!  Does this entry have a modification date?
	    		Map entry = ((Map) it.next());
				Date entryModificationDate = ((Date) entry.get(Constants.MODIFICATION_DATE_FIELD));
				if (null != entryModificationDate) {				
		    		// Yes!  Does this entry have an associated binder ID?
		    		String binderIdS = ((String) entry.get(Constants.BINDER_ID_FIELD));
					if (MiscUtil.hasString(binderIdS)) {
						// Yes!  Validate that it's in the map with at
		    			// least this recent of a date.
						validateIDInMap(
							newBinderMap,
							Long.valueOf(binderIdS),
							entryModificationDate);

						// Does this entry have an ancestry?
						SearchFieldResult ancestry = ((SearchFieldResult) entry.get(Constants.ENTRY_ANCESTRY));
						Set ancestrySet = ((null == ancestry) ? null : ancestry.getValueSet());
						if ((null != ancestrySet) && (!(ancestrySet.isEmpty()))) {
							// Yes!  Scan its ancestors.
							for (Object ancestorIdO: ancestrySet) {
								// Validating that each is in the map
				    			// with at least this recent of a date.
								validateIDInMap(
									newBinderMap,
									Long.valueOf(ancestorIdO.toString()),
									entryModificationDate);
							}
						}
					}
				
		    		// Does this entry have an associated modifier ID?
		    		String userIdS = ((String) entry.get(Constants.MODIFICATIONID_FIELD));
		    		if (MiscUtil.hasString(userIdS)) {
						// Yes!  Validate that it's in the map with at
		    			// least this recent of a date.
						validateIDInMap(
							newUserMap,
							Long.valueOf(userIdS),
							entryModificationDate);
		    		}
				}
	    	}
    	}

    	// Finally, store the new ID maps in the ID cache.
    	idCache.setBinderMap(newBinderMap);
    	idCache.setUserMap(  newUserMap  );
	}
	
	/**
	 * If necessary, updates the ID cache for the current zone.
	 * 
	 * @param bs
	 */
	public static void updateMaps(AllModulesInjected bs) {
    	// Do we need to create/refresh the ID cache for the current
    	// zone?
    	ActivityStreamIDCache cache = getIDCacheForTheZone();
    	Date now = new Date();
    	if((null == cache) ||
    			(now.getTime() >= (cache.getLastUpdate().getTime() + mToMS(GwtActivityStreamHelper.m_activityStreamParams.getCacheRefresh())))) {
    		// Yes!  Create a new one, store new ID maps into it, store
    		// the time that we updated it and store that as the ID
    		// cache for the zone.
    		ActivityStreamIDCache newCache = new ActivityStreamIDCache();
    		updateIDMaps(bs, now, newCache);    		        	
        	newCache.setLastUpdate(now);	        
	        setIDCacheForTheZone(newCache);
    	}
    }

	/*
	 * Checks a map for an ID with a date newer than that supplied.  If
	 * ID is not in the map, or is there with an older date, it is
	 * stored/updated.
	 */
	private static void validateIDInMap(Map<Long, Date> map, Long id, Date date) {
		// If we're not already tracking this ID or we're tracking it
		// with an older date...
		if ((!(map.containsKey(id))) || map.get(id).before(date)) {
			// ..store/update it.
			map.put(id, date);
		}
	}
}
