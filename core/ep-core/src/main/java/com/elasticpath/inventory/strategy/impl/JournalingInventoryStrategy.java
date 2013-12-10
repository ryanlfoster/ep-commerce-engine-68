/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.inventory.strategy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.capabilities.Capabilities;
import com.elasticpath.commons.util.capabilities.impl.CapabilitiesImpl;
import com.elasticpath.inventory.CommandFactory;
import com.elasticpath.inventory.InventoryCapabilities;
import com.elasticpath.inventory.InventoryCommand;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryExecutionResult;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.command.impl.AbstractInventoryCommand;
import com.elasticpath.inventory.dao.InventoryJournalDao;
import com.elasticpath.inventory.dao.InventoryJournalLockDao;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.domain.InventoryJournal;
import com.elasticpath.inventory.log.impl.InventoryLogContext;
import com.elasticpath.inventory.log.impl.InventoryLogSupport;
import com.elasticpath.inventory.strategy.InventoryJournalRollup;

/**
 * This strategy provides the new way of managing inventory by tracking changes to an inventory's on-hand quantity and allocation
 * in the TINVENTORYJOURNAL table.
 */
public class JournalingInventoryStrategy extends AbstractEPInventoryStrategy {

	/** The command factory. */
	private final CommandFactory commandFactory; 
	
	/** The inventory journal dao. */
	private InventoryJournalDao inventoryJournalDao;
	
	/** The inventory journal lock dao. */
	private InventoryJournalLockDao inventoryJournalLockDao;
	
	/** */
	private final Capabilities supportedCapabilities;
	
	/**
	 * Constructor.
	 */
	public JournalingInventoryStrategy() {
		commandFactory = new JournalingStrategyCommandFactory();
		supportedCapabilities = new CapabilitiesImpl(InventoryCapabilities.INVENTORY_ALLOCATION_TRACKED);
	}
	
	@Override
	public CommandFactory getCommandFactory() {
		return commandFactory;
	}
	
	/**
	 * Includes the InventoryJournal rollup for the given InventoryKey.
	 * Note that allocated qty is tracked and may go below 0.
	 * However, it is never returned as a negative value. Its lowest returned value is 0.
	 * 
	 * {@inheritDoc}
	 */
	public InventoryDto getInventory(final InventoryKey inventoryKey) {
		Inventory inventory = getInventoryDao().getInventory(inventoryKey.getSkuCode(), inventoryKey.getWarehouseUid());
		InventoryJournalRollup inventoryJournalRollup = getInventoryJournalDao().getRollup(inventoryKey);
		return getInventoryDtoAssembler().assembleDtoFromDomain(inventory, inventoryJournalRollup);
	}

	/**
	 * Includes the InventoryJournal rollups for the given InventoryKeys.
	 * 
	 * {@inheritDoc}
	 */
	public Map<InventoryKey, InventoryDto> getInventories(final Set<InventoryKey> inventoryKeys) {
		final Map<Long, Set<InventoryKey>> keysByWarehouses = sortInventoryKeysByWarehouses(inventoryKeys); 
		final Map<InventoryKey, InventoryDto> result = new HashMap<InventoryKey, InventoryDto>();
		
		for (final long warehouseId : keysByWarehouses.keySet()) {
			final Map<String, Inventory> inventoryMap = getInventoryDao().getInventoryMap(getSkuCodesFromInventoryKeys(
					keysByWarehouses.get(warehouseId)), warehouseId);
			addInventoriesToResult(result, inventoryMap, keysByWarehouses.get(warehouseId));
		}
		
		return result;
	}
	
	/**
	 * TODO: Do a single DB query to get rollups for all InventoryKeys at once.
	 * Right now we are doing DB queries one at a time.
	 * 
	 * @param result The map to fill up.
	 * @param inventoryMap The inventory map.
	 * @param keys The set of keys.
	 */
	private void addInventoriesToResult(final Map<InventoryKey, InventoryDto> result,
			final Map<String, Inventory> inventoryMap, final Set<InventoryKey> keys) {
		
		for (final String skuCode : inventoryMap.keySet()) {
			final InventoryKey key = findInventoryKey(skuCode, keys);
			final Inventory inventory = inventoryMap.get(skuCode);
			InventoryJournalRollup inventoryJournalRollup = getInventoryJournalDao().getRollup(key);
			result.put(key, getInventoryDtoAssembler().assembleDtoFromDomain(inventory, inventoryJournalRollup));
		}
		
	}
	
	@Override
	public Map<Long, InventoryDto> getInventoriesForSku(final String skuCode) {
		Map<Long, InventoryDto> result = new HashMap<Long, InventoryDto>();
		Map<Long, Inventory> inventoriesForSku = getInventoryDao().getInventoriesForSku(skuCode);
		for (Map.Entry<Long, Inventory> entry : inventoriesForSku.entrySet()) {			
			Inventory inventory = entry.getValue();
			InventoryDto inventoryDto = rollupDto(inventory);
			result.put(entry.getKey(), inventoryDto);			
		}
		return result;
	}
	
	@Override
	public Map<String, InventoryDto> getInventoriesForSkusInWarehouse(
			final Set<String> skuCodes, final long warehouseUid) {
		Map<String, InventoryDto> result = new HashMap<String, InventoryDto>();
		Map<String, Inventory> inventories = getInventoryDao().getInventoryMap(skuCodes, warehouseUid);
		for (Map.Entry<String, Inventory> entry : inventories.entrySet()) {			
			Inventory inventory = entry.getValue();
			InventoryDto inventoryDto = rollupDto(inventory);
			result.put(entry.getKey(), inventoryDto);			
		}
		return result;
	}

	private InventoryDto rollupDto(final Inventory inventory) {
		InventoryJournalRollup rollup = getInventoryJournalDao().getRollup(new InventoryKey(inventory.getSkuCode(), inventory.getWarehouseUid()));
		return getInventoryDtoAssembler().assembleDtoFromDomain(inventory, rollup);
	}
	
	private InventoryJournalDao getInventoryJournalDao() {
		return inventoryJournalDao;
	}
	
	/**
	 * Sets the {@link InventoryJournalDao} instance.
	 * 
	 * @param inventoryJournalDao The inventory journal DAO to set.
	 */
	public void setInventoryJournalDao(final InventoryJournalDao inventoryJournalDao) {
		this.inventoryJournalDao = inventoryJournalDao;
	}

	/**
	 * Sets the {@link InventoryJournalLockDao}.
	 * @param inventoryJournalLockDao inventory Journal Lock Dao
	 */
	public void setInventoryJournalLockDao(final InventoryJournalLockDao inventoryJournalLockDao) {
		this.inventoryJournalLockDao = inventoryJournalLockDao;
	}

	@Override
	public Capabilities getCapabilities() {
		return supportedCapabilities;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Inner classes
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Provides a set of commands for the {@link JournalingInventoryStrategy}.
	 * Note that no commands check inventory quantities and therefor always succeed (unless otherwise noted in the command).
	 */
	class JournalingStrategyCommandFactory implements CommandFactory {
		
		@Override
		public InventoryCommand getAdjustInventoryCommand(final InventoryKey inventoryKey, final int quantityToAdjust) {
			AdjustInventoryCommand adjustInventoryCommand = new AdjustInventoryCommand(inventoryKey, quantityToAdjust);
			adjustInventoryCommand.setLogContext(new InventoryLogContext(inventoryKey, "Adjust", quantityToAdjust));
			return adjustInventoryCommand; 

		}

		@Override
		public InventoryCommand getAllocateInventoryCommand(final InventoryKey inventoryKey, final int quantityToAllocate) {
			AllocateInventoryCommand allocateInventoryCommand = new AllocateInventoryCommand(inventoryKey, quantityToAllocate);
			allocateInventoryCommand.setLogContext(new InventoryLogContext(inventoryKey, "Allocate", quantityToAllocate));
			return allocateInventoryCommand; 
		}

		/**
		 * Does not write to the InventoryJournal table.
		 * So the Inventory table is updated but the InventoryJournal table is untouched.
		 * The InventoryDto that is returned will contain the updated values plus any rollup.
		 * 
		 * {@inheritDoc}
		 */
		public InventoryCommand getCreateOrUpdateInventoryCommand(final InventoryDto inventoryDto) {
			CreateUpdateInventoryCommand createUpdateInventoryCommand = new CreateUpdateInventoryCommand(inventoryDto);
			createUpdateInventoryCommand.setLogContext(new InventoryLogContext(inventoryDto.getInventoryKey(), "CreateOrUpdate", 0));
			return createUpdateInventoryCommand;		
		}
		
		@Override
		public InventoryCommand getDeallocateInventoryCommand(final InventoryKey inventoryKey, final int quantityToDeallocate) {
			DeallocateInventoryCommand deallocateInventoryCommand = new DeallocateInventoryCommand(inventoryKey, quantityToDeallocate);
			deallocateInventoryCommand.setLogContext(new InventoryLogContext(inventoryKey, "Deallocate", quantityToDeallocate));
			return deallocateInventoryCommand;
		}
		
		/**
		 * Does not delete any InventoryJournal entries.
		 * Any orphaned entries will be deleted later by an asynchronous service.
		 * 
		 * {@inheritDoc}
		 */
		public InventoryCommand getDeleteInventoryCommand(final InventoryKey inventoryKey) {
			DeleteJournalingInventoryCommandImpl deleteInventoryCommandImpl = new DeleteJournalingInventoryCommandImpl(inventoryKey);
			deleteInventoryCommandImpl.setLogContext(new InventoryLogContext(inventoryKey, "Delete", 0));
			return deleteInventoryCommandImpl;
		}

		@Override
		public InventoryCommand getReleaseInventoryCommand(final InventoryKey inventoryKey, final int quantityToRelease) {
			ReleaseInventoryCommand releaseInventoryCommand = new ReleaseInventoryCommand(inventoryKey, quantityToRelease);
			releaseInventoryCommand.setLogContext(new InventoryLogContext(inventoryKey, "Release", quantityToRelease));
			return releaseInventoryCommand;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * Also removes journaling and journaling lock records for the given inventory key.
	 */
	class DeleteJournalingInventoryCommandImpl extends DeleteInventoryCommandImpl {

		/**
		 * Constructor.
		 * 
		 * @param inventoryKey inventory key
		 */
		public DeleteJournalingInventoryCommandImpl(final InventoryKey inventoryKey) {
			super(inventoryKey);
		}

		@Override
		public void execute(final InventoryLogSupport logSupport) {
			super.execute(logSupport);
			inventoryJournalDao.removeByKey(getInventoryKey());
			inventoryJournalLockDao.removeByKey(getInventoryKey());
		}
	}
	
	/**
	 * Adds an entry to the InventoryJournal table.
	 * However, no row is added if allocatedQuantityDelta is 0 and quantityOnHandDelta is 0.
	 * 
	 * @param inventoryKey The inventory key.
	 * @param allocatedQuantityDelta The delta.
	 * @param quantityOnHandDelta The delta.
	 */
	private void addInventoryJournalEntry(final InventoryKey inventoryKey, final int allocatedQuantityDelta, final int quantityOnHandDelta) {
		if (allocatedQuantityDelta != 0 || quantityOnHandDelta != 0) {
			InventoryJournal inventoryJournal = getBeanFactory().getBean(ContextIdNames.INVENTORY_JOURNAL);
			inventoryJournal.setSkuCode(inventoryKey.getSkuCode());
			inventoryJournal.setWarehouseUid(inventoryKey.getWarehouseUid());
			inventoryJournal.setAllocatedQuantityDelta(allocatedQuantityDelta);
			inventoryJournal.setQuantityOnHandDelta(quantityOnHandDelta);
			inventoryJournal = getInventoryJournalDao().saveOrUpdate(inventoryJournal);
		}
	}
	
	/**
	 * This command adjusts inventory on-hand.
	 */
	class AdjustInventoryCommand extends AbstractInventoryCommand {

		private final InventoryKey inventoryKey;
		private final int onHandQtyToAdjust;

		/**
		 * Instantiates a new {@link AdjustInventoryCommand}.
		 * 
		 * @param inventoryKey The inventory key, identifies the Inventory to adjust.
		 * @param onHandQtyToAdjust The on-hand qty to adjust in this transaction.
		 */
		public AdjustInventoryCommand(final InventoryKey inventoryKey, final int onHandQtyToAdjust) {
			this.inventoryKey = inventoryKey;
			this.onHandQtyToAdjust = onHandQtyToAdjust;
		}

		@Override
		public void execute(final InventoryLogSupport logSupport) {
			addInventoryJournalEntry(inventoryKey, 0, onHandQtyToAdjust);
			InventoryExecutionResult executionResult = getBeanFactory().getBean(ContextIdNames.INVENTORY_EXECUTION_RESULT);
			executionResult.setQuantity(onHandQtyToAdjust);
			setExecutionResult(executionResult);
		}
	}

	/**
	 * This command allocates inventory.
	 */
	class AllocateInventoryCommand extends AbstractInventoryCommand {

		private final InventoryKey inventoryKey;
		private final int quantityToAllocate;

		/**
		 * Instantiates a new {@link AllocateInventoryCommand}.
		 * 
		 * @param inventoryKey The inventory key, identifies the Inventory to allocate.
		 * @param quantityToAllocate The qty to allocate in this transaction.
		 * @throws IllegalArgumentException If quantityToAllocate is < 0.
		 */
		public AllocateInventoryCommand(final InventoryKey inventoryKey, final int quantityToAllocate) {
			if (quantityToAllocate < 0) {
				throw new IllegalArgumentException("quantityToAllocate [" + quantityToAllocate + "] cannot be < 0.");
			}
			this.inventoryKey = inventoryKey;
			this.quantityToAllocate = quantityToAllocate;
		}

		@Override
		public void execute(final InventoryLogSupport logSupport) {
			addInventoryJournalEntry(inventoryKey, quantityToAllocate, 0);
			InventoryExecutionResult executionResult = getBeanFactory().getBean(ContextIdNames.INVENTORY_EXECUTION_RESULT);
			executionResult.setQuantity(quantityToAllocate);
			setExecutionResult(executionResult);
		}
	}

	/**
	 * This command deallocates inventory.
	 */
	class DeallocateInventoryCommand extends AbstractInventoryCommand {

		private final InventoryKey inventoryKey;
		private final int quantityToDeallocate;

		/**
		 * Instantiates a new {@link DeallocateInventoryCommand}.
		 * 
		 * @param inventoryKey The inventory key, identifies the Inventory to deallocate.
		 * @param quantityToDeallocate The qty to deallocate in this transaction.
		 * @throws IllegalArgumentException If quantityToDeallocate is < 0.
		 */
		public DeallocateInventoryCommand(final InventoryKey inventoryKey, final int quantityToDeallocate) {
			if (quantityToDeallocate < 0) {
				throw new IllegalArgumentException("quantityToDeallocate [" + quantityToDeallocate + "] cannot be < 0.");
			}
			this.inventoryKey = inventoryKey;
			this.quantityToDeallocate = quantityToDeallocate;
		}

		@Override
		public void execute(final InventoryLogSupport logSupport) {
			addInventoryJournalEntry(inventoryKey, -quantityToDeallocate, 0);
			InventoryExecutionResult executionResult = getBeanFactory().getBean(ContextIdNames.INVENTORY_EXECUTION_RESULT);
			executionResult.setQuantity(quantityToDeallocate);
			setExecutionResult(executionResult);
		}
	}

	/**
	 * This command releases inventory.
	 * In the CM Client it's called completing a shipment.
	 */
	class ReleaseInventoryCommand extends AbstractInventoryCommand {

		private final InventoryKey inventoryKey;
		private final int quantityToRelease;

		/**
		 * Instantiates a new {@link ReleaseInventoryCommand}.
		 * 
		 * @param inventoryKey The inventory key, identifies the Inventory to release.
		 * @param quantityToRelease The qty to release in this transaction.
		 * @throws IllegalArgumentException If quantityToRelease is <= 0.
		 */
		public ReleaseInventoryCommand(final InventoryKey inventoryKey, final int quantityToRelease) {
			if (quantityToRelease <= 0) {
				throw new IllegalArgumentException("quantityToRelease [" + quantityToRelease + "] cannot be <= 0.");
			}
			this.inventoryKey = inventoryKey;
			this.quantityToRelease = quantityToRelease;
		}

		@Override
		public void execute(final InventoryLogSupport logSupport) {
			addInventoryJournalEntry(inventoryKey, -quantityToRelease, -quantityToRelease);
			InventoryExecutionResult executionResult = getBeanFactory().getBean(ContextIdNames.INVENTORY_EXECUTION_RESULT);
			setExecutionResult(executionResult);
		}
	}

	@Override
	public List<InventoryDto> findLowStockInventories(final Set<String> skuCodes, final long warehouseUid) {
		List<InventoryDto> lowStockInventories = getInventoryDao().findLowStockInventories(skuCodes, warehouseUid);
		List<InventoryDto> lowStockInventoriesFromJournaling = inventoryJournalDao.findLowStockInventories(skuCodes, warehouseUid);
		
		Map<InventoryKey, InventoryDto> inventoryMap = new HashMap<InventoryKey, InventoryDto>();
		
		for (InventoryDto dto : lowStockInventoriesFromJournaling) {
			inventoryMap.put(dto.getInventoryKey(), dto);
		}
		
		for (InventoryDto dto : lowStockInventories) {
			if (inventoryMap.containsKey(dto.getInventoryKey())) {			
				continue; //prevent duplicate inventory records from inventory table and journaling processed twice
			}
			inventoryMap.put(dto.getInventoryKey(), dto);
		}
		
		return new ArrayList<InventoryDto>(inventoryMap.values());
	}

}
