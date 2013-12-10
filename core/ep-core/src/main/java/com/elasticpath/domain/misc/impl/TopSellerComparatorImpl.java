package com.elasticpath.domain.misc.impl;

import java.io.Serializable;
import java.util.Comparator;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.catalog.Product;

/**
 * This is a default implementation of <code>TopSellerComparator</code>.
 */
public class TopSellerComparatorImpl implements Comparator<Product>, Serializable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer if the first argument is less than, equal to, or
	 * greater than the second.
	 *
	 * @param product1 the first product to be compared.
	 * @param product2 the second product to be compared.
	 * @return a negative integer, zero, or a positive integer if the first argument is less than, equal to, or greater than the second.
	 * @throws ClassCastException if the arguments' types prevent them from being compared by this Comparator.
	 */
	public int compare(final Product product1, final Product product2) {

		validateObject(product1);
		validateObject(product2);

		final Integer salesCount1 = Integer.valueOf(product1.getSalesCount());
		final Integer salesCount2 = Integer.valueOf(product2.getSalesCount());

		return salesCount1.compareTo(salesCount2);
	}

	private void validateObject(final Product product) {
		if (product == null) {
			throw new EpSystemException("Product to be compared is a null object.");
		}
	}

}
