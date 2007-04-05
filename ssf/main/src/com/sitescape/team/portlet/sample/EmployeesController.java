/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portlet.sample;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.rss.util.UrlUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.DebugHelper;
import com.sitescape.team.web.util.PortletPreferencesUtil;
import com.sitescape.util.Validator;

public class EmployeesController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response)
	throws Exception {
		PortletPreferences prefs = request.getPreferences();
		String ss_initialized = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_INITIALIZED, null);
		if (Validator.isNull(ss_initialized)) {
			prefs.setValue(WebKeys.PORTLET_PREF_INITIALIZED, "true");
			prefs.store();
		}
		response.setRenderParameters(request.getParameterMap());
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
 		Map<String,Object> model = new HashMap<String,Object>();
 		PortletPreferences prefs = request.getPreferences();
		String ss_initialized = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_INITIALIZED, null);
		if (Validator.isNull(ss_initialized)) {
			//Signal that this is the initialization step
			model.put(WebKeys.PORTLET_INITIALIZATION, "1");
			
			PortletURL url;
			url = response.createActionURL();
			model.put(WebKeys.PORTLET_INITIALIZATION_URL, url);
		}

		/*
		AdaptedPortletURL url = new AdaptedPortletURL("jongportlet", false);
		url.setSecure(true);
		url.setParameter("firstparam", "10");
		url.setParameter("secondparam", "Testing");
		System.out.println(url.toString());
		*/
		
    	// Print debug information pertaining to cross context session sharing
		DebugHelper.testRequestEnv("EmployeesController", request);
		
		// Get the list of all employees from the business tier 
		// (via EmployeeModule) and create a ModelAndView datastructure
		// that the Spring's PortalMVC expects.
		model.put("employees", getEmployeeModule().getAllEmployees());
		//models.put("forumUrl", "http://localhost:8080/c/portal/layout?p_l_id=PRI.15.1&p_p_id=ss_forum_WAR_ssf_INSTANCE_d6E7&p_p_action=0&p_p_state=maximized&p_p_mode=view&p_p_col_id=column-2&p_p_col_pos=2&p_p_col_count=9&_ss_forum_WAR_ssf_INSTANCE_d6E7_action=view_folder_listing&_ss_forum_WAR_ssf_INSTANCE_d6E7_binderId=74");
		//models.put("forumUrl", "http://localhost:8080/c/portal/layout?p_l_id=PRI.15.2&p_p_id=ss_forum_WAR_ssf_INSTANCE_d6E7&p_p_action=0&p_p_state=maximized&p_p_mode=view&p_p_col_id=column-2&p_p_col_pos=2&p_p_col_count=9&_ss_forum_WAR_ssf_INSTANCE_d6E7_action=view_folder_listing&_ss_forum_WAR_ssf_INSTANCE_d6E7_binderId=74");
		
		return new ModelAndView("sample/employeesView", model);
	}

}
