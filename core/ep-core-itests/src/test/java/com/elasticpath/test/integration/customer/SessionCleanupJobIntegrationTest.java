/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.test.integration.customer;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.SessionCleanupJob;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShoppingCartFactoryForTestApplication;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.CustomerSessionCleanupService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shopper.ShopperCleanupService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.integration.cart.AbstractCartIntegrationTestParent;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * TODO.
 */
public class SessionCleanupJobIntegrationTest extends AbstractCartIntegrationTestParent {

	private static final String SHOULD_HAVE_BEEN_DELETED = "should have been deleted!";

	private static final String SHOULD_NOT_BE_DELETED = "should not be deleted!";

	private static final String WAS_NOT_PERSISTED = "was not persisted!";

	private static final int CUSTOMER_SESSION_CREATION_DAYS_AGO = 50;

	private static final int OLD_CUSTOMER_SESSION_ACCESSED_DAYS_AGO = 30;

	private static final int RECENT_CUSTOMER_SESSION_ACCESSED_DAYS_AGO = 10;

	private static final int DELETE_BEFORE_NUMBER_OF_DAYS = 20;

	@Autowired
	private CartDirector cartDirector;

	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerSessionService customerSessionService;

	@Autowired
	private CustomerSessionCleanupService customerSessionCleanupService;

	@Autowired
	private ShopperService shopperService;

	@Autowired
	private ShopperCleanupService shopperCleanupService;

	@Autowired
	private WishListService wishListService;

	@Autowired
	private SettingsService settingsService;

	private SessionCleanupJob sessionCleanupJob;

	private Product defaultProduct;

	/**
	 * Representing age of access date.
	 */
	private enum AccessAge {
		OLD, RECENT, MULTIAGED
	}

	/**
	 * Different types of carts.
	 */
	private enum CartType {
		NONE, EMPTY, NON_EMPTY
	}

	/**
	 * Different types of users.
	 */
	private enum CustomerType {
		ANONYMOUS, REGISTERED
	}


	/**
	 * Just a DTO that holds all the Sets we want to track for verification.
	 */
	private class TrackingSets {
		/** deletedSessions. */
		public final Set<CustomerSession> deletedSessions = new HashSet<CustomerSession>();
		/** keptSessions. */
		public final Set<CustomerSession> keptSessions = new HashSet<CustomerSession>();
		/** deletedShoppers. */
		public final Set<Shopper> deletedShoppers = new HashSet<Shopper>();
		/** keptShoppers. */
		public final Set<Shopper> keptShoppers = new HashSet<Shopper>();
	}

	/**
	 * Used as a verifying interface.
	 */
	private interface Verifier<T> {
		boolean verify(T test);
	}

	/**
	 *
	 * @throws java.lang.Exception Exception.
	 */
	@Before
	public void setUp() throws Exception {
		sessionCleanupJob = createSessionCleanupJob();
		defaultProduct = persistProductWithSku();

		// Setup expected settings.
		overrideSetting(SessionCleanupJob.SESSION_CLEANUP_MAX_HISTORY, String.valueOf(DELETE_BEFORE_NUMBER_OF_DAYS));
	}

	private SessionCleanupJob createSessionCleanupJob() {
		TimeService timeService = getBeanFactory().getBean(ContextIdNames.TIME_SERVICE);

		SessionCleanupJob sessionCleanupJob = new SessionCleanupJob();
		sessionCleanupJob.setTimeService(timeService);
		sessionCleanupJob.setCustomerSessionCleanupService(customerSessionCleanupService);
		sessionCleanupJob.setSettingsReader(settingsService);
		sessionCleanupJob.setShoppingCartService(shoppingCartService);
		sessionCleanupJob.setWishlistService(wishListService);
		sessionCleanupJob.setShopperCleanupService(shopperCleanupService);

		return sessionCleanupJob;
	}

	/**
	 * Tests setting up 8 different customer sessions where some sessions qualify for deletion.
	 *
	 * These are the target sessions:
	 * 1. Old Anonymous Customer Session w/ no Shopping Cart
	 * 2. Old Anonymous Customer Session w/ empty Shopping Cart
	 * 3. Old Anonymous Customer Session w/ a non-empty Shopping Cart
	 * 4. Recent Anonymous Customer Session w/ no Shopping Cart.
	 * 5. Old Registered (A) Customer Session w/ empty Shopping Cart
	 * 6. Recent Registered (B) Customer Session w/ no Shopping Cart.
	 * 7. Recent Registered (C) Customer Session w/ a non-empty Shopping Cart.
	 * 8. Old Registered (D) Customer Session w/ a non-empty Shopping Cart.
	 *
	 * Expected to delete customer sessions 1-3, 5, 8 but to keep 4, 6 and 7.
	 * Expected to keep shoppers 4, 6 - 8.  (8 because the registered customer cart kept, but not the session).
	 */
	@DirtiesDatabase
	@Test
	public void testStandard8Cases() {

		final TrackingSets track = new TrackingSets();

		// 1
		makeScenario(AccessAge.OLD, CustomerType.ANONYMOUS, CartType.NONE, "1", "A", track.deletedSessions, track.deletedShoppers);
		// 2
		makeScenario(AccessAge.OLD, CustomerType.ANONYMOUS, CartType.EMPTY, "2", "B", track.deletedSessions, track.deletedShoppers);
		// 3
		makeScenario(AccessAge.OLD, CustomerType.ANONYMOUS, CartType.NON_EMPTY, "3", "C", track.deletedSessions, track.deletedShoppers);
		// 4
		makeScenario(AccessAge.RECENT, CustomerType.ANONYMOUS, CartType.NONE, "4", "D", track.keptSessions, track.keptShoppers);

		// 5
		makeScenario(AccessAge.OLD, CustomerType.REGISTERED, CartType.EMPTY, "5", "E", track.deletedSessions, track.deletedShoppers);
		// 6
		makeScenario(AccessAge.RECENT, CustomerType.REGISTERED, CartType.NONE, "6", "F", track.keptSessions, track.keptShoppers);
		// 7
		makeScenario(AccessAge.RECENT, CustomerType.REGISTERED, CartType.NON_EMPTY, "7", "G", track.keptSessions, track.keptShoppers);
		// 8
		makeScenario(AccessAge.OLD, CustomerType.REGISTERED, CartType.NON_EMPTY, "8", "H", track.deletedSessions, track.keptShoppers);

		// Verify that I can find all created customer sessions and shoppers.
		verifyCreations(track, 0);

		// Test
		sessionCleanupJob.purgeSessionHistory();

		// Verify
		verifyDeletions(track, 1);
	}

	/**
	 * Test to see that Orphaned shoppers that have customers and non-empty carts are not deleted if the cleanup
	 * job happens more than once.
	 *
	 * These are the target sessions:
	 * No. |   CS   | Shopper | Cart Empty | Recent
	 * ----+--------+---------+------------+--------
	 *  1. |    1   |    A    |    Empty   | recent
	 *  2. |    2   |    A    |    Empty   |  old
	 *  3. |    3   |    B    |  Not-Empty | recent
	 *  4. |    4   |    B    |  Not-Empty |  old
	 *  5. |  5, 6  |    C    |    Empty   |  old
	 *  6. |  7, 8  |    D    |  Not-Empty |  old
	 *
	 * Expected:
	 * - to delete: customer sessions 2, 4 - 8.
	 * - to keep: customer sessions 1, 3.
	 * - to delete: shoppers C. (Because old and empty.)
	 * - to keep: shoppers A, B, D.
	 */
	@DirtiesDatabase
	@Test
	public void testOrphanedShopperWithCustomerAndNonEmptyCartPersistsAfterMultipleCleanups() {

		final TrackingSets track = new TrackingSets();

		// A: 1, 2
		final Shopper shopperA = makeShopperAndCustomer(AccessAge.MULTIAGED, CustomerType.REGISTERED, CartType.EMPTY, "A",
				track.keptShoppers);
		makeCustomerSessionAndCart(AccessAge.RECENT, CartType.EMPTY, "1", shopperA, track.keptSessions);
		makeCustomerSessionAndCart(AccessAge.OLD, CartType.EMPTY, "2", shopperA, track.deletedSessions);

		// B: 3, 4
		final Shopper shopperB = makeShopperAndCustomer(AccessAge.MULTIAGED, CustomerType.REGISTERED, CartType.EMPTY, "b",
				track.keptShoppers);
		makeCustomerSessionAndCart(AccessAge.RECENT, CartType.EMPTY, "3", shopperB, track.keptSessions);
		makeCustomerSessionAndCart(AccessAge.OLD, CartType.EMPTY, "4", shopperB, track.deletedSessions);

		// C: 5, 6
		final Shopper shopperC = makeShopperAndCustomer(AccessAge.OLD, CustomerType.REGISTERED, CartType.EMPTY, "C",
				track.deletedShoppers);
		makeCustomerSessionAndCart(AccessAge.OLD, CartType.EMPTY, "5", shopperC, track.deletedSessions);
		makeCustomerSessionAndCart(AccessAge.OLD, CartType.EMPTY, "6", shopperC, track.deletedSessions);

		// D: 7, 8
		final Shopper shopperD = makeShopperAndCustomer(AccessAge.OLD, CustomerType.REGISTERED, CartType.NON_EMPTY, "D",
				track.keptShoppers);
		makeCustomerSessionAndCart(AccessAge.OLD, CartType.NON_EMPTY, "7", shopperD, track.deletedSessions);
		makeCustomerSessionAndCart(AccessAge.OLD, CartType.NON_EMPTY, "8", shopperD, track.deletedSessions);

		// Verify that I can find all created customer sessions and shoppers.
		verifyCreations(track, 0);

		// Orphaned Shoppers sometimes get deleted on the 2nd pass because the referring CustomerSession has disappeared.
		// So run twice.
		// 1
		sessionCleanupJob.purgeSessionHistory();
		verifyDeletions(track, 1);
		// 2
		sessionCleanupJob.purgeSessionHistory();
		verifyDeletions(track, 2);
	}

	/**
	 * Test to see that the job adheres to the Cleanup BatchSize setting.
	 *
	 * Idea is to create 15 customer sessions and to run the cleaner twice.  It should
	 * return 10 and 5 respectively.
	 *
	 * These are the target type of sessions:
	 * No. |   CS   | Shopper | Cart Empty | Recent
	 * ----+--------+---------+------------+--------
	 *  1. |    1   |    A    |    Empty   |  old
	 *
	 * Expected:
	 * Delete 10 first.
	 * Then Delete 5.
	 */
	@DirtiesDatabase
	@Test
	public void testCleanUpAdheresToBatchSize() {

		final TrackingSets track = new TrackingSets();
		final int initialSessionBatchSize = 27;
		final int cleanupBatchSize = 10;
		final int expectedLastPassSize = initialSessionBatchSize % cleanupBatchSize;

		overrideSetting(SessionCleanupJob.SESSION_CLEANUP_BATCH_SIZE, String.valueOf(cleanupBatchSize));

		for (int i = 0; i < initialSessionBatchSize; i++) {
			makeScenario(AccessAge.OLD, CustomerType.ANONYMOUS, CartType.NONE, String.valueOf(i), "A", track.deletedSessions, track.deletedShoppers);
		}

		// Verify that I can find all created customer sessions and shoppers.
		verifyCreations(track, 0);

		// First delete should delete 10 items.
		int leftOverSessions = initialSessionBatchSize;
		int checkDeleteSize = cleanupBatchSize;
		int loopCount = 0;
		while (leftOverSessions > 0) {
			assertTrue(String.format("The delete size (%d) did not match the batch size (%d) on loop %d.",
					checkDeleteSize, cleanupBatchSize, loopCount), checkDeleteSize == cleanupBatchSize);
			checkDeleteSize = sessionCleanupJob.purgeSessionHistory();
			leftOverSessions -= checkDeleteSize;
			loopCount++;
		}

		// Verify
		assertTrue(String.format("The delete size (%d) does not match expected size (%d) on last pass.",
				checkDeleteSize, expectedLastPassSize), checkDeleteSize == expectedLastPassSize);
		verifyDeletions(track, 1);
	}

	private void verifyCreations(final TrackingSets tracking, final int passNumber) {
		final Set<CustomerSession> allSessions = unionSessions(tracking.deletedSessions, tracking.keptSessions);
		verifyCustomerSessionsPersistedState(getCustomerSessionExistsVerifier(), allSessions, WAS_NOT_PERSISTED, passNumber);

		final Set<Shopper> allShoppers = unionShoppers(tracking.deletedShoppers, tracking.keptShoppers);
		verifyShoppersPersistedState(getShopperExistsVerifier(), allShoppers, WAS_NOT_PERSISTED, passNumber);
	}

	/**
	 * Verifies that the right things are deleted, and the wrong things are not deleted.
	 *
	 * @param tracking tracking DTO.
	 * @param passNumber for test context.
	 */
	private void verifyDeletions(final TrackingSets tracking, final int passNumber) {
		verifyCustomerSessionsPersistedState(getCustomerSessionExistsVerifier(), tracking.keptSessions, SHOULD_NOT_BE_DELETED, passNumber);
		verifyCustomerSessionsPersistedState(getCustomerSessionNotExistsVerifier(), tracking.deletedSessions, SHOULD_HAVE_BEEN_DELETED, passNumber);
		verifyShoppersPersistedState(getShopperExistsVerifier(), tracking.keptShoppers, SHOULD_NOT_BE_DELETED, passNumber);
		verifyShoppersPersistedState(getShopperNotExistsVerifier(), tracking.deletedShoppers, SHOULD_HAVE_BEEN_DELETED, passNumber);
	}

	private Set<CustomerSession> unionSessions(final Set<CustomerSession> deletedSet, final Set<CustomerSession> keptSet) {
		final Set<CustomerSession> allSet = new HashSet<CustomerSession>();
		allSet.addAll(deletedSet);
		allSet.addAll(keptSet);
		return allSet;
	}

	private Set<Shopper> unionShoppers(final Set<Shopper> deletedSet, final Set<Shopper> keptSet) {
		final Set<Shopper> allSet = new HashSet<Shopper>();
		allSet.addAll(deletedSet);
		allSet.addAll(keptSet);
		return allSet;
	}

	private void verifyCustomerSessionsPersistedState(final Verifier<CustomerSession> customerSessionVerifier,
			final Set<CustomerSession> sessionSet, final String message, final int passNumber) {
		for (CustomerSession checkSession : sessionSet) {
			assertTrue(String.format("CustomerSession (guid:%s) (email:%s) %s - Pass %d",
					checkSession.getGuid(), checkSession.getShopper().getCustomer().getEmail(), message, passNumber),
					customerSessionVerifier.verify(checkSession));
		}
	}

	private void verifyShoppersPersistedState(final Verifier<Shopper> shopperVerifier, final Set<Shopper> shopperSet,
			final String message, final int passNumber) {
		for (Shopper shopper : shopperSet) {
			assertTrue(String.format("Shopper (uid:%d) (guid:%s) %s - Pass %d",
					shopper.getUidPk(), shopper.getGuid(), message, passNumber),
					shopperVerifier.verify(shopper));
		}
	}

	private void makeScenario(final AccessAge accessAge, final CustomerType customerType, final CartType cartType,
			final String customerSessionId, final String shopperId,
			final Set<CustomerSession> sessionSet, final Set<Shopper> shopperSet) {

		final Shopper shopper = makeShopperAndCustomer(accessAge, customerType, cartType, shopperId, shopperSet);
		makeCustomerSessionAndCart(accessAge, cartType, customerSessionId, shopper, sessionSet);
	}

	private Shopper makeShopperAndCustomer(final AccessAge accessAge, final CustomerType customerType, final CartType cartType,
			final String shopperId, final Set<Shopper> shopperSet) {

		final String customerEmail = getCustomerEmail(accessAge, customerType, cartType, shopperId);
		final Customer customer = makeCustomer(customerType, customerEmail);
		final Shopper shopper = createPersistedShopper(customer, shopperId);

		// Update tracking set for later verification.
		shopperSet.add(shopper);

		return shopper;
	}

	private String getCustomerEmail(final AccessAge accessAge, final CustomerType customerType, final CartType cartType, final String suffix) {
		final List<String> emailChunks = new ArrayList<String>();
		emailChunks.add(getEmailChunk(accessAge));
		emailChunks.add(getEmailChunk(customerType));
		emailChunks.add(getEmailChunk(cartType));
		emailChunks.add(suffix);
		return buildEmail(emailChunks);
	}

	private Customer makeCustomer(final CustomerType customerType, final String customerEmail) {
		if (customerType == CustomerType.ANONYMOUS) {
			return createAnonymousCustomer(customerEmail);
		}
		return createAndSaveCustomer(customerEmail);
	}


	private Customer createAnonymousCustomer(final String email) {
		final Customer customer = getBeanFactory().getBean(ContextIdNames.CUSTOMER);
		customer.setEmail(email);
		customer.setStoreCode(getScenarioStore().getCode());
		customer.setAnonymous(true);
		return customer;
	}

	private void makeCustomerSessionAndCart(final AccessAge accessAge, final CartType cartType,
			final String customerSessionId, final Shopper shopper, final Set<CustomerSession> sessionSet) {

		final int customerAccessAge = getAccessAgeInDays(accessAge);
		final CustomerSession customerSession = createPersistedCustomerSessionWithDaysOldAndCustomer(customerSessionId,
				customerAccessAge, shopper);

		makeShoppingCart(cartType, customerSession);

		sessionSet.add(customerSession);
	}

	private CustomerSession createPersistedCustomerSessionWithDaysOldAndCustomer(final String customerSessionGuidPrefix,
			final int customerSessionDaysOld, final Shopper shopper) {
    	final CustomerSession customerSession = buildCustomerSession(shopper, customerSessionDaysOld);

    	if (customerSessionGuidPrefix != null) {
	    	customerSession.setGuid(String.format("(%s)-%s", customerSessionGuidPrefix, customerSession.getGuid()));
    	}

		customerSessionService.add(customerSession);

		return customerSession;
	}

	private CustomerSession buildCustomerSession(final Shopper shopper, final int customerSessionDaysOld) {
		final Date lastAccessedDate = convertDaysToDate(customerSessionDaysOld);
		final Date creationDate = convertDaysToDate(CUSTOMER_SESSION_CREATION_DAYS_AGO);

		final CustomerSession customerSession =
			TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSessionWithContext(shopper);
    	customerSession.setCreationDate(creationDate);
		customerSession.setLastAccessedDate(lastAccessedDate);
		customerSession.setCurrency(getScenarioStore().getDefaultCurrency());

		return customerSession;
	}

    private Shopper createPersistedShopper(final Customer customer, final String shopperGuidId) {
    	Shopper shopper;
    	if (customer.isAnonymous()) {
    		shopper = shopperService.createAndSaveShopper(getScenarioStore().getCode());
    	} else {
    		shopper = shopperService.findOrCreateShopper(customer, getScenarioStore().getCode());
    	}

    	if (shopperGuidId != null) {
	    	shopper.setGuid(String.format("(%s)-%s", shopperGuidId, shopper.getGuid()));
	    	shopperService.save(shopper);
    	}

    	// re-added as anonymous customers are not persisted and therefore are set to null after the Shopper is saved.
    	if (customer.isAnonymous()) {
    		shopper.setCustomer(customer);
    	}

    	return shopper;
    }

	private void makeShoppingCart(final CartType cartType, final CustomerSession customerSession) {
		if (cartType == CartType.NONE) {
			return;
		}

		final ShoppingCart shoppingCart = TestShoppingCartFactoryForTestApplication.getInstance().createNewCartWithMemento(
				customerSession.getShopper(), getScenario().getStore());
		shoppingCart.setCustomerSession(customerSession);
		if (cartType == CartType.NON_EMPTY) {
			addProductToShoppingCart(shoppingCart);
		}
		shoppingCartService.saveOrUpdate(shoppingCart);
	}

	private Customer createAndSaveCustomer(final String email) {
		final Customer customer = getBeanFactory().getBean(ContextIdNames.CUSTOMER);
		customer.setEmail(email);
		customer.setStoreCode(getScenarioStore().getCode());
		customer.setAnonymous(false);

		customerService.add(customer);

		return customer;
	}

	private Store getScenarioStore() {
		SimpleStoreScenario scenario = (SimpleStoreScenario) getTac().getScenario(SimpleStoreScenario.class);
		return scenario.getStore();
	}

	private void addProductToShoppingCart(final ShoppingCart shoppingCart) {
		// Add a default Product.
		ShoppingItemDto dto = new ShoppingItemDto(defaultProduct.getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, dto);
	}

    private boolean verifyCustomerSessionExists(final CustomerSession customerSession) {
		return customerSessionCleanupService.checkPersistedCustomerSessionGuidExists(customerSession.getGuid());
    }

    private boolean verifyShopperExists(final Shopper shopper) {
    	if (!shopper.isPersisted()) {
    		return false;
    	}
    	if (shopperService.get(shopper.getUidPk()) == null) {
    		return false;
    	}
		return true;
    }

    // I wish Java had delegates. =(
    private Verifier<CustomerSession> getCustomerSessionExistsVerifier() {
    	final Verifier<CustomerSession> verifier = new Verifier<CustomerSession>() {
			public boolean verify(final CustomerSession customerSession) {
				return verifyCustomerSessionExists(customerSession);
			}
		};
		return verifier;
    }

    private Verifier<CustomerSession> getCustomerSessionNotExistsVerifier() {
    	final Verifier<CustomerSession> verifier = new Verifier<CustomerSession>() {
			public boolean verify(final CustomerSession customerSession) {
				return !verifyCustomerSessionExists(customerSession);
			}
		};
		return verifier;
    }

    private Verifier<Shopper> getShopperExistsVerifier() {
    	final Verifier<Shopper> verifier = new Verifier<Shopper>() {
			public boolean verify(final Shopper shopper) {
				return verifyShopperExists(shopper);
			}
		};
		return verifier;
    }

    private Verifier<Shopper> getShopperNotExistsVerifier() {
    	final Verifier<Shopper> verifier = new Verifier<Shopper>() {
			public boolean verify(final Shopper shopper) {
				return !verifyShopperExists(shopper);
			}
		};
		return verifier;
    }

    private void overrideSetting(final String path, final String value) {
        final SettingDefinition settingDefinition = settingsService.getSettingDefinition(path);
        settingDefinition.setDefaultValue(value);
        settingsService.updateSettingDefinition(settingDefinition);
    }

    private int getAccessAgeInDays(final AccessAge age) {
    	switch(age) {
	    	case OLD:
	    		return OLD_CUSTOMER_SESSION_ACCESSED_DAYS_AGO;
	    	case RECENT:
	    		return RECENT_CUSTOMER_SESSION_ACCESSED_DAYS_AGO;
	    	default:
	    		return 0;
    	}
    }

    private String getEmailChunk(final AccessAge age) {
    	switch(age) {
	    	case OLD:
	    		return "Old";
	    	case RECENT:
	    		return "Recent";
	    	case MULTIAGED:
	    		return "Multiaged";
	    	default:
	    		return null;
    	}
    }

    private String getEmailChunk(final CartType cartType) {
    	switch(cartType) {
	    	case NONE:
	    		return "No-Shopping-Cart";
	    	case EMPTY:
	    		return "Empty-Shopping-Cart";
	    	case NON_EMPTY:
	    		return "Non-Empty-Shopping-Cart";
	    	default:
	    		return null;
    	}
    }

    private String getEmailChunk(final CustomerType customerType) {
    	switch(customerType) {
	    	case ANONYMOUS:
	    		return "Anon";
	    	case REGISTERED:
	    		return "Reg";
	    	default:
	    		return null;
    	}
    }

    private String buildEmail(final List<String> emailChunks) {
    	return String.format("%s@test.com", StringUtils.join(emailChunks, '-'));
    }

	private Date convertDaysToDate(final int days) {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		return calendar.getTime();
	}
}

