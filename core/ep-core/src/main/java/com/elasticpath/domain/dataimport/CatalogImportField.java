package com.elasticpath.domain.dataimport;

import com.elasticpath.domain.catalog.Catalog;


/**
 * Represents an import field which is dependent on the catalog.
 */
public interface CatalogImportField extends ImportField {

	/**
	 * Set the <code>Catalog</code> for this object.
	 *
	 * @param catalog the <code>Catalog</code> to set.
	 */
	void setCatalog(Catalog catalog);

}