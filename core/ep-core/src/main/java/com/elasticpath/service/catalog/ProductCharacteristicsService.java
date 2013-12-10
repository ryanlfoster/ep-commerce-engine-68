package com.elasticpath.service.catalog;

import java.util.Collection;
import java.util.Map;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Service methods for getting the characteristics of a product.
 */
public interface ProductCharacteristicsService {

	/**
	 * Gets the product characteristics.
	 *
	 * @param product the product
	 * @return the product characteristics
	 */
	ProductCharacteristics getProductCharacteristics(Product product);
	
	/**
	 * Gets the product characteristics.
	 *
	 * @param productSku the product sku
	 * @return the product characteristics
	 */
	ProductCharacteristics getProductCharacteristics(ProductSku productSku);
	
	/**
	 * Gets the product characteristics for the product whose sku has the given sku code.
	 *
	 * @param skuCode the sku code
	 * @return the product characteristics
	 */
	ProductCharacteristics getProductCharacteristicsForSkuCode(String skuCode);
	
	/**
	 * Gets a map of product code to product characteristics for the given collection of products.
	 *
	 * @param products the products
	 * @return the product characteristics map
	 */
	Map<String, ProductCharacteristics> getProductCharacteristicsMap(Collection<? extends Product> products);
}
