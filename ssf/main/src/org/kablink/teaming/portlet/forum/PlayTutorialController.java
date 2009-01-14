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
package org.kablink.teaming.portlet.forum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WorkspaceTreeHelper;
import org.springframework.web.portlet.ModelAndView;


/**
 * This class handles the requests to play one of the tutorial videos.
 * 
 * @author Jay Wootton
 *
 */
public class PlayTutorialController extends SAbstractController
{
	private String m_tutorialName;		// Name of the tutorial passed in the request to invoke the Play Tutorial page.
	
	/**
	 * 
	 */
	public void handleActionRequestAfterValidation(
			ActionRequest	request,
			ActionResponse	response ) throws Exception
	{
		m_tutorialName = PortletRequestUtils.getStringParameter( request, WebKeys.TUTORIAL_NAME, "" );
	}// end handleActionRequestAfterValidation()
	
	
	/**
	 * 
	 */
	public ModelAndView handleRenderRequestInternal(
			RenderRequest	request, 
			RenderResponse	response ) throws Exception
	{
		String				jspName;
		String				tutorialName;
 		Map<String,Object>	model;
 		
 		model = new HashMap<String,Object>();
		
 		// Add the name of the tutorial to play to the response.
 		if ( m_tutorialName != null )
 			model.put( WebKeys.TUTORIAL_NAME, m_tutorialName );
 		
		jspName = WebKeys.VIEW_PLAY_TUTORIAL;

 		return new ModelAndView( jspName, model );
	}// end handleRenderRequestInternal()

}// end PlayTutorialController()
