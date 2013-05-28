/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.gwt.client.event;

import java.util.List;

import org.kablink.teaming.gwt.client.util.EntityId;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * The ZipAndDownloadSelectedFilesEvent is used to zip and download the
 * currently selected files in a folder.
 * 
 * See the definition of the SelectedEntriesEventBase class for how and
 * when an EntityId (or List<EntityId>) should be passed into the
 * construction of this class.
 * 
 * @author drfoster@novell.com
 */
public class ZipAndDownloadSelectedFilesEvent extends SelectedEntriesEventBase<ZipAndDownloadSelectedFilesEvent.Handler> {
    public static Type<Handler> TYPE = new Type<Handler>();
    
    public boolean	m_recursive;	// true -> Recursively descends and he folder hierarchies downloading everything.  Only downloads top level files.
    public Long		m_folderId;		//
    
	/**
	 * Handler interface for this event.
	 */
	public interface Handler extends EventHandler {
		void onZipAndDownloadSelectedFiles(ZipAndDownloadSelectedFilesEvent event);
	}
	
	/**
	 * Class constructor.
	 */
	public ZipAndDownloadSelectedFilesEvent() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param folderId
	 * @param selectedEntityId
	 * @param recursive
	 */
	public ZipAndDownloadSelectedFilesEvent(Long folderId, EntityId selectedEntityId, boolean recursive) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setFolderId(        folderId        );
		setSelectedEntityId(selectedEntityId);
		setRecursive(       recursive       );
	}

	/**
	 * Class constructor.
	 * 
	 * @param folderId
	 * @param selectedEntities
	 * @param recursive
	 */
	public ZipAndDownloadSelectedFilesEvent(Long folderId, List<EntityId> selectedEntities, boolean recursive) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setFolderId(        folderId        );
		setSelectedEntities(selectedEntities);
		setRecursive(       recursive       );
	}

	/**
	 * Class constructor.
	 * 
	 * @param folderId
	 * @param recursive
	 */
	public ZipAndDownloadSelectedFilesEvent(Long folderId, boolean recursive) {
		// Initialize this object.
		this(folderId, ((List<EntityId>) null), recursive);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isRecursive() {return m_recursive;}
	public Long    getFolderId() {return m_folderId; }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setRecursive(boolean recursive) {m_recursive = recursive;}
	public void setFolderId( Long    folderId)  {m_folderId  = folderId; } 
	
	/**
	 * Dispatches this event when one is triggered.
	 * 
	 * Implements the VibeEventBase.doDispatch() method.
	 * 
	 * @param handler
	 */
    @Override
    protected void doDispatch(Handler handler) {
   		handler.onZipAndDownloadSelectedFiles(this);
    }    
	
	/**
	 * Returns the GwtEvent.Type of this event.
	 *
	 * Implements GwtEvent.getAssociatedType()
	 * 
	 * @return
	 */
    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }
    
	/**
	 * Returns the TeamingEvents enumeration value corresponding to
	 * this event.
	 * 
	 * Implements VibeBaseEvent.getEventEnum()
	 * 
	 * @return
	 */
	@Override
	public TeamingEvents getEventEnum() {
		return TeamingEvents.ZIP_AND_DOWNLOAD_SELECTED_FILES;
	}
		
	/**
	 * Registers this event on the given event bus and returns its
	 * HandlerRegistration.
	 * 
	 * @param eventBus
	 * @param handler
	 * 
	 * @return
	 */
	public static HandlerRegistration registerEvent(SimpleEventBus eventBus, Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
}