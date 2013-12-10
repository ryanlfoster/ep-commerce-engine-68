/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.audit;

import java.util.Date;

import com.elasticpath.persistence.api.Persistable;

/**
 * Defines a change transaction. This is the transaction that
 * surrounds a collection of change operations.
 */
public interface ChangeTransaction extends Persistable {

	/**
	 * Get an identifier for the transaction.
	 *
	 * @return the transactionId
	 */
	String getTransactionId();

	/**
	 * Set the transaction identifier.
	 *
	 * @param transactionId the transactionId to set
	 */
	void setTransactionId(final String transactionId);

	/**
	 * Get date of the change.
	 *
	 * @return the date of the change
	 */
	Date getChangeDate();

	/**
	 * Set the date of the change.
	 *
	 * @param changeDate the date of the change
	 */
	void setChangeDate(Date changeDate);

}
