package com.elasticpath.commons.filter.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.filter.impl.CachingControlFilter.CachingControlEntry;


/**
 * Test <code>CachingControlFilter</code>.
 */
public class CachingControlFilterTest {


	private CachingControlFilter cachingControlFilter;

	/**
	 * Set up the test case.
	 *
	 * @throws Exception in case of failure
	 */
	@Before
	public void setUp() throws Exception {
		this.cachingControlFilter = new CachingControlFilter();
	}

	/**
	 * Test method for 'com.elasticpath.commons.filter.impl.CachingControlFilter.getLastModifiedInGMT()'.
	 */
	@Test
	public void testGetLastModifiedInGMT() {
		SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMddHHmmss" , Locale.ENGLISH);
		dateParser.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date;
		try {
			date = dateParser.parse("20061010152510");
		} catch (ParseException e) {
			throw new EpSystemException("ParseException should never happen.", e);
		}

		assertEquals("Tue, 10 Oct 2006 15:25:10 GMT", this.cachingControlFilter.getDateTimeStrInGMT(date));
	}

	/**
	 * Test method for {@link CachingControlFilter#getCacheControlValue(CachingControlEntry, HttpServletResponse)} with cookies set.
	 */
	@Test
	public void testGetCacheControlValueWithCookieSet() {
		final CachingControlEntry testCacheControlEntry = new CachingControlEntry();
		testCacheControlEntry.setMaxAge(0);
		
		final HttpServletResponse mockResponse = mockHttpServletResponseWithSetCookie(true);
		
		assertTrue("The Cache-Control header should contain 'private'", 
				this.cachingControlFilter.getCacheControlValue(testCacheControlEntry, mockResponse).contains("private"));
	}

	/**
	 * Test method for {@link CachingControlFilter#getCacheControlValue(CachingControlEntry, HttpServletResponse)} with no cookies set.
	 */
	@Test
	public void testGetCacheControlValueWithNoCookieSet() {
		final CachingControlEntry testCacheControlEntry = new CachingControlEntry();
		testCacheControlEntry.setMaxAge(0);
		
		final HttpServletResponse mockResponse = mockHttpServletResponseWithSetCookie(false);
		
		assertFalse("The Cache-Control header should not contain 'private'", 
				this.cachingControlFilter.getCacheControlValue(testCacheControlEntry, mockResponse).contains("private"));
	}

	private HttpServletResponse mockHttpServletResponseWithSetCookie(final boolean setCookie) {
		final HttpServletResponse mockResponse = new MockHttpServletResponse();
		if (setCookie) {
			mockResponse.addHeader("Set-Cookie", "anything");
		}

		return mockResponse;
	}
}
