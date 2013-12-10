package com.elasticpath.validation.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.validation.domain.ValidationConstraint;
import com.elasticpath.validation.domain.ValidationResult;
import com.elasticpath.validation.service.valang.ValangTypeOfFunction;

/**
 * Valang validation engine test.
 */
public class ValangValidationEngineImplTest  {

	private static final String RESULT_MUST_NEVER_BE_NULL = "Result must never be null";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ValangValidationEngineImpl validationEngine;

	/**
	 * Setups test.
	 */
	@Before
	public void setUp() {

		validationEngine = new ValangValidationEngineImpl();
		validationEngine.addCustomFunction(ValangTypeOfFunction.FUNCTION_NAME, ValangTypeOfFunction.class.getName());
	}


	/**
	 * Test that validation engine returns a valid result if no constraints are
	 * provided.
	 */
	@Test
	public void testValidateOnNullConstraint() {

		final ValidationResult result = validationEngine.validate(null, null);
		assertNotNull(RESULT_MUST_NEVER_BE_NULL, result);
		assertTrue("Result must return valid=true when constraint is null", result.isValid());

	}

	/**
	 * Test that validation engine returns a valid result if no constraints are
	 * provided.
	 */
	@Test
	public void testValidateOnEmptyConstraint() {

		final ValidationResult result = validationEngine.validate(null, null);
		assertNotNull(RESULT_MUST_NEVER_BE_NULL, result);
		assertTrue("Result must return valid=true when constraint is empty", result.isValid());

	}

	private Condition setUpCondition(final String tagDefinitionMockName, final Object value) {
		final TagDefinition tagDefinition = context.mock(TagDefinition.class, tagDefinitionMockName);
		return new Condition(tagDefinition, "operator", value);
	}

	/**
	 * Test simple validation passes. The following constraint will be used:
	 * { condition : tagValue <= 10 : 'value must be less or equal 10' }
	 * with value 1, and the expected result should be valid.
	 */
	@Test
	public void testValidateSimpleConstraint() {

		final Condition condition = setUpCondition("tagDefinition", Integer.valueOf(1));
		final ValidationConstraint constraintLessOrEqualTo10 = context.mock(ValidationConstraint.class, "less or equal to 10");

		context.checking(new Expectations() { {
			allowing(constraintLessOrEqualTo10).getConstraint();
			will(returnValue("{ condition : tagValue <= 10 : 'value must be less than or equal to 10' }"));
		} });

		final ValidationResult result = validationEngine.validate(condition, constraintLessOrEqualTo10);
		assertNotNull(RESULT_MUST_NEVER_BE_NULL, result);
		assertTrue("Result must return valid=true for this check", result.isValid());

	}

	/**
	 * Test simple validation passes. The following constraint will be used:
	 * { condition : typeof(tagValue, 'java.lang.Integer') IS true AND tagValue <= 10 : 'value must be less or equal 10' }
	 * with value 1, and the expected result should be valid.
	 */
	@Test
	public void testValidateConstraintWithTypeCheck() {

		final Condition condition = setUpCondition("tagDefinition", Integer.valueOf(1));
		final ValidationConstraint constraintIntegerAndLessOrEqualTo10 = context.mock(ValidationConstraint.class, "int less or equal to 10");

		context.checking(new Expectations() { {
			allowing(constraintIntegerAndLessOrEqualTo10).getConstraint();
			will(returnValue("{ condition : typeof(tagValue, 'java.lang.Integer') IS true AND tagValue <= 10 : "
					+ "'value must be integer that is less than or equal to 10' }"));
		} });

		final ValidationResult result = validationEngine.validate(condition, constraintIntegerAndLessOrEqualTo10);
		assertNotNull(RESULT_MUST_NEVER_BE_NULL, result);
		assertTrue("Result must return valid=true for this check", result.isValid());

	}

	/**
	 * Test simple validation passes. The following constraint will be used:
	 * { condition : typeof(tagValue, 'java.lang.Integer') IS true AND tagValue <= 10 : 'value must be less or equal 10' }
	 * with value 1, and the expected result should be invalid.
	 */
	@Test
	public void testValidateConstraintWithTypeCheckFail() {

		final Condition condition = setUpCondition("tagDefinition", "string");
		final ValidationConstraint constraintIntegerAndLessOrEqualTo10 = context.mock(ValidationConstraint.class, "int less or equal to 10");

		context.checking(new Expectations() { {
			allowing(constraintIntegerAndLessOrEqualTo10).getConstraint();
			will(returnValue("{ condition : typeof(tagValue, 'java.lang.Integer') IS true AND tagValue <= 10 : "
					+ "'value must be integer that is less than or equal to 10' }"));
		} });

		final ValidationResult result = validationEngine.validate(condition, constraintIntegerAndLessOrEqualTo10);
		assertNotNull(RESULT_MUST_NEVER_BE_NULL, result);
		assertFalse("Result must return valid=false for this check", result.isValid());

	}

}
