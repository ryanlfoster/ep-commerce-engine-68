package com.elasticpath.domain.misc;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Currency;

import com.elasticpath.domain.catalog.Product;

/**
 * Represents a <code>Comparator</code> on product lowest price.
 * <p>
 * Notice: it must be initialized with currency before use.
 */
public interface ProductLowestPriceComparator extends Comparator<Product>, Serializable {
	/**
	 * Intialize the comparaor with the given currency.
	 * 
	 * @param currency the currency
	 */
	void initialize(Currency currency);
}
