package com.elasticpath.validation.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;

/**
 * Test for {@link AbstractAttributeRequiredValidator}.
 */
public class AbstractAttributeRequiredValidatorTest {

	private static final String NOT_REQUIRED_KEY = "notRequired";
	private static final String REQUIRED_KEY = "required";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private TestValidator validator;
	private ConstraintValidatorContext validationContext;
	private Object objectToValidate;

	/** Test initialization. */
	@Before
	@SuppressWarnings("unchecked")
	public void initialize() {
		validator = new TestValidator();
		validator.iface = context.mock(TestValidatorInterface.class);

		validationContext = context.mock(ConstraintValidatorContext.class);
		objectToValidate = new Object();
	}

	private void shouldGetAttributesToValidate(final Attribute... attributes) {
		context.checking(new Expectations() {
			{
				allowing(validator.iface).getAttributesToValidate();
				will(returnValue(Arrays.asList(attributes)));
			}
		});
	}

	private void shouldValidateAttribute(final Attribute attribute, final Object objectToValidate,
			final ConstraintValidatorContext validatorContext, final boolean result) {
		context.checking(new Expectations() {
			{
				allowing(validator.iface).isAttributeValid(attribute, objectToValidate, validatorContext);
				will(returnValue(result));
			}
		});
	}

	/** Tests when there are no attributes given pass back to the abstract class. */
	@Test
	public void testNoAttributes() {
		shouldGetAttributesToValidate();

		assertTrue("no errors if no attributes", validator.isValid(objectToValidate, validationContext));
	}

	/** Should validate successfully if there are no required attributes regardless if there are attributes. */
	@Test
	public void testNoRequiredAttributes() {
		shouldGetAttributesToValidate(createAttribute(NOT_REQUIRED_KEY, false));

		assertTrue("no required attributes", validator.isValid(objectToValidate, validationContext));
	}

	/** Validate successfully if the attribute value is valid. */
	@Test
	public void testOneRequiredAttributeValid() {
		Attribute attribute = createAttribute(REQUIRED_KEY, true);
		shouldGetAttributesToValidate(attribute);
		shouldValidateAttribute(attribute, objectToValidate, validationContext, true);

		assertTrue("attribute validated successfully, but errors?", validator.isValid(objectToValidate, validationContext));
	}

	/** Should validate only a required attribute. */
	@Test
	public void testOneRequiredValidOneNotRequired() {
		Attribute requiredAttribute = createAttribute(REQUIRED_KEY, true);
		Attribute nonRequiredAttribute = createAttribute(NOT_REQUIRED_KEY, false);
		shouldGetAttributesToValidate(requiredAttribute, nonRequiredAttribute);
		shouldValidateAttribute(requiredAttribute, objectToValidate, validationContext, true);

		assertTrue("attribute validated successfully, but errors?", validator.isValid(objectToValidate, validationContext));
	}

	/** Required attribute that is invalid should fail validation. */
	@Test
	public void testOneRequiredInvalidOneNotRequired() {
		Attribute requiredAttribute = createAttribute(REQUIRED_KEY, true);
		Attribute nonRequiredAttribute = createAttribute(NOT_REQUIRED_KEY, false);
		shouldGetAttributesToValidate(requiredAttribute, nonRequiredAttribute);
		shouldValidateAttribute(requiredAttribute, objectToValidate, validationContext, false);

		context.checking(new Expectations() {
			{
				// not checking this context in this test
				allowing(validationContext);
			}
		});

		assertFalse("invalid attribute, but validated successfully?", validator.isValid(objectToValidate, validationContext));
	}

	/** Tests the violation property path is set correctly. */
	@Test
	public void testAttributeViolationPath() {
		Attribute requiredAttribute = createAttribute(REQUIRED_KEY, true);
		shouldGetAttributesToValidate(requiredAttribute);
		shouldValidateAttribute(requiredAttribute, objectToValidate, validationContext, false);

		context.checking(new Expectations() {
			{
				allowing(validationContext).disableDefaultConstraintViolation();
				allowing(validationContext).getDefaultConstraintMessageTemplate();

				ConstraintViolationBuilder builder = context.mock(ConstraintViolationBuilder.class);
				allowing(validationContext).buildConstraintViolationWithTemplate(with(Expectations.<String> anything()));
				will(returnValue(builder));

				allowing(builder).addNode(REQUIRED_KEY);
				allowing(builder).addConstraintViolation();
			}
		});

		assertFalse("testing a violation path, so we need a failure", validator.isValid(objectToValidate, validationContext));
	}

	/** A constraint violation should use the proper error message. */
	@Test
	public void testCorrectErrorMessage() {
		Attribute requiredAttribute = createAttribute(REQUIRED_KEY, true);
		shouldGetAttributesToValidate(requiredAttribute);
		shouldValidateAttribute(requiredAttribute, objectToValidate, validationContext, false);

		context.checking(new Expectations() {
			{
				String defaultViolationMessage = "default message";
				allowing(validationContext).getDefaultConstraintMessageTemplate();
				will(returnValue(defaultViolationMessage));

				allowing(validationContext).disableDefaultConstraintViolation();
				allowing(validationContext).buildConstraintViolationWithTemplate(defaultViolationMessage);
			}
		});

		assertFalse("testing a violation message, so we need a failure", validator.isValid(objectToValidate, validationContext));
	}

	private static AtomicInteger count = new AtomicInteger();

	private Attribute createAttributeWithType(final String attributeKey, final boolean required, final AttributeType attributeType) {
		final Attribute attribute = context.mock(Attribute.class, String.format("attribute-%d", count.incrementAndGet()));
		context.checking(new Expectations() {
			{
				allowing(attribute).getAttributeType();
				will(returnValue(attributeType));

				allowing(attribute).isRequired();
				will(returnValue(required));

				allowing(attribute).getKey();
				will(returnValue(attributeKey));
			}
		});
		return attribute;
	}

	private Attribute createAttribute(final String attributeKey, final boolean required) {
		return createAttributeWithType(attributeKey, required, AttributeType.SHORT_TEXT);
	}

	/** Test implementation for unit tests. */
	private static class TestValidator extends AbstractAttributeRequiredValidator<Object> {
		private TestValidatorInterface<? super Object> iface;

		@Override
		public boolean isAttributeValid(final Attribute attribute, final Object value, final ConstraintValidatorContext context) {
			return iface.isAttributeValid(attribute, value, context);
		}

		@Override
		protected List<Attribute> getAttributesToValidate() {
			return iface.getAttributesToValidate();
		}

	}

	/** Interface for mock expectations. */
	private interface TestValidatorInterface<T> {
		List<Attribute> getAttributesToValidate();

		boolean isAttributeValid(Attribute attribute, T value, ConstraintValidatorContext context);
	}
}
