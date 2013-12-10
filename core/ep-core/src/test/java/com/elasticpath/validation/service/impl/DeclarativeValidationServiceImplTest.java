package com.elasticpath.validation.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.validation.domain.ValidationConstraint;
import com.elasticpath.validation.domain.ValidationResult;
import com.elasticpath.validation.service.ValidationEngine;

/**
 * Test the general contract of the declarative validation service.
 */
public class DeclarativeValidationServiceImplTest {

	private static final String RESULT_MUST_NEVE_BE_NULL = "Result must neve be null";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private DeclarativeValidationServiceImpl declarativeValidationService; 

	private ValidationEngine validationEngine;

	private ValidationConstraint constraint1;
	private ValidationConstraint constraint2;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		declarativeValidationService = new DeclarativeValidationServiceImpl();
		validationEngine = context.mock(ValidationEngine.class, "engine");
		declarativeValidationService.setValidationEngine(validationEngine);
	}

	private Set<ValidationConstraint> oneMockedConstraintInHashSet() {
		final Set<ValidationConstraint> constraints = new HashSet<ValidationConstraint>();

		constraint1 = context.mock(ValidationConstraint.class, "constraint1");
		constraints.add(constraint1);
		return constraints;
	}

	private Set<ValidationConstraint> twoMockedConstraintInHashSet() {
		final Set<ValidationConstraint> constraints = oneMockedConstraintInHashSet();

		constraint2 = context.mock(ValidationConstraint.class, "constraint2");
		constraints.add(constraint2);
		return constraints;
	}

	/**
	 * Test that a valid result object is returned if null constrains are supplied.
	 */
	@Test
	public void testValidateWithNullConstrains() {

		final ValidationResult result = declarativeValidationService.validate(null, null);
		assertNotNull(RESULT_MUST_NEVE_BE_NULL, result);
		assertTrue("Result must return valid=true when constraints are null", result.isValid());

	}

	/**
	 * Test that a valid result object is returned if empty collection of constrains is supplied.
	 */
	@Test
	public void testValidateWithEmptyConstrains() {

		final Set<ValidationConstraint> constraints = new HashSet<ValidationConstraint>();

		final ValidationResult result = declarativeValidationService.validate(null, constraints);
		assertNotNull(RESULT_MUST_NEVE_BE_NULL, result);
		assertTrue("Result must return valid=true when constraints is an empty collection", result.isValid());

	}

	/**
	 * Test that if the constraint fails the result objects of this validation is returned with
	 * a valid result being false and a message for the error.
	 */
	@Test
	public void testValidateIfConstraintFails() {

		final Set<ValidationConstraint> constraints = oneMockedConstraintInHashSet();

		final ValidationResult failedValidationResult = context.mock(ValidationResult.class, "failure");

		context.checking(new Expectations() { {
			oneOf(validationEngine).validate(null, constraint1); will(returnValue(failedValidationResult));
			allowing(failedValidationResult).isValid(); will(returnValue(false));
		} });

		final ValidationResult result = declarativeValidationService.validate(null, constraints);
		assertNotNull(RESULT_MUST_NEVE_BE_NULL, result);
		assertFalse("Result must return valid=false when any constraint fails", result.isValid());

	}

	/**
	 * Test that if the constraint fails on any constaint is the collection the result objects
	 * of this validation is returned with a valid result being false and a message for the error.
	 */
	@Test
	public void testValidateIfOneOfConstraintsFails() {

		final Set<ValidationConstraint> constraints = twoMockedConstraintInHashSet();

		final ValidationResult successValidationResult = context.mock(ValidationResult.class, "success");
		final ValidationResult failedValidationResult = context.mock(ValidationResult.class, "failure");

		context.checking(new Expectations() { {
			allowing(validationEngine).validate(null, constraint1); will(returnValue(successValidationResult));
			allowing(successValidationResult).isValid(); will(returnValue(true));
			allowing(validationEngine).validate(null, constraint2); will(returnValue(failedValidationResult));
			allowing(failedValidationResult).isValid(); will(returnValue(false));
		} });

		final ValidationResult result = declarativeValidationService.validate(null, constraints);
		assertNotNull(RESULT_MUST_NEVE_BE_NULL, result);
		assertFalse("Result must return valid=false when any constraint fails", result.isValid());

	}


}
