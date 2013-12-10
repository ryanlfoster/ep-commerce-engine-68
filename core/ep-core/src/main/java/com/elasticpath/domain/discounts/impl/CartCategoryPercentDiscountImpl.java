package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.discounts.TotallingApplier;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.rules.PromotionRuleExceptions;

/**
 * Applies a discount percent to cart items with product in a category, with the assistance of the totalling applier,
 * as long as the product is not in the exceptions list.
 */
public class CartCategoryPercentDiscountImpl extends AbstractDiscountImpl {

	private static final long serialVersionUID = 1L;

	private final String percent;
	private final String exceptionStr;
	private final String compoundCategoryGuid;

	/**
	 * @param ruleElementType rule element type.
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the uid of the action
	 * @param compoundCategoryGuid the compound guid of the category based on catalog code and category code
	 * @param percent the percentage of the promotion X 100 (e.g. 50 means 50% off).
	 * @param exceptions exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @param availableDiscountQuantity the number of items that can be discounted. Set to zero for unlimited.
	 */
	public CartCategoryPercentDiscountImpl(final String ruleElementType,
			final long ruleId, final long actionId, final String compoundCategoryGuid, final String percent,
			final String exceptions, final int availableDiscountQuantity) {
		super(ruleElementType, ruleId, actionId, availableDiscountQuantity);
		this.percent = percent;
		exceptionStr = exceptions;
		this.compoundCategoryGuid = compoundCategoryGuid;
	}

	/**
	 * Apply discount when actuallyApply is true, and return total discount amount.
	 * @param actuallyApply true if actually apply discount.
	 * @param discountItemContainer discountItemContainer that passed in.
	 * @return total discount amount of this rule action.
	 */
	@Override
	public BigDecimal doApply(final boolean actuallyApply, final DiscountItemContainer discountItemContainer) {
		BigDecimal discountPercent = new BigDecimal(percent);
		discountPercent = discountPercent.setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(PERCENT_DIVISOR), BigDecimal.ROUND_HALF_UP);
		final TotallingApplier applier = getTotallingApplier(actuallyApply, discountItemContainer, getRuleId());

		final PromotionRuleExceptions promotionRuleExceptions = getPromotionRuleExceptions(exceptionStr);
		for (final ShoppingItem currCartItem : discountItemContainer.getItemsLowestToHighestPrice()) {
			if (cartItemIsEligibleForPromotion(discountItemContainer, currCartItem, compoundCategoryGuid, promotionRuleExceptions)) {
				final BigDecimal itemPrice = getItemPrice(discountItemContainer, currCartItem);
				final BigDecimal discount = itemPrice.multiply(discountPercent);
				applier.apply(currCartItem, discount);
			}
		}
		return applier.getTotalDiscount();

	}

	/**
	 * Checks if the cart item is eligible for apply promotion.
	 * @param discountItemContainer discountItemContainer that passed in.
	 * @param cartItem cart item of the shopping cart.
	 * @param compoundCategoryGuid input compound category guid.
	 * @param promotionRuleExceptions exclude exceptions.
	 * @return true if eligible for promotion.
	 */
	protected boolean cartItemIsEligibleForPromotion(
			final DiscountItemContainer discountItemContainer, final ShoppingItem cartItem,
			final String compoundCategoryGuid,
			final PromotionRuleExceptions promotionRuleExceptions) {
		return catalogProductInCategory(cartItem.getProductSku().getProduct(), true, compoundCategoryGuid, promotionRuleExceptions)
		&& (!promotionRuleExceptions.isSkuExcluded(cartItem.getProductSku()));
	}

	/**
	 * Checks if the given product is in the category with the specified categoryID.
	 * 
	 * @param product the product
	 * @param isIn set to true to specify that the product is in the category, false to require that it isn't in the category
	 * @param compoundCategoryGuid input compound category guid
	 * @param promotionRuleExceptions exclusions to the promotion.
	 * @return true if the product is in the category or one of its children.
	 */
	public boolean catalogProductInCategory(final Product product,
			final boolean isIn, final String compoundCategoryGuid,
			final PromotionRuleExceptions promotionRuleExceptions) {
		boolean isInCategoryAndNotExcluded = false;
		isInCategoryAndNotExcluded = product.isInCategory(compoundCategoryGuid) && !promotionRuleExceptions.isProductExcluded(product);

		if (!isIn) {
			return !isInCategoryAndNotExcluded;
		}
		return isInCategoryAndNotExcluded;
	}
}
