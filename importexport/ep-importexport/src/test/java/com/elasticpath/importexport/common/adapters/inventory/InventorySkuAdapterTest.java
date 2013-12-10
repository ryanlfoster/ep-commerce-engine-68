package com.elasticpath.importexport.common.adapters.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.inventory.InventorySkuDTO;
import com.elasticpath.importexport.common.dto.inventory.InventoryWarehouseDTO;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.impl.InventoryDtoImpl;
import com.elasticpath.service.catalog.ProductInventoryManagementService;

/**
 * Tests PriceListDescriptorAdapter.
 */
public class InventorySkuAdapterTest {

	private static final String WAREHOUSE_CODE = "warehouse_code";

	private static final Long WAREHOUSE_UID = 100L;

	private static final String SKU_CODE = "sku_code";
	
	private static final Date RESTOCK_DATE = new Date();
	
	private static final Integer ALLOCATED = 4;
	
	private static final Integer ONHAND = 15;
	
	private static final Integer REORDERMIN = 2;
	
	private static final Integer REORDERQTY = 3;
	
	private static final Integer RESERVED = 1;
	
	private static final Integer AVAILABLE = ONHAND - RESERVED - ALLOCATED;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CachingService mockCachingService;
	
	private ElasticPath mockElasticPath;
	
	private InventoryDto mockInventory;
	
	private Warehouse mockWarehouse;

	private ProductInventoryManagementService mockProductInventoryManagementService;

	private InventorySkuAdapter inventorySkuAdapter;
	
	/**
	 * Setup tests.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		mockInventory = context.mock(InventoryDto.class);
		mockElasticPath = context.mock(ElasticPath.class);
		mockCachingService = context.mock(CachingService.class);
		
		mockProductInventoryManagementService = context.mock(ProductInventoryManagementService.class);
		
		mockWarehouse = context.mock(Warehouse.class);

		context.checking(new Expectations() {
			{
				allowing(mockWarehouse).getUidPk();
				will(returnValue(WAREHOUSE_UID));

				allowing(mockWarehouse).getCode();
				will(returnValue(WAREHOUSE_CODE));

				allowing(mockCachingService).findWarehouseByCode(WAREHOUSE_CODE);
				will(returnValue(mockWarehouse));

				allowing(mockCachingService).findAllWarehouses();
				will(returnValue(Arrays.asList(mockWarehouse)));

				allowing(mockElasticPath).getBean(ContextIdNames.PRODUCT_SKU);
				will(returnValue(new ProductSkuImpl()));

				allowing(mockElasticPath).getBean(ContextIdNames.INVENTORYDTO);
				will(returnValue(new InventoryDtoImpl()));
			}
		});
		
		inventorySkuAdapter = new InventorySkuAdapter(mockProductInventoryManagementService);
		inventorySkuAdapter.setElasticPath(mockElasticPath);
		inventorySkuAdapter.setCachingService(mockCachingService);
	}

	/**
	 * Tests population of DTO.
	 * TODO: Not yet implemented
	 */
	@Test
	public void testPopulateDTO() {
		final ProductSku mockDomain = context.mock(ProductSku.class);

		context.checking(new Expectations() {
			{
				atLeast(1).of(mockDomain).getSkuCode();
				will(returnValue(SKU_CODE));

				oneOf(mockInventory).getAllocatedQuantity();
				will(returnValue(ALLOCATED));

				oneOf(mockInventory).getQuantityOnHand();
				will(returnValue(ONHAND));

				oneOf(mockInventory).getReorderMinimum();
				will(returnValue(REORDERMIN));

				oneOf(mockInventory).getReorderQuantity();
				will(returnValue(REORDERQTY));

				oneOf(mockInventory).getReservedQuantity();
				will(returnValue(RESERVED));

				oneOf(mockInventory).getAvailableQuantityInStock();
				will(returnValue(AVAILABLE));

				oneOf(mockInventory).getRestockDate();
				will(returnValue(RESTOCK_DATE));
			}
		});
		
		final HashMap<Warehouse, InventoryDto> iwMap = new HashMap<Warehouse, InventoryDto>();
		iwMap.put(mockWarehouse, mockInventory);

		context.checking(new Expectations() {
			{
				oneOf(mockProductInventoryManagementService).getInventoriesForSkuInWarehouses(SKU_CODE, Arrays.asList(mockWarehouse));
				will(returnValue(iwMap));
			}
		});
		
		InventorySkuDTO dto = inventorySkuAdapter.createDtoObject();
		
		inventorySkuAdapter.populateDTO(mockDomain, dto);
				
		assertEquals(SKU_CODE, dto.getCode());
		List<InventoryWarehouseDTO> inventoryWarehouseDTOs = dto.getWarehouses(); 
		
		assertEquals(1, inventoryWarehouseDTOs.size());
		
		InventoryWarehouseDTO inventoryWarehouseDTO = inventoryWarehouseDTOs.get(0); 
		
		assertEquals(Integer.valueOf(ALLOCATED), inventoryWarehouseDTO.getAllocated());
		assertEquals(Integer.valueOf(AVAILABLE), inventoryWarehouseDTO.getAvaliable());
		assertEquals(Integer.valueOf(ONHAND), inventoryWarehouseDTO.getOnHand());
		assertEquals(Integer.valueOf(REORDERMIN), inventoryWarehouseDTO.getReorderMin());
		assertEquals(Integer.valueOf(REORDERQTY), inventoryWarehouseDTO.getReorderQty());
		assertEquals(Integer.valueOf(RESERVED), inventoryWarehouseDTO.getReserved());
		assertEquals(RESTOCK_DATE, inventoryWarehouseDTO.getExpectedRestockDate());
		assertEquals(WAREHOUSE_CODE, inventoryWarehouseDTO.getCode());
	}
	

	/**
	 * Tests population of Domain.
	 */
	@Test
	public void testPopulateDomain() {
		InventorySkuDTO dto = inventorySkuAdapter.createDtoObject();
		
		dto.setCode(SKU_CODE);
		final List<InventoryWarehouseDTO> warehouses = new ArrayList<InventoryWarehouseDTO>();
		
		final InventoryWarehouseDTO inventoryWarehouseDto = new InventoryWarehouseDTO();
		
		inventoryWarehouseDto.setAllocated(ALLOCATED);
		inventoryWarehouseDto.setAvaliable(AVAILABLE);
		inventoryWarehouseDto.setOnHand(ONHAND);
		inventoryWarehouseDto.setReorderMin(REORDERMIN);
		inventoryWarehouseDto.setReorderQty(REORDERQTY);
		inventoryWarehouseDto.setReserved(RESERVED);
		inventoryWarehouseDto.setCode(WAREHOUSE_CODE);
		inventoryWarehouseDto.setExpectedRestockDate(RESTOCK_DATE);
				
		warehouses.add(inventoryWarehouseDto);
		dto.setWarehouses(warehouses);

		final ProductSku mockDomain = context.mock(ProductSku.class);

		context.checking(new Expectations() {
			{
				oneOf(mockDomain).setSkuCode(SKU_CODE);
				oneOf(mockDomain).getSkuCode();
				will(returnValue(SKU_CODE));

				oneOf(mockProductInventoryManagementService).getInventory(SKU_CODE, WAREHOUSE_UID);
				will(returnValue(null));

				oneOf(mockProductInventoryManagementService).merge(with(any(InventoryDto.class)));
				will(returnValue(null));
			}
		});
		
		inventorySkuAdapter.populateDomain(dto, mockDomain);
	}

	
	
	/**
	 * Tests findWarehouseUID.
	 */
	@Test
	public void testFindWarehouseUID() {
		final String unknownWarehouseCode = "UnknownWarehouse";
		context.checking(new Expectations() {
			{
				oneOf(mockCachingService).findWarehouseByCode(unknownWarehouseCode);
				will(returnValue(null));
			}
		});
		assertEquals("Unknown Warehouse code", Long.valueOf(-1L), inventorySkuAdapter.findWarehouseUID(unknownWarehouseCode));
		assertEquals(WAREHOUSE_UID, inventorySkuAdapter.findWarehouseUID(WAREHOUSE_CODE));
	}
	
	/**
	 * Tests checkNotNegative for Error.
	 */
	@Test
	public void testCheckNotNegative() {
		final String message = "message";
		assertFalse(inventorySkuAdapter.checkNotNegative(1, message)); // check should not throw exception here
		assertTrue(inventorySkuAdapter.checkNotNegative(-1, message));
	}
		
	/**
	 * Tests creation of Domain Object.
	 */
	@Test
	public void testCreateDomainObject() {
		assertNotNull(inventorySkuAdapter.createDomainObject());
	}

	/**
	 * Tests creation of DTO Object.
	 */
	@Test
	public void testCreateDtoObject() {		
		assertNotNull(inventorySkuAdapter.createDtoObject());
	}
}
