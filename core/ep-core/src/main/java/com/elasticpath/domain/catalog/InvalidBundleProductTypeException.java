package com.elasticpath.domain.catalog;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Thrown if a {@linkplain ProductBundle} has a multi sku product type.
 */
public class InvalidBundleProductTypeException extends EpServiceException {

	private static final long serialVersionUID = 1487175431737634536L;

	/**
	 * default constructor.
	 */
	public InvalidBundleProductTypeException() {
		super("Bundle cannot have multi-sku product type");
	}

}
