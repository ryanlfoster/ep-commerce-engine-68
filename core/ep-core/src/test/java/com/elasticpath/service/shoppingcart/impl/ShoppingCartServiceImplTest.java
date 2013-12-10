/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


import java.util.Collections;
import java.util.Date;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.shopper.impl.ShopperImpl;
import com.elasticpath.domain.shopper.impl.ShopperMementoImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartMemento;
import com.elasticpath.domain.shoppingcart.ShoppingCartMementoHolder;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.test.factory.TestShopperFactory;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test suite for <code>ShoppingCartServiceImpl</code>.
 */
public class ShoppingCartServiceImplTest extends AbstractEPServiceTestCase {

	public static final String GUID = "guid";

	private ShoppingCartServiceImpl shoppingCartServiceImpl;
	private StoreImpl store;
	private StoreService storeService;
	private ShopperService shopperService;
	private ShopperImpl shopper;
	private TimeService timeService;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		store = new StoreImpl();
		store.setCode("SNAPITUP");

		shopper = new ShopperImpl();
		shopper.setShopperMemento(new ShopperMementoImpl());
		shopper.setUidPk(1L);
		shopper.setGuid("shopper-1");
		shopper.setStoreCode(store.getCode());

		storeService = context.mock(StoreService.class);
		shopperService = context.mock(ShopperService.class);
		timeService = context.mock(TimeService.class);

		shoppingCartServiceImpl = new ShoppingCartServiceImpl();
		shoppingCartServiceImpl.setPersistenceEngine(getPersistenceEngine());

		stubGetBean(ContextIdNames.SHOPPING_CART_MEMENTO, ShoppingCartMementoImpl.class);

		ShoppingCartImpl shoppingCartImpl = new ShoppingCartImpl();
		shoppingCartImpl.setShopper(TestShopperFactory.getInstance().createNewShopperWithMemento());

		stubGetBean(ContextIdNames.SHOPPING_CART, shoppingCartImpl);

		context.checking(new Expectations() { {
			allowing(timeService).getCurrentTime(); will(returnValue(new Date()));
		} });

		shoppingCartServiceImpl.setFetchPlanHelper(getFetchPlanHelper());
		shoppingCartServiceImpl.setShopperService(shopperService);
		shoppingCartServiceImpl.setStoreService(storeService);
		shoppingCartServiceImpl.setTimeService(timeService);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ShoppingCartServiceImpl.add(ShoppingCart)'.
	 */
	@Test
	public void testSaveOrUpdateHappyPath() {
		final ShoppingCartImpl cart = new ShoppingCartImpl() {
			private static final long serialVersionUID = 7030377614773551661L;

			@Override
			public void fireRules() {
				// no-op
			}
		};
		cart.setStore(store);
		final ShoppingCartMementoImpl cartMemento = new ShoppingCartMementoImpl();
		cart.setShoppingCartMemento(cartMemento);

		final ShoppingCartMemento updatedShoppingCartMemento = new ShoppingCartMementoImpl();
		updatedShoppingCartMemento.setStoreCode(store.getCode());
		// expectations
		context.checking(new Expectations() {
			{
				allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));

				oneOf(getMockPersistenceEngine()).saveOrUpdate(with(same(cart.getShoppingCartMemento())));
				will(returnValue(updatedShoppingCartMemento));

				atLeast(1).of(getMockFetchPlanHelper()).configureLoadTuner(with(aNull(LoadTuner.class)));
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();
			}
		});

		ShoppingCart saved = shoppingCartServiceImpl.saveOrUpdate(cart);
		assertEquals("ShoppingCartMemento saved with new storeCode", store, saved.getStore());
		assertEquals("Old memento should have store code set before save", store.getCode(), cartMemento.getStoreCode());
		assertEquals("New cart has store", store, saved.getStore());
		assertEquals("New memento has store code", store.getCode(), updatedShoppingCartMemento.getStoreCode());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ShoppingCartServiceImpl.update(ShoppingCart)'.
	 */
	@Test
	public void testUpdate1() {
		final boolean [] rulesFired = new boolean [1];
		final ShoppingCartImpl cart = new ShoppingCartImpl() {
			private static final long serialVersionUID = 8672600056837448053L;

			@Override
			public void fireRules() {
				rulesFired[0] = true;
			}
		};
		cart.setStore(store);
		cart.setShoppingCartMemento(new ShoppingCartMementoImpl());

		// expectations
		final ShoppingCartMemento updatedShoppingCartMemento = new ShoppingCartMementoImpl();
		updatedShoppingCartMemento.setStoreCode(store.getCode());
		context.checking(new Expectations() {
			{
				allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));

				oneOf(getMockPersistenceEngine()).saveOrUpdate(with(same(cart.getShoppingCartMemento())));
				will(returnValue(updatedShoppingCartMemento));

				atLeast(1).of(getMockFetchPlanHelper()).configureLoadTuner(with(aNull(LoadTuner.class)));
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();
			}
		});
		shoppingCartServiceImpl.saveOrUpdate(cart);
		assertTrue("Rules not fired", rulesFired[0]);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ShoppingCartServiceImpl.update(ShoppingCart)'.
	 */
	@Test
	public void testUpdate2() {
		final boolean [] rulesFired = new boolean [1];
		final ShoppingCartImpl cart = new ShoppingCartImpl() {
			private static final long serialVersionUID = -1554812828187510348L;

			@Override
			public void fireRules() {
				rulesFired[0] = true;
			}
		};
		cart.setStore(store);
		cart.setShoppingCartMemento(new ShoppingCartMementoImpl());
		cart.getShoppingCartMemento().setUidPk(1L);

		// expectations
		final ShoppingCartMemento updatedShoppingCartMemento = new ShoppingCartMementoImpl();
		updatedShoppingCartMemento.setStoreCode(store.getCode());
		context.checking(new Expectations() {
			{
				allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));

				oneOf(getMockPersistenceEngine()).saveOrUpdate(with(same(cart.getShoppingCartMemento())));
				will(returnValue(updatedShoppingCartMemento));

				atLeast(1).of(getMockFetchPlanHelper()).configureLoadTuner(with(aNull(LoadTuner.class)));
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();
			}
		});
		shoppingCartServiceImpl.saveOrUpdate(cart);
		assertTrue("Rules not fired", rulesFired[0]);
	}

	@Test
	public void testFindByGuid() {
		final ShoppingCartMemento memento = new ShoppingCartMementoImpl();
		memento.setGuid(GUID);
		memento.setStoreCode(store.getCode());
		memento.setShopper(shopper);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(
						with(ShoppingCartServiceImpl.SHOPPING_CART_FIND_BY_GUID_EAGER), with(any(Object[].class)));
				will(returnValue(Collections.singletonList(memento)));

				allowing(shopperService).get(shopper.getUidPk()); will(returnValue(shopper));

				allowing(getMockFetchPlanHelper()).configureLoadTuner(with(aNull(LoadTuner.class)));
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();

				allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));
			}
		});

		final ShoppingCart loadedCart = shoppingCartServiceImpl.findByGuid(GUID);
		assertSame("Shopping Cart should have its store loaded", store, loadedCart.getStore());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ShoppingCartServiceImpl.getObject(long)'.
	 */
	@Test
	public void testGetObject() {
		final long uid = 1234L;
		final ShoppingCartMementoImpl cartMemento = new ShoppingCartMementoImpl();
		cartMemento.setGuid(GUID);
		cartMemento.setStoreCode(store.getCode());
		cartMemento.setShopper(shopper);

		final ShoppingCartImpl cart = new ShoppingCartImpl();
		cart.setShoppingCartMemento(cartMemento);

		stubGetBean(ContextIdNames.SHOPPING_CART_MEMENTO, ShoppingCartMementoImpl.class);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).load(ShoppingCartMementoImpl.class, uid);
				will(returnValue(cartMemento));

				allowing(shopperService).get(shopper.getUidPk()); will(returnValue(shopper));
				allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));

				atLeast(1).of(getMockFetchPlanHelper()).configureLoadTuner(with(aNull(LoadTuner.class)));
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();
			}
		});

		final ShoppingCart loadedCart = (ShoppingCart) shoppingCartServiceImpl.getObject(uid);
		assertSame(cart.getShoppingCartMemento(), ((ShoppingCartMementoHolder) loadedCart).getShoppingCartMemento());
		assertSame("Shopping Cart should have its store loaded", store, loadedCart.getStore());
	}

	@Test
	public void testFindOrCreateByShopperHappyFindPath() {
		final ShoppingCartMemento memento = new ShoppingCartMementoImpl();
		memento.setGuid(GUID);
		memento.setStoreCode(store.getCode());
		memento.setShopper(shopper);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(
						with(ShoppingCartServiceImpl.SHOPPING_CART_FIND_BY_SHOPPER_UID), with(any(Object[].class)));
				will(returnValue(Collections.singletonList(memento)));

				allowing(shopperService).get(shopper.getUidPk()); will(returnValue(shopper));

				allowing(getMockFetchPlanHelper()).configureLoadTuner(with(aNull(LoadTuner.class)));
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();

				allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));
			}
		});

		final ShoppingCart loadedCart = shoppingCartServiceImpl.findOrCreateByShopper(shopper);
		assertSame("Shopping Cart should have its store loaded", store, loadedCart.getStore());
	}

	@Test
	public void testTouch() {
		final ShoppingCartMemento memento = new ShoppingCartMementoImpl();
		memento.setGuid("memento-guid");

		final ShoppingCartMemento updatedShoppingCartMemento = new ShoppingCartMementoImpl();
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(
						with(ShoppingCartServiceImpl.SHOPPING_CART_FIND_BY_GUID_EAGER), with(any(Object[].class)));
				will(returnValue(Collections.singletonList(memento)));
				oneOf(getMockPersistenceEngine()).saveOrUpdate(with(same(memento)));
				will(returnValue(updatedShoppingCartMemento));
			}
		});

		// When
		shoppingCartServiceImpl.touch(memento.getGuid());

		// Then - saveOrUpdate() called plus...
		assertNotNull("Last modified date should be set before update", memento.getLastModifiedDate());
	}

	@Test
	public void testSaveIfNotPersistedWithUnpersistedCart() {
		final ShoppingCartImpl cart = new ShoppingCartImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public void fireRules() {
				// no-op
			}
		};
		cart.setStore(store);
		cart.setShoppingCartMemento(new ShoppingCartMementoImpl());

		final ShoppingCartMemento updatedShoppingCartMemento = new ShoppingCartMementoImpl();
		updatedShoppingCartMemento.setStoreCode(store.getCode());
		// expectations
		context.checking(new Expectations() {
			{
				allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));

				oneOf(getMockPersistenceEngine()).saveOrUpdate(with(same(cart.getShoppingCartMemento())));
				will(returnValue(updatedShoppingCartMemento));

				allowing(getMockFetchPlanHelper()).configureLoadTuner(with(aNull(LoadTuner.class)));
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();
			}
		});

		// When
		ShoppingCart returned = shoppingCartServiceImpl.saveIfNotPersisted(cart);
		assertNotNull(returned);
	}

	@Test
	public void testSaveIfNotPersistedWithPreviouslyPersistedCart() {
		final ShoppingCartImpl cart = new ShoppingCartImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public void fireRules() {
				// no-op
			}
		};
		cart.setShoppingCartMemento(new ShoppingCartMementoImpl());
		cart.getShoppingCartMemento().setUidPk(1L);

		// When
		ShoppingCart returned = shoppingCartServiceImpl.saveIfNotPersisted(cart);
		assertSame(cart, returned);
	}
}
