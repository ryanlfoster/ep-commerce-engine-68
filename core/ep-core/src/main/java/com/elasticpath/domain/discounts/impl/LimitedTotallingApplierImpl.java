package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.discounts.TotallingApplier;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Applies discounts to a cart items and totals all the discounts applied.  If a none-zero maxItems value is initialized, then
 * only the discount will only be applied to a maximum of maxItems cart items.
 */
public class LimitedTotallingApplierImpl implements TotallingApplier {

	/**
	 *  the max items can apply discount. 
	 */
	private int maxItems;
	
	/**
	 *  how many items can apply discount since previously applied.
	 *  if remainingItemsToDiscount < cart item quantities, only can apply remainingItemsToDiscount.    
	 */
	private int remainingItemsToDiscount;
	
	private boolean actuallyApply;
	
	private BigDecimal totalDiscount = BigDecimal.ZERO;

	private DiscountItemContainer discountItemContainer;

	private long ruleId;

	private long actionId;
		
	/**
	 * Apply discount for cart item using given discount amount.
	 * @param cartItem the cart item within the shopping cart that apply promotion.
	 * @param discountAmount the given discount amount.
	 */
	public void apply(final ShoppingItem cartItem, final BigDecimal discountAmount) {
		int cartItemQuantity = cartItem.getQuantity();

		// Check if there's a limit on the number of items to discount
		if (maxItems > 0) {
			if (remainingItemsToDiscount >= cartItemQuantity) {
				apply(cartItem, discountAmount, cartItemQuantity);
				remainingItemsToDiscount -= cartItemQuantity;
			} else {
				apply(cartItem, discountAmount, remainingItemsToDiscount);
				remainingItemsToDiscount = 0;
			}
		} else {
			// There's no limit on the number of items to discount
			apply(cartItem, discountAmount, cartItemQuantity);
		}
	}
	
	/**
	 *  Calculate the shopping cart total discount amount by applying promotions.  
	 *  @return total discount amount.
	 */
	public BigDecimal getTotalDiscount() {
		return totalDiscount;
	}

	/**
	 * Apply the given discount amount and return discount amount of the specific cart item.
	 * @param cartItem the cart item within the shopping cart that apply promotion.
	 * @param amount the given discount amount.
	 * @param quantityToDiscount The number of items to discount.
	 */
	protected void doApply(final ShoppingItem cartItem, final BigDecimal amount, final int quantityToDiscount) {

		if (actuallyApply) {
			cartItem.applyDiscount(amount);
			recordRuleApplied(cartItem, amount, quantityToDiscount);
		}
		totalDiscount = totalDiscount.add(amount);
	}
	
	/**
	 * Record rule id when actually apply discount.
	 * @param discountedItem The item that was discounted. Null if the subtotal was discounted.
	 * @param discountAmount The amount, not percent, of the discount.
	 * @param quantityAppliedTo The item quantity which the discount was applied to.
	 */
	protected void recordRuleApplied(
			final ShoppingItem discountedItem, final BigDecimal discountAmount, final int quantityAppliedTo) {
		discountItemContainer.recordRuleApplied(ruleId, actionId, discountedItem, discountAmount, quantityAppliedTo);
	}
	
	/**
	 * Apply given discount amount with N quantities of cart item. 
	 * @param cartItem specific cart item of shopping cart.
	 * @param amount the given discount amount.
	 * @param quantityToDiscount quantities for apply discount.
	 */
	public void apply(final ShoppingItem cartItem, final BigDecimal amount, final int quantityToDiscount) {
		doApply(cartItem, amount.multiply(BigDecimal.valueOf(quantityToDiscount)), quantityToDiscount);
	}

	/**
	 * Set whether to actually the discount or not.
	 * @param actuallyApply the actuallyApply to set
	 */
	public void setActuallyApply(final boolean actuallyApply) {
		this.actuallyApply = actuallyApply;
	}

	/**
	 * Set the max items to discount.  Also, set the initial count of items to discount.
	 * @param maxItems the maxItems to set
	 */
	public void initializeMaxItems(final int maxItems) {
		this.maxItems = maxItems;
		this.remainingItemsToDiscount = maxItems;
	}

	/**
	 * @param discountItemContainer The discount item container.
	 */
	public void setDiscountItemContainer(
			final DiscountItemContainer discountItemContainer) {
		this.discountItemContainer = discountItemContainer;
	}

	/**
	 * @param ruleId the rule id.
	 */
	public void setRuleId(final long ruleId) {
		this.ruleId = ruleId;
	}

	/**
	 * @param actionId the action id.
	 */
	public void setActionId(final long actionId) {
		this.actionId = actionId;		
	}
}
