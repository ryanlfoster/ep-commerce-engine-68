package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.domain.discounts.DiscountItemContainer;

/**
 * Applies a discount amount to subtotal of the discount item container.
 */
public class CartSubtotalAmountDiscountImpl extends AbstractDiscountImpl {

	private static final long serialVersionUID = 1L;

	private final String amount;

	/**
	 * @param ruleElementType rule element type.
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the id of the action providing this discount
	 * @param amount the amount by which to reduce the subtotal
	 */
	public CartSubtotalAmountDiscountImpl(final String ruleElementType,
			final long ruleId, final long actionId, final String amount) {
		super(ruleElementType, ruleId, actionId);
		this.amount = amount;
	}

	/**
	 * Apply discount when actuallyApply is true, and return total discount amount. Will not apply any discounts if
	 * the discount item container does not have any items that can receive promotions.
	 * @param actuallyApply true if actually apply discount.
	 * @param discountItemContainer discountItemContainer that passed in.
	 * @return total discount amount of this rule action.
	 */
	@Override
	public BigDecimal doApply(final boolean actuallyApply, final DiscountItemContainer discountItemContainer) {
		if (CollectionUtils.isEmpty(discountItemContainer.getItemsLowestToHighestPrice())) {
			return BigDecimal.ZERO;
		}
		final BigDecimal subtotal = new BigDecimal(amount);
		if (actuallyApply) {
			discountItemContainer.applySubtotalDiscount(subtotal, getRuleId(), getActionId());
		}
		return subtotal;
	}
}
