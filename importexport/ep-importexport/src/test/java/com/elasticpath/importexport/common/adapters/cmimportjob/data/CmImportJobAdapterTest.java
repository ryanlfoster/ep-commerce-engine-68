package com.elasticpath.importexport.common.adapters.cmimportjob.data;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobImpl;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.importexport.common.dto.cmimportjob.CmImportJobDTO;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.store.WarehouseService;

/**
 * Tests CmImportJobAdapter functionality.
 */
public class CmImportJobAdapterTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String STORE = "STORE";
	private static final String CATALOG = "CATALOG";
	private static final String WAREHOUSE = "WAREHOUSE";
	private final CmImportJobAdapter adapter = new CmImportJobAdapter();

	private final WarehouseService warehouseService = context.mock(WarehouseService.class);	
	private final StoreService storeService = context.mock(StoreService.class);
	private final CatalogService catalogService = context.mock(CatalogService.class);
	
	private CmImportJobDTO dto;
	
	/**
	 * Test set up.
	 */
	@Before
	public void setUp() {
		adapter.setCatalogService(catalogService);
		adapter.setStoreService(storeService);
		adapter.setWarehouseService(warehouseService);
		dto = new CmImportJobDTO();
		dto.setImportType(AbstractImportTypeImpl.INSERT_UPDATE_TYPE.getTypeId());
	}
	
	/**
	 * Tests that the domain object is populated with the dependent domain objects if their codes in the DTO are not empty.
	 */
	@Test
	public void testPopulateDomainWithNotEmptyStoreWarehouseCatalogCodesInDTO() {
		dto = populateDtoWithNotEmptyStoreWarehouseCatalogCodes(dto);
		
		ImportJob target = new ImportJobImpl();
		
		context.checking(new Expectations() { { //NOPMD
			oneOf(warehouseService).findByCode(WAREHOUSE); will(returnValue(new WarehouseImpl()));
			oneOf(storeService).findStoreWithCode(STORE); will(returnValue(new StoreImpl()));
			oneOf(catalogService).findByCode(CATALOG); will(returnValue(new CatalogImpl()));
		} });
		
		adapter.populateDomain(dto, target);
		
		
		assertNotNull(target.getCatalog());
		assertNotNull(target.getStore());
		assertNotNull(target.getWarehouse());
	}

	private CmImportJobDTO populateDtoWithNotEmptyStoreWarehouseCatalogCodes(final CmImportJobDTO dto) {
		dto.setCatalogueGuid(CATALOG);
		dto.setWarehouseGuid(WAREHOUSE);
		dto.setStoreGuid(STORE);
		return dto;
	}

	/**
	 * Tests that the domain object is not populated with the dependent domain objects if their codes in the DTO are empty
	 * and that those domain objects are cleared from the import job.
	 */
	@Test
	public void testPopulateDomainWithEmptyStoreWarehouseCatalogCodesInDTO() {
		ImportJob importJob = new ImportJobImpl();
		importJob.setStore(new StoreImpl());
		importJob.setWarehouse(new WarehouseImpl());
		importJob.setCatalog(new CatalogImpl());
		
		context.checking(new Expectations() { { //NOPMD
			never(warehouseService).findByCode(WAREHOUSE);
			never(storeService).findStoreWithCode(STORE);
			never(catalogService).findByCode(CATALOG);
		} });
		
		adapter.populateDomain(dto, importJob);
		
		
		assertNull(importJob.getCatalog());
		assertNull(importJob.getStore());
		assertNull(importJob.getWarehouse());
	}
}
