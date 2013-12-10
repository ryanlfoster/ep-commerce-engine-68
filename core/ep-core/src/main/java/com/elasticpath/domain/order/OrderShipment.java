/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.persistence.api.Persistable;

/**
 * <code>OrderShipment</code> represents a customer's order shipment.
 */
public interface OrderShipment extends Persistable {

	/**
	 * Get the type of this order shipment, i.e. electronic, physical, etc.
	 *
	 * @return the type of the order shipment subclass.
	 */
	ShipmentType getOrderShipmentType();

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
	 * Get the date that the order was last modified on.
	 *
	 * @return the last modified date
	 */
	Date getLastModifiedDate();

	/**
	 * Set the date that the order was last modified on.
	 *
	 * @param lastModifiedDate the date that the order was last modified
	 */
	void setLastModifiedDate(Date lastModifiedDate);

	/**
	 * Get the date that the order was shipped on.
	 *
	 * @return the shipped date
	 */
	Date getShipmentDate();

	/**
	 * Set the date that the order was shipped on.
	 *
	 * @param shipmentDate the date that the order was shipped on
	 */
	void setShipmentDate(Date shipmentDate);

	/**
	 * Get the status of the shipment. <br>
	 * This status is dependent on the parent Order status.
	 *
	 * @return the shipment status
	 */
	OrderShipmentStatus getShipmentStatus();

	/**
	 * Set the status of the shipment.
	 *
	 * @param status the status of the shipment
	 */
	void setStatus(OrderShipmentStatus status);

	/**
	 * Get the SKUs in this shipment.
	 *
	 * @return the shipment's <code>OrderSku</code>s
	 */
	Set<OrderSku> getShipmentOrderSkus();

	/**
	 * Add an <code>OrderSku</code> to this shipment.
	 *
	 * @param shipmentOrderSku the <code>OrderSku</code> to add to this shipment
	 */
	void addShipmentOrderSku(OrderSku shipmentOrderSku);

	/**
	 * Remove an <code>OrderSku</code> from this shipment.
	 *
	 * @param shipmentOrderSku the <code>OrderSku</code> to remove from this shipment
	 */
	void removeShipmentOrderSku(OrderSku shipmentOrderSku);

	/**
	 * Gets the item subtotal for this order shipment.
	 *
	 * @return BigDecimal
	 * @deprecated use {@link #getSubtotal()} instead
	 */
	@Deprecated
	BigDecimal getItemSubtotal();

	/**
	 * Gets the item subtotal for this order shipment in <code>Money</code>.
	 *
	 * @return Money
	 * @deprecated use {@link #getSubtotalMoney()} instead
	 */
	@Deprecated
	Money getItemSubtotalMoney();

	/**
	 * Checks whether an inclusive tax is required.
	 *
	 * @return boolean
	 */
	boolean isInclusiveTax();

	/**
	 * Sets the inclusive tax flag.
	 *
	 * @param inclusiveTax boolean
	 */
	void setInclusiveTax(boolean inclusiveTax);

	/**
	 * Gets the subtotal discount for this order shipment.
	 *
	 * @return BigDecimal
	 */
	BigDecimal getSubtotalDiscount();

	/**
	 * Gets the subtotal discount for this order shipment in <code>Money</code>.
	 *
	 * @return Money
	 */
	Money getSubtotalDiscountMoney();

	/**
	 * Sets the subtotal discount.
	 *
	 * @param subtotalDiscount BigDecimal
	 */
	void setSubtotalDiscount(BigDecimal subtotalDiscount);

	/**
	 * Checks whether a discount has been applied.
	 *
	 * @return boolean
	 */
	boolean hasSubtotalDiscount();

	/**
	 * Gets the item tax for this order shipment.
	 *
	 * @return BigDecimal
	 */
	BigDecimal getItemTax();

	/**
	 * Gets the item tax for this order shipment in <code>Money</code>.
	 *
	 * @return Money
	 */
	Money getItemTaxMoney();

	/**
	 * Gets the subtotal for this order shipment.
	 *
	 * @return the shipment subtotal
	 */
	BigDecimal getSubtotal();

	/**
	 * Gets the total cost of this order shipment.
	 *
	 * @return the shipment total
	 */
	BigDecimal getTotal();

	/**
	 * Gets the total cost of this order shipment in <code>Money</code>.
	 *
	 * @return Money
	 */
	Money getTotalMoney();

	/**
	 * Gets the total before taxes cost of this order shipment in <code>Money</code>.
	 *
	 * @return Money
	 */
	Money getTotalBeforeTaxMoney();

	/**
	 * Gets the item sub total before taxes cost of this order shipment in <code>Money</code>.
	 *
	 * @return Money
	 * @deprecated use {@link #getSubtotalBeforeTaxMoney()}
	 */
	@Deprecated
	Money getItemSubTotalBeforeTaxMoney();

	/**
	 * Gets the item sub total before taxes cost of this order shipment in <code>BigDecimal</code>.
	 *
	 * @return the item sub total before tax
	 * @deprecated use {@link #getSubtotalBeforeTax()}
	 */
	@Deprecated
	BigDecimal getItemSubTotalBeforeTax();

	/**
	 * @return the order that this shipment is part of
	 */
	Order getOrder();

	/**
	 * @param order the order to set
	 */
	void setOrder(Order order);

	/**
	 * Return the set of <code>OrderTaxValue</code>s.
	 *
	 * @return the set of <code>OrderTaxValue</code>s.
	 */
	Set<OrderTaxValue> getShipmentTaxes();

	/**
	 * Gets the total tax amount for this order shipment in <code>Money</code>.
	 *
	 * @return Money
	 */
	Money getTotalTaxMoney();

	/**
	 * Gets shipment number which is in form: [order_number] - [shipment_number].
	 *
	 * @return the shipmentNumber shipment number.
	 */
	String getShipmentNumber();

	/**
	 * Sets shipment number.
	 *
	 * @param shipmentNumber the shipmentNumber to set.
	 */
	void setShipmentNumber(String shipmentNumber);

	/**
	 * Determines whether or not this shipment is in a state that allows it to be cancelled.
	 *
	 * @return true if this shipment can be cancelled, false if not.
	 */
	boolean isCancellable();

	/**
	 * Determines whether or not this shipment is in a state that allows funds to be captured for the shipment.
	 *
	 * @return true if this shipment can have funds captured, false if not
	 */
	boolean isReadyForFundsCapture();

	/**
	 * Gets the subtotal amount in a Money instance.
	 *
	 * @return the subtotal money instance
	 */
	Money getSubtotalMoney();

	/**
	 * Gets the sub total before taxes cost of this order shipment in <code>Money</code>.
	 *
	 * @return Money
	 */
	Money getSubtotalBeforeTaxMoney();

	/**
	 * Gets the sub total before taxes cost of this order shipment in <code>BigDecimal</code>.
	 *
	 * @return the item sub total before tax
	 */
	BigDecimal getSubtotalBeforeTax();

}
