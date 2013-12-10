/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationParser;
import com.elasticpath.service.catalogview.filterednavigation.impl.FilteredNavigationConfigurationLoaderImpl;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.test.persister.SettingsTestPersister;

/**
 * An integration test for loading of filtered navigation.
 */
public class FilteredNavConfigurationLoadingTest extends BasicSpringContextTest {

	private static final String SETTING_PATH = "COMMERCE/STORE/FILTEREDNAVIGATION/filteredNavigationConfiguration";

	@Autowired
	@Qualifier("filteredNavigationConfigurationLoader")
	private FilteredNavigationConfigurationLoaderImpl fncLoader;

	private CountingAdvice counter;
	
	/**
	 * Test that filtered navigation configuration is loaded as expected.
	 * @throws IOException in case of error reading XML file
	 */
	@DirtiesDatabase
	@Test
	public void testLoadFilteredNavConfiguration() throws IOException {
		final String storeCode = "Store 1";
		setupXmlSettingValue(storeCode);
		FilteredNavigationConfiguration fnc = fncLoader.loadFilteredNavigationConfiguration(storeCode);
		final int expectedPriceRanges = 15;
		
		assertNotNull("Configuration should not be null", fnc);
		assertEquals("There should be 15 price ranges in the config", expectedPriceRanges, fnc.getAllPriceRanges().size());
	}
	
	/**
	 * Test that filtered navigation configuration doesn't call the parser every time
	 * if the underlying data hasn't changed.
	 * @throws IOException in case of error reading XML file
	 */
	@DirtiesDatabase
	@Test
	public void testReloadFilteredNavConfiguration() throws IOException {
		final String storeCode = "Store 2";
		setupXmlSettingValue(storeCode);
		setupParserCounter();
		
		final int multiples = 10;
		for (int i = 0; i < multiples; i++) {
			fncLoader.loadFilteredNavigationConfiguration(storeCode);
		}
		assertEquals("The parser should have been called once", 1, counter.getCount());
	}
	
	/**
	 * Test that filtered navigation configuration calls the parser if the underlying
	 * configuration data has changed.
	 * 
	 * @throws IOException in case of error reading XML file
	 */
	@DirtiesDatabase
	@Test
	public void testChangedFilteredNavConfiguration() throws IOException {
		final String storeCode = "Store 3";

		setupXmlSettingValue(storeCode);
		setupParserCounter();
		
		fncLoader.loadFilteredNavigationConfiguration(storeCode);
		assertEquals("The parser should have been called once", 1, counter.getCount());
		
		SettingsService settingsService = getBeanFactory().getBean(ContextIdNames.SETTINGS_SERVICE);
		SettingValue settingValue = settingsService.getSettingValue(SETTING_PATH, storeCode);
		settingValue.setValue(settingValue.getValue() + "\n<!-- This setting has changed -->\n");
		settingsService.updateSettingValue(settingValue);
		
		fncLoader.loadFilteredNavigationConfiguration(storeCode);
		assertEquals("The parser should have been called twice", 2, counter.getCount());
		
	}
	
	private void setupXmlSettingValue(final String storeCode) throws IOException {
		SettingsTestPersister settingsTestPersister = getTac().getPersistersFactory().getSettingsTestPersister();
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream xmlStream = classLoader.getResourceAsStream("filteredNavigation.xml");
		if (xmlStream != null) {
			try {
				settingsTestPersister.updateSettingValue(SETTING_PATH, storeCode, IOUtils.toString(xmlStream));
			} finally {
				xmlStream.close();
			}
		}
	}

	/**
	 * Use an AOP adviser to track the number of times the parser is called.
	 */
	private void setupParserCounter() {
		final FilteredNavigationConfigurationParser parser = fncLoader.getParser();
		ProxyFactory factory = new ProxyFactory(parser);
		counter = new CountingAdvice();
		factory.addAdvice(counter);
		fncLoader.setParser((FilteredNavigationConfigurationParser) factory.getProxy());
	}
	
	/**
	 * AOP adviser to count method calls.
	 */
	public class CountingAdvice implements MethodBeforeAdvice {
	
		private int count;
		
		/**
		 * @return the call count
		 */
		public int getCount() {
			return count;
		}

		@Override
		public void before(final Method arg0, final Object[] arg1, final Object arg2) throws Throwable {
			++count;
		}
		
	}
	
}
