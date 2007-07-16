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

package com.sitescape.team.module.mail.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentSource;
import org.springframework.util.FileCopyUtils;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.OrderBy;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NotificationDef;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WorkflowControlledEntry;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.binder.AccessUtils;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.definition.notify.Notify;
import com.sitescape.team.module.definition.notify.NotifyBuilderUtil;
import com.sitescape.team.module.definition.ws.ElementBuilderUtil;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.mail.FolderEmailFormatter;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.DirPath;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Html;
import com.sitescape.util.StringUtil;
import com.sitescape.util.Validator;
/**
 * @author Janet McCann
 *
 */
public class DefaultFolderEmailFormatter extends CommonDependencyInjection implements FolderEmailFormatter {
	private Log logger = LogFactory.getLog(getClass());
    private FolderModule folderModule;
    private BinderModule binderModule;
    protected DefinitionModule definitionModule;
    protected MailModule mailModule;
	private TransformerFactory transFactory = TransformerFactory.newInstance();

	protected Map transformers = new HashMap();
    public DefaultFolderEmailFormatter () {
	}
    public void setDefinitionModule(DefinitionModule definitionModule) {
        this.definitionModule = definitionModule;
    }
    public void setFolderModule(FolderModule folderModule) {
    	this.folderModule = folderModule;
    }
    public void setBinderModule(BinderModule binderModule) {
    	this.binderModule = binderModule;
    }
 	public void setMailModule(MailModule mailModule) {
		this.mailModule = mailModule;
	}
	private IcalModule icalModule;
	public IcalModule getIcalModule() {
		return icalModule;
	}
	public void setIcalModule(IcalModule icalModule) {
		this.icalModule = icalModule;
	}	



	/**
	 * Get updates to this folder and all its sub-folders
	 */
	public List getEntries(Folder folder, Date start, Date until) {
 		Folder top = folder.getTopFolder();
		if (top == null) top = folder;
		return getFolderDao().loadFolderTreeUpdates(top, start ,until, new OrderBy("HKey.sortKey"), -1);
 		
    }
	/**
	 * Determine which users have access to the entry.
	 * Return a map from locale to a collection of email Addresses
	 */
	public Map buildDistributionList(FolderEntry entry, Collection subscriptions, int style) {
		//Users wanting digest style messages
		List users = getUsers(subscriptions, style);
		return buildDistributionMap(entry, users);
	}
	/**
	 * return map where key is a locale and value is a list of email addresses
	 * @param entry
	 * @param users
	 * @return
	 */
	private Map buildDistributionMap(FolderEntry entry, Collection users) {
		Map languageMap = new HashMap();
		//check access to folder/entry and build lists of users to receive mail
		List checkList = new ArrayList();
		//remove users that don't have access to the entry
		for (Iterator iter=users.iterator(); iter.hasNext();) {
			User u = (User)iter.next();
			if (!Validator.isNull(u.getEmailAddress())) {
				try {
					AccessUtils.readCheck(u, entry);
					checkList.add(u);
				} catch (Exception ex) {};
			}
		}
		Set email;
		for (Iterator iter=checkList.iterator(); iter.hasNext();) {
			User u = (User)iter.next();
			email = (Set)languageMap.get(u.getLocale());
			if (email != null) {
				email.add(u.getEmailAddress().trim());
			} else {
				email = new HashSet();
				email.add(u.getEmailAddress().trim());
				languageMap.put(u.getLocale(), email);
			}
		}
		return languageMap;
	}
	/**
	 * Build digest style lists.  Include notifications, minus disabled subscriptions,
	 * plus digest folder subscriptions and explicit email address
	 * Determine which users have access to which entries.
	 * Return a list of Object[].  Each Object[0] contains a list of entries,
	 * Object[1] contains a map.  The map maps locales to a list of emailAddress of userse
	 * using that locale that have access to the entries.
	 * The list of entries will maintain the order used to do lookup.  This is important
	 * when actually building the message	
	 */
	public List buildDistributionList(Folder folder, Collection entries, Collection subscriptions) {
		//done if no-one is interested
		List result = new ArrayList();
		Set userIds = new HashSet();
		Set groupIds = new HashSet();
		for (Iterator iter=folder.getNotificationDef().getDistribution().iterator(); iter.hasNext();) {
			Principal p = (Principal)iter.next();
			if (p.getEntityType().equals(EntityType.group))
				groupIds.add(p.getId());
			else
				userIds.add(p.getId());
		}
		if (folder.getNotificationDef().isTeamOn()) {
			Set teamIds = folder.getTeamMemberIds();
			List team = getProfileDao().loadPrincipals(teamIds, folder.getZoneId(), true);
			for (Iterator iter=team.iterator(); iter.hasNext();) {
				Principal p = (Principal)iter.next();
				if (p.getEntityType().equals(EntityType.group))
					groupIds.add(p.getId());
				else
					userIds.add(p.getId());
			}
			
		}
		userIds.addAll(getProfileDao().explodeGroups(groupIds, folder.getZoneId()));
		//Add users wanting digest style messages, remove users wanting nothing
		for (Subscription notify: (Collection<Subscription>)subscriptions) {
			if (notify.getStyle() == Subscription.DIGEST_STYLE_EMAIL_NOTIFICATION) {
				userIds.add(notify.getId().getPrincipalId());
			} else {
				//user wants some other type of Notificaigton
				userIds.remove(notify.getId().getPrincipalId());
			}
		}
		List<User> users = getProfileDao().loadUsers(userIds, folder.getZoneId());
		//check access to folder/entry and build lists of users to receive mail
		List checkList = new ArrayList();
		for (User u: users) {
			if (!Validator.isNull(u.getEmailAddress())) {
				AclChecker check = new AclChecker(u);
				check.checkEntries(entries);
				checkList.add(check);
			}
		}
		//get a map containing a list of users mapped to a list of entries
		while (!checkList.isEmpty()) {
			Object [] lists = mapEntries(checkList);
			result.add(lists);
		}
		//add in email address only subscriptions
		return doEmailAddrs(folder, entries, result);
	}

	/**
	 * Determine which users have access to which entries.
	 * Return a list of Object[].  Each Object[0] contains a list of entries,
	 * Object[1] contains a map.  The map maps locales to a list of emailAddress of userse
	 * using that locale that have access to the entries.
	 * The list of entries will maintain the order used to do lookup.  This is important
	 * when actually building the message	
	 */
	public List buildDistributionList(Folder folder, Collection entries, Collection subscriptions, int style) {
		//done if no-one is interested
		List result = new ArrayList();
		if (subscriptions.isEmpty()) return result;
		
		//Users wanting digest style messages
		List users = getUsers(subscriptions, style);
		//check access to folder/entry and build lists of users to receive mail
		List checkList = new ArrayList();
		for (Iterator iter=users.iterator(); iter.hasNext();) {
			User u = (User)iter.next();
			if (!Validator.isNull(u.getEmailAddress())) {
				AclChecker check = new AclChecker(u);
				check.checkEntries(entries);
				checkList.add(check);
			}
		}
		//get a map containing a list of users mapped to a list of entries
		while (!checkList.isEmpty()) {
			Object [] lists = mapEntries(checkList);
			result.add(lists);
		}
		return result;
	}
	/**
	 * Add email only subscriptions to the lists
	 */
	private List doEmailAddrs(Folder folder, Collection entries, List result) {
 		NotificationDef nDef = folder.getNotificationDef();
 		String addrs = nDef.getEmailAddress();
 		if (Validator.isNull(addrs)) return result;
		String [] emailAddrs = StringUtil.split(addrs);
		//done if no-one is interested
		if (emailAddrs.length == 0)  return result;

		Set emailSet = new HashSet();
		//add email address listed 
		for (int j=0; j<emailAddrs.length; ++j) {
			if (!Validator.isNull(emailAddrs[j]))
				emailSet.add(emailAddrs[j].trim());
		}
			
		Locale l = Locale.getDefault();
		//see if an entry already exists for the entire list
		boolean done = false;
		for (int i=0; i<result.size(); ++i) {
			Object[] objs = (Object[])result.get(i);
			List es = (List)objs[0];
			//	if this is the full entry list
			if (es.size() == entries.size()) {
				Map lang = (Map)objs[1];
				Set email = (Set)lang.get(l);
				if (email == null) {
					lang.put(l, emailSet);
				} else {
					email.addAll(emailSet);
				}
				done = true;
				break;
			}
		}
		if (!done) {
			Map lang = new HashMap();
			lang.put(l, emailSet);
			result.add(new Object[] {entries, lang});
		}
		
		return result;
	}
	/**
	 * 
	 * @param checkList
	 * @return List of users, list of entries they all have access to
	 */
	private Object[] mapEntries(List checkList) {
		
		AclChecker check = (AclChecker)checkList.get(0);
		checkList.remove(0);
		Set email = new HashSet();
		//separate into languages
		email.add(check.getUser().getEmailAddress());
		Map languageMap = new HashMap();
		languageMap.put(check.getUser().getLocale(), email);
		//make a copy so we can alter original
		List toDo = new ArrayList(checkList);
		//compare the list of entries each user has access to
		for (int i=0; i<toDo.size(); ++i) {
			AclChecker c = (AclChecker)toDo.get(i);
			if (check.compareEntries(c)) {
				email = (Set)languageMap.get(c.getUser().getLocale());
				if (email != null) {
					email.add(c.getUser().getEmailAddress().trim());
				} else {
					email = new HashSet();
					email.add(c.getUser().getEmailAddress().trim());
					languageMap.put(c.getUser().getLocale(), email);
				}
				checkList.remove(c);
			}
		}
		
		return new Object[] {check.getEntries(), languageMap};
		
	}
	public String getSubject(Folder folder, Notify notify) {
		String subject = folder.getNotificationDef().getSubject();
		if (Validator.isNull(subject))
			subject = mailModule.getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.NOTIFY_SUBJECT);
		//if not specified, us a localized default
		if (Validator.isNull(subject))
			return NLT.get("notify.subject", notify.getLocale()) + " " + folder.toString();
		return subject;
	}
	
	public String getFrom(Folder folder, Notify notify) {
		String from = folder.getNotificationDef().getFromAddress();
		if (Validator.isNull(from))
			from = mailModule.getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.NOTIFY_FROM);
		return from;
	}

	private List getUsers(Collection<Subscription> subscriptions, int style) {
		Set userIds = new HashSet();
	
		for (Subscription notify: subscriptions) {
			if (notify.getStyle() == style) {
				userIds.add(notify.getId().getPrincipalId());
			} 
		}
		
 		return getProfileDao().loadUsers(userIds,  RequestContextHolder.getRequestContext().getZoneId());

		
	}
	private int checkDate(HistoryStamp dt1, Date dt2) {
		if (dt1 == null) return -1;
		Date date = dt1.getDate();
		if (date == null) return -1;
		return date.compareTo(dt2);
	}
	private int checkDate(HistoryStamp dt1, HistoryStamp dt2) {
		if (dt2 == null) return 1;
		return checkDate(dt1, dt2.getDate());
	}

	protected void doFolder(Element element, Folder folder) {
		element.addAttribute("name", folder.getId().toString());
		element.addAttribute("title", folder.getTitle());
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getEntityIdentifier().getEntityId().toString());
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, folder.getEntityType().toString());
		element.addAttribute("href", adapterUrl.toString());
		PostingDef post = folder.getPosting();
		if (post != null) {
			element.addAttribute("replyTo", post.getEmailAddress());
		}

	}

	protected void doEntry(final Element element, final FolderEntry entry, final Notify notifyDef, boolean hasChanges) {
		HistoryStamp stamp;
		if (hasChanges) {
			//style sheet will translate these tags
			element.addAttribute("hasChanges", "true");
			if (checkDate(entry.getCreation(), notifyDef.getStartDate()) > 0) {
				element.addAttribute("notifyType", "newEntry");
				stamp = entry.getCreation();
			} else if (checkDate(entry.getWorkflowChange(), entry.getModification()) >= 0) {
				stamp = entry.getWorkflowChange();
				element.addAttribute("notifyType", "workflowEntry");
			} else {
				element.addAttribute("notifyType", "modifiedEntry");
				stamp = entry.getModification();
			} 
		} else {
			stamp = entry.getModification();				
			element.addAttribute("hasChanges", "false");
		}
		if (stamp == null) stamp = new HistoryStamp();
		Principal p = stamp.getPrincipal();
		String title = null;
		if (p != null) title = p.getTitle();
		if (Validator.isNull(title)) element.addAttribute("notifyBy",NLT.get("entry.noTitle", notifyDef.getLocale()));
		else element.addAttribute("notifyBy", title);
		
		Date date = stamp.getDate();
		if (date == null) element.addAttribute("notifyDate", "");
		else element.addAttribute("notifyDate", notifyDef.getDateFormat().format(date));

		element.addAttribute("name", entry.getId().toString());
		element.addAttribute("title", entry.getTitle());			    
		element.addAttribute("docNumber", entry.getDocNumber());			    
		element.addAttribute("docLevel", String.valueOf(entry.getDocLevel()));
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, entry.getParentBinder().getId().toString());
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entry.getId().toString());
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, entry.getEntityType().toString());
		element.addAttribute("href", adapterUrl.toString());
		
		final String fullOrSummaryAttribute = (notifyDef.isFull()?"full":"summary");
		
		DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
			public void visit(Element entryElement, Element flagElement, Map args)
			{
				if(flagElement.attributeValue(fullOrSummaryAttribute).equals("true")) {
					String fieldBuilder = flagElement.attributeValue("notifyBuilder");
					String itemName = entryElement.attributeValue("name");
					String nameValue = DefinitionUtils.getPropertyValue(entryElement, "name");									
					if (Validator.isNull(nameValue)) {nameValue = itemName;}
                    String captionValue;
                    if (!args.containsKey("caption")) {
                        captionValue = DefinitionUtils.getPropertyValue(entryElement, "caption");
                       	if (Validator.isNull(captionValue)) {
                            	captionValue = entryElement.attributeValue("caption");
                        }
                    } else {
                       	captionValue = (String) args.get("caption");
                    }
                  
                    args.put("_caption", NLT.getDef(captionValue, notifyDef.getLocale()));
                    args.put("_itemName", itemName);
                    NotifyBuilderUtil.buildElement(element, notifyDef, entry,
                                    			   nameValue, fieldBuilder, args);
                }
			}
			public String getFlagElementName() { return "notify"; }
		};

		definitionModule.walkDefinition(entry, visitor);	
	}
	// get cached template.  If not cached yet,load it
	protected Transformer getTransformer(String zoneName, String type) throws TransformerConfigurationException {
		//convert mail templates into cached transformer temlates
		Templates trans;
		trans = (Templates)transformers.get(zoneName + ":" + type);
		if (trans == null) {
			String templateName = mailModule.getMailProperty(zoneName, type);
			Source xsltSource = new StreamSource(new File(DirPath.getXsltDirPath(),templateName));
			trans = transFactory.newTemplates(xsltSource);
			//replace name with actual template
			if (GetterUtil.getBoolean(mailModule.getMailProperty(zoneName, MailModule.NOTIFY_TEMPLATE_CACHE_DISABLED), false) == false)
				transformers.put(zoneName + ":" + type, trans);
		} 
		return trans.newTransformer();
	}

	protected String doTransform(Document document, String zoneName, String type, Locale locale, boolean oneEntry) {
		StreamResult result = new StreamResult(new StringWriter());
		try {
			Transformer trans = getTransformer(zoneName, type);
			trans.setParameter("Lang", locale.toString());
			trans.setParameter("TOC", Boolean.valueOf(oneEntry).toString());
			trans.transform(new DocumentSource(document), result);
		} catch (Exception ex) {
			return ex.getLocalizedMessage();
		}
		return result.getWriter().toString();
	}

	public Map buildNotificationMessage(Folder folder, Collection entries,  Notify notify) {
	    Map result = new HashMap();
	    if (notify.getStartDate() == null) return result;
		Set seenIds = new TreeSet();
		Document mailDigest = DocumentHelper.createDocument();
		
    	Element rootElement = mailDigest.addElement("mail");
       	rootElement.addAttribute("summary", String.valueOf(notify.isSummary()));
		Element element;
		Folder lastFolder=null;
		Element fElement=null;
		ArrayList parentChain = new ArrayList();
		element = rootElement.addElement("topFolder");
		element.addAttribute("changeCount", String.valueOf(entries.size()));
      	element.addAttribute("title", folder.getTitle());
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getEntityIdentifier().getEntityId().toString());
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, folder.getEntityType().toString());
		element.addAttribute("href", adapterUrl.toString());

		for (Iterator i=entries.iterator();i.hasNext();) {
			parentChain.clear();
			FolderEntry entry = (FolderEntry)i.next();	
			if (!entry.getParentFolder().equals(lastFolder)) {
				fElement = rootElement.addElement("folder");
				doFolder(fElement, entry.getParentFolder());
			}
			//make sure change of entries exist from topentry down to changed entry
			//since entries are sorted by sortKey, we should have processed an changed parents
			//already
			FolderEntry parent = entry.getParentEntry();
			while ((parent != null) && (!seenIds.contains(parent.getId()))) {
				parentChain.add(parent);
				parent=parent.getParentEntry();
			}
			for (int pos=parentChain.size()-1; pos>=0; --pos) {
				element = fElement.addElement("folderEntry");
				parent = (FolderEntry)parentChain.get(pos);
				doEntry(element, parent, notify, false);
				seenIds.add(parent.getId());
			}
					
			seenIds.add(entry.getId());
			element = fElement.addElement("folderEntry");
			doEntry(element, entry, notify, true);
		}
		
		
//		result.put(FolderEmailFormatter.PLAIN, doTransform(mailDigest, folder.getZoneName(), MailModule.NOTIFY_TEMPLATE_TEXT, notify.getLocale(), notify.isSummary()));
		result.put(FolderEmailFormatter.HTML, doTransform(mailDigest, RequestContextHolder.getRequestContext().getZoneName(), MailModule.NOTIFY_TEMPLATE_HTML, notify.getLocale(), notify.isSummary()));
		
		return result;
	}
	public Map buildNotificationMessage(Folder folder, FolderEntry entry,  Notify notify) {
	    Map result = new HashMap();
	    if (notify.getStartDate() == null) return result;
		Document mailDigest = DocumentHelper.createDocument();
		
    	Element rootElement = mailDigest.addElement("mail");
		Element element;
		Element fElement=null;
		ArrayList parentChain = new ArrayList();
		Folder topFolder = folder.getTopFolder();
		if (topFolder == null) topFolder = folder;
		element = rootElement.addElement("topFolder");
     	element.addAttribute("title", folder.getTitle());
		fElement = rootElement.addElement("folder");
		doFolder(fElement, folder);
 		
		FolderEntry parent = entry.getParentEntry();
		while (parent != null) {
			parentChain.add(parent);
			parent=parent.getParentEntry();
		}
		for (int pos=parentChain.size()-1; pos>=0; --pos) {
			element = fElement.addElement("folderEntry");
			parent = (FolderEntry)parentChain.get(pos);
			doEntry(element, parent, notify, false);
		}
					
		element = fElement.addElement("folderEntry");
		doEntry(element, entry, notify, true);
		
//		result.put(FolderEmailFormatter.PLAIN, doTransform(mailDigest, folder.getZoneName(), MailModule.NOTIFY_TEMPLATE_TEXT, notify.getLocale(), false));
		result.put(FolderEmailFormatter.HTML, doTransform(mailDigest, RequestContextHolder.getRequestContext().getZoneName(), MailModule.NOTIFY_TEMPLATE_HTML, notify.getLocale(), false));
		
		return result;
	}
	public List postMessages(Folder folder, PostingDef pDef, Message[] msgs, Session session) {
		String type;
		Object content;
		Map fileItems = new HashMap();
		Map inputData = new HashMap();
		Definition definition = pDef.getDefinition();
		if (definition == null) definition = folder.getDefaultEntryDef();
		String defId=null;
		if (definition != null) defId = definition.getId();
		InternetAddress from=null;
		String title;
		List errors = new ArrayList();
		Integer option = pDef.getReplyPostingOption();
		if (option == null) option = PostingDef.POST_AS_A_REPLY;
		
		for (int i=0; i<msgs.length; ++i) {
			try {
				from = (InternetAddress)msgs[i].getFrom()[0];
				title = msgs[i].getSubject();
				User fromUser = getFromUser(from);
				RequestContext oldCtx = RequestContextHolder.getRequestContext();
				try {
					//need to setup user context for request
					RequestContextUtil.setThreadContext(fromUser);
					
					inputData.put(ObjectKeys.INPUT_FIELD_POSTING_FROM, from.toString()); 
					inputData.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
					type=msgs[i].getContentType().trim();
					content = msgs[i].getContent();
					if (type.startsWith("text/plain")) {
						processText(content, inputData);
					} else if (type.startsWith("text/html")) {
						processHTML(content, inputData);
					} else if (content instanceof MimeMultipart) {
						processMime((MimeMultipart)content, inputData, fileItems);
					}
					//parse subject to see if this is a reply
					if (title.startsWith(MailModule.REPLY_SUBJECT)) {
						String flag = MailModule.REPLY_SUBJECT+folder.getId().toString()+":";
						//see if for this folder
						if (title.startsWith(flag)) {
							if (option == PostingDef.RETURN_TO_SENDER) 
						   		throw new NotSupportedException("errorcode.notsupported.postingReplies");
							 							
							String docId = title.substring(flag.length());
							Long id=null;
							int index = docId.indexOf(" ");
							if (index == -1) id=Long.valueOf(docId);
							else id=Long.valueOf(docId.substring(0, index));
							if (option.longValue() == PostingDef.POST_AS_A_REPLY.longValue())
								folderModule.addReply(folder.getId(), id, defId, new MapInputData(inputData), fileItems);
							else
								folderModule.addEntry(folder.getId(), defId, new MapInputData(inputData), fileItems);
							msgs[i].setFlag(Flags.Flag.DELETED, true);
						}
					} else {
						List entryIdsFromICalendars = new ArrayList();
						Iterator fileItemsIt = fileItems.entrySet().iterator();
						while (fileItemsIt.hasNext()) {
							Map.Entry me = (Map.Entry)fileItemsIt.next();
							FileHandler fileHandler = (FileHandler)me.getValue();
							entryIdsFromICalendars.addAll(getIcalModule().parseToEntries(folder.getId(), fileHandler.getInputStream()));
						}
							
						if (entryIdsFromICalendars.isEmpty()) {
							folderModule.addEntry(folder.getId(), defId, new MapInputData(inputData), fileItems);
						}
						msgs[i].setFlag(Flags.Flag.DELETED, true);
					}

				} finally {
					//reset context
					RequestContextHolder.setRequestContext(oldCtx);
					fileItems.clear();
					inputData.clear();
					
				}
			} catch (Exception ex) {
				logger.error("Cannot post the message from: " + from + "Error: " + ex.getLocalizedMessage());
				//if fails and from self, don't reply or we will get it back
				errors.add(postError(pDef, msgs[i], from, ex.getLocalizedMessage()));
			}
		}
		return errors;
	}
	private Message postError(PostingDef pDef, Message msg, InternetAddress from, String error) {
		try {
			msg.setFlag(Flags.Flag.DELETED, true);
			if (!pDef.getEmailAddress().equals(from.getAddress())) {
				String errorMsg = NLT.get("errorcode.postMessage.failed", new Object[]{Html.stripHtml(error)});
				Message reject = msg.reply(false);
				reject.setText(errorMsg);
				reject.setFrom(new InternetAddress(pDef.getEmailAddress()));
				reject.setContent(msg.getContent(), msg.getContentType());
				reject.setSubject(reject.getSubject() + " (" + errorMsg + ")");
				return reject;
			} 
		} catch (Exception ex2) {}
		return null;
	}
	private User getFromUser(InternetAddress from) {
		//try to map email address to a user
		String fromEmail = from.getAddress();	
		List users = getProfileDao().loadUsers(new FilterControls("lower(emailAddress)", fromEmail.toLowerCase()), RequestContextHolder.getRequestContext().getZoneId());
		if (users.size() == 1) return (User)users.get(0);
		if (users.size() > 1) {
			logger.error("Multiple users with same email address, cannot use for incoming email");
		}
		return getProfileDao().getReservedUser(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, RequestContextHolder.getRequestContext().getZoneId());
	}
	private void processText(Object content, Map inputData) {
		if (inputData.containsKey(ObjectKeys.FIELD_ENTITY_DESCRIPTION)) return;
		String[] val = new String[1];
		val[0] = (String)content;
		inputData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, val);			
	}
	private void processHTML(Object content, Map inputData) {
		String[] val = new String[1];
		val[0] = (String)content;
		inputData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, val);			
	}	
	private void processMime(MimeMultipart content, Map inputData, Map fileItems) throws MessagingException, IOException {
		int count = content.getCount();
		for (int i=0; i<count; ++i ) {
			BodyPart part = content.getBodyPart(i);
			String disposition = part.getDisposition();
			if ((disposition != null) && (disposition.compareToIgnoreCase(Part.ATTACHMENT) == 0))
				fileItems.put(ObjectKeys.INPUT_FIELD_ENTITY_ATTACHMENTS + Integer.toString(fileItems.size() + 1), new FileHandler(part));
			else if (part.isMimeType("text/html"))
				processHTML(part.getContent(), inputData);
			else if (part.isMimeType("text/plain"))
				processText(part.getContent(), inputData);
			else if (part instanceof MimeBodyPart) {
				Object bContent = ((MimeBodyPart)part).getContent();
				if (bContent instanceof MimeMultipart) processMime((MimeMultipart)bContent, inputData, fileItems);
			}
			
		}
	}
	public class FileHandler implements org.springframework.web.multipart.MultipartFile {
		BodyPart part;
		String fileName;
		String type;
		int size;
		
		public FileHandler(BodyPart part) throws MessagingException {
			this.part = part;
			fileName = part.getFileName();
			type = part.getContentType();
			size = part.getSize();
		}
		/**
		 * Return the name of the parameter in the multipart form.
		 * @return the name of the parameter
		 */
		public String getName() {return "attachment";}

		/**
		 * Return whether the uploaded file is empty in the sense that
		 * no file has been chosen in the multipart form.
		 * @return whether the uploaded file is empty
		 */
		public boolean isEmpty() {return false;}
		
		/**
		 * Return the original filename in the client's filesystem.
		 * This may contain path information depending on the browser used,
		 * but it typically will not with any other than Opera.
		 * @return the original filename, or null if empty
		 */
		public String getOriginalFilename() {return fileName;}
		
		
		/**
		 * Return the content type of the file.
		 * @return the content type, or null if empty or not defined
		 */
		public String getContentType() {return type;}

		/**
		 * Return the size of the file in bytes.
		 * @return the size of the file, or 0 if empty
		 */
		public long getSize() {return size;}
		
		/**
		 * Return the contents of the file as an array of bytes.
		 * @return the contents of the file as bytes,
		 * or an empty byte array if empty
		 * @throws IOException in case of access errors
		 * (if the temporary store fails)
		 */
		public byte[] getBytes() throws IOException {
			byte [] results = new byte[size];
			try {
				part.getInputStream().read(results);
			} catch (MessagingException me) {
				throw new IOException(me.getLocalizedMessage());
			}
			return results;
		}

		/**
		 * Return an InputStream to read the contents of the file from.
		 * The user is responsible for closing the stream.
		 * @return the contents of the file as stream,
		 * or an empty stream if empty
		 * @throws IOException in case of access errors
		 * (if the temporary store fails)
		 */
		public InputStream getInputStream() throws IOException {
			try {
				return part.getInputStream();
			} catch (MessagingException me) {
				throw new IOException(me.getLocalizedMessage());
			}
		}
		
		/**
		 * Transfer the received file to the given destination file.
		 * <p>This may either move the file in the filesystem, copy the file in the
		 * filesystem, or save memory-held contents to the destination file.
		 * If the destination file already exists, it will be deleted first.
		 * <p>If the file has been moved in the filesystem, this operation cannot
		 * be invoked again. Therefore, call this method just once to be able to
		 * work with any storage mechanism.
		 * @param dest the destination file
		 * @throws IOException in case of reading or writing errors
		 * @throws java.lang.IllegalStateException if the file has already been moved
		 * in the filesystem as is not available anymore for another transfer
		*/
		public void transferTo(File dest) throws IOException, IllegalStateException {
			//copied from org.springframework.web.multipart.commons.CommonsMultiPart
//			if (!isAvailable()) {
//				throw new IllegalStateException("File has already been moved - cannot be transferred again");
//			}

			if (dest.exists() && !dest.delete()) {
				throw new IOException(
						"Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
			}

			try {
				
				FileOutputStream out = new FileOutputStream(dest);
				InputStream in = getInputStream();
				FileCopyUtils.copy(in, out);
/*				dest.this.fileItem.write(dest);
				if (logger.isDebugEnabled()) {
					String action = "transferred";
					if (!this.fileItem.isInMemory()) {
						action = isAvailable() ? "copied" : "moved";
					}
					logger.debug("Multipart file '" + getName() + "' with original filename [" +
							getOriginalFilename() + "], stored " + getStorageDescription() + ": " +
							action + " to [" + dest.getAbsolutePath() + "]");
				}
*/
				}
			catch (IOException ex) {
				throw ex;
			}
			catch (Exception ex) {
				logger.error("Could not transfer to file", ex);
				throw new IOException("Could not transfer to file: " + ex.getLocalizedMessage());
			}
		}
	}
	protected class AclChecker {
		private User user;
		//keep an ordered list for easier comparision
		protected List entries = new ArrayList();
		
		protected AclChecker(User user) {
			this.user = user;	
		}
		protected User getUser() {
			return user;
		}
		protected void checkEntries(Collection entries) {
			for (Iterator iter=entries.iterator(); iter.hasNext(); ) {
				WorkflowControlledEntry e = (WorkflowControlledEntry)iter.next();
//TODO: this isn't accounting for the binder override
				try {
					AccessUtils.readCheck(user, e);
					this.entries.add(e);
				} catch (Exception ex) {};
			}
		}
		protected List getEntries() {
			return entries;
		}
		protected boolean compareEntries(AclChecker c) {
			if (c.entries.size() != entries.size()) return false;
			// address compare is okay, working from same input
			// lists are processed in the same order, so results should be in the same order
			for (int i=0; i<entries.size(); ++i) {
				if (entries.get(i) != c.entries.get(i)) return false;
			}
			return true;
		}
		
	}
}
