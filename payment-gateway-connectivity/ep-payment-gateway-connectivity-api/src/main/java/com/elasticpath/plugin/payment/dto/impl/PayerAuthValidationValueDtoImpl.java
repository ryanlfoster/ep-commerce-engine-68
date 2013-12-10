package com.elasticpath.plugin.payment.dto.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.PayerAuthValidationValueDto;

/**
 *	Payer Authentication Validation Response Value. 
 */
public class PayerAuthValidationValueDtoImpl implements PayerAuthValidationValueDto {

	private String aAV;
	private String cAVV;
	private String commerceIndicator;
	private String xID;
	private String eCI;
	private boolean validated;
	private String paRES;
	private String ucafCollectionIndicator;
	
	@Override
	public String getAAV() {
		return this.aAV;
	}

	@Override
	public String getCAVV() {
		return this.cAVV;
	}

	@Override
	public String getCommerceIndicator() {
		return this.commerceIndicator;
	}

	@Override
	public String getECI() {
		return this.eCI;
	}

	@Override
	public String getPaRES() {
		return this.paRES;
	}

	@Override
	public String getXID() {
		return this.xID;
	}

	@Override
	public boolean isValidated() {
		return this.validated;
	}

	@Override
	public void setAAV(final String aav) {
		this.aAV = aav;
	}

	@Override
	public void setCAVV(final String cavv) {
		this.cAVV = cavv;

	}

	@Override
	public void setCommerceIndicator(final String commerceIndicator) {
		this.commerceIndicator = commerceIndicator;
	}

	@Override
	public void setECI(final String eci) {
		this.eCI = eci;

	}

	@Override
	public void setPaRES(final String pares) {
		this.paRES = pares;

	}

	@Override
	public void setValidated(final boolean validated) {
		this.validated = validated;

	}

	@Override
	public void setXID(final String xid) {
		this.xID = xid;

	}
	
	@Override
	public String getUcafCollectionIndicator() {
		return this.ucafCollectionIndicator;
	}
	
	@Override
	public void setUcafCollectionIndicator(final String ucafCollectionIndicator) {
		this.ucafCollectionIndicator = ucafCollectionIndicator;
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
