/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.tax;

import java.util.Collection;
import java.util.Currency;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.EpService;

/**
 * A service that will calculate the applicable taxes for a <code>ShoppingCart</code>.
 */
public interface TaxCalculationService extends EpService {
	
	/**
	 * Calculates the applicable taxes on a list of items, depending on the address to which they are being billed or shipped.
	 * NOTICE: Only enabled is store tax jurisdictions should be used for calculating tax.
	 * 
	 *
	 * @param storeCode guid of the store that will be used to retrieve tax jurisdictions
	 * @param address the address to use for tax calculations
	 * @param currency the currency to use for tax calculations
	 * @param shippingCost the cost of shipping, so that shipping taxes can be factored in
	 * @param shoppingItems list of items that must be taxed
	 * @param preTaxDiscount the total pre-tax discount to be applied on items, before taxes are calculated
	 * @return the result of the tax calculations
	 */
	TaxCalculationResult calculateTaxes(
			final String storeCode,
			final Address address, 
			final Currency currency, 
			final Money shippingCost,
			final Collection< ? extends ShoppingItem> shoppingItems,
			final Money preTaxDiscount);
	
	/**
	 * Calculates the applicable taxes on a list of items, depending on the address to which they are being billed or shipped.
	 * NOTICE: Only enabled is store tax jurisdictions should be used for calculating tax.
	 *
	 *
	 * @param taxCalculationResult the tax calculation result to be used to add up the taxes to
	 * @param storeCode guid of the store that will be used to retrieve tax jurisdictions
	 * @param address the address to use for tax calculations. If null, no calculations will be performed.
	 * @param currency the currency to use for tax calculations, must be non-null
	 * @param shippingCost the cost of shipping, so that shipping taxes can be factored in, must be non-null
	 * @param shoppingItems list of items that must be taxed, must be non-null
	 * @param preTaxDiscount the total pre-tax discount to be applied on items, before taxes are calculated, must be non-null
	 * @return the result of the tax calculations
	 */
	TaxCalculationResult calculateTaxesAndAddToResult(
			final TaxCalculationResult taxCalculationResult, 
			final String storeCode,
			final Address address, 
			final Currency currency, 
			final Money shippingCost, 
			final Collection< ? extends ShoppingItem> shoppingItems, 
			final Money preTaxDiscount);

	
	/**
	 * Checks if that address is in a tax jurisdiction where tax inclusive method is used.
	 * NOTICE: Only tax jurisdictions that is enabled is store will be considered.
	 *
	 * @param storeCode guid of the store that will be used to retrieve tax jurisdictions.
	 * @param address the address to be checked
	 * @return true if inclusive tax method is used
	 */
	boolean isInclusiveTaxCalculationInUse(String storeCode, Address address);
}
