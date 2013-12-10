package com.elasticpath.service.shopper.dao;

import java.util.List;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shopper.ShopperMemento;


/**
 * The {@link ShopperMemento} dao interface.
 */
public interface ShopperDao {
	
	/**
	 * Get ShopperMemento by uidpk.
	 * 
	 * @param uid the uidpk
	 * @return the wish list found
	 */
	ShopperMemento get(long uid);

	/**
	 * save or update {@link ShopperMemento}. 
	 * 
	 * @param shopperMemento the {@link ShopperMemento} to be saved or updated.
	 * @return the saved/updated ShopperMemento
	 */
	ShopperMemento saveOrUpdate(ShopperMemento shopperMemento);

	/**
	 * Remove the {@link ShopperMemento}.
	 * 
	 * @param shopperMomento the {@link ShopperMemento}.
	 */
	void remove(ShopperMemento shopperMomento);
	
	/**
	 * Remove the {@link ShopperMemento} if it is not referenced by any {@link com.elasticpath.domain.customer.CustomerSession}s.
	 * 
	 * @param shopperMomento the {@link ShopperMemento}.
	 */
	void removeIfOrphaned(ShopperMemento shopperMomento);

	/**
	 * Finds the ShopperMemento based on customer and storecode.
	 * 
	 * The list should start with the most specific matches first.
	 *
	 * @param customer the {@Customer} that this Shopper belongs to.
	 * @param storeCode the storeCode of the Store this Shopper belongs to.
	 * @return ShopperMemento if found, otherwise null.
	 */
	ShopperMemento findByCustomerAndStoreCode(Customer customer, String storeCode);
	
	/**
	 * Deletes all {@link com.elasticpath.domain.shopper.Shopper}s whose UidPks are in the provided list
	 *  and that don't have any wishlists or shopping carts attached to them.
	 * 
	 * @param shopperUids the list of {@link com.elasticpath.domain.shopper.Shopper}s to delete.
	 * @return the number of deleted {@link com.elasticpath.domain.shopper.Shopper}s.
	 */
    int removeNonDependantShoppersByUidList(List<Long> shopperUids);
    
    /**
	 * Deletes all {@link com.elasticpath.domain.shopper.Shopper}s whose UidPks are in the provided list.
	 * 
	 * @param shopperUids the list of {@link com.elasticpath.domain.shopper.Shopper}s to delete.
	 * @return the number of deleted {@link com.elasticpath.domain.shopper.Shopper}s.
	 */
    int removeShoppersByUidList(final List<Long> shopperUids);

    /**
     * Returns a list of {@link ShopperMemento}s that are no longer referenced by any 
     * {@link com.elasticpath.domain.customer.CustomerSession}s.
     *
     * @param maxResults the maximum number of results to return.
     * @return a list (not null).
     */
    List<ShopperMemento> findShoppersOrphanedFromCustomerSessions(int maxResults);

    /**
     * Returns a list of  {@link ShopperMemento} UIDS that have the specified customer.
     * @param customer the customer. 
     * @return a list of shopper uids.
     */
	List<Long> findUidsByCustomer(Customer customer);
}
