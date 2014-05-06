/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManagerUtil;
import org.kablink.teaming.jobs.MirroredFolderSynchronization;
import org.kablink.teaming.jobs.NetFolderServerSynchronization;
import org.kablink.teaming.jobs.Schedule;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ldap.impl.LdapModuleImpl.HomeDirInfo;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.resourcedriver.RDException;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.runasync.RunAsyncCallback;
import org.kablink.teaming.runasync.RunAsyncManager;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;

/**
 * Helper class dealing with net folders and net folder roots
 * @author jwootton
 */
public class NetFolderHelper
{
	protected static Log m_logger = LogFactory.getLog( NetFolderHelper.class );

	/**
	 * Create a net folder server for the given home dir info
	 */
	public static ResourceDriverConfig createHomeDirNetFolderServer(
		ProfileModule profileModule,
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		HomeDirInfo homeDirInfo )
	{
		String serverAddr = null;
		String volume = null;
		String serverUNC;
		ResourceDriverConfig rdConfig;
		String rootName;
		ScheduleInfo scheduleInfo;
		Schedule schedule;
		Long zoneId;
		
		// Are we running Filr?
		if ( Utils.checkIfFilr() == false )
		{
			// No
			return null;
		}

		if ( homeDirInfo != null )
		{
			serverAddr = homeDirInfo.getServerAddr();
			volume = homeDirInfo.getVolume();
		}
		
		// Do we have all the information we need?
		if ( serverAddr == null || serverAddr.length() == 0 ||
			 volume == null || volume.length() == 0 )
		{
			// No
			m_logger.error( "In NetFolderHelper.createHomeDirNetFolderServer(), invalid server information" );
			return null;
		}

		// Does a net folder server already exist with this unc?
		serverUNC = "\\\\" + serverAddr + "\\" + volume;
		rdConfig = findNetFolderRootByUNC( adminModule, resourceDriverModule, serverUNC );
		if ( rdConfig != null )
		{
			// Yes
			m_logger.info( "In NetFolderHelper.createHomeDirNetFolderServer(), net folder server already exists" );
			return rdConfig;
		}

		// Create a net folder root.  The administrator will need to fill in credentials.
		rootName = serverAddr + "-" + volume;
		m_logger.info( "About to create a net folder server called: " + rootName  );
		
		// Create a default schedule for syncing the net folders associated with this net folder server
		// The schedule is configured to run every day at 11:00pm
		zoneId = RequestContextHolder.getRequestContext().getZoneId();
		scheduleInfo = new ScheduleInfo( zoneId );
		scheduleInfo.setEnabled( true );
		schedule = new Schedule( "" );
		schedule.setDaily( true );
		schedule.setHours( "23" );
		schedule.setMinutes( "0" );
		scheduleInfo.setSchedule( schedule );
		
		rdConfig = NetFolderHelper.createNetFolderRoot(
													adminModule,
													resourceDriverModule,
													rootName,
													serverUNC,
													DriverType.famt,
													null,
													null,
													null,
													null,
													false,
													false,
													scheduleInfo );
		
		// Add a task for the administrator to enter the proxy credentials for this server.
		{
			UserProperties userProperties;
			FilrAdminTasks filrAdminTasks;
			String xmlStr;
			User adminUser;
			String adminUserName;
			String zoneName;

			// Get the admin user so we can add an administrative task to his user properties.
			zoneName = RequestContextHolder.getRequestContext().getZoneName();
			adminUserName = SZoneConfig.getAdminUserName( zoneName );
			adminUser = profileModule.getUser( adminUserName );
			
			// Get the FilrAdminTasks from the administrator's user properties
			userProperties = profileModule.getUserProperties( adminUser.getId() );
			xmlStr = (String)userProperties.getProperty( ObjectKeys.USER_PROPERTY_FILR_ADMIN_TASKS );
			filrAdminTasks = new FilrAdminTasks( xmlStr );
			
			// Add a task for the administrator to enter the proxy credentials for this net folder server.
			filrAdminTasks.addEnterNetFolderServerProxyCredentialsTask( rdConfig.getId() );
			
			// Save the FilrAdminTasks to the administrator's user properties
			profileModule.setUserProperty(
										adminUser.getId(),
										ObjectKeys.USER_PROPERTY_FILR_ADMIN_TASKS,
										filrAdminTasks.toString() );
		}

		return rdConfig;
	}
	
	/**
	 * Create a net folder and if needed a net folder root for the given home directory information
	 */
	@SuppressWarnings("unchecked")
	public static void createHomeDirNetFolder(
		ProfileModule profileModule,
		TemplateModule templateModule,
		BinderModule binderModule,
		final FolderModule folderModule,
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		RunAsyncManager asyncManager,
		HomeDirInfo homeDirInfo,
		User user ) throws WriteFilesException, WriteEntryDataException
	{
		Long workspaceId;
		String serverAddr = null;
		String volume = null;
		String path = null;
		String serverUNC;
		ResourceDriverConfig rdConfig;
		boolean canSyncNetFolder = false;

		// Are we running Filr?
		if ( Utils.checkIfFilr() == false )
		{
			// No
			return;
		}
		
		if ( homeDirInfo != null )
		{
			serverAddr = homeDirInfo.getServerAddr();
			volume = homeDirInfo.getVolume();
			path = homeDirInfo.getPath();
		}
		
		// Do we have all the information we need?
		if ( serverAddr == null || serverAddr.length() == 0 ||
			 volume == null || volume.length() == 0 ||
			 path == null || path.length() == 0 )
		{
			// No
			m_logger.error( "In NetFolderHelper.createHomeDirNetFolder(), invalid server information" );
			return;
		}
		
		// Does the user have a workspace?
		workspaceId = user.getWorkspaceId();
		if ( workspaceId == null )
		{
			Workspace workspace;

			// No, create one
			workspace = profileModule.addUserWorkspace( user, null );
			workspaceId = workspace.getId();
		}
		
		// Does a net folder root exists with a unc path to the given server address / volume?
		serverUNC = "\\\\" + serverAddr + "\\" + volume;
		rdConfig = findNetFolderRootByUNC( adminModule, resourceDriverModule, serverUNC );
		if ( rdConfig == null )
		{
			// No, create one
			rdConfig = NetFolderHelper.createHomeDirNetFolderServer(
																profileModule,
																adminModule,
																resourceDriverModule,
																homeDirInfo );
		}
		else
		{
			// A net folder server already exists.  Is it fully configured?
			if ( isNetFolderServerConfigured( rdConfig ) )
			{
				// Yes, this means we can sync the home directory net folder if we need to.
				canSyncNetFolder = true;
			}
		}
		
		if ( rdConfig != null )
		{
			Binder netFolderBinder;
			boolean syncNeeded = false;
			
			// Does a net folder already exist for this user's home directory
			netFolderBinder = NetFolderHelper.findHomeDirNetFolder(
																binderModule,
																user.getWorkspaceId() );
			if ( netFolderBinder == null )
			{
				String folderName;
	
				// No, create one.
				folderName = NLT.get( "netfolder.default.homedir.name" );
				m_logger.info( "About to create a net folder called: " + folderName + ", for the users home directory for user: " + user.getName() );
				
				// Create a net folder in the user's workspace
				netFolderBinder = NetFolderHelper.createNetFolder(
															templateModule,
															binderModule,
															folderModule,
															adminModule,
															user,
															folderName,
															rdConfig.getName(),
															path,
															null,
															workspaceId,
															true,
															false );

				// Save the jits settings
				{
					NetFolderHelper.saveJitsSettings(
												binderModule,
												netFolderBinder.getId(),
												true,
												getDefaultJitsAclMaxAge(),
												getDefaultJitsResultsMaxAge() );
				}
				
				syncNeeded = false;
			}
			else
			{
				String currentServerUNC = null;
				
				// A home dir net folder already exists for this user.
				
				// Get the server unc path that is currently being used by the user's home dir net folder.
				{
					ResourceDriver driver;
					
					driver = netFolderBinder.getResourceDriver();
					if ( driver != null )
					{
						ResourceDriverConfig currentRdConfig;
						
						currentRdConfig = driver.getConfig();
						if ( currentRdConfig != null )
							currentServerUNC = currentRdConfig.getRootPath();
					}
				}

				// Did any information about the home directory change?
				if ( serverUNC.equalsIgnoreCase( currentServerUNC ) == false ||
					 homeDirInfo.getPath().equalsIgnoreCase( netFolderBinder.getResourcePath() ) == false )
				{
					// Update the folder's resource driver name and relative path.
					folderModule.modifyNetFolder(
											netFolderBinder.getId(),
											rdConfig.getName(),
											path );
		   			
		   			syncNeeded = false;
				}
			}
			
			// Do we need to sync the home directory net folder?
			if ( syncNeeded )
			{
				// Yes
				// Can we sync the home directory net folder?
				if ( canSyncNetFolder )
				{
					// Yes, sync it.
					try
					{
						final Long binderId;
						
						binderId = netFolderBinder.getId();
						
						asyncManager.execute( new RunAsyncCallback()
						{
							@Override
							public Object doAsynchronously() throws Exception 
							{
								m_logger.info( "About to sync home directory net folder: " + binderId );
								folderModule.fullSynchronize( binderId, null );
						    	return null;
							}

							@Override
							public String toString()
							{
								return "folderModule.synchronize()";
							}
						}, RunAsyncManager.TaskType.FULL_SYNC);
					}
					catch ( Exception e )
					{
						m_logger.error( "Error syncing next net folder: " + netFolderBinder.getName() + ", " + e.toString() );
					}
				}
			}
		}
	}
	
	/**
	 * Create a net folder from the given data
	 */
	public static Binder createNetFolder(
		TemplateModule templateModule,
		BinderModule binderModule,
		FolderModule folderModule,
		AdminModule adminModule,
		User owner,
		String name,
		String rootName,
		String path,
		ScheduleInfo scheduleInfo,
		Long parentBinderId,
		boolean isHomeDir,
		boolean indexContent ) throws WriteFilesException, WriteEntryDataException
	{
		Binder binder = null;
		Long templateId = null;
		List<TemplateBinder> listOfTemplateBinders;
		String templateInternalId;
		
		// Are we going to create a "home directory" net folder?
		if ( isHomeDir )
		{
			// Yes
			templateInternalId = ObjectKeys.DEFAULT_FOLDER_FILR_HOME_FOLDER_CONFIG;
		}
		else
		{
			// No
			templateInternalId = ObjectKeys.DEFAULT_FOLDER_FILR_ROOT_CONFIG ;
		}

		// Find the template binder for mirrored folders.
		listOfTemplateBinders = templateModule.getTemplates( Boolean.TRUE );
		if ( listOfTemplateBinders != null )
		{
			for ( TemplateBinder nextTemplateBinder : listOfTemplateBinders )
			{
				String internalId;
				
				internalId = nextTemplateBinder.getInternalId();
				if ( internalId != null && internalId.equalsIgnoreCase( templateInternalId) )
				{
					templateId = nextTemplateBinder.getId();
					break;
				}
			}
		}

		if ( templateId != null )
		{			
			binder = folderModule.createNetFolder(templateId, parentBinderId, name, owner, rootName, path, isHomeDir, indexContent);
			
			// Set the net folder's sync schedule
			if ( scheduleInfo != null )
			{
				scheduleInfo.setFolderId( binder.getId() );
				folderModule.setSynchronizationSchedule( scheduleInfo, binder.getId() );
			}
		}
		else
			m_logger.error( "Could not find the template binder for a mirrored folder" );
		
		return binder;
	}
	
	/**
	 * Create a net folder root from the given data
	 */
	@SuppressWarnings({ "unchecked" })
	public static ResourceDriverConfig createNetFolderRoot(
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		String name,
		String path,
		DriverType driverType,
		String proxyName,
		String proxyPwd,
		Set<Long> memberIds,
		String hostUrl,
		boolean allowSelfSignedCerts,
		boolean isSharePointServer,
		ScheduleInfo scheduleInfo ) throws RDException
	{
		Map options;
		ResourceDriverConfig rdConfig = null;
		
		adminModule.checkAccess( AdminOperation.manageResourceDrivers );
		
		// Does a net folder root already exist with the give name?
		if ( findNetFolderRootByName( adminModule, resourceDriverModule, name ) != null )
		{
			// Yes, do not allow this.
			throw new RDException(
								NLT.get( RDException.DUPLICATE_RESOURCE_DRIVER_NAME, new String[] {name}),
								name );
		}

		options = new HashMap();
		options.put( ObjectKeys.RESOURCE_DRIVER_READ_ONLY, Boolean.FALSE );
		options.put( ObjectKeys.RESOURCE_DRIVER_ACCOUNT_NAME, proxyName ); 
		options.put( ObjectKeys.RESOURCE_DRIVER_PASSWORD, proxyPwd );
		
		// Is the root type WebDAV?
		if ( driverType == DriverType.webdav )
		{
			// Yes, get the WebDAV specific values
			options.put(
					ObjectKeys.RESOURCE_DRIVER_HOST_URL,
					hostUrl );
			options.put(
					ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE,
					allowSelfSignedCerts );
			options.put(
					ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH,
					isSharePointServer );
		}

		// Always prevent the top level folder from being deleted
		// This is forced so that the folder could not accidentally be deleted if the 
		// external disk was offline
		options.put( ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE, Boolean.FALSE );

		//Add this resource driver
		rdConfig = resourceDriverModule.addResourceDriver(
														name,
														driverType, 
														path,
														memberIds,
														options );

		// Set the net folder server's sync schedule
		if ( scheduleInfo != null )
		{
			scheduleInfo.setFolderId( rdConfig.getId() );
			resourceDriverModule.setSynchronizationSchedule( scheduleInfo, rdConfig.getId() );
		}

		return rdConfig;
	}

	/**
	 * Delete the given net folder
	 */
	public static void deleteNetFolder(
		FolderModule folderModule,
		Long id,
		boolean deleteSource )
	{
		folderModule.deleteNetFolder( id, deleteSource );
	}
	
	/**
	 * See if a "home directory" net folder exists with the given rootName and path for the given user.
	 */
	@SuppressWarnings("unchecked")
	public static Binder findHomeDirNetFolder(
		BinderModule binderModule,
		Long workspaceId )
	{
		Binder binder;
		List<Binder> childBinders;
		
		if ( workspaceId == null )
			return null;
		
		//!!! Ask Dennis how to do a search so I don't have to read the list of binders.
		//!!! Maybe it is ok to enumerate through the list of binders.
		
		binder = binderModule.getBinder( workspaceId );
		childBinders = binder.getBinders();
		if ( childBinders != null )
		{
			for ( Binder nextBinder : childBinders )
			{
				if ( nextBinder.isMirrored() && nextBinder.isHomeDir() )
					return nextBinder;
			}
		}
		
		// If we get here we did not find a net folder.
		return null;
	}

	/**
	 * 
	 */
	public static ResourceDriverConfig findNetFolderRootById(
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		String id )
	{
		List<ResourceDriverConfig> drivers;

		if ( id == null )
			return null;
		
		// Get a list of the currently defined Net Folder Roots
		drivers = resourceDriverModule.getAllResourceDriverConfigs();
		for ( ResourceDriverConfig driver : drivers )
		{
			String driverId;
			
			driverId = String.valueOf( driver.getId() );
			if ( id.equalsIgnoreCase( driverId ) )
				return driver;
		}
		
		// If we get here we did not find a net folder root with the given id.
		return null;
	}

	/**
	 * 
	 */
	public static ResourceDriverConfig findNetFolderRootByUNC(
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		String serverUNC )
	{
		List<ResourceDriverConfig> drivers;

		if ( serverUNC == null )
			return null;
		
		// Get a list of the currently defined Net Folder Roots
		drivers = resourceDriverModule.getAllResourceDriverConfigs();
		for ( ResourceDriverConfig driver : drivers )
		{
			if ( serverUNC.equalsIgnoreCase( driver.getRootPath() ) )
				return driver;
		}
		
		// If we get here we did not find a net folder root with the given unc.
		return null;
	}
	
	/**
	 * 
	 */
	public static ResourceDriverConfig findNetFolderRootByName(
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		String name )
	{
		List<ResourceDriverConfig> drivers;

		if ( name == null )
			return null;
		
		// Get a list of the currently defined Net Folder Roots
		drivers = resourceDriverModule.getAllResourceDriverConfigs();
		for ( ResourceDriverConfig driver : drivers )
		{
			if ( name.equalsIgnoreCase( driver.getName() ) )
				return driver;
		}
		
		// If we get here we did not find a net folder root with the given name.
		return null;
	}
	
	/**
	 * 
	 */
	private static CoreDao getCoreDao()
	{
		return (CoreDao) SpringContextUtil.getBean( "coreDao" );
	}
	
	/**
	 * 
	 */
	public static long getDefaultJitsAclMaxAge()
	{
		return SPropsUtil.getLong( "nf.jits.acl.max.age", 60000L );
	}
	
	/**
	 * 
	 */
	public static long getDefaultJitsResultsMaxAge()
	{
		return SPropsUtil.getLong( "nf.jits.max.age", 30000L );
	}
	
	/**
	 * Return all the net folders that are associated with the given net folder server
	 */
	@SuppressWarnings({ "unchecked" })
	public static List<Long> getAllNetFolders(
		BinderModule binderModule,
		WorkspaceModule workspaceModule,
		String rootName,
		boolean includeHomeDirNetFolders )
	{
		List<Folder> results;
		List<Long> listOfNetFolderIds;
		Workspace zone;
		Long zoneId;
		FilterControls filterCtrls;
		
		zone = RequestContextHolder.getRequestContext().getZone();
		zoneId = zone.getId();
		
		// Add the criteria for finding all top-level net folders.
		{
			filterCtrls = new FilterControls();

			filterCtrls.add( ObjectKeys.FIELD_BINDER_MIRRORED, Boolean.TRUE );
			
			filterCtrls.addIsNull( "topFolder" );
			
			// Are we looking for a net folder that is associated with a specific net folder root?
			if ( rootName != null && rootName.length() > 0 )
			{
				// Yes
				filterCtrls.add( ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME, rootName );
			}
	
			// Are we including "home directory" net folders?
			if ( includeHomeDirNetFolders == false )
			{
				Binder parentBinder;
				
				// No
				filterCtrls.add( ObjectKeys.FIELD_BINDER_IS_HOME_DIR, false );
				
				// Get the binder where all non home dir net folders live.
				parentBinder = getCoreDao().loadReservedBinder(
														ObjectKeys.NET_FOLDERS_ROOT_INTERNALID, 
														zoneId );
				if ( parentBinder != null )
				{
					filterCtrls.add(
								ObjectKeys.FIELD_ENTITY_PARENTBINDER,
								parentBinder );
				}
			}
		}

		listOfNetFolderIds = new ArrayList<Long>();
		
		// Get the list of net folders for the given criteria.
		results = getCoreDao().loadObjects( Folder.class, filterCtrls, zoneId );
		if ( results != null )
		{
			// We only want to return top-level net folders.
			for ( Folder nextFolder: results )
			{
				if ( nextFolder.isTop() && nextFolder.isDeleted() == false )
					listOfNetFolderIds.add( nextFolder.getId() );
			}
		}

		return listOfNetFolderIds;
	}
	
	/**
	 * Determine if the given net folder server is fully configured
	 */
	private static boolean isNetFolderServerConfigured( ResourceDriverConfig rdConfig )
	{
		String path;
		String name;
		String pwd;
		
		if ( rdConfig == null )
			return false;
		
		// Is the driver configured
		path = rdConfig.getRootPath();
		name = rdConfig.getAccountName();
		pwd = rdConfig.getPassword();
		if ( path != null && path.length() > 0 &&
			 name != null && name.length() > 0 &&
			 pwd != null && pwd.length() > 0 )
		{
			// Yes
			return true;
		}
		
		// If we get here the net folder server is not fully configured
		return false;
	}

	/**
	 * 
	 */
	public static void modifyNetFolder(
		BinderModule binderModule,
		FolderModule folderModule,
		Long id,
		String netFolderName,
		String netFolderRootName,
		String relativePath,
		ScheduleInfo scheduleInfo,
		boolean indexContent ) throws AccessControlException, WriteFilesException, WriteEntryDataException
	{
		// Modify the binder with the net folder information.
		folderModule.modifyNetFolder(id, netFolderName, netFolderRootName, relativePath, null, indexContent);

		// Set the net folder's sync schedule
		if ( scheduleInfo != null )
		{
			scheduleInfo.setFolderId( id );
			folderModule.setSynchronizationSchedule( scheduleInfo, id );
		}
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static ResourceDriverConfig modifyNetFolderRoot(
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		final ProfileModule profileModule,
		BinderModule binderModule,
		WorkspaceModule workspaceModule,
		String rootName,
		String rootPath,
		String proxyName,
		String proxyPwd,
		DriverType driverType,
		String hostUrl,
		boolean allowSelfSignedCerts,
		boolean isSharePointServer,
		Set<Long> listOfPrincipals,
		ScheduleInfo scheduleInfo )
	{
		Map options;
		User adminUser;
		String adminUserName;
		String zoneName;
		String xmlStr;
		UserProperties userProperties;
		FilrAdminTasks filrAdminTasks;
		ResourceDriverConfig rdConfig;
		boolean isConfigured1;
		boolean isConfigured2;
		boolean reIndexNeeded = false;

		adminModule.checkAccess( AdminOperation.manageResourceDrivers );

		// Is the driver configured
		rdConfig = ResourceDriverManagerUtil.getResourceDriverManager().getDriverConfig( rootName );
		isConfigured1 = isNetFolderServerConfigured( rdConfig );
		
		options = new HashMap();
		options.put( ObjectKeys.RESOURCE_DRIVER_READ_ONLY, Boolean.FALSE );
		options.put( ObjectKeys.RESOURCE_DRIVER_ACCOUNT_NAME, proxyName ); 
		options.put( ObjectKeys.RESOURCE_DRIVER_PASSWORD, proxyPwd );

		// Always prevent the top level folder from being deleted
		// This is forced so that the folder could not accidentally be deleted if the 
		// external disk was offline
		options.put( ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE, Boolean.FALSE );

		// Is the root type WebDAV?
		if ( driverType == DriverType.webdav )
		{
			// Yes, get the WebDAV specific values
			options.put(
					ObjectKeys.RESOURCE_DRIVER_HOST_URL,
					hostUrl );
			options.put(
					ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE,
					allowSelfSignedCerts );
			options.put(
					ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH,
					isSharePointServer );
		}

		// Modify the resource driver
		rdConfig = resourceDriverModule.modifyResourceDriver(
															rootName,
															driverType, 
															rootPath,
															listOfPrincipals,
															options );

		// Set the net folder server's sync schedule
		if ( scheduleInfo != null )
		{
			scheduleInfo.setFolderId( rdConfig.getId() );
			resourceDriverModule.setSynchronizationSchedule( scheduleInfo, rdConfig.getId() );
		}

		// Is the configuration complete?
		rdConfig = ResourceDriverManagerUtil.getResourceDriverManager().getDriverConfig( rootName );
		isConfigured2 = isNetFolderServerConfigured( rdConfig );

		// Update the Filr admin tasks
		{
			// Get the admin user so we can remove an administrative task to his user properties.
			zoneName = RequestContextHolder.getRequestContext().getZoneName();
			adminUserName = SZoneConfig.getAdminUserName( zoneName );
			adminUser = profileModule.getUser( adminUserName );
			
			// Get the FilrAdminTasks from the administrator's user properties
			userProperties = profileModule.getUserProperties( adminUser.getId() );
			xmlStr = (String)userProperties.getProperty( ObjectKeys.USER_PROPERTY_FILR_ADMIN_TASKS );
			filrAdminTasks = new FilrAdminTasks( xmlStr );
			
			// Is the net folder server configured?
			if ( isConfigured2 )
			{
				// Yes
				// Remove the task for the administrator to enter the proxy credentials for this net folder server.
				filrAdminTasks.deleteEnterNetFolderServerProxyCredentialsTask( rdConfig.getId() );
			}
			else
			{
				// No
				// Add a task for the administrator to configure the net folder server.
				filrAdminTasks.addEnterNetFolderServerProxyCredentialsTask( rdConfig.getId() );
			}
			
			// Save the FilrAdminTasks to the administrator's user properties
			{
				RunasCallback callback;
				final String tmpXmlStr;
				final Long adminId;
				
				tmpXmlStr = filrAdminTasks.toString();
				adminId = adminUser.getId();
				
				callback = new RunasCallback()
				{
					@Override
					public Object doAs()
					{
						profileModule.setUserProperty(
													adminId,
													ObjectKeys.USER_PROPERTY_FILR_ADMIN_TASKS,
													tmpXmlStr );
						return null;
					}
				};
				RunasTemplate.runasAdmin( callback, RequestContextHolder.getRequestContext().getZoneName() );
			}
		}
		
		if ( isConfigured1 != isConfigured2 )
			reIndexNeeded = true;
		
		// Re-index all of the net folders that reference this net folder server.  We need to do
		// this so "home directory" net folders will show up in "My files" after the net folder server
		// has been configured fully.
		// We only need to re-index if the configuration completeness changed.
		if ( reIndexNeeded )
		{
			List<Long> listOfNetFolderIds;

			// Find all of the net folders that reference this net folder server.
			listOfNetFolderIds = NetFolderHelper.getAllNetFolders(
														binderModule,
														workspaceModule,
														rdConfig.getName(),
														true );

			if ( listOfNetFolderIds != null )
			{
				for ( Long binderId:  listOfNetFolderIds )
				{
					// Index this binder.
					binderModule.indexBinder( binderId, false );
				}
			}
		}
		
		return rdConfig;
	}

	/**
	 * Save the jits settings for the given net folder binder
	 */
	@SuppressWarnings({ "unchecked" })
	public static void saveJitsSettings(
		BinderModule binderModule,
		Long binderId,
		boolean jitsEnabled,
		long aclMaxAge,
		long resultsMaxAge )
	{
		if ( binderId != null )
		{
			Set deleteAtts;
			Map fileMap = null;
			MapInputData mid;
			Map formData = null;
			
			deleteAtts = new HashSet();
			fileMap = new HashMap();
			formData = new HashMap();

			formData.put( ObjectKeys.FIELD_BINDER_JITS_ENABLED, Boolean.toString( jitsEnabled ) );
			
			formData.put( ObjectKeys.FIELD_BINDER_JITS_ACL_MAX_AGE, String.valueOf( aclMaxAge ) );

			formData.put( ObjectKeys.FIELD_BINDER_JITS_RESULTS_MAX_AGE, String.valueOf( resultsMaxAge ) );

			mid = new MapInputData( formData );

			try
			{
				binderModule.modifyBinder( binderId, mid, fileMap, deleteAtts, null );
			}
			catch ( Exception ex )
			{
				m_logger.error( "In saveJitsSettings(), call to modifyBinder() failed. " + ex.toString() );
			}
		}
	}
	

	// This method is moved from FolderModule which is transactional proxy that automatically defines
	// read transaction around this method boundary, which can cause deadlock situation as the internal
	// quartz code attempts to obtain database connection under extremely loaded situation.
	public static ScheduleInfo getMirroredFolderSynchronizationSchedule(Long folderId) {
  		return getMirroredFolderSynchronizationScheduleObject().getScheduleInfo(folderId);
	}
	
	public static MirroredFolderSynchronization getMirroredFolderSynchronizationScheduleObject() {
		String className = SPropsUtil.getString("job.mirrored.folder.synchronization.class", "org.kablink.teaming.jobs.DefaultMirroredFolderSynchronization");
		return (MirroredFolderSynchronization)ReflectHelper.getInstance(className);
    }    

	/**
	 * Get the sync schedule for the given driver.
	 */
	// This method is moved from ResourceDriverModule which is transactional proxy that automatically 
	// defines read transaction around this method boundary, which can cause deadlock situation as the 
	// internal quartz code attempts to obtain database connection under extremely loaded situation.
	public static ScheduleInfo getNetFolderServerSynchronizationSchedule( Long driverId )
	{
  		return getNetFolderServerSynchronizationScheduleObject().getScheduleInfo( driverId );
	}

	public static NetFolderServerSynchronization getNetFolderServerSynchronizationScheduleObject() 
	{
		String className = SPropsUtil.getString("job.net.folder.server.synchronization.class", "org.kablink.teaming.jobs.DefaultNetFolderServerSynchronization");

		return (NetFolderServerSynchronization)ReflectHelper.getInstance(className);
    }    

}
