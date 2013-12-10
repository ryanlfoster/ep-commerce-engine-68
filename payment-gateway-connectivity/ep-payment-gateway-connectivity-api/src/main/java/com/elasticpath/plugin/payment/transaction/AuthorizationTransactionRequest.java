package com.elasticpath.plugin.payment.transaction;

import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Represents an authorization payment gateway transaction.
 */
public interface AuthorizationTransactionRequest extends PaymentTransactionRequest {
	/**
	 * Gets the {@link PaymentMethod} associated with this {@link AuthorizationTransactionRequest}.
	 *
	 * @return the {@link PaymentMethod} associated with this {@link AuthorizationTransactionRequest}
	 */
	PaymentMethod getPaymentMethod();
	
	/**
	 * Sets the {@link PaymentMethod} associated with this {@link AuthorizationTransactionRequest}.
	 *
	 * @param paymentMethod the {@link PaymentMethod} associated with this {@link AuthorizationTransactionRequest}
	 */
	void setPaymentMethod(PaymentMethod paymentMethod);
	
	/**
	 * Gets this {@link AuthorizationTransactionRequest}'s money. 
	 * The {@link Money} returned includes the authorization amount and currency.
	 *
	 * @return the {@link Money} associated with this {@link AuthorizationTransactionRequest}
	 */
	Money getMoney();
	
	/**
	 * Sets the {@link AuthorizationTransactionRequest}'s money.
	 *
	 * @param money the {@link Money} associated with this {@link AuthorizationTransactionRequest}
	 */
	void setMoney(Money money);
}
