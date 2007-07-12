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
package com.sitescape.team.remoting.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import net.fortuna.ical4j.data.ParserException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HKey;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.definition.ws.ElementBuilder;
import com.sitescape.team.module.definition.ws.ElementBuilderUtil;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.index.IndexUtils;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.remoting.Facade;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.util.AbstractAllModulesInjected;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.util.stringcheck.StringCheckUtil;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.FileUtil;
import com.sitescape.util.Validator;

/**
 * POJO implementation of Facade interface.
 * 
 * Important: This class is NOT tied to any specific remoting protocol 
 * such as SOAP. Therefore don't ever put protocol or tool specific code 
 * (such as capability that utilizes Axis engine directly) into this class.  
 * 
 * @author jong
 *
 */
public abstract class AbstractFacade extends AbstractAllModulesInjected implements Facade, ElementBuilder.BuilderContext {

	protected final Log logger = LogFactory.getLog(getClass());

	public static class AttachmentHandler {
		public void handleAttachment(FileAttachment att, String webUrl) {}
	}

	protected AttachmentHandler attachmentHandler = new AttachmentHandler();
	
	public void handleAttachment(FileAttachment att, String webUrl)
	{
		attachmentHandler.handleAttachment(att, webUrl);
	}

	public String getDefinitionAsXML(String definitionId) {
		return getDefinitionModule().getDefinition(definitionId).getDefinition().getRootElement().asXML();
	}
	
	public String getDefinitionConfigAsXML() {
		return getDefinitionModule().getDefinitionConfig().getRootElement().asXML();
	}

	public long addFolder(long parentBinderId, long binderConfigId, String title)
	{
		try {
			return getAdminModule().addBinderFromTemplate(binderConfigId, parentBinderId, title, null);
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	
	public String getFolderEntriesAsXML(long binderId) {
		com.sitescape.team.domain.Binder binder = getBinderModule().getBinder(new Long(binderId));

		Document doc = DocumentHelper.createDocument();
		Element folderElement = doc.addElement("folder");
		folderElement.addAttribute("title", binder.getTitle());
		folderElement.addAttribute("id", binder.getId().toString());

			if (binder instanceof Folder) {
			Map options = new HashMap();
			Map folderEntries = getFolderModule().getFullEntries(binder.getId(), options);
			List entrylist = (List)folderEntries.get(ObjectKeys.FULL_ENTRIES);
			Iterator entryIterator = entrylist.listIterator();
			while (entryIterator.hasNext()) {
				FolderEntry entry  = (FolderEntry) entryIterator.next();
				Element entryElem = folderElement.addElement("entry");
				addEntryAttributes(entryElem, entry);
			}
		}
		
		return doc.getRootElement().asXML();
	}

	public String getFolderEntryAsXML(long binderId, long entryId, boolean includeAttachments) {
		Long bId = new Long(binderId);
		Long eId = new Long(entryId);
		
		// Retrieve the raw entry.
		FolderEntry entry = 
			getFolderModule().getEntry(bId, eId);

		Document doc = DocumentHelper.createDocument();
		
		Element entryElem = doc.addElement("entry");

		// Handle structured fields of the entry known at compile time. 
		addEntryAttributes(entryElem, entry);

		// Handle custom fields driven by corresponding definition. 
		addCustomElements(entryElem, entry);
		
		String xml = doc.getRootElement().asXML();
		
		/*
		System.out.println("*** XML representation for entry " + entry.getId());
		System.out.println(xml);
		System.out.println("*** Pretty XML representation for entry " + entry.getId());
		prettyPrint(doc);
		*/
		
		return xml;
	}
	
	private void addEntryAttributes(Element entryElem, FolderEntry entry)
	{
		entryElem.addAttribute("id", entry.getId().toString());
		entryElem.addAttribute("binderId", entry.getParentBinder().getId().toString());
		entryElem.addAttribute("definitionId", entry.getEntryDef().getId());
		entryElem.addAttribute("title", entry.getTitle());
		entryElem.addAttribute("docNumber", entry.getDocNumber());
		entryElem.addAttribute("docLevel", String.valueOf(entry.getDocLevel()));
		String entryUrl = WebUrlUtil.getEntryViewURL(entry);
		entryElem.addAttribute("href", entryUrl);
	}
	private void addEntryAttributes(Element entryElem, Map entry, boolean isPrincipal)
	{
		entryElem.addAttribute("id", (String) entry.get(EntityIndexUtils.DOCID_FIELD));
		entryElem.addAttribute("binderId", (String) entry.get(EntityIndexUtils.BINDER_ID_FIELD));
		entryElem.addAttribute("definitionId", (String) entry.get(EntityIndexUtils.COMMAND_DEFINITION_FIELD));
		entryElem.addAttribute("title", (String) entry.get(EntityIndexUtils.TITLE_FIELD));
		if(isPrincipal) {
			entryElem.addAttribute("type", (String) entry.get(EntityIndexUtils.ENTRY_TYPE_FIELD));
			entryElem.addAttribute("reserved", Boolean.toString(entry.get(ProfileIndexUtils.RESERVEDID_FIELD)!=null));
		} else {
			entryElem.addAttribute("docNumber", (String) entry.get(IndexUtils.DOCNUMBER_FIELD));
			entryElem.addAttribute("docLevel", "" + (new HKey((String) entry.get(IndexUtils.SORTNUMBER_FIELD))).getLevel());

			String entryUrl = WebUrlUtil.getEntryViewURL((String) entry.get(EntityIndexUtils.BINDER_ID_FIELD),
					(String) entry.get(EntityIndexUtils.DOCID_FIELD));
			entryElem.addAttribute("href", entryUrl);
		}
	}

	public long addFolderEntry(long binderId, String definitionId, String inputDataAsXML) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);
		
		Document doc = getDocument(inputDataAsXML);
		SimpleProfiler.setProfiler(new SimpleProfiler("webServices"));
		try {
			return getFolderModule().addEntry(new Long(binderId), definitionId, 
				new DomInputData(doc, getIcalModule()), getFileAttachments("ss_attachFile", new String[]{} )).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		} finally {
//			logger.info(SimpleProfiler.toStr());
			SimpleProfiler.clearProfiler();
		}
	}
	
	public Map getFileAttachments(String fileUploadDataItemName, String[] fileNames)
	{
		return new HashMap();
	}
	
	public abstract void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName);

	public void uploadCalendarEntries(long folderId, String iCalDataAsXML)
	{
		iCalDataAsXML = StringCheckUtil.check(iCalDataAsXML);
		
		Document doc = getDocument(iCalDataAsXML);
		List<Node> entryNodes = (List<Node>) doc.selectNodes("//entry");
		for(Node entryNode : entryNodes) {
			String iCal = entryNode.getText();
			try {
				getIcalModule().parseToEntries(folderId, new StringBufferInputStream(iCal));
			} catch(IOException e) {
				throw new RemotingException(e);
			} catch(ParserException e) {
				throw new RemotingException(e);
			}
		}
	}
	
	public String search(String query, int offset, int maxResults)
	{
		query = StringCheckUtil.check(query);
		
		Document queryDoc = getDocument(query);
		
		Document doc = DocumentHelper.createDocument();
		Element folderElement = doc.addElement("searchResults");

		Map folderEntries = getBinderModule().executeSearchQuery(queryDoc, offset, maxResults);
		List entrylist = (List)folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
		Iterator entryIterator = entrylist.listIterator();
		while (entryIterator.hasNext()) {
			Map result = (Map) entryIterator.next();
			String docType = (String) result.get(BasicIndexUtils.DOC_TYPE_FIELD);
			Element resultElem = null;
			if(BasicIndexUtils.DOC_TYPE_ATTACHMENT.equals(docType)) {
				String attachmentType = "file";
				resultElem = folderElement.addElement(attachmentType);
			} else if(BasicIndexUtils.DOC_TYPE_BINDER.equals(docType)) {
				String binderType = (String) result.get(EntityIndexUtils.ENTITY_FIELD);
				resultElem = folderElement.addElement(docType);
				resultElem.addAttribute("type", binderType);
				resultElem.addAttribute("id", (String) result.get(EntityIndexUtils.DOCID_FIELD));
				resultElem.addAttribute("title", (String) result.get(EntityIndexUtils.TITLE_FIELD));
			} else if(BasicIndexUtils.DOC_TYPE_ENTRY.equals(docType)) {
				String entryType = (String) result.get(EntityIndexUtils.ENTRY_TYPE_FIELD);
				String elementName = null;
				boolean isPrincipal = true;
				if(EntityIndexUtils.ENTRY_TYPE_ENTRY.equalsIgnoreCase(entryType)) {
					elementName="entry";
					isPrincipal = false;
				} else if(EntityIndexUtils.ENTRY_TYPE_REPLY.equalsIgnoreCase(entryType)) {
					elementName="entry";
					isPrincipal = false;					
				} else if(EntityIndexUtils.ENTRY_TYPE_USER.equalsIgnoreCase(entryType)) {
					elementName="principal";
				} else if(EntityIndexUtils.ENTRY_TYPE_GROUP.equalsIgnoreCase(entryType)) {
					elementName="principal";
				}
				resultElem = folderElement.addElement(elementName);
				addEntryAttributes(resultElem, result, isPrincipal);
			}
		}
		
		return doc.getRootElement().asXML();
	}

	public void modifyFolderEntry(long binderId, long entryId, String inputDataAsXML) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);
		
		Document doc = getDocument(inputDataAsXML);
		
		try {
			getFolderModule().modifyEntry(new Long(binderId), new Long(entryId), 
				new DomInputData(doc, getIcalModule()), new HashMap(), null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}			
	}

	/*
	public void deleteFolderEntry(long binderId, long entryId) {
		getFolderModule().deleteEntry(new Long(binderId), new Long(entryId), WebStatusTicket.nullTicket);
	}
	*/
	
	public long addReply(long binderId, long parentId, String definitionId, String inputDataAsXML) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);

		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getFolderModule().addReply(new Long(binderId), new Long(parentId), 
				definitionId, new DomInputData(doc, getIcalModule()), null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	
	private Element addPrincipalToDocument(Branch doc, Principal entry)
	{
		Element entryElem = doc.addElement("principal");
		
		// Handle structured fields of the entry known at compile time. 
		entryElem.addAttribute("id", entry.getId().toString());
		entryElem.addAttribute("binderId", entry.getParentBinder().getId().toString());
		entryElem.addAttribute("definitionId", entry.getEntryDef().getId());
		entryElem.addAttribute("title", entry.getTitle());
		entryElem.addAttribute("type", entry.getEntityType().toString());
		entryElem.addAttribute("disabled", Boolean.toString(entry.isDisabled()));
		entryElem.addAttribute("reserved", Boolean.toString(entry.isReserved()));
		
		return entryElem;
	}
	
	public String getPrincipalAsXML(long binderId, long principalId) {
		Long bId = new Long(binderId);
		Long pId = new Long(principalId);
		
		// Retrieve the raw entry.
		Principal entry = 
			getProfileModule().getEntry(bId, pId);

		Document doc = DocumentHelper.createDocument();
		
		Element entryElem = addPrincipalToDocument(doc, entry);
		
		// Handle custom fields driven by corresponding definition. 
		addCustomElements(entryElem, entry);
		
		String xml = doc.getRootElement().asXML();
		
		return xml;
	}
	
	public long addUser(long binderId, String definitionId, String inputDataAsXML) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);

		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getProfileModule().addUser(new Long(binderId), definitionId, new DomInputData(doc, getIcalModule()), null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public long addGroup(long binderId, String definitionId, String inputDataAsXML) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);

		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getProfileModule().addGroup(new Long(binderId), definitionId, new DomInputData(doc, getIcalModule()), null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public void modifyPrincipal(long binderId, long principalId, String inputDataAsXML) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);

		Document doc = getDocument(inputDataAsXML);
		
		try {
			getProfileModule().modifyEntry(new Long(binderId), new Long(principalId), new DomInputData(doc, getIcalModule()));
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public void deletePrincipal(long binderId, long principalId) {
		try {
			getProfileModule().deleteEntry(new Long(binderId), new Long(principalId), false);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public String getWorkspaceTreeAsXML(long binderId, int levels) {
		com.sitescape.team.domain.Binder binder = null;
		
		if(binderId == -1) {
			binder = getWorkspaceModule().getTopWorkspace();
		} else {
			binder = getBinderModule().getBinder(new Long(binderId));
		}

		Document tree;
		String treeKey = "";
		if (binder instanceof Workspace) {
			tree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsDomTreeBuilder(binder, true, this, treeKey), levels);
		} 
		else {
			//com.sitescape.team.domain.Folder topFolder = ((com.sitescape.team.domain.Folder)binder).getTopFolder();
			tree = getFolderModule().getDomFolderTree(binder.getId(), new WsDomTreeBuilder(binder, false, this, treeKey), levels);
			
			//if (topFolder == null) topFolder = (com.sitescape.team.domain.Folder)binder;
			//tree = getFolderModule().getDomFolderTree(topFolder.getId(), new WsDomTreeBuilder(topFolder, false, this, treeKey));
		}

		String xml = tree.getRootElement().asXML();
		//System.out.println(xml);

		return xml;
	}
	
	public String getTeamMembersAsXML(long binderId)
	{
		Binder binder = getBinderModule().getBinder(new Long(binderId));
		SortedSet<Principal> principals = getBinderModule().getTeamMembers(binder.getId(), true);
		Document doc = DocumentHelper.createDocument();
		Element team = doc.addElement("team");
		team.addAttribute("inherited", binder.isTeamMembershipInherited()?"true":"false");
		for(Principal p : principals) {
			addPrincipalToDocument(team, p);
		}
		
		return doc.getRootElement().asXML();
	}
	
	public String getTeamsAsXML()
	{
		User user = RequestContextHolder.getRequestContext().getUser();
		List<Map> myTeams = getBinderModule().getTeamMemberships(user.getId());
		Document doc = DocumentHelper.createDocument();
		Element teams = doc.addElement("teams");
		teams.addAttribute("principalId", user.getId().toString());
		for(Map binder : myTeams) {
			Element team = teams.addElement("team");
			team.addAttribute("title", (String) binder.get(EntityIndexUtils.TITLE_FIELD));
			team.addAttribute("binderId", (String) binder.get(EntityIndexUtils.DOCID_FIELD));
		}
		return doc.getRootElement().asXML();
	}
	
	private Document getDocument(String xml) {
		// Parse XML string into a document tree.
		try {
			return DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			logger.error(e);
			throw new IllegalArgumentException(e.toString());
		}
	}
	
	private void prettyPrint(Document doc) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		try {
			XMLWriter writer = new XMLWriter(System.out, format);
			writer.write(doc);
		}
		catch(IOException e) {}
	}
	
	private void addCustomElements(final Element entryElem, final com.sitescape.team.domain.Entry entry) {
		final ElementBuilder.BuilderContext context = this;
		DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
			public void visit(Element entryElement, Element flagElement, Map args)
			{
                if (flagElement.attributeValue("apply").equals("true")) {
                	String fieldBuilder = flagElement.attributeValue("elementBuilder");
					String nameValue = DefinitionUtils.getPropertyValue(entryElement, "name");									
					if (Validator.isNull(nameValue)) {nameValue = entryElement.attributeValue("name");}
                	ElementBuilderUtil.buildElement(entryElem, entry, nameValue, fieldBuilder, context);
                }
			}
			public String getFlagElementName() { return "webService"; }
		};
		
		getDefinitionModule().walkDefinition(entry, visitor);
		
	}
}
