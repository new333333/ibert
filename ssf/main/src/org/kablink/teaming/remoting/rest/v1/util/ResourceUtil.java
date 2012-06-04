/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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

package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.domain.*;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.rest.v1.model.*;
import org.kablink.teaming.rest.v1.model.AverageRating;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.DefinableEntity;
import org.kablink.teaming.rest.v1.model.Description;
import org.kablink.teaming.rest.v1.model.Entry;
import org.kablink.teaming.rest.v1.model.Folder;
import org.kablink.teaming.rest.v1.model.FolderEntry;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.rest.v1.model.Principal;
import org.kablink.teaming.rest.v1.model.Tag;
import org.kablink.teaming.rest.v1.model.User;
import org.kablink.teaming.rest.v1.model.Workspace;
import org.kablink.teaming.rest.v1.model.ZoneConfig;
import org.kablink.teaming.web.util.PermaLinkUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * This class contains utility methods that are shared among multiple resource types.
 * Do not place in this class any methods that are used by a single resource type.
 * 
 * @author jong
 *
 */
public class ResourceUtil {

    public static Calendar toCalendar(Date date) {
   		Calendar cal = Calendar.getInstance();
   		cal.setTime(date);
   		return cal;
   	}

    public static FolderEntryBrief buildFolderEntryBrief(org.kablink.teaming.domain.FolderEntry entry) {
        FolderEntryBrief model = new FolderEntryBrief();
        populateDefinableEntityBrief(model, entry);
        model.setDocLevel(entry.getDocLevel());
        model.setDocNumber(entry.getDocNumber());
        List<String> filenames = new ArrayList<String>();
        for (FileAttachment attach : entry.getFileAttachments()) {
            filenames.add(attach.getName());
        }
        model.setFileNames(filenames.toArray(new String[filenames.size()]));
        LinkUriUtil.populateFolderEntryLinks(model, model.getId());
        return model;
    }

    public static FolderEntry buildFolderEntry(org.kablink.teaming.domain.FolderEntry entry, boolean includeAttachments) {
        FolderEntry model = new FolderEntry();
        populateEntry(model, entry, includeAttachments);
        model.setDocLevel(entry.getDocLevel());
        model.setDocNumber(entry.getDocNumber());
        model.setReservation(buildHistoryStamp(entry.getReservation()));
        model.setReplyCount(entry.getReplyCount());
        model.setTotalReplyCount(entry.getTotalReplyCount());
        org.kablink.teaming.domain.FolderEntry parent = entry.getParentEntry();
        if (parent!=null) {
            model.setParentEntry(new LongIdLinkPair(parent.getId(), LinkUriUtil.getFolderEntryLinkUri(parent.getId())));
        }
        org.kablink.teaming.domain.FolderEntry top = entry.getTopEntry();
        if (top!=null) {
            model.setTopEntry(new LongIdLinkPair(top.getId(), LinkUriUtil.getFolderEntryLinkUri(top.getId())));
        }
        LinkUriUtil.populateFolderEntryLinks(model, model.getId());
        return model;
    }

    public static FileProperties buildFileProperties(FileIndexData fa) {
        FileProperties fp = new FileProperties();
        fp.setId(fa.getId());
        fp.setOwningEntity(buildEntityId(fa.getOwningEntityType(), fa.getOwningEntityId()));
        fp.setBinder(new LongIdLinkPair(fa.getBinderId(), LinkUriUtil.getBinderLinkUri(fa.getBinderId())));
        fp.setName(fa.getName());
        fp.setCreation(new HistoryStamp(new LongIdLinkPair(fa.getCreatorId(), LinkUriUtil.getUserLinkUri(fa.getCreatorId())),
                                        fa.getCreatedDate()));
        fp.setModification(new HistoryStamp(new LongIdLinkPair(fa.getModifierId(), LinkUriUtil.getUserLinkUri(fa.getModifierId())),
                                            fa.getModifiedDate()));
        fp.setLength(fa.getSize());
        LinkUriUtil.populateFileLinks(fp);
        return fp;
    }

    public static FileProperties buildFileProperties(FileAttachment fa) {
        FileAttachment.FileLock fl = fa.getFileLock();
        Long creatorId = fa.getCreation().getPrincipal().getId();
        Long modifierId = fa.getModification().getPrincipal().getId();
        FileProperties fp = new FileProperties(fa.getId(),
      				fa.getFileItem().getName(),
      				new HistoryStamp(new LongIdLinkPair(creatorId, LinkUriUtil.getUserLinkUri(creatorId)),
                              fa.getCreation().getDate()),
      				new HistoryStamp(new LongIdLinkPair(modifierId, LinkUriUtil.getUserLinkUri(modifierId)),
                              fa.getModification().getDate()),
      				fa.getFileItem().getLength(),
      				fa.getHighestVersionNumber(),
      				fa.getMajorVersion(),
      				fa.getMinorVersion(),
      				fa.getFileItem().getDescription().getText(),
      				fa.getFileStatus(),
      				(fl != null && fl.getOwner() != null)? fl.getOwner().getId():null,
      				(fl!= null)? fl.getExpirationDate():null);
        org.kablink.teaming.domain.DefinableEntity entity = fa.getOwner().getEntity();
        fp.setOwningEntity(buildEntityId(entity.getEntityType(), entity.getId()));
        Long binderId = entity.getParentBinder().getId();
        fp.setBinder(new LongIdLinkPair(binderId, LinkUriUtil.getBinderLinkUri(binderId)));
        LinkUriUtil.populateFileLinks(fp);
        return fp;
    }

    public static TeamBrief buildTeamBrief(org.kablink.teaming.domain.TeamInfo binder) {
        TeamBrief model = new TeamBrief();
        model.setId(binder.getId());
        model.setTitle(binder.getTitle());
        model.setEntityType(binder.getEntityType());
        model.setFamily(binder.getFamily());
        model.setLibrary(binder.getLibrary());
        model.setMirrored(binder.getMirrored());
        model.setPath(binder.getPath());
        model.setCreation(buildHistoryStamp(binder.getCreation()));
        model.setModification(buildHistoryStamp(binder.getModification()));
        model.setLink(LinkUriUtil.getBinderLinkUri(model));
        if (model.isFolder()) {
            LinkUriUtil.populateFolderLinks(model);
        } else if (model.isWorkspace()) {
            LinkUriUtil.populateWorkspaceLinks(model);
        }
        return model;
    }

    public static Binder buildBinder(org.kablink.teaming.domain.Binder binder, boolean includeAttachments) {
        Binder model;
        if (binder instanceof org.kablink.teaming.domain.Folder) {
            model = new Folder();
            populateFolder((Folder)model, (org.kablink.teaming.domain.Folder)binder, includeAttachments);
        } else if (binder instanceof org.kablink.teaming.domain.Workspace) {
            model = new Workspace();
            populateWorkspace((Workspace)model, (org.kablink.teaming.domain.Workspace)binder, includeAttachments);
        } else {
            model = new Binder();
            populateBinder(model, binder, includeAttachments);
        }
        return model;
    }

    public static BinderBrief buildBinderBrief(org.kablink.teaming.domain.Binder binder) {
        BinderBrief model = new BinderBrief();
        populateBinderBrief(model, binder);
        return model;
    }

    public static User buildUser(org.kablink.teaming.domain.User user, boolean includeAttachments) {
        User model = new User();
        populatePrincipal(model, user, includeAttachments);
        model.setFirstName(user.getFirstName());
        model.setMiddleName(user.getMiddleName());
        model.setLastName(user.getLastName());
        model.setOrganization(user.getOrganization());
        model.setPhone(user.getPhone());

        java.util.Locale locale = user.getLocale();
        if(locale != null) {
            Locale localeModel = new Locale();
            localeModel.setLanguage(locale.getLanguage());
            localeModel.setCountry(locale.getCountry());
            model.setLocale(localeModel);
        }
        if(user.getTimeZone() != null) {
            model.setTimeZone(user.getTimeZone().getID());
        }
        model.setSkypeId(user.getSkypeId());
        model.setTwitterId(user.getTwitterId());
        if (user.getMiniBlogId()!=null) {
            model.setMiniBlog(new LongIdLinkPair(user.getMiniBlogId(), LinkUriUtil.getFolderLinkUri(user.getMiniBlogId())));
        }
        model.setDiskQuota(user.getDiskQuota());
        model.setFileSizeLimit(user.getFileSizeLimit());
        model.setDiskSpaceUsed(user.getDiskSpaceUsed());
        if (user.getWorkspaceId()!=null) {
            model.setWorkspace(new LongIdLinkPair(user.getWorkspaceId(), LinkUriUtil.getWorkspaceLinkUri(user.getWorkspaceId())));
        }

        LinkUriUtil.populateUserLinks(model);

        return model;
    }

    public static ZoneConfig buildZoneConfig(org.kablink.teaming.domain.ZoneConfig config) {
        ZoneConfig modelConfig = new ZoneConfig();
        BinderQuotasConfig binderQuotasConfig = new BinderQuotasConfig();
        binderQuotasConfig.setAllowOwner(config.isBinderQuotaAllowBinderOwnerEnabled());
        binderQuotasConfig.setEnabled(config.isBinderQuotaEnabled());
        binderQuotasConfig.setInitialized(config.isBinderQuotaInitialized());
        modelConfig.setBinderQuotasConfig(binderQuotasConfig);

        DiskQuotasConfig diskQuotasConfig = new DiskQuotasConfig();
        diskQuotasConfig.setEnabled(config.isDiskQuotaEnabled());
        diskQuotasConfig.setHighwaterPercentage(config.getDiskQuotasHighwaterPercentage());
        diskQuotasConfig.setUserDefault(config.getDiskQuotaUserDefault());
        modelConfig.setDiskQuotasConfig(diskQuotasConfig);

        modelConfig.setFileSizeLimitUserDefault(config.getFileSizeLimitUserDefault());
        modelConfig.setFileVersionsMaxAge(config.getFileVersionsMaxAge());

        FsaConfig fsaConfig = new FsaConfig();
        fsaConfig.setAutoUpdateUrl(config.getFsaAutoUpdateUrl());
        fsaConfig.setEnabled(config.getFsaEnabled());
        fsaConfig.setSyncInterval(config.getFsaSynchInterval());
        modelConfig.setFsaConfig(fsaConfig);

        modelConfig.setMobileAccessEnabled(config.isMobileAccessEnabled());
        return modelConfig;
    }

	public static FileVersionProperties fileVersionFromFileAttachment(VersionAttachment va) {
        Long creatorId = va.getCreation().getPrincipal().getId();
        Long modifierId = va.getModification().getPrincipal().getId();
        FileVersionProperties props = new FileVersionProperties(
                va.getId(),
                new HistoryStamp(new LongIdLinkPair(creatorId, LinkUriUtil.getUserLinkUri(creatorId)), va.getCreation().getDate()),
                new HistoryStamp(new LongIdLinkPair(modifierId, LinkUriUtil.getUserLinkUri(modifierId)), va.getModification().getDate()),
                Long.valueOf(va.getFileItem().getLength()),
                Integer.valueOf(va.getVersionNumber()),
                Integer.valueOf(va.getMajorVersion()),
                Integer.valueOf(va.getMinorVersion()),
                va.getFileItem().getDescription().getText(),
                va.getFileStatus()
        );
        LinkUriUtil.populateFileVersionLinks(props);
        return props;
	}

    public static Tag buildTag(org.kablink.teaming.domain.Tag tag) {
        Tag model = new Tag();
        model.setId(tag.getId());
        model.setName(tag.getName());
        model.setPublic(tag.isPublic());
        EntityIdentifier entityIdentifier = tag.getEntityIdentifier();
        model.setEntity(buildEntityId(entityIdentifier.getEntityType(), entityIdentifier.getEntityId()));
        model.setLink(LinkUriUtil.getTagLinkUri(model));
        return model;
    }

    public static EntityId buildEntityId(EntityIdentifier.EntityType type, Long id) {
        return new EntityId(id, type.name(), LinkUriUtil.getDefinableEntityLinkUri(type, id));
    }

    private static void populateDefinableEntity(DefinableEntity model, org.kablink.teaming.domain.DefinableEntity entity, boolean includeAttachments) {
        model.setId(entity.getId());

        if (entity.getParentBinder() != null) {
            Long binderId = entity.getParentBinder().getId();
            model.setParentBinder(new LongIdLinkPair(binderId, LinkUriUtil.getBinderLinkUri(binderId)));
        }

        if(entity.getEntryDefId() != null)
            model.setDefinition(new StringIdLinkPair(entity.getEntryDefId(), LinkUriUtil.getDefinitionLinkUri(entity.getEntryDefId())));

        model.setTitle(entity.getTitle());

        org.kablink.teaming.domain.Description desc = entity.getDescription();
        if(desc != null) {
            model.setDescription(buildDescription(desc));
        }

        if(entity.getCreation() != null) {
            model.setCreation(buildHistoryStamp(entity.getCreation()));
        }
        if(entity.getModification() != null) {
            model.setModification(buildHistoryStamp(entity.getModification()));
        }

        if(entity.getAverageRating() != null) {
            model.setAverageRating(buildAverageRating(entity.getAverageRating()));
        }
        if(entity.getEntityType() != null) {
            model.setEntityType(entity.getEntityType().name());
        }
        org.dom4j.Document def = entity.getEntryDefDoc();
        if(def != null) {
            model.setFamily(DefinitionUtils.getFamily(def));
        }
        model.setIcon(LinkUriUtil.buildIconLinkUri(entity.getIconName()));
        model.setPermaLink(PermaLinkUtil.getPermalink(entity));
        if (includeAttachments) {
            Set<Attachment> attachments = entity.getAttachments();
            List<BaseFileProperties> props = new ArrayList<BaseFileProperties>(attachments.size());
            int i = 0;
            for (Attachment attachment : attachments) {
                if (attachment instanceof FileAttachment) {
                    props.add(ResourceUtil.buildFileProperties((FileAttachment) attachment));
                } else if (attachment instanceof VersionAttachment) {
                    props.add(ResourceUtil.fileVersionFromFileAttachment((VersionAttachment) attachment));
                }
            }
            model.setAttachments(props.toArray(new BaseFileProperties[props.size()]));
        }
    }

    private static void populateEntry(Entry model, org.kablink.teaming.domain.Entry entry, boolean includeAttachments) {
        populateDefinableEntity(model, entry, includeAttachments);
    }

    private static void populatePrincipal(Principal model, org.kablink.teaming.domain.Principal principal, boolean includeAttachments) {
        populateEntry(model, principal, includeAttachments);
        model.setEmailAddress(principal.getEmailAddress());
        model.setDisabled(principal.isDeleted());
        model.setReserved(principal.isReserved());
        model.setName(principal.getName());
        model.setLink(LinkUriUtil.getPrincipalLinkUri(principal));
    }

    private static void populateBinder(Binder model, org.kablink.teaming.domain.Binder binder, boolean includeAttachments) {
        populateDefinableEntity(model, binder, includeAttachments);
        model.setPath(binder.getPathName());
        org.dom4j.Document def = binder.getEntryDefDoc();
        if(def != null) {
            model.setFamily(DefinitionUtils.getFamily(def));
        }
        model.setLink(LinkUriUtil.getBinderLinkUri(binder));
        if (model instanceof Folder) {
            LinkUriUtil.populateFolderLinks(model);
        } else if (model instanceof Workspace) {
            LinkUriUtil.populateWorkspaceLinks(model);
        }
    }

    private static void populateWorkspace(Workspace model, org.kablink.teaming.domain.Workspace workspace, boolean includeAttachments) {
        populateBinder(model, workspace, includeAttachments);
    }

    private static void populateFolder(Folder model, org.kablink.teaming.domain.Folder folder, boolean includeAttachments) {
        populateBinder(model, folder, includeAttachments);
        model.setLibrary(folder.isLibrary());
        model.setMirrored(folder.isMirrored());
    }

    private static void populateBinderBrief(BinderBrief model, org.kablink.teaming.domain.Binder binder) {
        populateDefinableEntityBrief(model, binder);
        model.setIcon(LinkUriUtil.buildIconLinkUri(binder.getIconName()));
        model.setLibrary(binder.isLibrary());
        model.setMirrored(binder.isMirrored());
        model.setPath(binder.getPathName());
        model.setLink(LinkUriUtil.getBinderLinkUri(model));
        if (model.isFolder()) {
            LinkUriUtil.populateFolderLinks(model);
        } else if (model.isWorkspace()) {
            LinkUriUtil.populateWorkspaceLinks(model);
        }
    }

    private static void populateDefinableEntityBrief(DefinableEntityBrief model, org.kablink.teaming.domain.DefinableEntity binder) {
        model.setId(binder.getId());
        model.setTitle(binder.getTitle());
        model.setEntityType(binder.getEntityType().toString());
        org.dom4j.Document def = binder.getEntryDefDoc();
        if(def != null) {
            model.setFamily(DefinitionUtils.getFamily(def));
        }
        //model.setIcon(LinkUriUtil.buildIconLinkUri(binder.getIconName()));
        if(binder.getEntryDefId() != null)
            model.setDefinition(new StringIdLinkPair(binder.getEntryDefId(), LinkUriUtil.getDefinitionLinkUri(binder.getEntryDefId())));
        if(binder.getCreation() != null) {
            model.setCreation(buildHistoryStamp(binder.getCreation()));
        }
        if(binder.getModification() != null) {
            model.setModification(buildHistoryStamp(binder.getModification()));
        }
    }

    private static AverageRating buildAverageRating(org.kablink.teaming.domain.AverageRating rating){
        AverageRating model = new AverageRating();
        model.setAverage(rating.getAverage());
        model.setCount(rating.getCount());
        return model;
    }

    private static HistoryStamp buildHistoryStamp(org.kablink.teaming.domain.HistoryStampBrief historyStamp) {
        Long userId = historyStamp.getPrincipalId();
        return new HistoryStamp(new LongIdLinkPair(userId, LinkUriUtil.getUserLinkUri(userId)), historyStamp.getDate());
    }

    public static HistoryStamp buildHistoryStamp(org.kablink.teaming.domain.HistoryStamp historyStamp) {
        if (historyStamp==null) {
            return null;
        }
        Long userId = historyStamp.getPrincipal().getId();
        return new HistoryStamp(new LongIdLinkPair(userId, LinkUriUtil.getUserLinkUri(userId)), historyStamp.getDate());
    }

    private static Description buildDescription(org.kablink.teaming.domain.Description description){
        Description model = new Description();
        model.setText(description.getText());
        model.setFormat(description.getFormat());
        return model;
    }

    public static DefinitionBrief buildDefinitionBrief(Definition def) {
        DefinitionBrief model = new DefinitionBrief();
        model.setId(def.getId());
        model.setInternalId(def.getInternalId());
        model.setName(def.getName());
        model.setTitle(def.getTitle());
        model.setType(def.getType());
        model.setLink(LinkUriUtil.getDefinitionLinkUri(model.getId()));
        return model;
    }

    public static TemplateBrief buildTemplateBrief(TemplateBinder template) {
        TemplateBrief model = new TemplateBrief();
        populateDefinableEntityBrief(model, template);
        model.setInternalId(template.getInternalId());
        model.setName(template.getName());
        model.setLink(LinkUriUtil.getTemplateLinkUri(model));
        return model;

    }

    public static Operation buildFolderOperation(BinderModule.BinderOperation operation) {
        Operation model = new Operation(operation.name(), LinkUriUtil.getFolderOperationLinkUri(operation));
        model.addAdditionalLink("permissions", model.getLink() + "/permissions");
        return model;
    }

    public static Operation buildFolderOperation(FolderModule.FolderOperation operation) {
        Operation model = new Operation(operation.name(), LinkUriUtil.getFolderOperationLinkUri(operation));
        model.addAdditionalLink("permissions", model.getLink() + "/permissions");
        return model;
    }

    public static Operation buildFolderEntryOperation(FolderModule.FolderOperation operation) {
        Operation model = new Operation(operation.name(), LinkUriUtil.getFolderEntryOperationLinkUri(operation));
        model.addAdditionalLink("permissions", model.getLink() + "/permissions");
        return model;
    }

}
