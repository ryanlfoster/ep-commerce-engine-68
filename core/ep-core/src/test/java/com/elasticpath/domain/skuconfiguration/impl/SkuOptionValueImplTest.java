/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.skuconfiguration.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.BrandLocalizedPropertyValueImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * Test cases for <code>SkuOptionValue</code>.
 */
public class SkuOptionValueImplTest {

	private static final String COMMON_GUID = "COMMON_GUID";

	private static final String TEST_STRING = "testString";

	private static final int TEST_INT = 27;

	private SkuOptionValueImpl skuOptionValue;

	private static final Locale CATALOG_DEFAULT_LOCALE = Locale.GERMANY;
	private static final String DISPLAYNAME_DEFAULT_LOCALE = "default locale display name";

	private static final Locale OTHER_LOCALE = Locale.ITALIAN;
	private static final String DISPLAYNAME_OTHER_LOCALE = "other locale display name";

	@Before
	public void setUp() throws Exception {
		skuOptionValue = new SkuOptionValueImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl.getOptionCode()'.
	 */
	@Test
	public void testGetSetOptionCode() {
		skuOptionValue.setOptionValueKey(TEST_STRING);
		assertEquals(TEST_STRING, skuOptionValue.getOptionValueKey());
	}

	/**
	 * Test method for 'com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl.getSmallImage()'.
	 */
	@Test
	public void testGetSetImage() {
		skuOptionValue.setImage("TEST_STRING");
		assertEquals("TEST_STRING", skuOptionValue.getImage());
	}


	/**
	 * Test method for 'com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl.getOrder()'.
	 */
	@Test
	public void testGetSetOrder() {
		skuOptionValue.setOrdering(TEST_INT);
		assertEquals(TEST_INT, skuOptionValue.getOrdering());
	}

	/**
	 * Test that getDisplayName() falls back when asked to do so.
	 */
	@Test
	public void testGetDisplayNameFallsBackIfNecessary() {
		final SkuOptionValueImpl skuOptionValue = createSkuOptionValueWithOnlyDefaultLocaleDisplayName();
		skuOptionValue.setDisplayName(CATALOG_DEFAULT_LOCALE, DISPLAYNAME_DEFAULT_LOCALE);
		assertEquals("Should fall back to default locale's display name since display name for other locale non-existent",
				DISPLAYNAME_DEFAULT_LOCALE, skuOptionValue.getDisplayName(OTHER_LOCALE, true));
	}

	/**
	 * Test that getDisplayName() doesn't fall back when the correct value exists,
	 * even though it could.
	 */
	@Test
	public void testGetDisplayNameDoesNotFallBackIfNotNecessary() {
		final SkuOptionValueImpl skuOptionValue = createSkuOptionValueWithDisplayNameInDefaultAndOtherLocale();
		skuOptionValue.setDisplayName(CATALOG_DEFAULT_LOCALE, DISPLAYNAME_DEFAULT_LOCALE);
		skuOptionValue.setDisplayName(OTHER_LOCALE, DISPLAYNAME_OTHER_LOCALE);
		assertEquals("Should not fall back since it doesn't have to",
				DISPLAYNAME_OTHER_LOCALE, skuOptionValue.getDisplayName(OTHER_LOCALE, true));
	}

	/**
	 * Test that getDisplayName doesn't fall back if it's not supposed to.
	 */
	@Test
	public void testGetDisplayNameDoesNotFallBackIfForbidden() {
		final SkuOptionValueImpl skuOptionValue = createSkuOptionValueWithOnlyDefaultLocaleDisplayName();
		assertEquals("Should not fall back if forbidden", null, skuOptionValue.getDisplayName(OTHER_LOCALE, false));
	}

	private SkuOptionValueImpl createSkuOptionValueWithDisplayNameInDefaultAndOtherLocale() {
		return new SkuOptionValueImpl() {
			private static final long serialVersionUID = -5082836448820498157L;

			@Override
			public LocalizedProperties getLocalizedProperties() {
				final LocalizedProperties localizedProperties = new LocalizedPropertiesImpl() {
					private static final long serialVersionUID = 8267811593561001044L;

					@Override
					protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
						return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
					}

					@Override
					public Utility getUtility() {
						return new UtilityImpl();
					}
				};

				localizedProperties.setValue(SkuOptionValueImpl.LOCALIZED_PROPERTY_DISPLAY_NAME, CATALOG_DEFAULT_LOCALE, DISPLAYNAME_DEFAULT_LOCALE);
				localizedProperties.setValue(SkuOptionValueImpl.LOCALIZED_PROPERTY_DISPLAY_NAME, OTHER_LOCALE, DISPLAYNAME_OTHER_LOCALE);
				return localizedProperties;
			}

			@Override
			protected Locale getMasterCatalogLocale() {
				return CATALOG_DEFAULT_LOCALE;
			}
		};
	}

	private SkuOptionValueImpl createSkuOptionValueWithOnlyDefaultLocaleDisplayName() {
		return new SkuOptionValueImpl() {
			private static final long serialVersionUID = 7996850480495524696L;

			@Override
			public LocalizedProperties getLocalizedProperties() {
				final LocalizedProperties localizedProperties = new LocalizedPropertiesImpl() {
					private static final long serialVersionUID = 9069602459685662545L;

					@Override
					protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
						return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
					}

					@Override
					public Utility getUtility() {
						return new UtilityImpl();
					}
				};

				localizedProperties.setValue(SkuOptionValueImpl.LOCALIZED_PROPERTY_DISPLAY_NAME, CATALOG_DEFAULT_LOCALE, DISPLAYNAME_DEFAULT_LOCALE);
				return localizedProperties;
			}

			@Override
			protected Locale getMasterCatalogLocale() {
				return CATALOG_DEFAULT_LOCALE;
			}
		};
	}

	/**
	 * Tests that hashCode() works for hash based collections.
	 */
	@Test
	public void testHashCode() {
		final SkuOptionValue skuOption1 = new SkuOptionValueImpl();
		// test two objects with no data defined for them
		final SkuOptionValue skuOption2 = new SkuOptionValueImpl();
		final Set<SkuOptionValue> set = new HashSet<SkuOptionValue>();
		set.add(skuOption1);
		set.add(skuOption2);

		// the sku options are identical as they do not have anything set yet
		assertEquals(1, set.size());

		// checks if one of the objects have different value for one of the attributes
		skuOption1.setOptionValueKey("key1");
		skuOption2.setOptionValueKey(null);
		set.clear();

		set.add(skuOption1);
		set.add(skuOption2);

		assertTrue(set.contains(skuOption1));
		assertTrue(set.contains(skuOption2));
		assertEquals(2, set.size());

		// make the two objects equal
		skuOption2.setOptionValueKey("key1");
		set.clear();

		set.add(skuOption1);
		set.add(skuOption2);

		assertTrue(set.contains(skuOption1));
		assertTrue(set.contains(skuOption2));
		assertEquals(1, set.size());

	}

	/**
	 * Tests that equals() behaves in the expected way.
	 */
	@Test
	public void testEquals() {
		final SkuOptionValue skuOption1 = new SkuOptionValueImpl();
		// test two objects with no data defined for them
		final SkuOptionValue skuOption2 = new SkuOptionValueImpl();

		boolean equals = skuOption1.equals(skuOption2);
		assertTrue(equals);

		skuOption2.setOptionValueKey("key1");

		equals = skuOption1.equals(skuOption2);
		assertFalse(equals);

		equals = skuOption2.equals(skuOption1);
		assertFalse(equals);
	}

	/**
	 * Test symmetry of the SkuOptionValue equals method.
	 */
	@Test
	public void testEqualsSymmetric() {

		final SkuOptionValue skuOptionValue1 = new SkuOptionValueImpl();
		skuOptionValue1.setGuid(COMMON_GUID);

		final SkuOptionValueImpl skuOptionValue2 = new SkuOptionValueImpl();
		skuOptionValue2.setGuid(COMMON_GUID);

		assertEquals(true, skuOptionValue1.equals(skuOptionValue2));
		assertEquals(true, skuOptionValue2.equals(skuOptionValue1));

		// create an instance that shares the same parent entity equals method
		final Attribute attribute = new AttributeImpl();
		attribute.setGuid(COMMON_GUID);

		assertEquals(false, skuOptionValue1.equals(attribute));
		assertEquals(false, attribute.equals(skuOptionValue1));
	}

	/**
	 * Test the null case of the SkuOptionValue equals method.
	 */
	@Test
	public void testEqualsNull() {
		final SkuOptionValue skuOptionValue1 = new SkuOptionValueImpl();
		skuOptionValue1.setGuid(COMMON_GUID);

		final SkuOptionValueImpl skuOptionValue2 = null;

		assertEquals(false, skuOptionValue1.equals(skuOptionValue2));
	}



}
