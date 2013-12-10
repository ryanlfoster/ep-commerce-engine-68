package com.elasticpath.ql.parser.valueresolver.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.ShieldUtility;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.ql.parser.valueresolver.EpQLValueResolver;
import com.elasticpath.service.search.index.Analyzer;

/**
 * Provides no transformation of EpQL field values. Should be used by default.
 */
public class LuceneValueResolver implements EpQLValueResolver {

	private static final Logger LOG = Logger.getLogger(LuceneValueResolver.class);

	private Analyzer analyzer;

	// TODO: this format is hard coded requirement for EP QL dates. E.g. StartDate='2008-01-02 10:22:22'
	// Think about introducing some cahrToDate() function to provide custom format for a user.
	/** YYYY-MM-DD hh:mm:ss . */
	public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

	@Override
	public List<String> resolve(final EpQLTerm epQLTerm, final EpQLFieldType fieldType, final EpQuery epQuery) throws ParseException {
		final List<String> resolvedValues = new ArrayList<String>();

		/** Resolve query text using some specific data type. */
		switch (fieldType) {
		case DATE:
			resolvedValues.add(resolveDateValue(epQLTerm));
			break;
		case STRING:
			resolvedValues.add(resolveStringValue(epQLTerm));
			break;
		case FLOAT:
			resolvedValues.add(resolveDecimalValue(epQLTerm));
			break;
		case BOOLEAN:
			resolvedValues.add(resolvedBooleanValue(epQLTerm));
			break;
		default:
			throw new ParseException("Unable to resolve EP QL field: "
					+ epQLTerm.getEpQLField().getFieldName() + " value: " + epQLTerm.getQueryText()
					+ " for data type: " + fieldType);
		}

		return resolvedValues;
	}

	/**
	 * Resolved date value.
	 *
	 * @param epQLTerm containing value to be resolved
	 * @return resolved value.
	 * @throws ParseException ParseException in case of any error during value resolution.
	 */
	String resolveDateValue(final EpQLTerm epQLTerm) throws ParseException {
		final Date date;
		try {
			synchronized (DEFAULT_DATE_FORMAT) {
				date = DEFAULT_DATE_FORMAT.parse(extractValue(epQLTerm));
			}
		} catch (java.text.ParseException e) {
			LOG.error("Unable to parse date", e);
			throw new ParseException("Unable to parse date for EP QL field: " // NOPMD
					+ epQLTerm.getEpQLField().getFieldName());
		}
		return analyzer.analyze(date);
	}

	/**
	 * Resolved date value.
	 * 
	 * @param epQLTerm containing value to be resolved
	 * @return resolved value.
	 * @throws ParseException ParseException in case of any error during value resolution.
	 */
	String resolveStringValue(final EpQLTerm epQLTerm) throws ParseException {
		/** EP QL parser need to escape some symbols. Convert escaped symbols. */
		/** Replace \\ to \ and \' to ' */
		String userQueryText = extractValue(epQLTerm).replaceAll("\\\\'", "'").replaceAll("\\\\\\\\", "\\\\");
		return ShieldUtility.shieldString(analyzer.analyze(userQueryText));
	}

	/**
	 * Resolves decimal value.
	 * 
	 * @param epQLTerm containing value to be resolved
	 * @return resolved value.
	 * @throws ParseException in case of any error during value resolution.
	 */
	String resolveDecimalValue(final EpQLTerm epQLTerm) throws ParseException {
		final BigDecimal value;
		try {
			value = new BigDecimal(epQLTerm.getQueryText());
		} catch (NumberFormatException e) {
			LOG.error("Unable to parse numeric", e);
			throw new ParseException("Unable to parse decimal value for EP QL field: "	// NOPMD
					+ epQLTerm.getEpQLField().getFieldName());
		}
		return analyzer.analyze(value);
	}

	/**
	 * Resolves product state value and provides verification.
	 * 
	 * @param epQLTerm containing value to be resolved
	 * @return exactly the same value
	 * @throws ParseException if value is unavailable for product state field
	 */
	String resolvedBooleanValue(final EpQLTerm epQLTerm) throws ParseException {
		if (Boolean.TRUE.toString().equalsIgnoreCase(epQLTerm.getQueryText())
			|| Boolean.FALSE.toString().equalsIgnoreCase(epQLTerm.getQueryText())) {
			return epQLTerm.getQueryText();
		}
		throw new ParseException("Only true, TRUE, false or FALSE values are valid for field "
				+ epQLTerm.getEpQLField().getFieldName());
	}

	/**
	 * Returns the analyzer.
	 * 
	 * @return the analyzer
	 */
	protected Analyzer getAnalyzer() {
		return analyzer;
	}

	/**
	 * Sets the analyzer.
	 * 
	 * @param analyzer the analyzer
	 */
	public void setAnalyzer(final Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	/**
	 * Checks that epQLTerm contains string literal as query text and retrieves its value from edging single quotes.
	 * 
	 * @param epQLTerm EPQL term to extract value from
	 * @return string value extracted from enclosing quotes
	 * @throws ParseException is epQLTerm contains query text of wong type (not string literal)
	 */
	String extractValue(final EpQLTerm epQLTerm) throws ParseException {
		if (!epQLTerm.getQueryText().endsWith("'") || !epQLTerm.getQueryText().startsWith("'")) {
			throw new ParseException("Value must be enclosed with single quotes for field "
					+ epQLTerm.getEpQLField().getFieldName());
		}
		return epQLTerm.getQueryText().substring(1, epQLTerm.getQueryText().length() - 1);
	}
}
