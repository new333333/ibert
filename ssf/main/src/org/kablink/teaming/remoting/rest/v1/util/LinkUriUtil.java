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
package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.rest.v1.model.BaseRestObject;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.FileVersionProperties;
import org.kablink.teaming.rest.v1.model.FolderEntry;
import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.teaming.rest.v1.model.Tag;
import org.kablink.teaming.rest.v1.model.TemplateBrief;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * User: david
 * Date: 5/24/12
 * Time: 9:11 AM
 */
public class LinkUriUtil {
    public static String getBinderLinkUri(Long id) {
        return "/binder/" + id;
    }

    public static String getFolderLinkUri(Long id) {
        return getDefinableEntityLinkUri(EntityIdentifier.EntityType.folder, id);
    }

    public static String getWorkspaceLinkUri(Long id) {
        return getDefinableEntityLinkUri(EntityIdentifier.EntityType.workspace, id);
    }

    public static String getBinderLinkUri(org.kablink.teaming.domain.Binder binder) {
        if (binder instanceof org.kablink.teaming.domain.Folder) {
            return getFolderLinkUri(binder.getId());
        } else if (binder instanceof org.kablink.teaming.domain.Workspace) {
            return getWorkspaceLinkUri(binder.getId());
        }
        return getBinderLinkUri(binder.getId());
    }

    public static String getBinderLinkUri(BinderBrief binder) {
        if ("folder".equals(binder.getEntityType())) {
            return getFolderLinkUri(binder.getId());
        } else if ("workspace".equals(binder.getEntityType())) {
            return getWorkspaceLinkUri(binder.getId());
        }
        return getBinderLinkUri(binder.getId());
    }

    public static String getPrincipalLinkUri(org.kablink.teaming.domain.Principal principal) {
        if (principal instanceof org.kablink.teaming.domain.UserPrincipal) {
            return getUserLinkUri(principal.getId());
        }
        return null;
    }

    public static String getFolderEntryLinkUri(Long id) {
        return getDefinableEntityLinkUri(EntityIdentifier.EntityType.folderEntry, id);
    }

    public static String getUserLinkUri(Long id) {
        return getDefinableEntityLinkUri(EntityIdentifier.EntityType.user, id);
    }

    public static String getFilePropertiesLinkUri(FileProperties fp) {
        return getFileBaseLinkUri(fp) + "/metadata";
    }

    public static String getFileBaseLinkUri(FileProperties fp) {
        return "/file/" + fp.getId();
    }

    public static String getFileVersionBaseLinkUri(FileVersionProperties fp) {
        return "/file_version/" + fp.getId();
    }

    public static void populateFolderEntryLinks(BaseRestObject model, Long id) {
        model.setLink(getFolderEntryLinkUri(id));
        model.addAdditionalLink("files", model.getLink() + "/files");
        model.addAdditionalLink("reservation", model.getLink() + "/reservation");
        model.addAdditionalLink("replies", model.getLink() + "/replies");
        model.addAdditionalLink("reply_tree", model.getLink() + "/reply_tree");
        model.addAdditionalLink("tags", model.getLink() + "/tags");
    }

    public static void populateBinderLinks(BaseRestObject model, boolean isWorkspace, boolean isFolder) {
        model.addAdditionalLink("child_binders", model.getLink() + "/binders");
        model.addAdditionalLink("child_binder_tree", model.getLink() + "/binder_tree");
        model.addAdditionalLink("child_files", model.getLink() + "/files");
        if (isWorkspace) {
            model.addAdditionalLink("child_workspaces", model.getLink() + "/workspaces");
            model.addAdditionalLink("child_folders", model.getLink() + "/folders");
        }
        if (isFolder) {
            model.addAdditionalLink("child_entries", model.getLink() + "/entries");
            model.addAdditionalLink("child_folders", model.getLink() + "/folders");
        }
    }

    public static void populateFileLinks(FileProperties fp) {
        fp.setLink(getFilePropertiesLinkUri(fp));
        String baseUrl = getFileBaseLinkUri(fp);
        fp.addAdditionalLink("content", baseUrl);
        fp.addAdditionalLink("major_version", baseUrl + "/major_version");
        fp.addAdditionalLink("versions", baseUrl + "/versions");
        fp.addAdditionalLink("current_version", baseUrl + "/version/current");
    }

    public static void populateFileVersionLinks(FileVersionProperties fp) {
        String baseUrl = getFileVersionBaseLinkUri(fp);
        fp.addAdditionalLink("content", baseUrl);
        fp.setLink(baseUrl + "/metadata");
        fp.addAdditionalLink("note", baseUrl + "/note");
        fp.addAdditionalLink("status", baseUrl + "/status");
        fp.addAdditionalLink("current", baseUrl + "/current");
        fp.addAdditionalLink("file_metadata", "/file/" + fp.getId() + "/metadata");
    }

    public static String buildIconLinkUri(String iconName) {
        if (iconName==null) {
            return null;
        }
        org.kablink.teaming.domain.User user = RequestContextHolder.getRequestContext().getUser();
        String colorTheme = user.getTheme();
        if (colorTheme == null || colorTheme.equals("")) colorTheme = WebKeys.THEME_IC_ICE_BLUE;
        return "/" + MiscUtil.getStaticPath() + "i/" + colorTheme + iconName;
    }

    public static String getDefinitionLinkUri(String id) {
        return "/definition/" + id;
    }

    public static String getTemplateLinkUri(TemplateBrief model) {
        return "/template/" + model.getId();
    }

    public static String getTagLinkUri(Tag model) {
        EntityIdentifier.EntityType type = EntityIdentifier.EntityType.valueOf(model.getEntity().getType());
        String baseEntityUri = getDefinableEntityLinkUri(type, model.getEntity().getId());
        if (baseEntityUri!=null) {
            return baseEntityUri + "/tag/" + model.getId();
        }
        return null;
    }

    public static String getDefinableEntityLinkUri(EntityIdentifier.EntityType type, Long id) {
        if (type==EntityIdentifier.EntityType.folderEntry) {
            return "/folder_entry/" + id;
        } else if (type==EntityIdentifier.EntityType.folder) {
            return "/folder/" + id;
        } else if (type==EntityIdentifier.EntityType.group) {
            return "/group/" + id;
        } else if (type==EntityIdentifier.EntityType.user) {
            return "/user/" + id;
        } else if (type==EntityIdentifier.EntityType.workspace) {
            return "/workspace/" + id;
        }
        return null;
    }
}
