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
package com.sitescape.team.portlet.administration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.servlet.forum.ViewFileController;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.util.FileHelper;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.TempFileUtil;
import com.sitescape.team.util.XmlFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;
public class ExportDefinitionController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		HashSet<String> uniqueFilenames = new HashSet<String>();
		if (formData.containsKey("okBtn")) {
			List errors = new ArrayList();
			//Get the forums to be indexed
			String dirPath = SPropsUtil.getDirPath("data.root.dir") + File.separator + "definitions" +
				File.separator + RequestContextHolder.getRequestContext().getZoneName();
			FileHelper.mkdirsIfNecessary(dirPath);
			Iterator itFormData = formData.entrySet().iterator();
			while (itFormData.hasNext()) {
				Map.Entry me = (Map.Entry) itFormData.next();
				if (((String)me.getKey()).startsWith("id_")) {
					String defId = ((String)me.getKey()).substring(3);
					if (Validator.isNotNull(defId)) {
						Definition def =null;
						try {
							def = getDefinitionModule().getDefinition(defId);
							// explicity set encoding so their is not mistake.
							//cannot guarentee default will be set to UTF-8
							String filePath = dirPath + File.separator +  def.getName() + ".xml";
							XmlFileUtil.writeFile(def.getDefinition(), filePath);
							uniqueFilenames.add(filePath);
						} catch (Exception ex) {
							errors.add(ex.getLocalizedMessage());
						}
					}
				}
			}
			
			Document listOfFiles = ViewFileController.createFileListingForZipDownload("definitions.zip");
			Element listOfFilesRoot = listOfFiles.getRootElement();
			for(String path : uniqueFilenames) {
				ViewFileController.addFileToList(listOfFiles, path);
			}
			File listOfFilesTempFile = TempFileUtil.createTempFile("exportDefinitions");
			XmlFileUtil.writeFile(listOfFiles, listOfFilesTempFile.getAbsolutePath());

			response.setRenderParameter(WebKeys.DOWNLOAD_URL, 
					WebUrlUtil.getServletRootURL() + WebKeys.SERVLET_VIEW_FILE + "?viewType=zipped&fileId=" +
					listOfFilesTempFile.getName());
			response.setRenderParameter(WebKeys.ERROR_LIST, (String[])errors.toArray( new String[0]));
			response.setRenderParameter("redirect", "true");
			
		} else if (formData.containsKey("cancelBtn")) {
			response.setRenderParameter("redirect", "true");
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		if (!Validator.isNull(request.getParameter("redirect"))) {
			String [] errors = request.getParameterValues(WebKeys.ERROR_LIST);
			model.put(WebKeys.ERROR_LIST, errors);
			model.put(WebKeys.DOWNLOAD_URL, PortletRequestUtils.getStringParameter(request, WebKeys.DOWNLOAD_URL, ""));
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT, model);
		}
		Document definitionConfig = getDefinitionModule().getDefinitionConfig();
		List currentDefinitions = getDefinitionModule().getDefinitions();
		
		//Build the definition tree
		Document definitionTree = DocumentHelper.createDocument();
		Element dtRoot = definitionTree.addElement(DomTreeBuilder.NODE_ROOT);
		dtRoot.addAttribute("title", NLT.getDef("__definitions"));
		dtRoot.addAttribute("id", "definitions");
		dtRoot.addAttribute("displayOnly", "true");
		dtRoot.addAttribute("url", "");
		Element root = definitionConfig.getRootElement();
		
		Iterator definitions = root.elementIterator("definition");
		while (definitions.hasNext()) {
			Element defEle = (Element) definitions.next();
			Element treeEle = dtRoot.addElement("child");
			treeEle.addAttribute("type", "definition");
			treeEle.addAttribute("title", NLT.getDef(defEle.attributeValue("caption")));
			treeEle.addAttribute("id", defEle.attributeValue("name"));	
			treeEle.addAttribute("displayOnly", "true");
			treeEle.addAttribute("url", "");
			//Add the current definitions (if any)
			ListIterator li = currentDefinitions.listIterator();
			while (li.hasNext()) {
				Definition curDef = (Definition)li.next();
				Document curDefDoc = curDef.getDefinition();
				if (curDefDoc == null) continue;
				if (curDef.getType() == Integer.valueOf(defEle.attributeValue("definitionType", "0")).intValue()) {
					Element curDefEle = treeEle.addElement("child");
					curDefEle.addAttribute("type", defEle.attributeValue("name"));
					String title = NLT.getDef(curDef.getName());
					//TODO get the caption from the definition meta data
					String caption = curDefDoc.getRootElement().attributeValue("caption", "");
					if (!caption.equals("")) {
						title = NLT.getDef(caption) + " (" + title + ")";
					}
					curDefEle.addAttribute("title", title);
					curDefEle.addAttribute("id", curDef.getId());
					curDefEle.addAttribute("url", "");
				}
			}
		}
		model.put(WebKeys.DOM_TREE, definitionTree);
 		return new ModelAndView(WebKeys.VIEW_ADMIN_EXPORT_DEFINITIONS, model);
	}
}
