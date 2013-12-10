package com.elasticpath.service.catalog;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Exception that occurs when a requested Gift Certificate does not exist.
 */
public class GiftCertificateNotFoundException extends EpServiceException {

	private static final long serialVersionUID = -8896293562397109279L;

	private final String giftCertificateCode;

	/**
	 * Constructor including the requested gift certificate code.
	 *
	 * @param message the exception message
	 * @param giftCertificateCode the requested Gift Certificate code
	 */
	public GiftCertificateNotFoundException(final String message, final String giftCertificateCode) {
		super(message);
		this.giftCertificateCode = giftCertificateCode;
	}

	/**
	 * Constructor including the requested gift certificate code and the causing throwable.
	 *
	 * @param message the exception message
	 * @param giftCertificateCode the requested Gift Certificate code
	 * @param cause the causing {@link Throwable}
	 */
	public GiftCertificateNotFoundException(final String message, final String giftCertificateCode, final Throwable cause) {
		super(message, cause);
		this.giftCertificateCode = giftCertificateCode;
	}

	public String getGiftCertificateCode() {
		return giftCertificateCode;
	}

}
