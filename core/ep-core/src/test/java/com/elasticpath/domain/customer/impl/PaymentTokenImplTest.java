/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.domain.customer.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.domain.customer.PaymentToken;
import com.google.common.testing.EqualsTester;

/**
 * Test for {@link PaymentTokenImpl}.
 */
public class PaymentTokenImplTest {
	
	/**
	 * Ensure builder copies with all fields.
	 */
	@Test
	public void ensureBuilderCopiesWithAllFields() {
		String testValue = "test token";
		String testDisplayValue = "displayValue";
		String testGatewayGuid = "gatewayGuid";

		PaymentToken paymentToken = new PaymentTokenImpl.TokenBuilder()
			.withValue(testValue)
			.withDisplayValue(testDisplayValue)
			.withGatewayGuid(testGatewayGuid)
			.build();
		
		assertEquals("PaymentToken value should match.", testValue, paymentToken.getValue());
		assertEquals("PaymentToken display value should match.", testDisplayValue, paymentToken.getDisplayValue());
		assertEquals("Payment Gateway Guid should match.", testGatewayGuid, paymentToken.getGatewayGuid());
	}

	@Test
	public void testHashCodeEquals() {
		PaymentTokenImpl.TokenBuilder tokenBuilder = new PaymentTokenImpl.TokenBuilder()
				.withValue("originalValue")
				.withDisplayValue("originalDisplayValue")
				.withGatewayGuid("originalGuid");

		PaymentToken originalToken = tokenBuilder.build();
		PaymentToken identicalToken = tokenBuilder.build();

		PaymentToken mutatedValue = tokenBuilder.withValue("mutatedValue").build();
		PaymentToken mutatedDisplayValue = tokenBuilder.withDisplayValue("mutatedDisplayValue").build();
		PaymentToken mutatedGatewayGuid = tokenBuilder.withDisplayValue("mutatedGatewayGuid").build();
		new EqualsTester()
				.addEqualityGroup(originalToken, identicalToken)
				.addEqualityGroup(mutatedValue)
				.addEqualityGroup(mutatedDisplayValue)
				.addEqualityGroup(mutatedGatewayGuid)
				.testEquals();
	}
}
