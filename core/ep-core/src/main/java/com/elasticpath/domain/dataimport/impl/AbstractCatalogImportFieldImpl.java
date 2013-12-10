/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.dataimport.impl;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.dataimport.CatalogImportField;


/**
 * Represents a template <code>ImportField</code> for a <code>Catalog</code> object.
 */
public abstract class AbstractCatalogImportFieldImpl extends AbstractImportFieldImpl implements CatalogImportField {

	private static final long serialVersionUID = -5329340354209704581L;

	private Catalog catalog;

	/**
	 * The default constructor.
	 * 
	 * @param name the name of the import field
	 * @param type the type of the import field
	 * @param required set it to <code>true</code> if the import field is required
	 * @param primaryRequired set it to <code>true</code> if the import field is primaryRequired
	 */
	public AbstractCatalogImportFieldImpl(final String name, final String type, final boolean required, final boolean primaryRequired) {
		super(name, type, required, primaryRequired);
	}

	/**
	 * This is <code>true</code> for all <code>Catalog</code> objects.
	 * 
	 * @return true since this is a <code>Catalog</code> object
	 */
	@Override
	public boolean isCatalogObject() {
		return true;
	}
	
	/**
	 * Gets the catalog of this import field.
	 *
	 * @return the catalog of this import field
	 */
	protected Catalog getCatalog() {
		if (catalog == null) {
			throw new EpDomainException("Catalog not initialized.");
		}
		return catalog;
	}
	
	/**
	 * Set the <code>Catalog</code> for this object.
	 *
	 * @param catalog the <code>Catalog</code> to set.
	 */
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}
}
