package com.elasticpath.domain.builder;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.builder.checkout.CheckoutTestCartBuilder;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.impl.EventOriginatorHelperImpl;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shoppingcart.CheckoutService;


public class OrderBuilder implements DomainObjectBuilder<Order> {

	private CheckoutTestCartBuilder checkoutTestCartBuilder;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CheckoutService checkoutService;

	private OrderPayment templateOrderPayment;

	private boolean allShipmentsCompleted = false;

	public OrderBuilder withCheckoutTestCartBuilder(final CheckoutTestCartBuilder checkoutTestCartBuilder) {
		this.checkoutTestCartBuilder = checkoutTestCartBuilder;
		return this;
	}

	public OrderBuilder withNonZeroPhysicalShipment() {
		checkoutTestCartBuilder.withPhysicalProduct();
		return this;
	}

	public OrderBuilder withNonZeroElectronicShipment() {
		checkoutTestCartBuilder.withElectronicProduct();
		return this;
	}

	public OrderBuilder withFreeElectronicShipment() {
		checkoutTestCartBuilder.withFreeElectronicProduct();
		return this;
	}

	public OrderBuilder withTemplateOrderPayment(final OrderPayment templateOrderPayment) {
		this.templateOrderPayment = templateOrderPayment;
		return this;
	}

	public OrderBuilder withGateway(final PaymentGateway gateway) {
		checkoutTestCartBuilder.withGateway(gateway);
		return this;
	}

	public OrderBuilder withAllShipmentsCompleted() {
		this.allShipmentsCompleted  = true;
		return this;
	}

	public Order build() {
		ShoppingCart shoppingCart = checkoutTestCartBuilder.build();
		CheckoutResults results = checkoutService.checkout(shoppingCart, templateOrderPayment, true);
		Order order = results.getOrder();

		if (allShipmentsCompleted) {
			order = completePhysicalShipmentsForOrder(order);
		}

		return order;
	}

	private Order completePhysicalShipmentsForOrder(final Order order) {
		Order completedOrder = order;
		for (OrderShipment orderShipment : order.getPhysicalShipments()) {
			orderShipment = orderService.processReleaseShipment(orderShipment);
			String shipmentNumber = orderShipment.getShipmentNumber();
			EventOriginator systemOriginator = new EventOriginatorHelperImpl().getSystemOriginator();
			completedOrder = orderService.completeShipment(shipmentNumber, "trackingNumber", true, null, false, systemOriginator);
		}

		return completedOrder;
	}
}