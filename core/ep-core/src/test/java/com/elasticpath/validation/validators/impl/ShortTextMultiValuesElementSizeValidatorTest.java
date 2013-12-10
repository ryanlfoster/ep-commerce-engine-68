/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.validation.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.drools.util.StringUtils;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.validation.constraints.ShortTextMultiValuesElementSize;

/**
 * Test class for {@link ShortTextMultiValuesElementSizeValidator}.
 */
public class ShortTextMultiValuesElementSizeValidatorTest {

	private static final int MIN = 10;

	private static final int MAX = 20;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ShortTextMultiValuesElementSizeValidator validator;

	private ConstraintValidatorContext validatorContext;

	private static final String LESS_THAN_MIN_ELEMENT = StringUtils.repeat("a", MIN - 1);

	private static final String MIN_ELEMENT = StringUtils.repeat("a", MIN);

	private static final String MID_RANGE_ELEMENT = StringUtils.repeat("a", MIN + 1);

	private static final String MAX_ELEMENT = StringUtils.repeat("a", MAX);

	private static final String GREATER_THAN_MAX_ELEMENT = StringUtils.repeat("a", MAX + 1);

	private ShortTextMultiValuesElementSize elementSizeAnnotation;

	private AttributeValueWithType attributeValue;

	private Attribute attribute;

	/** Test initialization. */
	@Before
	public void setUp() {
		validatorContext = context.mock(ConstraintValidatorContext.class);
		validator = new ShortTextMultiValuesElementSizeValidator();
		elementSizeAnnotation = context.mock(ShortTextMultiValuesElementSize.class);

		context.checking(new Expectations() {
			{
				allowing(validatorContext);
				allowing(elementSizeAnnotation).min();
				will(returnValue(MIN));
				allowing(elementSizeAnnotation).max();
				will(returnValue(MAX));
			}
		});

		validator.initialize(elementSizeAnnotation);

		attributeValue = context.mock(AttributeValueWithType.class);

		attribute = context.mock(Attribute.class);
	}

	/**
	 * Assert that no violation occurs when processing an attribute value with a null attribute type.
	 */
	@Test
	public void testNullAttributeType() {
		shouldHaveAttributeType(null);
		assertTrue("There should be no violation when processing an attribute value with a null attribute type",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that no violation occurs when processing an attribute value with a null attribute.
	 */
	@Test
	public void testNullAttribute() {
		shouldHaveAttributeType(AttributeType.SHORT_TEXT);
		shouldHaveAttribute(null);
		assertTrue("There should be no violation when processing an attribute value with a null attribute",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that no violation occurs when processing an attribute value with a null short text multivalues.
	 */
	@Test
	public void testNullShortTextMultiValues() {
		shouldHaveAttributeType(AttributeType.SHORT_TEXT);
		shouldHaveShortTextMultiValues(null);
		shouldHaveAttribute(attribute);
		shouldHaveShortTextMultiValuesEnabled(true);
		assertTrue("There should be no violation when processing an attribute value with a null short text multivalues.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that no violation occurs when processing an attribute value that isn't short text multivalues.
	 */
	@Test
	public void testNotShortTextMultiValues() {
		shouldHaveAttributeType(AttributeType.SHORT_TEXT);
		shouldHaveShortTextMultiValues(Collections.<String> emptyList());
		shouldHaveAttribute(attribute);
		shouldHaveShortTextMultiValuesEnabled(false);
		assertTrue("There should be no violation when processing an attribute value that isn't short text multivalues.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that a violation occurs when short text multivalues has an element which is too short.
	 */
	@Test
	public void testShortTextMultiValuesHasAnElementThatIsTooShort() {
		shouldHaveValidAttributeValueWithMultiValues(Arrays.asList(MIN_ELEMENT, LESS_THAN_MIN_ELEMENT));
		assertFalse("A violation occurs when short text multivalues has an element which is too short.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that a violation occurs when short text multivalues has an element which is too long.
	 */
	@Test
	public void testShortTextMultiValuesHasAnElementThatIsTooLong() {
		shouldHaveValidAttributeValueWithMultiValues(Arrays.asList(MAX_ELEMENT, GREATER_THAN_MAX_ELEMENT));
		assertFalse("A violation occurs when short text multivalues has an element which is too long.",
				validator.isValid(attributeValue, validatorContext));
	}

	/**
	 * Assert that no violations occur when short text multivalues has elements which are just right.
	 */
	@Test
	public void testShortTextMultiValuesHaveElementsThatAreAcceptable() {
		shouldHaveValidAttributeValueWithMultiValues(Arrays.asList(MIN_ELEMENT, MID_RANGE_ELEMENT, MAX_ELEMENT));
		assertTrue("No violations should occur when short text multivalues has elements which are just right.",
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

	private void shouldHaveAttribute(final Attribute attribute) {
		context.checking(new Expectations() {
			{
				allowing(attributeValue).getAttribute();
				will(returnValue(attribute));
			}
		});
	}

	private void shouldHaveShortTextMultiValuesEnabled(final boolean enabled) {
		context.checking(new Expectations() {
			{
				allowing(attribute).isMultiValueEnabled();
				will(returnValue(enabled));
			}
		});
	}

	private void shouldHaveShortTextMultiValues(final List<String> list) {
		context.checking(new Expectations() {
			{
				allowing(attributeValue).getShortTextMultiValues();
				will(returnValue(list));
			}
		});
	}

	private void shouldHaveValidAttributeValueWithMultiValues(final List<String> shortTextMultiValues) {
		shouldHaveAttributeType(AttributeType.SHORT_TEXT);
		shouldHaveShortTextMultiValuesEnabled(true);
		shouldHaveAttribute(attribute);
		shouldHaveShortTextMultiValues(shortTextMultiValues);
	}

}
