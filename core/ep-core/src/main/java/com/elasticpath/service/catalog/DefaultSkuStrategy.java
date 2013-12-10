package com.elasticpath.service.catalog;

import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;

/**
 * The strategy to pick the default SKU for a product.
 */
public interface DefaultSkuStrategy {

	/**
	 * Gets the default SKU.
	 *
	 * @param product the product
	 * @param shopper the shopper
	 * @return the default sku
	 */
	ProductSku getDefaultSku(Product product, Shopper shopper);

	/**
	 * Gets the default SKU code.
	 *
	 * @param product the product
	 * @param shopper the shopper
	 * @return the default sku
	 */
	String getDefaultSkuCode(Product product, Shopper shopper);

	/**
	 * Gets the default SKU code for the given constituent item.
	 *
	 * @param constituentItem the constituent item
	 * @param shopper the shopper
	 * @return the default SKU code
	 */
	String getDefaultSkuCode(ConstituentItem constituentItem, Shopper shopper);

}
