package com.elasticpath.service.pricing.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.service.pricing.PricePopulator;

/**
 * Assembles @{link Price} and {@link PriceTier}s from {@link BaseAmount}s. 
 */
public class PricePopulatorImpl implements PricePopulator {
	private BeanFactory beanFactory;
	
	@Override
	public boolean populatePriceFromBaseAmounts(final Collection<BaseAmount> amounts, final Currency currency, final Price price) {
		// update existing priceTiers if they exist
		Map<Integer, PriceTier> priceTiers = price.getPersistentPriceTiers();
		if (priceTiers == null) {
			priceTiers = new HashMap<Integer, PriceTier>();
		}

		boolean found = false;
		for (BaseAmount baseAmount : amounts) {
			PriceTier tier = populatePriceTierFromBaseAmount(baseAmount);
			if (tier != null) {
				// NOTE: Loss of precision converting to Integer
				int qty = baseAmount.getQuantity().intValue();
				priceTiers.put(qty, tier);
				found = true;
			}
		}

		if (found) {
			price.setPersistentPriceTiers(priceTiers);
			price.setCurrency(currency);
		}

		return found;
	}

	@Override
	public PriceTier populatePriceTierFromBaseAmount(final BaseAmount amount) {
		PriceTier tier = null;
		final BigDecimal listValue = amount.getListValue();
		final BigDecimal saleValue = amount.getSaleValue();
		if (!(listValue == null && saleValue == null)) {
			tier = beanFactory.getBean(ContextIdNames.PRICE_TIER);
			tier.setListPrice(listValue);
			tier.setSalePrice(saleValue);
			tier.setMinQty(amount.getQuantity().intValue());
			tier.setPriceListGuid(amount.getPriceListDescriptorGuid());
			
		}
		return tier;
	}
	
	/**
	 * Set bean factory.
	 * @param beanFactory bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
}
