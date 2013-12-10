/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.misc;

import java.io.Serializable;
import java.util.Comparator;

import com.elasticpath.domain.catalogview.FilterOption;

/**
 * Represents a <code>Comparator</code> on FilterOptions.
 * It sort the filter options by hints.
 *
 */
public interface FilterBucketComparator extends Comparator<FilterOption< ? >>, Serializable {

}
