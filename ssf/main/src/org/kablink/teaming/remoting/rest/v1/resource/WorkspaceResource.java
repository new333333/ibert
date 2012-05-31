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
package org.kablink.teaming.remoting.rest.v1.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.spi.resource.Singleton;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.Folder;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.Workspace;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.util.Constants;
import org.kablink.util.api.ApiErrorCode;

@Path("/v1/workspace/{id}")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class WorkspaceResource extends AbstractBinderResource {
	// TODO $$$ Get top workspace ID
	//public long binder_getTopWorkspaceId(String accessToken);
	
	// Read subworkspaces
	@GET
	@Path("workspaces")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<BinderBrief> getSubWorkspaces(@PathParam("id") long id) {
        SearchFilter filter = new SearchFilter();
        filter.addWorkspaceFilter("");
        return getSubBinders(id, filter);
	}

    @POST
   	@Path("workspaces")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public org.kablink.teaming.rest.v1.model.Workspace createSubWorkspace(@PathParam("id") long id, org.kablink.teaming.rest.v1.model.Binder binder, @QueryParam("template") Long templateId)
               throws WriteFilesException, WriteEntryDataException {
        if (templateId!=null) {
            TemplateBinder template = getTemplateModule().getTemplate(templateId);
            if (EntityIdentifier.EntityType.workspace != template.getEntityType()) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The specified 'template' parameter must be a workspace template.");
            }
        }
        return (Workspace) createBinder(id, binder, templateId);
    }

    // Read subfolders
	@GET
	@Path("folders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<BinderBrief> getSubFolders(@PathParam("id") long id) {
        SearchFilter filter = new SearchFilter();
        filter.addFolderFilter("");
        return getSubBinders(id, filter);
	}

    @POST
   	@Path("folders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public org.kablink.teaming.rest.v1.model.Folder createSubFolder(@PathParam("id") long id, org.kablink.teaming.rest.v1.model.Binder binder, @QueryParam("template") Long templateId)
               throws WriteFilesException, WriteEntryDataException {
        if (templateId==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing required 'template' query string parameter.");
        }
        TemplateBinder template = getTemplateModule().getTemplate(templateId);
        if (EntityIdentifier.EntityType.folder != template.getEntityType()) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The specified 'template' parameter must be a folder template.");
        }
        return (Folder) createBinder(id, binder, templateId);
    }

    @Override
    protected Binder _getBinder(long id) {
        return _getWorkspace(id);
    }

    private org.kablink.teaming.domain.Workspace _getWorkspace(long id) {
        try{
            org.kablink.teaming.domain.Binder binder = getBinderModule().getBinder(id);
            if (binder instanceof org.kablink.teaming.domain.Workspace) {
                org.kablink.teaming.domain.Workspace workspace = (org.kablink.teaming.domain.Workspace) binder;
                if (!workspace.isPreDeleted()) {
                    return workspace;
                }
            }
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.WORKSPACE_NOT_FOUND, "NOT FOUND");
    }

    @Override
    EntityIdentifier.EntityType _getEntityType() {
        return EntityIdentifier.EntityType.workspace;
    }
}
