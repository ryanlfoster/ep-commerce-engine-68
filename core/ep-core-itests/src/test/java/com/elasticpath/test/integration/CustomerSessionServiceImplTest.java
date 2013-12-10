package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShopperFactoryForTestApplication;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.CustomerSessionCleanupService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.test.db.DbTestCase;

/**
 * An integration test for the CustomerSessionServiceImpl, we are testing from a client's point of view with Spring and the Database up and running.
 */
public class CustomerSessionServiceImplTest extends DbTestCase {

	private static final int NUMBER_OF_CUSTOMER_SESSIONS = 6;

	/** The main object under test. */
	@Autowired
	private CustomerSessionService customerSessionService;

	@Autowired
	private CustomerSessionCleanupService customerSessionCleanupService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private StoreService storeService;

	@Autowired
	private ShopperService shopperService;

	/**
	 * Test that we can find a customer session with just a customer ID and a StoreCode, and test that the session returned is the latest one.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCustomerIdAndStoreCode() {
		final String customerId = "testuser@elasticpath.com";

		final Customer customer = createPersistedCustomer(customerId, scenario.getStore());

		// Create a few persisted sessions
		final Calendar calendar = Calendar.getInstance();
		CustomerSession lastCustomerSession = null;
		for (int count = 0; count < NUMBER_OF_CUSTOMER_SESSIONS; count++) {
			calendar.add(Calendar.MINUTE, 1);
			lastCustomerSession = createPersistedCustomerSessionWithCustomerAndFixedGuid(customer, String.valueOf(count), calendar.getTime());
		}

		final CustomerSession returnedSession = customerSessionService.findByCustomerIdAndStoreCode(customer.getUserId(),
				customer.getStoreCode());
		assertEquals("The most recently created CustomerSession should be returned: Guid", lastCustomerSession.getGuid(), returnedSession.getGuid());
	}

	private CustomerSession createPersistedCustomerSessionWithCustomerAndFixedGuid(final Customer customer, final String guid, final Date timeStamp) {
		Store store = storeService.findStoreWithCode(customer.getStoreCode());
		final CustomerSession session = createCustomerSessionWithTimestamp(timeStamp, store);

		persistCustomerSessionWithCustomerAndFixedGuid(session, customer, guid);

		return session;
	}

	private void persistCustomerSessionWithCustomerAndFixedGuid(final CustomerSession session, final Customer customer, final String guid) {
		session.getShopper().setCustomer(customer);
		session.setGuid(guid);
		Shopper persistedShopper = shopperService.save(session.getShopper());
		session.setShopper(persistedShopper);
		customerSessionService.add(session);
	}

	private CustomerSession createCustomerSessionWithTimestamp(final Date timeStamp, final Store store) {

		final Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		shopper.setStoreCode(store.getCode());
		shopperService.save(shopper);

		final CustomerSession session = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSessionWithContext(shopper);
		session.setCreationDate(timeStamp);
		session.setCurrency(Currency.getInstance(Locale.CANADA));
		session.setLastAccessedDate(timeStamp);
		session.setLocale(Locale.US);

		return session;
	}

	private CustomerSession createCustomerSession(final Store store) {
		return createCustomerSessionWithTimestamp(Calendar.getInstance().getTime(), store);
	}

	private Customer createPersistedCustomer(final String emailAddress, final Store store) {
		final Customer customer = getBeanFactory().getBean(ContextIdNames.CUSTOMER);
		customer.setEmail(emailAddress);
		customer.setStoreCode(store.getCode());
		customer.setAnonymous(false);

		return customerService.add(customer);
	}

	/**
	 * Tests to see that {@link CustomerSessionCleanupService#checkPersistedCustomerSessionGuidExists} works.
	 */
	@DirtiesDatabase
	@Test
	public void testCheckPersistedCustomerSessionGuidExists() {

		final CustomerSession session = createCustomerSession(scenario.getStore());
		assertFalse(customerSessionCleanupService.checkPersistedCustomerSessionGuidExists(session.getGuid()));

		customerSessionService.add(session);
		assertTrue(customerSessionCleanupService.checkPersistedCustomerSessionGuidExists(session.getGuid()));
	}

	/**
	 * Tests to see if a {@link CustomerSession} can be created and then read from the database.
	 */
	@DirtiesDatabase
	@Test
	public void testBasicCreateAndReadCustomerSession() {
		final CustomerSession customerSession = createCustomerSession(scenario.getStore());
		customerSessionService.add(customerSession);

		final CustomerSession readCustomerSession = customerSessionService.findByGuid(customerSession.getGuid());
		assertEquals(customerSession.getGuid(), readCustomerSession.getGuid());
	}
}
