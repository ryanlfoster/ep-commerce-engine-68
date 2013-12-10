package com.elasticpath.sfweb.filters;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Set of test to test behaviour of targeted selling filter. 
 */
public class TargetedSellingFilterTest {

	private TargetedSellingFilter filter;
	
	/**
	 * set up filter object.
	 * @throws Exception setup exception
	 */
	@Before
	public void setUp() throws Exception {
		filter = new TargetedSellingFilter();
	}
	
	/**
	 * Test that resolution of the base url (baseUrl) global variable
	 * is correct.
	 * 
	 * Test that when elasticPath.sfContextUrl will return null, 
	 * baseUrl resolves to "".
	 */
	@Test
	public void testResolveBasePathFromSfContextUrlNull() {
		
		filter.setStorefrontContextUrl(null);
		
		String resolved = filter.resolveBasePathFromSfContextUrl();
		assertEquals("", resolved);
	}
	
	/**
	 * Test that resolution of the base url (baseUrl) global variable
	 * is correct.
	 * 
	 * Test that when elasticPath.sfContextUrl will return TargetedSellingFilter.NO_SF_CONTEXT_PATH, 
	 * baseUrl resolves to "".
	 */
	@Test
	public void testResolveBasePathFromSfContextUrlEmpty() {
		
		filter.setStorefrontContextUrl(TargetedSellingFilter.NO_SF_CONTEXT_PATH);
		
		String resolved = filter.resolveBasePathFromSfContextUrl();
		assertEquals("", resolved);
	}
	
	/**
	 * Test that resolution of the base url (baseUrl) global variable
	 * is correct.
	 * 
	 * Test that when elasticPath.sfContextUrl will return a valid url pattern, 
	 * baseUrl resolves to sfContextUrl.
	 */
	@Test
	public void testResolveBasePathFromSfContextUrlValid() {
		
		final String testUrl1 = "/some/url/string/";
		final String expectedUrl1 = "/some/url/string";		
		
		filter.setStorefrontContextUrl(testUrl1);
		
		String resolved1 = filter.resolveBasePathFromSfContextUrl();
		assertEquals(expectedUrl1, resolved1);
		
		final String testUrl2 = "/some/url/string";
		final String expectedUrl2 = "/some/url/string";	
				
		filter.setStorefrontContextUrl(testUrl2);
		
		String resolved2 = filter.resolveBasePathFromSfContextUrl();
		assertEquals(expectedUrl2, resolved2);
	}
	
	/**
	 * Test that resolution of the asset base url for dynamic content (baseDcAssetUrl) global 
	 * variable is correct.
	 * 
	 * Test that when baseUrl does not end with "/" the asset path resolves to 
	 * baseUrl/TargetedSellingFilter.BASE_DC_ASSET_URL_PATH.
	 */
	@Test
	public void testResolveBaseDcAssetPathFromBaseUrl() {
		
		final String baseUrl1 = "";
		final String baseDcNoLastSlash = "/content/dcassets";
		
		String resolved1 = filter.resolveBaseDcAssetPathFromBaseUrl(baseUrl1);
		assertEquals(baseDcNoLastSlash, resolved1);
		
		final String baseUrl2 = "hello/world";
		
		String resolved2 = filter.resolveBaseDcAssetPathFromBaseUrl(baseUrl2);
		assertEquals("hello/world" + baseDcNoLastSlash, resolved2);
		
		final String baseUrl3 = "/hello/world";
		
		String resolved3 = filter.resolveBaseDcAssetPathFromBaseUrl(baseUrl3);
		assertEquals("/hello/world" + baseDcNoLastSlash, resolved3);
		
		
	}

}