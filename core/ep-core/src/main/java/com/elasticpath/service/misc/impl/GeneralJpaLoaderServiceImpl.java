/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.misc.impl;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.GeneralJpaLoaderService;

/**
 * Provide the general loading service for the value objects which don't have a seperate service.
 * JPA need all the object to be the detached object before update.
 * So this is used in the <code>EpBeanConverter</code> when convert the beans in bound.
 *
 */
public class GeneralJpaLoaderServiceImpl extends AbstractEpPersistenceServiceImpl implements GeneralJpaLoaderService {

	/**
	 * Load the given object with the given uidpk.
	 *
	 * @param implType the real object type
	 * @param uidPk uid
	 * @return the object, null if not found by JPA.
	 */
	public Object getObject(final Class<? extends Persistable> implType, final long uidPk) {
		return this.getPersistenceEngine().get(implType, uidPk);
	}

	/**
	 * Return null since we don't know what object you want.
	 * @param uid given id.
	 * @return null
	 */
	public Object getObject(final long uid) {
		return null;
	}

}
