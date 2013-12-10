package com.elasticpath.service.store.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.store.Store;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.store.StoreAssociationService;
import com.elasticpath.service.store.StoreService;

/**
 * Test cases for {@link StoreAssociationServiceImpl}. 
 */
public class StoreAssociationServiceImplTest {

	private static final String STORE_CODE_FOR_NULL_STORE = "NullStore";
	private static final String STORE_CODE_WITH_NULL_ASSOCIATION_LIST = "NullAssociationList";
	private static final String STORE_CODE_WITH_NO_ASSOCIATIONS = "NoAssociations";
	private static final String STORE_CODE_ALPHA = "Alpha";
	private static final String STORE_CODE_BETA = "Beta";
	private static final String STORE_CODE_CENTAURI = "Centauri";
	private static final String STORE_CODE_DELTA = "Delta";
	private static final String STORE_CODE_EPSILON = "Epsilon";
	private static final String STORE_CODE_PHI = "Phi";
	private static final String STORE_CODE_GAMMA = "Gamma";
	private static final String STORE_CODE_ZETA = "Zeta";
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final StoreService storeService = context.mock(StoreService.class);

	private StoreAssociationService storeAssociationService;

	/**
	 * Prepare Stores and StoreService.
	 * @throws Exception on error.
	 */
	@Before
	public void setUp() throws Exception {
		setUpStores();

		StoreAssociationServiceImpl storeAssociationServiceImpl = new StoreAssociationServiceImpl();
		storeAssociationServiceImpl.setStoreService(storeService);
		storeAssociationService = storeAssociationServiceImpl;
	}
	
	private void setUpStores() {
		
		final Long storeUidForNullStore = -2L;
		final Long storeUidForNullAssociationList = -1L;	
		final Long storeUidForEmptyAssociationList = 0L;
		final Long storeUidAlpha = 1L;
		final Long storeUidBeta = 2L;
		final Long storeUidCentauri = 3L;
		final Long storeUidDelta = 4L;
		final Long storeUidEpsilon = 5L;
		final Long storeUidPhi = 6L;
		final Long storeUidGamma = 7L;
		final Long storeUidZeta = 7L;
		
		// Set up the null store
		final Store nullStore = null;		
		context.checking(new Expectations() { {
			allowing(storeService).getStore(storeUidForNullStore); will(returnValue(nullStore));
			allowing(storeService).findStoreWithCode(STORE_CODE_FOR_NULL_STORE); will(returnValue(nullStore));
		} });	
		
		createStoreAndRegister(storeUidForNullAssociationList, STORE_CODE_WITH_NULL_ASSOCIATION_LIST, null);
		createStoreAndRegister(storeUidGamma, STORE_CODE_GAMMA, set(storeUidForNullStore));
		createStoreAndRegister(storeUidZeta, STORE_CODE_ZETA, set(storeUidForNullAssociationList));
		createStoreAndRegister(storeUidForEmptyAssociationList, STORE_CODE_WITH_NO_ASSOCIATIONS, this.<Long>set());
		createStoreAndRegister(storeUidAlpha, STORE_CODE_ALPHA, set(storeUidForEmptyAssociationList));

		// Bidirectional association
		createStoreAndRegister(storeUidBeta, STORE_CODE_BETA, set(storeUidCentauri));
		createStoreAndRegister(storeUidCentauri, STORE_CODE_CENTAURI, set(storeUidBeta));
		
		// Circular relationship with three stores
		createStoreAndRegister(storeUidDelta, STORE_CODE_DELTA, set(storeUidEpsilon));
		createStoreAndRegister(storeUidEpsilon, STORE_CODE_EPSILON, set(storeUidPhi));
		createStoreAndRegister(storeUidPhi, STORE_CODE_PHI, set(storeUidDelta));		
	}
	
	
	private void createStoreAndRegister(final Long storeUid, final String storeCode, final Collection<Long> associatedStoreUids) {
		final Store store = context.mock(Store.class, storeCode);
		context.checking(new Expectations() { {
			allowing(store).getCode(); will(returnValue(storeCode));
			allowing(store).getAssociatedStoreUids(); will(returnValue(associatedStoreUids));
			
			allowing(storeService).getStore(storeUid); will(returnValue(store));
			allowing(storeService).findStoreWithCode(storeCode); will(returnValue(store));
		} });	
	}

	/**
	 * Tests service on non-existent {@link Store} code.
	 */
	@Test
	public void testServiceWithNullStore() {
		try {
			storeAssociationService.getAllAssociatedStoreCodes(STORE_CODE_FOR_NULL_STORE);

			fail("EpServiceException should have been thrown for non-existent StoreCode.");
			
		} catch (EpServiceException storeAssociationServiceException) {
			assertThat("Exception message incorrect",
					storeAssociationServiceException.getMessage(),
					containsString("No store exists with code"));
		}
	}	
	
	/**
	 * Tests service on null association list.
	 */
	@Test
	public void testNullAssociationList() {
		Set<String> associatedStoreCodes = storeAssociationService.getAllAssociatedStoreCodes(STORE_CODE_WITH_NULL_ASSOCIATION_LIST);
		Assert.assertTrue("Store code list should be empty.", associatedStoreCodes.isEmpty());
	}	
	
	/**
	 * Tests service on empty association list.
	 */
	@Test
	public void testEmptyAssociationList() {
		Set<String> associatedStoreCodes = storeAssociationService.getAllAssociatedStoreCodes(STORE_CODE_WITH_NO_ASSOCIATIONS);
		Assert.assertTrue("Store code list should be empty.", associatedStoreCodes.isEmpty());
	}

	/**
	 * Tests service on one store associated with a non-existent store.
	 */
	@Test
	public void testStoreAssociatedWithNonExistentStore() {
		try {
			storeAssociationService.getAllAssociatedStoreCodes(STORE_CODE_GAMMA);

			fail("EpServiceException should have been thrown for non-existent StoreCode.");
			
		} catch (EpServiceException storeAssociationServiceException) {

			assertThat("Exception message incorrect", 
					storeAssociationServiceException.getMessage(),
					containsString("No store exists with uid"));
		}
	}

	/**
	 * Tests service on one store associated with a store that has a null association list.
	 */
	@Test
	public void testStoreAssociatedWithNullAssociationListStore() {
		assertAssociations(STORE_CODE_ZETA, set(STORE_CODE_WITH_NULL_ASSOCIATION_LIST));
	}
	
	/**
	 * Tests service on one store associated with a store that has no associations.
	 */
	@Test
	public void testStoreAssociatedWithEmptyAssociationStore() {
		assertAssociations(STORE_CODE_ALPHA, set(STORE_CODE_WITH_NO_ASSOCIATIONS));
	}
	
	/**
	 * Tests service on two stores associated with each other.
	 */
	@Test
	public void testTwoStoresAssociatedWithEachOther() {
		assertAssociations(STORE_CODE_BETA, set(STORE_CODE_CENTAURI));
	}	

	/**
	 * Tests service on three stores in a circular association.
	 */
	@Test
	public void testThreeStoresInACircularAssociation() {
		assertAssociations(STORE_CODE_DELTA, set(STORE_CODE_PHI, STORE_CODE_EPSILON));
	}

	/**
	 * Tests service with initial set of two Stores on test case of two stores in a circular association.
	 */
	@Test
	public void testInitialSetOfTwoStoresOnThreeStoresInACircularAssociation() {
		assertAssociationSet(set(STORE_CODE_DELTA, STORE_CODE_PHI), set(STORE_CODE_EPSILON));
	}

	
	/**
	 * Tests service with initial set of two Stores on test case of three stores in a circular association.
	 */
	@Test
	public void testInitialSetOfTwoStoresAssociatedWithEachOther() {
		assertAssociationSet(set(STORE_CODE_BETA, STORE_CODE_CENTAURI), Collections.<String>emptySet());
	}

	/**
	 * Tests service with two separate sets of associated stores.
	 */
	@Test
	public void testTwoSeperateSetsOfAssociatedStores() {
		assertAssociationSet(set(STORE_CODE_CENTAURI, STORE_CODE_PHI), set(STORE_CODE_BETA, STORE_CODE_DELTA, STORE_CODE_EPSILON));
	}
	
	/**
	 * Tests service with null initial store code.
	 */
	@Test
	public void testNullInitialStoreCode() {
		
		String nullStoreCode = null;
		try {
			storeAssociationService.getAllAssociatedStoreCodes(nullStoreCode);

			fail("EpServiceException should have been thrown for null store code.");
			
		} catch (EpServiceException storeAssociationServiceException) {

			assertThat("Incorrect exception message", 
					storeAssociationServiceException.getMessage(),
					containsString("Initial store code cannot be null"));
		}

	}
	
	/**
	 * Tests service with null initial store code.
	 */
	@Test
	public void testNullInitialSet() {
		
		Set <String> nullStoreCodeSet = null;
		try {
			storeAssociationService.getAllAssociatedStoreCodes(nullStoreCodeSet);

			fail("EpServiceException should have been thrown for null store code set.");
			
		} catch (EpServiceException storeAssociationServiceException) {

			assertThat("Exception message incorrect", 
					storeAssociationServiceException.getMessage(),
					containsString("Initial store code set cannot be null"));
		}

	}

	/**
	 * Tests service with empty initial store code set.
	 */
	@Test
	public void testEmptyInitialSet() {
		assertAssociationSet(Collections.<String>emptySet(), Collections.<String>emptySet());
	}
	
	private void assertAssociations(final String initialStoreCode, final Set<String> expectedAssociatedStoreCodes) {
		Set<String> actualAssociatedStoreCodes = storeAssociationService.getAllAssociatedStoreCodes(initialStoreCode);
		verifyAssociations(expectedAssociatedStoreCodes, actualAssociatedStoreCodes);
	}
	private void assertAssociationSet(final Set<String> initialStoreCodes, final Set<String> expectedAssociatedStoreCodes) {
		Set<String> actualAssociatedStoreCodes = storeAssociationService.getAllAssociatedStoreCodes(initialStoreCodes);
		verifyAssociations(expectedAssociatedStoreCodes, actualAssociatedStoreCodes);
	}

	/**
	 * Verifies actual set of store codes matches the expected set.
	 * @param expectedAssociatedStoreCodes the expected store codes
	 * @param actualAssociatedStoreCodes the actual store codes
	 */
	private void verifyAssociations(final Set<String> expectedAssociatedStoreCodes,	final Set<String> actualAssociatedStoreCodes) {
		Assert.assertEquals("Associated store code count", expectedAssociatedStoreCodes.size(), actualAssociatedStoreCodes.size());

		Assert.assertTrue("Store code list should contain "  + expectedAssociatedStoreCodes, 
				actualAssociatedStoreCodes.containsAll(expectedAssociatedStoreCodes));
	}
	
	
	
	private <T> Set<T> set(final T ... elems) {
		return new HashSet<T>(Arrays.asList(elems));
	}	
}
