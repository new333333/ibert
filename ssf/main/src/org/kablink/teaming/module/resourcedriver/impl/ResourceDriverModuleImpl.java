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
package org.kablink.teaming.module.resourcedriver.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.teaming.module.authentication.AuthenticationModule;
import org.kablink.teaming.module.definition.DefinitionModule.DefinitionOperation;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.resourcedriver.RDException;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.repository.RepositoryServiceException;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.FunctionManager;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaFunctionMembershipManager;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


public class ResourceDriverModuleImpl extends CommonDependencyInjection implements ResourceDriverModule {
	
    private FunctionManager functionManager;
    private WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager;
    private ResourceDriverManager resourceDriverManager;

    public FunctionManager getFunctionManager() {
    	if (functionManager == null) {
    		functionManager = (FunctionManager) SpringContextUtil.getBean("functionManager");
    	}
		return functionManager;
    }
    public void setFunctionManager(FunctionManager functionManager) {
        this.functionManager = functionManager;
    }
    
    public WorkAreaFunctionMembershipManager getWorkAreaFunctionMembershipManager() {
    	if (workAreaFunctionMembershipManager == null) {
    		workAreaFunctionMembershipManager = (WorkAreaFunctionMembershipManager) SpringContextUtil.getBean("workAreaFunctionMembershipManager");
    	}
		return workAreaFunctionMembershipManager;
    }
    public void setWorkAreaFunctionMembershipManager(WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager) {
        this.workAreaFunctionMembershipManager = workAreaFunctionMembershipManager;
    }
    public ResourceDriverManager getResourceDriverManager() {
    	if (resourceDriverManager == null) {
    		resourceDriverManager = (ResourceDriverManager) SpringContextUtil.getBean("resourceDriverManager");
    	}
		return resourceDriverManager;
    }

	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
    	if (transactionTemplate == null) {
    		transactionTemplate = (TransactionTemplate) SpringContextUtil.getBean("transactionTemplate");
    	}
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	
	@Override
	public boolean testAccess(ResourceDriverOperation operation) {
		try {
			checkAccess(operation);
			return true;
		} catch(AccessControlException ace) {}
		return false;
	}

	@Override
	public void checkAccess(ResourceDriverOperation operation)
			throws AccessControlException {
		switch (operation) {
		case manageResourceDrivers:
			ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
			if (getAccessControlManager().testOperation(zoneConfig, WorkAreaOperation.MANAGE_RESOURCE_DRIVERS)) {
				return;
			}
			getAccessControlManager().checkOperation(zoneConfig, WorkAreaOperation.ZONE_ADMINISTRATION);
			break;
		default:
			throw new NotSupportedException(operation.toString(),
					"checkAccess");
		}
	}
	
	@Override
	public boolean testAccess(ResourceDriverConfig resourceDriver, ResourceDriverOperation operation) {
		try {
			checkAccess(resourceDriver, operation);
			return true;
		} catch(AccessControlException ace) {}
		return false;
	}

	@Override
	public void checkAccess(ResourceDriverConfig resourceDriver, ResourceDriverOperation operation)
			throws AccessControlException {
		switch (operation) {
		case createFilespace:
			if (getAccessControlManager().testOperation(resourceDriver, WorkAreaOperation.CREATE_FILESPACE)) {
				return;
			}
			ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
			getAccessControlManager().checkOperation(zoneConfig, WorkAreaOperation.ZONE_ADMINISTRATION);
			break;
		default:
			throw new NotSupportedException(operation.toString(),
					"checkAccess");
		}
	}
	
	public List<ResourceDriverConfig> getAllResourceDriverConfigs() {
		return getResourceDriverManager().getAllResourceDriverConfigs();
	}
	
	public ResourceDriverConfig addResourceDriver(final String name, final DriverType type, final String rootPath,
			final Set<Long> memberIds, final Map options) 
 			throws AccessControlException, RDException {
		//Check that the user has the right to do this operation
		checkAccess(ResourceDriverOperation.manageResourceDrivers);
		
		//Look to see if there is a driver by this name already
		ResourceDriver d = null;
		try {
			d = getResourceDriverManager().getDriver(name);
			throw new RDException(NLT.get(RDException.DUPLICATE_RESOURCE_DRIVER_NAME, new String[] {name}), name);
		} catch(FIException fie) {}
		
	   	//Create the new resource driver
    	SimpleProfiler.start("addResourceDriverConfig");
    	// 	The following part requires update database transaction.
    	getTransactionTemplate().execute(new TransactionCallback() {
    		@Override
			public Object doInTransaction(TransactionStatus status) {
				Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
				ResourceDriverConfig newResourceDriver = new ResourceDriverConfig();
			   	newResourceDriver.setName(name);
			   	newResourceDriver.setDriverType(type);
			   	newResourceDriver.setZoneId(zoneId);
			   	newResourceDriver.setRootPath(rootPath);
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_READ_ONLY)) {
			   		newResourceDriver.setReadOnly((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_READ_ONLY));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_HOST_URL)) {
			   		newResourceDriver.setHostUrl((String)options.get(ObjectKeys.RESOURCE_DRIVER_HOST_URL));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE)) {
			   		newResourceDriver.setAllowSelfSignedCertificate((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE)) {
			   		newResourceDriver.setSynchTopDelete((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH)) {
			   		newResourceDriver.setPutRequiresContentLength((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH));
			   	}
				getCoreDao().save(newResourceDriver);
				
				//Set up the access controls for this new resource driver
				List functions = getFunctionManager().findFunctions(zoneId);
				Function manageResourceDriversFunction = null;
				for (int i = 0; i < functions.size(); i++) {
					Function function = (Function)functions.get(i);
					if (function.getInternalId() != null && 
							ObjectKeys.FUNCTION_MANAGE_RESOURCE_DRIVERS_INTERNALID.equals(function.getInternalId())) {
						//We have found the pseudo role
						manageResourceDriversFunction = function;
						break;
					}
				}
				
				if (manageResourceDriversFunction != null) {
					WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
					membership.setZoneId(zoneId);
					membership.setWorkAreaId(newResourceDriver.getWorkAreaId());
					membership.setWorkAreaType(newResourceDriver.getWorkAreaType());
					membership.setFunctionId(manageResourceDriversFunction.getId());
					membership.setMemberIds(memberIds);
					getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
				}
				return null;
    		}
    	});
    	SimpleProfiler.stop("addResourceDriverConfig");

		//Add this new resource driver to the list of drivers
		getResourceDriverManager().resetResourceDriverList();
		
		ResourceDriverConfig rdc = getResourceDriverManager().getDriverConfig(name);
		return rdc;
	}
	
	public ResourceDriverConfig modifyResourceDriver(final String name, final DriverType type, final String rootPath,
			final Set<Long> memberIds, final Map options) 
 			throws AccessControlException, RDException {
		//Check that the user has the right to do this operation
		checkAccess(ResourceDriverOperation.manageResourceDrivers);
		
		//Find the ResourceDriver
		final ResourceDriverConfig rdc = getResourceDriverManager().getDriverConfig(name);
		if (rdc == null) {
			throw new RDException(NLT.get(RDException.NO_SUCH_RESOURCE_DRIVER_NAME, new String[] {name}), name);
		}
		
	   	//Modify this resource driver config
    	SimpleProfiler.start("modifyResourceDriverConfig");
    	// 	The following part requires update database transaction.
    	getTransactionTemplate().execute(new TransactionCallback() {
    		@Override
			public Object doInTransaction(TransactionStatus status) {
				rdc.setDriverType(type);
				rdc.setRootPath(rootPath);
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_READ_ONLY)) {
			   		rdc.setReadOnly((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_READ_ONLY));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_HOST_URL)) {
			   		rdc.setHostUrl((String)options.get(ObjectKeys.RESOURCE_DRIVER_HOST_URL));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE)) {
			   		rdc.setAllowSelfSignedCertificate((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE)) {
			   		rdc.setSynchTopDelete((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH)) {
			   		rdc.setPutRequiresContentLength((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH));
			   	}
				getCoreDao().save(rdc);
    			return null;
    		}
    	});
    	SimpleProfiler.stop("modifyResourceDriverConfig");
				
    	SimpleProfiler.start("deleteResourceDriverConfigAcl");
    	// 	The following part requires update database transaction.
    	getTransactionTemplate().execute(new TransactionCallback() {
    		@Override
			public Object doInTransaction(TransactionStatus status) {
    	     	//Delete the membership ACL to this driver config object
    	    	getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMemberships(
    	    			RequestContextHolder.getRequestContext().getZoneId(), rdc);

    			return null;
    		}
    	});
    	SimpleProfiler.stop("deleteResourceDriverConfigAcl");

    	SimpleProfiler.start("addResourceDriverConfigAcl");
    	// 	The following part requires update database transaction.
    	getTransactionTemplate().execute(new TransactionCallback() {
    		@Override
			public Object doInTransaction(TransactionStatus status) {
				//Then add in the new ACL
				Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
				List functions = getFunctionManager().findFunctions(zoneId);
				Function manageResourceDriversFunction = null;
				for (int i = 0; i < functions.size(); i++) {
					Function function = (Function)functions.get(i);
					if (function.getInternalId() != null && 
							ObjectKeys.FUNCTION_MANAGE_RESOURCE_DRIVERS_INTERNALID.equals(function.getInternalId())) {
						//We have found the pseudo role
						manageResourceDriversFunction = function;
						break;
					}
				}
    	    	if (manageResourceDriversFunction != null) {
					WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
					membership.setZoneId(zoneId);
					membership.setWorkAreaId(rdc.getWorkAreaId());
					membership.setWorkAreaType(rdc.getWorkAreaType());
					membership.setFunctionId(manageResourceDriversFunction.getId());
					membership.setMemberIds(memberIds);
					getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
				}
    			return null;
    		}
    	});
    	SimpleProfiler.stop("addResourceDriverConfigAcl");
		

		//Add this new resource driver to the list of drivers
		getResourceDriverManager().resetResourceDriverList();
		
		return rdc;
	}
	
	public void deleteResourceDriver(final String name) 
			throws AccessControlException, RDException {
		//Check that the user has the right to do this operation
		checkAccess(ResourceDriverOperation.manageResourceDrivers);
		
		//Find the ResourceDriver
		final ResourceDriverConfig rdc = getResourceDriverManager().getDriverConfig(name);
		if (rdc == null) {
			throw new RDException(NLT.get(RDException.NO_SUCH_RESOURCE_DRIVER_NAME, new String[] {name}), name);
		}
		
	   	//Modify this resource driver config
    	SimpleProfiler.start("deleteResourceDriverConfig");
    	// 	The following part requires update database transaction.
    	getTransactionTemplate().execute(new TransactionCallback() {
    		@Override
			public Object doInTransaction(TransactionStatus status) {
    	     	//remove ACLs to this driver config object
    	    	getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMemberships(
    	    			RequestContextHolder.getRequestContext().getZoneId(), rdc);

				//Now delete the actual config object
    	    	getCoreDao().delete(rdc);
    			return null;
    		}
    	});
    	SimpleProfiler.stop("deleteResourceDriverConfig");
		
		//Remove this resource driver from the list of drivers
		getResourceDriverManager().resetResourceDriverList();
	}
}
