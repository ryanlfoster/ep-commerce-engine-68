package com.elasticpath.common.dto.sellingchannel;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.Product;

/**
 * Shopping Item Dto Factory.
 *
 */
public interface ShoppingItemDtoFactory {
	/**
	 * @param skuCode sku code
	 * @param qty quantity
	 * @return new shopping item dto
	 */
	ShoppingItemDto createDto(String skuCode, int qty);
	
	/**
	 * Creates a ShoppingItemDto tree for a given Product. In the case of multisku products,
	 * the dto that's created will specify the sku code of the product's default sku.
	 * @param product the product
	 * @param qty the quantity of the product to be specified on the dto
	 * @return the root dto
	*/
	ShoppingItemDto createDto(Product product, int qty);
	
	
	/**
	 * Creates a ShoppingItemDto tree for a given Product. The SKUs and bundle constituents will be selected
	 * based on the given item configuration.
	 * 
	 * @param product the product
	 * @param qty the quantity of the product to be specified on the dto
	 * @param configuration the item configuration
	 * @return the root dto
	 */
	ShoppingItemDto createDto(Product product, int qty, ItemConfiguration configuration);
}
