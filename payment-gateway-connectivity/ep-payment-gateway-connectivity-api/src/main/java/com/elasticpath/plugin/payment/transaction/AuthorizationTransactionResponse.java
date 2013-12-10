package com.elasticpath.plugin.payment.transaction;

import com.elasticpath.plugin.payment.dto.Money;

/**
 * Represents an authorization {@link PaymentTransactionResponse}. 
 */
public interface AuthorizationTransactionResponse extends PaymentTransactionResponse {

	/**
	 * Gets the money associated with the authorization transaction that took place.
	 *
	 * @return {@link Money} associated with transaction
	 */
	Money getMoney();
	
	/**
	 * Sets the money associated with the authorization transaction that took place.
	 *
	 * @param money the {@link Money}
	 */
	void setMoney(Money money);
}
