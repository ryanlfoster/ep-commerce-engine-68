/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.query.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.query.MutableQueryCriteria;
import com.elasticpath.service.query.Relation;
import com.elasticpath.service.query.ResultType;

/**
 * Encapsulation of common elements created when constructing a query.
 * @param <T> the type class to query upon
 */
public class QueryCriteriaImpl<T> implements MutableQueryCriteria<T> {

	private static final long serialVersionUID = 1L;

	private Class<T> queryClass;
	private ResultType resultType;
	private Date modifiedAfter;
	private Date startDate;
	private Date endDate;
	private LoadTuner loadTuner;

	private final Set<Relation<?>> relations = new HashSet<Relation<?>>();

	@Override
	public Class<T> getQueryClass() {
		return this.queryClass;
	}

	@Override
	public ResultType getResultType() {
		return this.resultType;
	}

	@Override
	public Date getModifiedAfter() {
		return modifiedAfter;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public void setQueryClass(final Class<T> queryClass) {
		this.queryClass = queryClass;
	}

	@Override
	public void setResultType(final ResultType resultType) {
		this.resultType = resultType;
	}

	@Override
	public void setModifiedAfter(final Date modifiedAfter) {
		this.modifiedAfter = modifiedAfter;
	}

	@Override
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public void setLoadTuner(final LoadTuner loadTuner) {
		this.loadTuner = loadTuner;
	}

	@Override
	public LoadTuner getLoadTuner() {
		return loadTuner;
	}

	@Override
	public Set<Relation<?>> getRelations() {
		return relations;
	}

	@Override
	public void addRelation(final Relation<?> relation) {
		relations.add(relation);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(endDate)
				.append(loadTuner)
				.append(modifiedAfter)
				.append(queryClass.getName())
				.append(relations)
				.append(resultType)
				.append(startDate)
				.toHashCode();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		QueryCriteriaImpl<T> other = (QueryCriteriaImpl<T>) obj;
		return new EqualsBuilder().append(endDate, other.endDate)
				.append(loadTuner, other.loadTuner)
				.append(modifiedAfter, other.modifiedAfter)
				.append(queryClass.getName(), other.queryClass.getName())
				.append(relations, other.relations)
				.append(resultType, other.resultType)
				.append(startDate, other.startDate)
				.isEquals();
	}

}
