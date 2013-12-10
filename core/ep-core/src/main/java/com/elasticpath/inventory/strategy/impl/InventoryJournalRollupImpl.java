/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.inventory.strategy.impl;

import org.springframework.util.ObjectUtils;

import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.strategy.InventoryJournalRollup;

/**
 * An InventoryJournalRollup contains the result of summing the quantity on-hand and allocated quantity of the TINVENTORYJOURNAL table
 * for an InventoryKey.
 */
public class InventoryJournalRollupImpl implements InventoryJournalRollup {

	/** Serial version id. */
	public static final long serialVersionUID = 5000000001L;

	/** */
	private InventoryKey inventoryKey;

	/** */
	private int allocatedQuantityDelta;

	/** */
	private int quantityOnHandDelta;

	/**
	 * Default constructor.
	 */
	public InventoryJournalRollupImpl() {
		super();
	}

	/**
	 * Constructor used by JPA Query.
	 * @param skuCode the sku code.
	 * @param warehouseUid warehouse uidpk.
	 * @param allocatedQuantityDelta summed allocated quantity delta.
	 * @param quantityOnHandDelta summed quantity on hand delta.
	 */
	public InventoryJournalRollupImpl(final String skuCode, final Long warehouseUid,
			final long allocatedQuantityDelta, final long quantityOnHandDelta) {
		this.inventoryKey = new InventoryKey(skuCode, warehouseUid);
		this.allocatedQuantityDelta = (int) allocatedQuantityDelta;
		this.quantityOnHandDelta = (int) quantityOnHandDelta;
	}

	@Override
	public int getQuantityOnHandDelta() {
		return this.quantityOnHandDelta;
	}

	@Override
	public void setQuantityOnHandDelta(final int quantityOnHandDelta) {
		this.quantityOnHandDelta = quantityOnHandDelta;
	}

	@Override
	public InventoryKey getInventoryKey() {
		return inventoryKey;
	}

	@Override
	public void setInventoryKey(final InventoryKey inventoryKey) {
		this.inventoryKey = inventoryKey;
	}

	@Override
	public int getAllocatedQuantityDelta() {
		return allocatedQuantityDelta;
	}

	@Override
	public void setAllocatedQuantityDelta(final int allocatedQuantityDelta) {
		this.allocatedQuantityDelta = allocatedQuantityDelta;
	}
	
	/**
	 * The hashcode is a combination of the allocated quantity, quantity on hand, and inventoryKey.
	 * 
	 * @return The hashcode.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + allocatedQuantityDelta;
		result = prime * result + quantityOnHandDelta;
		result = prime * result + ObjectUtils.nullSafeHashCode(inventoryKey);
		return result;
	}

	/**
	 * Equality is based on a combination of the allocated quantity, quantity on hand, and inventoryKey.
	 * 
	 * @param obj The object to test for equality.
	 * @return True if this object and the given object are equal.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		InventoryJournalRollupImpl other = (InventoryJournalRollupImpl) obj;
		if (allocatedQuantityDelta != other.allocatedQuantityDelta) {
			return false;
		}
		if (quantityOnHandDelta != other.quantityOnHandDelta) {
			return false;
		}
		if (inventoryKey == null) {
			if (other.inventoryKey != null) {
				return false;
			}
		} else if (!inventoryKey.equals(other.inventoryKey)) {
			return false;
		}
		return true;
	}

}
