/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.payment.GiftCertificateTransaction;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * The default implementation of <code>GiftCertificateTransaction</code>.
 */
@Entity
@Table(name = GiftCertificateTransactionImpl.TABLE_NAME)
@DataCache(enabled = false)
public class GiftCertificateTransactionImpl extends AbstractPersistableImpl implements GiftCertificateTransaction {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final int SHORT_TEXT_LENGTH = 50;

	private static final int TRANS_TYPE_LENGTH = 20;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TGIFTCERTIFICATETRANSACTION";

	private long uidPk;

	private Date createdDate;

	private GiftCertificate giftCertificate;

	private BigDecimal amount;

	private String authorizationCode;

	private String transactionType;

	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Get the authorization code.
	 * 
	 * @return the authorization code
	 */
	@Basic
	@Column(name = "AUTHORIZATION_CODE", length = SHORT_TEXT_LENGTH)
	public String getAuthorizationCode() {
		return this.authorizationCode;
	}

	/**
	 * Set the authorization code.
	 * 
	 * @param authorizationCode the authorization code
	 */
	public void setAuthorizationCode(final String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 * 
	 * @return the payment transaction type
	 */
	@Basic
	@Column(name = "TRANSACTION_TYPE", length = TRANS_TYPE_LENGTH)
	public String getTransactionType() {
		return this.transactionType;
	}

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 * 
	 * @param transactionType the payment transaction type
	 */
	public void setTransactionType(final String transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * Get the amount of this payment.
	 * 
	 * @return the amount
	 */
	@Basic
	@Column(name = "AMOUNT", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getAmount() {
		return this.amount;
	}

	/**
	 * Set the amount of this payment.
	 * 
	 * @param amount the amount
	 */
	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * Get the gift certificate for the payment.
	 * 
	 * @return the giftCertificate
	 */
	@ManyToOne(targetEntity = GiftCertificateImpl.class)
	@JoinColumn(name = "GIFTCERTIFICATE_UID")
	public GiftCertificate getGiftCertificate() {
		return giftCertificate;
	}

	/**
	 * Set the gift certificate for the payment.
	 * 
	 * @param giftCertificate the giftCertificate to set
	 */
	public void setGiftCertificate(final GiftCertificate giftCertificate) {
		this.giftCertificate = giftCertificate;
	}

	/**
	 * Get the date that this order was created on.
	 * 
	 * @return the created date
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Set the date that the order is created.
	 * 
	 * @param createdDate the start date
	 */
	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}
}
