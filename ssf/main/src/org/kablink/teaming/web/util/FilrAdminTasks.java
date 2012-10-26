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
package org.kablink.teaming.web.util;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * A FilrAdminTasks object contains a list of tasks that the Filr administrator needs to do.
 * At this time the only task would be "Enter the proxy credentials for a Net Folder Server"
 * 
 * The xml looks like the following xml:
 *	<FilrAdminTasks>
 *		<EnterNetFolderServerProxyCredentials>
 *			<NetFolderServer id="some value" />
 *			<NetFolderServer id="some value" />
 *		</EnterNetFolderServerProxyCredentials>
 *	</FilrAdminTasks>
 * 
 * @author jwootton
 */
public class FilrAdminTasks
{
	private Document m_adminTasksDoc = null;
	
	/**
	 * 
	 */
	public FilrAdminTasks()
	{
	}
	
	/**
	 * 
	 */
	public FilrAdminTasks( Document adminTasksDoc )
	{
		m_adminTasksDoc = adminTasksDoc;
	}

	/**
	 * 
	 */
	public FilrAdminTasks( String xmlEncoding )
	{
		if ( xmlEncoding == null )
			return;
		
		try
		{
			m_adminTasksDoc = DocumentHelper.parseText( xmlEncoding );
		}
		catch ( Exception ex )
		{
		};
	}

	/**
	 * Add an "enter net folder server proxy credentials" task. 
	 */
	public Document addEnterNetFolderServerProxyCredentialsTask( Long netFolderServerId )
	{
		Element taskElement;
		Element parentElement;
		String idStr;
		
		if ( netFolderServerId == null )
			return null;
		
		idStr = String.valueOf( netFolderServerId );
		
		// Make sure we have a document to work with.
		getAdminTasksDoc();
		
		// Does a <NetFolderServer id="xxx" /> exists under <EnterNetFolderServerProxyCredentials>?
		taskElement = getEnterProxyCredentialsTask( idStr );
		if ( taskElement != null )
		{
			// Yes
			return m_adminTasksDoc;
		}
		
		// Get the <EnterNetFolderServerProxyCredentials> element
		parentElement = getEnterProxyCredentialsElement();
		
		// Add <NetFolderServer id="some value" />
		taskElement = parentElement.addElement( "NetFolderServer" );
		taskElement.addAttribute( "id", idStr );
		
		return m_adminTasksDoc;
	}

	/**
	 * 
	 */
	private Document createAdminTasksRootDocument()
	{
		Document doc;

		doc = DocumentHelper.createDocument();
		doc.addElement( "FilrAdminTasks" );
		
		return doc;
	}
	
	/**
	 * 
	 */
	public Document deleteEnterNetFolderServerProxyCredentialsTask( Long netFolderServerId )
	{
		Element taskElement;
		String idStr;
		
		getAdminTasksDoc();
		
		idStr = String.valueOf( netFolderServerId );
		
		// Does a <NetFolderServer id="xxx" /> exists under <EnterNetFolderServerProxyCredentials>?
		taskElement = getEnterProxyCredentialsTask( idStr );
		if ( taskElement != null )
		{
			taskElement.getParent().remove( taskElement );
		}
		
		return m_adminTasksDoc;
	}

	/**
	 * 
	 */
	private Document getAdminTasksDoc()
	{
		if ( m_adminTasksDoc == null )
		{
			m_adminTasksDoc = createAdminTasksRootDocument();
		}
		
		return m_adminTasksDoc;
	}
	
	/**
	 * Return all of the "enter proxy credentials" tasks
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<EnterProxyCredentialsTask> getAllEnterProxyCredentialsTasks()
	{
		ArrayList<EnterProxyCredentialsTask> listOfTasks;
		List listOfElements;
		
		listOfTasks = new ArrayList<EnterProxyCredentialsTask>();
		
		getAdminTasksDoc();
		
		// Get all of the <NetFolderServer> elements that live under <EnterNetFolderServerProxyCredentials>
		listOfElements = m_adminTasksDoc.selectNodes( "/FilrAdminTasks/EnterNetFolderServerProxyCredentials/NetFolderServer" ); 
		
		if ( listOfElements != null )
		{
			Iterator iter;
			
			iter = listOfElements.iterator();
			while ( iter.hasNext() ) 
			{
				Element nextElement;
				Node node;
				
				nextElement = (Element) iter.next();
				node = nextElement.selectSingleNode( "@id" );
				if ( node != null && node instanceof Attribute )
				{
					Attribute attrib;
					EnterProxyCredentialsTask task;

					attrib = (Attribute) node;
					task = new EnterProxyCredentialsTask();
					task.setNetFolderServerId( attrib.getValue() );
					
					listOfTasks.add( task );
				}
			}
		}
		
		return listOfTasks;
	}
	
	/**
	 * See if a <NetFolderServer id="xxx" /> exists under <EnterNetFolderServerProxyCredentials>
	 */
	private Element getEnterProxyCredentialsTask( String netFolderServerId )
	{
		Element rootElement;
		Element serverElement;

		if ( netFolderServerId == null )
			return null;
		
		rootElement = m_adminTasksDoc.getRootElement();
		
		// Look for the <NetFolderServer> element
		serverElement = (Element)rootElement.selectSingleNode( "/FilrAdminTasks/EnterNetFolderServerProxyCredentials/NetFolderServer[@id='" + netFolderServerId + "']" ); 
			
		return serverElement;
	}
	
	/**
	 * Find the <EnterNetFolderServerProxyCredentials> element.  If it doesn't exist, create it.
	 */
	private Element getEnterProxyCredentialsElement()
	{
		Element rootElement;
		Element enterProxyCredentialsElement;
		
		rootElement = m_adminTasksDoc.getRootElement();
		
		// Look for the <EnterNetFolderServerProxyCredentials> element
		enterProxyCredentialsElement = (Element)rootElement.selectSingleNode( "/FilrAdminTasks/EnterNetFolderServerProxyCredentials" ); 
			
		// Did we find a <EnterNetFolderServerProxyCredentials> element?
		if ( enterProxyCredentialsElement == null )
		{
			// No, create one.
			enterProxyCredentialsElement = rootElement.addElement( "EnterNetFolderServerProxyCredentials" );
		}
		
		return enterProxyCredentialsElement;
	}

	/**
	 * 
	 */
	public String toString()
	{
		return getAdminTasksDoc().asXML();
	}
}