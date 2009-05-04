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
package org.kablink.teaming.ical.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.Summary;

public class ICalUtils {

	@SuppressWarnings("unchecked")
	public static String getSummary(Calendar calendar) {
		
		Iterator componentIt = calendar.getComponents().iterator();
		while (componentIt.hasNext()) {
			Component component = (Component)componentIt.next();
			Summary summary = (Summary)component.getProperty(Property.SUMMARY);
			if (summary != null) {
				return summary.getValue();
			}
		}
		return null;
	}
	
	public static String getMethod(Calendar calendar) {
		
		Property method = calendar.getProperty(Property.METHOD);
		if (method != null) {
			return method.getValue();
		}
		
		return null;
	}

	public static ByteArrayOutputStream toOutputStream(Calendar calendar) throws IOException, ValidationException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		CalendarOutputter calendarOutputter = getCalendarOutputter();
		calendarOutputter.output(calendar, out);
		return out;
	}

	public static CalendarOutputter getCalendarOutputter() {
		CalendarOutputter	co = new CalendarOutputter();
		
		// Bugzilla 488900 and 500218:
		//   Changed to output the iCal using a non-validating
		//   CalendarOutputter.  Changed to non-validating because
		//   ical4j throws a validation error if we use PUBLISH when
		//   there are attendees.  In order to get GroupWise
		//   interaction to work, that's what we're doing.
		co.setValidating(false);
		
		return co;
	}
}
