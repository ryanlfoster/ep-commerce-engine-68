/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import java.util.Date;

import com.elasticpath.persistence.api.Persistable;

/**
 * <code>ProductDeleted</code> represents a deleted product.
 */
public interface ProductDeleted extends Persistable {

	/**
	 * Returns the uid of the deleted product.
	 * 
	 * @return the uid of the deleted product
	 */
	long getProductUid();

	/**
	 * Sets the uid of the deleted product.
	 * 
	 * @param productUid the uid of the deleted product.
	 */
	void setProductUid(final long productUid);

	/**
	 * Returns the date when the product was deleted.
	 * 
	 * @return the date when the product was deleted
	 */
	Date getDeletedDate();

	/**
	 * Sets the date when the product was deleted.
	 * 
	 * @param deletedDate the date when the product was deleted
	 */
	void setDeletedDate(final Date deletedDate);

}