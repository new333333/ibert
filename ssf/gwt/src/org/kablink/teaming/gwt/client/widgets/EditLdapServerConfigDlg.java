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


import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtLdapConnectionConfig;
import org.kablink.teaming.gwt.client.GwtLdapSearchInfo;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;


/**
 * 
 * @author jwootton
 *
 */
public class EditLdapServerConfigDlg extends DlgBox
{
	private GwtLdapConnectionConfig m_serverConfig;
	
	private TextBox m_serverUrlTextBox;
	private TextBox m_proxyDnTextBox;
	private PasswordTextBox m_proxyPwdTextBox;
	private TextBox m_guidAttribTextBox;
	private TextBox m_nameAttribTextBox;

	private TextArea m_userFilterTextArea;
	private CellTable<GwtLdapSearchInfo> m_userSearchesTable;
    private MultiSelectionModel<GwtLdapSearchInfo> m_userSearchesSelectionModel;
	private ListDataProvider<GwtLdapSearchInfo> m_userSearchesDataProvider;
	private VibeSimplePager m_userSearchesPager;
	private List<GwtLdapSearchInfo> m_listOfUserSearches;
	
	/**
	 * Callback interface to interact with the "edit ldap server config" dialog
	 * asynchronously after it loads. 
	 */
	public interface EditLdapServerConfigDlgClient
	{
		void onSuccess( EditLdapServerConfigDlg elscDlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private EditLdapServerConfigDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		super( autoHide, modal, xPos, yPos, DlgButtonMode.OkCancel );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent(
						GwtTeaming.getMessages().editLdapServerConfigDlg_Header(),
						editSuccessfulHandler,
						null,
						null );
	}

	/**
	 * Add the given list of user search info to the dialog
	 */
	private void addUserSearches( List<GwtLdapSearchInfo> listOfLdapSearches )
	{
		m_listOfUserSearches = listOfLdapSearches;
		
		if ( m_userSearchesDataProvider == null )
		{
			m_userSearchesDataProvider = new ListDataProvider<GwtLdapSearchInfo>( m_listOfUserSearches );
			m_userSearchesDataProvider.addDataDisplay( m_userSearchesTable );
		}
		else
		{
			m_userSearchesDataProvider.setList( m_listOfUserSearches );
			m_userSearchesDataProvider.refresh();
		}
		
		// Clear all selections.
		m_userSearchesSelectionModel.clear();
		
		// Go to the first page
		m_userSearchesPager.firstPage();
		
		// Tell the table how many user searches we have.
		m_userSearchesTable.setRowCount( m_listOfUserSearches.size(), true );
	}


	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		TabPanel tabPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		tabPanel = new TabPanel();
		tabPanel.addStyleName( "vibe-tabPanel" );

		mainPanel.add( tabPanel );

		// Create a panel to hold the ldap server information
		{
			Panel serverPanel;
			
			serverPanel = createServerPanel( messages );
			tabPanel.add( serverPanel, messages.editLdapServerConfigDlg_ServerTab() );
		}
		
		// Create a panel to hold the user search criteria
		{
			Panel usersPanel;
			
			usersPanel = createUsersPanel( messages );
			tabPanel.add( usersPanel, messages.editLdapServerConfigDlg_UsersTab() );
		}
		
		// Create a panel to hold the group search criteria
		{
			Panel groupsPanel;
			
			groupsPanel = createGroupsPanel( messages );
			tabPanel.add( groupsPanel, messages.editLdapServerConfigDlg_GroupsTab() );
		}

		tabPanel.selectTab( 0 );
		
		return mainPanel;
	}

	/**
	 * 
	 */
	private Panel createGroupsPanel( GwtTeamingMessages messages )
	{
		FlowPanel groupsPanel;
		
		groupsPanel = new FlowPanel();
		groupsPanel.add( new Label( "This is the groups panel" ) );
		
		return groupsPanel;
	}
	
	/**
	 * 
	 */
	private Panel createServerPanel( GwtTeamingMessages messages )
	{
		FlowPanel serverPanel;
		FlexTable table;
		Label label;
		FlowPanel tmpPanel;
		int row = 0;
		
		serverPanel = new FlowPanel();
		
		table = new FlexTable();
		
		// Add the server url controls
		{
			// Add a hint for the server url
			tmpPanel = new FlowPanel();
			label = new Label( messages.editLdapServerConfigDlg_ServerUrlHint() );
			label.addStyleName( "editLdapServerConfigDlg_ServerUrlHint" );
			tmpPanel.add( label );
			table.setHTML( row, 1, tmpPanel.getElement().getInnerHTML() );
			++row;
			
			label = new Label( messages.editLdapServerConfigDlg_ServerUrlLabel() );
			table.setHTML( row, 0, label.getElement().getInnerHTML()  );
		
			m_serverUrlTextBox = new TextBox();
			m_serverUrlTextBox.setVisibleLength( 40 );
			table.setWidget( row, 1, m_serverUrlTextBox );
			++row;
		}
		
		// Add the proxy dn controls
		{
			label = new Label( messages.editLdapServerConfigDlg_ProxyDNLabel() );
			table.setHTML( row, 0, label.getElement().getInnerHTML()  );
			
			m_proxyDnTextBox = new TextBox();
			m_proxyDnTextBox.setVisibleLength( 40 );
			table.setWidget( row, 1, m_proxyDnTextBox );
			++row;
		}
		
		// Add the proxy password controls
		{
			label = new Label( messages.editLdapServerConfigDlg_ProxyPasswordLabel() );
			table.setHTML( row, 0, label.getElement().getInnerHTML()  );
			
			m_proxyPwdTextBox = new PasswordTextBox();
			m_proxyPwdTextBox.setVisibleLength( 20 );
			table.setWidget( row, 1, m_proxyPwdTextBox );
			++row;
		}
		
		// Add the ldap guid attribute controls
		{
			
		}
		
		// Add the name attribute controls
		{
			label = new Label( messages.editLdapServerConfigDlg_NameAttributeLabel() );
			table.setHTML( row, 0, label.getElement().getInnerHTML()  );
			
			m_nameAttribTextBox = new TextBox();
			m_nameAttribTextBox.setVisibleLength( 20 );
			table.setWidget( row, 1, m_nameAttribTextBox );
			++row;
		}
		
		serverPanel.add( table );
		
		return serverPanel;
	}

	/**
	 * 
	 */
	private Panel createUsersPanel( GwtTeamingMessages messages )
	{
		FlowPanel usersPanel;
		
		usersPanel = new FlowPanel();
		usersPanel.add( new Label( "This is the users panel" ) );
		
		return usersPanel;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		if ( m_serverConfig != null )
		{
			m_serverConfig.setServerUrl( m_serverUrlTextBox.getValue() );
			m_serverConfig.setProxyDn( m_proxyDnTextBox.getValue() );
			m_serverConfig.setProxyPwd( m_proxyPwdTextBox.getValue() );
			m_serverConfig.setUserIdAttribute( m_nameAttribTextBox.getValue() );
		}
		
		return m_serverConfig;
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
	 * 
	 */
	public void init( GwtLdapConnectionConfig config )
	{
		m_serverConfig = config;
		
		m_serverUrlTextBox.setValue( "" );
		m_proxyDnTextBox.setValue( "" );
		m_proxyPwdTextBox.setValue( "" );
		m_nameAttribTextBox.setValue( "" );
		
		if ( config == null )
			return;
		
		m_serverUrlTextBox.setValue( config.getServerUrl() );
		m_proxyDnTextBox.setValue( config.getProxyDn() );
		m_proxyPwdTextBox.setValue( config.getProxyPwd() );
		m_nameAttribTextBox.setValue( config.getUserIdAttribute() );
	}
	
	/**
	 * Loads the EditLdapConnectionConfigDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final int left,
							final int top,
							final EditSuccessfulHandler editSuccessfulHandler,
							final EditLdapServerConfigDlgClient elscDlgClient )
	{
		GWT.runAsync( EditLdapServerConfigDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditLdapServerConfigDlg() );
				if ( elscDlgClient != null )
				{
					elscDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				EditLdapServerConfigDlg elscDlg;
				
				elscDlg = new EditLdapServerConfigDlg(
											autoHide,
											modal,
											left,
											top,
											editSuccessfulHandler );
				elscDlgClient.onSuccess( elscDlg );
			}
		});
	}
}
