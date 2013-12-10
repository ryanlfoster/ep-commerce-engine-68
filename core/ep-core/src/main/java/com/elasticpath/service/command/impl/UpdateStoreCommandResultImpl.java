package com.elasticpath.service.command.impl;

import com.elasticpath.domain.store.Store;
import com.elasticpath.service.command.UpdateStoreCommandResult;

/**
 * Contains the result of executed command.
 */
public class UpdateStoreCommandResultImpl implements UpdateStoreCommandResult {

	private static final long serialVersionUID = 1L;

	private Store store;

	@Override
	public Store getStore() {
		return store;
	}

	@Override
	public void setStore(final Store store) {
		this.store = store;
	}
}
