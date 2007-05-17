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
package com.sitescape.team.search.local;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.DisposableBean;

import com.sitescape.team.search.AbstractLuceneSessionFactory;
import com.sitescape.team.search.LuceneException;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.FileHelper;
import com.sitescape.team.util.LuceneUtil;

/**
 * @author Jong Kim
 *
 */
public class LocalLuceneSessionFactory extends AbstractLuceneSessionFactory 
implements DisposableBean, LocalLuceneSessionFactoryMBean {
    
	private String indexRootDir;
	private static ConcurrentHashMap<String,String> indexNameMap= new ConcurrentHashMap();
	

	public LuceneSession openSession(String indexName) {
		String indexDirPath = getIndexDirPath(indexName);
		
		try {
			FileHelper.mkdirsIfNecessary(indexDirPath);
		} catch (IOException e) {
			throw new LuceneException(e);
		}
		if (!indexNameMap.containsKey(indexName)) {
			indexNameMap.put(indexName, indexDirPath);
			LuceneUtil.unlock(indexDirPath);
		}
        return new LocalLuceneSession(indexDirPath);
    }
	
	public void setIndexRootDir(String indexRootDir) {
		if(indexRootDir.endsWith(Constants.SLASH))
			this.indexRootDir = indexRootDir;
		else
			this.indexRootDir = indexRootDir + Constants.SLASH;
	}
	
	public String getIndexRootDir() {
		return indexRootDir;
	}
	
	private String getIndexDirPath(String indexName) {
		return indexRootDir + indexName;
	}

	public void destroy() throws Exception {
		String indexDirPath = "";
		if (indexNameMap.size() == 0) return;
		Iterator iter = indexNameMap.keySet().iterator();
		while (iter.hasNext()) {
			indexDirPath = indexNameMap.get(iter.next());
			LuceneUtil.closeAll();
			LuceneUtil.unlock(indexDirPath);
		}
		
	}
}
