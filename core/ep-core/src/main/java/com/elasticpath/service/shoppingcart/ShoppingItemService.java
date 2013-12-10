/**
 * 
 */
package com.elasticpath.service.shoppingcart;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Provides services for {@code ShoppingItem} persistence.
 */
public interface ShoppingItemService {

	/**
	 * Finds the {@code ShoppingItem} with the given GUID.
	 * @param guid the guid
	 * @param loadTuner the load tuner, if null it uses the default load tuner
	 * @return the {@code ShoppingItem} or null if not found.
	 * @throws com.elasticpath.base.exception.EpServiceException on error
	 */
	ShoppingItem findByGuid(String guid, LoadTuner loadTuner);
	
	/**
	 * Saves or updates the given {@code ShoppingItem}.
	 * @param shoppingItem the item to save
	 * @return the persisted item
	 * @throws com.elasticpath.base.exception.EpServiceException on error
	 */
	ShoppingItem saveOrUpdate(ShoppingItem shoppingItem);
}
