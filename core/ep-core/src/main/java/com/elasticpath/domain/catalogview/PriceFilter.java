package com.elasticpath.domain.catalogview;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Represents a filter on product price. Price is represented as a {@link BigDecimal}
 * implementation of {@Link RangeFilter}.
 */
public interface PriceFilter extends RangeFilter<PriceFilter, BigDecimal> {

	/** property key for the currency property. */
	String CURRENCY_PROPERTY = "currency";

	/** . property key for the seoId of the filter.*/
	String ALIAS_PROPERTY = "alias";
	
	/**
	 * Returns the currency of this price filter.
	 *
	 * @return the currency of this price filter.
	 */
	Currency getCurrency();

	/**
	 * Set the currency of this price filter.
	 *
	 * @param currency the currency.
	 */
	void setCurrency(Currency currency);
}
