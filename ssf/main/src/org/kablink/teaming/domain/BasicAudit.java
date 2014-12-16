/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.domain;

import java.util.Date;

import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

/**
 * @author Jong
 *
 */
public class BasicAudit extends ZonedObject {

	private static final int AUXILIARY_DATA_MAX_SIZE_DEFAULT = 255;
	
	private Long id;
	
	/// Required fields
	private Date date; // Date of event
	private Long userId; // ID of the user who triggered the event
	private Short entityType; // Type of the entity to which event applied
	private Long entityId; // ID of the entity to which event applied
    protected Short eventType; // Type of the event
	protected String owningBinderKey; // Sort key of the owning binder
	protected Long owningBinderId; // ID of the owning binder
	
	/// Optional fields
    protected String auxiliaryData; // Auxiliary data about the event in textual form 
    protected String fileId; // ID of the file associated with the event
    // This field is applicable only if eventType='delete' and entityType='folderEntry'.
    protected String deletedFolderEntryFamily; // Family info about the deleted folder entry

    public BasicAudit(AuditType what, User user, DefinableEntity entity) {
    	this(what, user, entity, new Date());
    }
    
	public BasicAudit(AuditType what, User user, DefinableEntity entity, Date startDate) {
		this(what, startDate, user.getId(), entity.getEntityType(), entity.getId(),
				((entity instanceof Binder)? ((Binder)entity).getBinderKey().getSortKey() : entity.getParentBinder().getBinderKey().getSortKey()),
				((entity instanceof Binder)? entity.getId() : entity.getParentBinder().getId()));

		if((what.equals(AuditType.delete) || what.equals(AuditType.preDelete) || what.equals(AuditType.restore)) 
				&& (entity instanceof FolderEntry) && entity.getCreatedWithDefinitionId() != null) {
        	org.dom4j.Document def = entity.getCreatedWithDefinitionDoc();
        	String family = DefinitionUtils.getFamily(def);
        	if (Validator.isNotNull(family)) {
        		setDeletedFolderEntryFamily(family);
                if (family.equals(Constants.FAMILY_FIELD_FILE)) {
                    setFileId(entity.getPrimaryFileAttachmentId());
                }
        	}
		}		
	}

	public BasicAudit(AuditType auditType, Date date, Long userId, EntityIdentifier.EntityType entityType, Long entityId, String owningBinderKey, Long owningBinderId) {
		this.eventType = auditType.getValue();
		this.date = date;
		this.userId = userId;
		this.entityType = (short) entityType.getValue();
		this.entityId = entityId;
		this.owningBinderKey = owningBinderKey;
		this.owningBinderId = owningBinderId;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getDate() {
		return date;
	}

	public Long getUserId() {
		return userId;
	}

	public EntityIdentifier.EntityType getEntityType() {
		if(entityType != null)
			return EntityIdentifier.EntityType.valueOf(entityType);
		else
			return null;
	}

	public Long getEntityId() {
		return entityId;
	}

	
	public EntityIdentifier getEntityIdentifier() {
		if(entityType != null && entityId != null) {
			return new EntityIdentifier(entityId, this.getEntityType());
		}
		return null;
	}
	
	public AuditType getAuditType() {
		if(eventType != null)
			return AuditType.valueOf(eventType);
		else
			return null;
	}

	public String getOwningBinderKey() {
		return owningBinderKey;
	}

	public Long getOwningBinderId() {
		return owningBinderId;
	}

	public String getAuxiliaryData() {
		return auxiliaryData;
	}
	
	public void setAuxiliaryData(String auxiliaryData) {
		int max = SPropsUtil.getInt("basic.audit.auxiliary.data.max.size", AUXILIARY_DATA_MAX_SIZE_DEFAULT);
		if(auxiliaryData != null && auxiliaryData.length() > max) {
			auxiliaryData = "..." + auxiliaryData.substring(auxiliaryData.length()-max+3);
		}		
		this.auxiliaryData = auxiliaryData;
	}

	public String getFileId() {
		return fileId;
	}
	
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getDeletedFolderEntryFamily() {
		return deletedFolderEntryFamily;
	}

	public void setDeletedFolderEntryFamily(String deletedFolderEntryFamily) {
		this.deletedFolderEntryFamily = deletedFolderEntryFamily;
	}

}