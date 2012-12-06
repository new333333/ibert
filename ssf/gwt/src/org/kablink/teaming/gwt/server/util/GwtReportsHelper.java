/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.FilterOutputStream;
import java.text.Collator;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Node;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.domain.LicenseStats;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.rpc.shared.CreateEmailReportCmd.EmailType;
import org.kablink.teaming.gwt.client.rpc.shared.EmailReportRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EmailReportRpcResponseData.EmailItem;
import org.kablink.teaming.gwt.client.rpc.shared.LicenseReportRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.LicenseReportRpcResponseData.LicenseItem;
import org.kablink.teaming.gwt.client.rpc.shared.LicenseReportRpcResponseData.LicenseReleaseInfo;
import org.kablink.teaming.gwt.client.rpc.shared.LicenseReportRpcResponseData.LicenseStatsItem;
import org.kablink.teaming.gwt.client.rpc.shared.ReportsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ReportsInfoRpcResponseData.ReportInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.license.LicenseModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TempFileUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.springframework.util.FileCopyUtils;

/**
 * Helper methods for the GWT reports handling.
 *
 * @author drfoster@novell.com
 */
public class GwtReportsHelper {
	protected static Log m_logger = LogFactory.getLog(GwtReportsHelper.class);

	/*
	 * Inner class used compare two ReportInfo objects.
	 */
	private static class ReportInfoComparator implements Comparator<ReportInfo> {
		private Collator	m_collator;	//
		
		/**
		 * Class constructor.
		 */
		public ReportInfoComparator() {
			m_collator = Collator.getInstance();
			m_collator.setStrength(Collator.IDENTICAL);
		}

	      
		/**
		 * Implements the Comparator.compare() method on two ReportInfo objects.
		 *
		 * Returns:
		 *    -1 if reportInfo1 <  reportInfo2;
		 *     0 if reportInfo1 == reportInfo2; and
		 *     1 if reportInfo1 >  reportInfo2.
		 */
		@Override
		public int compare(ReportInfo reportInfo1, ReportInfo reportInfo2) {
			String s1 = reportInfo1.getTitle();
			if (null == s1) {
				s1 = "";
			}

			String s2 = reportInfo2.getTitle();
			if (null == s2) {
				s2 = "";
			}

			return 	m_collator.compare(s1, s2);
		}
	}

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtReportsHelper() {
		// Nothing to do.
	}

	/**
	 * Creates an email report and returns the results via an
	 * EmailResportRpcResponseData object.
	 * 
	 * @param bs
	 * @param request
	 * @param begin
	 * @param end
	 * @param emailType
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static EmailReportRpcResponseData createEmailReport(AllModulesInjected bs, HttpServletRequest request, Date begin, Date end, EmailType emailType) throws GwtTeamingException {
		try {
			// Construct the EmailReportRpcResponseData object we'll
			// fill in and return.
			EmailReportRpcResponseData reply = new EmailReportRpcResponseData();

			// Read the report information.
			String reportType;
			switch (emailType) {
			default:
			case SENT:      reportType = ReportModule.EMAIL_REPORT_TYPE_SEND;    break;
			case ERROR:     reportType = ReportModule.EMAIL_REPORT_TYPE_ERRORS;  break;
			case RECEIVED:  reportType = ReportModule.EMAIL_REPORT_TYPE_RECEIVE; break;
			}
			List<Map<String, Object>> reportList = bs.getReportModule().generateEmailReport(begin, end, reportType);

			// Did we get any items back?
			if (MiscUtil.hasItems(reportList)) {
				// Yes!  Scan them...
				for (Map<String, Object> nextItem:  reportList) {
					// ...creating an EmailItem for each...
					EmailItem ei = new EmailItem();
					ei.setAttachedFiles(getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_ATTACHED_FILES));
					ei.setFrom(         getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_FROM_ADDRESS  ));
					ei.setComment(      getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_COMMENT       ));
					ei.setLogStatus(    getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_STATUS        ));
					ei.setLogType(      getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_TYPE          ));
					ei.setSendDate(     getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_SEND_DATE     ));
					ei.setSubject(      getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_SUBJECT       ));
					ei.setToAddresses(  getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_TO_ADDRESSES  ));
					
					// ...that gets added to the response data.
					reply.addEmailItem(ei);
				}
			}
			
			// If we get here, reply refers to the 
			// EmailReportRpcResponseData object containing the results
			// of the report.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtReportsHelper.createEmailReport( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtServerHelper.getGwtTeamingException(ex);
		}		
	}
	
	/**
	 * Creates a license report and returns the results via a
	 * LicenseResportRpcResponseData object.
	 * 
	 * The logic for this method was copied from
	 * LicenseReportController.populateModel().
	 * 
	 * @param bs
	 * @param request
	 * @param begin
	 * @param end
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static LicenseReportRpcResponseData createLicenseReport(AllModulesInjected bs, HttpServletRequest request, Date begin, Date end) throws GwtTeamingException {
		try {
			// Construct the LicenseReportRpcResponseData object we'll
			// fill in and return.
			Date currentDate = new Date();
			LicenseReportRpcResponseData reply = new LicenseReportRpcResponseData();
			reply.setBeginDate(      GwtServerHelper.getDateString(    begin)                                           );
			reply.setEndDate(        GwtServerHelper.getDateString(    end)                                             );
			reply.setReportDate(     GwtServerHelper.getDateTimeString(currentDate, DateFormat.MEDIUM, DateFormat.SHORT));

			Date originalEndDate = null;
			if (null != end) {
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(end);
				
				// Remember the original end date supplied by the user.
				originalEndDate = cal.getTime();
				
				// Add 1 day to the end date.
				cal.add(Calendar.DATE, 1);
				end = cal.getTime();
			}
			
			// There is a job that runs once a day that counts the
			// number of users in Filr/Vibe and writes this information
			// into the SS_LicenseStats table.  If the end date of the
			// report equals today, do a count right now and write the
			// count into the SS_LicenseStats table so the information
			// returned to the user is up-to-date.
			//
			// Is the end date of the report equal to today?
			LicenseModule lm = bs.getLicenseModule();
			if ((null != originalEndDate) && (null != currentDate)) {
				Calendar			todayCal = GregorianCalendar.getInstance();
				GregorianCalendar	endCal   = new GregorianCalendar();
				endCal.setTime(originalEndDate);
				if (endCal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH)) {
					if (endCal.get(Calendar.DAY_OF_MONTH) == todayCal.get(Calendar.DAY_OF_MONTH)) {
						if (endCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR)) {
							// If we get here then the end date of the
							// report is today.  Count the users in
							// Filr/Vibe so the data we return is up-to
							// date.
							lm.recordCurrentUsage();
						}
					}
				}
			}

			// Are there any license statistics to report on?
			ReportModule 		rm           = bs.getReportModule();
			List<LicenseStats>	licenseStats = rm.generateLicenseReport(begin, end);
			if (MiscUtil.hasItems(licenseStats)) {
				// Yes!  Scan them...
				for (LicenseStats ls:  licenseStats) {
					// ...adding a LicenseStatsItem to the reply for
					// ...each.
					LicenseStatsItem lsi = new LicenseStatsItem();
					
					lsi.setId(               ls.getId()                                         );
				    lsi.setSnapshotDate(     GwtServerHelper.getDateString(ls.getSnapshotDate()));
					lsi.setInternalUserCount(ls.getInternalUserCount()                          );
					lsi.setExternalUserCount(ls.getExternalUserCount()                          );
					lsi.setOpenIdUserCount(  ls.getOpenIdUserCount()                            );
					lsi.setActiveUserCount(  ls.getActiveUserCount()                            );
					lsi.setCheckSum(         ls.getChecksum()                                   );
					
					reply.addLicenseStats(lsi);
				}
			}

			// Scan the installed licenses...
			StringBuffer		uids   = new StringBuffer();
			TimeZone			gmt    = TimeZoneHelper.getTimeZone("GMT");
			DateTimeFormatter	dtf    = DateTimeFormat.forPattern("yyyy-MM-dd");
			dtf = dtf.withOffsetParsed();
			for(Document doc:  lm.getLicenses()) {
				// ...adding a LicenseItem to the reply for each.
				LicenseItem li = new LicenseItem();

				// Track this license's UID.
				uids.append(getLicenseValue(doc, "//KeyInfo/@uid") + " ");
				
				// Get the date issued from the license XML and convert
				// it to a Date object.
				Date	tmpDate    = null;
				String	tmpDateStr = getLicenseValue(doc, "//KeyInfo/@issued");
				try {
					tmpDate = dtf.parseDateTime(tmpDateStr).toDate();
				}
				catch (Exception ex) {
					// This should never happen.
				}
				if (null == tmpDate) {
					// This should never happen.
					tmpDate = new Date();
				}
				li.setIssued(GwtServerHelper.getDateString(tmpDate, DateFormat.MEDIUM, gmt));
				
				// Get the license start date.
				dtf     = DateTimeFormat.forPattern("MM/dd/yyyy");
				dtf     = dtf.withOffsetParsed();
				tmpDate = null;
				tmpDateStr = getLicenseValue(doc, "//Dates/@effective");
				try {
					tmpDate = dtf.parseDateTime(tmpDateStr).toDate();
				}
				catch (Exception ex) {
					// This should never happen.
				}
				if (null == tmpDate) {
					// This should never happen.
					tmpDate = new Date();
				}
				li.setEffectiveStart(GwtServerHelper.getDateString(tmpDate, DateFormat.MEDIUM, gmt));

				// Get the license end date.
				tmpDate    = null;
				tmpDateStr = getLicenseValue(doc, "//Dates/@expiration");
				try {
					tmpDate = dtf.parseDateTime(tmpDateStr).toDate();
				}
				catch (Exception ex) {
					// This should never happen.
				}
				if (null == tmpDate) {
					// This should never happen.
					tmpDate = new Date();
				}
				li.setEffectiveEnd(GwtServerHelper.getDateString(tmpDate, DateFormat.MEDIUM, gmt));

				li.setContact(getLicenseValue(       doc, "//AuditPolicy/ReportContact"));
				li.setProductTitle(getLicenseValue(  doc, "//Product/@title")           );
				li.setProductVersion(getLicenseValue(doc, "//Product/@version")         );
				
				reply.addLicense(li);
			}

			// Store the remaining data in the reply.
			LicenseReleaseInfo lri = new LicenseReleaseInfo();
			lri.setName(       ReleaseInfo.getName()       );
			lri.setReleaseInfo(ReleaseInfo.getReleaseInfo());
			lri.setVersion(    ReleaseInfo.getVersion()    );
			
			reply.setReleaseInfo(    lri                    );
			reply.setLicenseKey(     uids.toString()        );
			reply.setRegisteredUsers(lm.getRegisteredUsers());
			reply.setExternalUsers(  lm.getExternalUsers()  );
			
			
			// If we get here, reply refers to the 
			// LicenseReportRpcResponseData object containing the results
			// of the report.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtReportsHelper.createLicenseReport( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtServerHelper.getGwtTeamingException(ex);
		}		
	}
	
	/*
	 * Copied from LicenseReportController.getValue().
	 */
	private static String getLicenseValue(Document doc, String xpath) {
		Node node = null;
		return ((doc != null && (node = doc.selectSingleNode(xpath)) != null) ? node.getText() : "");
	}
	
	/**
	 * Returns a ReportsInfoRpcResponseData object containing the
	 * information for running reports.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ReportsInfoRpcResponseData getReportsInfo(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			// Construct the ReportsInfoRpcResponseData object we'll
			// fill in and return.
			ReportsInfoRpcResponseData reply = new ReportsInfoRpcResponseData();
			
			// Add the reports that all administrators have access to.
			reply.addReport(new ReportInfo(AdminAction.REPORT_ACTIVITY_BY_USER,              NLT.get("administration.report.title.activityByUser"))     );
			reply.addReport(new ReportInfo(AdminAction.REPORT_DATA_QUOTA_EXCEEDED,           NLT.get("administration.report.title.disk_quota_exceeded")));
			reply.addReport(new ReportInfo(AdminAction.REPORT_DATA_QUOTA_HIGHWATER_EXCEEDED, NLT.get("administration.report.title.highwater_exceeded")) );
			reply.addReport(new ReportInfo(AdminAction.REPORT_DISK_USAGE,                    NLT.get("administration.report.title.quota"))              );
			reply.addReport(new ReportInfo(AdminAction.REPORT_EMAIL,                         NLT.get("administration.report.title.email"))              );
			reply.addReport(new ReportInfo(AdminAction.REPORT_LICENSE,                       NLT.get("administration.report.title.license"))            );
			reply.addReport(new ReportInfo(AdminAction.REPORT_LOGIN,                         NLT.get("administration.report.title.login"))              );			
			reply.addReport(new ReportInfo(AdminAction.REPORT_USER_ACCESS,                   NLT.get("administration.report.title.user_access"))        );
			reply.addReport(new ReportInfo(AdminAction.REPORT_VIEW_CREDITS,                  NLT.get("administration.credits"))                         );
			reply.addReport(new ReportInfo(AdminAction.REPORT_XSS,                           NLT.get("administration.report.title.xss", "XSS Report"))  );
			
			// Does the user have rights to run 'Content Modification
			// Log Report'?
			AdminModule	am     = bs.getAdminModule();
			boolean		isFilr = Utils.checkIfFilr();
			if ((!isFilr) && am.testAccess( AdminOperation.manageFunction)) {
				// Yes!  Add that.
				reply.addReport(
					new ReportInfo(
						AdminAction.REPORT_VIEW_CHANGELOG,
						NLT.get("administration.view_change_log")));
			}
			
			// Does the user have rights to run the 'System error logs'
			// report?
			if (am.testAccess( AdminOperation.manageErrorLogs)) {
				// Yes!  Add that.
				reply.addReport(
					new ReportInfo(
						AdminAction.REPORT_VIEW_SYSTEM_ERROR_LOG,
						NLT.get("administration.system_error_logs")));
			}

			// Sort the reports we've got by title.
			ReportInfoComparator ric = new ReportInfoComparator();
			Collections.sort(reply.getReports(), ric);
			
			// If we get here, reply refers to the 
			// ReportsInfoRpcResponseData object containing the
			// information about the reports.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtReportsHelper.getReportsInfo( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtServerHelper.getGwtTeamingException(ex);
		}		
	}

	/*
	 * Extracts a non-null string from an email report map.
	 */
	private static String getStringFromEmailReportMap(Map<String, Object> reportMap, String key) {
		String reply = ((String) reportMap.get(key));
		if (reply == null) {
			reply = "";
		}
		return reply;
	}
	
	/**
	 * Returns a URL to download the system error log.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static String getSystemErrorLogUrl(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			// Generate a ZIP file containing the system error logs...
			File				tempFile = TempFileUtil.createTempFile("logfiles");
			FileOutputStream	fo       = new FileOutputStream(tempFile);
			ZipOutputStream		zipOut   = new ZipOutputStream(fo);
			FilterOutputStream	wrapper  = new FilterOutputStream(zipOut) {
				@Override
				public void close() {}  // FileCopyUtils will try to close this too soon
			};
			File logDirectory = new File(SpringContextUtil.getServletContext().getRealPath("/WEB-INF/logs"));
			for (String logFile : logDirectory.list(
					new FilenameFilter() {
						@Override
						public boolean accept(File file, String filename) {
							return filename.startsWith("ssf.log");
						}
					})) {
				zipOut.putNextEntry(new ZipEntry(logFile));
				FileCopyUtils.copy(new FileInputStream(new File(logDirectory, logFile)), wrapper);
			}
			zipOut.finish();

			// ...and return a link to it.  
			return (
				WebUrlUtil.getServletRootURL(request)          +
				WebKeys.SERVLET_VIEW_FILE                      + "?" +
				"viewType=zipped&fileId=" + tempFile.getName() + "&" +
				WebKeys.URL_FILE_TITLE    + "=logfiles.zip");
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtReportsHelper.getSystemErrorLogUrl( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtServerHelper.getGwtTeamingException(ex);
		}		
	}
}
