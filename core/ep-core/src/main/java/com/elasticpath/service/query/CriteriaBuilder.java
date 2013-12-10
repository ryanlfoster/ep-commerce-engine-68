package com.elasticpath.service.query;

import java.util.Date;

import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.query.impl.QueryCriteriaImpl;

/**
 * Build a query criteria.
 *
 * @param <T> the type of the object being queried
 */
public class CriteriaBuilder<T> {

	private final MutableQueryCriteria<T> queryCriteria;
	
	/**
	 * Criteria for.
	 *
	 * @param <T> the generic type
	 * @param queryClass the query class
	 * @return the criteria builder
	 */
	public static <T> CriteriaBuilder<T> criteriaFor(final Class<T> queryClass) {
		return new CriteriaBuilder<T>(queryClass);
	}
	
	/**
	 * Instantiates a new criteria builder impl.
	 *
	 * @param queryClass the query class
	 */
	protected CriteriaBuilder(final Class<T> queryClass) {
		super();
		this.queryCriteria = new QueryCriteriaImpl<T>();
		this.queryCriteria.setQueryClass(queryClass);
	}
	
	/**
	 * Instantiates a new criteria builder impl.
	 *
	 * @param resultType the result type
	 */
	public CriteriaBuilder(final ResultType resultType) {
		super();
		this.queryCriteria = new QueryCriteriaImpl<T>();
		this.queryCriteria.setResultType(resultType);
	}

	/**
	 * Returning.
	 *
	 * @param resultType the result type
	 * @return the query criteria
	 */
	public QueryCriteria<T> returning(final ResultType resultType) {
		queryCriteria.setResultType(resultType);
		return queryCriteria;
	}

	/**
	 * Modified after.
	 *
	 * @param modifiedAfter the modified after
	 * @return the criteria builder
	 */
	public CriteriaBuilder<T> modifiedAfter(final Date modifiedAfter) {
		queryCriteria.setModifiedAfter(modifiedAfter);
		return this;
	}

	/**
	 * In date range.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @return the criteria builder
	 */
	public CriteriaBuilder<T> inDateRange(final Date startDate, final Date endDate) {
		queryCriteria.setStartDate(startDate);
		queryCriteria.setEndDate(endDate);
		return this;
	}

	/**
	 * Using load tuner.
	 *
	 * @param loadTuner the load tuner
	 * @return the criteria builder
	 */
	public CriteriaBuilder<T> usingLoadTuner(final LoadTuner loadTuner) {
		queryCriteria.setLoadTuner(loadTuner);
		return this;
	}

	/**
	 * With.
	 *
	 * @param relation the relation
	 * @return the criteria builder
	 */
	public CriteriaBuilder<T> with(final Relation<?> relation) {
		queryCriteria.addRelation(relation);
		return this;
	}

}
