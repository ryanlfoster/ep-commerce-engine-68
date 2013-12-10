package com.elasticpath.domain.misc;

import java.io.Serializable;
import java.util.Comparator;

import com.elasticpath.domain.catalog.Brand;

/**
 * Comparator for ordering Currency objects by currency code.
 */
public interface BrandComparator extends Comparator<Brand>, Serializable {

}