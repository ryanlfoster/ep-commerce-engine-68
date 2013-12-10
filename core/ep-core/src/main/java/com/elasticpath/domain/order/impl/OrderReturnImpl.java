/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.elasticpath.domain.misc.impl.MoneyFactory;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderTaxValue;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.sellingchannel.ShoppingItemFactory;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;

/**
 * <code>OrderReturn</code> represents a customer's order return.
 */
@Entity
@Table(name = OrderReturnImpl.TABLE_NAME)
@FetchGroups({
		@FetchGroup(name = FetchGroupConstants.ORDER_RETURN_INDEX, attributes = { 
				@FetchAttribute(name = "rmaCode"), 
				@FetchAttribute(name = "order"),
				@FetchAttribute(name = "createdDate"),
				@FetchAttribute(name = "returnStatus") }),
		@FetchGroup(name = FetchGroupConstants.ORDER_INDEX, attributes = { 
				@FetchAttribute(name = "rmaCode") }),
		@FetchGroup(name = FetchGroupConstants.ORDER_SEARCH, attributes = { 
				@FetchAttribute(name = "rmaCode") })
})
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyFields", "PMD.ExcessiveImports", "PMD.ExcessiveClassLength" })
@DataCache(enabled = false)
public class OrderReturnImpl extends AbstractLegacyPersistenceImpl implements OrderReturn {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final int COMMENT_LENGTH = 2000;

	private static final int CALCULATION_SCALE = 10;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TORDERRETURN";

	/**
	 * The length of the RMA code.
	 */
	public static final int RMA_CODE_LENGTH = 64;

	private static final String NEXT_RMA = "NEXT_RMA";


	private Date createdDate;

	private Date lastModifiedDate;

	private String rmaCode;

	private BigDecimal lessRestockAmount;
	
	private BigDecimal shipmentDiscount;

	private Set<OrderReturnSku> orderReturnSkus = new HashSet<OrderReturnSku>();

	private String returnComment;

	private OrderReturnStatus status;

	private OrderReturnType returnType;

	private boolean physicalReturn;

	private CmUser createdByCmUser;

	private CmUser receivedByCmUser;

	private Order order;

	private Order exchangeOrder;

	private OrderPayment orderPayment;

	private BigDecimal beforeTaxReturnTotal;

	private BigDecimal returnTotal;

	private BigDecimal shippingCost = BigDecimal.ZERO;

	private BigDecimal shippingTax = BigDecimal.ZERO;

	private BigDecimal totalTax;

	private BigDecimal subtotal;

	private Set<OrderTaxValue> returnTaxes = new HashSet<OrderTaxValue>();

	private Currency currency;

	private ShoppingCart exchangeShoppingCart;

	private int version;

	private long uidPk;

	private OrderAddress orderReturnAddress;

	/**
	 * Get the date that this order was created on.
	 *
	 * @return the created date
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	public Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * Set the date that the order is created.
	 *
	 * @param createdDate the start date
	 */
	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Get the date that the order return was last modified on.
	 *
	 * @return the last modified date
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	/**
	 * Set the date that the order return was last modified on.
	 *
	 * @param lastModifiedDate the date that the order return was last modified
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * Get the return RMA code.
	 *
	 * @return the return RMA code.
	 */
	@Basic
	@Column(name = "RMA_CODE", length = RMA_CODE_LENGTH, nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = NEXT_RMA)
	@TableGenerator(name = NEXT_RMA, table = "TRMAGENERATOR", pkColumnName = "UIDPK", valueColumnName = NEXT_RMA, pkColumnValue = "1",
			allocationSize = 1)
	public String getRmaCode() {
		return this.rmaCode;
	}

	/**
	 * Set the return RMA code.
	 *
	 * @param rmaCode the return RMA code.
	 */
	public void setRmaCode(final String rmaCode) {
		this.rmaCode = rmaCode;
	}

	/**
	 * Get the set of orderReturnSkus for this <code>OrderReturn</code>.
	 *
	 * @return the set of orderReturnSkus
	 */
	@OneToMany(targetEntity = OrderReturnSkuImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "ORDER_RETURN_UID")
	@ElementForeignKey
	@ElementDependent
	public Set<OrderReturnSku> getOrderReturnSkus() {
		return this.orderReturnSkus;
	}

	/**
	 * Set the set of orderReturnSkus for this <code>OrderReturn</code>.
	 *
	 * @param orderReturnSkus the set of orderReturnSkus
	 */
	public void setOrderReturnSkus(final Set<OrderReturnSku> orderReturnSkus) {
		this.orderReturnSkus = orderReturnSkus;
	}

	/**
	 * Get the return comment.
	 *
	 * @return the return comment.
	 */
	@Basic
	@Column(name = "RETURN_COMMENT", length = COMMENT_LENGTH)
	public String getReturnComment() {
		return this.returnComment;
	}

	/**
	 * Set the return comment.
	 *
	 * @param returnComment the return comment.
	 */
	public void setReturnComment(final String returnComment) {
		this.returnComment = returnComment;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Get the return status.
	 *
	 * @return the return status.
	 */
	@Basic
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	public OrderReturnStatus getReturnStatus() {
		return status;
	}

	/**
	 * Set the return status.
	 *
	 * @param status the return status
	 */
	public void setReturnStatus(final OrderReturnStatus status) {
		this.status = status;
	}

	/**
	 * Get the return type.
	 *
	 * @return the return type.
	 */
	@Basic
	@Enumerated(EnumType.STRING)
	@Column(name = "RETURN_TYPE")
	public OrderReturnType getReturnType() {
		return returnType;
	}

	/**
	 * Set the return type.
	 *
	 * @param returnType the return type
	 */
	public void setReturnType(final OrderReturnType returnType) {
		this.returnType = returnType;
	}

	/**
	 * Get the physical return.
	 *
	 * @return true if physical return.
	 */
	@Basic
	@Column(name = "PHYSICAL_RETURN")
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getPhysicalReturn() {
		return physicalReturn;
	}

	/**
	 * Set the physical return.
	 *
	 * @param physicalReturn the physical return
	 */
	public void setPhysicalReturn(final boolean physicalReturn) {
		this.physicalReturn = physicalReturn;
	}

	/**
	 * Get the CmUser which create order return.
	 *
	 * @return CmUser which create order return
	 */
	@ManyToOne(targetEntity = CmUserImpl.class)
	@JoinColumn(name = "CREATED_BY")
	public CmUser getCreatedByCmUser() {
		return createdByCmUser;
	}

	/**
	 * Set the cmUser which create order return.
	 *
	 * @param createdByCmUser the cmUser which receive order return
	 */
	public void setCreatedByCmUser(final CmUser createdByCmUser) {
		this.createdByCmUser = createdByCmUser;
	}

	/**
	 * Get the CmUser which received order return.
	 *
	 * @return the receivedByCmUser which receive order return
	 */
	@ManyToOne(targetEntity = CmUserImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "RECEIVED_BY")
	public CmUser getReceivedByCmUser() {
		return receivedByCmUser;
	}

	/**
	 * Set the cmUser which received order return.
	 *
	 * @param receivedByCmUser the receivedByCmUser to set
	 */
	public void setReceivedByCmUser(final CmUser receivedByCmUser) {
		this.receivedByCmUser = receivedByCmUser;
	}

	/**
	 * Get the exchange order.
	 *
	 * @return ExchangeOrder if returnType=OrderReturnType.EXCHANGE or null otherwise
	 */
	@OneToOne(targetEntity = OrderImpl.class)
	@JoinColumn(name = "EXCHANGE_ORDER_UID")
	public Order getExchangeOrder() {
		return exchangeOrder;
	}

	/**
	 * Set the exchange order.
	 *
	 * @param exchangeOrder the echangeOrder
	 */
	public void setExchangeOrder(final Order exchangeOrder) {
		this.exchangeOrder = exchangeOrder;
	}

	/**
	 * Get return payment. Used to determine the refund given for the return, or the payment taken for an exchange.
	 *
	 * @return <code>OrderPayment</code>
	 */
	@OneToOne(targetEntity = OrderPaymentImpl.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "ORDER_PAYMENT_UID")
	public OrderPayment getReturnPayment() {
		return orderPayment;
	}

	/**
	 * Set order payment.
	 *
	 * @param orderPayment the orderPayment
	 */
	public void setReturnPayment(final OrderPayment orderPayment) {
		this.orderPayment = orderPayment;
	}

	/**
	 * Get the set of return taxes. This method will return incorrect values
	 * if you attempt to create a return for an order where the tax rate has
	 * changed since the order was completed.
	 *
	 * @return the set of return taxes.
	 */
	@Transient
	public Set<OrderTaxValue> getReturnTaxes() {
		return this.returnTaxes;
	}

	/**
	 * Set the set of return taxes.
	 *
	 * @param returnTaxes the set of return taxes.
	 */
	public void setReturnTaxes(final Set<OrderTaxValue> returnTaxes) {
		this.returnTaxes = returnTaxes;
	}

	/**
	 * Get the before-tax return total.
	 *
	 * @return the before-tax return total.
	 */
	@Transient
	public BigDecimal getBeforeTaxReturnTotal() {
		return this.beforeTaxReturnTotal;
	}

	/**
	 * Set the before-tax return total.
	 *
	 * @param beforeTaxReturnTotal the before-tax return total.
	 */
	public void setBeforeTaxReturnTotal(final BigDecimal beforeTaxReturnTotal) {
		this.beforeTaxReturnTotal = beforeTaxReturnTotal;
	}

	/**
	 * Get the return total.
	 *
	 * @return the return total.
	 */
	@Transient
	public BigDecimal getReturnTotal() {
		return this.returnTotal;
	}

	/**
	 * Set the return total.
	 *
	 * @param returnTotal the return total.
	 */
	public void setReturnTotal(final BigDecimal returnTotal) {
		this.returnTotal = returnTotal;
	}

	/**
	 * Get the total tax for this return.
	 *
	 * @return a <code>BigDecimal</code> object representing the total tax
	 */
	@Transient
	public BigDecimal getTaxTotal() {
		return totalTax;
	}

	/**
	 * Set the total of the item taxes for this order return. This value does not
	 * include the shipping taxes.
	 * @param totalTax the total taxes on items included in this OrderReturn
	 */
	@Transient
	public void setTaxTotal(final BigDecimal totalTax) {
		this.totalTax = totalTax;
	}

	@Override
	@Transient
	public BigDecimal getRefundedTotal() {
		BigDecimal refundedAmount = BigDecimal.ZERO;
		OrderPayment refundedPayment = getReturnPayment();
		if (refundedPayment != null && OrderPayment.CREDIT_TRANSACTION.equals(refundedPayment.getTransactionType())) {
			refundedAmount = refundedPayment.getAmount();
		}
		return refundedAmount;
	}

	@Override
	@Transient
	public BigDecimal getRefundTotal() {
		if (getReturnType() == OrderReturnType.EXCHANGE && getExchangeOrder() != null) {
			return getReturnTotal().subtract(getExchangeOrder().getTotal());
		} else if (getReturnType() == OrderReturnType.EXCHANGE && getExchangeShoppingCart() != null) {
			return getReturnTotal().subtract(getExchangeShoppingCart().getTotal());
		}
		return getReturnTotal();
	}

	@Override
	public void addOrderReturnSku(final OrderReturnSku orderReturnSku) {
		getOrderReturnSkus().add(orderReturnSku);
	}

	@Override
	@Transient
	public Currency getCurrency() {
		if (currency != null) {
			return currency;
		}
		if (getOrder() != null) {
			currency = getOrder().getCurrency();
		}

		return currency;
	}

	@Override
	@Transient
	public ShoppingCart getExchangeShoppingCart() {
		return exchangeShoppingCart;
	}

	@Override
	public void setExchangeShoppingCart(final ShoppingCart exchangeShoppingCart) {
		this.exchangeShoppingCart = exchangeShoppingCart;
	}
	
	@Override
	@ManyToOne(targetEntity = OrderImpl.class, fetch = FetchType.EAGER,
			cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "ORDER_UID", nullable = false)
	@ForeignKey
	public Order getOrder() {
		return order;
	}

	@Override
	public void setOrder(final Order order) {
		this.order = order;
	}

	@Override
	@Transient
	public void populateOrderReturn(final Order order, final OrderShipment orderShipment, final OrderReturnType returnType) {

		setReturnType(returnType);

		if (orderShipment.getShipmentStatus() == OrderShipmentStatus.SHIPPED) {
			for (OrderSku orderSku : orderShipment.getShipmentOrderSkus()) {
				OrderReturnSku orderReturnSku = getBean(ContextIdNames.ORDER_RETURN_SKU);
				orderReturnSku.setOrderSku(orderSku);
				orderReturnSku.setReturnAmount(orderSku.getTotal().getAmount());
				addOrderReturnSku(orderReturnSku);
			}
		}

		setOrder(order);
		setCreatedDate(new Date());
	}

	@Override
	@Basic
	@Column(name = "LESS_RESTOCK_AMOUNT", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getLessRestockAmount() {
		return lessRestockAmount;
	}

	@Override
	public void setLessRestockAmount(final BigDecimal lessRestockAmount) {
		this.lessRestockAmount = lessRestockAmount;
	}
	
	@Override
	@Basic
	@Column(name = "SHIPMENT_DISCOUNT", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getShipmentDiscount() {
		return shipmentDiscount;
	}

	@Override
	public void setShipmentDiscount(final BigDecimal shipmentDiscount) {
		this.shipmentDiscount = shipmentDiscount;
	}


	@Override
	@Transient
	public void recalculateOrderReturn() {
		AbstractOrderShipmentImpl returnShipment = (AbstractOrderShipmentImpl) getReturnShipment();
		if (returnShipment == null) {
			throw new EpDomainException("Attempt to recalculate an order return which has no associated shipment is invalid.");
		}
		updateOrderReturnSkuReturnAmounts();

		Address taxCalculationAddress = getOrderReturnAddress();
		
		Currency currency = getCurrency();
		TaxCalculationService taxCalculationService = getBean(ContextIdNames.TAX_CALCULATION_SERVICE);

		//We only calculate the taxes here using the tax service to get values for the return taxes (the call to setReturnTaxes)
		//because we don't keep historical tax rate data and don't have enough information to reconstruct these from the order
		//When we start keeping historical tax rates we can use the tax service with those rates for this entire process. 
		TaxCalculationResult taxResult = taxCalculationService.calculateTaxes(order.getStoreCode(), taxCalculationAddress, currency,
				getShippingCostMoney(), getOrderReturnSkusAsShoppingItems(), getMoneyZero(currency));

		/* set return taxes amount. */
		setReturnTaxes(taxResult, order.getLocale());

		setTaxTotal(calculateItemTaxes().setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP));

		setShippingTax(taxResult.getShippingTax().getAmount());
		
		setSubtotal(calculateBeforeTaxSubtotal().setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP));

		setBeforeTaxReturnTotal(getSubtotal().add(getShippingCost()));

		/** set total amount which should be subtotal + shipping cost. */
		BigDecimal total = getSubtotal().add(getShippingCost());

		if (getTaxTotal() != null && getShippingTax() != null && !returnShipment.isInclusiveTax()) {
			total = total.add(getTaxTotal().add(getShippingTax()));
		}
		
		if (getShipmentDiscount() != null && getShipmentDiscount().compareTo(total) <= 0) {
			total = total.subtract(getShipmentDiscount());
			setReturnTotal(total);
		} else {
			setReturnTotal(total);
		}

		if (getLessRestockAmount() != null && getLessRestockAmount().compareTo(total) <= 0) {
			setReturnTotal(total.subtract(getLessRestockAmount()));
		} else {
			setReturnTotal(total);
			setLessRestockAmount(BigDecimal.ZERO);
		}
	}

	@Transient
	private Collection< ? extends ShoppingItem> getOrderReturnSkusAsShoppingItems() {
		List<OrderReturnSku> orderReturnSkus = new ArrayList<OrderReturnSku>();
		orderReturnSkus.addAll(this.getOrderReturnSkus());
		List<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();
		for (OrderReturnSku orderReturnSku : orderReturnSkus) {
			ShoppingItem shoppingItem = convertToShoppingItem(orderReturnSku);
			shoppingItems.add(shoppingItem);
		}
		return shoppingItems;
	}

	private ShoppingItem convertToShoppingItem(final OrderReturnSku orderReturnSku) {
		ShoppingItemFactory shoppingItemFactory = getBean("shoppingItemFactory");
		return shoppingItemFactory.createShoppingItem(
				orderReturnSku.getOrderSku().getProductSku(),
				orderReturnSku.getOrderSku().getPrice(), 
				orderReturnSku.getOrderSku().getQuantity(),
				orderReturnSku.getOrderSku().getOrdering(), 
				orderReturnSku.getOrderSku().getFields());
	}

	private void updateOrderReturnSkuReturnAmounts() {
		for (final OrderReturnSku returnSku : getOrderReturnSkus()) {
			returnSku.setReturnAmount(returnSku.getAmountMoney().getAmount());
		}
	}

	private BigDecimal calculateBeforeTaxSubtotal() {
		BigDecimal totalBeforeTax = BigDecimal.ZERO.setScale(CALCULATION_SCALE);
		for (final OrderReturnSku returnSku : getOrderReturnSkus()) {
			totalBeforeTax = totalBeforeTax.add(returnSku.getAmountMoney().getAmount());
		}
		return totalBeforeTax;
	}

	/**
	 * The taxTotal is the sum of all the item taxes. This method will get the item taxes for
	 * all items being returned, apportion the value based on the number of items being returned vs
	 * the number in the original order, and sum them.
	 * @return
	 */
	private BigDecimal calculateItemTaxes() {
		BigDecimal totalItemTax = BigDecimal.ZERO.setScale(CALCULATION_SCALE);
		for (final OrderReturnSku returnSku : getOrderReturnSkus()) {
			//Can't just use OrderReturnSku.getTax() because that needs to be set somewhere, and is normally set by the tax service.
			BigDecimal returnOrderSkuTotalTax = getReturnSkuTotalItemTax(returnSku);
			returnSku.setTax(returnOrderSkuTotalTax);
			totalItemTax = totalItemTax.add(returnOrderSkuTotalTax);
		}
		return totalItemTax;
	}

	private BigDecimal getReturnSkuTotalItemTax(final OrderReturnSku returnSku) {
		BigDecimal originalOrderSkuTotalTax = returnSku.getOrderSku().getTax().getAmount();

		BigDecimal originalOrderSkuQuantity = new BigDecimal(returnSku.getOrderSku().getQuantity());
		BigDecimal returnSkuQuantity = new BigDecimal(returnSku.getQuantity());
		BigDecimal returnQuantityProportion = returnSkuQuantity.divide(originalOrderSkuQuantity, CALCULATION_SCALE, RoundingMode.HALF_UP);
		return originalOrderSkuTotalTax.multiply(returnQuantityProportion);
	}

	private void setReturnTaxes(final TaxCalculationResult taxCalculationResult, final Locale orderLocale) {
		Set<OrderTaxValue> returnTaxes = new HashSet<OrderTaxValue>();

		for (Iterator<TaxCategory> taxIter = taxCalculationResult.getTaxCategoriesIterator(); taxIter.hasNext();) {
			TaxCategory taxCategory = taxIter.next();
			OrderTaxValue orderTaxValue = getBean(ContextIdNames.ORDER_TAX_VALUE);
			orderTaxValue.setTaxCategoryName(taxCategory.getName());
			orderTaxValue.setTaxCategoryDisplayName(taxCategory.getDisplayName(orderLocale));
			orderTaxValue.setTaxValue(taxCalculationResult.getTaxValue(taxCategory).getAmount());
			returnTaxes.add(orderTaxValue);
		}
		setReturnTaxes(returnTaxes);
	}

	private Money getMoneyZero(final Currency currency) {
		return MoneyFactory.createMoney(BigDecimal.ZERO, currency);
	}

	/**
	 * Update order return status based on returned quantities. Call after receiving
	 * quantity for a return sku. 
	 */
	public void updateOrderReturnStatus() {
		if (isInTerminalState()) {
			return;
		}
		if (isFullyReceived()) {
			setReturnStatus(OrderReturnStatus.AWAITING_COMPLETION);
		} else {
			setReturnStatus(OrderReturnStatus.AWAITING_STOCK_RETURN);
		}
	}

	/**
	 * Check whether all return skus in the return are fully received.
	 *
	 * @return true if all return skus fully received
	 */
	@Transient
	public boolean isFullyReceived() {
		for (OrderReturnSku returnSku : getOrderReturnSkus()) {
			if (!returnSku.isFullyReceived()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Check if the return is partially received. This is the case when 
	 * we have received some but not all of the expected stock back from
	 * the customer.
	 *
	 * @return true if some stock returned but not all stock is returned
	 */
	@Transient
	public boolean isPartiallyReceived() {
		boolean someReceived = false;
		boolean fullyReceived = true;
		for (OrderReturnSku returnSku : getOrderReturnSkus()) {
			if (!returnSku.isFullyReceived()) {
				fullyReceived = false;
			}
			if (returnSku.getReceivedQuantity() > 0) {
				someReceived = true;
			}
		}
		return !fullyReceived && someReceived;
	}
	
	/**
	 * Checks whether the order return is in a final state, either cancelled or completed. 
	 *
	 * @return true if final
	 */
	@Transient
	public boolean isInTerminalState() {
		return (getReturnStatus() == OrderReturnStatus.CANCELLED || getReturnStatus() == OrderReturnStatus.COMPLETED);
	}
	
	@Override
	@SuppressWarnings({ "PMD.NPathComplexity" })
	@Transient
	public void updateOrderReturnableQuantity(final Order order) throws EpServiceException {

		Map<Pair<Long, Long>, OrderSku> productSkusForOrderSkus = new HashMap<Pair<Long, Long>, OrderSku>();
		OrderShipment shipment = getReturnShipment();

		for (OrderSku orderSku : shipment.getShipmentOrderSkus()) {
			productSkusForOrderSkus.put(new Pair<Long, Long>(orderSku.getProductSku().getUidPk(), orderSku.getUidPk()), orderSku);
			orderSku.setReturnableQuantity(orderSku.getQuantity());
		}
		ReturnAndExchangeService returnAndExchangeService = getBean(ContextIdNames.ORDER_RETURN_SERVICE);
		for (OrderReturn orderReturn : returnAndExchangeService.list(order.getUidPk())) { // order.getReturns()) { //
			if (orderReturn.getReturnStatus() == OrderReturnStatus.CANCELLED || orderReturn.getReturnShipment().getUidPk() != shipment.getUidPk()) {
				continue;
			}

			if (this.isPersisted() && this.getUidPk() == orderReturn.getUidPk()) { //edit mode
				continue;
			}

			for (OrderReturnSku orderReturnSku : orderReturn.getOrderReturnSkus()) {
				Pair<Long, Long> orderSkuKey = new Pair<Long, Long>(orderReturnSku.getOrderSku().getProductSku().getUidPk(), 
						orderReturnSku.getOrderSku().getUidPk());
				OrderSku orderSku = productSkusForOrderSkus.get(orderSkuKey);
				if (orderSku != null) {
					int returnableQuantity = orderSku.getReturnableQuantity() - orderReturnSku.getQuantity();

					if (returnableQuantity < 0) {
						throw new EpServiceException("Total quantity of returns exceed the order's quantity");
					}

					orderSku.setReturnableQuantity(returnableQuantity);
					orderReturnSku.getOrderSku().setReturnableQuantity(returnableQuantity);
				}
			}
		}

		for (OrderReturnSku orderReturnSku : getOrderReturnSkus()) {
			OrderSku orderSku = productSkusForOrderSkus.get(orderReturnSku.getOrderSku().getProductSku().getUidPk());
			if (orderSku != null) {
				orderReturnSku.getOrderSku().setReturnableQuantity(orderSku.getReturnableQuantity());
			}
		}
	}

	@Override
	public void normalizeOrderReturn() {
		Iterator<OrderReturnSku> iterator = getOrderReturnSkus().iterator();
		while (iterator.hasNext()) {
			OrderReturnSku nextOrderSku = iterator.next();
			if (nextOrderSku.getQuantity() == 0) {
				iterator.remove();
			}
		}
	}

	@Override
	@Basic
	@Column(name = "SHIPPING_COST", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getShippingCost() {
		return shippingCost;
	}

	@Override
	public void setShippingCost(final BigDecimal shippingCost) {
		this.shippingCost = shippingCost;
	}

	/**
	 * Set the shipping tax in <code>BigDecimal</code>.
	 *urnAndExchangeService returnAndExchangeService = getBean(ContextIdNames.ORDER_RETURN_SERVICE);
	 * @param shippingTax the shipping tax
	 */
	@Transient
	protected void setShippingTax(final BigDecimal shippingTax) {
		this.shippingTax = shippingTax;
	}

	@Override
	@Transient
	public BigDecimal getShippingTax() {
		return shippingTax;
	}

	@Override
	@Transient
	public OrderShipment getReturnShipment() {
		Set<OrderReturnSku> orderReturnSkus = getOrderReturnSkus();
		if (!orderReturnSkus.isEmpty()) {
			OrderReturnSku returnSku = orderReturnSkus.iterator().next();
			return returnSku.getOrderSku().getShipment();
		}
		return null;
	}

	/**
	 * Gets the subtotal for this return.
	 *
	 * @return the return subtotal
	 */
	@Transient
	public BigDecimal getSubtotal() {
		return subtotal;
	}

	/**
	 * Sets the subtotal for this return.
	 *
	 * @param subtotal the subtotal
	 */
	protected void setSubtotal(final BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	@Override
	@Transient
	public BigDecimal getOwedToCustomer() {
		return getReturnTotal().subtract(getRefundedTotal());
	}

	/**
	 * Get the owed to customer amount. The value is return total minus refund total.
	 *
	 * @return a <code>Money</code> object representing the owed to customer total.
	 */
	@Transient
	public Money getOwedToCustomerMoney() {
		return getMoney(getOwedToCustomer());
	}

	@Override
	@Transient
	public Money getBeforeTaxReturnTotalMoney() {
		return getMoney(getBeforeTaxReturnTotal());
	}

	@Override
	@Transient
	public Money getRefundTotalMoney() {
		return getMoney(getRefundTotal());
	}

	@Override
	@Transient
	public Money getRefundedTotalMoney() {
		return getMoney(getRefundedTotal()); 
	}

	@Override
	@Transient
	public Money getShippingCostMoney() {
		return getMoney(getShippingCost());
	}

	@Override
	@Transient
	public Money getReturnTotalMoney() {
		return getMoney(getReturnTotal());
	}

	@Override
	@Transient
	public Money getShippingTaxMoney() {
		return getMoney(getShippingTax());
	}

	@Override
	@Transient
	public Money getSubtotalMoney() {
		return getMoney(getSubtotal());
	}

	@Override
	@Transient
	public Money getTaxTotalMoney() {
		return getMoney(getTaxTotal());
	}

	@Override
	@Transient
	public Money getLessRestockAmountMoney() {
		return getMoney(getLessRestockAmount());
	}
	
	@Override
	@Transient
	public Money getShipmentDiscountMoney() {
		return getMoney(getShipmentDiscount());
	}

	private Money getMoney(final BigDecimal amount) {
		return MoneyFactory.createMoney(amount, getCurrency());
	}
	
	@Override
	@Version
	@Basic
	@Column(name = "VERSION")
	public int getVersion() {
		return version;
	}

	@Override
	public void setVersion(final int version) {
		this.version = version;
	}

	/**
	 * Calculate a hashcode based on the RMA code which uniquely identifies this object.
	 *
	 * @return the hashcode
	 */
	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(rmaCode);
	}

	/**
	 * Determine whether the given object is equal to this based on the unique RMA code.
	 *
	 * @param obj the object to compare with this
	 * @return true if the object is equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof OrderReturnImpl)) {
			return false;
		}
		final OrderReturnImpl other = (OrderReturnImpl) obj;
		if (!StringUtils.equals(rmaCode, other.rmaCode)) {
			return false;
		}
		return true;
	}

	@Override
	@ManyToOne(targetEntity = OrderAddressImpl.class, cascade = CascadeType.REFRESH, optional = false)
	@JoinColumn(name = "ORDER_RETURN_ADDRESS_UID")
	@ForeignKey
	public OrderAddress getOrderReturnAddress() {
		return orderReturnAddress;
	}

	@Override
	public void setOrderReturnAddress(final OrderAddress orderAddress) {
		this.orderReturnAddress = orderAddress;
	}

	@Override
	@Transient
	public boolean isInclusiveTax() {
		TaxCalculationService taxCalculationService = getBean(ContextIdNames.TAX_CALCULATION_SERVICE);
		return taxCalculationService.isInclusiveTaxCalculationInUse(order.getStoreCode(), getOrderReturnAddress());
	}
}
