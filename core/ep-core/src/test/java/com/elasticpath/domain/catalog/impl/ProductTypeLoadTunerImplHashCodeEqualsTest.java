/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.domain.catalog.impl;

import static com.elasticpath.test.util.AssertHashCodeEquals.assertNonEquivalence;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertNullity;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertReflexivity;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertSymmetry;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertTransitivity;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the specific behaviour of the hash code / equals contract in {@link ProductTypeLoadTunerImpl}.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class ProductTypeLoadTunerImplHashCodeEqualsTest {

	private ProductTypeLoadTunerImpl obj1;

	private ProductTypeLoadTunerImpl obj2;

	private ProductTypeLoadTunerImpl obj3;

	/**
	 * Sets up the test case for execution.
	 */
	@Before
	public void setUp() {
		obj1 = new ProductTypeLoadTunerImpl();
		obj2 = new ProductTypeLoadTunerImpl();
		obj3 = new ProductTypeLoadTunerImpl();
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
		populateProductTypeLoadTuner(obj1);
		assertReflexivity(obj1);
	}

	/**
	 * Test symmetry - no comparison fields populated.<br>
	 */
	@Test
	public void testSymmetryNoEqualsComparitorsPopulated() {
		assertSymmetry(obj1, obj2);
	}

	/**
	 * Test symmetry - all comparison fields populated and equal.
	 */
	@Test
	public void testSymmetryAllFieldsPopulated() {
		populateProductTypeLoadTuner(obj1);
		populateProductTypeLoadTuner(obj2);
		assertSymmetry(obj1, obj2);
	}

	/**
	 * Test symmetry - all comparison fields populated except for one which is not equal.
	 */
	@Test
	public void testSymmetryAllFieldsPopulatedWithOneNotEqual() {
		populateProductTypeLoadTuner(obj1);
		obj1.setLoadingAttributes(true);
		populateProductTypeLoadTuner(obj2);
		assertNonEquivalence(obj1, obj2);
	}

	/**
	 * Test transitivity - no comparison fields populated.
	 */
	@Test
	public void testTransitivityNoEqualsComparitorsPopulated() {
		assertTransitivity(obj1, obj2, obj3);
	}

	/**
	 * Test transitivity - all comparison fields populated and equal.
	 */
	@Test
	public void testTransitivityAllFieldsPopulated() {
		populateProductTypeLoadTuner(obj1);
		populateProductTypeLoadTuner(obj2);
		populateProductTypeLoadTuner(obj3);
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
		assertNonEquivalence(obj1, new ProductLoadTunerImpl());
	}

	private void populateProductTypeLoadTuner(final ProductTypeLoadTunerImpl loadTuner) {
		loadTuner.setLoadingAttributes(false);
		loadTuner.setLoadingSkuOptions(false);
	}
}
