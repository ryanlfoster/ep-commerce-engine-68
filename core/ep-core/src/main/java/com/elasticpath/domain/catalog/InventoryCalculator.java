package com.elasticpath.domain.catalog;

import com.elasticpath.common.dto.InventoryDetails;
import com.elasticpath.service.catalog.ProductInventoryManagementService;

/**
 * Calculates {@code InventoryDetails} for a given {@code ProductSku}. For example, this class calculates the correct
 * bundle inventory.
 */
public interface InventoryCalculator {

	/**
	 * Calculates the inventory details for {@code productSku}.
	 * @param productInventoryManagementService the service.
	 * @param productSku The sku to use.
	 * @param warehouseUid the ware house uid.
	 * @return The inventory details.
	 */
	InventoryDetails getInventoryDetails(
			ProductInventoryManagementService productInventoryManagementService, 
			ProductSku productSku, long warehouseUid);

}
