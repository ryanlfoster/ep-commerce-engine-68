package com.elasticpath.domain.payment.impl;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.payment.PaymentGatewayProperty;
import com.elasticpath.plugin.payment.PaymentGatewayPluginInvoker;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Tests for {@link PaymentGatewayFactoryImpl}.
 */
public class PaymentGatewayFactoryImplTest {
	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	@Test(expected = NullPointerException.class)
	public void testNullInvokerThrowsException() throws Exception {
		PaymentGatewayFactoryImpl factory = new PaymentGatewayFactoryImplStub(null);
		factory.getPaymentTypeForPlugin("pluginType");
	}

	@Test
	public void testInvokersPaymentTypeReturned() throws Exception {
		final PaymentGatewayPluginInvoker mockInvoker = context.mock(PaymentGatewayPluginInvoker.class);
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockInvoker).getPaymentType();
				will(returnValue(PaymentType.PAYMENT_TOKEN));
			}
		});

		PaymentGatewayFactoryImpl factory = new PaymentGatewayFactoryImplStub(mockInvoker);
		assertEquals("Factory should return the same PaymentType that the invoker returns",
				PaymentType.PAYMENT_TOKEN, factory.getPaymentTypeForPlugin("pluginType"));
	}

	private static final class PaymentGatewayFactoryImplStub extends PaymentGatewayFactoryImpl {

		private final PaymentGatewayPluginInvoker invoker;

		PaymentGatewayFactoryImplStub(final PaymentGatewayPluginInvoker invoker) {
			this.invoker = invoker;
		}

		@Override
		public PaymentGatewayPluginInvoker createConfiguredPaymentGatewayPluginInstance(
				final String pluginType, final Map<String, PaymentGatewayProperty> properties) {
			return invoker;
		}
	}
}
