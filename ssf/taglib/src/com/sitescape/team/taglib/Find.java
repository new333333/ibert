/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.taglib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.team.web.WebKeys;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;


/**
 * @author Peter Hurley
 *
 */
public class Find extends BodyTagSupport implements ParamAncestorTag {
    private Set userList;    
    private String formName = "";
    private String formElement;
    private String width = "30";
    private String type;
    private Boolean singleItem;
    private Boolean leaveResultsVisible;
    private String clickRoutine = "";
    private String clickRoutineArgs = "";
    private String binderId = "";
    private Boolean searchSubFolders;
    private Boolean foldersOnly;
    private String instanceCount;
    private String accessibilityText;

	private Map _params;

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			if (this._params == null) this._params = new HashMap();
			
			if (this.userList == null) this.userList = new HashSet();			
			if (this.type == null) this.type = WebKeys.USER_SEARCH_USER_GROUP_TYPE_USER;
			if (singleItem == null) singleItem = false;
			if (leaveResultsVisible == null) leaveResultsVisible = false;
			if (searchSubFolders == null) searchSubFolders = false;
			if (foldersOnly == null) foldersOnly = false;
			this.instanceCount = UUID.randomUUID().toString();
			
			//Output the start of the area
			RequestDispatcher rd;
			String jsp;
			if (singleItem) {
				if (type.equals("places")) {
					jsp = "/WEB-INF/jsp/tag_jsps/find/single_place.jsp";
				} else if (type.equals("tags") || type.equals("personalTags") || type.equals("communityTags")) {
					jsp = "/WEB-INF/jsp/tag_jsps/find/single_tag.jsp";
				} else if (type.equals("entries")) {
					jsp = "/WEB-INF/jsp/tag_jsps/find/single_entry.jsp";
				} else {
					jsp = "/WEB-INF/jsp/tag_jsps/find/single_user.jsp";
				}
			} else {
				if (type.equals("places")) {
					jsp = "/WEB-INF/jsp/tag_jsps/find/places_list.jsp";
				} else if (type.equals("tags")) {
					jsp = "/WEB-INF/jsp/tag_jsps/find/tag_list.jsp";
				} else if (type.equals("entries")) {
					jsp = "/WEB-INF/jsp/tag_jsps/find/entries_list.jsp";
				} else {
					jsp = "/WEB-INF/jsp/tag_jsps/find/user_list.jsp";
				}
			}
			rd = httpReq.getRequestDispatcher(jsp);

			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq, _params);
			req.setAttribute("user_list", this.userList);			
			req.setAttribute("form_name", this.formName);
			req.setAttribute("form_element", this.formElement);
			req.setAttribute("element_width", this.width);
			req.setAttribute("list_type", this.type);
			req.setAttribute("singleItem", this.singleItem);
			req.setAttribute("clickRoutine", this.clickRoutine);
			req.setAttribute("clickRoutineArgs", this.clickRoutineArgs);
			req.setAttribute("instanceCount", this.instanceCount);
			req.setAttribute("instanceCode", this.hashCode() + "_" + this.formName + "_" + this.formElement);
			req.setAttribute("leaveResultsVisible", this.leaveResultsVisible);
			req.setAttribute("searchSubFolders", this.searchSubFolders.toString());
			req.setAttribute("foldersOnly", this.foldersOnly.toString());
			req.setAttribute("binderId", this.binderId);
			req.setAttribute("accessibilityText", this.accessibilityText);
			
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			return EVAL_PAGE;
		}
	    catch(Exception e) {
			// TODO: temporary, remove later
	    	// System.out.println("EXCEPTION:"+e);
	        throw new JspException(e);
	    }
		finally {
			this.formName = "";
			this.userList = null;
			this.singleItem = false;
			this.width = "30";
			this.clickRoutine = "";
			this.clickRoutineArgs = "";
			this.leaveResultsVisible = false;
			this.binderId = "";
			this.searchSubFolders = false;
			this.foldersOnly = false;
			this.instanceCount = null;
			this.accessibilityText = null;
			if (_params != null) {
				_params.clear();
			}
		}
	}

	public void addParam(String name, String value) {
		if (_params == null) {
			_params = new HashMap();
		}

		String[] values = (String[])_params.get(name);
		if (values == null) {
			values = new String[] {value};
		} else {
			String[] newValues = new String[values.length + 1];
			System.arraycopy(values, 0, newValues, 0, values.length);
			newValues[newValues.length - 1] = value;
			values = newValues;
		}
		_params.put(name, values);
	}

	public void setUserList(Set userList) {
	    this.userList = userList;
	}

	public void setFormName(String formName) {
	    this.formName = formName;
	}

	public void setFormElement(String formElement) {
	    this.formElement = formElement;
	}

	public void setWidth(String width) {
	    this.width = width;
	}

	public void setType(String type) {
	    this.type = type;
	}

	public void setClickRoutine(String clickRoutine) {
	    this.clickRoutine = clickRoutine;
	}

	public void setClickRoutineArgs(String clickRoutineArgs) {
		this.clickRoutineArgs = clickRoutineArgs;
	}
	
	public void setSingleItem(Boolean singleItem) {
	    this.singleItem = singleItem;
	}

	public void setLeaveResultsVisible(Boolean leaveResultsVisible) {
	    this.leaveResultsVisible = leaveResultsVisible;
	}

	public void setBinderId(String binderId) {
	    this.binderId = binderId;
	}

	public void setSearchSubFolders(Boolean searchSubFolders) {
	    this.searchSubFolders = searchSubFolders;
	}
	
	public void setFoldersOnly(Boolean foldersOnly) {
	    this.foldersOnly = foldersOnly;
	}
	
	public void setAccessibilityText(String accessibilityText) {
	    this.accessibilityText = accessibilityText;
	}	
}
