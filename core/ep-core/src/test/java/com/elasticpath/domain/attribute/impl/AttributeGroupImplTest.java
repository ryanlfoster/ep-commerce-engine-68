package com.elasticpath.domain.attribute.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;

/**
 * Test <code>AttributeGroupImpl</code>.
 */
public class AttributeGroupImplTest {

	private AttributeGroupImpl attributeGroupImpl;

	@Before
	public void setUp() throws Exception {
		this.attributeGroupImpl = new AttributeGroupImpl();
	}

	/**
	 * Test that the AttributeGroupAttributes is an empty collection to start with.
	 */
	@Test
	public void testGetAttributeGroupAttributes() {
		assertTrue(attributeGroupImpl.getAttributeGroupAttributes().isEmpty());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeGroupImpl.setAttributeGroupAttributes(Set)'.
	 */
	@Test
	public void testSetAttributeGroupAttributes() {
		final Set<AttributeGroupAttribute> attributes = new HashSet<AttributeGroupAttribute>();
		attributeGroupImpl.setAttributeGroupAttributes(attributes);
		assertSame(attributes, attributeGroupImpl.getAttributeGroupAttributes());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeGroupImpl.addAttributeGroupAttribute(AttributeGroupAttribute)'.
	 */
	@Test
	public void testAddAttributeGroupAttribute() {
		final Attribute attribute = new AttributeImpl();
		final AttributeGroupAttribute attributeGroupAttribute = new AttributeGroupAttributeImpl();
		attributeGroupAttribute.setAttribute(attribute);
		this.attributeGroupImpl.addAttributeGroupAttribute(attributeGroupAttribute);
		assertTrue(this.attributeGroupImpl.getAttributeGroupAttributes().contains(attributeGroupAttribute));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AttributeGroupImpl.getRemovedAttributes(Set)'.
	 */
	@Test
	public void testGetRemovedAttributes() {
		AttributeGroupAttribute aga1 = getAGA("key1");
		AttributeGroupAttribute aga2 = getAGA("key2");
		
		assertEquals(0, attributeGroupImpl.getRemovedAttributes(null).size());
		assertEquals(0, attributeGroupImpl.getRemovedAttributes(new HashSet<AttributeGroupAttribute>()).size());
		
		Set<AttributeGroupAttribute> before = new HashSet<AttributeGroupAttribute>();
		before.add(aga1);
		assertEquals(1, attributeGroupImpl.getRemovedAttributes(before).size());
		assertTrue(attributeGroupImpl.getRemovedAttributes(before).contains(aga1.getAttribute()));

		Set<AttributeGroupAttribute> both = new HashSet<AttributeGroupAttribute>();
		both.add(aga1);
		both.add(aga2);
		
		attributeGroupImpl.setAttributeGroupAttributes(new HashSet<AttributeGroupAttribute>());
		assertEquals(2, attributeGroupImpl.getRemovedAttributes(both).size());
		assertTrue(attributeGroupImpl.getRemovedAttributes(both).contains(aga1.getAttribute()));
		assertTrue(attributeGroupImpl.getRemovedAttributes(both).contains(aga2.getAttribute()));
		
		attributeGroupImpl.setAttributeGroupAttributes(before);
		assertEquals(1, attributeGroupImpl.getRemovedAttributes(both).size());
		assertTrue(attributeGroupImpl.getRemovedAttributes(both).contains(aga2.getAttribute()));
		
		attributeGroupImpl.setAttributeGroupAttributes(both);
		assertEquals(0, attributeGroupImpl.getRemovedAttributes(null).size());
		assertEquals(0, attributeGroupImpl.getRemovedAttributes(new HashSet<AttributeGroupAttribute>()).size());
		assertEquals(0, attributeGroupImpl.getRemovedAttributes(before).size());
	}
	
	private AttributeGroupAttribute getAGA(final String key) {
		AttributeGroupAttribute aga = new AttributeGroupAttributeImpl();
		Attribute att = new AttributeImpl();
		att.setKey(key);
		aga.setAttribute(att);
		return aga;
	}
}
