package com.elasticpath.domain.shoppingcart.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.impl.CartNthProductPercentDiscountActionImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Records the details of a discount. Used as the value in the RuleDiscount map.
 */
public class ItemDiscountRecordImpl extends AbstractDiscountRecordImpl {
	
	/** Serial Version ID. **/
	private static final long serialVersionUID = 5000000001L;

	private final ShoppingItem shoppingItem;
	private int quantityAppliedTo;
	
	/**
	 * Default constructor.
	 * 
	 * @param shoppingItem The shopping item that was discount.
	 * @param discountAmount The dollar amount of the discount.
	 * @param quantityAppliedTo The number of items the discount was applied to.
	 */
	public ItemDiscountRecordImpl(final ShoppingItem shoppingItem, final BigDecimal discountAmount, final int quantityAppliedTo) {
		super(discountAmount);
		this.shoppingItem = shoppingItem;
		this.quantityAppliedTo = quantityAppliedTo;
	}
	
	/**
	 * 
	 * @return The shopping item.
	 */
	ShoppingItem getShoppingItem() {
		return shoppingItem;
	}
	
	/**
	 * 
	 * @return The number of items the discount applies to.
	 */
	public int getQuantityAppliedTo() {
		return quantityAppliedTo;
	}

	/**
	 * 
	 * @param quantityAppliedTo The number of items the discount applies to.
	 */
	public void setQuantityAppliedTo(final int quantityAppliedTo) {
		this.quantityAppliedTo = quantityAppliedTo;
	}

	@Override
	public int getCouponUsesRequired(final RuleAction action, final ShoppingCart shoppingCart) {
		if (isSuperceded()) {
			return 0;
		}
		// This is a discount applied to an item but it only counts as one
		// coupon use.
		if (onlyOneCouponUseRequired(action)) {
			return 1;
		}
		double quantityAppliedTo = getQuantityAppliedTo();			
		BigDecimal couponUsesRequiredForActionBD = new BigDecimal(quantityAppliedTo / action.getDiscountQuantityPerCoupon());
		
		// Scale to next positive integer.
		return couponUsesRequiredForActionBD.setScale(0, BigDecimal.ROUND_CEILING).intValue();
	}
	
	/**
	 * 
	 * @param action The rule action to use to determine the result.
	 * @return True if this action only requires one coupon use regardless of the number of items in the cart.
	 */
	protected boolean onlyOneCouponUseRequired(final RuleAction action) {
		return action instanceof CartNthProductPercentDiscountActionImpl;
	}
}