package com.elasticpath.tags.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.validation.domain.ValidationResult;
import com.elasticpath.validation.service.ValidationService;

/**
 * Test that condition validation facade provides a robust service for validating
 * conditions and condition trees.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class ConditionValidationFacadeImplTest  {

	private static final String ERROR2 = "error2";

	private static final String ERROR = "error";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ConditionValidationFacadeImpl validationFacade;

	private ValidationService validationService;

	/**
	 * Setups test.
	 */
	@Before
	public void setUp() {
		validationService = context.mock(ValidationService.class, "validationService");

		validationFacade = new ConditionValidationFacadeImpl();
		validationFacade.setValidationService(validationService);
	}

	/**
	 * Test that ensures that null condition as argument is not allowed.
	 */
	@Test
	public void testValidateSingleNullCondition() {

		try {

			final Condition condition = null;
			validationFacade.validate(condition);

			fail("Must not evaluate on null conditions");
		} catch (IllegalArgumentException iae) {
			assertNotNull(true);
		}

	}

	/**
	 * Test that ensures that null as tag definition of condition as argument is not allowed.
	 */
	@Test
	public void testValidateSingleNullTagDefinitionOfCondition() {

		try {

			final Condition condition = new Condition(null, "", "");
			validationFacade.validate(condition);

			fail("Must not evaluate on conditions with null tag definition");
		} catch (IllegalArgumentException iae) {
			assertNotNull(true);
		}

	}

	/**
	 * Test that ensures that null as tag value type of a tag definition of condition as argument
	 * is not allowed.
	 */
	@Test
	public void testValidateSingleNullTagValueTypeOfTagDefinitionOfCondition() {

		try {

			final TagDefinition tagDefinition = context.mock(TagDefinition.class, "tagDefinition");

			context.checking(new Expectations() { {
				allowing(tagDefinition).getValueType(); will(returnValue(null));
			} });

			final Condition condition = new Condition(tagDefinition, "", "");
			validationFacade.validate(condition);

			fail("Must not evaluate on conditions with null tag definition");
		} catch (IllegalArgumentException iae) {
			assertNotNull(true);
		}

	}

	/**
	 * Test that ensures that a valid condition will return a valid validation result.
	 */
	@Test
	public void testValidateSingleConditionSuccess() {

		final TagDefinition tagDefinition = context.mock(TagDefinition.class, "tagDefinition");
		final TagValueType tagValueType = context.mock(TagValueType.class, "tagValueType");
		final ValidationResult result = context.mock(ValidationResult.class, "validationResult");
		final Condition condition = new Condition(tagDefinition, "", "");

		context.checking(new Expectations() { {
			allowing(tagDefinition).getValueType(); will(returnValue(tagValueType));
			allowing(tagValueType).getValidationConstraints(); will(returnValue(null));
			oneOf(validationService).validate(condition, null); will(returnValue(result));
		} });


		assertEquals(result, validationFacade.validate(condition));

	}

	/**
	 * Test that ensures that null logical trees as argument is not allowed.
	 */
	@Test
	public void testValidateTreeNullOperator() {

		try {

			final LogicalOperator tree = null;
			validationFacade.validateTree(tree);

			fail("Must not evaluate on null root nodes");
		} catch (IllegalArgumentException iae) {
			assertNotNull(true);
		}

	}

	/**
	 * Test that a valid condition tree will be traversed by the service and return a
	 * valid result.
	 */
	@Test
	public void testValidateTreeSuccess() {

		final TagDefinition tagDefinition11 = context.mock(TagDefinition.class, "tagDefinition11");
		final TagValueType tagValueType11 = context.mock(TagValueType.class, "tagValueType11");
		final ValidationResult result11 = context.mock(ValidationResult.class, "validationResult11");
		final Condition condition11 = new Condition(tagDefinition11, "", "");

		final TagDefinition tagDefinition12 = context.mock(TagDefinition.class, "tagDefinition12");
		final TagValueType tagValueType12 = context.mock(TagValueType.class, "tagValueType12");
		final ValidationResult result12 = context.mock(ValidationResult.class, "validationResult12");
		final Condition condition12 = new Condition(tagDefinition12, "", "");

		final TagDefinition tagDefinition21 = context.mock(TagDefinition.class, "tagDefinition21");
		final TagValueType tagValueType21 = context.mock(TagValueType.class, "tagValueType21");
		final ValidationResult result21 = context.mock(ValidationResult.class, "validationResult21");
		final Condition condition21 = new Condition(tagDefinition21, "", "");

		final TagDefinition tagDefinition31 = context.mock(TagDefinition.class, "tagDefinition31");
		final TagValueType tagValueType31 = context.mock(TagValueType.class, "tagValueType31");
		final ValidationResult result31 = context.mock(ValidationResult.class, "validationResult31");
		final Condition condition31 = new Condition(tagDefinition31, "", "");

		final LogicalOperator root = new LogicalOperator(LogicalOperatorType.AND);
		root.addCondition(condition11);
		root.addCondition(condition12);

		final LogicalOperator sub1 = new LogicalOperator(LogicalOperatorType.OR);
		sub1.addCondition(condition21);
		sub1.setParentLogicalOperator(root);

		final LogicalOperator sub2 = new LogicalOperator(LogicalOperatorType.OR);
		sub2.addCondition(condition31);
		sub2.setParentLogicalOperator(sub1);

		context.checking(new Expectations() { {
			allowing(tagDefinition11).getValueType(); will(returnValue(tagValueType11));
			allowing(tagValueType11).getValidationConstraints(); will(returnValue(null));
			oneOf(validationService).validate(condition11, null); will(returnValue(result11));
			allowing(result11).isValid(); will(returnValue(true));

			allowing(tagDefinition12).getValueType(); will(returnValue(tagValueType12));
			allowing(tagValueType12).getValidationConstraints(); will(returnValue(null));
			oneOf(validationService).validate(condition12, null); will(returnValue(result12));
			allowing(result12).isValid(); will(returnValue(true));

			allowing(tagDefinition21).getValueType(); will(returnValue(tagValueType21));
			allowing(tagValueType21).getValidationConstraints(); will(returnValue(null));
			oneOf(validationService).validate(condition21, null); will(returnValue(result21));
			allowing(result21).isValid(); will(returnValue(true));

			allowing(tagDefinition31).getValueType(); will(returnValue(tagValueType31));
			allowing(tagValueType31).getValidationConstraints(); will(returnValue(null));
			oneOf(validationService).validate(condition31, null); will(returnValue(result31));
			allowing(result31).isValid(); will(returnValue(true));

		} });


		final ValidationResult result = validationFacade.validateTree(root);
		assertTrue(result.isValid());

		assertNotNull(result.getErrors());
		final int numberOfErrors = 0;
		assertEquals(numberOfErrors, result.getErrors().length);

	}

	/**
	 * Test that a valid condition tree with an invalid condition will produce an invalid result
	 * with error being captured.
	 */
	@Test
	public void testValidateTreeFailureWithTwoFailingConditions() {

		final TagDefinition tagDefinition11 = context.mock(TagDefinition.class, "tagDefinition11");
		final TagValueType tagValueType11 = context.mock(TagValueType.class, "tagValueType11");
		final ValidationResult result11 = context.mock(ValidationResult.class, "validationResult11");
		final Condition condition11 = new Condition(tagDefinition11, "", "");

		final TagDefinition tagDefinition12 = context.mock(TagDefinition.class, "tagDefinition12");
		final TagValueType tagValueType12 = context.mock(TagValueType.class, "tagValueType12");
		final ValidationResult result12 = context.mock(ValidationResult.class, "validationResult12");
		final Condition condition12 = new Condition(tagDefinition12, "", "");

		final TagDefinition tagDefinition21 = context.mock(TagDefinition.class, "tagDefinition21");
		final TagValueType tagValueType21 = context.mock(TagValueType.class, "tagValueType21");
		final ValidationResult result21 = context.mock(ValidationResult.class, "validationResult21");
		final Condition condition21 = new Condition(tagDefinition21, "", "");

		final TagDefinition tagDefinition31 = context.mock(TagDefinition.class, "tagDefinition31");
		final TagValueType tagValueType31 = context.mock(TagValueType.class, "tagValueType31");
		final ValidationResult result31 = context.mock(ValidationResult.class, "validationResult31");
		final Condition condition31 = new Condition(tagDefinition31, "", "");

		final LogicalOperator root = new LogicalOperator(LogicalOperatorType.AND);
		root.addCondition(condition11);
		root.addCondition(condition12);

		final LogicalOperator sub1 = new LogicalOperator(LogicalOperatorType.OR);
		sub1.addCondition(condition21);
		sub1.setParentLogicalOperator(root);

		final LogicalOperator sub2 = new LogicalOperator(LogicalOperatorType.OR);
		sub2.addCondition(condition31);
		sub2.setParentLogicalOperator(sub1);

		context.checking(new Expectations() { {
			allowing(tagDefinition11).getValueType(); will(returnValue(tagValueType11));
			allowing(tagValueType11).getValidationConstraints(); will(returnValue(null));
			oneOf(validationService).validate(condition11, null); will(returnValue(result11));
			allowing(result11).isValid(); will(returnValue(true));

			allowing(tagDefinition12).getValueType(); will(returnValue(tagValueType12));
			allowing(tagValueType12).getValidationConstraints(); will(returnValue(null));
			oneOf(validationService).validate(condition12, null); will(returnValue(result12));
			allowing(result12).isValid(); will(returnValue(true));

			allowing(tagDefinition21).getValueType(); will(returnValue(tagValueType21));
			allowing(tagValueType21).getValidationConstraints(); will(returnValue(null));
			oneOf(validationService).validate(condition21, null); will(returnValue(result21));
			allowing(result21).isValid(); will(returnValue(false));
			allowing(result21).getMessage(); will(returnValue(ERROR));

			allowing(tagDefinition31).getValueType(); will(returnValue(tagValueType31));
			allowing(tagValueType31).getValidationConstraints(); will(returnValue(null));
			oneOf(validationService).validate(condition31, null); will(returnValue(result31));
			allowing(result31).isValid(); will(returnValue(false));
			allowing(result31).getMessage(); will(returnValue(ERROR2));

		} });


		final ValidationResult result = validationFacade.validateTree(root);
		assertFalse(result.isValid());

		assertNotNull(result.getErrors());
		final int numberOfErrors = 2;
		assertEquals(numberOfErrors, result.getErrors().length);
		assertEquals(ERROR, result.getErrors()[0].getMessage());
		assertEquals(ERROR2, result.getErrors()[1].getMessage());

	}

	/**
	 * Test that a valid condition tree with an invalid condition will produce an invalid result
	 * with error being captured.
	 */
	@Test
	public void testValidateTreeFailure() {

		final TagDefinition tagDefinition11 = context.mock(TagDefinition.class, "tagDefinition11");
		final TagValueType tagValueType11 = context.mock(TagValueType.class, "tagValueType11");
		final ValidationResult result11 = context.mock(ValidationResult.class, "validationResult11");
		final Condition condition11 = new Condition(tagDefinition11, "", "");

		final TagDefinition tagDefinition12 = context.mock(TagDefinition.class, "tagDefinition12");
		final TagValueType tagValueType12 = context.mock(TagValueType.class, "tagValueType12");
		final ValidationResult result12 = context.mock(ValidationResult.class, "validationResult12");
		final Condition condition12 = new Condition(tagDefinition12, "", "");

		final TagDefinition tagDefinition21 = context.mock(TagDefinition.class, "tagDefinition21");
		final TagValueType tagValueType21 = context.mock(TagValueType.class, "tagValueType21");
		final ValidationResult result21 = context.mock(ValidationResult.class, "validationResult21");
		final Condition condition21 = new Condition(tagDefinition21, "", "");

		final TagDefinition tagDefinition31 = context.mock(TagDefinition.class, "tagDefinition31");
		final TagValueType tagValueType31 = context.mock(TagValueType.class, "tagValueType31");
		final ValidationResult result31 = context.mock(ValidationResult.class, "validationResult31");
		final Condition condition31 = new Condition(tagDefinition31, "", "");

		final LogicalOperator root = new LogicalOperator(LogicalOperatorType.AND);
		root.addCondition(condition11);
		root.addCondition(condition12);

		final LogicalOperator sub1 = new LogicalOperator(LogicalOperatorType.OR);
		sub1.addCondition(condition21);
		sub1.setParentLogicalOperator(root);

		final LogicalOperator sub2 = new LogicalOperator(LogicalOperatorType.OR);
		sub2.addCondition(condition31);
		sub2.setParentLogicalOperator(sub1);

		context.checking(new Expectations() { {
			allowing(tagDefinition11).getValueType(); will(returnValue(tagValueType11));
			allowing(tagValueType11).getValidationConstraints(); will(returnValue(null));
			oneOf(validationService).validate(condition11, null); will(returnValue(result11));
			allowing(result11).isValid(); will(returnValue(true));

			allowing(tagDefinition12).getValueType(); will(returnValue(tagValueType12));
			allowing(tagValueType12).getValidationConstraints(); will(returnValue(null));
			oneOf(validationService).validate(condition12, null); will(returnValue(result12));
			allowing(result12).isValid(); will(returnValue(true));

			allowing(tagDefinition21).getValueType(); will(returnValue(tagValueType21));
			allowing(tagValueType21).getValidationConstraints(); will(returnValue(null));
			oneOf(validationService).validate(condition21, null); will(returnValue(result21));
			allowing(result21).isValid(); will(returnValue(false));
			allowing(result21).getMessage(); will(returnValue(ERROR));

			allowing(tagDefinition31).getValueType(); will(returnValue(tagValueType31));
			allowing(tagValueType31).getValidationConstraints(); will(returnValue(null));
			oneOf(validationService).validate(condition31, null); will(returnValue(result31));
			allowing(result31).isValid(); will(returnValue(true));

		} });


		final ValidationResult result = validationFacade.validateTree(root);
		assertFalse(result.isValid());

		assertNotNull(result.getErrors());
		final int numberOfErrors = 1;
		assertEquals(numberOfErrors, result.getErrors().length);
		assertEquals(ERROR, result.getErrors()[0].getMessage());

	}


}
