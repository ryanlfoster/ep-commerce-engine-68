/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.order;

import java.util.Date;

import com.elasticpath.domain.EpDomain;

/**
 * Represents criteria used when searching for a customer's purchase history.
 */
public interface PurchaseHistorySearchCriteria extends EpDomain {

	/**
	 * Get the date from which purchases should be retrieved.
	 * @return the date from which purchases should be retrieved 
	 */
	Date getFromDate();

	/**
	 * Set the date from which purchases should be retrieved.
	 * @param fromDate the date from which purchases should be retrieved
	 */
	void setFromDate(final Date fromDate);

	/**
	 * Get the date to which purchases should be retrieved.
	 * @return the date to which purchases should be retrieved
	 */
	Date getToDate();

	/**
	 * Set the date to which purchases should be retrieved.
	 * @param toDate the date to which purchases should be retrieved
	 */
	void setToDate(final Date toDate);

	/**
	 * Get the id of the customer who made the purchases.
	 * @return the id of the customer who made the purchases
	 */
	String getUserId();
	
	/**
	 * Set the id of the customer who made the purchases.
	 * @param userId the id of the customer who made the purchases
	 */
	void setUserId(String userId);
	
	/**
	 * Get the code for the store in which the purchases were made.
	 * @return the code for the store in which the purchases were made
	 */
	String getStoreCode();
	
	/**
	 * Set the code for the store in which the purchases were made.
	 * @param storeCode the code for the store in which the purchases were made.
	 */
	void setStoreCode(String storeCode);
}
