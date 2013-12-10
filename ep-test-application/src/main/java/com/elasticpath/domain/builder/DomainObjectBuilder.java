/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.domain.builder;

/**
 * Base interface for all builders that build domain objects to implement.
 */
public interface DomainObjectBuilder<T> {
	
	/**
	 * Build domain object.
	 *
	 * @return the new domain object with properties set
	 */
	T build(); 
}
