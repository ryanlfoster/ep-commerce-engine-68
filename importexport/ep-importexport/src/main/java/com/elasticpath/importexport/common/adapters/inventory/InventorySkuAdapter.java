package com.elasticpath.importexport.common.adapters.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.inventory.InventorySkuDTO;
import com.elasticpath.importexport.common.dto.inventory.InventoryWarehouseDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.impl.InventoryDtoImpl;
import com.elasticpath.service.catalog.ProductInventoryManagementService;

/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between <code>ProductSku</code> and
 * <code>InventorySkuDTO</code> objects.
 */
public class InventorySkuAdapter extends AbstractDomainAdapterImpl<ProductSku, InventorySkuDTO> {
	
	private static final Logger LOG = Logger.getLogger(InventorySkuAdapter.class);
	
	private final ProductInventoryManagementService productInventoryManagementService;
	
	/** 
	 * Constructor. 
	 * 
	 * @param productInventoryManagementService {@link ProductInventoryManagementService} required to find/save Inventory.
	 */	
	public InventorySkuAdapter(final ProductInventoryManagementService productInventoryManagementService) {
		this.productInventoryManagementService = productInventoryManagementService;
	}

	@Override	
	public void populateDTO(final ProductSku source, final InventorySkuDTO target) {
		target.setCode(source.getSkuCode());
		
		final List<InventoryWarehouseDTO> warehouses = new ArrayList<InventoryWarehouseDTO>();
		final Map<Warehouse, InventoryDto> iwMap = getInventoryWarehouseMapForSku(source.getSkuCode());
		
		for (Entry<Warehouse, InventoryDto> iwEntry : iwMap.entrySet()) {
			InventoryWarehouseDTO dto = new InventoryWarehouseDTO();
			
			final Warehouse warehoue  = iwEntry.getKey();
			final InventoryDto inventory = iwEntry.getValue();
			
			if (inventory != null) {
				dto.setCode(warehoue.getCode());			
				dto.setOnHand(inventory.getQuantityOnHand());
				dto.setAllocated(inventory.getAllocatedQuantity());
				dto.setAvaliable(inventory.getAvailableQuantityInStock());
				dto.setReserved(inventory.getReservedQuantity());
				dto.setReorderMin(inventory.getReorderMinimum());
				dto.setReorderQty(inventory.getReorderQuantity());
				dto.setExpectedRestockDate(inventory.getRestockDate());

				warehouses.add(dto);
			}
		}
		
		target.setWarehouses(warehouses);
	}

	private Map<Warehouse, InventoryDto> getInventoryWarehouseMapForSku(final String skuCode) {
		final List<Warehouse> warehousesToFind = getCachingService().findAllWarehouses();
		final Map<Warehouse, InventoryDto> inventoryWarehouseMap = 
			productInventoryManagementService.getInventoriesForSkuInWarehouses(skuCode, warehousesToFind);
		return inventoryWarehouseMap;
	}

	
	@Override	
	public void populateDomain(final InventorySkuDTO source, final ProductSku target) {
		target.setSkuCode(source.getCode());
		for (InventoryWarehouseDTO inventoryWarehouseDTO : source.getWarehouses()) {
			boolean result = checkNotNegative(inventoryWarehouseDTO.getAllocated(), "IE-10501");			
			result |= checkNotNegative(inventoryWarehouseDTO.getReorderQty(), "IE-10502");
			result |= checkNotNegative(inventoryWarehouseDTO.getOnHand(), "IE-10503");
			result |= checkNotNegative(inventoryWarehouseDTO.getReorderMin(), "IE-10504");
			result |= checkNotNegative(inventoryWarehouseDTO.getReserved(), "IE-10505");
			InventoryDtoImpl inventory = createInventory(target, inventoryWarehouseDTO.getCode());

			result |= inventory == null;
			if (result) {
				continue;
			}
			
			inventory.setAllocatedQuantity(inventoryWarehouseDTO.getAllocated());
			inventory.setQuantityOnHand(inventoryWarehouseDTO.getOnHand());
			inventory.setReorderMinimum(inventoryWarehouseDTO.getReorderMin());
			inventory.setReorderQuantity(inventoryWarehouseDTO.getReorderQty());
			inventory.setReservedQuantity(inventoryWarehouseDTO.getReserved());
			inventory.setRestockDate(inventoryWarehouseDTO.getExpectedRestockDate());			
			inventory.setSkuCode(source.getCode());
						
			productInventoryManagementService.merge(inventory);
		}		
	}

	/*
	 * If There is Inventory in this ProductSku for this warehouseCode then it will be returned.
	 * Otherwise new Inventory will be created in memory and returned. It is not persisted.
	 * 
	 * @param productSku The ProductSku for Inventory.
	 * @param warehouseCode The Warehouse Code.
	 * @return Instance of Inventory.
	 */
	private InventoryDtoImpl createInventory(final ProductSku productSku, final String warehouseCode) {
		final Long warehouseUid = findWarehouseUID(warehouseCode);
		if (warehouseUid == -1L) {
			return null;
		}
		
		InventoryDto inventory = productInventoryManagementService.getInventory(productSku.getSkuCode(), warehouseUid);
		if (inventory == null) {
			inventory = getBean(ContextIdNames.INVENTORYDTO);
			inventory.setWarehouseUid(warehouseUid);
		}
		return (InventoryDtoImpl) inventory;
	}

	/**
	 * Finds Warehouse and returns its UidPk.
	 *  
	 * @param warehouseCode the code of the warehouse to find
	 * @throws PopulationRuntimeException if warehouse with this code could not be found
	 * @return Warehouse UidPk if success
	 */
	Long findWarehouseUID(final String warehouseCode) throws PopulationRuntimeException {
		final Warehouse warehouse = getCachingService().findWarehouseByCode(warehouseCode);
		if (warehouse == null) {
			LOG.error(Message.createJobTypeFailure("IE-10500", JobType.INVENTORY, warehouseCode));
			return -1L;
		}
		return warehouse.getUidPk();
	}

	/**
	 * Checks the appropriate value. It should no be negative.
	 *  
	 * @param value the value to check.
	 * @param message the message that will be used in case the value is negative
	 * @return true if value is negative and false otherwise
	 * @throws PopulationRuntimeException if value is negative  
	 */
	boolean checkNotNegative(final Integer value, final String message) throws PopulationRuntimeException {
		boolean result = value < 0; 
		if (result) {
			LOG.error(Message.createJobTypeFailure(message, JobType.INVENTORY));
		}
		return result;
	}

	@Override
	public ProductSku createDomainObject() {
		return getBean(ContextIdNames.PRODUCT_SKU);
	}

	@Override
	public InventorySkuDTO createDtoObject() {
		return new InventorySkuDTO();
	}
}
