package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.importexport.common.adapters.inventory.InventorySkuAdapter;
import com.elasticpath.importexport.common.dto.inventory.InventorySkuDTO;
import com.elasticpath.importexport.common.dto.inventory.InventoryWarehouseDTO;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.domain.impl.InventoryImpl;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.order.OrderAllocationProcessor;
import com.elasticpath.service.store.WarehouseService;

/**
 * Test for <code>InventoryImporterImpl</code>.
 */

public class InventoryImporterImplTest {

	private static final String WAREHOUSE_CODE = "warehouseCode1";

	private static final String SKU_CODE = "skuCode01";
	
	private static final String SKU_CODE2 = "skuCode02";

	private InventoryImporterImpl inventoryImporter;

	private SavingStrategy<ProductSku, InventorySkuDTO> mockSavingStrategy;

	private ProductSkuService mockProductSkuService;
	
	private ProductInventoryManagementService mockProductInventoryManagementService;

	private InventorySkuDTO inventorySkuDTO;

	private ProductSku productSku;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Before
	public void setUp() throws Exception {
		inventorySkuDTO = new InventorySkuDTO();
		inventorySkuDTO.setCode(SKU_CODE);

		productSku = new ProductSkuImpl();
		productSku.setUidPk(1L);
		productSku.setSkuCode(SKU_CODE);

		inventoryImporter = new InventoryImporterImpl();

		mockSavingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT, new SavingManager<ProductSku>() {

			@Override
			public void save(final ProductSku persistable) {
				//do nothing
			}

			@Override
			public ProductSku update(final ProductSku persistable) {
				return null;
			}
			
		});

		mockProductSkuService = context.mock(ProductSkuService.class);
		mockProductInventoryManagementService = context.mock(ProductInventoryManagementService.class);
		
		context.checking(new Expectations() {
			{
				allowing(mockProductSkuService).findBySkuCode(SKU_CODE); will(returnValue(productSku));
				allowing(mockProductInventoryManagementService).getInventoriesForSku(SKU_CODE); will(returnValue(Collections.emptyMap()));
			}
		});
		
		
		inventoryImporter.setProductSkuService(mockProductSkuService);
		inventoryImporter.setInventorySkuAdapter(new MockInventorySkuAdapter(mockProductInventoryManagementService));
		inventoryImporter.setStatusHolder(new ImportStatusHolder());
		inventoryImporter.setProductInventoryManagementService(mockProductInventoryManagementService);
	}

	/**
	 * Check an import with non-initialized importer.
	 */
	@Test(expected = ImportRuntimeException.class)
	public void testExecuteNonInitializedImport() {
		inventoryImporter.executeImport(inventorySkuDTO);
	}

	/**
	 * Check an import of inventory.
	 */
	@Test
	public void testExecuteImport() {

		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<JobType, ImporterConfiguration>());

		inventoryImporter.initialize(new ImportContext(importConfiguration), mockSavingStrategy);
		inventoryImporter.executeImport(inventorySkuDTO);
		assertEquals("sku", inventoryImporter.getImportedObjectName());
		assertNotNull(inventoryImporter.getProductSkuService());
		assertNotNull(inventoryImporter.getSavingStrategy());
	}

	/**
	 * Test for getObjectsQty method.
	 */
	@Test
	public void testGetObjectsQty() {
		InventorySkuDTO newInventorySkuDTO = new InventorySkuDTO();
		List<InventoryWarehouseDTO> inventoryWarehouseList = new ArrayList<InventoryWarehouseDTO>();
		inventoryWarehouseList.add(new InventoryWarehouseDTO());
		inventoryWarehouseList.add(new InventoryWarehouseDTO());
		newInventorySkuDTO.setWarehouses(inventoryWarehouseList);
		assertEquals(2, inventoryImporter.getObjectsQty(newInventorySkuDTO));
	}	
	
	/**
	 * Test process allocation orders after import.
	 */
	@Test
	public void testProcessAllocationOrders() {
		
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<JobType, ImporterConfiguration>());
		
		inventoryImporter.initialize(new ImportContext(importConfiguration), mockSavingStrategy);
				
		ProductSku productSku = new ProductSkuImpl();
		productSku.setUidPk(1L);
		productSku.setSkuCode(SKU_CODE2);
		
		Inventory inventory = new InventoryImpl();
		inventory.setSkuCode(productSku.getSkuCode());
		inventory.setWarehouseUid(1L);

		final Map<Long, Inventory> inventories = new HashMap<Long, Inventory>();
		inventories.put(inventory.getWarehouseUid(), inventory);
					
		context.checking(new Expectations() {
			{
				allowing(mockProductInventoryManagementService).getInventoriesForSku(SKU_CODE2); will(returnValue(inventories));
			}
		});
		
		InventorySkuDTO inventorySkuDTO = new InventorySkuDTO();
		inventorySkuDTO.setCode(SKU_CODE2);
		
		List<InventoryWarehouseDTO> warehouses = new ArrayList<InventoryWarehouseDTO>();
		InventoryWarehouseDTO inventoryWarehouseDTO = new InventoryWarehouseDTO();
		inventoryWarehouseDTO.setCode(WAREHOUSE_CODE);
		warehouses.add(inventoryWarehouseDTO);
		inventorySkuDTO.setWarehouses(warehouses);
		
		final WarehouseService mockWarehouseService = context.mock(WarehouseService.class);
		
		final Warehouse warehouse = new WarehouseImpl();
		warehouse.setUidPk(1L);
		warehouse.setCode(WAREHOUSE_CODE);

		context.checking(new Expectations() {
			{
				oneOf(mockWarehouseService).getWarehouse(1L);
				will(returnValue(warehouse));
			}
		});
		inventoryImporter.setWarehouseService(mockWarehouseService);
		
		CollectionsStrategy<ProductSku, InventorySkuDTO> collectionsStrategy = inventoryImporter.getCollectionsStrategy();
		
		collectionsStrategy.prepareCollections(productSku, inventorySkuDTO);
		
		final OrderAllocationProcessor mockOrderAllocationProcessor = context.mock(OrderAllocationProcessor.class);
		context.checking(new Expectations() {
			{
				oneOf(mockOrderAllocationProcessor).processOutstandingOrders(SKU_CODE2, WAREHOUSE_CODE);
			}
		});
		inventoryImporter.setOrderAllocationProcessor(mockOrderAllocationProcessor);
		
		inventoryImporter.postProcessingImportHandling();
	}

	/** The import classes should at least contain the DTO class we are operating on. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", InventorySkuDTO.class, inventoryImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(inventoryImporter.getAuxiliaryJaxbClasses());
	}

	/**
	 * Mock inventory sku adapter.
	 */
	private class MockInventorySkuAdapter extends InventorySkuAdapter {
		
		public MockInventorySkuAdapter(final ProductInventoryManagementService mockProductInventoryManagementService) {
			super(mockProductInventoryManagementService);
		}

		@Override
		public void populateDomain(final InventorySkuDTO source, final ProductSku target) {
			// do nothing
		}

		@Override
		public void populateDTO(final ProductSku source, final InventorySkuDTO target) {
			// do nothing
		}

	}
}
