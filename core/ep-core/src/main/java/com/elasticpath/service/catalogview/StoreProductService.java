package com.elasticpath.service.catalogview;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.EpService;

/**
 * A Storefront use-case based product service.  Returns read-mostly 
 * StoreProducts more suitable for use in Storefronts than the core 
 * domain Product object.
 * 
 */
public interface StoreProductService extends EpService {

	/**
	 * Retrieves a product instance using the product service and applies the flags isDisplayable and isAvailable using the default product sku.
	 * 
	 * @param uidPk the product UID
	 * @param store the store this product is being checked
	 * @param loadTuner the load tuner
	 * 
	 * @return the StoreProduct instance
	 */
	StoreProduct getProductForStore(final long uidPk, final Store store, final StoreProductLoadTuner loadTuner);

	
	/**
	 * Get a list of store products for a specified store.
	 * 
	 * @param uidPks the uids of the products.
	 * @param store the store the products belong to.
	 * @param loadTuner the load tuner to tune the store product
	 * @return the store product
	 */
	List<StoreProduct> getProductsForStore(final List<Long> uidPks, final Store store, final StoreProductLoadTuner loadTuner);
	
	/**
	 * Retrieves a product instance using the product service and applies the flags isDisplayable and isAvailable using the sku with a given uid. 
	 * The default product sku is used if there's no sku with the given uid
	 * 
	 * @param uidPk the product UID
	 * @param store the store this product is being checked
	 * @param loadTuner the load tuner
	 * @param skuUid the sku uid
	 * @return the StoreProduct instance
	 */
	StoreProduct getProductForStore(long uidPk, Long skuUid, Store store, StoreProductLoadTuner loadTuner);
	
	/**
	 * Get a store product for the specified product.
	 *
	 * @param product the product to get the store product for.
	 * @param store the store the product belongs to.
	 * @return the store product
	 */
	StoreProduct getProductForStore(final Product product, final Store store);
	
	
	/**
	 * Retrieves a list of {@link Product}s from a list of {@link Product} uids and returns them as {@link IndexProduct}s.  
	 * @param productUids the list of product uids to load
	 * @param stores the list of stores 
	 * @param fetchGroupLoadTuner the {@link StoreProductLoadTuner}
	 * @return a list of {@link IndexProduct}s
	 */
	Collection<IndexProduct> getIndexProducts(final Collection<Long> productUids, final Collection<Store> stores, 
			final FetchGroupLoadTuner fetchGroupLoadTuner);
	
	/**
	 * Gets the index product.
	 *
	 * @param uidPk the product UID
	 * @param loadTuner the load tuner
	 * @param stores a collection of stores for which the product is being indexed
	 * @return the IndexProduct instance
	 */
	IndexProduct getIndexProduct(long uidPk, FetchGroupLoadTuner loadTuner, Collection<Store> stores);

	
	/**
	 * Create an index product from the given product.
	 * 
	 * @param product the product to wrap as an index product
	 * @param stores a collection of store for which the product is going to be indexed
	 * @return an {@link IndexProduct}
	 */
	IndexProduct createIndexProduct(final Product product, Collection<Store> stores);
	
}
