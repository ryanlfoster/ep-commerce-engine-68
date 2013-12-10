/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.commons.util.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * Static helper class for making date calculations.
 */
public final class DateUtils {

	/**
	 * Internal data/time formatter. This is the Complete ISO 8601 Date syntax, which is used for SOLR date queries.
	 */
	public static final String DATE_TIME_FORMAT_STRING_US_INTERNAL = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private static final String TIMESTAMP_PATTERN = "yyyy_MM_dd_HH_mm_ss.SSS";

	private static final int ELEVEN_O_CLOCK = 23;
	private static final int FIFTY_NINE = 59;
	private static final int NINE_NINE_NINE = 999;

	private DateUtils() {
		//  Cannot be instantiated.
	}

	/**
	 * Takes a date and returns the date object at the end of the day (23:59:59.999).
	 * Note: will not change the timezone of the date object.
	 *
	 * @param original the original date.
	 * @return the end of the day.
	 */
	public static Date getEndOfDay(final Date original) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(original);

		cal.set(Calendar.HOUR_OF_DAY, ELEVEN_O_CLOCK);
		cal.set(Calendar.MINUTE, FIFTY_NINE);
		cal.set(Calendar.SECOND, FIFTY_NINE);
		cal.set(Calendar.MILLISECOND, NINE_NINE_NINE);

		return cal.getTime();
	}

	/**
	 * Checks if the given month and year have already occurred as of the given date.  Useful for verifying credit card expiry dates.
	 * Note that because we're not specifying a locale or a time zone, things can get fuzzy around the edges...
	 *
	 * @param evaluationDate the date to check
	 * @param year as a part of date to check
	 * @param month as a part of date to check
	 *
	 * @return false if month and year are greater than or equal to current year and month
	 */
	public static boolean isExpired(final Date evaluationDate, final String year, final String month) {
		final int expirationMonth = Integer.valueOf(month);
		final int expirationYear = Integer.valueOf(year);
		final Calendar expirationDate = Calendar.getInstance();
		// Expiration date is set to be the first day of the month 00:00:00
		expirationDate.set(expirationYear, expirationMonth, 1, 0, 0, 0);

		return expirationDate.getTime().before(evaluationDate);
	}

	/**
	 * Returns a formatted time stamp string.
	 *
	 * @param timeStamp the time stamp to format
	 * @return the formatted string
	 */
	public static String toFormattedString(final Date timeStamp) {
		return ConverterUtils.date2String(timeStamp, TIMESTAMP_PATTERN, Locale.CANADA);
	}
}
