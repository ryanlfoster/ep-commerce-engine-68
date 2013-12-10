package com.elasticpath.commons.util.email;

import java.util.Map;

import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.domain.store.Store;

/**
 * Factory class that creates the velocity context for generating email body.
 */
public interface EmailContextFactory {
	/**
	 * Creates a new Map containing all of the data required to render an email body.
	 *
	 * @param store the store, or null if no particular store is specified
	 * @param emailProperties email specific properties
	 * @return A map
	 */
	Map<String, Object> createVelocityContext(final Store store, final EmailProperties emailProperties);
}
