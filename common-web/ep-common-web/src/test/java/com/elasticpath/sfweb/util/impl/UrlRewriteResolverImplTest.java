package com.elasticpath.sfweb.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.WebApplicationContext;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.util.impl.UrlUtilityImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Test {@link UrlRewriteResolverImpl}.
 */
public class UrlRewriteResolverImplTest {

	private static final int CID_12 = 12;

	private static final int CID_16 = 16;

	private static final int CID_9 = 9;

	private static final int CID_4 = 4;

	private UrlRewriteResolverImpl urlRewriteResolver;
	private WebApplicationContext appContext;
	private SfRequestHelper requestHelper;
	private CustomerSession customerSession;
	private CustomerSessionService customerSessionService;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private static int mockCounter;


	/**
	 * Prepare for tests.
	 *
	 * @throws Exception -- in case of errors
	 */
	@Before
	public void setUp() throws Exception {
		appContext = context.mock(WebApplicationContext.class);
		setupRequestHelper();

		UrlUtilityImpl utility = new UrlUtilityImpl();
		utility.setCharacterEncoding("UTF-8");

		customerSessionService = context.mock(CustomerSessionService.class);
		this.urlRewriteResolver = new UrlRewriteResolverImpl(requestHelper, utility, customerSessionService);
	}

	private void setupRequestHelper() {
		customerSession = context.mock(CustomerSession.class);
		requestHelper = context.mock(SfRequestHelper.class);

		context.checking(new Expectations() {
			{
				allowing(requestHelper).getCustomerSession(with(any(HttpServletRequest.class)));
				will(returnValue(customerSession));

				allowing(appContext).getBean("requestHelper");
				will(returnValue(requestHelper));
			}
		});
	}

	/**
	 * Test the default decoding strategy.
	 *
	 * @throws IOException in case of errors
	 */
	@Test
	public void testResolve() throws IOException {
		// Browsing page Url1
		mockHttpRequestForUri("/cars/bwm/convertibles/c4-b5-p1.html?sort=topsellers", "c4 b5", CID_4);

		// Browsing page Url2
		mockHttpRequestForUri("/cars/bwm/convertibles/c4-b5-prUSD_100_200-p1.html", "c4 b5 prUSD_100_200", CID_4);

		// Browsing page Url3
		mockHttpRequestForUri("/cars/bwm/convertibles/c4-b5-prUSD_0_200-p1.html", "c4 b5 prUSD_0_200", CID_4);

		mockHttpRequestForUri("/cars/bwm/convertibles/c4-b5-prUSD_200_max-p1.html", "c4 b5 prUSD_200_max", CID_4);

		// Browsing page Url with locale
		mockHttpRequestForUri("/ep5/fr_CA/bwm/convertibles/c4-b5-prUSD_200_max-p1.html", "c4 b5 prUSD_200_max", CID_4);

		mockHttpUriWithPid("/cars/bwm/convertibles/prod356.html", "356");

		// Product Url
		mockHttpUriWithPid("/cars/bwm/convertibles/prodXXXX.html", "XXXX");

		mockHttpRequestForUri("/cars/bwm/convertibles/");

		// Unrelated url2
		mockHttpRequestForUri("/cars/bwm/convertibles/abc.ep");
	}

	/**
	 *  populates the mockHttpRequest expectations.
	 * @param uri the uri to use.
	 */
	private void mockHttpRequestForUri(final String uri) throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.name(), uri);
		MockHttpServletResponse response = new MockHttpServletResponse();

		urlRewriteResolver.resolve(request, response);
		assertEquals("Invalid URIs should produce an error", HttpStatus.NOT_FOUND.value(), response.getStatus());
	}

	/**
	 *  populates the mockHttpRequest expectations.
	 * @param uri the uri to use.
	 * @param pid the pid to use.
	 */
	private void mockHttpUriWithPid(final String uri, final String pid) {
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.name(), uri);

		urlRewriteResolver.resolve(request, null);
		assertEquals("Missing request product id", pid, request.getAttribute(WebConstants.REQUEST_PID));
	}

	/**
	 * Populates the mockHttpRequest expectations.
	 * @param uri the uri to use.
	 * @param requestFilter the request string.
	 * @param cid the CID.
	 */
	private void mockHttpRequestForUri(final String uri, final String requestFilter, final Integer cid) {
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.name(), uri);

		urlRewriteResolver.resolve(request, null);
		assertEquals("Missing request caregory id", String.valueOf(cid), request.getAttribute(WebConstants.REQUEST_CID));
		assertEquals("Filters weren't set", requestFilter, request.getAttribute(WebConstants.REQUEST_FILTERS));
		assertNotNull("Page should always be set", request.getAttribute(WebConstants.REQUEST_PAGE_NUM));
	}

	
	/**
	 * Test url decoding with non-default field separator.
	 *
	 * @throws IOException in case of errors
	 */
	@Test
	public void testResolveWithNonDefaultFieldSeparator() throws IOException {
		// Browsing page Url1
		initResolverWithFieldSeparator(urlRewriteResolver, ":");
		
		
		mockHttpRequestForUri("/cars/bwm/convertibles/c4:b5:p1.html?sort=topsellers", "c4 b5", CID_4);

		// Browsing page Url2
		initResolverWithFieldSeparator(urlRewriteResolver, "+++++");   // Regex style separator - make sure this is handled correctly.
		mockHttpRequestForUri("/cars/bwm/convertibles/c4+++++b5+++++prUSD_100_200+++++p1.html", "c4 b5 prUSD_100_200", CID_4);

		// Browsing page Url3
		initResolverWithFieldSeparator(urlRewriteResolver, "!");
		mockHttpRequestForUri("/cars/bwm/convertibles/c4!b5!prUSD_0_200!p1.html", "c4 b5 prUSD_0_200", CID_4);

		// Browsing page Url4
		initResolverWithFieldSeparator(urlRewriteResolver, ",");
		mockHttpRequestForUri("/cars/bwm/convertibles/c9,b5,prUSD_200_max,p1.html", "c9 b5 prUSD_200_max", CID_9);

		// Browsing page Url with locale
		initResolverWithFieldSeparator(urlRewriteResolver, "$^.");   // Regex style separator - make sure this is handled correctly.
		mockHttpRequestForUri("/ep5/fr_CA/bwm/convertibles/c4$^.b5$^.prUSD_200_max$^.p1.html", "c4 b5 prUSD_200_max", CID_4);

		// Product Url
		initResolverWithFieldSeparator(urlRewriteResolver, "~");
		mockHttpUriWithPid("/cars/bwm/convertibles/prod356.html", "356");

		// Product Url
		initResolverWithFieldSeparator(urlRewriteResolver, "|");
		mockHttpUriWithPid("/cars/bwm/convertibles/prodXXXX.html", "XXXX");

		// Unrelated url1
		initResolverWithFieldSeparator(urlRewriteResolver, ",");
		mockHttpRequestForUri("/cars/bwm/convertibles/");

		// Unrelated url2
		
		initResolverWithFieldSeparator(urlRewriteResolver, ";");
		mockHttpRequestForUri("/cars/bwm/convertibles/abc.ep");
	}	
	
	/**
	 * Make sure the field separator cannot be set to something invalid, it
	 * should default back to the default of '-'.
	 */
	@Test
	public void testInitWithInvalidFieldSeparators() {
		initResolverWithFieldSeparator(urlRewriteResolver, null);
		mockHttpRequestForUri("/cars/bwm/convertibles/c16-b5-p1.html?sort=topsellers", "c16 b5", CID_16);

		initResolverWithFieldSeparator(urlRewriteResolver, "");
		mockHttpRequestForUri("/cars/bwm/convertibles/c12-b5-p1.html?sort=topsellers", "c12 b5", CID_12);
	}

	private void initResolverWithFieldSeparator(final UrlRewriteResolverImpl rewriter, final String fieldSeparator) {
		final ServletConfig config = context.mock(ServletConfig.class, String.format("ServletConfig-%d", ++mockCounter));
		context.checking(new Expectations() {
			{
				allowing(config).getInitParameter(with(any(String.class)));
				will(returnValue(fieldSeparator));

				allowing(config);
			}
		});

		rewriter.init(config);
	}
	
	/**
	 * Tests a locale change. The default store locale is German and in the request 
	 * we receive locale Locale Canada we need to change the locale of the shopping cart
	 * to German as the only one supported locale.
	 */
	@Test
	public void testProcessPossibleLocaleChange() {
		final Locale supportedLocale = Locale.GERMAN;
		final Locale requestedLocale = Locale.CANADA;
		final Locale shoppingCartLocale = Locale.ITALIAN;
		final MockHttpServletRequest request = new MockHttpServletRequest();

		context.checking(new Expectations() {
			{
				oneOf(customerSession).setLocale(supportedLocale);
				oneOf(customerSessionService).update(customerSession);
			}
		});

		performLocaleChangeTest(request, null, requestedLocale, shoppingCartLocale, supportedLocale);
	}

	/**
	 * Tests that in case the supported locae is the same as the requested and the
	 * shopping cart locales, no change is performed onto the shopping cart. 
	 */
	@Test
	public void testProcessPossibleLocaleChangeWhenNoChangeRequired() {
		final Locale supportedLocale = Locale.GERMAN;
		final Locale requestedLocale = Locale.GERMAN;
		final Locale shoppingCartLocale = Locale.GERMAN;

		performLocaleChangeTest(new MockHttpServletRequest(), null, requestedLocale, shoppingCartLocale, supportedLocale);
	}

	/**
	 * Tests that in case the supported locae is the same as the requested and the
	 * shopping cart locales, no change is performed onto the shopping cart. 
	 */
	@Test
	public void testProcessPossibleLocaleChangeWhenChangeToRequestedLocale() {
		final Locale[] supportedLocale = { Locale.GERMAN, Locale.ITALIAN };
		final Locale requestedLocale = Locale.ITALIAN;
		final Locale shoppingCartLocale = Locale.GERMAN;
		final MockHttpServletRequest request = new MockHttpServletRequest();

		context.checking(new Expectations() {
			{
				oneOf(customerSession).setLocale(requestedLocale);
				oneOf(customerSessionService).update(customerSession);
			}
		});
		
		performLocaleChangeTest(request, null, requestedLocale, shoppingCartLocale, supportedLocale);
	}

	private void performLocaleChangeTest(final HttpServletRequest request, final HttpServletResponse response,
			final Locale requestedLocale, final Locale shoppingCartLocale, final Locale... supportedLocale) {
		context.checking(new Expectations() {
			{
				StoreConfig config = context.mock(StoreConfig.class);
				allowing(requestHelper).getStoreConfig();
				will(returnValue(config));

				allowing(customerSession).getLocale();
				will(returnValue(shoppingCartLocale));

				Store store = context.mock(Store.class);
				allowing(store).getSupportedLocales();
				will(returnValue(Arrays.asList(supportedLocale)));
				allowing(store).getDefaultLocale();
				will(returnValue(supportedLocale[0]));

				allowing(config).getStore();
				will(returnValue(store));
			}
		});

		request.setAttribute(WebConstants.URL_REQUEST_LOCALE, requestedLocale.toString());

		urlRewriteResolver.processPossibleLocaleChange(request, response);
	}
}
