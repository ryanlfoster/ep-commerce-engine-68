package com.elasticpath.domain.payment;

import java.io.Serializable;

/**
 * Session object to hold information relating to a Payer Authentication.
 */
public interface PayerAuthenticationSession extends Serializable {
	/** No errors. */
	int ALL_GOOD = 0;

	/** Payer Authenticate Invalid. */
	int PAYER_AUTHENTICATE_INVALID = 1;

	/** General payment gateway error. */
	int PAYMENT_GATEWAY_ERROR = 2;

	/** Insufficient fund error. */
	int INSUFFICIENT_FUND_ERROR = 3;

	/** Amount limit exceed error. */
	int AMOUNT_LIMIT_EXCEEDED_ERROR = 4;

	/** User status inactive error. */
	int USER_STATUS_INACTIVE_ERROR = 5;

	/** Card declined error. */
	int CARD_DECLINED_ERROR = 6;

	/** Card expired error. */
	int CARD_EXPIRED_ERROR = 7;

	/** Card error.*/
	int CARD_ERROR = 8;

	/** Invalid Address error. */
	int INVALID_ADDRESS_ERROR = 9;

	/** Invalid CVV2 error. */
	int INVALID_CVV2_ERROR = 10;

	/** Insufficient inventory error .*/
	int INSUFFICIENT_INVENTORY_ERROR = 11;

	/**
	 * @return the status
	 */
	int getStatus();

	/**
	 * @param status the status to set
	 */
	void setStatus(final int status);
}
