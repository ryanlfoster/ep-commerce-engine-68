package com.elasticpath.commons.util.impl;

import org.apache.commons.lang.StringUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.settings.domain.SettingValue;

/**
 * StoreThemeMessageSource tests.
 */
public class StoreThemeMessageSourceTest {
	
	private static final String SETTING = "COMMERCE/STORE/theme";
	private static final String STORE_CODE = "storeCode";

	private StoreConfig storeConfig;
	private SettingValue settingValue;
	
	private final StoreThemeMessageSource messageSource = new StoreThemeMessageSource();
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of any error
	 */
	@Before	
	public void setUp() throws Exception {
		storeConfig = context.mock(StoreConfig.class);
		settingValue = context.mock(SettingValue.class);
		messageSource.setStoreConfig(storeConfig);
	}
	
	/**
	 * Test that getThemeCode() will throw an exception if the setting value doesn't exist.
	 */
	@Test(expected = EpSystemException.class)
	public void testThrowExceptionWhenThemeSettingNonExistent() {
		context.checking(new Expectations() {
			{
				allowing(storeConfig).getSetting(SETTING); will(returnValue(null));
				allowing(storeConfig).getStoreCode(); will(returnValue(STORE_CODE));
			}
		});
		messageSource.getThemeCode();
	}
	
	/**
	 * Test that getThemeCode() will throw an exception if the theme code is null.
	 */
	@Test(expected = EpSystemException.class)
	public void testThrowExceptionWhenThemeNull() {
		context.checking(new Expectations() {
			{
				allowing(settingValue).getValue(); will(returnValue(null));
				allowing(storeConfig).getSetting(SETTING); will(returnValue(settingValue));
				allowing(storeConfig).getStoreCode(); will(returnValue(STORE_CODE));
			}
		});
		messageSource.getThemeCode();
	}
	
	/**
	 * Test that getThemeCode() will throw an exception if the theme code is of zero length.
	 */
	@Test(expected = EpSystemException.class)
	public void testThrowExceptionWhenThemeEmpty() {
		context.checking(new Expectations() {
			{
				allowing(settingValue).getValue(); will(returnValue(StringUtils.EMPTY));
				allowing(storeConfig).getSetting(SETTING); will(returnValue(settingValue));
				allowing(storeConfig).getStoreCode(); will(returnValue(STORE_CODE));
			}
		});
		messageSource.getThemeCode();
	}
}
