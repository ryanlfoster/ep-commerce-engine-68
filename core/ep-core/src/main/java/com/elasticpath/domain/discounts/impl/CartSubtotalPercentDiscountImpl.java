package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.domain.discounts.DiscountItemContainer;

/**
 * Applies a discount percent to subtotal of the discount item container.
 */
public class CartSubtotalPercentDiscountImpl extends AbstractDiscountImpl {

	private static final long serialVersionUID = 1L;

	private static final int CALCULATION_SCALE = 10;
	private final String percent;

	/**
	 * @param ruleElementType rule element type.
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the id of the action providing the discount
	 * @param percent The percentage discount to apply
	 */
	public CartSubtotalPercentDiscountImpl(final String ruleElementType,
			final long ruleId, final long actionId, final String percent) {
		super(ruleElementType, ruleId, actionId);
		this.percent = percent;
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
		final BigDecimal subtotalDiscount = calculateDiscount(discountItemContainer.calculateSubtotal(), percent);
		if (actuallyApply) {
			discountItemContainer.applySubtotalDiscount(subtotalDiscount, getRuleId(), getActionId());
		}
		return subtotalDiscount;
	}

	/**
	 * Calculates a discount from an initial amount given a percentage as a string. The percentage is rounded up to two decimal places, as is the
	 * resulting discount.
	 * 
	 * @param initialAmount The initial amount
	 * @param percentage The percentage discount to apply
	 * @return the resulting discount amount
	 */
	BigDecimal calculateDiscount(final BigDecimal initialAmount, final String percentage) {
		final BigDecimal decimalPercentage =
			new BigDecimal(percentage).divide(new BigDecimal(PERCENT_DIVISOR), CALCULATION_SCALE, RoundingMode.HALF_UP);
		return initialAmount.multiply(decimalPercentage).setScale(2, RoundingMode.HALF_UP);
	}
}
