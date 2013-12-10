/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.inventory.domain.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.inventory.domain.InventoryJournalLock;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * An InventoryJournal records changes to quantity on-hand and allocated quantity for a particular Sku and Warehouse.
 */
@DataCache(enabled = false)
@Entity
@Table(name = InventoryJournalLockImpl.TABLE_NAME)
public class InventoryJournalLockImpl extends AbstractPersistableImpl implements InventoryJournalLock {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/** The name of the table and generator to use for persistence. */
	public static final String TABLE_NAME = "TINVENTORYJOURNALLOCK";

	/** */
	private long uidPk;

	/** */
	private Long warehouseUid;

	/** */
	private String skuCode;

	/** */
	private int lockCount;

	/** */
	private int version;

	/* (non-Javadoc)
	 * @see com.elasticpath.domain.inventory.impl.InventoryJournalLock#getLockCount()
	 */
	@Basic
	@Column(name = "LOCKCOUNT", nullable = false)
	public int getLockCount() {
		return this.lockCount;
	}

	/* (non-Javadoc)
	 * @see com.elasticpath.domain.inventory.impl.InventoryJournalLock#setLockCount(int)
	 */
	public void setLockCount(final int lockCount) {
		this.lockCount = lockCount;
	}

	/* (non-Javadoc)
	 * @see com.elasticpath.domain.inventory.impl.InventoryJournalLock#getUidPk()
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/* (non-Javadoc)
	 * @see com.elasticpath.domain.inventory.impl.InventoryJournalLock#setUidPk(long)
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/* (non-Javadoc)
	 * @see com.elasticpath.domain.inventory.impl.InventoryJournalLock#getWarehouseUid()
	 */
	@Basic
	@Column(name = "WAREHOUSE_UID", nullable = false)
	public Long getWarehouseUid() {
		return warehouseUid;
	}

	/* (non-Javadoc)
	 * @see com.elasticpath.domain.inventory.impl.InventoryJournalLock#setWarehouseUid(java.lang.Long)
	 */
	public void setWarehouseUid(final Long warehouseUid) {
		this.warehouseUid = warehouseUid;
	}

	/* (non-Javadoc)
	 * @see com.elasticpath.domain.inventory.impl.InventoryJournalLock#getSkuCode()
	 */
	@Basic
	@Column(name = "SKUCODE", nullable = false)
	public String getSkuCode() {
		return skuCode;
	}

	/* (non-Javadoc)
	 * @see com.elasticpath.domain.inventory.impl.InventoryJournalLock#setSkuCode(java.lang.String)
	 */
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	@Basic
	@Version
	public int getVersion() {
		return version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	/**
	 * If this object is persisted, we can just use the UIDPK.
	 * Otherwise a combination of the allocated quantity, quantity on hand, sku code, and warehouseUid is used.
	 *
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		final int shift = 32;
		int result = 1;
		if (isPersisted()) {
			return prime * result + (int) (getUidPk() ^ (getUidPk() >>> shift));
		}
		result = prime * result + ObjectUtils.hashCode(getSkuCode());
		result = prime * result + ObjectUtils.hashCode(getWarehouseUid());
		return result;
	}

	/**
	 * If both objects are InventoryJournals and are persisted then the UIDPK values are sufficient to test for equality.
	 * Otherwise a combination of the allocated quantity, quantity on hand, sku code, and warehouseUid is used.
	 *
	 * @param obj The object to compare.
	 * @return <code>true</code> if the given object is equal to this object.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof InventoryJournalLockImpl)) {
			return false;
		}
		final InventoryJournalLockImpl other = (InventoryJournalLockImpl) obj;
		if (isPersisted() && other.isPersisted()) {
			return getUidPk() == other.getUidPk();
		}
		return	ObjectUtils.equals(getSkuCode(), other.getSkuCode())
				&& ObjectUtils.equals(getWarehouseUid(), other.getWarehouseUid());
	}

}
