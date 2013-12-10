/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.pagination;


/**
 * A paginator is used to move among the available pages.
 * 
 * @param <T> the type class this paginator acts on
 */
public interface Paginator<T> {	
	
	/**
	 *
	 * @return the total items of type T
	 */
	long getTotalItems();

	/**
	 *
	 * @return the total available pages for type T
	 */
	int getTotalPages();

	/**
	 *
	 * @return the current page size
	 */
	int getPageSize();

	/**
	 *
	 * @return the first page
	 */
	Page<T> first();

	/**
	 *
	 * @return the last page
	 */
	Page<T> last();

	/**
	 *
	 * @return the next page
	 */
	Page<T> next();

	/**
	 *
	 * @return the previous page
	 */
	Page<T> previous();

	/**
	 *
	 * @return the current page
	 */
	Page<T> getCurrentPage();

	/**
	 *
	 * @param paginationConfig the pagination configuration
	 */
	void init(PaginationConfig paginationConfig);
	
	/**
	 * Get the sorting fields.
	 * 
	 * @return the sorting fields
	 */
	DirectedSortingField[] getSortingFields();

	/**
	 * Sets the sorting fields to allow different sorting.
	 * @param sortingFields the new fields
	 */
	void setSortingFields(DirectedSortingField... sortingFields);

	/**
	 * 
	 * @param paginatorLocator The paginator locator to use to retrieve from the database or
	 * other source.
	 */
	void setPaginatorLocator(final PaginatorLocator<T> paginatorLocator);

	/**
	 * 
	 * @return the paginator locator.
	 */
	PaginatorLocator<T> getPaginatorLocator();

	/**
	 * Goes to the paginatorLocator to refresh the records for the current page.
	 */
	void refreshCurrentPage();
	
	/**
	 * Get the page based on the number passed in.
	 * 
	 * @param pageNumber the page number
	 * @return the page that represents the number passed in
	 */
	Page<T> getPage(final int pageNumber);
	

}
