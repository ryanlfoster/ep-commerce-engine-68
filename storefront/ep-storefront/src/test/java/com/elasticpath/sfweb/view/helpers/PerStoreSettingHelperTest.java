/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.view.helpers;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.service.catalogview.impl.ThreadLocalStorageImpl;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Unit tests for the PerStoreSettingHelper class.
 */
public class PerStoreSettingHelperTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final SettingsReader mockSettingsReader = context.mock(SettingsReader.class);
	private final StoreService mockStoreService = context.mock(StoreService.class);
	private SettingsReader settingsReader;
	private StoreService storeService;
	private ThreadLocalStorageImpl storeConfig;
	private PerStoreSettingHelper perStoreHelper;

	/**
	 * Setup test.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		settingsReader = mockSettingsReader;
		storeService = mockStoreService;
		storeConfig = new ThreadLocalStorageImpl();
		storeConfig.setStoreCode("SNAPITUP");
		storeConfig.setStoreService(storeService);
		perStoreHelper = new PerStoreSettingHelper();
		perStoreHelper.setSettingsReader(settingsReader);
		perStoreHelper.setStoreConfig(storeConfig);
	}
	
	/**
	 * Resolve boolean setting for a specific store setting that is retrieved
	 * using the SettingsReader.
	 */
	@Test
	public void testGetBooleanNotInCache() {
		// Try to resolve a setting value, call the SettingsReader.
		final boolean booleanValue = true;
		final SettingValue mockSettingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsReader).getSettingValue(with(aNull(String.class)), with(any(String.class)));
				will(returnValue(mockSettingValue));
				allowing(mockSettingValue).getValue();
				will(returnValue("true"));
				allowing(mockSettingValue).getBooleanValue();
				will(returnValue(booleanValue));
			}
		});
		boolean returnedValue = perStoreHelper.getBoolean();
		assertEquals(returnedValue, booleanValue);
	}

}
