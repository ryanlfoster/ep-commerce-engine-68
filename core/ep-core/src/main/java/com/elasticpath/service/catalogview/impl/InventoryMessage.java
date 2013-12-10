/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.catalogview.impl;

/**
 * Defines message codes for inventory availability.
 * 
 * Note: Changing the name of these enums should lead a change in the velocity templates. 
 */
public enum InventoryMessage {

	/**
	 * A message code indicating in stock.
	 * 
	 */
	IN_STOCK("inventoryMessage.instock"),
	
	/**
	 * A message code indicating out of stock.
	 */
	OUT_OF_STOCK("inventoryMessage.outofstock"),
	
	/**
	 * A message code indicating availability for pre order.
	 */
	AVAILABLE_FOR_PREORDER("inventoryMessage.availableForPreOrder"),
	
	/**
	 * A message code indicating availability for back order.
	 */
	AVAILABLE_FOR_BACKORDER("inventoryMessage.availableForBackOrder"), 
	
	/**
	 * 
	 */
	OUT_OF_STOCK_WITH_RESTOCK_DATE("inventoryMessage.outofstockWithRestockDate");
	
	private String propertyKey = "";

	/**
	 * Constructor.
	 * 
	 * @param propertyKey the property key.
	 */
	InventoryMessage(final String propertyKey) {
		this.propertyKey = propertyKey;
	}

	/**
	 * Get the localization property key.
	 * 
	 * @return the localized property key
	 */
	public String getPropertyKey() {
		return this.propertyKey;
	}

}
