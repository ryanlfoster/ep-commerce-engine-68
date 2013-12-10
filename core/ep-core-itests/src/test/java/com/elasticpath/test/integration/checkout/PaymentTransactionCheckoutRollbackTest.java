/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.test.integration.checkout;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.builder.CustomerBuilder;
import com.elasticpath.domain.builder.checkout.CheckoutTestCartBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.factory.OrderPaymentFactory;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.paymentgateways.testdouble.PaymentGatewayPluginTestDouble;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;
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
 * Test payment transaction rollbacks on a failed checkout action.
 */
@ContextConfiguration(inheritLocations = true)
public class PaymentTransactionCheckoutRollbackTest extends BasicSpringContextTest {

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderPaymentFactory orderPaymentFactory;

	@Autowired
	private CheckoutTestCartBuilder checkoutTestCartBuilder;

	@Autowired
	private CustomerBuilder customerBuilder;

	@Autowired
	private CustomerService customerService;

	private SimpleStoreScenario scenario;
	
	/**
	 * Set up common elements of the test.
	 */
	@Before
	public void setUp() {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
		checkoutTestCartBuilder.withScenario(scenario).withTestDoubleGateway();
		PaymentGatewayPluginTestDouble.clearPaymentGatewayTransactions();
	}

	/**
	 * Ensure payment transactions on mixed cart are reversed on checkout rollback.
	 */
	@DirtiesDatabase
	@Test
	public void ensurePaymentTransactionsOnMixedCartAreReversedOnCheckoutRollback() {
		OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPayment();
		Customer defaultCustomer = customerBuilder.withStoreCode(scenario.getStore().getCode())
												  .build();
		customerService.add(defaultCustomer);

		try {
			ShoppingCart shoppingCart = checkoutTestCartBuilder.withCustomer(defaultCustomer)
					.withElectronicProduct()
					.withPhysicalProduct()
					.build();
			
			checkoutService.checkout(shoppingCart, templateOrderPayment, true);
			fail("The order should fail with an exception on checkout.");
		} catch (EpServiceException e) {
			// ignore exception
		}

		Order retrievedOrder = retrieveOrderForCustomer(defaultCustomer);

		OrderValidator orderValidator = OrderValidator
				.builder()
				.withStatus(OrderStatus.FAILED)
				.withPaymentMatchers(OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(),
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(),
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture())
				.build();

		assertThat("The order validation should succeed.", retrievedOrder, orderValidator);

		assertTransactions(PaymentGatewayPluginTestDouble.AUTHORIZATION_TRANSACTION,
				PaymentGatewayPluginTestDouble.AUTHORIZATION_TRANSACTION, 
				PaymentGatewayPluginTestDouble.CAPTURE_TRANSACTION,
				PaymentGatewayPluginTestDouble.REVERSE_AUTHORIZATION, 
				PaymentGatewayPluginTestDouble.VOID_TRANSACTION,
				PaymentGatewayPluginTestDouble.REVERSE_AUTHORIZATION);
	}

	private Order retrieveOrderForCustomer(final Customer customer) {
		List<Order> orderList = orderService.findOrdersBySearchCriteria(getCriteriaThatFindsAllOrdersForCustomer(customer), 0, Integer.MAX_VALUE, null);
		return orderList.get(0);
	}

	private OrderSearchCriteria getCriteriaThatFindsAllOrdersForCustomer(final Customer customer) {
		CustomerSearchCriteria customerSearchCriteria = getBeanFactory().getBean(ContextIdNames.CUSTOMER_SEARCH_CRITERIA);
		customerSearchCriteria.setGuid(customer.getGuid());
		customerSearchCriteria.setFuzzySearchDisabled(true);

		OrderSearchCriteria orderSearchCriteria = getBeanFactory().getBean(ContextIdNames.ORDER_SEARCH_CRITERIA);
		orderSearchCriteria.setCustomerSearchCriteria(customerSearchCriteria);
		return orderSearchCriteria;
	}

	private void assertTransactions(String ... expectedTransactionTypes) {
		List<String> expectedTypeList = Arrays.asList(expectedTransactionTypes); 
		assertTrue("The order does not have the correct transactions. Expected: " + expectedTypeList + " Actual: " 
				+ PaymentGatewayPluginTestDouble.getPaymentGatewayTransactions(), 
				PaymentGatewayPluginTestDouble.verifyTransactions(expectedTypeList));
	}
}
