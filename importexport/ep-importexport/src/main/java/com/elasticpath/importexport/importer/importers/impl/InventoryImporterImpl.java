package com.elasticpath.importexport.importer.importers.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.inventory.InventorySkuAdapter;
import com.elasticpath.importexport.common.dto.inventory.InventorySkuDTO;
import com.elasticpath.importexport.common.dto.inventory.InventoryWarehouseDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.order.OrderAllocationProcessor;
import com.elasticpath.service.store.WarehouseService;

/**
 * Inventory importer implementation.
 */
public class InventoryImporterImpl extends AbstractImporterImpl<ProductSku, InventorySkuDTO> {

	private InventorySkuAdapter inventorySkuAdapter;
	private ProductSkuService productSkuService;
	private WarehouseService warehouseService;
	private OrderAllocationProcessor orderAllocationProcessor;
	private final Map<String, Set<String>> skuCodesMap = new HashMap<String, Set<String>>();
	private ProductInventoryManagementService productInventoryManagementService;
	
	@Override
	public void initialize(final ImportContext context, final SavingStrategy<ProductSku, InventorySkuDTO> savingStrategy) {
		super.initialize(
				context,
				AbstractSavingStrategy.<ProductSku, InventorySkuDTO> createStrategy(ImportStrategyType.UPDATE,
						savingStrategy.getSavingManager()));
	}

	@Override
	protected ProductSku findPersistentObject(final InventorySkuDTO dto) {
		final ProductSku productSku = productSkuService.findBySkuCode(dto.getCode());
		if (productSku == null || getContext().getImportConfiguration().getImporterConfiguration(
				JobType.PRODUCT).getImportStrategyType().equals(ImportStrategyType.INSERT)
				&& !getContext().isProductChanged(productSku.getProduct().getCode())) {
			return null;
		}
		return productSku;
	}
	
	@Override
	protected String getDtoGuid(final InventorySkuDTO dto) {
		return dto.getCode();
	}
	
	@Override
	protected void setImportStatus(final InventorySkuDTO object) {
		getStatusHolder().setImportStatus("(for sku " + object.getCode() + ")");
	}

	@Override
	protected DomainAdapter<ProductSku, InventorySkuDTO> getDomainAdapter() {
		return inventorySkuAdapter;
	}

	@Override
	public String getImportedObjectName() {
		return InventorySkuDTO.ROOT_ELEMENT;
	}
	
	@Override
	public int getObjectsQty(final InventorySkuDTO dto) {
		return dto.getWarehouses().size();
	}


	@Override
	public void postProcessingImportHandling() {
		for (Entry<String, Set<String>> entry : skuCodesMap.entrySet()) {
			for (String warehouseCode : entry.getValue()) {
				orderAllocationProcessor.processOutstandingOrders(entry.getKey(), warehouseCode);
			}
		}
	}

	/**
	 * Gets inventorySkuAdapter.
	 * 
	 * @return the inventorySkuAdapter
	 */
	public InventorySkuAdapter getInventorySkuAdapter() {
		return inventorySkuAdapter;
	}

	/**
	 * Sets inventorySkuAdapter.
	 * 
	 * @param inventorySkuAdapter the inventorySkuAdapter to set
	 */
	public void setInventorySkuAdapter(final InventorySkuAdapter inventorySkuAdapter) {
		this.inventorySkuAdapter = inventorySkuAdapter;
	}

	/**
	 * Gets productSkuService.
	 * 
	 * @return the productSkuService
	 */
	public ProductSkuService getProductSkuService() {
		return productSkuService;
	}

	/**
	 * Sets productSkuService.
	 * 
	 * @param productSkuService the productSkuService to set
	 */
	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

	/**
	 * Gets warehouseService.
	 * 
	 * @return the warehouseService
	 */
	public WarehouseService getWarehouseService() {
		return warehouseService;
	}

	/**
	 * Sets warehouseService.
	 * 
	 * @param warehouseService the warehouseService to set
	 */
	public void setWarehouseService(final WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}
	
	/**
	 * Gets the orderAllocationProcessor.
	 * 
	 * @return the orderAllocationProcessor
	 */
	public OrderAllocationProcessor getOrderAllocationProcessor() {
		return orderAllocationProcessor;
	}

	/**
	 * Sets the orderAllocationProcessor.
	 * 
	 * @param orderAllocationProcessor the orderAllocationProcessor to set
	 */
	public void setOrderAllocationProcessor(final OrderAllocationProcessor orderAllocationProcessor) {
		this.orderAllocationProcessor = orderAllocationProcessor;
	}

	@Override
	protected CollectionsStrategy<ProductSku, InventorySkuDTO> getCollectionsStrategy() {
		return new InventoryCollectionsStrategy(skuCodesMap);
	}

	public void setProductInventoryManagementService(
			final ProductInventoryManagementService productInventoryManagementService) {
		this.productInventoryManagementService = productInventoryManagementService;
	}

	@Override
	public Class<? extends InventorySkuDTO> getDtoClass() {
		return InventorySkuDTO.class;
	}

	/**
	 * Implementation of <code>CollectionsStrategy</code> for inventory object.
	 */
	private final class InventoryCollectionsStrategy implements CollectionsStrategy<ProductSku, InventorySkuDTO> {
		
		private final Map<String, Set<String>> skuCodesMap;
		
		public InventoryCollectionsStrategy(final Map<String, Set<String>> skuCodesMap) {
			this.skuCodesMap = skuCodesMap;
		}

		@Override
		public void prepareCollections(final ProductSku domainObject, final InventorySkuDTO dto) {
			final Map<Long, InventoryDto> iwMap = productInventoryManagementService.getInventoriesForSku(domainObject.getSkuCode());
			
			final Iterator<Entry<Long, InventoryDto>> iter = iwMap.entrySet().iterator();

			while (iter.hasNext()) {
				final Warehouse warehouse = warehouseService.getWarehouse(iter.next().getKey());
				if (isInventoryExist(dto, warehouse.getCode())) {
					fillSkuMap(domainObject, warehouse);
				} else {
					iter.remove();
				}
			}
		}

		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}

		private void fillSkuMap(final ProductSku domainObject, final Warehouse warehouse) {
			Set<String> warehouseSet = skuCodesMap.get(domainObject.getSkuCode());
			if (warehouseSet == null) {
				warehouseSet = new HashSet<String>();
				skuCodesMap.put(domainObject.getSkuCode(), warehouseSet);
			}
			warehouseSet.add(warehouse.getCode());
		}

		private boolean isInventoryExist(final InventorySkuDTO dto, final String warehouseCode) {
			for (InventoryWarehouseDTO iwDTO : dto.getWarehouses()) {
				if (iwDTO.getCode().equals(warehouseCode)) {
					return true;
				}
			}
			return false;
		}
	}
}
