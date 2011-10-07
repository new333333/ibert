/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.util.FolderColumnInfo;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Discussion folder view.
 * 
 * @author drfoster@novell.com
 */
public class DiscussionFolderView extends DataTableFolderViewBase {
	/**
	 * Constructor method.
	 * 
	 * @param folderId
	 * @param viewReady
	 */
	public DiscussionFolderView(Long folderId, ViewReady viewReady) {
		// Simply initialize the base class.
		super(folderId, FolderType.DISCUSSION, viewReady);
	}
	
	/**
	 * Callback interface used to interact with a discussion folder
	 * view asynchronously after it loads. 
	 */
	public interface DiscussionFolderViewClient {
		void onSuccess(DiscussionFolderView dfView);
		void onUnavailable();
	}

	/**
	 * Called from the base class to complete the construction of this
	 * discussion folder view.
	 * 
	 * Implements DataTableFolderViewBase.constructView().
	 * 
	 * @param folderColumnsList
	 * @param folderSortBy
	 * @param folderSortDescend
	 */
	@Override
	public void constructView(List<FolderColumnInfo> folderColumnsList, String folderSortBy, boolean folderSortDescend) {
		// Setup the appropriate styles for a discussion folder...
		getFlowPanel().addStyleName("vibe-discussionFolderFlowPanel");

		// ...reset the view's content...
		resetView(folderColumnsList, folderSortBy, folderSortDescend);
		
		// ...and tell the base class that we're done with the
		// ...construction.
		super.viewReady();
	}
	
	/**
	 * Loads the DiscussionFolderView split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param dfvClient
	 */
	public static void createAsync(final Long folderId, final ViewReady viewReady, final DiscussionFolderViewClient dfvClient) {
		GWT.runAsync(DiscussionFolderView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				DiscussionFolderView dfView = new DiscussionFolderView(folderId, viewReady);
				dfvClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_DiscussionFolderView());
				dfvClient.onUnavailable();
			}
		});
	}
	
	/**
	 * Called from the base class to reset the content of this
	 * discussion folder view.
	 * 
	 * Implements DataTableFolderViewBase.resetView().
	 * 
	 * @param folderColumnsList
	 * @param folderSortBy
	 * @param folderSortDescend
	 */
	@Override
	public void resetView(List<FolderColumnInfo> folderColumnsList, String folderSortBy, boolean folderSortDescend) {
		// Clear any existing content from the view.
		resetContent();
		
//!		...this needs to be implemented...
		VibeVerticalPanel vp = new VibeVerticalPanel();
		vp.add(new InlineLabel("Sort by:  " + folderSortBy));
		vp.add(new InlineLabel("Sort descending:  " + folderSortDescend));
		vp.add(new HTML("<br/>"));
		for (FolderColumnInfo fc:  folderColumnsList) {
			vp.add(new InlineLabel(fc.getColumnName() + "='" + fc.getColumnTitle() + "'"));
		}
		getFlowPanel().add(vp);
	}
}
