/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer;

import java.util.Date;

import com.elasticpath.persistence.api.Persistable;

/**
 * <code>CustomerDeleted</code> represents a deleted customer.
 */
public interface CustomerDeleted extends Persistable {

	/**
	 * Returns the uid of the deleted customer.
	 * 
	 * @return the uid of the deleted customer
	 */
	long getCustomerUid();

	/**
	 * Sets the uid of the deleted customer.
	 * 
	 * @param customerUid the uid of the deleted customer.
	 */
	void setCustomerUid(final long customerUid);

	/**
	 * Returns the date when the customer was deleted.
	 * 
	 * @return the date when the customer was deleted
	 */
	Date getDeletedDate();

	/**
	 * Sets the date when the customer was deleted.
	 * 
	 * @param deletedDate the date when the customer was deleted
	 */
	void setDeletedDate(final Date deletedDate);

}