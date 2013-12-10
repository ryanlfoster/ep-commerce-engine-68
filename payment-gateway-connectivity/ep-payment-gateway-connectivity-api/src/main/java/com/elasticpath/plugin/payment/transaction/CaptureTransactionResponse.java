package com.elasticpath.plugin.payment.transaction;

import com.elasticpath.plugin.payment.dto.Money;

/**
 * Represents a capture {@link PaymentTransactionResponse}.
 */
public interface CaptureTransactionResponse extends PaymentTransactionResponse {
	/**
	 * Gets the money associated with the capture transaction that took place.
	 *
	 * @return {@link Money} associated with transaction
	 */
	Money getMoney();
	
	/**
	 * Sets the money associated with the capture transaction that took place.
	 *
	 * @param money the {@link Money}
	 */
	void setMoney(Money money);
}
