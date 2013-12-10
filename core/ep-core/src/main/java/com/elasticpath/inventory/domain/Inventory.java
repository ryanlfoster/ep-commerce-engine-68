/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.inventory.domain;

import java.util.Date;

import com.elasticpath.persistence.api.Entity;

/**
 * Represents inventory information about a <code>ProductSku</code>. It is separated from <code>ProductSku</code> to allow only caching
 * <code>ProductSku</code> without <code>Inventory</code>. Currently, it is a one-to-one relationship between <code>ProductSku</code> and
 * <code>Inventory</code>. In the future, it may be changed to one-to-many to allow multi-stores.
 */
public interface Inventory extends Entity {

	/**
	 * Get the quantity on hand.
	 * 
	 * @return the quantity on hand
	 */
	int getQuantityOnHand();

	/**
	 * Set the quantity on hand. Can not be less than the reserved quantity.
	 * 
	 * @param quantityOnHand the quantity on hand
	 */
	void setQuantityOnHand(final int quantityOnHand);

	/**
	 * Get the reserved quantity. Skus should not be sold if the quantity is below the reserved quantity.
	 * 
	 * @return the reserved quantity
	 */
	int getReservedQuantity();

	/**
	 * Set the reserved quantity. Can not be more than the quantity on hand.
	 * 
	 * @param reservedQuantity the reserved quantity
	 */
	void setReservedQuantity(final int reservedQuantity);

	/**
	 * Get the reorder minimum quantity. This inventory item should be reported as below minimum quantity if the quantity on hand is below this
	 * value.
	 * 
	 * @return the reorder minimum quantity
	 */
	int getReorderMinimum();

	/**
	 * Set the reorder minimum quantity.
	 * 
	 * @param reorderMinimum the reorder minimum
	 */
	void setReorderMinimum(final int reorderMinimum);

	/**
	 * Set the restock date.
	 * 
	 * @param restockDate the restock date
	 */
	void setRestockDate(final Date restockDate);

	/**
	 * Get the expected date when this inventory item will be restocked.
	 * 
	 * @return the restock date
	 */
	Date getRestockDate();

	/**
	 * Gets the reorder quantity.
	 * @return quantity to be re-ordered
	 */
	int getReorderQuantity();
	
	/**
	 * Sets the quantity to be re-rodered.
	 * @param reorderQuantity the quantity to be re-ordered
	 */
	void setReorderQuantity(final int reorderQuantity);
	
	/**
	 * Gets the warehouseUid associated with inventory.
	 * 
	 * @return the warehouseUid
	 */
	Long getWarehouseUid();
	
	/**
	 * Sets warehouseUid associated with inventory. 
	 * 
	 * @param warehouseUid the associated warehouseUid
	 */
	void setWarehouseUid(final Long warehouseUid);
	
	/**
	 * Get the skuCode of the productSku to which this inventory belongs.
	 * 
	 * @return productSku's skuCode
	 */
	String getSkuCode();

	/**
	 * Set the skuCode of the productSku to which this inventory belongs.
	 * 
	 * @param skuCode the productSku's skuCode
	 */
	void setSkuCode(final String skuCode);
	/**
	 * Returns the number of allocated items.
	 * 
	 * @return allocated quantity integer
	 */
	int getAllocatedQuantity();

	/**
	 * Sets the number of allocated items.
	 * 
	 * @param allocated the allocated quantity integer
	 */
	void setAllocatedQuantity(int allocated);

}
