/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.store;

import java.util.List;

import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides warehouse-related services.
 */
public interface WarehouseService extends EpPersistenceService {

	/**
	 * Saves or updates a given <code>Warehouse</code>.
	 * 
	 * @param warehouse the <code>Warehouse</code> to save or update
	 * @throws EpServiceException in case of any errors
	 * 
	 * @return Warehouse the save or merged instance
	 */
	Warehouse saveOrUpdate(final Warehouse warehouse) throws EpServiceException;

	/**
	 * Deletes a warehouse and it's associated address.
	 * 
	 * @param warehouse the warehouse to remove
	 * @throws EpServiceException in case of any errors
	 */
	void remove(final Warehouse warehouse) throws EpServiceException;

	/**
	 * Gets a <code>Warehouse</code> with the given UID. Return null if no matching records exist.
	 * 
	 * @param warehouseUid the <code>Warehouse</code> UID
	 * @return the <code>Warehouse</code> with the attributes populated if the UID exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	Warehouse getWarehouse(final long warehouseUid) throws EpServiceException;

	/**
	 * Gets a list of all warehouse UIDs.
	 * 
	 * @return a list of all warehouse UIDs
	 * @throws EpServiceException in case of any errors
	 */
	List<Long> findAllWarehouseUids() throws EpServiceException;

	/**
	 * Gets a list of all warehouses.
	 * 
	 * @return a list of all warehouses
	 * @throws EpServiceException in case of any errors
	 */
	List<Warehouse> findAllWarehouses() throws EpServiceException;

	/**
	 * Check if warehouse in use.
	 *
	 * @param warehouseUidPk the warehouse uidPk
	 * @return true if given warehouse in use false otherwise
	 * @throws EpServiceException in case of any errors
	 */
	boolean warehouseInUse(final Long warehouseUidPk) throws EpServiceException;
	
	/**
	 * Retrieves a warehouse by it's code. If no warehouse is found with the given code, returns
	 * <code>null</code>.
	 * 
	 * @param code the code to look for
	 * @return the warehouse with the given code or <code>null</code> if none could be found
	 * @throws EpServiceException in case of any errors
	 */
	Warehouse findByCode(String code) throws EpServiceException;
}
