package com.elasticpath.service.rules.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.drools.RuleBase;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.rules.EpRuleBase;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.rules.RuleService;

/**
 * Test case for {@link DBReadingRuleEngingImpl}.
 */
public class DBReadingRuleEngineImplTest {

	private static final String CODE = "some code";

	private DBReadingRuleEngineImpl ruleEngine;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private RuleService mockRuleService;
	
	private RuleBase mockOldRuleBase;
	
	private RuleBase mockNewRuleBase;
	
	private Store mockStore;
	
	private Catalog mockCatalog;


	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception in case of any errors
	 */
	@Before
	public void setUp() throws Exception {
		ruleEngine = new DBReadingRuleEngineImpl();
		ruleEngine.setCatalogRuleBaseCacheTimeout(0);
		mockRuleService = context.mock(RuleService.class);
		ruleEngine.setRuleService(mockRuleService);
		mockOldRuleBase = context.mock(RuleBase.class, "old rule base");
		mockNewRuleBase = context.mock(RuleBase.class, "new rule base");
		
		mockStore = context.mock(Store.class);
		context.checking(new Expectations() {
			{
				allowing(mockStore).getCode();
				will(returnValue(CODE));
			}
		});
		
		mockCatalog = context.mock(Catalog.class);
		final Catalog catalog = mockCatalog;
		context.checking(new Expectations() {
			{
				allowing(mockStore).getCatalog();
				will(returnValue(catalog));

				allowing(mockCatalog).getCode();
				will(returnValue(CODE));
			}
		});
	}

	/**
	 * Test method for {@link DBReadingRuleEngineImpl#getCartRuleBase(Store)} where a rule has not
	 * yet been put into the database.
	 */
	@Test
	public void testCartRuleNotInDB() {
		final Store store = mockStore;
		context.checking(new Expectations() {
			{

				oneOf(mockRuleService).findRuleBaseByScenario(store, null, RuleScenarios.CART_SCENARIO);
				will(returnValue(null));
			}
		});
		assertNotNull(ruleEngine.getCartRuleBase(store));
	}

	/**
	 * Test method for {@link DBReadingRuleEngineImpl#getCartRuleBase(Store)} where a rule is in
	 * the database.
	 */
	@Test
	public void testCartRuleInDB() {
		final Store store = mockStore;

		final RuleBase ruleBase = mockOldRuleBase;
		final EpRuleBase mockEpRuleBase = context.mock(EpRuleBase.class);
		context.checking(new Expectations() {
			{
				allowing(mockEpRuleBase).getRuleBase();
				will(returnValue(ruleBase));


				oneOf(mockRuleService).findRuleBaseByScenario(store, null, RuleScenarios.CART_SCENARIO);
				will(returnValue(mockEpRuleBase));
			}
		});
		assertSame(ruleBase, ruleEngine.getCartRuleBase(store));
	}

	/**
	 * Test method for {@link DBReadingRuleEngineImpl#getCartRuleBase(Store)} where a rule is
	 * cached, but hasn't been updated.
	 */
	@Test
	public void testCartRuleCachedNotUpdated() {
		final Store store = mockStore;

		final RuleBase ruleBase = mockOldRuleBase;
		final EpRuleBase mockEpRuleBase = context.mock(EpRuleBase.class);
		final EpRuleBase epRuleBase = mockEpRuleBase;
		context.checking(new Expectations() {
			{
				allowing(mockEpRuleBase).getRuleBase();
				will(returnValue(ruleBase));


				allowing(mockRuleService).findRuleBaseByScenario(store, null, RuleScenarios.CART_SCENARIO);
				will(returnValue(epRuleBase));
			}
		});
		ruleEngine.getCartRuleBase(store);

		final Date date = new Date();
		context.checking(new Expectations() {
			{
				allowing(mockEpRuleBase).getLastModifiedDate();
				will(returnValue(date));


				oneOf(mockRuleService).findChangedStoreRuleBases(CODE, RuleScenarios.CART_SCENARIO, date);
				will(returnValue(null));
			}
		});
		assertSame(ruleBase, ruleEngine.getCartRuleBase(store));
	}

	/**
	 * Test method for {@link DBReadingRuleEngineImpl#getCartRuleBase(Store)} where a rule is
	 * cached, but it is now updated.
	 */
	@Test
	public void testCartRuleCachedAndUpdated() {
		final Store store = mockStore;

		final RuleBase ruleBase = mockOldRuleBase;
		final EpRuleBase mockEpRuleBase = context.mock(EpRuleBase.class);
		final EpRuleBase epRuleBase = mockEpRuleBase;
		context.checking(new Expectations() {
			{
				allowing(mockEpRuleBase).getRuleBase();
				will(returnValue(ruleBase));


				allowing(mockRuleService).findRuleBaseByScenario(store, null, RuleScenarios.CART_SCENARIO);

				will(returnValue(epRuleBase));
			}
		});
		ruleEngine.getCartRuleBase(store);

		final RuleBase updatedRuleBase = mockNewRuleBase;
		final EpRuleBase mockUpdatedEpRuleBase = context.mock(EpRuleBase.class, "updated rule base");
		final EpRuleBase updatedEpRuleBase = mockUpdatedEpRuleBase;
		context.checking(new Expectations() {
			{
				allowing(mockUpdatedEpRuleBase).getRuleBase();
				will(returnValue(updatedRuleBase));
			}
		});

		final Date date = new Date();
		context.checking(new Expectations() {
			{
				allowing(mockEpRuleBase).getLastModifiedDate();
				will(returnValue(date));


				oneOf(mockRuleService).findChangedStoreRuleBases(CODE, RuleScenarios.CART_SCENARIO, date);
				will(returnValue(updatedEpRuleBase));
			}
		});
		assertSame(updatedRuleBase, ruleEngine.getCartRuleBase(store));
	}

	/**
	 * Test method for {@link DBReadingRuleEngineImpl#getCatalogRuleBase(Store)} where a rule has
	 * not yet been put into the database.
	 */
	@Test
	public void testCatalogRuleNotInDB() {
		final Store store = mockStore;
		final Catalog catalog = mockCatalog;
		context.checking(new Expectations() {
			{

				oneOf(mockRuleService).findRuleBaseByScenario(null, catalog, RuleScenarios.CATALOG_BROWSE_SCENARIO);
				will(returnValue(null));
			}
		});
		assertNotNull(ruleEngine.getCatalogRuleBase(store));
	}

	/**
	 * Test method for {@link DBReadingRuleEngineImpl#getCatalogRuleBase(Store)} where a rule is
	 * in the database.
	 */
	@Test
	public void testCatalogRuleInDB() {
		final Store store = mockStore;
		final Catalog catalog = mockCatalog;

		final RuleBase ruleBase = mockOldRuleBase;
		final EpRuleBase mockEpRuleBase = context.mock(EpRuleBase.class);
		final EpRuleBase epRuleBase = mockEpRuleBase;
		context.checking(new Expectations() {
			{
				allowing(mockEpRuleBase).getRuleBase();
				will(returnValue(ruleBase));


				oneOf(mockRuleService).findRuleBaseByScenario(null, catalog, RuleScenarios.CATALOG_BROWSE_SCENARIO);
				will(returnValue(epRuleBase));
			}
		});
		assertSame(ruleBase, ruleEngine.getCatalogRuleBase(store));
	}

	/**
	 * Test method for {@link DBReadingRuleEngineImpl#getCatalogRuleBase(Store)} where a rule is
	 * cached, but hasn't been updated.
	 */
	@Test
	public void testCatalogRuleCachedNotUpdated() {
		final Store store = mockStore;
		final Catalog catalog = mockCatalog;

		final RuleBase ruleBase = mockOldRuleBase;
		final EpRuleBase mockEpRuleBase = context.mock(EpRuleBase.class);
		final EpRuleBase epRuleBase = mockEpRuleBase;
		context.checking(new Expectations() {
			{
				allowing(mockEpRuleBase).getRuleBase();
				will(returnValue(ruleBase));


				allowing(mockRuleService).findRuleBaseByScenario(null, catalog, RuleScenarios.CATALOG_BROWSE_SCENARIO);
				will(returnValue(epRuleBase));
			}
		});
		ruleEngine.getCatalogRuleBase(store);

		final Date date = new Date();
		context.checking(new Expectations() {
			{
				allowing(mockEpRuleBase).getLastModifiedDate();
				will(returnValue(date));


				oneOf(mockRuleService).findChangedCatalogRuleBases(CODE, RuleScenarios.CATALOG_BROWSE_SCENARIO, date);
				will(returnValue(null));
			}
		});
		assertEquals(ruleBase, ruleEngine.getCatalogRuleBase(store));
	}

	/**
	 * Test method for {@link DBReadingRuleEngineImpl#getCatalogRuleBase(Store)} where a rule is
	 * cached, but it is now updated.
	 */
	@Test
	public void testCatalogRuleCachedAndUpdated() {
		final Store store = mockStore;
		final Catalog catalog = mockCatalog;

		final RuleBase ruleBase = mockOldRuleBase;
		final EpRuleBase mockEpRuleBase = context.mock(EpRuleBase.class);
		final EpRuleBase epRuleBase = mockEpRuleBase;
		context.checking(new Expectations() {
			{
				allowing(mockEpRuleBase).getRuleBase();
				will(returnValue(ruleBase));


				allowing(mockRuleService).findRuleBaseByScenario(null, catalog, RuleScenarios.CATALOG_BROWSE_SCENARIO);
				will(returnValue(epRuleBase));
			}
		});
		ruleEngine.getCatalogRuleBase(store);

		final RuleBase updatedRuleBase = mockNewRuleBase;
		final EpRuleBase mockUpdatedEpRuleBase = context.mock(EpRuleBase.class, "updated rule base");
		final EpRuleBase updatedEpRuleBase = mockUpdatedEpRuleBase;
		context.checking(new Expectations() {
			{
				allowing(mockUpdatedEpRuleBase).getRuleBase();
				will(returnValue(updatedRuleBase));
			}
		});

		final Date date = new Date();
		context.checking(new Expectations() {
			{
				allowing(mockEpRuleBase).getLastModifiedDate();
				will(returnValue(date));


				oneOf(mockRuleService).findChangedCatalogRuleBases(CODE, RuleScenarios.CATALOG_BROWSE_SCENARIO, date);
				will(returnValue(updatedEpRuleBase));
			}
		});
		assertSame(updatedRuleBase, ruleEngine.getCatalogRuleBase(store));
	}
}
