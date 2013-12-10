/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.collections.map.ListOrderedMap;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.persistence.PropertiesDao;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.impl.OrderServiceImpl;
import com.elasticpath.settings.SettingsReader;

/** Test cases for <code>IndexBuildServiceImpl</code>. */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class TopSellerServiceImplTest {

	private static final String COUNT_ORDER_SKU_BY_ORDER_DATE = "COUNT_ORDER_SKU_BY_ORDER_DATE";
	private TopSellerServiceImpl topSellerService;

	private PersistenceEngine persistenceEngine;
	private OrderServiceImpl orderService;
	private TimeService timeService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {
		topSellerService = new TopSellerServiceImpl() {
			@Override
			protected Collection<Long> updateProductSalesCount(final List<Object[]> products) {
				// overridden as all it does is update the database with the values of the map
				return Collections.emptyList();
			}
		};

		SettingsReader reader = context.mock(SettingsReader.class);
		topSellerService.setSettingsReader(reader);

		timeService = context.mock(TimeService.class);
		context.checking(new Expectations() {
			{
				allowing(timeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});
		topSellerService.setTimeService(timeService);

		ProductService productService = context.mock(ProductService.class);
		persistenceEngine = context.mock(PersistenceEngine.class);

		orderService = new OrderServiceImpl();
		orderService.setPersistenceEngine(persistenceEngine);

		final Properties topSellerProperties = new Properties();
		topSellerProperties.put("LastProcessedDate", "");

		final PropertiesDao propertiesDao = context.mock(PropertiesDao.class);
		context.checking(new Expectations() {
			{
				allowing(propertiesDao).loadProperties();
				will(returnValue(Collections.singletonMap("topSeller", topSellerProperties)));

				allowing(propertiesDao).getPropertiesFile("topSeller");
				will(returnValue(topSellerProperties));
			}
		});

		topSellerService.setProductService(productService);
		topSellerService.setOrderService(orderService);
		topSellerService.setPropertiesDao(propertiesDao);

		CategoryService categoryService = context.mock(CategoryService.class);
		topSellerService.setCategoryService(categoryService);

		topSellerService.setPersistenceEngine(persistenceEngine);
	}


	/**
	 * Test method for TopSellerService.calculateSalesCount() where last processed date is null.
	 */
	@Test
	public void testCalculateSalesCountDateNull() {
		final List<Object[]> orderList = new ArrayList<Object[]>();
		orderList.add(new Object[] { 2, 1L });

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(with(equalTo(COUNT_ORDER_SKU_BY_ORDER_DATE)),
						with(any(Object[].class)));
				will(returnValue(orderList));
			}
		});

		topSellerService.calculateSalesCount();
	}

	/**
	 * Test method for TopSellerService.calculateSalesCount() where last processed date is null.
	 */
	@Test
	public void testCalculateSalesCountDateNotNull() {

		final List<Object[]> orderList = new ArrayList<Object[]>();
		orderList.add(new Object[] { 1, 2L });

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(with(equalTo(COUNT_ORDER_SKU_BY_ORDER_DATE)),
						with(any(Object [].class)));
				will(returnValue(orderList));
			}
		});

		topSellerService.calculateSalesCount();
	}

	/**
	 * Test method for TopSellerService.getTopProductsFromDate().
	 */
	@Test
	public void testGetTopProductsFromDate() {
		final int quantity = 5;
		final int numProducts = 2;
		final Date date = new Date();

		final List<Object[]> orderList = new ArrayList<Object[]>();
		orderList.add(new Object[] { quantity * 2, 2L });
		orderList.add(new Object[] { quantity, 1L });

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(COUNT_ORDER_SKU_BY_ORDER_DATE, date);
				will(returnValue(orderList));
			}
		});

		ListOrderedMap results = topSellerService.getTopProductsFromDate(date, numProducts);
		assertEquals(numProducts, results.size());

		assertEquals(2L, results.get(0));
		assertEquals(quantity * 2, results.getValue(0));

		assertEquals(1L, results.get(1));
		assertEquals(quantity, results.getValue(1));
	}

	/** Test getting the top products where we've limited the results. */
	@Test
	public void testGetTopProductsFromDateLimited() {
		final int quantity = 5;
		final Date date = new Date();

		final List<Object[]> orderList = new ArrayList<Object[]>();
		orderList.add(new Object[] { quantity * 2, 2L });
		orderList.add(new Object[] { quantity, 1L });

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(COUNT_ORDER_SKU_BY_ORDER_DATE, date);
				will(returnValue(orderList));
			}
		});

		ListOrderedMap results = topSellerService.getTopProductsFromDate(date, 1);
		assertEquals(1, results.size());

		assertEquals(2L, results.get(0));
		assertEquals(quantity * 2, results.getValue(0));
	}

	/** When limiting top products by a negative number, we should just return an empty set. */
	@Test
	public void testGetTopProductsNegative() {
		final int quantity = 5;
		final Date date = new Date();

		final List<Object[]> orderList = new ArrayList<Object[]>();
		orderList.add(new Object[] { quantity * 2, 2L });
		orderList.add(new Object[] { quantity, 1L });

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(COUNT_ORDER_SKU_BY_ORDER_DATE, date);
				will(returnValue(orderList));
			}
		});

		ListOrderedMap results = topSellerService.getTopProductsFromDate(date, -1);
		assertTrue(results.isEmpty());
	}

	/**
	 * Test method for TopSellerService.getTopCategoriesFromDate().
	 */
	@Test
	public void testGetTopCategoriesFromDate() {
		final int quantity = 5;
		final int numProducts = 2;
		final Date date = new Date();

		final List<Object[]> orderList = new ArrayList<Object[]>();

		orderList.add(new Object[] { quantity, 2L });
		orderList.add(new Object[] { quantity * 2, 1L });

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(COUNT_ORDER_SKU_BY_ORDER_DATE, date);
				will(returnValue(orderList));
			}
		});

		ListOrderedMap results = topSellerService.getTopProductsFromDate(date, numProducts);
		assertEquals(numProducts, results.size());

		// check for category 1
		assertEquals(1L, results.get(0));
		assertEquals(quantity * 2, results.getValue(0));

		// check for category 2
		assertEquals(2L, results.get(1));
		assertEquals(quantity, results.getValue(1));
	}

	/** Makes sure products which happen to have the same quantities are both returned. */
	@Test
	public void testTopProductsFromDateDuplicateQuantities() {
		final List<Object[]> orderList = new ArrayList<Object[]>();

		final Date date = new Date();
		final int quantity = 3;
		// CHECKSTYLE:OFF -- these are UIDs, nothing special about these numbers
		orderList.add(new Object[] {quantity, 4L});
		orderList.add(new Object[] {quantity, 5L});
		orderList.add(new Object[] {quantity, 6L});
		// CHECKSTYLE:ON

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(COUNT_ORDER_SKU_BY_ORDER_DATE, date);
				will(returnValue(orderList));
			}
		});

		ListOrderedMap results = topSellerService.getTopProductsFromDate(date, orderList.size());
		assertEquals("Not all products returned!", orderList.size(), results.size());

		assertEquals("Incorrect quantity", quantity, results.getValue(0));
		assertEquals("Incorrect quantity", quantity, results.getValue(1));
		assertEquals("Incorrect quantity", quantity, results.getValue(2));

		Collection<Long> actualUids = Arrays.<Long>asList((Long) results.get(0), (Long) results.get(1), (Long) results.get(2));
		// CHECKSTYLE:OFF -- these are UIDs, nothing special about them
		// quantities are the same, we don't enforce order
		Assert.assertThat("Incorrect UIDs returned", actualUids, hasItems(4L, 5L, 6L));
		// CHECKSTYLE:ON
	}

	/**
	 * Test method for IndexBuildService.setPropertiesDao().
	 */
	@Test(expected = EpServiceException.class)
	public void testSetPropertiesDao() {
		topSellerService.setPropertiesDao(null);
		topSellerService.calculateSalesCount();
	}

	/**
	 * Test method for IndexBuildService.setProductService().
	 */
	@Test(expected = EpServiceException.class)
	public void testSetProductService() {
		topSellerService.setProductService(null);
		topSellerService.calculateSalesCount();
	}

}
