package com.sitescape.team.portlet.forum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Dashboard;
import com.sitescape.team.domain.DashboardPortlet;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.shared.DomTreeBuilder;
import com.sitescape.team.module.shared.DomTreeHelper;
import com.sitescape.team.module.shared.WsDomTreeBuilder;
import com.sitescape.team.util.AllBusinessServicesInjected;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.team.web.util.FilterHelper;
import com.sitescape.team.web.util.FindIdsHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class EditController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response)
	throws Exception {

        //Make the prefs available to the jsp
		Map formData = request.getParameterMap();
		PortletPreferences prefs= request.getPreferences();
		String title = PortletRequestUtils.getStringParameter(request, "title", null);
		if (title != null) prefs.setValue(WebKeys.PORTLET_PREF_TITLE, title); 
		//see if type is being set
		if (formData.containsKey("applyBtn") || 
				formData.containsKey("okBtn")) {
			String displayType = prefs.getValue(WebKeys.PORTLET_PREF_TYPE, "");
			//	if not on form, must already be set.  
			if (Validator.isNull(displayType)) { 
				displayType = getDisplayType(request);
				prefs.setValue(WebKeys.PORTLET_PREF_TYPE, displayType);
			}
			if (ViewController.FORUM_PORTLET.equals(displayType)) {
				List forumPrefIdList = new ArrayList();
				//	Get the forums to be displayed
				Iterator itFormData = formData.entrySet().iterator();
				while (itFormData.hasNext()) {
					Map.Entry me = (Map.Entry) itFormData.next();
					if (((String)me.getKey()).startsWith("id_")) {
						String forumId = ((String)me.getKey()).substring(3);
						forumPrefIdList.add(forumId);
					}
				}
				if (forumPrefIdList.size() > 0) {
					prefs.setValues(WebKeys.FORUM_PREF_FORUM_ID_LIST, (String[]) forumPrefIdList.toArray(new String[forumPrefIdList.size()]));
				}

			} else if (ViewController.BLOG_SUMMARY_PORTLET.equals(displayType) ||
					ViewController.GUESTBOOK_SUMMARY_PORTLET.equals(displayType) ||
					ViewController.WIKI_PORTLET.equals(displayType) ||
					ViewController.SEARCH_PORTLET.equals(displayType)) {
				String id = prefs.getValue(WebKeys.PORTLET_PREF_DASHBOARD, null);
				DashboardPortlet d=null;
				if (id != null) {
					try {
						d = (DashboardPortlet)getDashboardModule().getDashboard(id);
					} catch (NoObjectByTheIdException no) {}
				}
				if (d == null) {
					PortletConfig pConfig = (PortletConfig)request.getAttribute(WebKeys.JAVAX_PORTLET_CONFIG);
					d = getDashboardModule().createDashboardPortlet( pConfig.getPortletName(), DashboardHelper.getNewDashboardMap());
					DashboardHelper.addComponent(request, d, DashboardHelper.Wide_Top, DashboardHelper.Portlet);
					prefs.setValue(WebKeys.PORTLET_PREF_DASHBOARD, d.getId());
					prefs.setValue(WebKeys.PORTLET_PREF_TYPE, displayType);
				}
				DashboardHelper.saveComponentData(request, d, DashboardHelper.PORTLET_COMPONENT_ID);

			} else if (ViewController.PRESENCE_PORTLET.equals(displayType)) {
				prefs.setValue(WebKeys.PRESENCE_PREF_USER_LIST, FindIdsHelper.getIdsAsString(request.getParameterValues("users")));
				prefs.setValue(WebKeys.PRESENCE_PREF_GROUP_LIST, FindIdsHelper.getIdsAsString(request.getParameterValues("groups"))); 			
			} else if (ViewController.WORKSPACE_PORTLET.equals(displayType)) {
				String id = PortletRequestUtils.getStringParameter(request, "topWorkspace"); 
				if (Validator.isNotNull(id)) {
					prefs.setValue(WebKeys.WORKSPACE_PREF_ID, id);
				}
			}
		}
		prefs.store();
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {


        //Make the prefs available to the jsp
		PortletPreferences prefs= request.getPreferences();
        Map model = new HashMap();
		String title = (String)prefs.getValue(WebKeys.PORTLET_PREF_TITLE, null);
		if (title != null) response.setTitle(title);
		else title="";
		model.put("portletTitle", prefs.getValue(WebKeys.PORTLET_PREF_TITLE, ""));
		String displayType = prefs.getValue(WebKeys.PORTLET_PREF_TYPE, "");
		if (Validator.isNull(displayType)) {
			displayType = getDisplayType(request);
			
		}
		if (ViewController.FORUM_PORTLET.equals(displayType)) {		
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(RequestContextHolder.getRequestContext().getZoneId(), 
					new WsDomTreeBuilder(null, true, this, new folderTree()),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);		
		
			String[] forumPrefIdList = prefs.getValues(WebKeys.FORUM_PREF_FORUM_ID_LIST, new String[0]);
		
			//	Build the jsp bean (sorted by folder title)
			List folderIds = new ArrayList();
			for (int i = 0; i < forumPrefIdList.length; i++) {
				folderIds.add(Long.valueOf(forumPrefIdList[i]));
			}
			Collection folders = getFolderModule().getFolders(folderIds);
		
			model.put(WebKeys.FOLDER_LIST, folders);
			model.put(WebKeys.BINDER_ID_LIST, folderIds);
			return new ModelAndView(WebKeys.VIEW_FORUM_EDIT, model);
		} else if (ViewController.BLOG_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_BLOG_EDIT);
		} else if (ViewController.GUESTBOOK_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_GUESTBOOK_EDIT);			
		} else if (ViewController.WIKI_PORTLET.equals(displayType)) {
			return setupSummaryPortlet(request, prefs, model, WebKeys.VIEW_WIKI_EDIT);
		} else if (ViewController.SEARCH_PORTLET.equals(displayType)) {
			String id = prefs.getValue(WebKeys.PORTLET_PREF_DASHBOARD, null);
			if (id != null) {
				try {
					DashboardPortlet d = (DashboardPortlet)getDashboardModule().getDashboard(id);
					Map userProperties = (Map) getProfileModule().getUserProperties(RequestContextHolder.getRequestContext().getUserId()).getProperties();
					model.put(WebKeys.USER_PROPERTIES, userProperties);
					DashboardHelper.getDashboardMap(d, userProperties, model);
					//make sure it works, before setting up model
					model.put(WebKeys.DASHBOARD_PORTLET, d);
				} catch (Exception no) {}
			}
			return new ModelAndView(WebKeys.VIEW_SEARCH_EDIT, model);
		} else if (ViewController.PRESENCE_PORTLET.equals(displayType)) {
			//This is the portlet view; get the configured list of principals to show
			Set<Long> userIds = new HashSet<Long>();
			Set<Long> groupIds = new HashSet<Long>();
			userIds.addAll(FindIdsHelper.getIdsAsLongSet(request.getPreferences().getValue(WebKeys.PRESENCE_PREF_USER_LIST, "")));
			groupIds.addAll(FindIdsHelper.getIdsAsLongSet(request.getPreferences().getValue(WebKeys.PRESENCE_PREF_GROUP_LIST, "")));

			model.put(WebKeys.USERS, getProfileModule().getUsers(userIds));
			model.put(WebKeys.GROUPS, getProfileModule().getGroups(groupIds));			
			return new ModelAndView(WebKeys.VIEW_PRESENCE_EDIT, model);
		} else if (ViewController.WORKSPACE_PORTLET.equals(displayType)) {
				
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(RequestContextHolder.getRequestContext().getZoneId(), 
					new WsDomTreeBuilder(null, true, this, new wsTree()),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);		
			
			String wsId = prefs.getValue(WebKeys.WORKSPACE_PREF_ID, null);
			try {
				Workspace ws;
				if (Validator.isNull(wsId)) ws = getWorkspaceModule().getWorkspace();	
				else ws = getWorkspaceModule().getWorkspace(Long.valueOf(wsId));				
				model.put(WebKeys.BINDER, ws);
			} catch (Exception ex) {};
			return new ModelAndView(WebKeys.VIEW_WORKSPACE_EDIT, model);
		}
		return null;
	}
	private ModelAndView setupSummaryPortlet(RenderRequest request, PortletPreferences prefs, Map model, String view) {
		Document wsTree = getWorkspaceModule().getDomWorkspaceTree(RequestContextHolder.getRequestContext().getZoneId(), 
				new WsDomTreeBuilder(null, true, this, new folderTree()),1);
		model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
		model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);		
		String id = prefs.getValue(WebKeys.PORTLET_PREF_DASHBOARD, null);
		if (id != null) {
			try {
				DashboardPortlet d = (DashboardPortlet)getDashboardModule().getDashboard(id);
				Map dataMap = DashboardHelper.getComponentData(d);
				if (dataMap != null) {
					List savedFolderIds = (List)dataMap.get(DashboardHelper.SearchFormSavedFolderIdList);
					//	Build the jsp bean (sorted by folder title)
					Long folderId;
					if (savedFolderIds != null && savedFolderIds.size() > 0) {
						for (int i = 0; i < savedFolderIds.size(); i++) {
							folderId = Long.valueOf((String)savedFolderIds.get(i));
							Binder folder = getFolderModule().getFolder(folderId);
							model.put(WebKeys.BINDER, folder);
							break;
						}
					}
				}
			} catch (Exception no) {}
		} 
		return new ModelAndView(view, model);
		
	}
	private String getDisplayType(PortletRequest request) {
		PortletConfig pConfig = (PortletConfig)request.getAttribute("javax.portlet.config");
		String pName = pConfig.getPortletName();
		if (pName.contains(ViewController.FORUM_PORTLET))
			return ViewController.FORUM_PORTLET;
		else if (pName.contains(ViewController.WORKSPACE_PORTLET))
			return ViewController.WORKSPACE_PORTLET;
		else if (pName.contains(ViewController.PRESENCE_PORTLET))
			return ViewController.PRESENCE_PORTLET;
		else if (pName.contains(ViewController.BLOG_SUMMARY_PORTLET))
			return ViewController.BLOG_SUMMARY_PORTLET;
		else if (pName.contains(ViewController.GUESTBOOK_SUMMARY_PORTLET))
			return ViewController.GUESTBOOK_SUMMARY_PORTLET;
		else if (pName.contains(ViewController.SEARCH_PORTLET))
			return ViewController.SEARCH_PORTLET;
		else if (pName.contains(ViewController.WIKI_PORTLET))
			return ViewController.WIKI_PORTLET;
		return null;

	}
	public static class wsTree implements DomTreeHelper {
		public boolean supportsType(int type, Object source) {
			if (type == DomTreeBuilder.TYPE_WORKSPACE) {return true;}
			return false;
		}
		public boolean hasChildren(AllBusinessServicesInjected bs, Object source, int type) {
			return bs.getBinderModule().hasBinders((Binder)source, EntityType.workspace);
		}
		
	
		public String getAction(int type, Object source) {
			return null;
		}
		public String getURL(int type, Object source) {return null;}
		public String getDisplayOnly(int type, Object source) {
			return "false";
		}
		public String getTreeNameKey() {return "editWs";}
		
	}	
	public static class folderTree implements DomTreeHelper {
		public boolean supportsType(int type, Object source) {
			if (type == DomTreeBuilder.TYPE_WORKSPACE) {return true;}
			if (type == DomTreeBuilder.TYPE_FOLDER) {return true;}
			return false;
		}
		public boolean hasChildren(AllBusinessServicesInjected bs, Object source, int type) {
			return bs.getBinderModule().hasBinders((Binder)source);
		}
	
		public String getAction(int type, Object source) {
			return null;
		}
		public String getURL(int type, Object source) {return null;}
		public String getDisplayOnly(int type, Object source) {
			if (type == DomTreeBuilder.TYPE_FOLDER) return "false";
			return "true";
		}
		public String getTreeNameKey() {return "editForum";}
		
	}	
}
