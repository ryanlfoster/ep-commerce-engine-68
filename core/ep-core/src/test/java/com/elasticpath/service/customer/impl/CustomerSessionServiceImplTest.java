/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.customer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.CustomerSessionMemento;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionMementoImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.customer.CustomerSessionShopperUpdateHandler;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shopper.impl.ShoppingCartMergerForShopperUpdates;
import com.elasticpath.service.shopper.impl.WishListMergerForShopperUpdates;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.impl.ShoppingCartMergerImpl;
import com.elasticpath.service.shoppingcart.impl.WishListServiceImpl;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>CustomerSessionServiceImpl</code>.
 */
public class CustomerSessionServiceImplTest {
	private static final String STORE_CODE = "STORE_CODE";

	private static final String CUSTOMER_SESSION_TEST_GUID = "testGuid";

	private static final String SHOPPING_START_TIME_TAG = "SHOPPING_START_TIME";

	private static final String SELLING_CHANNEL_TAG = "SELLING_CHANNEL";

	private CustomerSessionServiceImpl customerSessionServiceImpl;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine persistenceEngine;

    private BeanFactoryExpectationsFactory expectationsFactory;

    private BeanFactory beanFactory;

    private ShopperService shopperService;

    private ShoppingCartService shoppingCartService;

    private TimeService timeService;

	/**
	 * Sets up the test cases.
	 *
	 * @throws Exception in case of error
	 */
	@Before
	public void setUp() throws Exception {
		persistenceEngine = context.mock(PersistenceEngine.class);
        beanFactory = context.mock(BeanFactory.class);
        expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
        shopperService = context.mock(ShopperService.class);
        shoppingCartService = context.mock(ShoppingCartService.class);
        timeService = context.mock(TimeService.class);

		customerSessionServiceImpl = new CustomerSessionServiceImpl();
		customerSessionServiceImpl.setPersistenceEngine(persistenceEngine);
		customerSessionServiceImpl.setShopperService(shopperService);
		customerSessionServiceImpl.setTimeService(timeService);

		List<CustomerSessionShopperUpdateHandler> updateHandlers = new ArrayList<CustomerSessionShopperUpdateHandler>();
		CustomerSessionShopperUpdateHandler cartMerger =
			new ShoppingCartMergerForShopperUpdates(shoppingCartService, new ShoppingCartMergerImpl());
		CustomerSessionShopperUpdateHandler wishlistMerger = new WishListMergerForShopperUpdates(new WishListServiceImpl());

		updateHandlers.add(cartMerger);
		updateHandlers.add(wishlistMerger);

		customerSessionServiceImpl.setCustomerSessionUpdateHandlers(updateHandlers);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.add(CustomerSession)'.
	 */
	@Test
	public void testAddCustomerSession() {
		final CustomerSession customerSession = context.mock(CustomerSession.class);
		final CustomerSessionMemento customerSessionMemento = new CustomerSessionMementoImpl();

		context.checking(new Expectations() { {

			oneOf(customerSession).getCustomerSessionMemento();
			will(returnValue(customerSessionMemento));

			oneOf(persistenceEngine).save(customerSessionMemento);

		} });

		customerSessionServiceImpl.add(customerSession);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.findByGuid(String)'.
	 *
	 * Ensures that findByGuid does not create a blank CustomerSession when it doesn't find anything.
	 */
	@Test
	public void testFindCustomerSessionByGuidWithNullReturn() {
		final String guid = CUSTOMER_SESSION_TEST_GUID;
		context.checking(new Expectations() { {

			oneOf(persistenceEngine).retrieveByNamedQuery("CUSTOMER_SESSION_FIND_BY_GUID", CUSTOMER_SESSION_TEST_GUID);
			will(returnValue(Collections.emptyList()));

		} });

		final CustomerSession retrievedCustomerSession = customerSessionServiceImpl.findByGuid(guid);
		assertNull(retrievedCustomerSession);
	}

	/**
	 * Test initializing customer session for pricing.
	 */
	@Test
	public void testInitializeCustomerSessionForPricing() {
		final Date shoppingStartTime = new Date();
		final Currency currency = Currency.getInstance("CAD");

		context.checking(new Expectations() { {
			oneOf(timeService).getCurrentTime();
			will(returnValue(shoppingStartTime));
		} });

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.TAG_SET, new TagSet());

		final CustomerSession customerSession = new CustomerSessionImpl();
		final CustomerSessionMemento customerSessionMemento = new CustomerSessionMementoImpl();
		customerSession.setCustomerSessionMemento(customerSessionMemento);

		customerSessionServiceImpl.initializeCustomerSessionForPricing(customerSession, STORE_CODE, currency);

		TagSet tagSet = customerSession.getCustomerTagSet();
		assertNotNull("TagSet should not be null", tagSet);

		Tag storeTag = tagSet.getTagValue(SELLING_CHANNEL_TAG);
		assertEquals("Store tag is incorrect.", STORE_CODE, storeTag.getValue());

		Tag startTimeTag = tagSet.getTagValue(SHOPPING_START_TIME_TAG);
		assertEquals("Shopping start time tag is incorrect.", shoppingStartTime.getTime(), startTimeTag.getValue());

		assertEquals("Currency is incorrect.", currency, customerSession.getCurrency());
	}

}
