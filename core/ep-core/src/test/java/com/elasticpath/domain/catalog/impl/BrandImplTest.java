/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.BrandLocalizedPropertyValueImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;

/**
 * Test <code>BrandImpl</code>.
 */
public class BrandImplTest {

	private BrandImpl brand;
	private static final String DISPLAYNAME_DEFAULT_LOCALE = "testDisplayNameDefaultLocale";
	private static final Locale CATALOG_DEFAULT_LOCALE = Locale.GERMAN;
	private static final Locale NON_DEFAULT_LOCALE = Locale.FRANCE;
	private static final String DISPLAYNAME_OTHER_LOCALE = "testDisplayNameOtherLocale";

	@Before
	public void setUp() throws Exception {
		brand = new BrandImpl();
	}
	
	private LocalizedProperties createLocalizedPropertiesWithDisplayNameInDefaultLocale() {
		//give it a reference to the Utility class so that it can broaden the locale
		LocalizedProperties localizedProperties = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 7103749016476725247L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}

			@Override
			public Utility getUtility() {
				return new UtilityImpl();
			}
		};
		localizedProperties.setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, CATALOG_DEFAULT_LOCALE, DISPLAYNAME_DEFAULT_LOCALE);
		return localizedProperties;
	}
	
	private LocalizedProperties createLocalizedPropertiesWithDisplayNameInBothLocales() {
		LocalizedProperties localizedProperties = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 1294605010345851944L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		};
		localizedProperties.setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, CATALOG_DEFAULT_LOCALE, DISPLAYNAME_DEFAULT_LOCALE);
		localizedProperties.setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, NON_DEFAULT_LOCALE, DISPLAYNAME_OTHER_LOCALE);
		return localizedProperties;
	}
	
	private BrandImpl createBrandImplWithDisplayNameInDefaultLocale() {
		final LocalizedProperties lProperties = this.createLocalizedPropertiesWithDisplayNameInDefaultLocale();
		return createBrandImplForTesting(lProperties);
	}
	
	private BrandImpl createBrandImplWithDisplayNameInDefaultAndOtherLocale() {
		final LocalizedProperties lProperties = this.createLocalizedPropertiesWithDisplayNameInBothLocales();
		return createBrandImplForTesting(lProperties);		
	}	
	
	private BrandImpl createBrandImplForTesting(final LocalizedProperties lProperties) {
		return new BrandImpl() {
			private static final long serialVersionUID = 2287012119042990515L;

			@Override
			protected Locale getCatalogDefaultLocale() {
				return CATALOG_DEFAULT_LOCALE;
			}
			
			@Override
			public LocalizedProperties getLocalizedProperties() {
				return lProperties;
			}
		};
	}
	
	/**
	 * Test that getDisplayName falls back if directed to do so.
	 */
	@Test
	public void testGetDisplayNameFallsBackIfNecessary() {
		BrandImpl brand = createBrandImplWithDisplayNameInDefaultLocale();
		
		//fallback explicitly requested
		assertEquals("Should fall back if directed to do so", 
				DISPLAYNAME_DEFAULT_LOCALE, brand.getDisplayName(NON_DEFAULT_LOCALE, true));
		
		//fallback implicitly requested
		assertEquals("Should fall back to Catalog's default locale if display name is not available in given locale",
				DISPLAYNAME_DEFAULT_LOCALE, brand.getDisplayName(NON_DEFAULT_LOCALE));		
	}
	
	/**
	 * Test that getDisplayName gets the display name for the given locale,
	 * and that it doesn't get a display name if given a locale for which
	 * no display name exists if fallback is not enabled.
	 */
	@Test
	public void testGetDisplayNameDoesNotFallBackIfNotNecessary() {
		BrandImpl brand = createBrandImplWithDisplayNameInDefaultAndOtherLocale();
		
		//Base case
		assertEquals(DISPLAYNAME_DEFAULT_LOCALE, brand.getDisplayName(CATALOG_DEFAULT_LOCALE, false));
		
		//Doesn't fall back if not necessary
		assertEquals("Should not fall back if not necessary", 
				DISPLAYNAME_OTHER_LOCALE, brand.getDisplayName(NON_DEFAULT_LOCALE, true));
	}
	
	/**
	 * Test that getDisplayName doesn't fall back if it's not supposed to.
	 */
	@Test
	public void testGetDisplayNameDoesNotFallBackIfForbidden() {
		BrandImpl brand = createBrandImplWithDisplayNameInDefaultLocale();
		
		assertEquals("Should not fall back if forbidden", null, brand.getDisplayName(NON_DEFAULT_LOCALE, false));
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.BrandImpl.getImageUrl()'.
	 */
	@Test
	public void testGetSetImageUrl() {
		assertNull(brand.getImageUrl());
		brand.setImageUrl("images/logo.jpg");
		assertEquals("images/logo.jpg", brand.getImageUrl());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.BrandImpl.equals(Object)'.
	 */
	@Test
	public void testEquals() {
		final String gUid = "GUID";
		this.brand.setGuid(gUid);
		Brand brandToCompare = new BrandImpl();
		brandToCompare.setGuid(gUid);
		assertEquals(brand, brandToCompare);

		String anotherGuid = "Another_GUID";
		brandToCompare.setGuid(anotherGuid);
		assertFalse(brand.equals(brandToCompare));
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.BrandImpl.setCode(String)'.
	 */
	@Test
	public void testGetSetCode() {
		assertNull(brand.getCode());
		final String code1 = "testBrand1";
		brand.setCode(code1);
		assertSame(code1, brand.getCode());
		assertSame(code1, brand.getGuid());
		
		final String code2 = "testBrand2";
		brand.setGuid(code2);
		assertSame(code2, brand.getCode());
		assertSame(code2, brand.getGuid());
	}	
}
