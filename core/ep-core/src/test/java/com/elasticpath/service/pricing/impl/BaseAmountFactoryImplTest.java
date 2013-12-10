package com.elasticpath.service.pricing.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Validator;

import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.exceptions.BaseAmountInvalidException;

/**
 * Test class for BaseAmountFactory.
 */
public class BaseAmountFactoryImplTest {
	
	private static final String PLGUID = "PLGUID";
	private static final String GUID = "GUID";
	private BaseAmountFactoryImpl factory;
	
	/**  */
	@Before
	public void setUp() {
		factory = new BaseAmountFactoryImpl();
		Validator validator = new BaseAmountValidatorImpl();
		factory.setValidator(validator);
	}
	
	/**
	 * Test base case of creating BaseAmount.
	 */
	@Test
	public void testBaseAmountCreate() {
		String objGuid = GUID;
		String objType = "PRODUCT";
		String descriptorGuid = "PRICEGUID";
		final BigDecimal qty = new BigDecimal("2");
		final BigDecimal list = new BigDecimal("1.23");
		final BigDecimal sale = new BigDecimal("1.13");
		
		BaseAmount baseAmount = factory.createBaseAmount(null, objGuid, objType, qty, list, sale, descriptorGuid);
		assertEquals(baseAmount.getObjectGuid(), objGuid);
		assertEquals(baseAmount.getObjectType(), objType);
		assertEquals(baseAmount.getPriceListDescriptorGuid(), descriptorGuid);
		assertEquals(baseAmount.getQuantity().doubleValue(), qty.doubleValue(), 0);
		assertEquals(baseAmount.getSaleValue().doubleValue(), sale.doubleValue(), 0);
		assertEquals(baseAmount.getListValue().doubleValue(), list.doubleValue(), 0);
	}

	/**
	 * Test base case of creating BaseAmount.
	 */
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	@Test
	public void testBaseAmountCreateWithSchedule() {
		String objGuid = GUID;
		String objType = "PRODUCT";
		String descriptorGuid = "PRICEGUID";
		final BigDecimal qty = new BigDecimal("2");
		final BigDecimal list = new BigDecimal("1.23");
		final BigDecimal sale = new BigDecimal("1.13");
		
		BaseAmount baseAmount = factory.createBaseAmount(null, objGuid, objType, qty, list, sale, descriptorGuid);
		assertEquals(baseAmount.getObjectGuid(), objGuid);
		assertEquals(baseAmount.getObjectType(), objType);
		assertEquals(baseAmount.getPriceListDescriptorGuid(), descriptorGuid);
		assertEquals(baseAmount.getQuantity().doubleValue(), qty.doubleValue(), 0);
		assertEquals(baseAmount.getSaleValue().doubleValue(), sale.doubleValue(), 0);
		assertEquals(baseAmount.getListValue().doubleValue(), list.doubleValue(), 0);
	}
	
	/**
	 * Tests if validation fails when object type is null.
	 */
	@Test(expected = BaseAmountInvalidException.class)
	public void testValidityOfObjectType() {
		factory.createBaseAmount("test_GUID", GUID, null, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, PLGUID);
	}
}
