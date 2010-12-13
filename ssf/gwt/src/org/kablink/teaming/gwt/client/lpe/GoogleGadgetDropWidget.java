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
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 * @author jwootton
 *
 */
public class GoogleGadgetDropWidget extends DropWidget
{
	private static GoogleGadgetWidgetDlgBox m_googleGadgetDlgBox = null;		// For efficiency sake, we only create one dialog box.
	private GoogleGadgetProperties	m_properties = null;
	private FlowPanel m_htmlPanel = null;
	

	/**
	 * 
	 */
	public GoogleGadgetDropWidget( LandingPageEditor lpe, GoogleGadgetConfig configData )
	{
		GoogleGadgetProperties properties;
		
		properties = null;
		if ( configData != null )
			properties = configData.getProperties();
		
		init( lpe, properties );
	}
	
	
	/**
	 * 
	 */
	public GoogleGadgetDropWidget( LandingPageEditor lpe, GoogleGadgetProperties properties )
	{
		init( lpe, properties );
	}
	

	/**
	 * Create a configuration string that represents this widget and that can be stored in the db.
	 */
	public String createConfigString()
	{
		return m_properties.createConfigString();
	}
	
	
	/**
	 * Return the drag proxy object that should be displayed when the user drags this item.
	 */
	public DragProxy getDragProxy()
	{
		if ( m_dragProxy == null )
		{
			// Create a drag proxy that will be displayed when the user drags this item.
			m_dragProxy = new DragProxy( GwtTeaming.getImageBundle().landingPageEditorGoogleGadget(), GwtTeaming.getMessages().lpeGoogleGadget() );
		}
		
		return m_dragProxy;
	}
	

	/**
	 * Return the dialog box used to edit the properties of this widget.
	 */
	public DlgBox getPropertiesDlgBox( int xPos, int yPos )
	{
		// Have we already created a dialog?
		if ( m_googleGadgetDlgBox == null )
		{
			// Pass in the object that holds all the properties for a GoogleGadgetDropWidget.
			m_googleGadgetDlgBox = new GoogleGadgetWidgetDlgBox( this, this, false, true, xPos, yPos, m_properties );
		}
		else
		{
			m_googleGadgetDlgBox.init( m_properties );
			m_googleGadgetDlgBox.initHandlers( this, this );
		}
		
		return m_googleGadgetDlgBox;
	}
	
	
	/**
	 * @param lpe
	 * @param properties
	 */
	public void init( LandingPageEditor lpe, GoogleGadgetProperties properties )
	{
		FlowPanel wrapperPanel;
		
		m_lpe = lpe;
		
		wrapperPanel = new FlowPanel();
		wrapperPanel.addStyleName( "dropWidgetWrapperPanel" );
		
		// Create an Edit/Delete control and position it at the top/right of this widget.
		// This control allows the user to edit the properties of this widget and to delete this widget.
		{
			ActionsControl ctrl;
			FlowPanel panel;
			
			ctrl = new ActionsControl( this, this, this );
			ctrl.addStyleName( "upperRight" );
			
			// Wrap the edit/delete control in a panel.  We position the edit/delete control on the right
			// side of the wrapper panel.
			panel = new FlowPanel();
			panel.addStyleName( "editDeleteWrapperPanel" );
			
			panel.add( ctrl );
			wrapperPanel.add( panel );
		}
		
		// Create the controls that will hold the html
		{
			m_htmlPanel = new FlowPanel();
			m_htmlPanel.addStyleName( "lpeDropWidget" );
			m_htmlPanel.addStyleName( "lpeHtmlWidget" );
			
			wrapperPanel.add( m_htmlPanel );
		}
		
		// Create an object to hold all of the properties that define a "Google Gadget" widget.
		m_properties = new GoogleGadgetProperties();
		
		// If we were passed some properties, make a copy of them.
		if ( properties != null )
			m_properties.copy( properties );
		
		// Update the dynamic parts of this widget
		updateWidget( m_properties );
		
		// All composites must call initWidget() in their constructors.
		initWidget( wrapperPanel );
	}// end init()
	

	/**
	 * Create the appropriate ui based on the given properties.
	 */
	public void updateWidget( Object props )
	{
		String gadgetCode;
		
		// Save the properties that were passed to us.
		if ( props instanceof PropertiesObj )
			m_properties.copy( (PropertiesObj) props );
		
		// Get the Google Gadget Code
		gadgetCode = m_properties.getGadgetCode();
		
		if ( gadgetCode == null || gadgetCode.length() == 0 )
			m_htmlPanel.getElement().setInnerHTML( "" );
		else
			m_htmlPanel.getElement().setInnerHTML( gadgetCode );

	}

}
