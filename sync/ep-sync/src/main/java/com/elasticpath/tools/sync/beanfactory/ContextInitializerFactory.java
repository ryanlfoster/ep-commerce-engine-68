/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.beanfactory;

import java.util.Map;

import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * A context initializer factory for creating context initializers 
 * by connection and destination types.
 * Currently only retrieves a bean from the Spring context.
 */
public class ContextInitializerFactory {

	
	private Map<String, ContextInitializer> contextInitializersMap;

	/**
	 * Creates a new initializer.
	 * <p>
	 * Note: This implementation relies on the fact that context initializers are created and set by the Spring framework.
	 * 
	 * @param connectionType the connection type (e.g. local, remote, ...)
	 * @param destinationType the destination type (e.g. source, target, ...)
	 * @return the context initializer instance
	 */
	public ContextInitializer create(final String connectionType, final String destinationType) {
		if (connectionType == null) {
			throw new IllegalArgumentException("The parameter 'connectionType' is required");
		}
		if (destinationType == null) {
			throw new IllegalArgumentException("The parameter 'destinationType' is required");
		}
		final String configurationId = connectionType + '.' + destinationType;
		ContextInitializer contextInitializer = getContextInitializersMap().get(configurationId);
		if (contextInitializer == null) {
			throw new SyncToolConfigurationException("Unsupported configuration: " + configurationId);
		}
		return contextInitializer;
	}

	/**
	 *
	 * @return the contextInitializersMap
	 */
	public Map<String, ContextInitializer> getContextInitializersMap() {
		if (contextInitializersMap == null) {
			throw new SyncToolConfigurationException("No context initializers are specified");
		}
		return contextInitializersMap;
	}

	/**
	 *
	 * @param contextInitializersMap the contextInitializersMap to set
	 */
	public void setContextInitializersMap(final Map<String, ContextInitializer> contextInitializersMap) {
		this.contextInitializersMap = contextInitializersMap;
	}
	
	
}
