/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.attribute.impl;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.elasticpath.domain.attribute.AttributeUsage;

/**
 * Test <code>AttributeUsageImpl</code>.
 */
public class AttributeUsageImplTest {

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeUsageImpl.getValue()'.
	 */
	@Test
	public void testGetValue() {
		AttributeUsage attributeUsage = new AttributeUsageImpl();
		assertEquals(0, attributeUsage.getValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeUsageImpl.setValue(int)'.
	 */
	@Test
	public void testSetValue() {
		final int value = 1;
		AttributeUsage attributeUsage = new AttributeUsageImpl();
		attributeUsage.setValue(value);
		assertEquals(value, attributeUsage.getValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeUsageImpl.setValue(int)'.
	 */
	@Test
	public void testGetAttributeUsageMap() {
		Map<String, String> usages = AttributeUsageImpl.getAttributeUsageMapInternal();
		final int expectedSize = 4;
		assertEquals("Currently only 4 types of usages.", expectedSize, usages.size());
		assertEquals("Sanity check", "Category", usages.get(String.valueOf(AttributeUsage.CATEGORY)));
	}
}
