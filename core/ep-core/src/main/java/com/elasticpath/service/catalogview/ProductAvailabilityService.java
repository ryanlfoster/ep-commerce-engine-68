/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */

package com.elasticpath.service.catalogview;

import java.util.Map;

import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.Store;

/**
 * Product Availability and Displayability query methods.
 */
public interface ProductAvailabilityService {

	/**
	 * Returns <code>true</code> if the product is available for purchase from the given
	 * {@link com.elasticpath.domain.store.Store}. Checks whether the current date is within the product's date range and that
	 * the product has at least one SKU in stock.
	 *
	 * @param product the product to be checked for availability
	 * @param sku the sku to be checked for availability
	 * @param skuInventoryDetails The inventory details for the default sku
	 *
	 * @return <code>true</code> if the product is available for purchase, <code>false</code> otherwise
	 */
	boolean isSkuAvailable(final Product product, final ProductSku sku, final SkuInventoryDetails skuInventoryDetails);

	/**
	 * Returns <code>true</code> if the product is available for purchase from the given
	 * {@link com.elasticpath.domain.store.Store}. Checks whether the current date is within the product's date range and that
	 * the product has at least one SKU in stock.
	 *
	 * @param product the product to be checked for availability
	 * @param sku the sku to be checked for availability
	 * @param skuInventoryDetails The inventory details for the default sku
	 * @param checkProductStartEndDate true if the method should take the product start and date range into account
	 *
	 * @return <code>true</code> if the product is available for purchase, <code>false</code> otherwise
	 */
	boolean isSkuAvailable(final Product product, final ProductSku sku,
						   final SkuInventoryDetails skuInventoryDetails, final boolean checkProductStartEndDate);

	/**
	 * Returns <code>true</code> if the product is available for purchase given the supplied inventory details.
	 * Checks whether the current date is within the product's date range and that
	 * the product has at least one active SKU in stock.
	 *
	 * @param product the product to be checked for availability
	 * @param skuInventoryDetails The inventory details for the product's skus, keyed by sku code
	 * @param checkProductStartEndDate true if the method should take the product start and date range into account
	 *
	 * @return <code>true</code> if any of the product's skus are available for purchase, <code>false</code> otherwise
	 */
	boolean isProductAvailable(Product product,
							   Map<String, SkuInventoryDetails> skuInventoryDetails,
							   boolean checkProductStartEndDate);

	/**
	 * Returns <code>true</code> if the sku can be displayed for the given {@link com.elasticpath.domain.store.Store}.
	 * Checks whether the product is not hidden, current date is within the product's date range
	 * and that the sku is in stock or is out of stock but should be visible.
	 *
	 * @param product the product to be checked for displayability
	 * @param sku the sku to be checked for displayability
	 * @param store the {@link com.elasticpath.domain.store.Store} to check stock for
	 * @param skuInventoryDetails the inventory details for the sku
	 * @return <code>true</code> if the product is available for purchase, <code>false</code> otherwise
	 */
	boolean isSkuDisplayable(final Product product, final ProductSku sku, final Store store, final SkuInventoryDetails skuInventoryDetails);

	/**
	 * Returns <code>true</code> if the sku can be displayed for the given {@link Store}.
	 * Checks whether the product is not hidden, current date is within the product's date range
	 * and that the sku is in stock or is out of stock but should be visible.
	 *
	 * @param product the product to be checked for displayability
	 * @param sku the sku to be checked for displayability
	 * @param store the {@link com.elasticpath.domain.store.Store} to check stock for
	 * @param skuInventoryDetails the inventory details for the sku
	 * @param checkProductStartEndDate true if the method should take the product start and date range into account
	 * @return <code>true</code> if the sku is available for purchase, <code>false</code> otherwise
	 */
	boolean isSkuDisplayable(final Product product, final ProductSku sku, final Store store, final SkuInventoryDetails skuInventoryDetails,
							 final boolean checkProductStartEndDate);

	/**
	 * Returns <code>true</code> if the sku can be displayed for the given {@link Store}.
	 * Checks whether the product is not hidden, current date is within the product's date range
	 * and that the product has at least one SKU in stock or is out of stock but should be
	 * visible.
	 *
	 * @param product the product to be checked for displayability
	 * @param store the {@link com.elasticpath.domain.store.Store} to check stock for
	 * @param skuInventoryDetails the inventory details for the product's skus
	 * @param checkProductStartEndDate true if the method should take the product start and date range into account
	 *
	 * @return <code>true</code> if the product is available for purchase, <code>false</code> otherwise
	 */
	boolean isProductDisplayable(final Product product, final Store store, final Map<String, SkuInventoryDetails> skuInventoryDetails,
			final boolean checkProductStartEndDate);
}
