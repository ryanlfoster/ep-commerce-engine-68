/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.discounts;

import java.math.BigDecimal;

import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * A <code>TotallingApplier</code> applies discounts to a cart items and totals all the discounts for each apply() method called.
 * applied.
 */
public interface TotallingApplier {

	/**
	 * Apply given discount amount with N quantities of cart item. 
	 * @param cartItem specific cart item of shopping cart.
	 * @param amount the given discount amount.
	 * @param quantityToDiscount quantities for apply discount.
	 */
	void apply(final ShoppingItem cartItem, final BigDecimal amount, final int quantityToDiscount);

	/**
	 * Apply discount for cart item using given discount amount.
	 * @param cartItem the cart item within the shopping cart that apply promotion.
	 * @param discountAmount the given discount amount.
	 */
	void apply(final ShoppingItem cartItem, final BigDecimal discountAmount);
	
	/**
	 *  Calculate the shopping cart total discount amount by applying promotions.  
	 *  @return total discount amount.
	 */
	BigDecimal getTotalDiscount();
	
	/**
	 *
	 * @param actuallyApply the actuallyApply to set
	 */
	void setActuallyApply(final boolean actuallyApply);
	
	/**
	 *
	 * @param maxItems the maxItems to set
	 */
	void initializeMaxItems(final int maxItems);

	/**
	 * 
	 * @param discountItemContainer The discount item container.
	 */
	void setDiscountItemContainer(DiscountItemContainer discountItemContainer);

	/**
	 * 
	 * @param ruleId the rule id
	 */
	void setRuleId(long ruleId);

	/**
	 * 
	 * @param actionId The action id
	 */
	void setActionId(long actionId);
}
