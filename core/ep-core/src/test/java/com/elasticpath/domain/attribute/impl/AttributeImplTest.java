/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.attribute.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test <code>AttributeImpl</code>.
 */
public class AttributeImplTest extends AbstractEPTestCase {
	private static final String TEST_ATTRIBUTE_NAME_2 = "test attribute 2";

	private static final String TEST_ATTRIBUTE_NAME_1 = "test attribute 1";

	private static final String TEST_ATTRIBUTE_NAME = "test attribute";

	private static final long UID_PK_1 = 1;

	private static final long UID_PK_2 = 9999;

	private static final String TEST_ATTRIBUTE_KEY2 = "test key 2";

	private static final String TEST_ATTRIBUTE_KEY1 = "test key 1";

	private static final String TEST_ATTRIBUTE_KEY = "test key";

	private AttributeImpl attributeImpl1;

	private AttributeImpl attributeImpl2;


	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of error
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		stubGetBean(ContextIdNames.ATTRIBUTE_USAGE, AttributeUsageImpl.class);
        
		attributeImpl1 = new AttributeImpl();
		attributeImpl2 = new AttributeImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.getKey()'.
	 */
	@Test
	public void testGetKey() {
		assertNotNull(attributeImpl1.getKey());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.setKey(String)'.
	 */
	@Test
	public void testSetKey() {
		final String key1 = "key1";
		attributeImpl1.setKey(key1);
		assertSame(key1, attributeImpl1.getKey());
		assertSame(key1, attributeImpl1.getGuid());

		final String key2 = "key2";
		attributeImpl1.setGuid(key2);
		assertSame(key2, attributeImpl1.getKey());
		assertSame(key2, attributeImpl1.getGuid());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.isLocaleDependant()'.
	 */
	@Test
	public void testIsLocaleDependant() {
		assertFalse(attributeImpl1.isLocaleDependant());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.setLocaleDependant(boolean)'.
	 */
	@Test
	public void testSetLocaleDependant() {
		attributeImpl1.setLocaleDependant(true);
		assertTrue(attributeImpl1.isLocaleDependant());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.getAttributeType()'.
	 */
	@Test
	public void testGetAttributeType() {
		assertNull(attributeImpl1.getAttributeType());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.setAttributeType(AttributeType)'.
	 */
	@Test
	public void testSetAttributeType() {
		attributeImpl1.setAttributeType(AttributeType.SHORT_TEXT);
		assertSame(AttributeType.SHORT_TEXT, attributeImpl1.getAttributeType());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.getName()'.
	 */
	@Test
	public void testGetName() {
		assertEquals("", attributeImpl1.getName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.setName(String)'.
	 */
	@Test
	public void testSetName() {
		final String name = "name";
		attributeImpl1.setName(name);
		assertSame(name, attributeImpl1.getName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.compareTo()'.
	 */
	@Test
	public void testCompareTo() {

		// New categories are always dealed as the same
		assertEquals(0, attributeImpl1.compareTo(attributeImpl2));

		// compare by name
		attributeImpl1.setName(TEST_ATTRIBUTE_NAME_1);
		attributeImpl2.setName(TEST_ATTRIBUTE_NAME_2);
		assertTrue(attributeImpl1.compareTo(attributeImpl2) < 0);

		// compare by key
		attributeImpl1.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl2.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl1.setKey(TEST_ATTRIBUTE_KEY1);
		attributeImpl2.setKey(TEST_ATTRIBUTE_KEY2);
		assertTrue(attributeImpl1.compareTo(attributeImpl2) < 0);

		// compare by uid
		attributeImpl1.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl2.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl1.setKey(TEST_ATTRIBUTE_KEY);
		attributeImpl2.setKey(TEST_ATTRIBUTE_KEY);
		attributeImpl1.setUidPk(UID_PK_1);
		attributeImpl2.setUidPk(UID_PK_2);
		assertTrue(attributeImpl1.compareTo(attributeImpl2) < 0);

		// compare the same one
		attributeImpl1.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl2.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl1.setKey(TEST_ATTRIBUTE_KEY);
		attributeImpl2.setKey(TEST_ATTRIBUTE_KEY);
		attributeImpl1.setUidPk(UID_PK_1);
		attributeImpl2.setUidPk(UID_PK_1);
		assertEquals(0, attributeImpl1.compareTo(attributeImpl2));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.isRequired()'.
	 */
	@Test
	public void testIsRequired() {
		assertFalse(attributeImpl1.isRequired());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.setRequired(boolean)'.
	 */
	@Test
	public void testSetRequired() {
		attributeImpl1.setRequired(true);
		assertTrue(attributeImpl1.isRequired());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.isValueLookupEnabled()'.
	 */
	@Test
	public void testValueLookupEnabled() {
		assertFalse(attributeImpl1.isValueLookupEnabled());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.setValueLookupEnabled(boolean)'.
	 */
	@Test
	public void testSetValueLookupEnabled() {
		attributeImpl1.setValueLookupEnabled(true);
		assertTrue(attributeImpl1.isValueLookupEnabled());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.getAttributeUsage()'.
	 */
	@Test
	public void testGetAttributeUsage() {
		assertNull(attributeImpl1.getAttributeUsage());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.setAttributeUsage(AttributeUsage)'.
	 */
	@Test
	public void testSetAttributeUsage() {
		AttributeUsage attributeUsage = AttributeUsageImpl.getAttributeUsageByIdInternal(1);
		attributeImpl1.setAttributeUsage(attributeUsage);
		assertSame(attributeUsage, attributeImpl1.getAttributeUsage());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.isMultiValueEnabled()'.
	 */
	@Test
	public void testIsMultiValueEnabled() {
		assertFalse(attributeImpl1.isMultiValueEnabled());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.setMultiValueEnabled(multiValueEnabled)'.
	 */
	@Test
	public void testSetMultiValueEnabled() {
		attributeImpl1.setMultiValueEnabled(true);
		assertTrue(attributeImpl1.isMultiValueEnabled());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.equals(object)'.
	 */
	@Test
	public void testEquals() {
		// New attributes are equal
		assertEquals(true, attributeImpl1.equals(attributeImpl2));

		// compare the same one
		attributeImpl1.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl2.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl1.setKey(TEST_ATTRIBUTE_KEY);
		attributeImpl2.setKey(TEST_ATTRIBUTE_KEY);
		attributeImpl1.setUidPk(UID_PK_1);
		attributeImpl2.setUidPk(UID_PK_1);
		assertEquals(true, attributeImpl1.equals(attributeImpl2));

		// uidpk should be ignored
		attributeImpl1.setLocaleDependant(true);
		attributeImpl2.setLocaleDependant(true);
		attributeImpl1.setMultiValueEnabled(true);
		attributeImpl2.setMultiValueEnabled(true);
		attributeImpl1.setRequired(true);
		attributeImpl2.setRequired(true);
		attributeImpl1.setSystem(true);
		attributeImpl2.setSystem(true);
		attributeImpl1.setValueLookupEnabled(true);
		attributeImpl2.setValueLookupEnabled(true);
		attributeImpl1.setAttributeType(AttributeType.SHORT_TEXT);
		attributeImpl2.setAttributeType(AttributeType.SHORT_TEXT);
		attributeImpl1.setAttributeUsageId(1);
		attributeImpl2.setAttributeUsageId(1);
		attributeImpl1.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl2.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl1.setKey(TEST_ATTRIBUTE_KEY);
		attributeImpl2.setKey(TEST_ATTRIBUTE_KEY);
		attributeImpl1.setUidPk(UID_PK_1);
		attributeImpl2.setUidPk(0);
		assertEquals(true, attributeImpl1.equals(attributeImpl2));

		// symmetric
		assertEquals(true, attributeImpl2.equals(attributeImpl1));

		// equals itself
		assertEquals(true, attributeImpl1.equals(attributeImpl1));

		attributeImpl1.setKey(TEST_ATTRIBUTE_KEY1);
		assertFalse(attributeImpl1.equals(attributeImpl2));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeImpl.hashCode())'.
	 */
	@Test
	public void testHashCode() {
		// needs a mock because setAttributeTypeId and setAttributeSu
		// New attributes are equal
		assertEquals(attributeImpl1.hashCode(), attributeImpl2.hashCode());

		// compare the same one
		attributeImpl1.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl2.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl1.setKey(TEST_ATTRIBUTE_KEY);
		attributeImpl2.setKey(TEST_ATTRIBUTE_KEY);
		attributeImpl1.setUidPk(UID_PK_1);
		attributeImpl2.setUidPk(UID_PK_1);
		assertEquals(attributeImpl1.hashCode(), attributeImpl2.hashCode());

		// uidpk should be ignored
		attributeImpl1.setLocaleDependant(true);
		attributeImpl2.setLocaleDependant(true);
		attributeImpl1.setMultiValueEnabled(true);
		attributeImpl2.setMultiValueEnabled(true);
		attributeImpl1.setRequired(true);
		attributeImpl2.setRequired(true);
		attributeImpl1.setSystem(true);
		attributeImpl2.setSystem(true);
		attributeImpl1.setValueLookupEnabled(true);
		attributeImpl2.setValueLookupEnabled(true);
		attributeImpl1.setAttributeType(AttributeType.SHORT_TEXT);
		attributeImpl2.setAttributeType(AttributeType.SHORT_TEXT);
		attributeImpl1.setAttributeUsageId(1);
		attributeImpl2.setAttributeUsageId(1);
		attributeImpl1.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl2.setName(TEST_ATTRIBUTE_NAME);
		attributeImpl1.setKey(TEST_ATTRIBUTE_KEY);
		attributeImpl2.setKey(TEST_ATTRIBUTE_KEY);
		attributeImpl1.setUidPk(UID_PK_1);
		attributeImpl2.setUidPk(0);
		assertEquals(attributeImpl1.hashCode(), attributeImpl2.hashCode());

		// equals itself
		assertEquals(attributeImpl1.hashCode(), attributeImpl1.hashCode());

		attributeImpl1.setKey(TEST_ATTRIBUTE_KEY1);
		assertNotSame(attributeImpl1.hashCode(), attributeImpl2.hashCode());
	}

}
