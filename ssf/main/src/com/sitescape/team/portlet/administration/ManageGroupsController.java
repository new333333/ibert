package com.sitescape.team.portlet.administration;

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

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.FindIdsHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;
public class ManageGroupsController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		if (formData.containsKey("addBtn")) {
			Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			//make sure it is present
			String name = PortletRequestUtils.getRequiredStringParameter(request, "name");
			MapInputData inputData = new MapInputData(formData);
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			getProfileModule().addGroup(binderId, null, inputData, fileMap);
			
		} else if (formData.containsKey("applyBtn") || formData.containsKey("okBtn")) {
			Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			Long groupId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
			String title = PortletRequestUtils.getStringParameter(request, "title", "");
			String description = PortletRequestUtils.getStringParameter(request, "description", "");
			Set ids = FindIdsHelper.getIdsAsLongSet(request.getParameterValues("users"));
			ids.addAll(FindIdsHelper.getIdsAsLongSet(request.getParameterValues("groups")));
			List principals = getProfileModule().getPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId());
			Map updates = new HashMap();
			updates.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
			updates.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, description);
			updates.put(ObjectKeys.FIELD_GROUP_MEMBERS, principals);
			getProfileModule().modifyEntry(binderId, groupId, new MapInputData(updates));
		
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			response.setRenderParameter("redirect", "true");
		}
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
		
		Long groupId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		if (groupId != null) {
			String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
			model.put(WebKeys.NAMESPACE, namespace);
			model.put(WebKeys.BINDER_ID, binder.getId());
			Group group = (Group)getProfileModule().getEntry(binder.getId(), groupId);		
			model.put(WebKeys.GROUP, group);
			List memberList = group.getMembers();
			Set ids = new HashSet();
			Iterator itUsers = memberList.iterator();
			while (itUsers.hasNext()) {
				Principal member = (Principal) itUsers.next();
				ids.add(member.getId());
			}
			model.put(WebKeys.USERS, getProfileModule().getUsers(ids));
			model.put(WebKeys.GROUPS, getProfileModule().getGroups(ids));
		}

		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_GROUPS, model);
	}

}
