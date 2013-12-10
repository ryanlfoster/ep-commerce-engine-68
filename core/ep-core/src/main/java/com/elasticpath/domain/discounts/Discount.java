/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.discounts;

import java.math.BigDecimal;

import com.elasticpath.domain.EpDomainException;

/**
 * A <code>Discount</code> allows the calculation and application of a discount towards the discount item container.
 * Implementations of this class are different types of discounts, e.g. a discount amount or percent, based on a specific product code or sku code.
 */
public interface Discount {

	/**
	 * Apply a discount amount to all items in the cart when isActualApply is true.
	 *        otherwise only calculate subtotal discount of the cart.
	 * @param discountItemContainer the discountItemContainer to applied discount.
	 * @throws EpDomainException EpDomainException.
	 * @return the total discount of the discountItemContainer. 
	 */
	BigDecimal apply(final DiscountItemContainer discountItemContainer) throws EpDomainException;

	/**
	 * Calculate discount amount to all items in the cart.
	 * @param discountItemContainer the discountItemContainer to apply discounted.
	 * @return the total discount of the discountItemContainer.
	 * @throws EpDomainException EpDomainException.
	 */
	BigDecimal calculate(final DiscountItemContainer discountItemContainer) throws EpDomainException;

	/**
	 * Get the rule element type.
	 * @return rule element type name.
	 */
	String getRuleElementType();

}