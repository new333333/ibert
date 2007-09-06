/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.web.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;

import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.domain.LoginInfo;
import com.sitescape.team.domain.User;
import com.sitescape.team.security.authentication.AuthenticationManagerUtil;
import com.sitescape.team.security.authentication.DigestDoesNotMatchException;
import com.sitescape.team.security.authentication.UserDoesNotExistException;

public class DigestBasedHardAuthenticationFilter implements Filter {

	protected final Log logger = LogFactory.getLog(getClass());

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain chain) throws IOException, ServletException {
		String zoneName = RequestUtils.getRequiredStringParameter((HttpServletRequest) request, "zn");
		Long userId = RequestUtils.getRequiredLongParameter((HttpServletRequest) request, "ui");
		String binderId = RequestUtils.getRequiredStringParameter((HttpServletRequest) request, "bi"); 		
		String privateDigest = RequestUtils.getRequiredStringParameter((HttpServletRequest) request, "pd"); 
		
		try {
			User user = AuthenticationManagerUtil.authenticate(zoneName, userId, binderId, privateDigest, LoginInfo.AUTHENTICATOR_ICAL);
			//don't set user, session is not currently active
			RequestContextUtil.setThreadContext(user.getZoneId(), user.getId());
			
			chain.doFilter(request, response); // Proceed
			
			RequestContextUtil.clearThreadContext();
		}
		catch(UserDoesNotExistException e) {
			logger.warn(e);
			throw new ServletException(e);
		}
		catch(DigestDoesNotMatchException e) {
			logger.warn(e);
			throw new ServletException(e);
		}
	}

	public void destroy() {
	}
}
