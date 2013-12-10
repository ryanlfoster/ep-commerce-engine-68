/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.pagination;


import java.util.List;


/**
 * A page is a sublist of objects extracted from an ordered result set.
 * It adds the ability to move among different adjacent and non-adjacent pages of data.
 * 
 * @param <T> the type of objects to be held in this page
 */
public interface Page<T> {

	/**
	 *
	 * @return a collection of the items in this page
	 */
	List<T> getItems();
	
	/**
	 *
	 * @return the page starting index number, starting from 1
	 */
	int getPageStartIndex();
	
	/**
	 *
	 * @return the page ending index number
	 */
	int getPageEndIndex();
	
	/**
	 *
	 * @return the items number in this page
	 */
	int getPageSize();
	
	/**
	 *
	 * @return this page's number, starting from 1
	 */
	int getPageNumber();
	
	/**
	 *
	 * @return the number of all the items in all the pages
	 */
	long getTotalItems();

	/**
	 *
	 * @return the total number of pages available
	 */
	int getTotalPages();

	/**
	 *
	 * @return the field the data should be ordered by
	 */
	DirectedSortingField[] getOrderingFields();
}
