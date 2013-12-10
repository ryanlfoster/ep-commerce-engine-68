package com.elasticpath.sfweb.service.impl;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import com.elasticpath.commons.util.InvalidatableCache;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.sellingcontext.SellingContextRetrievalStrategy;
import com.elasticpath.service.sellingcontext.SellingContextService;

/**
 * the cache retrieval strategy class for selling context.
 */
public class EhCacheSellingContextRetrievalStrategyImpl implements
		SellingContextRetrievalStrategy, InvalidatableCache {

	private static final Logger LOG = Logger.getLogger(EhCacheSellingContextRetrievalStrategyImpl.class);

	private Ehcache cache;
	private SellingContextService sellingContextService;

	@Override
	public void invalidate() {
		cache.removeAll();
	}

	/**
	 * Set the selling context service.
	 *
	 * @param sellingContextService the selling context service
	 */
	public void setSellingContextService(final SellingContextService sellingContextService) {
		this.sellingContextService = sellingContextService;
	}



	/**
	 * Set the cache to use.
	 *
	 * @param cache
	 *            the cache to set
	 */
	public void setCache(final Ehcache cache) {
		this.cache = cache;
	}

	/**
	 * Get the cache.
	 *
	 * @return the cache
	 */
	public Ehcache getCache() {
		return cache;
	}

	@Override
	public SellingContext getByGuid(final String sellingContextGuid) {
		Element element = getCache().get(sellingContextGuid);
		if (element != null && !element.isExpired()) {
			LOG.debug("get objecct from cache... guid: " + sellingContextGuid);
			return (SellingContext) element.getValue();
		}

		LOG.debug("get objecct from database... guid: " + sellingContextGuid);
		SellingContext sellingContext = sellingContextService.getByGuid(sellingContextGuid);
		cache.put(new Element(sellingContextGuid, sellingContext));

		return sellingContext;
	}

	@Override
	public void invalidate(final Object objectUid) {
		getCache().remove(objectUid);

	}
}
