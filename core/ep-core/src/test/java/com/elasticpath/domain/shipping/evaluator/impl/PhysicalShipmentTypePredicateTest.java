/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.domain.shipping.evaluator.impl;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Test {@link PhysicalShipmentTypePredicate} functionality.
 */
public class PhysicalShipmentTypePredicateTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PhysicalShipmentTypePredicate predicate;

	/**
	 * Setup method to be called before any test methods.
	 */
	@Before
	public void setUp() {
		predicate = new PhysicalShipmentTypePredicate();
	}

	/**
	 * Tests whether the predicate accepts non ShoppingItem classes.
	 */
	@Test(expected = ClassCastException.class)
	public void testEvaluateObjectCCE() {
		predicate.evaluate(new Object());
	}

	/**
	 * Test predicate evaluation with true result.
	 */
	@Test
	public void testPredicateEvaluationWithTrueResult() {
		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);

		context.checking(new Expectations() {
			{
				oneOf(shoppingItem).isShippable();
				will(returnValue(true));
			}
		});
		boolean result = predicate.evaluate(shoppingItem);
		assertTrue("Evaluation should return true", result);
	}

	/**
	 * Test predicate evaluation with false result.
	 */
	@Test
	public void testPredicateEvaluationWithFalseResult() {
		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);

		context.checking(new Expectations() {
			{
				oneOf(shoppingItem).isShippable();
				will(returnValue(false));
			}
		});
		boolean result = predicate.evaluate(shoppingItem);
		assertFalse("Evaluation should return false", result);
	}


}
