package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link OrderTaxValueImpl}.
 */
public class OrderTaxValueImplTest {
	
	private static final String TAX_CATEGORY_NAME = "TC1";
	private static final String TAX_CATEGORY_DISPLAY_NAME = "Tax Category 1";
	private static final BigDecimal TAX_VALUE = new BigDecimal("5.50");
	
	private OrderTaxValueImpl orderTaxValueImpl;
	
	/**
	 * Prepare for the tests.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		this.orderTaxValueImpl = new OrderTaxValueImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderTaxValue.getTaxCategoryName()'.
	 */
	@Test
	public void testGetSetTaxCategoryName() {
		assertNull(this.orderTaxValueImpl.getTaxCategoryName());
		
		this.orderTaxValueImpl.setTaxCategoryName(TAX_CATEGORY_NAME);
		assertEquals(TAX_CATEGORY_NAME, this.orderTaxValueImpl.getTaxCategoryName());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderTaxValue.getTaxCategoryDisplayName()'.
	 */
	@Test
	public void testGetSetTaxCategoryDisplayName() {
		assertNull(this.orderTaxValueImpl.getTaxCategoryDisplayName());
		
		this.orderTaxValueImpl.setTaxCategoryDisplayName(TAX_CATEGORY_DISPLAY_NAME);
		assertEquals(TAX_CATEGORY_DISPLAY_NAME, this.orderTaxValueImpl.getTaxCategoryDisplayName());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderTaxValue.getTaxValue()'.
	 */
	@Test
	public void testGetSetTaxValue() {
		assertNull(this.orderTaxValueImpl.getTaxValue());
		
		this.orderTaxValueImpl.setTaxValue(TAX_VALUE);
		assertEquals(TAX_VALUE, this.orderTaxValueImpl.getTaxValue());
	}
}