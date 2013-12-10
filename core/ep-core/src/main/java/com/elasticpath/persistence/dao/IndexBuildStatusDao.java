package com.elasticpath.persistence.dao;

import java.util.List;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.service.search.IndexType;

/**
 * Interface for IndexBuildStatus DAO operations.
 */
public interface IndexBuildStatusDao {
	/**
	 * Saves an IndexBuildStatus object.
	 * 
	 * @param indexBuildStatus the IndexBuildStatus to save
	 * @return the persisted instance of IndexBuildStatus
	 */
	IndexBuildStatus saveOrUpdate(IndexBuildStatus indexBuildStatus);

	/**
	 * Returns an IndexBuildStatus by index name.
	 *
	 * @param indexType the IndexType
	 * @return the persisted instance of IndexBuildStatus
	 */
	IndexBuildStatus get(IndexType indexType);

	/**
	 * Returns a list of IndexBuildStatus objects.
	 * 
	 * @return List of IndexBuildStatus 
	 */
	List<IndexBuildStatus> list();
}
