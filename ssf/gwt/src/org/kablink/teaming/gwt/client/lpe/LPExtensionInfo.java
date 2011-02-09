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
package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;

/**
 * This class is used to hold information about a landing page extension
 */
public class LPExtensionInfo
{
	private String m_displayName;
	private String m_desc;
	private String m_jspName;
	private boolean m_folderRequired;
	private boolean m_entryRequired;
	
	/**
	 * 
	 */
	public LPExtensionInfo( String jspName )
	{
		GwtTeamingMessages messages;
		
		messages = GwtTeaming.getMessages();
		
		if ( jspName.equalsIgnoreCase( "landing_page_entry.jsp" ) )
		{
			m_displayName = messages.lpExtDisplayEntry();
			m_desc = messages.lpExtDisplayEntryDesc();
			m_folderRequired = false;
			m_entryRequired = true;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_full_entry.jsp" ) )
		{
			m_displayName = messages.lpExtDisplayFullEntry();
			m_desc = messages.lpExtDisplayFullEntryDesc();
			m_folderRequired = false;
			m_entryRequired = true;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_folder.jsp" ) )
		{
			m_displayName = messages.lpExtDisplayRecentEntries();
			m_desc = messages.lpExtDisplayRecentEntriesDesc();
			m_folderRequired = true;
			m_entryRequired = false;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_folder_list.jsp" ) )
		{
			m_displayName = messages.lpExtDisplayRecentEntriesList();
			m_desc = messages.lpExtDisplayRecentEntriesListDesc();
			m_folderRequired = true;
			m_entryRequired = false;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_folder_list_sorted.jsp" ) )
		{
			m_displayName = messages.lpExtDisplayRecentEntriesListSorted();
			m_desc = messages.lpExtDisplayRecentEntriesListSortedDesc();
			m_folderRequired = true;
			m_entryRequired = false;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_folder_list_sorted_files.jsp" ) )
		{
			m_displayName = messages.lpeExtDisplayFileListSorted();
			m_desc = messages.lpeExtDisplayFileListSortedDesc();
			m_folderRequired = true;
			m_entryRequired = false;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_calendar.jsp" ) )
		{
			m_displayName = messages.lpeExtDisplayCalendarFolder();
			m_desc = messages.lpeExtDisplayCalendarFolderDesc();
			m_folderRequired = true;
			m_entryRequired = false;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_task_folder.jsp" ) )
		{
			m_displayName = messages.lpeExtDisplayTaskFolder();
			m_desc = messages.lpeExtDisplayTaskFolderDesc();
			m_folderRequired = true;
			m_entryRequired = false;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_survey.jsp" ) )
		{
			m_displayName = messages.lpeExtDisplaySurvey();
			m_desc = messages.lpeExtDisplaySurveyDesc();
			m_folderRequired = false;
			m_entryRequired = true;
		}
		else
		{
			m_displayName = "Unknown jsp: " + jspName;
			m_desc = "Unknown";
			m_folderRequired = false;
			m_entryRequired = false;
		}

		m_jspName = jspName;
	}
	
	/**
	 * 
	 */
	public String getDesc()
	{
		return m_desc;
	}
	
	/**
	 * 
	 */
	public String getDisplayName()
	{
		return m_displayName;
	}
	
	/**
	 * 
	 */
	public String getJspName()
	{
		return m_jspName;
	}
	
	/**
	 * Does this extension require the user to select an entry?
	 */
	public boolean isEntryRequired()
	{
		return m_entryRequired;
	}
	
	/**
	 * Does this extension require the user to select a folder?
	 */
	public boolean isFolderRequired()
	{
		return m_folderRequired;
	}
}

