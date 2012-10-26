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
package org.kablink.teaming.gwt.client.binderviews;

import org.kablink.teaming.gwt.client.datatable.ManageCommentsCallback;
import org.kablink.teaming.gwt.client.datatable.ManageCommentsComposite;
import org.kablink.teaming.gwt.client.datatable.ManageCommentsComposite.ManageCommentsCompositeClient;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.CommentAddedCallback;
import org.kablink.teaming.gwt.client.util.CommentsInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

/**
 * Class that holds the folder entry viewer comment manager.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class FolderEntryComments extends VibeFlowPanel implements ManageCommentsCallback {
	private CommentAddedCallback			m_addedCallback;			// Interface to tell our container when a new comment gets added.
	private CommentsInfo					m_comments;					// Information about the entry being viewed's comments.
	private FolderEntryCallback				m_fec;						// Callback to the folder entry composite.
	private GwtTeamingDataTableImageBundle	m_images;					// Access to Vibe's images.
	private GwtTeamingFilrImageBundle		m_filrImages;				// Access to Filr's images.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private ManageCommentsComposite			m_manageCommentsComposite;	// The composite containing the main content of the comment manager. 
	
	public FolderEntryComments(FolderEntryCallback fec, CommentsInfo comments, CommentAddedCallback addedCallback) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_fec           = fec;
		m_comments      = comments;
		m_addedCallback = addedCallback;
		
		// ...initialize the data members requiring it...
		m_filrImages = GwtTeaming.getFilrImageBundle();
		m_images     = GwtTeaming.getDataTableImageBundle();
		m_messages   = GwtTeaming.getMessages();
		
		// ...and construct the comment manager's content.
		createContent();
	}
	
	/**
	 * Called when the composite has completed its initializations and
	 * is ready to run.
	 * 
	 * Implements the ManageCommentsCallback.compositeReady() method.
	 */
	@Override
	public void compositeReady() {
		// Tell the composite that we're ready.
		m_fec.viewComponentReady();
	}

	/*
	 * Creates the header's content.
	 */
	private void createContent() {
		// Add the panel's style...
		addStyleName("vibe-feView-commentsPanel");
		
		// ...asynchronously create the manage comments composite...
		ManageCommentsComposite.createAsync(new ManageCommentsCompositeClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ManageCommentsComposite mcc) {
				// Store the composite and add it to the panel...
				m_manageCommentsComposite = mcc;
				add(m_manageCommentsComposite);

				// ...and tell it to complete its initializations.
				ManageCommentsComposite.initAsync(
					m_manageCommentsComposite,
					m_comments,
					m_addedCallback);
			}},
			this,
			"vibe-feView_commentsComposite");
	}
	
	/**
	 * Called when the user presses the escape key in the manage
	 * comments composite.
	 * 
	 * Implements the ManageCommentsCallback.escape() method.
	 */
	@Override
	public void escape() {
		// Nothing do do.
	}
	
	/**
	 * Shows/hides the comments widget in the ManageCommentsComposite.
	 * 
	 * @param show
	 */
	public void setCommentsVisible(boolean show) {
		ManageCommentsComposite.setCommentsVisibleAsync(
			m_manageCommentsComposite,
			show);
	}
}