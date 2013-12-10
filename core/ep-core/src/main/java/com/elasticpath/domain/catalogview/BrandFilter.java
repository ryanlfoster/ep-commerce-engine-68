/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview;

import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.catalog.Brand;

/**
 * Represents a filter on product brand.
 */
public interface BrandFilter extends Filter<BrandFilter> {
	
	/**
	 * The constant for brand property key.
	 */
	String BRAND_PROPERTY_KEY = "brands";
	
	/**
	 * Get the set of brands.
	 * 
	 * @return the set of brands
	 */
	Set<Brand> getBrands();	
	
	/**
	 * Gets the brand that this associated to this filter.
	 *
	 * @return the brand that is associated to this filter
	 */
	Brand getBrand();
	
	/**
	 * Initializes the filter with the brand code.
	 * Left for compatibility. Now the multi brand code counter part should be used instead.
	 *
	 * @param brandCode the brand code
	 * @throws EpCatalogViewRequestBindException if the code is not valid
	 */
	void initializeWithCode(final String brandCode);

	
	/**
	 * Initializes the filter with brand codes.
	 *
	 * @param brandCodes the brand code
	 * @throws EpCatalogViewRequestBindException if the code is not valid
	 */
	void initializeWithCode(final String[] brandCodes);
	
	
	/**
	 * Initialize with the given brands.
	 *  
	 * @param properties the properties
	 */
	void initialize(Map<String, Object> properties);	
}
