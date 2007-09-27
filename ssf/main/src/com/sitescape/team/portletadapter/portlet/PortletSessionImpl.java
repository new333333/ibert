/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.portletadapter.portlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;

import com.sitescape.team.util.Constants;

public class PortletSessionImpl implements PortletSession {

 	protected static final String PORTLET_SCOPE_NAMESPACE = "javax.portlet.p.";
	
	private HttpSession ses;
	private String portletName;
	private PortletContext ctx;
	private boolean invalid;
	private long creationTime;
	private long lastAccessedTime;
	private int interval;
	private boolean _new;
	
	public PortletSessionImpl(HttpSession ses, String portletName, PortletContext portletContext) {
		this.ses = ses;
		this.portletName = portletName;
		this.ctx = portletContext;
		this.invalid = false;
		this.creationTime = new Date().getTime();
		this.lastAccessedTime = creationTime;
		this.interval = ses.getMaxInactiveInterval();
		this._new = true;
	}

	public Object getAttribute(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (invalid) {
			throw new IllegalStateException();
		}

		return getAttribute(name, PortletSession.PORTLET_SCOPE);

	}

	public Object getAttribute(String name, int scope) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (invalid) {
			throw new IllegalStateException();
		}

		if (scope == PortletSession.PORTLET_SCOPE) {
			return ses.getAttribute(getPortletScopeName(name));
		}
		else {
			return ses.getAttribute(name);
		}
	}

	public Enumeration getAttributeNames() {
		if (invalid) {
			throw new IllegalStateException();
		}

		return getAttributeNames(PortletSession.PORTLET_SCOPE);

	}

	public Enumeration getAttributeNames(int scope) {
		if (invalid) {
			throw new IllegalStateException();
		}

		if (scope == PortletSession.PORTLET_SCOPE) {
			List attributeNames = new ArrayList();

			Enumeration enu = ses.getAttributeNames();

			while (enu.hasMoreElements()) {
				String name = (String)enu.nextElement();

				StringTokenizer st = new StringTokenizer(name, "?");

				if (st.countTokens() == 2) {
					if (st.nextToken().equals(
							PORTLET_SCOPE_NAMESPACE + portletName)) {

						attributeNames.add(st.nextToken());
					}
				}
			}

			return Collections.enumeration(attributeNames);
		}
		else {
			return ses.getAttributeNames();
		}

	}

	public long getCreationTime() {
		if (invalid) {
			throw new IllegalStateException();
		}

		return creationTime;
	}

	public String getId() {
		return ses.getId();
	}

	public long getLastAccessedTime() {
		return lastAccessedTime;

	}

	public int getMaxInactiveInterval() {
		return interval;
	}

	public void invalidate() {
		if (invalid) {
			throw new IllegalStateException();
		}

		ses.invalidate();

		invalid = true;

	}

	public boolean isNew() {
		if (invalid) {
			throw new IllegalStateException();
		}

		return _new;
	}

	public void removeAttribute(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (invalid) {
			throw new IllegalStateException();
		}

		removeAttribute(name, PortletSession.PORTLET_SCOPE);

	}

	public void removeAttribute(String name, int scope) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (invalid) {
			throw new IllegalStateException();
		}

		if (scope == PortletSession.PORTLET_SCOPE) {
			ses.removeAttribute(getPortletScopeName(name));
		}
		else {
			ses.removeAttribute(name);
		}
	}

	public void setAttribute(String name, Object value) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (invalid) {
			throw new IllegalStateException();
		}

		setAttribute(name, value, PortletSession.PORTLET_SCOPE);
	}

	public void setAttribute(String name, Object value, int scope) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		if (invalid) {
			throw new IllegalStateException();
		}

		if (scope == PortletSession.PORTLET_SCOPE) {
			ses.setAttribute(getPortletScopeName(name), value);
		}
		else {
			ses.setAttribute(name, value);
		}
	}

	public void setMaxInactiveInterval(int interval) {
		this.interval = interval;
	}

	public PortletContext getPortletContext() {
		return ctx;
	}
	
	boolean isValid() {
		return !invalid;
	}
	
	private String getPortletScopeName(String name) {
		return PORTLET_SCOPE_NAMESPACE + portletName + Constants.QUESTION +
			name;
	}

}
