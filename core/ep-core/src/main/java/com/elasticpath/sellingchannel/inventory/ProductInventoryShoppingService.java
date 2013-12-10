package com.elasticpath.sellingchannel.inventory;

import java.util.List;
import java.util.Map;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.commons.util.capabilities.Capabilities;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.domain.store.Store;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;

/**
 * Calculates {@code SkuInventoryDetails} for a given {@code ProductSku}. For example, this class calculates the correct
 * bundle inventory.
 */
public interface ProductInventoryShoppingService {

	
	/**
	 * Calculates the inventory details (message code, availability criteria, inventory, restock/release date) for the provided sku.
	 * @param sku The sku to calculate for.
	 * @param store The store to use.
	 * @param shoppingItemDto The DTO to use to get the sku selection in the case that the sku represents
	 * a product or bundle containing multiple skus
	 * @return The inventory details.
	 */
	SkuInventoryDetails getSkuInventoryDetails(ProductSku sku,
			Store store, ShoppingItemDto shoppingItemDto);
	
	/**
	 * Calculates the inventory details (message code, availability criteria, inventory, restock/relese date) for the
	 * provided sku. If the sku is a Bundle containing any multi-sku products, the default sku of the multi-sku
	 * products will be assumed for the purposes of inventory calculation.
	 * 
	 * Quantity of 1 will be assumed.
	 * 
	 * @param productSku the sku for which the inventory details are required
	 * @param store the store in which the inventory must be calculated
	 * @return the inventory details
	 */
	SkuInventoryDetails getSkuInventoryDetails(final ProductSku productSku, final Store store);

	/**
	 * Calculates the inventory details (message code, availability criteria, inventory, restock/relese date) for the
	 * provided product. If the sku is a Bundle containing any multi-sku products, the default sku of the multi-sku
	 * products will be assumed for the purposes of inventory calculation.
	 * 
	 * Quantity of 1 will be assumed.
	 * 
	 * @param product the product for which the inventory details are required. All skus will be calculated.
	 * @param store the store in which the inventory must be calculated
	 * @return the inventory details
	 * @since 6.2.2
	 */
	Map<String, SkuInventoryDetails> getSkuInventoryDetailsForAllSkus(
			Product product, Store store);
	
	/**
	 * Calculates the inventory details (message code, availability criteria, inventory, restock/relese date) for all the
	 * provided products. If the sku is a Bundle containing any multi-sku products, the default sku of the multi-sku
	 * products will be assumed for the purposes of inventory calculation.
	 * 
	 * Quantity of 1 will be assumed.
	 * 
	 * @param products the products for which the inventory details are required. All skus will be calculated.
	 * @param store the store in which the inventory must be calculated
	 * @return a map of product code to a map of sku code to SkuInventoryDetails
	 * @since 6.2.3
	 */
	Map<String, Map<String, SkuInventoryDetails>> getSkuInventoryDetailsForAllSkus(
			List<Product> products, Store store);
	
	/**
	 * Retrieves the PreOrBackOrder details for the given product SKU.
	 * Forwards the request to ProductInventoryManagementService.
	 * 
	 * @param productSku The product SKU.
	 * @return the {@link PreOrBackOrderDetails}.
	 */
	PreOrBackOrderDetails getPreOrBackOrderDetails(ProductSku productSku);
	
	/**
	 * Retrieves the inventory according to the given InventoryKey.
	 * 
	 * @param inventoryKey The key.
	 * @return The inventory object for the given sku and warehouse.
	 */
	InventoryDto getInventory(InventoryKey inventoryKey);
	
	/**
	 * Gets the Inventory subsystem's Capabilities.
	 * 
	 * @return The Capabilities.
	 */
	Capabilities getInventoryCapabilities();
}
