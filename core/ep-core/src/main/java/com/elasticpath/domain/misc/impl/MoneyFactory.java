package com.elasticpath.domain.misc.impl;

import java.math.BigDecimal;
import java.util.Currency;

import com.elasticpath.domain.misc.Money;

/**
 * Static factory object that creates Money objects.  Edit this class in a feature branch if you need to override the Money implementation class.
 */
public final class MoneyFactory {
	private MoneyFactory() {
		//  Can't be instantiated
	}

	/**
	 * Create a money object.
	 *
	 * @param amount the amount of money to create
	 * @param currency the currency
	 *
	 * @return the money.
	 */
	public static Money createMoney(final BigDecimal amount, final Currency currency) {
		return new MoneyImpl(amount, currency);
	}
}
