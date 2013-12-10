/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.paymentgateways.cybersource.impl;

import com.elasticpath.paymentgateways.cybersource.CyberSourceSubscriberFactory;
import com.elasticpath.paymentgateways.cybersource.TestCybersourceClient;
import com.elasticpath.paymentgateways.cybersource.provider.CybersourceConfigurationProvider;
import com.elasticpath.paymentgateways.factory.TestPaymentGatewayPluginDtoFactory;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.CardDetailsPaymentMethod;

/**
 * Implementation of {@link CyberSourceSubscriberFactory} that creates subscribers using the {@link TestCybersourceClient}.
 */
public class CyberSourceSubscriberFactoryImpl implements CyberSourceSubscriberFactory {
	@Override
	public String createBillableSubscriber() {
		TestCybersourceClient testCybersourceClient = TestCybersourceClient.create(CybersourceConfigurationProvider.getProvider()
				.getConfigurationProperties());

		AddressDto testAddress = TestPaymentGatewayPluginDtoFactory.createTestBillingAddress();
		CardDetailsPaymentMethod testCardDetailsPaymentMethod = TestPaymentGatewayPluginDtoFactory.createTestCardDetailsPaymentMethod();

		return testCybersourceClient.createCybersourceSubscriber(testAddress, testCardDetailsPaymentMethod, "USD");
	}
}
