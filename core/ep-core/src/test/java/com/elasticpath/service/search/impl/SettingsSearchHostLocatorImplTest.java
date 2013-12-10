package com.elasticpath.service.search.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;

/**
 * Test class for SettingsSearchHostLocatorImpl.
 */
public class SettingsSearchHostLocatorImplTest {
	
	private static final String HTTP_MASTER_URL = "http://masterurl";
	private static final String HTTP_DEFAULT_URL = "http://defaulturl";
	private static final String MASTER_CONTEXT = "master";
	private static final String DEFAULT_CONTEXT = "default";
	private static final String SEARCH_HOST_SETTING_PATH = "COMMERCE/SYSTEM/SEARCH/searchHost";
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final SettingsReader reader = context.mock(SettingsReader.class);
	
	private SettingsSearchHostLocatorImpl locator;
	private SettingValue masterValue;
	private SettingValue defaultValue;

	/**
	 * Setup required for each test.
	 */
	@Before
	public void setUp() {
		locator = new SettingsSearchHostLocatorImpl();
		locator.setSettingsReader(reader);
		masterValue = new SettingValueImpl() {
			private static final long serialVersionUID = -7148910476004823141L;

			public String getValue() {
				return HTTP_MASTER_URL;
			}
		};
		defaultValue = new SettingValueImpl() {
			private static final long serialVersionUID = 3148831406370330506L;

			public String getValue() {
				return HTTP_DEFAULT_URL;
			}
		};
		context.checking(new Expectations() { {
			allowing(reader).getSettingValue(SEARCH_HOST_SETTING_PATH, MASTER_CONTEXT); will(returnValue(masterValue));
			allowing(reader).getSettingValue(SEARCH_HOST_SETTING_PATH, DEFAULT_CONTEXT); will(returnValue(defaultValue));
		} });
	}
	
	/**
	 * Test that the host url is retrieved from the setting for the master context.
	 */
	@Test
	public void testReturnMasterHostUrl() {
		locator.setRequiresMaster(true);
		assertEquals("The URL should be the value returned by the settings reader for the master context", HTTP_MASTER_URL, 
				locator.getSearchHostLocation());
	}
	
	/**
	 * Test that the host url is retrieved from the setting for the default context when master is not required.
	 */
	@Test
	public void testReturnDefaultHostUrl() {
		locator.setRequiresMaster(false);
		assertEquals("The URL should be the value returned by the settings reader for the default contect", HTTP_DEFAULT_URL,
				locator.getSearchHostLocation());
	}
	
	
}
