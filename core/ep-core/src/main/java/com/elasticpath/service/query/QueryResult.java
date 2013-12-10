package com.elasticpath.service.query;

import java.io.Serializable;
import java.util.List;

/**
 * The results of a query.
 *
 * @param <R> the type of the result object
 */
public interface QueryResult<R> extends Serializable {

	/**
	 * Gets the results.
	 *
	 * @return the results
	 */
	List<R> getResults();
	
	/**
	 * Gets s single result.
	 *
	 * @return the single result
	 */
	R getSingleResult();
}
