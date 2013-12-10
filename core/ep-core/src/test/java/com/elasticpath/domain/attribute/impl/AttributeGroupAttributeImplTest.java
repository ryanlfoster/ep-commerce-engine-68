package com.elasticpath.domain.attribute.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;

/**
 * Test <code>AttributeGroupAttributeImpl</code>.
 */
public class AttributeGroupAttributeImplTest {
	private AttributeGroupAttributeImpl productTypeAttribute1;
	private AttributeGroupAttributeImpl productTypeAttribute2;

	@Before
	public void setUp() throws Exception {
		this.productTypeAttribute1 = new AttributeGroupAttributeImpl();
		this.productTypeAttribute2 = new AttributeGroupAttributeImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeGroupAttributeImpl.getOrdering()'.
	 */
	@Test
	public void testGetOrdering() {
		assertEquals(0, productTypeAttribute1.getOrdering());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeGroupAttributeImpl.getAttribute()'.
	 */
	@Test
	public void testGetAttribute() {
		assertNull(productTypeAttribute1.getAttribute());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeGroupAttributeImpl.setAttribute(Attribute)'.
	 */
	@Test
	public void testSetAttribute() {
		final Attribute attributeImpl = new AttributeImpl();
		productTypeAttribute1.setAttribute(attributeImpl);
		assertEquals(attributeImpl, productTypeAttribute1.getAttribute());		
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeGroupAttributeImpl.setOrdering(int)'.
	 */
	@Test
	public void testSetOrdering() {
		final int ordering = Integer.MAX_VALUE;
		productTypeAttribute1.setOrdering(ordering);
		assertEquals(ordering, productTypeAttribute1.getOrdering());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.impl.AttributeGroupAttributeImplTest.compareTo(Object)'.
	 */
	@Test
	public void testCompareToByOrderingNumber() {
		final int ordering1 = Integer.MAX_VALUE;
		final int ordering2 = Integer.MIN_VALUE;
		productTypeAttribute1.setOrdering(ordering1);
		productTypeAttribute2.setOrdering(ordering2);
		assertTrue(productTypeAttribute1.compareTo(productTypeAttribute2) > 0);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.impl.AttributeGroupAttributeImplTest.compareTo(Object)'.
	 */
	@Test
	public void testCompareToByAttributeName() {
		final int ordering = Integer.MAX_VALUE;
		productTypeAttribute1.setOrdering(ordering);
		productTypeAttribute2.setOrdering(ordering);

		final Attribute attribute1 = new AttributeImpl();
		attribute1.setName("key1");
		productTypeAttribute1.setAttribute(attribute1);

		final Attribute attribute2 = new AttributeImpl();
		attribute2.setName("key2");
		productTypeAttribute2.setAttribute(attribute2);

		assertTrue(productTypeAttribute1.compareTo(productTypeAttribute2) < 0);
	}
}
