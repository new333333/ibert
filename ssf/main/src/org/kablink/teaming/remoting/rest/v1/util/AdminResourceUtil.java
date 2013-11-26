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
package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.rest.v1.model.Access;
import org.kablink.teaming.rest.v1.model.LongIdLinkPair;
import org.kablink.teaming.rest.v1.model.Recipient;
import org.kablink.teaming.rest.v1.model.SharingPermission;
import org.kablink.teaming.rest.v1.model.admin.AssignedRight;
import org.kablink.teaming.rest.v1.model.admin.KeyValuePair;
import org.kablink.teaming.rest.v1.model.admin.LdapHomeDirConfig;
import org.kablink.teaming.rest.v1.model.admin.LdapSearchInfo;
import org.kablink.teaming.rest.v1.model.admin.LdapUserSource;
import org.kablink.teaming.rest.v1.model.admin.NetFolder;
import org.kablink.teaming.rest.v1.model.admin.NetFolderServer;
import org.kablink.teaming.rest.v1.model.admin.Schedule;
import org.kablink.teaming.rest.v1.model.admin.SelectedDays;
import org.kablink.teaming.rest.v1.model.admin.Time;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.util.NetFolderHelper;
import org.kablink.teaming.web.util.NetFolderRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: David
 * Date: 11/18/13
 * Time: 12:43 PM
 */
public class AdminResourceUtil {

    public static NetFolderServer buildNetFolderServer(ResourceDriverConfig config, boolean fullDetails) {
        NetFolderServer model = new NetFolderServer();
        model.setAccountName(config.getAccountName());
        model.setAuthenticationType(config.getAuthenticationType().name());
        model.setChangeDetectionMechanism(config.getChangeDetectionMechanism().name());
        model.setDriverType(config.getDriverType().name());
        model.setFullSyncDirOnly(config.getFullSyncDirOnly());
        model.setId(config.getId());
        model.setModifiedOn(config.getModifiedOn());
        model.setName(config.getName());
        model.setPassword(config.getPassword());
        model.setRootPath(config.getRootPath());
        model.setCachedRightsRefreshInterval(config.getCachedRightsRefreshInterval());
        model.setUseDirectoryRights(config.getUseDirectoryRights());
        if (fullDetails) {
            model.setSyncSchedule(buildSchedule(NetFolderHelper.getNetFolderServerSynchronizationSchedule(model.getId())));
        }
        model.setLink(AdminLinkUriUtil.getNetFolderServerLinkUri(config.getId()));
        model.addAdditionalLink("net_folders", model.getLink() + "/net_folders");
        return model;
    }

    public static NetFolder buildNetFolder(Folder folder, AllModulesInjected ami, boolean fullDetails) {
        NetFolder model = new NetFolder();
        model.setId(folder.getId());
        model.setName(folder.getTitle());
        model.setRelativePath(folder.getResourcePath());

        ResourceDriverConfig driverConfig = NetFolderHelper.findNetFolderRootByName(ami.getAdminModule(), ami.getResourceDriverModule(), folder.getResourceDriverName());
        if (driverConfig!=null) {
            model.setServer(new LongIdLinkPair(driverConfig.getId(), AdminLinkUriUtil.getNetFolderServerLinkUri(driverConfig.getId())));
        }
        model.setHomeDir(folder.isHomeDir());

        model.setIndexContent(folder.getIndexContent());

        model.setJitsEnabled(folder.isJitsEnabled());
        model.setJitsMaxACLAge(folder.getJitsMaxAge());
        model.setJitsMaxAge(folder.getJitsAclMaxAge());

        model.setInheritSyncSchedule(folder.getSyncScheduleOption() != Binder.SyncScheduleOption.useNetFolderSchedule);
        if (fullDetails) {
            ScheduleInfo scheduleInfo = NetFolderHelper.getMirroredFolderSynchronizationSchedule( folder.getId() );
            model.setSyncSchedule(buildSchedule(scheduleInfo));

            model.setAssignedRights(buildAssignedRights(NetFolderHelper.getNetFolderRights(ami, folder)));
        }

        model.setAllowDesktopSync(folder.getAllowDesktopAppToSyncData());
        model.setFullSyncDirOnly(folder.getFullSyncDirOnly());

        model.setLink(AdminLinkUriUtil.getNetFolderLinkUri(folder.getId()));

        return model;
    }

    public static LdapUserSource buildUserSource(LdapConnectionConfig ldapConfig,
                                                 ResourceDriverModule resourceDriverModule) {
        LdapUserSource model = new LdapUserSource();
        model.setId(ldapConfig.getId());
        model.setUrl(ldapConfig.getUrl());
        model.setPrincipal(ldapConfig.getPrincipal());
        model.setCredentials(ldapConfig.getCredentials());
        model.setGuidAttribute(ldapConfig.getLdapGuidAttribute());
        model.setUsernameAttribute(ldapConfig.getUserIdAttribute());
        model.setMappings(buildKeyValueList(ldapConfig.getMappings()));
        model.setUserSearches(buildSearchInfoList(ldapConfig.getUserSearches(), resourceDriverModule));
        model.setGroupSearches(buildSearchInfoList(ldapConfig.getGroupSearches(), null));
        model.setLink(AdminLinkUriUtil.getUserSourceLinkUri(model.getId()));
        return model;
    }

    private static List<LdapSearchInfo> buildSearchInfoList(List<LdapConnectionConfig.SearchInfo> searchInfos,
                                                            ResourceDriverModule resourceDriverModule) {
        List<LdapSearchInfo> model = new ArrayList<LdapSearchInfo>();
        for (LdapConnectionConfig.SearchInfo searchInfo : searchInfos) {
            model.add(buildSearchInfo(searchInfo, resourceDriverModule));
        }
        return model;
    }

    private static LdapSearchInfo buildSearchInfo(LdapConnectionConfig.SearchInfo searchInfo,
                                                  ResourceDriverModule resourceDriverModule) {
        LdapSearchInfo model = null;
        if (searchInfo!=null) {
            model = new LdapSearchInfo();
            model.setBaseDn(searchInfo.getBaseDn());
            model.setFilter(searchInfo.getFilter());
            model.setSearchSubtree(searchInfo.isSearchSubtree());
            model.setHomeDirConfig(buildHomeDirConfig(searchInfo.getHomeDirConfig(), resourceDriverModule));
        }
        return model;
    }

    private static LdapHomeDirConfig buildHomeDirConfig(LdapConnectionConfig.HomeDirConfig homeDirConfig,
                                                        ResourceDriverModule resourceDriverModule) {
        LdapHomeDirConfig model = null;
        if (homeDirConfig!=null) {
            model = new LdapHomeDirConfig();
            LdapConnectionConfig.HomeDirCreationOption creationOption = homeDirConfig.getCreationOption();
            if (creationOption == LdapConnectionConfig.HomeDirCreationOption.USE_CUSTOM_CONFIG) {
                String serverName = homeDirConfig.getNetFolderServerName();
                if (serverName != null && resourceDriverModule!=null) {
                    ResourceDriverConfig netFolderServer = NetFolderHelper.findNetFolderRootByName(null, resourceDriverModule, serverName);
                    if (netFolderServer!=null) {
                        model.setOption(LdapHomeDirConfig.TYPE_CUSTOM_NET_FOLDER);
                        model.setNetFolderServer(new LongIdLinkPair(netFolderServer.getId(), AdminLinkUriUtil.getNetFolderServerLinkUri(netFolderServer.getId())));
                        model.setPath(homeDirConfig.getPath());
                    }
                }
            } else if (creationOption == LdapConnectionConfig.HomeDirCreationOption.USE_HOME_DIRECTORY_ATTRIBUTE) {
                model.setOption(LdapHomeDirConfig.TYPE_HOME_DIR_ATTRIBUTE);
            } else if (creationOption == LdapConnectionConfig.HomeDirCreationOption.USE_CUSTOM_ATTRIBUTE) {
                model.setOption(LdapHomeDirConfig.TYPE_CUSTOM_ATTRIBUTE);
                model.setLdapAttribute(homeDirConfig.getAttributeName());
            }
            if (model.getOption()==null) {
                model.setOption(LdapHomeDirConfig.TYPE_NONE);
            }
        }
        return model;
    }

    private static List<KeyValuePair> buildKeyValueList(Map<String, String> mappings) {
        List<KeyValuePair> model = new ArrayList<KeyValuePair>();
        for (Map.Entry<String, String> entry : mappings.entrySet()) {
            model.add(new KeyValuePair(entry.getKey(), entry.getValue()));
        }
        return model;
    }

    private static Schedule buildSchedule(ScheduleInfo scheduleInfo) {
        Schedule model = null;
        if (scheduleInfo!=null) {
            model = new Schedule();
            model.setEnabled(scheduleInfo.isEnabled());
            org.kablink.teaming.jobs.Schedule sch = scheduleInfo.getSchedule();
            if (sch.isDaily()) {
                model.setDayFrequency(Schedule.DayFrequency.daily.name());
            } else {
                model.setDayFrequency(Schedule.DayFrequency.selected_days.name());
                SelectedDays days = new SelectedDays();
                days.setSun(sch.isOnSunday());
                days.setMon(sch.isOnMonday());
                days.setTue(sch.isOnTuesday());
                days.setWed(sch.isOnWednesday());
                days.setThu(sch.isOnThursday());
                days.setFri(sch.isOnFriday());
                days.setSat(sch.isOnSaturday());
                model.setSelectedDays(days);
            }

            if ( sch.isRepeatMinutes() ) {
                Time every = new Time();
                every.setHour(0);
                every.setMinute(Integer.valueOf(sch.getMinutesRepeat()));
                model.setEvery(every);
            } else if ( sch.isRepeatHours() ) {
                Time every = new Time();
                every.setHour(Integer.valueOf( sch.getHoursRepeat() ));
                every.setMinute(0);
                model.setEvery(every);
            } else {
                Time at = new Time();
                at.setHour(Integer.valueOf( sch.getHours() ));
                at.setMinute(Integer.valueOf(sch.getMinutes()));
                model.setAt(at);
            }
        }
        return model;
    }

    public static List<AssignedRight> buildAssignedRights(List<NetFolderRole> roles) {
        List<AssignedRight> model = new ArrayList<AssignedRight>();
        for (NetFolderRole role : roles) {
            AssignedRight right = buildAssignedRight(role);
            if (right!=null) {
                model.add(right);
            }
        }
        return model;
    }
    public static AssignedRight buildAssignedRight(NetFolderRole role) {
        AssignedRight model = new AssignedRight();
        Recipient recipient = new Recipient();
        Principal principal = role.getPrincipal();
        if (principal instanceof User) {
            recipient.setType(Recipient.RecipientType.user.name());
            recipient.setId(principal.getId());
            recipient.setLink(LinkUriUtil.getUserLinkUri(principal.getId()));
        } else if (principal instanceof Group) {
            recipient.setType(Recipient.RecipientType.group.name());
            recipient.setId(principal.getId());
            recipient.setLink(LinkUriUtil.getGroupLinkUri(principal.getId()));
        } else {
            return null;
        }
        model.setPrincipal(recipient);

        Set<NetFolderRole.RoleType> roles = role.getRoles();
        Access access = new Access();
        if (roles.contains(NetFolderRole.RoleType.AllowAccess)) {
            access.setRole(Access.RoleType.ACCESS.name());
            SharingPermission permission = new SharingPermission();
            permission.setInternal(roles.contains(NetFolderRole.RoleType.ShareInternal));
            permission.setExternal(roles.contains(NetFolderRole.RoleType.ShareExternal));
            permission.setPublic(roles.contains(NetFolderRole.RoleType.SharePublic));
            permission.setGrantReshare(roles.contains(NetFolderRole.RoleType.ShareForward));
            access.setSharing(permission);
        } else {
            access.setRole(Access.RoleType.NONE.name());
        }
        model.setAccess(access);
        return model;
    }
}