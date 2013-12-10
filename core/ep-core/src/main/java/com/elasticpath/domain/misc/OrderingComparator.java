package com.elasticpath.domain.misc;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for ordering {@link Orderable} objects.
 */
public interface OrderingComparator extends Comparator<Orderable>, Serializable {

}
