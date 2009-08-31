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
package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EditCanceledHandler;
import org.kablink.teaming.gwt.client.widgets.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author jwootton
 *
 */
public class LinkToUrlWidgetDlgBox extends DlgBox
{
	private TextBox	m_titleTxtBox = null;
	private TextBox	m_urlTxtBox;
	private CheckBox	m_newWndCkBox;
	
	/**
	 * 
	 */
	public LinkToUrlWidgetDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		LinkToUrlProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().linkToUrlProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end LinkToUrlWidgetDlgBox()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@SuppressWarnings("unchecked")
	public Panel createContent( PropertiesObj props )
	{
		LinkToUrlProperties properties;
		Label			label;
		VerticalPanel	mainPanel;
		FlexTable		table;
		
		properties = (LinkToUrlProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Add label and edit control for "Title"
		table = new FlexTable();
		table.setCellSpacing( 2 );
		label = new Label( GwtTeaming.getMessages().title() );
		table.setWidget( 0, 0, label );
		m_titleTxtBox = new TextBox();
		m_titleTxtBox.setVisibleLength( 30 );
		m_titleTxtBox.setText( properties.getTitle() );
		table.setWidget( 1, 0, m_titleTxtBox );
		mainPanel.add( table );
		
		// Add label and edit control for "URL"
		table = new FlexTable();
		table.setCellSpacing( 2 );
		label = new Label( GwtTeaming.getMessages().linkToUrl() );
		table.setWidget( 2, 0, label );
		m_urlTxtBox = new TextBox();
		m_urlTxtBox.setVisibleLength( 30 );
		m_urlTxtBox.setText( properties.getUrl() );
		table.setWidget( 3, 0, m_urlTxtBox );
		
		mainPanel.add( table );
		
		// Add a checkbox for "Open the URL in a new window"
		table = new FlexTable();
		table.setCellSpacing( 4 );
		m_newWndCkBox = new CheckBox( GwtTeaming.getMessages().openUrlInNewWnd() );
		table.setWidget( 0, 0, m_newWndCkBox );
		mainPanel.add( table );
		if ( properties.getOpenInNewWindow() == true )
			m_newWndCkBox.setValue( Boolean.TRUE );

		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	public PropertiesObj getDataFromDlg()
	{
		LinkToUrlProperties	properties;
		
		properties = new LinkToUrlProperties();
		
		// Save away the title.
		properties.setTitle( getTitleValue() );

		// Save away the url
		properties.setUrl( getUrlValue() );
		
		// Save away the "open in new window" value.
		properties.setOpenInNewWindow( getOpenInNewWindowValue() );
		return properties;
	}// end getDataFromDlg()
	
	
	/**
	 * Return true if the "open in new window" checkbox is checked.
	 */
	public boolean getOpenInNewWindowValue()
	{
		return m_newWndCkBox.getValue().booleanValue();
	}// end getOpenInNewWindowValue()
	
	
	/**
	 * Return the text found in the title edit control.
	 */
	public String getTitleValue()
	{
		return m_titleTxtBox.getText();
	}// end getTitleValue()
	
	
	/**
	 * Return the text found in the url edit control.
	 */
	public String getUrlValue()
	{
		return m_urlTxtBox.getText();
	}// end getUrlValue()
	
}// end LinkToUrlWidgetDlgBox
