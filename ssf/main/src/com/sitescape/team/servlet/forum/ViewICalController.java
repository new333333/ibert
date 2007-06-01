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
package com.sitescape.team.servlet.forum;

import java.io.OutputStream;
import java.security.Principal;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;

import org.springframework.web.servlet.ModelAndView;
import javax.activation.FileTypeMap;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.ical.impl.IcalConverterImpl;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.rss.RssGenerator;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.util.XmlFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.PrincipalServletRequest;
import com.sitescape.team.web.servlet.SAbstractController;
import com.sitescape.util.FileUtil;

import org.springframework.web.bind.RequestUtils;

public class ViewICalController extends SAbstractController {
	
	private MailModule mailModule;
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		Long binderId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		Long entryId = new Long(RequestUtils.getRequiredStringParameter(request, WebKeys.URL_ENTRY_ID));
		Map folderEntries = getFolderModule().getEntryTree(binderId, entryId);
		FolderEntry entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
		
		User user = (User)request.getUserPrincipal();
		
		if(user == null) {
			// The request object has no information about authenticated user.
			// Note: It means that this is not a request made by the portal
			// through cross-context dispatch targeted to a SSF portlet. 
			HttpSession ses = request.getSession(false);

			if(ses != null) {
				user = (User) ses.getAttribute(WebKeys.USER_PRINCIPAL);
				
				if (user == null) {
					// No principal object is cached in the session.
					// Note: This occurs when a SSF web component (either a servlet
					// or an adapted portlet) is accessed BEFORE at least one SSF
					// portlet is invoked  by the portal through regular cross-context
					// dispatch. 
					user = RequestContextHolder.getRequestContext().getUser();
				}
			}
			else {
				throw new ServletException("No session in place - Illegal request sequence.");
			}
		}
		

		if (getFolderModule().testAccess(entry, "getEntry")) {
			response.resetBuffer();
			response.setContentType("text/calendar; charset=" + XmlFileUtil.FILE_ENCODING);
			response.setHeader("Cache-Control", "private");
			response.setHeader("Pragma", "no-cache");
			
			CalendarOutputter calendarOutputter = new CalendarOutputter();
			Calendar calendar = getIcalConverter().generate(entry, entry.getEvents(), mailModule.getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.DEFAULT_TIMEZONE));
			calendarOutputter.output(calendar, response.getWriter());
		}	
		
		response.flushBuffer();

		return null;
	}
	
	public MailModule getMailModule() {
		return mailModule;
	}
	public void setMailModule(MailModule mailModule) {
		this.mailModule = mailModule;
	}
}
