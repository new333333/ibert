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

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;


/**
 * @author Peter Hurley
 *
 */
public class MarkupTag extends BodyTagSupport {
	private String _bodyContent;
	private DefinableEntity entity = null;
	private String type = WebKeys.MARKUP_VIEW;
	private String binderId = "";
	private String entryId = "";
	private boolean leaveSectionsUnchanged = false;
    
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			RenderRequest renderRequest = (RenderRequest) httpReq.getAttribute("javax.portlet.request");
			RenderResponse renderResponse = (RenderResponse) httpReq.getAttribute("javax.portlet.response");
			
			// Transform the body
			String translatedString = _bodyContent;
			
			//Transform the markup 
			if (binderId.equals("")) {
				translatedString = WebHelper.markupStringReplacement(renderRequest, renderResponse, 
						httpReq, httpRes, entity, _bodyContent, type);
			} else if (!binderId.equals("") && !entryId.equals("")) {
				translatedString = WebHelper.markupStringReplacement(renderRequest, renderResponse, 
						httpReq, httpRes, entity, _bodyContent, type, 
						Long.valueOf(binderId), Long.valueOf(entryId));
			} else {
				translatedString = WebHelper.markupStringReplacement(renderRequest, renderResponse, 
						httpReq, httpRes, entity, _bodyContent, type, 
						Long.valueOf(binderId), null);
			}
			if (!this.leaveSectionsUnchanged) {
				//Translate the "sections" markup
				translatedString = WebHelper.markupSectionsReplacement(translatedString);
			}
			pageContext.getOut().print(translatedString);

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e); 
	    }
		finally {
			this.type = WebKeys.MARKUP_VIEW;
			this.entity = null;
			this.binderId = "";
			this.entryId = "";
			this.leaveSectionsUnchanged = false;
		}
	}

	public void setType(String type) {
	    this.type = type;
	}

	public void setEntity(DefinableEntity entity) {
	    this.entity = entity;
	}

	public void setBinderId(String binderId) {
	    this.binderId = binderId;
	}

	public void setEntryId(String entryId) {
	    this.entryId = entryId;
	}

	public void setLeaveSectionsUnchanged(boolean leaveSectionsUnchanged) {
	    this.leaveSectionsUnchanged = leaveSectionsUnchanged;
	}

}


