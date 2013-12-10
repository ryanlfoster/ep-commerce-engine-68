/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.validation.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.validation.ConstraintValidatorContext;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.drools.util.StringUtils;

import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.validation.constraints.ShortTextValueSize;

/**
 * Test class for {@link ShortTextValueSizeValidator}.
 */
public class ShortTextSizeValidatorTest {

	private static final int MIN = 10;

	private static final int MAX = 20;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ShortTextValueSizeValidator validator;

	private ConstraintValidatorContext validatorContext;

	private static final String LESS_THAN_MIN_VALUE = StringUtils.repeat("a", MIN - 1);

	private static final String MIN_VALUE = StringUtils.repeat("a", MIN);

	private static final String MID_RANGE_VALUE = StringUtils.repeat("a", MIN + 1);

	private static final String MAX_VALUE = StringUtils.repeat("a", MAX);

	private static final String GREATER_THAN_MAX_VALUE = StringUtils.repeat("a", MAX + 1);

	private ShortTextValueSize sizeAnnotation;

	private AttributeValueWithType attributeValue;


	/** Test initialization. */
	@Before
	public void setUp() {
		validatorContext = context.mock(ConstraintValidatorContext.class);
		validator = new ShortTextValueSizeValidator();
		validator.setSupportedAttributeTypes(Arrays.asList(AttributeType.SHORT_TEXT));
		sizeAnnotation = context.mock(ShortTextValueSize.class);

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
	 * Assert that no violation occurs when processing an attribute value with a null short text value.
	 */
	@Test
	public void testNullShortTextValue() {
		shouldHaveAttributeType(AttributeType.SHORT_TEXT);
		shouldHaveShortTextValue(null);
		assertTrue("There should be no violation when processing an attribute value with a a null short text value.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that no violation occurs when processing an attribute value that isn't a short text value.
	 */
	@Test
	public void testNotShortTextValue() {
		shouldHaveAttributeType(AttributeType.LONG_TEXT);
		shouldHaveShortTextValue(null);
		assertTrue("There should be no violation when processing an attribute value that isn't a short text value.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that a violation occurs when a short text value is too short.
	 */
	@Test
	public void testShortTextValueIsTooShort() {
		shouldHaveValidAttributeValueWithValue(LESS_THAN_MIN_VALUE);
		assertFalse("A violation occurs when a short text value is too short.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that no violation occurs when a short text value is equal to the minimum boundary.
	 */
	@Test
	public void testShortTextValueMatchesMin() {
		shouldHaveValidAttributeValueWithValue(MIN_VALUE);
		assertTrue("No violation occurs when a short text value is equal to the minimum boundary.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that no violation occurs when a short text value is equal to the maximum boundary.
	 */
	@Test
	public void testShortTextValueMatchesMax() {
		shouldHaveValidAttributeValueWithValue(MAX_VALUE);
		assertTrue("No violation occurs when a short text value is equal to the maximum boundary.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that a violation occurs when a short text value is too long.
	 */
	@Test
	public void testShortTextValueIsTooLong() {
		shouldHaveValidAttributeValueWithValue(GREATER_THAN_MAX_VALUE);
		assertFalse("A violation occurs when a short text value is too long.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that no violations occur when short text value is just right.
	 */
	@Test
	public void testShortTextValueIsAcceptableValue() {
		shouldHaveValidAttributeValueWithValue(MID_RANGE_VALUE);
		assertTrue("No violations should occur when a short text value is just right.",
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

	private void shouldHaveShortTextValue(final String value) {
		context.checking(new Expectations() {
			{
				allowing(attributeValue).getShortTextValue();
				will(returnValue(value));
			}
		});
	}

	private void shouldHaveValidAttributeValueWithValue(final String value) {
		shouldHaveAttributeType(AttributeType.SHORT_TEXT);
		shouldHaveShortTextValue(value);
	}

}
