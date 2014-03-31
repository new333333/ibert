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
package org.kablink.teaming.remoting.rest.v1.resource.admin;

import com.sun.jersey.spi.resource.Singleton;
import org.kablink.teaming.dao.util.NetFolderSelectSpec;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.BinderState;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.resource.AbstractResource;
import org.kablink.teaming.remoting.rest.v1.util.AdminResourceUtil;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.admin.NetFolder;
import org.kablink.teaming.rest.v1.model.admin.NetFolderServer;
import org.kablink.teaming.rest.v1.model.admin.NetFolderSyncStatus;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.web.util.NetFolderHelper;
import org.kablink.util.api.ApiErrorCode;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/admin/net_folders")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AdminNetFolderResource extends AbstractAdminResource {

    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   	public SearchResultList<NetFolder> getNetFolders(@QueryParam("include_full_details") @DefaultValue("false") boolean fullDetails,
                                                     @QueryParam("type") String type) {
        NetFolderSelectSpec selectSpec = new NetFolderSelectSpec();
        NetFolder.Type nfType = toEnum(NetFolder.Type.class, "type", type);
        if (nfType==NetFolder.Type.net) {
            selectSpec.setIncludeHomeDirNetFolders(false);
            selectSpec.setIncludeNonHomeDirNetFolders(true);
        } else if (nfType == NetFolder.Type.home) {
            selectSpec.setIncludeHomeDirNetFolders(true);
            selectSpec.setIncludeNonHomeDirNetFolders(false);
        } else {
            selectSpec.setIncludeHomeDirNetFolders(true);
            selectSpec.setIncludeNonHomeDirNetFolders(true);
        }
        List<Folder> folderList = NetFolderHelper.getAllNetFolders2(getBinderModule(), getWorkspaceModule(), selectSpec);

        SearchResultList<NetFolder> results = new SearchResultList<NetFolder>();
        for (Folder folder : folderList) {
            results.append(AdminResourceUtil.buildNetFolder(folder, this, fullDetails));
        }
        return results;
    }

    @POST
   	@Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   	public NetFolder createNetFolder(NetFolder netFolder) throws WriteFilesException, WriteEntryDataException {
        validateMandatoryField(netFolder, "getServer", "getId");
        ResourceDriverConfig driverConfig = getResourceDriverModule().getResourceDriverConfig(netFolder.getServer().getId());
        return _createNetFolder(netFolder, driverConfig);
   	}

    @GET
    @Path("{id}")
    public NetFolder getNetFolder(@PathParam("id") Long id) {
        Folder folder = lookupNetFolder(id);
        return AdminResourceUtil.buildNetFolder(folder, this, true);
    }

    @PUT
    @Path("{id}")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public NetFolder updateNetFolder(@PathParam("id") Long id, NetFolder netFolder) throws WriteFilesException, WriteEntryDataException {
        Folder folder = lookupNetFolder(id);
        netFolder.setId(id);
        NetFolder existing = AdminResourceUtil.buildNetFolder(folder, this, false);
        netFolder.replaceNullValues(existing);
        ResourceDriverConfig driverConfig = getResourceDriverModule().getResourceDriverConfig(netFolder.getServer().getId());
        return _modifyNetFolder(netFolder, driverConfig);
    }

    @DELETE
    @Path("{id}")
    public void deleteNetFolderServer(@PathParam("id") Long id) {
        Folder folder = lookupNetFolder(id);
        NetFolderHelper.deleteNetFolder(getFolderModule(), id, false);
    }


    @GET
    @Path("{id}/sync")
    public NetFolderSyncStatus getSyncStatus(@PathParam("id") Long id) {
        Folder netFolder = lookupNetFolder(id);
        BinderState binderState = getBinderState(id);
        if ( binderState != null ) {
            BinderState.FullSyncStats syncStats;
            syncStats = binderState.getFullSyncStats();
            if ( syncStats != null ) {
                return AdminResourceUtil.buildNetFolderSyncStatus(syncStats);
            }
        }
        return null;
    }

    private BinderState getBinderState(Long id) {
        BinderState state = (BinderState) getCoreDao().load(BinderState.class, id);
        getCoreDao().evict(state);
        return state;
    }

    @POST
    @Path("{id}/sync")
    @Consumes({"*/*"})
    public NetFolderSyncStatus syncNetFolder(@PathParam("id") Long id,
                                             @QueryParam("wait") @DefaultValue("false") boolean waitForCompletion)
            throws InterruptedException {
        Folder netFolder = lookupNetFolder(id);
        getFolderModule().enqueueFullSynchronize(netFolder.getId());
        while (waitForCompletion) {
            BinderState state = getBinderState(id);
            if (state!=null) {
                waitForCompletion = !state.getFullSyncStats().getStatus().isCompleted();
                if (waitForCompletion) {
                    Thread.sleep(2000);
                }
            } else {
                waitForCompletion = false;
            }
        }
        return getSyncStatus(id);
    }

    @DELETE
    @Path("{id}/sync")
    @Consumes({"*/*"})
    public void cancelNetFolderSync(@PathParam("id") Long id) {
        Folder netFolder = lookupNetFolder(id);
        getFolderModule().requestNetFolderFullSyncStop(netFolder.getId());
    }

    private Folder lookupNetFolder(Long id) {
        Folder folder;
        try {
            Binder binder = getBinderModule().getBinder(id);
            if (!(binder instanceof Folder)) {
                throw new NoFolderByTheIdException(id);
            }
            folder = (Folder) binder;
            if (!folder.isMirrored() || !folder.isTop()) {
                throw new NoFolderByTheIdException(id);
            }
        } catch (NoBinderByTheIdException e) {
            throw new NoFolderByTheIdException(id);
        }
        return folder;
    }
}
