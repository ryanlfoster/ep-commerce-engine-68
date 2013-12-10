package com.elasticpath.common.dto;


/**
 * Data Transfer Object (DTO) for communicating the details of inventory for a root shopping item.
 */
public class InventoryDetails {

	private int availableQuantityInStock;

	/**
	 * Get physical available quantity currently in stock and can be shipped out right away. <br/>
	 * Formula: quantityOnHand - quantityReserved - quantityAllocated <br/>
	 * When provided for a bundle, this method returns the availableQuantityInStock that can be shipped for the bundle.
	 * This is the smallest availableQuantityInStock with allowance for bundles with multiple quantities of an item.
	 * 
	 * @return the available quantity in stock.
	 */
	public int getAvailableQuantityInStock() {
		return availableQuantityInStock;
	}

	/**
	 * @param availableQuantityInStock the new available quantity in stock.
	 */
	public void setAvailableQuantityInStock(final int availableQuantityInStock) {
		this.availableQuantityInStock = availableQuantityInStock;
	}
}
