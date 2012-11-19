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
package org.kablink.teaming.gwt.client.widgets;


import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.PerUserRightsInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * 
 * @author jwootton
 *
 */
public class EditNetFolderRightsDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private CheckBox m_canViewCkbox;
	private CheckBox m_canShareForwardCkbox;
	private CheckBox m_canShareExternalCkbox;
	private CheckBox m_canShareInternalCkbox;
	private CheckBox m_canSharePublicCkbox;
	private EditSuccessfulHandler m_editSuccessfulHandler;
	private PerUserRightsInfo m_rightsInfo;
	
	/**
	 * Callback interface to interact with the "Edit Net Folder Rights" dialog asynchronously after it loads. 
	 */
	public interface EditNetFolderRightsDlgClient
	{
		void onSuccess( EditNetFolderRightsDlg esrDlg );
		void onUnavailable();
	}


	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EditNetFolderRightsDlg(
		boolean autoHide,
		boolean modal )
	{
		super( autoHide, modal );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent(
						GwtTeaming.getMessages().editNetFolderRightsDlg_Caption(),
						this,
						null,
						null ); 
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		VibeFlowPanel mainPanel;
		FlowPanel tmpPanel;
		Label label;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VibeFlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		mainPanel.addStyleName( "editNetFolderRightsDlg_MainPanel" );

		label = new Label( messages.editNetFolderRightsDlg_Instructions() );
		label.addStyleName( "editNetFolderRightsDlg_Instructions" );
		mainPanel.add( label );
		
		// Add the "View items" checkbox
		m_canViewCkbox = new CheckBox( messages.editNetFolderRightsDlg_CanViewLabel() );
		m_canViewCkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		mainPanel.add( m_canViewCkbox );
		
		// Add the "Re-share" checkbox
		m_canShareForwardCkbox = new CheckBox( messages.editNetFolderRightsDlg_ReShareLabel() );
		m_canShareForwardCkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canShareForwardCkbox );
		mainPanel.add( tmpPanel );
		
		// Add the "allow share internal" checkbox.
		m_canShareInternalCkbox = new CheckBox( messages.editNetFolderRightsDlg_ShareInternalLabel() );
		m_canShareInternalCkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canShareInternalCkbox );
		mainPanel.add( tmpPanel );
		
		// Add the "allow share external" checkbox.
		m_canShareExternalCkbox = new CheckBox( messages.editNetFolderRightsDlg_ShareExternalLabel() );
		m_canShareExternalCkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canShareExternalCkbox );
		mainPanel.add( tmpPanel );

		// Add the "allow share public" checkbox.
		m_canSharePublicCkbox = new CheckBox( messages.editNetFolderRightsDlg_SharePublicLabel() );
		m_canSharePublicCkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canSharePublicCkbox );
		mainPanel.add( tmpPanel );

		return mainPanel;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		// Do we have a PerUserRightsInfo object we are working with?
		if ( m_rightsInfo != null )
		{
			// Yes
			m_rightsInfo.setCanAccess( m_canViewCkbox.getValue() );
			m_rightsInfo.setCanReshare( m_canShareForwardCkbox.getValue() );
			m_rightsInfo.setCanShareExternal( m_canShareExternalCkbox.getValue() );
			m_rightsInfo.setCanShareInternal( m_canShareInternalCkbox.getValue() );
			m_rightsInfo.setCanSharePublic( m_canSharePublicCkbox.getValue() );

			// Do we have a handler we should call?
			if ( m_editSuccessfulHandler != null )
				m_editSuccessfulHandler.editSuccessful( Boolean.TRUE );
		}

		return true;
	}

	/**
	 * Get the text entered by the user.
	 */
	@Override
	public Object getDataFromDlg()
	{
		return Boolean.TRUE;
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}

	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init(
		PerUserRightsInfo rightsInfo,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		m_rightsInfo = rightsInfo;
		m_editSuccessfulHandler = editSuccessfulHandler;

		m_canShareExternalCkbox.setValue( false );
		m_canShareForwardCkbox.setValue( false );
		m_canShareInternalCkbox.setValue( false );
		m_canSharePublicCkbox.setValue( false );
		m_canViewCkbox.setValue( false );

		if ( m_rightsInfo != null )
		{
			m_canShareExternalCkbox.setValue( m_rightsInfo.canShareExternal() );
			m_canShareForwardCkbox.setValue( m_rightsInfo.canReshare() );
			m_canShareInternalCkbox.setValue( m_rightsInfo.canShareInternal() );
			m_canSharePublicCkbox.setValue( m_rightsInfo.canSharePublic() );
			m_canViewCkbox.setValue( m_rightsInfo.canAccess() );
		}
	}
	
	/**
	 * Loads the EditNetFolderRightsDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final EditNetFolderRightsDlgClient enfrDlgClient )
	{
		GWT.runAsync( EditNetFolderRightsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditNetFolderRightsDlg() );
				if ( enfrDlgClient != null )
				{
					enfrDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				EditNetFolderRightsDlg enfrDlg;
				
				enfrDlg = new EditNetFolderRightsDlg( autoHide, modal );
				enfrDlgClient.onSuccess( enfrDlg );
			}
		});
	}
}