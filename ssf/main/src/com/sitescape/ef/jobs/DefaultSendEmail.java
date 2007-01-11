
package com.sitescape.ef.jobs;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.mail.MailManager;
import com.sitescape.ef.mail.MimeMessagePreparator;
import com.sitescape.ef.repository.RepositoryUtil;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.SpringContextUtil;

/**
 * @author Janet McCann
 *
 */
public class DefaultSendEmail extends SSStatefulJob implements SendEmail {
	protected Log logger = LogFactory.getLog(getClass());

	/* (non-Javadoc)
	 * @see com.sitescape.ef.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	MailManager mail = (MailManager)SpringContextUtil.getBean("mailManager");
		Map message = (Map)jobDataMap.get("mailMessage");
		String name = (String)jobDataMap.get("mailSender");
		Date next = context.getNextFireTime();
		try {
			if (message.containsKey(SendEmail.MIME_MESSAGE)) {
				ByteArrayInputStream ios = new ByteArrayInputStream((byte[])message.get(SendEmail.MIME_MESSAGE));
				//send pre-composed message
				mail.sendMail(name, ios);
			} else {
				MimeHelper helper = new MimeHelper(message);
				mail.sendMail(name, helper);
			} 
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
			context.setResult("Success");
			return;
	   	} catch (MailSendException sx) {
    		logger.error("Error sending mail:" + sx.getMessage());
    	} catch (MailAuthenticationException ax) {
       		logger.error("Authentication Exception:" + ax.getMessage());				
		} catch (Exception ex) {
			//remove job
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJobOnError);
			throw new JobExecutionException(ex);
		}
		//see if we should give up
		if (next == null) {
			//end of schedule
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
			context.setResult("Failed");
		} else {
		//will be rescheduled
		context.setResult("Failed");
		}
    }

    public boolean sendMail(String mailSenderName, Map message, String comment) {
		MimeHelper helper = new MimeHelper(message);
    	MailManager mail = (MailManager)SpringContextUtil.getBean("mailManager");
		try {
			mail.sendMail(mailSenderName, helper);
			return true;
	   	} catch (MailSendException sx) {
    		logger.error("Error sending mail:" + sx.getMessage());
    	} catch (MailAuthenticationException ax) {
       		logger.error("Authentication Exception:" + ax.getMessage());				
    	}
       	// at this point we want to schedule a retry
		Map newMessage = new HashMap();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			helper.getMessage().writeTo(bos);
			newMessage.put(SendEmail.MIME_MESSAGE, bos.toByteArray());
		} catch (MessagingException io) {
			throw new MailPreparationException(NLT.get("errorcode.sendMail.cannotSerialize", new Object[] {io.getLocalizedMessage()}));
		} catch (IOException io) {
			throw new MailPreparationException(NLT.get("errorcode.sendMail.cannotSerialize", new Object[] {io.getLocalizedMessage()}));
		} finally {
			try {
				bos.close();
			} catch (Exception ex) {};
		}
		schedule(mailSenderName, newMessage, comment);
		return false;

    }	
    public void schedule(String mailSenderName, Map message, String comment) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		//each job is new = don't use verify schedule, cause this a unique
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.MINUTE, 1);
		
		//add time to jobName - may have multiple 
	 	String jobName =  "sendMail" + "-" + start.getTime().getTime();
	 	String className = this.getClass().getName();
	  	try {		
			JobDetail jobDetail = new JobDetail(jobName, SEND_MAIL_GROUP, 
					Class.forName(className),false, false, false);
			jobDetail.setDescription(comment);
			JobDataMap data = new JobDataMap();
			data.put("mailSender", mailSenderName);
			data.put("zoneName",RequestContextHolder.getRequestContext().getZoneName());
			data.put("mailMessage", message);
			
			jobDetail.setJobDataMap(data);
			jobDetail.addJobListener(getDefaultCleanupListener());
			//retry every hour
	  		SimpleTrigger trigger = new SimpleTrigger(jobName, SEND_MAIL_GROUP, jobName, SEND_MAIL_GROUP, start.getTime(), null, 24, 1000*60*60);
  			trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
  			trigger.setDescription(comment);
  			trigger.setVolatility(false);
			scheduler.scheduleJob(jobDetail, trigger);				
 		} catch (Exception e) {
   			throw new ConfigurationException("Cannot start (job:group) " + jobName 
   					+ ":" + SEND_MAIL_GROUP, e);
   		}
    }	
    private class MimeHelper implements MimeMessagePreparator {
			MimeMessage message;
			String from;
			Map details;
			
			private MimeHelper(Map details) {
				this.details = details;
			}
			public MimeMessage getMessage() {
				return message;
			}
			public void setDefaultFrom(String from) {
				this.from = from;
			}
			public void prepare(MimeMessage mimeMessage) throws MessagingException {
				//make sure nothing saved yet
				message = null;
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
				helper.setSubject((String)details.get(SendEmail.SUBJECT));
				if (details.containsKey(SendEmail.FROM)) 
					helper.setFrom((InternetAddress)details.get(SendEmail.FROM));
				else
					helper.setFrom(from);
				
				Collection<InternetAddress> addrs = (Collection)details.get(SendEmail.TO);
				for (InternetAddress a : addrs) {
					helper.addTo(a);
				}
				if (addrs.isEmpty()) {
					if (details.containsKey(SendEmail.FROM)) 
						helper.addTo((InternetAddress)details.get(SendEmail.FROM));
					else
						helper.addTo(from);
					helper.setSubject(NLT.get("errorcode.noRecipients") + " " + (String)details.get(SendEmail.SUBJECT));
				}
				String text = (String)details.get(SendEmail.TEXT_MSG);
				if (text == null) text="";
				String html = (String)details.get(SendEmail.HTML_MSG);
				if (html == null) html = "";
				helper.setText(text, html);
				mimeMessage.addHeader("Content-Transfer-Encoding", "8bit");
				Collection<FileAttachment> atts = (Collection)details.get(SendEmail.ATTACHMENTS);
				if (atts != null) {
					for (FileAttachment fAtt : atts) {
						FolderEntry entry = (FolderEntry)fAtt.getOwner().getEntity();
						DataSource ds = RepositoryUtil.getDataSource(fAtt.getRepositoryName(), entry.getParentFolder(), 
								entry, fAtt.getFileItem().getName(), helper.getFileTypeMap());
						
						helper.addAttachment(fAtt.getFileItem().getName(), ds);
					}
				}
	
				//save message incase cannot connect and need to resend;
				message = mimeMessage;
			}

		}
    
}

