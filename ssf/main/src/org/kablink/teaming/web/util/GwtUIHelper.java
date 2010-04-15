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

package org.kablink.teaming.web.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.Toolbar;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;

/**
 * Helper methods for the GWT UI code.
 * 
 * @author drfoster@novell.com
 */
public class GwtUIHelper {
	// Used as a qualifier in a toolbar to indicate the value maps to a
	// GWT UI TeamingAction.
	public final static String GWTUI_TEAMING_ACTION = "GwtUI.TeamingAction";
	
	// The key into the session cache to store the GWT UI toolbar
	// beans.
	public final static String CACHED_TOOLBARS_KEY = "gwt-ui-toolbars";
	
	// When caching a toolbar containing an AdaptedPortletURL, we
	// must also store the URL in string form for the GWT UI to use.
	// If we don't, an NPE is generated when we try to convert it. 
	public final static String URLFIXUP_PATCH = ".urlAsString";

	// The names of the toolbar beans stored in the session cache for
	// the GWT UI.
	public final static String[] CACHED_TOOLBARS = new String[] {
		WebKeys.CALENDAR_IMPORT_TOOLBAR,
		WebKeys.EMAIL_SUBSCRIPTION_TOOLBAR,
		WebKeys.FOLDER_ACTIONS_TOOLBAR,
		WebKeys.FOLDER_TOOLBAR,
		WebKeys.FOLDER_VIEWS_TOOLBAR,
		WebKeys.GWT_MISC_TOOLBAR,
		WebKeys.WHATS_NEW_TOOLBAR,
	};

	/*
	 * Adds a send email item to the toolbar.
	 */
	@SuppressWarnings("unchecked")
	private static void addSendEmailToToolbar(AllModulesInjected bs, RenderRequest request, Map model, Binder binder, Toolbar tb) {
		// If we don't have a URL...
		String url = ((String) model.get(WebKeys.TOOLBAR_SENDMAIL_URL));
		if (!(MiscUtil.hasString(url))) {
			// ...we don't add anything to the toolbar.
			return;
		}

		// If we don't have any contributor IDs to send to...
		String[] contributorIds = ((String[]) model.get(WebKeys.TOOLBAR_SENDMAIL_IDS));
		if (null == contributorIds) {
			// ...default to an empty list.
			contributorIds = new String[0];
		}
		int contributorsCount = contributorIds.length;
				
		// If we don't have any contributors...
		Boolean post;
		if (0 == contributorsCount) {
			// ...we don't have to worry about the post flag.
			post = Boolean.FALSE;
		}
		else {
			// Otherwise, if we weren't passed a post flag...
			post = ((Boolean) model.get(WebKeys.TOOLBAR_SENDMAIL_POST));
			if (null == post) {
				// ...default it to true since there were contributors.
				post = Boolean.TRUE;
			}
		}
		
		// Generate the qualifiers to run a URL in a popup window...
		Map qualifiers = new HashMap();
		qualifiers.put("popup",       "true");
		qualifiers.put("popupWidth",  "600");
		qualifiers.put("popupHeight", "600");
		if (post) {
			// ...allowing for any contributors...
			String contributors = "";
			for (int i = 0 ; i < contributorsCount; i += 1) {
				if (0 < i) contributors += ",";
				contributors += String.valueOf(contributorIds[i]);
			}
			qualifiers.put("popup.fromForm",            "true");
			qualifiers.put("popup.hiddenInput.count",   "1");
			qualifiers.put("popup.hiddenInput.0.name",  WebKeys.USER_IDS_TO_ADD);
			qualifiers.put("popup.hiddenInput.0.value", contributors);
		}
		
		// ...and add it as a toolbar menu item.
		tb.addToolbarMenu(
			"sendEmail",
			NLT.get("toolbar.menu.sendMail"),
			url,
			qualifiers);
	}
	
	/*
	 * Adds a share this binder item to the toolbar.
	 */
	@SuppressWarnings("unchecked")
	private static void addShareBinderToToolbar(AllModulesInjected bs, RenderRequest request, Map model, Binder binder, Toolbar tb) {
		// Generate the qualifiers to run a URL in a popup window...
		Map qualifiers = new HashMap();
		qualifiers.put("popup",       "true");
		qualifiers.put("popupWidth",  "550");
		qualifiers.put("popupHeight", "750");
		
		// ...generate the URL to share the binder...
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		url.setParameter(WebKeys.ACTION, "__ajax_relevance");
		url.setParameter(WebKeys.URL_OPERATION, "share_this_binder");
		url.setParameter(WebKeys.URL_BINDER_ID, String.valueOf(binder.getId()));
		
		// ...and add it as a toolbar menu item.
		tb.addToolbarMenu(
			"share",
			NLT.get(
				buildRelevanceKey(
					binder,
					"relevance.shareThis",
					false)),	// false -> Don't special case the key for person.
			url.toString(),
			qualifiers);
	}
	
	/**
	 * Adds a teaming action a toolbar.
	 * 
	 * @param tb
	 * @param menuName
	 * @param teamingAction
	 * @param title
	 * @param url
	 * @param qualifiers
	 */
	@SuppressWarnings("unchecked")
	public static void addTeamingActionToToolbar(Toolbar tb, String menuName, String teamingAction, String title, String url, Map qualifiers) {
		// Add the teaming action to the qualifiers...
		if (null == qualifiers) {
			qualifiers = new HashMap();
		}
		qualifiers.put(GWTUI_TEAMING_ACTION, teamingAction);
		
		// ...and add it as a toolbar menu item.
		tb.addToolbarMenu(
			menuName,
			((null == title) ? "" : title),
			((null == url)   ? "" : url),
			qualifiers);
	}
	
	/*
	 * Adds a track this binder item to the toolbar.
	 */
	@SuppressWarnings("unchecked")
	private static void addTrackBinderToToolbar(AllModulesInjected bs, RenderRequest request, Map model, Binder binder, Toolbar tb) {
		// Generate the appropriate teaming action and title string...
		String action   = "TRACK_BINDER";
		String titleKey = buildRelevanceKey(binder, "relevance.trackThis", true);
		if (BinderHelper.isBinderTracked(bs, binder.getId())) {
			action   = "UN" + action;
			titleKey += "Not";
		}
		
		// ...and add t to the toolbar.
		addTeamingActionToToolbar(
			tb,
			"track",
			action,
			NLT.get(titleKey),
			null,
			null);
	}

	/**
	 * Builds the GWT miscellaneous toolbar for a binder.
	 *
	 * @param bs
	 * @param request
	 * @param user
	 * @param binder
	 * @param model
	 * @param qualifiers
	 * @param gwtMiscToolbar
	 */
	@SuppressWarnings("unchecked")
	public static void buildGwtMiscToolbar(AllModulesInjected bs, RenderRequest request, User user, Binder binder, Map model, Toolbar gwtMiscToolbar) {
		// We only add the GWT miscellaneous toolbar items if the GWT
		// UI is active.  Is it?
		if (isGwtUIActive(request)) {
			// Yes!  Are we running as other than guest?
			if (!(isCurrentUserGuest())) {
				// Yes!  Is the binder we're on other than the profiles
				// container?
				if (EntityIdentifier.EntityType.profiles != binder.getEntityType()) {
					// Yes!  Add toolbar items to track and share the
					// binder.
					addSendEmailToToolbar(  bs, request, model, binder, gwtMiscToolbar);
					addShareBinderToToolbar(bs, request, model, binder, gwtMiscToolbar);
					addTrackBinderToToolbar(bs, request, model, binder, gwtMiscToolbar);
				}
			}
			
//!			...this needs to be implemented...
		}
	}

	/**
	 * Builds the GWT UI toolbar for a binder.
	 * 
	 * @param bs
	 * @param request
	 * @param user
	 * @param binder
	 * @param model
	 * @param qualifiers
	 * @param gwtUIToolbar
	 */
	@SuppressWarnings("unchecked")
	public static void buildGwtUIToolbar(AllModulesInjected bs, RenderRequest request, User user, Binder binder, Map model, Toolbar gwtUIToolbar) {
		// If the GWT UI is enabled and we're not in captive mode...
		if (isGwtUIEnabled() && (!(MiscUtil.isCaptive(request)))) {
			// ...add the GWT UI button to the menu bar.
			String title = "Activate the Durango UI";
			Map qualifiers = new HashMap();
			qualifiers.put("title", title);
			qualifiers.put("icon", "gwt.png");
			qualifiers.put("iconGwtUI", "true");
			qualifiers.put("onClick", "ss_toggleGwtUI(true);return false;");
			gwtUIToolbar.addToolbarMenu("1_gwtUI", title, "javascript: //;", qualifiers);
		}
	}

	/*
	 * Generates the appropriate resource key for the given binder.
	 * 
	 * Note:  The logic for which key is based on the logic from
	 *        sidebar_track2.jsp.
	 */
	private static String buildRelevanceKey(Binder binder, String keyBase, boolean specialCasePerson) {
		String relevanceKey = keyBase;
		switch (binder.getEntityType()) {
		case workspace:
			String addOn = "Workspace";
			if (specialCasePerson) {
				Integer dType = binder.getDefinitionType();
				if ((null != dType) && (Definition.USER_WORKSPACE_VIEW == dType)) {
					addOn = "Person";
				}
			}
			relevanceKey += addOn;
			break;
			
		case folder:
			String dFamily = "";
			Element familyProperty = ((Element) binder.getDefaultViewDef().getDefinition().getRootElement().selectSingleNode("//properties/property[@name='family']"));
			if (familyProperty != null) {
				dFamily = familyProperty.attributeValue("value", "");
				if (null == dFamily) {
					dFamily = "";
				}
			}
			if (dFamily.equalsIgnoreCase("calendar")) relevanceKey += "Calendar";
			else                                      relevanceKey += "Folder";
		}
		
		return relevanceKey;
	}
	
	/**
	 * When in GWT UI mode, extracts the toolbar beans from the model
	 * and stores them in the session cache.
	 * 
	 * @param pRequest
	 * @param mv
	 */
	public static ModelAndView cacheToolbarBeans(PortletRequest pRequest, ModelAndView mv) {
		return cacheToolbarBeans(WebHelper.getHttpServletRequest(pRequest), mv);
	}

	/**
	 * When in GWT UI mode, extracts the toolbar beans from the model
	 * and stores them in the session cache.
	 * 
	 * @param hRequest
	 * @param mv
	 */
	public static ModelAndView cacheToolbarBeans(HttpServletRequest hRequest, ModelAndView mv) {
		cacheToolbarBeansImpl(hRequest, ((null == mv) ? null : mv.getModel()));
		return mv;
	}

	/*
	 * When in GWT UI mode, extracts the toolbar beans from the model
	 * and stores them in the session cache.
	 */
	@SuppressWarnings("unchecked")
	private static void cacheToolbarBeansImpl(HttpServletRequest hRequest, Map model) {
		// If we don't have an HttpServletRequest...
		if (null == hRequest) {
			// ...bail.
			return;
		}

		// Clear any previous toolbars we may have cached.
		HttpSession hSession = WebHelper.getRequiredSession(hRequest);
		hSession.removeAttribute(CACHED_TOOLBARS_KEY);

		// If we're not in GWT UI mode...
		if (!(isGwtUIActive(hRequest))) {
			// ...bail.
			return;
		}

		// If we don't have a model or the model data is empty...
		if ((null == model) || (0 == model.size())) {
			// ...bail.
			return;
		}

		// Scan the names of toolbars we need to cache.
		HashMap<String, SortedMap> tbHM = new HashMap<String, SortedMap>();
		for (int i = 0; i < CACHED_TOOLBARS.length; i += 1) {
			// Does a toolbar by this name exist?
			String tbName = CACHED_TOOLBARS[i];
			SortedMap tb = ((SortedMap) model.get(tbName));
			if ((null != tb) && (!(tb.isEmpty()))) {
				// Yes! Add it to the HashMap.
				fixupAdaptedPortletURLs(tb);
				tbHM.put(tbName, tb);
			}
		}

		// Finally, store the HashMap of toolbars in the session
		// cache.
		hSession.setAttribute(CACHED_TOOLBARS_KEY, tbHM);
	}

	/*
	 * Walks the toolbar map and stores the URL from any
	 * AdaptedPortletURL in its string form.
	 * 
	 * We do this because when the GWT UI code accesses the toolbars,
	 * it cannot process the AdaptedPortletURL as a string as an NPE 
	 * is generated when we try to convert it. 
	 */
	@SuppressWarnings("unchecked")
	private static void fixupAdaptedPortletURLs(Map tbMap) {
		Set tbKeySet = tbMap.keySet();
		for (Iterator tbKeyIT = tbKeySet.iterator(); tbKeyIT.hasNext(); ) {
			Object tbKeyO = tbKeyIT.next();
			if (tbKeyO instanceof String) {
				String tbKey = ((String) tbKeyO);
				Object tbO = tbMap.get(tbKey);
				if (tbO instanceof AdaptedPortletURL) {
					String url = ((AdaptedPortletURL) tbO).toString();
					tbMap.put(tbKey + URLFIXUP_PATCH, url);
				}
				else if (tbO instanceof Map) {
					fixupAdaptedPortletURLs((Map) tbO);
				}
			}
		}
	}

	/**
	 * Returns true if the current user is the built-in guest user and
	 * false otherwise.
	 * 
	 * @return
	 */
	public static boolean isCurrentUserGuest() {
		User user = RequestContextHolder.getRequestContext().getUser();
		return ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId());
	}
	
	/**
	 * Returns true if the GWT UI should be available and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isGwtUIEnabled() {
		String durangoUI = SPropsUtil.getString("use-durango-ui", "");
		return (MiscUtil.hasString(durangoUI) && "1".equals(durangoUI));
	}

	/**
	 * Returns true if the GWT UI should be active and false otherwise.
	 * 
	 * @param pRequest
	 * 
	 * @return
	 */
	public static boolean isGwtUIActive(PortletRequest pRequest) {
		HttpServletRequest hRequest = WebHelper.getHttpServletRequest(pRequest);
		boolean reply = (null != pRequest);
		if (reply) {
			reply = isGwtUIActive(hRequest);
		}
		return reply;
	}

	/**
	 * Returns true if the GWT UI should be active and false otherwise.
	 * 
	 * @param hRequest
	 * 
	 * @return
	 */
	public static boolean isGwtUIActive(HttpServletRequest hRequest) {
		boolean reply = isGwtUIEnabled();
		if (reply) {
			reply = (null != hRequest);
			if (reply) {
				HttpSession hSession = WebHelper.getRequiredSession(hRequest);
				Object durangoUI = hSession.getAttribute("use-durango-ui");
				reply = ((null != durangoUI) && (durangoUI instanceof Boolean) && ((Boolean) durangoUI).booleanValue());
			}
		}
		return reply;

	}

	/**
	 * Updates stores the current GWT UI active flag in the session
	 * cache.
	 * 
	 * @param pRequest
	 * @param gwtUIActive
	 */
	public static void setGwtUIActive(PortletRequest pRequest, boolean gwtUIActive) {
		HttpServletRequest hRequest = WebHelper.getHttpServletRequest(pRequest);
		if (null != hRequest) {
			setGwtUIActive(hRequest, gwtUIActive);
		}
	}

	/**
	 * Updates stores the current GWT UI active flag in the session
	 * cache.
	 * 
	 * @param hRequest
	 * @param gwtUIActive
	 */
	public static void setGwtUIActive(HttpServletRequest hRequest, boolean gwtUIActive) {
		if (null != hRequest) {
			HttpSession hSession = WebHelper.getRequiredSession(hRequest);
			hSession.setAttribute("use-durango-ui", new Boolean(gwtUIActive && isGwtUIEnabled()));
		}
	}
}
