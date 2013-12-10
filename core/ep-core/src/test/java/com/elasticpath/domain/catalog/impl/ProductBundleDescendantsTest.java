package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * BundleVerifierImplTest.
 *
 */
public class ProductBundleDescendantsTest {

	private static final String PRODUCT = "X";
	private static final String BUNDLE = "BUNDLE";

	private Product productx;

	private ProductBundle topBundle;
	private ProductBundle bundlex;
	private BundleConstituent productxConstituent;
	private BundleConstituent bundleConstituent;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private BeanFactoryExpectationsFactory expectationsFactory;

	/** {@inheritDoc}
	 *
	 * Creates two bundle constituents;
	 * one is a simple product and the other is a bundleProduct.
	 *
	 * Creates a Bundle (topBundle) with no constituents.
	 */
	@Before
	public void setUp() {
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);

		bundlex = new ProductBundleImpl();
		bundlex.setGuid(BUNDLE);

		productx = createProduct(PRODUCT);

		productxConstituent = new BundleConstituentImpl();
		productxConstituent.setConstituent(productx);

		bundleConstituent = new BundleConstituentImpl();
		bundleConstituent.setConstituent(bundlex);

		topBundle = new ProductBundleImpl();

	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/** . */
	@Test
	public void testSameProduct() {
		assertTrue("A product has itself among its descendants.",
				bundlex.hasDescendant(bundlex));
	}

	/**
	 * Tests that ProductX is a descendant of BUNDLEX in
	 * the following case:
	 *
	 * BUNDLEX --
	 * 			- ProductX.
	 * */
	@Test
	public void testConstituentExistsFirstLevel() {
		bundlex.addConstituent(productxConstituent);
		assertTrue("A bundle with product x as a constituent has product x as a descendant.",
				bundlex.hasDescendant(productx));
	}

	/**
	 * Tests that ProductX is a descendant of TOPBUNDLE in
	 * the following case:
	 *
	 * TOPBUNDLE ----
	 * 				- BUNDLEX
	 * 						- PRODUCTX.
	 * */
	@Test
	public void testConstituentExistsSecondLevel() {
		topBundle.addConstituent(bundleConstituent);
		bundlex.addConstituent(productxConstituent);
		assertTrue(topBundle.hasDescendant(productx));
	}

	/**
	 * Tests that ProductX is a descendant of BUNDLEX in the following case:
	 * BUNDLEX --
	 * 			- PRODUCT
	 * 			- PRODUCTX.
	 */
	@Test
	public void testSecondConstituentFirstLevel() {
		BundleConstituent productConstituentBefore = new BundleConstituentImpl();
		productConstituentBefore.setConstituent(createProduct(""));

		bundlex.addConstituent(productConstituentBefore);
		bundlex.addConstituent(productxConstituent);

		assertTrue(bundlex.hasDescendant(productx));
	}

	/**
	 * Tests that ProductX is NOT a descendant of BundleX in the following case:
	 * BUNDLEX --.
	 */
	@Test
	public void testConstituentDoesNotExists() {
		assertFalse(bundlex.hasDescendant(productx));
	}

	private Product createProduct(final String productGuid) {
		productx = new ProductBundleImpl();
		productx.setGuid(productGuid);
		return productx;
	}
}
