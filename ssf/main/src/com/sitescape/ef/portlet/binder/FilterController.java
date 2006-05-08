package com.sitescape.ef.portlet.binder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class FilterController extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		User user = RequestContextHolder.getRequestContext().getUser();
			
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
			//Parse the search filter
			Document searchFilter = FilterHelper.getSearchFilter(request);
			if (searchFilter != null) {
				UserProperties userForumProperties = getProfileModule().getUserProperties(user.getId(), binderId);
				Map searchFilters = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
				if (searchFilters == null) searchFilters = new HashMap();
				searchFilters.put(FilterHelper.getFilterName(searchFilter), searchFilter);
				
				//Save the updated search filters
				getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, searchFilters);
			}
			setResponseOnClose(request, response);
		
		} else if (formData.containsKey("deleteBtn")) {
			//This is a request to delete a filter
			String selectedSearchFilter = PortletRequestUtils.getStringParameter(request, "selectedSearchFilter", "");
			if (!selectedSearchFilter.equals("")) {
				UserProperties userForumProperties = getProfileModule().getUserProperties(user.getId(), binderId);
				Map searchFilters = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
				if (searchFilters == null) searchFilters = new HashMap();
				if (searchFilters.containsKey(selectedSearchFilter)) {
					searchFilters.remove(selectedSearchFilter);
					//Save the updated search filters
					getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, searchFilters);
				}
			}
			setResponseOnClose(request, response);
		
		} else if (formData.containsKey("deleteTerm")) {
			//This is a request to delete a term
			//Parse the search filter
			Document searchFilter = FilterHelper.getSearchFilter(request);
			if (searchFilter != null) {
				UserProperties userForumProperties = getProfileModule().getUserProperties(user.getId(), binderId);
				Map searchFilters = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
				if (searchFilters == null) searchFilters = new HashMap();
				searchFilters.put(FilterHelper.getFilterName(searchFilter), searchFilter);
				
				//Save the updated search filters
				getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, searchFilters);
			}
			response.setRenderParameters(formData);
		
		} else if (formData.containsKey("cancelBtn")) {
			//Go back to the "Add filter" page
			response.setRenderParameters(formData);
		
		} else if (formData.containsKey("closeBtn")) {
			setResponseOnClose(request, response);
		
		} else {
			response.setRenderParameters(formData);
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		
		Map model = new HashMap();
		if (!Validator.isNull(request.getParameter("redirect"))) {
			model.put(WebKeys.BINDER_ID, binderId.toString());
			return new ModelAndView(WebKeys.VIEW_LISTING_REDIRECT, model);
		}
		Map formData = request.getParameterMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		Binder binder = getBinderModule().getBinder(binderId);
		
		//Get the name of the selected filter (if one is selected)
		String selectedSearchFilter = PortletRequestUtils.getStringParameter(request, "selectedSearchFilter", "");
		model.put(WebKeys.FILTER_SELECTED_FILTER_NAME, selectedSearchFilter);
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()));
			
		DefinitionUtils.getDefinitions(model);
		DefinitionUtils.getDefinitions(binder, model);
		DefinitionUtils.getDefinitions(Definition.WORKFLOW, WebKeys.PUBLIC_WORKFLOW_DEFINITIONS, model);

		UserProperties userForumProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map searchFilters = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
		model.put(WebKeys.FILTER_SEARCH_FILTERS, searchFilters);
		Map searchFilterData = new HashMap();
		searchFilterData.put("filterTermCount", new Integer(0));
		model.put(WebKeys.FILTER_SEARCH_FILTER_DATA, searchFilterData);

		if (formData.containsKey("addBtn")) {
			return new ModelAndView(WebKeys.VIEW_BUILD_FILTER, model);
		} else if (formData.containsKey("modifyBtn") || formData.containsKey("deleteTerm")) {
			//Build a bean that contains all of the fields to be shown
			if (searchFilters.containsKey(selectedSearchFilter)) {
				Map elementData = getFolderModule().getCommonEntryElements(binderId);
				searchFilterData = FilterHelper.buildFilterFormMap(
						(Document)searchFilters.get(selectedSearchFilter),
						(Map) model.get(WebKeys.PUBLIC_ENTRY_DEFINITIONS),
						elementData);
				model.put(WebKeys.FILTER_SEARCH_FILTER_DATA, searchFilterData);
			}
			return new ModelAndView(WebKeys.VIEW_BUILD_FILTER, model);
		} else {
			return new ModelAndView(WebKeys.VIEW_BUILD_FILTER_SELECT, model);
		}
	}
	protected void setResponseOnClose(ActionRequest request, ActionResponse response) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, request.getParameter(WebKeys.URL_BINDER_ID));
		response.setRenderParameter("redirect", "true");
	}
}
