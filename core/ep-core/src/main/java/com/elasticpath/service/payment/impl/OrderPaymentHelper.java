package com.elasticpath.service.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.commons.util.PaymentsComparatorFactory;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Provide helping capabilities for order payments' searching, amounts calculation.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.UseSingleton" })
public class OrderPaymentHelper {

	private static final Logger LOG = Logger.getLogger(OrderPaymentHelper.class);

	/**
	 * Return amount which should be authorized by the specified shipment for successful capture. It takes into account orderShipment.getTotal() and
	 * if this is a shipment of an Exchange Order.
	 *
	 * @param orderShipment shipment.
	 * @return amount which should be authorized by the specified shipment for successful capture.
	 */
	public static BigDecimal calculateFullAuthorizationAmount(final OrderShipment orderShipment) {
		return adjustExchangeOrderAuthAmount(orderShipment, calculateAmountForPreAuthorization(orderShipment));
	}

	/**
	 * Calculates total amount currently authorized for this shipment. Takes into account only active (not reversed) authorizations including GC's.
	 *
	 * @param orderShipment order shipment.
	 * @return amount currently authorized for the shipment.
	 */
	public static BigDecimal getCurrentlyAuthorizedAmount(final OrderShipment orderShipment) {
		return getAuthorizedAmountByGCPayments(orderShipment).add(getAuthorizedByConventional(orderShipment));
	}

	/**
	 * Calculates amount currently authorized for this shipment from conventional (any except GC) payments. Takes into account only active
	 * (non-reversed and non-captured) authorizations.
	 *
	 * @param orderShipment order shipment.
	 * @return amount currently authorized for the shipment from conventional payments.
	 */
	public static BigDecimal getAuthorizedByConventional(final OrderShipment orderShipment) {
		final OrderPayment payment = OrderPaymentHelper.findActiveConventionalAuthorizationPayment(orderShipment);
		if (payment == null) {
			return BigDecimal.ZERO;
		}
		return payment.getAmount();
	}

	/**
	 * Calculates amount currently authorized for this shipment from GC payments. Takes into account only non-reverted
	 * authorizations.
	 *
	 * @param orderShipment order shipment.
	 * @return amount currently authorized for the shipment from GC payments.
	 */
	public static BigDecimal getAuthorizedAmountByGCPayments(final OrderShipment orderShipment) {
		BigDecimal result = BigDecimal.ZERO;
		for (OrderPayment orderPayment : OrderPaymentHelper.findNonRevertedGiftCertificateAuthPayments(orderShipment)) {
			result = result.add(orderPayment.getAmount());
		}
		return result;
	}

	/**
	 * Gets total GC discount for given order.
	 *
	 * @param order the order
	 * @return total GC discount for given order
	 */
	public static BigDecimal getTotalGiftCertificateDiscount(final Order order) {
		List<String> gcAuthsRevertedOrCaptured = new ArrayList<String>();
		BigDecimal result = BigDecimal.ZERO;
		for (OrderPayment payment : order.getOrderPayments()) {
			if (payment.getPaymentMethod() == PaymentType.GIFT_CERTIFICATE && OrderPaymentStatus.APPROVED == payment.getStatus()
					&& OrderPayment.CAPTURE_TRANSACTION.equals(payment.getTransactionType())) {
				result = result.add(payment.getAmount());
			}
		}

		Map<String, OrderPayment> gcAuthsNonrevertedNonCaptured = new HashMap<String, OrderPayment>();

		for (OrderPayment orderPayment : order.getOrderPayments()) {
			if (orderPayment.getPaymentMethod() == PaymentType.GIFT_CERTIFICATE && OrderPaymentStatus.APPROVED == orderPayment.getStatus()) {
				if (OrderPayment.AUTHORIZATION_TRANSACTION.equals(orderPayment.getTransactionType())) {
					gcAuthsNonrevertedNonCaptured.put(orderPayment.getAuthorizationCode(), orderPayment);
				} else if (OrderPayment.REVERSE_AUTHORIZATION.equals(orderPayment.getTransactionType())
						|| OrderPayment.CAPTURE_TRANSACTION.equals(orderPayment.getTransactionType())) {
					gcAuthsRevertedOrCaptured.add(orderPayment.getAuthorizationCode());
				}
			}
		}

		for (String revOrCapt : gcAuthsRevertedOrCaptured) {
			gcAuthsNonrevertedNonCaptured.remove(revOrCapt);
		}

		for (OrderPayment payment : gcAuthsNonrevertedNonCaptured.values()) {
			result = result.add(payment.getAmount());
		}

		return result;
	}

	/**
	 * Adjusts authorization amount for exchange order only. Subtracts exchange.total from amount if the orderShipment is the only shipment in the
	 * exchange order. In case if calculated amount is less or equals to zero, sets $1 as dummy amount.
	 *
	 * @param orderShipment order shipment belonging to the order for which adjustment is taken place.
	 * @param amount amount to be adjusted. Basically equals to order total.
	 * @return adjusted amount or original amount in case if adjustment failed. No exceptions should be thrown.
	 */
	public static BigDecimal adjustExchangeOrderAuthAmount(final OrderShipment orderShipment, final BigDecimal amount) {
		// reduce money to be authorized on return's amount
		Order order = orderShipment.getOrder();
		if (order.isExchangeOrder()) {
			// TODO: currently EO can not be recalculated is there are multiple shipments.
			// In this case it's unclear now how to redistribute(subtract) exchange.total between multiple shipments.
			if (order.getStatus() == OrderStatus.AWAITING_EXCHANGE) {
				LOG.debug("Order is currently awaiting exchange. Returning value of 1.0.");
				return BigDecimal.ONE;
			}

			BigDecimal exchnageOrderAmount = amount.subtract(order.getDueToRMA());
			if (exchnageOrderAmount.compareTo(BigDecimal.ZERO) > 0) {
				return exchnageOrderAmount;
			}
			LOG.debug("OrderShipment adjusted amount = 1.0 because adjustment calculated to be less than zero.");
			return BigDecimal.ONE; // authorize $1
		}
		return amount;
	}

	/**
	 * Adjusts capture amount for exchange order only. Subtracts exchange.total from amount if the orderShipment is the only shipment in the exchange
	 * order.
	 *
	 * @param orderShipment order shipment belonging to the order for which adjustment is taken place.
	 * @return adjusted amount or original amount in case if adjustment failed. No exceptions should be thrown.
	 */
	public static BigDecimal adjustExchangeOrderCaptureAmount(final OrderShipment orderShipment) {
		final BigDecimal amount = orderShipment.getTotal();
		// reduce money to be captured on return's amount
		Order order = orderShipment.getOrder();
		if (order.isExchangeOrder()) {
			// TODO: currently EO can not be recalculated is there are multiple shipments.
			// In this case it's unclear now how to redistribute(subtract) exchange.total between multiple shipments.
			BigDecimal exchnageOrderAmount = amount.subtract(order.getDueToRMA());
			if (exchnageOrderAmount.compareTo(BigDecimal.ZERO) >= 0) {
				return exchnageOrderAmount;
			}
			return BigDecimal.ZERO;
		}
		return amount;
	}

	/**
	 * Calculates the total amount to be pre-authorized for conventional payments depending on the pre/back order availability criteria of the
	 * products in this shipment and their allocation status.
	 *
	 * @return
	 */
	private static BigDecimal calculateAmountForPreAuthorization(final OrderShipment orderShipment) {
		//A service shopping won't need any preauthorizations.
		if (orderShipment.getOrderShipmentType() == ShipmentType.SERVICE) {
			return BigDecimal.ZERO;
		}

		BigDecimal amount = orderShipment.getTotal();
		boolean hasNotAllocatedItemsOnPreOrBackOrder = false;
		for (OrderSku sku : orderShipment.getShipmentOrderSkus()) {
			AvailabilityCriteria availabilityCriteria = getProductSkuAvailabilityCriteriaForOrderSku(sku);
			if ((availabilityCriteria == AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER
					|| availabilityCriteria == AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER)
					&& !sku.isAllocated()) {
				hasNotAllocatedItemsOnPreOrBackOrder = true;
			}
		}
		// if we have not allocated items (on pre/back order) we need to authorize for the amount of $1
		if (hasNotAllocatedItemsOnPreOrBackOrder) {
			LOG.debug("Setting authorization amount to $1.00 because we have unallocated items on pre or back order.");
			amount = BigDecimal.ONE;
		}
		return amount;
	}

	private static AvailabilityCriteria getProductSkuAvailabilityCriteriaForOrderSku(final OrderSku orderSku) {
		return orderSku.getProductSku().getProduct().getAvailabilityCriteria();
	}

	/**
	 * Returns Last by creation time active (non-reversed and non-captured) conventional authorization payment. In fact currently there can be only
	 * one such payment, but to make sure we suppose that findAllConventionalAuthPayments() returns a list of payments and search the last one among
	 * them.
	 *
	 * @param orderShipment the order shipment.
	 * @return last active conventional authorization payment.
	 */
	public static OrderPayment findActiveConventionalAuthorizationPayment(final OrderShipment orderShipment) {
		return getLastAuthPayment(findAllConventionalAuthPayments(orderShipment));
	}

	/**
	 * Finds all conventional (no a gift certificate) authorization payments. First shipment-level payments are collected and if there are no one,
	 * then order-level payments are collected.
	 *
	 * @param orderShipment the order shipment.
	 * @return collection of all authorization conventional payments.
	 */
	public static Collection<OrderPayment> findAllConventionalAuthPayments(final OrderShipment orderShipment) {
		// TODO get the order payment using JPQL?
		List<OrderPayment> orderPayments = new ArrayList<OrderPayment>();
		List<OrderPayment> orderLevelPayments = new ArrayList<OrderPayment>();
		// Find on the order shipment level.
		for (OrderPayment orderPayment : orderShipment.getOrder().getOrderPayments()) {
			if (orderPayment.getStatus() == OrderPaymentStatus.APPROVED
					&& OrderPayment.AUTHORIZATION_TRANSACTION.equals(orderPayment.getTransactionType())
					&& orderPayment.getPaymentMethod() != PaymentType.GIFT_CERTIFICATE) {
				if (orderPayment.getOrderShipment() != null && orderShipment.getUidPk() == orderPayment.getOrderShipment().getUidPk()
						&& orderShipment.getOrderShipmentType() == orderPayment.getOrderShipment().getOrderShipmentType()) {
					orderPayments.add(orderPayment);
				} else if (orderPayment.getOrderShipment() == null) {
					orderLevelPayments.add(orderPayment);
				}
			}
		}
		// If not find on the order shipment level, get it on the order level.
		// (Like the paypal authorization will be on the order level.)
		if (orderPayments.isEmpty()) {
			orderPayments.addAll(orderLevelPayments);
		}
		return orderPayments;
	}

	private static OrderPayment getLastAuthPayment(final Collection<OrderPayment> allAuthPayments) {
		if (allAuthPayments.size() < 1) {
			return null;
		}
		List<OrderPayment> sortedPayments = new ArrayList<OrderPayment>(allAuthPayments);
		Collections.sort(sortedPayments, PaymentsComparatorFactory.getOrderPaymentDateCompatator());
		return sortedPayments.get(0);
	}

	/**
	 * Gets non-reversed and non-captured authorization payments made with gift certificates for this shipment.
	 *
	 * @param orderShipment order shipment.
	 * @return active (non-reversed and non-captured) authorization payments made with gift certificates for this shipment.
	 */
	public static Collection<OrderPayment> findActiveGiftCertificateAuthPayments(final OrderShipment orderShipment) {
		HashSet<String> filterTypes = new HashSet<String>();
		filterTypes.add(OrderPayment.REVERSE_AUTHORIZATION);
		filterTypes.add(OrderPayment.CAPTURE_TRANSACTION);
		return getAndFilterGiftCertificates(orderShipment, filterTypes);
	}

	private static Collection<OrderPayment> getAndFilterGiftCertificates(
			final OrderShipment orderShipment, final Set<String> filterTypes) {
		Map<String, OrderPayment> gcAuthsNonrevertedNonCaptured = new HashMap<String, OrderPayment>();

		List<String> gcAuthsRevertedOrCaptured = new ArrayList<String>();

		for (OrderPayment orderPayment : orderShipment.getOrder().getOrderPayments()) {
			if (orderPayment.getOrderShipment() != null && orderShipment.getUidPk() == orderPayment.getOrderShipment().getUidPk()
					&& orderShipment.getOrderShipmentType() == orderPayment.getOrderShipment().getOrderShipmentType()
					&& orderPayment.getPaymentMethod() == PaymentType.GIFT_CERTIFICATE && OrderPaymentStatus.APPROVED == orderPayment.getStatus()) {
				if (OrderPayment.AUTHORIZATION_TRANSACTION.equals(orderPayment.getTransactionType())) {
					gcAuthsNonrevertedNonCaptured.put(orderPayment.getAuthorizationCode(), orderPayment);
				} else if (filterTypes.contains(orderPayment.getTransactionType())) {
					gcAuthsRevertedOrCaptured.add(orderPayment.getAuthorizationCode());
				}
			}
		}

		for (String revOrCapt : gcAuthsRevertedOrCaptured) {
			gcAuthsNonrevertedNonCaptured.remove(revOrCapt);
		}

		return gcAuthsNonrevertedNonCaptured.values();
	}

	/**
	 * Gets non-reversed authorization payments made with gift certificates for this shipment.
	 *
	 * @param orderShipment order shipment.
	 * @return non-reverted authorization payments made with gift certificates for this shipment.
	 */
	public static Collection<OrderPayment> findNonRevertedGiftCertificateAuthPayments(final OrderShipment orderShipment) {
		HashSet<String> filterTypes = new HashSet<String>();
		filterTypes.add(OrderPayment.REVERSE_AUTHORIZATION);
		return getAndFilterGiftCertificates(orderShipment, filterTypes);
	}

	/**
	 * Finds last order-level authorization payment.
	 *
	 * @param order the order
	 * @return last order-level authorization payment
	 */
	public static OrderPayment findActiveOrderAuthorizationPayment(final Order order) {
		List<OrderPayment> orderPayments = new ArrayList<OrderPayment>();
		for (OrderPayment orderPayment : order.getOrderPayments()) {
			if (orderPayment.getOrderShipment() == null) {
				orderPayments.add(orderPayment);
			}
		}

		return getLastAuthPayment(orderPayments);
	}
}
