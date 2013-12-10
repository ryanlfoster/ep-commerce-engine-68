package com.elasticpath.common.pricing.service;

import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.pricing.DisplayPriceDTO;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;

/**
 * Interface to get Price and Promoted Price.
 */
public interface PriceLookupFacade {

	/**
	 * Loads the {@link Price} using {@link #getPriceForSku(ProductSku, PriceLookupContext)} and applies promotions.
	 *
	 * @param productSku the <code>ProductSku</code> instance
	 * @param store {@code Store}
	 * @param shopper {@link Shopper}
	 * @param ruleTracker set to track promotion rules applied
	 * @return the Promoted {@link Price} instance
	 */
	Price getPromotedPriceForSku(ProductSku productSku, Store store,
			Shopper shopper, Set<Long> ruleTracker);

	/**
	 * Loads the {@link Price} using {@link #getPriceForProduct(Product, PriceLookupContext)} and applies promotions.
	 *
	 * @param product the <code>Product</code> instance
	 * @param store {@code Store}
	 * @param shopper {@link Shopper}
	 * @param ruleTracker set to track promotion rules applied
	 * @return the Promoted {@link Price} instance
	 */
	Price getPromotedPriceForProduct(Product product, Store store,
			Shopper shopper, Set<Long> ruleTracker);

	/**
	 * Loads the {@link Price}s for given list of {@link Product}
	 * using {@link #getPriceForProduct(Product, PriceLookupContext)}
	 * and applies promotions.
	 *
	 * @param products the list of <code>Product</code>s
	 * @param store {@code Store}
	 * @param shopper {@link Shopper}
	 * @param ruleTracker set to track promotion rules applied
	 * @return the map of product code - and his lowest {@link Price}
	 */
	Map<String, Price> getPromotedPricesForProducts(Collection<Product> products, Store store,
			Shopper shopper, Set<Long> ruleTracker);


	/**
	 * Get all price list prices that are applicable for product sku, given the price list stack.
	 * @param productSku the product sku from order sku
	 * @param stack the price list stack for product sku
	 * @param tierQuantity the tier quantity to filter by (use null if all tiers are required)
	 * @param store store
	 * @return list of applicable prices or an empty list
	 */
	List<DisplayPriceDTO> getPricesForOrderSku(ProductSku productSku, PriceListStack stack, Integer tierQuantity, Store store);

	/**
	 * Get a map of all price adjustments on a {@link ProductBundle}, keyed by {@see BundleConstituent} GUID.
	 *
	 * @param bundle the {@link ProductBundle}
	 * @param catalogCode catalog code
	 * @param shopper the shopper
	 * @return Map of {@link PriceAdjustment}s on the bundle, empty map if none found.
	 */
	Map <String, PriceAdjustment> getPriceAdjustmentsForBundle(ProductBundle bundle, String catalogCode, Shopper shopper);

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

	/**
	 * Calculates the price of a shopping item, considering the selections inside bundles.
	 * 
	 * @param shoppingItem the shopping item
	 * @param shopper the customer session
	 * @param store the store
	 * @param ruleTracker the rules
	 * @return the calculated price
	 */
	Price getShoppingItemPrice(final ShoppingItem shoppingItem, final Store store, final Shopper shopper,
			final Set<Long> ruleTracker);

	/**
	 * Calculates the price of a shopping item DTO, considering the selections inside bundles.
	 * 
	 * @param shoppingItemDto the shopping item
	 * @param shopper the customer session
	 * @param store the store
	 * @param ruleTracker the rules
	 * @return the calculated price
	 */
	Price getShoppingItemDtoPrice(final ShoppingItemDto shoppingItemDto, final Store store, final Shopper shopper,
			final Set<Long> ruleTracker);

}
