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
package org.kablink.teaming.module.admin.impl;

import java.util.Date;
import java.util.List;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.IndexNode;
import org.kablink.teaming.domain.MobileAppsConfig;
import org.kablink.teaming.domain.OpenIDConfig;
import org.kablink.teaming.domain.OpenIDProvider;
import org.kablink.teaming.module.admin.ManageIndexException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

public class BaseAdminModule extends AbstractAdminModule {

	public List<IndexNode> retrieveIndexNodesHA() {
		return null; // No support for nodes
	}
	
	public void updateIndexNodeHA(String indexNodeId, String userModeAccess, Boolean enableDeferredUpdateLog, Boolean noDeferredUpdateLogRecords) {
		// Noop
	}
	
	public void applyDeferredUpdateLogRecordsHA(IndexNode indexNode) {
		// Noop
	}

	public void discardDeferredUpdateLogRecordsHA(IndexNode indexNode) {
		// Noop	
	}
	
	public void setFileSynchAppSettings(Boolean enabled, Integer synchInterval, String autoUpdateUrl, Boolean deployEnabled, Boolean allowCachePwd, Integer maxFileSize ) {
		// Noop
	}

	@Override
	public void addOpenIDProvider(OpenIDProvider openIDProvider) {
		// Noop
	}

	@Override
	public void modifyOpenIDProvider(OpenIDProvider openIDProvider) {
		// Noop
	}

	@Override
	public void deleteOpenIDProvider(String openIDProviderId) {
		// Noop
	}

	@Override
	public OpenIDProvider getOpenIDProvider(String openIDProviderId) {
		return null;
	}

	@Override
	public List<OpenIDProvider> getOpenIDProviders() {
		return null;
	}

	@Override
	public boolean isExternalUserEnabled() {
		return false;
	}

	@Override
	public void setExternalUserEnabled(boolean enabled) {
		// Noop
	}

	@Override
	public OpenIDConfig getOpenIDConfig() {
		return null;
	}

	@Override
	public void setOpenIDConfig(OpenIDConfig openIDConfig) {
		// Noop
	}

	@Override
	public MobileAppsConfig getMobileAppsConfig()
	{
		return null;
	}
	
	@Override
	public void setMobileAppsConfig( MobileAppsConfig config )
	{
		// Noop
	}

	@Override
	public void setJitsConfig( boolean enabled, long maxWait )
	{
		// Noop
	}
	
}
