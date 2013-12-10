package com.elasticpath.sfweb.service.impl;

import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.pricing.service.PriceListLookupService;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.sfweb.service.WebCustomerSessionService;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestResponseFacade;
import com.elasticpath.sfweb.servlet.facade.HttpServletResponseFacade;
import com.elasticpath.sfweb.servlet.listeners.CustomerLoginEventListener;
import com.elasticpath.sfweb.servlet.listeners.NewHttpSessionEventListener;

/**
 * {@code WebCustomerSessionService} provides services for managing {@code CustomerSession}s in the web application session, including cookie
 * management.
 */
public class WebCustomerSessionServiceImpl implements WebCustomerSessionService {

	private final Set<NewHttpSessionEventListener> newHttpSessionEventListeners = new LinkedHashSet<NewHttpSessionEventListener>();

	private final Set<CustomerLoginEventListener> customerLoginEventListeners = new LinkedHashSet<CustomerLoginEventListener>();

	private final Set<CustomerLoginEventListener> anonymousCustomerLoginEventListeners = new LinkedHashSet<CustomerLoginEventListener>();

	private BeanFactory beanFactory;

	private CustomerSessionService customerSessionService;

	private ShoppingCartService shoppingCartService;

	private StoreProductService storeProductService;

	private ShopperService shopperService;

	private TimeService timeService;

	private PriceListLookupService priceListLookupService;

	/**
	 * Handle a request intercepted by a filter. <br>
	 * Performs any required actions to update the CustomerSession, shopping cart, and cookie.
	 * @param request the request
	 *
	 * @return the customer session object
	 */
	public CustomerSession handleFilterRequest(final HttpServletRequestResponseFacade request) {
		final CustomerSession customerSession = setupCustomerSessionInRequest(request);
		if (request.isNewSession() || customerTagSetIsEmpty(customerSession)) {
			notifyStartNewHttpSessionEventListeners(request);
		}

		if (!customerSession.isPriceListStackValid()) {
			final Store store = request.getStore();
			retrievePriceListStack(customerSession, store.getCatalog());
		}

		return customerSession;
	}

	/**
	 * Retrieve price list stack for new session or if tag set changed.
	 *
	 * @param customerSession the customer session.
	 * @param catalog the catalog the price list stack is for
	 */
	void retrievePriceListStack(final CustomerSession customerSession, final Catalog catalog) {
		final PriceListStack priceListStack = priceListLookupService.getPriceListStack(catalog.getCode(),
				customerSession.getCurrency(), customerSession.getCustomerTagSet());

		customerSession.setPriceListStack(priceListStack);
	}

	/**
	 * Checks whether the tag set on a customer session is empty.
	 *
	 * @param customerSession the customer session containing a tag set
	 * @return true if the tag set it empty (there are no tags), false if not.
	 */
	protected boolean customerTagSetIsEmpty(final CustomerSession customerSession) {
		return customerSession.getCustomerTagSet().isEmpty();
	}

	/**
	 * Ensures that the customer's shopping cart contains a CustomerSession. <br>
	 * Tries to retrieve the CustomerSession from the Request's ShoppingCart. <br>
	 * If that doesn't work then it tries to find a persisted CustomerSession given the request's CustomerSessionGuid and attaches that to the
	 * ShoppingCart. <br>
	 * If that still doesn't work then it creates a new CustomerSession and attaches that to the ShoppingCart in the HttpSession.
	 *
	 * @param requestResponse the request
	 * @return the ShoppingCart's CustomerSession
	 */
	protected CustomerSession setupCustomerSessionInRequest(final HttpServletRequestResponseFacade requestResponse) {
		CustomerSession customerSession = requestResponse.getCustomerSession();

		// If no CustomerSession available, create one from information on the request and update the session shopping cart.
		if (customerSession == null) {
			customerSession = findOrCreateCustomerSession(requestResponse);

			requestResponse.setCustomerSession(customerSession);
			updateSessionLocale(requestResponse, customerSession, null);

			attachShoppingCartToNewCustomerSession(requestResponse, customerSession);
		}

		return customerSession;
	}

	@Override
	public void handleCustomerSignIn(final HttpServletRequestResponseFacade requestResponse, final Customer customer) {
		CustomerSession customerSession = requestResponse.getCustomerSession();

		//The if statement below was created to fix MOJITO-551.
		//The fix was created to handle session timeout while the user is in the login page
		//As per Dave B. comments, ideally we could re-arrange the filters so the customer session were never null at this point
		//However, since this would be a risk task at this point (regression testings for 6.3.1), this fix
		//is enough.
		if (customerSession == null) {
			customerSession = setupCustomerSessionInRequest(requestResponse);
		}

		putRegisteredCustomerOnCustomerSession(customer, customerSession);
		signInCustomer(requestResponse, customerSession, getLocaleForSignIn(customerSession, customer));
		notifyCustomerLoginEventListeners(customerSession, requestResponse);
		requestResponse.setCustomerSession(customerSession);
	}

	@Override
	public void handleGuestSignIn(final HttpServletRequestResponseFacade requestResponse, final Customer customer) {
		final CustomerSession customerSession = requestResponse.getCustomerSession();

		setAnonymousCustomerOnCustomerSession(customer, customerSession);
		signInCustomer(requestResponse, customerSession, getLocaleForSignIn(customerSession, customer));
		notifyAnonymousCustomerLoginEventListeners(customerSession, requestResponse);
	}

	private Locale getLocaleForSignIn(final CustomerSession customerSession, final Customer customer) {
		if (customerSession != null && customerSession.isCheckoutSignIn() && customerSession.getLocale() != null) {
			return customerSession.getLocale();
		}
		return customer.getPreferredLocale();
	}

	private void putRegisteredCustomerOnCustomerSession(final Customer customer, final CustomerSession customerSession) {
		customerSession.setSignedIn(true);

		final Shopper shopper = customerSession.getShopper();
		shopper.setCustomer(customer);
		shopper.setSignedIn(true);
	}

	private void setAnonymousCustomerOnCustomerSession(final Customer customer, final CustomerSession customerSession) {
		final Shopper shopper = customerSession.getShopper();
		shopper.setCustomer(customer);
	}

	/**
	 * Signs in a customer by writing a cookie, setting their lastAccessedDate, setting their session's locale, saving the CustomerSession,
	 * updating the session's shopping cart with the persisted Session.
	 *
	 * @param requestResponse the http request
	 * @param customerSession the customer's customer session
	 * @param customerLocale the signed in customer's locale.
	 */
	protected void signInCustomer(final HttpServletRequestResponseFacade requestResponse, final CustomerSession customerSession,
			final Locale customerLocale) {

		updateSessionLocale(requestResponse, customerSession, customerLocale);

		final Store store = requestResponse.getStore();
		customerSessionService.handleShopperChangeAndUpdate(customerSession, store.getCode());

		updateShoppingCart(customerSession.getShopper().getCurrentShoppingCart());
	}

	private void updateSessionLocale(final HttpServletRequestResponseFacade requestResponse, final CustomerSession customerSession,
			final Locale customerLocale) {
		if (customerLocale != null) {
			customerSession.setLocale(customerLocale);
		}
		
		// make sure that the shoppingCart Locale is in sync with the templates locale
		customerSession.setLocale(getLocaleMatchFromStore(requestResponse.getStore(), customerSession.getLocale()));
	}

	/**
	 * Finds a shopping cart to attach to the current customer session.
	 *
	 * @param requestResponse the request
	 * @param customerSession the CustomerSession
	 */
	@SuppressWarnings("deprecation")
	protected void attachShoppingCartToNewCustomerSession(final HttpServletRequestResponseFacade requestResponse,
			final CustomerSession customerSession) {

		final Shopper shopper = customerSession.getShopper();

		final ShoppingCart shoppingCart = shoppingCartService.findOrCreateByShopper(shopper);
		shoppingCart.setStore(requestResponse.getStore());
		shoppingCart.setIpAddress(requestResponse.getRemoteAddress());

		// Assigning shopping carts to locations that need it.
		shopper.setCurrentShoppingCart(shoppingCart);

		// FIXME: Remove once shoppingCart.getCustomerSession() has been removed.
		shoppingCart.setCustomerSession(customerSession);
	}

	/**
	 * Finds the {@link CustomerSession} for the customer submitting the request.<br>
	 * If no customer session can be found with the information in the request, then a new customer session will be created and returned.
	 *
	 * @param requestResponse the request
	 * @return the found or the new customer session
	 */
	CustomerSession findOrCreateCustomerSession(final HttpServletRequestResponseFacade requestResponse) {
		CustomerSession customerSession = requestResponse.getPersistedCustomerSession();

		if (customerSession == null) {
			customerSession = createAndPersistNewCustomerSession(requestResponse);

		} else {
			// Make sure expected entities are populated.
			populateCustomer(requestResponse, customerSession);
		}
		return customerSession;
	}

	private void populateCustomer(final HttpServletRequestResponseFacade requestResponse, final CustomerSession customerSession) {
		// FIXME: Customer should not be null if it is required.
		// Why do we need to populate the customer at this point, shouldn't this already be handled at some point previously?
		if (customerSession.getShopper().getCustomer() == null) {
			final Store store = requestResponse.getStore();
			final Locale locale = getLocaleFromServletPath(requestResponse);
			createEmptyCustomerOnCustomerSession(customerSession, store, locale);
		}
	}

	/**
	 * Clears estimates and fires the {@code ShoppingCart}'s rules, and updates the {@code ShoppingCart} if the number of items is > 0.
	 *
	 * @param shoppingCart the customer's CustomerSession
	 */
	private void updateShoppingCart(final ShoppingCart shoppingCart) {
		// clear and reset shopping cart values
		shoppingCart.clearEstimates();
		shoppingCart.fireRules();
		if (shoppingCart.getNumItems() > 0) {
			shoppingCartService.saveOrUpdate(shoppingCart);
		}
	}

	/**
	 * Attempts to write a the customer session guid to a cookie.
	 *
	 * @param response the servlet response facade
	 * @param customerSessionGuid the customer session guid to write as a cookie.
	 */
	private void writeCustomerSessionGuidToCookie(final HttpServletResponseFacade response, final String customerSessionGuid) {
		// TODO: Perhaps the responseFacade should know how to write the customerSessionGUID cookie explicitly.
		response.writeCookie(WebConstants.CUSTOMER_SESSION_GUID, customerSessionGuid);
	}

	/**
	 * Generates a new GUID.
	 *
	 * @return returns a new GUID.
	 */
	private String getNewGuid() {
		return UUID.randomUUID().toString();
	}

	private void createEmptyCustomerOnCustomerSession(final CustomerSession customerSession, final Store store, final Locale locale) {
		final Customer emptyCustomer = createEmptyCustomer(locale, store.getDefaultCurrency(), store);
		setAnonymousCustomerOnCustomerSession(emptyCustomer, customerSession);
	}

	/**
	 * Creates a customer session and persists the session to the system.<br>
	 * If there are any {@link TagProvider}s set then their tags will be added to the session during creation.
	 *
	 * @param requestResponse the HttpServletRequestFacade
	 * @return the added customer session
	 */
	protected CustomerSession createAndPersistNewCustomerSession(final HttpServletRequestResponseFacade requestResponse) {
		final String newGuid = getNewGuid();
		writeCustomerSessionGuidToCookie(requestResponse, newGuid);

		final Locale locale = getLocaleFromServletPath(requestResponse);
		final Store store = requestResponse.getStore();
		final CustomerSession customerSession = createPersistentCustomerSession(newGuid, locale, store, requestResponse.getRemoteAddress());
		createEmptyCustomerOnCustomerSession(customerSession, store, locale);

		return customerSession;
	}

	/**
	 * Gets the locale from the servlet path.
	 *
	 * @param request the request facade.
	 * @return the locale.
	 */
	protected Locale getLocaleFromServletPath(final HttpServletRequestFacade request) {
		final Store store = request.getStore();
		final String localeString = getLocaleStringFromServletPath(request);
		return getLocaleMatchFromStore(store, localeString);
	}

	/**
	 * Sends new session event to listeners.
	 *
	 * @param customerSession the session
	 * @param request the {@link HttpServletRequestFacade}
	 */
	private void notifyStartNewHttpSessionEventListeners(final HttpServletRequestFacade request) {
		final Collection<NewHttpSessionEventListener> newHttpSessionListeners = getNewHttpSessionEventListeners();
		final CustomerSession customerSession = request.getCustomerSession();

		for (final NewHttpSessionEventListener listener : newHttpSessionListeners) {
			listener.execute(customerSession, request);
		}
	}

	/**
	 * Sends events to CustomerLoginEventListeners.
	 *
	 * @param customerSession the customer session
	 * @param request the {@link HttpServletRequestFacade}
	 */
	protected void notifyCustomerLoginEventListeners(final CustomerSession customerSession, final HttpServletRequestFacade request) {
		for (final CustomerLoginEventListener listener : getCustomerLoginEventListeners()) {
			listener.execute(customerSession, request);
		}
	}

	/**
	 * Sends events to {@link CustomerLoginEventListeners}.
	 *
	 * @param customerSession the customer session
	 * @param request the {@link HttpServletRequestFacade}
	 */
	protected void notifyAnonymousCustomerLoginEventListeners(final CustomerSession customerSession, final HttpServletRequestFacade request) {
		for (final CustomerLoginEventListener listener : getAnonymousCustomerLoginEventListeners()) {
			listener.execute(customerSession, request);
		}
	}

	/**
	 * Gets the registered {@link CustomerLoginEventListeners}.
	 *
	 * @return a collection of {@link CustomerLoginEventListeners}.
	 */
	protected Collection<CustomerLoginEventListener> getAnonymousCustomerLoginEventListeners() {
		return anonymousCustomerLoginEventListeners;
	}

	/**
	 * Sets the list of listeners to {@link CustomerLoginEventListeners}.
	 *
	 * @param listeners the listeners to set
	 */
	public void setAnonymousCustomerLoginEventListeners(final List<CustomerLoginEventListener> listeners) {
		anonymousCustomerLoginEventListeners.addAll(listeners);
	}

	/**
	 * Creates a Customer object.
	 * <bad>Package protected for testing purposes</bad>.
	 *
	 * @param locale the customer's locale
	 * @param currency the customer's currency
	 * @param store the customer's current Store
	 * @return the created Customer
	 */
	protected Customer createEmptyCustomer(final Locale locale, final Currency currency, final Store store) {
		// In elastic path, not only a customer, but also a non-customer can browse the catalog
		// and put items into the shopping cart.
		// During the shopping process, a non-customer might be asked to input some personal data,
		// so we create an empty customer in the customer session to hold this personal data.
		// The uid of the empty customer is 0, which means it's not persistent.
		// It could be persisted at another point of the check-out process.
		final Customer customer = getBeanFactory().getBean(ContextIdNames.CUSTOMER);
		customer.setPreferredLocale(locale);
		customer.setPreferredCurrency(currency);
		customer.setStoreCode(store.getCode());
		return customer;
	}

	/**
	 * Creates a persistent CustomerSession object.
	 *
	 * @param guid the session's GUID
	 * @param locale the session locale
	 * @param store the session store
	 * @param ipAddress the session's originating Ip Address
	 * @return the created CustomerSession
	 */
	protected CustomerSession createPersistentCustomerSession(final String guid, final Locale locale, final Store store, final String ipAddress) {

		final Shopper shopper = shopperService.createAndSaveShopper(store.getCode());
		final CustomerSession customerSession = customerSessionService.createWithShopper(shopper);
		final Date currentTime = timeService.getCurrentTime();

		customerSession.setCreationDate(currentTime);
		customerSession.setLastAccessedDate(currentTime);
		customerSession.setLocale(locale);
		customerSession.setCurrency(store.getDefaultCurrency());
		customerSession.setGuid(guid);
		customerSession.setIpAddress(ipAddress);

		customerSessionService.add(customerSession);

		return customerSession;
	}

	/**
	 * Returns a matching local from the store, or the default store locale.
	 *
	 * @param store the request object
	 * @param localeString the locale string
	 * @return the locale specified in the request, or the default locale
	 */
	protected Locale getLocaleMatchFromStore(final Store store, final String localeString) {
		if (StringUtils.isNotBlank(localeString)) {
			final Locale match = matchAgainstSupportedLocale(localeString, store.getSupportedLocales());
			if (match != null) {
				return match;
			}
		}
		return store.getDefaultLocale();
	}

	/**
	 * Returns a matching local from the store, or the default store locale.
	 *
	 * @param store the request object
	 * @param locale the locale
	 * @return the locale specified in the request if the store supports that locale, or the store default locale
	 */
	protected Locale getLocaleMatchFromStore(final Store store, final Locale locale) {
		if (locale != null && store.getSupportedLocales().contains(locale)) {
			return getLocaleMatchFromStore(store, locale.toString());
		}

		return store.getDefaultLocale();
	}

	private String getLocaleStringFromServletPath(final HttpServletRequestFacade request) {
		final String servletPath = request.getServletPath();
		if ("".equals(servletPath)) {
		    return "";
		}
		return servletPath.split("/")[1];
	}

	/**
	 * Tries to find a supported locale by matching a locale string against a collection of supported locales. This can be customized to find
	 * nearest locale instead of exact match. e.g. locale string = en_US, supported locales = {en, fr}; will return en
	 *
	 * @param localeString the locale string key
	 * @param locales the list of locales to match in
	 * @return Locale if there is a match, otherwise null
	 */
	protected Locale matchAgainstSupportedLocale(final String localeString, final Collection<Locale> locales) {
		for (final Locale locale : locales) {
			if (locale.toString().equals(localeString)) {
				return locale;
			}
		}
		return null;
	}

	/**
	 * Updates the session and cookie when a new account is created. Save the existing session tags and attach to newly created session. Notify login
	 * listeners as signing up involves signing in.
	 *
	 * @param requestResponse the HTTP Request
	 * @param customer the new customer
	 */
	public void handleCreateNewAccount(final HttpServletRequestResponseFacade requestResponse, final Customer customer) {
		handleCustomerSignIn(requestResponse, customer);
	}

	/**
	 * Set the customer session service.
	 *
	 * @param customerSessionService the customer session service to set.
	 */
	public void setCustomerSessionService(final CustomerSessionService customerSessionService) {
		this.customerSessionService = customerSessionService;
	}

	/**
	 * @return the customer session service
	 */
	protected CustomerSessionService getCustomerSessionService() {
		return customerSessionService;
	}

	/**
	 * Sets the beanFactory.
	 *
	 * @param beanFactory the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return beanFactory instance in use
	 */
	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * Set the shopping cart service.
	 *
	 * @param shoppingCartService the shopping cart service to set.
	 */
	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	/**
	 * Sets the store product service.
	 *
	 * @param storeProductService the product service
	 */
	public void setStoreProductService(final StoreProductService storeProductService) {
		this.storeProductService = storeProductService;
	}

	/**
	 * @return the listeners of start customer session event
	 */
	protected Collection<NewHttpSessionEventListener> getNewHttpSessionEventListeners() {
		return newHttpSessionEventListeners;
	}

	/**
	 * Set the list of listeners of a start customer session event.
	 *
	 * @param listeners list of NewCustomerSessionEventListener
	 */
	public void setNewHttpSessionEventListeners(final List<NewHttpSessionEventListener> listeners) {
		newHttpSessionEventListeners.addAll(listeners);
	}

	/**
	 * @return the list of CustomerLoginEvent Listeners
	 */
	protected Collection<CustomerLoginEventListener> getCustomerLoginEventListeners() {
		return customerLoginEventListeners;
	}

	/**
	 * Sets the list of listeners to Customer Login Events.
	 *
	 * @param listeners the listeners to set
	 */
	public void setCustomerLoginEventListeners(final List<CustomerLoginEventListener> listeners) {
		customerLoginEventListeners.addAll(listeners);
	}

	@Override
	public void addNewHttpSessionEventListener(final NewHttpSessionEventListener newHttpSessionEventListener) {
		newHttpSessionEventListeners.add(newHttpSessionEventListener);
	}

	@Override
	public boolean removeNewHttpSessionEventListener(final NewHttpSessionEventListener newHttpSessionEventListener) {
		return newHttpSessionEventListeners.remove(newHttpSessionEventListener);
	}

	/**
	 * Gets the time service.
	 *
	 * @return the time service
	 */
	protected TimeService getTimeService() {
		return timeService;
	}

	/**
	 * Sets the time service.
	 *
	 * @param timeService the time service instance
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * Gets the shopping cart service.
	 *
	 * @return the shopping cart service
	 */
	protected ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}

	/**
	 * Gets the store product service.
	 *
	 * @return the store product service
	 */
	protected StoreProductService getStoreProductService() {
		return storeProductService;
	}

	/**
	 * Set the {@link PriceListLookupService}.
	 *
	 * @param priceListLookupService instance to set.
	 */
	public void setPriceListLookupService(final PriceListLookupService priceListLookupService) {
		this.priceListLookupService = priceListLookupService;
	}

	/**
	 * Gets the ShopperService.
	 *
	 * @return a ShopperService.
	 */
	public ShopperService getShopperService() {
		return shopperService;
	}

	/**
	 * Sets the ShopperService.
	 *
	 * @param shopperService ShopperService to set.
	 */
	public void setShopperService(final ShopperService shopperService) {
		this.shopperService = shopperService;
	}

}
