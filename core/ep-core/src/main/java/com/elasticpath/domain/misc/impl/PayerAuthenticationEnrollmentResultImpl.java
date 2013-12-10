/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.misc.impl;

import com.elasticpath.domain.misc.PayerAuthenticationEnrollmentResult;

/**
 *	Payer Authentication Checking Enrollment Response Value.
 */
public class PayerAuthenticationEnrollmentResultImpl implements PayerAuthenticationEnrollmentResult {

	private static final long serialVersionUID = 1L;

	private String paREQ;
	private String acsURL;
	private boolean enrolled = false;
	private String merchantData;
	private String termURL;

	@Override
	public String getPaREQ() {

		return paREQ;
	}

	@Override
	public boolean is3DSecureEnrolled() {
		return enrolled;
	}

	@Override
	public void setEnrolled(final boolean enrolled) {
		this.enrolled = enrolled;

	}

	@Override
	public void setPaREQ(final String pareq) {
		paREQ = pareq;
	}

	@Override
	public String getAcsURL() {
		return acsURL;
	}

	@Override
	public void setAcsURL(final String url) {
		acsURL = url;
	}

	@Override
	public String getMerchantData() {
		return merchantData;
	}

	@Override
	public void setMerchantData(final String merchantData) {
		this.merchantData = merchantData;
	}

	@Override
	public String getTermURL() {
		return termURL;
	}

	@Override
	public void setTermURL(final String termURL) {
		this.termURL = termURL;
	}

}
