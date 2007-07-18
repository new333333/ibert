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
/*
 * Created on Jul 18, 2005
 *
 */
package com.sitescape.team.web.util;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.joda.time.DateTime;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.util.cal.DayAndPosition;


/**
 * @author billmers
 *
 */
public class EventHelper {
    
    // default method assumes duration and recurrence patterns
    static public Event getEventFromMap (InputDataAccessor inputData, String id) {
        Boolean hasDur = new Boolean("true");
        Boolean hasRecur = new Boolean("true");
        Event e = getEventFromMap(inputData, id, hasDur, hasRecur);
        return e;
    }

    
    // basic method
    static public Event getEventFromMap (InputDataAccessor inputData, String id, 
    		Boolean hasDuration, Boolean hasRecurrence) {
    	User user = RequestContextHolder.getRequestContext().getUser();
    	
        // we make the id match what the event editor would do
    	String prefix = id + "_";
    	
        Event event = new Event();
       
        // duration present means there is a start and end id
        String startId = "dp_" + id;
        String endId = "dp2_" + id;

        Date start = inputData.getDateValue(startId);
        Date end = inputData.getDateValue(endId);
        // for now, if either date in the range is missing, we return null Event
        // (consider instead making a checked exception?)
//        if (start == null || (hasDuration && end == null)) {
        if (start == null) {
            return null;
        }
        GregorianCalendar startc = new GregorianCalendar();
        startc.setTime(start);
        event.setDtStart(startc);
        if (end != null) {
        	GregorianCalendar endc = new GregorianCalendar();
        	endc.setTime(end);
        	event.setDtEnd(endc);
        }
        
        if (!isAllDayEvent(inputData, id)) {
        	event.setTimeZone(user.getTimeZone());
        } else {
        	if (event.getDtStart() != null) {
        		DateTime startEvent = new DateTime(event.getDtStart());
        		event.setDtStart(startEvent.withMillisOfDay(0).toGregorianCalendar());
        	}
        	
        	if (event.getDtEnd() != null) {
        		DateTime endEvent = new DateTime(event.getDtEnd());
        		event.setDtEnd(endEvent.withMillisOfDay(DateHelper.MILIS_IN_THE_DAY).toGregorianCalendar());
        	}
        }

        if (hasRecurrence.booleanValue()) {
            String repeatUnit = inputData.getSingleValue(prefix+"repeatUnit");
            String intervalStr = inputData.getSingleValue(prefix+"everyN");
            // rangeSel is the count/ until/ forever radio button
            String rangeSel = inputData.getSingleValue(prefix+"rangeSel");

            if (repeatUnit.equals("none")) {
                event.setFrequency(Event.NO_RECURRENCE);
            } else {
	            if (repeatUnit.equals("day")) {
	                event.setFrequency(Event.DAILY);
	                event.setInterval(intervalStr);
	                
	                // it's not yet implemented in UI
	                parseOnDaysOfWeek(event, inputData, prefix);
	            }
	            if (repeatUnit.equals("week")) {
	                event.setFrequency(Event.WEEKLY);
	                event.setInterval(intervalStr);
	                
	                parseOnDaysOfWeek(event, inputData, prefix);
	            }
	            
	            if (repeatUnit.equals("month")) {
	                event.setFrequency(Event.MONTHLY);
	                event.setInterval(intervalStr);
	                
	                parseDaysOfWeekWithPositions(event, inputData, prefix);
	                parseDaysOfMonth(event, inputData, prefix);
	                parseMonths(event, inputData, prefix);
	                
	            } else if (repeatUnit.equals("year")) {
	                event.setFrequency(Event.YEARLY);
	                event.setInterval(intervalStr);
	                
	                parseDaysOfWeekWithPositions(event, inputData, prefix);
	                parseDaysOfMonth(event, inputData, prefix);
	                parseMonths(event, inputData, prefix);
	            }
	            
	            if (rangeSel.equals("count")) {
	                String repeatCount = inputData.getSingleValue(prefix+"repeatCount");
	                event.setCount(repeatCount);
	            } else if (rangeSel.equals("until")) {
	                String untilId = "endRange_" + id;
	                Date until = inputData.getDateValue(untilId);
	                GregorianCalendar untilCal = new GregorianCalendar();
	                untilCal.setTime(until);
	                event.setUntil(untilCal);
	            } else if (rangeSel.equals("forever")) {
	            	event.setCount(0);
	            }
            }
        } else {
            event.setFrequency(Event.NO_RECURRENCE);
        }
        
        return event;
    }


	private static boolean isAllDayEvent(InputDataAccessor inputData, String id) {
		return inputData.exists("allDayEvent_" + id);
	}


	private static void parseDaysOfWeekWithPositions(Event e, InputDataAccessor inputData, String prefix) {
        String onDayCard = inputData.getSingleValue(prefix+"onDayCard");
        String dow = inputData.getSingleValue(prefix+"dow");
        
        if (onDayCard != null && dow != null) {
        
            int dayNum = 0;
            if (onDayCard.equals("first")) {
                dayNum = 1;
            } else if (onDayCard.equals("second")) {
                dayNum = 2;
            } else if (onDayCard.equals("third")) {
                dayNum = 3;
            } else if (onDayCard.equals("fourth")) {
                dayNum = 4;
            } else if (onDayCard.equals("last")) {
                dayNum = 5;
            }
            int day = 0;
            if (dow.equals("Sunday")) {
                day = Calendar.SUNDAY;
            } else if (dow.equals("Monday")) {
                day = Calendar.MONDAY;
            } else if (dow.equals("Tuesday")) {
                day = Calendar.TUESDAY;
            } else if (dow.equals("Wednesday")) {
                day = Calendar.WEDNESDAY;
            } else if (dow.equals("Thursday")) {
                day = Calendar.THURSDAY;
            } else if (dow.equals("Friday")) {
                day = Calendar.FRIDAY;
            } else if (dow.equals("Saturday")) {
                day = Calendar.SATURDAY;
            }
            if (!onDayCard.equals("none")) {
                DayAndPosition dpa[] = new DayAndPosition[1];
                dpa[0] = new DayAndPosition();
                dpa[0].setDayOfWeek(day);
                dpa[0].setDayPosition(dayNum);
                e.setByDay(dpa);
            }
        
        }
	}


	private static void parseOnDaysOfWeek(Event e, InputDataAccessor inputData, String prefix) {
        // this array maps the form checkboxes to the day-and-position constants
        int daysints[] = { 
                Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, 
                Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY 
        };
        
        // first we count the number of checkboxes so that we can
        // make the array of the correct size (setByDay will try to 
        // clone the array, so any nulls inside will throw an exception)
        String days[] = new String[7];
        if (inputData.exists(prefix+"day0")) {
        	days[0] = inputData.getSingleValue(prefix+"day0");
        } else {
        	days[0] = "";
        }
        if (inputData.exists(prefix+"day1")) {
        	days[1] = inputData.getSingleValue(prefix+"day1");
        } else {
        	days[1] = "";
        }
        if (inputData.exists(prefix+"day2")) {
        	days[2] = inputData.getSingleValue(prefix+"day2");
        } else {
        	days[2] = "";
        }
        if (inputData.exists(prefix+"day3")) {
        	days[3] = inputData.getSingleValue(prefix+"day3");
        } else {
        	days[3] = "";
        }
        if (inputData.exists(prefix+"day4")) {
        	days[4] = inputData.getSingleValue(prefix+"day4");
        } else {
        	days[4] = "";
        }
        if (inputData.exists(prefix+"day5")) {
        	days[5] = inputData.getSingleValue(prefix+"day5");
        } else {
        	days[5] = "";
        }
        if (inputData.exists(prefix+"day6")) {
        	days[6] = inputData.getSingleValue(prefix+"day6");
        } else {
        	days[6] = "";
        }
        int arraysz = 0;
        for (int ct = 0; ct < 7; ct++) {
            if (days[ct].equals("on")) {
                arraysz++;
            }
        }
        if (arraysz > 0) {
            DayAndPosition dpa[] = new DayAndPosition[arraysz];
            for (int i = 0, j=0; i < 7; i++) {
                if (days[i].equals("on")) {
                    dpa[j] = new DayAndPosition();
                    dpa[j++].setDayOfWeek(daysints[i]);
                }
            }
            e.setByDay(dpa);
        }

	}


	private static void parseDaysOfMonth(Event e, InputDataAccessor inputData, String prefix) {
        String[] doms = inputData.getValues(prefix+"dom");
        if (doms != null) {
        	int[] domsArray = new int[doms.length];
        	for (int i = 0; i < doms.length; i++) {
        		domsArray[i] = Integer.parseInt(doms[i]);
        	}
        	e.setByMonthDay(domsArray);
        }
	}
	
	private static void parseMonths(Event e, InputDataAccessor inputData, String prefix) {
        String[] months = inputData.getValues(prefix+"month");
        if (months != null) {
        	int[] monthsArray = new int[months.length];
        	for (int i = 0; i < months.length; i++) {
        		monthsArray[i] = Integer.parseInt(months[i]);
        	}
        	e.setByMonth(monthsArray);
        }
	}
    
}

