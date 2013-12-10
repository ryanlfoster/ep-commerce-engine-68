package com.elasticpath.sfweb.service.impl;

import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import com.elasticpath.commons.util.InvalidatableCache;
import com.elasticpath.service.rules.impl.RuleEngineDataStrategy;

/**
 * Cached implementation of rule engine data strategy.
 */
public class EhCacheRuleEngineDataRetrievalStrategyImpl implements RuleEngineDataStrategy, InvalidatableCache {
	private static final Logger LOG = Logger.getLogger(EhCacheRuleEngineDataRetrievalStrategyImpl.class);

	private Ehcache cache;
	private RuleEngineDataStrategy strategyFallback;

	/**
	 * {@inheritDoc}
	 *
	 * Ehcache is used to cache selling contexts.
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findRuleIdSellingContextByScenario(final int scenario) {
		Element element = getCache().get(scenario);
		if (element != null && !element.isExpired()) {
			LOG.trace("cache hit for scenario " + scenario);
			return (List<Object[]>) element.getValue();
		}
		LOG.trace("cache miss for scenario " + scenario);
		final List<Object[]> result = getFallback().findRuleIdSellingContextByScenario(scenario);
		cacheSellingContext(scenario, result);
		return result;
	}


	/**
	 * @param scenario the scenario
	 * @param serializable object to cache
	 */
	protected void cacheSellingContext(final int scenario, final List <Object []> serializable) {
		getCache().put(new Element(scenario, serializable));
	}

	/**
	 * Invalidate the cache by removing all entries.
	 */
	public void invalidate() {
		getCache().removeAll();
	}

	/**
	 * Set the cache to use.
	 *
	 * @param cache the cache to set
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


	/**
	 * @param strategyFallback the strategyFallback to set
	 */
	public void setFallback(final RuleEngineDataStrategy strategyFallback) {
		this.strategyFallback = strategyFallback;
	}


	/**
	 *
	 * @return the strategyFallback
	 */
	public RuleEngineDataStrategy getFallback() {
		return strategyFallback;
	}

	@Override
	public void invalidate(final Object objectUid) {
		getCache().remove(objectUid);

	}
}
