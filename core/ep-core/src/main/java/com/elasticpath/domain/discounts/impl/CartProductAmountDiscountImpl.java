package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.discounts.TotallingApplier;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.rules.PromotionRuleExceptions;

/**
 * Applies a discount amount to cart items with a specific product code, with the assistance of the totalling applier,
 * as long as the product sku is not in the exceptions list.
 */
public class CartProductAmountDiscountImpl extends AbstractDiscountImpl {

	private static final long serialVersionUID = 1L;

	private final String amount;
	private final String exceptionStr;
	private final String productCode;

	/**
	 * @param ruleElementType the rule element type.
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the uid of the action
	 * @param productCode the code of the product to be discounted
	 * @param amount the amount of the discount
	 * @param exceptions exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @param availableDiscountQuantity the number of items that can be discounted. Set to zero for unlimited.
	 */
	public CartProductAmountDiscountImpl(final String ruleElementType,
			final long ruleId, final long actionId, final String productCode, final String amount,
			final String exceptions, final int availableDiscountQuantity) {
		super(ruleElementType, ruleId, actionId, availableDiscountQuantity);
		this.amount = amount;
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
		final BigDecimal discountAmount = new BigDecimal(amount);
		final TotallingApplier applier = getTotallingApplier(actuallyApply, discountItemContainer, getRuleId());
		final PromotionRuleExceptions promotionRuleExceptions = getPromotionRuleExceptions(exceptionStr);

		for (final ShoppingItem currCartItem : discountItemContainer.getItemsLowestToHighestPrice()) {
			if (cartItemIsEligibleForPromotion(discountItemContainer, currCartItem, productCode, promotionRuleExceptions)) {
				applier.apply(currCartItem, discountAmount);
			}
		}
		return applier.getTotalDiscount();
	}

	/**
	 * Checks if the cart item is eligible for apply promotion.
	 * @param discountItemContainer discountItemContainer that passed in.
	 * @param cartItem cart item of the shopping cart.
	 * @param productCode input product code.
	 * @param promotionRuleExceptions the exclusions to the promotion.
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
