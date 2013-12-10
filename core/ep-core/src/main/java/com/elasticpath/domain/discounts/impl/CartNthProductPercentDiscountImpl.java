package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.discounts.TotallingApplier;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.rules.PromotionRuleExceptions;

/**
 *
 * 
 */
public class CartNthProductPercentDiscountImpl extends AbstractDiscountImpl {

	private static final long serialVersionUID = 1L;

	private final String percent;
	private final int nthItem;
	private final String exceptionStr;
	private final String productCode;

	/**
	 * @param ruleElementType rule element type.
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the uid of the action
	 * @param productCode the code of the product to be discounted
	 * @param percent the percentage of the promotion X 100 (e.g. 50 means 50% off, 100 means free).
	 * @param nthItem the number of items that must be present before one is discounted
	 * @param exceptions exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 */
	public CartNthProductPercentDiscountImpl(final String ruleElementType,
			final long ruleId, final long actionId, final String productCode, final String percent,
			final int nthItem, final String exceptions) {
		super(ruleElementType, ruleId, actionId);
		this.percent = percent;
		this.nthItem = nthItem;
		exceptionStr = exceptions;
		this.productCode = productCode;
	}

	/**
	 * Apply discount when actuallyApply is true, and return total discount amount.
	 * @param actuallyApply true if actually apply discount.
	 * @param discountItemContainer discountItemContainer that passed in.
	 * @return total discount amount of this rule action.
	 */
	@Override
	public BigDecimal doApply(final boolean actuallyApply, final DiscountItemContainer discountItemContainer) {
		int numProductItemsInCart = 0;
		BigDecimal discountPercent = new BigDecimal(percent);
		final TotallingApplier applier = getTotallingApplier(actuallyApply, discountItemContainer, getRuleId());
		discountPercent = discountPercent.setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(PERCENT_DIVISOR), BigDecimal.ROUND_HALF_UP);

		final PromotionRuleExceptions promotionRuleExceptions = getPromotionRuleExceptions(exceptionStr);
		for (final ShoppingItem currCartItem : discountItemContainer.getItemsLowestToHighestPrice()) {
			if (cartItemIsEligibleForPromotion(discountItemContainer, currCartItem, productCode, promotionRuleExceptions)) {
				final int itemQuantity = currCartItem.getQuantity();
				numProductItemsInCart += itemQuantity;
				if (numProductItemsInCart >= nthItem) {
					final BigDecimal itemPrice = getItemPrice(discountItemContainer, currCartItem);
					final BigDecimal discount = itemPrice.multiply(discountPercent);
					applier.apply(currCartItem, discount, numProductItemsInCart / nthItem);
					numProductItemsInCart = numProductItemsInCart % nthItem;
				}
			}
		}
		return applier.getTotalDiscount();

	}
	/**
	 * Checks if the cart item is eligible for apply promotion.
	 * @param discountItemContainer discountItemContainer that passed in.
	 * @param cartItem cart item of the shopping cart.
	 * @param productCode input product code.
	 * @param promotionRuleExceptions exclusions to the promotion.
	 * @return true if eligible for promotion.
	 */
	protected boolean cartItemIsEligibleForPromotion(
			final DiscountItemContainer discountItemContainer, final ShoppingItem cartItem,
			final String productCode,
			final PromotionRuleExceptions promotionRuleExceptions) {
		return ("0".equals(productCode) || discountItemContainer.getCartItemProductCode(cartItem).equals(productCode))
		&& (!promotionRuleExceptions.isSkuExcluded(cartItem.getProductSku()));
	}
}
