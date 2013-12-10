/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.misc;

/**
 * Type of fetching to employ.
 */
public enum FetchMode {
	
	/** No eager fetching. */
	NONE,
	
	/** Join relations. */
	JOIN,
	
	/** Fetch in separate statements. */
	PARALLEL;

}
