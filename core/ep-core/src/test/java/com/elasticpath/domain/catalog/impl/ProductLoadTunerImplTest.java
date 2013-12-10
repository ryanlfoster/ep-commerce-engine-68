package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductTypeLoadTuner;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>ProductLoadTunerImpl</code>.
 */
public class ProductLoadTunerImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Set up required before each test.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductLoadTunerImpl.contains(ProductLoadTuner)'.
	 */
	@Test
	public void testContains() {
		final ProductLoadTuner loadTuner1 = new ProductLoadTunerImpl();
		final ProductLoadTuner loadTuner2 = new ProductLoadTunerImpl();

		// Always contains a <code>null<code> tuner.
		assertTrue(loadTuner1.contains(null));

		// Empty load tuner contains each other.
		assertTrue(loadTuner1.contains(loadTuner2));
		assertTrue(loadTuner2.contains(loadTuner1));

		// Load tuner 1 has more flags set than load tuner 2
		loadTuner1.setLoadingAttributeValue(true);
		assertTrue(loadTuner1.contains(loadTuner2));
		assertFalse(loadTuner2.contains(loadTuner1));

		// Load tuner 1 has more flags set than load tuner 2
		loadTuner1.setLoadingCategories(true);
		loadTuner1.setLoadingDefaultCategory(true);
		loadTuner1.setLoadingDefaultSku(true);
		loadTuner1.setLoadingProductType(true);
		loadTuner1.setLoadingSkus(true);

		loadTuner2.setLoadingCategories(true);
		loadTuner2.setLoadingDefaultCategory(true);
		loadTuner2.setLoadingDefaultSku(true);
		loadTuner2.setLoadingProductType(true);
		loadTuner2.setLoadingSkus(true);

		assertTrue(loadTuner1.contains(loadTuner2));
		assertFalse(loadTuner2.contains(loadTuner1));

		// Load tuner 2 has a product type load tuner
		final ProductTypeLoadTuner productTypeLoadTuner = setupProductTypeLoadTuner();
		loadTuner2.setProductTypeLoadTuner(productTypeLoadTuner);
		assertFalse(loadTuner1.contains(loadTuner2));
		assertFalse(loadTuner2.contains(loadTuner1));

		// Load tuner 1 and 2 both have a product type load tuner
		loadTuner1.setProductTypeLoadTuner(productTypeLoadTuner);
		assertTrue(loadTuner1.contains(loadTuner2));
		assertFalse(loadTuner2.contains(loadTuner1));
	}

	private ProductTypeLoadTuner setupProductTypeLoadTuner() {
		final ProductTypeLoadTuner loadTuner = new ProductTypeLoadTunerImpl();
		loadTuner.setLoadingAttributes(true);
		loadTuner.setLoadingSkuOptions(true);
		return loadTuner;
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductLoadTunerImpl.merge(ProductLoadTuner)'.
	 */
	@Test
	public void testMerge() {
		expectationsFactory.allowingBeanFactoryGetBean("productSkuLoadTuner", ProductSkuLoadTunerImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("categoryLoadTuner", CategoryLoadTunerImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("productTypeLoadTuner", ProductTypeLoadTunerImpl.class);

		final ProductLoadTuner loadTuner1 = new ProductLoadTunerImpl();
		final ProductLoadTuner loadTuner2 = new ProductLoadTunerImpl();

		// Merge null doesn't change anything
		loadTuner1.merge(null);
		assertTrue(loadTuner1.contains(loadTuner2));
		assertTrue(loadTuner2.contains(loadTuner1));

		// Load tuner 1 contains 2, we will just return load tuner 1
		loadTuner1.setLoadingAttributeValue(true);
		ProductLoadTuner loadTuner3 = loadTuner2.merge(loadTuner1);
		assertSame(loadTuner3, loadTuner1);

		// Load tuner 1 and 2 have different flags set
		loadTuner2.setLoadingCategories(true);

		// Merge tuner 1 to tuner 2
		loadTuner3 = loadTuner2.merge(loadTuner1);
		assertTrue(loadTuner3.contains(loadTuner1));
		assertTrue(loadTuner3.contains(loadTuner2));

		// Load tuner 2 has a product type load tuner
		final ProductTypeLoadTuner productTypeLoadTuner = setupProductTypeLoadTuner();
		loadTuner2.setProductTypeLoadTuner(productTypeLoadTuner);

		// Merge load tuner 2 into 1
		loadTuner3 = loadTuner1.merge(loadTuner2);
		assertTrue(loadTuner3.contains(loadTuner1));
		assertTrue(loadTuner3.contains(loadTuner2));
	}
}
