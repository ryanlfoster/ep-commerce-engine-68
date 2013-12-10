/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.shoppingcart;

import java.util.List;

import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.persistence.api.Entity;

/**
 * Represents a shopping list - i.e. a collection of {@link ShoppingItem}.
 */
public interface ShoppingList extends Entity {

	/**
	 * Get the {@link Shopper} that owns this list.
	 * 
	 * @return the {@link Shopper}
	 */
	Shopper getShopper();
	
	/**
	 * Set {@link Shopper} that owns this list.
	 * 
	 * @param shopper the {@link Shopper}.
	 */
	void setShopper(final Shopper shopper);
	
	/**
	 * Get all the items in the list.
	 * 
	 * @return all the items in the shopping list
	 */
	List<ShoppingItem> getAllItems();

	/**
	 * Set the items in the list after loading a saved list from the database.
	 * 
	 * @param allItems the allItems to set
	 */
	void setAllItems(final List<ShoppingItem> allItems);

}
