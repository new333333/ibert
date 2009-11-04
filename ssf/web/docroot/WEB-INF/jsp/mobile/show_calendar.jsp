<%
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
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
 
<%
Calendar calendarPrevDate = (java.util.Calendar) request.getAttribute("ssPrevDate");
Calendar calendarNextDate = (java.util.Calendar) request.getAttribute("ssNextDate");
Calendar calendarCurrDate = (java.util.Calendar) request.getAttribute("ssCurrDate");
Calendar calendarRangeEndDate = (java.util.Calendar) request.getAttribute("ssRangeEndDate");
User user = (User) request.getAttribute("ssUser");
TimeZone timeZone = user.getTimeZone();
Calendar calendarTodayDate = new GregorianCalendar(timeZone);

Integer gridSize = (java.lang.Integer) request.getAttribute("ssGridSize");
String strGridSize = "";
if (gridSize != null) {
	strGridSize = gridSize.toString();;
}

String strPrevDay = "" + calendarPrevDate.get(Calendar.DAY_OF_MONTH);
int intPrevMonth = calendarPrevDate.get(Calendar.MONTH) + 1;
String strPrevMonth = "" + intPrevMonth;
String strPrevYear = "" + calendarPrevDate.get(Calendar.YEAR);

String strNextDay = "" + calendarNextDate.get(Calendar.DAY_OF_MONTH);
int intNextMonth = calendarNextDate.get(Calendar.MONTH) + 1;
String strNextMonth = "" + intNextMonth;
String strNextYear = "" + calendarNextDate.get(Calendar.YEAR);

String strCurrDay = "" + calendarCurrDate.get(Calendar.DAY_OF_MONTH);
int intCurrMonth = calendarCurrDate.get(Calendar.MONTH) + 1;
String strCurrMonth = "" + intCurrMonth;
String strCurrYear = "" + calendarCurrDate.get(Calendar.YEAR);
int intCurrYear = calendarCurrDate.get(Calendar.YEAR);

String[] monthNames = new String[] {"calendar.january", "calendar.february", "calendar.march", "calendar.april", "calendar.may", "calendar.june", "calendar.july", "calendar.august", "calendar.september", "calendar.october", "calendar.november", "calendar.december"};
String strMonthName = NLT.get(monthNames[intCurrMonth - 1]);	

String strTodayDay = "" + calendarTodayDate.get(Calendar.DAY_OF_MONTH);
int intTodayDay = calendarTodayDate.get(Calendar.DAY_OF_MONTH);
int intTodayMonth = calendarTodayDate.get(Calendar.MONTH) + 1;
String strTodayMonth = "" + intTodayMonth;
String strTodayYear = "" + calendarTodayDate.get(Calendar.YEAR);

Date currDate = calendarCurrDate.getTime();
Date todayDate = calendarTodayDate.getTime();
Date nextDate = calendarNextDate.getTime();
Date rangeEndDate = calendarRangeEndDate.getTime();
%>

<c:set var="ssGridSize" value="<%= strGridSize %>" />
<c:set var="ssCurrDateFormat" value="<%= currDate %>" />
<c:set var="ssNextDateFormat" value="<%= nextDate %>" />
<c:set var="ssRangeEndDateFormat" value="<%= rangeEndDate %>" />
<c:set var="currMonthDate"><fmt:formatDate value="<%= currDate %>" pattern="MMM" /></c:set>
<c:set var="todayMonthDate"><fmt:formatDate value="<%= todayDate %>" pattern="MMM" /></c:set>

<%
 GregorianCalendar cal = new GregorianCalendar (intCurrYear, calendarCurrDate.get(Calendar.MONTH), 1); 
 int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
 int weekStartDay=cal.get(Calendar.DAY_OF_WEEK);
 
 cal = new GregorianCalendar (intCurrYear, calendarCurrDate.get(Calendar.MONTH), days); 
 int iTotalweeks=cal.get(Calendar.WEEK_OF_MONTH);
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td align="center" valign="middle" width="10%">
	<a class="ss_calDateDownButton" 
	  href="<ssf:url adapter="true" portletName="ss_forum" 
          folderId="${ssBinder.id}" action="__ajax_mobile" 
          operation="mobile_show_folder" actionUrl="false">
			<ssf:param name="day" value="<%= strPrevDay %>" />
			<ssf:param name="dayOfMonth" value="<%= strPrevDay %>" />
			<ssf:param name="month" value="<%= strPrevMonth %>" />
			<ssf:param name="year" value="<%= strPrevYear %>" />
		  </ssf:url>" >
		<img <ssf:alt tag="alt.viewCalPrev"/> title="<ssf:nlt tag="alt.viewCalPrev"/>"
		border="0" src="<html:imagesPath/>pics/sym_s_arrow_left.gif" />
	</a>
  </td>
  
  <td align="center" valign="middle" width="80%">	
	<span class="ss_mobile_calendar_header">
		<%= strMonthName %>, <fmt:formatDate value="${ssCurrDateFormat}" pattern="yyyy" />
	</span>
  </td>
	
  <td align="center" valign="middle" width="10%">	
	<a class="ss_calDateUpButton" 
	  href="<ssf:url adapter="true" portletName="ss_forum" 
          folderId="${ssBinder.id}" action="__ajax_mobile" 
          operation="mobile_show_folder" actionUrl="false">
			<ssf:param name="day" value="<%= strNextDay %>" />
			<ssf:param name="dayOfMonth" value="<%= strNextDay %>" />
			<ssf:param name="month" value="<%= strNextMonth %>" />
			<ssf:param name="year" value="<%= strNextYear %>" />
		  </ssf:url>">
		<img <ssf:alt tag="alt.viewCalNext"/> title="<ssf:nlt tag="alt.viewCalNext"/>"
		border="0" src="<html:imagesPath/>pics/sym_s_arrow_right.gif" />
	</a>
  </td>
  </tr>

</table>

<table width="100%" >
  <tr>
    <td>
     <table class="ss_mobile_calendar_month" border="1" cellspacing="0" cellpadding="0">
      <tbody>
        <tr>
          <th><ssf:nlt tag="calendar.day.abbrevs.su"/></th>
          <th><ssf:nlt tag="calendar.day.abbrevs.mo"/></th>
          <th><ssf:nlt tag="calendar.day.abbrevs.tu"/></th>
          <th><ssf:nlt tag="calendar.day.abbrevs.we"/></th>
          <th><ssf:nlt tag="calendar.day.abbrevs.th"/></th>
          <th><ssf:nlt tag="calendar.day.abbrevs.fr"/></th>
          <th><ssf:nlt tag="calendar.day.abbrevs.sa"/></th>
        </tr>
        <%
        int cnt = 1;
        for (int i = 1; i <= iTotalweeks; i++) {
		%>
        <tr>
          <% 
	        for(int j=1; j <= 7; j++)
	        {
		        if (cnt < weekStartDay || (cnt - weekStartDay + 1) > days) {
		         %>
                <td align="center" height="35">&nbsp;</td>
               <% 
		        }
		        else
		        {
		         %>
                <td align="center" height="35" id="day_<%= (cnt - weekStartDay + 1) %>"
                  <c:if test="${currMonthDate == todayMonthDate}">
                  <% if (intTodayDay == cnt - weekStartDay + 1) { %> class="ss_mobile_calendar_today" <% } %>
                  </c:if>
                >
                  <span><%= (cnt - weekStartDay + 1) %></span>
                </td>
               <% 
		        }
		        cnt++;
		      }
	        %>
        </tr>
        <% 
	    }
	    %>
      </tbody>
     </table>
    </td>
  </tr>
</table>

<table align="center">
  
  
  <tr>
  <td colspan="3" align="center">
	<a 
	  href="<ssf:url adapter="true" portletName="ss_forum" 
          folderId="${ssBinder.id}" action="__ajax_mobile" 
          operation="mobile_show_folder" actionUrl="false">
			<ssf:param name="day" value="<%= strTodayDay %>" />
			<ssf:param name="dayOfMonth" value="<%= strTodayDay %>" />
			<ssf:param name="month" value="<%= strTodayMonth %>" />
			<ssf:param name="year" value="<%= strTodayYear %>" />
		  </ssf:url>">
		<span class="ss_mobile_small"><ssf:nlt tag="mobile.calendar.showToday"/></span>
	</a>
  </td>
  </tr>
</table>

	<c:set var="lastCalDayDate" value=""/>
	<table class="ss_mobile_calendar" cellspacing="0" cellpadding="0">
	<c:set var="entriesSeen" value="0"/>
	<c:forEach var="entry2" items="${ssFolderEntries}" >
	<jsp:useBean id="entry2" type="java.util.HashMap" />
	<%
		if (entry2.containsKey("_event0")) {
			String eventName = (String)entry2.get("_event0");
			java.util.Date startDate = null;
			if (entry2.containsKey(eventName + "#StartDate")) 
				startDate = (java.util.Date)entry2.get(eventName + "#StartDate");
			java.util.Date endDate = null;
			if (entry2.containsKey(eventName + "#EndDate")) 
				endDate = (java.util.Date)entry2.get(eventName + "#EndDate");
			if (startDate == null) startDate = endDate;
			if (startDate != null) {
	%>
	<c:set var="calMonthDate"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
      		value="<%= startDate %>" pattern="MMM" /></c:set>
<c:if test="${currMonthDate == calMonthDate}">
    <c:set var="entriesSeen" value="${entriesSeen + 1}"/>
	<c:set var="calDayDate"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
      		value="<%= startDate %>" type="date" dateStyle="short" /></c:set>
    <c:if test="${calDayDate != lastCalDayDate}">
    <tr><td colspan="2" style="padding:5px 0px;"><img src="<html:imagesPath/>pics/1pix.gif"</td></tr>
    <tr>
      <th><fmt:formatDate value="<%= startDate %>" pattern="EEE" /></th>
      <th><fmt:formatDate value="<%= startDate %>" pattern="d MMM" /></th>
    </tr>
    </c:if>
    <c:set var="lastCalDayDate" value="${calDayDate}"/>
	 <tr>
	   <td class="ss_mobile_calendar_time">
		  <fmt:formatDate 
		    timeZone="${ssUser.timeZone.ID}"
      		value="<%= startDate %>" type="time" timeStyle="short" 
      	  />
	  	</td>
	  	<td>
		  <a href="<ssf:url adapter="true" portletName="ss_forum" 
			folderId="${ssBinder.id}"  entryId="${entry2._docId}"
			action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />">
	    	<c:if test="${empty entry2.title}">
	    		(<ssf:nlt tag="entry.noTitle"/>)
	    	</c:if>
			<c:out value="${entry2.title}" escapeXml="true"/>
		  </a>
	   </td>
	 </tr>
</c:if>
	 <%
	 		}
	 	}
	 %>
	</c:forEach>

	<c:if test="${entriesSeen == 0}">
	  <tr><td><ssf:nlt tag="folder.NoResults"/></td></tr>
	</c:if>
	</table>
