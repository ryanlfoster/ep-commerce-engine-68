package com.elasticpath.sfweb.util.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.service.catalogview.StoreConfig;

/**
 * Test that the controller work as expected.
 */
public class StoreAssetResourceRetrievalStrategyTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private static final String STOREASSETSFOLDER = "store_assets";

	/**
	 * Test that getFullPath() returns the store-specific path.
	 */
	@Test
	public void testFullPath() {
		final StoreConfig storeConfig = context.mock(StoreConfig.class);
		final AssetRepository assetRepository = context.mock(AssetRepository.class);
		context.checking(new Expectations() { {
			allowing(storeConfig).getStoreCode(); will(returnValue("mystore"));
			allowing(assetRepository).getStoreAssetsPath(); will(returnValue(STOREASSETSFOLDER));
		} });
		StoreAssetResourceRetrievalStrategy strategy = new StoreAssetResourceRetrievalStrategy();
		strategy.setAssetRepository(assetRepository);
		strategy.setStoreConfig(storeConfig);
		
		assertEquals("path should equal 'store_assets'", STOREASSETSFOLDER
				+ File.separator + "mystore" 
				+ File.separator + "js" 
				+ File.separator + "my2.js",	strategy.getFullPath("js" + File.separator + "my2.js"));
	}
}
