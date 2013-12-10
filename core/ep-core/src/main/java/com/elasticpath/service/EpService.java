/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service;

import com.elasticpath.domain.ElasticPath;

/**
 * <code>EpService</code> serves as the base interface for other services.
 */
public interface EpService {
	/**
	 * Inject the ElasticPath singleton.
	 * 
	 * @param elasticpath the ElasticPath singleton.
	 *           
	 */
	void setElasticPath(final ElasticPath elasticpath);

	/**
	 * Get the ElasticPath singleton.
	 * 
	 * @return elasticpath the ElasticPath singleton.
	 */
	ElasticPath getElasticPath();
}
