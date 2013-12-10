package com.elasticpath.sfweb.util.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.sfweb.exception.EpWebException;
import com.elasticpath.sfweb.util.FilenameUtils;

/**
 * Test for testing the store theme aware resource retrieval strategy.
 */
public class StoreThemeAssetResourceRetrievalStrategyTest {
	private static final String THE_PRESENTATION_FOLDER = "the-presentation-folder";
	private static final String THEME_NAME = "mytheme";
    @Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
    
	/**
	 * Tests that resource requests resolve on a per-store basis.
	 */
    @Test
	public void testResolvePath() {
    	final String storeCode = "myStore";
		final AssetRepository assetRepository = context.mock(AssetRepository.class);
		context.checking(new Expectations() { {
			allowing(assetRepository).getThemeAssetsPath(); will(returnValue(THE_PRESENTATION_FOLDER));
		} });
		
		StoreThemeAssetResourceRetrievalStrategy strategy = new StoreThemeAssetResourceRetrievalStrategy();
		strategy.setAssetRepository(assetRepository);
	
		assertEquals("Resolve path is wrong", "the-presentation-folder" + File.separator + "mytheme" 
				+ File.separator + storeCode + File.separator + "js" + File.separator + "my2.js", 
				strategy.resolvePath(FilenameUtils.formPath("js", "my2.js"), THEME_NAME, storeCode));
	}
	
	/**
	 * Tests that resource requests resolve on a per-store basis.
	 */
	@Test
	public void testResolvePathDefault() {
		final AssetRepository assetRepository = context.mock(AssetRepository.class);
		context.checking(new Expectations() { {
			allowing(assetRepository).getThemeAssetsPath(); will(returnValue(THE_PRESENTATION_FOLDER));
		} });
		StoreThemeAssetResourceRetrievalStrategy strategy = new StoreThemeAssetResourceRetrievalStrategy();
		strategy.setAssetRepository(assetRepository);
			
		assertEquals(" resolve path is wrong", "the-presentation-folder" + File.separator + "mytheme" + File.separator 
				+ "default" + File.separator + "js" + File.separator + "my3.js", 
				strategy.resolvePathDefault(FilenameUtils.formPath("js", "my3.js"), THEME_NAME));
	}
		
	/**
	 * Test that when given a relative path, the resolve method will throw an exception.
	 * We cannot get files relative to a web app context because files inside WAR cannot be retrieved.
	 */
	@Test(expected = EpWebException.class)
	public void testResolveAbsolutePath() throws Exception {
		StoreThemeAssetResourceRetrievalStrategy strategy = new StoreThemeAssetResourceRetrievalStrategy();
		strategy.getResource("hi");
	}
	
	/**
	 * Tests that when requesting a resource that the store doesn't have that
	 * we will fallback to the default version.
	 * 
	 * @throws Exception if there is an unexpected problem with the test.
	 */
	@Test
	public void testResolvePathWithFallback() throws Exception {
		final AssetRepository assetRepository = context.mock(AssetRepository.class);
		context.checking(new Expectations() { {
			allowing(assetRepository).getThemeAssetsPath(); will(returnValue(THE_PRESENTATION_FOLDER));
		} });

		final StoreConfig storeConfig = context.mock(StoreConfig.class);
		final SettingValue storeThemeValue = context.mock(SettingValue.class);
		context.checking(new Expectations() { {
			allowing(storeConfig).getStoreCode(); will(returnValue("something"));
			allowing(storeConfig).getSetting("COMMERCE/STORE/theme"); will(returnValue(storeThemeValue));
			allowing(storeThemeValue).getValue(); will(returnValue(THEME_NAME));
		} });
		
		StoreThemeAssetResourceRetrievalStrategy strategy = new StoreThemeAssetResourceRetrievalStrategy() {
			private boolean firstCall = true;
			@Override
			URL getResource(final String resourcePath) throws MalformedURLException, IllegalStateException {
				if (firstCall) {
					firstCall = false;
					return null;
				}
				return new URL("http://a.b.c");
			}
		};
		strategy.setAssetRepository(assetRepository);
		strategy.setStoreConfig(storeConfig);
		
		assertEquals("URL should equal a.b.c ", new URL("http://a.b.c"), strategy.resolveResource("/js/my.js"));
	}
}
