/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.payment.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.payment.PaymentGatewayFactory;
import com.elasticpath.domain.payment.PaymentGatewayProperty;
import com.elasticpath.plugin.payment.PaymentGatewayPluginInvoker;
import com.elasticpath.plugin.payment.capabilities.FinalizeShipmentCapability;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>PaymentGatewayImpl</code>.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class PaymentGatewayImplTest {

	private static final String TEST_PAYMENT_GATEWAY_TYPE = "test payment gateway type";

	private static final String DEFAULT_PROPERTY_VALUE = "default property value";

	private static final String DEFAULT_PROPERTY_KEY = "default property key";

	private static final String GATEWAY_NAME = "test gateway name";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private PaymentGatewayImpl gateway;
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private PaymentGatewayFactory paymentGatewayFactory;
	private final Map<String, PaymentGatewayProperty> propertiesMap = createDefaultPropertiesMap();
	private final PaymentGatewayPluginInvoker mockPaymentGatewayPluginInvoker = context.mock(PaymentGatewayPluginInvoker.class);

	/**
	 * Prepare for the tests.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		paymentGatewayFactory = context.mock(PaymentGatewayFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		gateway = new PaymentGatewayImpl();
		gateway.setType(TEST_PAYMENT_GATEWAY_TYPE);
		gateway.setPropertiesMap(propertiesMap);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PAYMENT_GATEWAY_FACTORY, paymentGatewayFactory);

		context.checking(new Expectations() {
			{
				allowing(paymentGatewayFactory).createConfiguredPaymentGatewayPluginInstance(with(TEST_PAYMENT_GATEWAY_TYPE), with(propertiesMap));
				will(returnValue(mockPaymentGatewayPluginInvoker));
			}
		});

			}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}


	/**
     * Test name can be set and retrieved.
	 */
    @Test
	public void testNameCanBeSetAndRetrieved() {
		gateway.setName(GATEWAY_NAME);
		assertEquals(GATEWAY_NAME, gateway.getName());
	}

	/**
	 * Test supported currencies can be set and retrieved.
	 */
    @Test
	public void testSupportedCurrenciesCanBeSetAndRetrieved() {
		List<String> currencyList = Arrays.asList("CAD", "USD");
		gateway.setSupportedCurrencies(currencyList);
		assertEquals(currencyList, gateway.getSupportedCurrencies());
	}

	/**
	 * Ensure finalize shipment ignores shipment when capability is not present.
	 */
    @Test
	public void ensureFinalizeShipmentIgnoresShipmentWhenCapabilityIsNotPresent() {
		final OrderShipment mockOrderShipment = context.mock(OrderShipment.class);
		context.checking(new Expectations() {
			{
				oneOf(mockPaymentGatewayPluginInvoker).getCapability(FinalizeShipmentCapability.class);
				will(returnValue(null));

				oneOf(mockOrderShipment).getShipmentNumber();
		}
		});

		gateway.finalizeShipment(mockOrderShipment);
	}

	/**
	 * Ensure finalize shipment delegates to capability.
	 */
    @Test
	public void ensureFinalizeShipmentDelegatesToCapability() {
		final OrderShipment mockOrderShipment = context.mock(OrderShipment.class);
		
		final FinalizeShipmentCapability mockFinalizeShipmentCapability = context.mock(FinalizeShipmentCapability.class);

		context.checking(new Expectations() {
			{
				oneOf(mockPaymentGatewayPluginInvoker).getCapability(FinalizeShipmentCapability.class);
				will(returnValue(mockFinalizeShipmentCapability));

				ignoring(mockOrderShipment);

				oneOf(mockFinalizeShipmentCapability).finalizeShipment(with(any(OrderShipmentDto.class)));
	}
		});

		gateway.finalizeShipment(mockOrderShipment);

	}

	private Map<String, PaymentGatewayProperty> createDefaultPropertiesMap() {
		Map<String, PaymentGatewayProperty> propertiesMap = new TreeMap<String, PaymentGatewayProperty>();
		PaymentGatewayProperty value = new PaymentGatewayPropertyImpl();
		value.setKey(DEFAULT_PROPERTY_KEY);
		value.setValue(DEFAULT_PROPERTY_VALUE);
		propertiesMap.put(DEFAULT_PROPERTY_KEY, value);

		return propertiesMap;
	}

}
