/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.inventory.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.dao.InventoryDao;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.persistence.dao.impl.AbstractDaoImpl;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Data access methods for <code>Inventory</code>.
 */
public class InventoryDaoImpl extends AbstractDaoImpl implements InventoryDao {

	@Override
	public Inventory saveOrUpdate(final Inventory inventory) throws EpServiceException {
		return getPersistenceEngine().saveOrMerge(inventory);
	}

	@Override
	public void remove(final Inventory inventory) throws EpServiceException {
		getPersistenceEngine().delete(inventory);
	}
	
	@Override
	public Map<String, Inventory> getInventoryMap(final Collection<String> skuCodes, final long warehouseUid) {
		final List<Inventory> results = getPersistenceEngine().retrieveByNamedQueryWithList("INVENTORY_LIST_BY_SKUCODES_AND_WAREHOUSE_UID", "list",
				skuCodes,
				warehouseUid);
		final Map<String, Inventory> inventoryMap = new HashMap<String, Inventory>();
		for (Inventory inventory : results) {
			inventoryMap.put(inventory.getSkuCode(), inventory);
		}
		return inventoryMap;
	}
	
	@Override
	public Inventory getInventory(final String skuCode, final long warehouseUid) throws EpServiceException {
		final List<Inventory> inventoryList = getPersistenceEngine().retrieveByNamedQuery("INVENTORY_SELECT_BY_GUID_AND_WAREHOUSE_UID",
				skuCode,
				warehouseUid);
		if (inventoryList.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate guid: " + skuCode);
		}
		if (inventoryList.isEmpty()) {
			return null;
		}
		return inventoryList.get(0);
	}
	
	@Override
	public Map<Long, Inventory> getInventoriesForSku(final String skuCode) throws EpServiceException {
		List<Inventory> results = getPersistenceEngine().retrieveByNamedQuery("INVENTORY_SELECT_BY_SKUCODE", skuCode);
		Map<Long, Inventory> inventoryMap = new HashMap<Long, Inventory>();
		for (Inventory inventory : results) {
			inventoryMap.put(inventory.getWarehouseUid(), inventory);
		}
		return inventoryMap;
	}
	
	@Override
	public List<InventoryDto> findLowStockInventories(final Set<String> skuCodes, final long warehouseUid) {
		if (skuCodes == null || skuCodes.isEmpty()) {
			return getPersistenceEngine().retrieveByNamedQuery("LOWSTOCK_LEGACY_ALL", warehouseUid);
		}
		return getPersistenceEngine().retrieveByNamedQueryWithList("LOWSTOCK_LEGACY", "list", new ArrayList<String>(skuCodes), warehouseUid);
	}

}
