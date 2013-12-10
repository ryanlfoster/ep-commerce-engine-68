package com.elasticpath.domain.pricing.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * Tests the {@code BaseAmountImpl} class.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class BaseAmountImplTest {

	/**	 */
	@Test
	public void testEquals() {
		BaseAmountImpl baseAmount1 = new BaseAmountImpl("GUID1", "GUID2", "PRODUCT", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE, "GUID3");
		BaseAmountImpl baseAmount2 = new BaseAmountImpl("GUID1", "GUID2", "PRODUCT", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE, "GUID3");
		assertEquals(baseAmount1, baseAmount2);
	}
}
