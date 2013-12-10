package com.elasticpath.commons.pagination;

import java.util.List;

/**
 * Interface for finding the model objects to paginate.
 * 
 * @param <T> the model class this paginator works with
 */
public interface PaginatorLocator<T> {

	/**
	 * Finds elements with the specified criteria.
	 * 
	 * @param unpopulatedPage the page to be returned
	 * @param objectId TODO
	 * @return the elements found for the specified criteria. Must not return null.
	 */
	List<T> findItems(final Page<T> unpopulatedPage, String objectId);
	
	/**
	 *
	 * @param objectId TODO
	 * @return the total items of type T
	 */
	long getTotalItems(String objectId);
}
