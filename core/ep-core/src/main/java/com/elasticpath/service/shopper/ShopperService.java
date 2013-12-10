package com.elasticpath.service.shopper;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSessionMemento;
import com.elasticpath.domain.shopper.Shopper;

/**
 * Provides a way to find or store the Shopper.
 */
public interface ShopperService {

	/**
	 * Finds a Shopper for this given customer.  If none found, then create one.
	 *
	 * @param customer Customer to use.
	 * @param storeCode the storeCode
	 * @return Shopper
	 */
	Shopper findOrCreateShopper(Customer customer, String storeCode);

	/**
	 * Find a shopper for a persisted customer session only.
	 *
	 * @param customerSessionMemento the customer session memento
	 * @return the shopper or null if not found
	 */
	Shopper findByPersistedCustomerSessionMemento(CustomerSessionMemento customerSessionMemento);

	/**
     * Create and save the {@Shopper}.
     *
	 * @param storeCode the storeCode.
     * @return the {@link Shopper} created
     */
	Shopper createAndSaveShopper(String storeCode);

	/**
	 * Get the shopper by uidPk.
	 *
	 * @param uid the uidPk
	 * @return the shopper found
	 */
	Shopper get(long uid);

	/**
	 * Save the shopper.
	 *
	 * @param shopper the shopper to be saved
	 * @return the saved shopper
	 */
	Shopper save(Shopper shopper);

	/**
	 * Remove the shopper.
	 *
	 * @param shopper the shopper to be removed
	 */
	void remove(Shopper shopper);

	/**
	 * Remove the shopper, if it is not referenced by any CustomerSessions.
	 *
	 * @param shopper the shopper removed
	 */
	void removeIfOrphaned(Shopper shopper);
}
