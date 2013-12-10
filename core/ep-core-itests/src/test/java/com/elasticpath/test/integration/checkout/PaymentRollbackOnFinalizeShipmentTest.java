/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.test.integration.checkout;

import com.elasticpath.domain.builder.CustomerBuilder;
import com.elasticpath.domain.builder.checkout.CheckoutTestCartBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.event.impl.EventOriginatorHelperImpl;
import com.elasticpath.domain.factory.OrderPaymentFactory;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.paymentgateways.testdouble.PaymentGatewayPluginTestDouble;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.order.CompleteShipmentFailedException;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test payment transaction rollbacks on a failed finalize shipment.
 */
@ContextConfiguration(inheritLocations = true)
public class PaymentRollbackOnFinalizeShipmentTest extends BasicSpringContextTest {

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderPaymentFactory orderPaymentFactory;

	@Autowired
	private CheckoutTestCartBuilder checkoutTestCartBuilder;

	@Autowired
	private CustomerBuilder customerBuilder;

	/**
	 * Set up common elements of the test.
	 */
	@Before
	public void setUp() {
		SimpleStoreScenario scenario = getTac().useScenario(SimpleStoreScenario.class);

		Customer testCustomer = customerBuilder.withStoreCode(scenario.getStore().getCode()).build();
		customerService.add(testCustomer);

		checkoutTestCartBuilder.withScenario(scenario)
							   .withCustomer(testCustomer)
							   .withTestDoubleGateway();
		PaymentGatewayPluginTestDouble.clearPaymentGatewayTransactions();
	}
	
	/**
	 * Ensure successful roll back of payment transactions when post completion of shipment fails with a physical shipment.
	 */
	@DirtiesDatabase
	@Test
	public void ensureSuccessfullRollbackOfPaymentsWhenPostCompletionOfShipmentFailsWithPhysicalShipment() {
		PaymentGatewayPluginTestDouble.setFailFinalizeShipment(true);
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPayment();
		CheckoutResults results = checkoutService.checkout(checkoutTestCartBuilder.withPhysicalProduct().build(), templateOrderPayment, true);

		Order order = results.getOrder();
		try {
			completePhysicalShipmentsForOrder(order);
			fail("The finalize shipment capability should have thrown an exception.");
		} catch (CompleteShipmentFailedException e) {
			// exception thrown as expected, move on
		}

		order = orderService.findOrderByOrderNumber(order.getOrderNumber());

		OrderValidator orderValidator = OrderValidator.builder()
				.withStatus(OrderStatus.IN_PROGRESS)
				.withPaymentMatchers(OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(),
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture()) 	
				.build();
		assertThat("The order validation should succeed.", order, orderValidator);
		
		assertTransactions(PaymentGatewayPluginTestDouble.AUTHORIZATION_TRANSACTION,
				PaymentGatewayPluginTestDouble.CAPTURE_TRANSACTION, 
				PaymentGatewayPluginTestDouble.VOID_TRANSACTION);
	}
	
	private void completePhysicalShipmentsForOrder(final Order order) {
		for (OrderShipment orderShipment : order.getPhysicalShipments()) {
			orderShipment = orderService.processReleaseShipment(orderShipment);
			orderService.completeShipment(orderShipment.getShipmentNumber(),
					"trackingNumber", true, null, false, new EventOriginatorHelperImpl().getSystemOriginator());
		}
	}
	
	private void assertTransactions(String ... expectedTransactionTypes) {
		List<String> expectedTypeList = Arrays.asList(expectedTransactionTypes); 
		assertTrue("The order does not have the correct transactions. Expected: " + expectedTypeList + " Actual: " 
				+ PaymentGatewayPluginTestDouble.getPaymentGatewayTransactions(), 
				PaymentGatewayPluginTestDouble.verifyTransactions(expectedTypeList));
	}
}
