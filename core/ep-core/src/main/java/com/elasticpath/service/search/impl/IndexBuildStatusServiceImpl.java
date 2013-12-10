package com.elasticpath.service.search.impl;

import java.util.List;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.persistence.dao.IndexBuildStatusDao;
import com.elasticpath.service.search.IndexBuildStatusService;

/**
 * The default implementation of <code>IndexBuildStatusService</code>.
 */
public class IndexBuildStatusServiceImpl implements IndexBuildStatusService {
	
	private IndexBuildStatusDao buildStatusDao;
	
	@Override
	public List<IndexBuildStatus> getIndexBuildStatuses() {
		return buildStatusDao.list();
	}

	/**
	 * Gets the index build index dao.
	 * 
	 * @return the buildStatusDao the build index dao
	 */
	public IndexBuildStatusDao getBuildStatusDao() {
		return buildStatusDao;
	}

	/**
	 * Sets the index build index dao.
	 * 
	 * @param buildStatusDao the buildStatusDao to set
	 */
	public void setBuildStatusDao(final IndexBuildStatusDao buildStatusDao) {
		this.buildStatusDao = buildStatusDao;
	}
}
