/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.util.impl;


import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.elasticpath.sfweb.util.StoreResolver;


/**
 * Test the caching store resolver works as expected.
 */
public class CachingStoreResolverImplTest {

	private static final String SAME_CODE_EXPECTED_ON_SECOND_CALL = "The second call to CachingStoreResolver should return the same store code";

	private static final String SAME_CODE_AS_DELEGATE_EXPECTED = "The same code returned by the delegate should be returned" 
		+ " by the CachingStoreResolver";

	private static final String STORE_CODE = "storeCode";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CachingStoreResolverImpl cachingResolver;

	private StoreResolver delegate;

	private MockHttpServletRequest request;
	
	private MockHttpSession session;

	/**
	 * Set up objects required for all tests.
	 * 
	 * @throws java.lang.Exception in case of an error setting up the objects.
	 */
	@Before
	public void setUp() throws Exception {
		delegate = context.mock(StoreResolver.class);
		cachingResolver = new CachingStoreResolverImpl();
		cachingResolver.setDelegate(delegate);
		request = new MockHttpServletRequest();
		session = new MockHttpSession();
	}

	/**
	 * Test that the store code is resolved and cached by <code>resolveDomainHeader</code>.
	 */
	@Test
	public void testResolveDomainHeaderIsCached() {
		final String host = "HOST";

		request.addHeader(host, "http://www.store1.com");
		context.checking(new Expectations() {
			{
				oneOf(delegate).resolveDomainHeader(request, host);
				will(returnValue("store1"));
			}
		});
		assertEquals(SAME_CODE_AS_DELEGATE_EXPECTED, "store1", cachingResolver.resolveDomainHeader(request, host));
		assertEquals(SAME_CODE_EXPECTED_ON_SECOND_CALL,	"store1", cachingResolver.resolveDomainHeader(request, host));
	}

	/**
	 * Test that the store code is resolved and cached by <code>resolveDomainParam</code>.
	 */
	@Test
	public void testResolveDomainParamIsCached() {
		final String domain = "domain";

		request.addParameter(domain, "http://www.store2.com");
		context.checking(new Expectations() {
			{
				oneOf(delegate).resolveDomainParam(request, domain);
				will(returnValue("store2"));
			}
		});
		assertEquals(SAME_CODE_AS_DELEGATE_EXPECTED, "store2", cachingResolver.resolveDomainParam(request, domain));
		assertEquals(SAME_CODE_EXPECTED_ON_SECOND_CALL,	"store2", cachingResolver.resolveDomainParam(request, domain));
	}
	
	/**
	 * Test that the store code is resolved and cached by <code>resolveStoreCodeParam</code>.
	 */
	@Test
	public void testResolveStoreCodeParamIsCached() {
		final String storeCode = "store3";
		request.addParameter(STORE_CODE, storeCode);
		session.setAttribute(STORE_CODE, storeCode);
		request.setSession(session);
		context.checking(new Expectations() {
			{
				oneOf(delegate).resolveStoreCodeParam(request, STORE_CODE);
				will(returnValue(storeCode));
			}
		});
		assertEquals(SAME_CODE_AS_DELEGATE_EXPECTED, storeCode, cachingResolver.resolveStoreCodeParam(request, STORE_CODE));
		assertEquals(SAME_CODE_EXPECTED_ON_SECOND_CALL,	storeCode, cachingResolver.resolveStoreCodeParam(request, STORE_CODE));
	}

	/**
	 * Test that the store code is resolved and cached by <code>resolveStoreCodeHeader</code>.
	 */
	@Test
	public void testResolveStoreCodeHeaderIsCached() {
		final String storeCode = "store4";
		request.addHeader(STORE_CODE, storeCode);
		context.checking(new Expectations() {
			{
				oneOf(delegate).resolveStoreCodeHeader(request, STORE_CODE);
				will(returnValue(storeCode));
			}
		});
		assertEquals(SAME_CODE_AS_DELEGATE_EXPECTED, storeCode, cachingResolver.resolveStoreCodeHeader(request, STORE_CODE));
		assertEquals(SAME_CODE_EXPECTED_ON_SECOND_CALL,	storeCode, cachingResolver.resolveStoreCodeHeader(request, STORE_CODE));
	}

	/**
	 * Test that the store code is resolved and cached by <code>resolveStoreCodeHeader</code>.
	 */
	@Test
	public void testResolveStoreCodeSessionIsCached() {
		final String storeCode = "store5";
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(STORE_CODE, storeCode);
		request.setSession(session);
		context.checking(new Expectations() {
			{
				oneOf(delegate).resolveStoreCodeSession(request, STORE_CODE);
				will(returnValue(storeCode));
			}
		});
		assertEquals(SAME_CODE_AS_DELEGATE_EXPECTED, storeCode, cachingResolver.resolveStoreCodeSession(request, STORE_CODE));
		assertEquals(SAME_CODE_EXPECTED_ON_SECOND_CALL,	storeCode, cachingResolver.resolveStoreCodeSession(request, STORE_CODE));
	}
	/**
	 * Test that the store code is resolved and cached by <code>resolveStoreCodeHeader</code>.
	 */
	@Test
	public void testResolveDomainSessionIsCached() {
		final String domain = "domain";
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(domain, "http://www.store6.com");
		request.setSession(session);
		context.checking(new Expectations() {
			{
				oneOf(delegate).resolveDomainSession(request, STORE_CODE);
				will(returnValue("store6"));
			}
		});
		assertEquals(SAME_CODE_AS_DELEGATE_EXPECTED, "store6", cachingResolver.resolveDomainSession(request, STORE_CODE));
		assertEquals(SAME_CODE_EXPECTED_ON_SECOND_CALL,	"store6", cachingResolver.resolveDomainSession(request, STORE_CODE));
	}
}
