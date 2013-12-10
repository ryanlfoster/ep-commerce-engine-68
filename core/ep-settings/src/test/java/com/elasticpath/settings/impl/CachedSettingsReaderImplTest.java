/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.settings.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingMetadata;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test that the Cached Settings Reader is properly delegating to refresh strategies when retrieving setting values.
 */
public class CachedSettingsReaderImplTest {

	private static final String[] SETTING_CONTEXTS = { "SNAPITUP", "SNAPITUPUK", "SLRWORLD" };

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String SETTING_PATH = "COMMERCE/Store/theme";

	private static final String SETTING_PATH_2 = "COMMERCE/Store/theme2";

	private static final String SETTING_PATH_3 = "COMMERCE/Store/theme3";

	private SettingsService reader;

	private SettingRefreshStrategy refreshStrategy;

	private CachedSettingsReaderImpl cachedSettingsReader;

	private SettingDefinition definition;

	/**
	 * Set up common objects needed by the tests.
	 */
	@Before
	public void runBeforeEveryTest() {
		cachedSettingsReader = new CachedSettingsReaderImpl();
		reader = context.mock(SettingsService.class);
		cachedSettingsReader.setSettingsService(reader);
		refreshStrategy = context.mock(SettingRefreshStrategy.class);
		final Map<String, SettingRefreshStrategy> refreshStrategies = new HashMap<String, SettingRefreshStrategy>();
		refreshStrategies.put("immediate", refreshStrategy);
		cachedSettingsReader.setRefreshStrategies(refreshStrategies);
		cachedSettingsReader.setRefreshStrategyKey("sfRefreshStrategy");
		definition = context.mock(SettingDefinition.class);
		final SettingMetadata strategyMetadata = context.mock(SettingMetadata.class);
		final Map<String, SettingMetadata> metadata = new HashMap<String, SettingMetadata>();
		metadata.put("sfRefreshStrategy", strategyMetadata);

		context.checking(new Expectations() {
			{
				allowing(definition).getMetadata();
				will(returnValue(metadata));

				allowing(strategyMetadata).getValue();
				will(returnValue("immediate"));
			}
		});
	}

	/**
	 * Test method for {@link com.elasticpath.settings.impl.CachedSettingsReaderImpl#getSettingValue(java.lang.String, java.lang.String)}.
	 * Tests that the setting definition is being checked and that the value is being retrieved from the refresh strategy.
	 */
	@Test
	public void testGetSettingValue() {
		final SettingValue value = context.mock(SettingValue.class);

		context.checking(new Expectations() {
			{
				oneOf(reader).getSettingDefinition(with(same(SETTING_PATH)));
				will(returnValue(definition));

				allowing(value).getValue();
				will(returnValue("value"));

				oneOf(refreshStrategy).retrieveSetting(with(any(String.class)), with(any(String.class)), with(any(String.class)));
				will(returnValue(value));
			}
		});

		SettingValue settingValue = cachedSettingsReader.getSettingValue(SETTING_PATH, "SNAPITUP");
		assertEquals("Should be getting back the same value returned from the refresh strategy.", settingValue.getValue(), value.getValue());
	}

	/**
	 * Test method for {@link com.elasticpath.settings.impl.CachedSettingsReaderImpl#getSettingValue(java.lang.String)}.
	 * Tests that the setting definition is being checked and that the value is being retrieved from the refresh strategy.
	 */
	@Test
	public void testGetSettingValueWithNoContext() {
		final SettingValue value = context.mock(SettingValue.class);

		context.checking(new Expectations() {
			{
				oneOf(reader).getSettingDefinition(with(same(SETTING_PATH_2)));
				will(returnValue(definition));

				allowing(value).getValue();
				will(returnValue("value"));

				oneOf(refreshStrategy).retrieveSetting(with(any(String.class)), with(any(String.class)));
				will(returnValue(value));
			}
		});

		SettingValue settingValue = cachedSettingsReader.getSettingValue(SETTING_PATH_2);
		assertEquals("Should be getting back the same value returned from the refresh strategy.", settingValue.getValue(), value.getValue());
	}

	/**
	 * Test method for {@link com.elasticpath.settings.impl.CachedSettingsReaderImpl#getSettingValues(java.lang.String, java.lang.String[])}.
	 * Tests that the setting definition is being checked and that the values are being retrieved from the refresh strategy.
	 */
	@Test
	public void testGetSettingValues() {
		final SettingValue value1 = context.mock(SettingValue.class, "value1");
		final SettingValue value2 = context.mock(SettingValue.class, "value2");
		final SettingValue value3 = context.mock(SettingValue.class, "value3");

		final Set<SettingValue> expectedSettingValues = new HashSet<SettingValue>();
		expectedSettingValues.add(value1);
		expectedSettingValues.add(value2);
		expectedSettingValues.add(value3);

		context.checking(new Expectations() {
			{
				oneOf(reader).getSettingDefinition(with(same(SETTING_PATH_3)));
				will(returnValue(definition));

				allowing(value1).getValue();
				will(returnValue("value1"));

				allowing(value2).getValue();
				will(returnValue("value2"));

				allowing(value3).getValue();
				will(returnValue("value3"));

				exactly(expectedSettingValues.size()).of(refreshStrategy).retrieveSetting(with(any(String.class)), with(any(String.class)),
						with(any(String.class)));
				will(onConsecutiveCalls(returnValue(value1), returnValue(value2), returnValue(value3)));
			}
		});

		Set<SettingValue> settingValues = cachedSettingsReader.getSettingValues(SETTING_PATH_3, SETTING_CONTEXTS);
		for (SettingValue settingValue : settingValues) {
			boolean valueWasExpected = false;
			for (SettingValue expectedSettingValue : expectedSettingValues) {
				if (expectedSettingValue.getValue().equals(settingValue.getValue())) {
					valueWasExpected = true;
				}
			}
			assertTrue("The value \"" + settingValue.getValue() + " was not expected. Should be getting back the same values we expected.",
					valueWasExpected);
		}
	}

	/**
	 * Test that the static setting data map is cleared when {@link CachedSettingsReaderImpl#destroy()} is called.
	 * 
	 * @throws Exception to accommodate the method signature of {@link org.springframework.beans.factory.DisposableBean#destroy()}
	 */
	@Test
	public void testSettingDataMapClearedOnDestroy() throws Exception {
		CachedSettingsReaderImpl.getSettingData().put("key", null);
		assertFalse("Setting data map should have one entry", CachedSettingsReaderImpl.getSettingData().isEmpty());
		CachedSettingsReaderImpl cachedSettingsReader = new CachedSettingsReaderImpl();
		cachedSettingsReader.destroy();
		assertTrue("Setting data map should be cleared on destroy", CachedSettingsReaderImpl.getSettingData().isEmpty());
	}
}
