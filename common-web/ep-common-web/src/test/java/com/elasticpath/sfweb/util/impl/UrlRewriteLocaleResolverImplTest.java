/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.WebApplicationContext;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.util.UrlUtility;
import com.elasticpath.commons.util.impl.UrlUtilityImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.StoreConfig;

/**
 * Unit test the methods in <code>UrlRewriteLocaleResolverImpl</code>.
 */
public class UrlRewriteLocaleResolverImplTest {

	private static final String LOCATION = "Location";

	private static final String DEFAULT_URI_EXPECTED = "redirected URI should be default";

	private static final String PERMANENT_REDIRECT_EXPECTED = "response should be a permanent redirect";

	private static final String DEFAULT_URI = "/storefront/category/prod1.html";

	private static final String UNEXPECTED_EXCEPTION = "Unexpected exception: ";

	private static final String SF_CONTEXT = "/storefront";

	private UrlRewriteLocaleResolverImpl urlRewriteLocaleResolver;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ServletContext servletContext;

	private StoreConfig storeConfig;

	private ServletConfig servletConfig;

	/**
	 * Setup objects required for all tests.
	 * 
	 * @throws java.lang.Exception in case of exception during setup
	 */
	@Before
	public void setUp() throws Exception {
		servletConfig = context.mock(ServletConfig.class);
		servletContext = context.mock(ServletContext.class);
		storeConfig = context.mock(StoreConfig.class);

		UrlUtility urlUtility = new UrlUtilityImpl();
		urlRewriteLocaleResolver = new UrlRewriteLocaleResolverImpl(urlUtility) {
			private static final long serialVersionUID = 4261473616132062398L;

			/**
			 * Override getServletConfig so we can get a mocked context.
			 * @return a mocked servlet config
			 */
			@Override
			public ServletConfig getServletConfig() {
				return servletConfig;
			}

			/**
			 * Override getRequestHelper so we can get a mocked helper.
			 * @return a mocked request helper
			 */
			@Override
			protected StoreConfig getStoreConfig() {
				return storeConfig;
			}
		};
	}

	/**
	 * Test that the init tries to get a request helper from the web application context.
	 */
	@Test
	public void testInitServletBean() {
		final WebApplicationContext webAppContext = context.mock(WebApplicationContext.class);
		context.checking(new Expectations() {
			{
				oneOf(servletConfig).getServletContext();
				will(returnValue(servletContext));
				
				oneOf(servletContext).getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
				will(returnValue(webAppContext));
				
				oneOf(webAppContext).getBean("threadLocalStorage");
				will(returnValue(storeConfig));
			}
		});
		
		try {
			urlRewriteLocaleResolver.initServletBean();
		} catch (ServletException e) {
			fail(UNEXPECTED_EXCEPTION + e);
		}
	}

	/**
	 * Test method for doGet() on a request with no locale specified in the URI.
	 */
	@Test
	public void testDoGetWithNoLocale() {
		prepareGetTest();
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		request.setRequestURI(DEFAULT_URI);
		request.setContextPath(SF_CONTEXT);
		
		try {
			urlRewriteLocaleResolver.doGet(request, response);
		} catch (Exception e) {
			fail(UNEXPECTED_EXCEPTION + e);
		}
		
		assertEquals("response should just be OK", HttpServletResponse.SC_OK, response.getStatus());
		assertEquals("request should have the locale attribute set to en", "en", request.getAttribute(WebConstants.URL_REQUEST_LOCALE));
		
	}

	/**
	 * Test method for doGet() on a request with default locale specified in the URI.
	 */
	@Test
	public void testDoGetWithDefaultLocale() {
		prepareGetTest();
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		request.setRequestURI("/storefront/en/category/prod1.html");
		request.setContextPath(SF_CONTEXT);
		
		try {
			urlRewriteLocaleResolver.doGet(request, response);
		} catch (Exception e) {
			fail(UNEXPECTED_EXCEPTION + e);
		}
		
		assertEquals(PERMANENT_REDIRECT_EXPECTED, HttpServletResponse.SC_MOVED_PERMANENTLY, response.getStatus());
		assertEquals(DEFAULT_URI_EXPECTED, DEFAULT_URI, response.getHeader(LOCATION));
		assertEquals("request should have the locale attribute set to en", "en", request.getAttribute(WebConstants.URL_REQUEST_LOCALE));
	
	}

	/**
	 * Test method for doGet() on a request with invalid locale specified in the URI.
	 */
	@Test
	public void testDoGetWithInvalidLocale() {
		prepareGetTest();
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		request.setRequestURI("/storefront/de/category/prod1.html");
		request.setContextPath(SF_CONTEXT);
		
		try {
			urlRewriteLocaleResolver.doGet(request, response);
		} catch (Exception e) {
			fail(UNEXPECTED_EXCEPTION + e);
		}
		
		assertEquals(PERMANENT_REDIRECT_EXPECTED, HttpServletResponse.SC_MOVED_PERMANENTLY, response.getStatus());
		assertEquals(DEFAULT_URI_EXPECTED, DEFAULT_URI, response.getHeader(LOCATION));
		assertEquals("request should have the locale attribute set to en", "en", request.getAttribute(WebConstants.URL_REQUEST_LOCALE));
		
	}

	/**
	 * Test method for doGet() on a request with valid non-default locale specified in the URI.
	 */
	@Test
	public void testDoGetWithNonDefaultLocale() {
		prepareGetTest();
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		request.setRequestURI("/storefront/fr/category/prod1.html");
		request.setContextPath(SF_CONTEXT);
		
		try {
			urlRewriteLocaleResolver.doGet(request, response);
		} catch (Exception e) {
			fail(UNEXPECTED_EXCEPTION + e);
		}
		
		assertEquals("response should just be OK", HttpServletResponse.SC_OK, response.getStatus());
		assertEquals("request should have the locale attribute set to fr", "fr", request.getAttribute(WebConstants.URL_REQUEST_LOCALE));
		
	}
	
	private void prepareGetTest() {
		final Store store = context.mock(Store.class);
		final Set<Locale> localeSet = new HashSet<Locale>();
		localeSet.add(Locale.ENGLISH);
		localeSet.add(Locale.FRENCH);
		
		context.checking(new Expectations() {
			{
				oneOf(storeConfig).getStore();
				will(returnValue(store));
				
				oneOf(store).getDefaultLocale();
				will(returnValue(Locale.ENGLISH));
				
				oneOf(store).getSupportedLocales();
				will(returnValue(localeSet));
			}
		});
	}

	/**
	 * Test that trying to get a locale from a URI with no locale returns null.
	 */
	@Test
	public void testGetLocaleFromRequestUriWithoutLocale() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		final String uri = DEFAULT_URI;
		request.setRequestURI(uri);
		request.setContextPath(SF_CONTEXT);
		
		assertNull("no locale was in the URI", urlRewriteLocaleResolver.getLocaleFromRequestUri(request));
	}

	/**
	 * Test that trying to get a locale from a URI with an invalid locale returns null.
	 */
	@Test
	public void testGetLocaleFromRequestUriWithLocale() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		request.setRequestURI("/storefront/de/category/prod1.html");
		request.setContextPath(SF_CONTEXT);
		
		assertEquals("locale should match", Locale.GERMAN, urlRewriteLocaleResolver.getLocaleFromRequestUri(request));
	}

	/**
	 * Test redirect happens to "default" URI when same locale provided as present in URI.
	 */
	@Test
	public void testRedirectForSameLocale() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		request.setRequestURI("/storefront/de/category/prod1.html");
		request.setContextPath(SF_CONTEXT);

		try {
			urlRewriteLocaleResolver.redirectForLocale(request, response, Locale.GERMAN);
		} catch (IOException e) {
			fail(UNEXPECTED_EXCEPTION + e);
		}
		assertEquals(PERMANENT_REDIRECT_EXPECTED, HttpServletResponse.SC_MOVED_PERMANENTLY, response.getStatus());
		assertEquals("redirected URI should have locale removed", DEFAULT_URI, response.getHeader(LOCATION));

	}

	/**
	 * Test redirect happens to same URI when different locale provided as present in URI.
	 */
	@Test
	public void testRedirectForDifferentLocale() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		final String uri = "/storefront/fr/category/prod1.html";
		request.setRequestURI(uri);
		request.setContextPath(SF_CONTEXT);

		try {
			urlRewriteLocaleResolver.redirectForLocale(request, response, Locale.ENGLISH);
		} catch (IOException e) {
			fail(UNEXPECTED_EXCEPTION + e);
		}
		assertEquals(PERMANENT_REDIRECT_EXPECTED, HttpServletResponse.SC_MOVED_PERMANENTLY, response.getStatus());
		assertEquals("redirected URI should have locale removed", uri, response.getHeader(LOCATION));

	}

	/**
	 * Test redirect doesn't lose any query string.
	 */
	@Test
	public void testRedirectWithQueryString() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		request.setRequestURI("/storefront/fr/category/prod1.html");
		request.setQueryString("param=test");
		request.setContextPath(SF_CONTEXT);

		try {
			urlRewriteLocaleResolver.redirectForLocale(request, response, Locale.ENGLISH);
		} catch (IOException e) {
			fail(UNEXPECTED_EXCEPTION + e);
		}
		assertEquals(PERMANENT_REDIRECT_EXPECTED, HttpServletResponse.SC_MOVED_PERMANENTLY, response.getStatus());
		assertEquals("redirected URI should have locale removed", "/storefront/fr/category/prod1.html?param=test", response.getHeader(LOCATION));
	}

	/**
	 * Test that the locale attribute is set.
	 */
	@Test
	public void testSetLocaleAttribute() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		urlRewriteLocaleResolver.setLocaleAttribute(request, Locale.ENGLISH);
		assertEquals("request should have the locale attribute set to english", "en", request.getAttribute(WebConstants.URL_REQUEST_LOCALE));
	}

	/**
	 * Test that getting a store calls the request helper to get a store from the store config.
	 */
	@Test
	public void testGetRequestStore() {
		final Store store = context.mock(Store.class);
		context.checking(new Expectations() {
			{
				oneOf(storeConfig).getStore();
				will(returnValue(store));
			}
		});
		assertSame("Returned store should be the mocked one", store, urlRewriteLocaleResolver.getRequestStore(null));
	}

}
