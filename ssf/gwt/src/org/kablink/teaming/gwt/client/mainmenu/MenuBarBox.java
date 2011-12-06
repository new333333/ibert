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
package org.kablink.teaming.gwt.client.mainmenu;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MenuItem;


/**
 * Class used to contain items on the main menu bar.  
 * 
 * @author drfoster@novell.com
 *
 */
public class MenuBarBox extends MenuItem {
	/**
	 * Constructor method.
	 *
	 * @param boxId
	 * @param itemImgRes
	 * @param itemText
	 * @param cmd
	 * @param dropdown
	 */
	public MenuBarBox(String boxId, ImageResource itemImgRes, String itemText, Command cmd, boolean dropdown) {
		// Initialize the superclass...
		super(
			"",
			cmd);
		
		// ...and initialize everything else.
		initBox(itemImgRes, itemText, dropdown);
	}
	
	/**
	 * Constructor method.
	 *
	 * @param boxId
	 * @param itemText
	 * @param dropdown
	 */
	public MenuBarBox(String boxId, String itemText, boolean dropdown) {
		// Always use the initial form of the constructor.
		this(
			boxId,
			null,
			itemText,
			new Command() {	// Place holder.  Actual command will be supplied later.
				@Override
				public void execute() {}
			},
			dropdown);
	}
	
	public MenuBarBox(String boxId, String itemText) {
		// Always use the initial form of the constructor.
		this(
			boxId,
			null,
			itemText,
			new Command() {	// Place holder.  Actual command will be supplied later.
				@Override
				public void execute() {}
			},
			false);
	}
	
	public MenuBarBox(String boxId, ImageResource itemImgRes, String itemText) {
		// Always use the initial form of the constructor.
		this(
			boxId,
			itemImgRes,
			itemText,
			new Command() {	// Place holder.  Actual command will be supplied later.
				@Override
				public void execute() {}
			},
			false);
	}

	/*
	 * Completes the initialization of a MenuBarBox.
	 */
	private void initBox(ImageResource itemImgRes, String itemText, boolean dropdown) {
		addStyleName("vibe-mainMenuContent");

		// If we need an image for the box...
		FlowPanel boxPanel = new FlowPanel();
		if (null != itemImgRes) {
			// ...add it...
			Image itemImg = new Image(itemImgRes);
			itemImg.addStyleName("vibe-mainMenuBar_BoxImg");
			if (!(GwtClientHelper.jsIsIE())) {
				itemImg.addStyleName("vibe-mainMenuBar_BoxImgNonIE");
			}
			boxPanel.add(itemImg);
		}

		// ...add the label for the box...
		InlineLabel itemLabel = new InlineLabel(itemText);
		itemLabel.addStyleName("vibe-mainMenuBar_BoxText");
		boxPanel.add(itemLabel);

		// ...if we need a drop down image for the box...
		if (dropdown) {
			// ...add it...
			Image dropDownImg = new Image(GwtTeaming.getMainMenuImageBundle().menuArrow());
			dropDownImg.addStyleName("vibe-mainMenuBar_BoxDropDownImg");
			if (!(GwtClientHelper.jsIsIE())) {
				dropDownImg.addStyleName("vibe-mainMenuBar_BoxDropDownImgNonIE");
			}
			boxPanel.add(dropDownImg);
		}

		// ...and finally, set the HTML for the MenuItem.
		setHTML(boxPanel.getElement().getInnerHTML());
	}
	
	/**
	 * Sets the Command for a MenuBarBox.
	 * 
	 * @param c
	 */
	public void setCommand(Command c) {
		super.setCommand(c);
	}

	/**
	 * Returns the menu bar box's absolute bottom position.
	 * 
	 * @return
	 */
	public int getBoxBottom() {
		return getElement().getAbsoluteBottom();
	}
	
	/**
	 * Returns the menu bar box's absolute left position.
	 * 
	 * @return
	 */
	public int getBoxLeft () {
		return getAbsoluteLeft();
	}
	
	/**
	 * Sets the widget's styles to reflect that it has a closed popup
	 * menu associated with it.
	 */
	public void popupMenuClosed() {
//!		...this needs to be implemented...
	}
	
	/**
	 * Sets the widget's styles to reflect that it has an open popup
	 * menu associated with it.
	 */
	public void popupMenuOpened() {
//!		...this needs to be implemented...
	}
}
