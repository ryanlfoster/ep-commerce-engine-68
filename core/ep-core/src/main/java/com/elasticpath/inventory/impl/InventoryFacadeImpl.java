/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.inventory.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.elasticpath.commons.util.capabilities.Capabilities;
import com.elasticpath.inventory.CommandFactory;
import com.elasticpath.inventory.InventoryCommand;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryFacade;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.strategy.InventoryStrategy;
import com.elasticpath.settings.SettingsReader;

/**
 * Out-of-the box implementation of the {@link InventoryFacade}. 
 */
public class InventoryFacadeImpl implements InventoryFacade {
	
	/** The selected strategy. */
	private InventoryStrategy selectedStrategy;
	
	/** */
	private static final String SETTING_INVENTORY_STRATEGY = "COMMERCE/SYSTEM/INVENTORY/inventoryStrategy";
	
	/** */
	private SettingsReader settingsReader;
	
	/** The strategies. */
	private Map<String, InventoryStrategy> strategies = new HashMap<String, InventoryStrategy>();

	/**
	 * @param settingsReader The SettingsReader to set.
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
	
	/**
	 * Sets the strategies, that are mapped to their keys.
	 *
	 * @param strategies the strategies
	 */
	public void setStrategies(final Map<String, InventoryStrategy> strategies) {
		this.strategies = strategies;
	}

	/**
	 * Selects the {@link InventoryStrategy} by key, which should be provided through the Settings framework.
	 * 
	 * @param strategyId The selected strategy ID, which is the key in the strategies map.
	 * @throws IllegalArgumentException If the given strategyId doesn't exist in the strategies map.
	 */
	public void selectStrategy(final String strategyId) { 		
		InventoryStrategy inventoryStrategy = strategies.get(strategyId);
		if (inventoryStrategy == null) {
			throw new IllegalArgumentException("InventoryStrategy with id [" + strategyId + "] does not exist.");
		}
		selectedStrategy = inventoryStrategy;
	}

	@Override
	public void executeInventoryCommand(final InventoryCommand command) {
		selectedStrategy.executeCommand(command);
	}
	
	@Override
	public CommandFactory getInventoryCommandFactory() {
		return selectedStrategy.getCommandFactory();
	}

	@Override
	public void executeInventoryCommands(final Queue<InventoryCommand> commandQueue) {
		while (!commandQueue.isEmpty()) {
			final InventoryCommand command = commandQueue.poll();
			executeInventoryCommand(command);
		}
	}

	@Override
	public InventoryDto getInventory(final InventoryKey inventoryKey) {
		return selectedStrategy.getInventory(inventoryKey);
	}
	
	@Override
	public Map<InventoryKey, InventoryDto> getInventories(final Set<InventoryKey> inventoryKeys) {
		return selectedStrategy.getInventories(inventoryKeys);
	}

	@Override
	public Map<Long, InventoryDto> getInventoriesForSku(final String skuCode) {
		return selectedStrategy.getInventoriesForSku(skuCode);
	}

	@Override
	public Map<String, InventoryDto> getInventoriesForSkusInWarehouse(
			final Set<String> skuCodes, final long warehouseUid) {
		return selectedStrategy.getInventoriesForSkusInWarehouse(skuCodes, warehouseUid);
	}

	/**
	 * Called by Spring after all setters are called.
	 * Reads the configured inventory strategy from the settings framework and sets the appropriate Strategy.
	 */
	public void init() {
		String configuredStrategy = settingsReader.getSettingValue(SETTING_INVENTORY_STRATEGY).getValue();
		selectStrategy(configuredStrategy);
	}
	
	/**
	 * @return capabilities
	 */
	public Capabilities getCapabilities() {
		return selectedStrategy.getCapabilities();
	}

	@Override
	public List<InventoryDto> findLowStockInventories(final Set<String> skuCodes, final long warehouseUid) {		
		return selectedStrategy.findLowStockInventories(skuCodes, warehouseUid);
	}
}
