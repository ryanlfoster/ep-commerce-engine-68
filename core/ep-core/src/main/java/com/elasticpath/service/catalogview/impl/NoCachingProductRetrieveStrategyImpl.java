package com.elasticpath.service.catalogview.impl;

import java.util.List;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.catalogview.ProductRetrieveStrategy;

/**
 * Provides an implementation of <code>ProductRetrieveStrategy</code> with no caching ability.
 * <p>
 * This service will return different instance of a product for different customers. So it will
 * take more more memory than other implementations, but it makes possible for customer-specific
 * rules being applied to products.
 */
public class NoCachingProductRetrieveStrategyImpl extends AbstractProductRetrieveStrategyImpl implements ProductRetrieveStrategy {

	/**
	 * No caching, so this method does nothing.
	 * 
	 * @param product the product to not cache
	 * @param loadTuner the load tuner used
	 */
	@Override
	protected void cacheProduct(final Product product, final LoadTuner loadTuner) {
		// No cache - do nothing
	}

	/**
	 * No caching, so this method does nothing.
	 * @param products the collection of product uids to not cache
	 * @param loadTuner the load tuner used
	 */
	@Override
	protected void cacheProducts(final List<Product> products, final LoadTuner loadTuner) {
		// No cache - do nothing
	}

	/**
	 * No caching, so just return null which indicates the product is not found in a cache.
	 * 
	 * @param productUid the uid of the product
	 * @param loadTuner the load tuner
	 * @return null to indicate no cached product
	 */
	@Override
	protected Pair<Product, LoadTuner> fetchFromCacheIfValid(final Long productUid, final LoadTuner loadTuner) {
		return null;
	}
}
