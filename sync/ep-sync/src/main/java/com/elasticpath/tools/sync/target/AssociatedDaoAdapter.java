/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.tools.sync.target;

import java.util.List;

import com.elasticpath.persistence.api.Persistable;

/**
 * An extension of the DaoAdapter that provides methods for getting a list of
 * guids of the adapted object class given the class and guid of the associated object.
 * 
 * @param <E> Persistable to synchronize, e.g a Coupon
 */
public interface AssociatedDaoAdapter<E extends Persistable> extends DaoAdapter<E> {

	/**
	 * Get the list of guids of the adapted class that are associated with the
	 * instance of the associated class that has the given guid.
	 * 
	 * @param clazz the associated class
	 * @param guid the guid of the associated object
	 * @return the list of associated guids
	 */
	List<String> getAssociatedGuids(Class< ? > clazz, String guid);
	
	/**
	 * Get the type of the object being adapted.
	 * 
	 * @return the class being adapted by this adapter.
	 */
	Class< ? > getType();
}
