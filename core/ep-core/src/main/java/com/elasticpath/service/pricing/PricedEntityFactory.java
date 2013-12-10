package com.elasticpath.service.pricing;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactory;

/**
 * The factory for getting {@link Priced} objects.
 */
public interface PricedEntityFactory {

	/**
	 * Create a priced Product SKU.
	 * 
	 * @param productSku the product SKU to price
	 * @param plStack the price list stack
	 * @param priceProvider the callback to the client. It will be used if the price for this SKU is derived from prices of other products,
	 * or SKUs (e.g. if the SKU belongs to a calculated bundle).
	 * @param dataSource the data source to be used to get the required base amounts 
	 * @return a priced SKU
	 */
	Priced createPricedProductSku(final ProductSku productSku, final PriceListStack plStack, final PriceProvider priceProvider, 
			final BaseAmountDataSourceFactory dataSource);

	/**
	 * Create a priced Product.
	 * 
	 * @param product the product to price
	 * @param priceProvider the callback to the client. It will be used to get the price for the SKUs of this product, or in case the product
	 * is actually a calculated bundle, it will be used to resolve the constituent prices.
	 * @return a priced product
	 */
	Priced createPricedProduct(final Product product, final PriceProvider priceProvider);


	/**
	 * Create a priced calculated bundle.
	 * 
	 * @param bundle the bundle to price
	 * @param priceProvider the callback to the client. It will be used to get the price for the constituents.
	 * @return a priced product
	 */
	Priced createPricedCalculatedBundle(final ProductBundle bundle, final PriceProvider priceProvider);

}