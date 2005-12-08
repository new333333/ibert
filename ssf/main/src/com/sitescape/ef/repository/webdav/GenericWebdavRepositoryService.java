package com.sitescape.ef.repository.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.WebdavResource;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.util.Constants;
import com.sitescape.ef.util.FileHelper;

public class GenericWebdavRepositoryService extends AbstractWebdavResourceFactory implements RepositoryService {

	protected Log logger = LogFactory.getLog(getClass());
	
	public WebdavResource openSession(String userName, String password) throws IOException {
		HttpURL hrl = new HttpURL(httpUrl);
		hrl.setUserinfo(userName, password);
		WebdavResource wdr = new WebdavResource(hrl);
		
		//WebdavUtil.dump(wdr);
		
		return wdr;
	}

	public void write(Folder folder, FolderEntry entry, String relativeFilePath, MultipartFile mf) throws RepositoryServiceException {
		try {
			writeInternal(folder, entry, relativeFilePath, mf);
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void read(Folder folder, FolderEntry entry, String relativeFilePath, OutputStream out) throws RepositoryServiceException {
		
		List versions = fileVersionsURIs(folder, entry, relativeFilePath);

		
		
		try {
			readInternal(folder, entry, relativeFilePath, out);
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void readVersion(String fileVersionURI, OutputStream out) throws RepositoryServiceException {
		try {
			readInternal(fileVersionURIToResourcePath(fileVersionURI), out);
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}
	
	/**
	 * Translates the file version URI into a resource path that can be used for
	 * Webdav request to the current server. This hook is useful when the 
	 * backing Webdav server has been migrated and somehow the server paths for 
	 * the versioned resources have subsequently changed.
	 * This method provides a hook for subclass to override so that the URI can 
	 * be translated from old format (which users may still have around, for
	 * example, in email notifications, etc.) into new one without major effort.
	 * 
	 * @param fileVersionURI
	 * @return
	 */
	protected String fileVersionURIToResourcePath(String fileVersionURI) {
		return fileVersionURI; // this is default implementation
	}

	public List fileVersionsURIs(Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		try {
			WebdavResource wdr = openSession();
			
			try {
				String resourcePath = getResourcePath(folder, entry, relativeFilePath);
				
				String value = WebdavUtil.getSingleHrefValue(wdr, 
						resourcePath, "version-history");
				
				if(value == null)
					throw new RepositoryServiceException("Cannot find version-history property for " + resourcePath);
				
				return WebdavUtil.getHrefValues(wdr, value, "version-set");
			}
			catch(IOException e) {
				logError(wdr);
				throw e;
			}
			finally {
				wdr.close();
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void checkout(Folder folder, FolderEntry entry, String relativeFilePath) throws RepositoryServiceException {
		try {
			WebdavResource wdr = openSession();
			
			try {
				boolean result = wdr.checkoutMethod(getResourcePath(folder, entry, relativeFilePath));
				if(!result)
					throw new RepositoryServiceException("Failed to checkout");
			}
			catch(IOException e) {
				logError(wdr);
				throw e;
			}
			finally {
				wdr.close();
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void checkin(Folder folder, FolderEntry entry, String relativeFilePath) throws RepositoryServiceException {
		try {
			WebdavResource wdr = openSession();
			
			try {
				boolean result = wdr.checkinMethod(getResourcePath(folder, entry, relativeFilePath));
				if(!result)
					throw new RepositoryServiceException("Failed to checkin");
			}
			catch(IOException e) {
				logError(wdr);
				throw e;
			}
			finally {
				wdr.close();
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public boolean supportVersioning() {
		return true;
	}

	public boolean supportCheckout() {
		return true;
	}

	public boolean supportVersionDeletion() {
		// It appears that the Slide server we use does not allows this.
		// It doesn't appear to me to be a restriction by the DeltaV spec
		// itself, but some Slide specific misbehavior (or mis-configuration).
		// It requires more investigation...
		return false; // for now
	}

	private void writeInternal(Folder folder, FolderEntry entry, String relativeFilePath, 
			MultipartFile mf) throws IOException {
		WebdavResource wdr = openSession();
		
		boolean result;
		
		try {
			// Get the path for the entry containing the file. 
			String entryDirPath = getEntryDirPath(folder, entry);

			/*
			boolean b = wdr.headMethod("/slide/files");
			System.out.println("Status Code: " + wdr.getStatusCode());
			System.out.println("Status Message: " + wdr.getStatusMessage());
			b = wdr.headMethod("/slide/files/sitescape");
			System.out.println("Status Code: " + wdr.getStatusCode());
			System.out.println("Status Message: " + wdr.getStatusMessage());
			*/
			
			// If necessary, create containing collections (recursively) before
			// writing the file itself. 
			WebdavUtil.createCollectionIfNecessary(wdr, entryDirPath);
			
			// Get the path for the file resource. 
			String resourcePath = getResourcePath(entryDirPath, relativeFilePath);
			
			if(wdr.headMethod(resourcePath)) {
				// The file resource already exists.
				// Since we always put file resource under version control as 
				// soon as it is created, it's largely unnecessary to do it
				// again here. But we can think of a couple of scenarios where
				// the file may not have been put into version control propertly.
				// For example, the file was created successfully but the 
				// subsequent command for turning it into a version controlled
				// resource failed. Another example would be that the file was
				// created through some other means (i.e., other than this
				// interface), although it's very unlikely. 
				// The following command will be noop if the resource was already
				// version controlled. 
				result = wdr.versionControlMethod(resourcePath);
				// Write the file creating a new version. 
				result = wdr.putMethod(resourcePath, mf.getInputStream());
			}
			else {
				// The file resource does not exist. 
				// We must write the file first. 
				result = wdr.putMethod(resourcePath, mf.getInputStream());
				// Put the file under version control. 
				result = wdr.versionControlMethod(resourcePath);
			}
		}
		catch(IOException e) {
			logError(wdr);
			throw e;
		}
		finally {
			wdr.close();
		}
	}
	
	private void readInternal(Folder folder, FolderEntry entry, String relativeFilePath, OutputStream out) throws IOException {
		readInternal(getResourcePath(folder, entry, relativeFilePath), out);
	}
	
	private WebdavResource openSession() throws IOException {
		// How do we get WebDAV username/password for individual users??
		return openSession("root", "root");
	}
	
	private void readInternal(String resourcePath, OutputStream out) throws IOException {
		WebdavResource wdr = openSession();
				
		boolean result = false;
		try {
			/* for debugging
			WebdavUtil.dumpProp(wdr, "/slide/history/101/1.2", "version-name");
			WebdavUtil.dumpProp(wdr, "/slide/history/101/1.2", "no-version-delete");	
			WebdavUtil.dumpProp(wdr, "/slide/history/101/1.2", "predecessor-set");	
			WebdavUtil.dumpProp(wdr, "/slide/files/sitescape/document/liferay.com/74/493/junk.txt", "no-version-delete");	
			
			if(wdr.headMethod("/slide/history/101/1.2")) { // failed
				result = wdr.deleteMethod("/slide/history/101/1.2");
			}
			
			if(wdr.headMethod("/slide/history/1")) { // successful
				result = wdr.deleteMethod("/slide/history/1");
			}
			
			if(wdr.headMethod("/slide/files/sitescape/document/liferay.com/74/489/license.html")) // successful
				result = wdr.deleteMethod("/slide/files/sitescape/document/liferay.com/74/489/license.html");
			
			String value = WebdavUtil.getSinglePropertyValue(wdr, "/slide/files/sitescape/document/liferay.com/74/493/junk.txt", "version-history");
			value = WebdavUtil.getHrefValue(value);
			WebdavUtil.dumpProp(wdr, "/slide/history/101", "version-set");
			*/
			
			InputStream is = wdr.getMethodData(resourcePath);
			
			FileHelper.copyContent(is, out);

			WebdavUtil.dumpAllProps(wdr, resourcePath); // for debugging	
			
			WebdavUtil.dumpAllProps(wdr, "/slide/history/101"); // for debugging
		}
		catch(IOException e) {
			logError(wdr);
			throw e;
		}
		finally {
			wdr.close();
		}
	}
	
	private void logError(WebdavResource wdr) {
		// Log the HTTP status code and error message.
		logger.error("status code=" + wdr.getStatusCode() + ", status message=[" + wdr.getStatusMessage() + "]");
		// The exception object associated with the error will be logged higher up.		
	}

	private String getEntryDirPath(Folder folder, FolderEntry entry) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		
		return new StringBuffer(getDocRootDir()).
			append(zoneName).
			append(Constants.SLASH).
			append(folder.getId()).
			append(Constants.SLASH).
			append(entry.getId()).
			append(Constants.SLASH).
			toString();
	}
	
	private String getResourcePath(String entryDirPath, String relativeFilePath) {
		return entryDirPath + relativeFilePath;
	}
	
	private String getResourcePath(Folder folder, FolderEntry entry, String relativeFilePath) {
		return getResourcePath(getEntryDirPath(folder, entry), relativeFilePath);
	}
}
