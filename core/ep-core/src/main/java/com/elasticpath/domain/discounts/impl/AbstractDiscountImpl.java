/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.discounts.Discount;
import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.discounts.TotallingApplier;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.rules.PromotionRuleExceptions;

/**
 * A default implementation of a <code>Discount</code>.  Subclasses need to overwrite doApply method to provide
 * discount application code.
 */
public abstract class AbstractDiscountImpl extends AbstractEpDomainImpl implements Discount {
	private static final long serialVersionUID = -1730651734754617375L;

	/**
	 *  the percent divisor.
	 */
	protected static final String PERCENT_DIVISOR = "100";
	private final String ruleElementType;
	private final long ruleId;
	private long actionId;
	private int availableDiscountQuantity;

	/**
	 * @param ruleElementType rule element type.
	 * @param ruleId the id of the rule executing this action
	 */
	protected AbstractDiscountImpl(final String ruleElementType, final long ruleId) {
		this.ruleElementType = ruleElementType;
		this.ruleId = ruleId;
	}

	/**
	 * Used when the discount applies to the cart or to shipping and not to individual items.
	 *
	 * @param ruleElementType The rule element type.
	 * @param ruleId The rule id.
	 * @param actionId The action id.
	 */
	protected AbstractDiscountImpl(final String ruleElementType, final long ruleId, final long actionId) {
		this(ruleElementType, ruleId, actionId, 0);
	}

	/**
	 * @param ruleElementType rule element type.
	 * @param ruleId the id of the rule executing this action
	 * @param actionId the id of the action executing this discount
	 * @param availableDiscountQuantity The number of items available for this discount.
	 */
	protected AbstractDiscountImpl(final String ruleElementType, final long ruleId, final long actionId, final int availableDiscountQuantity) {
		this.ruleElementType = ruleElementType;
		this.ruleId = ruleId;
		this.actionId = actionId;
		this.availableDiscountQuantity = availableDiscountQuantity;
	}

	@Override
	public BigDecimal apply(final DiscountItemContainer discountItemContainer) throws EpDomainException {
		return doApply(true, discountItemContainer);
	}

	@Override
	public BigDecimal calculate(final DiscountItemContainer discountItemContainer) throws EpDomainException {
		return doApply(false, discountItemContainer);
	}

	/**
	 * Apply discount when actuallyApply is true, and return total discount amount.
	 * @param actuallyApply true if actually apply discount.
	 * @param discountItemContainer discountItemContainer that passed in.
	 * @return total discount amount of this rule action.
	 */
	protected abstract BigDecimal doApply(final boolean actuallyApply, final DiscountItemContainer discountItemContainer);

	@Override
	public String getRuleElementType() {
		return ruleElementType;
	}

	/**
	 * Get the current rule id.
	 * @return Rule id.
	 */
	public long getRuleId() {
		return ruleId;
	}

	/**
	 *
	 * @return The action id.
	 */
	protected long getActionId() {
		return actionId;
	}

	/**
	 * Gets the calculated price amount for the <code>ShoppingItem</code>
	 * using the <code>DiscountItemContainer</code>.
	 *
	 * @param cartItem cartItem holds price, discount and quantity info.
	 * @param discountItemContainer discountItemContainer the container in use.
	 * @return the calculated price amount.
	 *
	 * @see com.elasticpath.domain.discounts.DiscountItemContainer#getPriceAmount(ShoppingItem)
	 */
	protected BigDecimal getItemPrice(final DiscountItemContainer discountItemContainer, final ShoppingItem cartItem) {
		return discountItemContainer.getPriceAmount(cartItem);
	}

	/**
	 * Record rule id when actually apply discount.
	 * @param discountItemContainer discountItemContainer that is passed in.
	 * @param actuallyApply true if actually applied discount.
	 * @param ruleId id of applied discount.
	 * @param actionId The id of the applied action.
	 * @param discountedItem The item that was discounted. Null if the subtotal was discounted.
	 * @param discountAmount The amount, not percent, of the discount.
	 * @param quantityAppliedTo The item quantity which the discount was applied to.
	 */
	protected void recordRuleApplied(
			final DiscountItemContainer discountItemContainer,
			final boolean actuallyApply, final long ruleId, final long actionId,
			final ShoppingItem discountedItem, final BigDecimal discountAmount, final int quantityAppliedTo) {
		if (actuallyApply) {
			discountItemContainer.recordRuleApplied(ruleId, actionId, discountedItem, discountAmount, quantityAppliedTo);
		}
	}

	/**
	 * Get a <code>PromotionRuleException</code> object populated with the given exception string.
	 *
	 * @param exceptionStr the exception string passed in by the rule
	 * @return the populated <code>PromotionRuleExceptions</code> object
	 */
	protected PromotionRuleExceptions getPromotionRuleExceptions(final String exceptionStr) {
		PromotionRuleExceptions promotionRuleExceptions = getBean(ContextIdNames.PROMOTION_RULE_EXCEPTIONS);
		promotionRuleExceptions.populateFromExceptionStr(exceptionStr);
		return promotionRuleExceptions;
	}

	/**
	 * Get the promotion receiver.
	 * @param actuallyApply true if actually apply.
	 * @param discountItemContainer The discount item container.
	 * @param ruleId The rule id
	 * @return promotion receiver.
	 */
	protected TotallingApplier getTotallingApplier(final boolean actuallyApply,
			final DiscountItemContainer discountItemContainer,
			final long ruleId) {
		TotallingApplier applier = getBean(ContextIdNames.TOTALLING_APPLIER);
		applier.setActuallyApply(actuallyApply);
		applier.initializeMaxItems(getAvailableDiscountQuantity());
		applier.setDiscountItemContainer(discountItemContainer);
		applier.setRuleId(ruleId);
		applier.setActionId(actionId);
		return applier;
	}

	/**
	 *
	 * @return The quantity of items available for this discount.
	 */
	protected int getAvailableDiscountQuantity() {
		return availableDiscountQuantity;
	}
}
