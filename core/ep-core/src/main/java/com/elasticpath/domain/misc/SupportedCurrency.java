package com.elasticpath.domain.misc;

import java.util.Currency;

import com.elasticpath.persistence.api.Persistable;


/**
 * The interface for supported currency.
 */
public interface SupportedCurrency extends Persistable {

	/**
	 * Get the currency.
	 * @return the Currency
	 */
	Currency getCurrency();

	/**
	 * Set the currency.
	 * @param currency the currency to set
	 */
	void setCurrency(final Currency currency);

}