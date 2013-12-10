package com.elasticpath.domain.catalog;

import com.elasticpath.domain.store.Store;

/**
 * Interface for a domain object that is part of a {@link Store}.
 */
public interface StoreObject {
	
	/**
	 * Gets the {@link Store} this object belongs to.
	 * 
	 * @return the {@link Store}
	 */
	Store getStore();

	/**
	 * Sets the {@link Store} this object belongs to.
	 * 
	 * @param store the {@link Store} to set
	 */
	void setStore(final Store store);
}
