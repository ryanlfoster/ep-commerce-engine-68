package com.elasticpath.domain.shopper;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.persistence.api.Persistable;

/**
 * Persistent data for the Shopper.
 *
 */
public interface ShopperMemento extends Persistable {

	/**
 	 * Gets the {@link Customer} associated with this {@link ShopperMemento}.
	 * @return the {@link Customer} associated with this {@link ShopperMemento}.
	 */
	Customer getCustomer();
	
	/**
	 * Sets the {@link Customer} associated with this {@link ShopperMemento}.
	 * @param customer the new {@link Customer}
	 */
	void setCustomer(Customer customer);
	
	/**
	 * Gets the code of the {@link Store} associated with this {@link ShopperMemento}.
	 * @return the store code
	 */
	String getStoreCode();
	
	/**
	 * Sets the new {@link Store} code associated with this {@link ShopperMemento}.  
	 * @param storeCode the new store code
	 */
	void setStoreCode(String storeCode);

	/**
	 * Sets the guid for this {@link ShopperMemento}.
	 * @param guid for this {@link ShopperMemento}.
	 */
	void setGuid(final String guid);

	/**
	 * Gets the guid for this {@link ShopperMemento}.
	 * @return the guid for this {@link ShopperMemento}.
	 */
	String getGuid();

	
	
}
