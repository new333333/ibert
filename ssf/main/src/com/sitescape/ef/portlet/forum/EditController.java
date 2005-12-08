package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.portlet.PortletKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.module.shared.DomTreeBuilder;

import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Workspace;

/**
 * @author Peter Hurley
 *
 */
public class EditController extends SAbstractController implements DomTreeBuilder {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {

		PortletPreferences prefs = request.getPreferences();

		String forumId = ActionUtil.getStringValue(request.getParameterMap(), PortletKeys.FORUM_URL_FORUM_ID);

		//Get the name of the forum to be displayed
		prefs.setValue(PortletKeys.FORUM_URL_FORUM_ID, forumId);

		prefs.store();
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {


        //Make the prefs available to the jsp
        Map model = new HashMap();
		
		model.put(PortletKeys.WORKSPACE_DOM_TREE, getWorkspaceModule().getDomWorkspaceTree(this));
		
		PortletPreferences prefsPP = request.getPreferences();
		String forumPref = prefsPP.getValue(PortletKeys.FORUM_URL_FORUM_ID, "");
    	if (!forumPref.equals("")) {		
			//Build the jsp beans
			try {
				model.put(PortletKeys.FOLDER, getFolderModule().getFolder(new Long(forumPref)));    
			} catch (NoFolderByTheIdException nf) {
				//fall thru
			}
    	}
			
		return new ModelAndView(PortletKeys.VIEW_EDIT, model);
	}
	public Element setupDomElement(String type, Object source, Element element) {
		if (type.equals(DomTreeBuilder.TYPE_WORKSPACE)) {
			Workspace ws = (Workspace)source;
			element.addAttribute("type", "workspace");
			element.addAttribute("title", ws.getTitle());
			element.addAttribute("id", ws.getId().toString());
			element.addAttribute("image", "workspace");
			element.addAttribute("displayOnly", "true");
			element.addAttribute("url", "");
		} else if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
			Folder f = (Folder)source;
			element.addAttribute("type", "forum");
			element.addAttribute("title", f.getTitle());
			element.addAttribute("id", f.getId().toString());
			element.addAttribute("image", "forum");
			element.addAttribute("url", "");
		} else return null;
		return element;
	}
}
