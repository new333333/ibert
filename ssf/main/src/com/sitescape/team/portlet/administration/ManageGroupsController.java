package com.sitescape.team.portlet.administration;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;
public class ManageGroupsController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("addBtn")) {
			Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			String groupName = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_GROUP_NAME);
		
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			response.setRenderParameter("redirect", "true");
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		Binder binder = getProfileModule().getProfileBinder();
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);

		Map searchResults = getProfileModule().getGroups(binder.getId(), options);
		List groups = (List) searchResults.get(ObjectKeys.SEARCH_ENTRIES);
		Map model = new HashMap();
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.GROUP_LIST, groups);
		
		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_GROUPS, model);
	}

}
