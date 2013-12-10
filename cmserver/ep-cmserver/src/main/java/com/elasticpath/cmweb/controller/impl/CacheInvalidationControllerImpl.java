/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.util.CacheInvalidationStrategy;

/**
 * Simple implementation of a cache invalidation mechanism that runs a cache invalidation strategy.
 * This has no control mechanism or authentication around it and should not be used in production without such mechanisms.
 */
public class CacheInvalidationControllerImpl extends SimplePageControllerImpl {

	private CacheInvalidationStrategy cacheInvalidationStrategy;

	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		cacheInvalidationStrategy.invalidateCaches();
		return super.handleRequestInternal(request, response);
	}

	/**
	 *
	 * @return the cache invalidation strategy
	 */
	protected CacheInvalidationStrategy getCacheInvalidationStrategy() {
		return cacheInvalidationStrategy;
	}

	/**
	 *
	 * @param strategy the invalidation strategy to set
	 */
	public void setCacheInvalidationStrategy(final CacheInvalidationStrategy strategy) {
		this.cacheInvalidationStrategy = strategy;
	}

}