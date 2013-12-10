/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.tax;

import java.math.BigDecimal;
import java.util.Map;

import com.elasticpath.persistence.api.Persistable;

/**
 * A <code>TaxRegion</code> represents mapping among <code>TaxCode</code>s and its values.
 */
public interface TaxRegion extends Persistable {

	/**
	 * Get the region code of the geographic area represented by the <code>TaxJurisdiction</code>.
	 * 
	 * @return the regionName
	 */
	String getRegionName();

	/**
	 * Set the region code of the geographic area represented by the <code>TaxJurisdiction</code>.
	 * 
	 * @param regionName the parameter regionCode
	 */
	void setRegionName(final String regionName);

	/**
	 * Get tax values map where String key is <code>TaxCode</code> code and TaxValue value is the value.
	 * 
	 * @return tax values map.
	 */
	Map<String, TaxValue> getTaxValuesMap();

	/**
	 * Set tax values map where String key is <code>TaxCode</code> code and TaxValue value is the value.
	 * 
	 * @param taxValuesMap the tax values map.
	 */
	void setTaxValuesMap(Map<String, TaxValue> taxValuesMap);

	/**
	 * Return the tax value.
	 * 
	 * @param taxCode the tax code.
	 * @return the tax value.
	 * @deprecated use #getTaxRate instead
	 */
	@Deprecated
	BigDecimal getValue(final String taxCode);
	
	/**
	 * @param taxCode the code representing the tax for which the rate is required
	 * @return the tax rate expressed as a percentage (e.g. a 7.5% tax is represented as 7.5), 
	 * or null if a the given tax code does not exist in this tax region
	 */
	BigDecimal getTaxRate(final String taxCode);

	/**
	 * Adds a tax value.
	 * @param targetValue tax value o add
	 */
	void addTaxValue(TaxValue targetValue);
}
