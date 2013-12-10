/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.beanfactory;

import org.springframework.beans.factory.BeanFactory;

import com.elasticpath.tools.sync.configuration.ConnectionConfiguration;

/**
 * A context initializer for setting up a Spring context.
 */
public interface ContextInitializer {

	/**
	 * Initializes a context by providing a connection configuration.
	 * 
	 * @param config the connection configuration
	 * @return a bean factory representing the initialized application context
	 */
	BeanFactory initializeContext(ConnectionConfiguration config);
	
	/**
	 * Destroy context.
	 *
	 * @param beanFactory the bean factory
	 */
	void destroyContext(BeanFactory beanFactory);
}
