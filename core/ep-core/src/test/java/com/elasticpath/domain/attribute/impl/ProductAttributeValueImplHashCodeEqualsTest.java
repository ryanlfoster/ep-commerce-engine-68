package com.elasticpath.domain.attribute.impl;

import static com.elasticpath.test.util.AssertHashCodeEquals.assertNonEquivalence;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertNullity;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertReflexivity;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertSymmetry;
import static com.elasticpath.test.util.AssertHashCodeEquals.assertTransitivity;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the specific behaviour of the hash code / equals contract in {@link ProductAttributeValueImpl}.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class ProductAttributeValueImplHashCodeEqualsTest extends AbstractAttributeValueImplHashCodeEqualsTest {

	private ProductAttributeValueImpl obj1;

	private ProductAttributeValueImpl obj2;

	private ProductAttributeValueImpl obj3;

	/**
	 * Sets up the test case for execution.
	 */
	@Before
	public void setUp() {
		obj1 = new ProductAttributeValueImpl();
		obj2 = new ProductAttributeValueImpl();
		obj3 = new ProductAttributeValueImpl();
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
		populateAbstractAttributeValue(obj1);
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
		populateAbstractAttributeValue(obj1);
		populateAbstractAttributeValue(obj2);
		assertSymmetry(obj1, obj2);
	}

	/**
	 * Test symmetry - all comparison fields populated except for one which is not equal.
	 */
	@Test
	public void testSymmetryAllFieldsPopulatedWithOneNotEqual() {
		populateAbstractAttributeValue(obj1);
		obj1.setBooleanValue(true);
		populateAbstractAttributeValue(obj2);
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
		populateAbstractAttributeValue(obj1);
		populateAbstractAttributeValue(obj2);
		populateAbstractAttributeValue(obj3);
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
		assertNonEquivalence(obj1, new CategoryAttributeValueImpl());
	}
}