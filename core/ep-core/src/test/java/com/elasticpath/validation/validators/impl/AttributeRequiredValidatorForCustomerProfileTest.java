package com.elasticpath.validation.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.ConstraintValidatorContext;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.customer.CustomerProfile;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.validation.constraints.AttributeRequired;

/**
 * Test class for {@link AttributeRequiredValidatorForCustomerProfile}.
 */
public class AttributeRequiredValidatorForCustomerProfileTest {

	private static final String REQUIRED_KEY = "attributeKey";
	private static final String NOT_REQUIRED_KEY = "anotherValue";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private AttributeRequiredValidatorForCustomerProfile validator;
	private AttributeService attributeService;
	private ConstraintValidatorContext validatorContext;
	private AttributeRequired annotation;
	private CustomerProfile customerProfile;

	/** Test initialization. */
	@Before
	public void setUp() {
		validator = new AttributeRequiredValidatorForCustomerProfile();

		annotation = context.mock(AttributeRequired.class);
		customerProfile = context.mock(CustomerProfile.class);

		attributeService = context.mock(AttributeService.class);
		validator.setAttributeService(attributeService);

		validatorContext = context.mock(ConstraintValidatorContext.class);
		context.checking(new Expectations() {
			{
				allowing(validatorContext);
				allowing(annotation);
			}
		});
		validator.initialize(annotation);
	}

	/** Should blow up without an attribute service set. */
	@Test(expected = NullPointerException.class)
	public void testNoService() {
		validator.setAttributeService(null);
		validator.isValid(null, null);
	}

	private void shouldGetAllCustomerProfileAttributes(final Attribute... attributes) {
		context.checking(new Expectations() {
			{
				oneOf(attributeService).getCustomerProfileAttributes();
				will(returnValue(Arrays.asList(attributes)));
			}
		});
	}

	private void shouldGetProfileAttributeValue(final String key, final Object value) {
		context.checking(new Expectations() {
			{
				oneOf(customerProfile).getProfileValue(key);
				will(returnValue(value));
			}
		});
	}

	/** If there is a required system attribute that the customer doesn't have, it should fail validation. */
	@Test
	public void testRequiredAttributeNotOnProfile() {
		shouldGetAllCustomerProfileAttributes(createAttribute(REQUIRED_KEY, true), createAttribute(NOT_REQUIRED_KEY, false));
		shouldGetProfileAttributeValue(REQUIRED_KEY, null);

		assertFalse("Missing required attribute should fail", validator.isValid(customerProfile, validatorContext));
	}

	/** If there is a required system attribute that the customer has but is blank, it should fail validation. */
	@Test
	public void testRequiredAttributeBlankOnProfile() {
		shouldGetAllCustomerProfileAttributes(createAttribute(REQUIRED_KEY, true), createAttribute(NOT_REQUIRED_KEY, false));
		shouldGetProfileAttributeValue(REQUIRED_KEY, "    		   ");

		assertFalse("A blank value for required attribute should fail", validator.isValid(customerProfile, validatorContext));
	}

	/** A profile which has all the required system attributes should validate successfully. */
	@Test
	public void testRequiredAttributeOnProfile() {
		shouldGetAllCustomerProfileAttributes(createAttribute(REQUIRED_KEY, true), createAttribute(NOT_REQUIRED_KEY, false));
		shouldGetProfileAttributeValue(REQUIRED_KEY, "ferenschik says no!");

		assertTrue("A valid value for required attribute should pass", validator.isValid(customerProfile, validatorContext));
	}

	/** A profile with 2 or more required attributes missing should report all of them, not just the first. */
	@Test
	public void test2RequiredAttributesBlank() {
		String anotherRequiredKey = "vvvvvvv";
		shouldGetAllCustomerProfileAttributes(createAttribute(REQUIRED_KEY, true), createAttribute(anotherRequiredKey, true));
		shouldGetProfileAttributeValue(REQUIRED_KEY, "    	    	  ");
		shouldGetProfileAttributeValue(anotherRequiredKey, null);

		assertFalse("Missing both required attributes should fail", validator.isValid(customerProfile, validatorContext));
	}

	/** A profile with 2 or more required attributes, but one is blank should fail. */
	@Test
	public void test2RequiredAttributesOneBlank() {
		String anotherRequiredKey = "vvvvvvv";
		shouldGetAllCustomerProfileAttributes(createAttribute(REQUIRED_KEY, true), createAttribute(anotherRequiredKey, true));
		shouldGetProfileAttributeValue(REQUIRED_KEY, "    	    	  ");
		shouldGetProfileAttributeValue(anotherRequiredKey, "sharks with laserbeams!");

		assertFalse("Missing both required attributes should fail", validator.isValid(customerProfile, validatorContext));
	}

	/** A profile with 2 or more required attributes and all are set correct should pass. */
	@Test
	public void test2RequiredAttributesValid() {
		String anotherRequiredKey = "rich";
		shouldGetAllCustomerProfileAttributes(createAttribute(REQUIRED_KEY, true), createAttribute(anotherRequiredKey, true));
		shouldGetProfileAttributeValue(REQUIRED_KEY, "one millllion  million doodle dooole doo dollars!");
		shouldGetProfileAttributeValue(anotherRequiredKey, "sharks with laserbeams!");

		assertTrue("Setting correct values for both attributes should pass", validator.isValid(customerProfile, validatorContext));
	}

	/** Tests validation with a date type attribute. */
	@Test
	public void testAttributeDate() {
		shouldGetAllCustomerProfileAttributes(createAttributeWithType(REQUIRED_KEY, true, AttributeType.DATE));
		shouldGetProfileAttributeValue(REQUIRED_KEY, new Date());

		assertTrue("Valid date should pass", validator.isValid(customerProfile, validatorContext));
	}

	/** Tests validation with a date type attribute. */
	@Test
	public void testAttributeDateNull() {
		shouldGetAllCustomerProfileAttributes(createAttributeWithType(REQUIRED_KEY, true, AttributeType.DATE));
		shouldGetProfileAttributeValue(REQUIRED_KEY, null);

		assertFalse("null date should fail", validator.isValid(customerProfile, validatorContext));
	}

	/** Tests validation with a integer type attribute. */
	@Test
	public void testAttributeInteger() {
		shouldGetAllCustomerProfileAttributes(createAttributeWithType(REQUIRED_KEY, true, AttributeType.INTEGER));
		shouldGetProfileAttributeValue(REQUIRED_KEY, 1);

		assertTrue("valid integer should pass", validator.isValid(customerProfile, validatorContext));
	}

	/** Tests validation with a integer type attribute. */
	@Test
	public void testAttributeIntegerNull() {
		shouldGetAllCustomerProfileAttributes(createAttributeWithType(REQUIRED_KEY, true, AttributeType.INTEGER));
		shouldGetProfileAttributeValue(REQUIRED_KEY, null);

		assertFalse("null integer should fail", validator.isValid(customerProfile, validatorContext));
	}

	/** Tests validation with a boolean type attribute. */
	@Test
	public void testAttributeBoolean() {
		shouldGetAllCustomerProfileAttributes(createAttributeWithType(REQUIRED_KEY, true, AttributeType.BOOLEAN));
		shouldGetProfileAttributeValue(REQUIRED_KEY, true);

		assertTrue("valid boolean should pass", validator.isValid(customerProfile, validatorContext));
	}

	/** Tests validation with a boolean type attribute. */
	@Test
	public void testAttributeBooleanNull() {
		shouldGetAllCustomerProfileAttributes(createAttributeWithType(REQUIRED_KEY, true, AttributeType.BOOLEAN));
		shouldGetProfileAttributeValue(REQUIRED_KEY, null);

		assertFalse("null boolean should fail", validator.isValid(customerProfile, validatorContext));
	}

	private Attribute createAttribute(final String attributeKey, final boolean required) {
		return createAttributeWithType(attributeKey, required, AttributeType.SHORT_TEXT);
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
}
