package com.elasticpath.service.query;


/**
 * A service that performs a query based on a criteria.
 *
 * @param <T> the type of the object being queried
 */
public interface QueryService<T> {

	/**
	 * Query by criteria.
	 *
	 * @param <R> the generic type
	 * @param criteria the criteria
	 * @return the query result
	 */
	<R> QueryResult<R> query(QueryCriteria<T> criteria);
}
