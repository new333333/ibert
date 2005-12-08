
package com.sitescape.ef.portlet.forum;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.WindowState;

import com.sitescape.ef.portlet.Constants;
import com.sitescape.ef.web.portlet.SAbstractController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Janet McCann
 *
 */
public class SAbstractForumController extends SAbstractController {
	public ModelAndView returnToViewForum(RenderRequest request, Map formData, Long folderId) {
		request.setAttribute(Constants.ACTION, Constants.FORUM_OPERATION_VIEW_FORUM);
		Map model = getForumActionModule().getShowFolder(formData, request, folderId);
		Object obj = model.get(Constants.CONFIG_ELEMENT);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(Constants.VIEW_NO_DEFINITION, model);
		obj = model.get(Constants.CONFIG_DEFINITION);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(Constants.VIEW_NO_DEFINITION, model);
		return new ModelAndView(Constants.VIEW_FORUM, model);
	}
/*	public ModelAndView returnToWorkspace(RenderRequest req) {
		Map model = new HashMap();
		model.put(ObjectKeys.WORKSPACE_DOM_TREE, getWorkspaceModule().getDomWorkspaceTree());
		
		//Make the tree available to the jsp
		ModelUtil.processModel(req,model);
		if (req.getWindowState().equals(WindowState.NORMAL)) {
		    return mapping.findForward("portlet.forum.view_workspacetree");
		} else {
			//Show the workspace tree maximized
		    return mapping.findForward("portlet.forum.view_workspacetree_maximized");
		}
	}
	public ModelAndView returnToWorkspace(HttpServletRequest req) {
		//There is no forum specified, show the workspace tree
		Map model = new HashMap();
		model.put(ObjectKeys.WORKSPACE_DOM_TREE, getWorkspaceModule().getDomWorkspaceTree());
		
		//Make the tree available to the jsp
		ModelUtil.processModel(req,model);
		return mapping.findForward("portlet.forum.view_workspacetree_maximized");				

	}
*/
}
