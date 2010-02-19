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
package org.kablink.teaming.lucene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.BooleanQuery;

import org.kablink.util.PropsUtil;

public class LuceneProviderManager {

	private static final int DEFAULT_MAX_BOOLEAN_CLAUSES = 10000;
	private static final String SLASH = "/";

	private String indexRootDirPath;
	
	// Access to this map is protected by "this"
	private Map<String,Object> lockMap = new HashMap<String,Object>();
	
	// Access to a particular entry in this map is protected by the corresponding lock object stored in the lockMap above.
	private ConcurrentMap<String,LuceneProvider> providerMap = new ConcurrentHashMap<String,LuceneProvider>();
	
	private Log logger = LogFactory.getLog(getClass());
	
	public LuceneProviderManager(String indexRootDirPath) throws LuceneException {
		if(indexRootDirPath == null)
			indexRootDirPath = "";
		if(!indexRootDirPath.endsWith(SLASH))
			indexRootDirPath = indexRootDirPath + SLASH;	
		if(indexRootDirPath.equals(SLASH))
			throw new IllegalArgumentException("Index root directory path must be specified");
		else
			this.indexRootDirPath = indexRootDirPath;
		
		logger.info("Index root directory path is set to [" + indexRootDirPath + "]");
		
		File indexRootDir = new File(indexRootDirPath);
		if (indexRootDir.exists()) {
			if (!indexRootDir.isDirectory()) {
				throw new LuceneException("The specified index root directory path [" + indexRootDirPath + "] exists, but is not a directory");
			}
			else {
				logger.info("The index root directory exists and is a directory");
			}
		} else {
			// Create the directory
			if(!indexRootDir.mkdirs()) {
				throw new LuceneException("Can not create index root directory [" + indexRootDirPath + "]");
			}
			else {
				logger.info("The index root directory is created");
			}
		}

		int maxBooleans = PropsUtil.getInt("lucene.max.booleans", DEFAULT_MAX_BOOLEAN_CLAUSES);
		BooleanQuery.setMaxClauseCount(maxBooleans);
		if(logger.isDebugEnabled())
			logger.debug("Max boolean clause count is set to " + maxBooleans);
	}
	
	public LuceneProvider getProvider(String indexName) throws LuceneException {
		// We control accesses to the global provider map such that only accesses to the "same" index are synchronized.
		// That is, we prevent more than one provider from being created for the same index. But for different indexes,
		// the creation process can proceed in parallel.
		Object lock = obtainLock(indexName);
		synchronized(lock) {
			LuceneProvider provider = providerMap.get(indexName);
			if(provider == null) {
				provider = createProvider(indexName);
				providerMap.put(indexName, provider);
			}
			return provider;
		}
	}
	
	public void close() {
		for(LuceneProvider provider: providerMap.values()) {
			provider.close();
		}
	}
	
	private synchronized Object obtainLock(String indexName) {
		Object lock = lockMap.get(indexName);
		if(lock == null) {
			lock = new Object();
			lockMap.put(indexName, lock);
		}
		return lock;
	}
	
	private LuceneProvider createProvider(String indexName) throws LuceneException {
		return new LuceneProvider(indexName, getIndexDirPath(indexName));
	}
	
	private String getIndexDirPath(String indexName) {
		return indexRootDirPath + indexName;
	}

}
