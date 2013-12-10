package com.elasticpath.service.dataimport.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.ImportConstants;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.impl.InventoryDtoImpl;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.order.OrderAllocationProcessor;

/**
 * An import runner to import product sku inventory into warehouses.
 * 
 * <p>How it does it:
 * <ul>
 * <li>For each row, handle the creation of and import of an inventory object</li>
 * <li>Keep a copy of each row so we can fire inventory update triggers 
 *      after each COMMIT_UNIT number of rows is updated</li>
 * <li>When informed by the parent class, fire inventory updates so that orders
 *      can be allocated with the newly imported inventory (once the inventory
 *      is safely committed to the database.</li>
 * </ul> 
 */
public class ImportJobRunnerInventoryImpl extends AbstractImportJobRunnerImpl {
	
	private static final Logger LOG = Logger.getLogger(ImportJobRunnerInventoryImpl.class);
	
	private final ProductInventoryManagementService pims;
	
	/**
	 * Constructor.
	 */
	public ImportJobRunnerInventoryImpl() {
		pims = getBean(ContextIdNames.PRODUCT_INVENTORY_MANAGEMENT_SERVICE);
	}
	
	/**
	 * Record the inventory sku code so we can fire off order allocation events
	 * after a chunk of inventory is imported.
	 */
	private final List<String> inventorySKUCodes = new ArrayList<String>(); 
	
	/**
	 * Find the entity with the given guid.
	 * 
	 * @param guid the guid
	 * @return the entity with the given guid if it exists, otherwise <code>null</code>.
	 */
	protected Entity findEntityByGuid(final String guid) {
		Inventory entity = null;

		InventoryDto inventoryDto = pims.getInventory(guid, getWarehouseObject().getUidPk());
		if (inventoryDto != null) {
			// Adapt the dto to the entity interface as the csv import subsystem
			// deals in entities.  It is cleaner to maintain the inventory data
			// in an InventoryDto since that allows us to talk to the inventory
			// subsystem.
			// All we really want here is to copy csv data into an InventoryDto
			// and save it via the ProductInventoryManagementService.
			entity = new InventoryDtoAdapter(inventoryDto);
		}

		return entity;
	}

	/**
	 * Creates a new entity.
	 * 
	 * @param baseObject the base object might be used to determine entity type, such as <code>ProductType</code> etc.
	 * @return the newly created entity
	 */
	protected Entity createNewEntity(final Object baseObject) {
		InventoryDto inventoryDto = new InventoryDtoImpl();
		return new InventoryDtoAdapter(inventoryDto);

	}

	/**
	 * Returns the commit unit.
	 * 
	 * @return the commit unit.
	 */
	protected int getCommitUnit() {
		return ImportConstants.COMMIT_UNIT;
	}

	/**
	 * Update the entity before it get saved.
	 * 
	 * @param entity the entity to save
	 */
	protected void updateEntityBeforeSave(final Entity entity) {
		Inventory inventory = (Inventory) entity;

		long warehouseUid = getWarehouseObject().getUidPk();
		inventory.setWarehouseUid(warehouseUid);

		// add the sku code for that inventory to the list of sku codes
		inventorySKUCodes.add(inventory.getSkuCode());
	}

	/**
	 * Uses the ProductInventoryManagementService to save or update the given Inventory.
	 * This method overrides the method in the abstract superclass.
	 * 
	 * @param session Not used.
	 * @param inventory Inventory entity to save or update.
	 * @return The entity after being updated/saved.
	 */
	@Override
	protected Entity saveEntityHelper(final PersistenceSession session, final Entity inventory) {
		InventoryDto inventoryDto = ((InventoryDtoAdapter) inventory).getDto();
		InventoryDto updatedInventoryDto = pims.merge(inventoryDto);

		// This return param here is unused throughout the csv import subsystem,
		// but we will be nice and return a value to satisfy the interface.
		return new InventoryDtoAdapter(updatedInventoryDto);
	}
	
	/**
	 * Clear down the list of sku codes.
	 */
	@Override
	protected void preCommitUnitTransactionCreate() {
		inventorySKUCodes.clear();
	}

	/**
	 * Loop through the imported row triggers and get their
	 * orders allocated.
	 */
	@Override
	protected void postCommitUnitTransactionCommit() {
		OrderAllocationProcessor processor = getBean(ContextIdNames.ORDER_ALLOCATION_PROCESSOR);
		for (String skuCode : inventorySKUCodes) {
			try {
				processor.processOutstandingOrders(skuCode, getWarehouseObject().getCode());
			} catch (Exception exception) {
				// We don't want to throw an exception and break the import
				LOG.error("Error occurred while allocating the outstanding orders", exception);		
			}
		} 
	}
	
	/**
	 * This method does nothing in order to avoid change set processing.
	 */
	@Override
	protected void prepareChangeSetProcessing() {
		// change set processing is not supported for the inventory import
	}
}