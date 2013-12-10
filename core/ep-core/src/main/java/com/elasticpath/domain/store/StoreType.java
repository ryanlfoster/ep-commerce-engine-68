package com.elasticpath.domain.store;

/**
 * Represents a type of <code>Store</code>.
 */
public enum StoreType {

	/**
	 * Customers are grouped into related accounts. Catalogs can be defined for each group.
	 */
	B2B,

	/**
	 * Customers are grouped into segments. Each segment may be eligible for special promotions,
	 * pricing, etc.
	 */
	B2C,
}
