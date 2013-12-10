/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */

package com.elasticpath.service.catalogview.impl;

import java.util.Date;
import java.util.Map;

import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.ProductAvailabilityService;
import com.elasticpath.service.misc.TimeService;

/**
 * Default implementation of Product Availability and Displayability query methods.
 */
public class ProductAvailabilityServiceImpl implements ProductAvailabilityService {
	private TimeService timeService;

	@Override
	public boolean isSkuAvailable(final Product product, final ProductSku sku, final SkuInventoryDetails skuInventoryDetails) {
		return isSkuAvailable(product, sku, skuInventoryDetails, true);
	}

	@Override
	public boolean isSkuAvailable(final Product product, final ProductSku sku,
								  final SkuInventoryDetails skuInventoryDetails, final boolean checkProductStartEndDate) {
		Date currentDate = getTimeService().getCurrentTime();

		return (!checkProductStartEndDate || (product.isWithinDateRange(currentDate) && sku.isWithinDateRange(currentDate)))
				&& skuInventoryDetails != null
				&& skuInventoryDetails.hasSufficientUnallocatedQty();
	}

	@Override
	public boolean isProductAvailable(final Product product,
			final Map<String, SkuInventoryDetails> skuInventoryDetails, final boolean checkProductStartEndDate) {
		for (Map.Entry<String, SkuInventoryDetails> skuInventory : skuInventoryDetails.entrySet()) {
			if (isSkuAvailable(product, product.getSkuByCode(skuInventory.getKey()), skuInventory.getValue(), checkProductStartEndDate)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isSkuDisplayable(final Product product, final ProductSku sku, final Store store, final SkuInventoryDetails skuInventoryDetails) {
		return isSkuDisplayable(product, sku, store, skuInventoryDetails, true);
	}

	@Override
	public boolean isSkuDisplayable(final Product product, final ProductSku sku, final Store store, final SkuInventoryDetails skuInventoryDetails,
									final boolean checkProductStartEndDate) {
		Date currentDate = getTimeService().getCurrentTime();
		return !product.isHidden()
				&& (!checkProductStartEndDate || (product.isWithinDateRange(currentDate) && sku.isWithinDateRange(currentDate)))
				&& (isInStock(skuInventoryDetails) || store.isDisplayOutOfStock())
				&& product.isInCatalog(store.getCatalog(), true)
				&& !product.getProductSkus().isEmpty();
	}

	/**
	 * Returns <code>true</code> if the sku can be displayed for the given {@link Store}.
	 * Checks whether the product is not hidden, current date is within the product's date range
	 * and that the product has at least one SKU in stock or is out of stock but should be
	 * visible.
	 *
	 *
	 * @param product the product to be checked for displayability
	 * @param store the {@link com.elasticpath.domain.store.Store} to check stock for
	 * @param skuInventoryDetails the inventory details for the product's skus
	 * @param checkProductStartEndDate true if the method should take the product start and date range into account
	 * @return <code>true</code> if the product is available for purchase, <code>false</code>
	 *         otherwise
	 */
	@Override
	public boolean isProductDisplayable(final Product product, final Store store, final Map<String, SkuInventoryDetails> skuInventoryDetails,
										   final boolean checkProductStartEndDate) {
		for (Map.Entry<String, SkuInventoryDetails> skuInventory : skuInventoryDetails.entrySet()) {
			if (isSkuDisplayable(product, product.getSkuByCode(skuInventory.getKey()), store,
					skuInventory.getValue(), checkProductStartEndDate)) {
				return true;
			}
		}

		return false;
	}

	private boolean isInStock(final SkuInventoryDetails details) {
		if (details == null) {
			return false;
		}

		AvailabilityCriteria availability = details.getAvailabilityCriteria();
		if (availability != AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK) {
			return details.hasSufficientUnallocatedQty();
		}

		return details.hasSufficientUnallocatedQty() && details.getAvailableQuantityInStock() > 0;
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	protected TimeService getTimeService() {
		return timeService;
	}
}
