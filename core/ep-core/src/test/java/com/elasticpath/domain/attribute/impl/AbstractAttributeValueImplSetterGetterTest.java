/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.attribute.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpBigDecimalBindException;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpBooleanBindException;
import com.elasticpath.commons.exception.EpDateBindException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test the setters and getters of attribute values on <code>AbstractAttributeValueImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.TooManyStaticImports" })
public class AbstractAttributeValueImplSetterGetterTest {

	private static final String EXPECT_EP_BIND_EXCEPTION = "Expect EpBindException";
	private static final String TEST_VALUE = "test value";
	private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

	private Utility utility = getUtility();

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of any error
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_USAGE, AttributeUsageImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.UTILITY, utility);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getNameMessageKey()'.
	 */
	@Test
	public void testGetNameMessageKey() {
		assertTrue(AttributeType.INTEGER.getNameMessageKey().equalsIgnoreCase("AttributeType_Integer"));
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getValue()'.
	 */
	@Test
	public void testGetAndSetShortTextValue() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		final Attribute attribute = new AttributeImpl();
		attribute.setAttributeUsage(AttributeUsageImpl.PRODUCT_USAGE);
		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(AttributeType.SHORT_TEXT);

		final String value = TEST_VALUE;
		attributeValue.setValue(value);
		assertSame(value, attributeValue.getValue());
		assertEquals(value, attributeValue.getStringValue());
	}

	/**
	 * Test that when setting a multi-value short text field using a comma-delimited
	 * string, the value can be retrieved (with entries trimmed of leading and trailing
	 * spaces).
	 */
	@Test
	public void testGetAndSetShortTextMultiValues() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		final Attribute attribute = new AttributeImpl();
		attribute.setAttributeUsage(AttributeUsageImpl.PRODUCT_USAGE);
		attribute.setMultiValueEnabled(true);
		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(AttributeType.SHORT_TEXT);

		String multiValueString = "test value, test value2, ttt";
		String trimmedMultiValueString = "test value,test value2,ttt";
		attributeValue.setValue(multiValueString);
		assertEquals(trimmedMultiValueString, attributeValue.getValue());
		assertEquals(trimmedMultiValueString, attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getValue()'.
	 */
	@Test
	public void testGetAndSetIntegerValue() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.INTEGER);

		final String value = "3";
		final Integer integerValue = Integer.valueOf(value);
		attributeValue.setValue(integerValue);
		assertSame(integerValue, attributeValue.getValue());
		assertEquals("3", attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getStringValue()'.
	 */
	@Test
	public void testGetAndSetIntegerStringValue() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.INTEGER);

		final String value = "3";
		final Integer integerValue = Integer.valueOf(value);
		attributeValue.setStringValue(value);
		assertEquals(integerValue, attributeValue.getValue());
		assertEquals("3", attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getStringValue()'.
	 */
	@Test(expected = EpBindException.class)
	public void testGetAndSetIntegerStringValueError() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.INTEGER);

		attributeValue.setStringValue("not a valid integer");
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getValue()'.
	 */
	@Test
	public void testGetAndSetLongTextValue() {

		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.LONG_TEXT);

		final String value = TEST_VALUE;
		attributeValue.setValue(value);
		assertSame(value, attributeValue.getValue());
		assertEquals(value, attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getValue()'.
	 */
	@Test
	public void testGetAndSetBooleanValue() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.BOOLEAN);

		final Boolean value = Boolean.TRUE;
		attributeValue.setValue(value);
		assertSame(value, attributeValue.getValue());

		assertEquals(value.toString(), attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getStringValue()'.
	 */
	@Test
	public void testGetAndSetBooleanStringValue() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.BOOLEAN);

		final Boolean value = Boolean.TRUE;
		attributeValue.setStringValue(value.toString());
		assertSame(Boolean.TRUE, attributeValue.getValue());
		assertEquals(value.toString(), attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getStringValue()'.
	 */
	@Test
	public void testGetAndSetBooleanStringValueError() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.BOOLEAN);

		try {
			attributeValue.setStringValue("not a valid boolean value");
			fail(EXPECT_EP_BIND_EXCEPTION);
		} catch (EpBooleanBindException e) {
			// succeed
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getValue()'.
	 */
	@Test
	public void testGetAndSetDecimalValue() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.DECIMAL);

		final String value = "3.1415";
		final BigDecimal decimalValue = new BigDecimal(value);
		attributeValue.setValue(decimalValue);
		assertSame(decimalValue, attributeValue.getValue());
		assertEquals(value, attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getStringValue()'.
	 */
	@Test
	public void testGetAndSetDecimalStringValue() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.DECIMAL);

		final String value = "3.1415";
		final BigDecimal decimalValue = new BigDecimal(value);
		attributeValue.setStringValue(value);
		assertEquals(decimalValue, attributeValue.getValue());
		assertEquals(value, attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getStringValue()'.
	 */
	@Test
	public void testGetAndSetDecimalStringValueError() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.DECIMAL);

		try {
			attributeValue.setStringValue("not a valid decimal value.");
			fail(EXPECT_EP_BIND_EXCEPTION);
		} catch (EpBigDecimalBindException e) {
			// succeed
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getValue()'.
	 */
	@Test
	public void testGetAndSetImageValue() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.IMAGE);

		final String value = TEST_VALUE;
		attributeValue.setValue(value);
		assertSame(value, attributeValue.getValue());
		assertEquals(value, attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getValue()'.
	 */
	@Test
	public void testGetAndSetFileValue() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.FILE);

		final String value = TEST_VALUE;
		attributeValue.setValue(value);
		assertSame(value, attributeValue.getValue());
		assertEquals(value, attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getValue()'.
	 */
	@Test
	public void testGetAndSetDateValue() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.DATE);

		final Date value = new Date();
		attributeValue.setValue(value);
		assertSame(value, attributeValue.getValue());
		assertEquals(ConverterUtils.date2String(value, DATE_FORMAT, Locale.getDefault()), attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getStringValue()'.
	 */
	@Test
	public void testGetAndSetDateStringValue() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.DATE);

		final Date value = new Date();
		String stringDate = ConverterUtils.date2String(value, DATE_FORMAT, Locale.getDefault());
		attributeValue.setStringValue(stringDate);
		assertEquals(stringDate, attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getStringValue()'.
	 */
	@Test
	public void testGetAndSetDateStringValueError() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.DATE);

		try {
			attributeValue.setStringValue("not a valid date string");
			fail(EXPECT_EP_BIND_EXCEPTION);
		} catch (EpDateBindException e) {
			// succeed
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getValue()'.
	 */
	@Test
	public void testGetAndSetDateTimeValue() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.DATETIME);

		final Date value = new Date();
		String stringDate = ConverterUtils.date2String(value, DATE_FORMAT, Locale.getDefault());

		attributeValue.setValue(value);
		assertSame(value, attributeValue.getValue());
		assertEquals(stringDate, attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getStringValue()'.
	 */
	@Test
	public void testGetAndSetDateTimeStringValue() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.DATETIME);

		final Date value = new Date();
		String stringDate = ConverterUtils.date2String(value, DATE_FORMAT, Locale.getDefault());
		attributeValue.setStringValue(stringDate);
		assertEquals(stringDate, attributeValue.getStringValue());
	}

	/**
	 * Test method for 'com.elasticpath.commons.enums.AttributeType.getStringValue()'.
	 */
	@Test
	public void testGetAndSetDateTimeStringValueError() {
		final AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttributeType(AttributeType.DATETIME);

		try {
			attributeValue.setStringValue("not a valid date string");
			fail(EXPECT_EP_BIND_EXCEPTION);
		} catch (EpDateBindException e) {
			// succeed
			assertNotNull(e);
		}
	}


	/**
	 * @return the <code>Utility</code> instance.
	 */
	private Utility getUtility() {
		if (utility == null) {
			utility = new UtilityImpl() {
				private static final long serialVersionUID = 291469250773157883L;

				@Override
				protected String getDefaultDateFormatPattern() {
					return DATE_FORMAT;
				}
			};
		}
		return utility;
	}

}
