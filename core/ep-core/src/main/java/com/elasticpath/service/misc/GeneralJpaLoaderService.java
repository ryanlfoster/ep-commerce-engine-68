/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.misc;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.EpPersistenceService;

/**
 * Provide the genral loading service for the value objects which don't have a seperate service.
 * JPA need all the object to be the detached object before update.
 * So this is used in the <code>EpBeanConverter</code> when convert the beans in bound.
 *
 */
public interface GeneralJpaLoaderService extends EpPersistenceService {

	/**
	 * Load the given object with the given uidpk.
	 *
	 * @param implType the real object type
	 * @param uidPk uid
	 * @return the object, null if not found by JPA.
	 */
	Object getObject(final Class<? extends Persistable> implType, final long uidPk);

}
