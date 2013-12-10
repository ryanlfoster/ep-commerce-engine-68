/**
 * 
 */
package com.elasticpath.tags.service;

/**
 * GenericService is a service for common operations.
 *
 *@param <T> - type of the domain object used in GenericService.
 */
public interface GenericService<T> {

	/**
	 * Find object by GUID.
	 * @param guid GUID
	 * @return object to find
	 */
	T findByGuid(String guid);
}
