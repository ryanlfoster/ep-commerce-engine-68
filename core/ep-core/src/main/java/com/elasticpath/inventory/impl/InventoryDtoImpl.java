/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.inventory.impl;

import java.util.Date;

import org.apache.commons.lang.ObjectUtils;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;

/**
 * Represents inventory information about a <code>ProductSku</code>.
 */
@SuppressWarnings("PMD.ExcessiveParameterList")
public class InventoryDtoImpl implements InventoryDto, Cloneable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * This is the physical number of units that the warehouse has in stock.
	 * Note: Can be negative if minor over-selling occurs under load.
	 */
	private int quantityOnHand = 0;

	/**
	 * This is the number of units that exist in the warehouse, but are not available for purchase. This supports the case where you may want to keep
	 * a number of items in stock at all times so that you can support, for example, exchanges. If the QuantityOnHand is 5 and the ReservedQuantity
	 * is 5, then the item is effectively out of stock.
	 */
	private int reservedQuantity = 0;

	/**
	 * Once the quantity on hand goes below this number, then the item should be re-ordered. Currently used for reporting purposes only.
	 */
	private int reorderMinimum = 0;

	/**
	 * This is the date on which new stock is expected to come into the warehouse. Currently used to indicate to shoppers some future date when an
	 * item may become available.
	 */
	private Date restockDate;

	/**
	 * This is the number of items a customer currently want to reorder, must be more than reorderMinimum.
	 */
	private int reorderQuantity = 1;

	private int allocatedQuantity;

	private InventoryKey inventoryKey = new InventoryKey();

	/**
	 * Default constructor.
	 */
	public InventoryDtoImpl() {
		super();
	}

	//CHECKSTYLE:OFF
	/**
	 * Constructor for the low stock query.
	 *
	 * @param skuCode sku code
	 * @param warehouseUid warehouse uid
	 * @param quantityOnHand quantity on hand
	 * @param onHandDeltaSum on hand delta sum
	 * @param allocatedQuantity allocated qty
	 * @param allocatedDeltaSum allocated delta sum
	 * @param reservedQuantity reserved qty
	 * @param reorderMinimum reorder minimum
	 * @param reorderQuantity reored qty
	 * @param restockDate restock date
	 */
	public InventoryDtoImpl(final String skuCode, final Long warehouseUid,
			final int quantityOnHand, final long onHandDeltaSum, final int allocatedQuantity, final long allocatedDeltaSum,
			final int reservedQuantity, final int reorderMinimum, final int reorderQuantity, final Date restockDate) { //NOCHECKSTYLE
		inventoryKey.setSkuCode(skuCode);
		inventoryKey.setWarehouseUid(warehouseUid);
		this.quantityOnHand = (int) (quantityOnHand + onHandDeltaSum);
		this.allocatedQuantity = (int) (allocatedQuantity + allocatedDeltaSum);
		this.reservedQuantity = reservedQuantity;
		this.reorderMinimum = reorderMinimum;
		this.restockDate = restockDate;
		this.reorderQuantity = reorderQuantity;
	}

	/**
	 * Constructor for the low stock query.
	 *
	 * @param skuCode sku code
	 * @param warehouseUid warehouse uid
	 * @param quantityOnHand quantity on hand
	 * @param allocatedQuantity allocated qty
	 * @param reservedQuantity reserved qty
	 * @param reorderMinimum reorder minimum
	 * @param reorderQuantity reored qty
	 * @param restockDate restock date
	 */
	public InventoryDtoImpl(final String skuCode, final Long warehouseUid,
			final int quantityOnHand, final int allocatedQuantity,
			final int reservedQuantity, final int reorderMinimum, final int reorderQuantity, final Date restockDate) { //NOCHECKSTYLE
		inventoryKey.setSkuCode(skuCode);
		inventoryKey.setWarehouseUid(warehouseUid);
		this.quantityOnHand = quantityOnHand;
		this.allocatedQuantity = allocatedQuantity;
		this.reservedQuantity = reservedQuantity;
		this.reorderMinimum = reorderMinimum;
		this.restockDate = restockDate;
		this.reorderQuantity = reorderQuantity;
	}
	//CHECKSTYLE:ON

	@Override
	public int getQuantityOnHand() {
		return this.quantityOnHand;
	}

	@Override
	public void setQuantityOnHand(final int quantityOnHand) {
		this.quantityOnHand = quantityOnHand;
	}

	@Override
	public int getReservedQuantity() {
		return reservedQuantity;
	}

	@Override
	public void setReservedQuantity(final int reservedQuantity) {
		if (reservedQuantity < 0) {
			throw new EpDomainException("Cannot set negative reserved quantity.");
		}

		this.reservedQuantity = reservedQuantity;
	}

	@Override
	public int getReorderMinimum() {
		return this.reorderMinimum;
	}

	@Override
	public void setReorderMinimum(final int reorderMinimum) {
		if (reorderMinimum < 0) {
			throw new EpDomainException("Cannot set negative reorder minimum");
		}
		this.reorderMinimum = reorderMinimum;
	}

	@Override
	public Date getRestockDate() {
		return this.restockDate;
	}

	@Override
	public void setRestockDate(final Date restockDate) {
		this.restockDate = restockDate;
	}

	@Override
	public int getAvailableQuantityInStock() {
		return getQuantityOnHand() - getReservedQuantity() - getAllocatedQuantity();
	}

	@Override
	public int getReorderQuantity() {
		return reorderQuantity;
	}

	@Override
	public void setReorderQuantity(final int reorderQuantity) {
		// FIXME: awaiting clarification from Pete
		// if (reOrderEnabled && reorderQuantity < this.reorderMinimum) {
		// throw new EpDomainException("Re-order quantity must not be less than re-order minimum quantity.");
		// }
		this.reorderQuantity = reorderQuantity;
	}

	@Override
	public Long getWarehouseUid() {
		return inventoryKey.getWarehouseUid();
	}

	@Override
	public void setWarehouseUid(final Long warehouseUid) {
		inventoryKey.setWarehouseUid(warehouseUid);
	}

	@Override
	public String getSkuCode() {
		return inventoryKey.getSkuCode();
	}

	@Override
	public void setSkuCode(final String skuCode) {
		inventoryKey.setSkuCode(skuCode);
	}

	@Override
	public int getAllocatedQuantity() {
		return allocatedQuantity;
	}

	/**
	 * Return the hash code. If this object is persisted, we can just use the UIDPK. Otherwise a combination of the warehouse UIDPK and the
	 * ProductSku guid can be used since there can only be one Inventory for each combination of these.
	 *
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ObjectUtils.hashCode(inventoryKey);
		return result;
	}

	/**
	 * Return <code>true</code> if the given object is an instance of <code>InventoryImpl</code> and is logically equal.
	 * If both objects are persisted then the UIDPK values are sufficient to test for equality.
	 *
	 * @param obj the object to compare
	 * @return <code>true</code> if the given object is equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof InventoryDtoImpl)) {
			return false;
		}
		final InventoryDtoImpl other = (InventoryDtoImpl) obj;

		return ObjectUtils.equals(inventoryKey, other.inventoryKey);
	}

	/**
	 * @param quantity the allocated qty
	 */
	@Override
	public void setAllocatedQuantity(final int quantity) {
		this.allocatedQuantity = quantity;
	}

	@Override
	public InventoryKey getInventoryKey() {
		return inventoryKey;
	}


	@Override
	@SuppressWarnings("PMD.CloneThrowsCloneNotSupportedException")
	public InventoryDtoImpl clone() {
		InventoryDtoImpl clone;
		try {
			clone = (InventoryDtoImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			//never happens.
			throw new EpSystemException("can't clone InventoryDtoImpl", e);
		}
		clone.inventoryKey = clone.inventoryKey.clone();
		return clone;
	}

}
