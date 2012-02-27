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

package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class that holds all of the information necessary to execute
 * a 'delete/purge of folder entries' commands.
 * 
 * @author drfoster@novell.com
 */
public abstract class DeletePurgeFolderEntriesCmdBase extends VibeRpcCmd {
	private List<Long>	m_entryIds;	//
	private Long		m_folderId;	//
	
	/**
	 * Class constructor.
	 * 
	 * For GWT serialization, must have a zero parameter
	 * constructor.
	 */
	public DeletePurgeFolderEntriesCmdBase() {
		super();		
	}

	/**
	 * Class constructor.
	 * 
	 * @param folderId
	 * @param entryIds
	 */
	public DeletePurgeFolderEntriesCmdBase(Long folderId, List<Long> entryIds) {
		this();
		
		setFolderId(folderId);
		setEntryIds(entryIds);
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param folderId
	 * @param entryId
	 */
	public DeletePurgeFolderEntriesCmdBase(Long folderId, Long entryId) {
		this(folderId, getLLFromL(entryId));
	}

	/*
	 * Returns a List<Long> that contains the specified Long.
	 */
	private static List<Long> getLLFromL(Long l) {
		List<Long> reply = new ArrayList<Long>();
		reply.add(l);
		return reply;
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public List<Long> getEntryIds() {return m_entryIds;}	
	public Long       getFolderId() {return m_folderId;}

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setEntryIds(List<Long> entryIds) {m_entryIds = entryIds;}
	public void setFolderId(Long       folderId) {m_folderId = folderId;}
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public abstract int getCmdType();
}
