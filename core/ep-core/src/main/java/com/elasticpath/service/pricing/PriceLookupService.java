package com.elasticpath.service.pricing;

import java.util.Collection;
import java.util.Map;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.pricing.PriceListStack;

/**
 * Service for looking up prices of products and sku.
 */
public interface PriceLookupService {

	/**
	 * Obtain the Price for a given {@link ProductSku} in a {@link PriceListStack}.
	 * 
	 * @param sku to look up
	 * @param plStack a {@link PriceListStack} for price lookup
	 * @return the price of the sku
	 */
	Price getSkuPrice(final ProductSku sku, final PriceListStack plStack);

	/**
	 * Obtain the Prices for a given {@link ProductSku} in a {@link PriceListStack}.
	 * 
	 * @param sku to look up
	 * @param plStack a {@link PriceListStack} for price lookup
	 * @return the price map where key is the Price List guid and value is the price for this guid
	 */
	Map<String, Price> getSkuPrices(final ProductSku sku, final PriceListStack plStack);
	
	/**
	 * Gets the appropriate price for this Product within the {@link PriceListStack}
	 * For multi-sku products, the lowest price among skus is returned.
	 * For single sku products, the default sku is used.
	 *  
	 * @param product the product
	 * @param plStack price list stack for lookup
	 * @return the price of the product
	 */
	Price getProductPrice(final Product product, final PriceListStack plStack);

	/**
	 * Gets the appropriate price for given list of Products within 
	 * the {@link PriceListStack}. For multi-sku products, the lowest 
	 * price among skus is returned. For single sku products, 
	 * the default sku is used.
	 *  
	 * @param products the list of products
	 * @param plStack price list stack for lookup
	 * @return the product price map
	 */	
	Map<Product, Price> getProductsPrices(final Collection<Product> products, final PriceListStack plStack);
	
	/**
	 * Retrieve the list of price adjustment for the given {@link ProductBundle}.
	 *  
	 * @param bundle the ProductBundle
	 * @param plGuid price list to look up.
	 * @return collection of {@link PriceAdjustment} found for the bundle, or empty list if none
	 * 
	 * @deprecated Use {@link #getProductBundlePriceAdjustmentsMap(ProductBundle, String)} instead.
	 */
	@Deprecated
	Collection <PriceAdjustment> getProductBundlePriceAdjustments(final ProductBundle bundle, final String plGuid);
	
	/**
	 * Gets a map of price adjustments for all product bundle constituents (including nested bundles), keyed by constituent GUID.
	 * The map will not contain adjustments with zero values, adjustments with negative 
	 * values on assigned bundles, and adjustments with positive values for calculated bundles.
	 * @param bundle the product bundle
	 * @param plGuid the price list GUID
	 * @return the map of price adjustments, keyed by bundle constituent GUID
	 */
	Map<String, PriceAdjustment> getProductBundlePriceAdjustmentsMap(final ProductBundle bundle, final String plGuid);

	/**
	 * Find the first price list for which a product sku (or its product) has a price in. 
	 *
	 * @param productSku the product sku
	 * @param plStack the price list stack to look up
	 * @return GUID of the price list if found. null if not.
	 */
	String findPriceListWithPriceForProductSku(final ProductSku productSku, final PriceListStack plStack);

}
