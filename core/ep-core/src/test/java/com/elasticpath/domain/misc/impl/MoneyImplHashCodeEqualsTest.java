/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.domain.misc.impl;

import static com.elasticpath.test.util.AssertHashCodeEquals.assertNonEquivalence;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertNullity;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertReflexivity;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertSymmetry;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertTransitivity;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;

import com.elasticpath.domain.misc.Money;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;

/**
 * Test the specific behaviour of the hash code / equals contract in {@link MoneyImpl}.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class MoneyImplHashCodeEqualsTest {

	private static final Currency CAD = Currency.getInstance(Locale.CANADA);
	private static final Currency USD = Currency.getInstance(Locale.US);

	/**
	 * Test reflexivity - no comparison fields populated.
	 */
	@Test
	public void testReflexivityNoEqualsComparatorsPopulated() {
		assertReflexivity(MoneyFactory.createMoney(null, null));
	}

	/**
	 * Test reflexivity - all comparison fields populated and equal.
	 */
	@Test
	public void testReflexivityAllFieldsPopulated() {
		assertReflexivity(createPopulatedMoney());
	}

	/**
	 * Test symmetry - no comparison fields populated.<br>
	 */
	@Test
	public void testSymmetryNoEqualsComparatorsPopulated() {
		assertSymmetry(MoneyFactory.createMoney(null, null), MoneyFactory.createMoney(null, null));
	}

	/**
	 * Test symmetry - all comparison fields populated and equal.
	 */
	@Test
	public void testSymmetryAllFieldsPopulated() {
		assertSymmetry(createPopulatedMoney(), createPopulatedMoney());
	}

	/**
	 * Test symmetry - all comparison fields populated except for one which is not equal.
	 */
	@Test
	public void testSymmetryAllFieldsPopulatedWithOneNotEqual() {
		Money obj1 = MoneyFactory.createMoney(BigDecimal.ZERO, USD);
		Money obj2 = createPopulatedMoney();
		assertNonEquivalence(obj1, obj2);
	}

	/**
	 * Test transitivity - no comparison fields populated.
	 */
	@Test
	public void testTransitivityNoEqualsComparitorsPopulated() {
		assertTransitivity(MoneyFactory.createMoney(null, null), MoneyFactory.createMoney(null, null), MoneyFactory.createMoney(null, null));
	}

	/**
	 * Test transitivity - all comparison fields populated and equal.
	 */
	@Test
	public void testTransitivityAllFieldsPopulated() {
		Money obj1 = createPopulatedMoney();
		Money obj2 = createPopulatedMoney();
		Money obj3 = createPopulatedMoney();
		assertTransitivity(obj1, obj2, obj3);
	}

	/**
	 * Test any non-null reference value. <br>
	 * <code>x.equals(null)</code> should return <code>false</code>
	 */
	@Test
	public void testAnyNonNullReferenceValue() {
		assertNullity(MoneyFactory.createMoney(null, null));
	}

	/**
	 * Test that using equals against a different object returns false.
	 */
	@Test
	public void testAgainstNonEquivalentObjects() {
		assertNonEquivalence(MoneyFactory.createMoney(null, null), new TaxCalculationResultImpl());
	}

	private Money createPopulatedMoney() {
		return MoneyFactory.createMoney(new BigDecimal("0.00"), CAD);
	}

}
