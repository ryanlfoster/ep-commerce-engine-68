package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elasticpath.domain.catalog.CategoryTypeLoadTuner;

/**
 * Test case for {@link CategoryTypeLoadTunerImpl}.
 */
public class CategoryTypeLoadTunerImplTest {


	/**
	 * Test method for {@link CategoryTypeLoadTunerImpl#contains(CategoryTypeLoadTuner)}.
	 */
	@Test
	public void testContains() {
		final CategoryTypeLoadTuner loadTuner1 = new CategoryTypeLoadTunerImpl();
		assertTrue(loadTuner1.contains(null));

		final CategoryTypeLoadTuner loadTuner2 = new CategoryTypeLoadTunerImpl();
		assertTrue(loadTuner1.contains(loadTuner2));
		assertTrue(loadTuner2.contains(loadTuner1));

		loadTuner1.setLoadingAttributes(true);
		assertTrue(loadTuner1.contains(loadTuner2));
		assertFalse(loadTuner2.contains(loadTuner1));
	}

	/**
	 * Test method for {@link CategoryTypeLoadTunerImpl#merge(CategoryTypeLoadTuner)}.
	 */
	@Test
	public void testMerge() {
		final CategoryTypeLoadTuner loadTuner1 = new CategoryTypeLoadTunerImpl();
		final CategoryTypeLoadTuner loadTuner2 = new CategoryTypeLoadTunerImpl();
		assertTrue(loadTuner1.contains(loadTuner2));
		assertTrue(loadTuner2.contains(loadTuner1));

		loadTuner1.setLoadingAttributes(true);

		final CategoryTypeLoadTuner loadTuner3 = loadTuner2.merge(loadTuner1);
		assertTrue(loadTuner3.contains(loadTuner2));
		assertTrue(loadTuner3.contains(loadTuner1));
	}
}
