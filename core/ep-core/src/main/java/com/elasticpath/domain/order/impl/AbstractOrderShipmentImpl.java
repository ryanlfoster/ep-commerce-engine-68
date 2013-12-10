/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
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
import org.apache.openjpa.persistence.jdbc.Index;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.PostLoadRecalculate;
import com.elasticpath.domain.RecalculableObject;
import com.elasticpath.domain.impl.AbstractListenableValueObjectImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.OrderTaxValue;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * <code>AbstractOrderShipmentImpl</code> represents a customer's order shipment.
 */
@Entity
@Table(name = AbstractOrderShipmentImpl.TABLE_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, length = GlobalConstants.SHORT_TEXT_MAX_LENGTH)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.ORDER_INDEX, attributes = {
			@FetchAttribute(name = "shipmentOrderSkusInternal"),
			@FetchAttribute(name = "orderInternal"),
			@FetchAttribute(name = "status") }),
	@FetchGroup(name = FetchGroupConstants.ORDER_DEFAULT, 
	postLoad = true),
	@FetchGroup(name = FetchGroupConstants.ORDER_SEARCH, attributes = {
			@FetchAttribute(name = "shipmentOrderSkusInternal"),
			@FetchAttribute(name = "status") })
})


@DataCache(enabled = false)
@SuppressWarnings({ "PMD.ExcessiveImports" })
public abstract class AbstractOrderShipmentImpl extends AbstractListenableValueObjectImpl implements OrderShipment, PropertyChangeListener,
		RecalculableObject, ListenableProperties, PostLoadRecalculate, DatabaseLastModifiedDate {

	private static final long serialVersionUID = 5000000001L;

	private static final Logger LOG = Logger.getLogger(AbstractOrderShipmentImpl.class);

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TORDERSHIPMENT";

	private Order order;

	private Date createdDate;

	private Date lastModifiedDate;

	private Date shipmentDate;

	/** By default, newly constructed Shipments will have an ONHOLD status unless told otherwise. */
	private OrderShipmentStatus status = OrderShipmentStatus.ONHOLD;

	private Set<OrderSku> shipmentOrderSkus = new HashSet<OrderSku>();

	private long uidPk;

	private BigDecimal itemSubtotal;

	private BigDecimal itemTax;

	private BigDecimal subtotalDiscount;

	private BigDecimal total;

	private BigDecimal subtotal;

	private Money totalBeforeTaxMoney;

	private boolean inclusiveTax;

	private Set<OrderTaxValue> shipmentTaxes = new HashSet<OrderTaxValue>();

	private boolean recalculationEnabled = false;

	private String shipmentNumber;

	/**
	 * Must be implemented by subclasses to return their type. (e.g. electronic or physical)
	 *
	 * @return the type of the order shipment subclass.
	 */
	@Override
	@Transient
	public abstract ShipmentType getOrderShipmentType();

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SHIPMENT_DATE")
	public Date getShipmentDate() {
		return shipmentDate;
	}

	/**
	 * This method checks if the tax recalculation using Live tax rates is required or not based on the shipment status. <br>
	 * Currently the tax recalculation is not required if the shipment status is in the list {OrderShipmentStatus.RELEASED,
	 * OrderShipmentStatus.SHIPPED, OrderShipmentStatus.CANCELLED}
	 *
	 * @return true if the tax recalculation is required else false
	 */
	@Transient
	protected boolean isTaxRecalculationRequired() {
		boolean shouldRecalculate = true;
		OrderShipmentStatus shipmentStatus = getShipmentStatus();
		if (shipmentStatus == OrderShipmentStatus.CANCELLED || shipmentStatus == OrderShipmentStatus.SHIPPED
				|| shipmentStatus == OrderShipmentStatus.RELEASED) {
			shouldRecalculate = false;
		}
		return shouldRecalculate;
	}

	@Override
	public void setShipmentDate(final Date shipmentDate) {
		this.shipmentDate = shipmentDate;
	}

	@Override
	@Transient
	public OrderShipmentStatus getShipmentStatus() {
		if (getOrder() == null) {
			return getStatus();
		} else if (getStatus() == OrderShipmentStatus.CANCELLED) {
			return getStatus();
		} else if (getOrder().getStatus() == OrderStatus.ONHOLD) {
			return OrderShipmentStatus.ONHOLD;
		} else if (getOrder().getStatus() == OrderStatus.CANCELLED) {
			return OrderShipmentStatus.CANCELLED;
		}
		return getStatus();
	}

	/**
	 * Private methods for persisting the status of the shipment independent of the parent Order.
	 *
	 * @return the shipment status
	 */
	@Persistent(optional = false)
	@Column(name = "STATUS")
	@Externalizer("getName")
	@Factory("valueOf")
	private OrderShipmentStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(final OrderShipmentStatus status) {
		OrderShipmentStatus oldStatus = this.status;
		this.status = status;
		firePropertyChange("shipmentStatus", oldStatus, status);
	}

	/**
	 * Get the SKUs in this shipment.
	 *
	 * @return the shipment's <code>OrderSku</code>s
	 */
	@OneToMany(targetEntity = OrderSkuImpl.class, cascade = { CascadeType.ALL }, mappedBy = "shipment", fetch = FetchType.EAGER)
	@ElementDependent
	protected Set<OrderSku> getShipmentOrderSkusInternal() {
		return shipmentOrderSkus;
	}

	@Override
	@Transient
	public Set<OrderSku> getShipmentOrderSkus() {
		Set<OrderSku> shipmentOrderSkus = new HashSet<OrderSku>();
		for (OrderSku orderSku : getShipmentOrderSkusInternal()) {
			shipmentOrderSkus.add(orderSku);
		}
		return Collections.unmodifiableSet(shipmentOrderSkus);
	}

	/**
	 * Internal method used by JPA.
	 *
	 * @param shipmentOrderSkus the shipment order skus.
	 */
	protected void setShipmentOrderSkusInternal(final Set<OrderSku> shipmentOrderSkus) {
		this.shipmentOrderSkus = shipmentOrderSkus;
	}

	@Override
	public void addShipmentOrderSku(final OrderSku shipmentOrderSku) {
		shipmentOrderSku.setShipment(this);
		getShipmentOrderSkusInternal().add(shipmentOrderSku);
		addSkuListeners(shipmentOrderSku);
		((RecalculableObject) shipmentOrderSku).enableRecalculation();

		// We shouldn't need to call recalculate directly here because we listen to the order sku for changes, and
		// it recalculates when enableRecalculation is called, which causes it to set the amount and fire a
		// property change for the amount.
		updateOrderShipmentStatus();
	}

	@Override
	public void removeShipmentOrderSku(final OrderSku shipmentOrderSku) {
		if (!getShipmentOrderSkusInternal().remove(shipmentOrderSku)) {
			LOG.error("Attempted to remove an unknown ordersku from a shipment: " + toString());
		}
		if (shipmentOrderSku.getParent() != null) {
			shipmentOrderSku.getParent().removeChildItem(shipmentOrderSku);
		}
		shipmentOrderSku.removePropertyChangeListener(this);
		recalculate();
		updateOrderShipmentStatus();
	}

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Gets the item subtotal for this order shipment.
	 * <p>
	 * Note: This method should be internal for the implementation once the deprecated method is removed from the interface.
	 *
	 * @return BigDecimal
	 */
	@Override
	@Basic
	@Column(name = "ITEM_SUBTOTAL", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getItemSubtotal() {
		return itemSubtotal;
	}

	@Override
	@Basic
	@Column(name = "ITEM_TAX", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getItemTax() {
		return itemTax;
	}

	/**
	 * Internal method for the persistence layer to gets the subtotal discount for this order shipment.
	 *
	 * @return BigDecimal
	 */
	@Basic
	@Column(name = "SUBTOTAL_DISCOUNT", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getSubtotalDiscountInternal() {
		return subtotalDiscount;
	}

	/**
	 * Internal method for the persistence layer to set the subtotal discount.
	 *
	 * @param subtotalDiscount BigDecimal
	 */
	public void setSubtotalDiscountInternal(final BigDecimal subtotalDiscount) {
		this.subtotalDiscount = subtotalDiscount;
	}

	@Override
	@Transient
	public BigDecimal getSubtotalDiscount() {
		if (getSubtotalDiscountInternal() == null) {
			setSubtotalDiscountInternal(BigDecimal.ZERO.setScale(2));
		}
		return getSubtotalDiscountInternal();
	}

	@Override
	public void setSubtotalDiscount(final BigDecimal subtotalDiscount) {
		setSubtotalDiscountInternal(subtotalDiscount);
		recalculate();
	}

	@Override
	@Transient
	public Money getSubtotalDiscountMoney() {
		return MoneyFactory.createMoney(getSubtotalDiscount(), getOrder().getCurrency());
	}

	@Override
	@Transient
	public boolean hasSubtotalDiscount() {
		return getSubtotalDiscount() != null && BigDecimal.ZERO.compareTo(getSubtotalDiscount()) != 0;
	}

	@Override
	@Basic
	@Column(name = "INCLUSIVE_TAX")
	public boolean isInclusiveTax() {
		return inclusiveTax;
	}

	@Override
	public void setInclusiveTax(final boolean inclusiveTax) {
		this.inclusiveTax = inclusiveTax;
	}

	/**
	 * Sets the item subtotal amount.
	 *
	 * @param itemSubtotal BigDecimal
	 */

	// ---- DOCsetItemSubtotal
	protected void setItemSubtotal(final BigDecimal itemSubtotal) {
		BigDecimal oldItemSubtotal = this.itemSubtotal;
		this.itemSubtotal = itemSubtotal;
		firePropertyChange("itemSubtotal", oldItemSubtotal, itemSubtotal); //$NON-NLS-1$
	}

	// ---- DOCsetItemSubtotal

	/**
	 * Sets the item tax.
	 *
	 * @param itemTax BigDecimal
	 */
	protected void setItemTax(final BigDecimal itemTax) {
		BigDecimal oldItemTax = this.itemTax;
		this.itemTax = itemTax;
		firePropertyChange("itemTax", oldItemTax, itemTax); //$NON-NLS-1$
	}

	/**
	 * @return the order that this shipment is part of
	 */
	@ManyToOne(targetEntity = OrderImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "ORDER_UID", nullable = false)
	@ForeignKey(name = "TORDERSHIPMENT_IBFK_2")
	protected Order getOrderInternal() {
		return order;
	}

	/**
	 * Get the order this shipment belongs to.
	 *
	 * @return the order that this shipment is part of
	 */
	@Override
	@Transient
	public Order getOrder() {
		return getOrderInternal();
	}

	protected void setOrderInternal(final Order order) {
		this.order = order;
	}

	@Override
	public void setOrder(final Order order) {
		setOrderInternal(order);
		recalculate();
	}

	@Override
	@Transient
	public BigDecimal getTotal() {
		return total;
	}

	/**
	 * Sets the total cost of this order shipment.
	 *
	 * @param total the total
	 */
	protected void setTotal(final BigDecimal total) {
		BigDecimal oldTotal = this.total;
		this.total = total;
		firePropertyChange("total", oldTotal, total); //$NON-NLS-1$
	}

	@Override
	@Transient
	public BigDecimal getSubtotal() {
		return subtotal;
	}

	/**
	 * Sets the subtotal for this order shipment.
	 *
	 * @param subtotal the subtotal
	 */
	protected void setSubtotal(final BigDecimal subtotal) {
		BigDecimal oldSubtotal = this.subtotal;
		this.subtotal = subtotal;
		firePropertyChange("subtotal", oldSubtotal, subtotal); //$NON-NLS-1$
	}

	/**
	 * Gets the total less the tax for this shipment.
	 *
	 * @return the shipment total less the shipment tax
	 */
	@Override
	@Transient
	public Money getTotalBeforeTaxMoney() {
		return totalBeforeTaxMoney;
	}

	/**
	 * Sets the totalBeforeTaxMoney for this orderShipment.
	 *
	 * @param totalBeforeTaxMoney the total less the total tax
	 */
	protected void setTotalBeforeTaxMoney(final Money totalBeforeTaxMoney) {
		Money oldTotalBeforeTaxMoney = this.totalBeforeTaxMoney;
		this.totalBeforeTaxMoney = totalBeforeTaxMoney;
		firePropertyChange("totalBeforeTaxMoney", oldTotalBeforeTaxMoney, totalBeforeTaxMoney); //$NON-NLS-1$
	}

	@Override
	@OneToMany(targetEntity = OrderTaxValueImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "ORDER_SHIPMENT_UID", nullable = false)
	@ElementForeignKey
	@ElementDependent
	public Set<OrderTaxValue> getShipmentTaxes() {
		return shipmentTaxes;
	}

	/**
	 * Set the set of <code>OrderTaxValue</code>s.
	 *
	 * @param shipmentTaxes - set of <code>OrderTaxValue</code>s.
	 */
	protected void setShipmentTaxes(final Set<OrderTaxValue> shipmentTaxes) {
		this.shipmentTaxes = shipmentTaxes;
	}

	/**
	 * Must be overridden in subclasses to recalculate taxes and totals when modifications are made to the linked object.
	 */
	protected abstract void recalculate();

	@Override
	@Transient
	public void enableRecalculation() {
		recalculationEnabled = true;
	}

	@Override
	public void disableRecalculation() {
		recalculationEnabled = false;
	}

	/**
	 * @return true if recalculation is enabled and necessary data is loaded, otherwise false
	 */
	@Transient
	protected boolean isRecalculationEnabled() {
		return (recalculationEnabled && getOrder() != null && getShipmentOrderSkus() != null);
	}

	/**
	 * Callback method for JPA so that recalculation is done before persisting.
	 */
	@PreUpdate
	@PrePersist
	protected void recalculateBeforePersist() {
		enableRecalculation();
		recalculate();
		disableRecalculation();
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equals("orderInventoryAllocation")) {
			updateOrderShipmentStatus();
		} else {
			recalculate();
		}
	}

	/**
	 * Status of a shipment can be changed depending on changes in inventory allocation to its Order skus.
	 */
	private void updateOrderShipmentStatus() {
		if (getStatus() == OrderShipmentStatus.AWAITING_INVENTORY || getStatus() == OrderShipmentStatus.INVENTORY_ASSIGNED) {

			OrderShipmentStatus status = OrderShipmentStatus.INVENTORY_ASSIGNED;
			for (OrderSku sku : getShipmentOrderSkus()) {
				if (!sku.isAllocated()) {
					status = OrderShipmentStatus.AWAITING_INVENTORY;
				}
			}
			setStatus(status);
		}
	}

	@Override
	@Basic
	@Index(name = "I_OSHIP_SHIPMENT_NUMBER", unique = true)
	@Column(name = "SHIPMENT_NUMBER", nullable = true)
	public String getShipmentNumber() {
		return shipmentNumber;
	}

	@Override
	public void setShipmentNumber(final String shipmentNumber) {
		this.shipmentNumber = shipmentNumber;
	}

	@Override
	@Transient
	public Money getItemTaxMoney() {
		return MoneyFactory.createMoney(getItemTax(), getOrder().getCurrency());
	}

	@Override
	@Transient
	public Money getItemSubtotalMoney() {
		return MoneyFactory.createMoney(getItemSubtotal(), getOrder().getCurrency());
	}

	@Override
	@Transient
	public Money getTotalMoney() {
		return MoneyFactory.createMoney(getTotal(), getOrder().getCurrency());
	}

	@Override
	@Transient
	public boolean isReadyForFundsCapture() {
		boolean isReleasedForShipping = OrderShipmentStatus.RELEASED == getShipmentStatus();
		if (isReleasedForShipping) {
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated
	 */
	@Override
	@Deprecated
	@Transient
	public Money getItemSubTotalBeforeTaxMoney() {
		return getSubtotalBeforeTaxMoney();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated
	 */
	@Override
	@Deprecated
	@Transient
	public BigDecimal getItemSubTotalBeforeTax() {
		return getSubtotalBeforeTax();
	}

	/**
	 * Update the shipment's tax values. This attempts to update the amount if a tax value for the corresponding tax category is already present,
	 * otherwise it will create a new one. If we just create new ones all the time then JPA will get confused (since this block of code gets run
	 * within a transaction sometimes) and will throw a ConcurrentModificationException.
	 *
	 * @param taxResult the result of a call to the tax calculation service
	 */
	protected void updateTaxValues(final TaxCalculationResult taxResult) {
		Set<OrderTaxValue> shipmentTaxes = getShipmentTaxes();
		// if taxes value is null this means that the object is in an intermediate state
		// of transmission and does not have the taxes set by the (de)serializer yet
		if (shipmentTaxes == null) {
			return;
		}
		Set<String> taxCategoryNames = new HashSet<String>();
		for (TaxCategory taxCategory : taxResult.getTaxCategoriesSet()) {
			OrderTaxValue correspondingTaxValue = null;

			for (OrderTaxValue taxValue : shipmentTaxes) {
				if (taxValue.getTaxCategoryName().equals(taxCategory.getName())) {
					correspondingTaxValue = taxValue;
				}
			}
			if (correspondingTaxValue == null) {
				OrderTaxValue orderTaxValue = getBean(ContextIdNames.ORDER_TAX_VALUE);
				orderTaxValue.setTaxCategoryName(taxCategory.getName());
				orderTaxValue.setTaxCategoryDisplayName(taxCategory.getDisplayName(getStore(getOrder()).getDefaultLocale()));
				orderTaxValue.setTaxValue(taxResult.getTaxValue(taxCategory).getAmount());
				shipmentTaxes.add(orderTaxValue);
			} else {
				correspondingTaxValue.setTaxValue(taxResult.getTaxValue(taxCategory).getAmount());
			}
			taxCategoryNames.add(taxCategory.getName());
		}

		// Remove any old tax values that don't apply anymore
		for (Iterator<OrderTaxValue> taxValueIter = shipmentTaxes.iterator(); taxValueIter.hasNext();) {
			if (!taxCategoryNames.contains(taxValueIter.next().getTaxCategoryName())) {
				taxValueIter.remove();
			}
		}
	}

	/**
	 * Returns the store associated with the given order.  Checks order.getStore() first, as we'd prefer not to make
	 * service calls from domain objects if we don't absolutely have to!
	 *
	 * @param order the order
	 * @return the order's store
	 */
	@SuppressWarnings("deprecation")
	private Store getStore(final Order order) {
		if (order.getStore() != null) {
			return order.getStore();
		}

		StoreService storeService = getBean(ContextIdNames.STORE_SERVICE);
		return storeService.findStoreWithCode(order.getStoreCode());
	}

	@Override
	@Transient
	public Money getSubtotalBeforeTaxMoney() {
		return MoneyFactory.createMoney(getSubtotalBeforeTax(), getOrder().getCurrency());
	}

	@Override
	@Transient
	public Money getSubtotalMoney() {
		return MoneyFactory.createMoney(getSubtotal(), getOrder().getCurrency());
	}

	/**
	 * Gets the items before tax subtotal minus the subtotal discount.
	 *
	 * @return the before tax subtotal
	 */
	@Override
	@Transient
	public BigDecimal getSubtotalBeforeTax() {
		if (isInclusiveTax()) {
			return getSubtotal().subtract(getItemTax());
		}
		return getSubtotal();
	}

	@Override
	@PostPersist
	public void recalculateAfterLoad() {
		registerPropertyListeners();
		enableRecalculation();
		recalculateTransientDerivedValues();
	}

	@Override
	public void registerPropertyListeners() {
		if (shipmentOrderSkus != null) {
			for (OrderSku orderSku : shipmentOrderSkus) {
				addSkuListeners(orderSku);
			}
		}
	}

	/**
	 * Add this as a listeners to property changes of the given sku.
	 *
	 * @param orderSku the sku to listen to
	 */
	protected void addSkuListeners(final OrderSku orderSku) {
		orderSku.addPropertyChangeListener("amount", this, true);
		orderSku.addPropertyChangeListener("orderInventoryAllocation", this, true);
	}

	/**
	 * This method recalculates values which are derived from persistent values. It does the recalculation from data held by this object and does not
	 * go outside to get other information.
	 */
	protected abstract void recalculateTransientDerivedValues();

	/**
	 * Perform recalculation of transient values and other calculated values after the object has been changed and persisted. <br>
	 * It is currently necessary to do the additional recalculation because we have a bug around correctly persisting OrderSku values. <br>
	 * Once that is fixed this can just recalculateTransientDerivedValues as in recalculateAfterLoad()
	 */
	@PostUpdate
	protected void recalculateAfterUpdate() {
		registerPropertyListeners();
		enableRecalculation();
		recalculate();
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof AbstractOrderShipmentImpl)) {
			return false;
		}

		AbstractOrderShipmentImpl shipment = (AbstractOrderShipmentImpl) other;
		return ObjectUtils.equals(shipmentNumber, shipment.shipmentNumber);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(shipmentNumber);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("createdDate", getCreatedDate())
				.append("lastModifiedDate", getLastModifiedDate())
				.append("orderShipmentType", getOrderShipmentType())
				.append("shipmentDate", getShipmentDate())
				.append("status", getStatus())
				.append("itemSubtotal", getItemSubtotal())
				.append("itemTax", getItemTax())
				.append("inclusiveTax", isInclusiveTax())
				.toString();
	}

}
