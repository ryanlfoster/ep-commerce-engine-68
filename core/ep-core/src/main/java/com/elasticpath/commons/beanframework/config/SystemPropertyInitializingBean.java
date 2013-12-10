/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.beanframework.config;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * Bean for initializing System properties given in the spring configuration.
 */
public class SystemPropertyInitializingBean implements InitializingBean {

	private Map<String, String> systemProperties;
	
	/**
	 * Sets the system properties supplied by the "systemProperties" property.
	 * 
	 * @throws Exception in case of error setting a system property
	 */
	public void afterPropertiesSet() throws Exception {
		if (MapUtils.isEmpty(systemProperties)) {
			return;
		}
		
		for (String property : systemProperties.keySet()) {
			System.setProperty(property, systemProperties.get(property));
		}

		// Allow garbage collection
		systemProperties = null;
	}

	/**
	 *
	 * @param systemProperties the systemProperties to set
	 */
	public void setSystemProperties(final Map<String, String> systemProperties) {
		this.systemProperties = systemProperties;
	}

}
