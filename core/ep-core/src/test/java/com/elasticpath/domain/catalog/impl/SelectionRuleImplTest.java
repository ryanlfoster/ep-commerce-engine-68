package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.SelectionRule;

/**
 * Test that {@code SelectionRuleImpl} behaves as expected.
 */
public class SelectionRuleImplTest {

	private ProductBundle bundle1;
	private ProductBundle bundle2;
	
	/**
	 * Setup required for each test.
	 */
	@Before
	public void setUp() {
		bundle1 = new ProductBundleImpl();
		bundle2 = new ProductBundleImpl();
		bundle1.setCode("BUNDLE1");
		bundle1.setCode("BUNDLE2");
	}

	/**
	 * Test that an object equals itself.
	 */
	@Test
	public void testEqualsSameObject() {
		SelectionRule selectionRule = new SelectionRuleImpl();
		selectionRule.setBundle(bundle1);
		assertEquals("An object should equal itself", selectionRule, selectionRule);
	}
	
	/**
	 * Test different select rules with the same bundle are equal.
	 */
	@Test
	public void testEqualsDifferentObjectSameBundle() {
		SelectionRule selectionRule1 = new SelectionRuleImpl();
		SelectionRule selectionRule2 = new SelectionRuleImpl();
		selectionRule1.setBundle(bundle1);
		selectionRule2.setBundle(bundle1);
		assertEquals("Different objects should be equal if the bundle is the same", selectionRule1, selectionRule2);
	}
	
	/**
	 * Test different selection rules with different bundles are not equal.
	 */
	@Test
	public void testNotEqualsDifferentBundles() {
		SelectionRule selectionRule1 = new SelectionRuleImpl();
		SelectionRule selectionRule2 = new SelectionRuleImpl();
		selectionRule1.setBundle(bundle1);
		selectionRule2.setBundle(bundle2);
		assertFalse("Selection Rules with different bundles should not be equal", selectionRule1.equals(selectionRule2));
		assertFalse("Selection Rules with different bundles should not be equal", selectionRule2.equals(selectionRule1));
	}

	/**
	 * Test hash code is the same for equal objects.
	 */
	@Test
	public void testHashCodeForEqualObjects() {
		SelectionRule selectionRule1 = new SelectionRuleImpl();
		SelectionRule selectionRule2 = new SelectionRuleImpl();
		selectionRule1.setBundle(bundle1);
		selectionRule2.setBundle(bundle1);
		assertEquals("Equal objects should have the same hashcode", selectionRule1.hashCode(), selectionRule2.hashCode());
	}
}

