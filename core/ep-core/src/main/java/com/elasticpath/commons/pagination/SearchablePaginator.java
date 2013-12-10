package com.elasticpath.commons.pagination;

import java.util.List;

/**
 * Extends the Paginator interface to allow search criteria to be set.
 * 
 * @param <T> The type of the model for the individual items
 */
public interface SearchablePaginator<T> extends Paginator<T> {
	/**
	 * 
	 * @param searchCriteria The search criteria to set which modifies the coupons returned.
	 */
	void setSearchCriteria(final List<SearchCriterion> searchCriteria);
	
	/**
	 * Finds elements with the specified criteria.
	 * 
	 * @param unpopulatedPage the page to be returned
	 * @return the elements found for the specified criteria. Must not return null.
	 */
	List<T> findItems(final Page<T> unpopulatedPage);
}
