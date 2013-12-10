/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.service.tax.impl;

import static com.elasticpath.test.util.AssertHashCodeEquals.assertNonEquivalence;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertNullity;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertReflexivity;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertSymmetry;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertTransitivity;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.impl.TaxCategoryImpl;

/**
 * Test the specific behaviour of the hash code / equals contract in {@link TaxCalculationResultImpl}.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class TaxCalculationResultImplHashCodeEqualsTest {

	private static final String ZERO_AMOUNT = "0.00";

	private static final Currency CAD = Currency.getInstance(Locale.CANADA);

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private TaxCalculationResultImpl obj1;

	private TaxCalculationResultImpl obj2;

	private TaxCalculationResultImpl obj3;

	private final ElasticPath elasticpath = context.mock(ElasticPath.class);

	/**
	 * Sets up the test case for execution.
	 */
	@Before
	public void setUp() {
		context.setImposteriser(ClassImposteriser.INSTANCE);
		obj1 = new TaxCalculationResultImpl();
		obj2 = new TaxCalculationResultImpl();
		obj3 = new TaxCalculationResultImpl();
		context.checking(new Expectations() {
			{
				allowing(elasticpath).getBean(ContextIdNames.LOCALIZED_PROPERTIES);
				will(returnValue(new LocalizedPropertiesImpl()));
			}
		});
	}

	/**
	 * Test reflexivity - no comparison fields populated.
	 */
	@Test
	public void testReflexivityNoEqualsComparitorsPopulated() {
		assertReflexivity(obj1);
	}

	/**
	 * Test reflexivity - all comparison fields populated and equal.
	 */
	@Test
	public void testReflexivityAllFieldsPopulated() {
		populateTaxCalculationResult(obj1);
		assertReflexivity(obj1);
	}

	/**
	 * Test symmetry - no comparison fields populated.<br>
	 * A side effect of doing this is that {@link TaxCalculationResultImpl#getBeforeTaxSubTotal()} and
	 * {@link TaxCalculationResultImpl#getBeforeTaxShippingCost() populates a new Money instance if the associated fields are found to be null, <br>
	 * and throws an error if the default currency is not set.<br>
	 * So we have to manage that as a minimum requirement.
	 */
	@Test
	public void testSymmetryNoEqualsComparitorsPopulated() {
		obj1.setDefaultCurrency(CAD);
		obj2.setDefaultCurrency(CAD);
		assertSymmetry(obj1, obj2);
	}

	/**
	 * Test symmetry - all comparison fields populated and equal.
	 */
	@Test
	public void testSymmetryAllFieldsPopulated() {
		populateTaxCalculationResult(obj1);
		populateTaxCalculationResult(obj2);
		assertSymmetry(obj1, obj2);
	}

	/**
	 * Test symmetry - all comparison fields populated except for one which is not equal.
	 */
	@Test
	public void testSymmetryAllFieldsPopulatedWithOneNotEqual() {
		populateTaxCalculationResult(obj1);
		obj1.setTaxValue(null, null);
		populateTaxCalculationResult(obj2);
		assertNonEquivalence(obj1, obj2);
	}

	/**
	 * Test transitivity - no comparison fields populated.
	 */
	@Test
	public void testTransitivityNoEqualsComparitorsPopulated() {
		// need to define default currency for each object to calculate getMoneyZero when
		// getBeforeTaxShippingCost() and getBeforeTaxSubTotal() are null as these are included in the equals test
		obj1.setDefaultCurrency(CAD);
		obj2.setDefaultCurrency(CAD);
		obj3.setDefaultCurrency(CAD);
		assertTransitivity(obj1, obj2, obj3);
	}

	/**
	 * Test transitivity - all comparison fields populated and equal.
	 */
	@Test
	public void testTransitivityAllFieldsPopulated() {
		populateTaxCalculationResult(obj1);
		populateTaxCalculationResult(obj2);
		populateTaxCalculationResult(obj3);
		assertTransitivity(obj1, obj2, obj3);
	}

	/**
	 * Test any non-null reference value. <br>
	 * <code>x.equals(null)</code> should return <code>false</code>
	 */
	@Test
	public void testAnyNonNullReferenceValue() {
		assertNullity(obj1);
	}

	/**
	 * Test that using equals against a different object returns false.
	 */
	@Test
	public void testAgainstNonEquivalentObjects() {
		assertNonEquivalence(obj1, new Object());
	}

	private TaxCategoryImpl createStubTaxCategory() {
		TaxCategoryImpl result = new TaxCategoryImpl() {

			private static final long serialVersionUID = 1L;

			@Override
			public ElasticPath getElasticPath() {
				return elasticpath;
			}
		};

		return result;
	}

	private void populateTaxCalculationResult(final TaxCalculationResultImpl result) {
		populateBeforeTaxShippingCost(result);

		populateBeforeTaxSubTotal(result);

		result.setDefaultCurrency(CAD);

		populateShippingTax(result);

		populateTaxInItemPrice(result);

		result.setTaxInclusive(false);

		populateTaxValue(result);
	}

	private void populateTaxInItemPrice(final TaxCalculationResultImpl result) {
		Money taxInItemPrice = createMoneyImpl(ZERO_AMOUNT, CAD);
		result.setTaxInItemPrice(taxInItemPrice);
	}

	private void populateTaxValue(final TaxCalculationResultImpl result) {
		TaxCategory taxCategory = createStubTaxCategory();
		Money amount = createMoneyImpl(ZERO_AMOUNT, CAD);
		result.setTaxValue(taxCategory, amount);
	}

	private void populateShippingTax(final TaxCalculationResultImpl result) {
		Money shippingTax = createMoneyImpl(ZERO_AMOUNT, CAD);
		result.addShippingTax(shippingTax);
	}

	private void populateBeforeTaxShippingCost(final TaxCalculationResultImpl result) {
		Money beforeTaxShippingCost = createMoneyImpl(ZERO_AMOUNT, CAD);
		result.setBeforeTaxShippingCost(beforeTaxShippingCost);
	}

	private void populateBeforeTaxSubTotal(final TaxCalculationResultImpl result) {
		Money beforeTaxSubTotal = createMoneyImpl(ZERO_AMOUNT, CAD);
		result.setBeforeTaxSubTotal(beforeTaxSubTotal);
	}

	private Money createMoneyImpl(final String amount, final Currency currency) {
		return MoneyFactory.createMoney(new BigDecimal(amount), currency);
	}

}
