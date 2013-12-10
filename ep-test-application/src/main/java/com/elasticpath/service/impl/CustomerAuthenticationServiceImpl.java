package com.elasticpath.service.impl;

import java.util.Date;
import java.util.UUID;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.testcontext.ShoppingTestData;
import com.elasticpath.service.CustomerAuthenticationService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.test.common.exception.TestApplicationException;

/**
 * <code>CustomerAuthenticationServiceImpl</code> provides services for managing <code>CustomerSession</code>s.
 */
public class CustomerAuthenticationServiceImpl implements CustomerAuthenticationService {

	private ElasticPath elasticPath;

	private CustomerSessionService customerSessionService;

	private ShoppingCartService shoppingCartService;

	private ShopperService shopperService;

	private CustomerService customerService;

	private Utility utility;

	@Override
	public void loginStore(final Store store, final String email) {
		loginStoreInternal(store, email, true);
	}

	@Override
	public void loginSecondarySessionToStore(final Store store, final String email) {
		loginStoreInternal(store, email, false);
	}

	@Override
	public void loginStore(final Store store, final String email, final CustomerSession oldCustomerSession) {
		if (oldCustomerSession == null) {
			throw new TestApplicationException("oldCustomerSession cannot be null");
		}

		loginStoreInternal(store, email, oldCustomerSession);
	}

	@Override
	public void guestLoginStore(final Store store, final String email) {
		guestLoginStoreInternal(store, email, ShoppingTestData.getInstance().getCustomerSession());
	}

	@Override
	public void guestLoginStoreAsSecondarySession(final Store store, final String email) {
		guestLoginStoreInternal(store, email, ShoppingTestData.getInstance().getSecondaryCustomerSession());
	}

	@Override
	public void createAnonymousCustomerSession(final Store store) {
		final Customer anonymousCustomer = createAnonymousCustomer(store);
		createAndAddAnonymousCustomerSession(store, obtainCustomerSessionGuid(), anonymousCustomer);
	}

	@Override
	public void createAnonymousCustomerSessionAsSecondarySession(final Store store) {
		final Customer anonymousCustomer = createAnonymousCustomer(store);
		createAndAddAnonymousCustomerSession(store, obtainCustomerSessionGuid(), anonymousCustomer, false);
	}

	public void createGuestCustomerSession(final Store store, final Customer customer) {
		createAndAddAnonymousCustomerSession(store, obtainCustomerSessionGuid(), customer);
	}

	public void createGuestCustomerSessionAsSecondarySession(final Store store, final Customer customer) {
		createAndAddAnonymousCustomerSession(store, obtainCustomerSessionGuid(), customer, false);
	}

	// -----------------------------------------------------------------------------------------------------------------------

	private void guestLoginStoreInternal(final Store store, final String email, final CustomerSession oldCustomerSession) {
		validate(store, email);

		final Customer customer = createAndSaveGuestCustomer(store, email);
		handleSignIn(store, customer, oldCustomerSession);
	}

	private void loginStoreInternal(final Store store, final String email, final boolean primaryCustomerSession) {
		validate(store, email);

		final Customer customer = findCustomer(store, email);
		if (customer == null) {
			throw new TestApplicationException("Couldn't log into store " + store.getCode() + " with email address " + email);
		}

		handleSignIn(store, customer, primaryCustomerSession);
	}

	private void loginStoreInternal(final Store store, final String email, final CustomerSession oldCustomerSession) {
		validate(store, email);

		final Customer customer = findCustomer(store, email);
		if (customer == null) {
			throw new TestApplicationException("Couldn't log into store " + store.getCode() + " with email address " + email);
		}

		handleSignIn(store, customer, oldCustomerSession);
	}

	private void handleSignIn(final Store store, final Customer customer, final boolean primaryCustomerSession) {
		handleCustomerSignIn(store, customer, primaryCustomerSession);
		ShoppingTestData.getInstance().setStore(store);
	}

	private void handleSignIn(final Store store, final Customer customer, final CustomerSession oldCustomerSession) {
		handleCustomerSignIn(store.getCode(), customer, oldCustomerSession);
		ShoppingTestData.getInstance().setStore(store);
	}

	private void validate(final Store store, final String email) {
		if (store == null) {
			throw new TestApplicationException("Store cannot be null");
		}

		if (email == null) {
			throw new TestApplicationException("Email cannot be null");
		}
	}

	private Customer findCustomer(final Store store, final String email) {
		final Customer customer = customerService.findByEmail(email, store.getCode(), true);
		if (customer != null) {
			customer.setPreferredLocale(store.getDefaultLocale());
		}
		return customer;
	}

	private Customer createAndSaveGuestCustomer(final Store store, final String email) {
		// In elastic path, not only a customer, but also a non-customer can
		// browse the catalog
		// and put items into the shopping cart.
		// During the shopping process, a non-customer might be asked to
		// input some personal data,
		// so we create an empty customer in the customer session to hold
		// this personal data.
		// The uid of the empty customer is 0, which means it's not
		// persistent.
		// It could be persisted at another point of the check-out process.
		final Customer customer = elasticPath.getBean(ContextIdNames.CUSTOMER);
		customer.setPreferredLocale(store.getDefaultLocale());
		customer.setPreferredCurrency(store.getDefaultCurrency());
		customer.setStoreCode(store.getCode());
		customer.setEmail(email);
		customer.setAnonymous(true);
		return customerService.add(customer);
	}

	private void createAndAddAnonymousCustomerSession(final Store store, final String customerSessionGuid, final Customer customer) {
		createAndAddAnonymousCustomerSession(store, customerSessionGuid, customer, true);
	}

	private void createAndAddAnonymousCustomerSession(
			final Store store, final String customerSessionGuid, final Customer customer, final boolean primaryCustomerSession) {
		if (customer == null) {
			//don't persist the customer unless you talk with Ivan, Mike, Edison or Matt (or all).
			throw new IllegalArgumentException("Customer cannot be null. If your customer is null, consider to use createAnonymousSession.");
		}

		final Shopper shopper = shopperService.findOrCreateShopper(customer, store.getCode());

		//setting the anonymous customer to the shopper
		//it get ripped of from the shopper service
		shopper.setCustomer(customer);

		final CustomerSession customerSession = customerSessionService.createWithShopper(shopper);
		customerSession.setCreationDate(new Date());
		customerSession.setLastAccessedDate(new Date());
		customerSession.setLocale(store.getDefaultLocale());
		customerSession.setCurrency(store.getDefaultCurrency());
		customerSession.setGuid(customerSessionGuid);

		customerSessionService.add(customerSession);
		createOrLoadShoppingCart(customerSession, store, customer);

		shopperService.save(shopper);

		if (primaryCustomerSession) {
			ShoppingTestData.getInstance().setCustomerSession(customerSession);
		} else {
			ShoppingTestData.getInstance().setSecondaryCustomerSession(customerSession);
		}
		ShoppingTestData.getInstance().setStore(store);
	}

	/**
	 * Handle cases of anonymous customers without an email.
	 *
	 * @param store the store
	 * @return an anonymous customer
	 */
	private Customer createAnonymousCustomer(final Store store) {
		// In elastic path, not only a customer, but also a non-customer can
		// browse the catalog
		// and put items into the shopping cart.
		// During the shopping process, a non-customer might be asked to
		// input some personal data,
		// so we create an empty customer in the customer session to hold
		// this personal data.
		// The uid of the empty customer is 0, which means it's not
		// persistent.
		// It could be persisted at another point of the check-out process.
		final Customer customer = elasticPath.getBean(ContextIdNames.CUSTOMER);
		customer.setPreferredLocale(store.getDefaultLocale());
		customer.setPreferredCurrency(store.getDefaultCurrency());
		customer.setStoreCode(store.getCode());
		customer.setAnonymous(true);
		return customer;
	}

	private void handleCustomerSignIn(final Store store, final Customer customer, final boolean primaryCustomerSession) {
		createAndAddAnonymousCustomerSession(store, obtainCustomerSessionGuid(), customer, primaryCustomerSession);
		if (primaryCustomerSession) {
			handleCustomerSignIn(store.getCode(), customer, ShoppingTestData.getInstance().getCustomerSession());
		} else {
			handleCustomerSignIn(store.getCode(), customer, ShoppingTestData.getInstance().getSecondaryCustomerSession());
		}
		ShoppingTestData.getInstance().setStore(store);
	}

	private void handleCustomerSignIn(final String storeCode, final Customer customer, final CustomerSession customerSession) {
		customerSession.getShopper().setCustomer(customer);
		customerSession.setSignedIn(true);

		customerSessionService.handleShopperChangeAndUpdate(customerSession, storeCode);
		customerSession.setPriceListStackValid(false);
	}

	private void createOrLoadShoppingCart(final CustomerSession customerSession, final Store store) {
		ShoppingCart shoppingCart = getShoppingCartService().findOrCreateByShopper(customerSession.getShopper());
		customerSession.setShoppingCart(shoppingCart);
		shoppingCart.setCustomerSession(customerSession);
		shoppingCart.setStore(store);
		shoppingCart.setCurrency(store.getDefaultCurrency());

		shoppingCart = getShoppingCartService().saveIfNotPersisted(shoppingCart);
		customerSession.getShopper().setCurrentShoppingCart(shoppingCart);
	}

	private ShoppingCart createOrLoadShoppingCart(final CustomerSession customerSession, final Store store, final Customer customer) {
		createOrLoadShoppingCart(customerSession, store);
		final ShoppingCart shoppingCart = customerSession.getShopper().getCurrentShoppingCart();
		shoppingCart.setShopper(customerSession.getShopper());
		if (customer != null) {
			shoppingCart.setBillingAddress(customer.getPreferredBillingAddress());
			shoppingCart.setShippingAddress(customer.getPreferredShippingAddress());
		}

		return shoppingCart;
	}

	/**
	 * Attempts to write a cookie.
	 *
	 * @param inResponse the servlet response
	 * @return The guid value of the cookie or null if the cookie could not be written
	 */
	private String obtainCustomerSessionGuid() {
		return UUID.randomUUID().toString();
	}

	@Override
	public void setCustomerSessionService(final CustomerSessionService customerSessionService) {
		this.customerSessionService = customerSessionService;
	}

	@Override
	public void setUtility(final Utility utility) {
		this.utility = utility;
	}

	@Override
	public Utility getUtility() {
		return utility;
	}

	@Override
	public void setElasticPath(final ElasticPath elasticPath) {
		this.elasticPath = elasticPath;
	}

	@Override
	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	protected ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}

	/**
	 * @param customerService the customerService to set
	 */
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	@Override
	public void setShopperService(final ShopperService shopperService) {
		this.shopperService = shopperService;
	}

}
