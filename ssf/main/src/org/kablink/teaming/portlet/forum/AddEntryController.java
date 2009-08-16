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
package org.kablink.teaming.portlet.forum;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.TimeZone;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.kablink.teaming.web.util.BinderHelper;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.ModelAndView;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.EventsViewHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.repository.RepositoryServiceException;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.FileUploadItem;
import org.kablink.teaming.util.SimpleMultipartFile;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.tree.FolderConfigHelper;
import org.kablink.teaming.web.tree.WorkspaceConfigHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.kablink.util.cal.Duration;

/**
 * @author Peter Hurley
 *
 */
public class AddEntryController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		String blogReply = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BLOG_REPLY, "");
		String addEntryFromIFrame = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "");
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
		//See if the add entry form was submitted
		Long entryId=null;
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			//The form was submitted. Go process it
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			
			if (action.equals(WebKeys.ACTION_ADD_FOLDER_ENTRY)) {
				MapInputData inputData = new MapInputData(formData);
				
				try {
					entryId= getFolderModule().addEntry(folderId, entryType, inputData, fileMap, null).getId();
				} catch(WriteFilesException e) {
		    		response.setRenderParameter(WebKeys.FILE_PROCESSING_ERRORS, e.getMessage());
		    		return;
				}
				setupReloadOpener(response, folderId, null);
				if (!addEntryFromIFrame.equals("")) {
					response.setRenderParameter(WebKeys.NAMESPACE, namespace);
					response.setRenderParameter(WebKeys.IN_IFRAME_ADD_ENTRY, "1");
					response.setRenderParameter(WebKeys.ENTRY_ID, entryId.toString());
				}
			} else if (action.equals(WebKeys.ACTION_ADD_FOLDER_REPLY)) {
				MapInputData inputData = new MapInputData(formData);
				Long id = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
				try {
					entryId = getFolderModule().addReply(folderId, id, entryType, inputData, fileMap, null).getId();
				} catch(WriteFilesException e) {
		    		response.setRenderParameter(WebKeys.FILE_PROCESSING_ERRORS, e.getMessage());
		    		return;
				} catch(WriteEntryDataException e) {
		    		response.setRenderParameter(WebKeys.ENTRY_DATA_PROCESSING_ERRORS, e.getMessage());
		    		return;
				}
				//Show the parent entry when this operation finishes
				setupReloadOpener(response, folderId, id);
				if (!blogReply.equals("")) {
			    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
					response.setRenderParameter(WebKeys.BLOG_REPLY, "1");
					response.setRenderParameter(WebKeys.NAMESPACE, namespace);
					response.setRenderParameter(WebKeys.ENTRY_ID, entry.getParentEntry().getId().toString());
					response.setRenderParameter(WebKeys.BLOG_REPLY_COUNT, String.valueOf(entry.getParentEntry().getTotalReplyCount()));
					response.setRenderParameter(WebKeys.BINDER_ID, folderId.toString());
				}
			}
			
			//If we just added a MiniBlog entry, update the user's status
			BinderHelper.updateUserStatus(this, request, folderId, entryId);
			
			//See if the user wants to send mail
			BinderHelper.sendMailOnEntryCreate(this, request, folderId, entryId);
			
			//See if the user wants to subscribe to this entry
			BinderHelper.subscribeToThisEntry(this, request, folderId, entryId);
			
			//flag reload of folder listing
			//response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");
		} else if (formData.containsKey("cancelBtn")) {
			if (!blogReply.equals("")) {
				setupReloadOpener(response, folderId, entryId);
		    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
				response.setRenderParameter(WebKeys.BLOG_REPLY, "1");
				response.setRenderParameter(WebKeys.NAMESPACE, namespace);
				response.setRenderParameter(WebKeys.ENTRY_ID, entryId.toString());
				response.setRenderParameter(WebKeys.BLOG_REPLY_COUNT, String.valueOf(entry.getTotalReplyCount()));
			} else if (!addEntryFromIFrame.equals("")) {
				setupReloadOpener(response, folderId, Long.MIN_VALUE);
				//clear entryId out
				response.setRenderParameter(WebKeys.URL_ENTRY_ID, "");
				response.setRenderParameter(WebKeys.IN_IFRAME_ADD_ENTRY, "1");				
			} else {
				setupCloseWindow(response);
			}
		} else if (action.equals(WebKeys.ACTION_ADD_FOLDER_ATTACHMENT) && WebHelper.isMethodPost(request)) {
			String isLibraryBinder = PortletRequestUtils.getStringParameter(request, WebKeys.URL_IS_LIBRARY_BINDER, "");
			
			Map fileMap = null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			
			//If the folder is a Library Folder 
			if (isLibraryBinder.equalsIgnoreCase("true")) {
				//We need to get the name of the files and the path information
				//Based on the path information, we need to see if the folder is already present or not 
				//If the folder is already present, we will use it, if not we will create the folder
				//Then we will check if the file we are trying to place in the folder is already present
				//If they are already present, then we will create a new version of the file
				//If they are not already present, we will have to create a new entry and associate the entry with 
				//the new attachment.
				String nameValue = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER;
				String folderNameValue = ObjectKeys.FILES_FROM_APPLET_FOLDER_INFO_FOR_BINDER;
	        	boolean blnCheckForAppletFile = true;
	        	int intFileCount = 1;

	        	//Looping through each of the uploaded file
	        	while (blnCheckForAppletFile) {
	        		
	        		//Get File Name Identifier
	        		String fileEleName = nameValue + Integer.toString(intFileCount);
	        		//Get Folder Name Identifier
	        		String fileFolderName = folderNameValue + Integer.toString(intFileCount);
	        		
	        		//Checking if the file with name is present 
	        		if (fileMap.containsKey(fileEleName)) {
	        	    	//Setting the parent folder to be used first
	        	    	Long lngFolderIdToUse = folderId;
	        			
	        			//Getting the file information
	        			MultipartFile myFile = (MultipartFile)fileMap.get(fileEleName);
	        	    	String strEncodedFileName = myFile.getOriginalFilename();
	        	    	String strDecodedFileName = URLDecoder.decode(strEncodedFileName, "UTF-8"); 
	        	    	
	        	    	//Getting the file folder information
	        	    	String fileFolderNameVal = PortletRequestUtils.getStringParameter(request, fileFolderName, "");
	        	    	//Getting the list of folders as a arraylist
	        	    	ArrayList folderArrayList = getFolderListWithDecodedValues(fileFolderNameVal);
	        	    	
	        	    	//Looping through the folder name to identify under which folder we need to add the attachment
	        	    	for (int i = 0; i < folderArrayList.size(); i++) {
	        	    		String strFolderName = (String) folderArrayList.get(i);
	        	    		if (!strFolderName.equals("")) {
	        	    			Folder folderObj = getFolderModule().getFolder(lngFolderIdToUse);
	        	    			
	        	    			//Getting the sub-folders that have been created under the current folder
			        	    	java.util.List arrSubFolders = (java.util.List) folderObj.getFolders();
			        	    	boolean doesFolderExist = false;
			        	    	
			        	    	//Looping through the sub-folders that have already been created
			        	    	for (int j = 0; j < arrSubFolders.size(); j++) {
			        	    		Binder subFolder = (Binder) arrSubFolders.get(j);
			        	    		String strSubFolderName = subFolder.getTitle();
			        	    		//Checking to see if the sub-folder already created matches with the folder that has been dragged & dropped
			        	    		if (strSubFolderName.equals(strFolderName)) {
			        	    			doesFolderExist = true;
			        	    			//Since the folder exists, we will use this folder
			        	    			lngFolderIdToUse = subFolder.getId();
			        	    			break;
			        	    		}
			        	    	}
			        	    	
			        	    	//Create a sub folder, if it does not exist
			        	    	if (!doesFolderExist) {
			        	    		Binder lngFolderToUse = FolderUtils.createLibraryFolder(folderObj, strFolderName);
			        	    		lngFolderIdToUse = lngFolderToUse.getId();
			        	    		this.getBinderModule().setDefinitionsInherited(lngFolderIdToUse, true);
			        	    	}
	        	    		}
	        	    	}
	        	    	
	        	    	//Using the Folder object that was already present or that was recently created 
	        	    	Folder entryCreationFolder = getFolderModule().getFolder(lngFolderIdToUse);
	        	    	//Checking to see if the folder already contains the file that we are trying to create
	        	    	FolderEntry preExistingEntry = getFolderModule().getLibraryFolderEntryByFileName(entryCreationFolder, strDecodedFileName);
	        	    	
	        	    	//If there is not pre-existing entry - we create a new entry
	        	    	//If there is a pre-existing entry - we modify the entry
	        	    	try {
	        	    		if (preExistingEntry == null) {
		        	    		FolderUtils.createLibraryEntry(entryCreationFolder, strDecodedFileName, myFile.getInputStream(), null, true);
		        	    	} else {
		        	    		FolderUtils.modifyLibraryEntry(preExistingEntry, strDecodedFileName, myFile.getInputStream(), null, true);
		        	    	}
		        	    	intFileCount++;
	        	    	} catch(WriteFilesException e) {
	        	    		response.setRenderParameter(WebKeys.FILE_PROCESSING_ERRORS, e.getMessage());
	        	    		blnCheckForAppletFile = false;
	        	    		break;
	        	    	}
	        		} else {
	        			//We will set this boolean value to false to exit from the while loop
	        			blnCheckForAppletFile = false;
	        		}
	        	}
				
			} else if (isLibraryBinder.equalsIgnoreCase("false")) {
				//If the folder is a non-library folder, we need to take one attached file at a time and 
				//for each attached file we will create a entry and add the attached file to the entry. 
				String nameValue = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER;
	        	boolean blnCheckForAppletFile = true;
	        	int intFileCount = 1;

	        	while (blnCheckForAppletFile) {
	        		Map oneFileMap = new HashMap();
	        		Map entryNameOnly = new HashMap();
	        		
	        		String fileEleName = nameValue + Integer.toString(intFileCount);
	        		if (fileMap.containsKey(fileEleName)) {
	        	    	MultipartFile myFile = (MultipartFile)fileMap.get(fileEleName);
	        	    	String utf8EncodedFileName = myFile.getOriginalFilename();
	        	    	String utf8DecodedFileName = URLDecoder.decode(utf8EncodedFileName, "UTF-8");
	        	    	java.io.InputStream content = myFile.getInputStream();
	        	    	MultipartFile mf = new SimpleMultipartFile(utf8DecodedFileName, content);
	        	    	intFileCount++;
	        	    	oneFileMap.put(nameValue+"1", mf);
	        	    	entryNameOnly.put(ObjectKeys.FIELD_ENTITY_TITLE, utf8DecodedFileName);
	        	    	MapInputData inputData = new MapInputData(entryNameOnly);
	        	    	try {
		        	    	entryId= getFolderModule().addEntry(folderId, null, inputData, oneFileMap, null).getId();
						} catch(WriteFilesException e) {
				    		response.setRenderParameter(WebKeys.FILE_PROCESSING_ERRORS, e.getMessage());
				    		return;
						}
	        		} else {
	        			blnCheckForAppletFile = false;
	        		}
	        	}
			} else {
				System.out.println("Not able to identify if the Folder is Library or Not");
			}
		} else {
			response.setRenderParameters(formData);
		}
	}
	
	private ArrayList getFolderListWithDecodedValues(String strFolderAndFileName) throws Exception {
		ArrayList arrFolders = new ArrayList();
		String [] strSplitValue = strFolderAndFileName.split("/");
		for (int i = 0; i < strSplitValue.length; i++) {
			String strEncodedValue = strSplitValue[i];
			String strDecodedValue = URLDecoder.decode(strEncodedValue, "UTF-8");
			arrFolders.add(strDecodedValue);
		}
		return arrFolders;
	}
	
	private void setupReloadOpener(ActionResponse response, Long folderId, Long entryId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		if (entryId != null) response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
	}
	private void setupCloseWindow(ActionResponse response) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CLOSE_WINDOW);
	}
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Map model = new HashMap();
			
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		String title = PortletRequestUtils.getStringParameter(request, "title", "");
		model.put(WebKeys.ENTRY_TITLE, title);
		model.put(WebKeys.OPERATION, action);
		
		String blogReply = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BLOG_REPLY, "");
		if (!blogReply.equals("")) model.put(WebKeys.FORM_STYLE_COMPACT, true);
		
		User user = RequestContextHolder.getRequestContext().getUser();
		
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		String path = WebKeys.VIEW_ADD_ENTRY;
		String errorMsg = PortletRequestUtils.getStringParameter(request, WebKeys.ENTRY_DATA_PROCESSING_ERRORS, "");
		String errorMsg2 = PortletRequestUtils.getStringParameter(request, WebKeys.FILE_PROCESSING_ERRORS, "");
		if (errorMsg.equals("")) {
			errorMsg = errorMsg2;
		} else if (!errorMsg.equals("") && !errorMsg2.equals("")) {
			errorMsg += "<br/>" + errorMsg2;
		}
		model.put(WebKeys.FILE_PROCESSING_ERRORS, errorMsg);
		
		if (action.equals(WebKeys.ACTION_ADD_FOLDER_ATTACHMENT)) {
			path="forum/applet_response";
		} else {
			if (!errorMsg.equals("")) {
				model.put(WebKeys.ERROR_MESSAGE, errorMsg);
				path = "forum/reload_opener";
				return new ModelAndView(path, model);
			}
			Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));		
			
			//See if this is an "add entry" or an "add reply" request
			if (action.equals(WebKeys.ACTION_ADD_FOLDER_ENTRY)) {
				Folder folder = getFolderModule().getFolder(folderId);
				//Adding an entry; get the specific definition
				Map folderEntryDefs = DefinitionHelper.getEntryDefsAsMap(folder);
				String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
				model.put(WebKeys.FOLDER, folder);
				model.put(WebKeys.BINDER, folder);
				model.put(WebKeys.ENTRY_DEFINITION_MAP, folderEntryDefs);
				model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_FORM);
				model.put(WebKeys.DEFINITION_ID, entryType);
				//Make sure the requested definition is legal
				if (folderEntryDefs.containsKey(entryType)) {
					Definition currentDef = (Definition)folderEntryDefs.get(entryType);
					DefinitionHelper.getDefinition(currentDef, model, "//item[@type='form']");
					Element familyProperty = (Element) currentDef.getDefinition().getRootElement().selectSingleNode("//properties/property[@name='family']");
					if (familyProperty != null) {
						String family = familyProperty.attributeValue("value", "");
						model.put(WebKeys.DEFINITION_FAMILY, family);
					}
				} else {
					DefinitionHelper.getDefinition(null, model, "//item[@name='entryForm']");
				}
			
				try {
					Workspace ws = getWorkspaceModule().getTopWorkspace();
					model.put(WebKeys.DOM_TREE, getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this, new FolderConfigHelper()),1));
				} catch(AccessControlException e) {}
			
				parseInitialCalendarEventData(model, request);
			} else {
		    	Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
		    	request.setAttribute(WebKeys.URL_ENTRY_ID,entryId.toString());
		    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
		    	Folder folder = entry.getParentFolder();
		    	model.put(WebKeys.FOLDER, folder); 
		    	model.put(WebKeys.ENTRY, entry); 
					
		    	//Get the legal reply types from the parent entry definition
				Document entryView = null;
				Definition entryDefinition = entry.getEntryDef();
				if (entryDefinition != null) {
					entryView = entryDefinition.getDefinition();
					Element familyProperty = (Element) entryView.getRootElement().selectSingleNode("//properties/property[@name='family']");
					if (familyProperty != null) {
						String family = familyProperty.attributeValue("value", "");
						model.put(WebKeys.DEFINITION_FAMILY, family);
					}
				}
				Iterator replyStyles = null;
				if (entryView != null) {
					//See if there is a reply style for this entry definition
					replyStyles = entryView.getRootElement().selectNodes("properties/property[@name='replyStyle']").iterator();
				}
		   	
		    	//Adding an entry; get the specific definition
				Map folderEntryDefs = DefinitionHelper.getEntryDefsAsMap(folder);
		    	String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
		    	model.put(WebKeys.ENTRY_DEFINITION_MAP, folderEntryDefs);
		    	model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_FORM);
		    	
		        //Make sure the requested reply definition is legal
		    	boolean replyStyleIsGood = false;
		    	while (replyStyles.hasNext()) {
		    		if (((String)((Element)replyStyles.next()).attributeValue("value", "")).equals(entryType)) {
		    			replyStyleIsGood = true;
		    			break;
		    		}
		    	}
			    	
				if (replyStyleIsGood) {
					DefinitionHelper.getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@type='form']");
				} else {
					DefinitionHelper.getDefinition(null, model, "//item[@name='entryForm']");
				}
				try {
					Workspace ws = getWorkspaceModule().getTopWorkspace();
					model.put(WebKeys.DOM_TREE, getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this, new FolderConfigHelper()),1));
				} catch(AccessControlException e) {}
			}
		}
		return new ModelAndView(path, model);
	}

	private void parseInitialCalendarEventData(Map model, RenderRequest request) {
		int year = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_YEAR, -1);
		int month = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_MONTH, -1);
		int dayOfMonth = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_DAY_OF_MONTH, -1);

		String time = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DATE_TIME, null);
		int duration = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_TIME_DURATION, 30);
		
		TimeZone timeZone = RequestContextHolder.getRequestContext().getUser().getTimeZone();
		DateTime startDate = null;
		if (year != -1 && month != -1 && dayOfMonth != -1) {
			month++;
		
			startDate = (new DateTime(DateTimeZone.forTimeZone(timeZone))).withYear(year).withMonthOfYear(month).withDayOfMonth(dayOfMonth);
			
			if (time != null && !time.equals("-1")) {
				String[] timeS = time.split(":");
				if (timeS != null) {
					try {
						if (timeS.length > 0) {
							int hour = Integer.parseInt(timeS[0]);
							if (hour != -1) {
								startDate = startDate.withHourOfDay(hour).withMinuteOfHour(0);
							}
						}
						if (timeS.length > 1) {
							int minute = Integer.parseInt(timeS[1]);
							startDate = startDate.withMinuteOfHour(minute > 30 ? 60 - minute : 30 - minute);
						}
					} catch (NumberFormatException e) {
						// do nothing, no hour, no minute
					}
				}
			} else if (time != null && time.equals("-1")) {
				timeZone = null;
			}
		} else {
			startDate = new DateTime(EventsViewHelper.getCalendarCurrentDate(request.getPortletSession()));
			startDate = startDate.plusMinutes(startDate
					.getMinuteOfHour() > 30 ? 60 - startDate
					.getMinuteOfHour() : 30 - startDate
					.getMinuteOfHour());
		}
		
		if (startDate != null) {
			Event event = new Event(startDate.toGregorianCalendar(), new Duration(0, 0, duration, 0), 0);
			event.setTimeZone(timeZone);
			model.put(WebKeys.CALENDAR_INITIAL_EVENT, event);
		}
	}
}
