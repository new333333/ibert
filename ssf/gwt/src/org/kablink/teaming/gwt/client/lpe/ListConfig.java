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

import java.util.ArrayList;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Composite;

/**
 * This class represents the configuration data for a List
 * @author jwootton
 *
 */
public class ListConfig extends ConfigItem
{
	private ListProperties m_properties;
	private ArrayList<ConfigItem> m_configItems;
	
	
	/**
	 * 
	 */
	public ListConfig( String configStr )
	{
		int i;
		String[] propsStr;
		
		m_properties = new ListProperties();
		m_configItems = new ArrayList<ConfigItem>();

		// Split the string "listStart,showBorder=1,title=xxx" into its parts.
		propsStr = configStr.split( "[,;]" );
		
		// Get the list properties
		if ( propsStr != null )
		{
			for (i = 0; i < propsStr.length; ++i)
			{
				String[] results2;
				
				results2 = ConfigData.splitConfigItem( propsStr[i] );
				if ( results2 != null && results2.length == 2 && results2[0] != null && results2[1] != null && results2[1].length() > 0 )
				{
					try
					{
						if ( results2[0].equalsIgnoreCase( "showBorder" ) )
							m_properties.setShowBorder( results2[1].equalsIgnoreCase( "1" ) );
						else if ( results2[0].equalsIgnoreCase( "title" ) )
							m_properties.setTitle( URL.decodeComponent( results2[1] ) );
					}
					catch (Exception ex)
					{
						// Nothing to do.  This is here to handle the case when the data is
						// not properly url encoded.
					}
				}
			}// end for()
		}
	}// end ListConfig()
	
	
	/**
	 * 
	 */
	public void addChild( ConfigItem configItem )
	{
		m_configItems.add( configItem );
	}// end addChild()
	
	
	/**
	 * Create a composite that can be used on any page.
	 */
	public Composite createComposite()
	{
		return null;
	}
	
	/**
	 * Create a DropWidget that can be used in the landing page editor.
	 */
	public ListDropWidget createDropWidget( LandingPageEditor lpe )
	{
		return new ListDropWidget( lpe, this );
	}
	
	
	/**
	 * 
	 */
	public ConfigItem get( int index )
	{
		if ( index < m_configItems.size() )
			return m_configItems.get( index );
		
		return null;
	}// end get()
	
	/**
	 * 
	 */
	public ListProperties getProperties()
	{
		return m_properties;
	}// end getProperties()


	/**
	 * 
	 */
	public int numItems()
	{
		return m_configItems.size();
	}// end numItems()
}// end ListConfig
