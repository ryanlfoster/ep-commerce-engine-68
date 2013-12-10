package com.elasticpath.service.search;

import java.util.List;

import com.elasticpath.domain.search.IndexBuildStatus;

/**
 * This interface provides build index state related business services.
 */
public interface IndexBuildStatusService {
	
	/**
	 * Gets the list of all index build statuses.
	 * 
	 * @return the list of index build statuses
	 */
	List<IndexBuildStatus> getIndexBuildStatuses();
}
