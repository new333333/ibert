package com.sitescape.ef.repository.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;

import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.util.FileHelper;

/**
 * An implementation of file-based repository which supports neither versioning
 * nor checkout/checkin features. This class should never be used for production
 * system. Also this implementation will NOT work if the pathname for file 
 * represents anything other than a flat file name.  
 * 
 * @author jong
 *
 */
public class FileRepositoryService implements RepositoryService {

	private String rootDirPath;
	
	public String getRootDirPath() {
		return rootDirPath;
	}

	public void setRootDirPath(String rootPath) throws IOException {
		this.rootDirPath = new File(rootPath).getCanonicalPath();
		
		if(!rootDirPath.endsWith(File.separator))
			rootDirPath += File.separator;
		
		FileHelper.mkdirsIfNecessary(rootDirPath);
	}

	public Object openRepositorySession() throws RepositoryServiceException {
		return null;
	}

	public void closeRepositorySession(Object session) throws RepositoryServiceException {
	}
	
	public void write(Object session, Folder folder, FolderEntry entry, String relativeFilePath, MultipartFile mf) throws RepositoryServiceException {
		
		// This implementation doesn't really follow the API spec in that
		// it completely ignores relativeFilePath. 
		
		File dir = getDir(folder, entry);
		
		if(!dir.exists())
			dir.mkdirs();
    	
        try {
        	mf.transferTo(new File(dir, mf.getOriginalFilename()));
		} catch (Exception e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void read(Object session, Folder folder, FolderEntry entry, String relativeFilePath, OutputStream out) throws RepositoryServiceException {
		String filePath = getFilePath(folder, entry, relativeFilePath);
		
		FileInputStream in = null;
		
		try {
			in = new FileInputStream(filePath);
		
			FileHelper.copyContent(in, out);
		}
		catch(IOException e) {
			throw new RepositoryServiceException(e);
		}
		finally {
			if(in != null) {
				try {
					in.close();
				} 
				catch (IOException e) {}
			}
		}	
	}

	public void readVersion(Object session, Folder folder, FolderEntry entry, String relativeFilePath, String versionName, OutputStream out) throws RepositoryServiceException {
		throw new UnsupportedOperationException();
	}

	public List getVersionNames(Object session, Folder folder, FolderEntry entry, String relativeFilePath) throws RepositoryServiceException {
		throw new UnsupportedOperationException();
	}

	public DataSource getDataSource(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath, FileTypeMap fileTypeMap)		
		throws RepositoryServiceException {
		String filePath = getFilePath(folder, entry, relativeFilePath);
		FileDataSource fSource = new FileDataSource(filePath);
		fSource.setFileTypeMap(fileTypeMap);
		return fSource;
	}
	public DataSource getDataSourceVersion(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath, String versionName, FileTypeMap fileTypeMap)		
		throws RepositoryServiceException {
		String filePath = getFilePath(folder, entry, relativeFilePath);
		FileDataSource fSource = new FileDataSource(filePath);
		fSource.setFileTypeMap(fileTypeMap);
		return fSource;
	}	
	public void checkout(Object session, Folder folder, FolderEntry entry, String filePath) throws RepositoryServiceException {
		throw new UnsupportedOperationException();
	}

	public void uncheckout(Object session, Folder folder, FolderEntry entry, String relativeFilePath) throws RepositoryServiceException {
		throw new UnsupportedOperationException();
	}

	public String checkin(Object session, Folder folder, FolderEntry entry, String filePath) throws RepositoryServiceException {
		throw new UnsupportedOperationException();
	}

	public boolean isCheckedOut(Object session, Folder folder, FolderEntry entry, String relativeFilePath) throws RepositoryServiceException {
		throw new UnsupportedOperationException();
	}

	public boolean supportVersioning() {
		return false;
	}
	
	public boolean supportVersionDeletion() {
		throw new UnsupportedOperationException();
	}

	public boolean exists(Object session, Folder folder, FolderEntry entry, String relativeFilePath) throws RepositoryServiceException {
		String filePath = getFilePath(folder, entry, relativeFilePath);
		return new File(filePath).exists();
	}

	private String getDirPath(Folder folder, FolderEntry entry) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		
		return new StringBuffer(rootDirPath).append(zoneName).append(File.separator).append(folder.getId()).append(File.separator).append(entry.getId()).append(File.separator).toString();
	}
	
	private File getDir(Folder folder, FolderEntry entry) {
		return new File(getDirPath(folder, entry));
	}
	
	private String getFilePath(Folder folder, FolderEntry entry, String fileName) {
		return getDirPath(folder, entry) + fileName;
	}
}
