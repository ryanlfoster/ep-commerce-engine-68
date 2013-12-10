/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.store.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.hamcrest.collection.IsArray;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.FlushMode;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test case for <code>StoreServiceImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports", "PMD.TooManyMethods" })
public class StoreServiceImplTest {

	private static final String STORE_DISPLAYABLE = "STORE_DISPLAYABLE_FLAG";

	private static final String STORE_NAME = "STORE_NAME";

	private static final String FIND_ALL_STORES = "FIND_ALL_STORES";

	private static final String FIND_ALL_COMPLETE_STORES = "FIND_ALL_COMPLETE_STORES";

	private StoreServiceImpl storeServiceImpl;

	private static final String PLACEHOLDER_FOR_LIST = "list";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private final PersistenceEngine persistenceEngine = context.mock(PersistenceEngine.class);
	private final PersistenceSession persistenceSession = context.mock(PersistenceSession.class);
	private final FetchPlanHelper fetchPlanHelper = context.mock(FetchPlanHelper.class);
	private final IndexNotificationService indexNotificationService = context.mock(IndexNotificationService.class);
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception in case of any errors
	 */
	@Before
	public void setUp() throws Exception {
		setupPersistenceEngine();
		setupElasticPath();

		storeServiceImpl = new StoreServiceImpl();
		storeServiceImpl.setPersistenceEngine(persistenceEngine);
		storeServiceImpl.setFetchPlanHelper(fetchPlanHelper);
		storeServiceImpl.setIndexNotificationService(indexNotificationService);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	private void setupElasticPath() {
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		context.checking(new Expectations() { {
			allowing(beanFactory).getBean(ContextIdNames.PRODUCT_SEARCH_CRITERIA); will(returnValue(new ProductSearchCriteria()));
		} });
	}

	private void setupPersistenceEngine() {
		context.checking(new Expectations() { {
			allowing(persistenceEngine).initialize(with(anything()));
			allowing(persistenceEngine).getSharedPersistenceSession(); will(returnValue(persistenceSession));
		} });
	}

	/**
	 * Tests the save or update method for store service.
	 */
	@Test
	public void testSaveOrUpdate() {
		final Store store = new StoreImpl();
		final List<SearchCriteria> result = new ArrayList<SearchCriteria>();
		result.add(new ProductSearchCriteria());
		final MockUpdateNotification mockUpdateNotification = context.mock(MockUpdateNotification.class);

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).saveOrUpdate(with(store)); will(returnValue(store));
			oneOf(mockUpdateNotification).mockMethod(with(same(result)));
		} });

		storeServiceImpl = new StoreServiceImpl() {
			@Override
			protected List<SearchCriteria> buildUpdateSearchCriteriaList(final Store store) {
				return result;
			}

			@Override
			protected void notifyObjectsByUpdateCriteria(final List<SearchCriteria> updateSearchCriteriaList) {
				mockUpdateNotification.mockMethod(updateSearchCriteriaList);
			}
		};

		storeServiceImpl.setPersistenceEngine(persistenceEngine);
		storeServiceImpl.saveOrUpdate(store);
	}

	/**
	 * Tests notifyObjectsByUpdateCriteria() method.
	 */
	@Test
	public void testNotifyObjectsByUpdateCriteria() {
		final List<SearchCriteria> result = new ArrayList<SearchCriteria>();
		final ProductSearchCriteria productSearchCriteria = new ProductSearchCriteria();
		result.add(productSearchCriteria);

		context.checking(new Expectations() {
			{
				oneOf(indexNotificationService).addViaQuery(with(same(UpdateType.UPDATE)), with(same(productSearchCriteria)), with(false));
			}
		});


		storeServiceImpl.notifyObjectsByUpdateCriteria(result);
	}

	/**
	 * Tests buildUpdateSearchCriteriaList() method for given store.
	 */
	@Test
	public void testBuildUpdateSearchCriteriaList() {
		final SearchCriteria productSearchCriteria = new ProductSearchCriteria();
		final SearchCriteria sslSearchCriteria = new ShippingServiceLevelSearchCriteria();

		storeServiceImpl = new StoreServiceImpl() {
			@Override
			protected SearchCriteria buildProductUpdateCriteria(final Store storeBeforePersistence) {
				return productSearchCriteria;
			}

			@Override
			protected SearchCriteria buildShippingServiceLevelUpdateCriteria(final Store storeBeforePersistence) {
				return sslSearchCriteria;
			}
		};

		final List<SearchCriteria> buildUpdateSearchCriteriaList = storeServiceImpl.buildUpdateSearchCriteriaList(null);
		assertEquals(2, buildUpdateSearchCriteriaList.size());
		assertSame(productSearchCriteria, buildUpdateSearchCriteriaList.get(0));
		assertSame(sslSearchCriteria, buildUpdateSearchCriteriaList.get(1));
	}

	/**
	 * Tests buildShippingServiceLevelUpdateCriteria() method in case of incomplete store.
	 */
	@Test
	public void testBuildShippingServiceLevelUpdateCriteriaIncompleteStore() {
		StoreImpl storeImpl = new StoreImpl();
		storeImpl.setStoreState(StoreState.UNDER_CONSTRUCTION);
		assertNull(storeServiceImpl.buildShippingServiceLevelUpdateCriteria(storeImpl));

		storeImpl.setUidPk(1L);
		assertNull(storeServiceImpl.buildShippingServiceLevelUpdateCriteria(storeImpl));
	}

	/**
	 * Tests buildShippingServiceLevelUpdateCriteria() method in case of complete not persisted store.
	 */
	@Test
	public void testBuildShippingServiceLevelUpdateCriteriaCompleteNotPersistedStore() {
		StoreImpl storeImpl = new StoreImpl();
		storeImpl.setStoreState(StoreState.OPEN);
		assertNull(storeServiceImpl.buildShippingServiceLevelUpdateCriteria(storeImpl));
	}

	/**
	 * Tests buildShippingServiceLevelUpdateCriteria() method in case of complete persisted store if name of store was not changed.
	 */
	@Test
	public void testBuildShippingServiceLevelUpdateCriteriaCompletePersistedStoreNameNotChanged() {
		final String storeName = "storeName";

		final StoreImpl storeImpl = new StoreImpl();
		storeImpl.setUidPk(1L);
		storeImpl.setStoreState(StoreState.OPEN);
		storeImpl.setName(storeName);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(STORE_NAME, storeImpl.getUidPk());
				will(returnValue(Arrays.asList(storeName)));
			}
		});

		assertNull(storeServiceImpl.buildShippingServiceLevelUpdateCriteria(storeImpl));
	}

	/**
	 * Tests buildShippingServiceLevelUpdateCriteria() method in case of complete persisted store if name of store was changed.
	 */
	@Test
	public void testBuildShippingServiceLevelUpdateCriteriaCompletePersistedStoreNameChanged() {
		final String storeName = "storeName";
		final String oldStoreName = "oldName";

		final StoreImpl storeImpl = new StoreImpl();
		storeImpl.setUidPk(1L);
		storeImpl.setStoreState(StoreState.OPEN);
		storeImpl.setName(storeName);

		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(with(equal(ContextIdNames.SHIPPING_SERVICE_LEVEL_SEACRH_CRITERIA)));
				will(returnValue(new ShippingServiceLevelSearchCriteria()));
				oneOf(persistenceEngine).retrieveByNamedQuery(STORE_NAME, storeImpl.getUidPk());
				will(returnValue(Arrays.asList(oldStoreName)));
			}
		});

		SearchCriteria serviceLevelUpdateCriteria = storeServiceImpl.buildShippingServiceLevelUpdateCriteria(storeImpl);
		assertNotNull(serviceLevelUpdateCriteria);
		assertTrue(serviceLevelUpdateCriteria instanceof ShippingServiceLevelSearchCriteria);
		assertEquals(oldStoreName, ((ShippingServiceLevelSearchCriteria) serviceLevelUpdateCriteria).getStore());
	}

	/**
	 * Tests buildProductUpdateCriteria() method with given store.
	 */
	@Test
	public void testBuildProductUpdateCriteria() {
		storeServiceImpl = new StoreServiceImpl() {
			@Override
			protected boolean updateProductsNotificationRequired(final Store storeBeforePersistence) {
				return false;
			}
		};

		assertNull(storeServiceImpl.buildProductUpdateCriteria(null));

		storeServiceImpl = new StoreServiceImpl() {
			@Override
			protected boolean updateProductsNotificationRequired(final Store storeBeforePersistence) {
				return true;
			}
		};

		Store store = new StoreImpl();
		Catalog catalog = new CatalogImpl();
		catalog.setCode("catalogCode");
		store.setCatalog(catalog);

		SearchCriteria productUpdateCriteria = storeServiceImpl.buildProductUpdateCriteria(store);
		assertNotNull(productUpdateCriteria);
		assertTrue(productUpdateCriteria instanceof ProductSearchCriteria);

		ProductSearchCriteria productSearchCriteria = (ProductSearchCriteria) productUpdateCriteria;
		assertEquals(Locale.US, productSearchCriteria.getLocale());
		assertEquals(catalog.getCode(), productSearchCriteria.getCatalogCode());
	}

	/**
	 * Tests updateProductsNotificationRequired() method with incomplete store.
	 */
	@Test
	public void testUpdateProductsNotificationRequiredIncompleteStore() {
		Store store = new StoreImpl();
		store.setStoreState(StoreState.UNDER_CONSTRUCTION);

		assertFalse(storeServiceImpl.updateProductsNotificationRequired(store));

		store.setUidPk(1L);
		assertFalse(storeServiceImpl.updateProductsNotificationRequired(store));
	}

	/**
	 * Tests updateProductsNotificationRequired() method with complete not persisted store.
	 */
	@Test
	public void testUpdateProductsNotificationRequiredCompleteNotPersistedStore() {
		Store store = new StoreImpl();
		store.setStoreState(StoreState.OPEN);
		assertTrue(storeServiceImpl.updateProductsNotificationRequired(store));
	}

	/**
	 * Tests updateProductsNotificationRequired() method with complete persisted store if displayablity not changed.
	 */
	@Test
	public void testUpdateProductsNotificationRequiredCompletePersistedStoreDisplayabilityNotChanged() {
		final Store store = new StoreImpl();
		store.setUidPk(1L);
		store.setStoreState(StoreState.OPEN);
		store.setDisplayOutOfStock(true);

		storeServiceImpl = new StoreServiceImpl() {
			@Override
			boolean isGivenStoreBecomingActive(final Store store) {
				return false;
			}

		};
		storeServiceImpl.setPersistenceEngine(persistenceEngine);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(STORE_DISPLAYABLE, store.getUidPk());
				will(returnValue(Arrays.asList(store.isDisplayOutOfStock())));
			}
		});

		assertFalse(storeServiceImpl.updateProductsNotificationRequired(store));
	}

	/**
	 * Tests updateProductsNotificationRequired() method with complete persisted store if displayablity changed.
	 */
	@Test
	public void testUpdateProductsNotificationRequiredCompletePersistedStoreDisplayabilityChanged() {
		final Store store = new StoreImpl();
		store.setUidPk(1L);
		store.setStoreState(StoreState.OPEN);
		store.setDisplayOutOfStock(true);

		storeServiceImpl = new StoreServiceImpl() {
			@Override
			boolean isGivenStoreBecomingActive(final Store store) {
				return false;
			}
		};
		storeServiceImpl.setPersistenceEngine(persistenceEngine);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(STORE_DISPLAYABLE, store.getUidPk());
				will(returnValue(Arrays.asList(!store.isDisplayOutOfStock())));
			}
		});

		assertTrue(storeServiceImpl.updateProductsNotificationRequired(store));
	}

	/**
	 * Tests updateProductsNotificationRequired() method with complete persisted store if it is becoming active.
	 */
	@Test
	public void testUpdateProductsNotificationRequiredCompletePersistedStoreBecomingActive() {
		final Store store = new StoreImpl();
		store.setUidPk(1L);
		store.setStoreState(StoreState.OPEN);

		storeServiceImpl = new StoreServiceImpl() {
			@Override
			boolean isStoreDisplayabilityDifferentThanPersistedStoreDisplayability(final Store store) {
				return false;
			}

			@Override
			public Store getStore(final long storeUid) throws EpServiceException {
				final Store oldStore = new StoreImpl();
				oldStore.setStoreState(StoreState.UNDER_CONSTRUCTION);
				return oldStore;
			}
		};
		storeServiceImpl.setPersistenceEngine(persistenceEngine);

		assertTrue(storeServiceImpl.updateProductsNotificationRequired(store));
	}

	/**
	 * Tests updateProductsNotificationRequired() method with complete persisted store if it is not becoming active.
	 */
	@Test
	public void testUpdateProductsNotificationRequiredCompletePersistedStoreNotBecomingActive() {
		final Store store = new StoreImpl();
		store.setUidPk(1L);
		store.setStoreState(StoreState.OPEN);

		storeServiceImpl = new StoreServiceImpl() {
			@Override
			boolean isStoreDisplayabilityDifferentThanPersistedStoreDisplayability(final Store store) {
				return false;
			}

			@Override
			public Store getStore(final long storeUid) throws EpServiceException {
				final Store oldStore = new StoreImpl();
				oldStore.setStoreState(StoreState.OPEN);
				return oldStore;
			}
		};
		storeServiceImpl.setPersistenceEngine(persistenceEngine);

		assertFalse(storeServiceImpl.updateProductsNotificationRequired(store));
	}

	/**
	 * Tests getPersistedStoreName() method for given store.
	 */
	@Test
	public void testGetPersistedStoreName() {
		final String persistedStoreName = "persistedStoreName";

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(STORE_NAME, 1L);
				will(returnValue(Arrays.asList(persistedStoreName)));
			}
		});

		assertEquals(persistedStoreName, storeServiceImpl.getPersistedStoreName(1L));
	}

	/**
	 * Test method for 'com.elasticpath.service.store.StoreServiceImpl.remove(Store)'.
	 */
	@Test
	public void testRemove() {
		final Store store = context.mock(Store.class);
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).delete(with(store));
		} });
		storeServiceImpl.remove(store);
	}

	/**
	 * Test that getStore() and getObject() return the same store when given a valid UIDPK.
	 */
	@Test
	public void testGetStore() {
		final Store store = context.mock(Store.class);
		final long uidpk = 1234L;
		context.checking(new Expectations() { {
			allowing(store).getUidPk(); will(returnValue(uidpk));
			allowing(persistenceEngine).get(with(StoreImpl.class), with(uidpk)); will(returnValue(store));
			allowing(store).getAssociatedStoreUids();
		} });

		storeServiceImpl = new StoreServiceImpl() {
			@Override
			Store getPersistentStore(final long uidPk) {
				return store;
			}
		};
		storeServiceImpl.setPersistenceEngine(persistenceEngine);
		storeServiceImpl.setFetchPlanHelper(fetchPlanHelper);
		storeServiceImpl.setIndexNotificationService(indexNotificationService);

		assertSame(store, storeServiceImpl.getStore(uidpk));
		assertSame(store, storeServiceImpl.getObject(uidpk));
	}

	/**
	 * Test that getStore() returns null when given a UID for a store that doesn't exist.
	 */
	@Test
	public void testGetStoreNonExisting() {
		final Store store = context.mock(Store.class);
		final long nonExistUid = 3456L;
		context.checking(new Expectations() { {
			allowing(store).getUidPk(); will(returnValue(nonExistUid));
			allowing(persistenceEngine).get(with(StoreImpl.class), with(nonExistUid)); will(returnValue(null));
		} });

		storeServiceImpl = new StoreServiceImpl() {
			@Override
			Store getPersistentStore(final long uidPk) {
				return null;
			}
		};
		storeServiceImpl.setPersistenceEngine(persistenceEngine);
		storeServiceImpl.setFetchPlanHelper(fetchPlanHelper);
		storeServiceImpl.setIndexNotificationService(indexNotificationService);

		assertNull(storeServiceImpl.getStore(nonExistUid));
	}

	/**
	 * Test that getStore() returns a new non-persistent Store instance when given a UID <= 0.
	 */
	@Test
	public void testGetStoreNew() {
		final Store store = context.mock(Store.class);
		context.checking(new Expectations() { {
			allowing(store).getUidPk(); will(returnValue(0L));
		} });

		storeServiceImpl = new StoreServiceImpl() {
			@SuppressWarnings("unchecked")
			@Override
			protected <T> T getBean(final String beanName) {
				return (T) store;
			}
		};
		storeServiceImpl.setPersistenceEngine(persistenceEngine);
		assertEquals(0, storeServiceImpl.getStore(0).getUidPk());
	}


	/**
	 * Test that findStoreWithCode returns the store from the persistence layer that has the given code.
	 */
	@Test
	public void testFindStoreWithCodeExisting() {
		final List<Store> storeList = new ArrayList<Store>();
		final String existingCode = "test code";
		final Store store = context.mock(Store.class);
		storeList.add(store);
		context.checking(new Expectations() { {
			allowing(store).getUidPk(); will(returnValue(1));
			allowing(store).getCode(); will(returnValue(existingCode));
			oneOf(persistenceEngine).retrieveByNamedQuery("FIND_STORE_WITH_CODE", FlushMode.COMMIT, existingCode);
				will(returnValue(storeList));
		} });

		assertSame(storeList.get(0), storeServiceImpl.findStoreWithCode(existingCode));
	}

	/**
	 * Test that findStoreWithCode returns null if the store with the given code doesn't exist.
	 */
	@Test
	public void testFindStoreWithCodeNonExisting() {
		final List<Store> storeList = new ArrayList<Store>();
		final String nonExistingCode = "non existing code";
		final Store store = context.mock(Store.class);
		context.checking(new Expectations() { {
			allowing(store).getUidPk(); will(returnValue(1));
			allowing(store).getCode(); will(returnValue(nonExistingCode));
			oneOf(persistenceEngine).retrieveByNamedQuery("FIND_STORE_WITH_CODE", FlushMode.COMMIT, nonExistingCode);
				will(returnValue(storeList));
		} });
		assertNull(storeServiceImpl.findStoreWithCode(nonExistingCode));
	}

	/**
	 * Test that a null store or url always counts as unique url.
	 */
	@Test
	public void testIsStoreUniqueForStateNull() {
		final Store store = context.mock(Store.class);

		context.checking(new Expectations() { {
			oneOf(store).getUrl(); will(returnValue(null));
		} });

		assertTrue("null store should just return true", storeServiceImpl.isStoreUrlUniqueForState(null, StoreState.OPEN));
		assertTrue("null url should return true", storeServiceImpl.isStoreUrlUniqueForState(store, StoreState.OPEN));
	}

	/**
	 * Test that if a store with the same URL as the given store's URL is already persistent (differing
	 * only by a trailing slash) then the given store's URL is deemed non-unique.
	 */
	@Test
	public void testIsStoreUniqueForStateWithUrlInUse() {

		final Store persistentStore = context.mock(Store.class, "persistentStore");
		final String persistentStoreUrl = "http://my.store.url";

		final Store newStore = context.mock(Store.class, "newStore");
		final String newStoreUrl = "http://my.store.url/";

		context.checking(new Expectations() { {
			allowing(persistentStore).getUrl(); will(returnValue(persistentStoreUrl));
			allowing(newStore).getUrl(); will(returnValue(newStoreUrl));
		} });


		StoreServiceImpl service = new StoreServiceImpl() {
			@Override
			java.util.List<Store> findStoresWithState(final StoreState state) {
				return Arrays.asList(persistentStore);
			};
		};

		assertFalse("The new store's URL already exists for another open store (without the given slash), so should be regarded as non-unique",
				service.isStoreUrlUniqueForState(newStore, StoreState.OPEN));
	}

	/**
	 * Test that a non-existent url counts as unique.
	 */
	@Test
	public void testIsStoreUniqueForStateWithUrlNotUsed() {
		final Store persistentStore = context.mock(Store.class, "persistentStore");
		final String persistentStoreUrl = "http://my.store.url";

		final Store newStore = context.mock(Store.class, "newStore");
		final String newStoreUrl = "http://my.newstore.url/";

		context.checking(new Expectations() { {
			allowing(persistentStore).getUrl(); will(returnValue(persistentStoreUrl));
			allowing(newStore).getUrl(); will(returnValue(newStoreUrl));
		} });

		StoreServiceImpl service = new StoreServiceImpl() {
			@Override
			java.util.List<Store> findStoresWithState(final StoreState state) {
				return Arrays.asList(persistentStore);
			};
		};

		assertTrue("The new store's URL doesn't already exist, so should be regarded as unique",
				service.isStoreUrlUniqueForState(newStore, StoreState.OPEN));
	}

	/**
	 * Test method for 'com.elasticpath.service.store.StoreServiceImpl.findAllStoreUids()'.
	 */
	@Test
	public void testFindAllStoreUids() {
		final List<Long> uidList = new ArrayList<Long>();
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery("FIND_ALL_STORE_UIDS");
			will(returnValue(uidList));
		} });
		assertSame(uidList, storeServiceImpl.findAllStoreUids());

		// make sure the query returns something seemingly valid
		final long storeUid = 1234L;
		uidList.add(storeUid);
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery("FIND_ALL_STORE_UIDS");
			will(returnValue(uidList));
		} });
		assertSame(uidList, storeServiceImpl.findAllStoreUids());
	}

	/**
	 * Test method for {@link StoreServiceImpl#findAllStores()}.
	 */
	@Test
	public void testFindAllStores() {
		final List<Store> storeList = new ArrayList<Store>();
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(FIND_ALL_STORES); will(returnValue(storeList));
			oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(null);
			oneOf(fetchPlanHelper).clearFetchPlan();
		} });
		assertSame(storeList, storeServiceImpl.findAllStores());

		// make sure the query returns something seemingly valid
		final Store store = context.mock(Store.class);
		final long storeUid = 1234L;
		context.checking(new Expectations() { {
			allowing(store).getUidPk(); will(returnValue(storeUid));
			oneOf(persistenceEngine).retrieveByNamedQuery(FIND_ALL_STORES); will(returnValue(storeList));
			oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(null);
			oneOf(fetchPlanHelper).clearFetchPlan();
		} });
		storeList.add(store);
		assertSame(storeList, storeServiceImpl.findAllStores());
	}

	/**
	 * Test method for 'com.elasticpath.service.store.StoreServiceImpl.storeInUse()'.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testStoreInUse() {
		final Store store = context.mock(Store.class);
		final long storeUid = 1L;
		context.checking(new Expectations() { {
			allowing(store).getUidPk(); will(returnValue(storeUid));
			oneOf(persistenceEngine).retrieveByNamedQuery(
					with(equal("STORE_WITH_ORDER_IN_USE")), with(IsArray.<Object>array(anything())));
			will(returnValue(new ArrayList<Long>()));
			oneOf(persistenceEngine).retrieveByNamedQuery(
					with(equal("STORE_WITH_USER_IN_USE")), with(IsArray.<Object>array(anything())));
			will(returnValue(new ArrayList<Long>()));
			oneOf(persistenceEngine).retrieveByNamedQuery(
					with(equal("STORE_WITH_IMPORTJOB_IN_USE")), with(IsArray.<Object>array(anything())));
			will(returnValue(new ArrayList<Long>()));
			oneOf(persistenceEngine).retrieveByNamedQuery(
					with(equal("STORE_WITH_SHIPPING_SERVICE_LEVEL_IN_USE")), with(IsArray.<Object>array(anything())));
			will(returnValue(new ArrayList<Long>()));
			oneOf(persistenceEngine).retrieveByNamedQuery(
					with(equal("STORE_WITH_STORE_ASSOCIATION_IN_USE")), with(IsArray.<Object>array(anything())));
			will(returnValue(new ArrayList<Long>()));
			oneOf(persistenceEngine).retrieveByNamedQuery(
					with(equal("STORE_WITH_PROMOTION_IN_USE")), with(IsArray.<Object>array(anything())));
			will(returnValue(new ArrayList<Long>()));
		} });
		assertFalse(storeServiceImpl.storeInUse(storeUid));

		final List<Long> storeList = new ArrayList<Long>();
		storeList.add(storeUid);

		context.checking(new Expectations() { {
			allowing(store).getUidPk(); will(returnValue(storeUid));
			oneOf(persistenceEngine).retrieveByNamedQuery(with(equal("STORE_WITH_ORDER_IN_USE")), with(IsArray.<Object>array(anything())));
			will(returnValue(new ArrayList<Long>()));
			oneOf(persistenceEngine).retrieveByNamedQuery(with(equal("STORE_WITH_USER_IN_USE")), with(IsArray.<Object>array(anything())));
			will(returnValue(storeList));
		} });
		assertTrue(storeServiceImpl.storeInUse(1L));
	}

	/**
	 * Test method for {@link StoreServiceImpl#findStoresWithCatalogUids(Collection)}.
	 */
	@Test
	public void testFindStoresWithCatalogUids() {
		final Collection<Long> uids = new ArrayList<Long>();
		uids.add(1L);
		uids.add(2L);

		final Collection<Store> result = new ArrayList<Store>();
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQueryWithList("STORE_WITH_CATALOG_UID", PLACEHOLDER_FOR_LIST, uids);
			will(returnValue(result));
		} });
		assertEquals(result, storeServiceImpl.findStoresWithCatalogUids(uids));
	}

	/**
	 * Test method for {@link StoreServiceImpl#findAllStores(FetchGroupLoadTuner)}.
	 */
	@Test
	public void testFindAllStoresFGLoadTuner() {
		final long uid = 1234L;
		final Store store = context.mock(Store.class);
		final FetchGroupLoadTuner loadTuner = context.mock(FetchGroupLoadTuner.class);
		final List<Store> stores = Arrays.asList(store);
		context.checking(new Expectations() { {
			allowing(store).getUidPk(); will(returnValue(uid));
			oneOf(persistenceEngine).retrieveByNamedQuery(FIND_ALL_STORES); will(returnValue(stores));
			oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(loadTuner);
			oneOf(fetchPlanHelper).clearFetchPlan();
		} });
		assertSame(stores, storeServiceImpl.findAllStores(loadTuner));

		context.checking(new Expectations() { {
			allowing(store).getUidPk(); will(returnValue(uid));
			oneOf(persistenceEngine).retrieveByNamedQuery(FIND_ALL_STORES); will(returnValue(new ArrayList<Store>()));
			oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(loadTuner);
			oneOf(fetchPlanHelper).clearFetchPlan();
		} });
		assertTrue(storeServiceImpl.findAllStores(loadTuner).isEmpty());
	}

	/**
	 * Test that findAllCompleteStores calls the method {@link StoreServiceImpl#findAllCompleteStores(FetchGroupLoadTuner)}
	 * with a null load tuner.
	 */
	@Test
	public void testFindAllCompleteStores() {
		final List<Store> completeStores = Collections.emptyList();
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(
					FIND_ALL_COMPLETE_STORES, StoreState.UNDER_CONSTRUCTION, StoreState.RESTRICTED);
			will(returnValue(completeStores));
			oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(null);
			oneOf(fetchPlanHelper).clearFetchPlan();
		} });

		assertSame(completeStores, storeServiceImpl.findAllCompleteStores());
	}

	/**
	 * Test that findAllCompleteStores(LoadTuner) calls the persistence engine with the appropriate named
	 * query and that the fetch plan is configured with the given load tuner.
	 */
	@Test
	public void testFindAllCompleteStoresWithLoadTuner() {
		final List<Store> completeStores = Collections.emptyList();
		final FetchGroupLoadTuner loadTuner = context.mock(FetchGroupLoadTuner.class);
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(
					FIND_ALL_COMPLETE_STORES, StoreState.UNDER_CONSTRUCTION, StoreState.RESTRICTED);
			will(returnValue(completeStores));
			oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(loadTuner);
			oneOf(fetchPlanHelper).clearFetchPlan();
		} });
		assertSame(completeStores, storeServiceImpl.findAllCompleteStores(loadTuner));
	}

	/**
	 * Test that findAllCompleteStoreUids() calls the persistence engine with the appropriate named query.
	 */
	@Test
	public void testFindAllCompleteStoreUids() {
		final List<Long> stores = Collections.emptyList();
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(
					"FIND_ALL_COMPLETE_STORE_UIDS", StoreState.UNDER_CONSTRUCTION, StoreState.RESTRICTED);
			will(returnValue(stores));
		} });
		assertSame(stores, storeServiceImpl.findAllCompleteStoreUids());
	}

	/**
	 * Tests that will find all credit card type for all complete stores.
	 */
	@Test
	public void testFindAllSupportedCreditCardTypes() {

		final Set<CreditCardType> types = new HashSet<CreditCardType>();

		final Store store = context.mock(Store.class);
		final CreditCardType visa = context.mock(CreditCardType.class, "visa");
		final CreditCardType masterCard = context.mock(CreditCardType.class, "masterCard");
		types.add(visa);
		types.add(masterCard);

		final List<Store> stores = new ArrayList<Store>();
		stores.add(store);

		context.checking(new Expectations() { {

			oneOf(visa).getCreditCardType();
			will(returnValue("Visa"));

			oneOf(masterCard).getCreditCardType();
			will(returnValue("MasterCard"));

		    oneOf(store).getCreditCardTypes();
		    will(returnValue(types));
		} });




		StoreService storeService = new StoreServiceImpl() {
			@Override
			public List <Store> findAllCompleteStores() {
				return stores;
			}
		};

		assertTrue(storeService.findAllSupportedCreditCardTypes().size() == 2);

	}

	/** Convenience interface for mocking object. */
	public interface MockUpdateNotification {

		/**
		 * Method that should be mocked.
		 * @param objects objects
		 */
		void mockMethod(final Object objects);
	}
}
