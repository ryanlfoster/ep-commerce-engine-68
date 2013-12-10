/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.inventory.domain.impl;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Represents inventory information about a <code>ProductSku</code>.
 */
@Entity
@Table(name = InventoryImpl.TABLE_NAME)
@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX,
			attributes = {
				@FetchAttribute(name = "quantityOnHandInternal"),
				@FetchAttribute(name = "reservedQuantityInternal"),
				@FetchAttribute(name = "allocatedQuantity"),
				@FetchAttribute(name = "warehouseUid")
			})
@DataCache(enabled = false)
public class InventoryImpl extends AbstractEntityImpl implements Inventory {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TINVENTORY";

	/**
	 * This is the physical number of units that the warehouse has in stock.
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

	private long uidPk;

	/**
	 * This is the number of items a customer currently want to reorder, must be more than reorderMinimum.
	 */
	private int reorderQuantity = 1;

	private Long warehouseUid;

	private String skuCode;

	private int allocatedQuantity;

	private String guid;

	/**
	 * Get the quantity on hand.
	 *
	 * @return the quantity on hand
	 */
	@Transient
	public int getQuantityOnHand() {
		return getQuantityOnHandInternal();
	}

	/**
	 * Set the quantity on hand.
	 * Note: May be negative if minor over-selling occurs under load.
	 * @param quantityOnHand the quantity on hand
	 */
	@Transient
	public void setQuantityOnHand(final int quantityOnHand) {
		if (quantityOnHand < 0) {
			throw new EpDomainException(String.format("Cannot set negative quantity [%s]", quantityOnHand));
		} else {
			setQuantityOnHandInternal(quantityOnHand);
		}
	}

	/**
	 * Get the quantity on hand.
	 * Note: The *Internal pattern is used here to isolate application logic in
	 * the public getter/setter from byte-code enhancement done by OpenJPA.
	 * @return the quantity on hand
	 */
	@Basic
	@Column(name = "QUANTITY_ON_HAND")
	protected int getQuantityOnHandInternal() {
		return this.quantityOnHand;
	}

	/**
	 * Set the quantity on hand.
	 * Note: The *Internal pattern is used here to isolate application logic in
	 * the public getter/setter from byte-code enhancement done by OpenJPA.
	 * @param quantityOnHand the quantity on hand
	 */
	protected void setQuantityOnHandInternal(final int quantityOnHand) {
		this.quantityOnHand = quantityOnHand;
	}

	/**
	 * Get the reserved quantity. SKUs should not be sold if the quantity is below the reserved quantity.
	 *
	 * @return the reserved quantity
	 */
	@Transient
	public int getReservedQuantity() {
		return getReservedQuantityInternal();
	}

	/**
	 * Set the reserved quantity.
	 *
	 * @param reservedQuantity the reserved quantity
	 */
	@Transient
	public void setReservedQuantity(final int reservedQuantity) {
		if (reservedQuantity < 0) {
			throw new EpDomainException("Cannot set negative reserved quantity.");
		} else {
			setReservedQuantityInternal(reservedQuantity);
		}
	}

	/**
	 * Get the reserved quantity. SKUs should not be sold if the quantity is below the reserved quantity.
	 *
	 * @return the reserved quantity
	 */
	@Basic
	@Column(name = "RESERVED_QUANTITY")
	protected int getReservedQuantityInternal() {
		return reservedQuantity;
	}

	/**
	 * Set the reserved quantity.
	 *
	 * @param reservedQuantity the reserved quantity
	 */
	protected void setReservedQuantityInternal(final int reservedQuantity) {
		this.reservedQuantity = reservedQuantity;
	}

	/**
	 * Get the reorder minimum quantity. This inventory item should be reported as below minimum quantity if the quantity on hand is below this
	 * value.
	 *
	 * @return the reorder minimum quantity
	 */
	@Transient
	public int getReorderMinimum() {
		return getReorderMinimumInternal();
	}

	/**
	 * Set the reorder minimum quantity.
	 *
	 * @param reorderMinimum the reorder minimum
	 */
	@Transient
	public void setReorderMinimum(final int reorderMinimum) {
		if (reorderMinimum < 0) {
			throw new EpDomainException("Cannot set negative reorder minimum");
		} else {
			setReorderMinimumInternal(reorderMinimum);
		}
	}

	/**
	 * Get the reorder minimum quantity. This inventory item should be reported as below minimum quantity if the quantity on hand is below this
	 * value.
	 *
	 * @return the reorder minimum quantity
	 */
	@Basic
	@Column(name = "REORDER_MINIMUM")
	protected int getReorderMinimumInternal() {
		return this.reorderMinimum;
	}

	/**
	 * Set the reorder minimum quantity.
	 *
	 * @param reorderMinimum the reorder minimum
	 */
	protected void setReorderMinimumInternal(final int reorderMinimum) {
		this.reorderMinimum = reorderMinimum;
	}

	/**
	 * Get the expected date when this inventory item will be re-stocked.
	 *
	 * @return the re-stock date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RESTOCK_DATE", nullable = true)
	public Date getRestockDate() {
		return this.restockDate;
	}

	/**
	 * Set the re-stock date.
	 *
	 * @param restockDate the re-stock date
	 */
	public void setRestockDate(final Date restockDate) {
		this.restockDate = restockDate;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Gets the reorder quantity.
	 *
	 * @return quantity to be re-ordered
	 */
	@Basic
	@Column(name = "REORDER_QUANTITY")
	public int getReorderQuantity() {
		return reorderQuantity;
	}

	/**
	 * Sets the quantity to be re-rodered.
	 *
	 * @param reorderQuantity the quantity to be re-ordered
	 */
	public void setReorderQuantity(final int reorderQuantity) {
		// FIXME: awaiting clarification from Pete
		// if (reOrderEnabled && reorderQuantity < this.reorderMinimum) {
		// throw new EpDomainException("Re-order quantity must not be less than re-order minimum quantity.");
		// }
		this.reorderQuantity = reorderQuantity;
	}

	/**
	 * Gets the warehouseUid associated with inventory.
	 *
	 * @return the warehouseUid
	 */
	@Basic
	@Column(name = "WAREHOUSE_UID")
	public Long getWarehouseUid() {
		return warehouseUid;
	}

	/**
	 * Sets warehouseUid associated with inventory.
	 *
	 * @param warehouseUid the associated warehouseUid
	 */
	public void setWarehouseUid(final Long warehouseUid) {
		this.warehouseUid = warehouseUid;
	}

	/**
	 * Get the product SKU.
	 *
	 * @return product SKU
	 */
	@Basic
	@Column(name = "PRODUCTSKU_SKUCODE", nullable = false)
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * Set the skuCode of the productSku to which this inventory belongs.
	 *
	 * @param skuCode the productSku's skuCode
	 */
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 *
	 * @return the allocated qty
	 */
	@Basic
	@Column(name = "ALLOCATED_QUANTITY")
	public int getAllocatedQuantity() {
		return allocatedQuantity;
	}

	/**
	 *
	 * @param quantity the allocated qty
	 */
	public void setAllocatedQuantity(final int quantity) {
		this.allocatedQuantity = quantity;
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
		final int shift = 32;
		int result = 1;
		if (this.isPersisted()) {
			return prime * result + (int) (uidPk ^ (uidPk >>> shift));
		}
		result = prime * result + ObjectUtils.hashCode(skuCode);
		result = prime * result + ObjectUtils.hashCode(warehouseUid);
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
		if (!(obj instanceof InventoryImpl)) {
			return false;
		}
		final InventoryImpl other = (InventoryImpl) obj;
		if (isPersisted() && other.isPersisted()) {
			return uidPk == other.uidPk;
		}

		return (ObjectUtils.equals(skuCode, other.skuCode)
				&& ObjectUtils.equals(warehouseUid, other.warehouseUid));
	}

	@Transient
	@Override
	public String getGuid() {
		return this.guid;
	}

	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

}
