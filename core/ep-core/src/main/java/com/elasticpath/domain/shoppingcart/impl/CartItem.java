package com.elasticpath.domain.shoppingcart.impl;

import java.util.List;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * CartItem.
 */
public interface CartItem extends ShoppingItem {
	/**
	 * Sets the cart uid.
	 * 
	 * @param cartUid the cart uid
	 */
	void setCartUid(final Long cartUid);
	
	/**
	 * Gets the cart uid.
	 * 
	 * @return the cart uid
	 */
	Long getCartUid();
	
	/**
	 * Get the list of dependent items for this item.
	 *
	 * @return the dependent ShoppingItems
	 */
	List<ShoppingItem> getDependentItems();
	
	/**
	 * Check for a dependent item with the specified skuCode.
	 *
	 * @param skuCode to check for
	 * @return true if present
	 */
	boolean hasDependentItem(final String skuCode);
	
	/**
	 * Check whether this item has dependent items.
	 *
	 * @return true if there are dependent items.
	 */
	boolean hasDependentItems();
	
	/**
	 * Is the site tax inclusive?  Should be set by the cart whenever a new cart item is created.
	 * @param isTaxInclusive true if site is tax inclusive
	 */
	void setTaxInclusive(boolean isTaxInclusive);
}
