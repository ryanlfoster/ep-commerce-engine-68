package com.elasticpath.domain.order.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.IllegalOperationException;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.ListenableObject;
import com.elasticpath.domain.PostLoadRecalculate;
import com.elasticpath.domain.RecalculableObject;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.impl.AbstractListenableEntityImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderData;
import com.elasticpath.domain.order.OrderEvent;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.ServiceOrderShipment;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.impl.AppliedRuleImpl;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.payment.impl.OrderPaymentHelper;
import com.elasticpath.service.tax.TaxCalculationService;

/**
 * The default implementation of <code>Order</code>.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessiveClassLength", "PMD.ExcessiveImports",
	"PMD.CyclomaticComplexity", "PMD.CouplingBetweenObjects", "PMD.AvoidDuplicateLiterals", "PMD.ExcessivePublicCount" })
@Entity
@Table(name = OrderImpl.TABLE_NAME)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.ORDER_RETURN_INDEX, attributes = {
			@FetchAttribute(name = "customer"),
			@FetchAttribute(name = "orderNumber")
	}),
	@FetchGroup(name = FetchGroupConstants.ORDER_INDEX, attributes = {
			@FetchAttribute(name = "orderNumber"),
			@FetchAttribute(name = "customer"),
			@FetchAttribute(name = "storeCode"),
			@FetchAttribute(name = "billingAddress"),
			@FetchAttribute(name = "createdDate"),
			@FetchAttribute(name = "status"),
			@FetchAttribute(name = "shipments"),
			@FetchAttribute(name = "returns"),
			@FetchAttribute(name = "total"),
			@FetchAttribute(name = "currency")
		}),
		@FetchGroup(name = FetchGroupConstants.ORDER_SEARCH, attributes = {
				@FetchAttribute(name = "orderNumber"),
				@FetchAttribute(name = "shipments"),
				@FetchAttribute(name = "billingAddress"),
				@FetchAttribute(name = "customer"),
				@FetchAttribute(name = "storeCode"),
				@FetchAttribute(name = "createdDate"),
				@FetchAttribute(name = "status"),
				@FetchAttribute(name = "currency"),
				@FetchAttribute(name = "locale"),
				@FetchAttribute(name = "total")
		}),
	@FetchGroup(name = FetchGroupConstants.ORDER_DEFAULT, fetchGroups = { FetchGroupConstants.ORDER_INDEX }, attributes = {
			@FetchAttribute(name = "orderEvents"),
			@FetchAttribute(name = "appliedRules"),
			@FetchAttribute(name = "shipments"),
			@FetchAttribute(name = "locale")
		},
		postLoad = true),
	@FetchGroup(name = FetchGroupConstants.ORDER_NOTES, attributes = {
			@FetchAttribute(name = "orderEvents")
	}),
	@FetchGroup(name = FetchGroupConstants.ORDER_STORE_AND_WAREHOUSE, attributes = {
			@FetchAttribute(name = "storeCode")
	}),
	@FetchGroup(name = FetchGroupConstants.ORDER_LIST_BASIC, attributes = {
			@FetchAttribute(name = "orderNumber"),
			@FetchAttribute(name = "storeCode"),
			@FetchAttribute(name = "createdDate"),
			@FetchAttribute(name = "status"),
			@FetchAttribute(name = "currency"),
			@FetchAttribute(name = "total")
	})
})
@DataCache(enabled = false)
public class OrderImpl extends AbstractListenableEntityImpl implements Order, PropertyChangeListener, RecalculableObject, ListenableProperties {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final Logger LOG = Logger.getLogger(OrderImpl.class);

	private Date createdDate;

	private Date lastModifiedDate;

	private EventOriginator modifiedBy;

	private String ipAddress;

	private Customer customer;

	private OrderAddress orderBillingAddress;

	private List<OrderShipment> shipments = new ArrayList<OrderShipment>();

	private Set<OrderReturn> returns = new HashSet<OrderReturn>();

	private Set<OrderEvent> orderEvents = new LinkedHashSet<OrderEvent>();

	private Locale locale;

	private Currency currency;

	private BigDecimal total;

	private Set<OrderPayment> orderPayments = new HashSet<OrderPayment>();

	// At creation time an order is in progress
	private OrderStatus status = OrderStatus.IN_PROGRESS;

	private Set<AppliedRule> appliedRules;

	private String orderNumber;

	private String externalOrderNumber;

	private static final int LOCALE_LENGTH = 20;

	private static final String ORDER_UID = "ORDER_UID";

	private static final String NEXT_ORDER_NUMBER = "NEXT_ORDER_NUMBER";


	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TORDER";

	// private static final String UIDPK_ASCENDING = "uidPk ASC";

	private Store store;
	private String storeCode;

	private Long cmUserUID;

	private long uidPk;

	private transient TaxCalculationService taxCalculationService;

	private boolean recalculationEnabled = true;

	private Boolean exchangeOrder = Boolean.FALSE;

	private OrderReturn exchange;

	private String orderSource;

	private String cartOrderGuid;
	private Map<String, OrderData> orderData = new HashMap<String, OrderData>();

	/**
	 * Gets the external order source.
	 *
	 * @return order source as String
	 */
	@Override
	@Basic
	@Column(name = "ORDER_SOURCE")
	public String getOrderSource() {
		return orderSource;
	}

	/**
	 * Sets the external order source for the order.
	 *
	 * @param orderSource the external order source
	 */
	@Override
	public void setOrderSource(final String orderSource) {
		this.orderSource = orderSource;
	}

	@Override
	@Basic
	@Column(name = "CART_ORDER_GUID")
	public String getCartOrderGuid() {
		return cartOrderGuid;
	}

	@Override
	public void setCartOrderGuid(final String cartOrderGuid) {
		this.cartOrderGuid = cartOrderGuid;
	}

	/**
	 * Gets cm user's uid.
	 *
	 * @return String cm user's uid
	 */
	@Override
	@Basic
	@Column(name = "CREATED_BY")
	public Long getCmUserUID() {
		return cmUserUID;
	}

	/**
	 * Sets the cm user's uid.
	 *
	 * @param cmUserUID the cm user's uid
	 */
	@Override
	public void setCmUserUID(final Long cmUserUID) {
		this.cmUserUID = cmUserUID;
	}

	/**
	 * Gets the {@link com.elasticpath.domain.store.Store} this object belongs to.
	 *
	 * @return the {@link com.elasticpath.domain.store.Store}
	 *
	 * @deprecated Use StoreService.findStoreWithCode(order.getStoreCode()) instead.
	 */
	@Override
	@Transient
	@Deprecated
	public Store getStore() {
		return store;
	}

	/**
	 * Sets the {@link Store} this object belongs to.
	 *
	 * @param store the {@link Store} to set
	 *
	 * @deprecated Use setStoreCode() instead.
	 */
	@Override
	@Deprecated
	public void setStore(final Store store) {
		this.store = store;

		if (store == null) {
			setStoreCode(null);
		} else {
			setStoreCode(store.getCode());
		}
	}

	@Override
	@Basic(optional = false)
	@Column(name = "STORECODE")
	public String getStoreCode() {
		return storeCode;
	}

	@Override
	public void setStoreCode(final String code) {
		this.storeCode = code;
	}

	/**
	 * Get the date that this order was created on.
	 *
	 * @return the created date
	 */
	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Set the date that the order is created.
	 *
	 * @param createdDate the start date
	 */
	@Override
	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Get the date that the order was last modified on.
	 *
	 * @return the last modified date
	 */
	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * Set the date that the order was last modified on.
	 *
	 * @param lastModifiedDate the date that the order was last modified
	 */
	@Override
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * Get the event originator who modified this order.
	 *
	 * @return the event originator
	 */
	@Override
	@Transient
	public EventOriginator getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * Set the event originator who modified this order.
	 *
	 * @param modifiedBy the event originator
	 */
	@Override
	public void setModifiedBy(final EventOriginator modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * Get the ip address of the computer that created the order.
	 *
	 * @return the ip address
	 */
	@Override
	@Basic
	@Column(name = "IP_ADDRESS")
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Set the ip address of the computer creating the order.
	 *
	 * @param ipAddress the ip address of the creating computer
	 */
	@Override
	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * Get the customer corresponding to this order.
	 *
	 * @return the customer Uid
	 */
	@Override
	@ManyToOne(targetEntity = CustomerImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinColumn(name = "CUSTOMER_UID")
	@ForeignKey(name = "FK_O_CUSTOMER", enabled = true)
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * Set the customer corresponding to this order.
	 *
	 * @param customer the Uid of the corresponding customer.
	 */
	@Override
	public void setCustomer(final Customer customer) {
		this.customer = customer;
	}

	/**
	 * Get the billing address corresponding to this order.
	 *
	 * @return the order address Uid
	 */
	@Override
	@ManyToOne(targetEntity = OrderAddressImpl.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "ORDER_BILLING_ADDRESS_UID")
	@ForeignKey(name = "FK_O_ORDERADDRESS", enabled = true)
	public OrderAddress getBillingAddress() {
		return orderBillingAddress;
	}

	/**
	 * Set the billing address corresponding to this order.
	 *
	 * @param orderBillingAddress the Uid of the corresponding order address.
	 */
	@Override
	public void setBillingAddress(final OrderAddress orderBillingAddress) {
		this.orderBillingAddress = orderBillingAddress;
	}

	/**
	 * Get the payment(s) for this order.
	 *
	 * @return a set of <code>OrderPayment</code> objects
	 */
	@Override
	@OneToMany(targetEntity = OrderPaymentImpl.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = ORDER_UID, nullable = false, updatable = false)
	@ElementForeignKey
	@ElementDependent
	@OrderBy
	public Set<OrderPayment> getOrderPayments() {
		return orderPayments;
	}

	/**
	 * Set the payment(s) for this order.
	 *
	 * @param orderPayments a set of <code>OrderPayment</code> objects.
	 */
	@Override
	public void setOrderPayments(final Set<OrderPayment> orderPayments) {
		this.orderPayments = orderPayments;
	}

	/**
	 * Convenience method to retrieve a default order payment for this order. This should only be used when only a single payment is supported for a
	 * single order.
	 *
	 * @return the first order payment for this Order
	 */
	@Override
	@Transient
	public OrderPayment getOrderPayment() {
		if (getOrderPayments() == null || getOrderPayments().isEmpty()) {
			return null;
		}
		// Find a payment which is not a gift certificate.
		for (OrderPayment orderPayment : getOrderPayments()) {
			if ((orderPayment != null) && !(orderPayment.getPaymentMethod() == PaymentType.GIFT_CERTIFICATE)
					&& !(orderPayment.getPaymentMethod() == PaymentType.RETURN_AND_EXCHANGE)) {
				return orderPayment;
			}
		}
		// if no non-gift cert payment found return the first one as there seems to be only gift certificate or exchange payments
		return getOrderPayments().iterator().next();
	}

	/**
	 * retrieve total amount by redeem the GiftCertificates.
	 *
	 * @return the total amount by redeem the GiftCertificates
	 */
	@Override
	@Transient
	public BigDecimal getTotalGiftCertificateDiscount() {
		return OrderPaymentHelper.getTotalGiftCertificateDiscount(this);
	}

	/**
	 * Get the total discount money for GC.
	 *
	 * @return a <code>Money</code> object representing the total discount money
	 */
	@Transient
	public Money getTotalGiftCertificateDiscountMoney() {
		return MoneyFactory.createMoney(getTotalGiftCertificateDiscount(), getCurrency());
	}

	/**
	 * Add a payment to the order.
	 *
	 * @param orderPayment an <code>OrderPayment</code>
	 */
	@Override
	public void addOrderPayment(final OrderPayment orderPayment) {
		getOrderPayments().add(orderPayment);
	}

	/**
	 * Get the shipments associated with this order.
	 *
	 * @return the orders's <code>OrderShipment</code>s
	 */
	@OneToMany(targetEntity = AbstractOrderShipmentImpl.class, cascade = CascadeType.ALL, mappedBy = "orderInternal", fetch = FetchType.EAGER)
	//@OrderBy("createdDate")
	protected List<OrderShipment> getShipments() {
		return shipments;
	}

	/**
	 * Get all the shipments associated with this order.
	 *
	 * @return the orders's <code>OrderShipment</code>s
	 */
	@Override
	@Transient
	public List<OrderShipment> getAllShipments() {
		if (getShipments() == null) {
			return Collections.emptyList();
		}
		List<OrderShipment> orderShipments = new LinkedList<OrderShipment>();
		orderShipments.addAll(getShipments()); // sort by createdDate, since @OrderBy above confuses JPA query generation
		Collections.sort(orderShipments, new ShipmentCreatedDateComparator());
		return Collections.unmodifiableList(orderShipments);
	}

	/**
	 * Get the physical shipments associated with this order.
	 *
	 * @return the orders's <code>PhysicalOrderShipment</code>s
	 */
	@Override
	@Transient
	public List<PhysicalOrderShipment> getPhysicalShipments() {
		List<PhysicalOrderShipment> physicalShipments = new ArrayList<PhysicalOrderShipment>();
		for (OrderShipment orderShipment : getAllShipments()) {
			if (PhysicalOrderShipment.class.isAssignableFrom(orderShipment.getClass())) {
				physicalShipments.add((PhysicalOrderShipment) orderShipment);
			}
		}
		return Collections.unmodifiableList(physicalShipments);
	}

	/**
	 * Get the electronic shipments associated with this order.
	 *
	 * @return the orders's <code>ElectronicOrderShipment</code>s
	 */
	@Override
	@Transient
	public Set<ElectronicOrderShipment> getElectronicShipments() {
		Set<ElectronicOrderShipment> electronicShipments = new HashSet<ElectronicOrderShipment>();

		for (OrderShipment orderShipment : getAllShipments()) {
			if (ElectronicOrderShipment.class.isAssignableFrom(orderShipment.getClass())) {
				electronicShipments.add((ElectronicOrderShipment) orderShipment);
			}
		}
		return Collections.unmodifiableSet(electronicShipments);
	}

	/**
	 * Get the service shipments associated with this order.
	 *
	 * @return the orders's <code>ServiceOrderShipment</code>s
	 */
	@Override
	@Transient
	public Set<ServiceOrderShipment> getServiceShipments() {
		Set<ServiceOrderShipment> shipments = new HashSet<ServiceOrderShipment>();
		for (OrderShipment shipment : getShipments()) {
			if (shipment.getOrderShipmentType() == ShipmentType.SERVICE) {
				shipments.add((ServiceOrderShipment) shipment);
			}
		}
		return Collections.unmodifiableSet(shipments);
	}

	/**
	 * Set the shipments of this order.
	 *
	 * @param shipments the set of <code>OrderShipment</code>s
	 */
	protected void setShipments(final List<OrderShipment> shipments) {
		this.shipments = shipments;
	}

	/**
	 * Sets up all the shipments after load or update occurs on order object.
	 */
	@PostLoad
	@PostUpdate
	protected void initializeOrderAndOrderShipments() {
		registerPropertyListeners();
		enableRecalculation();
	}

	/**
	 * Add an order shipment.
	 *
	 * @param orderShipment the order shipment to add
	 */
	@Override
	public void addShipment(final OrderShipment orderShipment) {
		if (!isPersisted()) {
			throw new OrderNotPersistedException("You can't add a shipment to an order that has not been persisted.");
		}
		//TODO: Threadsafe shipment number calculation
		orderShipment.setShipmentNumber(getOrderNumber() + "-" + (getShipments().size() + 1));
		getShipments().add(orderShipment);
		((ListenableObject) orderShipment).addPropertyChangeListener("total", this, true);
		((ListenableObject) orderShipment).addPropertyChangeListener("shipmentStatus", this, true);
		((RecalculableObject) orderShipment).enableRecalculation();
		orderShipment.setOrder(this);
	}

	/**
	 * Convenience method that should only be used when only a single shipment is supported for a single order.
	 *
	 * @return The shipping address of one of this order's shipments
	 */
	@Override
	@Transient
	public Address getShippingAddress() {
		for (OrderShipment orderShipment : getAllShipments()) {
			if (PhysicalOrderShipment.class.isAssignableFrom(orderShipment.getClass())) {
				return ((PhysicalOrderShipment) orderShipment).getShipmentAddress();
			}
		}
		return null;
	}

	/**
	 * Get the SKUs in this order.
	 *
	 * @return the orders's <code>OrderSkus</code>s
	 * @deprecated Call {@link #getRootShoppingItems()} instead
	 */
	@Override
	@Transient
	@Deprecated
	public Set<OrderSku> getOrderSkus() {
		Set<OrderSku> skus = new HashSet<OrderSku>();
		for (OrderShipment shipment : getAllShipments()) {
			skus.addAll(shipment.getShipmentOrderSkus());
		}
		return skus;
	}

	/**
	 * Get the events associated with this order.
	 *
	 * @return the orders's <code>OrderEvent</code>s
	 */
	@Override
	@OneToMany(targetEntity = OrderEventImpl.class, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = ORDER_UID, nullable = false, updatable = false)
	@OrderBy
	@ElementForeignKey
	public Set<OrderEvent> getOrderEvents() {
		return orderEvents;
	}

	/**
	 * Set the events of this order. Used by JPA. To be set to protected when
	 * the deprecated interface method is removed.
	 *
	 * @param orderEvents the set of <code>OrderEvent</code>s
	 */
	@Override
	@Deprecated
	public void setOrderEvents(final Set<OrderEvent> orderEvents) {
		this.orderEvents = orderEvents;
	}

	/**
	 * Add a order event.
	 *
	 * @param orderEvent a new order event.
	 */
	@Override
	public void addOrderEvent(final OrderEvent orderEvent) {
		getOrderEvents().add(orderEvent);
	}

	/**
	 * Sets the locale.
	 *
	 * @param locale the locale to set.
	 */
	@Override
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	/**
	 * Returns the locale.
	 *
	 * @return the locale
	 */
	@Override
	@Persistent
	@Externalizer("toString")
	@Factory("org.apache.commons.lang.LocaleUtils.toLocale")
	@Column(name = "LOCALE", length = LOCALE_LENGTH, nullable = false)
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Get the currency of the customer corresponding to the shopping cart.
	 *
	 * @return the <code>Currency</code>
	 */
	@Override
	@Persistent
	@Column(name = "CURRENCY")
	@Externalizer("getCurrencyCode")
	@Factory("com.elasticpath.commons.util.impl.ConverterUtils.currencyFromString")
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * Set the currency of the customer corresponding to the shopping cart.
	 *
	 * @param currency the <code>Currency</code>
	 */
	@Override
	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	/**
	 * Get the order total paid by the customer.
	 *
	 * @return the order total
	 */
	@Override
	@Basic
	@Column(name = "TOTAL", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getTotal() {
		return total;
	}

	/**
	 * Get the sub total of all items in the cart after shipping, promotions, etc. The order payment must be set before calling this method.
	 *
	 * @return a <code>Money</code> object representing the total
	 */
	@Override
	@Transient
	public Money getTotalMoney() {
		return MoneyFactory.createMoney(getTotal(), getCurrency());
	}

	/**
	 * Set the order total paid by the customer.
	 *
	 * @param orderTotal the order total
	 */
	protected void setTotal(final BigDecimal orderTotal) {
		total = orderTotal;
	}

	/**
	 * Get the discount to the shopping cart subtotal.
	 *
	 * @return the amount discounted from the subtotal
	 */
	@Override
	@Transient
	public BigDecimal getSubtotalDiscount() {
		BigDecimal subtotalDiscount = BigDecimal.ZERO.setScale(2);
		for (OrderShipment currShipment : getAllShipments()) {
			if (currShipment.getSubtotalDiscount() != null) {
				subtotalDiscount = subtotalDiscount.add(currShipment.getSubtotalDiscount());
			}
		}

		return subtotalDiscount;
	}

	/**
	 * Get the sub total of all items in the cart after shipping, promotions, etc. The order payment must be set before calling this method.
	 *
	 * @return a <code>Money</code> object representing the total
	 */
	@Override
	@Transient
	public Money getSubtotalDiscountMoney() {
		return MoneyFactory.createMoney(getSubtotalDiscount(), getCurrency());
	}

	/**
	 * Get the balance money of all items in the cart.
	 *
	 * @return a <code>Money</code> object representing the balance money
	 */
	@Override
	@Transient
	public Money getBalanceMoney() {
		return MoneyFactory.createMoney(getBalanceAmount(), getCurrency());
	}

	/**
	 * Get the paid amount money of all items in the cart.
	 *
	 * @return a <code>Money</code> object representing the paid amount money
	 */
	@Override
	@Transient
	public Money getPaidAmountMoney() {
		return MoneyFactory.createMoney(getPaidAmount(), getCurrency());
	}

	/**
	 * Get the order subtotal of all items in the cart.
	 *
	 * @return a <code>BigDecimal</code> object representing the order subtotal
	 */
	@Override
	@Transient
	public BigDecimal getSubtotal() {
		BigDecimal subtotal = BigDecimal.ZERO.setScale(2);
		for (OrderShipment currShipment : getAllShipments()) {
			subtotal = subtotal.add(currShipment.getSubtotal());
		}

		return subtotal;
	}

	/**
	 * Get the total shipping cost for this order.
	 *
	 * @return a <code>Money</code> representing the total shipping cost
	 */
	@Override
	@Transient
	public Money getTotalShippingCostMoney() {
		Money shippingMoney = MoneyFactory.createMoney(BigDecimal.ZERO.setScale(2), getCurrency());
		for (OrderShipment currShipment : getAllShipments()) {
			if (PhysicalOrderShipment.class.isAssignableFrom(currShipment.getClass())) {
				Money currShippingCostMoney = MoneyFactory.createMoney(((PhysicalOrderShipment) currShipment).getShippingCost(), getCurrency());
				shippingMoney = shippingMoney.add(currShippingCostMoney);
			}
		}
		return shippingMoney;
	}

	/**
	 * Get the before-tax total shipping cost for this order.
	 *
	 * @return a <code>Money</code> representing the before-tax total shipping cost
	 */
	@Override
	@Transient
	public Money getBeforeTaxTotalShippingCostMoney() {
		Money beforeTaxShippingMoney = MoneyFactory.createMoney(BigDecimal.ZERO.setScale(2), getCurrency());
		for (OrderShipment currShipment : getAllShipments()) {
			if (PhysicalOrderShipment.class.isAssignableFrom(currShipment.getClass())) {
				Money currShippingCostMoney = MoneyFactory.createMoney(
						((PhysicalOrderShipment) currShipment).getBeforeTaxShippingCost(), getCurrency());
				beforeTaxShippingMoney = beforeTaxShippingMoney.add(currShippingCostMoney);
			}
		}
		return beforeTaxShippingMoney;
	}

	/**
	 * Get the total tax for this order.
	 *
	 * @return a <code>Money</code> object representing the total tax
	 */
	@Override
	@Transient
	public Money getTotalTaxMoney() {
		Money totalTaxMoney = MoneyFactory.createMoney(BigDecimal.ZERO.setScale(2), getCurrency());
		for (OrderShipment shipment : getAllShipments()) {
			totalTaxMoney = totalTaxMoney.add(shipment.getTotalTaxMoney());
		}
		return totalTaxMoney;
	}

	/**
	 * Get the status of the order.
	 *
	 * @return the order status
	 */
	@Override
	@Persistent(optional = false)
	@Column(name = "STATUS")
	@Externalizer("getName")
	@Factory("valueOf")
	public OrderStatus getStatus() {
		return status;
	}

	/**
	 * Set the status of the order.
	 *
	 * @param status the status of the order
	 */
	void setStatus(final OrderStatus status) {
		this.status = status;
	}

	/**
	 * Return the paid amount for this order.
	 *
	 * @return the paid amount for this order.
	 */
	@Override
	@Transient
	public BigDecimal getPaidAmount() {
		BigDecimal paidAmount = BigDecimal.ZERO.setScale(2);
		for (Object element : getOrderPayments()) {
			final OrderPayment curOrderPayment = (OrderPayment) element;
			if (curOrderPayment.getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION)
					&& curOrderPayment.getStatus() == OrderPaymentStatus.APPROVED) {
				paidAmount = paidAmount.add(curOrderPayment.getAmount());
			}
		}
		return paidAmount;
	}

	/**
	 * Return the credit amount for this order.
	 *
	 * @return the credit amount for this order.
	 */
	@Override
	@Transient
	public BigDecimal getCreditAmount() {
		BigDecimal creditAmount = BigDecimal.ZERO.setScale(2);
		for (OrderPayment orderPayment : getOrderPayments()) {
			if (orderPayment.getTransactionType().equals(OrderPayment.CREDIT_TRANSACTION)
					&& orderPayment.getStatus() == OrderPaymentStatus.APPROVED) {
				creditAmount = creditAmount.add(orderPayment.getAmount());
			}
		}
		return creditAmount;
	}

	/**
	 * Return the balance amount for this order.
	 *
	 * @return the balance amount for this order.
	 */
	@Override
	@Transient
	public BigDecimal getBalanceAmount() {
		BigDecimal balanceAmount = getTotal();
		// subtract total amounts for all the shipments that are canceled
		for (OrderShipment shipment : getAllShipments()) {
			if (shipment.getShipmentStatus() == OrderShipmentStatus.CANCELLED) {
				balanceAmount = balanceAmount.subtract(shipment.getTotal());
			}
		}
		return getExchangeOrderAmount(balanceAmount).subtract(getPaidAmount());
	}

	/**
	 * Get the returns associated with this order.
	 *
	 * @return the orders's <code>OrderReturn</code>s
	 */
	@Override
	@OneToMany(targetEntity = OrderReturnImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH }, mappedBy = "order")
	public Set<OrderReturn> getReturns() {
		return returns;
	}

	/**
	 * Set the returns of this order. Used by JPA. To be set to protected when
	 * the deprecated interface method is removed.
	 *
	 * @param returns the set of <code>OrderReturn</code>s
	 */
	@Override
	@Deprecated
	public void setReturns(final Set<OrderReturn> returns) {
		this.returns = returns;
	}

	/**
	 * Add a return to the order.
	 *
	 * @param orderReturn the <code>OrderReturn</code> instance.
	 */
	@Override
	public void addReturn(final OrderReturn orderReturn) {
		orderReturn.setOrder(this);
		getReturns().add(orderReturn);
	}

	/**
	 * Set the order number that is used by customers to reference their order.
	 *
	 * @param orderNumber the order number, which may include characters.
	 */
	@Override
	public void setOrderNumber(final String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * Get the order number that is used by customers to reference their order.
	 *
	 * @return the order number
	 */
	@Override
	@Basic
	@Column(name = "ORDER_NUMBER", length = GUID_LENGTH, nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = NEXT_ORDER_NUMBER)
	@TableGenerator(name = NEXT_ORDER_NUMBER, table = "TORDERNUMBERGENERATOR", pkColumnName = "UIDPK",
			valueColumnName = NEXT_ORDER_NUMBER, pkColumnValue = "1", allocationSize = 1)
	public String getOrderNumber() {
		return orderNumber;
	}

	/**
	 * Set the external order number that is used by external services to reference their order.
	 * External systems such as google checkout or web service created orders, etc.
	 *
	 * @param externalOrderNumber the order number, which may include characters.
	 */
	@Override
	public void setExternalOrderNumber(final String externalOrderNumber) {
		this.externalOrderNumber = externalOrderNumber;
	}

	/**
	 * Get the external order number that is used by external services to reference their order.
	 * External systems such as google checkout or web service created orders, etc.
	 *
	 * @return the google order number
	 */
	@Override
	@Basic
	@Column(name = "EXTERNAL_ORDER_NUMBER", length = GUID_LENGTH, nullable = true, unique = true)
	public String getExternalOrderNumber() {
		return externalOrderNumber;
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Transient
	public String getGuid() {
		return getOrderNumber();
	}

	/**
	 * Always throws IllegalOperationException.
	 * Nobody is allowed to setGuid on Orders.
	 *
	 * @param guid this parameter will be ignored.
	 * @throws IllegalOperationException always
	 */
	@Override
	public void setGuid(final String guid) {
		throw new IllegalOperationException("setting of guid is not allowed for OrderImpl");
		// orderNumber is the real guid and is generated by openjpa
		// nothing should ever call this method
	}

	@Override
	public void initialize() {
		// we override this method to prevent the bean factory calling setGuid on this class.
	}

	/**
	 * Get the set of <code>AppliedRule</code> objects that correspond to rules that were fired while processing this order.
	 *
	 * @return a set of <code>AppliedRule</code> objects
	 */
	@Override
	@OneToMany(targetEntity = AppliedRuleImpl.class, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = ORDER_UID, nullable = false, updatable = false)
	public Set<AppliedRule> getAppliedRules() {
		return appliedRules;
	}

	/**
	 * Set the <code>AppliedRule</code> objects that represent rules that were fired while processing this order.
	 *
	 * @param appliedRules a set of <code>AppliedRule</code> objects
	 */
	@Override
	public void setAppliedRules(final Set<AppliedRule> appliedRules) {
		this.appliedRules = appliedRules;
	}

	/**
	 * Calculate total taxes on order. Iterate thru order taxes.
	 *
	 * @return total taxes on order.
	 */
	@Override
	@Transient
	public BigDecimal getTotalTaxes() {
		BigDecimal total = BigDecimal.ZERO.setScale(2);
		for (OrderShipment orderShipment : getAllShipments()) {
			total = total.add(orderShipment.getTotalTaxMoney().getAmount());
		}
		return total;
	}

	/**
	 * Retrieve an order Sku by its UidPk.
	 *
	 * @param uid the uidPk of the order SKU to be retrieved.
	 * @return the corresponding order SKU or null if no SKU is found
	 */
	@Override
	@Transient
	public OrderSku getOrderSkuByUid(final long uid) {
		for (Object element : getOrderSkus()) {
			OrderSku currOrderSku = (OrderSku) element;
			if (currOrderSku.getUidPk() == uid) {
				return currOrderSku;
			}
		}
		return null;
	}

	@Override
	public ShoppingItem getShoppingItemByGuid(final String itemGuid) {
		for (ShoppingItem item : getOrderSkus()) {
			if (item.getGuid().equals(itemGuid)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Returns true if an order subtotal discount has been applied.
	 *
	 * @return true if an order subtotal discount has been applied
	 */
	@Override
	@Transient
	public boolean hasSubtotalDiscount() {
		for (OrderShipment shipment : getAllShipments()) {
			if (shipment.hasSubtotalDiscount()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the before-tax subtotal of all items in the cart.
	 *
	 * @return a <code>Money</code> object representing the before-tax subtotal
	 */
	@Override
	@Transient
	public Money getBeforeTaxSubtotalMoney() {
		BigDecimal beforeTaxSubtotalAmount = BigDecimal.ZERO.setScale(2);
		for (OrderShipment shipment : getAllShipments()) {
			if (shipment.getSubtotalBeforeTax() != null) {
				beforeTaxSubtotalAmount = beforeTaxSubtotalAmount.add(shipment.getSubtotalBeforeTax());
			}
		}

		return MoneyFactory.createMoney(beforeTaxSubtotalAmount, getCurrency());
	}

	/**
	 * Recalculate total when modifications are made to the shipments.
	 */
	private void recalculate() {
		if (isRecalculationEnabled()) {
			BigDecimal total = BigDecimal.ZERO;
			for (OrderShipment shipment : getAllShipments()) {
				if (shipment.getTotal() != null) {
					total = total.add(shipment.getTotal());
				}
			}
			setTotal(total);
		}
	}

	@Override
	public void enableRecalculation() {
		recalculationEnabled = true;
	}

	@Override
	@PreUpdate
	public void disableRecalculation() {
		recalculationEnabled = false;
	}

	/**
	 * @return true if recalculation is enabled and necessary data is loaded, otherwise false
	 */
	@Transient
	protected boolean isRecalculationEnabled() {
		return (recalculationEnabled && getShipments() != null);
	}

	/**
	 * Sets the tax calculation service.
	 *
	 * @param taxCalculationService the tax calculation service
	 */
	public void setTaxCalculationService(final TaxCalculationService taxCalculationService) {
		this.taxCalculationService = taxCalculationService;
	}

	/**
	 * Gets the tax calculation service.
	 *
	 * @return the tax calculation service
	 */
	@Override
	@Transient
	public TaxCalculationService getTaxCalculationService() {
		if (taxCalculationService == null) {
			taxCalculationService = getBean(ContextIdNames.TAX_CALCULATION_SERVICE);
		}
		return taxCalculationService;
	}

	/**
	 * Callback method for property changes on any objects that are being listened to.
	 *
	 * @param event the property change event
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equals("shipmentStatus")) {
			updateStatusFromShipments();
		} else {
			recalculate();
		}
	}

	/**
	 * Update shipment status for Orders that are already in progress.
	 */
	private void updateStatusFromShipments() {
		if (getStatus() == OrderStatus.IN_PROGRESS || getStatus() == OrderStatus.PARTIALLY_SHIPPED
				|| getStatus() == OrderStatus.COMPLETED) {
			int shippedItemCount = 0;
			int cancelledItemCount = 0;
			for (OrderShipment shipment : getAllShipments()) {
				if (shipment.getShipmentStatus() == OrderShipmentStatus.SHIPPED) {
					shippedItemCount++;
				}
				if (shipment.getShipmentStatus() == OrderShipmentStatus.CANCELLED) {
					cancelledItemCount++;
				}
			}
			if (shippedItemCount == getAllShipments().size()) {
				setStatus(OrderStatus.COMPLETED);
			} else if (cancelledItemCount == getAllShipments().size()) {
				setStatus(OrderStatus.CANCELLED);
			} else if ((shippedItemCount + cancelledItemCount) == getAllShipments().size()) {
				setStatus(OrderStatus.COMPLETED);
			} else if (shippedItemCount > 0) {
				setStatus(OrderStatus.PARTIALLY_SHIPPED);
			} else {
				setStatus(OrderStatus.IN_PROGRESS);
			}
		}
	}

	/**
	 * Releases the hold on an order.
	 * This implementation resets the order's status based
	 * on the status of its shipments.
	 * This method should be called by the OrderService only.
	 */
	@Override
	public void releaseHoldOnOrder() {
		//temporarily set to in progress
		setStatus(OrderStatus.IN_PROGRESS);
		//update status based on status of shipments
		updateStatusFromShipments();
	}

	/**
	 * Sets the order status to ONHOLD.
	 * This method should be called by the OrderService only.
	 */
	@Override
	public void holdOrder() {
		setStatus(OrderStatus.ONHOLD);
	}

	/**
	 * Sets the order status to CANCELLED.
	 * Statuses of all shipments associated with this order will be set to CANCELLED.
	 * This method should be called by the OrderService only.
	 */
	@Override
	public void cancelOrder() {
		setStatus(OrderStatus.CANCELLED);
		for (OrderShipment orderShipment : getAllShipments()) {
			orderShipment.setStatus(OrderShipmentStatus.CANCELLED);
		}
	}

	/**
	 * Sets the order status to AWAITING_EXCHANGE.
	 * This method should be called by the OrderService only.
	 */
	@Override
	public void awaitExchnageCompletionOrder() {
		setStatus(OrderStatus.AWAITING_EXCHANGE);
	}

	@Override
	public void failOrder() {
		setStatus(OrderStatus.FAILED);
		for (OrderShipment orderShipment : getAllShipments()) {
			orderShipment.setStatus(OrderShipmentStatus.FAILED_ORDER);
		}
	}

	/**
	 * Get this order's shipment with a given number.
	 * @param shipmentNumber the shipment number to get
	 * @return the shipment with the given number, or null if no shipment with that number is found
	 */
	@Override
	@Transient
	public OrderShipment getShipment(final String shipmentNumber) {
		OrderShipment foundShipment = null;
		for (OrderShipment shipment : getAllShipments()) {
			if (shipment.getShipmentNumber().equals(shipmentNumber)) {
				foundShipment = shipment;
				break;
			}
		}
		if (foundShipment == null) {
			throw new EpDomainException("Shipment number " + shipmentNumber + " was not found in order number " + getOrderNumber());
		}
		return foundShipment;
	}

	@Override
	@Transient
	public Money getTotalItemTaxesMoney() {
		BigDecimal totalItemTaxesAmount = BigDecimal.ZERO.setScale(2);
		List<OrderShipment> orderShipments = getAllShipments();
		for (OrderShipment shipment : orderShipments) {
			if (shipment.getItemTax() != null) {
				totalItemTaxesAmount = totalItemTaxesAmount.add(shipment.getItemTax());
			}
		}

		return MoneyFactory.createMoney(totalItemTaxesAmount, getCurrency());
	}

	@Override
	@Transient
	public Money getTotalShippingTaxesMoney() {
		BigDecimal totalShippingTaxesAmount = BigDecimal.ZERO.setScale(2);
		List<PhysicalOrderShipment> orderShipments = getPhysicalShipments();
		for (PhysicalOrderShipment shipment : orderShipments) {
			totalShippingTaxesAmount = totalShippingTaxesAmount.add(shipment.getShippingTax());
		}

		return MoneyFactory.createMoney(totalShippingTaxesAmount, getCurrency());
	}

	@Override
	@Transient
	public Money getSubtotalMoney() {
		return MoneyFactory.createMoney(getSubtotal(), getCurrency());
	}

	/**
	 * Determines whether or not this order is in a state that allows it
	 * to be cancelled.
	 * @return true if this order can be cancelled, false if not.
	 */
	@Override
	@Transient
	public boolean isCancellable() {
		if (!isHoldable() && getStatus() != OrderStatus.ONHOLD) {
			return false;
		}
		if ((getElectronicShipments() != null) && (getElectronicShipments().size() > 0)) {
			LOG.debug("Can't cancel the order since the order has digital goods");
			return false;
		}
		return true;
	}

	/**
	 * Determines whether or not this order is in a state that allows it
	 * to be put on hold.
	 * @return true if this order can be put on hold, false if not.
	 */
	@Override
	@Transient
	public boolean isHoldable() {
		if (getStatus() == OrderStatus.COMPLETED) {
			LOG.debug("Can't hold or cancel the order since the Order is completed.");
			return false;
		}
		if (getStatus() == OrderStatus.PARTIALLY_SHIPPED) {
			LOG.debug("Can't hold or cancel the order since the Order is partially shipped.");
			return false;
		}
		if (getStatus() == OrderStatus.CANCELLED) {
			LOG.debug("Can't hold or cancel the order since the Order is already cancelled.");
			return false;
		}
		if (getStatus() == OrderStatus.ONHOLD) {
			LOG.debug("Can't hold the order since the Order is already on hold.");
			return false;
		}
		if (getStatus() == OrderStatus.AWAITING_EXCHANGE) {
			LOG.debug("Can't hold the order since the Order is already on awaiting exchange.");
			return false;
		}
		if (getStatus() == OrderStatus.FAILED) {
			LOG.debug("Can't hold the order since the Order is failed.");
			return false;
		}

		return true;
	}

	/**
	 * Determines whether or not this order is the exchange order.
	 * @return the exchnageOrder
	 */
	@Override
	@Basic
	@Column(name = "EXCHANGE_ORDER")
	public Boolean isExchangeOrder() {
		return exchangeOrder;
	}

	/**
	 * Sets exchange order flag.
	 * @param exchangeOrder the exchnageOrder to set
	 */
	@Override
	public void setExchangeOrder(final Boolean exchangeOrder) {
		this.exchangeOrder = exchangeOrder;
	}

	@Override
	@Transient
	public OrderReturn getExchange() {
		if (isExchangeOrder() && exchange == null) {
			ReturnAndExchangeService exchangeService = getBean(ContextIdNames.ORDER_RETURN_SERVICE);
			exchange = exchangeService.getExchange(getUidPk());
		}
		return exchange;
	}

	/**
	 *
	 * @param exchnage the exchnage to set
	 */
	@Override
	public void setExchange(final OrderReturn exchnage) {
		exchange = exchnage;
	}

	/**
	 * Get the discount due to exchange. The amount is calculated as
	 * exchange.total-exchange.refunded. Valid for exchange order only, otherwise return zero.
	 *
	 * @return the due to exchange amount
	 * @throws EpDomainException is exchange order contains more than 1 shipment.
	 */
	@Override
	@Transient
	public BigDecimal getDueToRMA() {
		BigDecimal dueToRMA = BigDecimal.ZERO;
		if (isExchangeOrder() && getExchange() != null) {
			if (getAllShipments() != null && getAllShipments().size() == 1) {
				getExchange().recalculateOrderReturn();
				dueToRMA = getExchange().getReturnTotal().subtract(getExchange().getRefundedTotal());
			} else {
				//TODO: currently EO can not be recalculated is there are multiple shipments.
				//In this case it's unclear now how to redistribute(subtract) exchange.total between multiple shipments.
				throw new EpDomainException("Exchange order can't have more than 1 shipment.");
			}
		}
		return dueToRMA;
	}

	@Override
	@Transient
	public Money getDueToRMAMoney() {
		return MoneyFactory.createMoney(getDueToRMA(), getCurrency());
	}

	private BigDecimal getExchangeOrderAmount(final BigDecimal regularAmount) {
		//reduce money to be authorized on return's amount
		BigDecimal amount = regularAmount;
		if (isExchangeOrder()) {
			getExchange().recalculateOrderReturn();
			final BigDecimal returnTotal = getExchange().getReturnTotal();
			final BigDecimal exchangeOrderAmount = regularAmount.subtract(returnTotal);
			if (exchangeOrderAmount.compareTo(BigDecimal.ZERO) >= 0) {
				amount = exchangeOrderAmount;
			} else {
				amount = BigDecimal.ZERO;
			}
		}

		return amount;
	}

	/**
	 * Refunds should be possible only if there is order shipment that has been shipped which will mean that
	 * payment has been received.
	 *
	 * @return true if the order is applicable for a refund
	 */
	@Override
	@Transient
	public boolean isRefundable() {
		for (OrderShipment shipment : getAllShipments()) {
			if (shipment.getShipmentStatus() == OrderShipmentStatus.SHIPPED) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the total before tax money.
	 *
	 * @return the total minus the total tax
	 */
	@Override
	@Transient
	public Money getTotalBeforeTaxMoney() {
		return getTotalMoney().subtract(getTotalTaxMoney());
	}

	@Override
	@Transient
	public Collection< ? extends ShoppingItem> getRootShoppingItems() {
		Set<OrderSku> skus = new HashSet<OrderSku>();
		for (OrderShipment shipment : getAllShipments()) {
			for (OrderSku orderSku : shipment.getShipmentOrderSkus()) {
				OrderSku root = orderSku.getRoot();
				if (root == null) {
					skus.add(orderSku);
				} else if (!skus.contains(root)) {
					skus.add(root);
				}
			}
		}
		return Collections.unmodifiableSet(skus);
	}

	@Override
	@Transient
	public Collection< ? extends ShoppingItem> getLeafShoppingItems() {
		Set<OrderSku> skus = new HashSet<OrderSku>();
		for (OrderShipment shipment : getAllShipments()) {
			for (OrderSku orderSku : shipment.getShipmentOrderSkus()) {
				skus.add(orderSku);
			}
		}
		return Collections.unmodifiableSet(skus);
	}

	@Override
	public void registerPropertyListeners() {
		if (getShipments() != null) {
			for (OrderShipment shipment : getShipments()) {
				((ListenableObject) shipment).addPropertyChangeListener("total", this, true);
				((ListenableObject) shipment).addPropertyChangeListener("shipmentStatus", this, true);
				((ListenableProperties) shipment).registerPropertyListeners();
				((PostLoadRecalculate) shipment).recalculateAfterLoad();
			}
		}
	}

	@Override
	public Set<ShipmentType> getShipmentTypes() {
		Set<ShipmentType> results = new HashSet<ShipmentType>();

		if (!getElectronicShipments().isEmpty()) {
			results.add(ShipmentType.ELECTRONIC);
		}

		if (!getPhysicalShipments().isEmpty()) {
			results.add(ShipmentType.PHYSICAL);
		}

		if (!getServiceShipments().isEmpty()) {
			results.add(ShipmentType.SERVICE);
		}

		return results;
	}

	/**
	 * Sets the {@code OrderData} - for JPA.
	 * @param orderData the order data
	 */
	protected void setOrderDataInternal(final Map<String, OrderData> orderData) {
		this.orderData = orderData;
	}

	/**
	 * Internal JPA method to get Order Data.
	 * @return the order data
	 */
	@OneToMany(targetEntity = OrderDataImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@MapKey(name = "key")
	@ElementJoinColumn(name = "ORDER_UID", nullable = false)
	@ElementForeignKey(name = "FK_TORDERDATA_ORDER_UID")
	@ElementDependent
	protected Map<String, OrderData> getOrderDataInternal() {
		return this.orderData;
	}

	@Override
	@Transient
	public String getFieldValue(final String key) {
		return getFieldValues().get(key);
	}

	@Override
	public void setFieldValue(final String name, final String value) {
		OrderData datum = getOrderDataInternal().get(name);
		if (datum == null) {
			datum = createOrderDatum(name, value);
			getOrderDataInternal().put(name, datum);
		} else {
			datum.setValue(value);
		}
	}

	@Override
	public void removeFieldValue(final String propertyKey) {
		getOrderDataInternal().remove(propertyKey);
	}

	/**
	 * Factory method for OrderData instances.  Override this method in an extension class if you need to change
	 * the OrderData implementation class.
	 *
	 * @param name the data's key
	 * @param value the data's value
	 *
	 * @return A newly constructed OrderData object with the given key and value
	 */
	protected OrderData createOrderDatum(final String name, final String value) {
		return new OrderDataImpl(name, value);
	}

	@Transient
	@Override
	public Map<String, String> getFieldValues() {
		HashMap<String, String> fieldValues = new HashMap<String, String>(getOrderDataInternal().size());
		for (OrderData data : getOrderDataInternal().values()) {
			fieldValues.put(data.getKey(), data.getValue());
		}

		return Collections.unmodifiableMap(fieldValues);
	}
}
