package com.elasticpath.service.search.solr;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.elasticpath.commons.util.impl.DateUtils;
import com.elasticpath.commons.util.impl.StringEscapeUtilityImpl;
import com.elasticpath.service.search.index.Analyzer;

/**
 * Provides a bunch of analyzing methods.
 */
public class AnalyzerImpl implements Analyzer {
	
	/** The string used in place of null values. */
	private static final String NULL = "";
	
	private static final DateFormat DATE_FORMATTER = 
		new SimpleDateFormat(DateUtils.DATE_TIME_FORMAT_STRING_US_INTERNAL, Locale.US);
	
	/** Synchronize on this object rather than the entire class. */
	private static final Object SYNC_ROOT = new Object();

	/**
	 * Static block to set up required properties for date formatter.
	 */
	static {
		DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	/**
	 * Analyzes the given date.
	 * <p>
	 * Required date format for SOLR dates, which is the Complete ISO 8601 Date syntax in UTC (aka zulu time), 
	 * for example 1976-03-06T23:59:59.999Z. Store single instance rather than create a new one all
	 * the time.
	 * 
	 * @param date the date to analyze
	 * @return the analyzed text
	 */
	public String analyze(final Date date) {
		if (date == null) {
			return NULL;
		}
		
		synchronized (SYNC_ROOT) {
			return DATE_FORMATTER.format(date);
		}
	}

	/**
	 * Analyzes the given int.
	 * 
	 * @param value the int value to analyze
	 * @return the analyzed text
	 */
	public String analyze(final int value) {
		return String.valueOf(value);
	}

	/**
	 * Analyzes the given long.
	 * 
	 * @param value the long value to analyze
	 * @return the analyzed text
	 */
	public String analyze(final long value) {
		return String.valueOf(value);
	}
	
	/**
	 * Analyzes the given string. Returns a trimmed instance of the string if not null, otherwise
	 * the empty string.
	 * 
	 * @param value the string value to analyze
	 * @return the analyzed text
	 */
	public String analyze(final String value) {
		return analyze(value, false);
	}

	/**
	 * Analyzes the given string. Returns a trimmed instance of the string if not null, otherwise
	 * the empty string.  Also escapes all quotes in the string.
	 * 
	 * @param value the string value to analyze
	 * @param isEscapeAllQuotes flag to force all quotes, matched and unmatched, to be escaped
	 * @return the analyzed text
	 */
	public String analyze(final String value, final boolean isEscapeAllQuotes) {
		if (value == null) {
			return NULL;
		}
		if (isEscapeAllQuotes) {
			return StringEscapeUtilityImpl.escapeUnescapedQuotes(value).trim();
		}
		return value.trim();
	}

	@Override
	public String analyze(final BigDecimal value) {
		if (value == null) {
			return NULL;
		}
		return String.valueOf(value);
	}
}
