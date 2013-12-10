/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence;

import java.util.List;

/**
 * This DAO encapsulates dynamically generated HQL queries used by
 * the <code>SkuConfigurationService</code>.
 */
public interface SkuConfigurationDao {

	/**
	 * Finds a UID corresponding to a SKU with the given set of option value codes.
	 * 
	 * @param productUid the UID of the product to search for matching SKUs
	 * @param optionValueCodes a List of Strings corresponding to the option value codes of the desired SKU
	 * @return the UID of the SKU with all the specified optionValueCodes or 0 if no such SKU is found.
	 */
	long getSkuWithMatchingOptionValues(final long productUid, final List<String> optionValueCodes);
}
