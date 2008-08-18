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
package com.sitescape.team.servlet.forum;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.FileHelper;
import com.sitescape.team.util.NLT;
import com.sitescape.util.FileUtil;
public class ReadFileController extends AbstractReadFileController {
	
	
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		// Assuming that the full request URL was http://localhost:8080/ssf/s/readFile/xxx/123/456/789/junk.doc,
		// the following call returns "/readFile/xxx/123/456/789/junk.doc" portion of the URL.
		String pathInfo = request.getPathInfo();
		
		String[] args = pathInfo.split(Constants.SLASH);
		//We expect the url to be formatted as /readFile/entityType/binderId/entryId/fileId/fileTime/fileVersion/filename.ext
		//To support sitescape forum, where folder structures were allowed on an entry, the url may contain more pathinfo.
		//For a binder, the entityId = binderId 
		//fileVersion=last, read latest
		//fileTime is present for browser cachinge
		//filename is present for browser handling of relative files
		if (args.length < 9) return null;
		
		try {
			String strEntityType = args[2];
			Long binderId = Long.valueOf(args[3]);
			Long entityId = Long.valueOf(args[4]);
			DefinableEntity entity = getEntity(strEntityType, binderId, entityId);
			//Set up the beans needed by the jsps
			FileAttachment fa = null;
			if (args.length > 9 && entity instanceof FolderEntry) {
				fa = getAttachment((FolderEntry)entity, Arrays.asList(args).subList(8, args.length).toArray());
				//entity may have changed
				if (fa != null) {
					entity = fa.getOwner().getEntity();
				}
			} else {
				fa = getAttachment(entity, args[5], args[7]);
			}

			if (fa != null) {
				String shortFileName = FileUtil.getShortFileName(fa.getFileItem().getName());	
				String contentType = getFileTypeMap().getContentType(shortFileName);
				response.setContentType(contentType);
				response.setHeader("Cache-Control", "private");
				String attachment = "";
				if (FileHelper.checkIfAttachment(contentType)) attachment = "attachment; ";
				response.setHeader("Content-Disposition",
						attachment + "filename=\"" + FileHelper.encodeFileName(request, shortFileName) + "\"");
				response.setHeader("Last-Modified", formatDate(fa.getModification().getDate()));	
				try {
					Binder parent = getBinder(entity);
					response.setHeader("Content-Length", 
							String.valueOf(FileHelper.getLength(parent, entity, fa)));
					getFileModule().readFile(parent, entity, fa, response.getOutputStream());
					getReportModule().addFileInfo(AuditType.download, fa);
				}
				catch(Exception e) {
					response.getOutputStream().print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
				}
			}
			try {
				response.getOutputStream().flush();
			}
			catch(Exception ignore) {}

		} catch(Exception e) {
			//Bad format of url; just return null
			response.getOutputStream().print(NLT.get("file.error.unknownFile"));
		}
		
		return null;
	}
}
