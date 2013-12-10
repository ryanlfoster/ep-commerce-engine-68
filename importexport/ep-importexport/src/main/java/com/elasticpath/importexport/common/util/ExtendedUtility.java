/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.importexport.common.util;

import java.util.Locale;

import com.elasticpath.commons.util.impl.UtilityImpl;

/**
 * Extend the utility to be able to push the {@link DateFormat} parameters: locale and pattern,
 * to decouple the importexport from the System.locale.
 */
public class ExtendedUtility extends UtilityImpl {

	private static final long serialVersionUID = 5223650789392093295L;

	private Locale dateFormatterLocale;
	private String dateFormatterPattern;

	//private static final Logger LOG = Logger.getLogger(ExtendedUtility.class);

	/**
	 * Setter.
	 * @param dateFormatterLocale the dateFormatterLocale to set
	 */
	public void setDateFormatterLocale(final Locale dateFormatterLocale) {
		this.dateFormatterLocale = dateFormatterLocale;
	}

	/**
	 *  Setter.
	 * @param dateFormatterPattern the dateFormatterPattern to set
	 */
	public void setDateFormatterPattern(final String dateFormatterPattern) {
		this.dateFormatterPattern = dateFormatterPattern;
	}

	@Override
	protected String getDefaultDateFormatPattern() {
		return this.dateFormatterPattern;
	}

	@Override
	protected Locale getDefaultDateFormatLocale() {
		return this.dateFormatterLocale;
	}

}
