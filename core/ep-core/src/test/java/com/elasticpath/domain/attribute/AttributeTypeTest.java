package com.elasticpath.domain.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Test;

/**
 * Test for AttributeType class.
 *
 */
public class AttributeTypeTest {

	/**
	 * Test for getStorageType.
	 */
	@Test
	public void testGetStorageType() {
		assertEquals("selfDerivedValue", AttributeType.getStorageType("SELF_DERIVED"));
		assertEquals("longTextValue", AttributeType.LONG_TEXT.getStorageType());
		assertEquals("integerValue", AttributeType.INTEGER.getStorageType());
		assertEquals("decimalValue", AttributeType.DECIMAL.getStorageType());
		assertEquals("booleanValue", AttributeType.BOOLEAN.getStorageType());
		assertEquals("shortTextValue", AttributeType.SHORT_TEXT.getStorageType());
		assertEquals("dateValue", AttributeType.DATE.getStorageType());
	}

	/**
	 * Test for getnameMessageKey.
	 */
	@Test
	public void testGetNameMessageKey() {
		assertEquals("AttributeType_AreNowJustEnums", AttributeType.getNameMessageKey("ARE_NOW_JUST_ENUMS"));
		assertEquals("AttributeType_ShortText", AttributeType.SHORT_TEXT.getNameMessageKey());
		assertEquals("AttributeType_LongText", AttributeType.LONG_TEXT.getNameMessageKey());
		assertEquals("AttributeType_Integer", AttributeType.INTEGER.getNameMessageKey());
		assertEquals("AttributeType_Decimal", AttributeType.DECIMAL.getNameMessageKey());
		assertEquals("AttributeType_Boolean", AttributeType.BOOLEAN.getNameMessageKey());
		assertEquals("AttributeType_Image", AttributeType.IMAGE.getNameMessageKey());
		assertEquals("AttributeType_File", AttributeType.FILE.getNameMessageKey());
		assertEquals("AttributeType_Date", AttributeType.DATE.getNameMessageKey());
		assertEquals("AttributeType_DateTime", AttributeType.DATETIME.getNameMessageKey());
	}

	/**
	 * Test for constantToCamelCase.
	 */
	@Test
	public void testConstantToCamelCase() {
		assertEquals("", AttributeType.constantToCamelCase(""));
		assertEquals("", AttributeType.constantToCamelCase("_"));
		assertEquals("CamelsAreFunny", AttributeType.constantToCamelCase("CAMELS_ARE_FUNNY"));
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getAttributeTypes()'.
	 */
	@Test
	public void testGetAllAttributeTypes() {
		Collection<AttributeType> attributeTypes = AttributeType.values();
		assertNotNull(attributeTypes.iterator().next());

		final int noOfTypes = 9;
		assertEquals(noOfTypes, attributeTypes.size());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getCustomerAttributeTypes()'.
	 */
	@Test
	public void testGetCustomerAttributeTypes() {
		AttributeType[] customerAttributeTypes = AttributeType.getCustomerAttributeTypes();
		assertNotNull(customerAttributeTypes[0]);

		final int noOfTypes = 6;
		assertEquals(noOfTypes, customerAttributeTypes.length); // 6 types
	}
}
