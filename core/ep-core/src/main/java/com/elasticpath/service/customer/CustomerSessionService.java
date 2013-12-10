/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.customer;


import java.util.Currency;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provide customer-session related business service.
 */
public interface CustomerSessionService extends EpPersistenceService {

	/**
	 * Creates a new {@link CustomerSession} with a particular {@link Shopper}.
	 *
	 * @param shopper the {@link Shopper} you want to create this {@link CustomerSession} with.
	 * @return a new {@link CustomerSession} attached to a {@link Shopper}.
	 */
	CustomerSession createWithShopper(Shopper shopper);

	/**
	 * Adds the given customer session.
	 *
	 * @param customerSession the customer session to add
	 * @throws EpServiceException - in case of any errors
	 */
	void add(final CustomerSession customerSession) throws EpServiceException;

	/**
	 * Updates the given {@link CustomerSession}.
	 *
	 * @param customerSession the {@link CustomerSession} to update
	 * @throws EpServiceException - in case of any errors
	 */
	void update(CustomerSession customerSession) throws EpServiceException;

	/**
	 * Handles a ShopperChange (such as merging {@link com.elasticpath.domain.shoppingcart.ShoppingCart}s
	 * and {@link com.elasticpath.domain.shoppingcart.WishList}s) and updating the {@link CustomerSession}.
	 *
	 * @param customerSession the {@link CustomerSession} to update
	 * @param storeCode the storeCode associated with the {@link CustomerSession}
	 * @throws EpServiceException - in case of any errors
	 */
	void handleShopperChangeAndUpdate(CustomerSession customerSession, String storeCode) throws EpServiceException;

	/**
	 * Updates the given customer session.
	 *
	 * @param customerSession the customer session to update
	 * @param customer the customer to be used to update customer session instance
	 * @throws EpServiceException - in case of any errors
	 */
	void updateCustomerAndSave(CustomerSession customerSession, Customer customer) throws EpServiceException;

	/**
	 * Find the customer session with the given guid.
	 *
	 * @param guid the customer session guid
	 * @return the customer session if guid address exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	CustomerSession findByGuid(String guid) throws EpServiceException;

	/**
	 * Find the customer session with the given customerUid and storeCode. If more than
	 * one session is found that matches the criteria, returns only one.
	 *
	 * @param customerUsername the customer username
	 * @param storeCode the code for the store in which the customer should have a session
	 * @return the customer session if one exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	CustomerSession findByCustomerIdAndStoreCode(String customerUsername, String storeCode) throws EpServiceException;

	/**
	 * Initializes a {@link CustomerSession} such that it can be used for pricing computations.
	 *
	 * @param customerSession the customer session
	 * @param storeCode the store code
	 * @param currency the currency
	 * @return the initialized customer session
	 */
	CustomerSession initializeCustomerSessionForPricing(CustomerSession customerSession, String storeCode, Currency currency);

}