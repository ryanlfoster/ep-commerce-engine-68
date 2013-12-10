/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.paymentgateways.cybersource;

import java.util.Map;

/**
* Represents an exception that occurred when processing an authorization reversal.
*/
public class AuthorizationReversalException extends RuntimeException {
	private static final long serialVersionUID = 0L;

	/**
	 * Constructor that accepts the CyberSource reply. This reply will be included in the message via a simple toString().
	 * @param reply the reply obtained from CyberSource
	 */
	public AuthorizationReversalException(final Map<String, String> reply) {
		super("Authorization reversal failed: " + reply);
	}
}
