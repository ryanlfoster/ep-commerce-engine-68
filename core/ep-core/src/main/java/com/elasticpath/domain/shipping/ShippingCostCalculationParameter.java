/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping;

import java.util.Currency;

import com.elasticpath.persistence.api.Persistable;

/**
 * Represents a parameter of a shipping cost calculation method, such as the dollar value of the fix base shipping cost.
 */
public interface ShippingCostCalculationParameter extends Persistable {

	/**
	 * Get the parameter key.
	 * 
	 * @return the parameter key
	 */
	String getKey();

	/**
	 * Set the parameter key.
	 * 
	 * @param key the parameter key
	 */
	void setKey(final String key);

	/**
	 * Get the parameter value.
	 * 
	 * @return the parameter value
	 */
	String getValue();

	/**
	 * Set the parameter value.
	 * 
	 * @param value the parameter value
	 */
	void setValue(final String value);

	/**
	 * Get the display text for this parameter.
	 * 
	 * @return the the display text, or the parameter value if there is no display text
	 */
	String getDisplayText();

	/**
	 * Set the text to be displayed for this parameter. For example, the display text for a sku code id long. might be the actual text sku code
	 * 
	 * @param displayText the text to display. Set to null to use the parameter value.
	 */
	void setDisplayText(final String displayText);

	/**
	 * Sets the currency for this <code>ShippingCostCalculationParameter</code>.
	 * 
	 * @param currency the currency for this <code>ShippingCostCalculationParameter</code>
	 */
	void setCurrency(Currency currency);

	/**
	 * Gets the currency for this <code>ShippingCostCalculationParameter</code>.
	 * 
	 * @return the currency for this <code>ShippingCostCalculationParameter</code>
	 */
	Currency getCurrency();

	/**
	 * Returns true if this parameter is currency aware, false otherwise.
	 * 
	 * @return true if the key is keyed currency aware parameter.
	 */
	boolean isCurrencyAware();
}
