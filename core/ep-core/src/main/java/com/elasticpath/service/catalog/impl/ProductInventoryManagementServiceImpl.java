/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.dto.InventoryDetails;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.capabilities.Capabilities;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.InsufficientInventoryException;
import com.elasticpath.domain.catalog.InventoryAudit;
import com.elasticpath.domain.catalog.InventoryCalculator;
import com.elasticpath.domain.catalog.InventoryEventType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.InventoryAuditImpl;
import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.inventory.CommandFactory;
import com.elasticpath.inventory.InventoryCommand;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryExecutionResult;
import com.elasticpath.inventory.InventoryFacade;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.impl.InventoryDtoAssembler;
import com.elasticpath.inventory.log.InventoryLogContextAware;
import com.elasticpath.inventory.log.impl.InventoryLogContext;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.InventoryListener;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.store.WarehouseService;

/**
 * Provides inventory-related services.
 */
public class ProductInventoryManagementServiceImpl extends AbstractEpServiceImpl implements ProductInventoryManagementService {

	private WarehouseService warehouseService;
	private ProductSkuService productSkuService;
	private InventoryCalculator inventoryCalculator;
	private IndexNotificationService indexNotificationService;
	private final InventoryDtoAssembler inventoryDtoAssembler = new InventoryDtoAssembler();

	private final List<InventoryListener> inventoryListeners = new ArrayList<InventoryListener>(1);

	private InventoryFacade inventoryFacade;

	@Override
	public InventoryDto saveOrUpdate(final InventoryDto inventoryDto) throws EpServiceException {
		InventoryDto inventoryDtoExisiting = getInventory(inventoryDto.getSkuCode(), inventoryDto.getWarehouseUid());

		// If the inventory is null then we want to delete it and the journal entries and the journal lock entry
		// before creating a new inventory.
		if (inventoryDtoExisiting == null) {
			remove(inventoryDto);
		}

		CommandFactory inventoryCommandFactory = inventoryFacade.getInventoryCommandFactory();
		InventoryCommand createOrUpdateInventoryCommand = inventoryCommandFactory.getCreateOrUpdateInventoryCommand(inventoryDto);
		inventoryFacade.executeInventoryCommand(createOrUpdateInventoryCommand);
		return inventoryFacade.getInventory(inventoryDto.getInventoryKey());
	}

	@Override
	public InventoryDto merge(final InventoryDto inventoryDto) throws EpServiceException {
		// inventoryDtoExisiting may be null.
		InventoryDto inventoryDtoExisiting = getInventory(inventoryDto.getSkuCode(), inventoryDto.getWarehouseUid());

		// ProductInventoryManagementService.saveOrUpdate() does not save quantityOnHand and allocatedQuantity
		// it only saves reserved quantity, restock date, reorder minimum, reorder quantity
		saveOrUpdate(inventoryDto);

		// Here we deal with quantity on hand and allocated quantity.
		int quantityOnhandDelta = getQuantityOnHandDelta(inventoryDtoExisiting, inventoryDto);
		processInventoryUpdate(inventoryDto, buildInventoryAudit(InventoryEventType.STOCK_ADJUSTMENT, quantityOnhandDelta));

		int allocatedQuantityDelta = getAllocatedQuantityDelta(inventoryDtoExisiting, inventoryDto);
		if (allocatedQuantityDelta > 0) {
			processInventoryUpdate(inventoryDto, buildInventoryAudit(InventoryEventType.STOCK_ALLOCATE, allocatedQuantityDelta));
		} else if (allocatedQuantityDelta < 0) {
			processInventoryUpdate(inventoryDto, buildInventoryAudit(InventoryEventType.STOCK_DEALLOCATE, -allocatedQuantityDelta));
		}

		// get updated inventory
		return getInventory(inventoryDto.getSkuCode(), inventoryDto.getWarehouseUid());
	}

	private InventoryAudit buildInventoryAudit(final InventoryEventType eventType, final int quantity) {
		InventoryAudit inventoryAudit = new InventoryAuditImpl();
		inventoryAudit.setEventType(eventType);
		inventoryAudit.setQuantity(quantity);
		return inventoryAudit;
	}

	private int getAllocatedQuantityDelta(final InventoryDto inventoryDtoExisiting, final InventoryDto newInventory) {
		if (inventoryDtoExisiting == null) {
			return newInventory.getAllocatedQuantity();
		}
		return newInventory.getAllocatedQuantity() - inventoryDtoExisiting.getAllocatedQuantity();
	}

	private int getQuantityOnHandDelta(final InventoryDto inventoryDtoExisiting, final InventoryDto newInventory) {
		if (inventoryDtoExisiting == null) {
			return newInventory.getQuantityOnHand();
		}
		return newInventory.getQuantityOnHand() - inventoryDtoExisiting.getQuantityOnHand();
	}

	@Override
	public InventoryExecutionResult processInventoryUpdate(final InventoryDto inventoryDto, final InventoryAudit inventoryAudit) {
		ProductSku productSku = null;
		if (inventoryDto != null) {
			productSku = productSkuService.findBySkuCode(inventoryDto.getSkuCode());
		}
		return processInventoryUpdate(inventoryDto, inventoryAudit.getEventType(), inventoryAudit.getQuantity(), inventoryAudit.getReason(),
				productSku, Collections.<String, Object>emptyMap());
	}

	private InventoryExecutionResult processInventoryUpdate(
			final InventoryDto inventoryDto, final InventoryEventType eventType,
			final int quantity, final String reason, final ProductSku productSku, final Map<String, Object> logAttributes) {
		if (eventType == null || inventoryDto == null) {
			// return empty inventory result in case the inventory or command are null
			return getBean(ContextIdNames.INVENTORY_EXECUTION_RESULT);
		}

		final Product product = productSku.getProduct();

		final boolean isOutOfStockBefore = inventoryDto.getAvailableQuantityInStock() < product.getMinOrderQty();

		InventoryExecutionResult inventoryEventResult = execute(inventoryDto, eventType, quantity, reason, productSku, logAttributes);

		if (isOutOfStockBefore != (inventoryEventResult.getInventoryAfter().getAvailableQuantityInStock() < product.getMinOrderQty())
				&& product.getAvailabilityCriteria().equals(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK)) {
			//trigger the search index in the case the out of stock status change to update the displayability
			getIndexNotificationService().addNotificationForEntityIndexUpdate(IndexType.PRODUCT, product.getUidPk());
		}

		return inventoryEventResult;
	}


	/**
	 * Execute the command on the inventory object.
	 *
	 * @param inventoryDto the inventory dto
	 * @param inventoryEventType the inventory event type
	 * @param quantity the quantity
	 * @param reason the reason
	 * @param productSku the product sku
	 * @param logAttributes inventory update attributes
	 * @return the execution result on the inventory event.
	 */
	@SuppressWarnings("fallthrough")
	protected InventoryExecutionResult execute(final InventoryDto inventoryDto, final InventoryEventType inventoryEventType,
			final int quantity, final String reason, final ProductSku productSku, final Map<String, Object> logAttributes) {
		final AvailabilityCriteria availabilityCriteria = productSku.getProduct().getAvailabilityCriteria();
		InventoryExecutionResult inventoryEventResult = null;
		InventoryDto inventoryAfter = inventoryDto.clone();
		switch (inventoryEventType) {
		case STOCK_RECEIVED:
		case STOCK_ADJUSTMENT:
			inventoryEventResult = adjustInventory(inventoryDto, quantity, reason,
					availabilityCriteria, logAttributes);
			if (inventoryEventResult.getQuantity() > 0) {
				fireNewInventoryEvent(inventoryDto);
			}
			adjustQuantityOnHand(inventoryAfter, inventoryEventResult.getQuantity());
			break;
		case STOCK_ALLOCATE:
			inventoryEventResult = allocateInventory(inventoryDto, Math.abs(quantity), reason,
					availabilityCriteria, logAttributes);
			adjustAllocatedQuantity(inventoryAfter, inventoryEventResult.getQuantity());
			break;
		case STOCK_DEALLOCATE:
			inventoryEventResult = deallocateInventory(inventoryDto, Math.abs(quantity), reason,
					availabilityCriteria, logAttributes);
			adjustAllocatedQuantity(inventoryAfter, -inventoryEventResult.getQuantity());
			break;
		case STOCK_RELEASE:
			inventoryEventResult = releaseInventory(inventoryDto, quantity, reason, availabilityCriteria, logAttributes);
			adjustQuantityOnHand(inventoryAfter, -inventoryEventResult.getQuantity());
			adjustAllocatedQuantity(inventoryAfter, -inventoryEventResult.getQuantity());
			break;
		default:
			throw new EpSystemException("Unkown inventory event type");
		}
		inventoryEventResult.setInventoryAfter(inventoryAfter);
		return inventoryEventResult;
	}

	/**
	 * Releases a quantity. Used when an order is shipped.
	 *
	 * @param inventoryDto the inventory
	 * @param quantity the quantity to be deallocated
	 * @param reason the reason
	 * @param availabilityCriteria the <code>AvailabilityCriteria</code> of the product to which this inventory belongs
	 * @param logAttributes inventory update attributes
	 * @return the deallocated quantity
	 */
	protected InventoryExecutionResult releaseInventory(final InventoryDto inventoryDto,
			final int quantity, final String reason, final AvailabilityCriteria availabilityCriteria, final Map<String, Object> logAttributes) {

		if (availabilityCriteria == AvailabilityCriteria.ALWAYS_AVAILABLE) {
			return getBean(ContextIdNames.INVENTORY_EXECUTION_RESULT);
		}

		CommandFactory inventoryCommandFactory = inventoryFacade.getInventoryCommandFactory();
		com.elasticpath.inventory.InventoryCommand releaseInventoryCommand =
			inventoryCommandFactory.getReleaseInventoryCommand(inventoryDto.getInventoryKey(), quantity);

		updateLog(releaseInventoryCommand, reason, logAttributes);

		inventoryFacade.executeInventoryCommand(releaseInventoryCommand);
		return releaseInventoryCommand.getExecutionResult();
	}

	/**
	 * Allocates a quantity. Used when an order is placed.
	 *
	 * @param inventoryDto the inventory
	 * @param quantity the quantity to be deallocated
	 * @param reason the reason
	 * @param availabilityCriteria the <code>AvailabilityCriteria</code> of the product to which this inventory belongs
	 * @param logAttributes inventory update attributes
	 * @return the deallocated quantity
	 */
	protected InventoryExecutionResult allocateInventory(final InventoryDto inventoryDto, final int quantity, final String reason,
			final AvailabilityCriteria availabilityCriteria, final Map<String, Object> logAttributes) {

		if (!availabilityCriteria.hasSufficientInventory(inventoryDto, quantity)) {
			throw new InsufficientInventoryException("Insufficient inventory. Requested: "
					+ quantity + " Available: " + inventoryDto.getAvailableQuantityInStock());
		}

		if (availabilityCriteria == AvailabilityCriteria.ALWAYS_AVAILABLE) {
			InventoryExecutionResult inventoryEventResult = getBean(ContextIdNames.INVENTORY_EXECUTION_RESULT);
			inventoryEventResult.setQuantity(quantity);
			return inventoryEventResult;
		}

		CommandFactory inventoryCommandFactory = inventoryFacade.getInventoryCommandFactory();
		InventoryCommand allocateInventoryCommand =
			inventoryCommandFactory.getAllocateInventoryCommand(inventoryDto.getInventoryKey(), quantity);

		updateLog(allocateInventoryCommand, reason, logAttributes);

		inventoryFacade.executeInventoryCommand(allocateInventoryCommand);
		return allocateInventoryCommand.getExecutionResult();
	}

	/**
	 * Deallocates a quantity. Used for canceling an order.
	 *
	 * @param inventoryDto the inventory
	 * @param quantity the quantity to be deallocated
	 * @param reason the reason
	 * @param availabilityCriteria the <code>AvailabilityCriteria</code> of the product to which this inventory belongs
	 * @param logAttributes inventory update attributes
	 * @return the deallocated quantity
	 */
	protected InventoryExecutionResult deallocateInventory(final InventoryDto inventoryDto, final int quantity,
			final String reason, final AvailabilityCriteria availabilityCriteria, final Map<String, Object> logAttributes) {

		if (availabilityCriteria == AvailabilityCriteria.ALWAYS_AVAILABLE) {
			InventoryExecutionResult inventoryEventResult = getBean(ContextIdNames.INVENTORY_EXECUTION_RESULT);
			inventoryEventResult.setQuantity(quantity);
			return inventoryEventResult;
		}

		CommandFactory inventoryCommandFactory = inventoryFacade.getInventoryCommandFactory();
		InventoryCommand deallocateInventoryCommand =
			inventoryCommandFactory.getDeallocateInventoryCommand(inventoryDto.getInventoryKey(), quantity);

		updateLog(deallocateInventoryCommand, reason, logAttributes);

		inventoryFacade.executeInventoryCommand(deallocateInventoryCommand);
		return deallocateInventoryCommand.getExecutionResult();
	}

	/**
	 * Adds inventory quantity.
	 *
	 * @param inventoryDto the inventory
	 * @param quantity the quantity to add
	 * @param reason the reason
	 * @param availabilityCriteria the <code>AvailabilityCriteria</code> of the product to which this inventory belongs
	 * @param logAttributes inventory update attributes
	 * @return InventoryEventResult
	 */
	protected InventoryExecutionResult adjustInventory(final InventoryDto inventoryDto, final int quantity,
			final String reason, final AvailabilityCriteria availabilityCriteria, final Map<String, Object> logAttributes) {

		if (availabilityCriteria == AvailabilityCriteria.ALWAYS_AVAILABLE) {
			return getBean(ContextIdNames.INVENTORY_EXECUTION_RESULT);
		}

		CommandFactory inventoryCommandFactory = inventoryFacade.getInventoryCommandFactory();
		InventoryCommand adjustInventoryCommand =
			inventoryCommandFactory.getAdjustInventoryCommand(inventoryDto.getInventoryKey(), quantity);
		updateLog(adjustInventoryCommand, reason, logAttributes);

		inventoryFacade.executeInventoryCommand(adjustInventoryCommand);
		return adjustInventoryCommand.getExecutionResult();
	}

	private void updateLog(final InventoryCommand inventoryCommand, final String reason, final Map<String, Object> logAttributes) {
		if (inventoryCommand instanceof InventoryLogContextAware) {
			InventoryLogContext logContext = ((InventoryLogContextAware) inventoryCommand).getLogContext();
			logContext.addContextAttribute(InventoryLogContext.REASON, reason);
			for (String attributeKey : logAttributes.keySet()) {
				logContext.addContextAttribute(attributeKey, logAttributes.get(attributeKey));
			}
		}
	}

	private void adjustQuantityOnHand(final InventoryDto inventory, final int quantity) {
		inventory.setQuantityOnHand(inventory.getQuantityOnHand() + quantity);
	}

	private void adjustAllocatedQuantity(final InventoryDto inventory, final int quantity) {
		inventory.setAllocatedQuantity(inventory.getAllocatedQuantity() + quantity);
	}


	/**
	 * Fires an inventory event.
	 * @param inventoryDto the inventory that caused the event
	 */
	protected void fireNewInventoryEvent(final InventoryDto inventoryDto) {
		final String warehouseCode = findWarehouseCode(inventoryDto.getWarehouseUid());
		for (final InventoryListener listener : inventoryListeners) {
			listener.newInventory(inventoryDto.getSkuCode(), warehouseCode);
		}

	}

	private String findWarehouseCode(final long warehouseUid) {
		return warehouseService.getWarehouse(warehouseUid).getCode();
	}

	@Override
	public InventoryExecutionResult processInventoryUpdate(
			final ProductSku productSku,
			final long warehouseUid,
			final InventoryEventType eventType,
			final String eventOriginator,
			final int quantity,
			final Order order,
			final String comment) {
		Map<String, Object> logAttributes = createLogAttributesMap(eventOriginator, order, comment);

		InventoryDto inventoryDto = getInventory(productSku.getSkuCode(), warehouseUid);
		return processInventoryUpdate(inventoryDto, eventType, quantity, null, productSku, logAttributes);

	}

	private Map<String, Object> createLogAttributesMap(final String eventOriginator, final Order order, final String comment) {
		Map<String, Object> logAttributes = new HashMap<String, Object>();

		if (comment != null) {
			logAttributes.put(InventoryLogContext.COMMENT, comment);
		}
		if (order != null) {
			logAttributes.put(InventoryLogContext.ORDER_NUMBER, order.getOrderNumber());
		}
		if (eventOriginator != null) {
			logAttributes.put(InventoryLogContext.EVENT_ORIGINATOR, eventOriginator);
		}

		return logAttributes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Including associated InventoryAudits.
	 *
	 */
	public void remove(final InventoryDto inventoryDto) throws EpServiceException {
		CommandFactory inventoryCommandFactory = inventoryFacade.getInventoryCommandFactory();
		com.elasticpath.inventory.InventoryCommand deleteInventoryCommand =
			inventoryCommandFactory.getDeleteInventoryCommand(inventoryDto.getInventoryKey());
		inventoryFacade.executeInventoryCommand(deleteInventoryCommand);
	}

	@SuppressWarnings("fallthrough")
	@Override
	public boolean hasSufficientInventory(final ProductSku productSku, final long warehouseUid, final int quantity) {
		if (quantity <= 0) {
			throw new EpDomainException("Invalid argument: cannot check for zero or negative quantity");
		}

		final InventoryDetails inventoryDetails = inventoryCalculator.getInventoryDetails(this, productSku, warehouseUid);

		final Product product = productSku.getProduct();

		boolean hasSufficientInventory = false;

		switch (product.getAvailabilityCriteria()) {
		case ALWAYS_AVAILABLE:
			hasSufficientInventory = true;
			break;
		case AVAILABLE_WHEN_IN_STOCK:
		case AVAILABLE_FOR_PRE_ORDER:
		case AVAILABLE_FOR_BACK_ORDER:
			hasSufficientInventory = inventoryDetails != null && inventoryDetails.getAvailableQuantityInStock() >= quantity;
			break;
		default:
			// do nothing
		}

		return hasSufficientInventory;
	}

	@Override
	public void registerInventoryListener(final InventoryListener inventoryListener) {
		if (inventoryListener != null) {
			inventoryListeners.add(inventoryListener);
		}
	}

	/**
	 * Sets the warehouse service.
	 * 
	 * @param warehouseService the warehouse service
	 */
	public void setWarehouseService(final WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	@Override
	public int getAvailableInStockQty(final ProductSku productSku, final long warehouseUid) {
		InventoryDto inventoryDto = getInventory(productSku.getSkuCode(), warehouseUid);
		if (inventoryDto == null) {
			return 0;
		}
		return inventoryDto.getAvailableQuantityInStock();
	}

	@Override
	public PreOrBackOrderDetails getPreOrBackOrderDetails(final String skuCode) {
		return productSkuService.getPreOrBackOrderDetails(skuCode);
	}

	@Override
	public InventoryDto getInventory(final String skuCode, final long warehouseUid) {
		return inventoryFacade.getInventory(new InventoryKey(skuCode, warehouseUid));
	}

	@Override
	public Capabilities getInventoryCapabilities() {
		return inventoryFacade.getCapabilities();
	}

	@Override
	public Map<Warehouse, InventoryDto> getInventoriesForSkuInWarehouses(final String skuCode, final Collection<Warehouse> warehouses)
	throws EpServiceException {
		Map<Warehouse, InventoryDto> inventoryDtos = new HashMap<Warehouse, InventoryDto>();

		for (Warehouse warehouse : warehouses) {
			InventoryDto inventoryDto = getInventory(skuCode, warehouse.getUidPk());
			if (inventoryDto != null) {
				inventoryDtos.put(warehouse, inventoryDto);
			}
		}
		return inventoryDtos;
	}

	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

	public void setInventoryCalculator(final InventoryCalculator inventoryCalculator) {
		this.inventoryCalculator = inventoryCalculator;
	}

	@Override
	public boolean isSelfAllocationSufficient(final OrderSku sku, final long warehouseUid) {
		InventoryDto inventoryDto = getInventory(sku.getSkuCode(), warehouseUid);
		if (inventoryDto != null && inventoryDto.getAllocatedQuantity() > 0) {
			int totalInventoryQtyForThisOrderSku = inventoryDto.getAllocatedQuantity() + inventoryDto.getAvailableQuantityInStock();
			if (totalInventoryQtyForThisOrderSku >= sku.getQuantity()) {
				return true;
			}
		}
		return false;
	}

	public void setIndexNotificationService(final IndexNotificationService indexNotificationService) {
		this.indexNotificationService = indexNotificationService;
	}

	protected IndexNotificationService getIndexNotificationService() {
		return indexNotificationService;
	}

	@Override
	public Map<Long, InventoryDto> getInventoriesForSku(final String skuCode) {
		return inventoryFacade.getInventoriesForSku(skuCode);
	}

	@Override
	public Inventory assembleDomainFromDto(final InventoryDto dto) {
		return this.inventoryDtoAssembler.assembleDomainFromDto(dto);
	}

	public void setInventoryFacade(final InventoryFacade inventoryFacade) {
		this.inventoryFacade = inventoryFacade;
	}

	@Override
	public InventoryDto assembleDtoFromDomain(final Inventory inventory) {
		return this.inventoryDtoAssembler.assembleDtoFromDomain(inventory);
	}

	@Override
	public Map<String, InventoryDto> getInventoriesForSkusInWarehouse(
			final Set<String> skuCodesForInventory, final long warehouseUidPk) {
		return inventoryFacade.getInventoriesForSkusInWarehouse(skuCodesForInventory, warehouseUidPk);
	}

	@Override
	public List<InventoryDto> findLowStockInventories(final Set<String> skuCodes, final long warehouseUid) {
		return inventoryFacade.findLowStockInventories(skuCodes, warehouseUid);
	}
}
