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
package org.kablink.teaming.web.util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.module.admin.SendMailErrorWrapper;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;

import static org.kablink.teaming.domain.ShareItem.RecipientType.group;
import static org.kablink.teaming.domain.ShareItem.RecipientType.user;

/**
 * Helper methods for email handling.
 * 
 * @author drfoster@novell.com
 */
public class EmailHelper {
	protected static Log m_logger = LogFactory.getLog(EmailHelper.class);
	
	/**
	 * Enumeration type used to indicate the type of notification a URL
	 * is being send for. 
	 */
	public enum UrlNotificationType {
		FORGOTTEN_PASSWORD,
		PASSWORD_RESET_REQUESTED,
	}
	
	/**
	 * Adds the valid email addresses from a collection to a
	 * Map<Locale, List<InternetAddress>> based on the current user's
	 * locale.
	 * 
	 * @param usedAs
	 * @param localeMap
	 * @param targetTZs
	 * @param emas
	 */
	public static void addEMAsToLocaleMap(String usedAs, Map<Locale, List<InternetAddress>> localeMap, List<TimeZone> targetTZs, Collection<String> emas) {
		// Scan the email addresses...
		User user = RequestContextHolder.getRequestContext().getUser();
		for (String ema:  emas) {
			// ...adding them to the current user's locale map.
			addEMAToUsersLocaleMap(usedAs, localeMap, targetTZs, user, ema);
		}
	}

	/**
	 * Adds the valid email addresses from a collection of principals
	 * to a Map<Locale, List<InternetAddress>> based on their locales.
	 * 
	 * @param usedAs
	 * @param localeMap
	 * @param targetTZs
	 * @param principalIds
	 */
	@SuppressWarnings("unchecked")
	public static void addPrincipalsToLocaleMap(String usedAs, Map<Locale, List<InternetAddress>> localeMap, List<TimeZone> targetTZs, Collection<Long> principalIds) {
		// Resolve the principal IDs and scan them.
		List<Principal> principals = ResolveIds.getPrincipals(principalIds);
		for (Principal principal:  principals) {
			if (null == principal) {
				continue;
			}

			// Is this principal a group?
			if (principal instanceof GroupPrincipal) {
				// Yes!  Should this group be included in the recipient
				// list?
				if (includeGroup(principal.getId())) {
					// Yes!  Explode it, resolve its members and scan
					// them.
					Collection<Long> userIds = MiscUtil.validateCL(getGroupMemberIds((GroupPrincipal) principal));
					List<Principal> users = ResolveIds.getPrincipals(userIds);
					for (Principal userP:  users) {
						// Is this principal a user?
						if ((null != userP) && (userP instanceof UserPrincipal)) {
							// Yes!  Add its email address to the
							// appropriate locale list.
							User user = ((User) userP);
							addEMAToUsersLocaleMap(usedAs, localeMap, targetTZs, user, user.getEmailAddress());
						}
					}
				}
			}
			
			// No, this principal isn't a group!  Is it a user?
			else if (principal instanceof UserPrincipal) {
				// Yes!  Add its email address to the appropriate
				// locale list.
				User user = ((User) principal);
				addEMAToUsersLocaleMap(usedAs, localeMap, targetTZs, user, user.getEmailAddress());
			}
		}
	}
	
	/**
	 * Adds the valid email addresses from a collection of teams
	 * to a Map<Locale, List<InternetAddress>> based on the team
	 * member's locales.
	 *
	 * @param bm
	 * @param usedAs
	 * @param localeMap
	 * @param targetTZs
	 * @param teamIds
	 */
	public static void addTeamsToLocaleMap(BinderModule bm, String usedAs, Map<Locale, List<InternetAddress>> localeMap, List<TimeZone> targetTZs, Collection<Long> teamIds) {
		// Scan the team IDs.
		for (Long teamId:  teamIds) {
			// Expand each team into a collection of principal IDs and
			// add them to the appropriate locale map.
			Collection<Long> userIds = MiscUtil.validateCL(getTeamMemberIds(bm, teamId, true));
			addPrincipalsToLocaleMap(usedAs, localeMap, targetTZs, userIds);
		}
	}
	
	/**
	 * Adds a valid email address to a
	 * Map<Locale, List<InternetAddress>> based on the given User's
	 * locale.
	 * 
	 * @param usedAs
	 * @param localeMap
	 * @param targetTZs
	 * @param user
	 * @param ema
	 */
	public static void addEMAToUsersLocaleMap(String usedAs, Map<Locale, List<InternetAddress>> localeMap, List<TimeZone> targetTZs, User user, String ema) {
		// If the user doesn't have a Locale defined...
		Locale locale = user.getLocale();
		if (null == locale) {
			// ...and the current user doesn't have one either...
			locale = RequestContextHolder.getRequestContext().getUser().getLocale();
			if (null == locale){
				// ...use the system's default.
				locale = Locale.getDefault();
			}
		}

		// Does this user have a timezone?
		TimeZone tz = user.getTimeZone();
		if (null != tz) {
			// Yes!  If it's not already in the list...
			if (!(isTZInList(targetTZs, tz))) {
				// ...add it.
				targetTZs.add(tz);
			}
		}

		// What locale list do we store the email addresses into?
		List<InternetAddress>	localeList = getLocaleList(localeMap, locale);
		boolean					newLocale  = (null == localeList);
		if (newLocale) {
			localeList = new ArrayList<InternetAddress>();
		}
		
		// If the email address is valid...
		InternetAddress ia = MiscUtil.validateEmailAddress(usedAs, ema);
		if (null != ia) {
			// ...and it's not already in the list...
			if (!(isIAInList(localeList, ia))) {
				// ...add it.
				localeList.add(ia);
			}
		}
		
		// If this is a new locale list that we put something into
		// it...
		if (newLocale && (!(localeList.isEmpty()))) {
			// ...store it in the map.
			localeMap.put(locale, localeList);
		}
	}

	/*
	 * Adds an error to a List regarding an Address[] of email
	 * addresses that could not be sent to.
	 */
	@SuppressWarnings("unchecked")
	public static void addMailFailures(List errors, Address[] errorAddrs, String resourceKey) {
		// Do we have anything to based the mail failures on?
		int errorCount = ((null == errorAddrs) ? 0 : errorAddrs.length);
		if ((null != errors) && (0 < errorCount) && MiscUtil.hasString(resourceKey)) {
			// Yes!  Add the failed addresses to a string...
			StringBuffer s = new StringBuffer("");
			for (Address errorAddr: errorAddrs) {
				if (0 < s.length()) {
					s.append(", ");
				}
				s.append(errorAddr.toString());
			}

			// ...and log the error.
			errors.add(
				NLT.get(
					resourceKey,
					new String[]{s.toString()},
					Locale.getDefault()));
		}
	}

	/**
	 * Returns true if sending to all users is enabled and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean canSendToAllUsers() {
		return SPropsUtil.getBoolean("mail.allowSendToAllUsers", false);
	}
	
	/*
	 * Returns a Set<Long> of the IDs of the members of a group.
	 */
	private static Set<Long> getGroupMemberIds(GroupPrincipal group) {
		List<Long> groupIds = new ArrayList<Long>();
		groupIds.add(group.getId());
		Set<Long> groupMemberIds = null;
		try {
			ProfileDao profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
			groupMemberIds = profileDao.explodeGroups(
				groupIds,
				group.getZoneId(),
				canSendToAllUsers());
		}
		catch (Exception ex) {/* Ignored. */}
		return validatePrincipalIds(groupMemberIds);
	}

	/*
	 * Scans a Map<Locale, List<InternetAddress>> for a specific
	 * locale.  If it is found, its List<InternetAddress> is returned,
	 * otherwise, null is returned.
	 */
	private static List<InternetAddress> getLocaleList(Map<Locale, List<InternetAddress>> localeMap, Locale locale2Find) {
		for (Locale locale:  localeMap.keySet()) {
			if (locale.equals(locale2Find)) {
				return localeMap.get(locale);
			}
		}
		return null;
	}

	/**
	 * Returns the string to use to inform about mail sending errors.
	 * 
	 * @param ex
	 * 
	 * @return
	 */
	public static String getMailExceptionMessage(Exception ex) {
		String reply = ex.getLocalizedMessage();
		if (!(MiscUtil.hasString(reply))) {
			reply = ex.getMessage();
		}
		return reply;
	}
	
    /**
     * Returns the localized string to display for a share expiration.
     * 
     * @param locale
     * @param share
     * @param tz
     * 
     * @return
     */
    public static String getShareExpiration(Locale locale, TimeZone tz, ShareItem share) {
    	// Does the share have an expiration date?
    	String reply;
		Date expiration = share.getEndDate();
		if (null == expiration) {
			// No!  It never expires.
			reply = NLT.get("share.expires.never", locale);
		}
		
		else {
			// Yes, there's an expiration date!  Is the an expires
			// after a number of days?
			int days = share.getDaysToExpire();
			if (0 < days) {
				// Yes!  Generate the appropriate string.
				reply = NLT.get("share.expires.after", new Object[]{days}, locale);
			}
			
			else {
				// No, there's no days!  It expires explicitly on the
				// specified date.
				DateFormat dateFmt = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
				dateFmt.setTimeZone(tz);
				String dateText = dateFmt.format(expiration);
				reply = NLT.get("share.expires.on", new Object[]{dateText}, locale);
			}
		}
		
		// If we get here, reply refers to the share expiration message
		// in the given locale.  Return it.
		return reply;
    }
    
	/**
	 * Returns a List<Locale> of the unique Locale's stored in a
	 * collection of Map<Locale, List<InternetAddress>>'s.
	 * 
	 * @param localeMaps
	 * 
	 * @return
	 */
	public static List<Locale> getTargetLocales(Map<Locale, List<InternetAddress>> ... localeMaps) {
		// Allocate a List<Locale> to return.
		List<Locale> reply = new ArrayList<Locale>();

		// Scan the locale maps.
		for (Map<Locale, List<InternetAddress>> localeMap:  localeMaps) {
			// Scan the Locale keys of this locale map.
			for (Locale locale:  localeMap.keySet()) {
				// If this Locale is not in the reply list...
				if (!(isLocaleInList(reply, locale))) {
					// ...add it.
					reply.add(locale);
				}
			}
		}
		
		// If we get here, reply refers to the unique Locale's referred
		// to by a collection of Map<Locale, List<InternetAddress>>'s.
		// Return it. 
		return reply;
	}
	
	/*
	 * Returns a Set<Long> of the member IDs of a team.
	 */
	private static Set<Long> getTeamMemberIds(BinderModule bm, Long binderId, boolean explodeGroups) {
		Set<Long> teamMemberIds = null;
		try {teamMemberIds = bm.getTeamMemberIds(binderId, explodeGroups);}
		catch (Exception ex) {/* Ignored. */}
		return validatePrincipalIds(teamMemberIds);
	}

	/*
	 * Returns true if a List<InternetAddress> contains a given
	 * InternetAddress and false otherwise.
	 */
	private static boolean isIAInList(List<InternetAddress> iaList, InternetAddress ia) {
		// Scan the List<InternetAddress>.
		for (InternetAddress iaScan:  iaList) {
			// Is this the InternetAddress in question?
			if (iaScan.equals(ia)) {
				// Yes!  Return true.
				return true;
			}
		}
		
		// If we get here, the List<InternetAddress> did not contain
		// the InternetAddress in question.  Return false.
		return false;
	}
	
	/*
	 * Returns true if a List<Locale> contains a given locale and false
	 * otherwise.
	 */
	private static boolean isLocaleInList(List<Locale> localeList, Locale locale) {
		// Scan the List<Locale>.
		for (Locale localeScan:  localeList) {
			// Is this the Locale in question?
			if (localeScan.equals(locale)) {
				// Yes!  Return true.
				return true;
			}
		}
		
		// If we get here, the List<Locale> did not contain the Locale
		// in question.  Return false.
		return false;
	}
	
	/*
	 * Returns true if a List<TimeZone> contains a given TimeZone and
	 * false otherwise.
	 */
	private static boolean isTZInList(List<TimeZone> iaList, TimeZone tz) {
		// Scan the List<TimeZone>.
		String tzId = tz.getID();
		for (TimeZone tzScan:  iaList) {
			// Is this the TimeZone in question?
			String tzScanId = tzScan.getID();
			if (tzScanId.equals(tzId)) {
				// Yes!  Return true.
				return true;
			}
		}
		
		// If we get here, the List<TimeZone> did not contain
		// the TimeZone in question.  Return false.
		return false;
	}
	
	/*
	 * Returns false if a group, based on its ID, should be excluded
	 * as the target of an email and true otherwise.
	 */
	private static boolean includeGroup(Long groupId) {
		// If we're not allowed to send email to all users...
		if (!(canSendToAllUsers())) {
			// ...and this is one of the all users groups...
			if (groupId.equals(Utils.getAllUsersGroupId()) || groupId.equals(Utils.getAllExtUsersGroupId())) {
				// ...exclude it.
				return false;
			}
		}
		
		// If we get here, the group should be included.  Return true.
		return true;
	}

	/*
	 * Scans the IDs in the given collection for any that are an all
	 * users group and returns a collection without them.
	 */
	@SuppressWarnings("unused")
	private static Collection<Long> removeAllUserGroups(Collection<Long> principalIds) {
		// Are there any IDs in the collection we were given?
		if (MiscUtil.hasItems(principalIds)) {
			// Yes!  Allocate a collection holding a copy of the list
			// we can remove the user groups from.
			List<Long>	nonAllUserIds = new ArrayList<Long>(principalIds);

			// If the collection contains all users...
			boolean removedAUGs = false;
			Long	augId       = Utils.getAllUsersGroupId();
			if (nonAllUserIds.contains(augId)) {
				// ...remove it.
				nonAllUserIds.remove(augId);
				removedAUGs = true;
			}
			
			// If the collection contains all external users...
			augId = Utils.getAllExtUsersGroupId();
			if (nonAllUserIds.contains(augId)) {
				// ...remove it.
				nonAllUserIds.remove(augId);
				removedAUGs = true;
			}

			// If we removed an any all user groups...
			if (removedAUGs) {
				// ...return the list without them.
				principalIds = nonAllUserIds;
			}
		}
		
		// If we get here, principalIds now refers to a collection
		// without any all user groups.  Return it.
		return principalIds;
	}
	
	/**
	 * Sends a confirmation mail message to an external user.
	 * 
	 * @param bs					- Access to modules.
	 * @param externalUserId		- ID of external user confirmation is being sent to.
	 * @param entityPermalinkUrl	- Permalink URL to the entity the confirmation is in regards to.
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static Map<String, Object> sendConfirmationToExternalUser(
		AllModulesInjected	bs,					//
		Long				externalUserId,		//
		String				entityPermalinkUrl)	//
			throws Exception
	{
		try {
			// Send the confirmation.
			Map<String, Object> reply = bs.getAdminModule().sendConfirmationMailToExternalUser(
				externalUserId,
				entityPermalinkUrl);
			
			// If we get here, reply contains a map of the results of
			// the email.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			m_logger.debug("EmailHelper.sendConfirmationToExternalUser( SOURCE EXCEPTION ):  ", ex);
			throw ex;
		}
	}
	
	/**
	 * Sends a share invitation mail message to an external user.
	 * 
	 * @param bs				- Access to modules.
	 * @param share				- Describes the share.
	 * @param sharedEntity		- Entity (folder or folder entry) being shared.
	 * @param externalUserId	- ID of external user invitation is being sent to.
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static Map<String, Object> sendShareInviteToExternalUser(
		AllModulesInjected	bs,					//
		ShareItem			share,				//
		DefinableEntity		sharedEntity,		//
		Long				externalUserIdId)	//
			throws Exception
	{
		try {
			// Send the invitation.
			Map<String, Object> reply = bs.getAdminModule().sendShareInviteMailToExternalUser(
				share,
				sharedEntity,
				externalUserIdId);
			
			// If we get here, reply contains a map of the results of
			// the email.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			m_logger.debug("EmailHelper.sendShareInviteToExternalUser( SOURCE EXCEPTION ):  ", ex);
			throw ex;
		}
	}
	
	/**
	 * Send a share notification mail message to a collection of users
	 * and/or explicit email addresses.
	 * 
	 * @param bs				- Access to modules.
	 * @param share				- Share item.
	 * @param sharedEntity		- Entity (folder or folder entry) being shared.
	 * @param principalIds		- toList,  users and groups
	 * @param teamIds			- toList,  teams.
	 * @param emailAddresses	- toList,  stand alone email address.
	 * @param ccIds				- ccList,  users and groups
	 * @param bccIds			- bccList, users and groups
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static Map<String, Object> sendShareNotification(
		AllModulesInjected	bs,				//
		ShareItem			share,			//
		DefinableEntity		sharedEntity,	//
		Collection<Long>	principalIds,	//
		Collection<Long>	teamIds,		//
		Collection<String>	emailAddresses,	//
		Collection<Long>	ccIds, 			//
		Collection<Long>	bccIds)			//
			throws Exception
	{
		try {
			// Are there any actual targets for the email notification?
			boolean hasTargets = (
				MiscUtil.hasItems(principalIds)   ||
				MiscUtil.hasItems(teamIds)        ||
				MiscUtil.hasItems(emailAddresses) ||
				MiscUtil.hasItems(ccIds)          ||
				MiscUtil.hasItems(bccIds));

			Map<String, Object> reply;
			if (hasTargets) {
				// Yes!  Send it.
				reply = bs.getAdminModule().sendMail(
					share,
					sharedEntity,
					principalIds,
					teamIds,
					emailAddresses,
					ccIds,
					bccIds);
			}
			else {
				// No, there aren't any targets!  Return an empty
				// reply.
				reply = new HashMap<String, Object>();
			}
			
			// If we get here, reply contains a map of the results of
			// the email notification.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			m_logger.debug("EmailHelper.sendShareNotification( SOURCE EXCEPTION ):  ", ex);
			throw ex;
		}
	}

    public static List<SendMailErrorWrapper> sendEmailToRecipient(
            AllModulesInjected ami,
            ShareItem shareItem,
            boolean isExternalUser,
            User currentUser )
    {
        List emailErrors;
        Set<Long> principalIds;
        Set<Long> teamIds;
        Set<Long> bccIds;
        String bccEmailAddress;
        EntityIdentifier entityId;
        DefinableEntity sharedEntity;

        if ( ami == null || currentUser == null || shareItem == null )
        {
            m_logger.error( "invalid parameter in sendEmailToRecipient()" );
            return null;
        }

        principalIds = new HashSet<Long>();
        teamIds = new HashSet<Long>();

        switch ( shareItem.getRecipientType() )
        {
            case group:
            case user:
                principalIds.add( shareItem.getRecipientId() );
                break;

            case team:
                teamIds.add( shareItem.getRecipientId() );
                break;

            default:
                m_logger.error( "unknow recipient type in sendEmailToRecipient()" );
                break;
        }

        entityId = shareItem.getSharedEntityIdentifier();
        if ( entityId.getEntityType().isBinder() )
            sharedEntity = ami.getBinderModule().getBinder( entityId.getEntityId() );
        else
            sharedEntity = ami.getFolderModule().getEntry( null, entityId.getEntityId() );

        // Does this user want to be BCC'd on all mail sent out?
        bccEmailAddress = currentUser.getBccEmailAddress();
        if ( MiscUtil.hasString( bccEmailAddress ) )
        {
            // Yes!
            // Add them to a BCC list.
            bccIds = new HashSet<Long>();
            bccIds.add( currentUser.getId() );
        }
        else
        {
            bccIds = null;
        }

        emailErrors = null;
        try
        {
            Map<String,Object> errorMap;

            if ( isExternalUser &&
                    getExternalUserAccountState( ami, shareItem.getRecipientId() ) == User.ExtProvState.initial &&
                    !getExternalUserLoggedInWithOpenIdAtLeastOnce( ami, shareItem.getRecipientId()))
            {
                errorMap = null;

                errorMap = EmailHelper.sendShareInviteToExternalUser(
                        ami,
                        shareItem,
                        sharedEntity,
                        shareItem.getRecipientId() );
            }
            else
            {
                errorMap = EmailHelper.sendShareNotification(
                        ami,
                        shareItem,
                        sharedEntity,
                        principalIds,
                        teamIds,
                        null,	// null -> No stand alone email addresses.
                        null,	// null -> No CC'ed users.
                        bccIds );
            }

            if ( errorMap != null )
            {
                emailErrors = ((List<SendMailErrorWrapper>) errorMap.get( ObjectKeys.SENDMAIL_ERRORS ));
            }
        }
        catch ( Exception ex )
        {
            m_logger.error( "GwtEmailHelper.sendShareNotification() threw an exception: " + ex.toString() );
        }
        return emailErrors;
    }

    /**
     * Return the state of the external user account.  Possible values are, initial, bound and verified.
     * This method will return null if the given user is not an external user.
     */
    private static User.ExtProvState getExternalUserAccountState(
            AllModulesInjected ami,
            Long userId )
    {
        try
        {
            ArrayList<Long> ids;
            SortedSet<Principal> principals;

            ids = new ArrayList<Long>();
            ids.add( userId );
            principals = ami.getProfileModule().getPrincipals( ids );
            if ( principals != null && principals.size() == 1 )
            {
                Principal principal;

                //
                principal = principals.first();
                if ( principal instanceof User )
                {
                    User user;

                    user = (User) principal;
                    if ( user.getIdentityInfo().isInternal() == false )
                        return user.getExtProvState();
                }
            }
        }
        catch ( AccessControlException acEx )
        {
            // Nothing to do
        }

        return null;
    }

    /**
     * Return true iff the user is an external user and has logged in using OpenID at least once.
     * It doesn't matter whether the user is self provisioned or not.
     */
    private static boolean getExternalUserLoggedInWithOpenIdAtLeastOnce(
            AllModulesInjected ami,
            Long userId )
    {
        try
        {
            ArrayList<Long> ids;
            SortedSet<Principal> principals;

            ids = new ArrayList<Long>();
            ids.add( userId );
            principals = ami.getProfileModule().getPrincipals( ids );
            if ( principals != null && principals.size() == 1 )
            {
                Principal principal;

                //
                principal = principals.first();
                if ( principal instanceof User )
                {
                    User user;

                    user = (User) principal;
                    if ( user.getIdentityInfo().isInternal() == false )
                        return user.getIdentityInfo().isFromOpenid();
                }
            }
        }
        catch ( AccessControlException acEx )
        {
            // Nothing to do
        }

        return false;
    }

    /**
	 * Sends a URL notification mail message to a collection of users
	 * and/or explicit email addresses.
	 * 
	 * @param bs					- Access to modules.
	 * @param url					- The URL embedded in the notification.
	 * @param urlNotificationType	- Type of notification to send.
	 * @param principalIds			- toList,  users and groups
	 * @param teamIds				- toList,  teams.
	 * @param emailAddresses		- toList,  stand alone email address.
	 * @param ccIds					- ccList,  users and groups
	 * @param bccIds				- bccList, users and groups
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static Map<String, Object> sendUrlNotification(
		AllModulesInjected	bs,						//
		String				url,					//
		UrlNotificationType	urlNotificationType,	//
		Collection<Long>	principalIds,			//
		Collection<Long>	teamIds,				//
		Collection<String>	emailAddresses,			//
		Collection<Long>	ccIds, 					//
		Collection<Long>	bccIds)					//
			throws Exception
	{
		try {
			// Are there any actual targets for the email notification?
			boolean hasTargets = (
				MiscUtil.hasItems(principalIds)   ||
				MiscUtil.hasItems(teamIds)        ||
				MiscUtil.hasItems(emailAddresses) ||
				MiscUtil.hasItems(ccIds)          ||
				MiscUtil.hasItems(bccIds));

			Map<String, Object> reply;
			if (hasTargets) {
				// Yes!  Send it.
				reply = bs.getAdminModule().sendUrlNotification(
					url,
					urlNotificationType,
					principalIds,
					teamIds,
					emailAddresses,
					ccIds,
					bccIds);
			}
			else {
				// No, there aren't any targets!  Return an empty
				// reply.
				reply = new HashMap<String, Object>();
			}
			
			// If we get here, reply contains a map of the results of
			// the email notification.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			m_logger.debug("EmailHelper.sendUrlNotification( SOURCE EXCEPTION ):  ", ex);
			throw ex;
		}
	}
	
	public static Map<String, Object> sendUrlNotification(
		AllModulesInjected	bs,						//
		String				url,					//
		UrlNotificationType	urlNotificationType,	//
		Long				principalId)			//
			throws Exception
	{
		// Always use the initial form of the method.
		Collection<Long> principalIds = new ArrayList<Long>();
		principalIds.add(principalId);
		return
			sendUrlNotification(
				bs,
				url,
				urlNotificationType,
				principalIds,
				null,	// null -> No teams.
				null,	// null -> No specific email addresses.
				null,	// null -> No CCs.
				null);	// null -> No BCCs.
	}
	
	/*
	 * Validates that the Long's in a Set<Long> are valid principal
	 * IDs.
	 */
	@SuppressWarnings("unchecked")
	private static Set<Long> validatePrincipalIds(Set<Long> principalIds) {
		Set<Long> reply = new HashSet<Long>();
		if (MiscUtil.hasItems(principalIds)) {
			List principals = null;
			try {principals = ResolveIds.getPrincipals(principalIds);}
			catch (Exception ex) {/* Ignored. */}
			if (MiscUtil.hasItems(principals)) {
				for (Object o:  principals) {
					Principal p = ((Principal) o);
					reply.add(p.getId());
				}
			}
		}
		return reply;
	}
}
