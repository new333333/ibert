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
package com.sitescape.team.docconverter.impl;

import com.sitescape.team.docconverter.HtmlConverter;
import com.sitescape.team.docconverter.IHtmlConverterManager;

public class HtmlConverterManagerImpl
	implements IHtmlConverterManager
{
	int _active = IHtmlConverterManager.OPENOFFICE;
	HtmlConverter _stellentConverter = null,
				  _openOfficeConverter = null;
	
	public HtmlConverterManagerImpl() {}
	
	public HtmlConverter getConverter()
	{
		if (_active == IHtmlConverterManager.STELLANT)
			return _stellentConverter;
		else
		if (_active == IHtmlConverterManager.OPENOFFICE)
			return _openOfficeConverter;
		
		return null;
	}
	
	public HtmlConverter getConverter(int type)
	{
		if (type == IHtmlConverterManager.STELLANT)
			return _stellentConverter;
		else
		if (type == IHtmlConverterManager.OPENOFFICE)
			return _openOfficeConverter;
		
		return null;
	}
	
	public void setActive(int active)
	{
		_active = active;
	}
	
	public int getActive()
	{
		return _active;
	}
	
	public void setStellentConverter(HtmlConverter converter_in)
	{
		_stellentConverter = converter_in;
	}
	
	public HtmlConverter getStellentConverter()
	{
		return _stellentConverter;
	}
	
	public void setOpenOfficeConverter(HtmlConverter converter_in)
	{
		_openOfficeConverter = converter_in;
	}
	
	public HtmlConverter getOpenOfficeConverter()
	{
		return _openOfficeConverter;
	}
}
