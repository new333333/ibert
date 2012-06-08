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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.portlet.administration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;


@SuppressWarnings({"unchecked","unused"})
public class ManageResourceDriverController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		getAdminModule().checkAccess(AdminOperation.manageResourceDrivers);
		if ((formData.containsKey("okBtn") || formData.containsKey("addBtn") || 
				formData.containsKey("deleteBtn")) && WebHelper.isMethodPost(request)) {
			if (getAdminModule().testAccess(AdminOperation.manageResourceDrivers)) {
				if (formData.containsKey("deleteBtn")) {
				
				} else if (formData.containsKey("addBtn")) {
					String name = PortletRequestUtils.getStringParameter(request, "driverName");
					String type = PortletRequestUtils.getStringParameter(request, "driverType");
					Set<Long> groupIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addedGroups"));
					Set<Long> userIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addedUsers"));
					
					//Add this resource driver
				
				} else {
					//See if a selected driver needs to be modified
				}

			}

		} else {
			response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Map formData = request.getParameterMap();
		getAdminModule().checkAccess(AdminOperation.manageFunction);

		if (formData.containsKey("okBtn")) {
			//return new ModelAndView("forum/close_window", model);
		}

		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_RESOURCE_DRIVERS, model);

	}

}
