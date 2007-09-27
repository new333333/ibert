/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.domain;

import java.util.Date;


/**
 * @hibernate.class table="SS_LicenseStats"
 * 
 * @author Joe
 *
 * Log information used for license monitoring.
 * 
 */
public class LicenseStats {
	protected String id;
    protected Long zoneId; 
    protected Date snapshotDate;
	protected long internalUserCount;
	protected long externalUserCount;
	protected long checksum;

	/**
	 * 
	 */
	public LicenseStats() {
		super();
	}

	/**
	 * @param snapshotDate
	 * @param internalUserCount
	 * @param externalUserCount
	 * @param checksum
	 */
	public LicenseStats(Long zoneId, Date snapshotDate, long internalUserCount, long externalUserCount, long checksum) {
		super();
		setZoneId(zoneId);
		setSnapshotDate(snapshotDate);
		setInternalUserCount(internalUserCount);
		setExternalUserCount(externalUserCount);
		setChecksum(checksum);
	}
	
	/**
	 * @hibernate.id generator-class="uuid.hex" unsaved-value="null" node="@id"
	 * @hibernate.column name="id" sql-type="char(32)"
	 */    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @hibernate.property not-null="true"
     */
    public Long getZoneId() {
    	return this.zoneId;
    }
    public void setZoneId(Long zoneId) {
    	this.zoneId = zoneId;
    }
	/**
	 * @hibernate.property  
	 * @return the checksum
	 */
	public long getChecksum() {
		return checksum;
	}

	/**
	 * @param checksum the checksum to set
	 */
	public void setChecksum(long checksum) {
		this.checksum = checksum;
	}

	/**
	 * @hibernate.property  
	 * @return the externalUserCount
	 */
	public long getExternalUserCount() {
		return externalUserCount;
	}

	/**
	 * @param externalUserCount the externalUserCount to set
	 */
	public void setExternalUserCount(long externalUserCount) {
		this.externalUserCount = externalUserCount;
	}

	/**
	 * @hibernate.property  
	 * @return the internalUserCount
	 */
	public long getInternalUserCount() {
		return internalUserCount;
	}

	/**
	 * @param internalUserCount the internalUserCount to set
	 */
	public void setInternalUserCount(long internalUserCount) {
		this.internalUserCount = internalUserCount;
	}

	/**
	 * @hibernate.property  
	 * @return the snapshotDate
	 */
	public Date getSnapshotDate() {
		return snapshotDate;
	}

	/**
	 * @param snapshotDate the snapshotDate to set
	 */
	public void setSnapshotDate(Date snapshotDate) {
		this.snapshotDate = snapshotDate;
	}

}
