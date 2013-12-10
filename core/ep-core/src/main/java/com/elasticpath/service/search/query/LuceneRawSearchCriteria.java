package com.elasticpath.service.search.query;

import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;

/**
 * Raw search criteria which allows raw queries to be created.
 */
public class LuceneRawSearchCriteria extends AbstractSearchCriteriaImpl {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private String query;

	private IndexType indexType;

	/**
	 * Returns the index type this criteria deals with.
	 * 
	 * @return the index type this criteria deals with.
	 */
	public IndexType getIndexType() {
		return indexType;
	}

	/**
	 * Sets the index type this criteria deals with.
	 * 
	 * @param indexType the index type this criteria deals with.
	 */
	public void setIndexType(final IndexType indexType) {
		this.indexType = indexType;
	}

	@Override
	public void optimize() {
		if (!isStringValid(query)) {
			query = null;
		}
	}

	/**
	 * Gets the raw query string.
	 * 
	 * @return the raw query string
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Sets the raw query string.
	 * 
	 * @param query the raw query string
	 */
	public void setQuery(final String query) {
		this.query = query;
	}
}
