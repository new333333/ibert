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

package org.kablink.teaming.gwt.client;

import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl;
import org.kablink.teaming.gwt.client.widgets.ContentControl;
import org.kablink.teaming.gwt.client.widgets.VibeDockLayoutPanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 * This panel holds the content of the main Vibe page.  Currently this content panel
 * holds both the ActivityStreamCtrl and the ContentCtrl.  These both must exist but
 * only one is visible.
 * 
 * @author jwootton
 *
 */
public class MainContentLayoutPanel extends VibeDockLayoutPanel
{
	private FlowPanel m_contentFlowPanel = null;
	private ContentControl m_contentCtrl;
	private ActivityStreamCtrl m_activityStreamCtrl;

	/**
	 * 
	 */
	public MainContentLayoutPanel( ContentControl contentCtrl, ActivityStreamCtrl asCtrl )
	{
		super( Style.Unit.PX );

		m_contentCtrl = contentCtrl;
		m_activityStreamCtrl = asCtrl;
		
		m_contentFlowPanel = new FlowPanel();
		m_contentFlowPanel.getElement().setId( "contentFlowPanel" );
		m_contentFlowPanel.addStyleName( "contentFlowPanel" );
		
		m_contentFlowPanel.add( contentCtrl );
		m_contentFlowPanel.add( asCtrl );
		
		add( m_contentFlowPanel );
	}

	/**
	 * 
	 */
	public void hideAllContent()
	{
		m_contentCtrl.setVisible( false );
		m_activityStreamCtrl.hide();
	}
	
	/**
	 * Show the activity stream and hide any other controls 
	 */
	public void showActivityStream()
	{
		m_contentCtrl.setVisible( false );
		m_activityStreamCtrl.show();
	}
	
	/**
	 * Show the content control and hide any other controls.
	 */
	public void showContentControl()
	{
		m_activityStreamCtrl.hide();
		m_contentCtrl.setVisible( true );
	}
	
	/**
	 * Show a widget other than m_contentCtrl and m_activityStreamCtrl
	 */
	public void showWidget( ResizeComposite composite )
	{
		int i;
		
		hideAllContent();
		
		// Remove all widgets (but not m_contentCtrl or m_activityStreamCtrl)
		for (i = 0; i < m_contentFlowPanel.getWidgetCount(); ++i)
		{
			Widget nextWidget;
			
			nextWidget = m_contentFlowPanel.getWidget( i );
			if ( nextWidget != m_contentCtrl && nextWidget != m_activityStreamCtrl )
				m_contentFlowPanel.remove( nextWidget );
		}
		
		// Add the new widget
		m_contentFlowPanel.add( composite );
	}
}
