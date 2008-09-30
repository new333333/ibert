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
package com.sitescape.team.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Interceptor;
import org.hibernate.engine.SessionImplementor;

import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
/**
 * Helper class to setup hibernate.  Simulates openSessionInView for
 * non-web users, ie) scheduled jobs.
 * @author Janet McCann
 *
 */
public class SessionUtil {
	private static SessionFactory sessionFactory=null;
	private static SessionFactory getSessionFactory() {
		if (sessionFactory == null) sessionFactory = (SessionFactory)SpringContextUtil.getBean("sessionFactory");
		return sessionFactory;
	}
	public static Session getSession() {
		return SessionFactoryUtils.getSession(getSessionFactory(), false);
	}
	public static void sessionStartup() {
		//open shared session
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));		
	}
	public static void sessionStartup(Interceptor interceptor) {
		//open shared session
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), interceptor, null);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));		
	}
	public static Interceptor getInterceptor() {
		Session session = getSession();
		if (session == null) return null;
		if (session instanceof SessionImplementor) {
			SessionImplementor sI = (SessionImplementor) session;
			return sI.getInterceptor();
		}
		return null;
	}
	public static void sessionStop() {
	   	if (TransactionSynchronizationManager.hasResource(getSessionFactory())) {
	   	 		SessionHolder sessionHolder =
			(SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
	   	 		SessionFactoryUtils.releaseSession(sessionHolder.getSession(), sessionFactory);
	   	}
	}
	public static boolean sessionActive() {
	   	if (TransactionSynchronizationManager.hasResource(getSessionFactory())) return true;
	   	return false;
		
	}
}
