/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.order.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.security.CreditCardEncrypter;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;

/**
 * Represents a credit card number. Credit card masking and encryption logic is encapsulated within this class.
 */
@Embeddable
public class CreditCardNumber extends AbstractEpDomainImpl {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private String encryptedCardNumber;

	private String persistedEncryptedCardNumber;

	private boolean shouldPersistEncrypted;

	/**
	 * Retrieves the value that should be stored as the encrypted credit card number. This method should only ever be called by
	 * OpenJPA.
	 * 
	 * @param persistedEncryptedCardNumber the persisted value of the credit card
	 */
	protected void setPersistedEncryptedCardNumber(final String persistedEncryptedCardNumber) {
		this.persistedEncryptedCardNumber = persistedEncryptedCardNumber;
	}

	/**
	 * Retrieves the value that should be stored as the encrypted credit card number. This method should only ever be called by
	 * OpenJPA.
	 * 
	 * @return the credit card number stored that is persisted
	 */
	@Basic
	@Column(name = "CARD_NUMBER")
	protected String getPersistedEncryptedCardNumber() {
		return persistedEncryptedCardNumber;
	}

	/**
	 * Gets the encrypted credit card number; <code>null</code> if the credit card number is stored masked.
	 * <p/>
	 * There were concerns that making this method public might be a security issue. Here are the reasons why
	 * that is not the case:
	 * <ul>
	 * <li>(A) this class is only used internally by <code>OrderPaymentImpl</code>,
	 * <li>(B) it is never passed to a client, and
	 * <li>(C) <code>OrderPaymentImpl</code> provides no setter method for this.
	 * </ul>
	 * Therefore access to the encrypted credit card number is completely controlled by <code>OrderPaymentImpl</code>.
	 * 
	 * @return the encrypted credit card number; <code>null</code> if the credit card number is stored masked
	 */
	@Transient
	public String getEncryptedCardNumber() {
		return encryptedCardNumber;
	}

	/**
	 * Returns a masked credit card number.
	 * 
	 * @return a masked credit card number
	 */
	public String getMaskedCardNumber() {
		String fullCardNumber = getFullCardNumber();
		if (null != fullCardNumber) {
			return this.getCreditCardEncrypter().mask(fullCardNumber);
		}
		return null;
	}

	/**
	 * Sets whether or not this <code>OrderPayment</code>'s credit card number should be encrypted before it is persisted.
	 * Specify <code>true</code> to encrypt the credit card number before persisting, <code>false</code> otherwise.
	 * <strong>Note that if false is specified, the number will be masked before it is stored unencrypted</strong>.
	 * 
	 * @param shouldEncrypt whether or not the credit card should be encrypted
	 */
	public void setShouldPersistEncrypted(final boolean shouldEncrypt) {
		this.shouldPersistEncrypted = shouldEncrypt;
		updatePersistedCardNumber();
	}

	/**
	 * Returns <code>true</code> if the credit card number is being persisted encrypted, <code>false</code> otherwise. Note
	 * that if <code>false</code> is returned, the number is being persisted masked.
	 * 
	 * @return whether or not the credit card is being persisted encrypted
	 */
	@Transient
	public boolean isShouldPersistEncrypted() {
		return this.shouldPersistEncrypted;
	}

	/**
	 * Sets the full credit card number.
	 * 
	 * @param fullCardNumber the full credit card number.
	 */
	public void setFullCardNumber(final String fullCardNumber) {
		if (StringUtils.isBlank(fullCardNumber)) {
			encryptedCardNumber = null;
		} else {
			encryptedCardNumber = this.getCreditCardEncrypter().encrypt(fullCardNumber);
		}
		updatePersistedCardNumber();
	}

	/**
	 * Update the persisted encrypted card number, if the shouldPersistEncrypted flag is set to true.
	 */
	private void updatePersistedCardNumber() {
		if (shouldPersistEncrypted) {
			setPersistedEncryptedCardNumber(encryptedCardNumber);
		} else {
			setPersistedEncryptedCardNumber(null);
		}
	}

	/**
	 * Post load method. We cannot use the <code>@PostLoad</code> annotation here
	 * because this class is an <code>@Embeddable</code> not an <code>@Entity</code>. 
	 */
	protected void postLoad() {
		encryptedCardNumber = persistedEncryptedCardNumber;
		shouldPersistEncrypted = (null != encryptedCardNumber);
	}

	/**
	 * Returns the full, unencrypted credit card number (e.g. "4012888888881881"). If the credit card number stored is not
	 * encrypted, <code>null</code> is returned.
	 * 
	 * @return the full, unencrypted credit card number; <code>null</code> if the stored credit card is no encrypted
	 */
	@Transient
	public String getFullCardNumber() {
		if (null != encryptedCardNumber) {
			return this.getCreditCardEncrypter().decrypt(encryptedCardNumber);
		}
		return null;
	}

	/**
	 * Returns an instance of {@link CreditCardEncrypter}.
	 * 
	 * @return an instance of {@link CreditCardEncrypter}
	 */
	@Transient
	private CreditCardEncrypter getCreditCardEncrypter() {
		return getBean(ContextIdNames.CREDIT_CARD_ENCRYPTER);
	}
}