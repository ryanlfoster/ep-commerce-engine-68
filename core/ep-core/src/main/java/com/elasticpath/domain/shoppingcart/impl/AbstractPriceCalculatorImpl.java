/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.shoppingcart.PriceCalculator;

/**
 * Abstract class for handling common portions of PriceCalculator classes.
 */
public abstract class AbstractPriceCalculatorImpl implements PriceCalculator {

	private boolean includeCartDiscounts = false;
	private TaxHandlingEnum taxHandling = TaxHandlingEnum.USE_SITE_DEFAULTS;
	
	/**
	 * Enum for determining how taxes should be handled.
	 */
	protected enum TaxHandlingEnum {
		/** Get the tax inclusive/exclusive from the site.*/
		USE_SITE_DEFAULTS,
		/** included tax in the price. */
		INCLUDE,
		/** tax is not included in price.*/
		EXCLUDE
	}
	
	@Override
	public PriceCalculator withCartDiscounts() {
		includeCartDiscounts = true;
		return this;
	}

	@Override
	public PriceCalculator withTaxes() {
		taxHandling = TaxHandlingEnum.INCLUDE;
		return this;
	}

	@Override
	public PriceCalculator withoutTaxes() {
		taxHandling = TaxHandlingEnum.EXCLUDE;
		return this;
	}

	@Override
	public abstract BigDecimal getAmount();
	
	protected boolean isIncludeCartDiscounts() {
		return includeCartDiscounts;
	}

	protected TaxHandlingEnum getTaxHandling() {
		return taxHandling;
	}
}

