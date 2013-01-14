package org.kablink.teaming.remoting.rest.v1.resource;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoTagByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.BinderUtils;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.exc.NotModifiedException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UniversalBuilder;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderTree;
import org.kablink.teaming.rest.v1.model.EntityId;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.Folder;
import org.kablink.teaming.rest.v1.model.ParentBinder;
import org.kablink.teaming.rest.v1.model.PrincipalBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.Share;
import org.kablink.teaming.rest.v1.model.Tag;
import org.kablink.teaming.rest.v1.model.TeamMember;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Criterion;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Restrictions;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:08 PM
 */
abstract public class AbstractBinderResource extends AbstractDefinableEntityResource {

    abstract protected String getBasePath();
    
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
                            @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                            @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        return ResourceUtil.buildBinder(_getBinder(id), includeAttachments, textDescriptions);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Binder updateBinder(@PathParam("id") long id, Binder binder,
                               @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                               @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions)
            throws WriteFilesException, WriteEntryDataException {
        _getBinder(id);
        getBinderModule().modifyBinder(id,
                new RestModelInputData(binder), null, null, null);
        return ResourceUtil.buildBinder(_getBinder(id), includeAttachments, textDescriptions);
    }

    /**
     * Deletes the specifed binder object.  The binder is moved into the Trash, not permanently deleted.
     * @param id    The ID of the binder to delete.
     */
    @DELETE
    @Path("{id}")
    public void deleteBinder(@PathParam("id") long id,
                             @QueryParam("purge") @DefaultValue("false") boolean purge) {
        _deleteBinder(id, purge);
    }

    @POST
    @Path("{id}/title")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Binder renameBinder(@PathParam("id") Long id,
                                     @FormParam("title") String name,
                                     @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                                     @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) throws WriteFilesException, WriteEntryDataException {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        if (name==null || name.length()==0) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'title' form parameter");
        }

        Map data = new HashMap();
        data.put("title", name);
        InputDataAccessor inputData = new MapInputData(data);

        getBinderModule().modifyBinder(id, inputData, null, null, null);

        return ResourceUtil.buildBinder(_getBinder(id), includeAttachments, textDescriptions);
    }

    @POST
    @Path("{id}/parent_binder")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Binder moveBinder(@PathParam("id") Long id,
                             @FormParam("binder_id") Long newBinderId,
                             @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                             @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) throws WriteFilesException, WriteEntryDataException {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        if (newBinderId ==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'folder_id' form parameter");
        }
        Long finalParentId = null;
        if (newBinderId.equals(ObjectKeys.MY_FILES_ID)) {
            finalParentId = newBinderId;
            if (SearchUtils.useHomeAsMyFiles(this)) {
                newBinderId = SearchUtils.getHomeFolderId(this);
            } else {
                newBinderId = getLoggedInUser().getWorkspaceId();
            }
        }

        org.kablink.teaming.domain.Binder parentBinder = _getBinderImpl(newBinderId);
        getBinderModule().moveBinder(binder.getId(), parentBinder.getId(), null);

        Binder modifiedBinder = ResourceUtil.buildBinder(_getBinder(id), includeAttachments, textDescriptions);
        if (finalParentId!=null) {
            modifiedBinder.setParentBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
        }
        return modifiedBinder;
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
	public Binder createSubBinder(@PathParam("id") long id, Binder binder, @QueryParam("template") Long templateId,
                                  @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions)
            throws WriteFilesException, WriteEntryDataException {
        return createBinder(id, binder, templateId, textDescriptions);
	}

	@GET
	@Path("{id}/binder_tree")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public BinderTree getSubBinderTree(@PathParam("id") long id,
                                       @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        return getSubBinderTree(id, null, textDescriptions);
	}

	@GET
	@Path("{id}/library_tree")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public BinderTree getLibraryTree(@PathParam("id") long id,
                                     @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        return getSubBinderTree(id, buildLibraryTreeCriterion(), textDescriptions);
	}

    @GET
   	@Path("{id}/library_folders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public Response getLibraryFolders(@PathParam("id") long id,
                                         @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                         @QueryParam("first") @DefaultValue("0") Integer offset,
                                         @QueryParam("count") @DefaultValue("-1") Integer maxCount,
                                         @Context HttpServletRequest request) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        SearchResultList<BinderBrief> subBinders = getSubBinders(id, SearchUtils.libraryFolders(), offset, maxCount, getBasePath() + id + "/library_folders", nextParams, textDescriptions, ifModifiedSince);
        return Response.ok(subBinders).lastModified(subBinders.getLastModified()).build();
   	}

    @POST
   	@Path("{id}/library_folders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public Folder createLibraryFolder(@PathParam("id") long id,
                                      org.kablink.teaming.rest.v1.model.BinderBrief binder,
                                      @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions)
            throws WriteFilesException, WriteEntryDataException {
        return (Folder) _createLibraryFolder(id, binder, textDescriptions);
   	}

    // Read entries
	@GET
	@Path("{id}/library_files")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<FileProperties> getLibraryFiles(@PathParam("id") long id,
                                                  @QueryParam("file_name") String fileName,
                                                  @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                  @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                  @QueryParam("first") Integer offset,
                                                  @QueryParam("count") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        if (fileName!=null) {
            nextParams.put("recursive", fileName);
        }
        nextParams.put("recursive", Boolean.toString(recursive));
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        return getSubFiles(id, fileName, recursive, true, includeParentPaths, offset, maxCount, getBasePath() + id + "/library_files", nextParams);
	}

    // Read entries
	@GET
	@Path("{id}/files")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<FileProperties> getFiles(@PathParam("id") long id,
                                                     @QueryParam("file_name") String fileName,
                                                     @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                  @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                  @QueryParam("first") Integer offset,
                                                  @QueryParam("count") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        if (fileName!=null) {
            nextParams.put("recursive", fileName);
        }
        nextParams.put("recursive", Boolean.toString(recursive));
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        return getSubFiles(id, fileName, recursive, false, includeParentPaths, offset, maxCount, getBasePath() + id + "/files", nextParams);
	}

    @GET
    @Path("{id}/recent_activity")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<SearchableObject> getRecentActivity(@PathParam("id") Long id,
                                                                @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                @QueryParam("first") @DefaultValue("0") Integer offset,
                @QueryParam("count") @DefaultValue("20") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));

        List<String> binders = new ArrayList<String>();
        binders.add(id.toString());
        Criteria criteria = SearchUtils.entriesForTrackedPlacesEntriesAndPeople(this, binders, null, null, true, Constants.LASTACTIVITY_FIELD);
        return _getRecentActivity(includeParentPaths, textDescriptions, offset, maxCount, criteria, this.getBasePath() + id + "/recent_activity", nextParams);
    }

    @GET
    @Path("{id}/shares")
    public SearchResultList<Share> getShares(@PathParam("id") Long id) {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        spec.setSharerId(getLoggedInUserId());
        spec.setLatest(true);
        spec.setSharedEntityIdentifier(new EntityIdentifier(id, binder.getEntityType()));
        SearchResultList<Share> results = new SearchResultList<Share>();
        List<ShareItem> shareItems = getShareItems(spec, true);
        for (ShareItem shareItem : shareItems) {
            results.append(ResourceUtil.buildShare(shareItem));
        }
        return results;
    }

    @POST
    @Path("{id}/shares")
    public Share shareEntity(@PathParam("id") Long id, Share share) {
        return shareEntity(_getBinder(id), share);
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
        return getBinderTags(entry, false);
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

    protected Binder _createLibraryFolder(long parentId, BinderBrief newBinder, boolean textDescriptions) throws WriteFilesException, WriteEntryDataException {
        org.kablink.teaming.domain.Binder parent = _getBinderImpl(parentId);
        if (newBinder.getTitle()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No folder title was supplied in the POST data.");
        }
        org.kablink.teaming.domain.Binder binder = FolderUtils.createLibraryFolder(parent, newBinder.getTitle());
        return ResourceUtil.buildBinder(binder, true, textDescriptions);
    }

    protected Binder createBinder(long parentId, Binder newBinder, Long templateId, boolean textDescriptions) throws WriteFilesException, WriteEntryDataException {
        _getBinderImpl(parentId);
        if (newBinder.getTitle()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No binder title was supplied in the POST data.");
        }
        org.kablink.teaming.domain.Binder binder = BinderUtils.createBinder(parentId, newBinder.getTitle(), null, templateId);
        return ResourceUtil.buildBinder(binder, true, textDescriptions);
    }

    protected void _deleteBinder(long id, boolean purge) {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        if (purge || binder.isMirrored()) {
            getBinderModule().deleteBinder(id);
        } else {
            getBinderModule().preDeleteBinder(id, getLoggedInUserId());
        }
    }

    protected SearchResultList<SearchableObject> getSubEntities(long id, boolean recursive, boolean includeBinders,
                                                                boolean includeFolderEntries, boolean includeFiles,
                                                                boolean includeReplies,
                                                                boolean onlyLibrary, boolean includeParentPaths, String keyword,
                                                                Integer offset, Integer maxCount, String nextUrl, Map<String, Object> nextParams, boolean textDescriptions) {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        Junction criterion = Restrictions.conjunction();

        criterion.add(buildSearchBinderCriterion(id, recursive));
        criterion.add(buildDocTypeCriterion(includeBinders, includeFolderEntries, includeFiles, includeReplies));
        if (onlyLibrary) {
            criterion.add(buildLibraryCriterion(onlyLibrary));
        }
        if (keyword!=null) {
            criterion.add(buildKeywordCriterion(keyword));
        }
        Criteria crit = new Criteria();
        crit.add(criterion);
        Map resultsMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>(offset, binder.getModificationDate());
        SearchResultBuilderUtil.buildSearchResults(results, new UniversalBuilder(textDescriptions), resultsMap, nextUrl, nextParams, offset);
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        return results;
    }

    protected SearchResultList<FileProperties> getSubFiles(long id, String fileName, boolean recursive, boolean onlyLibraryFiles, boolean includeParentPaths,
                                                           Integer offset, Integer maxCount, String nextUrl, Map<String, Object> nextParams) {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        Junction criterion = Restrictions.conjunction()
            .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ATTACHMENT));

        criterion.add(buildSearchBinderCriterion(id, recursive));
        if (onlyLibraryFiles) {
            criterion.add(buildLibraryCriterion(onlyLibraryFiles));
        }
        if (fileName!=null) {
            criterion.add(buildFileNameCriterion(fileName));
        }
        List<FileIndexData> files = getFileModule().getFileDataFromIndex(new Criteria().add(criterion));
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>(0, binder.getModificationDate());
        results.setFirst(0);
        results.setCount(files.size());
        results.setTotal(files.size());
        for (FileIndexData file : files) {
            results.append(ResourceUtil.buildFileProperties(file));
        }

        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }

        return results;
    }

    protected SearchResultList<BinderBrief> getSubBinders(long id, Criterion filter, Integer offset, Integer maxCount,
                                                          String nextUrl, Map<String, Object> nextParams, boolean textDescriptions,
                                                          Date modifiedSince) {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
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
        Map resultMap = getBinderModule().searchFolderOneLevelWithInferredAccess(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, offset, maxCount, binder.getId(), binder.getPathName());
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset, binder.getModificationDate());
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(textDescriptions), resultMap, nextUrl, nextParams, offset);
        if (modifiedSince!=null && !modifiedSince.before(results.getLastModified())) {
            throw new NotModifiedException();
        }
        return results;
    }

    protected BinderTree getSubBinderTree(long id, Criterion filter, boolean textDescriptions) {
        _getBinder(id);
        Criteria crit = new Criteria();
        if (filter!=null) {
            crit.add(filter);
        }
        crit.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER));
        crit.add(buildAncentryCriterion(id));
        Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, -1);
        BinderTree results = new BinderTree();
        SearchResultBuilderUtil.buildSearchResultsTree(this, results, id, new BinderBriefBuilder(textDescriptions), resultMap);
        results.setItem(null);
        return results;
    }

    protected org.kablink.teaming.domain.Binder _getBinder(long id) {
        return _getBinderImpl(id);
    }

    private org.kablink.teaming.domain.Binder _getBinderImpl(long id) {
        try{
            return getBinderModule().getBinder(id, false, true);
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.BINDER_NOT_FOUND, "NOT FOUND");
    }
}
