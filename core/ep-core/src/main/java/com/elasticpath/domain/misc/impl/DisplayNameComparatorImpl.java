package com.elasticpath.domain.misc.impl;

import java.util.Locale;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.catalog.ObjectWithLocaleDependantFields;
import com.elasticpath.domain.misc.DisplayNameComparator;

/**
 * This is a default implementation of <code>DisplayNameComparator</code>.
 */
public class DisplayNameComparatorImpl implements DisplayNameComparator {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private Locale locale;

	/**
	 * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or
	 * greater than the second.
	 *
	 * @param object1 the first object to be compared.
	 * @param object2 the second object to be compared.
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
	 * @throws ClassCastException if the arguments' types prevent them from being compared by this Comparator.
	 */
	public int compare(final ObjectWithLocaleDependantFields object1, final ObjectWithLocaleDependantFields object2) {
		if (this.locale == null) {
			throw new EpSystemException("DisplayNameComparator not initialized.");
		}

		validateObject(object1);
		validateObject(object2);

		final String displayName1 = object1.getDisplayName(this.locale);
		final String displayName2 = object2.getDisplayName(this.locale);
		return displayName1.compareTo(displayName2);
	}

	private void validateObject(final ObjectWithLocaleDependantFields object) {
		if (object == null) {
			throw new ClassCastException("Null object.");
		}
	}

	/**
	 * Intialize the comparator with the given locale.
	 *
	 * @param locale the locale
	 */
	public void initialize(final Locale locale) {
		this.locale = locale;
	}
}
