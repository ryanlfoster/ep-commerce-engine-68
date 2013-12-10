package com.elasticpath.service.pricing.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.pricing.PriceProvider;

/**
 * A Product that has been priced.
 */
class PricedProduct extends AbstractPricedEntity<Product> {

	
	/**
	 * Construct a priced product given the product and price list stack and a mapping between
	 * price schedules and base amounts.
	 *
	 * @param entity the product to price
	 * @param priceProvider the price provider
	 */
	public PricedProduct(final Product entity, final PriceProvider priceProvider) {
		super(entity, priceProvider);
	}

	
	/**
	 * Get the lowest price for product.
	 * 
	 * @param product given product
	 * @param plStack price list stack
	 * @param baseAmounts the list of base amounts
	 * @return lowest price for all tiers
	 */
	private Price getLowestPriceForProduct(final Product product) {
		if (getBundleIdentifier().isCalculatedBundle(product)) {
			throw new EpSystemException("This class does not support calculated bundles. Use PricedCalculatedBundle instead.");
		}
		return findLowestPricedSku(product);
	}

	private Price findLowestPricedSku(final Product product) {
		final Price lowestPrice = getPriceBean();
		lowestPrice.setCurrency(getPriceProvider().getCurrency());

		boolean foundSkuPrices = false;

		final Map<String, Price> skuPricesByGuid = findSkuPrices(product);
		final Set<Integer> tierQuantities = findTiers(skuPricesByGuid.values());

		TimeService timeService = getBeanFactory().getBean(ContextIdNames.TIME_SERVICE);
		Date currentTime = timeService.getCurrentTime();
		for (final int tierQuantity : tierQuantities) {
			for (final ProductSku sku : product.getProductSkus().values()) {
				if (!sku.isWithinDateRange(currentTime)) {
					continue;
				}

				final Price skuPrice = skuPricesByGuid.get(sku.getSkuCode());
				PriceTier priceTier = null;
				if (skuPrice != null) {
					priceTier = skuPrice.getPriceTierByExactMinQty(tierQuantity);
				}

				if (priceTier != null) {
					final PriceTier currentLowest = lowestPrice.getPriceTierByExactMinQty(tierQuantity);
					if (currentLowest == null || priceTier.compareTo(currentLowest) < 0) {
						lowestPrice.addOrUpdatePriceTier(priceTier);
						foundSkuPrices = true;
					}
				}
			}
		}

		if (foundSkuPrices) {
			return lowestPrice;
		}
		return null;
	}


	private Set<Integer> findTiers(final Collection<Price> prices) {
		final Set<Integer> tierQuantities = new HashSet<Integer>();
		for (final Price price : prices) {
			if (price != null) {
				tierQuantities.addAll(price.getPriceTiersMinQuantities());
			}
		}
		return tierQuantities;
	}

	private Map<String, Price> findSkuPrices(final Product product) {
		final Map<String, Price> skuPricesByGuid = new HashMap<String, Price>();
		for (final ProductSku sku : product.getProductSkus().values()) {
			final Price skuPrice = getPriceProvider().getProductSkuPrice(sku);
			skuPricesByGuid.put(sku.getSkuCode(), skuPrice);
		}
		return skuPricesByGuid;
	}

	/**
	 * Get the price.
	 * 
	 * @return the price
	 */
	@Override
	public Price getPrice() {
		Price purchaseTimePrice = getLowestPriceForProduct(getEntity());
		if (purchaseTimePrice != null) {
			PriceSchedule priceSchedule = getBeanFactory().getBean(ContextIdNames.PRICE_SCHEDULE);
			priceSchedule.setType(PriceScheduleType.PURCHASE_TIME);
			PricingScheme pricingScheme = getBeanFactory().getBean(ContextIdNames.PRICING_SCHEME);
			pricingScheme.setPriceForSchedule(priceSchedule, purchaseTimePrice);
			purchaseTimePrice.setPricingScheme(pricingScheme);
		}
		return purchaseTimePrice;
	}
	
}
