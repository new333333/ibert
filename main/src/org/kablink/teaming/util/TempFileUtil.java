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
package org.kablink.teaming.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.springframework.util.FileCopyUtils;


public class TempFileUtil {
	
	/**
	 * Create a temporary file. The created temporary file is set to be deleted
	 * when the virtual machine terminates.
	 * 
	 * @param prefix The prefix string to be used in generating the file's name;
	 * must be at least three characters long
	 * @return
	 */
	public static File createTempFile(String prefix) {
		return createTempFile(prefix, null, getTempFileDir(), true);
	}
	
	/**
	 * Create a temporary file and set its initial content. 
	 * The created temporary file is set to be deleted when the virtual machine 
	 * terminates.
	 * 
	 * @param prefix The prefix string to be used in generating the file's name;
	 * must be at least three characters long
	 * @param content The initial content of the file 
	 * @return
	 */
	public static File createTempFileWithContent(String prefix, InputStream content) {
		return createTempFileWithContent(prefix, null, getTempFileDir(), true, content);
	}
	
	/**
	 * Create a temporary file. The created temporary file is set to be deleted
	 * when the virtual machine terminates.
	 * 
	 * @param prefix The prefix string to be used in generating the file's name;
	 * must be at least three characters long
	 * @param fileDir The directory in which the file is to be created, or 
	 * <code>null</code> if the default temporary-file directory is to be used
	 * @return
	 */
	public static File createTempFile(String prefix, File fileDir) {
		return createTempFile(prefix, null, fileDir, true);
	}
	
	/**
	 * Create a temporary file and set its initial content. 
	 * The created temporary file is set to be deleted when the virtual machine 
	 * terminates.
	 * 
	 * @param prefix The prefix string to be used in generating the file's name;
	 * must be at least three characters long
	 * @param fileDir The directory in which the file is to be created, or 
	 * <code>null</code> if the default temporary-file directory is to be used
	 * @param content The initial content of the file 
	 * @return
	 */
	public static File createTempFileWithContent(String prefix, File fileDir, InputStream content) {
		return createTempFileWithContent(prefix, null, fileDir, true, content);
	}
	
	/**
	 * Create a temporary file.
	 * 
	 * @param prefix The prefix string to be used in generating the file's name;
	 * must be at least three characters long
	 * @param suffix The suffix string to be used in generating the file's name; 
	 * may be <code>null</code>, in which case the suffix ".tmp" will be used
	 * @param fileDir The directory in which the file is to be created, or 
	 * <code>null</code> if the default temporary-file directory is to be used
	 * @param deleteOnExit If <code>true</code>, the created file is set to be
	 * deleted when the virtual machine terminates. 
	 * @return
	 * @throws UncheckedIOException
	 */
	public static File createTempFile(String prefix, String suffix, File fileDir,
			boolean deleteOnExit)
		throws UncheckedIOException {
		try {
			if(fileDir != null && !fileDir.exists())
				FileHelper.mkdirs(fileDir);
			
			File file = doCreateTempFile(prefix, suffix, fileDir);
			
			if(deleteOnExit)
				file.deleteOnExit();
			
			return file;
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}	
	}
	
	/**
	 * Create a temporary file.
	 * 
	 * @param prefix The prefix string to be used in generating the file's name;
	 * must be at least three characters long
	 * @param suffix The suffix string to be used in generating the file's name; 
	 * may be <code>null</code>, in which case the suffix ".tmp" will be used
	 * @param fileDir The directory in which the file is to be created, or 
	 * <code>null</code> if the default temporary-file directory is to be used
	 * @param deleteOnExit If <code>true</code>, the created file is set to be
	 * deleted when the virtual machine terminates. 
	 * @param content The initial content of the file 
	 * @return
	 * @throws UncheckedIOException
	 */
	public static File createTempFileWithContent(String prefix, String suffix, File fileDir, 
			boolean deleteOnExit, InputStream content)
		throws UncheckedIOException {
		File tempFile = TempFileUtil.createTempFile(prefix, suffix, fileDir, deleteOnExit);
		
		try {
			FileCopyUtils.copy(content, new BufferedOutputStream(new FileOutputStream(tempFile)));
			
			return tempFile;
		}
		catch(IOException e) {
			tempFile.delete();
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Opens a previously created temporary file and returns a stream to it.
	 * 
	 * @param fileHandle The name of the file as returned by File.getName().
	 */
	public static InputStream openTempFile(String fileHandle) throws UncheckedIOException {
		try {
			return new FileInputStream(getTempFileByName(fileHandle));
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Returns a File object on a previously created temporary file.
	 * @param fileHandle The name of the file as returned by File.getName().
	 * @return
	 */
	public static File getTempFileByName(String fileHandle) {
		File junk = new File(fileHandle);
		fileHandle = junk.getName();

		return new File(getTempFileDirPath(), fileHandle);
	}

	/**
	 * This method differs from the rest of the public methods in this class in that
	 * this method returns a sub-directory within the temp area rather than returning
	 * a temporary file. The purpose is to give the caller a temporary area to work in. 
	 * To avoid namespace collision among different parts of the application and to 
	 * keep them from stepping over each other, the caller must supply the name of the 
	 * sub-directory and be fully responsible for managing the resources within it. 
	 * As such, the sub-directory name must be unique within the application.
	 *  
	 * @param caller
	 * @return
	 */
	public static File getTempFileDir(String subDirName) {
		return new File(getTempFileDir(), subDirName);
	}
	
	public static void main(String[] args) {
		System.out.println(new File("").getAbsolutePath());
	}
	
	private static File getTempFileDir() {
		return new File(getTempFileDirPath());
	}
	
	private static String getTempFileDirPath() {
		return getTempFileDirPathPerUser(RequestContextHolder.getRequestContext().getUserId());
	}
	
	private static String getTempFileDirRootPath() {
		String filePath = SPropsUtil.getString("temp.dir", "");
		if(filePath.equals(""))
			filePath = System.getProperty("java.io.tmpdir");
		return filePath;
	}
	
	private static String getTempFileDirPathPerUser(Long userId) {
		return getTempFileDirRootPath() + File.separator + userId.toString();
	}
	
	private static File doCreateTempFile(String prefix, String suffix, File fileDir) throws IOException {
		try {
			return File.createTempFile(prefix, suffix, fileDir);
		}
		catch(IOException e) {
			// Give it one more try after brief pause.
			try {
				Thread.sleep(1);
			} 
			catch (InterruptedException ignore) {}
			return File.createTempFile(prefix, suffix, fileDir);
		}
	}
	
}
