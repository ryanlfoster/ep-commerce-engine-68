/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * Test <code>DateUtils</code>.
 */
public class DateUtilsTest {

	private static final String LAST_YEAR = "1998";
	private static final String YEAR = "1999";
	private static final String NEXT_YEAR = "2000";

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.DateUtils.getEndOfDay()'.
	 */
	@Test
	public void testGetEndOfDay() {
		Date currentDate = new Date(); // current date

		// add one day to current date
		final Date tomorrowSameTime = org.apache.commons.lang.time.DateUtils.addDays(currentDate, 1);
		// the tomorrow date will be set to 00h:00m:00s
		final Date tomorrowMidnight = new Date(tomorrowSameTime.getYear(), tomorrowSameTime.getMonth(), tomorrowSameTime.getDate());
		// by removing a second from midnight we get back to today's last millisecond
		final Date todayLastMilli = org.apache.commons.lang.time.DateUtils.addMilliseconds(tomorrowMidnight, -1);
		// compare the result with what is expected
		assertEquals(todayLastMilli, DateUtils.getEndOfDay(currentDate));
		
		// add one day to today's last millisecond time
		final Date tomorrowLastMilli = org.apache.commons.lang.time.DateUtils.addDays(todayLastMilli, 1);
		// compare with any tomorrow date (for example midnight
		assertEquals(tomorrowLastMilli, DateUtils.getEndOfDay(tomorrowMidnight));
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.UtilityImpl.getTimeStamp()'.
	 */
	@Test
	public void testToString() {
		final Calendar cal = Calendar.getInstance();
		// CHECKSTYLE:OFF
		cal.set(Calendar.YEAR, 2001);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 2);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 45);
		cal.set(Calendar.MILLISECOND, 123);
		// CHECKSTYLE:ON

		// This method is tricky to test right now.  So just call it make sure it
		// doesn't throw any exceptions.
		final String output = DateUtils.toFormattedString(cal.getTime());
		assertEquals("Something ugly and incomprehensible comes out", "2001_01_02_12_30_45.123", output);
	}

	/**
	 * Test for testIsExpired. This method should be using the last day of the month to
	 * calculate if a month has passed.
	 */
	@Test
	public void testIsExpired() {
		final Calendar cal = Calendar.getInstance();

		// CHECKSTYLE:OFF
		cal.set(Integer.parseInt(YEAR), Calendar.JULY, Integer.parseInt("01")); //Last day of the month
		final Date JULY_01_1999 = cal.getTime();
		cal.set(Integer.parseInt(YEAR), Calendar.JULY, Integer.parseInt("31")); //Last day of the month
		final Date JULY_31_1999 = cal.getTime();
		cal.set(Integer.parseInt(YEAR), Calendar.FEBRUARY, Integer.parseInt("1"));
		final Date FEB_1_1999 = cal.getTime();
		// CHECKSTYLE:ON

		assertTrue("July should have passed if date is set to " + JULY_01_1999,
				DateUtils.isExpired(JULY_01_1999, YEAR, String.valueOf(Calendar.JULY)));
		assertFalse("August is to come when date is " + JULY_31_1999,
				DateUtils.isExpired(JULY_31_1999, YEAR, String.valueOf(Calendar.AUGUST)));

		assertTrue("January has passed when date is " + FEB_1_1999,
				DateUtils.isExpired(FEB_1_1999, YEAR, String.valueOf(Calendar.JANUARY)));
		assertTrue("February has already been stepped in when the current date is " + FEB_1_1999,
				DateUtils.isExpired(FEB_1_1999, YEAR, String.valueOf(Calendar.FEBRUARY)));
		assertFalse("March should be in the future when date is " + FEB_1_1999,
				DateUtils.isExpired(FEB_1_1999, YEAR, String.valueOf(Calendar.MARCH)));
		assertTrue("Last March should be in the past when date is " + FEB_1_1999,
				DateUtils.isExpired(FEB_1_1999, LAST_YEAR, String.valueOf(Calendar.MARCH)));
		assertFalse("Next January has yet to arrive when date is " + FEB_1_1999,
				DateUtils.isExpired(FEB_1_1999, NEXT_YEAR, String.valueOf(Calendar.JANUARY)));
	}

}
