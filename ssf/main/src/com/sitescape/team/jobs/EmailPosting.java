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

package com.sitescape.team.jobs;
/**
 * @author Janet McCann
 *
 */
public interface EmailPosting  {
    
    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_emailPostingJob";
	public static final String POSTING_GROUP="email-posting";
	public void enable(boolean enable, Long zoneId);
	public ScheduleInfo getScheduleInfo(Long zoneId);
	public void setScheduleInfo(ScheduleInfo schedulerInfo);
	
}
