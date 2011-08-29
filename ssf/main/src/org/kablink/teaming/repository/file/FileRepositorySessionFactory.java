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
package org.kablink.teaming.repository.file;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.repository.RepositoryServiceException;
import org.kablink.teaming.repository.RepositorySession;
import org.kablink.teaming.repository.impl.AbstractExclusiveRepositorySessionFactory;
import org.kablink.teaming.util.Constants;


public class FileRepositorySessionFactory extends AbstractExclusiveRepositorySessionFactory 
implements FileRepositorySessionFactoryMBean {

	private String repositoryRootDir;

	public String getRepositoryRootDir() {
		return repositoryRootDir;
	}

	public void setRepositoryRootDir(String repositoryRootDir) {
		if(repositoryRootDir.endsWith(Constants.SLASH))
			this.repositoryRootDir = repositoryRootDir;
		else
			this.repositoryRootDir = repositoryRootDir + Constants.SLASH;
	}

	public void shutdown() {
	}

	public RepositorySession openSession() throws RepositoryServiceException, UncheckedIOException {
		return new FileRepositorySession(this, repositoryRootDir);
	}

	public boolean supportVersioning() {
		return true;
	}

	public boolean isVersionDeletionAllowed() {
		return true;
	}

	public boolean supportSmartCheckin() {
		return true;
	}

	public DataSource getDataSourceVersioned(Binder binder,
			DefinableEntity entity, String relativeFilePath,
			String versionName, Boolean isEncrypted, byte[] encryptionKey, FileTypeMap fileTypeMap)
			throws RepositoryServiceException, UncheckedIOException {
		return new FileRepositoryDataSource(binder, entity, relativeFilePath, 
				versionName, isEncrypted, encryptionKey, fileTypeMap);
	}
	
	public class FileRepositoryDataSource extends AbstractExclusiveRepositoryDataSource {
		public FileRepositoryDataSource(Binder binder, DefinableEntity entity, 
				String relativeFilePath, String versionName, 
				Boolean isEncrypted, byte[] encryptionKey, FileTypeMap fileMap) {
			super(binder, entity, relativeFilePath, versionName, isEncrypted, encryptionKey, fileMap);
		}
	}
}
