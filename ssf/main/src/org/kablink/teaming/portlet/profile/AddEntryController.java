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
package org.kablink.teaming.portlet.profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.kablink.teaming.IllegalCharacterInNameException;
import org.kablink.teaming.InvalidExternalUserNameException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.PasswordMismatchException;
import org.kablink.teaming.TextVerificationException;
import org.kablink.teaming.UserExistsException;
import org.kablink.teaming.UserNameMissingException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.NoPrincipalByTheNameException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.portletadapter.portlet.HttpServletRequestReachable;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.ParamsWrappedActionRequest;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;


/**
 * @author Peter Hurley
 *
 */
public class AddEntryController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Long binderId;
		
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		
		try
		{
			binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		}
		catch (Exception ex)
		{
			// We can't just call getProfileModule().getProfileBinder() all the time because
			// getProfileBinder() requires the user to have read access to the personal
			// workspaces binder.  For self registration, the guest user only needs "create entries"
			// rights to the personal workspaces binder.
			ProfileBinder profilesBinder = getProfileModule().getProfileBinder();
			binderId = profilesBinder.getId();
		}
		
		String context = PortletRequestUtils.getStringParameter(request, WebKeys.URL_CONTEXT, "");				
		//See if the add entry form was submitted
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			boolean markUserAsExternal = false;
			
			//The form was submitted. Go process it
			//If no entryType is given, then the default definition will be used when adding the user account
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			MapInputData inputData = new MapInputData(formData);
        	String name = inputData.getSingleValue(WebKeys.USER_PROFILE_NAME);
        	if (name == null || name.equals("")) throw new UserNameMissingException();
        	if (!BinderHelper.isBinderNameLegal(name)) throw new IllegalCharacterInNameException("errorcode.illegalCharacterInName");
        
        	//check if the user already exists, if found throw ObjectExistsException,
        	//if not found catch exception, and go ahead and add the new user
        	try
        	{
        		User user = getProfileModule().getUserDeadOrAlive( name );
        		throw new UserExistsException();
        	} catch (NoPrincipalByTheNameException nue){
        		//if user not found continue, this is what we want
        	}
        	catch ( NoUserByTheNameException ex )
        	{
        		// A user with the given name was not found.  Ok to continue.
        	}
        	
        	String password = inputData.getSingleValue(WebKeys.USER_PROFILE_PASSWORD);
        	String password2 = inputData.getSingleValue(WebKeys.USER_PROFILE_PASSWORD2);
        	if (password == null || password.equals("")) 
        		throw new PasswordMismatchException("errorcode.password.cannotBeNull");
        	if (!password.equals(password2)) {
        		throw new PasswordMismatchException("errorcode.password.mismatch");
        	}
        	
			// Should we mark this user as an external user?
        	String value = inputData.getSingleValue( "externalUserCB" );
        	if ( value != null &&
        		 (value.equalsIgnoreCase( "true" ) || value.equalsIgnoreCase( "on" )) )
        	{
        		// Yes
        		// Is the user's name an email address?
    			if ( MiscUtil.isEmailAddressValid( name ) == false )
    			{
    				// No, tell the user that an external user's name must be an email address.
    				throw new InvalidExternalUserNameException();
    			}
        		
        		markUserAsExternal = true;
        	}

    		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
    		if (operation.equals(WebKeys.OPERATION_RELOAD_OPENER) ) {
    			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
    			response.setRenderParameter(WebKeys.URL_BINDER_ID, "");				
    		} else {
    			// Are we dealing with the Guest user?
    			if ( isGuestUser() )
    			{
    				String				kaptchaResponse;
    				String				kaptchaExpected;
    				ActionRequest		actionRequest;
    				HttpServletRequest	httpServletRequest;

    				// Yes
    				// The request parameter is actually a ParamsWrappedActionRequest object.  Get the real ActionRequest object.
    				if ( request instanceof ParamsWrappedActionRequest )
    				{
	    				actionRequest = ((ParamsWrappedActionRequest)request).getActionRequest();
	    				if ( actionRequest instanceof HttpServletRequestReachable )
	    				{
		    				httpServletRequest = ((HttpServletRequestReachable)actionRequest).getHttpServletRequest();
		    				// Get the text used to create the kaptcha image.  It is stored in the http session.
		    				kaptchaExpected = (String) httpServletRequest.getSession().getAttribute( com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY );
		    				
		    				// Get the text the user entered.
		    				kaptchaResponse = inputData.getSingleValue( WebKeys.TEXT_VERIFICATION_RESPONSE );
		    				if ( kaptchaExpected == null || kaptchaResponse == null || !kaptchaExpected.equalsIgnoreCase( kaptchaResponse  ) )
		    				{
		    					// The text entered by the user did not match the text used to create the kaptcha image.
		    	        		throw new TextVerificationException();
		    				}
	    				}
    				}
    			}

    			User newUser = addUser(request, response, entryType, inputData, fileMap, null);

    			// Are we running the new GWT UI and doing a self registration?
    			if ( GwtUIHelper.isGwtUIActive( request ) && isGuestUser() )
    			{
    				// Yes, set up the response to close the window.
    				response.setRenderParameter( WebKeys.ACTION, WebKeys.ACTION_CLOSE_WINDOW );
    			}
    			else
    			{
    				// No
    				if (context.equals("adminMenu")) {
    					// Should we mark this user as an external user?
    		        	if ( markUserAsExternal )
    		        	{
    		        		// Yes
    		        		// Set the "external user" property to true.
    		        		getProfileModule().setUserProperty(
    		        										newUser.getId(),
    		        										ObjectKeys.USER_PROPERTY_EXTERNAL_USER,
    		        										"true" );
    		        		
    		        		// Re-index the user so the "external user" info is found in the index
    		        		getProfileModule().indexEntry( newUser );
    		        	}
    					
    					setupShowSuccess(response, binderId, newUser.getId());
    				} else {
    					setupReloadBinder(response, binderId);
	        			//flag reload of folder listing
	    				response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");
    				}
    			}
    		}
		} else if (formData.containsKey("cancelBtn")) {
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());				
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);

		}
			
	}
	private void setupShowSuccess(ActionResponse response, Long binderId, Long userId) {
		//return to view profile
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_SUCCESS);
	}
	private void setupReloadBinder(ActionResponse response, Long folderId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_BINDER);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
	}
	private void setupReloadOpener(ActionResponse response, Long binderId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
	}
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		String context = PortletRequestUtils.getStringParameter(request, WebKeys.URL_CONTEXT, "");				
		Map model = new HashMap();
		//Adding an entry; get the specific definition
		try {
			Map folderEntryDefs = getProfileModule().getProfileBinderEntryDefsAsMap();
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			if (entryType.equals("")) {
				ProfileBinder profilesBinder = getProfileModule().getProfileBinder();
				List defaultEntryDefinitions = profilesBinder.getEntryDefinitions();
				if (!defaultEntryDefinitions.isEmpty()) {
					// Only one option
					Definition def = (Definition) defaultEntryDefinitions.get(0);
					entryType = def.getId();
				}
				model.put(WebKeys.FOLDER, profilesBinder);
			}
			model.put(WebKeys.ENTRY_TYPE, entryType);
			model.put(WebKeys.BINDER_ID, getProfileModule().getProfileBinderId());
			model.put(WebKeys.ENTRY_DEFINITION_MAP, folderEntryDefs);
			model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_FORM);
	
			// Are we dealing with the Guest user?
			if ( isGuestUser() )
			{
				// Yes, set the flag that will enable the text verification controls in the page.
				model.put( WebKeys.URL_DO_TEXT_VERIFICATION, "true" );
				model.put( WebKeys.CAN_MAKE_EXTERNAL_USER, "false" );
			}
			else
				model.put( WebKeys.CAN_MAKE_EXTERNAL_USER, "true" );
			
			//Make sure the requested definition is legal
			if (folderEntryDefs.containsKey(entryType)) {
				DefinitionHelper.getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@type='form']");
			} else {
				DefinitionHelper.getDefinition((Document) null, model, "//item[@name='profileEntryForm']");
			}
		} catch(AccessControlException e) {}
		
		if (context.equals("adminMenu")) {
			return new ModelAndView(WebKeys.VIEW_ADD_USER_ACCOUNT, model);
		} else {
			return new ModelAndView(WebKeys.VIEW_ADD_ENTRY, model);
		}
	}
	
	
	/**
	 * This method will determine if the user is the guest user.
	 */
	private boolean isGuestUser()
	{
		boolean	guestUser	= false;
		
		if ( RequestContextHolder.getRequestContext() != null )
		{
			User	user;
			
        	user = RequestContextHolder.getRequestContext().getUser();
    		if ( user != null )
    		{
				// Are we dealing with the Guest user?
    			if ( ObjectKeys.GUEST_USER_INTERNALID.equals( user.getInternalId() ) )
    				guestUser = true;
    		}
		}
		
		return guestUser;
	}// end isGuestUser()
	
	 protected User addUser(ActionRequest request, ActionResponse response,
			 String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
		throws AccessControlException, WriteFilesException, WriteEntryDataException {
		 return getProfileModule().addUser(definitionId, inputData, fileItems, options);
	 }

}


