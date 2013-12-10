package com.elasticpath.search.index.solr.service;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.IndexType;

/**
 * This interface provides methods for building/rebuilding search indexes. 
 */
public interface IndexBuildService {
	
	/**
	 * Builds the index of a given index type.
	 * 
	 * @param indexType the index type enum
	 * @throws EpServiceException in case of problems during building indexes
	 */
	void buildIndex(final IndexType indexType) throws EpServiceException;
	
}
