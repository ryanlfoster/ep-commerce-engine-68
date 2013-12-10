package com.elasticpath.domain.shoppingcart;

import com.elasticpath.domain.rules.RuleAction;

/**
 * Represents a discount applied to a cart. Discounts can either be per item (use {ItemDiscountRecordImpl}), 
 * for a subtotal (use {SubtotalDiscountRecordImpl}) or for shipping (use {ShippingDiscountRecordImpl}).
 */
public interface DiscountRecord {

	/**
	 * Calculates the number of coupon uses required for this discount to have been
	 * created by {@code action}.
	 * 
	 * @param action The action.
	 * @param shoppingCart the cart.
	 * @return The number of coupon uses required.
	 */
	int getCouponUsesRequired(final RuleAction action, final ShoppingCart shoppingCart);
	
	/**
	 * 
	 * @return True if the action that created this discount record was superceded or overriden by another action.
	 * e.g. Shipping discounts supercede previous discounts based on which one was applied last.
	 * Superceded discount records should not decrease coupon usage.
	 */
	boolean isSuperceded();

}