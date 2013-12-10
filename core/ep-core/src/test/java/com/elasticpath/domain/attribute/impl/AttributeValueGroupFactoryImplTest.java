package com.elasticpath.domain.attribute.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueFactory;
import com.elasticpath.domain.attribute.AttributeValueGroup;

/**
 * Tests the AttributeValueGroupFactoryImpl class.
 */
public class AttributeValueGroupFactoryImplTest {
	private static final String NAME = "name";

	private AttributeValueGroupFactoryImpl valueGroupFactory;
	private AttributeValueFactory valueFactory;
	private Map<String, AttributeValue> attributeMap;
	private AttributeImpl nameAttribute;

	/**
	 * Test set up.
	 */
	@Before
	public void setUp() {
		nameAttribute = new AttributeImpl();
		nameAttribute.setKey(NAME);
		nameAttribute.setAttributeType(AttributeType.SHORT_TEXT);

		valueFactory = new ProductAttributeValueFactoryImpl();
		valueGroupFactory = new AttributeValueGroupFactoryImpl(valueFactory);
		attributeMap = new HashMap<String, AttributeValue>();
	}

	/**
	 * Tests that attribute value groups are created.
	 */
	@Test
	public void testAttributeValueGroupCreation() {
		AttributeValueGroup group = valueGroupFactory.createAttributeValueGroup();
		group.setStringAttributeValue(nameAttribute, null, "foo");

		AttributeValue nameValue = group.getAttributeValueMap().get(NAME);
		assertTrue("Value Factory set properly",
				nameValue instanceof ProductAttributeValueImpl);
	}

	/**
	 * Tests the factory with a map param.
	 */
	@Test
	public void testAttributeValueGroupCreationWithMap() {
		final AttributeValue nameValue = valueFactory.createAttributeValue(nameAttribute, NAME);
		nameValue.setValue("bar");
		attributeMap.put(NAME, nameValue);

		AttributeValueGroup group = valueGroupFactory.createAttributeValueGroup(attributeMap);
		assertEquals("Group initialized with starting map",
				"bar", group.getStringAttributeValue(NAME, null));
	}

	/**
	 * Test the factory method with a (null) map param.
	 */
	@Test
	public void testAttributeValueGroupCreationWithNullMap() {
		AttributeValueGroup group = valueGroupFactory.createAttributeValueGroup(null);
		group.setStringAttributeValue(nameAttribute, null, "foo");

		AttributeValue nameValue = group.getAttributeValueMap().get(NAME);
		assertTrue("Value Factory set properly",
				nameValue instanceof ProductAttributeValueImpl);
	}
}
