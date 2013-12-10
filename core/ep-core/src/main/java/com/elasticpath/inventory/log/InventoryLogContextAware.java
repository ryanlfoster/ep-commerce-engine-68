package com.elasticpath.inventory.log;

import com.elasticpath.inventory.log.impl.InventoryLogContext;

/**
 * Gives access to the logging context of the inventory command. 
 */
public interface InventoryLogContextAware {

	/**
	 * @return inventory logging context
	 */
	InventoryLogContext getLogContext();
	
}
