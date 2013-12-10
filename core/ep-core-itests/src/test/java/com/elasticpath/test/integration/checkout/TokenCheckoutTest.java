/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.test.integration.checkout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.builder.CustomerBuilder;
import com.elasticpath.domain.builder.checkout.CheckoutTestCartBuilder;
import com.elasticpath.domain.builder.payment.gateway.PaymentGatewayBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.event.impl.EventOriginatorHelperImpl;
import com.elasticpath.domain.factory.OrderPaymentFactory;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.paymentgateways.cybersource.CyberSourceSubscriberFactory;
import com.elasticpath.paymentgateways.cybersource.TestCybersourceClient;
import com.elasticpath.paymentgateways.cybersource.provider.CybersourceConfigurationProvider;
import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.order.CompleteShipmentFailedException;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.payment.PaymentServiceException;
import com.elasticpath.service.payment.gateway.impl.PaymentGatewayPluginDtoConverter;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

@ContextConfiguration(inheritLocations = true)
public class TokenCheckoutTest extends BasicSpringContextTest {

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderPaymentFactory orderPaymentFactory;

	@Autowired
	private CheckoutTestCartBuilder checkoutTestCartBuilder;

	@Autowired
	private CyberSourceSubscriberFactory cyberSourceSubscriberFactory;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerBuilder customerBuilder;

	@Autowired
	private PaymentGatewayBuilder paymentGatewayBuilder;  

	/**
	 * Set up common elements of the test.
	 */
	@Before
	public void setUp() {
		SimpleStoreScenario scenario = getTac().useScenario(SimpleStoreScenario.class);

		Customer testCustomer = customerBuilder.withStoreCode(scenario.getStore().getCode())
											   .build();
		customerService.add(testCustomer);

		checkoutTestCartBuilder.withScenario(scenario)
							   .withCustomer(testCustomer);
	}

	/**
	 * Test checkout with token is successful.
	 */
	@DirtiesDatabase
	@Test
	public void successfulCheckoutWithToken() {
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPaymentWithToken(cyberSourceSubscriberFactory.createBillableSubscriber());
		ShoppingCart shoppingCart = checkoutTestCartBuilder.withGateway(createCyberSourceExternalPaymentGateway())
				.withElectronicProduct()
				.build();
		
		CheckoutResults results = checkoutService.checkout(shoppingCart, templateOrderPayment, true);

		Order order = results.getOrder();
		
		OrderValidator orderValidator = OrderValidator.builder()
				.withStatus(OrderStatus.COMPLETED)
				.withPaymentMatchers(OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(), 
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture())
				.build();
		assertThat("The order validation should succeed.", order, orderValidator);
	}

	/**
	 * Test checkout with invalid token throws an {@link EpServiceException}.
	 */
	@DirtiesDatabase
	@Test(expected = EpServiceException.class)
	public void ensureCheckoutWithInvalidTokenThrowsException() {
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPaymentWithToken("invalidSubscriberId");
		ShoppingCart shoppingCart = checkoutTestCartBuilder.withGateway(createCyberSourceExternalPaymentGateway())
				.withElectronicProduct()
				.build();
		
		checkoutService.checkout(shoppingCart, templateOrderPayment, true);
	}
	
	/**
	 * Test checkout with invalid gateway parameters throws a {@link PaymentServiceException}.
	 */
	@DirtiesDatabase
	@Test(expected = PaymentServiceException.class)
	public void ensureCheckoutWithInvalidGatewayParametersThrowsException() {
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPayment();
		ShoppingCart shoppingCart = checkoutTestCartBuilder.withInvalidPaymentTokenGateway()
				.withElectronicProduct()
				.build();
		
		checkoutService.checkout(shoppingCart, templateOrderPayment, true);
	}
	
	/**
	 * Test checkout with token and physical goods is successful.
	 */
	@DirtiesDatabase
	@Test
	public void checkoutWithTokenAndPhysicalGoodsIsSuccessful() {
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPaymentWithToken(cyberSourceSubscriberFactory.createBillableSubscriber());
		ShoppingCart shoppingCart = checkoutTestCartBuilder.withGateway(createCyberSourceExternalPaymentGateway())
				.withPhysicalProduct()
				.build();
		
		CheckoutResults results = checkoutService.checkout(shoppingCart, templateOrderPayment, true);
		
		Order order = results.getOrder();
		OrderValidator orderValidator = OrderValidator.builder()
				.withStatus(OrderStatus.IN_PROGRESS)
				.withPaymentMatchers(OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization())
				.build();
		assertThat("The order validation should succeed.", order, orderValidator);
	}

	/**
	 * Test checkout with token and physical goods creates a split shipment.
	 */
	@DirtiesDatabase
	@Test
	public void checkoutWithTokenAndPhysicalProductCreatesSplitShipment() {
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPaymentWithToken(cyberSourceSubscriberFactory.createBillableSubscriber());
		ShoppingCart shoppingCart = checkoutTestCartBuilder.withGateway(createCyberSourceExternalPaymentGateway())
				.withElectronicProduct()
				.withPhysicalProduct()
				.build();
		
		CheckoutResults results = checkoutService.checkout(shoppingCart, templateOrderPayment, true);

		Order order = results.getOrder();

		OrderValidator orderValidator = OrderValidator.builder()
				.withStatus(OrderStatus.PARTIALLY_SHIPPED)
				.withPaymentMatchers(OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(), 
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(), OrderPaymentMatcherFactory.createSuccessfulTokenCapture())
				.build();
		assertThat("The order validation should succeed.", order, orderValidator);
	}

	/**
	 * Test checkout with token and both physical and electronic goods is successful.
	 */
	@DirtiesDatabase
	@Test
	public void checkoutWithTokenAndBothPhysicalAndElectronicProductsIsSuccessful() {
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPaymentWithToken(cyberSourceSubscriberFactory.createBillableSubscriber());
		ShoppingCart shoppingCart = checkoutTestCartBuilder.withGateway(createCyberSourceExternalPaymentGateway())
				.withElectronicProduct()
				.withPhysicalProduct()
				.build();
		
		CheckoutResults results = checkoutService.checkout(shoppingCart, 
				templateOrderPayment, true);

		Order order = results.getOrder();
		completePhysicalShipmentsForOrder(order);

		order = orderService.findOrderByOrderNumber(order.getOrderNumber());

		OrderValidator orderValidator = OrderValidator.builder()
				.withStatus(OrderStatus.COMPLETED)
				.withPaymentMatchers(OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(), 
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(), 
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture(), 
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture())
				.build();
		assertThat("The order validation should succeed.", order, orderValidator);
	}

	/**
	 * Test checkout with token, applied gift certificate to cart and electronic goods is successful.
	 */
	@DirtiesDatabase
	@Test
	public void checkoutWithTokenAndAppliedGiftCertificateAndElectronicProductsIsSuccessful() {
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPaymentWithToken(cyberSourceSubscriberFactory.createBillableSubscriber());
		ShoppingCart testShoppingCart = checkoutTestCartBuilder.withGateway(createCyberSourceExternalPaymentGateway())
				.withGiftCertificateGateway()
				.withElectronicProduct()
				.withGiftCertificateAmount(BigDecimal.TEN)
				.build();
		
		CheckoutResults results = checkoutService.checkout(testShoppingCart, templateOrderPayment, true);

		Order order = results.getOrder();

		OrderValidator orderValidator = OrderValidator.builder()
				.withStatus(OrderStatus.COMPLETED)
				.withPaymentMatchers(OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(), 
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture(), 
						OrderPaymentMatcherFactory.createSuccessfulGiftCertificateAuthorization(), 
						OrderPaymentMatcherFactory.createSuccessfulGiftCertificateCapture())
				.build();
		assertThat("The order validation should succeed.", order, orderValidator);
	}
	
	/**
	 * Test checkout with token, applied gift certificate to cart and both electronic and physical goods
	 * with a gift certificate amount that exceeds the amount of the physical shipment is successful.
	 */
	@DirtiesDatabase
	@Test
	public void checkoutWithTokenAndBothElectronicAndPhysicalProductsAndAppliedGiftCertificateThatExceedsPhyscialShipmentAmountIsSuccessful() {
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPaymentWithToken(cyberSourceSubscriberFactory.createBillableSubscriber());
		BigDecimal amountExceedingPhyscialShipment = new BigDecimal(650.0);
		ShoppingCart testShoppingCart = checkoutTestCartBuilder.withGateway(createCyberSourceExternalPaymentGateway())
				.withGiftCertificateGateway()
				.withElectronicProduct()
				.withPhysicalProduct()
				.withGiftCertificateAmount(amountExceedingPhyscialShipment)
				.build();
		
		CheckoutResults results = checkoutService.checkout(testShoppingCart, templateOrderPayment, true);

		Order order = results.getOrder();
		completePhysicalShipmentsForOrder(order);

		order = orderService.findOrderByOrderNumber(order.getOrderNumber());

		OrderValidator orderValidator = OrderValidator.builder()
				.withStatus(OrderStatus.COMPLETED)
				.withPaymentMatchers(OrderPaymentMatcherFactory.createSuccessfulGiftCertificateCapture(),
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture(),
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(), 
						OrderPaymentMatcherFactory.createSuccessfulGiftCertificateAuthorization(), 
						OrderPaymentMatcherFactory.createSuccessfulGiftCertificateCapture(),
						OrderPaymentMatcherFactory.createSuccessfulGiftCertificateAuthorization()) 
				.build();
		assertThat("The order validation should succeed.", order, orderValidator);
	}
	
	/**
	 * Test checkout with token, applied gift certificate to cart and both electronic and physical goods
	 * with a gift certificate amount that is equal to the total amount of the cart.
	 */
	@DirtiesDatabase
	@Test
	public void checkoutWithTokenAndBothElectronicAndPhysicalProductsAndAppliedGiftCertificateThatFullPurchaseAmountIsSuccessful() {
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPaymentWithToken(cyberSourceSubscriberFactory.createBillableSubscriber());
		BigDecimal fullCartAmount = new BigDecimal("1101.00");
		ShoppingCart testShoppingCart = checkoutTestCartBuilder.withGateway(createCyberSourceExternalPaymentGateway())
				.withGiftCertificateGateway()
				.withElectronicProduct()
				.withPhysicalProduct()
				.withGiftCertificateAmount(fullCartAmount)
				.build();
		
		CheckoutResults results = checkoutService.checkout(testShoppingCart, templateOrderPayment, true);

		Order order = results.getOrder();
		completePhysicalShipmentsForOrder(order);

		order = orderService.findOrderByOrderNumber(order.getOrderNumber());

		OrderValidator orderValidator = OrderValidator.builder()
				.withStatus(OrderStatus.COMPLETED)
				.withPaymentMatchers(OrderPaymentMatcherFactory.createSuccessfulGiftCertificateAuthorization(), 
						OrderPaymentMatcherFactory.createSuccessfulGiftCertificateCapture(),
						OrderPaymentMatcherFactory.createSuccessfulGiftCertificateCapture(),
						OrderPaymentMatcherFactory.createSuccessfulGiftCertificateAuthorization()) 				
				.build();
		assertThat("The order validation should succeed.", order, orderValidator);
	}

	/**
	 * Ensure failed capture prevents shipment completion.
	 */
	@DirtiesDatabase
	@Test
	public void ensureFailedCapturePreventsShipmentCompletion() {
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPaymentWithToken(cyberSourceSubscriberFactory.createBillableSubscriber());
		ShoppingCart shoppingCart = checkoutTestCartBuilder.withGateway(createCyberSourceExternalPaymentGateway())
				.withPhysicalProduct()
				.build();
		
		CheckoutResults results = checkoutService.checkout(shoppingCart,
				templateOrderPayment, true);

		Order order = results.getOrder();
		OrderShipment orderShipment = order.getPhysicalShipments().get(0);
		orderShipment = orderService.processReleaseShipment(orderShipment);

		OrderPayment authorization = order.getOrderPayment();
		if (!authorization.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)) {
			fail("Only payment on physical shipment should be authorization at this point");
		}

		ensureCaptureFails(authorization);

		try {
			orderService.completeShipment(orderShipment.getShipmentNumber(),
					"trackingNumber", true, null, false, new EventOriginatorHelperImpl().getSystemOriginator());
			fail("Exception should have been thrown because there was an authorization reversal");
		} catch (CompleteShipmentFailedException e) {
			// exception thrown as expected, move on
		}

		order = orderService.findOrderByOrderNumber(order.getOrderNumber());

		OrderValidator orderValidator = OrderValidator.builder()
				.withStatus(OrderStatus.IN_PROGRESS)
				.withPaymentMatchers(OrderPaymentMatcherFactory.createFailedTokenCapture(),
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization()) 					
				.build();
		assertThat("The order validation should succeed.", order, orderValidator);
	}
	
	/**
	 * Ensure successful roll back of payment transactions when post completion of shipment fails with a physical shipment.
	 */
	@DirtiesDatabase
	@Test
	public void ensureSuccessfullRollbackOfPaymentsWhenPostCompletionOfShipmentFailsWithPhysicalShipment() {
		TokenCyberSourceGatewayWithFinalizeShipment.setFailFinalizeShipment(true);
		
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPaymentWithToken(cyberSourceSubscriberFactory.createBillableSubscriber());
		ShoppingCart shoppingCart = checkoutTestCartBuilder.withGateway(createCyberSourceExternalPaymentGateway()).withPhysicalProduct().build();
		CheckoutResults results = checkoutService.checkout(shoppingCart,
				templateOrderPayment, true);

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
				.withPaymentMatchers(OrderPaymentMatcherFactory.createSuccessfulTokenCapture(),
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization()) 	
				.build();
		assertThat("The order validation should succeed.", order, orderValidator);
	}

	/**
	 * Ensure token display value is persisted on authorization order payment.
	 */
	@DirtiesDatabase
	@Test
	public void ensureTokenDisplayValueIsPersistedOnAuthorizationOrderPayment() {
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPaymentWithToken(cyberSourceSubscriberFactory.createBillableSubscriber());
		ShoppingCart shoppingCart = checkoutTestCartBuilder.withGateway(createCyberSourceExternalPaymentGateway())
				.withElectronicProduct()
				.build();
		
		CheckoutResults results = checkoutService.checkout(shoppingCart, templateOrderPayment, true);

		Order persistedOrder = orderService.findOrderByOrderNumber(results.getOrder().getOrderNumber());
		Set<OrderPayment> orderPayments = persistedOrder.getOrderPayments();
		OrderPayment persistedAuthorizationOrderPayment = null;
		for (OrderPayment orderPayment : orderPayments) {
			if (orderPayment.getTransactionType() == OrderPayment.AUTHORIZATION_TRANSACTION) {
				persistedAuthorizationOrderPayment = orderPayment;
			}
		}
		assertNotNull("An authorization OrderPayment should be present.", persistedAuthorizationOrderPayment);
		assertEquals("Token display value should be set.", templateOrderPayment.extractPaymentToken().getDisplayValue(),
				persistedAuthorizationOrderPayment.extractPaymentToken().getDisplayValue());
	}

	
	private void completePhysicalShipmentsForOrder(final Order order) {
		for (OrderShipment orderShipment : order.getPhysicalShipments()) {
			orderShipment = orderService.processReleaseShipment(orderShipment);
			orderService.completeShipment(orderShipment.getShipmentNumber(),
					"trackingNumber", true, null, false, new EventOriginatorHelperImpl().getSystemOriginator());
		}
	}

	private void ensureCaptureFails(final OrderPayment authorization) {
		Money purchaseTotal = PaymentGatewayPluginDtoConverter.toMoney(authorization);
		TestCybersourceClient.create(CybersourceConfigurationProvider.getProvider()
				.getConfigurationProperties()).authorizationReversal(authorization.getAuthorizationCode(), purchaseTotal);
	}
	
	private PaymentGateway createCyberSourceExternalPaymentGateway() {
		final PaymentGateway cyberSourceGateway = paymentGatewayBuilder.withName(Utils.uniqueCode("CybersourceTokenPaymentGateway"))
				.withType("paymentGatewayCyberSourceToken")
				.withProperties(CybersourceConfigurationProvider.getProvider().getConfigurationProperties())
				.build();
		return cyberSourceGateway;
	}
}
