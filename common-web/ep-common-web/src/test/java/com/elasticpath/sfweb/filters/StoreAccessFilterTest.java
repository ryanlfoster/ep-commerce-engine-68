/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.servlet.ServletException;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.elasticpath.sfweb.security.AccessChecker;

/**
 * Test the methods of <code>StoreAccessFilter</code> behave as expected.
 */
public class StoreAccessFilterTest {

	private StoreAccessFilter filter;

	private MockHttpServletRequest request;
	
	private MockHttpServletResponse response;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private MockFilterChain filterChain;

	private AccessChecker accessChecker;
	
	/**
	 * Set up objects required for all tests.
	 * 
	 * @throws java.lang.Exception in case of an error setting up the objects.
	 */
	@Before
	public void setUp() throws Exception {
		// Dependencies
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filterChain = new MockFilterChain();
		accessChecker = context.mock(AccessChecker.class);	
		
		// The object to be tested
		filter = new StoreAccessFilter();
		filter.setAccessChecker(accessChecker);
	}

	/**
	 * Test the the filter when the store is accessible.
	 */
	@Test
	public void testDoFilterWhenStoreAccessible() {
		
		context.checking(new Expectations() {
			{
				oneOf(accessChecker).isAccessible(request);
				will(returnValue(true));
			}
		});
		
		try {
			filter.doFilter(request, response, filterChain);
		} catch (IOException e) {
			fail("Unexpected I/O error: " + e);
		} catch (ServletException e) {
			fail("Unexpected servlet exception: " + e);
		}
		
		assertEquals("filter chain request should be our request", request, filterChain.getRequest());
		assertEquals("filter chain response should be our response", response, filterChain.getResponse());
	}

	/**
	 * Test the the filter when the store is not accessible.
	 */
	@Test
	public void testDoFilterWhenStoreNotAccessible() {
		final String redirectedView = "/not-accessible-view.ep";
		filter.setStoreNotAccessibleView(redirectedView);
		filter.setStorefrontContextUrl("");
		context.checking(new Expectations() {
			{
				oneOf(accessChecker).isAccessible(request);
				will(returnValue(false));
			}
		});
		
		try {
			filter.doFilter(request, response, filterChain);
		} catch (IOException e) {
			fail("Unexpected I/O error: " + e);
		} catch (ServletException e) {
			fail("Unexpected servlet exception: " + e);
		}
		assertNull("filter chain should not have been called", filterChain.getRequest());
		assertEquals("response should contain our redirection", redirectedView, response.getRedirectedUrl());
	}
	
	/**
	 * Test the redirecting when the store is not accessible.
	 */
	@Test
	public void testStoreNotAccessible() {
		filter.setStorefrontContextUrl("");
		final String redirectedView = "/not-accessible-view.ep";
		filter.setStoreNotAccessibleView(redirectedView);
		try {
			filter.storeNotAccessible(request, response);
		} catch (IOException e) {
			fail("Unexpected I/O error: " + e);
		}
		assertEquals("response should contain our redirection", redirectedView, response.getRedirectedUrl());
	}
	
	/**
	 * Test that the redirect URI is the context plus the view.
	 */
	@Test
	public void testGetRedirectUri() {
		filter.setStorefrontContextUrl("context");
		
		final String redirectedView = "not-accessible-view.ep";
		filter.setStoreNotAccessibleView(redirectedView);
		String uri = filter.getRedirectUri(request);
		assertEquals("redirect should be formed from the context and redirect view", "context/not-accessible-view.ep", uri);
	}
	
}
