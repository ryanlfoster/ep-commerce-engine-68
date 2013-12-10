/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.service.rules;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Exception that occurs when a rule is expected but it was not found.
 */
public class RuleNotFoundException extends EpServiceException {

	private static final long serialVersionUID = 6301359329518962201L;

	private final String ruleCode;
	
	/**
	 * Construct a new instance of this exception.
	 * 
	 * @param message a message describing the exception
	 * @param ruleCode the ruleCode that corresponds to the rule that was not found
	 */
	public RuleNotFoundException(final String message, final String ruleCode) {
		super(message);
		this.ruleCode = ruleCode;
	}
	
	/**
	 * Return the code of the {@code Rule} that was not found.
	 * 
	 * @return the ruleCode
	 */
	public String getRuleCode() {
		return ruleCode;
	}

}
