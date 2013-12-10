package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

/**
 * Tests for StoreMessageSource.
 *
 */
public class StoreMessageSourceTest {
	
	private static final String MSG_CODE = "msg code";
	
	private static final String NOT_FOUND = null;
	
	private final Locale locale = Locale.CANADA;
	
	/**
	 * Test that if the storecode is null then the known error message will be returned.
	 */
	@Test
	public void testResolveCodeWithoutArgumentsMissingCMMessage() {
		//NULL storecodes mean we try to get a global property first.
		StoreMessageSourceImpl storeMessageSource = new StoreMessageSourceImpl();
		assertEquals(NOT_FOUND, storeMessageSource.getMessage(null, null, MSG_CODE, locale));
	}
	
	/**
	 * Check the store-specific happy-case when everything is set up
	 * to work correctly. 
	 */
	@Test
	public void testResolveCodeWithoutArgumentsStoreSpecificMessage() {
		final String cachedMessage = "MyCachedMessage";
		StoreMessageSourceImpl storeMessageSource = new StoreMessageSourceImpl() {
			@Override
			String getCachedMessage(final String storeCode, final String themeCode, final String messageCode, final Locale locale) {
				return cachedMessage;
			}
		};
		assertEquals(cachedMessage, storeMessageSource.getMessage("storeCode", "themeCode", MSG_CODE, locale));
	}
	
	/**
	 * Test that if the store-specific message isn't found that if 
	 * one is found in the global messages it is returned.
	 */
	@Test
	public void testResolveCodeWithoutArgumentsFallbackToGlobal() {
		final String globalMessage = "MyGlobalMessage";
		StoreMessageSourceImpl storeMessageSource = new StoreMessageSourceImpl() {
			@Override
			String getCachedMessage(final String storeCode, final String themeCode, final String messageCode, final Locale locale) {
				return null;
			}
			@Override
			String getGlobalMessage(final String messageCode, final Locale locale) {
				return globalMessage;
			}
		};
		assertEquals(globalMessage, storeMessageSource.getMessage("storeCode", "themeCode", MSG_CODE, locale));
	}

}
