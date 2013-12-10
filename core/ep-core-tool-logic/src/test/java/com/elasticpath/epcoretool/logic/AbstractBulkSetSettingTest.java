package com.elasticpath.epcoretool.logic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.dto.EpSetting;

/**
 * The Class AbstractBulkSetSettingTest.
 */
public class AbstractBulkSetSettingTest {

	private AbstractBulkSetSetting logic;

	/**
	 * Sets up the test - initializing objects.
	 */
	@Before
	public void setUp() {
		logic = new AbstractBulkSetSetting(null, null, null, null, null, null) {
			@Override
			protected LoggerFacade getLogger() {
				return new TestLogger();
			}
		};
	}

	/**
	 * Test parse setting string with context.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testParseSettingStringWithContext() throws Exception {
		EpSetting setting = logic.parseSettingString("COMMERCE/SYSTEM/SEARCH/searchHost@default=http://localhost:8080/searchserver");
		Assert.assertEquals("COMMERCE/SYSTEM/SEARCH/searchHost", setting.getName());
		Assert.assertEquals("default", setting.getContext());
		Assert.assertEquals("http://localhost:8080/searchserver", setting.getValue());
	}

	/**
	 * Test parse setting string without context.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testParseSettingStringWithoutContext() throws Exception {
		EpSetting setting = logic.parseSettingString("COMMERCE/APPSPECIFIC/RCP/ASSETS/vfsProtocol=file");
		Assert.assertEquals("COMMERCE/APPSPECIFIC/RCP/ASSETS/vfsProtocol", setting.getName());
		Assert.assertNull(setting.getContext());
		Assert.assertEquals("file", setting.getValue());
	}

	/**
	 * Test parse setting string without context and with '=' char in value.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testParseSettingStringWithoutContextWithEqualCharInValue() throws Exception {
		EpSetting setting = logic.parseSettingString("COMMERCE/CAMEL/ORDER/endpoint=seda:trash?size=0");
		Assert.assertEquals("COMMERCE/CAMEL/ORDER/endpoint", setting.getName());
		Assert.assertNull(setting.getContext());
		Assert.assertEquals("seda:trash?size=0", setting.getValue());
	}

	/**
	 * Test parse setting string with '=' char in value.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void testParseSettingStringWithEqualCharInValue() throws Exception {
		EpSetting setting = logic.parseSettingString("COMMERCE/CAMEL/ORDER/endpoint@test=seda:trash?size=0");
		Assert.assertEquals("COMMERCE/CAMEL/ORDER/endpoint", setting.getName());
		Assert.assertEquals("test", setting.getContext());
		Assert.assertEquals("seda:trash?size=0", setting.getValue());
	}
	
	/**
	 * Test parse setting string without value.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testParseSettingStringWithoutValue() throws Exception {
		EpSetting setting = logic.parseSettingString("COMMERCE/APPSPECIFIC/RCP/ASSETS/vfsUsername@null=");
		Assert.assertEquals("COMMERCE/APPSPECIFIC/RCP/ASSETS/vfsUsername", setting.getName());
		Assert.assertEquals("null", setting.getContext());
		Assert.assertNull(setting.getValue());
	}

	/**
	 * Test parse setting string invalid.
	 *
	 * @throws Exception the exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testParseSettingStringInvalid() throws Exception {
		logic.parseSettingString("COMMERCE/APPSPECIFIC/RCP/ASSETS/vfsProtocol");
	}
}
