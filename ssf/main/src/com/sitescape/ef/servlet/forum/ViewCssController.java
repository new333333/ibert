package com.sitescape.ef.servlet.forum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.servlet.SAbstractController;

public class ViewCssController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
		response.setContentType("text/css");			
		String viewPath = "common/ssf_css";
		return new ModelAndView(viewPath);
	}
}
