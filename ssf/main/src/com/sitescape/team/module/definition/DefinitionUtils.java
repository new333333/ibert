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
/**
 * Utility routines to deal with definitions.
 */
package com.sitescape.team.module.definition;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Validator;


public class DefinitionUtils {
   public static String getPropertyValue(Element element, String name) {
		Element variableEle = (Element)element.selectSingleNode("./properties/property[@name='" + name + "']");
		if (variableEle == null) return null;
		return variableEle.attributeValue("value");   	
    }
   public static String getPropertyValue(Element element, String name, String attribute) {
		Element variableEle = (Element)element.selectSingleNode("./properties/property[@name='" + name + "']");
		if (variableEle == null) return null;
		return variableEle.attributeValue(attribute);   	
   }
    public static List getPropertyValueList(Element element, String name) {
		List resultElements = element.selectNodes("./properties/property[@name='" + name + "']");
    	List results = new ArrayList();
    	for (int i=0; i<resultElements.size(); ++i) {
    		Element variableEle = (Element)resultElements.get(i);
    		results.add(variableEle.attributeValue("value",  ""));
    	}
		return results;   	
    }
    public static boolean isSourceItem(Document definitionTree, String itemSource, String itemTarget) {
    	if (definitionTree == null) return false;
		Element root = definitionTree.getRootElement();
		
		//Get a list of all of the form items in the definition (i.e., from the "form" section of the definition)
		Element entryFormItem = (Element)root.selectSingleNode("item[@type='form']");
		if (entryFormItem == null) return false;
		//see if item is generated and save source
		Element itemEle = (Element)entryFormItem.selectSingleNode(".//item[@name='" + itemTarget + "']");
		if (itemEle == null) return false;
		boolean generated = GetterUtil.get(DefinitionUtils.getPropertyValue(itemEle, "generated"), false);
		if (generated) {
			String source = getPropertyValue(itemEle, "itemSource");
			if (!Validator.isNull(source) && source.equals(itemSource)) return true;
		}
		return false;
    }
   public static String getViewType(Document definitionTree) {
	   	if (definitionTree == null) return null;
		Element root = definitionTree.getRootElement();
		if (root == null) return null;
		Element viewItem = (Element)root.selectSingleNode("//item[@name='forumView' or @name='profileView' or @name='workspaceView' or @name='userWorkspaceView']");
		if (viewItem == null) return null;
		return DefinitionUtils.getPropertyValue(viewItem, "type");
   }
   public static Element getItemByPropertyName(Element item, String itemType, String nameValue) {
		//Find the item in the definition
		Element propertyEle = (Element) item.selectSingleNode(
				"//item[@name='" + itemType + "']/properties/property[@name='name' and @value='"+nameValue+"']");
		if (propertyEle != null) {
			return propertyEle.getParent().getParent();
		}
		return null;

   }

   public static String getViewURL(FolderEntry fEntry, FileAttachment att)
   {
		return WebUrlUtil.getServletRootURL() + WebKeys.SERVLET_VIEW_FILE + "?" +
		WebKeys.URL_BINDER_ID + "=" + fEntry.getParentFolder().getId().toString() +
		"&entityType=folderEntry" +
		"&" + WebKeys.URL_ENTRY_ID + "=" + fEntry.getId().toString() +
		"&" + WebKeys.URL_FILE_ID + "=" + att.getId(); 
   }

   public static String getViewPermalinkURL(FolderEntry fEntry, FileAttachment att)
   {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, fEntry.getParentFolder().getId().toString());
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, fEntry.getId().toString());
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, fEntry.getEntityType().toString());
		adapterUrl.setParameter(WebKeys.URL_FILE_ID, att.getId().toString());
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		adapterUrl.setParameter(WebKeys.URL_ZONE_ID, zoneId.toString());
		return adapterUrl.toString();
   }
}
