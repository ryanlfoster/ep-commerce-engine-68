package com.elasticpath.plugin.payment.dto;

import java.math.BigDecimal;

/**
 * DTO for shipping details, currency, and total from a Shopping Cart. Used in Payment Gateways.
 */
public interface ShoppingCartDto {
	
	/**
	 * Gets the country.
	 *
	 * @return the country
	 */
	String getCountry();
	
	/**
	 * Sets the country.
	 *
	 * @param country the new country
	 */
	void setCountry(String country);
	
	/**
	 * Gets the total amount.
	 *
	 * @return the total amount
	 */
	BigDecimal getTotalAmount();
	
	/**
	 * Sets the total amount.
	 *
	 * @param totalAmount the new total amount
	 */
	void setTotalAmount(BigDecimal totalAmount);
	
	/**
	 * Gets the currency code.
	 *
	 * @return the currency code
	 */
	String getCurrencyCode();
	
	/**
	 * Sets the currency code.
	 *
	 * @param currencyCode the new currency code
	 */
	void setCurrencyCode(String currencyCode);
	
	/**
	 * Gets the shipping address.
	 *
	 * @return the shipping address
	 */
	AddressDto getShippingAddress();
	
	/**
	 * Sets the shipping address.
	 *
	 * @param shippingAddress the new shipping address
	 */
	void setShippingAddress(AddressDto shippingAddress);
	
	/**
	 * Checks if is requires shipping.
	 *
	 * @return true, if is requires shipping
	 */
	boolean isRequiresShipping();
	
	/**
	 * Sets the requires shipping.
	 *
	 * @param requireShipping the new requires shipping
	 */
	void setRequiresShipping(boolean requireShipping);
}
