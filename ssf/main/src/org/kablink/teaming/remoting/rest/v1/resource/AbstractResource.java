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
package org.kablink.teaming.remoting.rest.v1.resource;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.UnsupportedMediaTypeException;
import org.kablink.teaming.rest.v1.model.DefinableEntity;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractResource extends AbstractAllModulesInjected {

    @javax.ws.rs.core.Context
    ServletContext context;

    protected Log logger = LogFactory.getLog(getClass());

    protected ServletContext getServletContext() {
        return context;
    }

    protected Long getLoggedInUserId() {
        return RequestContextHolder.getRequestContext().getUserId();
    }

    protected org.kablink.teaming.domain.User getLoggedInUser() {
        Long userId = getLoggedInUserId();
        // Retrieve the raw entry.
        Principal entry = getProfileModule().getEntry(userId);

        if(!(entry instanceof org.kablink.teaming.domain.User))
            throw new IllegalArgumentException(userId + " does not represent an user. It is " + entry.getClass().getSimpleName());
        return (org.kablink.teaming.domain.User)entry;
    }

    protected org.kablink.teaming.domain.User _getUser(long userId) {
        Principal entry = getProfileModule().getEntry(userId);

        if(!(entry instanceof org.kablink.teaming.domain.User))
            throw new IllegalArgumentException(userId + " does not represent an user. It is " + entry.getClass().getSimpleName());
        return (org.kablink.teaming.domain.User) entry;
    }

    protected Document getDocument(String xml) {
   		// Parse XML string into a document tree.
   		try {
            if (xml==null || xml.length()==0) {
                return DocumentHelper.createDocument(DocumentHelper.createElement("QUERY"));
            }
   			return DocumentHelper.parseText(xml);
   		} catch (DocumentException e) {
   			throw new BadRequestException(ApiErrorCode.BAD_INPUT, "POST body did not contain valid XML: " + e.getLocalizedMessage());
   		}
   	}

    protected InputStream getInputStreamFromMultipartFormdata(HttpServletRequest request)
            throws WebApplicationException, UncheckedIOException {
        InputStream is;
        try {
            ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory());
            FileItemIterator fii = sfu.getItemIterator(request);
            if (fii.hasNext()) {
                FileItemStream item = fii.next();
                is = item.openStream();
            } else {
                throw new BadRequestException(ApiErrorCode.MISSING_MULTIPART_FORM_DATA, "Missing form data");
            }
        } catch (FileUploadException e) {
            logger.warn("Received bad multipart form data", e);
            throw new BadRequestException(ApiErrorCode.BAD_MULTIPART_FORM_DATA, "Received bad multipart form data");
        } catch (IOException e) {
            logger.error("Error reading multipart form data", e);
            throw new UncheckedIOException(e);
        }
        return is;
    }

    protected String getRawInputStreamAsString(HttpServletRequest request) {
        InputStream is = getRawInputStream(request);
        try {
            return IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            logger.error("Error reading data", e);
            throw new UncheckedIOException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    protected InputStream getRawInputStream(HttpServletRequest request)
            throws UncheckedIOException {
        if (MediaType.APPLICATION_FORM_URLENCODED.equals(request.getContentType())) {
            throw new UnsupportedMediaTypeException(MediaType.APPLICATION_FORM_URLENCODED + " is not a supported media type.");
        }
        if (MediaType.MULTIPART_FORM_DATA.equals(request.getContentType())) {
            throw new UnsupportedMediaTypeException(MediaType.MULTIPART_FORM_DATA + " is not a supported media type.");
        }
        try {
            return request.getInputStream();
        } catch (IOException e) {
            logger.error("Error reading data", e);
            throw new UncheckedIOException(e);
        }
    }

    protected void populateTimestamps(Map options, DefinableEntity entry)
   	{
   		HistoryStamp creation = entry.getCreation();
   		if(creation != null) {
   			if(creation.getPrincipal() != null)
   				options.put(ObjectKeys.INPUT_OPTION_CREATION_ID, creation.getPrincipal().getId());
   			if(creation.getDate() != null)
   				options.put(ObjectKeys.INPUT_OPTION_CREATION_DATE, creation.getDate());
   		}
   		HistoryStamp modification = entry.getModification();
   		if(modification != null) {
            if(modification.getPrincipal() != null)
                options.put(ObjectKeys.INPUT_OPTION_CREATION_ID, modification.getPrincipal().getId());
   			if(modification.getDate() != null)
   				options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE, modification.getDate());
   		}
   	}

    protected Criterion buildLibraryTreeCriterion() {
        Junction criteria = Restrictions.disjunction();
        criteria.add(SearchUtils.libraryFolders());
        criteria.add(buildWorkspacesCriterion());
        return criteria;
    }

    protected Criterion buildDocTypeCriterion(boolean includeBinders, boolean includeFolderEntries, boolean includeFiles, boolean includeReplies) {
        Junction types = Restrictions.disjunction();
        // Include a restriction that will always evaluate to false.  That way if all of the include* parameters are false
        // no results will be returned (instead of all results being returned)
        types.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, "_fake_"));
        if (includeBinders) {
            types.add(buildBindersCriterion());
        }
        if (includeFiles) {
            types.add(buildAttachmentsCriterion());
        }
        if (includeFolderEntries) {
            types.add(buildEntriesCriterion());
        }
        if (includeReplies) {
            types.add(buildRepliesCriterion());
        }
        return types;
    }

    protected Criterion buildEntryCriterion(Long id) {
        return Restrictions.conjunction()
        			.add(buildEntriesAndRepliesCriterion())
                    .add(Restrictions.disjunction()
                            .add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()))
                            .add(Restrictions.eq(Constants.ENTRY_TOP_ENTRY_ID_FIELD, id.toString())));
    }

    protected Criterion buildAttachmentsCriterion() {
        return Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ATTACHMENT);
    }

    protected Criterion buildAttachmentCriterion(Long entryId) {
        return Restrictions.conjunction()
                .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ATTACHMENT))
                .add(Restrictions.eq(Constants.ENTRY_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
                .add(Restrictions.eq(Constants.DOCID_FIELD, entryId.toString()));
    }

    protected Criterion buildEntriesAndRepliesCriterion() {
        return Restrictions.conjunction()
        			.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
        			.add(Restrictions.in(Constants.ENTRY_TYPE_FIELD, new String[]{Constants.ENTRY_TYPE_ENTRY, Constants.ENTRY_TYPE_REPLY}));
    }

    protected Criterion buildEntriesCriterion() {
        return Restrictions.conjunction()
        			.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
        			.add(Restrictions.in(Constants.ENTRY_TYPE_FIELD, new String[]{Constants.ENTRY_TYPE_ENTRY}));
    }

    protected Criterion buildRepliesCriterion() {
        return Restrictions.conjunction()
        			.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
        			.add(Restrictions.in(Constants.ENTRY_TYPE_FIELD, new String[]{Constants.ENTRY_TYPE_REPLY}));
    }

    protected Criterion buildFoldersCriterion() {
        return Restrictions.conjunction()
        			.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER))
        			.add(Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_FOLDER));
    }

    protected Criterion buildWorkspacesCriterion() {
        return Restrictions.conjunction()
        			.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER))
        			.add(Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_WORKSPACE));
    }

    protected Criterion buildBinderCriterion(Long id) {
        return Restrictions.conjunction()
        			.add(buildBindersCriterion())
                    .add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
    }

    protected Criterion buildBindersCriterion() {
        return Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER);
    }

    protected Criterion buildAncentryCriterion(Long id) {
        return Restrictions.eq(Constants.ENTRY_ANCESTRY, id.toString());
    }

    protected Criterion buildParentBinderCriterion(Long id) {
        return Restrictions.eq(Constants.BINDER_ID_FIELD, ((Long) id).toString());
    }

    protected Criterion buildSearchBinderCriterion(Long id, boolean recursive) {
        if (recursive) {
            return buildAncentryCriterion(id);
        } else {
            return buildParentBinderCriterion(id);
        }
    }

    protected Criterion buildLibraryCriterion(Boolean onlyLibrary) {
        return Restrictions.eq(Constants.IS_LIBRARY_FIELD, ((Boolean) onlyLibrary).toString());
    }

    protected Criterion buildKeywordCriterion(String keyword) {
        return Restrictions.disjunction()
                .add(Restrictions.like(Constants.TITLE_FIELD, keyword))
                .add(Restrictions.like(Constants.DESC_FIELD, keyword))
                .add(Restrictions.like(Constants.GENERAL_TEXT_FIELD, keyword));
    }

    protected Document buildQueryDocument(String query, Criterion additionalCriteria) {
        query = StringCheckUtil.check(query);

        Document queryDoc = getDocument(query);
        if (additionalCriteria!=null) {
            Element queryRoot = queryDoc.getRootElement();

            // Find the root junction element
            Iterator it = queryRoot.selectNodes("AND|OR|NOT").iterator();

            Element implicit = additionalCriteria.toQuery(queryRoot);

            // Add the user supplied query criteria into our implicit AND
            while(it.hasNext()) {
                implicit.add(((Element)it.next()).detach());
            }

            // Add the SORTBY back to the end of the document
            it = queryRoot.selectNodes("SORTBY").iterator();
            while(it.hasNext()) {
                queryRoot.add(((Element)it.next()).detach());
            }
        }
        return queryDoc;
    }
}
