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
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.CategoryTypeLoadTuner;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test class for {@link CategoryLoadTunerImpl}.
 */
public class CategoryLoadTunerImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Setup for the tests.
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
	 * Test method for {@link CategoryLoadTunerImpl#contains(CategoryLoadTuner)}.
	 */
	@Test
	public void testContains() {
		final CategoryLoadTuner loadTuner1 = new CategoryLoadTunerImpl();
		final CategoryLoadTuner loadTuner2 = new CategoryLoadTunerImpl();

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
		loadTuner1.setLoadingCategoryType(true);
		loadTuner1.setLoadingChildren(true);
		loadTuner1.setLoadingLocaleDependantFields(true);
		loadTuner1.setLoadingMaster(true);
		loadTuner1.setLoadingParent(true);

		loadTuner2.setLoadingCategoryType(true);
		loadTuner2.setLoadingChildren(true);
		loadTuner2.setLoadingLocaleDependantFields(true);
		loadTuner2.setLoadingMaster(true);
		loadTuner2.setLoadingParent(true);

		assertTrue(loadTuner1.contains(loadTuner2));
		assertFalse(loadTuner2.contains(loadTuner1));

		// Load tuner 2 has a product type load tuner
		final CategoryTypeLoadTuner categoryTypeLoadTuner = setupCategoryTypeLoadTuner();
		loadTuner2.setCategoryTypeLoadTuner(categoryTypeLoadTuner);
		assertFalse(loadTuner1.contains(loadTuner2));
		assertFalse(loadTuner2.contains(loadTuner1));

		// Load tuner 1 and 2 both have a product type load tuner
		loadTuner1.setCategoryTypeLoadTuner(categoryTypeLoadTuner);
		assertTrue(loadTuner1.contains(loadTuner2));
		assertFalse(loadTuner2.contains(loadTuner1));
	}

	private CategoryTypeLoadTuner setupCategoryTypeLoadTuner() {
		final CategoryTypeLoadTuner loadTuner = new CategoryTypeLoadTunerImpl();
		loadTuner.setLoadingAttributes(true);
		return loadTuner;
	}

	/**
	 * Test method for {@link CategoryLoadTunerImpl#merge(CategoryLoadTuner)}.
	 */
	@Test
	public void testMerge() {

		expectationsFactory.allowingBeanFactoryGetBean("categoryTypeLoadTuner", CategoryTypeLoadTunerImpl.class);

		final CategoryLoadTuner loadTuner1 = new CategoryLoadTunerImpl();
		final CategoryLoadTuner loadTuner2 = new CategoryLoadTunerImpl();

		// Merge null doesn't change anything
		loadTuner1.merge(null);
		assertTrue(loadTuner1.contains(loadTuner2));
		assertTrue(loadTuner2.contains(loadTuner1));

		// Load tuner 1 contains 2, we will just return load tuner 1
		loadTuner1.setLoadingAttributeValue(true);
		CategoryLoadTuner loadTuner3 = loadTuner2.merge(loadTuner1);
		assertSame(loadTuner3, loadTuner1);

		// Load tuner 1 and 2 have different flags set
		loadTuner2.setLoadingMaster(true);

		// Merge tuner 1 to tuner 2
		loadTuner3 = loadTuner2.merge(loadTuner1);
		assertTrue(loadTuner3.contains(loadTuner1));
		assertTrue(loadTuner3.contains(loadTuner2));

		// Load tuner 2 has a product type load tuner
		final CategoryTypeLoadTuner categoryTypeLoadTuner = setupCategoryTypeLoadTuner();
		loadTuner2.setCategoryTypeLoadTuner(categoryTypeLoadTuner);

		// Merge load tuner 2 into 1
		loadTuner3 = loadTuner1.merge(loadTuner2);
		assertTrue(loadTuner3.contains(loadTuner1));
		assertTrue(loadTuner3.contains(loadTuner2));
	}
}
