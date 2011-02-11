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


import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * This widget is used as the palette in the Landing Page Editor.
 * @author jwootton
 *
 */
public class Palette extends Composite
{
	/**
	 * 
	 */
	public Palette( LandingPageEditor lpe )
	{
		FlowPanel	panel;
		PaletteItem	paletteItem;
		
		// Create a FlowPanel for the palette items to live in.
		panel = new FlowPanel();

		// Associate the panel with its stylesheet.
		panel.addStyleName( "lpePalette" );

		// Add the various items to the palette.
		paletteItem = new TablePaletteItem();
		panel.add( paletteItem );
		paletteItem.addMouseDownHandler( lpe );
		
		paletteItem = new ListPaletteItem();
		panel.add( paletteItem );
		paletteItem.addMouseDownHandler( lpe );
		
		paletteItem = new EntryPaletteItem();
		panel.add( paletteItem );
		paletteItem.addMouseDownHandler( lpe );
		
		paletteItem = new FolderPaletteItem();
		panel.add( paletteItem );
		paletteItem.addMouseDownHandler( lpe );
		
		paletteItem = new EnhancedViewPaletteItem();
		panel.add( paletteItem );
		paletteItem.addMouseDownHandler( lpe );

		paletteItem = new GraphicPaletteItem();
		panel.add( paletteItem );
		paletteItem.addMouseDownHandler( lpe );
		
		paletteItem = new LinkUrlPaletteItem();
		panel.add( paletteItem );
		paletteItem.addMouseDownHandler( lpe );
		
		paletteItem = new LinkToFolderPaletteItem();
		panel.add( paletteItem );
		paletteItem.addMouseDownHandler( lpe );
		
		paletteItem = new LinkToEntryPaletteItem();
		panel.add( paletteItem );
		paletteItem.addMouseDownHandler( lpe );
		
		paletteItem = new UtilityElementPaletteItem();
		panel.add( paletteItem );
		paletteItem.addMouseDownHandler( lpe );
		
		paletteItem = new CustomJspPaletteItem();
		panel.add( paletteItem );
		paletteItem.addMouseDownHandler( lpe );

		paletteItem = new HtmlPaletteItem();
		panel.add( paletteItem );
		paletteItem.addMouseDownHandler( lpe );

		paletteItem = new GoogleGadgetPaletteItem();
		panel.add( paletteItem );
		paletteItem.addMouseDownHandler( lpe );

		// All composites must call initWidget() in their constructors.
		initWidget( panel );
	}// end Palette()
}// end Palette
