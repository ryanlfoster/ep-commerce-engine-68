package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.inventory.InventorySkuAdapter;
import com.elasticpath.importexport.common.dto.inventory.InventorySkuDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuService;

/**
 * Exporter implementation for productSku and inventorySku.
 */
public class InventoryExporterImpl extends AbstractExporterImpl<ProductSku, InventorySkuDTO, Long> {
	
	private InventorySkuAdapter inventorySkuAdapter;

	private ProductSkuService productSkuService;
	
	private static final Logger LOG = Logger.getLogger(InventoryExporterImpl.class);
	
	private ProductInventoryManagementService productInventoryManagementService;
	
	@Override
	protected void initializeExporter(final ExportContext context) {
		// do nothing
	}

	@Override
	protected List<ProductSku> findByIDs(final List<Long> subList) {
		List<ProductSku> productSkus = new ArrayList<ProductSku>();
		for (Long uid : subList) {
			ProductSku productSku = productSkuService.get(uid);
			if (productSku == null) {
				LOG.error(new Message("IE-22000", uid.toString()));
				continue;
			}
			
			productSkus.add(productSku);
		}
		return productSkus;
	}

	@Override
	protected int getObjectsQty(final ProductSku domain) {
		Map<Long, InventoryDto> inventories = productInventoryManagementService.getInventoriesForSku(domain.getSkuCode());
		return inventories.size();
	}

	@Override
	protected DomainAdapter<ProductSku, InventorySkuDTO> getDomainAdapter() {
		return inventorySkuAdapter;
	}

	@Override
	protected Class<? extends InventorySkuDTO> getDtoClass() {
		return InventorySkuDTO.class;
	}

	@Override
	protected List<Long> getListExportableIDs() {
		return new ArrayList<Long>(getContext().getDependencyRegistry().getDependentUids(ProductSku.class));
	}

	@Override
	public Class< ? >[] getDependentClasses() {
		return new Class< ? >[] {ProductSku.class};
	}

	@Override
	public JobType getJobType() {
		return JobType.INVENTORY;
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

	public void setProductInventoryManagementService(
			final ProductInventoryManagementService productInventoryManagementService) {
		this.productInventoryManagementService = productInventoryManagementService;
	}
}
