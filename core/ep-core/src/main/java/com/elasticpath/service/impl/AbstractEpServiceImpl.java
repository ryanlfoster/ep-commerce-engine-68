/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.impl;

import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.service.EpService;

/**
 * <code>EpServiceImpl</code> is implementation of the base interface for other services.
 */
public class AbstractEpServiceImpl implements EpService {

	private ElasticPath elasticPath;

	/**
	 * Inject the ElasticPath singleton.
	 * 
	 * @param elasticpath the ElasticPath singleton.
	 */
	public void setElasticPath(final ElasticPath elasticpath) {
		this.elasticPath = elasticpath;
	}

	/**
	 * Get the ElasticPath singleton.
	 * 
	 * @return elasticpath the ElasticPath singleton.
	 */
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	public ElasticPath getElasticPath() {
		if (this.elasticPath != null) {
			return this.elasticPath;
		}
		return ElasticPathImpl.getInstance();
	}
	
	/**
	 * Convenience method for getting a bean instance from elastic path.
	 * @param <T> the type of bean to return
	 * @param beanName the name of the bean to get and instance of.
	 * @return an instance of the requested bean.
	 */
	protected <T> T getBean(final String beanName) {
		return getElasticPath().<T>getBean(beanName);
	}
	
}
