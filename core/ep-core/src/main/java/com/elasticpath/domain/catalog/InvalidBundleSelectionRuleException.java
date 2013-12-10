package com.elasticpath.domain.catalog;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Thrown if a {@linkplain ProductBundle} has a selection rule that cannot be satisfied.
 */
public class InvalidBundleSelectionRuleException extends EpServiceException {

	private static final long serialVersionUID = 69369597058858386L;

	/**
	 * @param selectionRule that's invalid for the {@linkplain ProductBundle}
	 */
	public InvalidBundleSelectionRuleException(final SelectionRule selectionRule) {
		super("Bundle has less constituents than its selection rule of " + selectionRule.getParameter());
	}

}
