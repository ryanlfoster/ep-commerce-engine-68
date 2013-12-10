/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import java.util.Date;

import com.elasticpath.persistence.api.Persistable;

/**
 * <code>CategoryDeleted</code> represents a deleted category.
 */
public interface CategoryDeleted extends Persistable {

	/**
	 * Returns the uid of the deleted category.
	 * 
	 * @return the uid of the deleted category
	 */
	long getCategoryUid();

	/**
	 * Sets the uid of the deleted category.
	 * 
	 * @param categoryUid the uid of the deleted category.
	 */
	void setCategoryUid(final long categoryUid);

	/**
	 * Returns the date when the category was deleted.
	 * 
	 * @return the date when the category was deleted
	 */
	Date getDeletedDate();

	/**
	 * Sets the date when the category was deleted.
	 * 
	 * @param deletedDate the date when the category was deleted
	 */
	void setDeletedDate(final Date deletedDate);

}