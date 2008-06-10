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
package com.sitescape.team.samples.remoteapp.web;

import static com.sitescape.util.search.Constants.ENTRY_TYPE_ENTRY;
import static com.sitescape.util.search.Constants.ENTRY_TYPE_FIELD;
import static com.sitescape.util.search.Constants.FAMILY_FIELD;
import static com.sitescape.util.search.Constants.FAMILY_FIELD_TASK;
import static com.sitescape.util.search.Restrictions.eq;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.client.ws.TeamingServiceSoapBindingStub;
import com.sitescape.team.client.ws.TeamingServiceSoapServiceLocator;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.util.search.Constants;
import com.sitescape.util.search.Criteria;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.task.TaskHelper;
import com.sitescape.util.servlet.StringServletResponse;

public class AddEntryServlet extends HttpServlet {

	private static final String TEAMING_SERVICE_ADDRESS = "http://localhost:8080/ssr/token/ws/TeamingService";
	
	private static final String PARAMETER_NAME_VERSION = "ss_version";
	private static final String PARAMETER_NAME_APPLICATION_ID = "ss_application_id";
	private static final String PARAMETER_NAME_USER_ID = "ss_user_id";
	private static final String PARAMETER_NAME_ACCESS_TOKEN = "ss_access_token";
	private static final String PARAMETER_NAME_TOKEN_SCOPE = "ss_token_scope";
	private static final String PARAMETER_NAME_RENDERABLE = "ss_renderable";

	private static final String PARAMETER_FORM_TITLE = "title";
	private static final String PARAMETER_FORM_DESCRIPTION = "description";
	private static final String PARAMETER_FORM_BINDER_ID = "binderId";
	private static final String PARAMETER_FORM_DEFINITION_ID = "definitionId";
	private static final String PARAMETER_FORM_BUTTON_OK = "okBtn";
	private static final String PARAMETER_FORM_BUTTON_CANCEL = "cancelBtn";

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
	throws ServletException, IOException {
		try {
			String version = req.getParameter(PARAMETER_NAME_VERSION);
			String applicationId = req.getParameter(PARAMETER_NAME_APPLICATION_ID);
			String userId = req.getParameter(PARAMETER_NAME_USER_ID);
			String accessToken = req.getParameter(PARAMETER_NAME_ACCESS_TOKEN);
			String tokenScope = req.getParameter(PARAMETER_NAME_TOKEN_SCOPE);
			boolean renderable = Boolean.parseBoolean(req.getParameter(PARAMETER_NAME_RENDERABLE));
			String pathInfo = req.getPathInfo();
			if (pathInfo.equals("/form")) {
				String binderId = req.getParameter(PARAMETER_FORM_BINDER_ID);
				String definitionId = ObjectKeys.DEFAULT_FOLDER_ENTRY_DEF;
				String jsp = "/WEB-INF/jsp/addentry/entry_form.jsp";				
				RequestDispatcher rd = req.getRequestDispatcher(jsp);	
				StringServletResponse resp2 = new StringServletResponse(resp);	
				req.setAttribute(PARAMETER_FORM_BINDER_ID, binderId);
				req.setAttribute(PARAMETER_FORM_DEFINITION_ID, definitionId);
				rd.include(req, resp2);	
				resp.getWriter().print(resp2.getString());
			
			} else if (pathInfo.equals("/submit")) {
				Map params = req.getParameterMap();
				String result = "";
				if (params.containsKey(PARAMETER_FORM_BUTTON_OK)) {
					String title = req.getParameter(PARAMETER_FORM_TITLE);
					String description = req.getParameter(PARAMETER_FORM_DESCRIPTION);
					String binderId = req.getParameter(PARAMETER_FORM_BINDER_ID);
					String definitionId = req.getParameter(PARAMETER_FORM_DEFINITION_ID);

					TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
					locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS);
					TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator
							.getTeamingService();

					Document entryDoc = DocumentHelper.createDocument();
					Element rootElement = entryDoc.addElement(Constants.ENTRY_TYPE_FIELD);
					Element titleElement = rootElement.addElement(Constants.TITLE_FIELD);
					titleElement.setText(title);
					Element descriptionElement = rootElement.addElement(Constants.DESC_FIELD);
					descriptionElement.setText(description);
					Long entryId = stub.folder_addFolderEntry(accessToken, new Long(binderId), 
							definitionId, entryDoc.asXML(), null);
				} else if (params.containsKey(PARAMETER_FORM_BUTTON_CANCEL)) {
					//Cancel button
				}
				resp.getWriter().print(result);
				
			} else {
				String result = "error: URL must end with \"/form\" or \"/submit\"";
				
				resp.getWriter().print(result);
			}
		}
		catch(IOException e) {
			throw e;
		}
		catch(Exception e) {
			throw new ServletException(e);
		}
	}
}
