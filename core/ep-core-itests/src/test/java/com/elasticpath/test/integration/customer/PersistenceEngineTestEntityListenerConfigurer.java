package com.elasticpath.test.integration.customer;

import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * Reconfigures the PersistenceEngine for test. Can disable the last modified date listener for specific types.
 * This will effectively dirty the spring context. Tests using this functionality should indicate this using the
 * {@link org.springframework.test.annotation.DirtiesContext} annotation.
 */
public interface PersistenceEngineTestEntityListenerConfigurer {
	/**
	 * Prevents the {@link PersistenceEngine} from automatically setting the last modified date on the specified types.
	 * @param ignoredTypes the types for which to disable the behaviour
	 */
	public void disableLastModifiedListenerOnTypes(final Class<?>... ignoredTypes);
}
