package com.elasticpath.domain.catalog;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Thrown if a bundle constituent has an invalid pricing mechanism type for the root bundle {@linkplain ProductBundle}.
 * Nested bundles MUST be of the same Bundle Pricing mechanism as the bundle in which it is being nested.
 * @author hdavid
 *
 */
public class InvalidBundleConstituentPricingMechanism extends
		EpServiceException {

	private static final long serialVersionUID = -6023418552437346664L;

	/**
	 * default constructor.
	 */
	public InvalidBundleConstituentPricingMechanism() {
		super("Nested bundle must be of the same Bundle Pricing Mechanism as the bundle in which it is being nested");
	}
}
