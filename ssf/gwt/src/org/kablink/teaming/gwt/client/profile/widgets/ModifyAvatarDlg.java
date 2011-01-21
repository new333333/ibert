/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.client.profile.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeAttachment;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class ModifyAvatarDlg extends DlgBox implements NativePreviewHandler, SubmitCompleteHandler  {

	ProfileAttributeListElement attrEle;
	private ProfileAttributeAttachment attach;
	private EditSuccessfulHandler editSuccessfulHandler;
	private ProfileRequestInfo profileRequestInfo;
	private FlowPanel photoPanel;
	
	public ModifyAvatarDlg(boolean autoHide, boolean modal, int pos, int pos2, ProfileAttributeListElement attrItem, ProfileRequestInfo requestInfo, EditSuccessfulHandler editSuccessfulHandler) {
		super(autoHide, modal, pos, pos2);
		
		this.attrEle = attrItem;
		this.editSuccessfulHandler=editSuccessfulHandler;
		this.profileRequestInfo = requestInfo;
		
		attach = (ProfileAttributeAttachment) attrEle.getValue();
		
		createAllDlgContent("", null, null, null);
		
		// Register a preview-event handler.  We do this so we can see the mouse-down event
		// in and out side of the widget.
		Event.addNativePreviewHandler( this );
	}

	/**
	 * Create the header, content and footer for the dialog box.
	 */
	public void createAllDlgContent(String caption,
			EditSuccessfulHandler editSuccessfulHandler,// We will call this
														// handler when the user
														// presses the ok button
			EditCanceledHandler editCanceledHandler, // This gets called when
														// the user presses the
														// Cancel button
			Object properties) // Where properties used in the dialog are read
								// from and saved to.
	{
		FlowPanel panel;
		Panel content;
		Panel header;
		Panel footer;

		panel = new FlowPanel();
		panel.addStyleName("modifyAvatarDlg");

		// Add the header.
		header = createHeader(caption);
		panel.add(header);

		// Add the main content of the dialog box.
		content = createContent(properties);
		panel.add(content);

		// Create the footer.
		footer = createFooter();
		panel.add(footer);

		init(properties);

		// Initialize the handlers
		initHandlers(editSuccessfulHandler, editCanceledHandler);

		setWidget(panel);
	}// end createAllDlgContent()
	
	
	private void init(Object properties) {
		
	}

	/**
	 * Override the createHeader() method because we need to make it nicer.
	 */
	public Panel createHeader(String caption) {
		FlowPanel panel;

		panel = new FlowPanel();
		panel.addStyleName("teamingDlgBoxHeader");

		String stitle = attach.getName();
		if(stitle == null){
			stitle = ""; 
		}

		Label titleLabel = new Label(stitle, false);
		titleLabel.addStyleName("modifyAvatarTitle");
		panel.add(titleLabel);

		Anchor closeA = new Anchor();
		closeA.addStyleName("qViewClose");

		Image cancelImage = new Image(GwtTeaming.getMainMenuImageBundle()
				.closeCircle16());
		closeA.getElement().appendChild(cancelImage.getElement());

		// GwtTeaming.getMessages().cancel()
		closeA.setVisible(true);
		panel.add(closeA);

		closeA.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		return panel;
	}// end createHeader()

	
	public Panel createContent(Object propertiesObj) {
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("modifyAvatarDlgContent");
		
		photoPanel = new FlowPanel();
		panel.add(photoPanel);
		photoPanel.addStyleName("modifyAvatarDlgPhoto");
		
		Image img = new Image(attrEle.getValue().toString());
		photoPanel.add(img);
		
		FlowPanel content = new FlowPanel();
		panel.add(content);
		content.addStyleName("modifyAvatarDlgActions");
		
		//create a form element in order to upload a new photo
		final FormPanel formPanel = new FormPanel();
		panel.add(formPanel);
		
		formPanel.setEncoding( FormPanel.ENCODING_MULTIPART );
		formPanel.setMethod( FormPanel.METHOD_POST );
		formPanel.addSubmitCompleteHandler( this );
		
		formPanel.getElement().setId("form1");
		formPanel.setAction( profileRequestInfo.getModifyUrl() + "&okBtn=1" + "&profile=1" );
		
		if( profileRequestInfo.isBinderAdmin() || profileRequestInfo.isOwner() ) {
		
		final Anchor setDefaultAvatar = new Anchor();
		setDefaultAvatar.setWidth("150px");
		content.add(setDefaultAvatar);
		setDefaultAvatar.addStyleName("qView-a");
		setDefaultAvatar.addStyleName("qView-action");
		setDefaultAvatar.setText(GwtTeaming.getMessages().profileSetDefaultAvatar());
		
		final Anchor removeAvatar = new Anchor();
		removeAvatar.setWidth("150px");
		content.add(removeAvatar);
		removeAvatar.addStyleName("qView-a");
		removeAvatar.addStyleName("qView-action");
		removeAvatar.setText(GwtTeaming.getMessages().profileRemoveAvatar());
		
		setDefaultAvatar.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				//get the ordered list of picture id's, with this one at the top
				Hidden hidden = new Hidden("picture__order", getReOrderList());
				formPanel.add(hidden);
				formPanel.submit();
			}
		});

		removeAvatar.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				Hidden hidden = new Hidden("_delete_"+ attach.getId(), "");
				formPanel.add(hidden);
				formPanel.submit();
			}});
		
		
		setDefaultAvatar.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				setDefaultAvatar.addStyleName("qView-action2");
			}});
		
		setDefaultAvatar.addMouseOutHandler(new MouseOutHandler(){
			public void onMouseOut(MouseOutEvent event) {
				setDefaultAvatar.removeStyleName("qView-action2");
			}});
		
		removeAvatar.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				removeAvatar.addStyleName("qView-action2");
			}});
		
		removeAvatar.addMouseOutHandler(new MouseOutHandler(){
			public void onMouseOut(MouseOutEvent event) {
				removeAvatar.removeStyleName("qView-action2");
			}});
		}
		
		return panel;
	}
	
	public void setPhotoPanelSize(int x, int y) {
		photoPanel.setSize(x+"px", y+"px");
	}
	
	//The modify controller expects the picture ids in an order list in which the pictures need to be displayed
	//Get the ProfileAttribute which has the list of ProfileAttributeListElements
	//We can then generate the current order of the pictures then reorder them to what the user selects
	@SuppressWarnings("unchecked")
	private String getReOrderList() {
		ArrayList<String> idList = new ArrayList<String>();

		ProfileAttribute pAttr = attrEle.getParent();
		List<ProfileAttributeListElement> attrList = (List<ProfileAttributeListElement>)pAttr.getValue();

		String setId = attach.getId();
		idList.add(setId);
		
		for(ProfileAttributeListElement attrItem: attrList){
			String id =((ProfileAttributeAttachment) attrItem.getValue()).getId();
			if(id.equals(setId)) { continue; }
			idList.add(id);
		}
		
		String orderedString = null;
		for(String idString: idList){
			if(orderedString != null) { 
				orderedString += ' '; 
				orderedString += idString;
			}
			if(orderedString == null) {	orderedString = idString; }
		}
		
		return orderedString;
	}
	
	/*
	 * Override the createFooter() method so we can control what buttons are in
	 * the footer.
	 */
	public Panel createFooter() {
		FlowPanel panel;

		panel = new FlowPanel();
		panel.addStyleName("qViewFooter");

		return panel;
	}// end createFooter()

	public Object getDataFromDlg() {
		return null;
	}

	public FocusWidget getFocusWidget() {
		return null;
	}

	
	/**
	 * Using this onPreviewNativeEvent to check if the mouse click is in the input widget 
	 */
	public void onPreviewNativeEvent(NativePreviewEvent previewEvent) {


		int eventType = previewEvent.getTypeInt();
		
		// We are only interested in mouse-down events.
		if ( eventType != Event.ONMOUSEDOWN )
			return;
		
		NativeEvent nativeEvent = previewEvent.getNativeEvent();
		//EventTarget target = event.getEventTarget();
		
		if ( isMouseOver(this, nativeEvent.getClientX(), nativeEvent.getClientY())) {
			return;
		} else {
			hide();
			return;
		}
	}
	
	/**
	 * Determine if the given coordinates are over this control.
	 */
	public boolean isMouseOver( Widget widget, int mouseX, int mouseY )
	{
		int left;
		int top;
		int width;
		int height;
		
		// Get the position and dimensions of this control.
		left = widget.getAbsoluteLeft() - widget.getElement().getOwnerDocument().getScrollLeft();
		top = widget.getAbsoluteTop() - widget.getElement().getOwnerDocument().getScrollTop();
		height = widget.getOffsetHeight();
		width = widget.getOffsetWidth();
		
		// Is the mouse over this control?
		if ( mouseY >= top && mouseY <= (top + height) && mouseX >= left && (mouseX <= left + width) )
			return true;
		
		return false;
	}// end isMouseOver()
	
	/**
	 * This method will get called when we get the response to our "modify binder" request.
	 */
	public void onSubmitComplete( FormPanel.SubmitCompleteEvent event )
	{
		// Do we have an editSuccessfulHandler?
		if ( editSuccessfulHandler != null )
		{
			// Yes, call it.
			editSuccessfulHandler.editSuccessful( null );
		}
		
		// Close this dialog.
		hide();
	}// end onSubmitComplete()
}
