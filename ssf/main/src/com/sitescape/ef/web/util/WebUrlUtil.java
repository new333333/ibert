package com.sitescape.ef.web.util;

import javax.servlet.http.HttpServletRequest;

import com.sitescape.util.Http;
import com.sitescape.ef.util.Constants;
import com.sitescape.ef.util.SPropsUtil;

public class WebUrlUtil {
	
	/**
	 * Returns URL up to the SSF web app context root. The URL starts with a
	 * scheme and ends with a "/" character (e.g. http://abc.com:8080/ssf/).
	 * <p>
	 * If <code>req</code> is null, it uses system config information stored 
	 * in ssf.properties (which is static) to construct the URL as opposed to 
	 * the dynamic data available in the <code>HttpServletRequest</code>.   
	 * 
	 * @param req may be null
	 * @return
	 */
	public static StringBuffer getContextRootURL(HttpServletRequest req) {
		if(req == null)
			return getContextRootURL(null, false);
		else
			return getContextRootURL(req, req.isSecure());
	}

	/**
	 * Returns URL up to the SSF web app context path. The URL starts with a 
	 * scheme and ends with a "/" character (e.g. http://abc.com:8080/ssf/).
	 * <p>
	 * If <code>req</code> is null, it uses system config information stored 
	 * in ssf.properties (which is static) to construct the URL as opposed to 
	 * the dynamic data available in the <code>HttpServletRequest</code>.   
	 * 
	 * @param req may be null
	 * @param secure
	 * @return
	 */
	public static StringBuffer getContextRootURL(HttpServletRequest req, boolean secure) {

		// Because URLs generated by this class can only be served up by a
		// SSF server (in other words, it can not be served by WSRP consumer
		// because it is not a real portlet URL that the consumer understands), 
		// we need to encode the actual server name in the URL. 
		
		StringBuffer sb = new StringBuffer();
		
		// Scheme
		if(secure)
			sb.append(Http.HTTPS_WITH_SLASH);
		else
			sb.append(Http.HTTP_WITH_SLASH);
			
		// Server host
		String host = null;
		if(req == null) {
			host = SPropsUtil.getString(SPropsUtil.SSF_HOST);
		}
		else {
			host = req.getServerName();
		}
		sb.append(host);
		
		// Port number
		int port;
		if(req == null) {
			if(secure)
				port = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
			else
				port = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
		}
		else {
			port = req.getServerPort();
		}
		if(secure) {
			if(port != Http.HTTPS_PORT) {
				sb.append(Constants.COLON).append(port);
			}
		}
		else {
			if(port != Http.HTTP_PORT) {
				sb.append(Constants.COLON).append(port);
			}			
		}
		
		// Context path
		String ctx;
		if(req == null)
			ctx = SPropsUtil.getString(SPropsUtil.SSF_CTX, "/ssf");
		else
			ctx = req.getContextPath();
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	/**
	 * Returns URL up to the SSF portlet adapter root. The returned URL ends
	 * with a "/" character (e.g. http://abc.com:8080/ssf/a/).
	 * <p>
	 * If <code>req</code> is null, it uses system config information stored 
	 * in ssf.properties (which is static) to construct the URL as opposed to 
	 * the dynamic data available in the <code>HttpServletRequest</code>.   
	 *  
	 * @param req may be null
	 * @return
	 */
	public static String getAdapterRootURL(HttpServletRequest req) {
		if(req == null)
			return getAdapterRootURL(null, false);
		else
			return getAdapterRootURL(req, req.isSecure());
	}
	
	/**
	 * Returns URL up to the SSF portlet adapter root. The returned URL ends
	 * with a "/" character (e.g. http://abc.com:8080/ssf/a/).
	 * <p>
	 * If <code>req</code> is null, it uses system config information stored 
	 * in ssf.properties (which is static) to construct the URL as opposed to 
	 * the dynamic data available in the <code>HttpServletRequest</code>.   
	 * 
	 * @param req may be null
	 * @param secure
	 * @return
	 */
	public static String getAdapterRootURL(HttpServletRequest req, boolean secure) {
		return getContextRootURL(req, secure).append("a/").toString();
	}
	
	public static String getServletRootURL() {
		return getServletRootURL(null, false);
	}

	public static String getServletRootURL(HttpServletRequest req) {
		return getServletRootURL(req, req.isSecure());
	}
	
	/**
	 * Returns URL up to the SSF's regular servlet root. The returned URL ends
	 * with a "/" character (e.g. http://abc.com:8080/ssf/s/).
	 * 
	 * @param req
	 * @param secure
	 * @return
	 */
	public static String getServletRootURL(HttpServletRequest req, boolean secure) {
		return getContextRootURL(req, secure).append("s/").toString();
	}
}
