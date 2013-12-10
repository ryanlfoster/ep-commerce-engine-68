package com.elasticpath.domain.misc.impl;


import java.util.Locale;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.misc.BrandComparator;
/**
 * The comparator defined for sorting brand list alphabetically.
 */
public class BrandComparatorImpl implements BrandComparator {

	private static final long serialVersionUID = 1L;

	private final Locale locale;

	/**
	 * The constructor with locale passed in.
	 * @param locale the locale object for comparator.
	 */
	public BrandComparatorImpl(final Locale locale) {
		super();
		this.locale = locale;
	}


	/**
	 * The compare method of the comparator.
	 * @param brand1 the Brand object to be compared.
	 * @param brand2 the Brand object to be compared.
	 * @return &lt; 0 if brand1 &lt; brand2, 0 if brand1 = brand2, &gt;0 if brand1 &gt; brand2.
	 */
	public int compare(final Brand brand1, final Brand brand2) {
		final String brand1DisplayName = String.valueOf(brand1.getDisplayName(locale, true));
		final String brand2DisplayName = String.valueOf(brand2.getDisplayName(locale, true));
		return brand1DisplayName.compareToIgnoreCase(brand2DisplayName);
	}

}