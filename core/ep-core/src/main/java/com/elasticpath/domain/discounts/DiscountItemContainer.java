/**
 * 
 */
package com.elasticpath.domain.discounts;

import java.math.BigDecimal;
import java.util.List;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * A discount item container where the discounts can be applied to.
 * It also provides helper methods to get cart items and calculate subtotal for discount calculation.   
 */
public interface DiscountItemContainer {
	
	/**
	 * Record rule id that applied discount. 
	 * @param ruleId id of applied discount.
	 * @param actionId the action id
	 * @param discountedItem The item that was discounted or null if applied to the subtotal.
	 * @param discountAmount The amount, not percent, of the discount.
	 * @param quantityAppliedTo The item quantity which the discount was applied to
	 */
	void recordRuleApplied(long ruleId, long actionId, ShoppingItem discountedItem, BigDecimal discountAmount, int quantityAppliedTo);
	
	/**
	 * Sort the cart Items in discount item container from lowest to highest price.
	 * @return list of cart items.
	 */
	List<ShoppingItem> getItemsLowestToHighestPrice();
	
	/**
	 * Get the discount item container's catalog. 
	 * @return catalog.
	 */
	Catalog getCatalog();
	
	/**
	 * Gets the calculated price amount for the <code>ShoppingItem</code>.
	 * 
	 * @param cartItem cartItem holds price, discount and quantity info.
	 * 
	 * @return the calculated price amount.
	 */
	BigDecimal getPriceAmount(ShoppingItem cartItem);
	
	/**
	 * Get cart item product code.
	 * @param cartItem cart item in the discount item container.
	 * @return the product code of cart item.
	 */
	String getCartItemProductCode(final ShoppingItem cartItem);
	
	/**
	 * Get cart item product sku code.
	 * @param cartItem cart item in the discount item container.
	 * @return the product sku code of cart item.
	 */
	String getCartItemSkuCode(final ShoppingItem cartItem);
	
	/**
	 * Find the specific sku and create N sku quantities in cart item.
	 * @param skuCode the given skuCode.
	 * @param numItems create N items based on the given sku code.
	 * @return cart items with N quantities. Return null if shopping item is out of stock.
	 * to create a cart item and then add it to the shopping cart.
	 */
	ShoppingItem addCartItem(final String skuCode, final int numItems);
	
	/**
	 * Calculates the subtotal of the cart minus the amount of all the items representing a gift certificate. This is required so that we do not
	 * apply promotions on gift certificates in the cart.
	 *
	 * @param discountAmount the amount to discount the subtotal by as a BigInteger
	 * @param ruleId The id for the rule.
	 * @param actionId The id for the action.
	 */
	void applySubtotalDiscount(final BigDecimal discountAmount, long ruleId, long actionId);
	
	/**
	 * Calculates the discount item container subtotal.
	 *
	 * @return the subtotal
	 */
	BigDecimal calculateSubtotal();
}