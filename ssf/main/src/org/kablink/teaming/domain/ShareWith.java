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

package org.kablink.teaming.domain;

import java.util.ArrayList;
import java.util.Collection;

import org.kablink.teaming.domain.EntityIdentifier.EntityType;

/**
 * @author jong
 * 
 */
public class ShareWith extends NotDefinableEntity {

	protected DefinableEntity sharedEntity;
	protected Collection<ShareWithMember> members;

	// For use by Hibernate only
	protected ShareWith() {
	}

	// For user by application
	public ShareWith(User sharer, DefinableEntity sharedEntity, Description description,
			Collection<ShareWithMember> members) {
		if (sharer == null)
			throw new IllegalArgumentException("Sharer must be specified");
		if (sharedEntity == null)
			throw new IllegalArgumentException(
					"Shared entity must be specified");
		if (members == null)
			throw new IllegalArgumentException(
					"Share members must be specified");

		this.setCreation(new HistoryStamp(sharer));
		this.setModification(this.getCreation());
		this.sharedEntity = sharedEntity;
		this.description = description;
		this.members = members;
	}

	@Override
	public EntityType getEntityType() {
		return EntityIdentifier.EntityType.shareWith;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DefinableEntity getSharedEntity() {
		return sharedEntity;
	}

	public void setSharedEntity(DefinableEntity sharedEntity) {
		this.sharedEntity = sharedEntity;
	}

	public Collection<ShareWithMember> getMembers() {
		if (members == null)
			members = new ArrayList<ShareWithMember>();
		return members;
	}

	public void setMembers(Collection<ShareWithMember> members) {
		this.members = members;
	}

}
