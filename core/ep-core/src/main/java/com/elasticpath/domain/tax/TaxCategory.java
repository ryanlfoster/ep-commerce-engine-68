/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.tax;

import java.util.Locale;
import java.util.Set;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.persistence.api.Entity;

/**
 * A TaxCategory represents a category of tax, i.e. GST (Canada).
 */
public interface TaxCategory extends Entity {

	/**
	 * The name of localized property -- displayName.
	 */
	String LOCALIZED_PROPERTY_DISPLAY_NAME = "taxCategoryDisplayName";

	/**
	 * Get the tax category name.
	 * 
	 * @return the parameter name
	 */
	String getName();

	/**
	 * Set the tax category name.
	 * 
	 * @param name the parameter name
	 */
	void setName(final String name);

	/**
	 * Returns the display name of the <code>TaxCategory</code> with the given locale.
	 * 
	 * @param locale the locale
	 * @return the display name of the taxCategory displayName
	 */
	String getDisplayName(final Locale locale);

	/**
	 * Returns the <code>LocalizedProperties</code>, i.e. <code>TaxCategory</code> name.
	 * 
	 * @return the <code>LocalizedProperties</code>
	 */
	LocalizedProperties getLocalizedProperties();

	/**
	 * Set the <code>LocalizedProperties</code>, i.e. <code>TaxCategory</code> name.
	 * 
	 * @param localizedProperties - the <code>LocalizedProperties</code>
	 */
	void setLocalizedProperties(final LocalizedProperties localizedProperties);

	/**
	 * Retrieve the field match type for this <code>TaxJurisdiction</code>.
	 * 
	 * @return the field match type for this <code>TaxJurisdiction</code>.
	 */
	TaxCategoryTypeEnum getFieldMatchType();

	/**
	 * Set the field match type for this <code>TaxJurisdiction</code>.
	 * 
	 * @param fieldMatchType the field match type.
	 */
	void setFieldMatchType(final TaxCategoryTypeEnum fieldMatchType);

	/**
	 * Get the set of <code>TaxRegion</code> of this <code>TaxCategory</code>.
	 * 
	 * @return the set of <code>TaxRegion</code>s.
	 */
	Set<TaxRegion> getTaxRegionSet();

	/**
	 * Set the set of <code>TaxRegion</code> of this <code>TaxCategory</code>.
	 * 
	 * @param taxRegionSet the set of <code>TaxRegion</code>s.
	 */
	void setTaxRegionSet(final Set<TaxRegion> taxRegionSet);

	/**
	 * Add the <code>TaxRegion</code> to the list.
	 * 
	 * @param taxRegion to be added to the list.
	 * @throws EpDomainException if Tax Region of the same name already exists in the regions map.
	 */
	void addTaxRegion(TaxRegion taxRegion) throws EpDomainException;

	/**
	 * Find <code>TaxRegion</code> with the specified name in the Tax Region map.
	 * 
	 * @param taxRegionName <code>TaxRegion</code> name
	 * @return <code>TaxRegion</code> found or null if not found.
	 */
	TaxRegion getTaxRegion(String taxRegionName);

	/**
	 * Remove <code>TaxRegion</code> with the specified name from the Tax Region map.
	 * 
	 * @param taxRegionName <code>TaxRegion</code> name
	 * @return <code>TaxRegion</code> removed or null if not found.
	 */
	TaxRegion removeTaxRegion(String taxRegionName);

	/**
	 * Remove <code>TaxRegion</code> from the Tax Region map.
	 * 
	 * @param taxRegion <code>TaxRegion</code> to be removed
	 * @return <code>TaxRegion</code> removed or null if not found.
	 */
	TaxRegion removeTaxRegion(TaxRegion taxRegion);
}
