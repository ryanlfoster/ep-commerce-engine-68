package com.elasticpath.domain.payment.impl;

import com.elasticpath.domain.payment.PayerAuthenticationSession;

/**
 * Session object to hold information relating to a Payer Authentication.
 */
public class PayerAuthenticationSessionImpl implements PayerAuthenticationSession {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private int status;

	/**
	 * Contract.
	 * 
	 * @param status the status.
	 */
	public PayerAuthenticationSessionImpl(final int status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(final int status) {
		this.status = status;
	}

}
