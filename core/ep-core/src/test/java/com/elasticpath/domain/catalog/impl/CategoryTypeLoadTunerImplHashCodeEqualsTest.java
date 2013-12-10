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
 * Test the specific behaviour of the hash code / equals contract in {@link CategoryTypeLoadTunerImpl}.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class CategoryTypeLoadTunerImplHashCodeEqualsTest {

	private CategoryTypeLoadTunerImpl obj1;

	private CategoryTypeLoadTunerImpl obj2;

	private CategoryTypeLoadTunerImpl obj3;

	/**
	 * Sets up the test case for execution.
	 */
	@Before
	public void setUp() {
		obj1 = new CategoryTypeLoadTunerImpl();
		obj2 = new CategoryTypeLoadTunerImpl();
		obj3 = new CategoryTypeLoadTunerImpl();
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
		populateCategoryTypeLoadTuner(obj1);
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
		populateCategoryTypeLoadTuner(obj1);
		populateCategoryTypeLoadTuner(obj2);
		assertSymmetry(obj1, obj2);
	}

	/**
	 * Test symmetry - all comparison fields populated except for one which is not equal.
	 */
	@Test
	public void testSymmetryAllFieldsPopulatedWithOneNotEqual() {
		populateCategoryTypeLoadTuner(obj1);
		obj1.setLoadingAttributes(true);
		populateCategoryTypeLoadTuner(obj2);
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
		populateCategoryTypeLoadTuner(obj1);
		populateCategoryTypeLoadTuner(obj2);
		populateCategoryTypeLoadTuner(obj3);
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

	private void populateCategoryTypeLoadTuner(final CategoryTypeLoadTunerImpl loadTuner) {
		loadTuner.setLoadingAttributes(false);
	}
}
