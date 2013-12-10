package com.elasticpath.test.integration.customer.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.persistence.impl.LastModifiedEntityListener;
import com.elasticpath.persistence.openjpa.impl.JpaPersistenceEngineImpl;
import com.elasticpath.test.integration.customer.PersistenceEngineTestEntityListenerConfigurer;
import com.elasticpath.test.integration.customer.TypeFilteringLastModifiedEntityListenerDouble;

/**
 * Reconfigures the PersistenceEngine for test. Can disable the last modified date for specific types.
 */
public class JpaPersistenceEngineTestEntityListenerConfigurerImpl implements PersistenceEngineTestEntityListenerConfigurer {
	@Autowired
	LastModifiedEntityListener oldLastModifiedEntityListener;

	@Autowired
	BeanFactory beanFactory;

	@Autowired
	JpaPersistenceEngineImpl jpaPersistenceEngine;

	public void disableLastModifiedListenerOnTypes(final Class<?>... ignoredTypes) {

		LastModifiedEntityListener lastModifiedEntityListener = new TypeFilteringLastModifiedEntityListenerDouble(ignoredTypes);

		lastModifiedEntityListener.setBeanFactory(beanFactory);

		jpaPersistenceEngine.removePersistenceEngineEntityListener(oldLastModifiedEntityListener);
		jpaPersistenceEngine.addPersistenceEngineEntityListener(lastModifiedEntityListener);
	}
}