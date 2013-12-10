/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.sfweb.util.impl;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;

import org.junit.Before;
import org.junit.Test;


/**
 * Test <code>IpAddressResolverImpl</code>.
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public class IpAddressResolverImplTest {
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private HttpServletRequest mockHttpRequest;
	
	private IpAddressResolverImpl ipAddressResolver;
	
	private HttpServletRequest request;
	
	private static final String X_FORWARDED_HEADER_NAME = "X-Forwarded-For";
	
	private static final String X_FORWARDED_HEADER_VALUE = " 123.123.123.123 , 100.100.100.100 , 127.0.0.1";
	
	private static final String CLIENT_IP_ADDRESS = "130.1.25.65";
	
	private static final String CLIENT_IP_ADDRESS_THROUGH_PROXY = "123.123.123.123";
	

	@Before
	public void setUp() throws Exception { // NOPMD
		
		ipAddressResolver = new IpAddressResolverImpl();
		
		ipAddressResolver.setForwardedHeaderName(X_FORWARDED_HEADER_NAME);
		
		mockHttpRequest = context.mock(HttpServletRequest.class);
		
		this.request = mockHttpRequest;

		
	}
	
	/**
	 * 
	 * Test client ip resolving with proxy.
	 * 
	 */
	@Test
	public void testIpResolveWithProxy() {
		context.checking(new Expectations() {
			{
				allowing(mockHttpRequest).getHeader(X_FORWARDED_HEADER_NAME);
				will(returnValue(X_FORWARDED_HEADER_VALUE));

				allowing(mockHttpRequest).getRemoteAddr();
				will(returnValue(CLIENT_IP_ADDRESS));
			}
		});

		assertEquals("Remote ip Address should equal client ip address", CLIENT_IP_ADDRESS_THROUGH_PROXY, ipAddressResolver.getRemoteAddr(request));
	}
	
	/**
	 * 
	 * Test client ip resolving without proxy.
	 * 
	 */
	@Test
	public void testIpResolveWithoutProxy() {
		context.checking(new Expectations() {
			{
				allowing(mockHttpRequest).getHeader(X_FORWARDED_HEADER_NAME);
				will(returnValue(null));

				allowing(mockHttpRequest).getRemoteAddr();
				will(returnValue(CLIENT_IP_ADDRESS));
			}
		});
		
		assertEquals("Remote ip Address should equal client ip address", CLIENT_IP_ADDRESS, ipAddressResolver.getRemoteAddr(request));
		
		
	}
	
	/**
	 * 
	 * Test client ip resolving without proxy.
	 * 
	 */
	@Test
	public void testIpResolverWrongConfigured() {
		context.checking(new Expectations() {
			{
				allowing(mockHttpRequest).getHeader(X_FORWARDED_HEADER_NAME);
				will(returnValue(null));

				allowing(mockHttpRequest).getRemoteAddr();
				will(returnValue(CLIENT_IP_ADDRESS));
			}
		});
		
		ipAddressResolver.setForwardedHeaderName(null);
		
		assertEquals("Remote ip Address should equal client ip address", CLIENT_IP_ADDRESS, ipAddressResolver.getRemoteAddr(request));
		
		
	}

}
