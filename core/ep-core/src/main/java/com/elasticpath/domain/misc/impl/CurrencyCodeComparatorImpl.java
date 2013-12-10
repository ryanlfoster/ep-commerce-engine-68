package com.elasticpath.domain.misc.impl;

import java.util.Currency;

import com.elasticpath.domain.misc.CurrencyCodeComparator;

/**
 * Default implementation of {@link CurrencyCodeComparator}.
 */
public class CurrencyCodeComparatorImpl implements CurrencyCodeComparator {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(final Currency currency1, final Currency currency2) {
		return currency1.getCurrencyCode().compareTo(currency2.getCurrencyCode());
	}

}
