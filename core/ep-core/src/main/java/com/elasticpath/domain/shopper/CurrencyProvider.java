package com.elasticpath.domain.shopper;

import java.util.Currency;

/**
 * Provides a {@link Currency}.
 */
public interface CurrencyProvider {

	/**
	 * Gets the {@link Currency}.
	 *
	 * @return a {@link Currency}.
	 */
	Currency getCurrency();
	
}
