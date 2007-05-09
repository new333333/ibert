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
/*
 * Created on Jun 10, 2005
 *
 */
package com.sitescape.team.taglib;

import java.lang.Boolean;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sitescape.team.domain.Event;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

/**
 * @author billmers
 *;
 */

public class Eventeditor extends TagSupport {
        
  private String contextPath;
  private String id;
  private String formName;
  private Event initEvent = null;
  private Boolean hasDuration = new Boolean("false");
  private Boolean hasRecurrence = new Boolean("true");

  public int doStartTag() throws JspException {
    JspWriter jspOut = pageContext.getOut(); 
	    
    try {
        if (id == null) {
        	throw new JspException("You must provide an element name"); 
        }
        if (formName == null) {
        	throw new JspException("You must provide a form name"); 
        }
        
      HttpServletRequest req2 = (HttpServletRequest) pageContext.getRequest();
      contextPath = req2.getContextPath();
      if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);
				
      ServletRequest req = null;
      req = new DynamicServletRequest((HttpServletRequest)pageContext.getRequest());

      String jsp = "/WEB-INF/jsp/tag_jsps/eventeditor/eventeditor.jsp";
      String icon = contextPath + "/images/pics/sym_s_repeat.gif";
      RequestDispatcher rd = req.getRequestDispatcher(jsp); 
      
      // if initEvent is provided, take it apart and pass in two dates
      Date startDate = new Date();
      Date endDate = new Date();
      // initialize the event, if none was provided
      if (initEvent != null) {
          Calendar startCal = initEvent.getDtStart();
          Calendar endCal = initEvent.getDtEnd();
          // if the start or end dates were never initialized, set to today
          if (startCal.getTime().getTime() == 0) {
        	  startCal.setTime(startDate);
          }
          if (endCal.getTime().getTime() == 0) {
        	  endCal.setTime(endDate);
          }
          
          startDate = startCal.getTime();
          endDate = endCal.getTime();
      } else {
    	  initEvent = new Event();
    	  GregorianCalendar startCal = new GregorianCalendar();
    	  startCal.setTime(startDate);
    	  initEvent.setDtStart(startCal);
    	  if (hasDuration.booleanValue()) {
    		  GregorianCalendar endCal = new GregorianCalendar();
    		  endCal.setTime(endDate);
    		  initEvent.setDtEnd(endCal);
    	  }
      }
      
      // any attributes we might want to pass into the jsp go here
      req.setAttribute("initEvent", initEvent);
      // these need to be beans because the jsp page will pass them on to other tags
      req.setAttribute("evid", id);
      req.setAttribute("formName", formName);
      req.setAttribute("startDate", startDate);
      req.setAttribute("endDate", endDate);
      // any other miscellaneous pieces can go here, for access by JSTL on the JSP page
      HashMap attMap = new HashMap();
      attMap.put("hasDur", hasDuration);
      attMap.put("hasRecur", hasRecurrence);
      req.setAttribute("attMap", attMap);
      
      StringServletResponse res =
          new StringServletResponse((HttpServletResponse)pageContext.getResponse());
      // this next line invokes the jsp and captures it into res
      rd.include(req, res);
      // and now dump it out into this response
      pageContext.getOut().print(res.getString());
    }

    catch (Exception e) {
      throw new JspException(e);
    }
    finally {
	  id = null;
	  formName = null;
	  initEvent = null;
	  hasDuration = new Boolean("false");
	  hasRecurrence = new Boolean("true");
    }
    return SKIP_BODY;
  }

  public int doEndTag() throws JspException {
      return SKIP_BODY;
  }

  public void setId(String id) {
      this.id = id;
  }

  public void setFormName(String formName) {
      this.formName = formName;
  }

  public void setHasDuration(Boolean hasDuration) {
      this.hasDuration = hasDuration;
  }

  public void setHasRecurrence(Boolean hasRecurrence) {
      this.hasRecurrence = hasRecurrence;
  }

  public void setInitEvent(Event initEvent) {
      this.initEvent = initEvent;
  }
  
}

