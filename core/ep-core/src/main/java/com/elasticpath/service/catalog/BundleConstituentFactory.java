package com.elasticpath.service.catalog;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * A factory that creates a {@link BundleConstituent}} for a {@link Product}.
 */
public interface BundleConstituentFactory {
	/**
	 * Creates a bundle constituent from a product.
	 * 
	 * @param product the product to create a bundle constituent from
	 * @param quantity the quantity of the bundle constituent
	 * 
	 * @return {@link BundleConstituent}
	 */
	BundleConstituent createBundleConstituent(Product product, int quantity);
	
	/**
	 * Creates a bundle constituent from a SKU.
	 * 
	 * @param productSku the SKU to create a bundle constituent from
	 * @param quantity the quantity of the bundle constituent
	 * 
	 * @return {@link BundleConstituent}
	 */
	BundleConstituent createBundleConstituent(ProductSku productSku, int quantity);
}
