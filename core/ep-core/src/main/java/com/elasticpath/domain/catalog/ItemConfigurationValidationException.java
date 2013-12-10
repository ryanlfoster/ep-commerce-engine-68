package com.elasticpath.domain.catalog;

import com.elasticpath.base.exception.EpServiceException;

/**
 * The exception for validation errors on an item configuration.
 */
public class ItemConfigurationValidationException extends EpServiceException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param message the message
	 */
	public ItemConfigurationValidationException(final String message) {
		super(message);
	}

}
