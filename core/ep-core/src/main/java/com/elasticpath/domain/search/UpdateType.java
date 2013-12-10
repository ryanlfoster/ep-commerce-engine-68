package com.elasticpath.domain.search;

/**
 * Represents the different types of updates.
 */
public enum UpdateType {
	
	/** Represents a complete index rebuild. */
	REBUILD,
	
	/** Represents an update of items affected by a specific entity. */
	UPDATE,
	
	/** Represents a request to delete the items affected by a specific entity. */
	DELETE,
	
	/** Represents a request to delete the entire index. */
	DELETE_ALL;
}
