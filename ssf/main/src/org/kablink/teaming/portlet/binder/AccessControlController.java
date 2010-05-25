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
package org.kablink.teaming.portlet.binder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WorkAreaHelper;
import org.springframework.web.portlet.ModelAndView;


/**
 * This controller/jsp is used by administration/ConfigureAccessController
 * Keep in sync
 * @author Peter Hurley
 *
 */
public class AccessControlController extends AbstractBinderController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		//navigation links still use binderId
		Long workAreaId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		if (workAreaId == null) workAreaId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_WORKAREA_ID));				
		WorkArea workArea = null;
		request.setAttribute("roleId", "");
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_WORKAREA_TYPE, "");	
		if (EntityIdentifier.EntityType.zone.name().equals(type)) {
			workArea = getZoneModule().getZoneConfig(workAreaId);
		} else if (EntityIdentifier.EntityType.folderEntry.name().equals(type)) {
			FolderEntry entry = getFolderModule().getEntry(null, workAreaId);
			workArea = entry;
		} else {
			workArea = getBinderModule().getBinder(workAreaId);
		}
		response.setRenderParameter(WebKeys.URL_WORKAREA_ID, workArea.getWorkAreaId().toString());
		response.setRenderParameter(WebKeys.URL_WORKAREA_TYPE, workArea.getWorkAreaType());
		//See if the form was submitted
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			if (!(workArea instanceof FolderEntry) || ((FolderEntry)workArea).isTop()) {
				SimpleProfiler.setProfiler(new SimpleProfiler("lucene"));
				Map functionMemberships = new HashMap();
				getAccessResults(request, functionMemberships);
				if (workArea instanceof Entry) {
					Boolean includeFolderAcl = PortletRequestUtils.getBooleanParameter(request, "includeFolderAcl", false);
					getAdminModule().setEntryHasAcl(workArea, Boolean.TRUE, includeFolderAcl);
				}
				getAdminModule().setWorkAreaFunctionMemberships(workArea, functionMemberships);
				if(logger.isDebugEnabled())
					logger.debug(SimpleProfiler.toStr());
				SimpleProfiler.clearProfiler();
				if (EntityIdentifier.EntityType.zone.name().equals(type)) {
					getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_UPGRADE_ACCESS_CONTROLS, "true");
				}
			}
		} else if (formData.containsKey("delBtn") && WebHelper.isMethodPost(request)) {
			if (!(workArea instanceof FolderEntry) || ((FolderEntry)workArea).isTop()) {
				if (workArea instanceof Entry) {
					getAdminModule().setEntryHasAcl(workArea, Boolean.FALSE, Boolean.TRUE);
					SimpleProfiler.setProfiler(new SimpleProfiler("lucene"));
					Map functionMemberships = new HashMap();
					getAdminModule().setWorkAreaFunctionMemberships(workArea, functionMemberships);
					if(logger.isDebugEnabled())
						logger.debug(SimpleProfiler.toStr());
					SimpleProfiler.clearProfiler();
				}
			}
			setupCloseWindow(response);
		} else if (formData.containsKey("inheritanceBtn") && WebHelper.isMethodPost(request)) {
			boolean inherit = PortletRequestUtils.getBooleanParameter(request, "inherit", false);
			getAdminModule().setWorkAreaFunctionMembershipInherited(workArea,inherit);			
		
		} else if (formData.containsKey("aclSelectionBtn") && WebHelper.isMethodPost(request)) {
			if ((workArea instanceof FolderEntry) && ((FolderEntry)workArea).isTop()) {
				String aclType = PortletRequestUtils.getStringParameter(request, "aclSelection", "entry");	
				if (aclType.equals("folder")) {
					getAdminModule().setEntryHasAcl(workArea, Boolean.FALSE, Boolean.TRUE);
					SimpleProfiler.setProfiler(new SimpleProfiler("lucene"));
					Map functionMemberships = new HashMap();
					getAdminModule().setWorkAreaFunctionMemberships(workArea, functionMemberships);
					if(logger.isDebugEnabled())
						logger.debug(SimpleProfiler.toStr());
					SimpleProfiler.clearProfiler();
				} else if (aclType.equals("entry")) {
					//Set the entry acl
					Boolean includeFolderAcl = PortletRequestUtils.getBooleanParameter(request, "includeFolderAcl", true);
					SimpleProfiler.setProfiler(new SimpleProfiler("lucene"));
					Map functionMemberships = new HashMap();
					getAccessResults(request, functionMemberships);
					getAdminModule().setEntryHasAcl(workArea, Boolean.TRUE, includeFolderAcl);
					getAdminModule().setWorkAreaFunctionMemberships(workArea, functionMemberships);
					if(logger.isDebugEnabled())
						logger.debug(SimpleProfiler.toStr());
					SimpleProfiler.clearProfiler();
				}
			}
		
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			if (workArea instanceof TemplateBinder) {
				response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				response.setRenderParameter(WebKeys.URL_BINDER_ID, workAreaId.toString());
			} else {
				setupCloseWindow(response);
			}
			
		} else {
			response.setRenderParameters(request.getParameterMap());
		}
	}
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		//navigation links still use binderId
		Long workAreaId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		if (workAreaId == null) workAreaId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_WORKAREA_ID));				
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_WORKAREA_TYPE);	
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");	
		WorkArea wArea=null;
		Map model = new HashMap();
		if (EntityIdentifier.EntityType.zone.name().equals(type)) {
			ZoneConfig zone = getZoneModule().getZoneConfig(workAreaId);
			model.put(WebKeys.ACCESS_SUPER_USER, AccessUtils.getZoneSuperUser(zone.getZoneId()));
			wArea = zone;
			model.put(WebKeys.ACCESS_CONTROL_CONFIGURE_ALLOWED, 
					getAdminModule().testAccess(zone, AdminOperation.manageFunctionMembership));
		} else if (EntityIdentifier.EntityType.folderEntry.name().equals(type)) {
			FolderEntry entry = getFolderModule().getEntry(null, workAreaId);
			model.put(WebKeys.ENTRY_HAS_ENTRY_ACL, entry.hasEntryAcl());
			wArea = entry;
			model.put(WebKeys.ACCESS_SUPER_USER, AccessUtils.getZoneSuperUser(entry.getZoneId()));
			Boolean configureAccess = false;
			if (entry.hasEntryAcl()) {
				configureAccess = getAdminModule().testAccess(entry, AdminOperation.manageFunctionMembership);
				if (!configureAccess && entry.isIncludeFolderAcl()) {
					configureAccess = getFolderModule().testAccess(entry.getParentFolder(), FolderOperation.setEntryAcl);
				}
				if (!configureAccess && entry.isIncludeFolderAcl() && entry.getOwnerId().equals(user.getId())) {
					configureAccess = getFolderModule().testAccess(entry.getParentFolder(), FolderOperation.entryOwnerSetAcl);
				}
			} else {
				if (!configureAccess) {
					configureAccess = getAdminModule().testAccess(entry.getParentFolder(), AdminOperation.manageFunctionMembership);
				}
				if (!configureAccess) {
					configureAccess = getFolderModule().testAccess(entry.getParentFolder(), FolderOperation.setEntryAcl);
				}
				if (!configureAccess && entry.getOwnerId().equals(user.getId())) {
					configureAccess = getFolderModule().testAccess(entry.getParentFolder(), FolderOperation.entryOwnerSetAcl);
				}
			}
			model.put(WebKeys.ACCESS_CONTROL_CONFIGURE_ALLOWED, configureAccess);
			
		} else {
			Binder binder = getBinderModule().getBinder(workAreaId);			
			//Build the navigation beans
			BinderHelper.buildNavigationLinkBeans(this, binder, model);
			wArea = binder;
			model.put(WebKeys.BINDER, binder);
			model.put(WebKeys.DEFINITION_ENTRY, binder);
			model.put(WebKeys.ACCESS_SUPER_USER, AccessUtils.getZoneSuperUser(binder.getZoneId()));
			model.put(WebKeys.ACCESS_CONTROL_CONFIGURE_ALLOWED, 
					getAdminModule().testAccess(binder, AdminOperation.manageFunctionMembership));
		}
		
		setupAccess(this, request, response, wArea, model);
		model.put(WebKeys.ACCESS_ALL_USERS_GROUP, Utils.getAllUsersGroupId());
		model.put(WebKeys.ACCESS_WORKAREA_IS_PERSONAL, Utils.isWorkareaInProfilesTree(wArea));		

		if (ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			//Cannot do these things as guest
			model.put(WebKeys.ERROR_MESSAGE, NLT.get("errorcode.access.denied"));
			return new ModelAndView(WebKeys.VIEW_ERROR_RETURN, model);
		}
		if (operation.equals(WebKeys.OPERATION_VIEW_ACCESS)) {
			return new ModelAndView(WebKeys.VIEW_ACCESS_TO_BINDER, model);
		} else {
			if (wArea instanceof Entry) {
				return new ModelAndView(WebKeys.VIEW_ACCESS_CONTROL_ENTRY, model);
			} else {
				return new ModelAndView(WebKeys.VIEW_ACCESS_CONTROL, model);
			}
		}
	}
	//shared with binder config 
	public static void getAccessResults(ActionRequest request, Map functionMemberships) {
		Map formData = request.getParameterMap();

		if (formData.containsKey("roleIds")) {
			String[] roleIds = (String[]) formData.get("roleIds");
			for (int i = 0; i < roleIds.length; i++) {
				if (!roleIds[i].equals("")) {
					Long roleId = Long.valueOf(roleIds[i]);
					if (!functionMemberships.containsKey(roleId)) {
						functionMemberships.put(roleId, new HashSet());
					}
				}
			}
		}
		//Look for role settings (e.g., role_id..._...)
		Iterator itFormData = formData.entrySet().iterator();
		while (itFormData.hasNext()) {
			Map.Entry me = (Map.Entry)itFormData.next();
			String key = (String)me.getKey();
			if (key.length() >= 8 && key.substring(0,7).equals("role_id")) {
				String[] s_roleId = key.substring(7).split("_");
				if (s_roleId.length == 2) {
					Long roleId = Long.valueOf(s_roleId[0]);
					Long memberId = null;
					if (s_roleId[1].equals("owner")) {
						memberId = ObjectKeys.OWNER_USER_ID;
					} else if (s_roleId[1].equals("teamMember")) {
						memberId = ObjectKeys.TEAM_MEMBER_ID;
					} else {
						memberId = Long.valueOf(s_roleId[1]);
					}
						Set members = (Set)functionMemberships.get(roleId);
					if (!members.contains(memberId)) members.add(memberId);
				}
			}
		}

	}
	//used by ajax controller
	public static void setupAccess(AllModulesInjected bs, RenderRequest request, RenderResponse response, WorkArea wArea, Map model) {
		String scope = ObjectKeys.ROLE_TYPE_BINDER;
		if (wArea instanceof Entry) scope = ObjectKeys.ROLE_TYPE_ENTRY;
		if (wArea instanceof ZoneConfig) scope = ObjectKeys.ROLE_TYPE_ZONE;
		List functions = bs.getAdminModule().getFunctions(scope);
		List membership;
		boolean zoneWide = wArea.getWorkAreaType().equals(EntityIdentifier.EntityType.zone.name());
		if (wArea.isFunctionMembershipInherited()) {
			membership = bs.getAdminModule().getWorkAreaFunctionMembershipsInherited(wArea);
		} else {
			membership = bs.getAdminModule().getWorkAreaFunctionMemberships(wArea);
		}
		WorkAreaHelper.buildAccessControlTableBeans(bs, request, response, wArea, functions, 
				membership, model, false);

		if (!wArea.isFunctionMembershipInherited()) {
			WorkArea parentArea = wArea.getParentWorkArea();
			if (parentArea != null) {
				List parentMembership;
				if (parentArea.isFunctionMembershipInherited()) {
					parentMembership = bs.getAdminModule().getWorkAreaFunctionMembershipsInherited(parentArea);
				} else {
					parentMembership = bs.getAdminModule().getWorkAreaFunctionMemberships(parentArea);
				}
				Map modelParent = new HashMap();
				WorkAreaHelper.buildAccessControlTableBeans(bs, request, response, parentArea, 
						functions, parentMembership, modelParent, true);
				model.put(WebKeys.ACCESS_PARENT, modelParent);
				WorkAreaHelper.mergeAccessControlTableBeans(model);
			}
		}
		
		//Set up the role beans
		WorkAreaHelper.buildAccessControlRoleBeans(bs, model, zoneWide);
	}

}
