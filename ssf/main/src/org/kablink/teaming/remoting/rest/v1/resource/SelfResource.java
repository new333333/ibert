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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.remoting.rest.v1.resource;

import com.sun.jersey.spi.resource.Singleton;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.LinkUriUtil;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UniversalBuilder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderTree;
import org.kablink.teaming.rest.v1.model.DefinableEntity;
import org.kablink.teaming.rest.v1.model.DefinableEntityBrief;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.LongIdLinkPair;
import org.kablink.teaming.rest.v1.model.ParentBinder;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.TeamBrief;
import org.kablink.teaming.rest.v1.model.User;
import org.kablink.teaming.rest.v1.model.ZoneConfig;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Restrictions;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.*;

/**
 * User: david
 * Date: 5/16/12
 * Time: 4:04 PM
 */
@Path("/self")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class SelfResource extends AbstractFileResource {

    /**
     * Gets the User object representing the authenticated user.
     * @param includeAttachments    Configures whether attachments should be included in the returned User object.
     * @return  Returns the authenticated User object
     */
    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public User getSelf(@QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                        @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        Principal entry = getLoggedInUser();

        User user = ResourceUtil.buildUser((org.kablink.teaming.domain.User) entry, includeAttachments, textDescriptions);
        user.setDiskSpaceQuota(getProfileModule().getMaxUserQuota(entry.getId()));
        user.setLink("/self");
        user.addAdditionalLink("roots", "/self/roots");
        if (user.getWorkspace()!=null) {
            user.addAdditionalLink("my_file_folders", user.getWorkspace().getLink() + "/library_folders");
        }
        user.addAdditionalLink("file_folders", "/self/file_folders");
        user.addAdditionalLink("my_files", "/self/my_files");
        user.addAdditionalLink("net_folders", "/self/net_folders");
        user.addAdditionalLink("shared_with_me", "/self/shared_with_me");
        user.addAdditionalLink("shared_by_me", "/self/shared_by_me");
        user.addAdditionalPermaLink("my_files", PermaLinkUtil.getUserPermalink(null, entry.getId().toString(), PermaLinkUtil.COLLECTION_MY_FILES));
        user.addAdditionalPermaLink("net_folders", PermaLinkUtil.getUserPermalink(null, entry.getId().toString(), PermaLinkUtil.COLLECTION_NET_FOLDERS));
        user.addAdditionalPermaLink("shared_with_me", PermaLinkUtil.getUserPermalink(null, entry.getId().toString(), PermaLinkUtil.COLLECTION_SHARED_WITH_ME));
        user.addAdditionalPermaLink("shared_by_me", PermaLinkUtil.getUserPermalink(null, entry.getId().toString(), PermaLinkUtil.COLLECTION_SHARED_BY_ME));
        Long myFilesFolderId = SearchUtils.getMyFilesFolderId(this, entry.getWorkspaceId(), true);
        if (myFilesFolderId!=null) {
            user.setHiddenFilesFolder(new LongIdLinkPair(myFilesFolderId, LinkUriUtil.getFolderLinkUri(myFilesFolderId)));
        }
        ZoneConfig zoneConfig = ResourceUtil.buildZoneConfig(
                getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId()),
                getProfileModule().getUserProperties(getLoggedInUserId()));

        user.setDesktopAppConfig(zoneConfig.getDesktopAppConfig());
        user.setMobileAppConfig(zoneConfig.getMobileAppConfig());
        return user;
    }

    /**
     * Returns the authenticated user's favorite binders
     * @return Returns a list of BinderBrief objects.
     */
    @GET
    @Path("/favorites")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<BinderBrief> getFavorites() {
        Long userId = getLoggedInUserId();
        List<Binder> binders = getProfileModule().getUserFavorites(userId);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>();
        for (Binder binder : binders) {
            results.append(ResourceUtil.buildBinderBrief(binder));
        }
        return results;
    }

    /**
     * Returns the teams that the authenticated user is a member of.
     * @return Returns a list of BinderBrief objects.
     */
    @GET
    @Path("/teams")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<TeamBrief> getTeams() {
        Long userId = getLoggedInUserId();
        List<TeamInfo> binders = getProfileModule().getUserTeams(userId);
        SearchResultList<TeamBrief> results = new SearchResultList<TeamBrief>();
        for (TeamInfo binder : binders) {
            results.append(ResourceUtil.buildTeamBrief(binder));
        }
        return results;
    }

    /**
     * Returns a list of virtual workspace roots for the authenticated user.  This is useful for displaying
     * starting points for browsing different parts of the workspace hierarchy.
     * @deprecated  This operation is temporary and is very likely to change.
     * @return Returns a list of BinderBrief objects.
     */
    @GET
    @Path("/roots")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<BinderBrief> getRoots() {
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>();
        results.appendAll(new BinderBrief[] {
                getFakeMyWorkspace(), getFakeMyTeams(), getFakeMyFavorites(),
                ResourceUtil.buildBinderBrief(getBinderModule().getBinder(getWorkspaceModule().getTopWorkspaceId()))
        });
        return results;
    }

    @GET
    @Path("/file_folders")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getFileFolders() {
        return getFakeMyFileFolders();
    }

    @GET
    @Path("/net_folders")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getNetFolders() {
        return getFakeNetFolders();
    }

    @GET
    @Path("/shared_with_me")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getSharedWithMe() {
        return getFakeSharedWithMe();
    }

    @GET
    @Path("/shared_by_me")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getSharedByMe() {
        return getFakeSharedByMe();
    }

    @GET
    @Path("/my_files")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getMyFiles() {
        return getFakeMyFileFolders();
    }

    @GET
    @Path("/my_files/library_folders")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<BinderBrief> getMyFileLibraryFolders(
            @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("text_descriptions", textDescriptions);
        Criteria crit = SearchUtils.getMyFilesSearchCriteria(this, getLoggedInUser().getWorkspaceId(), true, false, false, false);
        SearchResultList<BinderBrief> results = lookUpBinders(crit, textDescriptions, offset, maxCount, "/self/my_files/library_folders", nextParams);
        setMyFilesParents(results);
        return results;
    }

    @POST
   	@Path("/my_files/library_folders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public org.kablink.teaming.rest.v1.model.Folder createLibraryFolder(
                                      org.kablink.teaming.rest.v1.model.BinderBrief newBinder,
                                      @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions)
            throws WriteFilesException, WriteEntryDataException {
        org.kablink.teaming.domain.Binder parent = getBinderModule().getBinder(getLoggedInUser().getWorkspaceId());
        if (newBinder.getTitle()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No folder title was supplied in the POST data.");
        }
        org.kablink.teaming.domain.Binder binder = FolderUtils.createLibraryFolder(parent, newBinder.getTitle());
        org.kablink.teaming.rest.v1.model.Folder folder = (org.kablink.teaming.rest.v1.model.Folder) ResourceUtil.buildBinder(binder, true, textDescriptions);
        folder.setParentBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
        return folder;
   	}

    @GET
    @Path("/my_files/library_tree")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderTree getMyFileLibraryTree(
            @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        BinderTree results = new BinderTree();
        SearchResultList<BinderBrief> folders = getMyFileLibraryFolders(false, 0, -1);
        if (folders.getCount()>0) {
            Criteria crit = new Criteria();
            crit.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER));
            List<String> idList = new ArrayList<String>(folders.getCount());
            for (BinderBrief folder : folders.getResults()) {
                idList.add(folder.getId().toString());
            }
            crit.add(Restrictions.in(Constants.ENTRY_ANCESTRY, idList));
            Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, -1);
            SearchResultBuilderUtil.buildSearchResultsTree(results, folders.getResults().toArray(new BinderBrief[folders.getCount()]),
                    new BinderBriefBuilder(textDescriptions), resultMap);
            for (SearchResultTreeNode<BinderBrief> node : results.getChildren()) {
                node.getItem().setParentBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
            }
            results.setItem(null);
        }
        return results;
    }

    @GET
    @Path("/my_files/library_entities")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<SearchableObject> getMyFileLibraryEntities(
              @QueryParam("recursive") @DefaultValue("false") boolean recursive,
              @QueryParam("binders") @DefaultValue("true") boolean includeBinders,
              @QueryParam("folder_entries") @DefaultValue("true") boolean includeFolderEntries,
              @QueryParam("files") @DefaultValue("true") boolean includeFiles,
              @QueryParam("replies") @DefaultValue("true") boolean includeReplies,
              @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
              @QueryParam("keyword") String keyword,
              @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
              @QueryParam("first") @DefaultValue("0") Integer offset,
              @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>(offset);
        Criteria subContextSearch = null;
        if (recursive) {
            SearchResultList<BinderBrief> folders = getMyFileLibraryFolders(false, 0, -1);
            if (folders.getCount()>0) {
                subContextSearch = new Criteria();
                Junction searchContext = Restrictions.disjunction();
                for (BinderBrief binder : folders.getResults()) {
                    Junction shareCrit = Restrictions.conjunction();
                    shareCrit.add(buildSearchBinderCriterion(binder.getId(), true));
                    searchContext.add(shareCrit);
                }
                subContextSearch.add(searchContext);
            }
        }
        Criteria crit = new Criteria();
        if (keyword!=null) {
            crit.add(buildKeywordCriterion(keyword));
        }
        crit.add(buildDocTypeCriterion(includeBinders, includeFolderEntries, includeFiles, includeReplies));
        crit.add(buildLibraryCriterion(true));
        Criteria myFilesCrit = SearchUtils.getMyFilesSearchCriteria(this, getLoggedInUser().getWorkspaceId(), includeBinders, includeFolderEntries, includeReplies, includeFiles);
        if (subContextSearch!=null) {
            crit.add(Restrictions.disjunction()
                    .add(myFilesCrit.asJunction())
                    .add(subContextSearch.asJunction())
            );
        } else {
            crit.add(myFilesCrit.asJunction());
        }

        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("recursive", Boolean.toString(recursive));
        nextParams.put("binders", Boolean.toString(includeBinders));
        nextParams.put("folder_entries", Boolean.toString(includeFolderEntries));
        nextParams.put("files", Boolean.toString(includeFiles));
        nextParams.put("replies", Boolean.toString(includeReplies));
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        if (keyword!=null) {
            nextParams.put("keyword", keyword);
        }
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        Map resultsMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultBuilderUtil.buildSearchResults(results, new UniversalBuilder(textDescriptions), resultsMap,
                "/self/my_files/library_entities", nextParams, offset);
        setMyFilesParents(results);
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        return results;
    }

    private void setMyFilesParents(SearchResultList results) {
        List<Long> hiddenFolderIds = getEffectiveMyFilesFolderIds();
        Set<Long> allParentIds = new HashSet(hiddenFolderIds);
        allParentIds.add(getLoggedInUser().getWorkspaceId());
        for (Object obj : results.getResults()) {
            if (obj instanceof FileProperties && allParentIds.contains(((FileProperties)obj).getBinder().getId())) {
                ((FileProperties)obj).setBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
            } else if (obj instanceof DefinableEntity && allParentIds.contains(((DefinableEntity)obj).getParentBinder().getId())) {
                ((DefinableEntity)obj).setParentBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
            } else if (obj instanceof DefinableEntityBrief && allParentIds.contains(((DefinableEntityBrief)obj).getParentBinder().getId())) {
                ((DefinableEntityBrief)obj).setParentBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
            }
        }
    }

    private List<Long> getEffectiveMyFilesFolderIds() {
        org.kablink.teaming.domain.User user = getLoggedInUser();
        if (SearchUtils.useHomeAsMyFiles(this, user)) {
            return SearchUtils.getHomeFolderIds(this, user);
        } else {
            return SearchUtils.getMyFilesFolderIds(this, user);
        }
    }

    @GET
    @Path("/my_files/library_files")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<FileProperties> getMyFileLibraryFiles(
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
        Criteria crit = new Criteria();
        crit.add(buildAttachmentsCriterion());
        crit.add(buildLibraryCriterion(true));
        Junction searchContexts = null;
        if (recursive) {
            SearchResultList<BinderBrief> folders = getMyFileLibraryFolders(false, 0, -1);
            if (folders.getCount()>0) {
                searchContexts = Restrictions.disjunction();
                for (BinderBrief folder : folders.getResults()) {
                    searchContexts.add(buildAncentryCriterion(folder.getId()));
                }
            }
        }
        Criteria myFiles = SearchUtils.getMyFilesSearchCriteria(this, getLoggedInUser().getWorkspaceId(), false, false, false, true);
        if (searchContexts!=null) {
            searchContexts.add(myFiles.asJunction());
            crit.add(searchContexts);
        } else {
            crit.add(myFiles.asJunction());
        }
        if (fileName!=null) {
            crit.add(buildFileNameCriterion(fileName));
        }
        SearchResultList<FileProperties> resultList = lookUpAttachments(crit, offset, maxCount, "/self/my_files/library_files", nextParams);
        setMyFilesParents(resultList);
        if (includeParentPaths) {
            populateParentBinderPaths(resultList);
        }
        return resultList;
    }

    @POST
    @Path("/my_files/library_files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties addLibraryFileFromMultipart(@QueryParam("file_name") String fileName,
                                         @QueryParam("mod_date") String modDateISO8601,
                                         @QueryParam("md5") String expectedMd5,
                                         @QueryParam("overwrite_existing") @DefaultValue("false") Boolean overwriteExisting,
                                         @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        Folder folder = _getHiddenFilesFolder();
        InputStream is = getInputStreamFromMultipartFormdata(request);
        FileProperties file = createEntryWithAttachment(folder, fileName, modDateISO8601, expectedMd5, overwriteExisting, is);
        file.setBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
        return file;
    }

    @POST
    @Path("/my_files/library_files")
    @Consumes("*/*")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties addLibraryFile(@QueryParam("file_name") String fileName,
                                         @QueryParam("mod_date") String modDateISO8601,
                                         @QueryParam("md5") String expectedMd5,
                                         @QueryParam("overwrite_existing") @DefaultValue("false") Boolean overwriteExisting,
                                         @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        Folder folder = _getHiddenFilesFolder();
        InputStream is = getRawInputStream(request);
        FileProperties file = createEntryWithAttachment(folder, fileName, modDateISO8601, expectedMd5, overwriteExisting, is);
        file.setBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
        return file;
    }

    @GET
    @Path("/my_files/recent_activity")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<SearchableObject> getMyFileRecentActivity(
            @QueryParam("file_name") String fileName,
            @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
            @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("20") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        if (fileName!=null) {
            nextParams.put("recursive", fileName);
        }
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));

        List<String> binders = null;
        List<String> entries = null;
        SearchResultList<BinderBrief> folders = getMyFileLibraryFolders(true, 0, -1);
        if (folders.getCount()>0) {
            binders = new ArrayList<String>();
            for (BinderBrief binder : folders.getResults()) {
                binders.add(binder.getId().toString());
            }
        }
        SearchResultList<FileProperties> files = getMyFileLibraryFiles(fileName, false, false, 0, -1);
        if (files.getCount()>0) {
            entries = new ArrayList<String>();
            for (FileProperties file : files.getResults()) {
                entries.add(file.getOwningEntity().getId().toString());
            }
        }
        if (entries==null && binders==null) {
            return new SearchResultList<SearchableObject>();
        }
        Criteria criteria = SearchUtils.entriesForTrackedPlacesEntriesAndPeople(this, binders, entries, null, true, Constants.LASTACTIVITY_FIELD);
        SearchResultList<SearchableObject> resultList = _getRecentActivity(includeParentPaths, textDescriptions, offset, maxCount, criteria, "/net_folders/recent_activity", nextParams);
        setMyFilesParents(resultList);
        return resultList;
    }

    private BinderBrief getFakeMyWorkspace() {
        org.kablink.teaming.domain.User loggedInUser = RequestContextHolder.getRequestContext().getUser();
        Binder myWorkspace = getBinderModule().getBinder(loggedInUser.getWorkspaceId());
        BinderBrief binder = ResourceUtil.buildBinderBrief(myWorkspace);
        //TODO: localize
        binder.setTitle("My Workspace");
        return binder;
    }

    private BinderBrief getFakeMyTeams() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setTitle("My Teams");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace_team.png"));
        binder.addAdditionalLink("child_binders", "/self/teams");
        return binder;
    }

    private BinderBrief getFakeMyFavorites() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setTitle("My Favorites");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace_star.png"));
        binder.addAdditionalLink("child_binders", "/self/favorites");
        return binder;
    }

    private BinderBrief getFakeFileFolders() {
        org.kablink.teaming.domain.User user = getLoggedInUser();
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setTitle("File Folders");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace.png"));
        String baseUri = "/workspaces/" + user.getWorkspaceId();
        binder.addAdditionalLink("child_binders", baseUri + "/library_folders");
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri + "/library_folders");
        binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
        binder.addAdditionalLink("recent_activity", baseUri + "/recent_activity");
        return binder;
    }

    private BinderBrief getFakeMyFileFolders() {
        org.kablink.teaming.domain.User user = getLoggedInUser();
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setId(ObjectKeys.MY_FILES_ID);
        binder.setTitle("My Files");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace.png"));
        binder.setPermaLink(PermaLinkUtil.getUserPermalink(null, user.getId().toString(), PermaLinkUtil.COLLECTION_MY_FILES));
        String baseUri = "/self/my_files";
        binder.setLink(baseUri);
        binder.addAdditionalLink("child_binders", baseUri + "/library_folders");
        binder.addAdditionalLink("child_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri + "/library_folders");
        binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
        binder.addAdditionalLink("recent_activity", baseUri + "/recent_activity");
        return binder;
    }

    private BinderBrief getFakeSharedWithMe() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setId(ObjectKeys.SHARED_WITH_ME_ID);
        binder.setTitle("Shared with Me");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace.png"));
        Long userId = getLoggedInUserId();
        binder.setPermaLink(PermaLinkUtil.getUserPermalink(null, userId.toString(), PermaLinkUtil.COLLECTION_SHARED_WITH_ME));
        binder.setLink("/self/shared_with_me");
        String baseUri = "/shares/with_user/" + userId;
        binder.addAdditionalLink("child_binders", baseUri + "/binders");
        binder.addAdditionalLink("child_binder_tree", baseUri + "/binder_tree");
        binder.addAdditionalLink("child_entries", baseUri + "/entries");
        binder.addAdditionalLink("child_files", baseUri + "/files");
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri + "/library_folders");
        binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
        binder.addAdditionalLink("recent_activity", baseUri + "/recent_activity");
        return binder;
    }

    private BinderBrief getFakeSharedByMe() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setId(ObjectKeys.SHARED_BY_ME_ID);
        binder.setTitle("Shared by Me");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace.png"));
        Long userId = getLoggedInUserId();
        binder.setPermaLink(PermaLinkUtil.getUserPermalink(null, userId.toString(), PermaLinkUtil.COLLECTION_SHARED_BY_ME));
        binder.setLink("/self/shared_by_me");
        String baseUri = "/shares/by_user/" + userId;
        binder.addAdditionalLink("child_binders", baseUri + "/binders");
//        binder.addAdditionalLink("child_binder_tree", baseUri + "/binder_tree");
        binder.addAdditionalLink("child_entries", baseUri + "/entries");
        binder.addAdditionalLink("child_files", baseUri + "/files");
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri + "/library_folders");
//        binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
        binder.addAdditionalLink("recent_activity", baseUri + "/recent_activity");
        return binder;
    }

    private BinderBrief getFakeNetFolders() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setId(ObjectKeys.NET_FOLDERS_ID);
        binder.setTitle("Net Folders");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace.png"));
        Long userId = getLoggedInUserId();
        binder.setLink("/self/net_folders");
        binder.setPermaLink(PermaLinkUtil.getUserPermalink(null, userId.toString(), PermaLinkUtil.COLLECTION_NET_FOLDERS));
        String baseUri = "/net_folders";
        binder.addAdditionalLink("child_binders", baseUri);
        //binder.addAdditionalLink("child_binder_tree", baseUri + "/binder_tree");
        //binder.addAdditionalLink("child_files", baseUri + "/files");
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        //binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri);
        //binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
        binder.addAdditionalLink("recent_activity", baseUri + "/recent_activity");
        return binder;
    }
}
