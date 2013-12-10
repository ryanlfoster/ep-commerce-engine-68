/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.geoip.provider.impl;

import org.junit.Test;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.elasticpath.commons.util.Pair;


/**
 * Test <code>DomainNameResolverImpl</code>.
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public class DomainNameResolverImplTest extends AbstractDependencyInjectionSpringContextTests {

	private static final String CLIENT_IP_ADDRESS = "130.1.25.65";
	
	private static final String ORACLE = "oracle";
	
	private static final String COM = "com";
	
	private static final String CLIENT_IP_HOST_NAME_1 = "www.oracle.com";
	
	private static final String CLIENT_IP_HOST_NAME_2 = "com";

	/**
	 * Get the location of the spring configuration.
	 *
	 * @return the spring configuration file for this test
	 */
	@Override
	protected String[] getConfigLocations() {
		return new String[] { "classpath:/testNullGeoIp.xml" };
	}

	/**
	 * Test getFirstAndSecondLevelDomainNameByHostName method.
	 */
	@Test
	public void testGetFirstAndSecondLevelDomainNameByHostName() {
		
		DomainNameResolverImpl domainNameResolverImpl = new DomainNameResolverImpl();
		
		Pair<String, String> firstAndSecondDomainName = 
			domainNameResolverImpl.getFirstAndSecondLevelDomainNameByHostName(CLIENT_IP_HOST_NAME_1, CLIENT_IP_ADDRESS);		
		assertEquals("First level name is not 'com' but '" + firstAndSecondDomainName.getFirst() + "'", 
			COM, firstAndSecondDomainName.getFirst());		
		assertEquals("Second level name is not 'oracle' but '" + firstAndSecondDomainName.getSecond() + "'", 
			ORACLE, firstAndSecondDomainName.getSecond());
		
		firstAndSecondDomainName = 
			domainNameResolverImpl.getFirstAndSecondLevelDomainNameByHostName(CLIENT_IP_HOST_NAME_2, CLIENT_IP_ADDRESS);		
		assertEquals("First level name is not 'com' but '" + firstAndSecondDomainName.getFirst() + "'", 
			COM, firstAndSecondDomainName.getFirst());		
		assertNull("Second level name is not NULL but '" + firstAndSecondDomainName.getSecond() + "'", 
			firstAndSecondDomainName.getSecond());
		
		firstAndSecondDomainName = 
			domainNameResolverImpl.getFirstAndSecondLevelDomainNameByHostName(CLIENT_IP_ADDRESS, CLIENT_IP_ADDRESS);		
		assertNull("The first and second level domain name pair is not NULL", firstAndSecondDomainName);
		
		firstAndSecondDomainName = 
			domainNameResolverImpl.getFirstAndSecondLevelDomainNameByHostName("", CLIENT_IP_ADDRESS);		
		assertNull("The first and second level domain name pair is not NULL", firstAndSecondDomainName);

		firstAndSecondDomainName =
			domainNameResolverImpl.getFirstAndSecondLevelDomainNameByHostName(null, CLIENT_IP_ADDRESS);
		assertNull("The first and second level domain name pair is not NULL", firstAndSecondDomainName);


	}
	

}
