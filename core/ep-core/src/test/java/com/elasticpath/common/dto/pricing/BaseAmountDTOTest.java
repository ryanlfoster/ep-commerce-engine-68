package com.elasticpath.common.dto.pricing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

/**
 *	Tests BaseAmountDTO methods. 
 */
@SuppressWarnings("PMD.UseAssertEqualsInsteadOfAssertTrue")
public class BaseAmountDTOTest {

	private static final String TYPE = "type";
	private static final String GUID = "guid";

	/**
	 * Tests equals() with same objects.
	 */
	@Test
	public void testEqualsSameObject() {
		BaseAmountDTO baseAmount1 = new BaseAmountDTO();
		assertTrue(baseAmount1.equals(baseAmount1));
	}

	/**
	 * Tests equals() with not same objects.
	 */
	@Test
	public void testEqualsDifferentObjectType() {
		BaseAmountDTO baseAmount1 = new BaseAmountDTO();
		assertFalse(baseAmount1.equals(new Object()));
	}

	/**
	 * Tests equals() when one of the object is null.
	 */
	@Test
	public void testEqualsNull() {
		BaseAmountDTO baseAmount1 = new BaseAmountDTO();
		final Object nullObj = null;
		assertFalse(baseAmount1.equals(nullObj));
	}

	/**
	 * Tests equals() without pricing provided.
	 */
	@Test
	public void testEqualsWithoutPrices() {
		BaseAmountDTO baseAmount2 = new BaseAmountDTO();
		baseAmount2.setGuid(GUID);
		baseAmount2.setObjectType(TYPE);
		baseAmount2.setQuantity(BigDecimal.ONE);
		BaseAmountDTO baseAmount1 = new BaseAmountDTO();
		baseAmount1.setGuid(GUID);
		baseAmount1.setObjectType(TYPE);
		baseAmount1.setQuantity(BigDecimal.ONE);
		assertTrue(baseAmount1.equals(baseAmount2));
	}

	/**
	 * Tests equals() with pricing provided.
	 */
	@Test
	public void testEqualsWithPrices() {
		BaseAmountDTO baseAmount2 = new BaseAmountDTO();
		baseAmount2.setObjectGuid(GUID);
		baseAmount2.setObjectType(TYPE);
		baseAmount2.setQuantity(BigDecimal.ONE);
		baseAmount2.setListValue(BigDecimal.TEN);
		BaseAmountDTO baseAmount1 = new BaseAmountDTO();
		baseAmount1.setObjectGuid(GUID);
		baseAmount1.setObjectType(TYPE);
		baseAmount1.setQuantity(BigDecimal.ONE);
		baseAmount1.setListValue(BigDecimal.TEN);
		assertTrue(baseAmount1.equals(baseAmount2));
	}

	/**
	 * Tests equals() with different pricing for tho objects provided.
	 */
	@Test
	public void testNotEqualsWithPrices() {
		BaseAmountDTO baseAmount2 = new BaseAmountDTO();
		baseAmount2.setObjectGuid(GUID);
		baseAmount2.setObjectType(TYPE);
		baseAmount2.setQuantity(BigDecimal.ONE);
		baseAmount2.setListValue(BigDecimal.TEN);
		baseAmount2.setSaleValue(BigDecimal.TEN);
		BaseAmountDTO baseAmount1 = new BaseAmountDTO();
		baseAmount1.setObjectGuid(GUID);
		baseAmount1.setObjectType(TYPE);
		baseAmount1.setQuantity(BigDecimal.ONE);
		baseAmount1.setListValue(BigDecimal.TEN);
		assertFalse(baseAmount1.equals(baseAmount2));
	}

	/**
	 * Tests equals() when all fields of the objects are equal except their guids.
	 */
	@Test
	public void testNotEqualsWithSameFieldsAndDifferentGuids() {
		BaseAmountDTO baseAmount2 = new BaseAmountDTO();
		baseAmount2.setObjectGuid(GUID);
		baseAmount2.setGuid(GUID);
		baseAmount2.setObjectType(TYPE);
		baseAmount2.setQuantity(BigDecimal.ONE);
		baseAmount2.setListValue(BigDecimal.TEN);
		baseAmount2.setSaleValue(BigDecimal.TEN);	
		BaseAmountDTO baseAmount1 = new BaseAmountDTO();
		baseAmount1.setObjectGuid(GUID);
		baseAmount1.setGuid(GUID + "2");
		baseAmount1.setObjectType(TYPE);
		baseAmount1.setQuantity(BigDecimal.ONE);
		baseAmount1.setListValue(BigDecimal.TEN);
		baseAmount1.setSaleValue(BigDecimal.TEN);
		assertFalse(baseAmount1.equals(baseAmount2));
	}

	/**
	 * Tests equals() when all fields of the objects are equal.
	 */
	@Test
	public void testEqualsWithListPricesAndSameGuids() {
		BaseAmountDTO baseAmount2 = new BaseAmountDTO();
		baseAmount2.setObjectGuid(GUID);
		baseAmount2.setGuid(GUID);
		baseAmount2.setObjectType(TYPE);
		baseAmount2.setQuantity(BigDecimal.ONE);
		baseAmount2.setListValue(BigDecimal.TEN);
		baseAmount2.setSaleValue(BigDecimal.TEN);
		BaseAmountDTO baseAmount1 = new BaseAmountDTO();
		baseAmount1.setObjectGuid(GUID);
		baseAmount1.setGuid(GUID);
		baseAmount1.setObjectType(TYPE);
		baseAmount1.setQuantity(BigDecimal.ONE);
		baseAmount1.setListValue(BigDecimal.TEN);
		baseAmount1.setSaleValue(BigDecimal.TEN);
		assertTrue(baseAmount1.equals(baseAmount2));
	}

	
}
