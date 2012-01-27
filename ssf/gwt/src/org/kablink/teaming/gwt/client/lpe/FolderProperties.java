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

import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * This class holds all of the properties needed to define a "Folder" widget in a landing page.
 * @author jwootton
 *
 */
public class FolderProperties
	implements PropertiesObj
{
	private boolean m_showTitle;
	private boolean m_showDesc;
	private boolean m_showEntriesOpened;
	private int m_numEntriesToBeShown;
	private String m_folderId;
	private String m_folderName;
	private String m_parentBinderName;	// Name of the binder the folder is found in.
	private String m_zoneUUID;
	private String m_viewFolderUrl;
	private AsyncCallback<VibeRpcResponse> m_folderCallback;
	private boolean m_rpcInProgress;
	
	// The following data members are used to define the width and height of the view.
	private int m_width;
	private Style.Unit m_widthUnits;
	private int m_height;
	private Style.Unit m_heightUnits;
	private Style.Overflow m_overflow;

	/**
	 * 
	 */
	public FolderProperties()
	{
		m_showTitle = false;
		m_showDesc = false;
		m_showEntriesOpened = false;
		m_numEntriesToBeShown = 0;
		m_folderId = null;
		m_folderName = null;
		m_parentBinderName = null;
		m_zoneUUID = null;
		m_viewFolderUrl = null;
		
		// Default the width and height to 100%
		m_width = 100;
		m_widthUnits = Style.Unit.PCT;
		m_height = 100;
		m_heightUnits = Style.Unit.PCT;
		m_overflow = Style.Overflow.HIDDEN;

		// Create the callback that will be used when we issue an ajax call to get a GwtFolder object.
		m_folderCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetFolder(),
					m_folderId );
				m_rpcInProgress = false;
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( VibeRpcResponse response )
			{
				GwtFolder gwtFolder;
				
				gwtFolder = (GwtFolder) response.getResponseData();
				
				if ( gwtFolder != null )
				{
					setFolderName( gwtFolder.getFolderName() );
					setParentBinderName( gwtFolder.getParentBinderName() );
					setViewFolderUrl( gwtFolder.getViewFolderUrl() );
				}
				
				m_rpcInProgress = false;
			}// end onSuccess()
		};
		m_rpcInProgress = false;
	}// end FolderProperties()
	
	
	/**
	 * 
	 */
	public void copy( PropertiesObj props )
	{
		if ( props instanceof FolderProperties )
		{
			FolderProperties folderProps;
			String newFolderId;
			
			folderProps = (FolderProperties) props;
			
			// Did the folder id change?
			newFolderId = folderProps.getFolderId();
			if ( m_folderId != null && newFolderId != null && !m_folderId.equalsIgnoreCase( newFolderId ) )
			{
				// Yes, throw away the zone id.
				m_zoneUUID = null;
			}
			
			m_folderId = newFolderId;
			m_folderName = folderProps.getFolderName();
			m_parentBinderName = folderProps.getParentBinderName();
			m_showTitle = folderProps.getShowTitleValue();
			m_showDesc = folderProps.getShowDescValue();
			m_showEntriesOpened = folderProps.getShowEntriesOpenedValue();
			m_numEntriesToBeShown = folderProps.getNumEntriesToBeShownValue();
			m_viewFolderUrl = folderProps.getViewFolderUrl();
			m_width = folderProps.getWidth();
			m_widthUnits = folderProps.getWidthUnits();
			m_height = folderProps.getHeight();
			m_heightUnits = folderProps.getHeightUnits();
			m_overflow = folderProps.getOverflow();
		}
	}// end copy()
	

	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	public String createConfigString()
	{
		String str;
		
		// The string should look like: "folder,folderId=some id,showTitle=1,showFolderDescription=1,showEntriesOpened=1,entriesToShow=2;"
		str = "folder,folderId=";
		if ( m_folderId != null )
			str += m_folderId;
		str += ",";
		
		// Add the zone uuid if we have one.
		if ( m_zoneUUID != null && m_zoneUUID.length() > 0 )
			str += "zoneUUID=" + m_zoneUUID + ",";
		
		if ( m_showTitle )
			str += "showTitle=1,";
		
		if ( m_showDesc )
			str += "showFolderDescription=1,";

		if ( m_showEntriesOpened )
			str += "showEntriesOpened=1,";

		str += "entriesToShow=" + String.valueOf( m_numEntriesToBeShown );

		// Add the width
		str += ",width=" + String.valueOf( m_width );
		if ( m_widthUnits == Style.Unit.PCT )
			str += "%";
		else
			str += "px";

		// Add the height
		str += ",height=" + String.valueOf( m_height );
		if ( m_heightUnits == Style.Unit.PCT )
			str += "%";
		else
			str += "px";

		// Add overflow
		str += ",overflow=";
		if ( m_overflow == Style.Overflow.AUTO )
			str += "auto";
		else
			str += "hidden";

		str += ";";
		
		return str;
	}// end createConfigString()
	
	
	/**
	 * Issue an ajax request to get the folder's name from the server.
	 */
	public void getDataFromServer()
	{
		// Do we have a folder id?
		if ( m_folderId != null )
		{
			GetFolderCmd cmd;
			
			// Yes, Issue an ajax request to get the GwtFolder object for the given folder id.
			m_rpcInProgress = true;
			cmd = new GetFolderCmd( m_zoneUUID, m_folderId );
			GwtClientHelper.executeCommand( cmd, m_folderCallback );
		}
	}
	
	
	/**
	 * Return the folder id.
	 */
	public String getFolderId()
	{
		return m_folderId;
	}// end getFolderId()
	
	
	/**
	 * Return the name of the folder.
	 */
	public String getFolderName()
	{
		return m_folderName;
	}// end getFolderName()
	
	
	/**
	 * Return the value of height.
	 */
	public int getHeight()
	{
		return m_height;
	}
	
	/**
	 * Return the height units
	 */
	public Style.Unit getHeightUnits()
	{
		return m_heightUnits;
	}
	
	/**
	 * Return the "number of entries to be shown" property.
	 */
	public int getNumEntriesToBeShownValue()
	{
		return m_numEntriesToBeShown;
	}// end getNumEntriesToBeShownValue()
	
	
	/**
	 * Return the value of overflow.
	 */
	public Style.Overflow getOverflow()
	{
		return m_overflow;
	}
	
	/**
	 * Return the name of the binder the folder lives in.
	 */
	public String getParentBinderName()
	{
		return m_parentBinderName;
	}// end getParentBinderName()
	
	
	/**
	 * Return the "show the folder description" property.
	 */
	public boolean getShowDescValue()
	{
		return m_showDesc;
	}// end getShowDescValue()
	
	
	/**
	 * Return the property that tells us whether to show an entry opened.
	 */
	public boolean getShowEntriesOpenedValue()
	{
		return m_showEntriesOpened;
	}// end getShowEntriesOpenedValue()
	
	
	/**
	 * Return the "show title" property.
	 */
	public boolean getShowTitleValue()
	{
		return m_showTitle;
	}// end getShowTitleValue()
	
	
	/**
	 * Return the url that can be used to view this folder.
	 */
	public String getViewFolderUrl()
	{
		return m_viewFolderUrl;
	}// end getViewFolderUrl()
	
	
	/**
	 * Return the value of width.
	 */
	public int getWidth()
	{
		return m_width;
	}
	
	/**
	 * Return the width units
	 */
	public Style.Unit getWidthUnits()
	{
		return m_widthUnits;
	}
	
	/**
	 * Return the zone uuid
	 */
	public String getZoneUUID()
	{
		return m_zoneUUID;
	}// end getZoneUUID()
	
	
	/**
	 * Return whether an rpc call is in progress.
	 */
	public boolean isRpcInProgress()
	{
		return m_rpcInProgress;
	}// end isRpcInProgress()
	
	
	/**
	 * 
	 */
	public void setFolderId( String folderId )
	{
		// Did the folder id change?
		if ( m_folderId != null && m_folderId.equalsIgnoreCase( folderId ) )
		{
			// Yes
			// Since we are changing the folder id clear out the folder name and the name of the parent binder.
			m_folderName = "???";
			m_parentBinderName = "???";
		}
		
		m_folderId = folderId;
	}// end setFolderId()
	
	
	/**
	 * 
	 */
	public void setFolderName( String folderName )
	{
		m_folderName = folderName;
	}// end setFolderName()
	
	
	/**
	 * 
	 */
	public void setHeight( int height )
	{
		m_height = height;
	}
	
	/**
	 * 
	 */
	public void setHeightUnits( Style.Unit units )
	{
		m_heightUnits = units;
	}
	
	/**
	 * Set the "number of entries to be shown" property.
	 */
	public void setNumEntriesToBeShownValue( int numEntries )
	{
		m_numEntriesToBeShown = numEntries;
	}// end setNumEntriesToBeShownValue()
	
	
	/**
	 * 
	 */
	public void setOverflow( Style.Overflow overflow )
	{
		m_overflow = overflow;
	}
	
	/**
	 * 
	 */
	public void setParentBinderName( String parentBinderName )
	{
		m_parentBinderName = parentBinderName;
	}// end setParentBinderName()
	
	
	/**
	 * Return the "show the folder description" property.
	 */
	public void setShowDescValue( boolean showDesc )
	{
		m_showDesc = showDesc;
	}// end setShowDescValue()
	
	
	/**
	 * Set the property that tells us whether to show an entry opened.
	 */
	public void setShowEntriesOpenedValue( boolean showEntriesOpened )
	{
		m_showEntriesOpened = showEntriesOpened;
	}// end setShowEntriesOpenedValue()
	
	
	/**
	 * 
	 */
	public void setShowTitle( boolean showTitle)
	{
		m_showTitle = showTitle;
	}// end setShowBorder()


	/**
	 * Set the url that can be used to view this folder.
	 */
	public void setViewFolderUrl( String url )
	{
		m_viewFolderUrl = url;
	}// end setViewFolderUrl()
	
	
	/**
	 * 
	 */
	public void setWidth( int width )
	{
		m_width = width;
	}
	
	/**
	 * 
	 */
	public void setWidthUnits( Style.Unit units )
	{
		m_widthUnits = units;
	}
	
	/**
	 * 
	 */
	public void setZoneUUID( String zoneUUID )
	{
		m_zoneUUID = zoneUUID;
	}// end setZoneUUID()
}// end FolderProperties
