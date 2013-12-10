package com.elasticpath.service.command;

import com.elasticpath.domain.store.Store;

/**
 * Holds the result of <code>UpdateStoreCommand</code> execution.
 */
public interface UpdateStoreCommandResult extends CommandResult {

	/**
	 * Gets updated store as a result of command execution.
	 * 
	 * @return updated store
	 */
	Store getStore();

	/**
	 * Sets the store after it has been updated.
	 * 
	 * @param store store to return with this result
	 */
	void setStore(final Store store);
}
