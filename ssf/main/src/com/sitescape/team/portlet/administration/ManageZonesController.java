/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.portlet.administration;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
public class ManageZonesController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		//response.setRenderParameters(formData);
		if (formData.containsKey("addBtn")) {
			String zoneName = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_ZONE_NAME);
			String virtualHost = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_VIRTUAL_HOST);
			getZoneModule().addZoneUnderPortal(zoneName, virtualHost, null);
		} else if (formData.containsKey("deleteBtn")) {
			String zoneName = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_ZONE_NAME);
			getZoneModule().deleteZoneUnderPortal(zoneName);
		} else if (formData.containsKey("modifyBtn")) {
			String zoneName = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_ZONE_NAME);
			String virtualHost = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_VIRTUAL_HOST);
			getZoneModule().modifyZoneUnderPortal(zoneName, virtualHost);
		} else {
			response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_ZONES, WebKeys.ZONE_INFO_LIST, getZoneModule().getZoneInfos());
	}

}
