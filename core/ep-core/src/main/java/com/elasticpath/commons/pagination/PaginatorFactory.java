/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.pagination;


/**
 * Paginator factory responsible for finding and creating new paginators.
 */
public interface PaginatorFactory {

	/**
	 *
	 * @param <T> the class this paginator factory should be created for
	 * @param objectClass the object class to find a paginator factory for
	 * @param paginationConfig the pagination configuration
	 * @return a new instance of a {@link Paginator}
	 */
	<T> Paginator<T> createPaginator(Class<T> objectClass, PaginationConfig paginationConfig);
}
