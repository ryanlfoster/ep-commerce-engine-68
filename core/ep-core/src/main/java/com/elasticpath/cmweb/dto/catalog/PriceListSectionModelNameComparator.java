/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmweb.dto.catalog;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares PriceListb by their names (case-insensitive).
 */
public class PriceListSectionModelNameComparator implements Comparator<PriceListSectionModel>, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Compares priceListSectionModels by the name of the PriceLists contained
	 * within. 
	 * 
	 * {@inheritDoc}
	 */
	public int compare(final PriceListSectionModel plsm1, final PriceListSectionModel plsm2) {
		return plsm1.getPriceListDescriptorDTO().getName().compareToIgnoreCase(plsm2.getPriceListDescriptorDTO().getName());
	}
}