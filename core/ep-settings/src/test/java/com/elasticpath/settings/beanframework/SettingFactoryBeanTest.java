package com.elasticpath.settings.beanframework;


import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test class for {@link com.elasticpath.settings.beanframework.SettingFactoryBean}.
 */
public class SettingFactoryBeanTest {

	private SettingFactoryBean settingFactoryBean;

	private SettingsReader settingsReader;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Setup required for each test.
	 */
	@Before
	public void setUp() {
		settingsReader = context.mock(SettingsReader.class);

		settingFactoryBean = new SettingFactoryBean();
		settingFactoryBean.setSettingsReader(settingsReader);
	}

	/**
	 * Test that createInstance returns the expected value when a context is present.
	 *
	 * @throws Exception in case of error
	 */
	@Test
	public void testCreateInstanceUsesContextWhenPresent() throws Exception {
		final String settingContext = "context";
		final String path = "/TEST/PATH/TO/setting";
		final String expectedSettingValue = "value";

		settingFactoryBean.setContext(settingContext);
		settingFactoryBean.setPath(path);

		context.checking(new Expectations() {
			{
				final SettingValue settingValue = context.mock(SettingValue.class);

				oneOf(settingsReader).getSettingValue(path, settingContext);
				will(returnValue(settingValue));

				allowing(settingValue).getValue();
				will(returnValue(expectedSettingValue));
			}
		});

		final String actualSettingValue = settingFactoryBean.createInstance();
		assertEquals("Expected the value returned by the settings reader", expectedSettingValue, actualSettingValue);
	}

	/**
	 * Test that createInstance returns the expected value when no context is given.
	 *
	 * @throws Exception in case of error
	 */
	@Test
	public void testCreateInstanceIgnoresNullContext() throws Exception {
		final String path = "/TEST/PATH/TO/setting";
		final String expectedSettingValue = "value";

		settingFactoryBean.setPath(path);

		context.checking(new Expectations() {
			{
				final SettingValue settingValue = context.mock(SettingValue.class);

				oneOf(settingsReader).getSettingValue(path);
				will(returnValue(settingValue));

				allowing(settingValue).getValue();
				will(returnValue(expectedSettingValue));
			}
		});

		final String actualSettingValue = settingFactoryBean.createInstance();
		assertEquals("Expected the value returned by the settings reader", expectedSettingValue, actualSettingValue);
	}

	/**
	 * Test that createInstance throws an exception when the path is null.
	 *
	 * @throws Exception in case of error
	 */
	@Test(expected = IllegalStateException.class)
	public void testCreateInstanceThrowsExceptionWhenPathIsNull() throws Exception {
		settingFactoryBean.createInstance();
	}

	/**
	 * Test that createInstance throws an exception when the path is not found.
	 *
	 * @throws Exception in case of error
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateInstanceThrowsExceptionWhenNoSuchSettingPath() throws Exception {
		final String path = "/NO/SUCH/setting";

		settingFactoryBean.setPath(path);

		context.checking(new Expectations() {
			{
				oneOf(settingsReader).getSettingValue(path);
				will(returnValue(null));
			}
		});

		settingFactoryBean.createInstance();
	}

	/**
	 * Test that setPath throws an exception when the path is null. 
	 *
	 * @throws Exception in case of error
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetPathThrowsExceptionForNullPath() throws Exception {
		settingFactoryBean.setPath(null);
	}

	/**
	 * Test that setPath throws an exception when the path is blank.
	 *
	 * @throws Exception in case of error
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetPathThrowsExceptionForEmptyStringPath() throws Exception {
		settingFactoryBean.setPath("");
	}

}