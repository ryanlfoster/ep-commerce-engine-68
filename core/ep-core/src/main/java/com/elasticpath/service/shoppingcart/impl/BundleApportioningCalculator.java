package com.elasticpath.service.shoppingcart.impl;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import com.elasticpath.service.tax.impl.ApportioningCalculator;

/**
 * Calculates apportioned prices for bundle constituents.
 */
public class BundleApportioningCalculator extends ApportioningCalculator {
	/**
	 * Apportions an {@link ItemPricing} by a collection of {@link ItemPricing}.
	 * 
	 * @param pricingToApportion the {@link ItemPricing} to be apportioned.
	 * @param constituents a collection of {@link ItemPricing} to apportion.
	 * @return a collection {@link ItemPricing} that is apportioned.
	 */
	public Map<String, ItemPricing> apportion(final ItemPricing pricingToApportion, final Map<String, ItemPricing> constituents) {
		Map<String, BigDecimal> constituentPrices = extractPricesWithPreservedOrder(constituents);

		Map<String, BigDecimal> apportionedPrices = calculateApportionedAmounts(pricingToApportion.getPrice(), constituentPrices);
		Map<String, BigDecimal> apportionedDiscounts = calculateApportionedAmounts(pricingToApportion.getDiscount(), constituentPrices);

		return apportionItemPricing(constituents, apportionedPrices, apportionedDiscounts);
	}

	private Map<String, ItemPricing> apportionItemPricing(final Map<String, ItemPricing> constituents,
			final Map<String, BigDecimal> apportionedPrices, final Map<String, BigDecimal> apportionedDiscounts) {
		Map<String, ItemPricing> result = new LinkedHashMap<String, ItemPricing>();
		for (String key : constituents.keySet()) {
			BigDecimal price = apportionedPrices.get(key);
			BigDecimal discount = apportionedDiscounts.get(key);
			int quantity = constituents.get(key).getQuantity();

			result.put(key, new ItemPricing(price, discount, quantity));
		}

		return result;
	}

	/**
	 * Extracts prices from the constituent {@link ItemPricing} map while preserving its order.
	 * 
	 * @param constituents the constituent {@link ItemPricing} map.
	 * @return the extracted {@link BigDecimal} prices.
	 */
	protected Map<String, BigDecimal> extractPricesWithPreservedOrder(final Map<String, ItemPricing> constituents) {
		// using LinkedHashMap to persist the constituents order.
		Map<String, BigDecimal> prices = new LinkedHashMap<String, BigDecimal>();
		for (String key : constituents.keySet()) {
			prices.put(key, constituents.get(key).getPrice());
		}
		return prices;
	}
}
