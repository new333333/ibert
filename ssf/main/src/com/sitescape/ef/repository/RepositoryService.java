package com.sitescape.ef.repository;

import java.io.OutputStream;

import org.springframework.web.multipart.MultipartFile;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Binder;

public interface RepositoryService {

	/**
	 * Opens a session with the repository system. 
	 * 
	 * @return
	 */
	public Object openRepositorySession() throws RepositoryServiceException;
	
	/**
	 * Closes the session with the repository system.
	 * 
	 * @param session
	 */
	public void closeRepositorySession(Object session) throws RepositoryServiceException;
	
	/**
	 * Creates a new file resource in the repository system. 
	 * <p>
	 * The first version of the resource is created and its version name is
	 * returned. 
	 * <p>
	 * If the underlying repository system does not support versioning, it
	 * returns <code>null</code>.
	 * 
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param mf
	 * @return
	 * @throws RepositoryServiceException
	 */
	public String create(Object session, Binder binder, Entry entry, 
			String relativeFilePath, MultipartFile mf) 
		throws RepositoryServiceException;
	
	/**
	 * Updates the existing file resource.  
	 * <p>
	 * The resource is expected to have been checked out prior to invoking this 
	 * method. The changes made to the repository through this method are made 
	 * permanent when {@link #checkin} is executed.
	 * 
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param mf
	 * @throws RepositoryServiceException
	 */
	public void update(Object session, Binder binder, Entry entry, 
			String relativeFilePath, MultipartFile mf) 
		throws RepositoryServiceException;
	
	/**
	 * Reads the content of the specified file resource from the repository 
	 * system. 
	 * <p>
	 * The content being read is identical to the latest checked-in version 
	 * of the resource.
	 * 
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param out
	 * @throws RepositoryServiceException
	 */
	public void read(Object session, Binder binder, Entry entry, 
			String relativeFilePath, OutputStream out) 
		throws RepositoryServiceException;
	
	/**
	 * Reads from the repository system the content of the specified version 
	 * of the file resource. 
	 * 
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param versionName the name of the version
	 * @param out
	 * @throws RepositoryServiceException thrown if the specified version does
	 * not exist, or if some other error occurs
	 */
	public void readVersion(Object session, Binder binder, Entry entry, 
			String relativeFilePath, String versionName, OutputStream out) 
		throws RepositoryServiceException;
	
	/**
	 * Return a datasource that will be used to read the file to a mime message
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param fileTypeMap
	 * @return
	 * @throws RepositoryServiceException
	 */
	public DataSource getDataSource(Object session, Binder binder, Entry entry, 
				String relativeFilePath, FileTypeMap fileTypeMap) throws RepositoryServiceException;
	/**
	 * 
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param versionName the name of the version
	 * @param fileTypeMap 
	 * @return
	 * @throws RepositoryServiceException
	 */
	public DataSource getDataSourceVersion(Object session, Binder binder, Entry entry, 
			String relativeFilePath, String versionName, FileTypeMap fileTypeMap) throws RepositoryServiceException;

	/**
	 * Returns the names of the versions for the specified file resource. 
	 * The specified file resource must exist. 
	 * 
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	//public List getVersionNames(Object session, Binder binder, Entry entry,
	//		String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Checks out the specified file resource.
	 * <p>
	 * If the resource is already checked out (by anyone), this method has no 
	 * effect. If the specified resource does not exist, it throws an exception.
	 * <p>
	 * Important: Notice the semantics of this method; It has nothing to do with
	 * granting an exclusive access to the resource to the caller. Checkout/
	 * checkin is merely a mechanism whereby creation of new versions can be
	 * controlled, which is orthogonal to the concept of locking issued under
	 * specific user. Locking is used to allow a user to temporarily lock 
	 * resources in order to prevent other users from changing them. The lock
	 * functionality is neither exposed nor required by this API. 
	 * 
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @throws RepositoryServiceException
	 */
	public void checkout(Object session, Binder binder, Entry entry, 
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Cancels the checkout for the specified file resource. 
	 * <p>
	 * If the resource is not checked out, this method has no effect. 
	 * 
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @throws RepositoryServiceException
	 */
	public void uncheckout(Object session, Binder binder, Entry entry, 
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Checks in the specified file resource and returns the name of the new
	 * version created. 
	 * <p>
	 * If the resource is already checked in, this method has no effect but
	 * returns the name of the current checked-in version of the resource.  
	 * <p>
	 * If the underlying repository system does not support versioning, it
	 * returns <code>null</code>.
	 * 
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. 
	 * This may simply be the name of the file. 
	 * @return the name of the new version
	 * @throws RepositoryServiceException
	 */
	public String checkin(Object session, Binder binder, Entry entry, 
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Returns whether the specified file resource is currently checked out
	 * or not.
	 * 
	 * @param
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	public boolean isCheckedOut(Object session, Binder binder, Entry entry, 
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Returns whether the specified file resource exists or not. 
	 * 
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 */
	public boolean exists(Object session, Binder binder, Entry entry, 
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Returns the length (in byte) of the content of the specific file resource. 
	 *  
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	public long getContentLength(Object session, Binder binder, Entry entry,
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Returns the length (in byte) of the content of the specific version
	 * of the file resource. 
	 *  
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @param versionName
	 * @return
	 * @throws RepositoryServiceException
	 */
	public long getContentLength(Object session, Binder binder, Entry entry,
			String relativeFilePath, String versionName) throws RepositoryServiceException;
	
	/**
	 * Returns whether the repository service allows users to delete individual
	 * versions of a resource without deleting the entire resource. In other
	 * words, for repository system that does not support this, the only way
	 * to remove a particular resource is to delete it in its entirety which
	 * deletes all of its versions as well. 
	 * 
	 * @return
	 */
	public boolean supportVersionDeletion();
}
