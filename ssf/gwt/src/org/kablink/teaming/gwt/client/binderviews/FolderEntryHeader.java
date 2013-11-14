/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.FolderEntryCookies.Cookie;
import org.kablink.teaming.gwt.client.binderviews.ProfileEntryDlg.ProfileEntryDlgClient;
import org.kablink.teaming.gwt.client.event.FolderEntryActionCompleteEvent;
import org.kablink.teaming.gwt.client.event.InvokeSimpleProfileEvent;
import org.kablink.teaming.gwt.client.rpc.shared.SetSeenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.SimpleProfileParams;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails.UserInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ViewFolderEntryInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class that holds the folder entry viewer header.
 * 
 * @author drfoster@novell.com
 */
public class FolderEntryHeader extends VibeFlowPanel {
	private FolderEntryCallback				m_fec;				// Callback to the folder entry composite.
	private FolderEntryDetails				m_fed;				// Details about the entry being viewed.
	private GwtTeamingDataTableImageBundle	m_images;			// Access to Vibe's images.
	private GwtTeamingFilrImageBundle		m_filrImages;		// Access to Filr's images.
	private GwtTeamingMessages				m_messages;			// Access to Vibe's messages.
	private ProfileEntryDlg					m_profileEntryDlg;	// A profile entry dialog, once one has been instantiated.
	private VibeFlowPanel					m_descPanel;		//
	private VibeFlowPanel					m_showDescPanel;	//
	
	public FolderEntryHeader(FolderEntryCallback fec, FolderEntryDetails fed) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_fec = fec;
		m_fed = fed;
		
		// ...initialize the data members requiring it...
		m_filrImages = GwtTeaming.getFilrImageBundle();
		m_images     = GwtTeaming.getDataTableImageBundle();
		m_messages   = GwtTeaming.getMessages();
		
		// ...and construct the header's content.
		createContent();
	}

	/*
	 * Creates an Anchor for navigating to a bread crumb link.
	 */
	private Anchor createBCAnchor(final ViewFolderEntryInfo bcItem) {
		Anchor bcAnchor = new Anchor();
		bcAnchor.addStyleName("vibe-feView-headerContentBCAnchor");
		Element bcAE = bcAnchor.getElement();
		bcAE.appendChild(new InlineLabel(bcItem.getTitle()).getElement());
		bcAE.appendChild(GwtClientHelper.buildImage(m_images.breadSpace()).getElement());
		bcAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						m_fec.doNavigate(bcItem);
					}
				});
			}
		});
		return bcAnchor;
	}
	
	/*
	 * Creates the header's content.
	 */
	private void createContent() {
		// Add the panel's style...
		addStyleName("vibe-feView-headerRoot");

		// ...create the header's content panel...
		VibeFlowPanel contentPanel = new VibeFlowPanel();
		contentPanel.addStyleName("vibe-feView-headerContentPanel");
		add(contentPanel);
		
		// ...create the header's content...
		createEntryImage();
		createEntryTitle(          contentPanel);
		createEntryUsers(          contentPanel);
		if (m_fed.hasDescripion()) {
			createEntryDescription(contentPanel);
		}

		// ...and tell the composite that we're ready.
		m_fec.viewComponentReady();
	}
	
	/*
	 * Creates the header's main content.
	 */
	private void createEntryDescription(VibeFlowPanel contentPanel) {
		// What's the visibility state for the description on this entry?
		boolean descVisible = FolderEntryCookies.getBooleanCookieValue(Cookie.DESCRIPTION_VISIBLE, m_fed.getEntityId(), true);
		
		// Add the description and hide description panels...
		m_descPanel = new VibeFlowPanel();
		m_descPanel.addStyleName("vibe-feView-headerDescription");
		contentPanel.add(m_descPanel);
		VibeFlowPanel descHidePanel = new VibeFlowPanel();
		descHidePanel.addStyleName("vibe-feView-headerDescriptionHide");
		m_descPanel.add(descHidePanel);
		Anchor descHideAnchor = new Anchor();
		descHideAnchor.addStyleName("vibe-feView-headerDescriptionAnchor");
		descHideAnchor.setTitle(m_messages.folderEntry_Alt_Hide());
		descHidePanel.add(descHideAnchor);
		descHideAnchor.getElement().setInnerText(m_messages.folderEntry_Hide());
		descHideAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setDescVisible(false);
			}
		});
		VibeFlowPanel descHtmlPanel = new VibeFlowPanel();
		descHtmlPanel.addStyleName("vibe-feView-headerDescriptionHtml");
		if (m_fed.isDescHtml())
		     descHtmlPanel.getElement().setInnerHTML(m_fed.getDesc()   );
		else descHtmlPanel.getElement().setInnerText(m_fed.getDescTxt());
		m_descPanel.add(descHtmlPanel);
		m_descPanel.setVisible(descVisible);

		// ...and add the show description panel.
		m_showDescPanel = new VibeFlowPanel();
		m_showDescPanel.addStyleName("vibe-feView-headerDescriptionShowOuter");
		contentPanel.add(m_showDescPanel);
		VibeFlowPanel showDescAnchorPanel = new VibeFlowPanel();
		showDescAnchorPanel.addStyleName("vibe-feView-headerDescriptionShowInner");
		m_showDescPanel.add(showDescAnchorPanel);
		Anchor showDescAnchor = new Anchor();
		showDescAnchor.addStyleName("vibe-feView-headerDescriptionAnchor");
		showDescAnchorPanel.add(showDescAnchor);
		showDescAnchor.getElement().setInnerText(m_messages.folderEntry_ShowDescription());
		showDescAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setDescVisible(true);
			}
		});
		m_showDescPanel.setVisible(!descVisible);
	}
	
	/*
	 * Creates the header's entry image, including a lock indication.
	 */
	private void createEntryImage() {
		// Create the entry image.
		String url = m_fed.getEntryIcon(BinderIconSize.LARGE);
		if (GwtClientHelper.hasString(url))
		     url = (GwtClientHelper.getRequestInfo().getImagesPath() + url);
		else url = m_filrImages.entry_large().getSafeUri().asString();
		Image i = GwtClientHelper.buildImage(url);
		i.addStyleName("vibe-feView-headerEntryImage");
		add(i);

		// Is the entry locked?
		if (m_fed.isLocked()) {
			// Yes!  Added the locked indicator to the image.
			i = GwtClientHelper.buildImage(m_images.lock().getSafeUri().asString());
			i.addStyleName("vibe-feView-headerEntryLockImage");
			i.setTitle(m_messages.folderEntry_Alt_LockedBy(m_fed.getLocker().getPrincipalInfo().getTitle()));
			add(i);
			
			Label l = new Label(m_messages.folderEntry_Locked());
			l.addStyleName("vibe-feView-headerEntryLockLabel");
			add(l);
		}
	}

	/*
	 * Creates the header's title information.
	 */
	private void createEntryTitle(VibeFlowPanel contentPanel) {
		// If this is not the top entry...
		if (!(m_fed.isTop())) {
			// ...add bread crumbs to the top entry...
			VibeFlowPanel bcPanel = new VibeFlowPanel();
			bcPanel.addStyleName("vibe-feView-headerContentBCPanel");
			List<ViewFolderEntryInfo> bcItems = m_fed.getCommentBreadCrumbs();
			for (ViewFolderEntryInfo bcItem:  bcItems) {
				bcPanel.add(createBCAnchor(bcItem));
			}
			contentPanel.add(bcPanel);
		}

		// ...if this entry hasn't been read...
		VibeFlowPanel titlePanel = new VibeFlowPanel();
		titlePanel.addStyleName("vibe-feView-headerContentTitlePanel");
		contentPanel.add(titlePanel);
		if (!(m_fed.isSeen())) {
			// ...let the user mark it read from there...
			Image i = GwtClientHelper.buildImage(m_images.unread(), m_messages.folderEntry_Alt_MarkRead());
			i.addStyleName("vibe-feView-headerContentTitleMarkRead");
			i.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					markReadAsync();
				}
			});
			titlePanel.add(i);
		}

		// Create the title label...
		Label titleL = new Label(m_fed.getTitle());
		titleL.addStyleName("vibe-feView-headerContentTitleAsLabel");
		
		// ...iIf the entry has a file download URL...
		String dlUrl = m_fed.getDownloadUrl();
		if (GwtClientHelper.hasString(dlUrl)) {
			// ...add the title in an <A> that can download it...
			Anchor titleA = new Anchor();
			titleA.addStyleName("vibe-feView-headerContentTitleAsAnchor");
			titleA.setHref(dlUrl);
			titleA.setTarget("_blank");
			titleA.getElement().appendChild(titleL.getElement());
			titleL.addStyleName("displayInline");
			titlePanel.add(titleA);

			// ...and if there's a file size to display...
			String fileSize = m_fed.getFileSizeDisplay();
			if (GwtClientHelper.hasString(fileSize)) {
				// ...add that to the right of the download URL...
				Label sizeL = new Label(m_messages.folderEntry_FileSize(fileSize));
				sizeL.addStyleName("vibe-feView-headerContentTitleFileSize displayInline");
				titlePanel.add(sizeL);
			}
		}
		
		else {
			// ...otherwise, just add the title Label directly...
			titleL.addStyleName("displayBlock");
			titlePanel.add(titleL);
		}
		
		// ...and add the entry's path.
		Label path = new Label(m_fed.getPath());
		path.addStyleName("vibe-feView-headerContentPath");
		contentPanel.add(path);
	}
	
	/*
	 * Creates the header's user information including creator and
	 * modifier.
	 */
	private void createEntryUsers(VibeFlowPanel contentPanel) {
		// Add the entry's creator...
		final UserInfo creatorInfo = m_fed.getCreator();
		VibeFlowPanel creatorPanel = new VibeFlowPanel();
		creatorPanel.addStyleName("vibe-feView-headerContentPersonPanel");
		final Anchor creatorAnchor = new Anchor();
		creatorAnchor.addStyleName("vibe-feView-headerContentPersonAnchor");
		creatorAnchor.getElement().appendChild(creatorPanel.getElement());
		creatorAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				invokeProfile(creatorInfo.getPrincipalInfo(), creatorAnchor);
			}
		});
		contentPanel.add(creatorAnchor);
		String creatorAvatarUrl = creatorInfo.getPrincipalInfo().getAvatarUrl();
		if (!(GwtClientHelper.hasString(creatorAvatarUrl))) {
			creatorAvatarUrl = m_images.userPhoto().getSafeUri().asString();
		}
		Image creatorAvatar = GwtClientHelper.buildImage(creatorAvatarUrl);
		creatorAvatar.addStyleName("vibe-feView-headerContentPersonAvatar");
		creatorPanel.add(creatorAvatar);
		InlineLabel creator = new InlineLabel(creatorInfo.getPrincipalInfo().getTitle());
		creatorPanel.add(creator);

		// ...add the entry's creation time...
		InlineLabel creationLabel = new InlineLabel(creatorInfo.getDate());
		creationLabel.addStyleName("vibe-feView-headerContentPersonCreated");
		creatorPanel.add(creationLabel);

		// ...if the entry's creator is the same person as its
		// ...modifier...
		final UserInfo modifierInfo = m_fed.getModifier(); 
		if (m_fed.isModifierCreator()) {
			// ...add the modification time...
			VibeFlowPanel html     = new VibeFlowPanel();
			InlineLabel   htmlSpan = new InlineLabel(modifierInfo.getDate());
			htmlSpan.addStyleName("vibe-feView-headerContentPersonModifiedLabel");
			html.add(htmlSpan);
			InlineLabel modifierLabel = new InlineLabel();
			modifierLabel.addStyleName("vibe-feView-headerContentPersonModified");
			modifierLabel.getElement().setInnerHTML(("<strong>" + m_messages.folderEntry_Modified() + "</strong>") + html.getElement().getInnerHTML());
			creatorPanel.add(modifierLabel);
		}
		
		else {
			// ...otherwise, add the entry's modifier...
			VibeFlowPanel modifierPanel = new VibeFlowPanel();
			modifierPanel.addStyleName("vibe-feView-headerContentPersonPanel");
			final Anchor modifierPanelAnchor = new Anchor();
			modifierPanelAnchor.addStyleName("vibe-feView-headerContentPersonAnchor");
			modifierPanelAnchor.getElement().appendChild(modifierPanel.getElement());
			modifierPanelAnchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					invokeProfile(modifierInfo.getPrincipalInfo(), modifierPanelAnchor);
				}
			});
			contentPanel.add(modifierPanelAnchor);
			String modifierAvatarUrl = modifierInfo.getPrincipalInfo().getAvatarUrl();
			if (!(GwtClientHelper.hasString(modifierAvatarUrl))) {
				modifierAvatarUrl = m_images.userPhoto().getSafeUri().asString();
			}
			Image modifierAvatar = GwtClientHelper.buildImage(modifierAvatarUrl);
			modifierAvatar.addStyleName("vibe-feView-headerContentPersonAvatar");
			modifierPanel.add(modifierAvatar);
			InlineLabel modifier = new InlineLabel(modifierInfo.getPrincipalInfo().getTitle());
			modifierPanel.add(modifier);

			// ...and add then entry's modification time.
			InlineLabel modificationLabel = new InlineLabel();
			modificationLabel.addStyleName("vibe-feView-headerContentPersonCreated");
			VibeFlowPanel html     = new VibeFlowPanel();
			InlineLabel   htmlSpan = new InlineLabel(modifierInfo.getDate());
			htmlSpan.addStyleName("vibe-feView-headerContentPersonModifiedLabel");
			html.add(htmlSpan);
			InlineLabel modifierLabel = new InlineLabel();
			modifierLabel.addStyleName("vibe-feView-headerContentPersonModified");
			modifierLabel.getElement().setInnerHTML(("<strong>" + m_messages.folderEntry_Modified() + "</strong>") + html.getElement().getInnerHTML());
			modifierPanel.add(modifierLabel);
		}
	}
	
	/*
	 * Runs the appropriate profile viewer for the given PrincipalInfo. 
	 */
	private void invokeProfile(PrincipalInfo pi, Widget referenceWidget) {
		if (pi.isUserHasWS())
		     invokeSimpleProfile(   pi, referenceWidget);
		else invokeViewProfileEntry(pi                 );
	}
	
	/*
	 * Invokes the simple profile dialog on the principal.
	 */
	private void invokeSimpleProfile(PrincipalInfo pi, Widget referenceWidget) {
		Long wsId = pi.getPresenceUserWSId();
		String wsIdS = ((null == wsId) ? null : String.valueOf(wsId));
		GwtTeaming.fireEventAsync(
			new InvokeSimpleProfileEvent(
				new SimpleProfileParams(
					referenceWidget.getElement(),
					wsIdS,
					pi.getTitle())));
	}

	/*
	 * Invokes the view profile entry dialog on the principal.
	 */
	private void invokeViewProfileEntry(final PrincipalInfo pi) {
		// Have we instantiated a profile entry dialog yet?
		if (null == m_profileEntryDlg) {
			// No!  Instantiate one now.
			ProfileEntryDlg.createAsync(new ProfileEntryDlgClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(final ProfileEntryDlg peDlg) {
					// ...and show it.
					m_profileEntryDlg = peDlg;
					showProfileEntryDlgAsync(pi);
				}
			});
		}
		
		else {
			// Yes, we've instantiated a profile entry dialog already!
			// Simply show it.
			showProfileEntryDlgAsync(pi);
		}
	}

	/*
	 * Asynchronously marks the entry as having been read.
	 */
	private void markReadAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				markReadNow();
			}
		});
	}
	
	/*
	 * Synchronously marks the entry as having been read.
	 */
	private void markReadNow() {
		final Long entryId = m_fed.getEntityId().getEntityId();
		SetSeenCmd cmd = new SetSeenCmd(entryId);
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SetSeen(),
					String.valueOf(entryId));
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				GwtTeaming.fireEventAsync(new FolderEntryActionCompleteEvent(m_fed.getEntityId(), false));
			}
		});
	}

	/*
	 * Shows/hides the description.
	 */
	private void setDescVisible(boolean show) {
		// Set the visibility state...
		m_descPanel.setVisible(     show);
		m_showDescPanel.setVisible(!show);
		m_fec.resizeView();
		
		// ...and store the current state in a cookie.
		if (show)
		     FolderEntryCookies.removeCookieValue(    Cookie.DESCRIPTION_VISIBLE, m_fed.getEntityId()       );
		else FolderEntryCookies.setBooleanCookieValue(Cookie.DESCRIPTION_VISIBLE, m_fed.getEntityId(), false);
	}
	
	/*
	 * Asynchronously shows the profile entry dialog.
	 */
	private void showProfileEntryDlgAsync(final PrincipalInfo pi) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showProfileEntryDlgNow(pi);
			}
		});
	}
	
	/*
	 * Synchronously shows the profile entry dialog.
	 */
	private void showProfileEntryDlgNow(PrincipalInfo pi) {
		ProfileEntryDlg.initAndShow(m_profileEntryDlg, pi.getId());
	}
}