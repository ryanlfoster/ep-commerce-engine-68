/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.service.impl;

import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.elasticpath.commons.util.InvalidatableCache;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.catalogview.ProductRetrieveStrategy;
import com.elasticpath.service.catalogview.impl.AbstractProductRetrieveStrategyImpl;

/**
 * A <code>ProductRetrieveStrategy</code> using EhCache.
 */
public class EhCacheProductRetrieveStrategyImpl extends AbstractProductRetrieveStrategyImpl implements ProductRetrieveStrategy, InvalidatableCache {

	private Ehcache cache;

	/**
	 * <p>
	 * Use a separate cache to handle product bundles.<br>
	 * This is necessary to alleviate symptoms due to solr indexes being updated, but cached product bundles are still retrieved.<br>
	 * This would cause items to appear in sorted price lists in the correct order based on a current price adjustment, but
	 * the product bundle itself would still have the cached price adjustment, which would cause the item to look out of order
	 * until the cache is invalidated by using the associated cache timeout.
	 * For example, seen on the category product listing page, using a low to high price sort.
	 * </p>
	 * TODO: This fix allows a tunable window to mitigate this situation from appearing with performance issues, until a better resolution
	 * is found.<br>
	 *
	 * Possible solutions include:
	 * <ul>
	 *    <li>separation of price adjustments from bundles (like product), or</li>
	 *    <li>better cache management by ejecting individual items using event notifications.</li>
	 * </ul>
	 */
	private Ehcache productBundleCache;

	private Ehcache productWithFetchGroupLoadTunerCache;

	/**
	 * Put the product into ehcache.
	 *
	 * @param product the product to cache
	 * @param loadTuner the load tuner used
	 */
	@Override
	protected void cacheProduct(final Product product, final LoadTuner loadTuner) {
		Element element = getCacheElement(product, loadTuner);
		if (product instanceof ProductBundle) {
			// We need to cache bundles separately with a shorter timeout.......
			// until we add a active notification system.
			getProductBundleCache().put(element);
		} else {
			getCacheByLoadTuner(loadTuner).put(element);
		}
	}

	/**
	 * Get a new cache element for the given product.
	 *
	 * @param product the product
	 * @param loadTuner the load tuner used
	 * @return a cache element.
	 */
	protected Element getCacheElement(final Product product, final LoadTuner loadTuner) {
		Pair<Product, LoadTuner> productWithTuner = new Pair<Product, LoadTuner>(product, loadTuner);
		return new Element(product.getUidPk(), productWithTuner);
	}

	/**
	 * Put the collection of products into ehcache.
	 *
	 * @param products the collection of products to cache
	 * @param loadTuner the load tuner used
	 */
	@Override
	protected void cacheProducts(final List<Product> products, final LoadTuner loadTuner) {
		for (Product product : products) {
			cacheProduct(product, loadTuner);
		}
	}

	/**
	 * Fetch the product from ehcache.
	 *
	 * @param productUid the uid of the product
	 * @param loadTuner the load tuner
	 * @return a {@link Pair} consisting of the {@link Product} and its associated {@link LoadTuner}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Pair<Product, LoadTuner> fetchFromCacheIfValid(final Long productUid, final LoadTuner loadTuner) {
		Element element = getCacheByLoadTuner(loadTuner).get(productUid);

		// also check the product bundle cache
		if (element == null) {
			element = getProductBundleCache().get(productUid);
		}

		if (element != null && !element.isExpired()) {
			return (Pair<Product, LoadTuner>) element.getValue();
		}
		return null;
	}

	@Override
	public void invalidate() {
		getCache().removeAll();
		getProductBundleCache().removeAll();
		getProductWithFetchGroupLoadTunerCache().removeAll();
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
	 * Get the product bundle cache to use.
	 *
	 * @return the productBundleCache
	 */
	public Ehcache getProductBundleCache() {
		return productBundleCache;
	}

	/**
	 * Set the product bundle cache to use.
	 *
	 * @param productBundleCache the productBundleCache to set
	 */
	public void setProductBundleCache(final Ehcache productBundleCache) {
		this.productBundleCache = productBundleCache;
	}

	/**
	 * Get the cache for products with fetch group load tuner.
	 *
	 * @return the cache.
	 */
	public Ehcache getProductWithFetchGroupLoadTunerCache() {
		return productWithFetchGroupLoadTunerCache;
	}

	/**
	 * Set the cache for products with fetch group load tuner.
	 *
	 * @param productWithFetchGroupLoadTunerCache the cache.
	 */
	public void setProductWithFetchGroupLoadTunerCache(final Ehcache productWithFetchGroupLoadTunerCache) {
		this.productWithFetchGroupLoadTunerCache = productWithFetchGroupLoadTunerCache;
	}

	/**
	 * To handle product bundles we need the added check on product to obtain the correct cache.<br>
	 * If the product is not a ProductBundle then default to using load tuner only to determine the cache.<br>
	 *
	 * @param product the product
	 * @param loadTuner the load tuner
	 * @return the cache
	 */
	protected Ehcache getCacheByCriteria(final Product product, final LoadTuner loadTuner) {
		if (product instanceof ProductBundle) {
			return productBundleCache;
		}
		return getCacheByLoadTuner(loadTuner);
	}

	/**
	 * Get correct cache by load tuner.
	 *
	 * @param loadTuner the load tuner
	 * @return the cache
	 */
	protected Ehcache getCacheByLoadTuner(final LoadTuner loadTuner) {
		if (loadTuner instanceof FetchGroupLoadTuner) {
			return productWithFetchGroupLoadTunerCache;
		}
		return cache;
	}

	@Override
	public void invalidate(final Object objectUid) {
			cache.remove(objectUid);
			productWithFetchGroupLoadTunerCache.remove(objectUid);
			productBundleCache.remove(objectUid);
	}


}
