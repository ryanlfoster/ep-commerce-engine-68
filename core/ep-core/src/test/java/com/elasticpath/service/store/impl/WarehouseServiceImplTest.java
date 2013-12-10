/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.store.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.store.WarehouseService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test case for {@link WarehouseServiceImpl}.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })

public class WarehouseServiceImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private WarehouseService warehouseServiceImpl;
	private PersistenceEngine persistenceEngine;
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {
		warehouseServiceImpl = new WarehouseServiceImpl();

		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		persistenceEngine = context.mock(PersistenceEngine.class);

		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.WAREHOUSE);
				will(returnValue(new WarehouseImpl()));
				allowing(beanFactory).getBeanImplClass(ContextIdNames.WAREHOUSE);
				will(returnValue(WarehouseImpl.class));
			}
		});

		warehouseServiceImpl.setPersistenceEngine(persistenceEngine);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.service.store.WarehouseServiceImpl.saveOrUpdate(Warehouse)'.
	 */
	@Test
	public void testSaveOrUpdate() {
		final Warehouse warehouse = new WarehouseImpl();
		final Warehouse updatedWarehouse = new WarehouseImpl();
		final String name = "testWarehouse";
		final long uidPk = 123456;
		warehouse.setName(name);
		warehouse.setUidPk(uidPk);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).saveOrUpdate(warehouse);
				will(returnValue(updatedWarehouse));
			}
		});

		final Warehouse returnedWarehouse = warehouseServiceImpl.saveOrUpdate(warehouse);
		assertSame(returnedWarehouse, updatedWarehouse);
	}

	/**
	 * Test method for 'com.elasticpath.service.store.WarehouseServiceImpl.remove(Warehouse)'.
	 */
	@Test
	public void testRemove() {
		final Warehouse warehouse = new WarehouseImpl();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).delete(warehouse);
			}
		});

		warehouseServiceImpl.remove(warehouse);
	}

	/**
	 * Test method for 'com.elasticpath.service.store.WarehouseServiceImpl.getWarehouse(Long)'.
	 */
	@Test
	public void testGetWarehouse() {
		final long uid = 1234L;
		final Warehouse warehouse = new WarehouseImpl();
		warehouse.setUidPk(uid);

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).get(WarehouseImpl.class, uid);
				will(returnValue(warehouse));
			}
		});

		assertSame(warehouse, warehouseServiceImpl.getWarehouse(uid));
		assertSame(warehouse, warehouseServiceImpl.getObject(uid));

		final long nonExistUid = 3456L;

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).get(WarehouseImpl.class, nonExistUid);
				will(returnValue(null));
			}
		});

		assertNull(warehouseServiceImpl.getWarehouse(nonExistUid));
		assertEquals("Should get a new warehouse instance with 0 uid", 0, warehouseServiceImpl.getWarehouse(0).getUidPk());
	}

	/**
	 * Test method for 'com.elasticpath.service.store.WarehouseServiceImpl.findAllWarehouseUids()'.
	 */
	@Test
	public void testFindAllWarehouseUids() {
		final List<Long> uidList = new ArrayList<Long>();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_ALL_WAREHOUSE_UIDS");
				will(returnValue(uidList));
			}
		});

		assertSame(uidList, warehouseServiceImpl.findAllWarehouseUids());

		// make sure the query returns something seemingly valid
		final Warehouse warehouse = new WarehouseImpl();
		final long warehouseUid = 1234L;
		warehouse.setUidPk(warehouseUid);
		uidList.add(warehouseUid);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_ALL_WAREHOUSE_UIDS");
				will(returnValue(uidList));
			}
		});

		assertSame(uidList, warehouseServiceImpl.findAllWarehouseUids());
	}

	/**
	 * Test method for 'com.elasticpath.service.store.WarehouseServiceImpl.findAllWarehouses()'.
	 */
	@Test
	public void testFindAllWarehouses() {
		final List<Warehouse> warehouseList = new ArrayList<Warehouse>();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_ALL_WAREHOUSES");
				will(returnValue(warehouseList));
			}
		});

		assertSame(warehouseList, warehouseServiceImpl.findAllWarehouses());

		// make sure the query returns something seemingly valid
		final Warehouse warehouse = new WarehouseImpl();
		final long warehouseUid = 1234L;
		warehouse.setUidPk(warehouseUid);
		warehouseList.add(warehouse);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_ALL_WAREHOUSES");
				will(returnValue(warehouseList));
			}
		});

		assertSame(warehouseList, warehouseServiceImpl.findAllWarehouses());
	}

	/**
	 * Test method for 'com.elasticpath.service.store.WarehouseServiceImpl.warehouseInUse()'.
	 */
	@Test
	public void testwarehouseInUse() {
		final List<Long> warehouseList = new ArrayList<Long>();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_WAREHOUSE_UIDS_IN_STORE_USE", 1L);
				will(returnValue(warehouseList));
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_WAREHOUSE_UIDS_IN_USER_USE", 1L);
				will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_WAREHOUSE_UIDS_IN_IMPORTJOB_USE", 1L);
				will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_WAREHOUSE_UIDS_IN_INVENTORY_USE", 1L);
				will(returnValue(Collections.emptyList()));
			}
		});

		assertFalse(warehouseServiceImpl.warehouseInUse(1L));

		final long warehouseUid = 1L;
		warehouseList.add(warehouseUid);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_WAREHOUSE_UIDS_IN_STORE_USE", warehouseUid);
				will(returnValue(warehouseList));
			}
		});

		assertTrue(warehouseServiceImpl.warehouseInUse(1L));

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_WAREHOUSE_UIDS_IN_STORE_USE", warehouseUid);
				will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_WAREHOUSE_UIDS_IN_USER_USE", warehouseUid);
				will(returnValue(warehouseList));
			}
		});
		assertTrue(warehouseServiceImpl.warehouseInUse(1L));
	}
	
	/**
	 * Test method for {@link WarehouseServiceImpl#getByCode(String)}.
	 */
	@Test(expected = EpServiceException.class)
	public void testGetByCode() {
		final String nonExistingCode = "some code";

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_WAREHOUSE_BY_CODE", (new Object[] { nonExistingCode }));
				will(returnValue(Collections.emptyList()));
			}
		});
		assertNull(warehouseServiceImpl.findByCode(nonExistingCode));

		final Warehouse warehouse = context.mock(Warehouse.class);
		final String existingCode = "another code";
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_WAREHOUSE_BY_CODE", (new Object[] { existingCode }));
				will(returnValue(Collections.singletonList(warehouse)));
			}
		});
		assertSame(warehouse, warehouseServiceImpl.findByCode(existingCode));

		final Warehouse warehouse2 = context.mock(Warehouse.class, "warehouse-2");
		final String inconsistentCode = "inconsistent code";
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("FIND_WAREHOUSE_BY_CODE", (new Object[] { inconsistentCode }));
				will(returnValue(Arrays.asList(warehouse, warehouse2)));
			}
		});

		warehouseServiceImpl.findByCode(inconsistentCode);
	}
}
