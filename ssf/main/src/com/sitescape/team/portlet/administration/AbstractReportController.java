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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.portlet.binder.AbstractBinderController;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.SearchTreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DateHelper;
import com.sitescape.team.web.util.PortletRequestUtils;

public abstract class AbstractReportController extends  AbstractBinderController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId());
		String binderType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_TYPE, null);	
		if (binderType != null && formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			setupViewBinder(response, binderId, binderType);
		}
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {

		Map formData = request.getParameterMap();

		if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		
		Map model = new HashMap();
		populateModel(request, model);
		return new ModelAndView(chooseView(formData), model);
	}
	
	protected void populateModel(RenderRequest request, Map model)
	{
		Date startDate = null;
		Date endDate = null;
		try {
			MapInputData data = new MapInputData(request.getParameterMap());
			startDate = data.getDateValue(WebKeys.URL_START_DATE);
			endDate = data.getDateValue(WebKeys.URL_END_DATE);
		} catch(Exception e) {
		}
		if(startDate == null) { startDate = new Date(); }
		if(endDate == null) { endDate = new Date(); }
		model.put(WebKeys.REPORT_START_DATE, startDate);
		model.put(WebKeys.REPORT_END_DATE, endDate);
	}
	
	abstract protected String chooseView(Map formData);
}
