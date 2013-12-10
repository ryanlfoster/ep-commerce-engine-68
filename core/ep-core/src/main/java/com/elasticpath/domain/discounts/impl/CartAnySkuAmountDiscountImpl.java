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
 * Applies a discount amount to any cart item, with the assistance of the totalling applier,
 * as long as the sku is not in the exceptions list.
 */
public class CartAnySkuAmountDiscountImpl extends AbstractDiscountImpl {

	private static final long serialVersionUID = 1L;

	private final String amount;
	private final String exceptionStr;

	/**
	 * Constructor, instantiate the discount object.
	 * @param ruleElementType rule element type.
	 * @param ruleId ruleId the id of the rule executing this action.
	 * @param actionId the uid of the action
	 * @param amount amount the amount by which the price is to be reduced.
	 * @param exceptions exceptions to this rule element; to be used to populate the PromotionRuleExceptions.
	 * @param availableDiscountQuantity the number of items that can be discounted
	 */
	public CartAnySkuAmountDiscountImpl(final String ruleElementType,
			final long ruleId, final long actionId, final String amount,
			final String exceptions, final int availableDiscountQuantity) {
		super(ruleElementType, ruleId, actionId, availableDiscountQuantity);
		this.amount = amount;
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
		final BigDecimal discountAmount = new BigDecimal(amount);
		final TotallingApplier applier = getTotallingApplier(actuallyApply, discountItemContainer, getRuleId());

		final PromotionRuleExceptions exceptions = getPromotionRuleExceptions(exceptionStr);
		for (final ShoppingItem item : discountItemContainer.getItemsLowestToHighestPrice()) {
			if (cartItemIsEligibleForPromotion(item, discountItemContainer.getCatalog(), exceptions)) {
				applier.apply(item, discountAmount);
			}
		}
		return applier.getTotalDiscount();
	}

	/**
	 * Checks whether a {@code ShoppingItem} is eligible for a promotion given exceptions to the
	 * promotion rule and the catalog in which the promotion is being applied.
	 * @param shoppingItem the item in question
	 * @param catalog the catalog in which the promotion is being applied
	 * @param exceptions the exclusions to the promotion
	 * @return true if the item is eligible for a promotion with the given exceptions, false if not
	 */
	protected boolean cartItemIsEligibleForPromotion(final ShoppingItem shoppingItem,
			final Catalog catalog, final PromotionRuleExceptions exceptions) {
		if (!exceptions.isSkuExcluded(shoppingItem.getProductSku())
				&& !exceptions.isProductExcluded(shoppingItem.getProductSku().getProduct())
				&& !productIsInCategoryExcludedFromPromotion(shoppingItem.getProductSku().getProduct(), catalog, exceptions)) {
			return true;
		}
		return false;
	}

	private boolean productIsInCategoryExcludedFromPromotion(
			final Product product, final Catalog catalog,
			final PromotionRuleExceptions promotionRuleExceptions) {
		for (final Category category : product.getCategories(catalog)) {
			if (promotionRuleExceptions.isCategoryExcluded(category)) {
				return true;
			}
		}
		return false;
	}
}
