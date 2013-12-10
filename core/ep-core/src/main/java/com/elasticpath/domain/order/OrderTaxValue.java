/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

import java.math.BigDecimal;
import java.util.Currency;

import com.elasticpath.domain.misc.Money;
import com.elasticpath.persistence.api.Persistable;

/**
 * Represents order tax information.
 * 
 */
public interface OrderTaxValue extends Persistable {
	/**
	 * Get the taxCategory name.
	 * @return the taxCategory name.
	 * @domainmodel.property
	 */
	String getTaxCategoryName();
	
	/**
	 * Set the taxCategory name.
	 * @param taxCategoryName the taxCategory name.
	 */
	void setTaxCategoryName(final String taxCategoryName);
	
	/**
	 * Get the taxCategory display name in order locale.
	 * @return the taxCategory display name in order locale.
	 * @domainmodel.property
	 */
	String getTaxCategoryDisplayName();
	
	/**
	 * Set the taxCategory display name in order locale.
	 * @param taxCategoryDisplayName - taxCategory display name in order locale.
	 */
	void setTaxCategoryDisplayName(final String taxCategoryDisplayName);
	
	/**
	 * Get the tax value.
	 * @return the tax value.
	 * @domainmodel.property
	 */
	BigDecimal getTaxValue();
	
	/**
	 * Set the tax value.
	 * @param taxValue the tax value.
	 */
	void setTaxValue(final BigDecimal taxValue);
	
	/**
	 * Return the taxValue as Money object, given the order locale.
	 * 
	 * @param orderCurrency - the currency the order is placed in.
	 * @return the taxValue as Money object.
	 */
	Money getTaxValueMoney(final Currency orderCurrency);
}