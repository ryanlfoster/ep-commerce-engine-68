/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.test.integration.token;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.persistence.dao.PaymentTokenDao;
import com.elasticpath.test.db.DbTestCase;

public class PaymentTokenDaoImplTest extends DbTestCase {
	@Autowired
	private PaymentTokenDao paymentTokenDao;
	
	@Test
	public void ensurePersistedTokenCanBeRetrieved() {
		String testValue = "test token";
		String testDisplayValue = "displayValue";
		String testGatewayGuid = "gatewayGuid";
		
		PaymentToken paymentToken = new PaymentTokenImpl.TokenBuilder()
			.withValue(testValue)
			.withDisplayValue(testDisplayValue)
			.withGatewayGuid(testGatewayGuid)
			.build();
		
		PaymentToken persistedPaymentToken = paymentTokenDao.add(paymentToken);
		PaymentToken retrievedPaymentToken = paymentTokenDao.get(persistedPaymentToken.getUidPk());

		assertEquals("PaymentToken value should match.", testValue, retrievedPaymentToken.getValue());
		assertEquals("PaymentToken display value should match.", testDisplayValue, retrievedPaymentToken.getDisplayValue());
		assertEquals("Payment Gateway Guid should match.", testGatewayGuid, retrievedPaymentToken.getGatewayGuid());
	}
}
