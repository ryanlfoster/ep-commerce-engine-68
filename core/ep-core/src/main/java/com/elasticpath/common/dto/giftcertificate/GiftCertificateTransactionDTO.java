package com.elasticpath.common.dto.giftcertificate;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * Data Transfer Object for {@link com.elasticpath.common.dto.giftcertificate.GiftCertificateTransactionDTO}.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class GiftCertificateTransactionDTO implements Dto {

	private static final long serialVersionUID = -6919697165346135928L;

	@XmlElement(name = "creation_date", required = true)
	private Date creationDate;
	
	@XmlElement(name = "amount")
	private BigDecimal amount;

	@XmlElement(name = "authorization_code")	
	private String authorizationCode;

	@XmlElement(name = "transaction_type")	
	private String transactionType;
	
	@XmlElement(name = "gift_certificate_guid")	
	private String giftCertificateGuid;

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the authorizationCode
	 */
	public String getAuthorizationCode() {
		return authorizationCode;
	}

	/**
	 * @param authorizationCode the authorizationCode to set
	 */
	public void setAuthorizationCode(final String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	/**
	 * @return the transactionType
	 */
	public String getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(final String transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * @return the giftCertificateGuid
	 */
	public String getGiftCertificateGuid() {
		return giftCertificateGuid;
	}

	/**
	 * @param giftCertificateGuid the giftCertificateGuid to set
	 */
	public void setGiftCertificateGuid(final String giftCertificateGuid) {
		this.giftCertificateGuid = giftCertificateGuid;
	}
	
}
