/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.attribute.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/** Test cases for <code>AttributeValueGroupImpl</code>. */
public class AttributeValueGroupImplTest {

	private static final String HELLO = "Hello";

	private static final String BONJOUR = "Bonjour";

	private static final String CUSTOM_LOCALE = "Custom Locale";

	private static final String ATTRIBUTE_KEY_1 = "details";

	private static final String ATTRIBUTE_KEY_2 = "testDate";

	private static final String ATTRIBUTE_KEY_3 = "image";

	private static final int ORIGINAL_COUNT = 4;

	private AttributeValueGroupImpl attributeValueGroup;

	private Map<String, AttributeValue> attributeValueMap;

	private Set<AttributeGroupAttribute> fullAttributeSet;

	/** Locale-dependent attribute. */
	private AttributeImpl attribute1;

	/** Locale-independent attribute. */
	private AttributeImpl attribute2;

	/** Locale-dependent attribute. */
	private AttributeImpl attribute3;

	private AttributeGroup attributeGroup;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepares for the next test.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Before
	public void setUp() throws Exception {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_USAGE, new AttributeUsageImpl());
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.UTILITY, new UtilityImpl());
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, new RandomGuidImpl());

		attributeValueMap = new HashMap<String, AttributeValue>();
		this.setupAttribute1();
		this.setupAttribute2();
		this.setupAttribute3();

		setupAttributeGroup();

		// Instantiate a subclass
		attributeValueGroup = new AttributeValueGroupImpl(new ProductAttributeValueFactoryImpl());
		attributeValueGroup.setAttributeValueMap(attributeValueMap);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	private void setupAttributeGroup() {
		fullAttributeSet = new HashSet<AttributeGroupAttribute>();

		AttributeGroupAttribute attributeGroupAttribute = new AttributeGroupAttributeImpl();
		attributeGroupAttribute.setAttribute(attribute1);
		fullAttributeSet.add(attributeGroupAttribute);

		attributeGroupAttribute = new AttributeGroupAttributeImpl();
		attributeGroupAttribute.setAttribute(attribute2);
		fullAttributeSet.add(attributeGroupAttribute);

		attributeGroupAttribute = new AttributeGroupAttributeImpl();
		attributeGroupAttribute.setAttribute(attribute3);
		fullAttributeSet.add(attributeGroupAttribute);

		attributeGroup = new AttributeGroupImpl();
		attributeGroup.setAttributeGroupAttributes(fullAttributeSet);

	}

	/**
	 * Test method for getting and setting the attribute value set.
	 */
	@Test
	public void testGetSetAttributeValues() {
		assertEquals(attributeValueMap, attributeValueGroup.getAttributeValueMap());
	}

	/**
	 * Defines a locale-dependent attribute.
	 */
	private void setupAttribute1() {
		// Add a text short attribute
		this.attribute1 = new AttributeImpl();
		attribute1.setAttributeType(AttributeType.SHORT_TEXT);
		attribute1.setAttributeUsage(AttributeUsageImpl.PRODUCT_USAGE);
		attribute1.setLocaleDependant(true);
		attribute1.setKey(ATTRIBUTE_KEY_1);

		AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttribute(attribute1);
		attributeValue.setAttributeType(AttributeType.SHORT_TEXT);
		attributeValue.setValue(HELLO);
		attributeValueMap.put(ATTRIBUTE_KEY_1 + '_' + Locale.CANADA.getLanguage(), attributeValue);

		// This one mustn't be added to the map, because CANADA and US are having the same language.
		attributeValueMap.put(ATTRIBUTE_KEY_1 + '_' + Locale.US.getLanguage(), attributeValue);

		attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttribute(attribute1);
		attributeValue.setAttributeType(AttributeType.SHORT_TEXT);
		attributeValue.setValue(BONJOUR);
		attributeValueMap.put(ATTRIBUTE_KEY_1 + '_' + Locale.CANADA_FRENCH.getLanguage(), attributeValue);

		attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttribute(attribute1);
		attributeValue.setAttributeType(AttributeType.SHORT_TEXT);
		attributeValueMap.put(ATTRIBUTE_KEY_1 + '_' + CUSTOM_LOCALE, attributeValue);
	}

	/**
	 * Defines a locale-independent attribute and the value is given for that attribute.
	 */
	private void setupAttribute2() {
		// Add a date attribute
		this.attribute2 = new AttributeImpl();
		attribute2.setAttributeType(AttributeType.DATE);
		attribute2.setAttributeUsage(AttributeUsageImpl.PRODUCT_USAGE);
		attribute2.setKey(ATTRIBUTE_KEY_2);

		AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttribute(attribute2);
		attributeValue.setAttributeType(AttributeType.DATE);
		attributeValue.setValue(new Date());
		attributeValueMap.put(ATTRIBUTE_KEY_2, attributeValue);
	}

	/**
	 * Defines a locale-dependent attribute and the value is not given for that attribute.
	 */
	private void setupAttribute3() {
		// Add another text short attribute
		this.attribute3 = new AttributeImpl();
		attribute3.setAttributeType(AttributeType.SHORT_TEXT);
		attribute3.setAttributeUsage(AttributeUsageImpl.PRODUCT_USAGE);
		attribute3.setKey(ATTRIBUTE_KEY_3);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeValueGroupImpl.getAttributeValue(String, Locale)'.
	 */
	@Test
	public void testGetAttributeValue() {
		AttributeValue returnedValue = attributeValueGroup.getAttributeValue(ATTRIBUTE_KEY_1, Locale.CANADA);

		assertNotNull(returnedValue);
		assertEquals(HELLO, returnedValue.getStringValue());

		returnedValue = attributeValueGroup.getAttributeValue(ATTRIBUTE_KEY_1, Locale.CANADA_FRENCH);
		assertNotNull(returnedValue);
		assertEquals(BONJOUR, returnedValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeValueGroupImpl.getStringAttributeValue(String, Locale)'.
	 */
	@Test
	public void testGetStringAttributeValue() {
		assertNotNull(attributeValueGroup.getAttributeValue(ATTRIBUTE_KEY_1, Locale.CANADA));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeValueGroupImpl.setAttributeValue(String, Locale, String)'.
	 */
	@Test
	public void testSetAttributeValue() {
		final String attrValue = "some details in English";
		final String attrValueInFr = "some details in French";


		attributeValueGroup.setAttributeValue(this.attribute1, Locale.CANADA, attrValue);
		assertEquals(attrValue, attributeValueGroup.getStringAttributeValue(ATTRIBUTE_KEY_1, Locale.CANADA));

		attributeValueGroup.setAttributeValue(this.attribute1, Locale.CANADA_FRENCH, attrValueInFr);
		assertEquals(attrValueInFr, attributeValueGroup.getStringAttributeValue(ATTRIBUTE_KEY_1, Locale.CANADA_FRENCH));

		attributeValueGroup.setAttributeValue(this.attribute3, Locale.CANADA, attrValue);
		assertEquals(attrValue, attributeValueGroup.getStringAttributeValue(ATTRIBUTE_KEY_3, Locale.CANADA));

		attributeValueGroup.setAttributeValue(this.attribute3, Locale.CANADA_FRENCH, attrValueInFr);
		assertEquals(attrValueInFr, attributeValueGroup.getStringAttributeValue(ATTRIBUTE_KEY_3, Locale.CANADA_FRENCH));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeValueGroupImpl.getFullAttributeValues(Locale)'.
	 */
	@Test
	public void testGetFullAttributeValues() {
		Collection<Locale> supportedLocales = new ArrayList<Locale>();
		supportedLocales.add(Locale.US);
		supportedLocales.add(Locale.FRANCE);

		assertEquals(supportedLocales.size() + 2, attributeValueGroup.getFullAttributeValues(this.attributeGroup, supportedLocales).size());

		List<AttributeValue> fullAttributeValuesForLocaleCanada = attributeValueGroup.getFullAttributeValues(this.attributeGroup, Locale.CANADA);
		final int fullAttributeValuesNumberForLocaleCanada = 3;
		assertEquals(fullAttributeValuesNumberForLocaleCanada, fullAttributeValuesForLocaleCanada.size());

		List<AttributeValue> fullAttributeValuesForLocaleCanadaFrench =
			attributeValueGroup.getFullAttributeValues(this.attributeGroup, Locale.CANADA_FRENCH);
		final int fullAttributeValuesNumberForLocaleCanadaFrench = 3;
		assertEquals(fullAttributeValuesNumberForLocaleCanadaFrench, fullAttributeValuesForLocaleCanadaFrench.size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeValueGroupImpl.getAttributeValues(Locale)'.
	 */
	@Test
	public void testGetAttributeValues() {
		List<AttributeValue> attributeValuesForLocaleCanada = attributeValueGroup.getAttributeValues(this.attributeGroup, Locale.CANADA);
		final int attributeValuesNumberForLocaleCanada = 2;
		assertEquals(attributeValuesNumberForLocaleCanada, attributeValuesForLocaleCanada.size());

		List<AttributeValue> attributeValuesForLocaleJapan = attributeValueGroup.getAttributeValues(this.attributeGroup, Locale.JAPAN);
		final int attributeValuesNumberForLocaleJapan = 1;
		assertEquals(attributeValuesNumberForLocaleJapan, attributeValuesForLocaleJapan.size());
	}

	/**
		 * Test method for 'com.elasticpath.domain.impl.AttributeValueGroupImpl.setDefaultValues()'.
		 */
		@Test
		public void testInitialize() {
			attributeValueGroup = new AttributeValueGroupImpl(new ProductAttributeValueFactoryImpl());

			attributeValueGroup.initialize();

			assertNotNull(attributeValueGroup.getAttributeValueMap());
		}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeValueGroupImpl.setStringAttributeValue(String, Locale, String)'.
	 */
	@Test
	public void testSetStringAttributeValue() {
		final String attrValue = "some details in English";
		final String attrValueInFr = "some details in French";

		attributeValueGroup.setStringAttributeValue(this.attribute1, Locale.CANADA, attrValue);
		assertEquals(attrValue, attributeValueGroup.getStringAttributeValue(ATTRIBUTE_KEY_1, Locale.CANADA));

		attributeValueGroup.setStringAttributeValue(this.attribute1, Locale.CANADA_FRENCH, attrValueInFr);
		assertEquals(attrValueInFr, attributeValueGroup.getStringAttributeValue(ATTRIBUTE_KEY_1, Locale.CANADA_FRENCH));

		attributeValueGroup.setStringAttributeValue(this.attribute3, Locale.CANADA, attrValue);
		assertEquals(attrValue, attributeValueGroup.getStringAttributeValue(ATTRIBUTE_KEY_3, Locale.CANADA));

		attributeValueGroup.setStringAttributeValue(this.attribute3, Locale.CANADA_FRENCH, attrValueInFr);
		assertEquals(attrValueInFr, attributeValueGroup.getStringAttributeValue(ATTRIBUTE_KEY_3, Locale.CANADA_FRENCH));
	}

	/**
	 * Test that you can remove a locale-dependent attribute.
	 */
	@Test
	public void testRemoveByAttributeLocaleDependent() {
		Attribute attToRemove = new AttributeImpl();
		attToRemove.setKey(ATTRIBUTE_KEY_1);
		attToRemove.setLocaleDependant(true);

		assertEquals(ORIGINAL_COUNT, attributeValueGroup.getAttributeValueMap().size());
		assertNotNull(attributeValueGroup.getAttributeValue(ATTRIBUTE_KEY_1, Locale.CANADA));
		attributeValueGroup.removeByAttribute(attToRemove);
		final int numRemoved = 2;
		final int removedCount = ORIGINAL_COUNT - numRemoved;

		assertEquals(removedCount, attributeValueGroup.getAttributeValueMap().size());
		assertNull(attributeValueGroup.getAttributeValue(ATTRIBUTE_KEY_1, Locale.CANADA));
	}

	/**
	 * Test that you can remove a non-locale-dependent attribute.
	 */
	@Test
	public void testRemoveByAttributeNotLocaleDependent() {
		Attribute attToRemove = new AttributeImpl();
		attToRemove.setKey(ATTRIBUTE_KEY_1);

		assertEquals(ORIGINAL_COUNT, attributeValueGroup.getAttributeValueMap().size());
		attributeValueGroup.removeByAttribute(attToRemove);
		assertEquals(ORIGINAL_COUNT, attributeValueGroup.getAttributeValueMap().size());

		attToRemove.setKey(ATTRIBUTE_KEY_2);
		final int removedCount = ORIGINAL_COUNT - 1;
		attributeValueGroup.removeByAttribute(attToRemove);
		assertEquals(removedCount, attributeValueGroup.getAttributeValueMap().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeValueGroupImpl.removeByAttributes(Set)'.
	 */
	@Test
	public void testRemoveByAttributes() {
		Attribute attToRemove1 = new AttributeImpl();
		attToRemove1.setKey(ATTRIBUTE_KEY_1);
		attToRemove1.setLocaleDependant(true);
		Attribute attToRemove2 = new AttributeImpl();
		attToRemove2.setKey(ATTRIBUTE_KEY_2);
		Set<Attribute> toRemove = new HashSet<Attribute>();
		toRemove.add(attToRemove1);
		toRemove.add(attToRemove2);

		assertEquals(ORIGINAL_COUNT, attributeValueGroup.getAttributeValueMap().size());
		attributeValueGroup.removeByAttributes(toRemove);
		final int numRemoved = 3;
		final int removedCount = ORIGINAL_COUNT - numRemoved;
		assertEquals(removedCount, attributeValueGroup.getAttributeValueMap().size());
	}

	/**
	 * Test that getAttributeWithoutFallback broadens the search for an attribute
	 * if it doesn't find one for a Locale with a (language, country, variant). It
	 * should look again for one with just the (language, country) and then
	 * again for just (language). If it doesn't find any of them, it should return null.
	 */
	@Test
	public void testGetAttributeWithoutFallbackBroadensLocale() {
		final String language = "en";
		final String country = "US";
		final String variant = "va";
		Locale variantLocale = new Locale(language, country, variant);
		Locale countryLocale = new Locale(language, country);
		Locale languageLocale = new Locale(language);

		AttributeValue languageValue = new ProductAttributeValueImpl();
		languageValue.setUidPk(1L);
		AttributeValue countryValue = new ProductAttributeValueImpl();
		countryValue.setUidPk(2L);
		AttributeValue variantValue = new ProductAttributeValueImpl();
		final long three = 3L;
		variantValue.setUidPk(three);

		final Map<String, AttributeValue> allThreeMap = new HashMap<String, AttributeValue>();
		allThreeMap.put("_" + languageLocale.toString(), languageValue);
		allThreeMap.put("_" + countryLocale.toString(), countryValue);
		allThreeMap.put("_" + variantLocale.toString(), variantValue);

		final Map<String, AttributeValue> languageAndCountryMap = new HashMap<String, AttributeValue>();
		languageAndCountryMap.put("_" + languageLocale.toString(), languageValue);
		languageAndCountryMap.put("_" + countryLocale.toString(), countryValue);

		final Map<String, AttributeValue> languageMap = new HashMap<String, AttributeValue>();
		languageMap.put("_" + languageLocale.toString(), languageValue);

		AttributeValueGroupImpl avg = new AttributeValueGroupImpl(new ProductAttributeValueFactoryImpl());
		avg.setAttributeValueMap(allThreeMap);
		//If all three are in the map, should return the variant when asked for it (most specific)
		assertEquals("If all three are in the map, should return the variant when asked for it (most specific)",
				variantValue.getUidPk(), avg.getAttributeValueWithoutFallBack("", variantLocale).getUidPk());
		//If all three are in the map, should return the country when asked for it (most specific)
		assertEquals("If all three are in the map, should return the country when asked for it (most specific)",
				countryValue.getUidPk(), avg.getAttributeValueWithoutFallBack("", countryLocale).getUidPk());
		//If all three are in the map, should return the language when asked for it (most specific)
		assertEquals("If all three are in the map, should return the language when asked for it (most specific)",
				languageValue.getUidPk(), avg.getAttributeValueWithoutFallBack("", languageLocale).getUidPk());

		avg.setAttributeValueMap(languageAndCountryMap);
		//If only language and country are in the map, should return the country when asked for the variant (falls back to country)
		assertEquals("If only language and country are in the map, should return the country when asked for the variant (falls back to country)",
				countryValue.getUidPk(), avg.getAttributeValueWithoutFallBack("", variantLocale).getUidPk());

		avg.setAttributeValueMap(languageMap);
		//If only language is in map, should return the language when asked for variant
		assertEquals("If only language is in map, should return the language when asked for variant",
				languageValue.getUidPk(), avg.getAttributeValueWithoutFallBack("", variantLocale).getUidPk());
		//If only language is in map, should return the language when asked for country
		assertEquals("If only language is in map, should return the language when asked for variant",
				languageValue.getUidPk(), avg.getAttributeValueWithoutFallBack("", countryLocale).getUidPk());
	}

	/**
	 * Test that passing in a locale to retrieve a NON-locale-dependent attribute value
	 * will still return the attribute value when it doesn't find a matching key in
	 * the given locale.
	 */
	@Test
	public void testGetNonLocaleDependentAttributeValueWithLocale() {
		//Create an attribute value
		AttributeValue nonLocaleDependentAttributeValue = new ProductAttributeValueImpl();
		nonLocaleDependentAttributeValue.setUidPk(1L);
		final String nonLocaleDependentAttributeKey = "myAttributeKey";
		final String nonLocaleDependentAttributeValueString = "myAttributeValueString";
		nonLocaleDependentAttributeValue.setAttributeType(AttributeType.LONG_TEXT);
		nonLocaleDependentAttributeValue.setLocalizedAttributeKey(nonLocaleDependentAttributeKey);
		nonLocaleDependentAttributeValue.setStringValue(nonLocaleDependentAttributeValueString);
		//put the attribute value in a map
		final Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		attributeValueMap.put(nonLocaleDependentAttributeKey, nonLocaleDependentAttributeValue);
		//create a new AVG
		AttributeValueGroupImpl avg = new AttributeValueGroupImpl(new ProductAttributeValueFactoryImpl());
		//Put the map in the AVG
		avg.setAttributeValueMap(attributeValueMap);
		//Test
		assertEquals("Passing in a locale to retrieve a NON-locale-dependent attribute value should not return null.",
				nonLocaleDependentAttributeValueString, avg.getAttributeValue(nonLocaleDependentAttributeKey, Locale.GERMAN).getStringValue());
	}
}
