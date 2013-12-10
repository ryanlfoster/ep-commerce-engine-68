/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;

/**
 * Test the store resolver works as expected.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.AvoidUsingHardCodedIP" })
public class StoreResolverImplTest {

	private static final String WWW_SNAPITUP_COM = "www.snapitup.com";
	private static final String STORE_CODE_RESOLUTION_EXPECTATION = "Store code should resolve from the mapped domain";
	private static final String SNAPITUP = "SNAPITUP";
	private static final String STORE_CODE = "storeCode";
	private static final String HOST_HEADER = "HOST";
	
	private StoreResolverImpl resolver;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Set up objects required for all tests.
	 * 
	 * @throws java.lang.Exception in case of an error setting up the objects.
	 */
	@Before
	public void setUp() throws Exception {
		resolver = new StoreResolverImpl() {
			
			@Override
			protected Map<String, String> getDomainToStoreMapping() {
				Map<String, String> map = new HashMap<String, String>();
				map.put(WWW_SNAPITUP_COM, SNAPITUP);
				map.put("www.shoes.com", "SHOES");
				
				return map;
			}
			
			@Override
			protected Collection<String> getStoreCodes() {
				Set<String> codes = new HashSet<String>();
				codes.add(SNAPITUP);
				return codes;
			}
		};
	}

	
	/**
	 * Test that a null or invalid header name doesn't resolve to a store code.
	 */
	@Test
	public void testResolveDomainHeaderWithNullHeaderName() {
		HttpServletRequest request = new MockHttpServletRequest();
		assertNull("no header name should return null store code", resolver.resolveDomainHeader(request, null));
		assertNull("no header should return null store code", resolver.resolveDomainHeader(request, "invalidname"));
	}
	
	/**
	 * Test that a invalid domain in a header doesn't resolve to a store code.
	 */
	@Test
	public void testResolveDomainHeaderWithInvalidDomain() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HOST_HEADER, "www.non-existing-domain.com");
		assertNull("invalid domain should return null store code", resolver.resolveDomainHeader(request, HOST_HEADER));
	}
	
	/**
	 * Test that a header with a valid domain resolves to a store code.
	 */
	@Test
	public void testResolveDomainHeaderWithValidDomain() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HOST_HEADER, WWW_SNAPITUP_COM);
		request.addHeader("another-header", "www.shoes.com");
		assertEquals(STORE_CODE_RESOLUTION_EXPECTATION, SNAPITUP, resolver.resolveDomainHeader(request, HOST_HEADER));
		assertEquals(STORE_CODE_RESOLUTION_EXPECTATION, "SHOES", resolver.resolveDomainHeader(request, "another-header"));
	}
	
	/**
	 * Test that a header with a valid domain name and port resolves to a store code.
	 */
	@Test
	public void testResolveDomainHeaderWithValidDomainAndPort() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HOST_HEADER, "www.snapitup.com:8080");
		assertEquals(STORE_CODE_RESOLUTION_EXPECTATION, SNAPITUP, resolver.resolveDomainHeader(request, HOST_HEADER));
	}
	
	/**
	 * Test that a null or invalid header name doesn't resolve to a store code.
	 */
	@Test
	public void testResolveStoreCodeHeaderWithNullHeaderName() {
		HttpServletRequest request = new MockHttpServletRequest();
		assertNull("no header name should return null store code", resolver.resolveStoreCodeHeader(request, null));
		assertNull("no header should return null store code", resolver.resolveStoreCodeHeader(request, "nosuchheader"));
	}
	
	/**
	 * Test that a header with an invalid store code doesn't resolve to a store code.
	 */
	@Test
	public void testResolveStoreCodeHeaderWithInvalidStoreCode() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(STORE_CODE, "nosuchstore");
		assertNull("invalid store code should return null store code", resolver.resolveStoreCodeHeader(request, STORE_CODE));
	}
	
	/**
	 * Test that a header with a valid store code resolves correctly.
	 */
	@Test
	public void testResolveStoreCodeHeaderWithValidStoreCode() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(STORE_CODE, SNAPITUP);
		assertEquals(STORE_CODE_RESOLUTION_EXPECTATION, SNAPITUP, resolver.resolveStoreCodeHeader(request, STORE_CODE));
	}
	
	/**
	 * Test that a null or invalid parameter name doesn't resolve to a store code.
	 */
	@Test
	public void testResolveDomainParamWithNullParamName() {
		HttpServletRequest request = new MockHttpServletRequest();
		assertNull("no param name should return null store code", resolver.resolveDomainParam(request, null));
		assertNull("no param should return null store code", resolver.resolveDomainParam(request, "invalidname"));
	}
	
	/**
	 * Test that a invalid domain name doesn't resolve to a store code.
	 */
	@Test
	public void testResolveDomainParamWithInvalidDomain() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter(HOST_HEADER, "www.non-existing-domain.com");
		assertNull("invalid domain should return null store code", resolver.resolveDomainParam(request, HOST_HEADER));
	}
	
	/**
	 * Test that a valid domain name resolves to the correct store code.
	 */
	@Test
	public void testResolveDomainParamWithValidDomain() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter(STORE_CODE, WWW_SNAPITUP_COM);
		assertEquals(STORE_CODE_RESOLUTION_EXPECTATION, SNAPITUP, resolver.resolveDomainParam(request, STORE_CODE));
	}

	/**
	 * Test that valid domain name and port resolves to the correct store code.
	 */
	@Test
	public void testResolveDomainParamWithValidDomainAndPort() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter(STORE_CODE, "www.snapitup.com:8080");
		assertEquals(STORE_CODE_RESOLUTION_EXPECTATION, SNAPITUP, resolver.resolveDomainParam(request, STORE_CODE));
	}
	
	/**
	 * Test that a null or empty or invalid parameter name doesn't resolve to a store code.
	 */
	@Test
	public void testResolveStoreCodeSessionWithInvalidParamName() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		assertNull("no parameter name should return null store code", resolver.resolveStoreCodeSession(request, null));
		assertNull("no parameter should return null store code", resolver.resolveStoreCodeSession(request, "anything"));
	}
	
	/**
	 * Test that an invalid store code name doesn't resolve.
	 */
	@Test
	public void testResolveStoreCodeSessionWithInvalidStoreCode() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(STORE_CODE, "nosuchstore");
		request.setSession(session);
		assertNull("invalid store code should return null store code", resolver.resolveStoreCodeSession(request, STORE_CODE));
	}
	
	/**
	 * Test that a valid store code on the parameter resolves correctly.
	 */
	@Test
	public void testResolveStoreCodeSessionWithValidStoreCode() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(STORE_CODE, SNAPITUP);
		request.setSession(session);
		assertEquals(STORE_CODE_RESOLUTION_EXPECTATION, SNAPITUP, resolver.resolveStoreCodeSession(request, STORE_CODE));
	}

	/**
	 * Test that a null or invalid parameter name doesn't resolve to a store code.
	 */
	@Test
	public void testResolveStoreCodeParamWithNullParamName() {
		HttpServletRequest request = new MockHttpServletRequest();
		assertNull("no parameter name should return null store code", resolver.resolveStoreCodeParam(request, null));
		assertNull("no parameter should return null store code", resolver.resolveStoreCodeParam(request, "anything"));
	}
	
	/**
	 * Test that an invalid store code name doesn't resolve.
	 */
	@Test
	public void testResolveStoreCodeParamWithInvalidStoreCode() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter(STORE_CODE, "nosuchstore");
		assertNull("invalid store code should return null store code", resolver.resolveStoreCodeParam(request, STORE_CODE));
	}
	
	/**
	 * Test that a valid store code on the parameter resolves correctly.
	 */
	@Test
	public void testResolveStoreCodeParamWithValidStoreCode() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter(STORE_CODE, SNAPITUP);
		assertEquals(STORE_CODE_RESOLUTION_EXPECTATION, SNAPITUP, resolver.resolveStoreCodeParam(request, STORE_CODE));
	}

	/**
	 * Test that a null or invalid parameter name doesn't resolve to a store code.
	 */
	@Test
	public void testResolveDomainSessionWithNullParamName() {
		HttpServletRequest request = new MockHttpServletRequest();
		assertNull("no param name should return null store code", resolver.resolveDomainSession(request, null));
		assertNull("no param should return null store code", resolver.resolveDomainSession(request, "anything"));
	}
	
	/**
	 * Test that a invalid domain name doesn't resolve to a store code.
	 */
	@Test
	public void testResolveDomainSessionWithInvalidDomain() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(HOST_HEADER, "www.non-existing-domain.com");
		request.setSession(session);
		assertNull("invalid domain should return null store code", resolver.resolveDomainSession(request, HOST_HEADER));
	}
	
	/**
	 * Test that a valid domain name resolves to the correct store code.
	 */
	@Test
	public void testResolveDomainSessionWithValidDomain() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(HOST_HEADER, WWW_SNAPITUP_COM);
		request.setSession(session);
		assertEquals(STORE_CODE_RESOLUTION_EXPECTATION, SNAPITUP, resolver.resolveDomainSession(request, HOST_HEADER));
	}

	/**
	 * Test that valid domain name and port resolves to the correct store code.
	 */
	@Test
	public void testResolveDomainSessionWithValidDomainAndPort() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(STORE_CODE, "www.snapitup.com:8080");
		request.setSession(session);
		assertEquals(STORE_CODE_RESOLUTION_EXPECTATION, SNAPITUP, resolver.resolveDomainSession(request, STORE_CODE));
	}
	
	/**
	 * Test that a null domain results in a null store code.
	 */
	@Test
	public void testResolveStoreCodeFromDomainForNullDomain() {
		assertNull("null domain should return null store code", resolver.resolveStoreCodeFromDomain(null));
	}

	/**
	 * Test that an empty domain results in a null store code.
	 */
	@Test
	public void testResolveStoreCodeFromDomainForEmptyDomain() {
		assertNull("null domain should return null store code", resolver.resolveStoreCodeFromDomain(""));
	}

	/**
	 * Test that an valid domain results in a valid store code.
	 */
	@Test
	public void testResolveStoreCodeFromDomain() {
		assertEquals("domain should map to expected store code", SNAPITUP, resolver.resolveStoreCodeFromDomain(WWW_SNAPITUP_COM));
	}

	/**
	 * Test that an valid domain results in a valid store code.
	 */
	@Test
	public void testResolveStoreCodeFromDomainWithInvalidDomain() {
		assertNull("invalid domain should return null", resolver.resolveStoreCodeFromDomain("www.invalid.com"));
	}

	/**
	 * Test that a null store code is not valid.
	 */
	@Test
	public void testValidateStoreCodeForNullCode() {
		assertNull("null storecode should return null", resolver.validateStoreCode(null));
	}

	/**
	 * Test that an empty store code is not valid.
	 */
	@Test
	public void testValidateStoreCodeForEmptyCode() {
		assertNull("empty storecode should return null", resolver.validateStoreCode(""));
	}

	/**
	 * Test that an invalid store code returns null.
	 */
	@Test
	public void testValidateStoreCodeForInvalidCode() {
		assertNull("empty storecode should return null", resolver.validateStoreCode("invalid"));
	}

	/**
	 * Test that a valid store code resolves correctly.
	 */
	@Test
	public void testValidateStoreCode() {
		assertEquals("valid storecode should be returned", SNAPITUP, resolver.validateStoreCode(SNAPITUP));
	}

	/**
	 * Test method for {@link com.elasticpath.sfweb.util.impl.StoreResolverImpl#getStoreCodes()}.
	 */
	@Test
	public void testGetStoreCodes() {
		final StoreResolverImpl resolver = new StoreResolverImpl();
		final StoreService storeService = context.mock(StoreService.class);
		final Store store1 = context.mock(Store.class, "storeA");
		final Store store2 = context.mock(Store.class, "storeB");
		final List<Store> stores = Arrays.asList(store1, store2);
		
		context.checking(new Expectations() {
			{
				oneOf(storeService).findAllCompleteStores();
				will(returnValue(stores));

				oneOf(store1).getCode();
				will(returnValue("store1"));
				
				oneOf(store2).getCode();
				will(returnValue("store2"));
			}
		});
		resolver.setStoreService(storeService);
		Collection<String> storeCodes = resolver.getStoreCodes();
		assertTrue("store codes should contain first store's code", storeCodes.contains("store1"));
		assertTrue("store codes should contain second store's code", storeCodes.contains("store2"));
	}

	/**
	 * Test that getting a domain to store mapping maps the stores correctly.
	 */
	@Test
	public void testGetDomainToStoreMapping() {
		final StoreResolverImpl resolver = new StoreResolverImpl();
		final StoreService storeService = context.mock(StoreService.class);
		final Store store1 = context.mock(Store.class, "storeA");
		final Store store2 = context.mock(Store.class, "storeB");
		final List<Store> stores = Arrays.asList(store1, store2);
		
		context.checking(new Expectations() {
			{
				oneOf(storeService).findAllCompleteStores();
				will(returnValue(stores));
				
				oneOf(store1).getUrl();
				will(returnValue("http://www.store3.com"));
				
				oneOf(store1).getCode();
				will(returnValue("store3"));
				
				oneOf(store2).getUrl();
				will(returnValue("http://www.store4.com/storefront"));
				
				oneOf(store2).getCode();
				will(returnValue("store4"));
			}
		});
		resolver.setStoreService(storeService);
		Map<String, String> storeMappings = resolver.getDomainToStoreMapping();
		assertEquals("Store mapping size should = 2", 2, storeMappings.size());
		assertEquals("www.store3.com should map to store3", "store3", storeMappings.get("www.store3.com"));
		assertEquals("www.store4.com should map to store4", "store4", storeMappings.get("www.store4.com"));
	}

	/**
	 * Test that we can extract the domain from a URL correctly.
	 */
	@Test
	public void testExtractDomainName() {
		assertNull("resolver should be null", resolver.extractDomainName(""));
		assertEquals("extracted domain should be www.simpledomain.com", "www.simpledomain.com", 
				resolver.extractDomainName("http://www.simpledomain.com"));
		assertEquals("extracted domain should be www.abcdef-nonexistent-domain.jom", 
				"www.abcdef-nonexitent-domain.jom", resolver.extractDomainName("https://www.abcdef-nonexitent-domain.jom:8080/fred/bob"));
		assertEquals("extracted domain should be null", null, resolver.extractDomainName("htt:/w/fred"));
		assertEquals("extracted domain should be 127.0.0.1", "127.0.0.1", resolver.extractDomainName("http://127.0.0.1"));
	}

	/**
	 * Test stripping a port number where there isn't one.
	 */
	@Test
	public void testStripAnyPortNumberWithNoPortNumber() {
		assertEquals("host should be somedomain.com", "somedomain.com", resolver.stripAnyPortNumber("somedomain.com"));
	}

	/**
	 * Test stripping a port number.
	 */
	@Test
	public void testStripAnyPortNumber() {
		assertEquals("host should be whatever.com", "whatever.com", resolver.stripAnyPortNumber("whatever.com:8000"));
	}

	
}

