/**
 * 
 */
package com.elasticpath.domain.pricing.exceptions;

import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Represents an attempt to add a {@code BaseAmount} that already exists.
 */
public class DuplicateBaseAmountException extends EpServiceException {

	private static final long serialVersionUID = -2283603981108647332L;

	private final BaseAmount baseAmount;
	
	/**
	 * Constructor.
	 * @param message the error message.
	 * @param baseAmount the base amount that would be a duplicate.
	 */
	public DuplicateBaseAmountException(final String message, final BaseAmount baseAmount) {
		super(message);
		this.baseAmount = baseAmount;
	}
	
	/**
	 * @return The {@code BaseAmount} that prompted the exception.
	 */
	public BaseAmount getBaseAmount() {
		return this.baseAmount;
	}

}
