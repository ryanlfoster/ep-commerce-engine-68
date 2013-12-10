/**
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.test.integration.inventory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.WarehouseAddress;
import com.elasticpath.domain.store.impl.WarehouseAddressImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.service.store.WarehouseService;
import com.elasticpath.service.store.impl.WarehouseServiceImpl;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests {@link WarehouseServiceImpl}.
 */
public class WarehouseServiceImplTest extends BasicSpringContextTest {

	@Autowired
	private WarehouseService warehouseService;
	
	/**
	 * Tests that a WarehouseAddressImpl can have a null SubCountry.
	 * This is important for import/export because Clients may have warehouses in Countries without SubCountries.
	 */
	@DirtiesDatabase
	@Test
	public void testPersistWarehouseWithNullSubCountry() throws Exception {
		WarehouseAddress address = new WarehouseAddressImpl();
		address.setCity("");
		address.setCountry("");
		address.setStreet1("");
		address.setStreet2("");
		address.setSubCountry(null);
		address.setZipOrPostalCode("");
		Warehouse warehouse = new WarehouseImpl();
		warehouse.setCode("warehouseCode");
		warehouse.setName("warehouseName");
		warehouse.setAddress(address);
		warehouse = warehouseService.saveOrUpdate(warehouse);
	}
}
