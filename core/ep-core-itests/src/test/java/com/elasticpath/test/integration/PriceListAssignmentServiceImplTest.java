package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.test.persister.TestDataPersisterFactory;

/**
 * Integration test class for PriceListAssignementServiceImpl.
 */

public class PriceListAssignmentServiceImplTest extends BasicSpringContextTest {
	
	private static final String NAMES_FOR_THE_NON_HIDDEN_SHOULD_BE_THE_SAME = "Names for the non hidden should be the same";

	private static final String QUERY_CURRENCY_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG = 
		"We query a currency code that doesn't exist, should not return anything regardless of the hidden flag.";

	private static final String RANDOM_CODE = "RANDOM_CODE";

	private static final String ONLY_1_NON_HIDDEN_PLA = "Only 1 non hidden pla";

	private static final String TOTAL_NUMBER_OF_PLAS_IN_SYSTEM_SHOULD_BE_2 = "Total number of plas in system should be 2";

	private static final String QUERY_CATALOG_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG = "We query a catalog code that doesn't exist,"
			+ " should not return anything regardless of the hidden flag.";

	private static final String PRICE_LIST_CODE = "pl1";
	
	private static final String PRICE_LIST_NAME = PRICE_LIST_CODE;

	private static final String NON_HIDDEN_PLA_NAME = "pla";

	private static final String PLA_HIDDEN_NAME = "plaHidden";

	private static final String CATALOG_CODE = "catalog1";
	
	private static final String CATALOG_NAME = CATALOG_CODE;

	private static final String DEFAULT_CURRENCY = "CAD";

	private static final String DEFAULT_DESCRIPTION = "DESC";

	@Autowired
	private PriceListAssignmentService plaService;

	private TestDataPersisterFactory persistersFactory;

	private static Catalog defaultCatalog;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		persistersFactory = getTac().getPersistersFactory();
	}
	
	/**
	 * Test for method list(). Will return all plas or plas that are non hidden.
	 */
	@DirtiesDatabase
	@Test
	public void testListInludingHiddenPLAs() {
		createHiddenAndNonHiddenPLA();
		
		assertEquals(TOTAL_NUMBER_OF_PLAS_IN_SYSTEM_SHOULD_BE_2, 2, plaService.list(true).size());
		assertEquals(ONLY_1_NON_HIDDEN_PLA, 1, plaService.list(false).size());
		assertEquals(NAMES_FOR_THE_NON_HIDDEN_SHOULD_BE_THE_SAME, 
				plaService.list(false).get(0).getName(), NON_HIDDEN_PLA_NAME);
	}
	
	/**
	 * Test for method listByCatalogAndCurrencyCode(). Ensures it filters out hidden price lists if flag is set to false.
	 */
	@DirtiesDatabase
	@Test
	public void testListByCatalogAndCurrencyCode() {
		createHiddenAndNonHiddenPLA();
		
		assertEquals(TOTAL_NUMBER_OF_PLAS_IN_SYSTEM_SHOULD_BE_2, 2, 
				plaService.listByCatalogAndCurrencyCode(CATALOG_CODE, DEFAULT_CURRENCY, true).size());
		assertEquals(ONLY_1_NON_HIDDEN_PLA, 1, 
				plaService.listByCatalogAndCurrencyCode(CATALOG_CODE, DEFAULT_CURRENCY, false).size());
	
		assertEquals(NAMES_FOR_THE_NON_HIDDEN_SHOULD_BE_THE_SAME, 
				plaService.listByCatalogAndCurrencyCode(CATALOG_CODE, DEFAULT_CURRENCY, false).
					get(0).getName(), NON_HIDDEN_PLA_NAME);
		
		assertTrue(QUERY_CATALOG_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG,
				plaService.listByCatalogAndCurrencyCode(RANDOM_CODE, DEFAULT_CURRENCY, true).isEmpty());	
		assertTrue(QUERY_CATALOG_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG,
				plaService.listByCatalogAndCurrencyCode(RANDOM_CODE, DEFAULT_CURRENCY, false).isEmpty());
		
		assertTrue(QUERY_CURRENCY_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG,
				plaService.listByCatalogAndCurrencyCode(CATALOG_CODE, "USD", true).isEmpty());	
		assertTrue(QUERY_CURRENCY_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG,
				plaService.listByCatalogAndCurrencyCode(CATALOG_CODE, "USD", false).isEmpty());
	}
	
	/**
	 * Test for method listByCatalogAndPriceListNames(). Ensures it filters out hidden price lists if flag is set to false.
	 */
	@DirtiesDatabase
	@Test
	public void testListByCatalogAndPriceListName() {
		createHiddenAndNonHiddenPLA();
		
		assertEquals(TOTAL_NUMBER_OF_PLAS_IN_SYSTEM_SHOULD_BE_2, 2, 
				plaService.listByCatalogAndPriceListNames(CATALOG_CODE, PRICE_LIST_NAME, true).size());
		assertEquals(ONLY_1_NON_HIDDEN_PLA, 1, 
				plaService.listByCatalogAndPriceListNames(CATALOG_CODE, PRICE_LIST_NAME, false).size());
		
		assertEquals(NAMES_FOR_THE_NON_HIDDEN_SHOULD_BE_THE_SAME, 
				plaService.listByCatalogAndPriceListNames(CATALOG_CODE, PRICE_LIST_NAME, false)
					.get(0).getName(), NON_HIDDEN_PLA_NAME);
		
		assertTrue(QUERY_CATALOG_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG,
				plaService.listByCatalogAndPriceListNames(RANDOM_CODE, PRICE_LIST_NAME, true).isEmpty());	
		assertTrue(QUERY_CATALOG_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG,
				plaService.listByCatalogAndPriceListNames(RANDOM_CODE, PRICE_LIST_NAME, false).isEmpty());
		
		assertTrue(QUERY_CURRENCY_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG,
				plaService.listByCatalogAndPriceListNames(CATALOG_CODE, "RANDOM_NAME", true).isEmpty());	
		assertTrue(QUERY_CURRENCY_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG,
				plaService.listByCatalogAndPriceListNames(CATALOG_CODE, "RANDOM_NAME", false).isEmpty());
		
	}
	
	/**
	 * Test for method listByCatalog(Catalog). Ensures it filters out hidden price lists if flag is set to false.
	 */
	@DirtiesDatabase
	@Test
	public void testListByCatalog()  throws InterruptedException {
		createHiddenAndNonHiddenPLA();
		
		assertEquals(TOTAL_NUMBER_OF_PLAS_IN_SYSTEM_SHOULD_BE_2, 2, 
				plaService.listByCatalog(defaultCatalog, true).size());
		assertEquals(ONLY_1_NON_HIDDEN_PLA, 1, 
				plaService.listByCatalog(defaultCatalog, false).size());
		
		assertEquals(NAMES_FOR_THE_NON_HIDDEN_SHOULD_BE_THE_SAME, 
				plaService.listByCatalog(defaultCatalog, false).get(0).getName(), NON_HIDDEN_PLA_NAME);
		
		assertTrue(QUERY_CATALOG_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG,
				plaService.listByCatalog(createRandomCatalog(), true).isEmpty());	
		Thread.sleep(1);
		assertTrue(QUERY_CATALOG_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG,
				plaService.listByCatalog(createRandomCatalog(), false).isEmpty());
		
	}
	
	/**
	 * Test for method listByCatalog(String). Ensures it filters out hidden price lists if flag is set to false.
	 */
	@DirtiesDatabase
	@Test
	public void testListByCatalogCode() {
		createHiddenAndNonHiddenPLA();
		
		assertEquals(TOTAL_NUMBER_OF_PLAS_IN_SYSTEM_SHOULD_BE_2, 2, 
				plaService.listByCatalog(CATALOG_CODE, true).size());
		assertEquals(ONLY_1_NON_HIDDEN_PLA, 1, 
				plaService.listByCatalog(CATALOG_CODE, false).size());
		
		assertEquals(NAMES_FOR_THE_NON_HIDDEN_SHOULD_BE_THE_SAME, 
				plaService.listByCatalog(CATALOG_CODE, false).get(0).getName(), NON_HIDDEN_PLA_NAME);
		
		assertTrue(QUERY_CATALOG_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG,
				plaService.listByCatalog(RANDOM_CODE, true).isEmpty());	
		assertTrue(QUERY_CATALOG_EXPECT_EMPTY_REGARDLESS_OF_HIDDEN_FLAG,
				plaService.listByCatalog(RANDOM_CODE, false).isEmpty());
		
	}

	private Catalog createRandomCatalog() {
		List<String> currencies = new ArrayList<String>();
		List<String> locales = new ArrayList<String>();
		
		return persistersFactory.getCatalogTestPersister().persistCatalog(
				CATALOG_CODE + System.currentTimeMillis(), CATALOG_NAME + System.currentTimeMillis(),
				true, DEFAULT_CURRENCY, "EN", currencies, locales, false);
	}

	/**
	 * Creates a hidden and a non-hidden PLA.
	 */
	private void createHiddenAndNonHiddenPLA() {
		List<String> currencies = new ArrayList<String>();
		List<String> locales = new ArrayList<String>();
		
		defaultCatalog = persistersFactory.getCatalogTestPersister().persistCatalog(
				CATALOG_CODE, CATALOG_NAME, true, DEFAULT_CURRENCY, "EN", currencies, locales, false);
		
		PriceListDescriptor priceList = persistersFactory.getPriceListPersister().createAndPersistPriceList(
				PRICE_LIST_CODE, PRICE_LIST_NAME, DEFAULT_CURRENCY, DEFAULT_DESCRIPTION, true);
		
		PriceListAssignment plAssignmentHidden = persistersFactory.getPriceListAssignmentPersister().
			createPriceListAssignment(defaultCatalog.getGuid(), priceList.getGuid(), PLA_HIDDEN_NAME, DEFAULT_DESCRIPTION, 0);
		
		plAssignmentHidden.setHidden(true);
		
		PriceListAssignment plAssignment = persistersFactory.getPriceListAssignmentPersister().
		createPriceListAssignment(defaultCatalog.getGuid(), priceList.getGuid(), NON_HIDDEN_PLA_NAME, DEFAULT_DESCRIPTION, 0);

		
		plaService.saveOrUpdate(plAssignmentHidden);
		plaService.saveOrUpdate(plAssignment);
	}
	
	
	
}
