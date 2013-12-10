package com.elasticpath.sfweb.service.impl;

import java.util.Set;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.elasticpath.commons.util.InvalidatableCache;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.service.catalog.ProductAssociationRetrieveStrategy;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * The cacheable product association retrieve strategy implementation.
 */
public class EhCacheProductAssociationRetrieveStrategyImpl
	implements ProductAssociationRetrieveStrategy, InvalidatableCache {

	private Ehcache cache;

	private ProductAssociationService productAssociationService;

	/**
	 * Get the cache.
	 *
	 * @return the cache
	 */
	protected Ehcache getCache() {
		return cache;
	}

	/**
	 * Get product association service.
	 *
	 * @return the product association service
	 */
	protected ProductAssociationService getProductAssociationService() {
		return productAssociationService;
	}

	/**
	 * Set the cache.
	 *
	 * @param cache the cache
	 */
	public void setCache(final Ehcache cache) {
		this.cache = cache;
	}


	/**
	 * Set product association service.
	 *
	 * @param productAssociationService the product associaton service
	 */
	public void setProductAssociationService(
			final ProductAssociationService productAssociationService) {
		this.productAssociationService = productAssociationService;
	}


	/**
	 * {@inheritDoc}
	 *
	 * Ehcache is used to cache selling contexts.
	 */
	@SuppressWarnings("unchecked")
	public Set<ProductAssociation> getAssociations(final ProductAssociationSearchCriteria criteria) {
		Element element = getCache().get(criteria);
		if (element != null && !element.isExpired()) {
			return (Set<ProductAssociation>) element.getValue();
		}

		Set<ProductAssociation> productAssociations = getProductAssociationService().getAssociations(criteria);
		cacheProductAssociation(criteria, productAssociations);
		return productAssociations;
	}


	/**
	 * Cache product associations.
	 *
	 * @param criteria the criteria
	 * @param productAssociations the product associations found by the criteria
	 */
	protected void cacheProductAssociation(
			final ProductAssociationSearchCriteria criteria,
			final Set<ProductAssociation> productAssociations) {
		getCache().put(new Element(criteria, productAssociations));
	}

	@Override
	public void invalidate() {
		getCache().removeAll();
	}

	@Override
	public void invalidate(final Object objectUid) {
		getCache().remove(objectUid);

	}
}
