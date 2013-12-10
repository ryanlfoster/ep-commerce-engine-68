package com.elasticpath.domain.shoppingcart;

import java.math.BigDecimal;

import com.elasticpath.domain.misc.Money;

/**
 * A consistent interface for retrieving prices from shopping carts and order skus.
 * 
 * @author gdenning
 *
 */
public interface PriceCalculator {
		
	/**
	 * Include any cart discounts when calculating the amount.
	 * @return PriceCalculator to satisfy Builder pattern.
	 */
	PriceCalculator withCartDiscounts();

	/**
	 * Include any applicable taxes when calculating the amount.
	 * If this method is not called, default behavior is to include taxes for tax inclusive sites, and exclude it for tax separate sites.
	 * @return PriceCalculator to satisfy Builder pattern.
	 */
	PriceCalculator withTaxes();

	/**
	 * Exclude any applicable taxes when calculating the amount.
	 * If this method is not called, default behavior is to include taxes for tax inclusive sites, and exclude it for tax separate sites.
	 * @return PriceCalculator to satisfy Builder pattern.
	 */
	PriceCalculator withoutTaxes();

	/**
	 * The amount calculated with any include/ignore considerations.
	 * @return calculated price
	 */
	BigDecimal getAmount();
	
	/**
	 * The Money amount calculated with any include/ignore considerations.
	 * @return calculated money
	 */
	Money getMoney();
}
