package com.elasticpath.service.catalogview.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalogview.ProductRetrieveStrategy;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.relations.ProductRelation;

/**
 * An abstract {@link ProductRetrieveStrategy}. This will make calls to abstract methods for
 * fetching products from a cache and adding to a cache as appropriate. 
 */
public abstract class AbstractProductRetrieveStrategyImpl extends AbstractEpServiceImpl implements ProductRetrieveStrategy {

	private static final Logger LOG = Logger.getLogger(AbstractProductRetrieveStrategyImpl.class);

	private ProductService productService;

	private QueryService<Product> productQueryService;
	
	/**
	 * Retrieve a product. This will attempt to retrieve a product from a cache if possible, otherwise
	 * it will use the product service to get the product and then add it to the cache if there is one.
	 * 
	 * @param productUid the uid of the product to retrieve
	 * @param loadTuner the product load tuner to use
	 * @return the product
	 */
	@Override
	public Product retrieveProduct(final long productUid, final LoadTuner loadTuner) {
		Pair<Product, LoadTuner> productWithTuner = fetchFromCacheIfValid(productUid, loadTuner);
		Product product = null;
		if (productWithTuner != null && productWithTuner.getSecond().contains(loadTuner)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Product cache hit: " + productUid + "/" + loadTuner);
			}
			product = productWithTuner.getFirst();
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Product cache MISS: " + productUid + "/" + loadTuner);
			}
			LoadTuner tunerToUse = loadTuner;
			if (productWithTuner != null && productWithTuner.getSecond() != null) {
				tunerToUse = loadTuner.merge(productWithTuner.getSecond());
			}
			if (tunerToUse instanceof ProductLoadTuner) {
				product = getProductService().getTuned(productUid, (ProductLoadTuner) tunerToUse);
			} else {
				product = getProductService().getTuned(productUid, (FetchGroupLoadTuner) tunerToUse);
			}
			cacheProduct(product, tunerToUse);
		}
		return product;
	}

	/**
	 * Retrieve a list of products. This will attempt to retrieve the products from a cache if possible,
	 * otherwise it will use the product service to get the products and then add them to the
	 * cache if there is one.
	 * 
	 * @param productUids the uids of the products to retrieve
	 * @param loadTuner the load tuner to use if loading uncached products
	 * @return a list of products
	 */
	@Override
	public List<Product> retrieveProducts(final Collection<Long> productUids, final LoadTuner loadTuner) {
		List<Product> products = new ArrayList<Product>();
		LoadTuner loadTunerToUse = loadTuner;
		
		List<Long> inputUids = new ArrayList<Long>(productUids);
		for (Iterator<Long> iter = inputUids.iterator(); iter.hasNext();) {
			Pair<Product, LoadTuner> productWithTuner = fetchFromCacheIfValid(iter.next(), loadTunerToUse);
			if (productWithTuner != null && productWithTuner.getSecond().contains(loadTunerToUse)) {
				iter.remove();
				products.add(productWithTuner.getFirst());
			} else 	if (productWithTuner != null && productWithTuner.getSecond() != null) {
				loadTunerToUse = loadTunerToUse.merge(productWithTuner.getSecond());
			}
		}
		
		Collection<Long> uncachedProductUids = inputUids;
		
		if (LOG.isDebugEnabled()) {
			for (Product product : products) {
				LOG.debug("Product cache hit: " + product.getUidPk() + "/" + loadTunerToUse);
			}
			for (Long uidpk : uncachedProductUids) {
				LOG.debug("Product cache MISS: " + uidpk + "/" + loadTunerToUse);
			}
		}
		
		if (!uncachedProductUids.isEmpty()) {
			QueryResult<Product> result = getProductQueryService().query(CriteriaBuilder.criteriaFor(Product.class)
					.with(ProductRelation.having().uids(uncachedProductUids))
					.usingLoadTuner(loadTunerToUse)
					.returning(ResultType.ENTITY));
			List<Product> loadedProducts = result.getResults(); 
			products.addAll(loadedProducts);
			cacheProducts(loadedProducts, loadTunerToUse);
		}
		
		return products;
	}
	
	/**
	 * Retrieve a <code>Product</code> with the given product code. Return <code>null</code> if
	 * a product with the given code doesn't exist.
	 * <p>
	 * 
	 * @param productCode a product code
	 * @param loadTuner the product load tuner
	 * @return a <code>Product</code> with the given product code.
	 */
	@Override
	public Product retrieveProduct(final String productCode, final LoadTuner loadTuner) {
		// Simplistic initial implementation - could provide a code->uid cache to speed things up.
		long productUid = productService.findUidById(productCode);
		if (productUid == 0) {
			return null;
		}
		return retrieveProduct(productUid, loadTuner);
	}
	
	/**
	 * Cache the product.
	 * 
	 * @param product the product to cache
	 * @param loadTuner the load tuner used
	 */
	protected abstract void cacheProduct(final Product product, final LoadTuner loadTuner);

	/**
	 * Cache a collection of products.
	 * 
	 * @param products the products to cache
	 * @param loadTuner the load tuner used
	 */
	protected abstract void cacheProducts(final List<Product> products, final LoadTuner loadTuner);

	/**
	 * Fetch a product from the cache if there is a cache and the cache entry is valid.
	 * @param productUid the uid of the product to retrieve
	 * @param loadTuner the load tuner
	 * @return a {@link Product}
	 */
	protected abstract Pair<Product, LoadTuner> fetchFromCacheIfValid(final Long productUid, final LoadTuner loadTuner);

	/**
	 * Sets the product service.
	 * 
	 * @param productService the product service
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Returns the product service.
	 * 
	 * @return the product service
	 */
	protected ProductService getProductService() {
		return productService;
	}
	
	/**
	 * Sets the product query service.
	 * 
	 * @param productQueryService the product query service
	 */
	public void setProductQueryService(final QueryService<Product> productQueryService) {
		this.productQueryService = productQueryService;
	}

	/**
	 * Returns the product query service.
	 * 
	 * @return the product query service
	 */
	protected QueryService<Product> getProductQueryService() {
		return productQueryService;
	}
}
