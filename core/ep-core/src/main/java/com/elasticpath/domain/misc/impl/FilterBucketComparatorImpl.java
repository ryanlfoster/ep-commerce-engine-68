/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.misc.impl;

import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.misc.FilterBucketComparator;

/**
 * This is a default implementation of <code>FilterBucketComparator</code>.
 */
public class FilterBucketComparatorImpl implements FilterBucketComparator {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or
	 * greater than the second.
	 * We want the sort by desc.
	 * @param filterOption1 the first object to be compared.
	 * @param filterOption2 the second object to be compared.
	 * @return a positive integer, zero, or a negative integer as the first argument is less than, equal to, or greater than the second.
	 * @throws ClassCastException if the arguments' types prevent them from being compared by this Comparator.
	 */
	public int compare(final FilterOption< ? > filterOption1, final FilterOption< ? > filterOption2) {

		checkFilterOption(filterOption1);
		checkFilterOption(filterOption2);
		
		// By desc
		return filterOption2.getHitsNumber() - filterOption1.getHitsNumber();
	}

	private void checkFilterOption(final FilterOption< ? > object) {
		if (object == null) {
			throw new ClassCastException("Null object.");
		}
	}
}
