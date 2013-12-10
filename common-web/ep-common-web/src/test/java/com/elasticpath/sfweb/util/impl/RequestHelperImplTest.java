/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.util.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.service.catalogview.StoreConfig;

/**
 * Test <code>RequestHelperImpl</code>.
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public class RequestHelperImplTest {

	private static final String BAD_INT_STRING = "abcde";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private RequestHelperImpl requestHelper;

	private MockHttpServletRequest request;


	/**
	 * Prepare for tests.
	 *
	 * @throws Exception
	 * @throws Exception -- in case of errors
	 */
	@Before
	public void setUp() throws Exception {
		requestHelper = new RequestHelperImpl();
		setupHttpRequest();

		final StoreConfig mockStoreConfig = context.mock(StoreConfig.class);
		context.checking(new Expectations() {
			{
				allowing(mockStoreConfig).getStore();
				will(returnValue(null));
			}
		});

		requestHelper.setStoreConfig(mockStoreConfig);

	}

	private void setupHttpRequest() {
		this.request = new MockHttpServletRequest();
	}

	/**
	 * Test method for 'com.elasticpath.web.util.impl.RequestHelperImpl.getUrl(HttpServletRequest)'.
	 */
	@Test
	public void testGetUrl() {
		final String requestUri = "/aaa/bbb/ccc.ep";
		final String context = "/aaa";
		String scheme = "http";
		String serverName = "aaa.bbb";
		final int serverPort = 8080;
		request.setScheme(scheme);
		request.setServerName(serverName);
		request.setServerPort(serverPort);
		request.setRequestURI(requestUri);
		request.setContextPath(context);
		final String result = this.requestHelper.getUrl(request);
		final String expectedUrl = scheme + "://" + serverName + ":" + serverPort + context;
		assertEquals("url should = http://aaa.bbb", expectedUrl, result);
	}

	/**
	 * Test method for 'com.elasticpath.web.util.impl.RequestHelperImpl.getUrl(HttpServletRequest)'.
	 */
	@Test
	public void testGetIntParameterOrAttribute() {
		final String name = "cID";
		final String parameterValue = "12";
		final String attributeValue = "23";
		final int defaultValue = 0;


		// Parameter value has the highest priority.
		request.setParameter(name, parameterValue);
		assertEquals("parameter should be 12",
				Integer.parseInt(parameterValue), requestHelper.getIntParameterOrAttribute(request, name, defaultValue));

		// When parameter value is null, use the attribute value instead.
		request.setParameter(name, (String) null);
		request.setAttribute(name, attributeValue);
		assertEquals("attribute should be 23",
				Integer.parseInt(attributeValue), requestHelper.getIntParameterOrAttribute(request, name, defaultValue));

		// When parameter value is not a valid number, use the attribute value instead.
		request.setParameter(name, BAD_INT_STRING);
		request.setAttribute(name, attributeValue);
		assertEquals("atrribute should be 23",
				Integer.parseInt(attributeValue), requestHelper.getIntParameterOrAttribute(request, name, defaultValue));

		// When both parameter value ant attribute value is null, use the default value.
		request.setParameter(name, (String) null);
		request.setAttribute(name, null);
		assertEquals("parameter should be 0", defaultValue, requestHelper.getIntParameterOrAttribute(request, name, defaultValue));

		// When neither parameter value nor attribute value is valid, use the default value.
		request.removeAllParameters();
		assertEquals("parameter should be 0", defaultValue, requestHelper.getIntParameterOrAttribute(request, name, defaultValue));

		request.setAttribute(name, BAD_INT_STRING);
		assertEquals("parameter should be 0",
				defaultValue, requestHelper.getIntParameterOrAttribute(request, name, defaultValue));
	}

	/**
	 * Test method for 'com.elasticpath.web.util.impl.RequestHelperImpl.getString(HttpServletRequest)'.
	 */
	@Test
	public void testGetStringParameterOrAttribute() {
		final String name = "cID";
		final String parameterValue = "12";
		final String attributeValue = "23";
		final String defaultValue = "default";

		// Parameter value has the highest priority.
		request.setParameter(name, parameterValue);
		assertEquals("parameter should be 12", parameterValue, requestHelper.getStringParameterOrAttribute(request, name, defaultValue));

		// When parameter value is null, use the attribute value instead.
		request.setParameter(name, (String) null);
		request.setAttribute(name, attributeValue);
		assertEquals("parameter should be 23", attributeValue, requestHelper.getStringParameterOrAttribute(request, name, defaultValue));

		// When both parameter value ant attribute value is null, use the default value.
		request.removeAllParameters();
		request.setAttribute(name, null);
		assertEquals("parameter should be 'default'", defaultValue, requestHelper.getStringParameterOrAttribute(request, name, defaultValue));
	}
}
