package org.kablink.teaming.remoting.rest.v1.resource;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoTagByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.BinderUtils;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderTree;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.PrincipalBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.Tag;
import org.kablink.teaming.rest.v1.model.TeamMember;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Criterion;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Restrictions;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:08 PM
 */
abstract public class AbstractBinderResource extends AbstractDefinableEntityResource {

    /**
     * Returns the binder with the specified ID.
     * @param id    The ID of the binder to return.
     * @param includeAttachments    Configures whether attachments should be included in the returned binder object.
     * @return  Returns a subclass of Binder.
     */
    @GET
    @Path("{id}")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Binder getBinder(@PathParam("id") long id,
                            @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments) {
        return ResourceUtil.buildBinder(_getBinder(id), includeAttachments);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updateBinder(@PathParam("id") long id, Binder binder)
            throws WriteFilesException, WriteEntryDataException {
        _getBinder(id);
        getBinderModule().modifyBinder(id,
                         new RestModelInputData(binder), null, null, null);
    }

    /**
     * Deletes the specifed binder object.  The binder is moved into the Trash, not permanently deleted.
     * @param id    The ID of the binder to delete.
     */
    @DELETE
    @Path("{id}")
    public void deleteBinder(@PathParam("id") long id) {
        _deleteBinder(id);
    }

    /**
     * Gets a list of child binders contained in the specified binder.
     * @param id The id of the parent binder
     * @return Returns a list of BinderBrief objects.
     */
	@GET
	@Path("{id}/binders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<BinderBrief> getSubBinders(@PathParam("id") long id,
			@QueryParam("first") @DefaultValue("0") Integer offset,
			@QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        return getSubBinders(id, null, offset, maxCount, null);
	}

    /**
     * Creates a new binder as a child of the specified binder.
     * @param id    The id of the binder where the new binder will be created.
     * @param binder    An object containing the information about the binder to be created.  Requires the <code>title</code>
     *                  property to be set.  All other binder properties are ignored.
     * @param templateId    Optional template ID defining the type of binder to create.  If no template is specified, the
     *                      new parent binder is used to determine the type of binder to create.
     * @return Returns a Binder object representing the newly created binder.
     */
	@POST
	@Path("{id}/binders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Binder createSubBinder(@PathParam("id") long id, Binder binder, @QueryParam("template") Long templateId)
            throws WriteFilesException, WriteEntryDataException {
        return createBinder(id, binder, templateId);
	}

	@GET
	@Path("{id}/binder_tree")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public BinderTree getSubBinderTree(@PathParam("id") long id) {
        return getSubBinderTree(id, null);
	}

    @GET
   	@Path("{id}/library_folders")
       @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public SearchResultList<BinderBrief> getLibraryFolders(@PathParam("id") long id,
   			@QueryParam("first") @DefaultValue("0") Integer offset,
   			@QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        Junction crit = Restrictions.conjunction();
        crit.add(Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_FOLDER));
        crit.add(Restrictions.eq(Constants.IS_LIBRARY_FIELD, "true"));
        crit.add(Restrictions.disjunction()
                .add(Restrictions.eq(Constants.FAMILY_FIELD, Constants.FAMILY_FIELD_FILE))
                .add(Restrictions.eq(Constants.FAMILY_FIELD, Constants.FAMILY_FIELD_PHOTO)));
        return getSubBinders(id, crit, offset, maxCount, null);
   	}

	// Read entries
	@GET
	@Path("{id}/files")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<FileProperties> getFiles(@PathParam("id") long id,
                                                  @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                  @QueryParam("first") Integer offset,
                                                  @QueryParam("count") Integer maxCount) {
        return getSubFiles(id, recursive, offset, maxCount, null);
	}

    @GET
   	@Path("{id}/team_members")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public SearchResultList<TeamMember> getTeamMembers(@PathParam("id") long id,
            @QueryParam("expand_groups") @DefaultValue("false") boolean expandGroups) {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        SortedSet<Principal> teamMembers = getBinderModule().getTeamMembers(binder, expandGroups);
        SearchResultList<TeamMember> results = new SearchResultList<TeamMember>();
        for (Principal principal : teamMembers) {
            results.append(ResourceUtil.buildTeamMember(id, principal));
        }
        return results;
    }

    @POST
   	@Path("{id}/team_members")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public TeamMember addTeamMember(@PathParam("id") long id, PrincipalBrief principal) {
        _getBinder(id);
        Principal member = getProfileModule().getEntry(principal.getId());
        Set<Long> teamMembers = getBinderModule().getTeamMemberIds(id, false);
        if (!teamMembers.contains(principal.getId())) {
            teamMembers.add(principal.getId());
            getBinderModule().setTeamMembers(id, teamMembers);
        }
        return ResourceUtil.buildTeamMember(id, member);
    }

    @DELETE
   	@Path("{id}/team_members")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public void clearTeamMembers(@PathParam("id") long id) {
        _getBinder(id);
        getBinderModule().setTeamMembershipInherited(id, true);
    }

    @DELETE
   	@Path("{id}/team_members/{memberId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public void deleteTeamMember(@PathParam("id") long id, @PathParam("memberId") long memberId) {
        _getBinder(id);
        Set<Long> teamMembers = getBinderModule().getTeamMemberIds(id, false);
        if (teamMembers.contains(memberId)) {
            teamMembers.remove(memberId);
            getBinderModule().setTeamMembers(id, teamMembers);
        }
        teamMembers = getBinderModule().getTeamMemberIds(id, false);
        logger.debug("");
    }

    @GET
    @Path("{id}/tags")
    public SearchResultList<Tag> getTags(@PathParam("id") Long id) {
        org.kablink.teaming.domain.Binder entry = _getBinder(id);
        Collection<org.kablink.teaming.domain.Tag> tags = getBinderModule().getTags(entry);
        SearchResultList<Tag> results = new SearchResultList<Tag>();
        for (org.kablink.teaming.domain.Tag tag : tags) {
            results.append(ResourceUtil.buildTag(tag));
        }
        return results;
    }

    @POST
    @Path("{id}/tags")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public SearchResultList<Tag> addTag(@PathParam("id") Long id, Tag tag) {
        _getBinder(id);
        org.kablink.teaming.domain.Tag[] tags = getBinderModule().setTag(id, tag.getName(), tag.isPublic());
        SearchResultList<Tag> results = new SearchResultList<Tag>();
        for (org.kablink.teaming.domain.Tag tg : tags) {
            results.append(ResourceUtil.buildTag(tg));
        }
        return results;
    }

    @DELETE
    @Path("{id}/tags")
    public void deleteTags(@PathParam("id") Long id) {
        org.kablink.teaming.domain.Binder entry = _getBinder(id);
        Collection<org.kablink.teaming.domain.Tag> tags = getBinderModule().getTags(entry);
        for (org.kablink.teaming.domain.Tag tag : tags) {
            getBinderModule().deleteTag(id, tag.getId());
        }
    }

    @GET
    @Path("{id}/tags/{tagId}")
    public Tag getTag(@PathParam("id") Long id, @PathParam("tagId") String tagId) {
        org.kablink.teaming.domain.Binder entry = _getBinder(id);
        Collection<org.kablink.teaming.domain.Tag> tags = getBinderModule().getTags(entry);
        for (org.kablink.teaming.domain.Tag tag : tags) {
            if (tag.getId().equals(tagId)) {
                return ResourceUtil.buildTag(tag);
            }
        }
        throw new NoTagByTheIdException(tagId);
    }

    @DELETE
    @Path("{id}/tags/{tagId}")
    public void deleteTag(@PathParam("id") Long id, @PathParam("tagId") String tagId) {
        getFolderModule().deleteTag(null, id, tagId);
    }

    protected Binder createBinder(long parentId, Binder newBinder, Long templateId) throws WriteFilesException, WriteEntryDataException {
        _getBinder(parentId);
        if (newBinder.getTitle()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No binder title was supplied in the POST data.");
        }
        org.kablink.teaming.domain.Binder binder = BinderUtils.createBinder(parentId, newBinder.getTitle(), null, templateId);
        return ResourceUtil.buildBinder(binder, true);
    }

    protected void _deleteBinder(long id) {
        _getBinder(id);
        getBinderModule().preDeleteBinder(id, getLoggedInUserId());
    }

    protected SearchResultList<FileProperties> getSubFiles(long id, boolean recursive, Integer offset, Integer maxCount, String nextUrl) {
        _getBinder(id);
        Map<String,FileIndexData> files = (recursive) ?
                getFileModule().getChildrenFileDataFromIndexRecursively(id) : getFileModule().getChildrenFileDataFromIndex(id);
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.setFirst(0);
        results.setCount(files.size());
        results.setTotal(files.size());
        for (FileIndexData file : files.values()) {
            results.append(ResourceUtil.buildFileProperties(file));
        }

        return results;
    }

    protected SearchResultList<BinderBrief> getSubBinders(long id, Criterion filter, Integer offset, Integer maxCount, String nextUrl) {
        _getBinder(id);
        if (offset==null) {
            offset = 0;
        }
        if (maxCount==null) {
            maxCount = -1;
        }
        Criteria crit = new Criteria();
        if (filter!=null) {
            crit.add(filter);
        }
        crit.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER));
        crit.add(Restrictions.eq(Constants.BINDERS_PARENT_ID_FIELD, ((Long) id).toString()));
        Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, offset, maxCount);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(), resultMap, nextUrl, offset);
        return results;
    }

    protected BinderTree getSubBinderTree(long id, SearchFilter filter) {
        org.kablink.teaming.domain.Binder workspace = _getBinder(id);
        Map<String, Object> options = new HashMap<String, Object>();
        if (filter!=null) {
            options.put( ObjectKeys.SEARCH_SEARCH_FILTER, filter.getFilter() );
        }
        Map resultMap = getBinderModule().getBindersRecursively(workspace, options);
        BinderTree results = new BinderTree();
        SearchResultBuilderUtil.buildSearchResultsTree(results, id, new BinderBriefBuilder(), resultMap);
        results.setItem(null);
        return results;
    }

    protected org.kablink.teaming.domain.Binder _getBinder(long id) {
        try{
            return getBinderModule().getBinder(id);
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.BINDER_NOT_FOUND, "NOT FOUND");
    }
}
