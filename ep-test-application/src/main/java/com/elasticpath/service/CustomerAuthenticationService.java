package com.elasticpath.service;

import com.elasticpath.commons.util.Utility;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * <code>CustomerAuthenticationService</code> provides services for managing <code>CustomerSession</code>s.
 */
public interface CustomerAuthenticationService {

	void loginStore(final Store store, final String email, final CustomerSession oldCustomerSession);
	
	void loginStore(final Store store, final String email);
	
	void loginSecondarySessionToStore(final Store store, final String email);
	
	/**
	 * Login to a store using an anonymous customer with the given email.
	 * 
	 * @param store the store
	 * @param email the anonymous customer's email
	 */
	void guestLoginStore(final Store store, final String email);
	
	void guestLoginStoreAsSecondarySession(final Store store, final String email);
	
	/**
	 * Set the customer session service.
	 * 
	 * @param customerSessionService the customer session service to set.
	 */
	void setCustomerSessionService(CustomerSessionService customerSessionService);

	/**
	 * Sets the utility.
	 * 
	 * @param utility the utility.
	 */
	void setUtility(Utility utility);

	/**
	 * Returns the utility.
	 * 
	 * @return the utility.
	 */
	Utility getUtility();

	/**
	 * Sets the elastic path context.
	 * 
	 * @param elasticPath the elastic path context
	 */
	void setElasticPath(ElasticPath elasticPath);

	/**
	 * Set the shopping cart service.
	 * 
	 * @param shoppingCartService the shopping cart service to set.
	 */
	void setShoppingCartService(ShoppingCartService shoppingCartService);

	/**
	 * Creates and persist an empty customer session. 
	 * @param store the store. 
	 * @return {@link CustomerSession}.
	 */
	void createAnonymousCustomerSession(Store store);
	
	void createAnonymousCustomerSessionAsSecondarySession(Store store);
	
	/**
	 * Sets the ShopperService.
	 *
	 * @param shopperService the ShopperService
	 */
	void setShopperService(ShopperService shopperService);

}