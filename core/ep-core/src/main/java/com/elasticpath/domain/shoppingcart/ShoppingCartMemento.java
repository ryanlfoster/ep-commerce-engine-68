/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.shoppingcart;

import com.elasticpath.domain.DatabaseLastModifiedDate;

/**
 * <code>ShoppingCartMemento</code> represents the persistable portion of
 * a <code>Customer</code>'s shopping cart.
 */
public interface ShoppingCartMemento extends ShoppingList, DatabaseLastModifiedDate {

	/**
	 * Gets the uid of the {@link com.elasticpath.domain.shopper.Shopper}.
	 * @return the {@link com.elasticpath.domain.shopper.Shopper} uid
	 */
	long getShopperUid();

	/**
	 * Gets the unique store code for this shopping cart's {@link com.elasticpath.domain.store.Store}.
	 * @return the store's code
	 */
	String getStoreCode();

	/**
	 * Sets the unique store code for this shopping cart's {@link com.elasticpath.domain.store.Store}.
	 *
	 * @param code the store code
	 */
	void setStoreCode(String code);
}
