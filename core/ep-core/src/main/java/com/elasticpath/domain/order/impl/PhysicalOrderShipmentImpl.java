/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.order.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;

/**
 * <code>PhysicalOrderShipmentImpl</code> represents a customer's order shipment for shippable goods.
 */
@Entity
@DiscriminatorValue(ShipmentType.PHYSICAL_STRING)
@FetchGroups({
		@FetchGroup(name = FetchGroupConstants.ORDER_INDEX, attributes = { @FetchAttribute(name = "shipmentAddressInternal") }),
		@FetchGroup(name = FetchGroupConstants.ORDER_DEFAULT, attributes = { @FetchAttribute(name = "shipmentAddressInternal"),
				@FetchAttribute(name = "shippingServiceLevelGuidInternal") }, fetchGroups = { FetchGroupConstants.DEFAULT }, postLoad = true) })
@DataCache(enabled = false)
public class PhysicalOrderShipmentImpl extends AbstractOrderShipmentImpl implements PhysicalOrderShipment {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private static final Logger LOG = Logger.getLogger(PhysicalOrderShipmentImpl.class);

	private String carrier;

	private String serviceLevel;

	private String trackingCode;

	private BigDecimal shippingCost = BigDecimal.ZERO.setScale(DECIMAL_SCALE);

	private BigDecimal beforeTaxShippingCost;

	private BigDecimal shippingTax;

	private OrderAddress shipmentAddress;

	private BigDecimal shippingSubtotal;

	private String shippingServiceLevelGuid;

	private final Set<OrderSku> shipmentRemovedOrderSkus = new HashSet<OrderSku>();

	/**
	 * Must be implemented by subclasses to return their type. (e.g. electronic or physical)
	 *
	 * @return the type of the order shipment subclass.
	 */
	@Override
	@Transient
	public ShipmentType getOrderShipmentType() {
		return ShipmentType.PHYSICAL;
	}

	/**
	 * Internal method for persistence layer use to get the shipping cost.
	 *
	 * @return the shipping cost in <code>BigDecimal</code>.
	 */
	@Basic
	@Column(name = "SHIPPING_COST", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	protected BigDecimal getShippingCostInternal() {
		return shippingCost;
	}

	/**
	 * Internal method for the persistence layer to set the shipping cost.
	 *
	 * @param shippingCost the shipping cost
	 */
	protected void setShippingCostInternal(final BigDecimal shippingCost) {
		this.shippingCost = shippingCost;
	}

	@Override
	@Transient
	public BigDecimal getShippingCost() {
		final BigDecimal shippingCostInt = getShippingCostInternal();
		if (shippingCostInt == null) {
			return BigDecimal.ZERO;
		}
		return shippingCostInt;
	}

	@Override
	public void setShippingCost(final BigDecimal shippingCost) {
		BigDecimal oldShippingCost = getShippingCost();
		setShippingCostInternal(shippingCost);
		if (isRecalculationEnabled()) {
			firePropertyChange("shippingCost", oldShippingCost, shippingCost); //$NON-NLS-1$
			recalculate();
		}
	}

	@Override
	@Transient
	public Money getShippingCostMoney() {
		return MoneyFactory.createMoney(getShippingCost(), getOrder().getCurrency());
	}

	@Override
	@Transient
	public Money getBeforeTaxShippingCostMoney() {
		return MoneyFactory.createMoney(getBeforeTaxShippingCost(), getOrder().getCurrency());
	}

	@Override
	@Basic
	@Column(name = "BEFORE_TAX_SHIPPING_COST", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getBeforeTaxShippingCost() {
		return beforeTaxShippingCost;
	}

	@Override
	public void setBeforeTaxShippingCost(final BigDecimal beforeTaxShippingCost) {
		this.beforeTaxShippingCost = beforeTaxShippingCost;
	}

	@Override
	@Basic
	@Column(name = "SHIPPING_TAX", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getShippingTax() {
		return shippingTax;
	}

	@Override
	public void setShippingSubtotal(final BigDecimal shippingSubtotal) {
		this.shippingSubtotal = shippingSubtotal;
	}

	@Override
	@Basic
	@Column(name = "SHIPPING_SUBTOTAL", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getShippingSubtotal() {
		return shippingSubtotal;
	}

	/**
	 * Set the shipping tax in <code>BigDecimal</code>.
	 *
	 * @param shippingTax the shipping tax
	 */
	protected void setShippingTax(final BigDecimal shippingTax) {
		BigDecimal oldShippingTax = this.shippingTax;
		this.shippingTax = shippingTax;
		firePropertyChange("shippingTax", oldShippingTax, shippingTax); //$NON-NLS-1$
	}

	/**
	 * Get the shipment address corresponding to this shipment.
	 *
	 * @return the shipment address Uid
	 */
	@ManyToOne(targetEntity = OrderAddressImpl.class, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ORDER_ADDRESS_UID")
	@ForeignKey(name = "TORDERSHIPMENT_IBFK_1")
	protected OrderAddress getShipmentAddressInternal() {
		return shipmentAddress;
	}

	/**
	 * Set the shipping address corresponding to this shipment.
	 *
	 * @param shipmentAddress the Uid of the corresponding shipment address.
	 */
	protected void setShipmentAddressInternal(final OrderAddress shipmentAddress) {
		this.shipmentAddress = shipmentAddress;
		// FIXME change the carrier from the list of available ones as well? But not in this method as it is used by JPA!
	}

	@Override
	@Transient
	public OrderAddress getShipmentAddress() {
		return getShipmentAddressInternal();
	}

	@Override
	public void setShipmentAddress(final OrderAddress shipmentAddress) {
		setShipmentAddressInternal(shipmentAddress);
		recalculate();
	}

	@Override
	@Basic
	@Column(name = "CARRIER")
	public String getCarrier() {
		return carrier;
	}

	@Override
	public void setCarrier(final String carrier) {
		this.carrier = carrier;
	}

	@Override
	@Basic
	@Column(name = "SERVICE_LEVEL")
	public String getServiceLevel() {
		return serviceLevel;
	}

	@Override
	public void setServiceLevel(final String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	@Override
	@Basic
	@Column(name = "TRACKING_CODE")
	public String getTrackingCode() {
		return trackingCode;
	}

	@Override
	public void setTrackingCode(final String trackingCode) {
		this.trackingCode = trackingCode;
	}

	@Basic
	@Column(name = "SERVICE_LEVEL_GUID", nullable = true)
	protected String getShippingServiceLevelGuidInternal() {
		return shippingServiceLevelGuid;
	}

	protected void setShippingServiceLevelGuidInternal(final String shippingServiceLevelGuid) {
		this.shippingServiceLevelGuid = shippingServiceLevelGuid;
	}

	@Transient
	@Override
	public String getShippingServiceLevelGuid() {
		return getShippingServiceLevelGuidInternal();
	}

	@Override
	public void setShippingServiceLevelGuid(final String shippingServiceLevelGuid) {
		setShippingServiceLevelGuidInternal(shippingServiceLevelGuid);
		recalculateShippingCost();
	}

	/**
	 * Updates all calculated values in the linked shipment (taxes, subtotals, etc.).
	 */
	@Override
	protected void recalculate() {
		if (isRecalculationEnabled()) {
			synchronized (this) {
				recalculateTaxesUsingLiveTaxRates();
			}
		}
	}

	/**
	 * Recalculate the tax amounts using the tax calculation service. <br>
	 * If this is an existing order it is possible that the tax rates have changed since the order was placed, which will change the tax values and
	 * the total prices for this order. <br>
	 * Thus, this method should only be called after a change which affects the price of the order.
	 */
	private void recalculateTaxesUsingLiveTaxRates() {
		if (isTaxRecalculationRequired()) {
			final TaxCalculationResult taxResult = calculateTaxes();

			taxResult.applyTaxes(getShipmentOrderSkus());

			setInclusiveTax(taxResult.isTaxInclusive());
			setItemTax(taxResult.getTotalItemTax().getAmount());
			setShippingTax(taxResult.getShippingTax().getAmount());

			updateTaxValues(taxResult);

			final Money beforeTaxShippingCostMoney = taxResult.getBeforeTaxShippingCost();
			final BigDecimal beforeTaxShippingCost = beforeTaxShippingCostMoney.getAmount();
			setBeforeTaxShippingCost(beforeTaxShippingCost);

			final BigDecimal itemSubtotal = taxResult.getSubtotal().getAmount();
			setItemSubtotal(itemSubtotal);
		}
		recalculateTransientDerivedValues();
	}

	/**
	 * This method recalculates values which are derived from persistent values. <br>
	 * It does the recalculation from data held by this object and does not go outside to get other information.
	 */
	@Override
	protected void recalculateTransientDerivedValues() {
		if (isRecalculationEnabled()) {
			// They are here rather than in the getters so we can fire a property change on when the setter is called.
			setSubtotal(getItemSubtotal());
			if (isInclusiveTax()) {
				setTotal(getSubtotal().subtract(getSubtotalDiscount()).add(getShippingCost()));
			} else {
				setTotal(getSubtotal().subtract(getSubtotalDiscount()).add(getShippingCost()).add(getItemTax()).add(getShippingTax()));
			}
			Money totalBeforeTaxMoney = MoneyFactory.createMoney(
					getItemSubTotalBeforeTaxMoney().getAmount().add(getBeforeTaxShippingCost()).subtract(getSubtotalDiscount()),
					getOrder().getCurrency());
			setTotalBeforeTaxMoney(totalBeforeTaxMoney);

		}
	}

	/**
	 * Calculate the taxes for the order.
	 *
	 * @return the tax result
	 */
	protected TaxCalculationResult calculateTaxes() {
		Order order = getOrder();
		TaxCalculationService taxCalculationService = order.getTaxCalculationService();
		TaxCalculationResult taxResult = taxCalculationService.calculateTaxes(order.getStoreCode(),
				getShipmentAddress(),
				getOrder().getCurrency(),
				getShippingCostMoney(),
				getShipmentOrderSkus(),
				getSubtotalDiscountMoney());
		return taxResult;
	}

	private void recalculateShippingCost() {
		if (isRecalculationEnabled()) {
			final ShippingServiceLevel serviceLevel = getShippingServiceLevelService().findByGuid(getShippingServiceLevelGuid());
			final Money shippingCost = serviceLevel.calculateRegularPriceShippingCost(getShipmentOrderSkus(), getOrder().getCurrency());
			setShippingCost(shippingCost.getAmount());
		}
	}

	@Override
	@Transient
	public Money getTotalTaxMoney() {
		BigDecimal totalTax = BigDecimal.ZERO.setScale(2);
		if (getItemTax() != null) {
			totalTax = totalTax.add(getItemTax());
		}
		if (getShippingTax() != null) {
			totalTax = totalTax.add(getShippingTax());
		}

		return MoneyFactory.createMoney(totalTax, getOrder().getCurrency());
	}

	@Override
	@Transient
	public Set<OrderSku> getShipmentRemovedOrderSku() {
		return shipmentRemovedOrderSkus;
	}

	/**
	 * Remove shipment order sku from the shipment, and add it to removedShipmentOrderSku set.
	 *
	 * @param orderSku orderSku to be deleted
	 */
	@Override
	public void removeShipmentOrderSku(final OrderSku orderSku) {
		super.removeShipmentOrderSku(orderSku);
		OrderSku removedOrderSku = getBean(ContextIdNames.ORDER_SKU);
		removedOrderSku.copyFrom(orderSku, false);
		shipmentRemovedOrderSkus.add(removedOrderSku);
	}

	@Override
	@Transient
	public Money getShippingTaxMoney() {
		return MoneyFactory.createMoney(getShippingTax(), getOrder().getCurrency());
	}

	@SuppressWarnings("fallthrough")
	@Override
	@Transient
	public boolean isCancellable() {
		boolean cancellable = true;

		switch (getShipmentStatus().getOrdinal()) {
		case OrderShipmentStatus.CANCELLED_ORDINAL:
		case OrderShipmentStatus.SHIPPED_ORDINAL:
			cancellable = false;
			break;
		case OrderShipmentStatus.AWAITING_INVENTORY_ORDINAL:
		case OrderShipmentStatus.INVENTORY_ASSIGNED_ORDINAL:
		case OrderShipmentStatus.ONHOLD_ORDINAL:
		case OrderShipmentStatus.RELEASED_ORDINAL:
			cancellable = true;
			break;

		default:
			throw new EpSystemException("Error: unhandled shipment status: " + getShipmentStatus());

		}
		if (LOG.isDebugEnabled() && !cancellable) {
			LOG.debug("Cannot cancel orderShipment because it is already " + getShipmentStatus());
		}
		return cancellable;
	}

	/**
	 * Implements equals semantics.<br>
	 * This class more than likely would be extended to add functionality that would not effect the equals method in comparisons, and as such would
	 * act as an entity type. In this case, content is not crucial in the equals comparison. Using instanceof within the equals method enables
	 * comparison in the extended classes where the equals method can be reused without violating symmetry conditions. If getClass() was used in the
	 * comparison this could potentially cause equality failure when we do not expect it. If when extending additional fields are included in the
	 * equals method, then the equals needs to be overridden to maintain symmetry.
	 *
	 * @param obj the other object to compare
	 * @return true if equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PhysicalOrderShipmentImpl)) {
			return false;
		}

		final PhysicalOrderShipmentImpl other = (PhysicalOrderShipmentImpl) obj;
		EqualsBuilder builder = new EqualsBuilder();
		return builder
				.append(getOrderShipmentType(), other.getOrderShipmentType())
				.appendSuper(super.equals(obj))
				.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder
				.append(getOrderShipmentType())
				.appendSuper(super.hashCode())
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("carrier", getCarrier())
			.append("serviceLevel", getServiceLevel())
			.append("trackingCode", getTrackingCode())
			.append("shippingCost", getShippingCost())
			.append("beforeTaxShippingCost", getBeforeTaxShippingCost())
			.append("shippingTax", getShippingTax())
			.append("shipmentAddress", getShipmentAddress())
			.appendSuper(super.toString())
			.toString();
	}

	/**
	 * Returns the shipping service level service, retrieved from the global bean factory.  Sorry.
	 * @return the shipping service level service.
	 */
	@Transient
	protected ShippingServiceLevelService getShippingServiceLevelService() {
		return getBean(ContextIdNames.SHIPPING_SERVICE_LEVEL_SERVICE);
	}
}
