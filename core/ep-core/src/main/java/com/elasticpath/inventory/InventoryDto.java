/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.inventory;

import java.io.Serializable;
import java.util.Date;


/**
 * Represents inventory information about a <code>ProductSku</code>. It is separated from <code>ProductSku</code> to allow only caching
 * <code>ProductSku</code> without <code>Inventory</code>. Currently, it is a one-to-one relationship between <code>ProductSku</code> and
 * <code>Inventory</code>. In the future, it may be changed to one-to-many to allow multi-stores.
 */
public interface InventoryDto extends Serializable, Cloneable {

	/**
	 * Get the quantity on hand.
	 * 
	 * @return the quantity on hand
	 */
	int getQuantityOnHand();
	
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
	 * Get physical available quantity currently in stock and can be shipped out right away.
	 * Formula: quanityOnHand - quantityReserved - quantityAllocated
	 * 
	 * @return available quantity in stock and can be shipped out right away
	 */
	int getAvailableQuantityInStock();
	
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
	 * Sets the allocated quantity.
	 *
	 * @param quantity the new allocated quantity
	 */
	void setAllocatedQuantity(int quantity);
	/**
	 * 
	 * @return inventory key.
	 */
	InventoryKey getInventoryKey();
	
	/**
	 * Creates a clone of the inventory DTO.
	 *
	 *@See {@link Object#clone()}.
	 * @return the clone
	 */
	@SuppressWarnings({"PMD.CloneThrowsCloneNotSupportedException", "PMD.CloneMethodMustImplementCloneable" })
	InventoryDto clone();

	/**
	 * Set the quantity on hand.
	 * 
	 * @param quantityOnHand The quantity on hand. Can be zero or negative.
	 */
	void setQuantityOnHand(final int quantityOnHand);

}
