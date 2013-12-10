/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util;

import com.elasticpath.base.exception.EpSystemException;

/**
 * FileListingUnavailableException for when there is an error obtaining the
 * file listing.
 */
public class FileListingUnavailableException extends EpSystemException {

	private static final long serialVersionUID = 5226818551097797301L;

	/**
	 * FileListingUnavaiableException constructor.
	 * @param message exception message
	 * @param cause cause of the exception
	 */
	public FileListingUnavailableException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * FileListingUnavaiableException constructor.
	 * @param message exception message
	 */
	public FileListingUnavailableException(final String message) {
		super(message);
	}
	
}
