/**
 * 
 */
package com.elasticpath.sfweb.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.elasticpath.common.pricing.service.PriceListLookupService;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.pricing.impl.PriceListStackImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestResponseFacade;
import com.elasticpath.sfweb.servlet.facade.impl.HttpServletFacadeFactoryImpl;
import com.elasticpath.sfweb.servlet.listeners.CustomerLoginEventListener;
import com.elasticpath.sfweb.servlet.listeners.NewHttpSessionEventListener;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.sfweb.util.impl.DefaultCookieHandlerImpl;
import com.elasticpath.sfweb.util.impl.IpAddressResolverImpl;
import com.elasticpath.sfweb.util.impl.RequestHelperImpl;
import com.elasticpath.tags.TagSet;

/**
 * Tests for WebCustomerSessionServiceImpl.
 */
public class WebCustomerSessionServiceImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Test that when a customer signs in, any registered {@link CustomerLoginEventListener}s are notified.
	 */
	@Test
	public void testCustomerLoginEventsNotified() {
		final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		final MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		final RequestHelperImpl requestHelper = new RequestHelperImpl();
		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(requestHelper, null, null);
		final HttpServletRequestResponseFacade requestResponse = httpServletFacadeFactory.createRequestResponseFacade(mockRequest, mockResponse);
		final Customer customer = context.mock(Customer.class);
		final CustomerSession session = context.mock(CustomerSession.class);
		final CustomerLoginEventListener listener = context.mock(CustomerLoginEventListener.class);
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Shopper shopper = context.mock(Shopper.class);
		final ShopperService shopperService = context.mock(ShopperService.class);
		final StoreConfig storeConfig = context.mock(StoreConfig.class);
		
		
		context.checking(new Expectations() {
			{
				allowing(session).getShopper(); will(returnValue(shopper));
				allowing(shopper).setCustomer(with(any(Customer.class)));
				allowing(session).setSignedIn(with(any(Boolean.class)));
				allowing(customer).getPreferredLocale();
				will(returnValue(null));
                allowing(session).getShoppingCart(); 
                will(returnValue(shoppingCart));
                allowing(shoppingCart).setIpAddress(with(any(String.class)));
                allowing(session).getShopper(); will(returnValue(shopper));
                
				// EXPECTATIONS (TEST THAT THE LISTENER'S EXECUTE METHOD IS CALLED)
				oneOf(listener).execute(session, requestResponse);
				
				allowing(session).getShopper(); will(returnValue(shopper));
				allowing(shopper).setCustomer(with(any(Customer.class)));
				allowing(shopper).setSignedIn(with(any(Boolean.class)));
				allowing(shopperService).save(with(shopper)); will(returnValue(shopper));
				allowing(storeConfig).getStore(); will(returnValue(new StoreImpl()));
				allowing(session).getLocale(); will(returnValue(Locale.CANADA));
				allowing(session).isCheckoutSignIn(); will(returnValue(false));
			}
		});
		
		requestHelper.setStoreConfig(storeConfig);
        requestResponse.setCustomerSession(session);
		
		WebCustomerSessionServiceImpl service = new WebCustomerSessionServiceImpl() {
			@Override
			public CustomerSession setupCustomerSessionInRequest(final HttpServletRequestResponseFacade requestResponse) {
				return session;
			}

			@Override
			public void signInCustomer(final HttpServletRequestResponseFacade requestResponse, 
					final CustomerSession customerSession, final Locale locale) {
				//No-op
			}

			@Override
			protected Collection<CustomerLoginEventListener> getCustomerLoginEventListeners() {
				return Arrays.asList(listener);
			}

		};
		service.setShopperService(shopperService);
		
		service.handleCustomerSignIn(requestResponse, customer);
	}

	/**
	 * Test, that new customer session has invalid price list stack and retrieve it via priceListLookupService.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNewHttpSessionHasInvalidPriceListStack() {
		final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		final MockHttpServletResponse mockResponse = new MockHttpServletResponse();

		final SfRequestHelper mockRequestHelper = context.mock(SfRequestHelper.class);
		final StoreConfig mockStoreConfig = context.mock(StoreConfig.class);
		final ShoppingCart mockShoppingCart = context.mock(ShoppingCart.class);

		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(mockRequestHelper, null, null);
		final HttpServletRequestResponseFacade requestResponse = httpServletFacadeFactory.createRequestResponseFacade(mockRequest, mockResponse);

		final CustomerSession session = context.mock(CustomerSession.class);
		final NewHttpSessionEventListener listener = context.mock(NewHttpSessionEventListener.class);
		final PriceListLookupService priceListLookupService = context.mock(PriceListLookupService.class);
		final Customer customer = context.mock(Customer.class);
		final Store store = context.mock(Store.class);
		final Catalog catalog = context.mock(Catalog.class);
		final Currency currency = Currency.getInstance("CAD");
		final TagSet tagSet = new TagSet();
		final PriceListStackImpl priceListStack = new PriceListStackImpl();

		context.checking(new Expectations() {
			{
				allowing(mockRequestHelper).setCustomerSession(mockRequest, session);
				allowing(mockRequestHelper).getCustomerSession(mockRequest);
				will(returnValue(session));
				allowing(session).getShopper().getCustomer();
				will(returnValue(customer));
				oneOf(listener).execute(session, requestResponse); // expectation that the listener will be called
				allowing(session).isPriceListStackValid();
				will(returnValue(false));
				allowing(mockRequestHelper).getStoreConfig();
				will(returnValue(mockStoreConfig));
				allowing(mockStoreConfig).getStore();
				will(returnValue(store));
				allowing(store).getCatalog();
				will(returnValue(catalog));
				allowing(catalog).getCode();
				will(returnValue("catalogCode"));
				allowing(session).getCurrency();
				will(returnValue(currency));
				allowing(session).getCustomerTagSet();
				will(returnValue(tagSet));
				allowing(priceListLookupService).getPriceListStack("catalogCode", currency, tagSet);
				will(returnValue(priceListStack));
				allowing(session).setPriceListStack(priceListStack);
				allowing(session).setShoppingCart(mockShoppingCart);
				allowing(mockShoppingCart).setCustomerSession(session);
			}
		});

		requestResponse.setCustomerSession(session);

		WebCustomerSessionServiceImpl service = new WebCustomerSessionServiceImpl();

		List<NewHttpSessionEventListener> newSessionListeners = new ArrayList<NewHttpSessionEventListener>();
		newSessionListeners.add(listener);

		service.setNewHttpSessionEventListeners(newSessionListeners);
		service.setPriceListLookupService(priceListLookupService);

		service.handleFilterRequest(requestResponse);
	}

	/**
	 * Test that {@link NewHttpSessionEventListener}s are notified when an incoming HTTP request has a new HTTP session.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testStartCustomerSessionEvent() {
		final Map<String, String> tagMap = new HashMap<String, String>();
		final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		final MockHttpServletResponse mockResponse = new MockHttpServletResponse();

		final SfRequestHelper mockRequestHelper = context.mock(SfRequestHelper.class);
		final ShoppingCart mockShoppingCart = context.mock(ShoppingCart.class);
		final StoreConfig mockStoreConfig = context.mock(StoreConfig.class);
		final Store store = context.mock(Store.class);
		final Customer customer = context.mock(Customer.class);

		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(mockRequestHelper, null, null);
		final HttpServletRequestResponseFacade requestResponse = httpServletFacadeFactory.createRequestResponseFacade(mockRequest, mockResponse);

		final CustomerSession session = context.mock(CustomerSession.class);
		final NewHttpSessionEventListener listener = context.mock(NewHttpSessionEventListener.class);

		context.checking(new Expectations() {
			{
				allowing(mockRequestHelper).getCustomerSession(mockRequest);
				will(returnValue(session));
				allowing(mockRequestHelper).getStoreConfig();
				will(returnValue(mockStoreConfig));
				allowing(mockStoreConfig).getStore();
				will(returnValue(store));
				will(returnValue(mockShoppingCart));
				allowing(session).setShoppingCart(mockShoppingCart);
				allowing(mockShoppingCart).setCustomerSession(session);
				allowing(session).getShopper().getCustomer();
				will(returnValue(customer));

				allowing(session).getCustomerTagSet();
				will(returnValue(tagMap));
				allowing(session).isPriceListStackValid();
				will(returnValue(true));
				oneOf(listener).execute(session, requestResponse); // expectation that the listener will be called
			}
		});

		WebCustomerSessionServiceImpl service = new WebCustomerSessionServiceImpl();
		List<NewHttpSessionEventListener> newSessionListeners = new ArrayList<NewHttpSessionEventListener>();
		newSessionListeners.add(listener);

		service.setNewHttpSessionEventListeners(newSessionListeners);
		service.handleFilterRequest(requestResponse);
	}

	/**
	 * Test that a request is deemed to start a new session if the session is new.
	 */
	@Test
	public void testRequestStartsNewSessionTrue() {
		final HttpServletRequest mockRequest = context.mock(HttpServletRequest.class);
		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(null, null, null);
		final HttpServletRequestFacade request = httpServletFacadeFactory.createRequestFacade(mockRequest);
		final MockHttpSession session = new MockHttpSession();
		session.setNew(true);
		context.checking(new Expectations() {
			{
				allowing(mockRequest).getSession();
				will(returnValue(session));
			}
		});
		assertTrue(request.isNewSession());
	}

	/**
	 * Test that a request is deemed to NOT start a new session if the session is NOT new.
	 */
	@Test
	public void testRequestStartsNewSessionFalse() {
		final HttpServletRequest mockRequest = context.mock(HttpServletRequest.class);
		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(null, null, null);
		final HttpServletRequestFacade request = httpServletFacadeFactory.createRequestFacade(mockRequest);
		final MockHttpSession session = new MockHttpSession();
		session.setNew(false);
		context.checking(new Expectations() {
			{
				allowing(mockRequest).getSession();
				will(returnValue(session));
			}
		});
		assertFalse(request.isNewSession());
	}

	/**
	 * Test that when we ensure that a ShoppingCart has a CustomerSession we also ensure that the CustomerSession has a Customer. This is due to a
	 * bug uncovered with TCH-924. It seems that when a user comes in we try to get the CustomerSession from the ShoppingCart in their HttpRequest,
	 * and if we find one then all of our subsequent code assumes that it has a Customer. The problem is that when we persist the CustomerSession we
	 * don't persist the Customer along with it if the Customer is not a registered Customer - we save the CustomerSession with no Customer data for
	 * non-existent customers. However, if a CustomerSession is not found in the Request then one is loaded from the persistence layer (and assumed
	 * to have been saved with a Customer).
	 */
	@Test
	public void testSetupCustomerSessionInRequestEnsuresCustomerWhenCreatingNewCustomerSession() {
		final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		final MockHttpServletResponse mockResponse = new MockHttpServletResponse();

		final SfRequestHelper mockRequestHelper = context.mock(SfRequestHelper.class);
		final StoreConfig mockStoreConfig = context.mock(StoreConfig.class);

		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(mockRequestHelper, new IpAddressResolverImpl(),
				new DefaultCookieHandlerImpl());
		final HttpServletRequestResponseFacade requestResponse = httpServletFacadeFactory.createRequestResponseFacade(mockRequest, mockResponse);

		final CustomerSession mockCustomerSession = context.mock(CustomerSession.class);
		final Customer customer = context.mock(Customer.class);
		final Store mockStore = context.mock(Store.class);
		
		mockRequest.setServletPath("");

		context.checking(new Expectations() {
			{
				oneOf(mockRequestHelper).getCustomerSession(mockRequest);
				will(returnValue(null));
				
				oneOf(mockRequestHelper).getPersistedCustomerSession(mockRequest);
				will(returnValue(null));
				
				allowing(mockRequestHelper).getStoreConfig();
				will(returnValue(mockStoreConfig));
				
				allowing(mockStoreConfig).getStore();
				will(returnValue(mockStore));
				
				allowing(mockRequestHelper).setCustomerSession(mockRequest, mockCustomerSession);

				allowing(mockCustomerSession).getLocale();
				will(returnValue(null));
				
				oneOf(mockStore).getDefaultCurrency();
				will(returnValue(Currency.getInstance("USD")));
				
				oneOf(mockStore).getDefaultLocale();
				will(returnValue(Locale.CANADA));
				
				allowing(mockCustomerSession).setLocale(Locale.CANADA);
				
				oneOf(mockCustomerSession).getShopper().setCustomer(customer); // test that it's set
			}
		});

		WebCustomerSessionServiceImpl service = new WebCustomerSessionServiceImpl() {
			@Override
			protected CustomerSession createPersistentCustomerSession(final String guid, final Locale locale, final Store store, 
					final String ipAddress) {
				return mockCustomerSession;
			}

			@Override
			protected Customer createEmptyCustomer(final Locale locale, final Currency currency, final Store store) {
				return customer;
			}
			
			@Override
			protected Locale getLocaleFromServletPath(final HttpServletRequestFacade request) {
				return null;
			}
			
			@Override
			protected void attachShoppingCartToNewCustomerSession(final HttpServletRequestResponseFacade requestResponse, 
					final CustomerSession customerSession) {
				// DO NOTHING.
			}
		};

		service.setupCustomerSessionInRequest(requestResponse);
		// test was performed with expectations.

	}

	/**
	 * Test that given a request url with a locale element a proper locale is retrieved.
	 */
	@Test
	public void testRetrieveLocaleFromRequestURL() {
		final MockHttpServletRequest request = new MockHttpServletRequest();

		final SfRequestHelper mockRequestHelper = context.mock(SfRequestHelper.class);
		final StoreConfig mockStoreConfig = context.mock(StoreConfig.class);

		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(mockRequestHelper, null, null);
		final HttpServletRequestFacade requestFacade = httpServletFacadeFactory.createRequestFacade(request);

		request.setServletPath("/en_US/foo");
		final Locale locale = new Locale("en", "US");
		final Locale locale2 = new Locale("en", "CA");
		WebCustomerSessionServiceImpl service = new WebCustomerSessionServiceImpl();

		context.checking(new Expectations() {
			{
				allowing(mockRequestHelper).getCustomerSession(request);
				will(returnValue(null));
				allowing(mockRequestHelper).getStoreConfig();
				will(returnValue(mockStoreConfig));
				allowing(mockStoreConfig).getStore();
				will(returnValue(new StoreImpl() {
					private static final long serialVersionUID = -2894340147292452342L;

					@Override
					public Set<Locale> getSupportedLocales() {
						return new HashSet<Locale>(Arrays.asList(locale, locale2));
					}
				}));
			}
		});
		
		assertEquals(locale, service.getLocaleFromServletPath(requestFacade));
	}

	/**
	 * Test that given a request url with no locale element returns a store supported locale.
	 */
	@Test
	public void testRetrieveLocaleFromRequestURLgetDefault() {
		final MockHttpServletRequest request = new MockHttpServletRequest();

		final SfRequestHelper mockRequestHelper = context.mock(SfRequestHelper.class);
		final StoreConfig mockStoreConfig = context.mock(StoreConfig.class);

		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(mockRequestHelper, null, null);
		final HttpServletRequestFacade requestFacade = httpServletFacadeFactory.createRequestFacade(request); 

		request.setServletPath("/foo");
		final Locale locale = new Locale("en", "US");
		final Locale localeDefault = new Locale("en", "PH");
		WebCustomerSessionServiceImpl service = new WebCustomerSessionServiceImpl();

		context.checking(new Expectations() {
			{
				allowing(mockRequestHelper).getCustomerSession(request);
				will(returnValue(null));
				allowing(mockRequestHelper).getStoreConfig();
				will(returnValue(mockStoreConfig));
				allowing(mockStoreConfig).getStore();
				will(returnValue(new StoreImpl() {
					private static final long serialVersionUID = 2320305348903734755L;

					@Override
					public Set<Locale> getSupportedLocales() {
						return Collections.singleton(locale);
					}

					@Override
					public Locale getDefaultLocale() {
						return localeDefault;
					}
				}));
			}
		});

		assertEquals(localeDefault, service.getLocaleFromServletPath(requestFacade));
	}
}
