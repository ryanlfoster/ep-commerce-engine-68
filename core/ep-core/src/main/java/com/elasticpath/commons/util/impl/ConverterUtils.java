package com.elasticpath.commons.util.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.ObjectUtils;

import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.exception.EpBigDecimalBindException;
import com.elasticpath.commons.exception.EpBooleanBindException;
import com.elasticpath.commons.exception.EpDateBindException;
import com.elasticpath.commons.exception.EpIntBindException;

/**
 * Static utility class that converts primitives without throwing checked exceptions.  This code lives on because
 * client code relies on the exception types.  However, using these utilities for new code is deprecated.
 */
public final class ConverterUtils {
	private ConverterUtils() {
		//  Can't be instantiated.
	}

	/**
	 * Convert a <code>String</code> to a <code>BigDecimal</code> object.
	 *
	 * @param stringValue the string
	 * @return a <code>BigDecimal</code> object
	 * @throws com.elasticpath.commons.exception.EpBigDecimalBindException if the given string cannot be converted to a <code>BigDecimal</code> object
	 */
	@Deprecated
	public static BigDecimal string2BigDecimal(final String stringValue) throws EpBigDecimalBindException {
		try {
			return new BigDecimal(stringValue);
		} catch (NumberFormatException e) {
			throw new EpBigDecimalBindException(stringValue + " is not a valid BigDecmial.", e);
		}
	}

	/**
	 * Convert a <code>BigDecimal</code> object to a <code>String</code> object.
	 *
	 * @param bigDecimalValue the <code>BigDecimal</code> object
	 * @return a <code>String</code> object
	 * @deprecated use org.apache.commons.lang.ObjectUtils#toString(Object, String) instead
	 */
	@Deprecated
	public static String bigDecimal2String(final BigDecimal bigDecimalValue) {
		return ObjectUtils.toString(bigDecimalValue, GlobalConstants.NULL_VALUE);
	}

	/**
	 * Convert a <code>String</code> to a <code>boolean</code> value.
	 *
	 * @param stringValue the string
	 * @return a <code>boolean</code> value
	 * @throws EpBooleanBindException if the given string cannot be converted to a <code>boolean</code> value
	 */
	@Deprecated
	public static boolean string2Boolean(final String stringValue) throws EpBooleanBindException {
		if ("true".equalsIgnoreCase(stringValue)) {
			return true;
		}

		if ("false".equalsIgnoreCase(stringValue)) {
			return false;
		}

		if ("1".equalsIgnoreCase(stringValue)) {
			return true;
		}

		if ("0".equalsIgnoreCase(stringValue)) {
			return false;
		}

		if ("yes".equalsIgnoreCase(stringValue)) {
			return true;
		}

		if ("no".equalsIgnoreCase(stringValue)) {
			return false;
		}

		throw new EpBooleanBindException(stringValue + " is not a valid Boolean value.");
	}

	/**
	 * Returns the <code>Currency</code> instance for the given currency code. <br>
	 * Used in @Factory annotations in OpenJPA Domain classes to convert to a currency from a string.  This is needed because
	 * OpenJPA has class-loader bugs with @Factory that prevent it from calling Currency.getInstance() directly.
	 *
	 * @param currencyCode the code of the currency
	 * @return the <code>Currency</code> instance for the given currency code
	 */
	public static Currency currencyFromString(final String currencyCode) {
		return Currency.getInstance(currencyCode);
	}

	/**
	 * Convert a date string and return a <code>Date</code> object.
	 *
	 * @param dateString A date string to parse
	 * @param locale The locale to parse the date
	 * @param pattern - the pattern used to parse the date
	 * @throws EpDateBindException if the given string cannot be converted to a <code>Date</code> object
	 * @return Return a <code>Date</code> object if the data string is valid. <br>
	 *         Return null if the data string is invalid.
	 */
	public static Date string2Date(final String dateString, final String pattern, final Locale locale) throws EpDateBindException {
		try {
			return createDateFormat(pattern, locale).parse(dateString);
		} catch (Exception e) {
			throw new EpDateBindException(dateString + " is a bad date string.", e);
		}
	}

	/**
	 * Convert a date string and return a <code>Date</code> object.
	 *
	 * @param dateString A date string to parse
	 * @param dateFormat The localized date format parse the date with
	 * @throws EpDateBindException if the given string cannot be converted to a <code>Date</code> object
	 * @return Return a <code>Date</code> object if the data string is valid. <br>
	 *         Return null if the data string is invalid.
	 */
	public static Date string2Date(final String dateString, final LocalizedDateFormat dateFormat) throws EpDateBindException {
		return string2Date(dateString, dateFormat.getDateFormatPattern(), dateFormat.getLocale());
	}

	/**
	 * Convert a date to a string based on the given date format.
	 *
	 * @param date the date
	 * @param locale The locale to parse the date
	 * @param pattern - the pattern used to parse the date
	 * @return the converted date string
	 */
	public static String date2String(final Date date, final String pattern, final Locale locale) {
		if (date == null) {
			return GlobalConstants.NULL_VALUE;
		}
		return createDateFormat(pattern, locale).format(date);
	}

	/**
	 * Convert a date to a string based on the given localized date format.
	 *
	 * @param date the date
	 * @param dateFormat the localized date format used to parse the date
	 * @return the converted date string
	 */
	public static String date2String(final Date date, final LocalizedDateFormat dateFormat) {
		return date2String(date, dateFormat.getDateFormatPattern(), dateFormat.getLocale());
	}

	/**
	 * <p>Returns the default date format. Note that {@link java.text.SimpleDateFormat} is not thread safe.
	 * Please replace {@link java.text.SimpleDateFormat} with {@link FastDateFormat} when {@link FastDateFormat} has parse functionality.
	 * The usage of this DateFormat class should be thread safe, because for each request we are using a new object.
	 *
	 * @param pattern the pattern to use for format parsing
	 * @param locale the Locale to use for format parsing
	 * @return the default date format
	 */
	private static DateFormat createDateFormat(final String pattern, final Locale locale) {
		return new SimpleDateFormat(pattern, locale);
	}

	/**
	 * Convert a <code>String</code> to an <code>int</code> value.
	 *
	 * @param stringValue the string
	 * @return an <code>int</code> value
	 * @throws EpIntBindException if the given string cannot be converted to an <code>int</code> value
	 */
	@Deprecated
	public static int string2Int(final String stringValue) throws EpIntBindException {
		try {
			return Integer.parseInt(stringValue);
		} catch (NumberFormatException e) {
			throw new EpIntBindException(stringValue + " is not a valid int.", e);
		}
	}



}
