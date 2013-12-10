package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSkuLoadTuner;

/**
 * Test <code>ProductSkuLoadTunerImpl</code>.
 */
public class ProductSkuLoadTunerImplTest {

	
	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductSkuLoadTunerImpl.contains(ProductSkuLoadTuner)'.
	 */
	@Test
	public void testContains() {
		final ProductSkuLoadTuner loadTuner1 = new ProductSkuLoadTunerImpl();
		assertTrue(loadTuner1.contains(null));

		final ProductSkuLoadTuner loadTuner2 = new ProductSkuLoadTunerImpl();
		assertTrue(loadTuner1.contains(loadTuner2));
		assertTrue(loadTuner2.contains(loadTuner1));

		loadTuner1.setLoadingAttributeValue(true);
		assertTrue(loadTuner1.contains(loadTuner2));
		assertFalse(loadTuner2.contains(loadTuner1));

		loadTuner1.setLoadingDigitalAsset(true);
		loadTuner1.setLoadingOptionValue(true);
		loadTuner1.setLoadingProduct(true);

		loadTuner2.setLoadingDigitalAsset(true);
		loadTuner2.setLoadingOptionValue(true);
		loadTuner2.setLoadingProduct(true);
		assertTrue(loadTuner1.contains(loadTuner2));
		assertFalse(loadTuner2.contains(loadTuner1));
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductSkuLoadTunerImpl.merge(ProductSkuLoadTuner)'.
	 */
	@Test
	public void testMerge() {
		final ProductSkuLoadTuner loadTuner1 = new ProductSkuLoadTunerImpl();
		final ProductSkuLoadTuner loadTuner2 = new ProductSkuLoadTunerImpl();
		assertTrue(loadTuner1.contains(loadTuner2));
		assertTrue(loadTuner2.contains(loadTuner1));

		loadTuner1.setLoadingAttributeValue(true);
		loadTuner1.setLoadingDigitalAsset(true);
		loadTuner1.setLoadingOptionValue(true);
		loadTuner1.setLoadingProduct(true);

		final ProductSkuLoadTuner loadTuner3 = loadTuner2.merge(loadTuner1);
		assertTrue(loadTuner3.contains(loadTuner2));
		assertTrue(loadTuner3.contains(loadTuner1));
	}
}
