package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elasticpath.domain.catalog.ProductTypeLoadTuner;

/**
 * Test <code>ProductTypeLoadTunerImpl</code>.
 */
public class ProductTypeLoadTunerImplTest {

	
	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductTypeLoadTunerImpl.contains(ProductTypeLoadTuner)'.
	 */
	@Test
	public void testContains() {
		final ProductTypeLoadTuner loadTuner1 = new ProductTypeLoadTunerImpl();
		assertTrue(loadTuner1.contains(null));

		final ProductTypeLoadTuner loadTuner2 = new ProductTypeLoadTunerImpl();
		assertTrue(loadTuner1.contains(loadTuner2));
		assertTrue(loadTuner2.contains(loadTuner1));

		loadTuner1.setLoadingAttributes(true);
		assertTrue(loadTuner1.contains(loadTuner2));
		assertFalse(loadTuner2.contains(loadTuner1));

		loadTuner1.setLoadingSkuOptions(true);

		loadTuner2.setLoadingSkuOptions(true);
		assertTrue(loadTuner1.contains(loadTuner2));
		assertFalse(loadTuner2.contains(loadTuner1));
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductTypeLoadTunerImpl.merge(ProductTypeLoadTuner)'.
	 */
	@Test
	public void testMerge() {
		final ProductTypeLoadTuner loadTuner1 = new ProductTypeLoadTunerImpl();
		final ProductTypeLoadTuner loadTuner2 = new ProductTypeLoadTunerImpl();
		assertTrue(loadTuner1.contains(loadTuner2));
		assertTrue(loadTuner2.contains(loadTuner1));

		loadTuner1.setLoadingAttributes(true);
		loadTuner1.setLoadingSkuOptions(true);

		final ProductTypeLoadTuner loadTuner3 = loadTuner2.merge(loadTuner1);
		assertTrue(loadTuner3.contains(loadTuner2));
		assertTrue(loadTuner3.contains(loadTuner1));
	}
}
