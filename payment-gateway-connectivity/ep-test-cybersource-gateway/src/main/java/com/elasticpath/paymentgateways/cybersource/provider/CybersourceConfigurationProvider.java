/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.paymentgateways.cybersource.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Cybersource configuration values provider.
 */
public final class CybersourceConfigurationProvider {
	private static final CybersourceConfigurationProvider INSTANCE = new CybersourceConfigurationProvider();
	
	/**
	 * Gets singleton instance.
	 * 
	 * @return gets the singleton instance of the {@link CybersourceConfigurationProvider}
	 */
	public static CybersourceConfigurationProvider getProvider() {
		return INSTANCE;
	}
	
	/**
	 * Ensure class is not instantiated.
	 */
	private CybersourceConfigurationProvider() {
		//empty constructor
	}
	
	/**
	 * Gets the configuration values used to configure the Cybersource payment gateway.
	 *
	 * @return the Cybsersource configuration values
	 */
	public Map<String, String> getConfigurationMap() {
		HashMap<String, String> cybersourceConfigurations = new HashMap<String, String>();
		cybersourceConfigurations.put("merchantID", "ekontest2013");
		cybersourceConfigurations.put("logMaximumSize", "10");
		cybersourceConfigurations.put("sendToProduction", "false");
		cybersourceConfigurations.put("logDirectory", "log");
		cybersourceConfigurations.put("targetAPIVersion", "1.24");
		cybersourceConfigurations.put("keysDirectory", "target/test-classes/payment/cybersource");
		cybersourceConfigurations.put("enableLog", "false");
		return cybersourceConfigurations;
	}
	
	/**
	 * Gets the configuration values as properties.
	 *
	 * @return the properties
	 */
	public Properties getConfigurationProperties() {
		Properties cybersourceProperties = new Properties();
		cybersourceProperties.putAll(getConfigurationMap());
		return cybersourceProperties;
	}
}
