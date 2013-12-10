package com.elasticpath.inventory.strategy.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.elasticpath.inventory.CommandFactory;
import com.elasticpath.inventory.InventoryCommand;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.command.impl.AbstractInventoryCommand;
import com.elasticpath.inventory.impl.InventoryDtoAssembler;
import com.elasticpath.inventory.log.InventoryLogContextAware;
import com.elasticpath.inventory.log.impl.InventoryLogSupport;
import com.elasticpath.inventory.strategy.InventoryStrategy;

/**
 * An abstract implementation of the {@link InventoryStrategy} interface.
 * Provides common functionality for the {@link InventoryCommand} execution. 
 */
public abstract class AbstractInventoryStrategy implements InventoryStrategy {

	private final InventoryDtoAssembler inventoryDtoAssembler;
	
	private InventoryLogSupport inventoryLogSupport;
	
	/**
	 * Constructor.
	 */
	public AbstractInventoryStrategy() {
		inventoryDtoAssembler = new InventoryDtoAssembler();
	}
	
	@Override
	public void executeCommand(final InventoryCommand command) {
		((AbstractInventoryCommand) command).execute(inventoryLogSupport);
		
		if (command instanceof InventoryLogContextAware) {
			InventoryLogContextAware logContextAware = (InventoryLogContextAware) command;
			inventoryLogSupport.logCommandExecution(logContextAware.getLogContext());
	}
	}

	/**
	 * Finds an InventoryKey in a set of keys.
	 * 
	 * @param skuCode The SkuCode.
	 * @param keys The set of keys.
	 * @throws IllegalArgumentException If the InventoryKey cannot be found.
	 * @return The InventoryKey if found.
	 */
	protected InventoryKey findInventoryKey(final String skuCode, final Set<InventoryKey> keys) {
		for (final InventoryKey key : keys) {
			if (key.getSkuCode().equals(skuCode)) {
				return key;
			}
		}
		throw new IllegalArgumentException("No inventory key was found for sku code: " + skuCode);
	}
	
	@Override
	public abstract CommandFactory getCommandFactory();
	
	/**
	 * Return the {@link InventoryDtoAssembler} instance.
	 * 
	 * @return The inventoryDtoAssembler.
	 */
	protected InventoryDtoAssembler getInventoryDtoAssembler() {
		return inventoryDtoAssembler;
	}

	/**
	 * Get the SkuCodes from a set of InventoryKeys.
	 * 
	 * @param keys The InventoryKeys.
	 * @return The set of SkuCodes.
	 */
	protected Set<String> getSkuCodesFromInventoryKeys(final Set<InventoryKey> keys) {
		final Set<String> skuCodes = new HashSet<String>();
		
		for (final InventoryKey key : keys) {
			skuCodes.add(key.getSkuCode());
		}
		
		return skuCodes;
	}
	
	/**
	 * Creates a map of WarehouseUids to InventoryKeys.
	 * 
	 * @param inventoryKeys The keys to convert into a map.
	 * @return The map.
	 */
	protected Map<Long, Set<InventoryKey>> sortInventoryKeysByWarehouses(final Set<InventoryKey> inventoryKeys) {
		final Map<Long, Set<InventoryKey>> result = new HashMap<Long, Set<InventoryKey>>();
		
		for (final InventoryKey key : inventoryKeys) {
			final long warehouseId = key.getWarehouseUid();
			if (result.containsKey(warehouseId)) {
				result.get(warehouseId).add(key);
			} else {
				final Set<InventoryKey> keys = new HashSet<InventoryKey>();
				keys.add(key);
				result.put(warehouseId, keys);
			}
		}
		
		return result;
	}

	/**
	 * Sets the inventory log support.
	 * 
	 * @param inventoryLogSupport inventory log support
	 */
	public void setInventoryLogSupport(final InventoryLogSupport inventoryLogSupport) {
		this.inventoryLogSupport = inventoryLogSupport;
}
	
}
