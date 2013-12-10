/**
 *
 */
package com.elasticpath.service.targetedselling;

import java.util.List;

import com.elasticpath.persistence.api.Entity;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Service
 * base interface for persistent operations.
 * @param <T>
 */
public interface TargetedSellingService<T extends Entity> {

	/**
	 * Persist the given entity.
	 * @param object persisted entity
	 * @return result operation entity
	 * @throws EpServiceException for any errors
	 */
	T add(T object) throws EpServiceException;

	/**
	 * Save or update entity.
	 * @param object entity for save or update
	 * @return result operation entity
	 * @throws EpServiceException for any errors
	 */
	T saveOrUpdate(T object) throws EpServiceException;

	/**
	 * Try to remove entity.
	 * @param object entity for remove
	 * @throws EpServiceException for any errors
	 */
	void remove(T object) throws EpServiceException;

	/**
	 * Find all entities.
	 * @return return result list
	 * @throws EpServiceException for any error
	 */
	List<T> findAll() throws EpServiceException;

	/**
	 * Find entity by name.
	 * @param name entity name
	 * @return result entity
	 * @throws EpServiceException for any errors
	 */
	T findByName(String name) throws EpServiceException;

	/**
	 * Find entity by name via like.
	 * @param string entity name
	 * @return result entity
	 * @throws EpServiceException for any errors
	 */
	List<T> findByNameLike(String string) throws EpServiceException;

}
