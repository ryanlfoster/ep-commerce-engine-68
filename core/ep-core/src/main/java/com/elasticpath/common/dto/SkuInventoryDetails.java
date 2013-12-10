package com.elasticpath.common.dto;

import java.io.Serializable;
import java.util.Date;

import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.service.catalogview.impl.InventoryMessage;


/**
 * Data Transfer Object (DTO) for communicating the details of inventory for a root shopping item.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName") //Field name seems the best to me
public class SkuInventoryDetails implements Serializable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private int availableQuantityInStock;

	private boolean hasSufficientUnallocatedQty = true;

	private InventoryMessage messageCode;

	private AvailabilityCriteria availabilityCriteria;

	private Date stockDate;

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

	/**
	 * @param hasSufficientUnallocatedQty the hasSufficientUnallocatedQty to set
	 */
	public void setHasSufficientUnallocatedQty(final boolean hasSufficientUnallocatedQty) {
		this.hasSufficientUnallocatedQty = hasSufficientUnallocatedQty;
	}

	/**
	 * @return true if there is enough inventory to fulfil the request.
	 */
	public boolean hasSufficientUnallocatedQty() {
		return hasSufficientUnallocatedQty;
	}

	/**
	 * @param messageCode the messageCode to set
	 */
	public void setMessageCode(final InventoryMessage messageCode) {
		this.messageCode = messageCode;
	}

	/**
	 * @return the messageCode
	 */
	public InventoryMessage getMessageCode() {
		return messageCode;
	}

	/**
	 * @param availabilityCriteria the availabilityCriteria to set
	 */
	public void setAvailabilityCriteria(final AvailabilityCriteria availabilityCriteria) {
		this.availabilityCriteria = availabilityCriteria;
	}

	/**
	 * @return the availabilityCriteria
	 */
	public AvailabilityCriteria getAvailabilityCriteria() {
		return availabilityCriteria;
	}

	/**
	 * @param stockDate the restockDate to set
	 */
	public void setStockDate(final Date stockDate) {
		this.stockDate = stockDate;
	}

	/**
	 * @return the restockDate
	 */
	public Date getStockDate() {
		return stockDate;
	}
}
