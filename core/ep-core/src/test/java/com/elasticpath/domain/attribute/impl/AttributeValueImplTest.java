/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.attribute.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.math.BigDecimal;
import java.util.Date;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>AbstractAttributeValueImpl</code>.
 */
public class AttributeValueImplTest {

	@Rule
	public final JUnitRuleMockery mockery = new JUnitRuleMockery();
	private AbstractAttributeValueImpl attributeValueImpl1;
	private BeanFactoryExpectationsFactory beanFactoryExpectationsFactory;
    private BeanFactory beanFactory;

	/**
	 * Prepare for the test.
	 *
	 * @throws Exception in case of error
	 */
	@Before
	public void setUp() throws Exception {
	    beanFactory = mockery.mock(BeanFactory.class);
	    beanFactoryExpectationsFactory = new BeanFactoryExpectationsFactory(mockery, beanFactory);
	    beanFactoryExpectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.UTILITY, new UtilityImpl());

		attributeValueImpl1 = new ProductAttributeValueImpl();
	}

	@After
	public void tearDown() {
		beanFactoryExpectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.getShortTextValue()'.
	 */
	@Test
	public void testGetShortTextValue() {
		assertNull(attributeValueImpl1.getShortTextValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.setShortTextValue(String)'.
	 */
    @Test
	public void testSetShortTextValue() {
		final String value = "value";
		attributeValueImpl1.setShortTextValue(value);
		assertSame(value, attributeValueImpl1.getShortTextValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.getLongTextValue()'.
	 */
    @Test
	public void testGetLongTextValue() {
		assertNull(attributeValueImpl1.getLongTextValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.setLongTextValue(String)'.
	 */
    @Test
	public void testSetLongTextValue() {
		final String value = "value";
		attributeValueImpl1.setLongTextValue(value);
		assertSame(value, attributeValueImpl1.getLongTextValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.getIntegerValue()'.
	 */
    @Test
	public void testGetIntegerValue() {
		assertNull(attributeValueImpl1.getIntegerValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.setIntegerValue(Integer)'.
	 */
    @Test
	public void testSetIntegerValue() {
		final Integer value = Integer.valueOf("3");
		attributeValueImpl1.setIntegerValue(value);
		assertSame(value, attributeValueImpl1.getIntegerValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.getDecimalValue()'.
	 */
    @Test
	public void testGetDecimalValue() {
		assertNull(attributeValueImpl1.getDecimalValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.setDecimalValue(BigDecimal)'.
	 */
    @Test
	public void testSetDecimalValue() {
		final BigDecimal value = new BigDecimal("3");
		attributeValueImpl1.setDecimalValue(value);
		assertSame(value, attributeValueImpl1.getDecimalValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.getBooleanValue()'.
	 */
    @Test
	public void testGetBooleanValue() {
		assertNull(attributeValueImpl1.getBooleanValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.setBooleanValue(Boolean)'.
	 */
    @Test
	public void testSetBooleanValue() {
		final Boolean value = Boolean.TRUE;
		attributeValueImpl1.setBooleanValue(value);
		assertSame(value, attributeValueImpl1.getBooleanValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.getDateValue()'.
	 */
    @Test
	public void testGetDateValue() {
		assertNull(attributeValueImpl1.getDateValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.setDateValue(Date)'.
	 */
    @Test
	public void testSetDateValue() {
		final Date value = new Date();
		attributeValueImpl1.setDateValue(value);
		assertSame(value, attributeValueImpl1.getDateValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.getAttribute()'.
	 */
    @Test
	public void testGetAttribute() {
		assertNull(attributeValueImpl1.getAttribute());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.setAttribute(Attribute)'.
	 */
    @Test
	public void testSetAttribute() {
		final Attribute attributeImpl = new AttributeImpl();
		attributeValueImpl1.setAttribute(attributeImpl);
		assertSame(attributeImpl, attributeValueImpl1.getAttribute());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractAttributeValueImpl.getAttributeType()'.
	 */
	@Test
	public void testGetSetAttributeType() {
		assertNull(attributeValueImpl1.getAttributeType());
		attributeValueImpl1.setAttributeType(AttributeType.BOOLEAN);
		assertEquals(AttributeType.BOOLEAN, attributeValueImpl1.getAttributeType());
	}

}
