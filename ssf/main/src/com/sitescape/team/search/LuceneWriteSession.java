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
package com.sitescape.team.search;

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

public interface LuceneWriteSession extends LuceneSession {
	/**
	 * Add a document.
	 * 
	 * @param doc
	 * @throws LuceneException
	 */
	public void addDocument(Document doc) throws LuceneException;

	/**
	 * Add a collection of documents.
	 * 
	 * @param doc
	 * @throws LuceneException
	 */
	public void addDocuments(Collection docs) throws LuceneException;

	/**
	 * Update the field in the document identified by the uid. 
	 * 
	 * @param uid
	 * @param fieldname
	 * @param fieldvalue
	 * @throws LuceneException
	 */
	//public void updateDocument(String uid, String fieldname, String fieldvalue)
	//		throws LuceneException;

	/**
	 * Update all documents matching the query.
	 * 
	 * @param query
	 * @param fieldname
	 * @param fieldvalue
	 * @throws LuceneException
	 */
	public void updateDocuments(Query query, String fieldname, String fieldvalue)
			throws LuceneException;
	
	/**
	 * Delete the document identified by the uid. 
	 * 
	 * @param uid
	 * @throws LuceneException
	 */
	public void deleteDocument(String uid) throws LuceneException;

	/**
	 * Delete all documents matching the term.
	 * 
	 * @param term
	 * @throws LuceneException
	 */
	public void deleteDocuments(Term term) throws LuceneException;

	/**
	 * Apply the query and delete all matching documents.
	 * 
	 * @param query
	 * @throws LuceneException
	 */
	public void deleteDocuments(Query query) throws LuceneException;

	/**
	 * Force the <code>LuceneSession</code> to flush.
	 * <p>
	 * Flushing is the process of synchronizing the underlying persistent store
	 * (ie, index files on disk) with persistable state held in memory.
	 * 
	 * @throws LuceneException
	 *
	 */
	public void flush() throws LuceneException;

	/**
	 * Force the <code>LuceneSession</code> to optimize the index. This helps reduce
	 * the size of the index by removing deleted docs and renumbering the inner docs.
	 * 
	 * @throws LuceneException
	 *
	 */
	public void optimize() throws LuceneException;
	
	/**
	 * Force the <code>LuceneSession</code> to clear the contents of the index.
	 * 
	 * @throws LuceneException
	 *
	 */
	public void clearIndex() throws LuceneException;

	/**
	 * Note: This modifies the javadoc in {@link LuceneSession#close()}.
	 * 
	 * End the <code>LuceneSession</code> by disconnecting from the Lucene
	 * service and cleaning up. Note that this does NOT implicitly perform
	 * <code>flush</code> operation. In other words, <code>flush</code>
	 * must be invoked explicitly by the caller before closing the session
	 * if that was intended. Once <code>close</code> method is called, the 
	 * session object is no longer usable.
	 * 
	 * @throws LuceneException
	 *
	 */
	public void close() throws LuceneException;

}
