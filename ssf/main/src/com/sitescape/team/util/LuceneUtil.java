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
package com.sitescape.team.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import com.sitescape.team.lucene.ChineseAnalyzer;
import com.sitescape.team.lucene.SsfIndexAnalyzer;

public class LuceneUtil {

	protected static Log logger = LogFactory.getLog(LuceneUtil.class);

	private static final int SEARCH = 1;
	private static final int READ = 2;
	private static final int WRITE = 3;

	private static Analyzer defaultAnalyzer = new SsfIndexAnalyzer();

	private static int prevState = SEARCH;

	private static IndexWriter indexWriter = null;
	private static IndexReader indexReader = null;
	private static IndexSearcher indexSearcher = null;

	
	
	public static IndexReader getReader(String indexPath) throws IOException {
		synchronized (LuceneUtil.class) {
			switch (prevState) {
			case (READ):
				if (indexReader != null)
					return indexReader;
			case (SEARCH):
				indexReader = getNewReader(indexPath);
				break;
			case (WRITE):
				closeWriter();
				closeReader();
				indexReader = getNewReader(indexPath);
			}
			prevState = READ;
		}
		return indexReader;
	}

	public static IndexSearcher getSearcher(String indexPath)
			throws IOException {
		synchronized (LuceneUtil.class) {
			switch (prevState) {
			case (SEARCH):
				if (indexSearcher != null)
					return indexSearcher;
				indexSearcher = getNewSearcher(indexPath);
				break;
			case (READ):
				closeReader();
				prevState = SEARCH;
				indexSearcher = getNewSearcher(indexPath);
				break;
			case (WRITE):
				if (indexWriter != null) indexWriter.flush();
				indexSearcher = getNewSearcher(indexPath);
			}
		}
		return indexSearcher;
	}

	public static IndexWriter getWriter(String indexPath, boolean create)
			throws IOException {
		synchronized (LuceneUtil.class) {
			switch (prevState) {
			case (WRITE):
				if (indexWriter != null && !create)
					break;
			case (SEARCH):
				indexWriter = getNewWriter(indexPath, create);
				break;
			case (READ):
				closeReader();
				indexWriter = getNewWriter(indexPath, create);
			}
			prevState = WRITE;
		}
		return indexWriter;
	}

	private static IndexReader getNewReader(String indexPath)
			throws IOException {
		try {
			indexReader = IndexReader.open(indexPath);
		} catch (IOException ioe) {
			if (!indexExists(indexPath)) {
				if (initializeIndex(indexPath)) {
					indexReader = IndexReader.open(indexPath);
					return indexReader;
				}
			} else {
				try {
					// force unlock of the directory
					IndexReader.unlock(FSDirectory.getDirectory(indexPath));
					indexReader = IndexReader.open(indexPath);
				} catch (IOException e) {
					throw e;
				}
			}
		}
		return indexReader;

	}

	private static IndexSearcher getNewSearcher(String indexPath)
			throws IOException {
		IndexSearcher indexSearcher = null;
		try {
			indexSearcher = new IndexSearcher(indexPath);
		} catch (IOException ioe) {
			try {
				if (indexExists(indexPath)) {
					// force unlock of the directory
					IndexReader.unlock(FSDirectory.getDirectory(indexPath));
					indexSearcher = new IndexSearcher(indexPath);
				} else {
					if (initializeIndex(indexPath)) {
						indexSearcher = new IndexSearcher(indexPath);
						return indexSearcher;
					} else {
						throw ioe;
					}
				}
			} catch (IOException e) {
				throw e;
			}
		}
		return indexSearcher;
	}

	private static IndexWriter getNewWriter(String indexPath, boolean create)
			throws IOException {
		try {
			indexWriter = new IndexWriter(indexPath, new SsfIndexAnalyzer(),
					create);
		} catch (Exception ie) {
			try {
				// force unlock of the directory
				IndexReader.unlock(FSDirectory.getDirectory(indexPath));
				indexWriter = new IndexWriter(indexPath,
						new SsfIndexAnalyzer(), create);
			} catch (IOException e) {
				throw e;
			}
		}
		indexWriter.setUseCompoundFile(false);
		return indexWriter;
	}

	public static IndexSearcher getSearcher(IndexReader reader) {
		synchronized (LuceneUtil.class) {
			indexSearcher = new IndexSearcher(reader);
			// prevState = READSEARCH;
		}
		return indexSearcher;
	}

	public static IndexWriter getWriter(String indexPath) throws IOException {
		return getWriter(indexPath, false);
	}

	private static boolean initializeIndex(String indexPath) throws IOException {
		synchronized (LuceneUtil.class) {
			if (IndexReader.indexExists(indexPath)) {
				// Index already exists at the specified directory.
				// We shouldn't initialize index in this case.
				return false;
			} else {
				// No index exists at the specified directory. Create a new one.
				getWriter(indexPath, true);
				indexWriter.close();
				indexWriter = null;
				return true;
			}
		}
	}

	public static boolean indexExists(String indexPath) {
		if (IndexReader.indexExists(indexPath))
			return true;
		else
			return false;
	}

	public static void closeAll() {
		synchronized (LuceneUtil.class) {
			closeWriter();
			closeReader();
			closeSearcher();
		}
	}

	// needed for searchers that are opened on existing readers.
	public static void closeSearcher() {
		try {
			indexSearcher.close();
		} catch (Exception se) {
		}
		indexSearcher = null;
	}

	public static void closeReader() {
		try {
			indexReader.close();
		} catch (Exception se) {
		}
		indexReader = null;
	}

	public static void closeWriter() {
		try {
			indexWriter.close();
		} catch (Exception se) {
		}
		indexWriter = null;
	}

	// unlock the index
	public static void unlock(String indexPath) {
		try {
			closeAll();
		} catch (Exception e) {
		}
		try {
			if (indexExists(indexPath)) {
				// force unlock of the directory
				IndexReader.unlock(FSDirectory.getDirectory(indexPath));
			}
		} catch (Exception e) {
			logger.info("Can't unlock Lucene lockfile: " + e.toString());
		}
		try {
			File lockFile = new File(indexPath + "write.lock");
			if (lockFile.exists()) {
				lockFile.delete();
			}
		} catch (Exception e) {
			logger.info("Can't delete Lucene lockfile: " + e.toString());
		}
	}

	// return the correct analyzer based on the text passed in.
	public static Analyzer getAnalyzer(String snippet) {
		// pass the snippet to the language taster and see which
		// analyzer to use
		String language = LanguageTaster.taste(snippet.toCharArray());
		if (language.equalsIgnoreCase(LanguageTaster.DEFAULT)) {
			return defaultAnalyzer;
		} else if (language.equalsIgnoreCase(LanguageTaster.CJK)) {
			return new ChineseAnalyzer();
		} else if (language.equalsIgnoreCase(LanguageTaster.HEBREW)) {
			// return new HEBREWAnalyzer;
			Analyzer analyzer = defaultAnalyzer;
			String aName = SPropsUtil.getString("lucene.hebrew.analyzer", "");
			if (!aName.equalsIgnoreCase("")) {
				// load the hebrew analyzer here
				try {
					Class hebrewClass = ReflectHelper.classForName(aName);
					analyzer = (Analyzer) hebrewClass.newInstance();
				} catch (Exception e) {
					logger.error("Could not initialize hebrew analyzer class: "
							+ e.toString());
				}
			}
			return analyzer;
		} else {
			// return new ARABICAnalyzer;
			Analyzer analyzer = defaultAnalyzer;
			String aName = SPropsUtil.getString("lucene.arabic.analyzer", "");
			if (!aName.equalsIgnoreCase("")) {
				// load the arabic analyzer here
				try {
					Class arabicClass = ReflectHelper.classForName(aName);
					analyzer = (Analyzer) arabicClass.newInstance();
				} catch (Exception e) {
					logger.error("Could not initialize arabic analyzer class: "
							+ e.toString());
				}
			}
			return analyzer;
		}
	}

}
