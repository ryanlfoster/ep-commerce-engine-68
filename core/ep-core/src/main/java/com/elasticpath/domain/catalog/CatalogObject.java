package com.elasticpath.domain.catalog;

/**
 * Interface for a domain object that is part of a <code>Catalog</code>.
 */
public interface CatalogObject {

	/**
	 * Get the catalog that this object belongs to.
	 * @return the catalog
	 */
	Catalog getCatalog();

	/**
	 * Set the catalog that this object belongs to.
	 * @param catalog the catalog to set
	 */
	void setCatalog(final Catalog catalog);

}