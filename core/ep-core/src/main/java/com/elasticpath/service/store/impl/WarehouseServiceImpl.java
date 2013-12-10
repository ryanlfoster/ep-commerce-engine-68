/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.store.impl;

import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.store.WarehouseService;

/**
 * Provides warehouse-related business service.
 */
public class WarehouseServiceImpl extends AbstractEpPersistenceServiceImpl implements WarehouseService {

	/**
	 * Saves or updates a given <code>Warehouse</code>.
	 * 
	 * @param warehouse the <code>Warehouse</code> to save or update
	 * @throws EpServiceException in case of any errors
	 *
	 * @return Warehouse the save or merged instance
	 */
	public Warehouse saveOrUpdate(final Warehouse warehouse) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().saveOrUpdate(warehouse);
	}

	/**
	 * Deletes a warehouse and it's associated address.
	 * 
	 * @param warehouse the warehouse to remove
	 * @throws EpServiceException in case of any errors
	 */
	public void remove(final Warehouse warehouse) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(warehouse);
	}

	/**
	 * Gets a <code>Warehouse</code> with the given UID. Return null if no matching records exist.
	 * 
	 * @param warehouseUid the <code>Warehouse</code> UID
	 * @return the <code>Warehouse</code> with the attributes populated if the UID exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	public Warehouse getWarehouse(final long warehouseUid) throws EpServiceException {
		sanityCheck();
		Warehouse warehouse = null;
		if (warehouseUid <= 0) {
			warehouse = getBean(ContextIdNames.WAREHOUSE);
		} else {
			warehouse = getPersistentBeanFinder().get(ContextIdNames.WAREHOUSE, warehouseUid);
		}
		return warehouse;
	}

	/**
	 * Gets a list of all warehouse UIDs.
	 * 
	 * @return a list of all warehouse UIDs
	 * @throws EpServiceException in case of any errors
	 */
	public List<Long> findAllWarehouseUids() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_WAREHOUSE_UIDS");
	}

	/**
	 * Gets a list of all warehouses.
	 * 
	 * @return a list of all warehouses
	 * @throws EpServiceException in case of any errors
	 */
	public List<Warehouse> findAllWarehouses() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_ALL_WAREHOUSES");
	}

	/**
	 * Generic get method for a warehouse.
	 * 
	 * @param uid the persisted warehouse uid
	 * @return the persisted instance of a <code>Warehouse</code> if it exists, otherwise null
	 * @throws EpServiceException in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return getWarehouse(uid);
	}

	/**
	 * Check if warehouse in use.
	 * 
	 * @param warehouseUidPk the warehouse uidPk
	 * @return true if given warehouse in use false otherwise
	 * @throws EpServiceException in case of any errors
	 */
	public boolean warehouseInUse(final Long warehouseUidPk) throws EpServiceException {
		sanityCheck();
		Object[] queryParams = new Object[] { warehouseUidPk };
		return !getPersistenceEngine().retrieveByNamedQuery("FIND_WAREHOUSE_UIDS_IN_STORE_USE", queryParams).isEmpty()
				|| !getPersistenceEngine().retrieveByNamedQuery("FIND_WAREHOUSE_UIDS_IN_USER_USE", queryParams).isEmpty()
				|| !getPersistenceEngine().retrieveByNamedQuery("FIND_WAREHOUSE_UIDS_IN_IMPORTJOB_USE", queryParams).isEmpty()
				|| !getPersistenceEngine().retrieveByNamedQuery("FIND_WAREHOUSE_UIDS_IN_INVENTORY_USE", queryParams).isEmpty();
	}
	
	/**
	 * Retrieves a warehouse by it's code. If no warehouse is found with the given code, returns
	 * <code>null</code>.
	 * 
	 * @param code the code to look for
	 * @return the warehouse with the given code or <code>null</code> if none could be found
	 * @throws EpServiceException in case of any errors
	 */
	public Warehouse findByCode(final String code) throws EpServiceException {
		sanityCheck();
		final List<Warehouse> result = getPersistenceEngine().retrieveByNamedQuery("FIND_WAREHOUSE_BY_CODE", code);
		if (result == null || result.isEmpty()) {
			return null;
		}
		if (result.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate code " + code);
		}
		return result.get(0);
	}
}
