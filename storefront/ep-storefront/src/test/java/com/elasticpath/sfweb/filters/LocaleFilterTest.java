package com.elasticpath.sfweb.filters;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;

import com.elasticpath.commons.constants.WebConstants;

/**
 * Test class for {@link com.elasticpath.sfweb.filters.LocaleFilter}.
 */
public class LocaleFilterTest {

	private LocaleFilter filter;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final LocaleResolver localeResolver = context.mock(LocaleResolver.class);
	private final MockHttpServletRequest request = new MockHttpServletRequest();
	private final MockHttpServletResponse response = new MockHttpServletResponse();
	private final FilterChain chain = context.mock(FilterChain.class);

	@Before
	public void setUp() {
		filter = new LocaleFilter();
		filter.setLocaleResolver(localeResolver);
	}

	@Test
	public void verifyDoFilterAddsSpringLocaleToRequest() throws Exception {
		final Locale expected = Locale.CANADA;

		context.checking(new Expectations() {
			{
				oneOf(localeResolver).resolveLocale(request);
				will(returnValue(expected));

				oneOf(chain).doFilter(request, response);
			}
		});

		filter.doFilter(request, response, chain);

		final Locale actual = (Locale) request.getAttribute(WebConstants.LOCALE_PARAMETER_NAME);
		assertEquals("Did not get expected locale from HttpServletRequest", expected, actual);
	}

	@Test
	public void verifyDoFilterWithNonHttpServletRequestContinuesChain() throws Exception {
		final ServletRequest nonHttpRequest = context.mock(ServletRequest.class);

		context.checking(new Expectations() {
			{
				oneOf(chain).doFilter(nonHttpRequest, response);
			}
		});

		filter.doFilter(nonHttpRequest, response, chain);
	}

}