/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.domain.customer;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.plugin.payment.dto.TokenPaymentMethod;

/**
 * Represents an immutable payment gateway token. Read-only interface.
 */
public interface PaymentToken extends Persistable, TokenPaymentMethod {
	/**
	 * Gets the payment gateway guid.
	 *
	 * @return the gateway guid
	 */
	String getGatewayGuid();

	/**
	 * Gets the token display value.
	 *
	 * @return the display value
	 */
	String getDisplayValue();
}
