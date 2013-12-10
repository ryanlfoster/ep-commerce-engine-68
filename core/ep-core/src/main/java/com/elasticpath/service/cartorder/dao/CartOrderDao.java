/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.service.cartorder.dao;

import java.util.List;

import com.elasticpath.domain.cartorder.CartOrder;

/**
 * The CartOrder DAO interface, CartOrder should not be used in versions of EP prior to 6.4.
 */
public interface CartOrderDao {

	/**
	 * @param uid The CartOrder uidpk.
	 * @return The CartOrder.
	 */
	CartOrder get(long uid);

	/**
	 * @param guid The CartOrder GUID.
	 * @return The CartOrder.
	 */
	CartOrder findByGuid(String guid);

	/**
	 * Finds a {@link CartOrder} given the shopping cart guid.
	 *
	 * @param guid the guid
	 * @return the cart order
	 */
	CartOrder findByShoppingCartGuid(String guid);

	/**
	 * @param cartOrder The CartOrder to save or update.
	 * @return The saved/updated CartOrder.
	 */
	CartOrder saveOrUpdate(CartOrder cartOrder);

	/**
	 * @param cartOrder The cartOrder.
	 */
	void remove(CartOrder cartOrder);

	/**
	 * Removes the cart order related to the shopping cart with the given GUID.
	 *
	 * @param shoppingCartGuid the shopping cart GUID
	 */
	void removeByShoppingCartGuid(final String shoppingCartGuid);

	/**
	 * Removes the cart orders corresponding to the associated shopping cart GUIDs.
	 *
	 * @param shoppingCartGuids the shopping cart GUIDs
	 * @return the the number of cart orders that were removed
	 */
	int removeByShoppingCartGuids(List<String> shoppingCartGuids);


	/**
	 * Find the GUIDs of all the cart orders owned by a customer in a certain store, given customer's GUID and store code.
	 * @param storeCode the store code
	 * @param customerGuid the customer GUID
	 *
	 * @return the list of cart order GUIDs
	 */
	List<String> findCartOrderGuidsByCustomerGuid(String storeCode, String customerGuid);
}
