package com.sitescape.ef.portlet.profile;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.domain.ProfileBinder;

public class ViewController extends  SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		HashMap model = new HashMap();
//temp to bootstrap
	ProfileBinder binder = getProfileModule().addProfileBinder();
		model.put(WebKeys.BINDER, binder);
		Toolbar toolbar = new Toolbar();
		PortletURL url;
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setWindowState(WindowState.MAXIMIZED);
		toolbar.addToolbarMenu("listing", "List", url);
		model.put(WebKeys.TOOLBAR, toolbar.getToolbar());
		return new ModelAndView(WebKeys.VIEW_PROFILE, model);
	}


}
