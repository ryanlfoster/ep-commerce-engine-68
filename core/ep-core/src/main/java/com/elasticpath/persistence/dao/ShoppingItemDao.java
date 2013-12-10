/**
 * 
 */
package com.elasticpath.persistence.dao;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Provides persistence-layer methods for {@code ShoppingItem}s.
 */
public interface ShoppingItemDao {

	/**
	 * Finds the {@code ShoppingItem} with the given GUID.
	 * @param guid the guid
	 * @param loadTuner a load tuner
	 * @return the {@code ShoppingItem} or null if not found.
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	ShoppingItem findByGuid(String guid, LoadTuner loadTuner);
	
	/**
	 * Saves or updates the given {@code ShoppingItem}.
	 * @param shoppingItem the item to save
	 * @return the persisted item
	 * @throws com.elasticpath.persistence.api.EpPersistenceException on error
	 */
	ShoppingItem saveOrUpdate(ShoppingItem shoppingItem);
	
}
