package com.elasticpath.service.pricing.impl;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.SimplePrice;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.service.pricing.PriceProvider;
import com.elasticpath.service.pricing.Priced;

/**
 * Calculates the price for a payment-schedule-capable product.
 */
public class PricedPaymentScheduleCapableProduct implements Priced {
	private final Product entity;
	private final PriceProvider priceProvider;


	/**
	 * Default c'tor.
	 * @param product the product to perform the calculation on
	 * @param priceProvider the price provider to use
	 */
	public PricedPaymentScheduleCapableProduct(final Product product, final PriceProvider priceProvider) {
		this.entity = product;
		this.priceProvider = priceProvider;
	}

	@Override
	public Price getPrice() {
		Money lowestMoney = null;
		Price lowestPrice = null;
		int lowestQuantity = Integer.MAX_VALUE;
		for (ProductSku sku : getEntity().getProductSkus().values()) {
			final Price skuPrice = getPriceProvider().getProductSkuPrice(sku);
			Money skuMoney = null;
			int skuMinTier = Integer.MAX_VALUE;
			if (skuPrice != null) {
				PricingScheme pricingScheme = skuPrice.getPricingScheme();
				PriceSchedule lowestSchedule = pricingScheme.getScheduleForLowestPrice();
				SimplePrice simplePriceForSchedule = pricingScheme.getSimplePriceForSchedule(lowestSchedule);
				skuMinTier = simplePriceForSchedule.getFirstPriceTierMinQty();
				skuMoney = simplePriceForSchedule.getLowestPrice(skuMinTier);
			}
			if (skuMoney != null
					&& (lowestMoney == null || skuMinTier < lowestQuantity
							|| (skuMinTier == lowestQuantity && skuMoney.compareTo(lowestMoney) < 0)
					    )) {
				lowestMoney = skuMoney;
				lowestPrice = skuPrice;
				lowestQuantity = skuMinTier;
			}
		}

		return lowestPrice;
	}


	protected Product getEntity() {
		return entity;
	}


	protected PriceProvider getPriceProvider() {
		return priceProvider;
	}

}
