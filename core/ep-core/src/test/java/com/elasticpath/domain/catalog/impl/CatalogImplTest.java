/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;

/**
 * Tests for various methods of the CatalogImpl class.
 */
public class CatalogImplTest {

	private static final Locale DEFAULT_LOCALE = Locale.GERMANY;
		
	/**
	 * Test that setting a Default Locale adds the locale
	 * to the collection of Supported Locales if not already there.
	 */
	@Test
	public void testSetDefaultLocaleAddsSupportedLocale() {
		CatalogImpl catalog = getCatalog();
		catalog.setMaster(true);
		assertFalse(catalog.getSupportedLocales().contains(DEFAULT_LOCALE));
		catalog.setDefaultLocale(DEFAULT_LOCALE);
		assertTrue("Adding a default locale should add to the supported locales", 
				catalog.getSupportedLocales().contains(DEFAULT_LOCALE));
		assertEquals(DEFAULT_LOCALE, catalog.getDefaultLocale());
	}
	
	/**
	 * Test that removing the default locale from the collection
	 * of supported locales throws a DefaultValueRemovalForbiddenException.
	 */
	@Test
	public void testRemoveDefaultLocaleFromSupportedLocalesForbidden() {
		CatalogImpl catalog = getCatalog();
		catalog.setMaster(true);
		catalog.setDefaultLocale(Locale.US);
		Collection<Locale> supportedLocales = new ArrayList<Locale>();
		supportedLocales.add(Locale.US);
		supportedLocales.add(Locale.CANADA);
		assertTrue("Setting the default locale should add that locale to supported locales", 
				catalog.getSupportedLocales().contains(Locale.US));
		assertSame(Locale.US, catalog.getDefaultLocale());
		Collection<Locale> localesWithoutDefault = new ArrayList<Locale>();
		localesWithoutDefault.add(Locale.CANADA);
		try {
			catalog.setSupportedLocales(getMultipleLocalesWithoutDefault());
			fail("Removal of default locale from supported locales is forbidden");
		} catch (DefaultValueRemovalForbiddenException exception) {
			assertNotNull(exception);
		}
	}
	
	/**
	 * Test that you can set the collection of supported locales without
	 * first setting a default locale.
	 */
	@Test
	public void testSettingSupportedLocalesWithoutDefaultLocale() {
		CatalogImpl catalog = getCatalog();
		catalog.setMaster(true);
		try {
			catalog.setSupportedLocales(this.getMultipleLocalesWithoutDefault());
		} catch (Exception ex) {
			fail("Should be able to set supported locales without first setting default locale");
		}
		assertEquals("The collection of supported locales should remain unchanged",
				this.getMultipleLocalesWithoutDefault(), catalog.getSupportedLocales());
	}

	private CatalogImpl getCatalog() {
		final CatalogImpl catalog = new StubbedCatalogImpl();
		catalog.initialize();
		catalog.setCode((new RandomGuidImpl()).toString());
		return catalog;
	}
	
	/**
	 * Test that you can't set the default locale to null.
	 */
	@Test
	public void testSetDefaultLocaleNullForbidden() {
		CatalogImpl catalog = getCatalog();
		try {
			catalog.setDefaultLocale(null);
			fail("Should not be able to set default locale to null");
		} catch (IllegalArgumentException ex) { 
			assertNotNull(ex);
		}
	}

	/**
	 * Test that getting the supported locales results in an unmodifiable collection.
	 */
	@Test
	public void testGetSupportedLocalesIsUnmodifiable() {
		CatalogImpl catalog = getCatalog();
		catalog.setMaster(true);
		try {
			catalog.getSupportedLocales().add(Locale.US);
			fail("Should not be allowed to modify the collection of returned locales");
		} catch (UnsupportedOperationException ex) {
			assertNotNull(ex);
		}
	}
	
	/**
	 * Test that supported currencies, currencies in use, 
	 * supported locales, and locales in use are never null but 
	 * are instead empty if none are set.
	 */
	@Test
	public void testSetsNeverNull() {
		CatalogImpl catalog = getCatalog();
		catalog.setMaster(true);
		assertEquals(Collections.emptySet(), catalog.getSupportedLocales());
	}
	
	/**
	 * Test that adding a supported locale adds the locale to the list of supported locales.
	 */
	@Test
	public void testAddSupportedLocale() {
		Catalog catalog = getCatalog();
		catalog.setMaster(true);
		Locale locale = Locale.JAPANESE;
		catalog.addSupportedLocale(locale);
		assertEquals(1, catalog.getSupportedLocales().size());
		assertTrue(catalog.getSupportedLocales().contains(locale));
		assertEquals(locale.getDisplayName(), catalog.getSupportedLocales().iterator().next().getDisplayName());
	}

	/**
	 * Test that adding a supported locale adds nothing for a virtual catalog.
	 */
	@Test
	public void testAddSupportedLocaleVirtualCatalog() {
		CatalogImpl vcatalog = getCatalog();
		Locale locale = Locale.JAPANESE;
		try {
			vcatalog.addSupportedLocale(locale);
			fail("Virtual catalog should not support this operation");
		} catch (UnsupportedOperationException exc) {
			assertNotNull(exc);
		}
		assertEquals(0, vcatalog.getSupportedLocalesInternal().size());
	}

	/**
	 * Gets a collection of Locales that does not contain the 
	 * US locale.
	 * @return collection of locales not containing the default locale
	 */
	private Set<Locale> getMultipleLocalesWithoutDefault() {
		Set<Locale> locales = new HashSet<Locale>();
		locales.add(Locale.FRANCE);
		locales.add(Locale.GERMANY);
		return locales;
	}
		
	/**
	 * Test that getGuid() and setGuid() delegate to the getCode() and setCode() methods.
	 */
	@Test
	public void testGetSetGuid() {
		final String code = "MyCode";
		CatalogImpl catalog = getCatalog();
		catalog.setGuid(code);
		assertEquals("setGuid and getGuid should delegate to setCode/getCode", code, catalog.getCode());
	}
	
	/**
	 * Test specifically for equality between transient and persistent instances.
	 */
	@Test
	public void testHashcodeAndEquals() {
		final String persistentShouldNotEqualTransient = "persistent instance should not equal a transient instance";
		
		// Test with two transient instances.
		CatalogImpl catalog1 = getCatalog();
		CatalogImpl catalog2 = getCatalog();
	
		assertFalse("Shouldn't equal null", catalog1.equals(null));   // NOPMD - literal in first position
		assertFalse("Shouldn't equal a string", catalog1.equals("catalog"));     // NOPMD - literal in first position
		assertFalse("Shouldn't equal a random object", catalog1.equals(new Object()));
		assertFalse("Two different transient objects should not be equals", catalog1.equals(catalog2));
		assertEquals("The catalog should always be equal to itself", catalog1, catalog1);
		assertEquals("Consecutive hashCode calls should be consistent", catalog1.hashCode(), catalog1.hashCode());
		assertTrue("It is very unlikely that hashCode should be zero", 0 != catalog1.hashCode());
		
		// Create and test with a persistent instance		
		CatalogImpl catalog3 = getCatalog();
		final long uidPk5000 = 5000;
		catalog3.setUidPk(uidPk5000);
		
		assertFalse(persistentShouldNotEqualTransient, catalog3.equals(catalog1));
		assertFalse(persistentShouldNotEqualTransient, catalog3.equals(catalog2));
		assertFalse("transient instance should not equal a persistent instance (associative)", catalog2.equals(catalog3));
		
		// Create and tests with a duplicate persistent instance.
		CatalogImpl catalog4 = getCatalog();
		catalog4.setUidPk(catalog3.getUidPk());
		catalog4.setCode(catalog3.getCode());
		
		assertFalse(persistentShouldNotEqualTransient, catalog4.equals(catalog1));
		assertFalse(persistentShouldNotEqualTransient, catalog4.equals(catalog2));
		assertEquals("persistent instance should equal a duplicate instance", catalog4, catalog3);
		assertEquals("persistent instance should equal a duplicate instance (associative)", catalog3, catalog4);
		assertEquals("hashCodes of equal persistent objects are the same", catalog3.hashCode(), catalog4.hashCode());
		
		
		// Now test with a new unique persistent instance
		CatalogImpl catalog5 = getCatalog();
		final long uidPk9999 = 9999;
		catalog5.setUidPk(uidPk9999);
		
		assertFalse(persistentShouldNotEqualTransient, catalog5.equals(catalog1));
		assertFalse(persistentShouldNotEqualTransient, catalog5.equals(catalog2));
		assertFalse("persistent instance should not equal a different instance (db row)", catalog5.equals(catalog3));
		assertFalse("persistent instance should not equal a different instance (db row)", catalog5.equals(catalog4));
		assertEquals("should equal itself", catalog5, catalog5);
		
	}

	/**
	 * Separate class to work around a Checkstyle defect.
	 * 
	 * @see <a href="http://sourceforge.net/p/checkstyle/bugs/472/">the defect</a>
	 */
	private class StubbedCatalogImpl extends CatalogImpl {
		private static final long serialVersionUID = 911805475665750452L;

		@Override
		public ElasticPath getElasticPath() {
			return new StubbedElasticPathImpl();
		}

		final class StubbedElasticPathImpl extends ElasticPathImpl {
			@SuppressWarnings("unchecked")
			@Override
			public <T> T getBean(final String name) {
				if (name.equals(ContextIdNames.CATALOG_LOCALE)) {
					return (T) new CatalogLocaleImpl();
				}
				if (name.equals(ContextIdNames.RANDOM_GUID)) {
					return (T) new RandomGuidImpl();
				}
				return null;
			}
		}
	}

}
