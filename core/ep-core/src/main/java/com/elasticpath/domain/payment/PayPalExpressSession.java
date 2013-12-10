/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment;

/**
 * Session object to hold information relating to a PayPal Express checkout session.
 */
public interface PayPalExpressSession {
	/** No errors. */
	int ALL_GOOD = 0;
	
	/** Insufficient inventory error. */
	int INSUFFICIENT_INVENTORY_ERROR = 1;

	/** General payment gateway error. */
	int PAYMENT_GATEWAY_ERROR = 2;

	/** Insufficient fund error. */
	int INSUFFICIENT_FUND_ERROR = 3;
	
	/** Amount limit exceed error. */
	int AMOUNT_LIMIT_EXCEEDED_ERROR = 4;
	
	/** User status inactive error. */
	int USER_STATUS_INACTIVE_ERROR = 5;
	/**
	 * @return the emailId
	 */
	String getEmailId();

	/**
	 * @param emailId the emailId to set
	 */
	void setEmailId(final String emailId);

	/**
	 * @return the status
	 */
	int getStatus();

	/**
	 * @param status the status to set
	 */
	void setStatus(final int status);

	/**
	 * @return the token
	 */
	String getToken();

	/**
	 * @param token the token to set
	 */
	void setToken(final String token);
	
	/**
	 * Clear PayPal EC session information.
	 * Does not alter the currrent "status".
	 */
	void clearSessionInformation();
}
