package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.discounts.TotallingApplier;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 *
 * 
 */
public class CartNFreeSkusDiscountImpl extends AbstractDiscountImpl {

	private static final long serialVersionUID = 1L;

	private final String skuCode;

	/**
	 * @param ruleElementType rule element type.
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the uid of the action
	 * @param skuCode the sku code of the sku to be added to the cart
	 * @param availableDiscountQuantity the number of items that can be discounted
	 */
	public CartNFreeSkusDiscountImpl(final String ruleElementType,
			final long ruleId, final long actionId, final String skuCode, final int availableDiscountQuantity) {
		super(ruleElementType, ruleId, actionId, availableDiscountQuantity);
		this.skuCode = skuCode;
	}

	/**
	 * Apply discount when actuallyApply is true, and return total discount amount.
	 * @param actuallyApply true if actually apply discount.
	 * @param discountItemContainer discountItemContainer that passed in.
	 * @return total discount amount of this rule action.
	 */
	@Override
	public BigDecimal doApply(final boolean actuallyApply, final DiscountItemContainer discountItemContainer) {
		final TotallingApplier applier = getTotallingApplier(actuallyApply, discountItemContainer, getRuleId());
		final ShoppingItem cartItem = discountItemContainer.addCartItem(skuCode, getAvailableDiscountQuantity());
		if (cartItem != null) {
			final BigDecimal discount = getItemPrice(discountItemContainer, cartItem);
			applier.apply(cartItem, discount);
		}
		return applier.getTotalDiscount();
	}
}
