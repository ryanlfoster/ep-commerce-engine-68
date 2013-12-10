package com.elasticpath.domain.catalog;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Thrown if a {@linkplain ProductBundle} of assigned payment mechanism has a constituent item with a recurring charge.
 * @author hdavid
 *
 */
public class InvalidAssignedBundleWithRecurringChargeItemsException extends
		EpServiceException {

	private static final long serialVersionUID = -3042443885184622364L;

	/**
	 * default constructor.
	 */
	public InvalidAssignedBundleWithRecurringChargeItemsException() {
		super("Bundle of Assigned pricing mechanism cannot have constituent items with Recurring Charge");
	}
}
