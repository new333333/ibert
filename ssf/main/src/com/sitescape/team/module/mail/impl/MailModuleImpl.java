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

package com.sitescape.team.module.mail.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.OrTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;

import org.dom4j.Element;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiAccessor;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.NotifyStatus;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.jobs.FillEmailSubscription;
import com.sitescape.team.jobs.SendEmail;
import com.sitescape.team.jobs.ZoneSchedule;
import com.sitescape.team.module.definition.notify.Notify;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.mail.FolderEmailFormatter;
import com.sitescape.team.module.mail.FolderEmailPoster;
import com.sitescape.team.module.mail.JavaMailSender;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.module.mail.MimeMapPreparator;
import com.sitescape.team.module.mail.MimeMessagePreparator;
import com.sitescape.team.module.mail.MimeNotifyPreparator;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.PortabilityUtil;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.util.Validator;
/**
 * The public methods exposed by this implementation are not transaction 
 * demarcated. If transactions are needed, the FolderEmailProcessors will be 
 * responsible.
 * Of course, this finer granularity transactional control will be of no effect
 * if the caller of this service was already transactional (i.e., it controls
 * transaction boundary that is more coarse). Whenever possible, this practise 
 * is discouraged for obvious performance/scalability reasons.  
 *   
 * @author Janet McCann
 *
 */
public class MailModuleImpl extends CommonDependencyInjection implements MailModule, ZoneSchedule, InitializingBean {
	protected ConcurrentHashMap<String, List> mailPosters = new ConcurrentHashMap();
	protected Map<String, JavaMailSender> mailSenders = new HashMap();
	protected JavaMailSender mailSender;
	protected JndiAccessor jndiAccessor;
	protected Map defaultProps = new HashMap();
	private String mailRootDir;
	protected boolean useAliases=false;
	protected boolean sendVTODO = true;
	protected int rcptToLimit = 500;
//	protected Map<String,String> mailAccounts = new TreeMap(String.CASE_INSENSITIVE_ORDER);
	public MailModuleImpl() {
		defaultProps.put(MailModule.Property.POSTING_JOB.getKey(), "com.sitescape.team.jobs.DefaultEmailPosting");
		defaultProps.put(MailModule.Property.NOTIFICATION_JOB.getKey(), "com.sitescape.team.jobs.DefaultEmailNotification");
	 	defaultProps.put(MailModule.Property.SUBSCRIPTION_JOB.getKey(), "com.sitescape.team.jobs.DefaultFillEmailSubscription");
		defaultProps.put(MailModule.Property.NOTIFY_TEMPLATE_TEXT.getKey(), "mailText.xslt");
		defaultProps.put(MailModule.Property.NOTIFY_TEMPLATE_HTML.getKey(), "mailHtml.xslt");
		defaultProps.put(MailModule.Property.NOTIFY_TEMPLATE_CACHE_DISABLED.getKey(), "false");
	}

	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	
	private IcalModule icalModule;
	public IcalModule getIcalModule() {
		return icalModule;
	}
	public void setIcalModule(IcalModule icalModule) {
		this.icalModule = icalModule;
	}	

	public String getMailRootDir() {
		return mailRootDir;
	}

	public void setMailRootDir(String mailRootDir) {
		if(mailRootDir.endsWith(Constants.SLASH))
			this.mailRootDir = mailRootDir;
		else
			this.mailRootDir = mailRootDir + Constants.SLASH;
	}

	public void setJndiAccessor(JndiAccessor jndiAccessor) {
		this.jndiAccessor = jndiAccessor;
	}
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	/**
	 * Called after bean is initialized.  
	 */
	public void afterPropertiesSet() {
		//Get alias setting
		useAliases = SPropsUtil.getBoolean("mail.posting.useAliases", false);
		//Get send tasks in email
		sendVTODO = SPropsUtil.getBoolean("mail.sendVTODO", true);
		rcptToLimit = SPropsUtil.getInt("mail.rcpt.limit", 500);
		//preload mailSenders so retry mail will work.  Needs name available.  Posters are also senders, when replying on failures.
		List<Element> senders = SZoneConfig.getAllElements("//mailConfiguration/notify | //mailConfiguration/posting");
		for (Element sEle:senders) {
			String jndiName = sEle.attributeValue("session");
			if (Validator.isNotNull(jndiName)) {
				try {
					jndiName = PortabilityUtil.getJndiName(jndiName);
					JavaMailSender sender = (JavaMailSender)mailSender.getClass().newInstance();				
					SpringContextUtil.applyDependencies(sender, "mailSender");
					Object jndiObj = jndiAccessor.getJndiTemplate().lookup(jndiName);
					
					//System.out.println(ClassLoaderUtils.showClassLoaderHierarchy(jndiObj, "jndi")); // $$$ TODO
					//System.out.println(ClassLoaderUtils.showClassLoaderHierarchy(javax.mail.Session.class.getClassLoader()));
					
					sender.setSession((javax.mail.Session) jndiObj);
					sender.setName(jndiName);		
					mailSenders.put(jndiName, sender);
				
				} catch (Exception ex) {
					logger.error("Error locating " + jndiName + " " + getMessage(ex));
				}
			}		
		}

		//get mail posting accounts - possible alternative to storing passwords in database
/*		List<Element>accounts = SZoneConfig.getAllElements("//mailConfiguration//account");
		for (Element sEle:accounts) {
			String account = sEle.attributeValue("name");
			if (Validator.isNotNull(account)) {
				String pwd = sEle.getText();
				if ((pwd != null) && (pwd.length() != 0)) { //don't know if blanks allowed??
					mailAccounts.put(account, pwd);
				}
			}			
		}
*/	

	}

	protected FillEmailSubscription getSubscriptionJob(Workspace zone) {
    	String jobClass = getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.SUBSCRIPTION_JOB);
    	try {
    		Class processorClass = ReflectHelper.classForName(jobClass);
    		FillEmailSubscription job = (FillEmailSubscription)processorClass.newInstance();
    		return job;
    	} catch (ClassNotFoundException e) {
    		throw new ConfigurationException(
    				"Invalid FillEmailSubscription class name '" + jobClass + "'", e);
    	} catch (InstantiationException e) {
    		throw new ConfigurationException(
    				"Cannot instantiate FillEmailSubscription of type '" + jobClass + "'");
    	} catch (IllegalAccessException e) {
    		throw new ConfigurationException(
	 				"Cannot instantiate FillEmailSubscription of type '" + jobClass + "'");
    	} 
	}
	protected SendEmail getEmailJob(Workspace zone) {
    	String jobClass = getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.SUBSCRIPTION_JOB);
    	try {
    		Class processorClass = ReflectHelper.classForName(jobClass);
    		SendEmail job = (SendEmail)processorClass.newInstance();
    		return job;
    	} catch (ClassNotFoundException e) {
    		throw new ConfigurationException(
    				"Invalid FillEmailSubscription class name '" + jobClass + "'", e);
    	} catch (InstantiationException e) {
    		throw new ConfigurationException(
    				"Cannot instantiate FillEmailSubscription of type '" + jobClass + "'");
    	} catch (IllegalAccessException e) {
    		throw new ConfigurationException(
	 				"Cannot instantiate FillEmailSubscription of type '" + jobClass + "'");
    	} 
	}
	//called on zone delete
	public void stopScheduledJobs(Workspace zone) {
		FillEmailSubscription sub = getSubscriptionJob(zone);
		sub.remove(zone.getId());
	}
	//called on zone startup
	public void startScheduledJobs(Workspace zone) {
		if (zone.isDeleted()) return;
		//make sure a delete job is scheduled for the zone
		FillEmailSubscription sub = getSubscriptionJob(zone);
		String minString = (String)getMailProperty(zone.getName(), MailModule.Property.SUBSCRIPTION_MINUTES);
		int minutes = 5;
		try {
			minutes = Integer.parseInt(minString);
		} catch (Exception ex) {};
		sub.schedule(zone.getId(), null, minutes);
	}

	public File getMailDirPath(Binder binder) {
		return new File(new StringBuffer(mailRootDir).append(RequestContextHolder.getRequestContext().getZoneName()).append(File.separator).append(binder.getId().toString()).append(File.separator).toString());
	}
	public String getMailProperty(String zoneName, MailModule.Property property) {
		return getMailProperty(zoneName, property.getKey());
	}
	public String getMailProperty(String zoneName, String name) {
		String val = SZoneConfig.getString(zoneName, "mailConfiguration/property[@name='" + name + "']");
		if (Validator.isNull(val)) {
			val = (String)defaultProps.get(name);
		}
		return val;
	}
	public String getMailAttribute(String zoneName, String node, String name) {
		Element result = SZoneConfig.getElement(zoneName, "mailConfiguration/" + node);
		if (result == null) return null;
		return result.attributeValue(name);
	}

// Think this is overkill
//	public String getMailAttribute(Binder binder, String node, String name) {
//		String result = getMailAttribute(RequestContextHolder.getRequestContext().getZoneName(), "binder[@id='" + binder.getId().toString() +"']/" + node, name);
//		if (result != null) return result;
//		if (!binder.isRoot()) return getMailAttribute(binder.getParentBinder(), node, name);
//		return getMailAttribute(RequestContextHolder.getRequestContext().getZoneName(), node, name);

//	}
	protected JavaMailSender getMailSender(String jndiName) {
		JavaMailSender sender=null;
		sender = mailSenders.get(jndiName);
		if (sender == null) throw new ConfigurationException("Missing JavaMailSender bean");
		return sender;
	}
	public JavaMailSender getMailSender(Binder binder) {
		String jndiName;
		if (binder.isZone()) jndiName = PortabilityUtil.getJndiName(getMailAttribute(binder.getName(), "notify", "session"));
		else jndiName = PortabilityUtil.getJndiName(getMailAttribute(RequestContextHolder.getRequestContext().getZoneName(), "notify", "session"));
//		else jndiName = PortabilityUtil.getJndiName(getMailAttribute(binder, "notify", "session"));
		return getMailSender(jndiName);
	}


	protected List<String> getMailPosters(String zoneName) {
		//posting map is indexed by zoneName.  
		List<String> result = mailPosters.get(zoneName);
	    if (result != null) return result;
		List<Element> posters = SZoneConfig.getElements("mailConfiguration/posting");
		if (posters == null) posters = new ArrayList();
		result = new ArrayList();
		for (Element nElement:posters) {
			String jndiName = PortabilityUtil.getJndiName(nElement.attributeValue("session"));
			if (mailSenders.containsKey(jndiName)) {
				result.add(jndiName); 
			} else {
				logger.error("Error locating mail poster " + jndiName);
			}
		}
		mailPosters.put(zoneName, result);
		return result;
	}
	/**
	 * Read mail from all incoming mail servers.
	 *
	 */
	public void receivePostings() {
		String prefix, auth;
		List<String> posters = getMailPosters(RequestContextHolder.getRequestContext().getZoneName());
		List<PostingDef> allPostings = getCoreDao().loadPostings(RequestContextHolder.getRequestContext().getZoneId());
		/* There are 3 types of posting
		 * 1. Using aliases, where mail is sent to different address which are aliases for the address configured in ssf.xml
		 * 	  This works as long as the mail server doesn't convert the alias to the real email address, as does GroupWise!
		 * 2. Each posting definition defines a userName/password that is used to connect to the store as configured in ssf.xml
		 * 3. There is one connect as configured in ssf.xml that has the rights to read the folders of different users.
		 */
		List<PostingDef>aliases = new ArrayList();
		List<PostingDef>useUserNamePwd = new ArrayList();
		List<PostingDef>useUserName = new ArrayList();
		for (PostingDef p:allPostings) {
			if (!p.isEnabled()) continue;
			if (p.getBinder() == null) continue;
			if (useAliases) aliases.add(p);
			else if (Validator.isNull(p.getPassword())) useUserName.add(p);
			else useUserNamePwd.add(p);
		}
		
		SearchTerm[] aliasSearch = new SearchTerm[2];
		
		for (String jndiName:posters) {  //multiple aren't tested
			JavaMailSender sender;
			try {
				sender = getMailSender(jndiName);
			} catch (Exception ex) {
				continue;
			}
			Session session = sender.getSession();
			String protocol = session.getProperty("mail.store.protocol");
			// see if need password
			prefix = "mail." + protocol + ".";
			auth = session.getProperty(prefix + "auth");
			if (Validator.isNull(auth)) 
				auth = session.getProperty("mail.auth");
			String password=null;
			if ("true".equals(auth)) {
				password = session.getProperty(prefix + "password");
				if (Validator.isNull(password)) 
					password = session.getProperty("mail.password");
			}
			String hostName = session.getProperty(prefix + "host");
			if (Validator.isNull(hostName)) {
				hostName = session.getProperty("mail.host");
			}

			javax.mail.Folder mFolder=null;
			Store store=null;
			try {				
				store = session.getStore(protocol);
			} catch (Exception ex) {
				logger.error("Error posting mail from [" + hostName + "]", ex);
				continue;
			}
			try {				
				if (!aliases.isEmpty() || !useUserName.isEmpty()) {
					if (Validator.isNotNull(password)) {
						//rest of defaults from jndi setting
						store.connect(null, null, password);
					} else {
						store.connect();
					}
					if (!aliases.isEmpty()) {
						mFolder = store.getFolder("inbox");				
						mFolder.open(javax.mail.Folder.READ_WRITE);
					
						//	determine which alias a message belongs to and post it
						for (PostingDef postingDef: aliases) {
							try {
								aliasSearch[0] = new RecipientStringTerm(Message.RecipientType.TO,postingDef.getEmailAddress());
								aliasSearch[1] = new RecipientStringTerm(Message.RecipientType.CC,postingDef.getEmailAddress());
								Message aliasMsgs[]=mFolder.search(new OrTerm(aliasSearch));
								if (aliasMsgs.length == 0) continue;
								Folder folder = (Folder)postingDef.getBinder();
								FolderEmailPoster processor = (FolderEmailPoster)processorManager.getProcessor(folder,FolderEmailPoster.PROCESSOR_KEY);
								sendErrors(folder, postingDef, sender, processor.postMessages(folder,postingDef, aliasMsgs, session));
							} catch (Exception ex) {
								logger.error("Error posting mail from [" + hostName + "]"+postingDef.getEmailAddress(), ex);
							}
						}				
						try {mFolder.close(true);} catch (Exception ex) {};
					}
					//now see if we have a privledged account
					for (PostingDef postingDef: useUserName) {
						try {
							javax.mail.Folder[] mailFolders = store.getUserNamespaces(postingDef.getEmailAddress());
							if (mailFolders.length == 0) {
								logger.info("Cannot read mail box for " + postingDef.getEmailAddress());
								continue;
							}
							Folder folder = (Folder)postingDef.getBinder();
							FolderEmailPoster processor = (FolderEmailPoster)processorManager.getProcessor(folder,FolderEmailPoster.PROCESSOR_KEY);
							for (int j=0; j<mailFolders.length; ++j) {
								mFolder = mailFolders[j];
								if ("inbox".equals(mFolder.getFullName())) {
									try {
										mFolder.open(javax.mail.Folder.READ_WRITE);
										sendErrors(folder, postingDef,  sender, processor.postMessages(folder, postingDef, mFolder.getMessages(), session));							
									} finally {
										mFolder.close(true);
									}
								}						
							}
						} catch (Exception ex) {
							logger.error("Error posting mail from [" + hostName + "]"+postingDef.getEmailAddress(), ex);
						}
					}	
				}
					
			} catch (AuthenticationFailedException ax) {
				logger.error("Error posting mail from [" + hostName + "] " + getMessage(ax));
				continue;
			} catch (Exception ex) {
				//Close connection and expunge
				if (mFolder != null) try {mFolder.close(true);} catch (Exception ex1) {};
				logger.error("Error posting mail from [" + hostName + "]", ex);
			} finally  {
				//Close connection 
				if (store != null) try {store.close();} catch (Exception ex) {};
			}
			//Now try connecting by user/password
			for (PostingDef postingDef: useUserNamePwd) {
				mFolder = null;
				Folder folder = (Folder)postingDef.getBinder();
				FolderEmailPoster processor = (FolderEmailPoster)processorManager.getProcessor(folder,FolderEmailPoster.PROCESSOR_KEY);
				try {
					store.connect(null, postingDef.getEmailAddress(), postingDef.getPassword());
					mFolder = store.getFolder("inbox");				
					mFolder.open(javax.mail.Folder.READ_WRITE);
					sendErrors(folder, postingDef, sender, processor.postMessages(folder, postingDef, mFolder.getMessages(), session));							
				} catch (AuthenticationFailedException ax) {
					logger.error("Error posting mail from [" + hostName + "]"+postingDef.getEmailAddress() + " " + getMessage(ax));
					continue;
				} catch (Exception ex) {
					logger.error("Error posting mail from [" + hostName + "]"+postingDef.getEmailAddress(), ex);
					continue;
				} finally {
					if (mFolder != null) try {mFolder.close(true);} catch (Exception ex) {};
					try {store.close();} catch (Exception ex) {};
				}
				
			}

		}		
		
	}
	private String getMessage(Exception ex) {
		if (Validator.isNotNull(ex.getLocalizedMessage())) return ex.getLocalizedMessage();
		return ex.getMessage();
	}
	private void sendErrors(Binder binder, PostingDef postingDef, JavaMailSender srcSender, List errors) {
		if (!errors.isEmpty()) {
			try	{
				JavaMailSender sender;
				if (!useAliases && !Validator.isNull(postingDef.getPassword()))  {
					//need our own sender, so we can change the username/password
					 sender = (JavaMailSender)mailSender.getClass().newInstance();				
					 SpringContextUtil.applyDependencies(sender, "mailSender");	
					 sender.setSession(srcSender.getSession(), postingDef.getEmailAddress(), postingDef.getPassword());
				} else {
					sender = srcSender;
				}
				sender.send((MimeMessage[])errors.toArray(new MimeMessage[errors.size()]));
			} catch (MailAuthenticationException ax) {
				logger.error("Authentication Exception:" + getMessage(ax));
			} catch (MailSendException ms) {
				logger.error("Error sending posting reject:" + getMessage(ms));
				Map failures = ms.getFailedMessages();
				if ((binder != null) && !failures.isEmpty()) {
					SendEmail job = getEmailJob(RequestContextHolder.getRequestContext().getZone());
					for (Iterator iter=failures.entrySet().iterator(); iter.hasNext();) {
						Map.Entry me = (Map.Entry)iter.next();
						if (!useAliases && !Validator.isNull(postingDef.getPassword()))  {
							job.schedule(srcSender, postingDef.getEmailAddress(), postingDef.getPassword(), (MimeMessage)me.getKey(), binder.getTitle(), getMailDirPath(binder), false);
						} else {
							job.schedule(srcSender, (MimeMessage)me.getKey(), binder.getTitle(), getMailDirPath(binder), false);							
						}
						logger.error("Error sending posting reject:" + getMessage((Exception)me.getValue()));						
					}
				}
			} catch (Exception ex) {
				logger.error("Error sending posting reject:" + getMessage(ex));
			}
		}
		
	}
    /*
     * Fill any subscriptions to modified entries.  Handles user requested subscriptions and administrator notifications that are not digest.
     * Begin represents the last time we got through this without errors and used as a query optimization.
     */
	public Date fillSubscriptions(Date begin)  {
		final String updateString="update com.sitescape.team.domain.NotifyStatus set lastFullSent=:p1 where ownerId in (:p2)";
   		final Map values = new HashMap();
		Object ctx = null;
		Date end = new Date();
		values.put("p1", end);
		Map params = new HashMap();
		JavaMailSender mailSender = getMailSender(RequestContextHolder.getRequestContext().getZone());
		try {
			ctx = mailSender.initializeConnection();
			Folder currentFolder = null;
			List<Subscription> folderSubscriptions = null;
			FolderEmailFormatter processor=null;
			MimeNotifyPreparator mHelper = null;
			String timeZone = getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.DEFAULT_TIMEZONE);
			//Will be sorted by owningBinderkey
			List<NotifyStatus> uStatus = getCoreDao().loadNotifyStatus("lastFullSent", begin, end, 100, RequestContextHolder.getRequestContext().getZoneId());
			List ids = new ArrayList();
			while (!uStatus.isEmpty()) {
				//get Ids to log folderEntries
				ids.clear();
				for (NotifyStatus status: uStatus) {
					ids.add(status.getOwnerId());
				}
				params.put("ids", ids);
				List<FolderEntry> entries = getCoreDao().loadObjects("from com.sitescape.team.domain.FolderEntry where id in (:ids)", params);
				for (NotifyStatus eStatus:uStatus) {
					//find corresponding folderEntry; attempting to keep in folder order
					FolderEntry entry = null;
					for (FolderEntry fEntry:entries) {
						if (fEntry.getId().equals(eStatus.getOwnerId())) {
							entry = fEntry;
							break;
						}
					}
					if (entry == null) continue;
	   				if (!entry.getRootFolder().equals(currentFolder)) {
	   					currentFolder = entry.getRootFolder();
	   					folderSubscriptions = getCoreDao().loadSubscriptionByEntity(currentFolder.getEntityIdentifier());  					
	   					processor = (FolderEmailFormatter)processorManager.getProcessor(currentFolder,FolderEmailFormatter.PROCESSOR_KEY);
	   					mHelper = new MimeNotifyPreparator(processor, currentFolder, begin, logger, sendVTODO);
	   	   				mHelper.setDefaultFrom(mailSender.getDefaultFrom());		
	   	  				mHelper.setTimeZone(timeZone);
					}
	  				FolderEntry parent = entry.getTopEntry();
	   				if (parent == null) parent = entry;
	   				//Handle subscriptions plus notifications for 3 types 
	   				List subscriptions;
	   				if (parent.isSubscribed()) {
	   					subscriptions = getCoreDao().loadSubscriptionByEntity(parent.getEntityIdentifier());
	   					//	make sure entry subscription is 1st in list so overrides folder subscription for same user
	   					subscriptions.addAll(folderSubscriptions);
	   				} else {
	   					subscriptions = folderSubscriptions;
	   				}
	   				//still have to add in notifications, so continue event if subscriptions is empty
	   				// Users wanting individual, message style email with attachments
	   				Map messageResults = processor.buildDistributionList(entry, subscriptions, Subscription.MESSAGE_STYLE_EMAIL_NOTIFICATION);
	   				// Users wanting individual, message style email without attachments
	   				Map messageNoAttsResults = processor.buildDistributionList(entry, subscriptions, Subscription.MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION);
	   				// Users wanting individual, text message email
	   				Map messageTxtResults = processor.buildDistributionList(entry, subscriptions, Subscription.MESSAGE_STYLE_TXT_EMAIL_NOTIFICATION);
	   				mHelper.setEntry(entry);
	   				mHelper.setStartDate(eStatus.getLastFullSent());
	   				if (!messageTxtResults.isEmpty()) {
	   					mHelper.setType(Notify.NotifyType.text);
	   					mHelper.setSendAttachments(false);
	   					doSubscription (currentFolder, mailSender, mHelper, messageTxtResults, ctx);
	   				}	
	  				if (!messageNoAttsResults.isEmpty()) {
	  					mHelper.setType(Notify.NotifyType.full);
	  					mHelper.setSendAttachments(false);
	  					doSubscription (currentFolder, mailSender, mHelper, messageNoAttsResults, ctx);
	  				}
	   		   					
	  				if (!messageResults.isEmpty()) {
	  					mHelper.setType(Notify.NotifyType.full);
	  					mHelper.setSendAttachments(true);
	  					doSubscription (currentFolder, mailSender, mHelper, messageResults, ctx);
	  				}
				}
				values.put("p2", ids);
				for (int count=0; count<10; ++count) {
					try {
						getTransactionTemplate().execute(new TransactionCallback() {
							public Object doInTransaction(TransactionStatus status) {
								getCoreDao().executeUpdate(updateString, values);
								return null;
							}});
					} catch (org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException ol) {
						continue;
					} catch (org.springframework.dao.DataIntegrityViolationException di) {
						continue;
					} catch (StaleObjectStateException so) {
						continue;
					}
					break;  //assume we got through
				}
				getCoreDao().evict(uStatus);
				getCoreDao().evict(entries);
				uStatus = getCoreDao().loadNotifyStatus("lastFullSent", begin, end, 100, RequestContextHolder.getRequestContext().getZoneId());
 			}

		} catch (MailSendException sx) {
    		logger.error("Error sending mail:" + getMessage(sx));
    		throw sx;
		} catch (MailAuthenticationException ax) {
    		logger.error("Error sending mail:" + getMessage(ax));
    		throw ax;
		} finally {
			if (ctx !=null) mailSender.releaseConnection(ctx);
		}
		return end;
	}
	
	private void doSubscription (Folder folder, JavaMailSender mailSender, MimeNotifyPreparator mHelper, Map results, Object ctx) {
		for (Iterator iter=results.entrySet().iterator(); iter.hasNext();) {			
			//Use spring callback to wrap exceptions into something more useful than javas 
			Map.Entry e = (Map.Entry)iter.next();
			mHelper.setLocale((Locale)e.getKey());
			//break to list into pieces if big
			ArrayList rcpts = new ArrayList((Set)e.getValue());
			for (int i=0; i<rcpts.size(); i+=rcptToLimit) {
				try {
					List subList = rcpts.subList(i, Math.min(rcpts.size(), i+rcptToLimit));
					mHelper.setToAddrs(subList);
					mailSender.send(mHelper, ctx);
				} catch (MailSendException sx) {
		    		logger.error("Error sending mail:" + getMessage(sx));
			  		SendEmail job = getEmailJob(RequestContextHolder.getRequestContext().getZone());
			   		job.schedule(mailSender, mHelper.getMessage(), folder.getTitle(), getMailDirPath(folder), false);
			   	} catch (Exception ex) {
			   		//message gets thrown away here
		       		logger.error(getMessage(ex));
		    	}
			}
		}

	}
	/**
	 * Send email notifications for recent changes.  Only used for digest style messages
	 */
    public Date sendNotifications(Long binderId, Date begin) {
		Binder binder = coreDao.loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId()); 
		final String updateString="update com.sitescape.team.domain.NotifyStatus set lastDigestSent=:p1 where ownerId in (:p2)";
   		final Map values = new HashMap();
		Object ctx = null;
		Date end = new Date();
		values.put("p1", end);
		JavaMailSender mailSender = getMailSender(RequestContextHolder.getRequestContext().getZone());
		Map params = new HashMap();
		try {
			ctx = mailSender.initializeConnection();
			Folder currentFolder = null;
			List<Subscription> folderSubscriptions = null;
			FolderEmailFormatter processor=null;
			MimeNotifyPreparator mHelper = null;
			String timeZone = getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.DEFAULT_TIMEZONE);
			List ids = new ArrayList();
			//Will be sorted by owningBinderkey
			List<NotifyStatus> uStatus;
			if (binder.isRoot()) {
				uStatus = getCoreDao().loadNotifyStatus("lastDigestSent", begin, end, 100, RequestContextHolder.getRequestContext().getZoneId());
			} else {
				uStatus = getCoreDao().loadNotifyStatus(binder, "lastDigestSent", begin, end, 100, RequestContextHolder.getRequestContext().getZoneId());				
			}
			while (!uStatus.isEmpty()) {
				//get Ids to log folderEntries
				ids.clear();
				NotifyStatus current = uStatus.get(0);
				uStatus.remove(0);
				currentFolder = (Folder)coreDao.loadBinder(current.getOwningBinderId(), RequestContextHolder.getRequestContext().getZoneId()); 
				currentFolder = currentFolder.getRootFolder();
				//find other entries for same folder tree.  Ordered by owingBinderKey
				List<NotifyStatus>currentStatus = new ArrayList();
				currentStatus.add(current);
				ids.add(current.getOwnerId());
				for (NotifyStatus eStatus: uStatus) {
					if (eStatus.getOwningBinderId().equals(current.getOwningBinderId()) ||
							eStatus.getOwningBinderId().equals(currentFolder.getId())) {
						currentStatus.add(eStatus);
						ids.add(eStatus.getOwnerId());
						continue;
					}
					Folder parent = (Folder)coreDao.loadBinder(eStatus.getOwningBinderId(), RequestContextHolder.getRequestContext().getZoneId()); 
					if (parent.getRootFolder().equals(currentFolder)) {
						currentStatus.add(eStatus);
						ids.add(eStatus.getOwnerId());
						continue;
					}
					break; //done with this folder tree				
				}
				//remove from pending list
				uStatus.removeAll(currentStatus);
				params.put("ids", ids);
				List<FolderEntry> entries = getCoreDao().loadObjects("from com.sitescape.team.domain.FolderEntry where id in (:ids) order by HKey.sortKey", params);
				if (!entries.isEmpty()) {
	   				//Handle digest subscriptions and notifications 
	   		 		//get folder specific helper to build message
					processor = (FolderEmailFormatter)processorManager.getProcessor(currentFolder,FolderEmailFormatter.PROCESSOR_KEY);
	   		  		folderSubscriptions = getCoreDao().loadSubscriptionByEntity(currentFolder.getEntityIdentifier());
	   				List digestResults = processor.buildDistributionList(currentFolder, entries, folderSubscriptions, Subscription.DIGEST_STYLE_EMAIL_NOTIFICATION);		
	   				mHelper = new MimeNotifyPreparator(processor, currentFolder, begin, logger, sendVTODO);
	   				mHelper.setDefaultFrom(mailSender.getDefaultFrom());		
	   				mHelper.setTimeZone(timeZone);
   					for (int i=0; i<digestResults.size(); ++i) {
   						Object row[] = (Object [])digestResults.get(i);
   						Collection msgs = (Collection)row[0];
   						if (msgs.isEmpty()) continue; //didn't have access
   						mHelper.setEntries(msgs);
   						mHelper.setType(Notify.NotifyType.summary);
   						mHelper.setSendAttachments(false);
   						doSubscription(currentFolder, mailSender, mHelper, (Map)row[1], ctx);
   					}
	   				
				}
				values.put("p2", ids);
				for (int count=0; count<10; ++count) {
					try {
						getTransactionTemplate().execute(new TransactionCallback() {
							public Object doInTransaction(TransactionStatus status) {
								getCoreDao().executeUpdate(updateString, values);
								return null;
							}});
					} catch (org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException ol) {
						continue;
					} catch (org.springframework.dao.DataIntegrityViolationException di) {
						continue;
					} catch (StaleObjectStateException so) {
						continue;
					}
					break;  //assume we got through
				}
				getCoreDao().evict(currentStatus);
				getCoreDao().evict(entries);
				if (uStatus.isEmpty()) {
					if (binder.isRoot()) {
						uStatus = getCoreDao().loadNotifyStatus("lastDigestSent", begin, end, 100, RequestContextHolder.getRequestContext().getZoneId());
					} else {
						uStatus = getCoreDao().loadNotifyStatus(binder, "lastDigestSent", begin, end, 100, RequestContextHolder.getRequestContext().getZoneId());				
					}
    			}
			}

		} catch (MailSendException sx) {
    		logger.error("Error sending mail:" + getMessage(sx));
    		throw sx;
		} catch (MailAuthenticationException ax) {
    		logger.error("Error sending mail:" + getMessage(ax));
    		throw ax;
		} finally {
			if (ctx !=null) mailSender.releaseConnection(ctx);
		}
		return end;    
    }

 
    //used for re-try mail.  MimeMessage has been serialized 
    public void sendMail(String mailSenderName, java.io.InputStream input) {
    	JavaMailSender mailSender = getMailSender(mailSenderName);
    	try {
        	MimeMessage mailMsg = new MimeMessage(mailSender.getSession(), input);
			mailSender.send(mailMsg);
 		} catch (MessagingException mx) {
			throw new MailPreparationException(NLT.get("errorcode.sendMail.badInputStream", new Object[] {getMessage(mx)}));
		}
    }
    //used for re-try mail.  MimeMessage has been serialized 
    public void sendMail(String mailSenderName, String account, String password, java.io.InputStream input) {
    	JavaMailSender mailSender = getMailSender(mailSenderName);
    	try {
    	   	if (Validator.isNotNull(account)) {
    			//need to get our own sender, so the account/password can be changed
				//need our own sender, so we can change the username/password
    	   		Session session = mailSender.getSession();
    	   		mailSender = (JavaMailSender)mailSender.getClass().newInstance();				
    	   		SpringContextUtil.applyDependencies(mailSender, "mailSender");	
    	   		mailSender.setSession(session, account, password);
    		}
        	MimeMessage mailMsg = new MimeMessage(mailSender.getSession(), input);
			mailSender.send(mailMsg);
 		} catch (MessagingException mx) {
			throw new MailPreparationException(NLT.get("errorcode.sendMail.badInputStream", new Object[] {getMessage(mx)}));
		} catch (Exception ex) {
			logger.error("Error on mail retry:" + getMessage(ex));
		}
    }
   //prepare mail and send it - caller must retry if desired
    public void sendMail(String mailSenderName, MimeMessagePreparator mHelper) {
    	JavaMailSender mailSender = getMailSender(mailSenderName);
		mHelper.setDefaultFrom(mailSender.getDefaultFrom());
		//Use spring callback to wrap exceptions into something more useful than javas 
		mailSender.send(mHelper);
	}
    //used to send prepared mail now.
    public void sendMail(MimeMessage mailMsg) {
       	Binder zone = RequestContextHolder.getRequestContext().getZone();
        sendMail(getMailSender(zone).getName(), mailMsg);
    }
    //used to send prepared mail now.    
    public void sendMail(String mailSenderName, MimeMessage mailMsg) {
    	JavaMailSender mailSender = getMailSender(mailSenderName);
		mailSender.send(mailMsg);
    }
    //send mail now, if fails, reschedule
    public boolean sendMail(Binder binder, Map message, String comment) {
  		SendEmail job = getEmailJob(RequestContextHolder.getRequestContext().getZone());
  		JavaMailSender mailSender = getMailSender(binder);
		Collection<InternetAddress> addrs = (Collection)message.get(MailModule.TO);
		if ((addrs == null) || addrs.isEmpty()) return true;
		//handle large recipient list 
		boolean sent = true;
		ArrayList rcpts = new ArrayList(addrs);
		for (int i=0; i<rcpts.size(); i+=rcptToLimit) {
			List subList = rcpts.subList(i, Math.min(rcpts.size(), i+rcptToLimit));
			message.put(MailModule.TO, subList);
	 		MimeMessagePreparator helper = new MimeMapPreparator(message, logger, sendVTODO);
	 		try {
	 			mailSender.send(helper);
	 			sent = true;
	 		} catch (MailSendException sx) {
	 			logger.error("Error sending mail:" + getMessage(sx));
	 			job.schedule(mailSender, helper.getMessage(), comment, getMailDirPath(binder), false);
	 	   	} catch (MailAuthenticationException ax) {
	       		logger.error("Authentication Exception:" + getMessage(ax));				
	       		job.schedule(mailSender, helper.getMessage(), comment, getMailDirPath(binder), false);
	 		} catch (Exception ex) {
	 			//message gets thrown away here
	 			logger.error(getMessage(ex));
	 		}
		}
		//replace list with original
		message.put(MailModule.TO, addrs);
		return sent;
    }
    // schedule mail delivery - 
    public void scheduleMail(Binder binder, Map message, String comment) throws Exception {
  		SendEmail job = getEmailJob(RequestContextHolder.getRequestContext().getZone());
  		JavaMailSender mailSender = getMailSender(binder);
 		Collection<InternetAddress> addrs = (Collection)message.get(MailModule.TO);
 		if ((addrs == null) || addrs.isEmpty()) return;
		//handle large recipient list 
		ArrayList rcpts = new ArrayList(addrs);
		for (int i=0; i<rcpts.size(); i+=rcptToLimit) {
			List subList = rcpts.subList(i, Math.min(rcpts.size(), i+rcptToLimit));
			message.put(MailModule.TO, subList);
	 		MimeMessagePreparator helper = new MimeMapPreparator(message, logger, sendVTODO);
			MimeMessage msg = mailSender.createMimeMessage();
			helper.prepare(msg);
			job.schedule(mailSender, msg, comment, getMailDirPath(binder), true);
		}
		//replace list with original
		message.put(MailModule.TO, addrs);
	}
 
}
