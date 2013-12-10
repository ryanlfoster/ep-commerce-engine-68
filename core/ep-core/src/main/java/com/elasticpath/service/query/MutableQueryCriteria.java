package com.elasticpath.service.query;

import java.util.Date;

import com.elasticpath.persistence.api.LoadTuner;

/**
 * The Interface MutableQueryCriteria.
 *
 * @param <T> the generic type
 */
public interface MutableQueryCriteria<T> extends QueryCriteria<T> {
	
	/**
	 * Sets the query class.
	 *
	 * @param queryClass the new query class
	 */
	void setQueryClass(final Class<T> queryClass);
	
	/**
	 * Sets the result type.
	 *
	 * @param resultType the new result type
	 */
	void setResultType(final ResultType resultType);

	/**
	 * Sets the modified after.
	 *
	 * @param modifiedAfter the new modified after
	 */
	void setModifiedAfter(final Date modifiedAfter);

	/**
	 * Sets the start date.
	 *
	 * @param startDate the new start date
	 */
	void setStartDate(final Date startDate);

	/**
	 * Sets the end date.
	 *
	 * @param endDate the new end date
	 */
	void setEndDate(final Date endDate);

	/**
	 * Sets the load tuner.
	 *
	 * @param loadTuner the new load tuner
	 */
	void setLoadTuner(final LoadTuner loadTuner);

	/**
	 * Adds the relation.
	 *
	 * @param relation the relation
	 */
	void addRelation(final Relation<?> relation);
}
