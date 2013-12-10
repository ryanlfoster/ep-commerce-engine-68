package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.discounts.TotallingApplier;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.rules.PromotionRuleExceptions;

/**
 * Applies a discount percent to any cart item, with the assistance of the totalling applier,
 * as long as the sku is not in the exceptions list.
 */
public class CartAnySkuPercentDiscountImpl extends AbstractDiscountImpl {

	private static final long serialVersionUID = 1L;

	private final String percent;
	private final String exceptionStr;

	/**
	 * Constructor, instantiate the discount object.
	 * @param ruleElementType rule element type.
	 * @param ruleId ruleId the id of the rule executing this action.
	 * @param actionId the uid of the action
	 * @param percent the percentage of the promotion X 100 (e.g. 50 means 50% off, 100 means free).
	 * @param exceptions exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @param availableDiscountQuantity the number of items that can be discounted
	 */
	public CartAnySkuPercentDiscountImpl(final String ruleElementType, final long ruleId, final long actionId, final String percent,
			final String exceptions, final int availableDiscountQuantity) {
		super(ruleElementType, ruleId, actionId, availableDiscountQuantity);
		this.percent = percent;
		exceptionStr = exceptions;
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
		final PromotionRuleExceptions exceptions = getPromotionRuleExceptions(exceptionStr);

		for (final ShoppingItem currCartItem : discountItemContainer.getItemsLowestToHighestPrice()) {
			if (cartItemIsEligibleForPromotion(currCartItem, discountItemContainer.getCatalog(), exceptions)) {

				final BigDecimal itemPrice = getItemPrice(discountItemContainer, currCartItem);
				final BigDecimal discount = itemPrice.multiply(discountPercent);
				applier.apply(currCartItem, discount);
			}
		}
		return applier.getTotalDiscount();
	}

	/**
	 * Checks whether a CartItem is eligible for a promotion given exceptions to the promotion rule and the catalog in which the promotion is being
	 * applied.
	 * 
	 * @param cartItem the cart item in question
	 * @param catalog the catalog in which the promotion is being applied
	 * @param promotionRuleExceptions the exclusions to the promotion
	 * @return true if the CartItem is eligible for a promotion with the given exceptions, false if not
	 */
	protected boolean cartItemIsEligibleForPromotion(final ShoppingItem cartItem,
			final Catalog catalog,
			final PromotionRuleExceptions promotionRuleExceptions) {
		if (!promotionRuleExceptions.isSkuExcluded(cartItem.getProductSku())
				&& !promotionRuleExceptions.isProductExcluded(cartItem
						.getProductSku().getProduct())
						&& !productIsInCategoryExcludedFromPromotion(cartItem
								.getProductSku().getProduct(), catalog,
								promotionRuleExceptions)) {
			return true;
		}
		return false;
	}

	private boolean productIsInCategoryExcludedFromPromotion(final Product product, final Catalog catalog, final PromotionRuleExceptions exceptions) {
		for (final Category category : product.getCategories(catalog)) {
			if (exceptions.isCategoryExcluded(category)) {
				return true;
			}
		}
		return false;
	}
}
