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
package org.kablink.teaming.module.binder.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.NonUniqueObjectException;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.InternalException;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.comparator.BinderComparator;
import org.kablink.teaming.comparator.PrincipalComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.ObjectControls;
import org.kablink.teaming.dao.util.SFQuery;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.LibraryEntry;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.NotificationDef;
import org.kablink.teaming.domain.PostingDef;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SimpleName;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.lucene.Hits;
import org.kablink.teaming.lucene.TagObject;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.processor.BinderProcessor;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.definition.export.ElementBuilder;
import org.kablink.teaming.module.definition.export.ElementBuilderUtil;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.ObjectBuilder;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.remoting.RemotingException;
import org.kablink.teaming.remoting.ws.util.DomInputData;
import org.kablink.teaming.runasync.RunAsyncCallback;
import org.kablink.teaming.runasync.RunAsyncManager;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.search.IndexSynchronizationManager;
import org.kablink.teaming.search.LuceneReadSession;
import org.kablink.teaming.search.LuceneWriteSession;
import org.kablink.teaming.search.QueryBuilder;
import org.kablink.teaming.search.SearchObject;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.util.TempFileUtil;
import org.kablink.teaming.util.XmlFileUtil;
import org.kablink.teaming.util.ZipEntryStream;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.ExportHelper;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.FileUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.FileCopyUtils;

import static org.kablink.util.search.Restrictions.between;
import static org.kablink.util.search.Restrictions.eq;
import static org.kablink.util.search.Restrictions.in;

/**
 * @author Janet McCann
 * 
 */
public class BinderModuleImpl extends CommonDependencyInjection implements
		BinderModule {

	private TransactionTemplate transactionTemplate;
	private RunAsyncManager runAsyncManager;

	protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	protected RunAsyncManager getRunAsyncManager() {
		return runAsyncManager;
	}

	public void setRunAsyncManager(RunAsyncManager runAsyncManager) {
		this.runAsyncManager = runAsyncManager;
	}

	/*
	 * Check access to binder.
	 * 
	 * @see
	 * org.kablink.teaming.module.binder.BinderModule#checkAccess(org.kablink
	 * .teaming.domain.Binder, java.lang.String)
	 */
	public boolean testAccess(Binder binder, BinderOperation operation) {
		try {
			checkAccess(binder, operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}

	/**
	 * Use operation so application doesn't have the required knowledge. This
	 * also makes it easier to change what operations and allow multiple
	 * operations need to execute a method.
	 * 
	 * @param binder
	 * @param operation
	 * @throws AccessControlException
	 */
	public void checkAccess(Binder binder, BinderOperation operation)
			throws AccessControlException {
		if (binder instanceof TemplateBinder) {
			getAccessControlManager().checkOperation(
					getCoreDao().loadZoneConfig(
							RequestContextHolder.getRequestContext()
									.getZoneId()),
					WorkAreaOperation.ZONE_ADMINISTRATION);
		} else {
			switch (operation) {
			case addFolder:
				getAccessControlManager().checkOperation(binder,
						WorkAreaOperation.CREATE_FOLDERS);
				break;
			case addWorkspace:
				getAccessControlManager().checkOperation(binder,
						WorkAreaOperation.CREATE_WORKSPACES);
				break;
			case restoreBinder:
			case preDeleteBinder:
			case deleteBinder:
			case indexBinder:
			case indexTree:
			case manageMail:
			case moveBinder:
			case copyBinder:
			case modifyBinder:
			case setProperty:
			case manageConfiguration:
			case manageTeamMembers:
			case manageSimpleName:
			case changeEntryTimestamps:
				getAccessControlManager().checkOperation(binder,
						WorkAreaOperation.BINDER_ADMINISTRATION);
				break;
			case manageTag:
				getAccessControlManager().checkOperation(binder,
						WorkAreaOperation.ADD_COMMUNITY_TAGS);
				break;
			case report:
				getAccessControlManager().checkOperation(binder,
						WorkAreaOperation.GENERATE_REPORTS);
				break;
			case export:
				getAccessControlManager().checkOperation(binder,
						WorkAreaOperation.READ_ENTRIES);
				break;
			default:
				throw new NotSupportedException(operation.toString(),
						"checkAccess");

			}
		}
	}

	private Binder loadBinder(Long binderId) {
		return loadBinder(binderId, RequestContextHolder.getRequestContext()
				.getZoneId());
	}

	private Binder loadBinder(Long binderId, Long zoneId) {
		Binder binder = getCoreDao().loadBinder(binderId, zoneId);
		if (binder.isDeleted())
			throw new NoBinderByTheIdException(binderId);
		return binder;
	}

	private BinderProcessor loadBinderProcessor(Binder binder) {
		// This is nothing but a dispatcher to an appropriate processor.
		// Shared logic, if exists, must be put into the corresponding method in
		// org.kablink.teaming.module.folder.AbstractfolderCoreProcessor class,
		// not
		// in this method.

		return (BinderProcessor) getProcessorManager().getProcessor(binder,
				binder.getProcessorKey(BinderProcessor.PROCESSOR_KEY));

	}

	public Binder getBinder(Long binderId) throws NoBinderByTheIdException,
			AccessControlException {
		Binder binder = loadBinder(binderId);
		// Check if the user has "read" access to the binder.
		if (!(binder instanceof TemplateBinder))
			getAccessControlManager().checkOperation(binder,
					WorkAreaOperation.READ_ENTRIES);

		return binder;
	}

	public boolean checkAccess(Long binderId, User user) {
		boolean value = false;
		Binder binder = null;
		try {
			binder = loadBinder(binderId, user.getZoneId());
		} catch (NoBinderByTheIdException e) {
			return false;
		}

		// Check if the user has "read" access to the binder.
		if (binder != null && !(binder instanceof TemplateBinder))
			value = getAccessControlManager().testOperation(user, binder,
					WorkAreaOperation.READ_ENTRIES);

		return value;
	}

	public SortedSet<Binder> getBinders(Collection<Long> binderIds) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Comparator c = new BinderComparator(user.getLocale(),
				BinderComparator.SortByField.title);
		TreeSet<Binder> result = new TreeSet<Binder>(c);
		for (Long id : binderIds) {
			try {// access check done by getBinder
				// assume most binders are cached
				result.add(getBinder(id));
			} catch (NoObjectByTheIdException ex) {
			} catch (AccessControlException ax) {
			}

		}
		return result;
	}

	// Use search engine
	public Map getBinders(Binder binder, Map options) {
		// assume have access to binder cause have a reference
		BinderProcessor processor = loadBinderProcessor(binder);
		return processor.getBinders(binder, options);
	}

	public Map getBinders(Binder binder, List binderIds, Map options) {
		// assume have access to binder cause have a reference
		BinderProcessor processor = loadBinderProcessor(binder);
		return processor.getBinders(binder, binderIds, options);
	}

	// no transaction by default
	public Binder addBinder(Long parentBinderId, String definitionId,
			InputDataAccessor inputData, Map fileItems, Map options)
			throws AccessControlException, WriteFilesException {
		Binder parentBinder = loadBinder(parentBinderId);
		Definition def = null;
		if (Validator.isNotNull(definitionId)) {
			def = getCoreDao().loadDefinition(definitionId,
					RequestContextHolder.getRequestContext().getZoneId());
		} else {
			def = parentBinder.getEntryDef();
		}

		if (options != null
				&& (options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_DATE) || options
						.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)))
			checkAccess(parentBinder, BinderOperation.changeEntryTimestamps);
		if (def.getType() == Definition.FOLDER_VIEW) {
			checkAccess(parentBinder, BinderOperation.addFolder);
			Binder binder = loadBinderProcessor(parentBinder).addBinder(
					parentBinder, def, Folder.class, inputData, fileItems,
					options);
			if (parentBinder instanceof Folder && parentBinder.isMirrored()
					&& binder.isMirrored()) {
				// Because addBinder method is not running inside a write
				// transaction, setDefinitionsInherited
				// method needs to start its own transaction in order for the
				// changes to be persisted to
				// the database. To do that, we need to invoke the method on the
				// proxy, instead of local
				// invocation.
				((BinderModule) SpringContextUtil.getBean("binderModule"))
						.setDefinitionsInherited(binder.getId(), true);
			}
			return binder;
		} else {
			if (!(parentBinder instanceof Workspace))
				throw new NotSupportedException(
						"errorcode.notsupported.addbinder");
			// allow users workspaces to be created for all users
			if (parentBinder.isReserved()
					&& ObjectKeys.PROFILE_ROOT_INTERNALID.equals(parentBinder
							.getInternalId())) {
				if ((def == null)
						|| (def.getType() != Definition.USER_WORKSPACE_VIEW)) {
					checkAccess(parentBinder, BinderOperation.addWorkspace);
				}
			} else {
				checkAccess(parentBinder, BinderOperation.addWorkspace);
			}
			return loadBinderProcessor(parentBinder).addBinder(parentBinder,
					def, Workspace.class, inputData, fileItems, options);
		}
	}

	public Set<Long> indexTree(Long binderId) {
		Set<Long> ids = new HashSet();
		ids.add(binderId);
		return indexTree(ids, StatusTicket.NULL_TICKET, null);
	}

	// optimization so we can manage the deletion to the searchEngine
	public Set<Long> indexTree(Collection binderIds, StatusTicket statusTicket,
			String[] nodeNames) {
		IndexErrors errors = new IndexErrors();
		return indexTree(binderIds, statusTicket, nodeNames, errors);
	}

	public Set<Long> indexTree(Collection binderIds, StatusTicket statusTicket,
			String[] nodeNames, IndexErrors errors) {
		getCoreDao().flush(); // just incase
		try {
			// make list of binders we have access to first
			boolean clearAll = false;
			List<Binder> binders = getCoreDao().loadObjects(binderIds,
					Binder.class,
					RequestContextHolder.getRequestContext().getZoneId());
			List<Binder> checked = new ArrayList();
			for (Binder binder : binders) {
				try {
					checkAccess(binder, BinderOperation.indexTree);
					if (binder.isDeleted())
						continue;
					if (binder.isZone())
						clearAll = true;
					checked.add(binder);
				} catch (AccessControlException ex) {
					// Skip the ones we cannot access
				} catch (Exception ex) {
					errors.addError(binder);
				}

			}
			Set<Long> done = new HashSet();
			if (checked.isEmpty())
				return done;

			IndexSynchronizationManager.setNodeNames(nodeNames);
			try {
				if (clearAll) {
					LuceneWriteSession luceneSession = getLuceneSessionFactory()
							.openWriteSession(nodeNames);
					try {
						luceneSession.clearIndex();

					} catch (Exception e) {
						logger.info("Exception:" + e);
					} finally {
						luceneSession.close();
					}
				} else {
					// delete all sub-binders - walk the ancestry list
					// and delete all the entries under each folderid.
					for (Binder binder : checked) {
						IndexSynchronizationManager.deleteDocuments(new Term(
								Constants.ENTRY_ANCESTRY, binder.getId()
										.toString()));
					}
				}
				for (Binder binder : checked) {
					done.addAll(loadBinderProcessor(binder).indexTree(binder,
							done, statusTicket, errors));
				}
				// Normally, all updates to the index are managed by the
				// framework so that
				// the index update won't be made until after the related
				// database transaction
				// has committed successfully. This is to avoid the index going
				// out of synch
				// with the database under rollback situation. However, in this
				// particular
				// case, we need to take an exception and flush out all index
				// changes before
				// returning from the method so that the select node ids set
				// above can be
				// applied during the flush. This does not violate the original
				// design intention
				// because, unlike other business operations, this operation is
				// specifically
				// written for index update only, and there is no corresponding
				// update transaction
				// on the database.
				IndexSynchronizationManager.applyChanges();
			} finally {
				IndexSynchronizationManager.clearNodeNames();
			}
			return done;
		} finally {
			// It is important to call this at the end of the processing no
			// matter how it went.
			if (statusTicket != null)
				statusTicket.done();
		}
	}

	public IndexErrors indexBinder(Long binderId) {
		return indexBinder(binderId, false);
	}

	public IndexErrors indexBinder(Long binderId, boolean includeEntries) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.indexBinder);
		return loadBinderProcessor(binder).indexBinder(binder, includeEntries);
	}

	public IndexErrors indexBinderIncremental(Long binderId,
			boolean includeEntries) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.indexBinder);
		return loadBinderProcessor(binder).indexBinderIncremental(binder,
				includeEntries);
	}

	// no transaction
	public void modifyBinder(Long binderId, InputDataAccessor inputData,
			Map fileItems, Collection<String> deleteAttachments, Map options)
			throws AccessControlException, WriteFilesException {
		final Binder binder = loadBinder(binderId);

		if (inputData.exists(ObjectKeys.FIELD_BINDER_MIRRORED)) {
			boolean mirrored = Boolean.valueOf(inputData
					.getSingleValue(ObjectKeys.FIELD_BINDER_MIRRORED));
			if (mirrored && !binder.isMirrored() && binder.getBinderCount() > 0) {
				// We allow changing regular binder to mirrored one only when it
				// has no child binders.
				// It is ok for the binder to have existing entries though.
				throw new NotSupportedException(
						"errorcode.notsupported.not.leaf");
			}
		}

		// save library flag
		boolean oldLibrary = binder.isLibrary();
		boolean oldUnique = binder.isUniqueTitles();

		checkAccess(binder, BinderOperation.modifyBinder);
		List atts = new ArrayList();
		if (deleteAttachments != null) {
			for (String id : deleteAttachments) {
				Attachment a = binder.getAttachment(id);
				if (a != null)
					atts.add(a);
			}
		}
		loadBinderProcessor(binder).modifyBinder(binder, inputData, fileItems,
				atts, options);
		if (inputData.exists(ObjectKeys.FIELD_BINDER_LIBRARY)) {
			final boolean newLibrary = Boolean.valueOf(inputData
					.getSingleValue(ObjectKeys.FIELD_BINDER_LIBRARY));
			if (oldLibrary != newLibrary) {
				// wrap in a transaction
				getTransactionTemplate().execute(new TransactionCallback() {
					public Object doInTransaction(TransactionStatus status) {
						// remove old reserved names
						getCoreDao().clearFileNames(binder);
						if (newLibrary) {
							// add new ones
							// get all attachments in this binder
							FilterControls filter = new FilterControls(
									new String[] { "owner.owningBinderId",
											"type" }, new Object[] {
											binder.getId(), "f" });
							filter.setZoneCheck(false); // skip zone, binder
							// good enough
							ObjectControls objs = new ObjectControls(
									FileAttachment.class, new String[] {
											"fileItem.name", "owner.ownerId" });
							SFQuery query = getCoreDao().queryObjects(objs,
									filter, binder.getZoneId());
							try {
								while (query.hasNext()) {
									Object[] result = (Object[]) query.next();
									// skip files attached to the binder itself
									if (result[1].equals(binder.getId()))
										continue;
									LibraryEntry le = new LibraryEntry(binder
											.getId(), LibraryEntry.FILE,
											(String) result[0]);
									le.setEntityId((Long) result[1]);
									getCoreDao().save(le);
								}
							} catch (HibernateSystemException he) {
								if (he.contains(NonUniqueObjectException.class)) {
									throw new ConfigurationException(
											"errorcode.cannot.make.library",
											(Object[]) null);
								}
							} finally {
								query.close();
							}
						}
						binder.setLibrary(newLibrary);
						return null;
					}
				});

			}
		}
		if (inputData.exists(ObjectKeys.FIELD_BINDER_UNIQUETITLES)) {
			final boolean newUnique = Boolean.valueOf(inputData
					.getSingleValue(ObjectKeys.FIELD_BINDER_UNIQUETITLES));
			if (newUnique != oldUnique) {
				// wrap in a transaction
				getTransactionTemplate().execute(new TransactionCallback() {
					public Object doInTransaction(TransactionStatus status) {
						// remove old reserved names
						getCoreDao().clearTitles(binder);
						if (newUnique) {
							List<Binder> binders = binder.getBinders();
							// first add subfolder titles
							try {
								for (Binder b : binders) {
									getCoreDao().updateTitle(binder, b, null,
											b.getNormalTitle());
								}
							} catch (HibernateSystemException he) {
								if (he.contains(NonUniqueObjectException.class)) {
									throw new ConfigurationException(
											"errorcode.cannot.make.unique",
											(Object[]) null);
								}
							}
							// add entry titles
							if (binder instanceof Folder) {
								Folder parentFolder = (Folder) binder;
								SFQuery query = getFolderDao().queryEntries(
										parentFolder,
										new FilterControls("HKey.level",
												Integer.valueOf(1)));

								try {
									while (query.hasNext()) {
										Object obj = query.next();
										if (obj instanceof Object[])
											obj = ((Object[]) obj)[0];
										FolderEntry entry = (FolderEntry) obj;
										LibraryEntry le = new LibraryEntry(
												binder.getId(),
												LibraryEntry.TITLE, entry
														.getNormalTitle());
										le.setEntityId(entry.getId());
										getCoreDao().save(le);
									}
								} catch (HibernateSystemException he) {
									if (he
											.contains(NonUniqueObjectException.class)) {
										throw new ConfigurationException(
												"errorcode.cannot.make.unique",
												(Object[]) null);
									}

								} finally {
									query.close();
								}
							}
						}
						binder.setUniqueTitles(newUnique);
						return null;
					}
				});

			}
		}
	}

	// inside write transaction
	public void setProperty(Long binderId, String property, Object value) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.setProperty);
		binder.setProperty(property, value);
	}

	// inside write transaction
	public void restoreBinder(Long binderId, Object renameData) {
		restoreBinder(binderId, renameData, true);
	}
	public void restoreBinder(Long binderId, Object renameData, boolean reindex) {
		restoreBinder(binderId, renameData, true, null, reindex);
	}

	// inside write transaction
	public void restoreBinder(Long binderId, Object renameData, boolean deleteMirroredSource, Map options) {
		restoreBinder(binderId, renameData, deleteMirroredSource, options, true);
	}
	public void restoreBinder(Long binderId, Object renameData, boolean deleteMirroredSource, Map options, boolean reindex) {
		Binder binder = loadBinder(binderId);
		if ((null != binder) && (!(binder.isMirrored()))) {
			EntityType et = binder.getEntityType();
			boolean isFolder    = (EntityType.folder    == et);
			boolean isWorkspace = (EntityType.workspace == et);
			if (isFolder || isWorkspace) {
		        checkAccess(binder, BinderOperation.restoreBinder);
		        if (isFolder) {
		        	Folder folder = ((Folder) binder);
		        	folder.setPreDeleted(null);
		        	folder.setPreDeletedWhen(null);
		        	folder.setPreDeletedBy(null);
		        }
		        
		        else {
		        	Workspace ws = ((Workspace) binder);
		        	ws.setPreDeleted(null);
		        	ws.setPreDeletedWhen(null);
		        	ws.setPreDeletedBy(null);
		        }
		        
		        TrashHelper.registerBinderNames(getCoreDao(), binder, renameData);
		        if (reindex) {
		        	loadBinderProcessor(binder).indexBinder(binder, true);
		        }
			}
		}
	}

	// inside write transaction
	public void preDeleteBinder(Long binderId, Long userId) {
		preDeleteBinder(binderId, userId, true);
	}
	public void preDeleteBinder(Long binderId, Long userId, boolean reindex) {
		preDeleteBinder(binderId, userId, true, null, reindex);
	}

	// inside write transaction
	public void preDeleteBinder(Long binderId, Long userId, boolean deleteMirroredSource, Map options) {
		preDeleteBinder(binderId, userId, deleteMirroredSource, options, true);
	}
	public void preDeleteBinder(Long binderId, Long userId, boolean deleteMirroredSource, Map options, boolean reindex) {
		Binder binder = loadBinder(binderId);
		if ((null != binder) && (!(binder.isMirrored()))) {
			EntityType et = binder.getEntityType();
			boolean isFolder    = (EntityType.folder    == et);
			boolean isWorkspace = (EntityType.workspace == et);
			if (isFolder || isWorkspace) {
		        checkAccess(binder, BinderOperation.preDeleteBinder);
		        if (isFolder) {
		        	Folder folder = ((Folder) binder);
		        	folder.setPreDeleted(Boolean.TRUE);
		        	folder.setPreDeletedWhen(System.currentTimeMillis());
		        	folder.setPreDeletedBy(userId);
		        }
		        
		        else {
		        	Workspace ws = ((Workspace) binder);
		        	ws.setPreDeleted(Boolean.TRUE);
		        	ws.setPreDeletedWhen(System.currentTimeMillis());
		        	ws.setPreDeletedBy(userId);
		        }
		        
		        TrashHelper.unRegisterBinderNames(getCoreDao(), binder);
		        if (reindex) {
		        	loadBinderProcessor(binder).indexBinder(binder, true);
		        }
			}
		}
	}

	// no transaction
	public void deleteBinder(Long binderId) {
		deleteBinder(binderId, true, null);
	}

	// no transaction
	public void deleteBinder(Long binderId, boolean deleteMirroredSource,
			Map options) {
		deleteBinderPhase1(binderId, deleteMirroredSource, options);		
		deleteBinderPhase2();
	}

	// inside write transaction
	public void moveBinder(Long fromId, Long toId, Map options) {
		Binder source = loadBinder(fromId);
		checkAccess(source, BinderOperation.moveBinder);
		Binder destination = loadBinder(toId);
		if (source.getEntityType().equals(EntityType.folder)) {
			getAccessControlManager().checkOperation(destination,
					WorkAreaOperation.CREATE_FOLDERS);
		} else {
			getAccessControlManager().checkOperation(destination,
					WorkAreaOperation.CREATE_WORKSPACES);
		}
		// move whole tree at once
		loadBinderProcessor(source).moveBinder(source, destination, options);

	}

	// no transaction
	public Binder copyBinder(Long fromId, Long toId, boolean cascade,
			Map options) {
		Binder source = loadBinder(fromId);
		checkAccess(source, BinderOperation.copyBinder);
		Binder destinationParent = loadBinder(toId);
		if (source.getEntityType().equals(EntityType.folder)) {
			getAccessControlManager().checkOperation(destinationParent,
					WorkAreaOperation.CREATE_FOLDERS);
		} else {
			getAccessControlManager().checkOperation(destinationParent,
					WorkAreaOperation.CREATE_WORKSPACES);
		}
		Map params = new HashMap();
		if (options != null)
			params.putAll(options);
		params.put(ObjectKeys.INPUT_OPTION_FORCE_LOCK, Boolean.TRUE);
		params.put(ObjectKeys.INPUT_OPTION_PRESERVE_DOCNUMBER, Boolean.TRUE);
		// lock top level
		Binder binder = loadBinderProcessor(source).copyBinder(source,
				destinationParent, params);
		if (cascade)
			doCopyChildren(source, binder);
		return binder;
	}

	private void doCopyChildren(Binder source, Binder destinationParent) {
		Map params = new HashMap();
		params.put(ObjectKeys.INPUT_OPTION_FORCE_LOCK, Boolean.FALSE);
		params.put(ObjectKeys.INPUT_OPTION_PRESERVE_DOCNUMBER, Boolean.TRUE);
		List<Binder> children = source.getBinders();
		for (Binder child : children) {
			Binder binder = loadBinderProcessor(child).copyBinder(child,
					destinationParent, params);
			doCopyChildren(child, binder);
		}
	}

	// inside write transaction
	public Binder setDefinitionsInherited(Long binderId,
			boolean inheritFromParent) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageConfiguration);
		boolean oldInherit = binder.isDefinitionsInherited();
		if (inheritFromParent != oldInherit) {
			if (inheritFromParent) {
				// remove old mappings
				Map m = binder.getWorkflowAssociations();
				m.clear();
				binder.setWorkflowAssociations(m);
				List l = binder.getDefinitions();
				l.clear();
				binder.setDefinitions(l);
			} else {
				// copy parents definitions to this binder before changing
				// setting
				binder
						.setWorkflowAssociations(binder
								.getWorkflowAssociations());
				binder.setDefinitions(binder.getDefinitions());
			}
			binder.setDefinitionsInherited(inheritFromParent);
		}
		return binder;

	}

	// inside write transaction
	public Binder setDefinitions(Long binderId, List<String> definitionIds,
			Map<String, String> workflowAssociations)
			throws AccessControlException {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageConfiguration);
		List definitions = new ArrayList();
		Definition def;
		// Build up new set - domain object will handle associations
		if (definitionIds != null) {
			for (String id : definitionIds) {
				try {
					// should be cached
					def = getCoreDao().loadDefinition(
							id,
							RequestContextHolder.getRequestContext()
									.getZoneId());
					definitions.add(def);
				} catch (NoDefinitionByTheIdException nd) {
				}
			}
		}

		binder.setDefinitions(definitions);
		binder.setDefinitionsInherited(false);

		Map wf = new HashMap();
		if (workflowAssociations != null) {
			for (Iterator iter = workflowAssociations.entrySet().iterator(); iter
					.hasNext();) {
				Map.Entry me = (Map.Entry) iter.next();
				try {
					// should be cached
					def = getCoreDao().loadDefinition(
							(String) me.getValue(),
							RequestContextHolder.getRequestContext()
									.getZoneId());
					wf.put(me.getKey(), def);
				} catch (NoDefinitionByTheIdException nd) {
				}
			}
		}
		binder.setWorkflowAssociations(wf);

		BinderProcessor processor = loadBinderProcessor(binder);
		processor.indexBinder(binder, false);
		
		return binder;
	}

	/**
	 * Get tags owned by this binder or current user
	 */
	public Collection<Tag> getTags(Binder binder) {
		// have binder - so assume read access
		// bulk load tags
		return getCoreDao().loadEntityTags(
				binder.getEntityIdentifier(),
				RequestContextHolder.getRequestContext().getUser()
						.getEntityIdentifier());
	}

	/**
	 * Add a new tag, to binder
	 */
	// inside write transaction
	public void setTag(Long binderId, String newTag, boolean community) {
		Binder binder = loadBinder(binderId);
		if (community)
			checkAccess(binder, BinderOperation.manageTag);
		if (Validator.isNull(newTag))
			return;
		Collection<String> newTags = TagUtil.buildTags(newTag);
		if (newTags.size() == 0)
			return;
		User user = RequestContextHolder.getRequestContext().getUser();
		EntityIdentifier uei = user.getEntityIdentifier();
		EntityIdentifier bei = binder.getEntityIdentifier();
		for (String tagName : newTags) {
			Tag tag = new Tag();
			// community tags belong to the binder - don't care who created it
			if (!community)
				tag.setOwnerIdentifier(uei);
			tag.setEntityIdentifier(bei);
			tag.setPublic(community);
			tag.setName(tagName);
			getCoreDao().save(tag);
		}
		loadBinderProcessor(binder).indexBinder(binder, false);
	}

	/**
	 * Delete a tag on this binder
	 */
	// inside write transaction
	public void deleteTag(Long binderId, String tagId) {
		Binder binder = loadBinder(binderId);
		Tag tag;
		try {
			tag = coreDao.loadTag(tagId, binder.getZoneId());
		} catch (Exception ex) {
			return;
		}
		if (tag.isPublic())
			checkAccess(binder, BinderOperation.manageTag);
		else if (!tag.isOwner(RequestContextHolder.getRequestContext()
				.getUser()))
			return;
		getCoreDao().delete(tag);
		loadBinderProcessor(binder).indexBinder(binder, false);
	}

	// inside write transaction
	public void setSubscription(Long binderId, Map<Integer, String[]> styles) {
		Binder binder = getBinder(binderId);
		User user = RequestContextHolder.getRequestContext().getUser();
		Subscription s = getProfileDao().loadSubscription(user.getId(),
				binder.getEntityIdentifier());
		if (styles == null || styles.isEmpty()) {
			if (s != null)
				getCoreDao().delete(s);
		} else if (s == null) {
			s = new Subscription(user.getId(), binder.getEntityIdentifier());
			s.setStyles(styles);
			getCoreDao().save(s);
		} else
			s.setStyles(styles);
	}

	public Subscription getSubscription(Binder binder) {
		User user = RequestContextHolder.getRequestContext().getUser();
		return getProfileDao().loadSubscription(user.getId(),
				binder.getEntityIdentifier());
	}

	public Map executeSearchQuery(Criteria crit, int offset, int maxResults) {
		return executeSearchQuery(crit, offset, maxResults, false);
	}
	public Map executeSearchQuery(Criteria crit, int offset, int maxResults, boolean preDeleted) {
		return executeSearchQuery(crit.toQuery(), offset, maxResults, preDeleted);
	}

	public Map executeSearchQuery(Criteria crit, int offset, int maxResults,
			Long asUserId) {
		return executeSearchQuery(crit, offset, maxResults, asUserId, false);
	}
	public Map executeSearchQuery(Criteria crit, int offset, int maxResults,
			Long asUserId, boolean preDeleted) {
		return executeSearchQuery(crit.toQuery(), offset, maxResults, asUserId, preDeleted);
	}

	public Map executeSearchQuery(Document query, int offset, int maxResults) {
		return executeSearchQuery(query, offset, maxResults, false);
	}
	public Map executeSearchQuery(Document query, int offset, int maxResults, boolean preDeleted) {
		// Create the Lucene query
		QueryBuilder qb = new QueryBuilder(true);
		SearchObject so;
		if (preDeleted) {
			so = qb.buildQueryPreDeleted(query);
		}
		else {
			so = qb.buildQuery(query);
		}

		return executeSearchQuery(so, offset, maxResults);
	}

	public Map executeSearchQuery(Document query, int offset, int maxResults,
			Long asUserId) {
		return executeSearchQuery(query, offset, maxResults, false);
	}
	public Map executeSearchQuery(Document query, int offset, int maxResults,
			Long asUserId, boolean preDeleted) {
		// Create the Lucene query
		QueryBuilder qb = new QueryBuilder(true);
		SearchObject so;
		if (preDeleted) {
			so = qb.buildQueryPreDeleted(query, asUserId);
		}
		else {
			so = qb.buildQuery(query, asUserId);
		}

		return executeSearchQuery(so, offset, maxResults);
	}

	public Map executeSearchQuery(Document searchQuery, Map options) {
		SearchObject so;
		
		Document qTree = SearchUtils.getInitalSearchDocument(searchQuery,
				options);
		SearchUtils.getQueryFields(qTree, options);

		// Create the Lucene query
		QueryBuilder qb = new QueryBuilder(true);
		if (options.containsKey(ObjectKeys.SEARCH_PRE_DELETED)) {
			so = qb.buildQueryPreDeleted(qTree);
		} else {
			so = qb.buildQuery(qTree);
		}

		// Set the sort order
		SortField[] fields = SearchUtils.getSortFields(options);
		so.setSortBy(fields);

		if (logger.isDebugEnabled() && searchQuery != null) {
			logger.debug("Query is in executeSearchQuery: "
					+ searchQuery.asXML());
		}

		int maxResults = 10;
		int offset = 0;
		if (options != null) {
			if (options.containsKey(ObjectKeys.SEARCH_MAX_HITS))
				maxResults = (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS);
			if (options.containsKey(ObjectKeys.SEARCH_OFFSET))
				offset = (Integer) options.get(ObjectKeys.SEARCH_OFFSET);
		}

		return executeSearchQuery(so, offset, maxResults);
	}

	protected Map executeSearchQuery(SearchObject so, int offset, int maxResults) {
		List entries = new ArrayList();
		Hits hits = executeLuceneQuery(so, offset, maxResults);
		entries = SearchUtils.getSearchEntries(hits);
		SearchUtils.extendPrincipalsInfo(entries, getProfileDao(),
				Constants.CREATORID_FIELD);

		Map retMap = new HashMap();
		retMap.put(ObjectKeys.SEARCH_ENTRIES, entries);
		retMap.put(ObjectKeys.SEARCH_COUNT_TOTAL, new Integer(hits
				.getTotalHits()));
		retMap.put(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, new Integer(hits
				.length()));

		return retMap;
	}

	private Hits executeLuceneQuery(SearchObject so, int offset, int maxResults) {
		Hits hits = new Hits(0);

		Query soQuery = so.getQuery(); // Get the query into a variable to avoid
		// doing this very slow operation twice

		if (logger.isDebugEnabled()) {
			logger.debug("Query is in executeSearchQuery: "
					+ soQuery.toString());
		}

		LuceneReadSession luceneSession = getLuceneSessionFactory()
				.openReadSession();
		try {
			hits = luceneSession.search(soQuery, so.getSortBy(), offset,
					maxResults);
		} catch (Exception e) {
			logger.info("Exception:" + e);
		} finally {
			luceneSession.close();
		}
		return hits;

	}

	public List<Map> getSearchTags(String wordroot, String type) {
		ArrayList tags;

		User user = RequestContextHolder.getRequestContext().getUser();
		SearchObject so = null;
		if (!user.isSuper()) {
			// Top of query doc
			Document qTree = DocumentHelper.createDocument();
			qTree.addElement(Constants.QUERY_ELEMENT);
			// Create the query
			QueryBuilder qb = new QueryBuilder(true);
			so = qb.buildQuery(qTree);
		}
		LuceneReadSession luceneSession = getLuceneSessionFactory()
				.openReadSession();

		try {
			tags = luceneSession.getTags(so != null ? so.getQuery() : null,
					wordroot, type);
		} finally {
			luceneSession.close();
		}
		ArrayList tagList = new ArrayList();
		if (tags != null) {
			for (int j = 0; j < tags.size(); j++) {
				HashMap tag = new HashMap();
				String strTag = (String) tags.get(j);
				tag.put(WebKeys.TAG_NAME, strTag);
				tagList.add(tag);
			}
		}
		return tagList;
	}

	public List<Map> getSearchTagsWithFrequencies(String wordroot, String type) {
		ArrayList tags;

		User user = RequestContextHolder.getRequestContext().getUser();
		SearchObject so = null;
		if (!user.isSuper()) {
			// Top of query doc
			Document qTree = DocumentHelper.createDocument();
			qTree.addElement(Constants.QUERY_ELEMENT);
			// Create the query
			QueryBuilder qb = new QueryBuilder(true);
			so = qb.buildQuery(qTree);
		}
		LuceneReadSession luceneSession = getLuceneSessionFactory()
				.openReadSession();

		try {
			tags = luceneSession.getTagsWithFrequency(so != null ? so
					.getQuery() : null, wordroot, type);
		} finally {
			luceneSession.close();
		}
		ArrayList tagList = new ArrayList();
		if (tags != null) {
			for (int j = 0; j < tags.size(); j++) {
				HashMap tag = new HashMap();
				TagObject tagObj = (TagObject) tags.get(j);
				tag.put(WebKeys.TAG_NAME_FREQ, tagObj);
				tagList.add(tag);
			}
		}
		return tagList;
	}

	public Binder getBinderByPathName(String pathName)
			throws AccessControlException {
		List<Binder> binders = getCoreDao().loadObjectsCacheable(Binder.class,
				new FilterControls("lower(pathName)", pathName.toLowerCase()),
				RequestContextHolder.getRequestContext().getZoneId());

		// only maximum of one matching non-deleted binder
		for (Binder binder : binders) {
			if (binder.isDeleted())
				continue;
			getAccessControlManager().checkOperation(binder,
					WorkAreaOperation.READ_ENTRIES);
			return binder;
		}

		return null;
	}

	public SortedSet<Principal> getTeamMembers(Binder binder,
			boolean explodeGroups) {
		// If have binder , can read so no more access checking is needed
		Set ids = binder.getTeamMemberIds();
		// explode groups
		if (explodeGroups)
			ids = getProfileDao().explodeGroups(ids, binder.getZoneId());
		// turn ids into real Principals
		User user = RequestContextHolder.getRequestContext().getUser();
		Comparator c = new PrincipalComparator(user.getLocale());
		TreeSet<Principal> result = new TreeSet<Principal>(c);
		if (explodeGroups) {
			// empty teams can end up in the list of ids, this will prune them
			result.addAll(getProfileDao().loadUsers(ids,
					RequestContextHolder.getRequestContext().getZoneId()));
		} else {
			result
					.addAll(getProfileDao().loadUserPrincipals(
							ids,
							RequestContextHolder.getRequestContext()
									.getZoneId(), true));
		}
		return result;
	}

	public Set<Long> getTeamMemberIds(Long binderId, boolean explodeGroups) {
		// getBinder does read check
		Binder binder = getBinder(binderId);
		Set ids = binder.getTeamMemberIds();
		// explode groups
		if (explodeGroups)
			return getProfileDao().explodeGroups(ids, binder.getZoneId());
		return ids;
	}

	// no transaction
	public void setTeamMembershipInherited(Long binderId, final boolean inherit) {
		final Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageTeamMembers);
		Boolean index = (Boolean) getTransactionTemplate().execute(
				new TransactionCallback() {
					public Object doInTransaction(TransactionStatus status) {
						Set oldMbrs = binder.getTeamMemberIds();
						if (inherit) {
							binder.setTeamMemberIds(null);
						} else if (binder.isTeamMembershipInherited()) {
							// going from was inheriting to not inheriting =>
							// copy
							Set ids = new HashSet(binder.getTeamMemberIds());
							binder.setTeamMemberIds(ids);
						}
						// see if there is a real change
						if (binder.isTeamMembershipInherited() != inherit) {
							binder.setTeamMembershipInherited(inherit);
							if (!(binder instanceof TemplateBinder)) {
								User user = RequestContextHolder
										.getRequestContext().getUser();
								binder.incrLogVersion();
								binder.setModification(new HistoryStamp(user));
								BinderProcessor processor = loadBinderProcessor(binder);
								processor.processChangeLog(binder,
										ChangeLog.ACCESSMODIFY);
								// Always reindex top binder to update the team
								// members field
								processor.indexBinder(binder, false);
								// just changed from not inheritting to inherit
								// = need to update index acls
								// if changed from inherit to not, acls remains
								// the same
								if (inherit
										&& !oldMbrs.equals(binder
												.getTeamMemberIds()))
									return Boolean.TRUE;
							}
						}
						return Boolean.FALSE;
					}
				});
		// only index if change occured
		if (index) {
			loadBinderProcessor(binder).indexTeamMembership(binder, true);
		}

	}

	// no transaction
	public void setTeamMembers(Long binderId, final Collection<Long> memberIds)
			throws AccessControlException {
		final Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageTeamMembers);
		if (binder.getTeamMemberIds().equals(memberIds))
			return;
		final BinderProcessor processor = loadBinderProcessor(binder);
		Boolean index = (Boolean) getTransactionTemplate().execute(
				new TransactionCallback() {
					public Object doInTransaction(TransactionStatus status) {
						binder.setTeamMemberIds(new HashSet(memberIds));
						binder.setTeamMembershipInherited(false);
						if (!(binder instanceof TemplateBinder)) {
							User user = RequestContextHolder
									.getRequestContext().getUser();
							binder.incrLogVersion();
							binder.setModification(new HistoryStamp(user));
							processor.processChangeLog(binder,
									ChangeLog.ACCESSMODIFY);
							return Boolean.TRUE;
						}
						return Boolean.FALSE;
					}
				});
		if (index) {
			// Always reindex top binder to update the team members field
			processor.indexBinder(binder, false);
			// update readAcl on binders and entries
			processor.indexTeamMembership(binder, true);
		}
	}

	// return binders this user is a team_member of
	public List<Map> getTeamMemberships(Long userId) {

		// We use search engine to get the list of binders.
		Criteria crit = new Criteria().add(
				eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER))
				.addOrder(new Order(Constants.SORT_TITLE_FIELD, true));

		// look for user
		Principal prin = RequestContextHolder.getRequestContext().getUser();
		if (!userId.equals(prin.getId())) {
			try {
				prin = getProfileDao().loadPrincipal(userId, prin.getZoneId(),
						true);
			} catch (Exception e) {
				return Collections.EMPTY_LIST;
			}
		}
		Set<Long> ids = getProfileDao().getPrincipalIds(prin);
		if (ids.isEmpty())
			return Collections.EMPTY_LIST;
		crit.add(in(Constants.TEAM_MEMBERS_FIELD, LongIdUtil
				.getIdsAsStringSet(ids)));
		QueryBuilder qb = new QueryBuilder(true);
		SearchObject so = qb.buildQuery(crit.toQuery());

		Hits hits = executeLuceneQuery(so, 0, Integer.MAX_VALUE);
		if (hits == null)
			return new ArrayList();
		return SearchUtils.getSearchEntries(hits);
	}

	// inside write transaction
	public void setPosting(Long binderId, String emailAddress, String password) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageMail);
		PostingDef post = binder.getPosting();
		if (Validator.isNull(emailAddress)) {
			// if posting exists for this binder, remove it
			if (post == null)
				return;
			binder.setPosting(null);
			getCoreDao().delete(post);
			return;
		} else {
			// see if it exists already
			emailAddress = emailAddress.toLowerCase();
			// see if assigned to someone else
			if ((post == null) || !emailAddress.equals(post.getEmailAddress())) {
				List results = getCoreDao().loadObjects(PostingDef.class,
						new FilterControls("emailAddress", emailAddress),
						binder.getZoneId());
				if (!results.isEmpty()) {
					// exists, see if it is assigned
					PostingDef oldPost = (PostingDef) results.get(0);
					// if address is assigned, cannot continue
					if (oldPost.getBinder() != null) {
						if (!oldPost.getBinder().equals(binder)) {
							throw new NotSupportedException(
									"errorcode.posting.assigned",
									new String[] { emailAddress });
						}
					}
					if (post != null)
						getCoreDao().delete(post);
					post = oldPost;
				}
			}
		}
		if (post == null) {
			post = new PostingDef();
			post
					.setZoneId(RequestContextHolder.getRequestContext()
							.getZoneId());
			getCoreDao().save(post);
		}
		post.setBinder(binder);
		post.setEnabled(true);
		post.setReplyPostingOption(PostingDef.REPLY_POST_AS_A_REPLY);
		post.setEmailAddress(emailAddress);
		post.setCredentials(password);
		binder.setPosting(post);
	}

	/**
	 * Set the notification definition for a folder.
	 * 
	 * @param id
	 * @param updates
	 * @param principals
	 *            - if null, don't change list.
	 */
	// inside write transaction
	public void modifyNotification(Long binderId,
			Collection<Long> principalIds, Map updates) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageMail);
		NotificationDef current = binder.getNotificationDef();
		if (current == null) {
			current = new NotificationDef();
			binder.setNotificationDef(current);
		}
		ObjectBuilder.updateObject(current, updates);
		if (principalIds == null)
			return;
		// Pre-load for performance
		List notifyUsers = getProfileDao().loadUserPrincipals(principalIds,
				binder.getZoneId(), true);
		current.setDistribution(notifyUsers);
	}

	public org.dom4j.Document getDomBinderTree(Long id,
			DomTreeBuilder domTreeHelper, int levels)
			throws AccessControlException {
		// getWorkspace does access check
		Binder top = getBinder(id);

		User user = RequestContextHolder.getRequestContext().getUser();
		Comparator c = new BinderComparator(user.getLocale(),
				BinderComparator.SortByField.searchTitle);
		Document wsTree = DocumentHelper.createDocument();
		Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
		buildBinderDomTree(rootElement, top, c, domTreeHelper, levels);
		return wsTree;
	}

	public org.dom4j.Document getDomBinderTree(Long topId, Long bottomId,
			DomTreeBuilder domTreeHelper) throws AccessControlException {
		User user = RequestContextHolder.getRequestContext().getUser();
		// getWorkspace does access check
		Binder top = getBinder(topId);
		Binder bottom = (Binder) getCoreDao().loadBinder(bottomId,
				user.getZoneId());

		List<Binder> ancestors = new ArrayList<Binder>();
		Binder parent = bottom;
		// build inverted list of parents
		while ((parent != null) && !parent.equals(top)) {
			ancestors.add(parent);
			parent = (Binder) parent.getParentBinder();
		}
		if (parent == null)
			throw new InternalException("Top is not a parent");
		ancestors.add(parent);
		Comparator c = new BinderComparator(user.getLocale(),
				BinderComparator.SortByField.searchTitle);
		Document wsTree = DocumentHelper.createDocument();
		Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
		for (int i = ancestors.size() - 1; i >= 0; --i) {
			buildBinderDomTree(rootElement, (Binder) ancestors.get(i), c,
					domTreeHelper, 1);
			if (i != 0) {
				parent = ancestors.get(i - 1);
				String parentId = parent.getId().toString();
				Iterator itRootElements = rootElement.selectNodes(
						"./" + DomTreeBuilder.NODE_CHILD).iterator();
				rootElement = null;
				while (itRootElements.hasNext()) {
					Element childNode = (Element) itRootElements.next();
					String id = childNode.attributeValue("id");
					int n = id.indexOf(".");
					if (n >= 0)
						id = id.substring(0, n);
					if (id.equals(parentId)) {
						rootElement = childNode;
						break;
					}
				}
				if (rootElement == null)
					break;
			}
		}
		return wsTree;
	}

	protected void buildBinderDomTree(Element current, Binder top,
			Comparator c, DomTreeBuilder domTreeHelper, int levels) {
		Element next;
		int maxBucketSize = SPropsUtil.getInt("wsTree.maxBucketSize");
		int domTreeType;
		if (EntityIdentifier.EntityType.folder.equals(top.getEntityType())) {
			domTreeType = DomTreeBuilder.TYPE_FOLDER;
		} else {
			domTreeType = DomTreeBuilder.TYPE_WORKSPACE;
		}

		// callback to setup tree
		domTreeHelper.setupDomElement(domTreeType, top, current);
		if (levels == 0)
			return;
		--levels;
		TreeSet ws = new TreeSet(c);
		List searchBinders = null;
		if (levels >= 0
				&& (!domTreeHelper.getPage().equals("") || top.getBinderCount() > maxBucketSize)) { // what
			// is
			// the
			// best
			// number
			// to
			// avoid
			// search??
			// do search
			if (domTreeHelper.getPage().equals("")) {
				Map options = new HashMap();
				options.put(ObjectKeys.SEARCH_MAX_HITS, Integer
						.valueOf(SPropsUtil.getInt("wsTree.maxBucketSize")));
				Map searchResults = getBinders(top, options);
				searchBinders = (List) searchResults
						.get(ObjectKeys.SEARCH_ENTRIES);
				int results = (Integer) searchResults
						.get(ObjectKeys.TOTAL_SEARCH_COUNT);
				if (results > SPropsUtil.getInt("wsTree.maxBucketSize")) { // just
					// to
					// get
					// started
					searchResults = buildBinderVirtualTree(current, top,
							domTreeHelper, results, maxBucketSize);
					// If no results are returned, the work was completed in
					// buildBinderVirtualTree and we can exit now
					if (searchResults == null)
						return;
					searchBinders = (List) searchResults
							.get(ObjectKeys.SEARCH_ENTRIES);
				}
			} else {
				// We are looking for a virtual page
				Map searchResults = buildBinderVirtualTree(current, top,
						domTreeHelper, 0, maxBucketSize);
				// If no results are returned, the work was completed in
				// buildBinderVirtualTree and we can exit now
				if (searchResults == null)
					return;
				searchBinders = (List) searchResults
						.get(ObjectKeys.SEARCH_ENTRIES);
			}
			if (domTreeHelper.supportsType(DomTreeBuilder.TYPE_FOLDER, null)) {
				// get folders
				for (int i = 0; i < searchBinders.size(); ++i) {
					Map search = (Map) searchBinders.get(i);
					String entityType = (String) search
							.get(Constants.ENTITY_FIELD);
					if (EntityType.folder.name().equals(entityType)) {
						String sId = (String) search.get(Constants.DOCID_FIELD);
						try {
							Long id = Long.valueOf(sId);
							Object obj = getCoreDao().load(Folder.class, id);
							if (obj != null)
								ws.add(obj);
						} catch (Exception ex) {
							continue;
						}
					}
				}
				for (Iterator iter = ws.iterator(); iter.hasNext();) {
					Folder f = (Folder) iter.next();
					if (f.isDeleted() || f.isPreDeleted())
						continue;
					// Check if the user has "read" access to the folder.
					next = current.addElement(DomTreeBuilder.NODE_CHILD);
					if (domTreeHelper.setupDomElement(
							DomTreeBuilder.TYPE_FOLDER, f, next) == null)
						current.remove(next);
				}
			}
			ws.clear();
			// get workspaces
			for (int i = 0; i < searchBinders.size(); ++i) {
				Map search = (Map) searchBinders.get(i);
				String entityType = (String) search.get(Constants.ENTITY_FIELD);
				if (EntityType.workspace.name().equals(entityType)) {
					String sId = (String) search.get(Constants.DOCID_FIELD);
					try {
						Long id = Long.valueOf(sId);
						Object obj = getCoreDao().load(Workspace.class, id);
						if (obj != null)
							ws.add(obj);
					} catch (Exception ex) {
						continue;
					}
				}
			}

			for (Iterator iter = ws.iterator(); iter.hasNext();) {
				Workspace w = (Workspace) iter.next();
				if (w.isDeleted() || w.isPreDeleted())
					continue;
				next = current.addElement(DomTreeBuilder.NODE_CHILD);
				buildBinderDomTree(next, w, c, domTreeHelper, levels);
			}
		} else {
			if (domTreeHelper.supportsType(DomTreeBuilder.TYPE_FOLDER, null)) {
				// get folders sorted
				if (EntityIdentifier.EntityType.workspace.equals(top
						.getEntityType())
						|| EntityIdentifier.EntityType.profiles.equals(top
								.getEntityType())) {
					ws.addAll(((Workspace) top).getFolders());
				} else if (EntityIdentifier.EntityType.folder.equals(top
						.getEntityType())) {
					ws.addAll(((Folder) top).getFolders());
				}
				for (Iterator iter = ws.iterator(); iter.hasNext();) {
					Folder f = (Folder) iter.next();
					if (f.isDeleted() || f.isPreDeleted())
						continue;
					// Check if the user has "read" access to the folder.
					if (!getAccessControlManager().testOperation(f,
							WorkAreaOperation.READ_ENTRIES))
						continue;
					next = current.addElement(DomTreeBuilder.NODE_CHILD);
					if (domTreeHelper.setupDomElement(
							DomTreeBuilder.TYPE_FOLDER, f, next) == null)
						current.remove(next);
				}
			}
			ws.clear();
			// handle sorted workspaces
			if (EntityIdentifier.EntityType.workspace.equals(top
					.getEntityType())
					|| EntityIdentifier.EntityType.profiles.equals(top
							.getEntityType())) {
				ws.addAll(((Workspace) top).getWorkspaces());
				for (Iterator iter = ws.iterator(); iter.hasNext();) {
					Workspace w = (Workspace) iter.next();
					if (w.isDeleted() || w.isPreDeleted())
						continue;
					// Check if the user has "read" access to the workspace.
					if (!getAccessControlManager().testOperation(w,
							WorkAreaOperation.READ_ENTRIES))
						continue;
					next = current.addElement(DomTreeBuilder.NODE_CHILD);
					buildBinderDomTree(next, w, c, domTreeHelper, levels);
				}
			}
		}

	}

	// Build a list of buckets (or get the final page)
	/**
	 * protected Map buildBinderVirtualTree(Element current, Binder top,
	 * DomTreeBuilder domTreeHelper, int totalHits, int maxBucketSize) { Element
	 * next; int skipLength = maxBucketSize; if (totalHits > maxBucketSize) {
	 * skipLength = totalHits / maxBucketSize; if (skipLength < maxBucketSize)
	 * skipLength = maxBucketSize; }
	 * 
	 * //See if this has a page already set List tuple =
	 * domTreeHelper.getTuple(); String tuple1 = ""; String tuple2 = ""; if
	 * (tuple != null && tuple.size() >= 2) { tuple1 = (String) tuple.get(0);
	 * tuple2 = (String) tuple.get(1); }
	 * 
	 * Document queryTree = DocumentHelper.createDocument(); Element
	 * qTreeRootElement = queryTree.addElement(Constants.QUERY_ELEMENT); Element
	 * qTreeAndElement = qTreeRootElement.addElement(Constants.AND_ELEMENT);
	 * 
	 * Element field = qTreeAndElement.addElement(Constants.FIELD_ELEMENT);
	 * field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.
	 * BINDERS_PARENT_ID_FIELD); Element child =
	 * field.addElement(Constants.FIELD_TERMS_ELEMENT);
	 * child.setText(top.getId().toString());
	 * 
	 * field = qTreeAndElement.addElement(Constants.FIELD_ELEMENT);
	 * field.addAttribute
	 * (Constants.FIELD_NAME_ATTRIBUTE,Constants.DOC_TYPE_FIELD); child =
	 * field.addElement(Constants.FIELD_TERMS_ELEMENT);
	 * child.setText(Constants.DOC_TYPE_BINDER); //Create the Lucene query
	 * QueryBuilder qb = new QueryBuilder(true); SearchObject so =
	 * qb.buildQuery(queryTree); if(logger.isDebugEnabled()) {
	 * logger.debug("Query is: " + queryTree.asXML()); }
	 * 
	 * //Set the sort order SortField[] fields = new SortField[1]; String sortBy
	 * = Constants.NORM_TITLE;
	 * 
	 * fields[0] = new SortField(sortBy, SortField.AUTO, true);
	 * so.setSortBy(fields); Query soQuery = so.getQuery(); //Get the query into
	 * a variable to avoid doing this very slow operation twice
	 * if(logger.isDebugEnabled()) { logger.debug("Query is: " +
	 * soQuery.toString()); }
	 * 
	 * //Before doing the search, create another query in case the buckets are
	 * exhausted Document queryTreeFinal = DocumentHelper.createDocument();
	 * qTreeRootElement = queryTreeFinal.addElement(Constants.QUERY_ELEMENT);
	 * qTreeAndElement = qTreeRootElement.addElement(Constants.AND_ELEMENT);
	 * 
	 * field = qTreeAndElement.addElement(Constants.FIELD_ELEMENT);
	 * field.addAttribute
	 * (Constants.FIELD_NAME_ATTRIBUTE,Constants.BINDERS_PARENT_ID_FIELD); child
	 * = field.addElement(Constants.FIELD_TERMS_ELEMENT);
	 * child.setText(top.getId().toString());
	 * 
	 * field = qTreeAndElement.addElement(Constants.FIELD_ELEMENT);
	 * field.addAttribute
	 * (Constants.FIELD_NAME_ATTRIBUTE,Constants.DOC_TYPE_FIELD); child =
	 * field.addElement(Constants.FIELD_TERMS_ELEMENT);
	 * child.setText(Constants.DOC_TYPE_BINDER);
	 * 
	 * QueryBuilder qbFinal = new QueryBuilder(true); SearchObject
	 * singleBucketSO = qbFinal.buildQuery(queryTreeFinal);
	 * 
	 * Element range = qTreeAndElement.addElement(Constants.RANGE_ELEMENT);
	 * range.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, Constants.NORM_TITLE);
	 * range.addAttribute(Constants.INCLUSIVE_ATTRIBUTE,
	 * Constants.INCLUSIVE_TRUE); Element start =
	 * range.addElement(Constants.RANGE_START); start.setText(tuple1); Element
	 * end = range.addElement(Constants.RANGE_FINISH); end.setText(tuple2);
	 * 
	 * //Create the Lucene query SearchObject soFinal =
	 * qbFinal.buildQuery(queryTreeFinal); if(logger.isDebugEnabled()) {
	 * logger.debug("Final query is: " + queryTreeFinal.asXML()); }
	 * 
	 * //Set the sort order SortField[] fieldsFinal = new SortField[1]; String
	 * sortByFinal = Constants.NORM_TITLE;
	 * 
	 * fieldsFinal[0] = new SortField(sortByFinal, SortField.AUTO, true);
	 * soFinal.setSortBy(fieldsFinal); Query soQueryFinal = soFinal.getQuery();
	 * //Get the query into a variable to avoid doing this very slow operation
	 * twice if(logger.isDebugEnabled()) { logger.debug("Query is: " +
	 * soQueryFinal.toString()); } LuceneReadSession luceneSession =
	 * getLuceneSessionFactory().openReadSession();
	 * 
	 * List results = new ArrayList(); Hits hits = null; try { if (totalHits ==
	 * 0) { //We have to figure out the size of the pool before building the
	 * buckets Hits testHits = luceneSession.search(soQueryFinal,
	 * soFinal.getSortBy(), 0, maxBucketSize); totalHits =
	 * testHits.getTotalHits(); if (totalHits > maxBucketSize) { skipLength =
	 * testHits.getTotalHits() / maxBucketSize; if (skipLength < maxBucketSize)
	 * skipLength = maxBucketSize; } } if (totalHits > skipLength) results =
	 * luceneSession.getNormTitles(soQuery, tuple1, tuple2, skipLength); if
	 * (results == null || results.size() <= 1) { //We must be at the end of the
	 * buckets; now get the real entries if ("".equals(tuple1) &&
	 * "".equals(tuple2)) { singleBucketSO.setSortBy(fieldsFinal); soQueryFinal
	 * = singleBucketSO.getQuery(); //Get the query into a variable to avoid
	 * doing this very slow operation twice } hits =
	 * luceneSession.search(soQueryFinal, soFinal.getSortBy(), 0, -1); } }
	 * finally { luceneSession.close(); } //See if we are at the end of the
	 * bucket search if (hits != null) { List entries =
	 * SearchUtils.getSearchEntries(hits);
	 * //SearchUtils.extendPrincipalsInfo(entries, getProfileDao());
	 * 
	 * Map retMap = new HashMap();
	 * retMap.put(ObjectKeys.SEARCH_ENTRIES,entries);
	 * retMap.put(ObjectKeys.SEARCH_COUNT_TOTAL, new
	 * Integer(hits.getTotalHits()));
	 * retMap.put(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, new
	 * Integer(hits.length()));
	 * 
	 * domTreeHelper.setPage(""); return retMap; } //Build the virtual tree
	 * String page = domTreeHelper.getPage(); if (!page.equals("")) page += ".";
	 * for (int i = 0; i < results.size(); i++) { List result = (List)
	 * results.get(i); Map skipMap = new HashMap();
	 * skipMap.put(DomTreeBuilder.SKIP_TUPLE, result);
	 * skipMap.put(DomTreeBuilder.SKIP_PAGE, page + String.valueOf(i));
	 * skipMap.put(DomTreeBuilder.SKIP_BINDER_ID, top.getId().toString()); next
	 * = current.addElement(DomTreeBuilder.NODE_CHILD); if
	 * (domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_SKIPLIST, skipMap,
	 * next) == null) current.remove(next); } return null; }
	 **/
	// Build a list of buckets (or get the final page)
	protected Map buildBinderVirtualTree(Element current, Binder top,
			DomTreeBuilder domTreeHelper, int totalHits, int maxBucketSize) {
		Element next;
		int skipLength = maxBucketSize;
		if (totalHits > maxBucketSize) {
			skipLength = totalHits / maxBucketSize;
			if (skipLength < maxBucketSize)
				skipLength = maxBucketSize;
		}

		// See if this has a page already set
		List tuple = domTreeHelper.getTuple();
		String tuple1 = "";
		String tuple2 = "";
		if (tuple != null && tuple.size() >= 2) {
			tuple1 = (String) tuple.get(0);
			tuple2 = (String) tuple.get(1);
		}
		QueryBuilder qb = new QueryBuilder(true);

		List results = new ArrayList();
		Hits hits = null;
		LuceneReadSession luceneSession = getLuceneSessionFactory()
				.openReadSession();
		try {
			Criteria crit = new Criteria().add(
					eq(Constants.BINDERS_PARENT_ID_FIELD, top.getId()
							.toString())).add(
					eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER));
			if (totalHits == 0) {
				crit.add(between(Constants.NORM_TITLE, tuple1, tuple2))
						.addOrder(new Order(Constants.NORM_TITLE, true));

				// Create the Lucene query
				SearchObject searchObject = qb.buildQuery(crit.toQuery());
				Query query = searchObject.getQuery(); // Get the query into a
				// variable to avoid
				// doing this very slow
				// operation twice
				if (logger.isDebugEnabled()) {
					logger.debug("Query is in executeSearchQuery: "
							+ query.toString());
				}

				// We have to figure out the size of the pool before building
				// the buckets
				Hits testHits = luceneSession.search(query, searchObject
						.getSortBy(), 0, maxBucketSize);
				totalHits = testHits.getTotalHits();
				if (totalHits > maxBucketSize) {
					skipLength = testHits.getTotalHits() / maxBucketSize;
					if (skipLength < maxBucketSize)
						skipLength = maxBucketSize;
				}
			}
			if (totalHits > skipLength) {
				SearchObject searchObject = qb.buildQuery(crit.toQuery());
				Query query = searchObject.getQuery(); // Get the query into a
				// variable to avoid
				// doing this very slow
				// operation twice
				if (logger.isDebugEnabled()) {
					logger.debug("Query is: " + searchObject.toString());
				}
				// no order here
				results = luceneSession.getNormTitles(query, tuple1, tuple2,
						skipLength);
			}
			if (results == null || results.size() <= 1) {
				// We must be at the end of the buckets; now get the real
				// entries
				if ("".equals(tuple1) && "".equals(tuple2)) {
					crit.addOrder(new Order(Constants.NORM_TITLE, true));
				} else {
					crit.add(between(Constants.NORM_TITLE, tuple1, tuple2))
							.addOrder(new Order(Constants.NORM_TITLE, true));

				}
				SearchObject searchObject = qb.buildQuery(crit.toQuery());
				Query query = searchObject.getQuery(); // Get the query into a
				// variable to avoid
				// doing this very slow
				// operation twice
				if (logger.isDebugEnabled()) {
					logger.debug("Query is in executeSearchQuery: "
							+ query.toString());
				}
				hits = luceneSession.search(query, searchObject.getSortBy(), 0,
						-1);
			}
		} finally {
			luceneSession.close();
		}
		// See if we are at the end of the bucket search
		if (hits != null) {
			List entries = SearchUtils.getSearchEntries(hits);
			// SearchUtils.extendPrincipalsInfo(entries, getProfileDao());

			Map retMap = new HashMap();
			retMap.put(ObjectKeys.SEARCH_ENTRIES, entries);
			retMap.put(ObjectKeys.SEARCH_COUNT_TOTAL, new Integer(hits
					.getTotalHits()));
			retMap.put(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, new Integer(
					hits.length()));

			domTreeHelper.setPage("");
			return retMap;
		}
		// Build the virtual tree
		String page = domTreeHelper.getPage();
		if (!page.equals(""))
			page += ".";
		for (int i = 0; i < results.size(); i++) {
			List result = (List) results.get(i);
			Map skipMap = new HashMap();
			skipMap.put(DomTreeBuilder.SKIP_TUPLE, result);
			skipMap.put(DomTreeBuilder.SKIP_PAGE, page + String.valueOf(i));
			skipMap.put(DomTreeBuilder.SKIP_BINDER_ID, top.getId().toString());
			next = current.addElement(DomTreeBuilder.NODE_CHILD);
			if (domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_SKIPLIST,
					skipMap, next) == null)
				current.remove(next);
		}
		return null;
	}

	// Build a sorted list of children binders and their recent entries
	public SortedSet buildWorkspaceBinderMap(Long id) {
		SortedSet binderMap = new TreeSet();
		return binderMap;
	}

	public SimpleName getSimpleName(String name) {
		// Do we need access check here or not?
		return getCoreDao().loadSimpleName(name.toLowerCase(),
				RequestContextHolder.getRequestContext().getZoneId());
	}

	public SimpleName getSimpleNameByEmailAddress(String emailAddress) {
		// Do we need access check here or not?
		return getCoreDao().loadSimpleNameByEmailAddress(
				emailAddress.toLowerCase(),
				RequestContextHolder.getRequestContext().getZoneId());
	}

	public void addSimpleName(String name, Long binderId, String binderType) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.manageSimpleName);
		SimpleName simpleName = new SimpleName(RequestContextHolder
				.getRequestContext().getZoneId(), name.toLowerCase(), binderId,
				binderType);
		getCoreDao().save(simpleName);
	}

	public void deleteSimpleName(String name) {
		SimpleName simpleName = getCoreDao().loadSimpleName(name.toLowerCase(),
				RequestContextHolder.getRequestContext().getZoneId());
		Binder binder = loadBinder(simpleName.getBinderId());
		checkAccess(binder, BinderOperation.manageSimpleName);
		getCoreDao().delete(simpleName);
	}

	public List<SimpleName> getSimpleNames(Long binderId) {
		return getCoreDao().loadSimpleNames(binderId,
				RequestContextHolder.getRequestContext().getZoneId());
	}

	// no transaction
	public void setPostingEnabled(Long binderId, final Boolean postingEnabled)
			throws AccessControlException {
		final Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.modifyBinder);
		getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				binder.setPostingEnabled(postingEnabled);
				return postingEnabled;
			}
		});
	}

	public void export(Long binderId, Long entityId, OutputStream out,
			Map options, Collection<Long> binderIds, Boolean noSubBinders, 
			StatusTicket statusTicket, Map reportMap) throws Exception {

		Binder binder = loadBinder(binderId);
		checkAccess(binder, BinderOperation.modifyBinder);
		
		ExportHelper.export(binderId, entityId, out, options, binderIds, noSubBinders, 
				statusTicket, reportMap);

	}

	public void importZip(Long binderId, InputStream fIn, StatusTicket statusTicket, 
			Map reportMap) throws IOException {
		ExportHelper.importZip(binderId, fIn, statusTicket, reportMap);
	}


	public String filename8BitSingleByteOnly(FileAttachment attachment,
			boolean _8BitSingleByteOnly) {
		String fileName = attachment.getFileItem().getName();

		String fileExt = EntityIndexUtils.getFileExtension(attachment
				.getFileItem().getName());

		if (!_8BitSingleByteOnly) {
			return fileName;
		} else {
			for (int i = 0; i < fileName.length(); i++) {
				int c = (int) fileName.charAt(i);

				if (c >= 0 && c < 256) {
					// it's ok
				} else
					return attachment.getId() + "." + fileExt;
			}

			return fileName;
		}
	}
	
	public Long getZoneBinderId(Long binderId, String zoneUUID, String entityType) {
		if (Validator.isNull(zoneUUID)) return binderId;
		List<Long> ids = getCoreDao().findZoneEntityIds(binderId, zoneUUID, entityType);
		if (ids.isEmpty()) return null;
		return ids.get(0);
	}
	
	// no transaction
	protected void deleteBinderPhase1(Long binderId, final boolean deleteMirroredSource,
			final Map options) {
		final Binder top = loadBinder(binderId);
		checkAccess(top, BinderOperation.deleteBinder);
		Map params = new HashMap();
		params.put("deleted", Boolean.FALSE);
		// get list of ids, so don't have to load large trees all at once
		List<Object[]> objs = getCoreDao()
				.loadObjects(
						"select x.id,x.binderKey.sortKey from org.kablink.teaming.domain.Binder x where x.binderKey.sortKey like '"
								+ top.getBinderKey().getSortKey()
								+ "%' and deleted=:deleted order by x.binderKey.sortKey desc",
						params);
		// convert to list of ids
		List<Long> ids = new ArrayList();// maintain order, bottom up
		for (Object row[] : objs) {
			ids.add((Long) row[0]);
		}
		ids.remove(top.getId());
		for (Long id : ids) {
			try {
				final Binder child = getCoreDao().loadBinder(id,
						top.getZoneId());
				if (child.isDeleted())
					continue;
				checkAccess(child, BinderOperation.deleteBinder);
				// determine if need to deleted mirrored for this binder, or if
				// parent will take care of it
				boolean deleteMirroredSourceForChildren = deleteMirroredSource;
				if (top.isMirrored() && deleteMirroredSource)
					deleteMirroredSourceForChildren = false;
				if (deleteMirroredSourceForChildren == true) {
					// top is not mirrored; see if a parent is
					Binder parent = child.getParentBinder();
					while (parent != top) {
						if (parent.isMirrored()) {
							deleteMirroredSourceForChildren = false;
							break;
						}
						parent = parent.getParentBinder();
					}
				}
				final boolean doMirrored = deleteMirroredSourceForChildren;
				getTransactionTemplate().execute(new TransactionCallback() {
					public Object doInTransaction(TransactionStatus status) {
						loadBinderProcessor(child).deleteBinder(child,
								doMirrored, options);
						return null;
					}
				});
				// get updates committed, this is needed if their is another
				// transaction wrapping the call to delete binder
				// This is the case with delete user workspace !!
				getCoreDao().flush();

				getCoreDao().evict(child); // update committed

			} catch (NoObjectByTheIdException musthavebeendeleted) {
				continue;
			}
		}
		getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				loadBinderProcessor(top).deleteBinder(top,
						deleteMirroredSource, options);
				return null;
			}
		});

	}

	protected void deleteBinderPhase2() {
		if(SPropsUtil.getBoolean("binder.delete.immediate", true)) {
			// Initiate the phase 2 of the process needed for deleting a binder hierarchy. 
			// This part is executed asynchronously and we do not check its outcome.
			getRunAsyncManager().execute(new RunAsyncCallback() {
				public Object doAsynchronously() throws Exception {
			    	FolderModule folderModule = (FolderModule)SpringContextUtil.getBean("folderModule");
			    	folderModule.cleanupFolders();
			    	return null;
				}
			});
		}
	}
}