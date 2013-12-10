package com.elasticpath.sfweb.filters;

import static org.hamcrest.Matchers.anyOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.catalogview.StoreSeoUrlBuilderFactory;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;

/**
 * EP URL Rewrite Filter unit test.
 * 
 */
public class StoreUrlRewriteFilterTest {

	private static final String INIT_PARAM_MODREWRITECONFTEXT = "modRewriteConfText";
	private static final String INIT_PARAM_CONFRELOADCHECKINTERVAL = "confReloadCheckInterval";
	
	private static final int INIT_PARAM_CONFRELOADCHECKINTERVAL_VALUE = 3;
	
	private static final long MILLI = 1000;

	private static final String CONFIG_SETTING_VALUE = "<xml></xml>";
	private MockFilterConfig mockFilterConfig;
	private MockHttpServletResponse mockResponse;
	private MockHttpServletRequest mockRequest;
	private MockFilterChain mockFilterChain;
	
	private StoreUrlRewriteFilter filter;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SettingsReader mockSettingsReader;
	private StoreSeoUrlBuilderFactory mockStoreSeoUrlBuilderFactory;

	@Before
	public void setUp() throws Exception {
		mockFilterConfig = new MockFilterConfig();
		mockFilterChain = new MockFilterChain();
		mockResponse = new MockHttpServletResponse();
		mockRequest = new MockHttpServletRequest();
		mockSettingsReader = context.mock(SettingsReader.class);
		
		mockStoreSeoUrlBuilderFactory = context.mock(StoreSeoUrlBuilderFactory.class);
		filter = new StoreUrlRewriteFilter();
		
	}
	
	/**
	 * Test configuration for exception being thrown if the SettingsReader is not set.
	 * @throws IOException HttpServletResponse and HttpServletRequest errors.
	 * @throws ServletException HttpServletResponse and HttpServletRequest errors.
	 */
	@Test(expected = EpSystemException.class)
	public void testLoadUrlRewriterNullSettingsReader() throws IOException, ServletException {
		filter.setSettingsReader(null);
		filter.loadUrlRewriter(mockFilterConfig);
	}
	
	/**
	 * Test configuration for exception being thrown if the Context is not retrieved correctly. 
	 * @throws ServletException exception when trying to load context
	 */
	@Test(expected = EpSystemException.class)
	public void testLoadUrlRewriterNullContext() throws ServletException {
		mockFilterConfig = new MockFilterConfig() {
			@Override
			public ServletContext getServletContext() {
				return null;
			}
		};
		filter.setSettingsReader(mockSettingsReader);
		filter.loadUrlRewriter(mockFilterConfig);
	}
	
	/**
	 * Test configuration for exception being thrown if the setting value retrieved is null.
	 * @throws ServletException exception when trying to load context
	 */
	@Test(expected = EpSystemException.class)
	public void testLoadUrlRewriterNullSettingValue() throws ServletException {
		filter.setSettingsReader(mockSettingsReader);
		mockFilterConfig = new MockFilterConfig() {
			@Override
			public String getInitParameter(final String path) {
				return "setting:/some/path";
			}
		};
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsReader).getSettingValue(with(any(String.class)));
				will(returnValue(null));
			}
		});
		filter.loadUrlRewriter(mockFilterConfig);
	}
	
	/**
	 * Load configuration from a setting that is presently persisted in the database.
	 * @throws ServletException exception when trying to load context
	 * @throws IOException exception when trying to open input stream
	 */
	@Test
	public void testLoadUrlRewriterFromSetting() throws ServletException, IOException {
		filter.setSettingsReader(mockSettingsReader);
		mockFilterConfig = new MockFilterConfig() { 
			@Override
			public String getInitParameter(final String path) {
				return "setting:/some/path";
			}
		};
		
		final SettingValue settingValue = new SettingValueImpl() {
			private static final long serialVersionUID = -8877001051377995998L;

			@Override
			public Date getLastModifiedDateInternal() {
				return new Date();
			}
		};
		settingValue.setValue("");

		context.checking(new Expectations() {
			{
				allowing(mockSettingsReader).getSettingValue(with(any(String.class)));
				will(returnValue(settingValue));
			}
		});
		filter.init(mockFilterConfig);
	}
	
	
	/**
	 * Load configuration from a setting and process a request/response loop.
	 * 
	 * @throws ServletException exception when trying to load context
	 * @throws IOException exception when trying to open input stream
	 */
	@Test
	public void testLoadUrlRewriterFromConfPath() throws ServletException, IOException {
		filter.setSettingsReader(mockSettingsReader);
		filter.setStoreSeoUrlBuilderFactory(mockStoreSeoUrlBuilderFactory);
		mockFilterConfig = new MockFilterConfig() { 
			@Override
			public String getInitParameter(final String path) {
				if (path.equals(INIT_PARAM_MODREWRITECONFTEXT)) {
					return null;
					
				} else if (path.equals(INIT_PARAM_CONFRELOADCHECKINTERVAL)) {
					return "-1"; // no reloading
				}
				
				return "/some/path";
			}
		};
		
		final SettingValue settingValue = new SettingValueImpl() {
			private static final long serialVersionUID = 6455132727624661504L;

			@Override
			public Date getLastModifiedDateInternal() {
				return new Date();
			}
		};
		settingValue.setValue(CONFIG_SETTING_VALUE);

		context.checking(new Expectations() {
			{
				allowing(mockSettingsReader).getSettingValue(with(aNull(String.class)));
				will(returnValue(settingValue));
				allowing(mockStoreSeoUrlBuilderFactory).resetFieldSeparator(with(any(String.class)));
			}
		});
		filter.init(mockFilterConfig);
		filter.doFilter(mockRequest, mockResponse, mockFilterChain);
	}

	/**
	 * Ensure that a file input stream is created when loading a file which contains the configuration.
	 * @throws ServletException exception when trying to load context
	 */
	@Test
	public void testGetResourceInputStream() throws Exception {
		final ServletContext mockServletContext = context.mock(ServletContext.class);
		filter.setSettingsReader(mockSettingsReader);
		filter.setStoreSeoUrlBuilderFactory(mockStoreSeoUrlBuilderFactory);
		
		mockFilterConfig = new MockFilterConfig() { 
			@Override
			public ServletContext getServletContext() {
				return mockServletContext;
			}
		};
		final SettingValue settingValue = new SettingValueImpl();
		settingValue.setValue(CONFIG_SETTING_VALUE);
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockServletContext).getResourceAsStream(with(any(String.class)));
				will(returnValue(null));
				atLeast(1).of(mockServletContext).getResource(with(any(String.class)));
				will(returnValue(null));
				allowing(mockSettingsReader).getSettingValue(with(aNull(String.class)));
				will(returnValue(settingValue));
				allowing(mockStoreSeoUrlBuilderFactory).resetFieldSeparator(with(any(String.class)));
			}
		});
		filter.loadUrlRewriter(mockFilterConfig);
	}
	
	/**
	 * Ensure that a exception is thrown when the context method getResource throws a MalformedURLException.
	 * @throws ServletException exception when trying to load context
	 *
	 * test not implemented correctly.  needs fixing.
	 */
	@Ignore
	@Test(expected = EpServiceException.class)
	public void testGetResourceInputStreamSystemException() throws Exception {
		final ServletContext mockServletContext = context.mock(ServletContext.class);
		filter.setSettingsReader(mockSettingsReader);
		mockFilterConfig = new MockFilterConfig() { 
			@Override
			public ServletContext getServletContext() {
				return mockServletContext;
			}
		};
		final SettingValue settingValue = new SettingValueImpl();
		settingValue.setValue(CONFIG_SETTING_VALUE);
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockServletContext).getResourceAsStream(with(any(String.class)));
				will(returnValue(null));
				atLeast(1).of(mockServletContext).getResource(with(any(String.class)));
				will(throwException(new MalformedURLException()));
				allowing(mockSettingsReader).getSettingValue(with(any(String.class)));
				will(returnValue(settingValue));
			}
		});
		filter.loadUrlRewriter(mockFilterConfig);
	}
	
	/**
	 * Test reloading of configuration from a setting that is presently persisted in the database.
	 * @throws ServletException exception when trying to load context
	 * @throws IOException exception when trying to open input stream
	 */
	@Test
	public void testReloadUrlRewriterFromSetting() throws ServletException, IOException {
		filter.setSettingsReader(mockSettingsReader);
		filter.setStoreSeoUrlBuilderFactory(mockStoreSeoUrlBuilderFactory);
		
		mockFilterConfig = new MockFilterConfig() { 
			@Override
			public String getInitParameter(final String path) {
				if (path.equals(INIT_PARAM_MODREWRITECONFTEXT)) {
					return null;
					
				} else if (path.equals(INIT_PARAM_CONFRELOADCHECKINTERVAL)) {
					return String.valueOf(INIT_PARAM_CONFRELOADCHECKINTERVAL_VALUE); // seconds
				}
				
				return "setting:/some/path";
			}
		};
		
		final SettingValue settingValue = new SettingValueImpl() {
			private static final long serialVersionUID = -8632761342806376703L;

			/**
			 * Return a new Date() to indicate that setting has changed since last call.
			 */
			@Override
			public Date getLastModifiedDateInternal() {
				return new Date();
			}
		};
		settingValue.setValue("");
	
		// init
		context.checking(new Expectations() {
			{
				allowing(mockSettingsReader).getSettingValue(with(anyOf(aNull(String.class), any(String.class))));
				will(returnValue(settingValue));
				allowing(mockStoreSeoUrlBuilderFactory).resetFieldSeparator(with(any(String.class)));
			}
		});
		
		filter.init(mockFilterConfig);
		
		// first call to doFilter to test overridden methods of parent UrlRewriteFilter
		mockFilterChain = new MockFilterChain();
		filter.doFilter(mockRequest, mockResponse, mockFilterChain);
		long confLastLoad1 = filter.getConfLastLoad();
		
		// immediate 2nd call
		mockFilterChain = new MockFilterChain();
		filter.doFilter(mockRequest, mockResponse, mockFilterChain);
		long confLastLoad2 = filter.getConfLastLoad();
		
		assertEquals("The confLastLoad variables should be equal since a reload has not occurred.", 
				confLastLoad1, confLastLoad2);
		
		// sleep for a time longer than the interval
		try {
			Thread.sleep((INIT_PARAM_CONFRELOADCHECKINTERVAL_VALUE + 1) * MILLI);
		} catch (InterruptedException e) {
			fail("Test unreliable since Thread sleep time has been interrupted.");
		}
		
		// delayed 3rd call to force reload
		mockFilterChain = new MockFilterChain();
		filter.doFilter(mockRequest, mockResponse, mockFilterChain);
		long confLastLoad3 = filter.getConfLastLoad();
		
		assertTrue("The confLastLoad variables should not be equal since a reload must have occurred.", 
				confLastLoad2 != confLastLoad3);
	}
}
