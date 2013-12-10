package com.elasticpath.inventory.domain;

import com.elasticpath.persistence.api.Persistable;

/**
 * Inventory Journal Lock.
 */
public interface InventoryJournalLock extends Persistable {

	/**
	 * @return sku code.
	 */
	String getSkuCode();

	/**
	 * @param skuCode to set.
	 */
	void setSkuCode(final String skuCode);
	
	/**
	 * @return warehouse uid.
	 */
	Long getWarehouseUid();

	/**
	 * @param warehouseUid to set.
	 */
	void setWarehouseUid(final Long warehouseUid);
	
	/**
	 * @return lock count.
	 */
	int getLockCount();

	/**
	 * @param lockCount to set.
	 */
	void setLockCount(final int lockCount);

}