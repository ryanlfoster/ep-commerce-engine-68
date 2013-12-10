/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.settings.domain.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test case for <code>SettingValueImpl</code>. 
 */
public class SettingValueImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SettingValueImpl settingValueImpl;

	private static final String CONTEXT = "SOME-CONTEXT";
	private static final String VALUE = "SomeValue";
	private static final String BOOLEAN = "Boolean";
	private static final String PATH = "SOMEPATH";
	private static final Date DATE = new Date();
	
	
	/**
	 * Setup the test invariants.
	 * @throws Exception in case of error
	 */
	@Before
	public void setUp() throws Exception {
		this.settingValueImpl = new SettingValueImpl();
	}

	private SettingValueImpl createNewSettingValue(
			final SettingDefinition settingDefinition, final String value, final String context) {
		SettingValueImpl settingValue = new SettingValueImpl();
		settingValue.setSettingDefinition(settingDefinition);
		settingValue.setValue(value);
		settingValue.setContext(context);
		return settingValue;
	}
	
	private SettingDefinition createSettingDefinition(final String path, final String type, final String defaultValue) {
		String mockName = String.format("mock setting def for %s %s %s", path, type, defaultValue);
		final SettingDefinition mockDef = context.mock(SettingDefinition.class, mockName);
		context.checking(new Expectations() {
			{
				allowing(mockDef).getPath();
				will(returnValue(path));

				allowing(mockDef).getValueType();
				will(returnValue(type));

				allowing(mockDef).getDefaultValue();
				will(returnValue(defaultValue));

				allowing(mockDef).getLastModifiedDate();
				will(returnValue(DATE));
			}
		});
		return mockDef;
	}

	/**
	 * Test that you get the same context that you set.
	 */
	@Test
	public void testGetSetContext() {
		this.settingValueImpl.setContext(CONTEXT);
		assertEquals(CONTEXT, this.settingValueImpl.getContext());
	}

	/**
	 * Test that you get the same value that you set.
	 */
	@Test
	public void testGetValue() {
		this.settingValueImpl.setValue(VALUE);
		assertEquals(VALUE, this.settingValueImpl.getValue());
	}

	/**
	 * Test that the last modified date is the definition's date when the object has not been persisted,
	 * falls back to the setting definition's last modified date.
	 */
	@Test
	public void testGetLastModifiedDate() {
		SettingDefinition def = createSettingDefinition(PATH, BOOLEAN, VALUE);
		settingValueImpl.setSettingDefinition(def);
		assertEquals(settingValueImpl.getLastModifiedDate(), DATE);
	}

	/**
	 * Test that the setter for last modified date exists.
	 */
	@Test
	public void testSetLastModifiedDate() {
		final Date newDate = new Date();
		settingValueImpl.setLastModifiedDateInternal(newDate);
		assertSame(newDate, settingValueImpl.getLastModifiedDate());
	}
	
	/**
	 * Test that the method gets a true boolean value when the setting's value is 
	 * a "true" string.
	 */
	@Test
	public void testGetBooleanValueTrue() {
		final String context = "testContext";
		final String value1 = "true";
		final String path1 = "COMMERCE/STORE/theme";
		final String defaultValue1 = "DefaultValue1";
		final String type1 = BOOLEAN;
		final SettingDefinition settingsDefinition1 = createSettingDefinition(path1, type1, defaultValue1);
		final SettingValue settingValueOne = createNewSettingValue(settingsDefinition1, value1, context);
		assertEquals(true, settingValueOne.getBooleanValue());
		
		
	}
	
	/**
	 * Test that the method gets a false boolean value when the setting's value is 
	 * a "false" string.
	 */
	@Test
	public void testGetBooleanValueFalse() {
		final String newcontext = "NewContext";
		final String value2 = "false";
		final String path2 = "COMMERCE/SYSTEM/ASSETS/assetLocation";
		final String defaultValue2 = "DefaultValue2";
		final String type2 = BOOLEAN;
		final SettingDefinition settingsDefinition2 = createSettingDefinition(path2, type2, defaultValue2);
		final SettingValue settingValueTwo = createNewSettingValue(settingsDefinition2, value2, newcontext);
		assertEquals(false, settingValueTwo.getBooleanValue());
	}
	
	/**
	 * Test that the method gets a false boolean value when the setting's value is 
	 * a string value that is not "true" or "false".
	 */
	@Test
	public void testGetBooleanValueNeither() {
		final String newercontext = "NewerContext";
		final String value3 = "one";
		final String path3 = "COMMERCE/SYSTEM/seoEnabled";
		final String defaultValue3 = "DefaultValue3";
		final String type3 = "Integer";
		final SettingDefinition settingsDefinition3 = createSettingDefinition(path3, type3, defaultValue3);
		final SettingValue settingValueThree = createNewSettingValue(settingsDefinition3, value3, newercontext);
		assertEquals(false, settingValueThree.getBooleanValue());
	}

	/**
	 * Test that the method sets a "true" string when the parameter value is true.
	 */
	@Test
	public void testSetBooleanValueTrue() {
		final String context = "testContext";
		final String value1 = "one";
		final String path1 = "COMMERCE/STORE/theme";
		final String defaultValue1 = "DefaultValue1";
		final String type1 = BOOLEAN;
		final SettingDefinition settingsDefinition1 = createSettingDefinition(path1, type1, defaultValue1);
		SettingValue settingValueOne = createNewSettingValue(settingsDefinition1, value1, context);
		settingValueOne.setBooleanValue(true);
		assertEquals(true, settingValueOne.getBooleanValue());
	}
	
	/**
	 * Test that the method sets a "false" string when the parameter value is false.
	 */
	@Test
	public void testSetBooleanValueFalse() {
		final String newcontext = "NewContext";
		final String value2 = "two";
		final String path2 = "COMMERCE/SYSTEM/ASSETS/assetLocation";
		final String defaultValue2 = "DefaultValue2";
		final String type2 = BOOLEAN;
		final SettingDefinition settingsDefinition2 = createSettingDefinition(path2, type2, defaultValue2);
		SettingValue settingValueTwo = createNewSettingValue(settingsDefinition2, value2, newcontext);
		settingValueTwo.setBooleanValue(false);
		assertEquals(false, settingValueTwo.getBooleanValue());
		 
	}
	/**
	 * Test that two SettingValue objects are considered equal if their Contexts and their
	 * SettingDefinitions are both equal.
	 */
	@Test
	public void testEquals() {
		
		final String context = "testContext";
		final String newcontext = "NewContext";
		final String value1 = "value1";
		final String value2 = "value2";

		final String path1 = "COMMERCE/STORE/theme";
		final String path2 = "COMMERCE/SYSTEM/ASSETS/assetLocation";
		final String defaultValue1 = "DefaultValue1";
		final String defaultValue2 = "DefaultValue2";
		final String type1 = "Type1";
		final String type2 = "Type2";

		final SettingDefinition settingsDefinition1 = createSettingDefinition(path1, type1, defaultValue1);
		final SettingDefinition settingsDefinition2 = createSettingDefinition(path2, type2, defaultValue2);
		
		final SettingValue settingValueOne = createNewSettingValue(settingsDefinition1, value1, context);
		final SettingValue settingValueTwo = createNewSettingValue(settingsDefinition1, value2, context);
 
		assertEquals("These two settingValues should be equal since they both have the same contexts and settingsdefinitions", 
				settingValueOne, settingValueTwo);
				
		final SettingValue settingValueThree = createNewSettingValue(settingsDefinition1, value1, context);
		final SettingValue settingValueFour = createNewSettingValue(settingsDefinition1, value1, newcontext);
		
		assertFalse("These two settingValues should not be equal since they have different contexts", settingValueThree.equals(settingValueFour));
		
		final SettingValue settingValueFive = createNewSettingValue(settingsDefinition1, value1, context);
		final SettingValue settingValueSix = createNewSettingValue(settingsDefinition2, value1, context);
		
		assertFalse("These two settingValues should not be equal since they have different settingDefinitions", 
				settingValueFive.equals(settingValueSix));
	}
}
