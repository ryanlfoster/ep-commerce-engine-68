package com.elasticpath.service.shopper;

import java.util.List;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shopper.ShopperMemento;

/**
 * Supports cleanup activities around Shopper.
 */
public interface ShopperCleanupService {

	/**
	 * Deletes all {@link ShopperMemento}s from the provided list.
	 *
	 * @param shopperUids the list of uids to delete.
	 * @return the number of deleted records.
	 */
    int removeShoppersByUidList(List<Long> shopperUids);

	/**
	 * Deletes shopperMementos specified by a list of uids and their dependencies.
	 *
	 * @param mementoUidsToRemove the memento uids to delete
	 * @return the number of deleted records
	 */
	int removeShoppersByUidListAndTheirDependents(List<Long> mementoUidsToRemove);

    /**
     * Returns a list of {@link ShopperMemento}s that are no longer referenced by any
     * {@link com.elasticpath.domain.customer.CustomerSession}s.
     *
     * @param maxResults the maximum number of results to return.
     * @return a list (not null).
     */
    List<ShopperMemento> findShoppersOrphanedFromCustomerSessions(int maxResults);

    /**
     * Deletes all {@link ShopperMemento}s and associated wishlists and shoppingcarts by customer.
     * @param customer the customer.
     * @return the number of deleted records.
     */
    int removeShoppersByCustomer(Customer customer);

    /**
     * Returns a list of shopperMomento uids that contain the specified customer.
     * @param customer the customer.
     * @return a list (not null).
     */
	List<Long> findShoppersByCustomer(Customer customer);
}