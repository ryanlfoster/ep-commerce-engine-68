package com.elasticpath.tools.sync.merge;

import com.elasticpath.persistence.api.Persistable;

/**
 * Interface provides factory method for creating EP domain bean by class specification.
 */
public interface BeanCreator {

	/**
	 * Creates object by given class.
	 * 
	 * @param clazz the class of object
	 * @return created object 
	 */
	Persistable createBean(final Class< ? > clazz);

}