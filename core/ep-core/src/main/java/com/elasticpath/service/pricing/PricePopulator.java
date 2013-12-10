package com.elasticpath.service.pricing;

import java.util.Collection;
import java.util.Currency;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.pricing.BaseAmount;

/**
 * Assembles @{link Price} and {@link PriceTier}s from {@link BaseAmount}s. 
 */
public interface PricePopulator {

	/**
	 * Populate a {@link com.elasticpath.domain.catalog.Price} from a collection of {@link com.elasticpath.domain.pricing.BaseAmount}.
	 * 
	 * @param amounts collection of base amounts
	 * @param currency of the base amounts
	 * @param price the Price object to populate
	 * @return true if price found in list of base amounts
	 */
	boolean populatePriceFromBaseAmounts(final Collection<BaseAmount> amounts, final Currency currency, final Price price);

	/**
	 * Populate a {@link PriceTier} from the given {@link BaseAmount}.
	 * 
	 * The priceListDescriptorGuid of the base amounts price list will also be set into the price tier.
	 *
	 * @param amount the base amount
	 * @return the price tier
	 */
	PriceTier populatePriceTierFromBaseAmount(final BaseAmount amount);
	
	
}
