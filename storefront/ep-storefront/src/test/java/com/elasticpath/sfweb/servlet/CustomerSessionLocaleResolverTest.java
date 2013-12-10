package com.elasticpath.sfweb.servlet;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Test class for {@link CustomerSessionLocaleResolver}.
 */
public class CustomerSessionLocaleResolverTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CustomerSessionLocaleResolver localeResolver;

	private SfRequestHelper requestHelper;

	private MockHttpServletRequest mockHttpRequest;

	@Before
	public void setUp() throws Exception {
		mockHttpRequest = new MockHttpServletRequest();
		requestHelper = context.mock(SfRequestHelper.class);

		localeResolver = new CustomerSessionLocaleResolver();
		localeResolver.setRequestHelper(requestHelper);
	}

	/**
	 * Verifies an IllegalStateException is thrown when no Customer Session exists on the session.
	 */
	@Test(expected = IllegalStateException.class)
	public void testResolveLocaleWithNoCustomerSessionOnSession() throws Exception {
		context.checking(new Expectations() {
			{
				atLeast(1).of(requestHelper).getCustomerSession(mockHttpRequest);
				will(returnValue(null));
			}
		});

		localeResolver.resolveLocale(mockHttpRequest);
	}

	/**
	 * Verifies an IllegalStateException is thrown when the Customer Session does not contain a locale.
	 */
	@Test(expected = IllegalStateException.class)
	public void testResolveLocaleWithNoLocaleOnCustomerSession() throws Exception {
		final CustomerSession customerSession = context.mock(CustomerSession.class);
		context.checking(new Expectations() {
			{
				atLeast(1).of(requestHelper).getCustomerSession(mockHttpRequest);
				will(returnValue(customerSession));

				atLeast(1).of(customerSession).getLocale();
				will(returnValue(null));
			}
		});

		localeResolver.resolveLocale(mockHttpRequest);
	}

	/**
	 * Verifies the locale resolver retrieves the locale from the Customer Session.
	 */
	@Test
	public void testResolveLocale() throws Exception {
		final Locale locale = Locale.CANADA;
		final CustomerSession customerSession = context.mock(CustomerSession.class);
		context.checking(new Expectations() {
			{
				atLeast(1).of(requestHelper).getCustomerSession(mockHttpRequest);
				will(returnValue(customerSession));

				atLeast(1).of(customerSession).getLocale();
				will(returnValue(locale));
			}
		});

		assertEquals("Unexpected Locale returned from LocaleResolver", locale, localeResolver.resolveLocale(mockHttpRequest));
	}

	/**
	 * Verifies the CustomerSessionLocaleResolver#setLocale(HttpServletRequest, HttpServletResponse, Locale)} method throws
	 * an UnsupportedOperationException.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testSetLocale() throws Exception {
		localeResolver.setLocale(mockHttpRequest, new MockHttpServletResponse(), Locale.CANADA);
	}

}
