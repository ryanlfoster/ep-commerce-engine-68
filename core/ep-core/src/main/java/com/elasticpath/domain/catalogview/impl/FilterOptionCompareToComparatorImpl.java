package com.elasticpath.domain.catalogview.impl;

import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.catalogview.FilterOptionCompareToComparator;

/**
 * Default implementation of of {@link FilterOptionCompareToComparator}.
 * 
 * @param <T> the type of filter
 */
public class FilterOptionCompareToComparatorImpl<T extends Filter<T>> implements FilterOptionCompareToComparator<T> {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Compares 2 {@link FilterOptions}. Uses the filter options filter's compareTo method.
	 * 
	 * @param option1 the first {@link FilterOption}
	 * @param option2 the second {@link FilterOption}
	 * @return a value greater than 0, equal to 0 or less than 0 for the lower price of
	 *         <code>option1</code> being greater than, equal to or less than the lower price of
	 *         <code>option2</code>
	 */
	public int compare(final FilterOption<T> option1, final FilterOption<T> option2) {
		return option1.getFilter().compareTo(option2.getFilter());
	}
}
