package com.elasticpath.service.query;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents the criteria for a query.
 *
 * @param <T> the type of the object being queried
 */
public interface QueryCriteria<T> extends Serializable {
	
	/**
	 * Gets the query class.
	 *
	 * @return the query class
	 */
	Class<T> getQueryClass();
	
	/**
	 * Gets the result type.
	 *
	 * @return the result type
	 */
	ResultType getResultType();
	
	/**
	 * Gets the modified after date.
	 *
	 * @return the modified after
	 */
	Date getModifiedAfter();
	
	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	Date getStartDate();
	
	/**
	 * Gets the end date.
	 *
	 * @return the end date
	 */
	Date getEndDate();

	/**
	 * Gets the query relationships.
	 *
	 * @return the relations
	 */
	Set<Relation<?>> getRelations();
	
	/**
	 * Gets the load tuner.
	 *
	 * @return the load tuner
	 */
	LoadTuner getLoadTuner();
}
