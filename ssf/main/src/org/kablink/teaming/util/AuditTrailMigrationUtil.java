/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.util;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.StatelessSessionTemplate;
import org.kablink.teaming.domain.AuditTrail;
import org.kablink.teaming.domain.ZoneConfig;

/**
 * @author Jong
 *
 */
public class AuditTrailMigrationUtil {

	private static Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private static void migrateSince(StatelessSession session, Long zoneId, long beginTime, Long endTime) {
		
		Criteria crit = session.createCriteria(AuditTrail.class)
				.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
				.add(Restrictions.ge("startDate", beginTime))
				.setCacheable(false);
		if(endTime != null)
			crit.add(Restrictions.le("startDate", endTime));
		final List<AuditTrail> oldRecords = crit.list();

		if(oldRecords.size() > 0) {
			// Use Hibernate transaction and manage it directly. 
			// The Spring TransactionTemplate is of no use here, because it only works with regular Session and not StatelessSession.
			Transaction trans = session.beginTransaction();
			try {
				List<Object> newRecords;
				for(AuditTrail oldRecord:oldRecords) {
					newRecords = oldRecord.toNewAuditObjects();
					if(newRecords.size() > 0) {
						// The old record maps to one or more new records. Migrate it into new tables.
						for(Object newRecord:newRecords) {
							session.insert(newRecord);
						}
						// Purge the old record.$$$$$$$$$$$$$
						//session.delete(oldRecord);
					}
					else {
						// The old record doesn't map to new scheme. Leave the record in the old table.
					}
				}
				trans.commit();
			}
			catch(Exception e) {
				trans.rollback();
			}
		}
	}
	
	private static StatelessSessionTemplate getStatelessSessionTemplate() {
		return (StatelessSessionTemplate)SpringContextUtil.getBean("statelessSessionTemplate");
	}
	
	public static void migrateMinimum(final ZoneConfig zoneConfig) {
		// $$$$$$$$$$ TODO TBC
		int i = 0;
		if(i == 0) return;
		
		getStatelessSessionTemplate().execute(new StatelessSessionTemplate.Callback<Void>() {
			@Override
			public Void doInHibernate(final StatelessSession session)
					throws HibernateException, SQLException {
				int numberOfDays = SPropsUtil.getInt("binder.changes.allowed.days", 7) + 1;
				long dayInMilliseconds = 1000L*60L*60L*24L;
				Long endTime = null;
				Long beginTime = System.currentTimeMillis() - dayInMilliseconds;
				// We're going to migrate only enough data to cover the specified number of days in descending time order
				// and do it in one day increment at a time. Each day's worth of data is migrated in a single transaction.
				for(int i = 0; i < numberOfDays; i++) {
					// Migrate data between the specified times
					migrateSince(session, zoneConfig.getZoneId(), beginTime, endTime);
					// Rewind the times backward by a day
					endTime = beginTime;
					beginTime -= dayInMilliseconds;
				}
				return null;
			}
		});
	}
	
	public static void migrateAll() {
		StatelessSessionTemplate sst = (StatelessSessionTemplate)SpringContextUtil.getBean("statelessSessionTemplate");
		
		sst.execute(new StatelessSessionTemplate.Callback<Void>() {
			@Override
			public Void doInHibernate(final StatelessSession session)
					throws HibernateException, SQLException {
				final List<AuditTrail> oldRecords = session.createCriteria(AuditTrail.class)
						//.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneConfig.getZoneId()))
						.add(Restrictions.isNotNull("startDate"))
						.add(Restrictions.lt("startDate", new Date()))
						.setCacheable(false)
                    	.addOrder(Order.asc("startDate"))
                    	.list();
				if(oldRecords.size() > 0) {
					// Use Hibernate transaction and manage it directly. 
					// The Spring TransactionTemplate is of no use here, because it only works with regular Session and not StatelessSession.
					Transaction trans = session.beginTransaction();
					try {
						List<Object> newRecords;
						for(AuditTrail oldRecord:oldRecords) {
							newRecords = oldRecord.toNewAuditObjects();
							if(newRecords.size() > 0) {
								// The old record maps to one or more new records. Migrate it into new tables.
								for(Object newRecord:newRecords) {
									session.insert(newRecord);
								}
								// Purge the old record.
								//session.delete(oldRecord);
							}
							else {
								// The old record doesn't map to new scheme. Leave the record in the old table.
							}
						}
						trans.commit();
					}
					catch(Exception e) {
						trans.rollback();
					}
				}
				
				return null;
			}
		});		
	}
}
