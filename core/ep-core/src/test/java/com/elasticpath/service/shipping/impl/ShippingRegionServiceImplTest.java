/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shipping.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
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
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.impl.ShippingRegionImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.shipping.ShippingRegionExistException;
import com.elasticpath.service.shipping.ShippingRegionService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>ShippingRegionServiceImpl</code>.
 */
public class ShippingRegionServiceImplTest {
	private static final String SHIPPINGREGION_FIND_BY_NAME = "SHIPPINGREGION_FIND_BY_NAME";

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private static final String TEST_REGION_NAME = "Canada BC";

	private ShippingRegionService shippingRegionServiceImpl;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine mockPersistenceEngine;

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	@Before
	public void setUp() throws Exception {
		shippingRegionServiceImpl = new ShippingRegionServiceImpl();
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		shippingRegionServiceImpl.setPersistenceEngine(mockPersistenceEngine);

		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBeanImplClass(ContextIdNames.SHIPPING_REGION);
				will(returnValue(ShippingRegionImpl.class));
			}
		});

	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test
	public void testSetPersistenceEngine() {
		shippingRegionServiceImpl.setPersistenceEngine(null);
		try {
			shippingRegionServiceImpl.add(new ShippingRegionImpl());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(shippingRegionServiceImpl.getPersistenceEngine());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.add(ShippingRegion)'.
	 */
	@Test
	public void testAdd() {
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		shippingRegion.setName(TEST_REGION_NAME);
		// expectations
		final Object[] parameters = new Object[] { shippingRegion.getName() };
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(with(same(shippingRegion)));

				allowing(mockPersistenceEngine).retrieveByNamedQuery(SHIPPINGREGION_FIND_BY_NAME, parameters);
				will(returnValue(new ArrayList<ShippingRegion>()));
			}
		});
		this.shippingRegionServiceImpl.add(shippingRegion);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.update(ShippingRegion)'.
	 */
	@Test
	public void testUpdate() {
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		final ShippingRegion updatedShippingRegion = new ShippingRegionImpl();
		shippingRegion.setUidPk(1L);
		shippingRegion.setName(TEST_REGION_NAME);
		// expectations
		final Object[] parameters = new Object[] { shippingRegion.getName() };
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(SHIPPINGREGION_FIND_BY_NAME, parameters);
				will(returnValue(new ArrayList<ShippingRegion>()));

				allowing(mockPersistenceEngine).merge(with(shippingRegion));
				will(returnValue(updatedShippingRegion));
			}
		});
		final ShippingRegion returnedShippingRegion = shippingRegionServiceImpl.update(shippingRegion);
		assertSame(returnedShippingRegion, updatedShippingRegion);
	}

	@Test(expected = ShippingRegionExistException.class)
	public void testUpdateFailure() {
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		shippingRegion.setUidPk(1L);
		shippingRegion.setName(TEST_REGION_NAME);
		// test faliure
		final ShippingRegion shippingRegion2 = new ShippingRegionImpl();
		shippingRegion2.setUidPk(2L);
		shippingRegion2.setName(TEST_REGION_NAME);
		final List<ShippingRegion> srList = new ArrayList<ShippingRegion>();
		srList.add(shippingRegion2);

		// expectations
		context.checking(new Expectations() {
			{
				final Object[] parameters = new Object[] { shippingRegion.getName() };
				allowing(mockPersistenceEngine).retrieveByNamedQuery(SHIPPINGREGION_FIND_BY_NAME, parameters);
				will(returnValue(srList));
			}
		});

		shippingRegionServiceImpl.update(shippingRegion);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.delete(ShippingRegion)'.
	 */
	@Test
	public void testDelete() {
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(with(same(shippingRegion)));
			}
		});
		shippingRegionServiceImpl.remove(shippingRegion);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.list()'.
	 */
	@Test
	public void testList() {
		final ShippingRegion shippingRegion1 = new ShippingRegionImpl();
		shippingRegion1.setName("aaa");
		final ShippingRegion shippingRegion2 = new ShippingRegionImpl();
		shippingRegion2.setName("bbb");
		final List<ShippingRegion> srList = new ArrayList<ShippingRegion>();
		srList.add(shippingRegion2);
		srList.add(shippingRegion1);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(with("SHIPPINGREGION_SELECT_ALL"), with(any(Object[].class)));
				will(returnValue(new ArrayList<ShippingRegion>(srList)));
			}
		});
		final List<ShippingRegion> retrievedSRList = shippingRegionServiceImpl.list();
		assertEquals(srList.size(), retrievedSRList.size());
		assertEquals(srList.get(0), retrievedSRList.get(0));
		assertEquals(srList.get(1), retrievedSRList.get(1));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ShippingRegionServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		shippingRegion.setName("aaa");
		shippingRegion.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).load(ShippingRegionImpl.class, uid);
				will(returnValue(shippingRegion));
			}
		});
		final ShippingRegion loadedShippingRegion = shippingRegionServiceImpl.load(uid);
		assertSame(shippingRegion, loadedShippingRegion);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ShippingRegionServiceImpl.get(Long)'.
	 */
	@Test
	public void testGet() {
		final long uid = 1234L;
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		shippingRegion.setName("aaa");
		shippingRegion.setUidPk(uid);
		// expectationss
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).get(ShippingRegionImpl.class, uid);
				will(returnValue(shippingRegion));
			}
		});
		final ShippingRegion loadedShippingRegion = shippingRegionServiceImpl.get(uid);
		assertSame(shippingRegion, loadedShippingRegion);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.nameExists(String)'.
	 */
	@Test
	public void testNameExists() {
		final String regionName = "test region";
		final String existRegionName = regionName;
		final Object[] parameters = new Object[] { existRegionName };
		final ShippingRegion shippingRegion1 = new ShippingRegionImpl();
		final long uidPk1 = 1L;
		shippingRegion1.setUidPk(uidPk1);
		shippingRegion1.setName(regionName);
		final List<ShippingRegion> srList = new ArrayList<ShippingRegion>();
		srList.add(shippingRegion1);

		// Test emailExists(String)email
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(SHIPPINGREGION_FIND_BY_NAME, parameters);
				will(returnValue(srList));
			}
		});
		assertTrue(shippingRegionServiceImpl.nameExists(existRegionName));

		// Test emailExists(Customer)
		final ShippingRegion shippingRegion2 = new ShippingRegionImpl();
		shippingRegion2.setName(regionName);
		assertTrue(shippingRegionServiceImpl.nameExists(shippingRegion2));
		final long uidPk2 = 2L;
		shippingRegion2.setUidPk(uidPk2);
		assertTrue(shippingRegionServiceImpl.nameExists(shippingRegion2));
		assertFalse(shippingRegionServiceImpl.nameExists(shippingRegion1));
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.findByName(String)'.
	 */
	@Test
	public void testFindByName() {
		final String regionName = "TEST REGION";
		final Object[] parameters = new Object[] { regionName };
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		shippingRegion.setName(regionName);
		final List<ShippingRegion> srList = new ArrayList<ShippingRegion>();
		srList.add(shippingRegion);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(SHIPPINGREGION_FIND_BY_NAME, parameters);
				will(returnValue(srList));
			}
		});

		final ShippingRegion retrievedSR = shippingRegionServiceImpl.findByName(regionName);
		assertSame(shippingRegion, retrievedSR);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.getAllRegions()'.
	 */
	@Test
	public void testGetAllRegions() {
		//TBD
	}
}
