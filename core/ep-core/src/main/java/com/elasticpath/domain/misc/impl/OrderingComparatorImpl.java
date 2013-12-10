package com.elasticpath.domain.misc.impl;

import com.elasticpath.domain.misc.Orderable;
import com.elasticpath.domain.misc.OrderingComparator;

/**
 * Default implementation of {@link OrderingComparator}.
 */
public class OrderingComparatorImpl implements OrderingComparator {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(final Orderable object1, final Orderable object2) {
		return object1.getOrdering() - object2.getOrdering();
	}

}
