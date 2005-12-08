package com.sitescape.ef.portlet.workspaceTree;

import java.util.HashMap;
import java.util.Map;


import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.portlet.PortletSession;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.portlet.PortletKeys;
import com.sitescape.ef.web.portlet.SAbstractController;

/**
 * @author Peter Hurley
 *
 */
public class WorkspaceTreeController extends SAbstractController implements DomTreeBuilder {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		PortletSession ses = request.getPortletSession();
		

		Map model = new HashMap();
		Document wsTree = (Document)ses.getAttribute(PortletKeys.WORKSPACE_DOM_TREE);
		if (wsTree == null) {
			wsTree = getWorkspaceModule().getDomWorkspaceTree(this);
			//Save the tree for the session as a performance improvement
			ses.setAttribute(PortletKeys.WORKSPACE_DOM_TREE, wsTree);
		}
		model.put(PortletKeys.WORKSPACE_DOM_TREE, wsTree);
			
	    return new ModelAndView("workspacetree/view", model);
	}

	public Element setupDomElement(String type, Object source, Element element) {
		Element url;
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
        	url = element.addElement("url");
	    	url.addAttribute(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_VIEW_FORUM);
	     	url.addAttribute(PortletKeys.FORUM_URL_FORUM_ID, f.getId().toString());
		} else return null;
		return element;
	}
}