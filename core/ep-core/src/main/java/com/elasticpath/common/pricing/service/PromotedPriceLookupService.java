package com.elasticpath.common.pricing.service;

import java.util.Collection;
import java.util.Currency;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactory;

/**
 * Obtains the promoted price for the given product/SKUs.
 */
public interface PromotedPriceLookupService {
	/**
	 * Obtain the Prices for a given {@link ProductSku} in a {@link PriceListStack}.
	 * 
	 * @param sku to look up
	 * @param plStack a {@link PriceListStack} for price lookup
	 * @param store store
	 * @return the price map where key is the Price List guid and value is the price for this guid
	 */
	Map<String, Price> getSkuPrices(final ProductSku sku, final PriceListStack plStack, final Store store);

	/**
	 * Obtain the Price for a given {@link ProductSku} in a {@link PriceListStack}.
	 * 
	 * @param sku to look up
	 * @param plStack a {@link PriceListStack} for price lookup
	 * @param store store
	 * @param ruleTracker rule tracker
	 * @return the catalog promoted price for the sku
	 */
	Price getSkuPrice(final ProductSku sku, final PriceListStack plStack, final Store store, final Set <Long> ruleTracker);
	

	/**
	 * Obtain the Price for a given {@link Product} in a {@link PriceListStack}.
	 * 
	 * @param product the product to look up
	 * @param plStack a {@link PriceListStack} for price lookup
	 * @param store store
	 * @param ruleTracker rule tracker
	 * @return the catalog promoted price for the product
	 */
	Price getProductPrice(final Product product, final PriceListStack plStack, final Store store, final Set <Long> ruleTracker);


	/**
	 * Obtain the Price for a given {@link Product} in a {@link PriceListStack}.
	 * 
	 * @param product the product to look up
	 * @param plStack a {@link PriceListStack} for price lookup
	 * @param store store
	 * @param ruleTracker rule tracker
	 * @param dataSourceFactory the BaseAmountDataSourceFactory to be used to access the base amounts.
	 * @return the catalog promoted price for the product
	 */
	Price getProductPrice(final Product product, final PriceListStack plStack, final Store store, 
			final Set <Long> ruleTracker, final BaseAmountDataSourceFactory dataSourceFactory);

	
	/**
	 * Obtain the prices for the given collection of {@link Product} in a {@link PriceListStack}.
	 * 
	 * @param products the products to look up
	 * @param plStack a {@link PriceListStack} for price lookup
	 * @param store store
	 * @param ruleTracker rule tracker
	 * @return price map where key is the product code and value is the price for this code
	 */
	Map<String, Price> getProductsPrices(final Collection<Product> products, 
			final PriceListStack plStack, final Store store, final Set <Long> ruleTracker);
	
	
	/**
	 * Apply catalog promotions on a given product price.
	 *
	 * @param product the product used to determine the catalog promotion conditions to apply to the price
	 * @param store the store catalog used to determine the catalog promotions
	 * @param currency currency
	 * @param price to be promoted
	 * @param ruleTracker tracking rules applied
	 */
	void applyCatalogPromotions(Product product, Store store, Currency currency, Price price, Set<Long> ruleTracker);

	
}
