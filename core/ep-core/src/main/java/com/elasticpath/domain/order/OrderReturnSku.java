/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

import java.math.BigDecimal;
import java.util.Currency;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.persistence.api.Entity;

/**
 * Represents a sku included into the <code>OrderSku</code>.
 */
public interface OrderReturnSku extends Entity {
	/**
	 * Get the orderSku for this return.
	 * 
	 * @return the orderSku for this return.
	 */
	OrderSku getOrderSku();

	/**
	 * Set the orderSku for this return.
	 * 
	 * @param orderSku the orderSku for this return.
	 */
	void setOrderSku(final OrderSku orderSku);

	/**
	 * Set the quantity of this sku returned.
	 * 
	 * @param quantity the quantity
	 */
	void setQuantity(final int quantity);

	/**
	 * Get the return amount for this item.
	 * 
	 * @return the return amount
	 */
	BigDecimal getReturnAmount();

	/**
	 * Get the return amount as a <code>Money</code> object.
	 * 
	 * @param currency The currency of the order.
	 * @return a <code>Money</code> object representing the return amount
	 */
	Money getReturnAmountMoney(final Currency currency);

	/**
	 * Set the return amount for this item.
	 * 
	 * @param returnAmount the return amount
	 */
	void setReturnAmount(final BigDecimal returnAmount);

	/**
	 * Get the return reason.
	 * 
	 * @return the return reason.
	 */
	String getReturnReason();

	/**
	 * Set the return reason.
	 * 
	 * @param returnReason the return reason.
	 */
	void setReturnReason(final String returnReason);

	/**
	 * Get the receive quantity of this sku returned.
	 * 
	 * @return the receive quantity
	 */
	int getReceivedQuantity();

	/**
	 * Set the receive quantity of this sku returned.
	 * 
	 * @param receiveQuantity the receive quantity
	 */
	void setReceivedQuantity(final int receiveQuantity);

	/**
	 * Get the received state.
	 * 
	 * @return the received state
	 */
	String getReceivedState();

	/**
	 * Set the received state.
	 * 
	 * @param receivedState the received state.
	 */
	void setReceivedState(final String receivedState);
	
	/**
	 * @return the ProductSku
	 */
	ProductSku getProductSku();	

	/**
	 * @return the quantity
	 */
	int getQuantity();
	
	/**
	 * 
	 * @return the tax
	 */
	BigDecimal getTax();
	
	/**
	 * @param tax the amount
	 */
	void setTax(BigDecimal tax);
	
	/**
	 * @return get calculated total amount from quantity multiplied by price
	 */
	Money getAmountMoney();

	/**
	 * @return true if the quantity received is the same as the quantity expected.
	 */
	boolean isFullyReceived();
}