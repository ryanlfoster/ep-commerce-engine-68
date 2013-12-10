package com.elasticpath.base;


/**
 * Indicates that the class has a global identity field.
 * 
 * @param <T> type of the global identity field
 */
public interface GloballyIdentifiable<T> {

	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	T getGuid();

	/**
	 * Set the guid.
	 * 
	 * @param guid the guid to set.
	 */
	void setGuid(T guid);
}
