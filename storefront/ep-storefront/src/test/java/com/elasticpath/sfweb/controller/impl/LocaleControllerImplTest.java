package com.elasticpath.sfweb.controller.impl;

import java.util.Collections;
import java.util.Currency;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.sfweb.util.SfRequestHelper;

/** Test case for {@link LocaleControllerImpl}. */
public class LocaleControllerImplTest {

	private static final Locale LOCALE = Locale.ITALY;
	private static final Currency CURRENCY = Currency.getInstance(LOCALE);
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private LocaleControllerImpl controller;
	private CustomerSession customerSession;
	private SfRequestHelper requestHelper;
	private CustomerSessionService customerSessionService;

	/** Test initialization. */
	@Before
	public void setUp() {
		controller = new LocaleControllerImpl();

		requestHelper = context.mock(SfRequestHelper.class);
		customerSession = context.mock(CustomerSession.class);
		customerSessionService = context.mock(CustomerSessionService.class);

		controller.setRequestHelper(requestHelper);
		controller.setCustomerSessionService(customerSessionService);

		context.checking(new Expectations() {
			{
				StoreConfig storeConfig = context.mock(StoreConfig.class);
				Store store = context.mock(Store.class);

				allowing(requestHelper).getStoreConfig();
				will(returnValue(storeConfig));
				allowing(requestHelper).getCustomerSession(with(any(HttpServletRequest.class)));
				will(returnValue(customerSession));

				allowing(storeConfig).getStore();
				will(returnValue(store));

				allowing(store).getSupportedLocales();
				will(returnValue(Collections.singleton(LOCALE)));
				allowing(store).getDefaultLocale();
				will(returnValue(LOCALE));
				allowing(store).getSupportedCurrencies();
				will(returnValue(Collections.singleton(CURRENCY)));
				allowing(store).getDefaultCurrency();
				will(returnValue(CURRENCY));
			}
		});
	}

	/**
	 * When the locale is changed via this controller, then it should be persisted to the customer session.
	 *
	 * @throws Exception in case of errors
	 */
	@Test
	public void testLocaleChangeIsPersisted() throws Exception {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		final MockHttpServletResponse response = new MockHttpServletResponse();
		request.setMethod("GET");
		request.setParameter("locale", LOCALE.toString());
		request.setParameter("currency", CURRENCY.toString());

		context.checking(new Expectations() {
			{
				Shopper shopper = context.mock(Shopper.class);
				ShoppingCart shoppingCart = context.mock(ShoppingCart.class);

				oneOf(customerSession).setLocale(LOCALE);
				oneOf(customerSession).setCurrency(CURRENCY);
				oneOf(customerSessionService).update(customerSession);

				allowing(customerSession).getShopper();
				will(returnValue(shopper));
				allowing(shopper).getCurrentShoppingCart();
				will(returnValue(shoppingCart));

				// since the currency changed, then rules may have changed also
				oneOf(shoppingCart).fireRules();
			}
		});

		controller.handleRequest(request, response);
	}
}
