package com.elasticpath.tools.sync.merge.configuration;

import com.elasticpath.persistence.api.Persistable;

/**
 * For filtering entities from a merge based on a function.
 */
public interface EntityFilter {
	
	/**
	 * Determine whether an entity should be filtered out of the merging process.
	 *
	 * @param value the entity to consider
	 * @return true if the entity represented by param value should be filtered out, false if it should be merged
	 */
	boolean isFiltered(final Persistable value);
}
