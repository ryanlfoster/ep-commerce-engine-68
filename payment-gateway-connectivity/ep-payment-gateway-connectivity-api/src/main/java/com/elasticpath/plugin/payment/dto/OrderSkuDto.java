package com.elasticpath.plugin.payment.dto;

import java.math.BigDecimal;

/**
 * DTO for line items on an Order. Used in Payment Gateways.
 */
public interface OrderSkuDto {

	/**
	 * Get the productSku SKU.
	 * 
	 * @return the productSku system name
	 */
	String getSkuCode();

	/**
	 * Set the productSku SKU.
	 * 
	 * @param code the productSku system name
	 */
	void setSkuCode(String code);

	/**
	 * Get the unit price for this sku.
	 * 
	 * @return the price
	 */
	BigDecimal getUnitPrice();

	/**
	 * Set the unit price for this sku.
	 * 
	 * @param price the price
	 */
	void setUnitPrice(final BigDecimal price);
	
	/**
	 * Get the product's display name.
	 * 
	 * @return the product's display name.
	 */
	String getDisplayName();

	/**
	 * Set the product's display name.
	 * 
	 * @param displayName the product's display name
	 */
	void setDisplayName(final String displayName);

	/**
	 * Get the quantity of this item.
	 * 
	 * @return the quantity
	 */
	int getQuantity();

	/**
	 * Sets the quantity. 
	 * This method is to be removed when the PriceTier and Quantity can be set together.
	 * @param quantity the quantity to set
	 */
	void setQuantity(int quantity);
	
	/**
	 * Gets the tax amount.
	 *
	 * @return the tax amount
	 */
	BigDecimal getTaxAmount(); 
	
	/**
	 * Sets the tax amount.
	 *
	 * @param amount the new tax amount
	 */
	void setTaxAmount(BigDecimal amount);
}
