/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.search;

import java.util.Date;

import com.elasticpath.persistence.api.Persistable;

/**
 * <code>ObjectDeleted</code> represents a deleted object.
 */
public interface ObjectDeleted extends Persistable {

	/**
	 * String for sku delete type.
	 */
	String OBJECT_DELETED_TYPE_SKU = "sku";
	
	/**
	 * Returns the uid of the deleted object.
	 * 
	 * @return the uid of the deleted object
	 */
	long getObjectUid();

	/**
	 * Sets the uid of the deleted object.
	 * 
	 * @param objectUid the uid of the deleted object.
	 */
	void setObjectUid(final long objectUid);
	
	/**
	 * get object type.
	 * 
	 * @return the object type
	 */
	String getObjectType();
	
	/**
	 * set object type.
	 * 
	 * @param objectType the object type to set
	 */
	void setObjectType(final String objectType);

	/**
	 * Returns the date when the object was deleted.
	 * 
	 * @return the date when the object was deleted
	 */
	Date getDeletedDate();

	/**
	 * Sets the date when the object was deleted.
	 * 
	 * @param deletedDate the date when the object was deleted
	 */
	void setDeletedDate(final Date deletedDate);

}