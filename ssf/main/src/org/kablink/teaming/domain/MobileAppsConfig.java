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
package org.kablink.teaming.domain;

import org.kablink.teaming.util.SPropsUtil;

/**
 * Component of zoneConfig that holds the settings for mobile
 * applications.
 * 
 * @author jwootton
 */
public class MobileAppsConfig
{
	private Boolean mobileAppsEnabled;
	private Boolean mobileAppsAllowCachePwd;
	private Boolean mobileAppsAllowCacheContent;
	private Boolean mobileAppsAllowPlayWithOtherApps;
	private Integer mobileAppsSyncInterval;

	// The following are the data members for the Mobile Application
	// Management (MAM) settings.
    private Boolean					mobileCutCopyEnabled;						//
    private Boolean					mobileAndroidScreenCaptureEnabled;			//
    private Boolean					mobileDisableOnRootedOrJailBrokenDevices;	//
    private Integer					mobileOpenIn;								//
    private MobileOpenInWhiteLists	mobileOpenInWhiteLists;						//

    /**
     * Enumeration mapping of the Integer stored for mobileOpenIn. 
     * 
     * Note:  The ordinal numbers for these MUST MATCH EXACTLY
     * those defined in GwtMobileOpenInSetting.
     */
    public enum MobileOpenInSetting
    {
    	DISABLED,
    	ALL_APPLICATIONS,
    	WHITE_LIST;

    	/**
    	 * Returns a MobileOpenInSetting mapped from an integer.
    	 * 
    	 * @param setting
    	 * 
    	 * @return
    	 */
    	public static MobileOpenInSetting valueOf( int setting )
    	{
    		MobileOpenInSetting reply;
    		if      ( DISABLED.ordinal()   == setting ) reply = MobileOpenInSetting.DISABLED;
    		else if ( WHITE_LIST.ordinal() == setting ) reply = MobileOpenInSetting.WHITE_LIST;
    		else                                        reply = MobileOpenInSetting.ALL_APPLICATIONS;
    		return reply;
    	}
    }
	
	/**
	 * 
	 */
	public MobileAppsConfig()
	{
		super();
		this.mobileOpenInWhiteLists = new MobileOpenInWhiteLists();
	}

	/**
	 * 
	 */
	public boolean getMobileAppsAllowCacheContent()
	{
		if ( mobileAppsAllowCacheContent == null )
			return SPropsUtil.getBoolean( "mobile.apps.allow.cache.content.default", true );

		return mobileAppsAllowCacheContent.booleanValue();
	}
	
	/**
	 * 
	 */
	public boolean getMobileAppsAllowCachePwd()
	{
		if ( mobileAppsAllowCachePwd == null )
			return SPropsUtil.getBoolean( "mobile.apps.allow.cache.pwd.default", true );
		
		return mobileAppsAllowCachePwd.booleanValue();
	}

	/**
	 * 
	 */
	public boolean getMobileAppsAllowPlayWithOtherApps()
	{
		if ( mobileAppsAllowPlayWithOtherApps == null )
			return SPropsUtil.getBoolean( "mobile.apps.allow.play.with.other.apps.default", true );
		
		return mobileAppsAllowPlayWithOtherApps.booleanValue();
	}
	
	/**
	 * 
	 */
	public boolean getMobileAppsEnabled()
	{
		if( mobileAppsEnabled == null )
			return SPropsUtil.getBoolean( "mobile.apps.enabled.default", true );
		
		return mobileAppsEnabled.booleanValue();
	}

	/**
	 * The sync interval is in minutes.
	 */
	public int getMobileAppsSyncInterval()
	{
		if ( mobileAppsSyncInterval == null )
			return SPropsUtil.getInt( "mobile.apps.sync.interval.default", 15 );
		
		return mobileAppsSyncInterval.intValue();
	}

	/**
	 * 
	 */
	public void setMobileAppsAllowCacheContent( boolean allow )
	{
		mobileAppsAllowCacheContent = Boolean.valueOf( allow );
	}
	
	/**
	 * 
	 */
	public void setMobileAppsAllowCachePwd( boolean allow )
	{
		mobileAppsAllowCachePwd = Boolean.valueOf( allow );
	}

	/**
	 * 
	 */
	public void setMobileAppsAllowPlayWithOtherApps( boolean allow )
	{
		mobileAppsAllowPlayWithOtherApps = Boolean.valueOf( allow );
	}

	/**
	 * 
	 */
	public void setMobileAppsEnabled( boolean enabled )
	{
		mobileAppsEnabled = Boolean.valueOf( enabled );
	}
	
	/**
	 * 
	 */
	public void setMobileAppsSyncInterval( int intervalInMinutes )
	{
		mobileAppsSyncInterval = Integer.valueOf( intervalInMinutes );
	}
	
    /**
     * Get'er methods for the Mobile Application Management (MAM)
     * settings.
     * 
     * @return
     */
    public boolean getMobileCutCopyEnabled()
    {
		if ( null == mobileCutCopyEnabled )
		{
			return SPropsUtil.getBoolean( "mobile.apps.cut.copy.enabled", true );
		}

    	return this.mobileCutCopyEnabled;
    }
    
    public boolean getMobileAndroidScreenCaptureEnabled()
    {
    	if ( null == mobileAndroidScreenCaptureEnabled )
    	{
			return SPropsUtil.getBoolean( "mobile.apps.android.screen.capture.enabled", true );
    	}
    	
    	return this.mobileAndroidScreenCaptureEnabled;
    }
    
    public boolean getMobileDisableOnRootedOrJailBrokenDevices()
    {
    	if ( null == mobileDisableOnRootedOrJailBrokenDevices )
    	{
			return SPropsUtil.getBoolean( "mobile.apps.disable.on.rooted.or.jail.broken.devices", true );
    	}
    	
    	return this.mobileDisableOnRootedOrJailBrokenDevices;
    }
    
    public MobileOpenInSetting getMobileOpenIn()
    {
    	if ( null == this.mobileOpenIn )
    	{
    		return null;
    	}
    	
    	return MobileOpenInSetting.valueOf( this.mobileOpenIn );
    }
    
    public MobileOpenInWhiteLists getMobileOpenInWhiteLists()
    {
    	return this.mobileOpenInWhiteLists;
    }
    
    /**
     * Set'er methods for the Mobile Application Management (MAM)
     * settings.
     * 
     * @param
     */
    public void setMobileCutCopyEnabled(                     boolean                mobileCutCopyEnabled )                     { this.mobileCutCopyEnabled                     = mobileCutCopyEnabled;                                    }
    public void setMobileAndroidScreenCaptureEnabled(        boolean                mobileAndroidScreenCaptureEnabled )        { this.mobileAndroidScreenCaptureEnabled        = mobileAndroidScreenCaptureEnabled;                       }
    public void setMobileDisableOnRootedOrJailBrokenDevices( boolean                mobileDisableOnRootedOrJailBrokenDevices ) { this.mobileDisableOnRootedOrJailBrokenDevices = mobileDisableOnRootedOrJailBrokenDevices;                }
    public void setMobileOpenIn(                             MobileOpenInSetting    mobileOpenIn )                             { this.mobileOpenIn                             = ((null == mobileOpenIn) ? null : mobileOpenIn.ordinal());}
    public void setMobileOpenInWhiteLists(                   MobileOpenInWhiteLists mobileOpenInWhiteLists )                   { this.mobileOpenInWhiteLists                   = mobileOpenInWhiteLists;                                  }
}