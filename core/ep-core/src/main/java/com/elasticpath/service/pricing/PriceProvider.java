package com.elasticpath.service.pricing;

import java.util.Currency;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;


/**
 * Interface for classes that can provide the price for a product or a sku. 
 */
public interface PriceProvider {
	/**
	 * Get the price for the SKU.
	 * @param productSku the SKU
	 * @return the price
	 */
	Price getProductSkuPrice(final ProductSku productSku);
	
	/**
	 * Get the price for the product.
	 * @param product the product
	 * @return the price
	 */
	Price getProductPrice(final Product product);
	
	/**
	 * @return the currency of the price provider
	 */
	Currency getCurrency();
}
