/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.service.cartorder;

import java.util.Date;
import java.util.List;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.EpPersistenceService;

/**
 * This Class can perform services for CartOrders, CartOrder should not be used in versions of EP prior to 6.4.
 */
public interface CartOrderService extends EpPersistenceService {

	/**
	 * If a CartOrder with the given GUID cannot be found then null is returned.
	 * If the referenced ShoppingCart cannot be found then null is returned.
	 *
	 * @param guid The GUID of the desired CartOrder.
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
	 * Get the billing address for the given CartOrder.
	 *
	 * @param cartOrder The CartOrder.
	 * @return The found Address or <code>null</code> if it is not set.
	 */
	Address getBillingAddress(CartOrder cartOrder);

	/**
	 * Get the shipping address for the given CartOrder.
	 *
	 * @param cartOrder The CartOrder.
	 * @return The found Address or <code>null</code> if it is not set.
	 */
	Address getShippingAddress(CartOrder cartOrder);

	/**
	 * Deleting a CartOrder will not delete its Addresses or ShoppingCart.
	 * Note: deleting an Address will not delete any CartOrders referring to it.
	 * Note: deleting a ShoppingCart will delete the CartOrder referring to it.
	 *
	 * @param cartOrder The CartOrder to remove.
	 */
	void remove(CartOrder cartOrder);

	/**
	 * Saves or updates the given CartOrder.
	 *
	 * @param cartOrder The cartOrder to save or update.
	 * @return The updated CartOrder.
	 */
	CartOrder saveOrUpdate(CartOrder cartOrder);


	@Override
	CartOrder getObject(final long uid);

	/**
	 * Creates a {@link CartOrder} for the given cart, if it does not exists already.
	 *
	 * @param cartGuid the cart GUID
	 * @return <code>true</code>, iff a new {@link CartOrder} is created
	 */
	boolean createIfNotExists(final String cartGuid);

	/**
	 * Populates the transient fields on the shopping cart using the information in the cart order.
	 *
	 * @param shoppingCart the shopping cart
	 * @param cartOrder the cart order
	 * @return the same shopping cart, now populated with the transient fields.
	 * @deprecated use populateAddressAndShippingFields instead as this method will call ShoppingCart.calculateShoppingCartTaxAndBeforeTaxPrices()
	 */
	@Deprecated
	ShoppingCart populateShoppingCartTransientFields(ShoppingCart shoppingCart, CartOrder cartOrder);

	/**
	 * Populates the transient address and shipping fields on the shopping cart using the information in the cart order.
	 * <p/>
	 * Does not call ShoppingCart.calculateShoppingCartTaxAndBeforeTaxPrices()
	 *
	 * @param shoppingCart the shopping cart
	 * @param cartOrder the cart order
	 * @return the same shopping cart, now populated with the transient fields.
	 */
	ShoppingCart populateAddressAndShippingFields(ShoppingCart shoppingCart, CartOrder cartOrder);

	/**
	 * Removes the cart order related to the given shopping cart. It does not fail
	 * if no such cart order exists.
	 *
	 * @param shoppingCart the shopping cart
	 */
	void removeIfExistsByShoppingCart(ShoppingCart shoppingCart);

	/**
	 * Remove the cart orders corresponding to the associated shopping cart GUIDs.
	 *
	 * @param shoppingCartGuids the shopping cart GUIDs
	 * @return the number of cart orders deleted
	 */
	int removeIfExistsByShoppingCartGuids(List<String> shoppingCartGuids);

	/**
	 * Find the GUIDs of all the cart orders owned by a customer in a certain store, given customer's GUID and store code.
	 *
	 * @param storeCode the store code
	 * @param customerGuid the customer GUID
	 * @return the list of cart order GUIDs
	 */
	List<String> findCartOrderGuidsByCustomerGuid(String storeCode, String customerGuid);

	/**
	 * Gets the store code for cart order.
	 *
	 * @param cartOrderGuid the cart order GUID
	 * @return the store code for cart order
	 */
	String getStoreCodeForCartOrder(String cartOrderGuid);

	/**
	 * Gets the last modified date for a CartOrder given its GUID.
	 *
	 * @param cartOrderGuid the cart order GUID
	 * @return the last modified date
	 */
	Date getCartOrderLastModifiedDate(String cartOrderGuid);
}
