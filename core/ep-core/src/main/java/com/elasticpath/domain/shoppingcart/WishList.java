/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.shoppingcart;

import java.util.List;

/**
 * Represents a wish list.
 */
public interface WishList extends ShoppingList {

	/**
	 * Add item into wish list.
	 * 
	 * @param item the item to be added
	 * @return the shopping item added
	 */
	ShoppingItem addItem(final ShoppingItem item);

	/**
	 * Add all items in the wish list.
	 * 
	 * @param allItems the collection of items to add
	 */
	void addAllItems(List<ShoppingItem> allItems);
	
	/**
	 * Remove item from wish list by sku code.
	 * 
	 * @param skuCode the sku code of the item to be removed
	 */
	void removeItem(String skuCode);
	
	/**
	 * Remove item from wish list by wish list item uidpk.
	 * 
	 * @param wishListItemUid the uidpk of the wish list item
	 */
	void removeItem(long wishListItemUid);
	
	/**
	 * Gets the {@link Store} code for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	String getStoreCode();
	
	/**
	 * Sets the {@Store} code for this domain model object.
	 * 
	 * @param storeCode the new storeCode.
	 */
	void setStoreCode(final String storeCode);
	
}
