/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.order.impl;

import java.math.BigDecimal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;

/**
 * <code>ElectronicOrderShipmentImpl</code> represents a customer's order shipment for non-shippable goods.
 */
@Entity
@DiscriminatorValue(ShipmentType.ELECTRONIC_STRING)
@DataCache(enabled = false)
public class ElectronicOrderShipmentImpl extends AbstractOrderShipmentImpl implements ElectronicOrderShipment {

	private static final long serialVersionUID = 5000000001L;

	@Override
	@Transient
	public ShipmentType getOrderShipmentType() {
		return ShipmentType.ELECTRONIC;
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

			updateTaxValues(taxResult);

			setItemSubtotal(taxResult.getSubtotal().getAmount());
		}

		// the subtotal has excluded the tax from Unit price if the price is defined as inclusive tax for that tax region.
		recalculateTransientDerivedValues();
	}

	/**
	 * This method checks if the tax recalculation using Live tax rates is required or not based on the shipment status. <br>
	 * Currently the tax recalculation is not required if the shipment status is in the list {OrderShipmentStatus.RELEASED,
	 * OrderShipmentStatus.SHIPPED, OrderShipmentStatus.CANCELLED}
	 *
	 * @return true if the tax recalculation is required else false
	 */
	@Override
	@Transient
	protected boolean isTaxRecalculationRequired() {
		boolean shouldRecalculate = true;
		OrderShipmentStatus shipmentStatus = getShipmentStatus();
		if (shipmentStatus == OrderShipmentStatus.CANCELLED || shipmentStatus == OrderShipmentStatus.SHIPPED) {
			shouldRecalculate = false;
		}
		return shouldRecalculate;
	}

	/**
	 * This method recalculates values which are derived from persistent values.<br>
	 * It does the recalculation from data held by this object and does not go outside to get other information.
	 */
	@Override
	protected void recalculateTransientDerivedValues() {
		setSubtotal(getItemSubtotal());
		if (isInclusiveTax()) {
			setTotal(getSubtotal().subtract(getSubtotalDiscount()));
		} else {
			setTotal(getSubtotal().subtract(getSubtotalDiscount()).add(getItemTax()));
		}

		Money totalBeforeTaxMoney = MoneyFactory.createMoney(
				getItemSubTotalBeforeTaxMoney().getAmount().subtract(getSubtotalDiscount()), getOrder().getCurrency());
		setTotalBeforeTaxMoney(totalBeforeTaxMoney);
	}

	@Override
	@Transient
	public Money getTotalTaxMoney() {
		BigDecimal totalTax = BigDecimal.ZERO.setScale(2);
		if (getItemTax() != null) {
			totalTax = totalTax.add(getItemTax());
		}
		return MoneyFactory.createMoney(totalTax, getOrder().getCurrency());
	}

	private TaxCalculationResult calculateTaxes() {
		Order order = getOrder();
		TaxCalculationService taxCalculationService = order.getTaxCalculationService();
		Money shippingCost = MoneyFactory.createMoney(BigDecimal.ZERO.setScale(2), order.getCurrency());

		return taxCalculationService.calculateTaxes(order.getStoreCode(),
				order.getBillingAddress(),
				order.getCurrency(),
				shippingCost,
				getShipmentOrderSkus(),
				getSubtotalDiscountMoney());
	}

	/**
	 * Determines whether or not this shipment is in a state that allows it to be cancelled. <br>
	 * Electronic shipments cannot be cancelled.
	 *
	 * @return false
	 */
	@Override
	@Transient
	public boolean isCancellable() {
		return false;
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

		if (!(obj instanceof ElectronicOrderShipmentImpl)) {
			return false;
		}

		final ElectronicOrderShipmentImpl other = (ElectronicOrderShipmentImpl) obj;
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
}
