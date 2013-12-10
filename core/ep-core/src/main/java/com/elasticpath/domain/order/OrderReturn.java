/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Set;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.persistence.api.Persistable;

/**
 * <code>OrderReturn</code> represents a customer's order return.
 */
public interface OrderReturn extends Persistable {

	/**
	 * Get the date that this order was created on.
	 * 
	 * @return the created date
	 */
	Date getCreatedDate();

	/**
	 * Set the date that the order is created.
	 * 
	 * @param createdDate the start date
	 */
	void setCreatedDate(Date createdDate);

	/**
	 * Get the date that the order return was last modified on.
	 * 
	 * @return the last modified date
	 */
	Date getLastModifiedDate();

	/**
	 * Set the date that the order return was last modified on.
	 * 
	 * @param lastModifiedDate the date that the order return was last modified
	 */
	void setLastModifiedDate(Date lastModifiedDate);
	
	/**
	 * Get the return RMA code.
	 * 
	 * @return the return RMA code.
	 */
	String getRmaCode();

	/**
	 * Set the return RMA code.
	 * 
	 * @param rmaCode the return RMA code.
	 */
	void setRmaCode(final String rmaCode);

	/**
	 * Get the set of <code>OrderReturnSku</code>s for this <code>OrderReturn</code>.
	 * 
	 * @return the set of orderReturnSkus
	 */
	Set<OrderReturnSku> getOrderReturnSkus();

	/**
	 * Set the set of <code>OrderReturnSku</code>s for this <code>OrderReturn</code>.
	 * 
	 * @param orderReturnSkus the set of orderReturnSkus
	 */
	void setOrderReturnSkus(final Set<OrderReturnSku> orderReturnSkus);

	/**
	 * Add order return sku to the return.
	 * 
	 * @param orderReturnSku order return sku to be added.
	 */
	void addOrderReturnSku(final OrderReturnSku orderReturnSku);

	/**
	 * Get the return comment.
	 * 
	 * @return the return comment.
	 */
	String getReturnComment();

	/**
	 * Set the return comment.
	 * 
	 * @param returnComment the return comment.
	 */
	void setReturnComment(final String returnComment);

	/**
	 * Get the return status.
	 * 
	 * @return the return status.
	 */
	OrderReturnStatus getReturnStatus();

	/**
	 * Set the return status.
	 * 
	 * @param status the return status
	 */
	void setReturnStatus(final OrderReturnStatus status);

	/**
	 * Get the return type.
	 * 
	 * @return the return type.
	 */
	OrderReturnType getReturnType();

	/**
	 * Set the return type.
	 * 
	 * @param returnType the return type
	 */
	void setReturnType(final OrderReturnType returnType);

	/**
	 * Get the physical return.
	 * 
	 * @return true if physical return.
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName")
	boolean getPhysicalReturn();

	/**
	 * Set the physical return.
	 * 
	 * @param physicalReturn the physical return
	 */
	void setPhysicalReturn(boolean physicalReturn);

	/**
	 * Get the CmUser which create order return.
	 * 
	 * @return CmUser which create order return
	 */
	CmUser getCreatedByCmUser();

	/**
	 * Set the CmUser which create order return.
	 * 
	 * @param cmUser the cmUser
	 */
	void setCreatedByCmUser(final CmUser cmUser);

	/**
	 * Get the CmUser which received order return.
	 * 
	 * @return CmUser which received order return
	 */
	CmUser getReceivedByCmUser();

	/**
	 * Set the CmUser which received order return.
	 * 
	 * @param cmUser the cmUser
	 */
	void setReceivedByCmUser(final CmUser cmUser);

	/**
	 * Get the exchange order.
	 * 
	 * @return ExchangeOrder if returnType=OrderReturnType.EXCHANGE or null otherwise
	 */
	Order getExchangeOrder();

	/**
	 * Set the exchange order.
	 * 
	 * @param exchangeOrder the echangeOrder
	 */
	void setExchangeOrder(final Order exchangeOrder);
	
	/**
	 * Get the order the return is linked to.
	 * 
	 * @return order the order.
	 */
	Order getOrder();

	/**
	 * Set the order the return is linked to.
	 * 
	 * @param order the order.
	 */
	void setOrder(final Order order);
	
	/**
	 * Get order payment. Used to determine the refund given for the return, or the payment taken for an exchange.
	 * 
	 * @return <code>OrderPayment</code>
	 */
	OrderPayment getReturnPayment();

	/**
	 * Set order payment.
	 * 
	 * @param orderPayment the orderPayment
	 */
	void setReturnPayment(OrderPayment orderPayment);

	/**
	 * Get the order return's currency.
	 * 
	 * @return the <code>Currency</code>
	 */
	Currency getCurrency();

	/**
	 * Get the return total.
	 * 
	 * @return the return total.
	 */
	BigDecimal getReturnTotal();
	
	/**
	 * Get the return total.
	 * 
	 * @return a <code>Money</code> object representing the return total.
	 */
	Money getReturnTotalMoney();
	
	/**
	 * Return refund total. If this OrderReturn represents Return, this value will equal return total, else if this OrderReturn represents Exchange,
	 * this value will be calculated considering exchange order total. Thus, refund can be a negative value which means that additional charge
	 * required.
	 * 
	 * @return refund total
	 */
	BigDecimal getRefundTotal();
	
	/**
	 * Return refund total. If this OrderReturn represents Return, this value will equal return total, else if this OrderReturn represents Exchange,
	 * this value will be calculated considering exchange order total. Thus, refund can be a negative value which means that additional charge
	 * required.
	 * 
	 * @return a <code>Money</code> object representing the refund total
	 */
	Money getRefundTotalMoney();
	
	/**
	 * Return refunded total. Derives the amount from refund payment if the last was already
	 * processed.
	 * 
	 * @return refund total
	 */
	BigDecimal getRefundedTotal();
	
	/**
	 * Return refunded total. Derives the amount from refund payment if the last was already
	 * processed.
	 * 
	 * @return a <code>Money</code> object representing the refunded total
	 */
	Money getRefundedTotalMoney();
	
	/**
	 * Get the set of return taxes. This method will return incorrect values
	 * if you attempt to create a return for an order where the tax rate has
	 * changed since the order was completed.  
	 * 
	 * @return the set of return taxes.
	 */
	Set<OrderTaxValue> getReturnTaxes();

	/**
	 * Get the before-tax return total.
	 * 
	 * @return the before-tax return total.
	 */
	BigDecimal getBeforeTaxReturnTotal();
	
	/**
	 * Get the before-tax return total.
	 * 
	 * @return a <code>Money</code> object representing the before-tax return total.
	 */
	Money getBeforeTaxReturnTotalMoney();
	
	/**
	 * Get the return shipping cost.
	 * 
	 * @return the return shipping cost.
	 */
	BigDecimal getShippingCost();
	
	/**
	 * Get the return shipping cost.
	 * 
	 * @return a <code>Money</code> object representing the return shipping cost.
	 */
	Money getShippingCostMoney();
	
	/**
	 * Get the return shipping tax.
	 * 
	 * @return the return shipping tax.
	 */
	BigDecimal getShippingTax(); 
	
	/**
	 * Get the return shipping tax.
	 * 
	 * @return a <code>Money</code> object representing the return shipping tax.
	 */
	Money getShippingTaxMoney();

	/**
	 * Get the total tax for this return.
	 * 
	 * @return the total tax
	 */
	BigDecimal getTaxTotal();
	
	/**
	 * Get the total tax for this return.
	 * 
	 * @return a <code>Money</code> object representing the total tax
	 */
	Money getTaxTotalMoney();
	
	/**
	 * Get less Restock Amount parameter of this order return.
	 * @return less Restock Amount parameter.
	 */
	BigDecimal getLessRestockAmount();
	
	/**
	 * Get less Restock Amount parameter of this order return.
	 * @return a <code>Money</code> object representing the less Restock Amount parameter.
	 */
	Money getLessRestockAmountMoney();
	
	/**
	 * Set less Restock Amount parameter for this order return.
	 * @param lessRestockAmount less Restock Amount parameter.
	 */
	void setLessRestockAmount(final BigDecimal lessRestockAmount);
	
	/**
	 * Get shipment discount parameter of this order return.
	 * @return shipment discount parameter.
	 */
	BigDecimal getShipmentDiscount();
	
	/**
	 * Get shipment discount parameter of this order return.
	 * @return a <code>Money</code> object representing the shipment discount parameter.
	 */
	Money getShipmentDiscountMoney();
	
	/**
	 * Set shipment discount parameter for this order return.
	 * @param shipmentDiscount shipment discount parameter.
	 */
	void setShipmentDiscount(final BigDecimal shipmentDiscount);
	
	
	/**
	 * Gets the subtotal for this return.
	 * 
	 * @return the return subtotal
	 */
	BigDecimal getSubtotal();
	
	/**
	 * Get the owed to customer amount. The value is return total minus refund total.
	 * 
	 * @return the owed to customer total.
	 */
	BigDecimal getOwedToCustomer();
	
	/**
	 * Get the owed to customer amount. The value is return total minus refund total.
	 * 
	 * @return a <code>Money</code> object representing the owed to customer total.
	 */
	Money getOwedToCustomerMoney();

	
	/**
	 * Gets the subtotal for this return.
	 * 
	 * @return a <code>Money</code> object representing the return subtotal
	 */
	Money getSubtotalMoney();

	/**
	 * Get exchange shopping cart for this return. Shopping cart is transient object required to calculate exchange order.
	 * 
	 * @return shopping cart
	 */
	ShoppingCart getExchangeShoppingCart();

	/**
	 * Set exchange shopping cart for this return. Shopping cart is transient object required to calculate exchange order.
	 * 
	 * @param exchangeShoppingCart the exchange shopping cart
	 */
	void setExchangeShoppingCart(ShoppingCart exchangeShoppingCart);

	/**
	 * Populate order return with the data derived from order for the shipment that is being returned.
	 * 
	 * @param order order to be returned
	 * @param orderShipment order shipment to be returned
	 * @param returnType type of the return. 
	 */
	void populateOrderReturn(final Order order, final OrderShipment orderShipment, final OrderReturnType returnType);
	
	/**
	 * Recalculate the order return taxes, before-tax total and total.
	 */
	void recalculateOrderReturn();
	
	/**
	 * Update order return status based on returned quantities. Call after receiving
	 * quantity for a return sku. 
	 */
	void updateOrderReturnStatus();
	
	/**
	 * Updates returnable quantities for each item of the order across all non-canceled
	 * returns or exchanges for the order.
	 * Checks if quantity of return not exceed the order's quantity for each order's item.
	 *
	 * @param order the order to be updated	 
	 */
	void updateOrderReturnableQuantity(final Order order);
	
	/**
	 * Removes order return skus that has 0 quantity.
	 */
	void normalizeOrderReturn();
		
	/**
	 * Obtains shipment this return is for.
	 *
	 * @return return's shipment
	 */
	OrderShipment getReturnShipment();
	
	/**
	 * Gets version of this order return.
	 * @return the version
	 */	
	int getVersion();

	/**
	 * Sets version of this order return.
	 * @param version the version to set
	 */
	void setVersion(int version);
	
	/**
	 * Gets the address this order return is applicable for.
	 * 
	 * @return an instance of {@link OrderAddress}
	 */
	OrderAddress getOrderReturnAddress();
	
	/**
	 * Sets the address for which this order return is applicable.
	 * 
	 * @param orderAddress an instance of {@link OrderAddress}
	 */
	void setOrderReturnAddress(OrderAddress orderAddress);

	/**
	 * Checks whether inclusive tax method is employed.
	 * 
	 * @return boolean
	 */
	boolean isInclusiveTax();

	/**
	 * Check whether all return skus in the return are fully received.
	 *
	 * @return true if all return skus fully received
	 */
	boolean isFullyReceived();	

	/**
	 * Checks whether the order return is in a final state, either cancelled or completed. 
	 *
	 * @return true if final
	 */
	boolean isInTerminalState();	
	
	/**
	 * Check if the return is partially received. This is the case when 
	 * we have received some but not all of the expected stock back from
	 * the customer.
	 *
	 * @return true if some stock returned but still AWAITING_STOCK_RETURN
	 */
	boolean isPartiallyReceived();
	
	/**
	 * Sets the shipping cost for the order return to be
	 * refunded to customer.
	 *
	 * @param shippingCost - the shipping cost
	 */
	void setShippingCost(final BigDecimal shippingCost);

}
