/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.validation.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.ConstraintValidatorContext;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.drools.util.StringUtils;

import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.validation.constraints.LongTextValueSize;

/**
 * Test class for {@link LongTextValueSizeValidator}.
 */
public class LongTextSizeValidatorTest {

	private static final int MIN = 10;

	private static final int MAX = 20;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private LongTextValueSizeValidator validator;

	private ConstraintValidatorContext validatorContext;

	private static final String LESS_THAN_MIN_VALUE = StringUtils.repeat("a", MIN - 1);

	private static final String MIN_VALUE = StringUtils.repeat("a", MIN);

	private static final String MID_RANGE_VALUE = StringUtils.repeat("a", MIN + 1);

	private static final String MAX_VALUE = StringUtils.repeat("a", MAX);

	private static final String GREATER_THAN_MAX_VALUE = StringUtils.repeat("a", MAX + 1);

	private LongTextValueSize sizeAnnotation;

	private AttributeValueWithType attributeValue;


	/** Test initialization. */
	@Before
	public void setUp() {
		validatorContext = context.mock(ConstraintValidatorContext.class);
		validator = new LongTextValueSizeValidator();
		sizeAnnotation = context.mock(LongTextValueSize.class);

		context.checking(new Expectations() {
			{
				allowing(validatorContext);
				allowing(sizeAnnotation).min();
				will(returnValue(MIN));
				allowing(sizeAnnotation).max();
				will(returnValue(MAX));
			}
		});

		validator.initialize(sizeAnnotation);

		attributeValue = context.mock(AttributeValueWithType.class);
	}

	/**
	 * Assert that no violation occurs when processing an attribute value with a null long text value.
	 */
	@Test
	public void testNullLongTextValue() {
		shouldHaveAttributeType(AttributeType.SHORT_TEXT);
		shouldHaveLongTextValue(null);
		assertTrue("There should be no violation when processing an attribute value with a a null long text value.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that no violation occurs when processing an attribute value that isn't a long text value.
	 */
	@Test
	public void testNotLongTextValue() {
		shouldHaveAttributeType(AttributeType.LONG_TEXT);
		shouldHaveLongTextValue(null);
		assertTrue("There should be no violation when processing an attribute value that isn't a long text value.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that a violation occurs when a long text value is too short.
	 */
	@Test
	public void testLongTextValueIsTooShort() {
		shouldHaveValidAttributeValueWithValue(LESS_THAN_MIN_VALUE);
		assertFalse("A violation occurs when a long text value is too short.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that no violation occurs when a long text value is equal to the minimum boundary.
	 */
	@Test
	public void testLongTextValueMatchesMin() {
		shouldHaveValidAttributeValueWithValue(MIN_VALUE);
		assertTrue("No violation occurs when a long text value is equal to the minimum boundary.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that no violation occurs when a long text value is equal to the maximum boundary.
	 */
	@Test
	public void testLongTextValueMatchesMax() {
		shouldHaveValidAttributeValueWithValue(MAX_VALUE);
		assertTrue("No violation occurs when a long text value is equal to the maximum boundary.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that a violation occurs when a long text value is too long.
	 */
	@Test
	public void testLongTextValueIsTooLong() {
		shouldHaveValidAttributeValueWithValue(GREATER_THAN_MAX_VALUE);
		assertFalse("A violation occurs when a long text value is too long.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that no violations occur when long text value is just right.
	 */
	@Test
	public void testLongTextValueIsAcceptableValue() {
		shouldHaveValidAttributeValueWithValue(MID_RANGE_VALUE);
		assertTrue("No violations should occur when a long text value is just right.",
				validator.isValid(attributeValue, validatorContext));
	}

	private void shouldHaveAttributeType(final AttributeType attributeType) {
		context.checking(new Expectations() {
			{
				allowing(attributeValue).getAttributeType();
				will(returnValue(attributeType));
			}
		});
	}

	private void shouldHaveLongTextValue(final String value) {
		context.checking(new Expectations() {
			{
				allowing(attributeValue).getLongTextValue();
				will(returnValue(value));
			}
		});
	}


	private void shouldHaveValidAttributeValueWithValue(final String value) {
		shouldHaveAttributeType(AttributeType.LONG_TEXT);
		shouldHaveLongTextValue(value);
	}

}
