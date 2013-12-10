package com.elasticpath.service.query.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.service.query.QueryResult;

/**
 * The result of a query.
 *
 * @param <R> the type of the result
 */
public class QueryResultImpl<R> implements QueryResult<R> {
	
	private static final long serialVersionUID = 1L;

	private List<R> results;
	
	@Override
	public List<R> getResults() {
		return results;
	}

	@Override
	public R getSingleResult() {
		if (CollectionUtils.isEmpty(results)) {
			return null;
		}
		return results.get(0);
	}

	public void setResults(final List<R> results) {
		this.results = results;
	}

}
