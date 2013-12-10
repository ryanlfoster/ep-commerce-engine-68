/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shipping.impl;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.search.impl.IndexNotificationImpl;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.impl.RegionImpl;
import com.elasticpath.domain.shipping.impl.ShippingRegionImpl;
import com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.rules.RuleParameterService;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.query.ShippingLevelsSearchCriteriaImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;


/**
 * Test <code>ShippingServiceLevelServiceImpl</code>.
 */
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.TooManyMethods" })
public class ShippingServiceLevelServiceImplTest {
	private static final String INVALID_STORE_CODE = "INVALID_STORE_CODE";

	private static final String DEFAULT_LEVEL_GUID = "DEFAULT_LEVEL_GUID";

	private static final String DEFAULT_LEVEL_CODE = "DEFAULT_SERVICE";

	private static final long SHIPPING_SERVICE_LEVEL_UID = 1001L;

	private static final String SHIPPING_SERVICE_LEVEL_STATE_PARAMETER = "ssl.enabled = ?";

	private static final String SHIPPING_SERVICE_LEVEL_STORE_PARAMETER = "ssl.store.uidPk = ?";

	private static final String SHIPPING_SERVICE_LEVEL_REGION_PARAMETER = "ssl.shippingRegion.uidPk = ?";

	private static final String SHIPPING_SERVICE_LEVEL_SEARCH_QUERY_PREFIX = "SELECT ssl FROM ShippingServiceLevelImpl ssl WHERE ";

	private static final String SELECT_BY_STORE_AND_STATE_QUERY = "SHIPPINGSERVICELEVEL_SELECT_BY_STORE_AND_STATE";

	private static final String SUBCOUNTRY_ONTARIO_NAME = "CA.on";

	private static final String SUBCOUNTRY_BRITISH_COLUMBIA_NAME = "CA.bc";

	private static final String SUBCOUNTRY_ALBERTA_NAME = "CA.ab";

	private static final String COUNTRY_CODE_CA = "CA";

	private static final String EXPECTED_SUB_COUNTRY_CODE_AB = "AB";

	private static final String SUB_COUNTRY_CODE_BC = "BC";

	private static final String EXPECTED_SUB_COUNTRY_CODE_ON = "ON";

	private static final String COUNTRY_CODE_US = "US";

	private static final String COUNTRY_CODE_GB = "GB";

	private static final long EXPECTED_SHIPPING_REGION_UID = 2L;

	private static final long EXPECTED_STORE_UID = 3L;

	private static final String STORE_CODE = "SHIPPING_STORE_CODE";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private BeanFactoryExpectationsFactory expectationsFactory;

	private ShippingServiceLevelServiceImpl shippingServiceLevelServiceImpl;
	private IndexNotificationService mockIndexNotificationService;
	private TimeService mockTimeService;
	private PersistenceEngine mockPersistenceEngine;
	private RuleParameterService ruleParameterService;
	private Geography mockGeography;
	private ShippingServiceLevel shippingServiceLevel;
	private Store store;
	private ShippingLevelsSearchCriteriaImpl shippingLevelSearchCriteria;
	private List<Object> searchParameters;

	/**
	 * Initialize mock objects and test data.
	 */
	@Before
	public void initializeMockObjectsAndTestData() {
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.INDEX_NOTIFICATION, IndexNotificationImpl.class);

		mockIndexNotificationService = context.mock(IndexNotificationService.class);
		mockTimeService = context.mock(TimeService.class);
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		mockGeography = context.mock(Geography.class);
		ruleParameterService = context.mock(RuleParameterService.class);

		shippingServiceLevelServiceImpl = new ShippingServiceLevelServiceImpl();
		shippingServiceLevelServiceImpl.setPersistenceEngine(mockPersistenceEngine);
		shippingServiceLevelServiceImpl.setGeography(mockGeography);
		shippingServiceLevelServiceImpl.setIndexNotificationService(mockIndexNotificationService);
		shippingServiceLevelServiceImpl.setTimeService(mockTimeService);
		shippingServiceLevelServiceImpl.setRuleParameterService(ruleParameterService);
		store = new StoreImpl();
		store.setUidPk(EXPECTED_STORE_UID);
		store.setCode(STORE_CODE);


		shippingLevelSearchCriteria = new ShippingLevelsSearchCriteriaImpl();
		searchParameters = new ArrayList<Object>();
		shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setUidPk(SHIPPING_SERVICE_LEVEL_UID);
		shippingServiceLevel.setCode(DEFAULT_LEVEL_CODE);
		shippingServiceLevel.setGuid(DEFAULT_LEVEL_GUID);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests that index update notification occurs after adding a {@link ShippingServiceLevel} is posted.
	 */
	@Test
	public void testPostIndexUpdateNotificationAfterAdd() {
		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).addNotificationForEntityIndexUpdate(IndexType.SHIPPING_SERVICE_LEVEL,
						shippingServiceLevel.getUidPk());
				oneOf(mockPersistenceEngine).save(shippingServiceLevel);
			}
		});
		shippingServiceLevelServiceImpl.add(shippingServiceLevel);
	}

	/**
	 * Tests that index update notification occurs after updating a {@link ShippingServiceLevel} is posted.
	 */
	@Test
	public void testPostIndexUpdateNotificationAfterUpdate() {
		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).addNotificationForEntityIndexUpdate(IndexType.SHIPPING_SERVICE_LEVEL,
						shippingServiceLevel.getUidPk());

				oneOf(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));

				oneOf(mockPersistenceEngine).merge(shippingServiceLevel);
				will(returnValue(shippingServiceLevel));

			}
		});
		shippingServiceLevelServiceImpl.update(shippingServiceLevel);
	}

	/**
	 * Test post index update notification after removal.
	 */
	@Test
	public void testPostIndexUpdateNotificationAfterRemoval() {
		context.checking(new Expectations() {
			{
				oneOf(mockIndexNotificationService).add(with(any(IndexNotificationImpl.class)));
				oneOf(mockPersistenceEngine).delete(shippingServiceLevel);
			}
		});

		shippingServiceLevelServiceImpl.remove(shippingServiceLevel);
	}


	/**
	 * Test find by store.
	 */
	@Test
	public void testFindByStore() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(
						with(containsString("SHIPPINGSERVICELEVEL_SELECT_BY_STORE")),
						with(equal(new Object[] { STORE_CODE })));
				will(returnValue(getShippingServiceLevels()));
			}
		});

		shippingServiceLevelServiceImpl.findByStore(STORE_CODE);
	}


	/**
	 * Test find by valid guid.
	 */
	@Test
	public void testFindByValidGuid() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(
						with(containsString("SHIPPINGSERVICELEVEL_FIND_BY_GUID")),
						with(equal(new Object[] { shippingServiceLevel.getGuid() })));
				will(returnValue(Arrays.asList(shippingServiceLevel)));
			}
		});

		ShippingServiceLevel retrievedLevel = shippingServiceLevelServiceImpl.findByGuid(shippingServiceLevel.getGuid());
		assertEquals(retrievedLevel, shippingServiceLevel);
	}

	/**
	 * Test find by invalid guid.
	 */
	@Test
	public void testFindByInvalidGuid() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(
						with(containsString("SHIPPINGSERVICELEVEL_FIND_BY_GUID")),
						with(equal(new Object[] { shippingServiceLevel.getGuid() })));
				will(returnValue(Collections.emptyList()));
			}
		});

		ShippingServiceLevel retrievedLevel = shippingServiceLevelServiceImpl.findByGuid(shippingServiceLevel.getGuid());
		assertEquals(retrievedLevel, null);
	}


	/**
	 * Test find by null guid.
	 */
	@Test(expected = EpServiceException.class)
	public void testFindByNullGuid() {
		shippingServiceLevelServiceImpl.findByGuid(null);
	}


	/**
	 * Test valid shipping service level that is in use.
	 */
	@Test
	public void testValidShippingServiceLevelThatIsInUse() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(
						with(containsString("SHIPPINGSERVICELEVEL_UID_IN_USE")), with(any(Object[].class)));
				will(returnValue(Arrays.asList(shippingServiceLevel.getUidPk())));
			}
		});

		assertTrue(shippingServiceLevelServiceImpl.isShippingServiceLevelInUse(shippingServiceLevel.getUidPk()));
	}

	/**
	 * Test invalid shipping service level that is not in use.
	 */
	@Test
	public void testInvalidShippingServiceLevelIsNotInUse() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(containsString("SHIPPINGSERVICELEVEL_UID_IN_USE")),
						with(any(Object[].class)));
				will(returnValue(Collections.emptyList()));
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(
						with(containsString("SHIPPINGSERVICELEVEL_CODE_BY_UID")),
						with(equal(new Object[] { shippingServiceLevel.getUidPk() })));
				will(returnValue(Collections.emptyList()));

			}
		});

		assertFalse(shippingServiceLevelServiceImpl.isShippingServiceLevelInUse(shippingServiceLevel.getUidPk()));
	}

	/**
	 * Test valid shipping service level that is not in use.
	 */
	@Test
	public void testValidShippingServiceLevelThatIsNotInUse() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(containsString("SHIPPINGSERVICELEVEL_UID_IN_USE")),
						with(any(Object[].class)));
				will(returnValue(Collections.emptyList()));

				oneOf(mockPersistenceEngine).retrieveByNamedQuery(
						with(containsString("SHIPPINGSERVICELEVEL_CODE_BY_UID")),
						with(equal(new Object[] { shippingServiceLevel.getUidPk() })));
				will(returnValue(Arrays.asList(shippingServiceLevel.getCode())));

				oneOf(ruleParameterService).findUniqueParametersWithKey("shippingServiceLevelCode");
				will(returnValue(Collections.emptyList()));
			}
		});

		assertFalse(shippingServiceLevelServiceImpl.isShippingServiceLevelInUse(shippingServiceLevel.getUidPk()));
	}


	/**
	 * Test method for 'com.elasticpath.service.impl.ShippingRegionServiceImpl.getSortedCountriesWithShippingAllowed()'.
	 */
	@Test
	public void testGetSortedCountriesWithShippingAllowed() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(
						with(containsString(SELECT_BY_STORE_AND_STATE_QUERY)),
						with(equal(new Object[] { STORE_CODE, true })));
				will(returnValue(getShippingServiceLevels()));
			}
		});
		setGeographyExpectations();

		SortedMap<String, String> sortedMap = shippingServiceLevelServiceImpl.getSortedCountriesWithShippingAllowed(Locale.ENGLISH, store);
		assertTrue("The last key should be 'United States'", sortedMap.lastKey().toString().startsWith("U"));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ShippingRegionServiceImpl.getCountrySubCountryMapWithShippingService()'.
	 */
	@Test
	public void testGetCountrySubCountryMapWithShippingService() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(containsString("SHIPPINGREGION_IN_USE")),
						with(any(Object[].class)));
				will(returnValue(getTestShippingRegionList()));
			}
		});
		setGeographyExpectations();

		final Map<String, Map<String, String>> countrySubCountryInUseMap =
			shippingServiceLevelServiceImpl.getCountrySubCountryMapWithShippingService(Locale.CANADA_FRENCH);
		final Map<String, String> subCountryMapForCanada = countrySubCountryInUseMap.get(COUNTRY_CODE_CA);
		final int expectedSubCountryCount = 3;
		assertEquals("Incorrect Subcountry Count.", expectedSubCountryCount, subCountryMapForCanada.size());
		assertEquals("Missing Subcountry Code", EXPECTED_SUB_COUNTRY_CODE_AB, subCountryMapForCanada.get(SUBCOUNTRY_ALBERTA_NAME));
		assertEquals("Incorrect Subcountry Code", SUB_COUNTRY_CODE_BC, subCountryMapForCanada.get(SUBCOUNTRY_BRITISH_COLUMBIA_NAME));
		assertEquals("Incorrect Subcountry Code", EXPECTED_SUB_COUNTRY_CODE_ON, subCountryMapForCanada.get(SUBCOUNTRY_ONTARIO_NAME));
	}

	/**
	 * Test query and parameter building with null parameters.
	 */
	@Test
	public void testQueryAndParameterBuildingWithNullParameters() {
		shippingLevelSearchCriteria.setStoreUid(null);
		shippingLevelSearchCriteria.setShippingRegionUid(null);
		String constructedQuery = shippingServiceLevelServiceImpl.buildQueryAndParamsForCriteria(shippingLevelSearchCriteria, searchParameters);
		assertEquals("SELECT ssl FROM ShippingServiceLevelImpl ssl", constructedQuery);
		assertEquals(0, searchParameters.size());
	}

	/**
	 * Test query and parameter building with active state.
	 */
	@Test
	public void testQueryAndParameterBuildingWithNullParametersButActiveState() {
		shippingLevelSearchCriteria.setStoreUid(null);
		shippingLevelSearchCriteria.setShippingRegionUid(null);
		shippingLevelSearchCriteria.setState(ShippingLevelsSearchCriteriaImpl.STATE_ACTIVE);
		String constructedQuery = shippingServiceLevelServiceImpl.buildQueryAndParamsForCriteria(shippingLevelSearchCriteria, searchParameters);
		assertEquals("SELECT ssl FROM ShippingServiceLevelImpl ssl WHERE ssl.enabled = ?1", constructedQuery);
		assertEquals(1, searchParameters.size());
		boolean expectedShippingLevelState = true;
		assertEquals("Invalid shipping level state.", expectedShippingLevelState, searchParameters.get(0));
	}

	/**
	 * Test query and parameter building with null store uid.
	 */
	@Test
	public void testQueryAndParameterBuildingWithNullStoreUid() {
		shippingLevelSearchCriteria.setStoreUid(null);
		shippingLevelSearchCriteria.setShippingRegionUid(EXPECTED_SHIPPING_REGION_UID);
		String constructedQuery = shippingServiceLevelServiceImpl.buildQueryAndParamsForCriteria(shippingLevelSearchCriteria, searchParameters);
		assertThat(constructedQuery, containsString(SHIPPING_SERVICE_LEVEL_SEARCH_QUERY_PREFIX));
		assertThat(constructedQuery, containsString(SHIPPING_SERVICE_LEVEL_REGION_PARAMETER));
		assertEquals("Incorrect Shipping Region UID.", EXPECTED_SHIPPING_REGION_UID, searchParameters.get(0));
	}

	/**
	 * Test query and parameter building with null shipping region uid.
	 */
	@Test
	public void testQueryAndParameterBuildingWithNullShippingRegionUid() {
		shippingLevelSearchCriteria.setShippingRegionUid(null);
		shippingLevelSearchCriteria.setStoreUid(EXPECTED_STORE_UID);
		String constructedQuery = shippingServiceLevelServiceImpl.buildQueryAndParamsForCriteria(shippingLevelSearchCriteria, searchParameters);
		assertThat(constructedQuery, containsString(SHIPPING_SERVICE_LEVEL_SEARCH_QUERY_PREFIX));
		assertThat(constructedQuery, containsString(SHIPPING_SERVICE_LEVEL_STORE_PARAMETER));
		assertEquals("Incorrect Store UID.", EXPECTED_STORE_UID, searchParameters.get(0));
	}

	/**
	 * Test query and parameter building with valid region and store.
	 */
	@Test
	public void testQueryAndParameterBuildingWithNonNullParameters() {
		shippingLevelSearchCriteria.setShippingRegionUid(EXPECTED_SHIPPING_REGION_UID);
		shippingLevelSearchCriteria.setStoreUid(EXPECTED_STORE_UID);
		String constructedQuery = shippingServiceLevelServiceImpl.buildQueryAndParamsForCriteria(shippingLevelSearchCriteria, searchParameters);
		assertThat(constructedQuery, containsString(SHIPPING_SERVICE_LEVEL_SEARCH_QUERY_PREFIX));
		assertThat(constructedQuery, containsString(SHIPPING_SERVICE_LEVEL_STORE_PARAMETER));
		assertEquals("Incorrect Shipping Region UID.", EXPECTED_SHIPPING_REGION_UID, searchParameters.get(0));
		assertEquals("Incorrect Store UID.", EXPECTED_STORE_UID, searchParameters.get(1));
	}

	/**
	 * Test query and parameter building with all parameters valid.
	 */
	@Test
	public void testQueryAndParameterBuildingWithAllParametersValid() {
		shippingLevelSearchCriteria.setShippingRegionUid(EXPECTED_SHIPPING_REGION_UID);
		shippingLevelSearchCriteria.setStoreUid(EXPECTED_STORE_UID);
		shippingLevelSearchCriteria.setState(ShippingLevelsSearchCriteriaImpl.STATE_INACTIVE);

		String constructedQuery = shippingServiceLevelServiceImpl.buildQueryAndParamsForCriteria(shippingLevelSearchCriteria, searchParameters);
		assertThat(constructedQuery, containsString(SHIPPING_SERVICE_LEVEL_SEARCH_QUERY_PREFIX));
		assertThat(constructedQuery, containsString(SHIPPING_SERVICE_LEVEL_STORE_PARAMETER));
		assertThat(constructedQuery, containsString(SHIPPING_SERVICE_LEVEL_STATE_PARAMETER));
		assertEquals("Incorrect Shipping Region UID.", EXPECTED_SHIPPING_REGION_UID, searchParameters.get(0));
		assertEquals("Incorrect Store UID.", EXPECTED_STORE_UID, searchParameters.get(1));
		boolean expectedShippingLevelState = false;
		assertEquals("Invalid shipping level state.", expectedShippingLevelState, searchParameters.get(2));
	}
	/**
	 * Retrieve shipping service level with valid store and address.
	 */
	@Test
	public void retrieveShippingServiceLevelWithValidStoreAndAddress() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(
						with(containsString(SELECT_BY_STORE_AND_STATE_QUERY)),
						with(equal(new Object[] { STORE_CODE, true })));
				will(returnValue(getShippingServiceLevels()));
			}
		});
		Address address = new CustomerAddressImpl();
		address.setCountry(COUNTRY_CODE_CA);
		address.setSubCountry(SUB_COUNTRY_CODE_BC);
		List<ShippingServiceLevel> actualShippingServiceLevels =
			shippingServiceLevelServiceImpl.retrieveShippingServiceLevel(STORE_CODE, address);

		assertEquals(1, actualShippingServiceLevels.size());
		ShippingServiceLevel actualShippingServiceLevel = actualShippingServiceLevels.get(0);
		ShippingRegion actualShippingRegion = actualShippingServiceLevel.getShippingRegion();

		Map<String, Region> associatedRegionMap = actualShippingRegion.getRegionMap();
		assertTrue("Country code" + COUNTRY_CODE_CA + " is missing from region map.",
				associatedRegionMap.containsKey(COUNTRY_CODE_CA));

		Region associatedRegion = associatedRegionMap.get(COUNTRY_CODE_CA);
		assertTrue("Subcountry code" + SUB_COUNTRY_CODE_BC + " is missing from region subcountry list.",
				associatedRegion.getSubCountryCodeList().contains(SUB_COUNTRY_CODE_BC));
	}

	/**
	 * Retrieve shipping service level with invalid store.
	 */
	@Test
	public void retrieveShippingServiceLevelWithInvalidStore() {
		final Store invalidStore = new StoreImpl();
		invalidStore.setCode(INVALID_STORE_CODE);

		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(
						with(containsString(SELECT_BY_STORE_AND_STATE_QUERY)),
						with(equal(new Object[] { INVALID_STORE_CODE, true })));
				will(returnValue(Collections.emptyList()));
			}
		});
		Address address = new CustomerAddressImpl();
		address.setCountry(COUNTRY_CODE_CA);
		address.setSubCountry(SUB_COUNTRY_CODE_BC);
		List<ShippingServiceLevel> actualShippingServiceLevels =
			shippingServiceLevelServiceImpl.retrieveShippingServiceLevel(INVALID_STORE_CODE, address);
		assertTrue("No shipping service levels should be returned.", actualShippingServiceLevels.isEmpty());
	}

	/**
	 * Retrieve shipping service level with address in unsupported country.
	 */
	@Test
	public void retrieveShippingServiceLevelWithAddressInUnsupportedCountry() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(
						with(containsString(SELECT_BY_STORE_AND_STATE_QUERY)),
						with(equal(new Object[] { STORE_CODE, true })));
				will(returnValue(getShippingServiceLevels()));
			}
		});
		Address address = new CustomerAddressImpl();
		address.setCountry("NA");
		address.setSubCountry("NA");
		List<ShippingServiceLevel> actualShippingServiceLevels = shippingServiceLevelServiceImpl.retrieveShippingServiceLevel(STORE_CODE, address);
		assertTrue("No shipping service levels should be returned.", actualShippingServiceLevels.isEmpty());
	}

	/**
	 * Retrieve shipping service level with null address .
	 */
	@Test
	public void retrieveShippingServiceLevelWithNullAddress() {
		List<ShippingServiceLevel> actualShippingServiceLevels = shippingServiceLevelServiceImpl.retrieveShippingServiceLevel(STORE_CODE, null);
		assertTrue("No shipping service levels should be returned.", actualShippingServiceLevels.isEmpty());
	}



	/**
	 * Retrieve shipping service level with valid ShoppingCart.
	 */
	@Test
	public void retrieveShippingServiceLevelWithValidShoppingCart() {
		final Address address = new CustomerAddressImpl();
		address.setCountry(COUNTRY_CODE_CA);
		address.setSubCountry(SUB_COUNTRY_CODE_BC);

		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);

		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(
						with(containsString(SELECT_BY_STORE_AND_STATE_QUERY)),
						with(equal(new Object[] { STORE_CODE, true })));
				will(returnValue(getShippingServiceLevels()));

				oneOf(shoppingCart).getShippingAddress();
				will(returnValue(address));

				oneOf(shoppingCart).getStore();
				will(returnValue(store));
			}
		});

		List<ShippingServiceLevel> actualShippingServiceLevels = shippingServiceLevelServiceImpl.retrieveShippingServiceLevel(shoppingCart);
		assertEquals(1, actualShippingServiceLevels.size());
		ShippingServiceLevel actualShippingServiceLevel = actualShippingServiceLevels.get(0);
		ShippingRegion actualShippingRegion = actualShippingServiceLevel.getShippingRegion();

		Map<String, Region> associatedRegionMap = actualShippingRegion.getRegionMap();
		assertTrue("Country code" + COUNTRY_CODE_CA + " is missing from the shipping service level's region map.",
				associatedRegionMap.containsKey(COUNTRY_CODE_CA));

		Region associatedRegion = associatedRegionMap.get(COUNTRY_CODE_CA);
		assertTrue("Subcountry code" + SUB_COUNTRY_CODE_BC + " is missing from region subcountry list.",
				associatedRegion.getSubCountryCodeList().contains(SUB_COUNTRY_CODE_BC));
	}

	/**
	 * Gets a list of shipping regions that should include the United States, Canada, and Great Britain.
	 *
	 * @return list of shipping regions
	 */
	private List<ShippingRegion> getTestShippingRegionList() {
		List<ShippingRegion> shippingRegionList = new ArrayList<ShippingRegion>();
		shippingRegionList.add(createShippingRegionInCanada());
		shippingRegionList.add(createShippingRegionInCanadaAndUS());
		shippingRegionList.add(createShippingRegionInGreatBritain());
		return shippingRegionList;
	}

	private ShippingRegion createShippingRegionInCanada() {
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		Region region = new RegionImpl(COUNTRY_CODE_CA, Arrays.asList(EXPECTED_SUB_COUNTRY_CODE_AB, SUB_COUNTRY_CODE_BC));
		Map<String, Region> regionMap1 = new HashMap<String, Region>();
		regionMap1.put(COUNTRY_CODE_CA, region);

		shippingRegion.setRegionMap(regionMap1);
		return shippingRegion;
	}

	private ShippingRegion createShippingRegionInCanadaAndUS() {
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		Region canadaRegion = new RegionImpl(COUNTRY_CODE_CA, Arrays.asList(EXPECTED_SUB_COUNTRY_CODE_ON));
		Map<String, Region> regionMap = new HashMap<String, Region>();
		regionMap.put(COUNTRY_CODE_CA, canadaRegion);

		Region unitedStatesRegion = new RegionImpl(COUNTRY_CODE_US);
		regionMap.put(COUNTRY_CODE_US, unitedStatesRegion);

		shippingRegion.setRegionMap(regionMap);
		return shippingRegion;
	}

	private ShippingRegion createShippingRegionInGreatBritain() {
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		Region region = new RegionImpl(COUNTRY_CODE_GB);
		Map<String, Region> regionMap = new HashMap<String, Region>();
		regionMap.put(COUNTRY_CODE_GB, region);
		shippingRegion.setRegionMap(regionMap);
		return shippingRegion;
	}

	private ShippingServiceLevel createServiceLevelFromStoreAndRegion(final Store store, final ShippingRegion shippingRegion) {
		ShippingServiceLevel shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setEnabled(true);
		shippingServiceLevel.setStore(store);
		shippingServiceLevel.setShippingRegion(shippingRegion);
		return shippingServiceLevel;
	}

	private List<ShippingServiceLevel> getShippingServiceLevels() {
		final List<ShippingServiceLevel> shippingServiceLevels = new ArrayList<ShippingServiceLevel>();
		for (ShippingRegion shippingRegion : getTestShippingRegionList()) {
			shippingServiceLevels.add(createServiceLevelFromStoreAndRegion(store, shippingRegion));
		}
		return shippingServiceLevels;
	}

	private void setGeographyExpectations() {
		context.checking(new Expectations() {
			{
				allowing(mockGeography).getCountryDisplayName(COUNTRY_CODE_CA, Locale.ENGLISH);
				will(returnValue("Canada"));
				allowing(mockGeography).getCountryDisplayName(COUNTRY_CODE_US, Locale.ENGLISH);
				will(returnValue("United States"));
				allowing(mockGeography).getCountryDisplayName(COUNTRY_CODE_GB, Locale.ENGLISH);
				will(returnValue("Great Britain"));

				allowing(mockGeography).getSubCountryDisplayName(COUNTRY_CODE_CA, EXPECTED_SUB_COUNTRY_CODE_AB, Locale.CANADA_FRENCH);
				will(returnValue(SUBCOUNTRY_ALBERTA_NAME));
				allowing(mockGeography).getSubCountryDisplayName(COUNTRY_CODE_CA, SUB_COUNTRY_CODE_BC, Locale.CANADA_FRENCH);
				will(returnValue(SUBCOUNTRY_BRITISH_COLUMBIA_NAME));
				allowing(mockGeography).getSubCountryDisplayName(COUNTRY_CODE_CA, EXPECTED_SUB_COUNTRY_CODE_ON, Locale.CANADA_FRENCH);
				will(returnValue(SUBCOUNTRY_ONTARIO_NAME));
			}
		});
	}
}
