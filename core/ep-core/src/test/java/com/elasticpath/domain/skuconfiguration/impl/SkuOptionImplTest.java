/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.skuconfiguration.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * Test cases for {@link SkuOptionImpl}.
 */
@SuppressWarnings({"PMD.TooManyStaticImports" })
public class SkuOptionImplTest {

	private static final String KEY1 = "key1";

	private static final String TEST_STRING = "testString";

	private static final String VALUE_CODE_1 = "value_code_1";

	private static final String VALUE_CODE_2 = "value_code_2";
	
	private static final Locale CATALOG_DEFAULT_LOCALE = Locale.GERMANY;
	private static final String DISPLAYNAME_DEFAULT_LOCALE = "default locale display name";
	private static final Locale OTHER_LOCALE = Locale.ITALIAN;
	private static final String DISPLAYNAME_OTHER_LOCALE = "other locale display name";

	private ElasticPath elasticPath;
	private SkuOptionImpl skuOption;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Prepare for the tests.
	 */
	@Before
	public void setUp() {
		elasticPath = context.mock(ElasticPath.class);
		skuOption = new SkuOptionImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public ElasticPath getElasticPath() {
				return elasticPath;
			}
		};

		final Catalog catalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				allowing(catalog).getDefaultLocale();
				will(returnValue(CATALOG_DEFAULT_LOCALE));
			}
		});
		skuOption.setCatalog(catalog);
	}

	/**
	 * Test method for 'com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl.getName(Locale)'.
	 */
	@Test
	public void testGetSetName() {
		skuOption.setOptionKey(TEST_STRING);
		assertEquals(TEST_STRING, skuOption.getOptionKey());
	}

	/**
	 * Test method for 'com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl.getOptionValueMap()'.
	 */
	@Test
	public void testGetSetOptionValueMap() {
		Map<String, SkuOptionValue> optionValueMap = getOptionValueMap();
		skuOption.setOptionValueMap(optionValueMap);
		assertSame(optionValueMap, skuOption.getOptionValueMap());
	}

	/**
	 * Test method for 'com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl.getOptionValues()'.
	 */
	@Test
	public void testGetSetOptionValues() {
		Set<SkuOptionValue> optionValues = getOptionValueSet();
		skuOption.setOptionValues(optionValues);
		assertEquals(1, skuOption.getOptionValueMap().size());
		assertTrue(skuOption.contains(VALUE_CODE_1));
	}

	/**
	 * Test method for 'com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl.addOptionValue(SkuOptionValue)'.
	 */
	@Test
	public void testAddOptionValue() {
		Map<String, SkuOptionValue> optionValues = getOptionValueMap();
		int numOptionValues = optionValues.size();
		skuOption.setOptionValueMap(optionValues);

		final SkuOptionValue newValue = context.mock(SkuOptionValue.class);
		context.checking(new Expectations() {
			{
				allowing(newValue).getOptionValueKey();
				will(returnValue(VALUE_CODE_2));
				allowing(newValue).getSkuOption();
				will(returnValue(null));
				oneOf(newValue).setSkuOption(skuOption);
			}
		});

		skuOption.addOptionValue(newValue);
		assertEquals(numOptionValues + 1, skuOption.getOptionValues().size());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl.contains(SkuOptionValueCode)'.
	 */
	@Test
	public void testContains() {
		Map<String, SkuOptionValue> optionValues = getOptionValueMap();
		skuOption.setOptionValueMap(optionValues);

		assertTrue(skuOption.contains(VALUE_CODE_1));
		assertFalse(skuOption.contains(VALUE_CODE_2));
	}

	/**
	 * Test method for 'com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl.getOptionValue(SkuOptionValueCode)'.
	 */
	@Test
	public void testGetOptionValue() {
		Map<String, SkuOptionValue> optionValues = getOptionValueMap();
		skuOption.setOptionValueMap(optionValues);

		assertNotNull(skuOption.getOptionValue(VALUE_CODE_1));
		assertNull(skuOption.getOptionValue(VALUE_CODE_2));
	}

	private Map<String, SkuOptionValue> getOptionValueMap() {
		Map<String, SkuOptionValue> valueSet = new HashMap<String, SkuOptionValue>();

		final SkuOptionValue skuOptionValue = context.mock(SkuOptionValue.class, "SkuOptionValue-" + VALUE_CODE_1);
		context.checking(new Expectations() {
			{
				allowing(skuOptionValue).getOptionValueKey();
				will(returnValue(VALUE_CODE_1));
			}
		});

		valueSet.put(VALUE_CODE_1, skuOptionValue);
		return valueSet;
	}

	private Set<SkuOptionValue> getOptionValueSet() {
		return new HashSet<SkuOptionValue>(getOptionValueMap().values());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.SkuOptionImpl.setOptionKey(String)'.
	 */
	@Test
	public void testSetKey() {
		assertNull(skuOption.getOptionKey());

		final String key1 = KEY1;
		skuOption.setOptionKey(key1);
		assertSame(key1, skuOption.getOptionKey());
		assertSame(key1, skuOption.getGuid());

		final String key2 = "key2";
		skuOption.setGuid(key2);
		assertSame(key2, skuOption.getOptionKey());
		assertSame(key2, skuOption.getGuid());
	}
	
	private void setupLocalizedDisplayNames(final boolean includeOtherLocale) {
		context.checking(new Expectations() {
			{
				LocalizedProperties localizedProperties = context.mock(LocalizedProperties.class);

				allowing(elasticPath).getBean(ContextIdNames.LOCALIZED_PROPERTIES);
				will(returnValue(localizedProperties));

				allowing(localizedProperties).setLocalizedPropertiesMap(with(skuOption.getLocalizedPropertiesMap()),
						with(any(String.class)));
				oneOf(localizedProperties).setValue(with(any(String.class)), with(equal(CATALOG_DEFAULT_LOCALE)),
						with(equal(DISPLAYNAME_DEFAULT_LOCALE)));
				allowing(localizedProperties).getValue(with(any(String.class)), with(equal(CATALOG_DEFAULT_LOCALE)));
				will(returnValue(DISPLAYNAME_DEFAULT_LOCALE));

				if (includeOtherLocale) {
					oneOf(localizedProperties).setValue(with(any(String.class)), with(equal(OTHER_LOCALE)),
							with(equal(DISPLAYNAME_OTHER_LOCALE)));
					allowing(localizedProperties).getValue(with(any(String.class)), with(equal(OTHER_LOCALE)));
					will(returnValue(DISPLAYNAME_OTHER_LOCALE));
				} else {
					allowing(localizedProperties).getValue(with(any(String.class)), with(equal(OTHER_LOCALE)));
					will(returnValue(null));
				}
			}
		});

		skuOption.setDisplayName(DISPLAYNAME_DEFAULT_LOCALE, CATALOG_DEFAULT_LOCALE);
		if (includeOtherLocale) {
			skuOption.setDisplayName(DISPLAYNAME_OTHER_LOCALE, OTHER_LOCALE);
		}
	}
	
	/**
	 * Test that getDisplayName will fall back to the default locale if
	 * the display name doesn't exist in the given locale and we're allowing fallbacks.
	 * Also tests that if fallbacks are forbidden then it will NOT fall back to the
	 * default locale.
	 */
	@Test
	public void testGetDisplayNameFallsBackIfNecessaryButNotIfForbidden() {
		setupLocalizedDisplayNames(false);
			
		assertEquals("Should fall back to default locale when asked for display name for non-existent locale and " 
				+ "fallback is set to true",
				DISPLAYNAME_DEFAULT_LOCALE, skuOption.getDisplayName(OTHER_LOCALE, true));
		
		assertEquals("Should not fall back if forbidden",
				null, skuOption.getDisplayName(OTHER_LOCALE, false));		
	}
	
	/**
	 * Test that getDisplayName doesn't fall back to the default locale if it
	 * finds the display name in the given locale.
	 */
	@Test
	public void testGetDisplayNameDoesNotFallBackIfNotNecessary() {
		setupLocalizedDisplayNames(true);

		assertEquals("Should not fall back if not necessary",
				DISPLAYNAME_OTHER_LOCALE, skuOption.getDisplayName(OTHER_LOCALE, true));
	}
	
	/**
	 * Tests that hashCode() works for hash based collections.
	 */
	@Test
	public void testHashCode() {
		SkuOption skuOption1 = new SkuOptionImpl();
		// test two objects with no data defined for them
		SkuOption skuOption2 = new SkuOptionImpl();
		Set<SkuOption> set = new HashSet<SkuOption>();
		set.add(skuOption1);
		set.add(skuOption2);
		
		// the sku options are identical as they do not have anything set yet
		assertEquals(1, set.size());

		// checks if one of the objects have different value for one of the attributes
		skuOption1.setOptionKey(KEY1);
		skuOption2.setOptionKey(null);
		set.clear();
		
		set.add(skuOption1);
		set.add(skuOption2);
		
		assertTrue(set.contains(skuOption1));
		assertTrue(set.contains(skuOption2));
		assertEquals(2, set.size());
		
		// make the two objects equal
		skuOption2.setOptionKey(KEY1);
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
		SkuOption skuOption1 = new SkuOptionImpl();
		// test two objects with no data defined for them
		SkuOptionImpl skuOption2 = new SkuOptionImpl();

		boolean equals = skuOption1.equals(skuOption2);
		assertTrue(equals);
		
		skuOption2.setOptionKey(KEY1);
		
		equals = skuOption1.equals(skuOption2);
		assertFalse(equals);

		equals = skuOption2.equals(skuOption1);
		assertFalse(equals);
	}
}
