/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.util;

import org.apache.commons.lang.StringUtils;

/**
 * A utility class for forming the category GUID out of a category and catalog GUIDs.
 * This is currently used for the change set feature.
 */
public class CategoryGuidUtil {

	private static final String DELIMITER = "|";

	/**
	 * Creates the category GUID.
	 * 
	 * @param categoryGuid the category GUID
	 * @param catalogGuid the catalog GUID
	 * @return the new GUID of the category
	 */
	public String get(final String categoryGuid, final String catalogGuid) {
		if (categoryGuid == null || catalogGuid == null) {
			throw new IllegalArgumentException("Category and catalog GUIDs must be non-null values.");
		}
		return categoryGuid + DELIMITER + catalogGuid;
	}
	
	/**
	 * Get category guid from the combined category catalog guid.
	 * 
	 * @param categoryCatalogGuid the combined category catalog guid
	 * @return the category guid
	 */
	public String parseCategoryGuid(final String categoryCatalogGuid) {
		String[] str = StringUtils.split(categoryCatalogGuid, DELIMITER);
		return str[0];
	}
	
	/**
	 * Get catalog guid from the combined category catalog guid.
	 * 
	 * @param categoryCatalogGuid the combined category catalog guid
	 * @return the catalog guid
	 */
	public String parseCatalogGuid(final String categoryCatalogGuid) {
		String[] str = StringUtils.split(categoryCatalogGuid, DELIMITER);
		return str[1];
	}
}
