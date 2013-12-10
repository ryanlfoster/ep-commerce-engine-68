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

import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.ProductSkuLoadTuner;
import com.elasticpath.domain.catalog.ProductTypeLoadTuner;

/**
 * Test the specific behaviour of the hash code / equals contract in {@link ProductLoadTunerImpl}.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class ProductLoadTunerImplHashCodeEqualsTest {

	private ProductLoadTunerImpl obj1;

	private ProductLoadTunerImpl obj2;

	private ProductLoadTunerImpl obj3;

	/**
	 * Sets up the test case for execution.
	 */
	@Before
	public void setUp() {
		obj1 = new ProductLoadTunerImpl();
		obj2 = new ProductLoadTunerImpl();
		obj3 = new ProductLoadTunerImpl();
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
		populateProductLoadTuner(obj1);
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
		populateProductLoadTuner(obj1);
		populateProductLoadTuner(obj2);
		assertSymmetry(obj1, obj2);
	}

	/**
	 * Test symmetry - all comparison fields populated except for one which is not equal.
	 */
	@Test
	public void testSymmetryAllFieldsPopulatedWithOneNotEqual() {
		populateProductLoadTuner(obj1);
		obj1.setLoadingAttributeValue(true);
		populateProductLoadTuner(obj2);
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
		populateProductLoadTuner(obj1);
		populateProductLoadTuner(obj2);
		populateProductLoadTuner(obj3);
		assertTransitivity(obj1, obj2, obj3);
	}

	/**
	 * Test any non-null reference value. <br>
	 * <code>x.equals(null)</code> should return <code>false</code>
	 */
	@SuppressWarnings({ "PMD.EqualsNull", "PMD.PositionLiteralsFirstInComparisons" })
	@Test
	public void testAnyNonNullReferenceValue() {
		assertNullity(obj1);
	}

	/**
	 * Test that using equals against a different object returns false.
	 */
	@Test
	public void testAgainstNonEquivalentObjects() {
		assertNonEquivalence(obj1, new StoreProductLoadTunerImpl());
	}

	private void populateProductLoadTuner(final ProductLoadTunerImpl loadTuner) {
		loadTuner.setLoadingAttributeValue(false);
		loadTuner.setLoadingCategories(false);
		loadTuner.setLoadingDefaultCategory(false);
		loadTuner.setLoadingDefaultSku(false);
		loadTuner.setLoadingProductType(false);
		loadTuner.setLoadingSkus(false);

		ProductSkuLoadTuner productSkuLoadTuner = new ProductSkuLoadTunerImpl();
		loadTuner.setProductSkuLoadTuner(productSkuLoadTuner);

		CategoryLoadTuner categoryLoadTuner = new CategoryLoadTunerImpl();
		loadTuner.setCategoryLoadTuner(categoryLoadTuner);

		ProductTypeLoadTuner productTypeLoadTuner = new ProductTypeLoadTunerImpl();
		loadTuner.setProductTypeLoadTuner(productTypeLoadTuner);
	}

}
