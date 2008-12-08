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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.Address;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.module.mail.MailSentStatus;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.util.StringUtil;
import org.springframework.web.portlet.ModelAndView;

/**
 * @author Peter Hurley
 *
 */
public class SendEntryMailController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
			String subject = PortletRequestUtils.getStringParameter(request, "subject", "");	
			String[] to = StringUtil.split(PortletRequestUtils.getStringParameter(request, "addresses", ""));
			Set emailAddress = new HashSet();
			for (int i=0; i<to.length; ++i) {
				emailAddress.add(to[i]);				
			}
			boolean self = PortletRequestUtils.getBooleanParameter(request, "self", false);
			String body = PortletRequestUtils.getStringParameter(request, "mailBody", "");
			Set memberIds = new HashSet();
			if (self) memberIds.add(RequestContextHolder.getRequestContext().getUserId());
			if (formData.containsKey("users")) memberIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("users")));
			if (formData.containsKey("groups")) memberIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("groups")));
			Set ccIds = new HashSet();
			if (formData.containsKey("ccusers")) ccIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("ccusers")));
			if (formData.containsKey("ccgroups")) ccIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("ccgroups")));
			Set bccIds = new HashSet();
			if (formData.containsKey("bccusers")) bccIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("bccusers")));
			if (formData.containsKey("bccgroups")) bccIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("bccgroups")));

			boolean sendAttachments = PortletRequestUtils.getBooleanParameter(request, "attachments", false);
			FolderEntry entry  = getFolderModule().getEntry(folderId, entryId);
			
			Map status = getAdminModule().sendMail(entry, memberIds, null, emailAddress, ccIds, bccIds, subject, 
					new Description(body, Description.FORMAT_HTML), sendAttachments);
			
			MailSentStatus result = (MailSentStatus)status.get(ObjectKeys.SENDMAIL_STATUS);
			response.setRenderParameter(WebKeys.EMAIL_SENT_ADDRESSES, getStringEmail(result.getSentTo()));
			response.setRenderParameter(WebKeys.EMAIL_QUEUED_ADDRESSES,  getStringEmail(result.getQueuedToSend()));
			response.setRenderParameter(WebKeys.EMAIL_FAILED_ADDRESSES,  getStringEmail(result.getFailedToSend()));
			List errors = (List)status.get(ObjectKeys.SENDMAIL_ERRORS);
			response.setRenderParameter(WebKeys.ERROR_LIST, (String[])errors.toArray( new String[0]));
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CLOSE_WINDOW);			
		} else {
			response.setRenderParameters(formData);
		}
	}
	private String[] getStringEmail(Collection<Address> addrs) {
		String addresses[] = new String[addrs.size()];
		int i=0;
		for (Address email: addrs) addresses[i++] = email.toString();
		return addresses;
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String [] errors = request.getParameterValues(WebKeys.ERROR_LIST);
		if (errors != null) {
			model.put(WebKeys.ERROR_LIST, errors);
			model.put(WebKeys.EMAIL_SENT_ADDRESSES, request.getParameterValues(WebKeys.EMAIL_SENT_ADDRESSES));
			model.put(WebKeys.EMAIL_QUEUED_ADDRESSES, request.getParameterValues(WebKeys.EMAIL_QUEUED_ADDRESSES));
			model.put(WebKeys.EMAIL_FAILED_ADDRESSES, request.getParameterValues(WebKeys.EMAIL_FAILED_ADDRESSES));
			return new ModelAndView(WebKeys.VIEW_SENDMAIL_RESULT, model);
		}
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));	
		FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
		Binder folder = entry.getParentFolder();
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.BINDER, folder);

		List userIds = PortletRequestUtils.getLongListParameters(request, WebKeys.USER_IDS_TO_ADD);
		try {
			model.put(WebKeys.USERS, getProfileModule().getUsers(new HashSet(userIds)));
		} catch (AccessControlException ex) {} //cannot search for users?
		
		return new ModelAndView(WebKeys.VIEW_BINDER_SENDMAIL, model);
	}

}
