/**
 * 
 */
package com.elasticpath.sfweb.util.impl;

import static org.junit.Assert.assertNull;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;


/**
 * Tests for AbstractResourceRetrievalStrategyTest.
 */
public class AbstractResourceRetrievalStrategyTest {

//	private static final String TEST_DIRECTORY = "WEB-INF/src/test";
	
	/**
	 * Test that resolveResource returns null if the resource file doesn't exist.
	 * @throws Exception on error
	 */
	@Test
	public void testResolveResourceNull() throws Exception {
		AbstractResourceRetrievalStrategy strategy = new AbstractResourceRetrievalStrategy() {

			@Override
			public String getFullPath(final String resourcePath) {
				return "PathToNonExistentFile";
			}
			
		};
		assertNull("Strategy should be null", strategy.resolveResource(StringUtils.EMPTY));
	}
	
//	/**
//	 * Test that resolveResource returns a URL to a file that exists.
//	 * @throws Exception on error
//	 */
//	@Test
//	public void testResolveResourceNotNull() throws Exception {
//		AbstractResourceRetrievalStrategy strategy = new AbstractResourceRetrievalStrategy() {
//
//			@Override
//			public String getFullPath(final String resourcePath) {
//				return TEST_DIRECTORY;
//			}
//			
//		};
//		assertNotNull(strategy.resolveResource(StringUtils.EMPTY));
//	}
}
