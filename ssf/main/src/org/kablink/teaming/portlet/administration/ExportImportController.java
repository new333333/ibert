/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.portlet.administration;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.activation.FileTypeMap;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipOutputStream;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.portletadapter.portlet.HttpServletResponseReachable;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.ModelAndView;

public class ExportImportController  extends  SAbstractController {

	private Long binderId;
	private Map options;
	private String filename = "export.zip";
	private FileTypeMap mimeTypes = new ConfigurableMimeFileTypeMap();
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.OPERATION, "");
		
		if(operation.equals(WebKeys.OPERATION_IMPORT)){
			binderId = PortletRequestUtils.getLongParameter(request,  WebKeys.URL_BINDER_ID);
			
			Map formData = request.getParameterMap();
			if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
				Map fileMap=null;
				if (request instanceof MultipartFileSupport) {
					fileMap = ((MultipartFileSupport) request).getFileMap();
			    	MultipartFile myFile = (MultipartFile)fileMap.get("imports");
			    	InputStream fIn = myFile.getInputStream();
			    	
			    	getBinderModule().importZip(binderId, fIn);	
				} else {
					response.setRenderParameters(formData);
				}
			} else
				response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		binderId = PortletRequestUtils.getLongParameter(request,  WebKeys.URL_BINDER_ID);
		HttpServletResponse res = ((HttpServletResponseReachable)response).getHttpServletResponse();		
		Map<String,Object> model = new HashMap<String,Object>();
		
		try{
			//Set up the standard beans
			BinderHelper.setupStandardBeans(this, request, response, model, binderId);
		}catch(NoBinderByTheIdException exc){
			res.setContentType(mimeTypes.getContentType(filename));
			res.setHeader("Cache-Control", "private");
			res.setHeader(
						"Content-Disposition",
						"attachment; filename=\"" + filename + "\"");
			
			ZipOutputStream zipOut = new ZipOutputStream(res.getOutputStream());
			
			//Standard zip encoding is cp437. (needed when chars are outside the ASCII range)
			zipOut.setEncoding("cp437");
			zipOut.finish();
			
			return null;
		}
	
		String entryIdStr = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		Long entryId = null;
		if (!entryIdStr.equals("")) entryId = new Long(entryIdStr);
		
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.OPERATION, "");
		
		boolean showMenu = PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_SHOW_MENU, false);
		
		if (showMenu) {
			//not ajax request
			model.put(WebKeys.URL_BINDER_ID, binderId);
			return new ModelAndView(WebKeys.VIEW_ADMIN_EXPORT_IMPORT, model);
		}
		
		//EXPORTING...
		if(operation.equals(WebKeys.OPERATION_EXPORT)){
			res.setContentType(mimeTypes.getContentType(filename));
			res.setHeader("Cache-Control", "private");
			res.setHeader(
						"Content-Disposition",
						"attachment; filename=\"" + filename + "\"");
			
			if(entryId != null){
				FolderEntry entry = null;
				
				try{
					entry = getFolderModule().getEntry(binderId, entryId);
				}catch(NoFolderEntryByTheIdException exc){
					ZipOutputStream zipOut = new ZipOutputStream(res.getOutputStream());		
		
					//Standard zip encoding is cp437. (needed when chars are outside the ASCII range)
					zipOut.setEncoding("cp437");
					zipOut.finish();
				
					return null;
				}
			}else{
				UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);
				options = ListFolderHelper.getSearchFilter(this, request, getBinderModule().getBinder(binderId), userFolderProperties);
			}
			
			getBinderModule().export(binderId, entryId, res.getOutputStream(), options);
		}
		return null;
	}
}