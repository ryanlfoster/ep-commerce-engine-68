/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.audit;

import com.elasticpath.persistence.api.Persistable;


/**
 * The meta data of the change transaction.
 */
public interface ChangeTransactionMetadata extends Persistable {
	/**
	 * Gets change transaction.
	 *
	 * @return the instance of business object group member
	 */
	ChangeTransaction getChangeTransaction();
	
	
	/**
	 * Sets change transaction.
	 * @param changeTransaction the instance of change transaction
	 */
	void setChangeTransaction(ChangeTransaction changeTransaction);
	
	
	/**
	 * Gets meta data key.
	 * @return the key of meta data
	 */
	String getMetadataKey();
	
	/**
	 * Get meta data value.
	 * @return the value of meta data
	 */
	String getMetadataValue();
	
	/**
	 * Sets meta data key.
	 *
	 * @param metadataKey the key of meta data
	 */
	void setMetadataKey(String metadataKey);
	
	/**
	 * Sets meta data value.
	 *
	 * @param metadataValue the value of meta data
	 */
	void setMetadataValue(String metadataValue);
}
