/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.tax;

import java.util.Set;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.persistence.api.Entity;

/**
 * A TaxJurisdiction represents a country and a set of <code>TaxCategory</code>s.
 */
public interface TaxJurisdiction extends Entity {

	/**
	 * the "exclusive" price calculation method - item price does NOT include tax.
	 */
	Boolean PRICE_CALCULATION_EXCLUSIVE = Boolean.FALSE;

	/**
	 * the "inclusive" price calculation method - item price DOES include tax.
	 */
	Boolean PRICE_CALCULATION_INCLUSIVE = Boolean.TRUE;

	/**
	 * Get the region code of the country represented by the <code>TaxJurisdiction</code>.
	 * 
	 * @return the regionCode
	 */
	String getRegionCode();

	/**
	 * Set the region code of the country represented by the <code>TaxJurisdiction</code>.
	 * 
	 * @param regionCode the parameter regionCode
	 */
	void setRegionCode(final String regionCode);

	/**
	 * Return the price calculation method of this <code>TaxJurisdiction</code>.
	 * A value of TRUE means "inclusive taxes", FALSE means "exclusive taxes".
	 * 
	 * @return the price calculation method (inclusive=true, exclusive=false)
	 */
	Boolean getPriceCalculationMethod();

	/**
	 * Set the price calculation method of this <code>TaxJurisdiction</code>.
	 * A value of TRUE means "inclusive taxes", FALSE means "exclusive taxes".
	 * 
	 * @param priceCalculationMethod TRUE=inclusive, FALSE=exclusive
	 */
	void setPriceCalculationMethod(final Boolean priceCalculationMethod);

	/**
	 * Get the set of tax <code>TaxCategory</code> of this <code>TaxJurisdiction</code>.
	 * 
	 * @return the set of <code>TaxCategory</code>s.
	 */
	Set<TaxCategory> getTaxCategorySet();

	/**
	 * Set the set of <code>TaxCategory</code> of this <code>TaxJurisdiction</code>.
	 * 
	 * @param taxCategorySet the set of <code>TaxCategory</code>s.
	 */
	void setTaxCategorySet(final Set<TaxCategory> taxCategorySet);

	/**
	 * Add the <code>TaxCategory</code> to the map.
	 * 
	 * @param taxCategory to be added to the map.
	 * @throws EpDomainException if Tax Category of the same name already exists in the categories map.
	 */
	void addTaxCategory(TaxCategory taxCategory) throws EpDomainException;

	/**
	 * Find <code>TaxCategory</code> with the specified name in the categories map.
	 * 
	 * @param taxCategoryName <code>TaxCategory</code> name
	 * @return <code>TaxCategory</code> found or null if not found.
	 */
	TaxCategory getTaxCategory(String taxCategoryName);

	/**
	 * Remove <code>TaxCategory</code> with the specified name from the categories map.
	 * 
	 * @param taxCategoryName <code>TaxCategory</code> name
	 * @return <code>TaxCategory</code> removed or null if not found.
	 */
	TaxCategory removeTaxCategory(String taxCategoryName);

	/**
	 * Remove <code>TaxCategory</code> from the categories map.
	 * 
	 * @param taxCategory <code>TaxCategory</code> to be removed
	 * @return <code>TaxCategory</code> removed or null if not found.
	 */
	TaxCategory removeTaxCategory(TaxCategory taxCategory);

}