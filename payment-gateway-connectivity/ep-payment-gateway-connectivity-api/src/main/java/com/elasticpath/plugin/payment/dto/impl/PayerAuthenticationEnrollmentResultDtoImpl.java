package com.elasticpath.plugin.payment.dto.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.PayerAuthenticationEnrollmentResultDto;

/**
 *	Payer Authentication Checking Enrollment Response Value.
 */
public class PayerAuthenticationEnrollmentResultDtoImpl implements PayerAuthenticationEnrollmentResultDto {
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

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
