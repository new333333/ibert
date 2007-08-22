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
package com.sitescape.team.repository.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.repository.RepositoryServiceException;
import com.sitescape.team.repository.RepositorySession;
import com.sitescape.team.repository.RepositorySessionFactory;
import com.sitescape.team.util.FileHelper;
import com.sitescape.team.util.FilePathUtil;

public class FileRepositorySession implements RepositorySession {

	protected static Log logger = LogFactory.getLog(FileRepositorySession.class);

	private static final String VERSION_NAME_PREFIX = "_ssfversionfile_";
	private static final String VERSION_NAME_SUFFIX = "_";
	private static final String WORKING_STRING = "_ssfworkingfile_";
	private static final String TEMPFILE_PREFIX = "_ssftempfile_";
	private static final int MAX_TRY = 5;
	private static final long RETRY_INTERVAL = 1L;

	private FileRepositorySessionFactory factory;
	private String repositoryRootDir;
	
	public FileRepositorySession(FileRepositorySessionFactory factory, String repositoryRootDir) {
		this.factory = factory;
		this.repositoryRootDir = repositoryRootDir;
	}
	
	public void close() throws RepositoryServiceException, UncheckedIOException {
	}
	
	private String createVersionFileFromTemporaryFile(Binder binder, DefinableEntity entry,
			String relativeFilePath, File tempFile) throws RepositoryServiceException {
    	String versionName = null;
    	File versionFile = null;
    	for(int i = MAX_TRY; i > 0; i--) {
    		if(i != MAX_TRY) {
    			try {
					Thread.sleep(RETRY_INTERVAL);
				} 
    			catch(InterruptedException ignore) {}     			
    		}
    		versionName = newVersionName();
    		versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
    		if(tempFile.renameTo(versionFile))
    			return versionName;
    	}
    	
    	// If still here, bad news.
    	throw new RepositoryServiceException("Cannot write file " + relativeFilePath + " for entry " + entry.getId());
	}
	
	public String createVersioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath, InputStream in) throws RepositoryServiceException, UncheckedIOException {
		File fileDir = getFileDir(binder, entry, relativeFilePath);
		
		try {
			FileHelper.mkdirsIfNecessary(fileDir);

    		File tempFile = File.createTempFile(TEMPFILE_PREFIX, null, fileDir);

    		copyData(in, tempFile);
    
        	return createVersionFileFromTemporaryFile(binder, entry, relativeFilePath, tempFile);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void createUnversioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath, InputStream in) throws RepositoryServiceException, UncheckedIOException {
		File fileDir = getFileDir(binder, entry, relativeFilePath);
		
		try {
			FileHelper.mkdirsIfNecessary(fileDir);
			
			File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
			
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unversionedFile));
			
			try {
				FileCopyUtils.copy(in, bos);
			} 
			finally {
				try {
					bos.close();
				} catch (IOException e) {
					logger.warn(e); // Log and eat up.
				}
			}
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void update(Binder binder, DefinableEntity entry, 
			String relativeFilePath, InputStream in) throws RepositoryServiceException, UncheckedIOException {
		
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
		try {
			if(fileInfo == VERSIONED_FILE) {
				File workingFile = getWorkingFile(binder, entry, relativeFilePath);
				
				if(!workingFile.exists())
					throw new RepositoryServiceException("Cannot update file " + 
							relativeFilePath + " for entry " + entry.getTypedId() + 
							": It must be checked out first"); 
	
				copyData(in, workingFile);
			}
			else if(fileInfo == UNVERSIONED_FILE) {
				File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
				
				copyData(in, unversionedFile);
			}
			else {
				throw new RepositoryServiceException("Cannot update file " + relativeFilePath + 
						" for entry " + entry.getTypedId() + ": It does not exist"); 
			}
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}			
	}
	
	public void delete(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		// Since this operation may involve deleting multiple files, it is 
		// tricky to deal with error conditions precisely. For now, I'll 
		// take simplistic approach of throwing an exception on the first
		// I/O error. An alternative would be to stay in the method until
		// it tries all files, accumulate all errors that may arise during
		// the operation, and throw at the end one big exception that
		// summarizes all the errors. 
		
		// Unlike other methods that take different actions based on the
		// return value from fileInfo method, this method tries to account
		// for all circumstances inclusive. Under some rare error situations,
		// it is potentially possible that both unversioned and versioned
		// files exist on disk (in which case fileInfo's return indicates
		// that the file is unversioned, but that is rather implementation
		// dependent and not well defined at the API level). In such case,
		// we want delete method to be able to clean up the entire mess
		// so that the next invocation of fileInfo returns NON_EXISTING_FILE. 
		// At least this much is semantically clear and hence should be
		// enforced. 
		
		// Delete temp file if exists
		File workingFile = getWorkingFile(binder, entry, relativeFilePath);
		
		try {
			FileHelper.delete(workingFile);
		}
		catch(IOException e) {
			logger.error("Error deleting file [" + workingFile.getAbsolutePath() + "]");
			throw new UncheckedIOException(e);
		}
		
		// Delete all existing version files
		String[] versionFileNames = getVersionFileNames(binder, entry, relativeFilePath);
		
		File versionFile;
		for(int i = 0; i < versionFileNames.length; i++) {
			versionFile = getVersionFileFromVersionFileName(binder, entry, 
				relativeFilePath, versionFileNames[i]);
			try {
				FileHelper.delete(versionFile);
			}
			catch(IOException e) {
				logger.error("Error deleting file [" + versionFile.getAbsolutePath() + "]");			
				throw new UncheckedIOException(e);
			}
		}
		
		// Delete unversioned file if exists
		File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
		
		try {
			FileHelper.delete(unversionedFile);
		}
		catch(IOException e) {
			logger.error("Error deleting file [" + unversionedFile.getAbsolutePath() + "]");
			throw new UncheckedIOException(e);
		}			
	}


	public void delete(Binder binder, DefinableEntity entity) 
	throws RepositoryServiceException, UncheckedIOException {
		File dir = new File(getEntityDirPath(binder, entity));
		FileHelper.deleteRecursively(dir);
	}

	public void delete(Binder binder) 
	throws RepositoryServiceException, UncheckedIOException {
		File dir = new File(getBinderDirPath(binder));
		FileHelper.deleteRecursively(dir);
	}
	
	public void readUnversioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath, OutputStream out) throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot read file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is versioned"); 
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
			
			readFile(unversionedFile, out);
		}
		else {
			throw new RepositoryServiceException("Cannot read file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}
	}

	public InputStream readUnversioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot read file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is versioned"); 
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
			
			try {
				return new BufferedInputStream(new FileInputStream(unversionedFile));
			} catch (FileNotFoundException e) {
				throw new UncheckedIOException(e);
			}
		}
		else {
			throw new RepositoryServiceException("Cannot read file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}
	}
	
	public void readVersioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath, String versionName, OutputStream out) 
		throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File file;
			if(versionName != null)
				file = getVersionFile(binder, entry, relativeFilePath, versionName);
			else
				file = getLatestFile(binder, entry, relativeFilePath);
			readFile(file, out);
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot read file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is not versioned"); 
		}
		else {
			throw new RepositoryServiceException("Cannot read file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}
	}

	public InputStream readVersioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File file;
			if(versionName != null)
				file = getVersionFile(binder, entry, relativeFilePath, versionName);
			else
				file = getLatestFile(binder, entry, relativeFilePath);
			
			try {
				return new BufferedInputStream(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				throw new UncheckedIOException(e);
			}
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot read file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is not versioned"); 
		}
		else {
			throw new RepositoryServiceException("Cannot read file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}
	}

	public int fileInfo(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
		
		if(unversionedFile.exists()) {
			return UNVERSIONED_FILE;
		}
		else {
			String[] versionFileNames = getVersionFileNames(binder, entry, relativeFilePath);
			if(versionFileNames == null || versionFileNames.length == 0)
				return NON_EXISTING_FILE;
			else
				return VERSIONED_FILE;
		}
	}
	
	public DataSource getDataSourceVersioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath, String versionName, FileTypeMap fileTypeMap)		
		throws RepositoryServiceException, UncheckedIOException {
		File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
		FileDataSource fSource = new FileDataSource(versionFile);
		fSource.setFileTypeMap(fileTypeMap);
		return fSource;
	}	
	
	public void checkout(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File workingFile = getWorkingFile(binder, entry, relativeFilePath);
			
			if(!workingFile.exists()) { // It is not checked out
				try {
					// The latest version from this repository's point of view may not be
					// necessarily the latest version of the file from the application/user 
					// point of view (as strange it may sound) due to the potentially 
					// possible out-of-orderness between the db's metadata and the
					// repository's metadata. However, since a file is only checked out
					// to be immediately replaced with a new content, it should not 
					// really matter which version we use as the basis for initial state. 
					File latestVersionFile = getLatestVersionFile(binder, entry, relativeFilePath);
					
					if(latestVersionFile != null) {
						// Check it out by coping the content of the latest version of the file
						FileCopyUtils.copy(latestVersionFile, workingFile);
					}
					else {
						// This shouldn't occur.
						throw new RepositoryServiceException("No version file is found");
					}
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot checkout file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is not versioned"); 			
		}
		else {
			throw new RepositoryServiceException("Cannot checkout file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}
	}

	public void uncheckout(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File workingFile = getWorkingFile(binder, entry, relativeFilePath);
			
			if(workingFile.exists()) { // It is checked out
				// Delete the temp file
				try {
					FileHelper.delete(workingFile);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot uncheckout file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is not versioned"); 			
		}
		else {
			throw new RepositoryServiceException("Cannot uncheckout file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}
	}

	public String checkin(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File workingFile = getWorkingFile(binder, entry, relativeFilePath);
			
			if(workingFile.exists()) { // It is checked out
	        	return createVersionFileFromTemporaryFile(binder, entry, relativeFilePath, workingFile);
			}	
			else { // It is already checked in.
				return null;
			}
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot checkin file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is not versioned"); 			
		}
		else {
			throw new RepositoryServiceException("Cannot checkin file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}		
	}

	/*
	public boolean isCheckedOut(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		File workingFile = getWorkingFile(binder, entry, relativeFilePath);
		
		return workingFile.exists();
	}*/

	/*
	public boolean exists(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		String[] versionFileNames = getVersionFileNames(binder, entry, relativeFilePath);
		if(versionFileNames == null || versionFileNames.length == 0)
			return false;
		else
			return true;
	}*/

	public long getContentLengthUnversioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot get length of file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is versioned"); 			
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
			
			return unversionedFile.length();
		}
		else {
			throw new RepositoryServiceException("Cannot get length of file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}
	}
	
	public long getContentLengthVersioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath, String versionName) throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
			
			return versionFile.length();
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot get length of file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is not versioned"); 			
		}
		else {
			throw new RepositoryServiceException("Cannot get length of file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}
	}
	
	public void move(Binder binder, DefinableEntity entity, 
			String relativeFilePath, Binder destBinder, 
			DefinableEntity destEntity, String destRelativeFilePath)
	throws RepositoryServiceException, UncheckedIOException {
		File workingFile = getWorkingFile(binder, entity, relativeFilePath);
		
		if(workingFile.exists()) {
			File newWorkingFile = getWorkingFile(destBinder, destEntity, destRelativeFilePath);			
			move(workingFile, newWorkingFile);
		}
		
		String[] versionFileNames = getVersionFileNames(binder, entity, relativeFilePath);
		
		String versionName;
		File versionFile, newVersionFile;
		for(int i = 0; i < versionFileNames.length; i++) {
			versionName = getVersionName(versionFileNames[i]);
			versionFile = getVersionFileFromVersionFileName(binder, entity, relativeFilePath, versionFileNames[i]);
			newVersionFile = getVersionFile(destBinder, destEntity, destRelativeFilePath, versionName);
			move(versionFile, newVersionFile);
		}
		
		File unversionedFile = getUnversionedFile(binder, entity, relativeFilePath);

		if(unversionedFile.exists()) {
			File newUnversionedFile = getUnversionedFile(destBinder, destEntity, destRelativeFilePath);
			move(unversionedFile, newUnversionedFile);
		}
	}


	public void copy(Binder binder, DefinableEntity entity, String relativeFilePath, 
			Binder destBinder, DefinableEntity destEntity, String destRelativeFilePath) 
	throws RepositoryServiceException, UncheckedIOException {
		File workingFile = getWorkingFile(binder, entity, relativeFilePath);
		
		if(workingFile.exists()) {
			File newWorkingFile = getWorkingFile(destBinder, destEntity, destRelativeFilePath);			
			copy(workingFile, newWorkingFile);
		}
		
		String[] versionFileNames = getVersionFileNames(binder, entity, relativeFilePath);
		
		String versionName;
		File versionFile, newVersionFile;
		for(int i = 0; i < versionFileNames.length; i++) {
			versionName = getVersionName(versionFileNames[i]);
			versionFile = getVersionFileFromVersionFileName(binder, entity, relativeFilePath, versionFileNames[i]);
			newVersionFile = getVersionFile(destBinder, destEntity, destRelativeFilePath, versionName);
			copy(versionFile, newVersionFile);
		}
		
		File unversionedFile = getUnversionedFile(binder, entity, relativeFilePath);

		if(unversionedFile.exists()) {
			File newUnversionedFile = getUnversionedFile(destBinder, destEntity, destRelativeFilePath);
			copy(unversionedFile, newUnversionedFile);
		}
	}

	public void deleteVersion(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entity, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File versionFile = getVersionFile(binder, entity, relativeFilePath, versionName);
			
			try {
				FileHelper.delete(versionFile);
			}
			catch(IOException e) {
				logger.error("Error deleting file [" + versionFile.getAbsolutePath() + "]");
				throw new UncheckedIOException(e);
			}			
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot delete a version from the file " + 
					relativeFilePath + " for entry " + entity.getTypedId() + ": It is not versioned"); 
		}
		else {
			throw new RepositoryServiceException("Cannot delete a version from the file " + 
					relativeFilePath + " for entry " + entity.getTypedId() + ": It does not exist"); 
		}	
	}

	// For internal use only
	/*
	public List<String> getVersionNames(Binder binder, DefinableEntity entity,
			String relativeFilePath) throws RepositoryServiceException {
		String[] versionFileNames = getVersionFileNames(binder, entity, relativeFilePath);
		List<String> list = new ArrayList<String>(versionFileNames.length);
		for(int i = 0; i < versionFileNames.length; i++) 
			list.add(versionFileNames[i]);
		return list;
	}*/
	
	/**
	 * Returns an array of file names representing each version of the specified file. 
	 * 
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 */
	private String[] getVersionFileNames(Binder binder, DefinableEntity entry, String relativeFilePath) {
		File file = getFile(binder, entry, relativeFilePath);
		File fileDir = file.getParentFile();
		String fileName = file.getName();
		final String versionFileNamePrefix;
		final String versionFileNameSuffix;
		
		int index = fileName.lastIndexOf(".");
		if(index == -1) {
			// The file name doesn't contain extension 
			versionFileNamePrefix = fileName + VERSION_NAME_PREFIX;
			versionFileNameSuffix = null;
		}
		else {
			versionFileNamePrefix = fileName.substring(0, index) + VERSION_NAME_PREFIX;
			versionFileNameSuffix = fileName.substring(index);
		}
		
		String[] versionFileNames = fileDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(name.startsWith(versionFileNamePrefix) &&
						(versionFileNameSuffix == null || (name.endsWith(versionFileNameSuffix))))
					return true;
				else
					return false;	
			}
		});
		
		if(versionFileNames == null)
			versionFileNames = new String[0];
		
		return versionFileNames;
	}
	
	/**
	 * Returns latest version name or <code>null</code> if no version exists.
	 * 
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 */
	private String getLatestVersionName(Binder binder, DefinableEntity entry, String relativeFilePath) {
		String[] versionFileNames = getVersionFileNames(binder, entry, relativeFilePath);
		String latestVersionName = null;
		if(versionFileNames != null) {
			for(int i = 0; i < versionFileNames.length; i++) {
				String versionName = getVersionName(versionFileNames[i]);
				latestVersionName = getLaterVersionName(latestVersionName, versionName);
			}
		}
		return latestVersionName;
	}
	
	/**
	 * Return version name that is later of two.
	 * 
	 * @param versionName1
	 * @param versionName2
	 * @return
	 */
	private String getLaterVersionName(String versionName1, String versionName2) {
		if(versionName1 == null) {
			if(versionName2 == null)
				return null;
			else
				return versionName2;
		}
		else {
			if(versionName2 == null)
				return versionName1;
			else {
				long value1 = Long.valueOf(versionName1).longValue();
				long value2 = Long.valueOf(versionName2).longValue();
				return ((value1 > value2)? versionName1 : versionName2);
			}
		}
	}
	
	/**
	 * Given a name of version file, return its version name portion. 
	 * 
	 * @param versionFileName
	 * @return
	 */
	private String getVersionName(String versionFileName) {
		int versionNameBeginIndex = versionFileName.indexOf(VERSION_NAME_PREFIX) + VERSION_NAME_PREFIX.length();
		int versionNameEndIndex = versionFileName.indexOf(VERSION_NAME_SUFFIX, versionNameBeginIndex);
		
		return versionFileName.substring(versionNameBeginIndex, versionNameEndIndex);
	}
	
	/**
	 * Returns latest version file or <code>null</code> if no version file exists.
	 * 
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 */
	private File getLatestVersionFile(Binder binder, DefinableEntity entry, String relativeFilePath) {
		String latestVersionName = getLatestVersionName(binder, entry, relativeFilePath);
		if(latestVersionName != null)
			return getVersionFile(binder, entry, relativeFilePath, latestVersionName);
		else
			return null;
	}

	private File getFileDir(Binder binder, DefinableEntity entry, String relativeFilePath) {
		File file = getFile(binder, entry, relativeFilePath);
		
		return file.getParentFile();
	}
	
	/*
	 * Return a physical path for the binder
	 */
	private String getBinderDirPath(Binder binder) {
		return repositoryRootDir + FilePathUtil.getBinderDirPath(binder);
	}
	
	/*
	 * Return a physical path for the entity
	 */
	private String getEntityDirPath(Binder binder, DefinableEntity entry) {
		return repositoryRootDir + FilePathUtil.getEntityDirPath(binder, entry); 
	}
	
	private File getFile(Binder binder, DefinableEntity entry, String relativeFilePath) {
		return new File(getEntityDirPath(binder, entry), relativeFilePath);
	}
	
	private String newVersionName() {
		return String.valueOf(System.currentTimeMillis());
	}
	
	private File getVersionFileFromVersionFileName(Binder binder, DefinableEntity entry, String relativeFilePath, String versionFileName) {
		File file = getFile(binder, entry, relativeFilePath);
		
		return new File(file.getParent(), versionFileName);
	}
	
	private File getVersionFile(Binder binder, DefinableEntity entry, 
			String relativeFilePath, String versionName) {
		File file = getFile(binder, entry, relativeFilePath);
		
		String fileName = file.getName();
		String versionFileName;
		int index = fileName.lastIndexOf(".");
		if(index == -1) {
			// The file name doesn't contain extension 
			versionFileName = fileName + VERSION_NAME_PREFIX + versionName + 
				VERSION_NAME_SUFFIX;
		}
		else {
			versionFileName = fileName.substring(0, index) + VERSION_NAME_PREFIX + 
				versionName + VERSION_NAME_SUFFIX + "." + fileName.substring(index+1);
		}
		return new File(file.getParent(), versionFileName);		
	}
	
	private File getUnversionedFile(Binder binder, DefinableEntity entry, String relativeFilePath) {
		return getFile(binder, entry, relativeFilePath);
	}
	
	private File getWorkingFile(Binder binder, DefinableEntity entry, String relativeFilePath) {
		File file = getFile(binder, entry, relativeFilePath);
		
		String fileName = file.getName();
		String workingFileName;
		int index = fileName.lastIndexOf(".");
		if(index == -1) {
			// The file name doesn't contain extension 
			workingFileName = fileName + WORKING_STRING;
		}
		else {
			workingFileName = fileName.substring(0, index) + WORKING_STRING + 
				"." + fileName.substring(index+1);
		}
		return new File(file.getParent(), workingFileName);		
	}

	private void readFile(File file, OutputStream out) throws RepositoryServiceException, UncheckedIOException {
		try {
			FileCopyUtils.copy(new BufferedInputStream(new FileInputStream(file)), out);
		} 
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void copyData(InputStream in, File outFile) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
		
		try {
			FileCopyUtils.copy(in, bos);
		} 
		finally {
			try {
				bos.close();
			} catch (IOException e) {
				logger.warn(e); // Log and return normally.
			}
		}
	}
	
	private void move(File source, File target) throws UncheckedIOException {
		try {
			FileHelper.move(source, target);
		} catch (IOException e) {
			logger.error("Error moving file [" + source.getAbsolutePath() + "] to [" + target.getAbsolutePath() + "]");			
			throw new UncheckedIOException(e);
		}
	}
	
	private void copy(File source, File target) throws UncheckedIOException {
		try {
			FileCopyUtils.copy(source, target);
		} catch (IOException e) {
			logger.error("Error copyiing file [" + source.getAbsolutePath() + "] to [" + target.getAbsolutePath() + "]");			
			throw new UncheckedIOException(e);
		}
	}

	public RepositorySessionFactory getFactory() {
		return factory;
	}
	
	/**
	 * Returns latest snapshot of the file (which is either the latest version
	 * of the file or the working copy in progress which is created when the 
	 * file is checked out). It is assumed that the file is versioned.
	 * 
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 */
	private File getLatestFile(Binder binder, DefinableEntity entry, String relativeFilePath) {
		File workingFile = getWorkingFile(binder, entry, relativeFilePath);
		
		if(workingFile.exists()) {
			return workingFile;
		}
		else {
			File latestVersionFile = getLatestVersionFile(binder, entry, relativeFilePath);
			if(latestVersionFile != null)
				return latestVersionFile;
			else
				throw new RepositoryServiceException("The specified file does not exist");
		}
	}
}
