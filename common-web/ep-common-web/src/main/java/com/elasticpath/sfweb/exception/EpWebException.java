package com.elasticpath.sfweb.exception;

import com.elasticpath.base.exception.EpSystemException;

/**
 * Runtime exception for web-specific issues. As of 6.2.2 EpSfWebException extends this exception. You likely should not use this class unless the
 * caller cares about the difference between an EpWebException vs an EpSystemException (which as they're both Runtime, they likely don't).
 * 
 * @since 6.2.3
 */
public class EpWebException extends EpSystemException {

	private static final long serialVersionUID = 1L;

	/**
	 * Are you sure you want to call this method instead of letting the exception you're catching just bubble up? If it's a checked exception you
	 * could always just use a {@code EpSystemException} unless you think the caller will handle {@code EpSystemException} differently than a
	 * {@code EpWebException}.
	 * 
	 * @param message A detailed message explaining why you're throwing this exception, including variable values, etc.
	 * @param cause the original exception
	 */
	public EpWebException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Are you sure you want to call this method instead just use a {@code EpSystemException} unless you think the caller will handle
	 * {@code EpSystemException} differently than a {@code EpWebException}.
	 * 
	 * @param message A detailed message explaining why you're throwing this exception, including variable values, etc.
	 */
	public EpWebException(final String message) {
		super(message);
	}
}
