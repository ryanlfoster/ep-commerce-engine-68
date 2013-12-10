package com.elasticpath.inventory.strategy.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryExecutionResult;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.command.impl.AbstractInventoryCommand;
import com.elasticpath.inventory.dao.InventoryDao;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.log.impl.InventoryLogSupport;

/**
 * An extension of {@link AbstractInventoryStrategy} which provides methods for an out-of-the-box implementation.
 */
public abstract class AbstractEPInventoryStrategy extends AbstractInventoryStrategy {

	private BeanFactory beanFactory;

	private InventoryDao inventoryDao;

	/**
	 * Return the {@link InventoryDao} instance.
	 *
	 * @return The inventoryDao.
	 */
	protected InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	/**
	 * Sets the {@link InventoryDao} instance.
	 *
	 * @param inventoryDao inventory DAO to set
	 */
	public void setInventoryDao(final InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * This command creates or updates an Inventory record.
	 */
	protected class CreateUpdateInventoryCommand extends AbstractInventoryCommand {

		/** The inventory dto. */
		private final InventoryDto inventoryDto;

		/**
		 * Instantiates a new {@link CreateUpdateInventoryCommand}.
		 *
		 * @param inventoryDto The inventory dto, provides Inventory information to persist.
		 */
		public CreateUpdateInventoryCommand(final InventoryDto inventoryDto) {
			this.inventoryDto = inventoryDto;
		}

		@Override
		public void execute(final InventoryLogSupport logSupport) {
			Inventory inventory = getInventoryDao().getInventory(
					inventoryDto.getInventoryKey().getSkuCode(),
					inventoryDto.getInventoryKey().getWarehouseUid());

			if (inventory == null) {
				// It's going to insert a new inventory.
				inventory = getBeanFactory().getBean(ContextIdNames.INVENTORY);
				inventory.initialize();
				inventory.setWarehouseUid(inventoryDto.getInventoryKey().getWarehouseUid());
				inventory.setSkuCode(inventoryDto.getInventoryKey().getSkuCode());
			}
			// copy values from dto to domain.
			getInventoryDtoAssembler().copyFieldsFromDtoToDomain(inventory, inventoryDto);
			// save to database
			getInventoryDao().saveOrUpdate(inventory);
			InventoryExecutionResult executionResult = getBeanFactory().getBean(ContextIdNames.INVENTORY_EXECUTION_RESULT);
			setExecutionResult(executionResult);
		}
	}

	/**
	 * This command deletes an Inventory record.
	 */
	protected class DeleteInventoryCommandImpl extends AbstractInventoryCommand {

		/** The inventory key. */
		private final InventoryKey inventoryKey;

		/**
		 * Instantiates a new {@link DeleteInventoryCommandImpl}.
		 *
		 * @param inventoryKey the inventory key, identifies the Inventory to delete
		 */
		public DeleteInventoryCommandImpl(final InventoryKey inventoryKey) {
			this.inventoryKey = inventoryKey;
		}

		@Override
		public void execute(final InventoryLogSupport logSupport) {
			final Inventory inventory = getInventoryDao().getInventory(inventoryKey.getSkuCode(), inventoryKey.getWarehouseUid());
			if (inventory != null) {
				getInventoryDao().remove(inventory);
			}
		}

		/**
		 * @return inventory key
		 */
		protected InventoryKey getInventoryKey() {
			return inventoryKey;
		}
	}

}
