/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart;

import java.util.Date;
import java.util.List;

import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;
/**
 * Provide customer-session related business service.
 */
public interface ShoppingCartService extends EpPersistenceService {

	/**
	 * Updates the given shopping cart. Note that client code should use CartDirector.saveShoppingCart().
	 *
	 * @param shoppingCart the shopping cart to update
	 * @return the persisted instance of shoppingCart
	 * @throws EpServiceException - in case of any errors
	 */
	ShoppingCart saveOrUpdate(ShoppingCart shoppingCart) throws EpServiceException;

	/**
	 * Saves the given shopping cart if and only if the cart has never been persisted before.
	 *
	 * @param shoppingCart the shopping cart to save
	 * @return the persisted instance of shoppingCart
	 * @throws EpServiceException - in case of any errors
	 */
	ShoppingCart saveIfNotPersisted(ShoppingCart shoppingCart) throws EpServiceException;

	/**
	 * Removes the given shopping cart.
	 *
	 * @param shoppingCart the shopping cart to remove.
	 * @throws EpServiceException if the cart cannot be removed.
	 */
	void remove(ShoppingCart shoppingCart) throws EpServiceException;

	/**
	 * This method retrieves the shopping cart corresponding to the given guid.<br/>
	 * Note that the result of this method is not yet a valid ShoppingCart. Calling code needs to call 
	 * setCustomerSession() and then calculateShoppingCartTaxAndBeforeTaxPrices() in order to ensure that the
	 * taxes and totals are calculated.
	 *
	 * @param guid the guid for the shopping cart to retrieve
	 * @return the shopping cart corresponding to the specified guid or null if no matching cart can be found
	 * @throws EpServiceException - in case of any errors
 	 * @deprecated This is a legacy method to support Connect. Future calls should get {@link ShoppingCart} via {@link Shopper}
	 */
	@Deprecated
	ShoppingCart findByGuid(String guid) throws EpServiceException;

	/**
	 * This method retrieves the shopping cart corresponding to the given Shopper<br/>
	 * Note that the result of this method is not yet a valid ShoppingCart. Calling code needs to call 
	 * setCustomerSession() and then calculateShoppingCartTaxAndBeforeTaxPrices() in order to ensure that the
	 * taxes and totals are calculated.
	 *
	 * @param shopper the shopping context that defines the shopping cart.
	 * @return the shopping cart corresponding to the shopping context or an empty cart if no matching cart can be found
	 * @throws EpServiceException - in case of any errors
	 */
	ShoppingCart findOrCreateByShopper(Shopper shopper) throws EpServiceException;

	/**
	 * "Touches" the cart by setting the last modified date on the given cart to the current date.
	 *
	 * @param shoppingCartGuid the cart guid of the cart to touch
	 * @throws EpServiceException if something goes wrong
	 */
	void touch(String shoppingCartGuid) throws EpServiceException;

	/**
	 * Deletes all empty shopping carts that are associated with the provided list of {@link Shopper} uids.
	 * @param shopperUids the shopping context uids.
	 * @return the number of deleted shopping carts
	 */
    int deleteEmptyShoppingCartsByShopperUids(List<Long> shopperUids);

	/**
	 * Deletes all shopping carts that are associated with the list of {@link Shopper} uids.
	 * Even the ones that are not empty.
	 * 
	 * @param shopperUids the shopping context uids.
	 * @return the number of deleted shopping carts
	 */
    int deleteAllShoppingCartsByShopperUids(List<Long> shopperUids);
    
    /**
     * Returns a list of ShoppingCart GUIDs for the given Customer GUID and Store Code.
     * 
     * @param customerGuid The Customer GUID.
     * @param storeCode The Store Code.
     * @return The list of ShoppingCart GUIDS or an empty list if there are none.
     */
	List<String> findByCustomerAndStore(String customerGuid, String storeCode);
	
	/**
	 * Gets the last modified date of the shopping cart.
	 *
	 * @param cartGuid the cart Guid
	 * @return the last modified date
	 */
	Date getShoppingCartLastModifiedDate(String cartGuid);

	/**
	 * Checks if a shopping cart with the given cart GUID exists.
	 *
	 * @param cartGuid the cart guid
	 * @return true, shopping cart exists
	 */
	boolean shoppingCartExists(String cartGuid);
	
	/**
	 * Checks if the shopping cart has items.
	 *
	 * @param cartGuid the cart guid
	 * @return true, if shopping cart contains items
	 */
	boolean shoppingCartHasItems(String cartGuid);
	
	/**
	 * Checks if shopping cart exists for a store.
	 *
	 * @param cartGuid the cart guid
	 * @param storeCode the store code
	 * @return true, if shopping cart exists in store
	 */
	boolean shoppingCartExistsForStore(String cartGuid, String storeCode);
	
	/**
	 * Checks if shopping cart contains bundle items.
	 *
	 * @param cartGuid the cart guid
	 * @return true, if shopping cart has bundle items
	 */
	boolean shoppingCartContainsBundles(String cartGuid);
	
	/**
	 * Checks if shopping cart contains shippable items.
	 *
	 * @param cartGuid the cart guid
	 * @return true, if shopping cart has shippable items
	 */
	boolean shoppingCartContainsShippableItems(String cartGuid);

	/**
	 * Returns true if this cart has ever been persisted.
	 *
	 * @param shoppingCart the cart
	 * @return true if the cart has ever been persisted
	 */
	boolean isPersisted(ShoppingCart shoppingCart);
}
