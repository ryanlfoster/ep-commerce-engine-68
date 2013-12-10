package com.elasticpath.inventory.command.impl;

import com.elasticpath.inventory.InventoryCommand;
import com.elasticpath.inventory.InventoryExecutionResult;
import com.elasticpath.inventory.log.InventoryLogContextAware;
import com.elasticpath.inventory.log.impl.InventoryLogContext;
import com.elasticpath.inventory.log.impl.InventoryLogSupport;

/**
 * Abstract implementation of the {@link InventoryCommand} interface. Contains executable methods,
 * that should not be exposed to clients through the interface.
 */
public abstract class AbstractInventoryCommand implements InventoryCommand, InventoryLogContextAware {

	private InventoryLogContext inventoryLogContext;

	/**
	 * result of command.
	 */
	private InventoryExecutionResult executionResult;

	/**
	 * Contains business logic that should be executed within this command. Specific commands should override
	 * it in their own way.
	 *
	 * @param logSupport logging support  to log any very command specific actions
	 */
	public abstract void execute(InventoryLogSupport logSupport);

	@Override
	public InventoryExecutionResult getExecutionResult() {
		return executionResult;
	}

	protected void setExecutionResult(final InventoryExecutionResult executionResult) {
		this.executionResult = executionResult;
	}

	@Override
	public InventoryLogContext getLogContext() {
		return inventoryLogContext;
	}

	public void setLogContext(final InventoryLogContext logContext) {
		inventoryLogContext = logContext;
}
}
