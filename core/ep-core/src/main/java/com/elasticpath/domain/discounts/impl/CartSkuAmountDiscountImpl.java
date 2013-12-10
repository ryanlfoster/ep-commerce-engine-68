package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.discounts.TotallingApplier;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.rules.PromotionRuleExceptions;

/**
 * Applies a discount amount to cart items with a specific sku code, with the assistance of the totalling applier,
 * as long as the product sku is not in the exceptions list.
 */
public class CartSkuAmountDiscountImpl extends AbstractDiscountImpl {
	private static final long serialVersionUID = -4521436188919978640L;

	private final String amount;
	private final String exceptionStr;
	private final String skuCode;

	/**
	 * @param ruleElementType rule element type.
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the uid of the action
	 * @param skuCode the sku code of the sku to be discounted
	 * @param amount the amount of the discount
	 * @param exceptions exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @param availableDiscountQuantity the number of items that can be discounted
	 */
	public CartSkuAmountDiscountImpl(final String ruleElementType,
			final long ruleId, final long actionId, final String skuCode, final String amount,
			final String exceptions, final int availableDiscountQuantity) {
		super(ruleElementType, ruleId, actionId, availableDiscountQuantity);
		this.amount = amount;
		this.exceptionStr = exceptions;
		this.skuCode = skuCode;
	}

	/**
	 * Apply discount when actuallyApply is true, and return total discount amount. 
	 * @param actuallyApply true if actually apply discount.
	 * @param discountItemContainer discountItemContainer that passed in. 
	 * @return total discount amount of this rule action.
	 */
	public BigDecimal doApply(final boolean actuallyApply, final DiscountItemContainer discountItemContainer) {
		BigDecimal discountAmount = new BigDecimal(amount);
		TotallingApplier applier = getTotallingApplier(actuallyApply, discountItemContainer, getRuleId());
		PromotionRuleExceptions promotionRuleExceptions = getPromotionRuleExceptions(exceptionStr);
		for (ShoppingItem currCartItem : discountItemContainer.getItemsLowestToHighestPrice()) {
			if (cartItemIsEligibleForPromotion(discountItemContainer, currCartItem, skuCode, promotionRuleExceptions)) {
				applier.apply(currCartItem, discountAmount);			
			}
		}
		return applier.getTotalDiscount();		
	}
	
	/**
	 * Checks if the cart item is eligible for apply promotion.
	 * @param discountItemContainer discountItemContainer that passed in. 
	 * @param cartItem cart item of the shopping cart.
	 * @param skuCode input skuCode code.
	 * @param promotionRuleExceptions exclusions to the promotion.
	 * @return true if eligible for promotion.
	 */
	protected boolean cartItemIsEligibleForPromotion(
			final DiscountItemContainer discountItemContainer, final ShoppingItem cartItem,
			final String skuCode,
			final PromotionRuleExceptions promotionRuleExceptions) {
		return discountItemContainer.getCartItemSkuCode(cartItem).equals(skuCode) 
					&& (!promotionRuleExceptions.isSkuExcluded(cartItem.getProductSku()));
	}
}
