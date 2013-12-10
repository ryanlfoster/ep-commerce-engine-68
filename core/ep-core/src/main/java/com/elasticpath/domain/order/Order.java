/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.ShoppingItemContainer;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.service.tax.TaxCalculationService;

/**
 * <code>Order</code> represents a customer's order.
 */
public interface Order extends Entity, ShoppingItemContainer {

	/** this is used for google checkout service.
	 * TODO: Move this out of the interface. [MSC-7420] */
	String ORDER_SOURCE_GOOGLE_CHECKOUT = "GoogleCheckout";

	/**
	 * Gets cm user's uid.
	 *
	 * @return String cm user's uid
	 */
	Long getCmUserUID();

	/**
	 * Sets the cm user's uid.
	 *
	 * @param cmUserUID the cm user's uid
	 */
	void setCmUserUID(Long cmUserUID);

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
	 * Get the event originator who modified this order.
	 *
	 * @return the event originator
	 */
	EventOriginator getModifiedBy();

	/**
	 * Set the event originator who modified this order.
	 *
	 * @param modifiedBy the event originator
	 */
	void setModifiedBy(EventOriginator modifiedBy);

	/**
	 * Get the ip address of the computer that created the order.
	 *
	 * @return the ip address
	 */
	String getIpAddress();

	/**
	 * Set the ip address of the computer creating the order.
	 *
	 * @param ipAddress the ip address of the creating computer
	 */
	void setIpAddress(String ipAddress);

	/**
	 * Get the customer corresponding to this order.
	 *
	 * @return the customer Uid
	 */
	Customer getCustomer();

	/**
	 * Set the customer corresponding to this order.
	 *
	 * @param customer the Uid of the corresponding customer.
	 */
	void setCustomer(Customer customer);

	/**
	 * Get the billing address corresponding to this order.
	 *
	 * @return the order address Uid
	 */
	OrderAddress getBillingAddress();

	/**
	 * Set the billing address corresponding to this order.
	 *
	 * @param billingAddress the Uid of the corresponding order address.
	 */
	void setBillingAddress(OrderAddress billingAddress);

	/**
	 * Get the shipments associatied with this order.
	 *
	 * @return the orders's <code>OrderShipment</code>s
	 */
	List<OrderShipment> getAllShipments();

	/**
	 * Get the physical shipments associated with this order.
	 *
	 * @return the orders's <code>PhysicalOrderShipment</code>s
	 */
	List<PhysicalOrderShipment> getPhysicalShipments();

	/**
	 * Get the electronic shipments associated with this order.
	 *
	 * @return the orders's <code>ElectronicOrderShipment</code>s
	 */
	Set<ElectronicOrderShipment> getElectronicShipments();

	/**
	 * Get the service shipments associated with this order.
	 *
	 * @return the orders's <code>ServiceOrderShipment</code>s
	 */
	Set<ServiceOrderShipment> getServiceShipments();

	/**
	 * Add an order shipment.
	 *
	 * @param orderShipment the order shipment to add
	 */
	void addShipment(OrderShipment orderShipment);

	/**
	 * Convenience method that should only be used when only a single shipment is supported for a single order.
	 *
	 * @return The shipping address of one of this order's shipments
	 */
	Address getShippingAddress();

	/**
	 * Get the SKUs in this order.
	 *
	 * @return the orders's <code>OrderSkus</code>s
	 * @deprecated Call {@link #getRootShoppingItems()} instead
	 */
	@Deprecated
	Set<OrderSku> getOrderSkus();

	/**
	 * Get the payment(s) for this order.
	 *
	 * @return a set of <code>OrderPayment</code> objects
	 */
	Set<OrderPayment> getOrderPayments();

	/**
	 * Convenience method to retrieve a default order payment for this order. This should only be used when only a single payment is supported for a
	 * single order.
	 *
	 * @return the first order payment for this Order
	 */
	OrderPayment getOrderPayment();

	/**
	 * Set the payment(s) for this order.
	 *
	 * @param orderPayments a set of <code>OrderPayment</code> objects.
	 */
	void setOrderPayments(Set<OrderPayment> orderPayments);

	/**
	 * Add a payment to the order.
	 *
	 * @param orderPayment an <code>OrderPayment</code>
	 */
	void addOrderPayment(OrderPayment orderPayment);

	/**
	 * Set the google order number that is used by google checkout customers to reference their order.
	 *
	 * @param externalOrderNumber the order number, which may include characters.
	 */
	void setExternalOrderNumber(String externalOrderNumber);

	/**
	 * Get the google order number that is used by google checkout customer to reference their order.
	 *
	 * @return the google order number
	 */
	String getExternalOrderNumber();

	/**
	 * Get the events associated with this order.
	 *
	 * @return the orders's <code>OrderEvent</code>s
	 */
	Set<OrderEvent> getOrderEvents();

	/**
	 * Set the events of this order.
	 *
	 * @param orderEvents the set of <code>OrderEvent</code>s
	 * @deprecated Should not be used. Instead add new events with addOrderEvent(OrderEvent).
	 */
	@Deprecated
	void setOrderEvents(Set<OrderEvent> orderEvents);

	/**
	 * Add a order event.
	 *
	 * @param orderEvent a new order event.
	 */
	void addOrderEvent(OrderEvent orderEvent);

	/**
	 * Get the order's locale.
	 *
	 * @return the <code>Locale</code>
	 */
	Locale getLocale();

	/**
	 * Set the order's locale.
	 *
	 * @param locale the <code>Locale</code>
	 */
	void setLocale(Locale locale);

	/**
	 * Get the order's currency.
	 *
	 * @return the <code>Currency</code>
	 */
	Currency getCurrency();

	/**
	 * Set the order's currency.
	 *
	 * @param currency the <code>Currency</code>
	 */
	void setCurrency(Currency currency);

	/**
	 * Get the order total paid by the customer.
	 *
	 * @return the order total
	 */
	BigDecimal getTotal();

	/**
	 * retrieve total amount by redeem the GiftCertificates.
	 *
	 * @return the total amount by redeem the GiftCertificates
	 */
	BigDecimal getTotalGiftCertificateDiscount();

	/**
	 * Get the sub total of all items in the cart after shipping, promotions, etc.
	 *
	 * @return a <code>Money</code> object representing the total
	 */
	Money getTotalMoney();

	/**
	 * Get the order subtotal of all items in the cart.
	 *
	 * @return a <code>BigDecimal</code> object representing the order subtotal
	 */
	BigDecimal getSubtotal();

	/**
	 * Get the discount to the shopping cart subtotal.
	 *
	 * @return the amount discounted from the subtotal
	 */
	BigDecimal getSubtotalDiscount();

	/**
	 * Get the amount discounted from the order subtotal.
	 *
	 * @return the order subtotal discount as a <code>Money</code> object
	 */
	Money getSubtotalDiscountMoney();

	/**
	 * Get the total shipping cost for this order.
	 *
	 * @return a <code>Money</code> representing the total shipping cost
	 */
	Money getTotalShippingCostMoney();

	/**
	 * Get the status of the order.
	 *
	 * @return the order status
	 */
	OrderStatus getStatus();

	/**
	 * Get the total tax for this order.
	 *
	 * @return a <code>Money</code> object representing the total tax
	 */
	Money getTotalTaxMoney();

	/**
	 * Return the paid amount for this order.
	 *
	 * @return the paid amount for this order.
	 */
	BigDecimal getPaidAmount();

	/**
	 * Return the credit amount for this order.
	 *
	 * @return the credit amount for this order.
	 */
	BigDecimal getCreditAmount();

	/**
	 * Return the balance amount for this order.
	 *
	 * @return the balance amount for this order.
	 */
	BigDecimal getBalanceAmount();

	/**
	 * Get the returns associated with this order.
	 *
	 * @return the orders's <code>OrderReturn</code>s
	 */
	Set<OrderReturn> getReturns();

	/**
	 * Set the returns of this order.
	 *
	 * @param returns the set of <code>OrderReturn</code>s
	 * @deprecated Should not be used. Instead add order returns using addReturn(OrderReturn).
	 */
	@Deprecated
	void setReturns(Set<OrderReturn> returns);

	/**
	 * Add a return to the order.
	 *
	 * @param orderReturn the <code>OrderReturn</code> instance.
	 */
	void addReturn(OrderReturn orderReturn);

	/**
	 * Set the order number that is used by customers to reference their order.
	 *
	 * @param orderNumber the order number, which may include characters.
	 */
	void setOrderNumber(String orderNumber);

	/**
	 * Get the order number that is used by customers to reference their order.
	 *
	 * @return the order number
	 */
	String getOrderNumber();

	/**
	 * Get the set of <code>AppliedRule</code> objects that correspond to rules that were fired while processing this order.
	 *
	 * @return a set of <code>AppliedRule</code> objects
	 */
	Set<AppliedRule> getAppliedRules();

	/**
	 * Set the <code>AppliedRule</code> objects that represent rules that were fired while processing this order.
	 *
	 * @param appliedRules a set of <code>AppliedRule</code> objects
	 */
	void setAppliedRules(Set<AppliedRule> appliedRules);

	/**
	 * Calculate total taxes on order. Iterate thru order taxes.
	 *
	 * @return total taxes on order.
	 */
	BigDecimal getTotalTaxes();

	/**
	 * Retrieve an order Sku by its UidPk.
	 *
	 * @param uid the uidPk of the order SKU to be retrieved.
	 * @return the corresponding order SKU or null if no SKU is found
	 */
	OrderSku getOrderSkuByUid(long uid);

	/**
	 * Get the balance money of all items in the cart.
	 *
	 * @return a <code>Money</code> object representing the balance money
	 */
	Money getBalanceMoney();

	/**
	 * Get the paid amount money of all items in the cart.
	 *
	 * @return a <code>Money</code> object representing the paid amount money
	 */
	Money getPaidAmountMoney();

	/**
	 * Get the before-tax total shipping cost for this order.
	 *
	 * @return a <code>Money</code> representing the before-tax total shipping cost
	 */
	Money getBeforeTaxTotalShippingCostMoney();

	/**
	 * Returns true if an order subtotal discount has been applied.
	 *
	 * @return true if an order subtotal discount has been applied
	 */
	boolean hasSubtotalDiscount();

	/**
	 * Get the subtotal of all items in the cart.
	 *
	 * @return a <code>Money</code> object representing the subtotal
	 */
	Money getBeforeTaxSubtotalMoney();

	/**
	 * Gets the tax calculation service.
	 *
	 * @return the tax calculation service
	 */
	TaxCalculationService getTaxCalculationService();


	/**
	 * Get this order's shipment with a given number.
	 * @param shipmentNumber the shipment number to get
	 * @return the shipment with the given number, or null if no shipment with that number is found
	 */
	OrderShipment getShipment(String shipmentNumber);

	/**
	 * Sets the external order source for the order.
	 *
	 * @param orderSource the external order source
	 */
	void setOrderSource(String orderSource);

	/**
	 * Gets the external order source.
	 *
	 * @return order source as String
	 */
	String getOrderSource();

	/**
	 * Get the total of all shipping taxes in the cart.
	 *
	 * @return a <code>Money</code> object representing the total
	 */
	Money getTotalShippingTaxesMoney();

	/**
	 * Get the total of all items' taxes in the cart.
	 *
	 * @return a <code>Money</code> object representing the total
	 */
	Money getTotalItemTaxesMoney();

	/**
	 * Gets the sum of all items money amount before tax.
	 *
	 * @return a <code>Money</code> object representing the total
	 */
	Money getSubtotalMoney();

	/**
	 * Determines whether or not this order is in a state that allows it
	 * to be cancelled.
	 * @return true if this order can be cancelled, false if not.
	 */
	boolean isCancellable();

	/**
	 * Determines whether or not this order is in a state that allows it
	 * to be put on hold.
	 * @return true if this order can be put on hold, false if not.
	 */
	boolean isHoldable();

	/**
	 * Releases the hold on an order.
	 * This implementation resets the order's status based
	 * on the status of its shipments.
	 * This method should be called by the OrderService only.
	 */
	void releaseHoldOnOrder();

	/**
	 * Sets the order status to ONHOLD.
	 * This method should be called by the OrderService only.
	 */
	void holdOrder();

	/**
	 * Sets the order status to CANCELLED.
	 * Statuses of all shipments associated with this order will be set to CANCELLED.
	 * This method should be called by the OrderService only.
	 */
	void cancelOrder();

	/**
	 * Sets the order status to FAILED.
	 * Sets the order shipment status to FAILED_ORDER.
	 * This method should be called by the OrderService only.
	 */
	void failOrder();

	/**
	 * Sets the order status to AWAITING_EXCHANGE.
	 * This method should be called by the OrderService only.
	 */
	void awaitExchnageCompletionOrder();

	/**
	 * Determines whether or not this order is the exchange order.
	 * @return the exchangeOrder
	 */
	Boolean isExchangeOrder();

	/**
	 * Sets exchange order flag.
	 * @param exchnageOrder the exchnageOrder to set
	 */
	void setExchangeOrder(Boolean exchnageOrder);

	/**
	 * If this order is exchange order, the method returns associated exchange.
	 *
	 * @return exchange for this exchange order.
	 */
	OrderReturn getExchange();

	/**
	 * Set exchange for this exchange order.
	 *
	 * @param exchnage exchange
	 */
	void setExchange(OrderReturn exchnage);

	/**
	 * Get the discount due to exchange. The amount is calculated as
	 * exchange.total-exchange.refunded. Valid for exchange order only, otherwise return zero.
	 *
	 * @return the due to exchange amount
	 */
	BigDecimal getDueToRMA();

	/**
	 * Get the discount money due to exchange.
	 *
	 * @return a <code>Money</code> object representing the due to exchange amount
	 */
	Money getDueToRMAMoney();

	/**
	 * Checks whether this order supports refunds.
	 *
	 * @return true if refund is possible
	 */
	boolean isRefundable();

	/**
	 * Gets the total before tax which includes the shipping cost and items cost.
	 *
	 * @return the {@link Money} of the total before tax
	 */
	Money getTotalBeforeTaxMoney();

	/**
	 * Gets the cart order GUID.
	 *
	 * @return the cart order GUID
	 */
	String getCartOrderGuid();

	/**
	 * Sets the cart order GUID.
	 *
	 * @param cartOrderGuid the new cart order GUID
	 */
	void setCartOrderGuid(String cartOrderGuid);

	/**
	 * Gets the {@link com.elasticpath.domain.store.Store} this object belongs to.
	 *
	 * @return the {@link com.elasticpath.domain.store.Store}
	 *
	 * @deprecated Use {@link com.elasticpath.service.store.StoreService#findStoreWithCode(String)} instead.
	 */
	@Deprecated
	Store getStore();

	/**
	 * Sets the {@link Store} this object belongs to.
	 *
	 * @param store the {@link Store} to set
	 *
	 * @deprecated Use {@link #setStoreCode(String)} instead.
	 */
	@Deprecated
	void setStore(final Store store);

	/**
	 * Gets the unique code for the {@link com.elasticpath.domain.store.Store} this object belongs to.
	 *
	 * @return the {@link com.elasticpath.domain.store.Store} code
	 */
	String getStoreCode();

	/**
	 * Sets the unique code for the {@link Store} this object belongs to.
	 *
	 * @param storeCode the {@link Store} code to set
	 */
	void setStoreCode(final String storeCode);

	/**
	 * Accesses the field for {@code name} and returns the current value. If the field has not been set
	 * then will return null.
	 *
	 * @param name The name of the field.
	 * @return The current value of the field or null.
	 */
	String getFieldValue(final String name);

	/**
	 * Assigns {@code value} to {@code name}. Any previous value is replaced.
	 *
	 * @param name The name of the field to assign.
	 * @param value The value to assign to the field.
	 */
	void setFieldValue(final String name, final String value);

	/**
	 * Assigns {@code value} to {@code name}. Any previous value is replaced.
	 *
	 * @param propertyKey The name of the field to remove.
	 */
	void removeFieldValue(String propertyKey);

	/**
	 * @return An immutable map containing all key/value data field pairs
	 */
	Map<String, String> getFieldValues();

}
